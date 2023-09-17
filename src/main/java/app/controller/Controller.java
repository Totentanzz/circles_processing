package app.controller;

import app.model.DataStorage;
import app.model.DynamicCircle;
import app.model.StaticCircle;
import app.utils.Initializable;
import app.utils.tools.MeasureTool;
import app.view.ControlsName;
import app.view.ControlsView;
import app.view.ObjectsView;
import controlP5.Toggle;
import processing.core.PApplet;
import processing.core.PVector;

import java.util.*;
import java.util.concurrent.*;
import java.util.function.BooleanSupplier;
import java.util.function.Supplier;

public class Controller extends PApplet implements Initializable {

    public static PApplet processing;
    private DataStorage dataStorage;
    private ControlsView controlsView;
    private ObjectsView objectsView;


    private ExecutorService singleExecutor, parallelExecutor;
    private BooleanSupplier toggleSupplier;
    private Supplier<Float> speedSupplier;
    private Supplier<Integer> circlesNumSupplier;

    private Optional<DynamicCircle> draggedCircle;
    private PVector mousePos;
    private boolean startFlag;

    public Controller() {
        processing = this;
    }

    private void setControlsAction() {
        toggleSupplier = () -> ((Toggle) controlsView.getControl(ControlsName.linesTgl.getValue())).getState();
        speedSupplier = () -> controlsView.getControl(ControlsName.speedSld.getValue()).getValue();
        circlesNumSupplier = () -> (int) controlsView.getControl(ControlsName.circlesSld.getValue()).getValue();

        controlsView.getControl(ControlsName.pauseBtn.getValue()).onClick(
                callbackEvent -> startFlag=false
        );
        controlsView.getControl(ControlsName.resumeBtn.getValue()).onClick(
                callbackEvent -> startFlag=true
        );
        controlsView.getControl(ControlsName.randBtn.getValue()).onClick(
                callbackEvent -> singleExecutor.execute(dataStorage::resetMovingCirclesColor)
        );
        controlsView.getControl(ControlsName.resetBtn.getValue()).onClick(
                callbackEvent -> singleExecutor.execute(dataStorage::resetMovingCirclesAngle)
        );
        controlsView.getControl(ControlsName.circlesSld.getValue()).onChange(
                callbackEvent -> singleExecutor.execute(()->{
                    startFlag=false;
                    dataStorage.resetMovingCirclesNum(circlesNumSupplier.get());
                    startFlag=true;
                })
        );
        controlsView.getControl(ControlsName.speedSld.getValue()).onChange(
                callbackEvent -> singleExecutor.execute(()->{
                    float newSpeedFactor = speedSupplier.get();
                    startFlag=false;
                    synchronized (dataStorage.getTaskList()) {
                        dataStorage.setTaskList(dataStorage.createMoveTaskList(newSpeedFactor));
                        dataStorage.setSpeedFactor(newSpeedFactor);
                    }
                    startFlag=true;
                })
        );
    }

    @Override
    public void mousePressed() {
        singleExecutor.execute(()->{
            mousePos.set(mouseX,mouseY);
            draggedCircle = dataStorage.getMovableCircles().stream().filter(circle->{
                float dist = PVector.dist(mousePos, circle.getCenter());
                if (dist < circle.getRadius()) {
                    startFlag = false;
                    return true;
                }
                return false;
            }).findFirst();
        });
    }

    @Override
    public void mouseDragged() {
        draggedCircle.ifPresent((circle)->{
            singleExecutor.execute(()->{
                mousePos.set(mouseX,mouseY);
                float mouseAngle = MeasureTool.calculateAngleAtan2(dataStorage.getPathCircle().getCenter(),mousePos);
                mouseAngle += mouseAngle<0 ? 2*MeasureTool.PI : 0;
                circle.setAngle(mouseAngle);
            });
        });
    }

    @Override
    public void mouseReleased() {
        draggedCircle.ifPresent((circle)-> {
            singleExecutor.execute(() -> {
                dataStorage.getMovableCircles().forEach(DynamicCircle::stopMoving);
                dataStorage.sortCirclesList();
                circle.setDirection(1);
                draggedCircle = Optional.empty();
            });
        });
    }

    @Override
    public void settings() {
        size(1600,800);
    }

    @Override
    public void setup() {
        initialize();
        objectsView.drawBackground(0);
        objectsView.setStrokeWeight(3);
        setControlsAction();
    }

    @Override
    public void draw() {
        objectsView.drawBackground(0);
        objectsView.drawPathCircle();
        objectsView.drawMovableCircles();
        if (dataStorage.checkMovableCircles() && startFlag) {
            try {
                parallelExecutor.invokeAll(dataStorage.getTaskList());
            } catch (InterruptedException exc) {
                throw new RuntimeException(exc);
            }
        }
        if (toggleSupplier.getAsBoolean()) {
            objectsView.drawLines();
        }
    }

    @Override
    public void initialize() {
        this.dataStorage = DataStorage.getInstance();
        this.dataStorage.createPathCircle(this.width/2,this.height/2);
        this.dataStorage.createMovableCircles(this.dataStorage.getPathCircle(),this.dataStorage.getCirclesAmount());
        this.dataStorage.createMoveTaskList(this.dataStorage.getSpeedFactor());
        this.controlsView = new ControlsView(this);
        this.objectsView = new ObjectsView(this);
        this.singleExecutor = Executors.newSingleThreadExecutor();
        this.parallelExecutor = Executors.newWorkStealingPool(36);
        this.startFlag = true;
        this.draggedCircle = Optional.empty();
        this.mousePos = new PVector();
    }


}
