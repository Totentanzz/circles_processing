package app.controller;

import app.model.DynamicCircle;
import app.model.StaticCircle;
import app.utils.Initializable;
import app.utils.tools.MeasureTool;
import app.view.ControlsName;
import app.view.ControlsView;
import controlP5.Toggle;
import processing.core.PApplet;
import processing.core.PVector;

import java.util.*;
import java.util.concurrent.*;
import java.util.function.BooleanSupplier;

public class Controller extends PApplet implements Initializable {

    public static PApplet processing;
    private Random randomGenerator;
    private StaticCircle pathCircle;
    private ArrayList<DynamicCircle> movableCircles;
    private Optional<DynamicCircle> draggedCircle;
    private ExecutorService singleExecutor, parallelExecutor;
    private ArrayList<Callable<Void>> taskList;
    private ControlsView controlsView;

    private float dt;
    private int newDirection;
    private boolean startFlag;
    private BooleanSupplier toggleSupplier;
    private PVector mousePos;

    public Controller() {
        processing = this;
    }

    public void createPathCircle() {
        this.pathCircle = new StaticCircle(new PVector(this.width/2,this.height/2),
                                     300+randomGenerator.nextInt(41));
    }

    public void createMovableCircles(StaticCircle path, int amount) {
        if (amount>0) {
            for (int i=0;i<amount;i++) {
                DynamicCircle circle = new DynamicCircle(path,
                        randomGenerator.nextFloat()*2*MeasureTool.PI,
                        30+randomGenerator.nextInt(21),
                        1+randomGenerator.nextInt(1000),
                        randomGenerator.nextFloat()*0.9999f
                );
                movableCircles.add(circle);
            }
            sortCirclesList();
        }
    }

    public ArrayList<Callable<Void>> createMoveTaskList() {
        ArrayList<Callable<Void>> taskList = new ArrayList<>();
        for (int i = 0; i< movableCircles.size(); i++) {
            int finalI = i;
            taskList.add(()->{
                DynamicCircle curCircle = movableCircles.get(finalI);
                DynamicCircle leftCircle = finalI == 0 ? movableCircles.get(movableCircles.size() - 1) : movableCircles.get(finalI - 1);
                DynamicCircle rightCircle = finalI == movableCircles.size() - 1 ? movableCircles.get(0) : movableCircles.get(finalI + 1);
                newDirection = MeasureTool.calculateLongestArcDirection(pathCircle.getCenter(), curCircle.getCenter(), leftCircle.getCenter(), rightCircle.getCenter());
                if (newDirection != curCircle.getDirection()) {
                    curCircle.stopMoving();
                    curCircle.setDirection(newDirection);
                }
                curCircle.moveAroundCircle(dt);
                return null;
            });
        }
        return taskList;
    }

    public void sortCirclesList() {
        movableCircles.sort((obj1, obj2)-> Float.compare(obj1.getAngle(),obj2.getAngle()));
    }

    public void resetMovingCirclesAngle() {
        movableCircles.forEach(obj->{
            obj.setAngle(randomGenerator.nextFloat()*2*MeasureTool.PI);
            obj.setDirection(1);
            obj.stopMoving();
        });
        sortCirclesList();
    }

    public void resetMovingCirclesColor() {
        movableCircles.forEach(obj->{
            obj.setR(1+randomGenerator.nextInt(255));
            obj.setG(1+randomGenerator.nextInt(255));
            obj.setB(1+randomGenerator.nextInt(255));
        });
    }

    public void drawPathCircle() {
        noFill();
        stroke(255,255,255);
        pathCircle.create();
    }

    public void drawMovableCircles() {
        movableCircles.forEach(circle->{
            stroke(circle.getR(), circle.getG(), circle.getB());
            fill(circle.getR(), circle.getG(), circle.getB());
            circle.create();
        });
    }

    public void drawLines() {
        stroke(255,255,255);
        for (int i=0;i<movableCircles.size()-1;i++) {
            PVector curCircleCenter = movableCircles.get(i).getCenter();
            PVector nextCircleCenter = movableCircles.get(i+1).getCenter();
            processing.line(curCircleCenter.x,curCircleCenter.y,nextCircleCenter.x,nextCircleCenter.y);
        }

    }

    private boolean checkMovableCircles() {
        return movableCircles.stream().anyMatch(obj->obj.getDirection()!=0);
    }

    private void setControlsAction() {
        controlsView.getControl(ControlsName.pause.getValue()).onClick(
                callbackEvent -> startFlag=false
        );
        controlsView.getControl(ControlsName.resume.getValue()).onClick(
                callbackEvent -> startFlag=true
        );
        controlsView.getControl(ControlsName.rand.getValue()).onClick(
                callbackEvent -> singleExecutor.execute(this::resetMovingCirclesColor)
        );
        controlsView.getControl(ControlsName.reset.getValue()).onClick(
                callbackEvent -> singleExecutor.execute(this::resetMovingCirclesAngle)
        );
        toggleSupplier = () -> ((Toggle) controlsView.getControl(ControlsName.lines.getValue())).getState();
    }

    @Override
    public void mousePressed() {
        singleExecutor.execute(()->{
            mousePos.set(mouseX,mouseY);
            draggedCircle = movableCircles.stream().filter(circle->{
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
                float mouseAngle = MeasureTool.calculateAngleAtan2(pathCircle.getCenter(),mousePos);
                mouseAngle += mouseAngle<0 ? 2*MeasureTool.PI : 0;
                circle.setAngle(mouseAngle);
            });
        });
    }

    @Override
    public void mouseReleased() {
        draggedCircle.ifPresent((circle)-> {
            singleExecutor.execute(() -> {
                sortCirclesList();
                movableCircles.forEach(DynamicCircle::stopMoving);
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
        background(0);
        strokeWeight(3);
        initialize();
        controlsView.createControls(0,0);
        setControlsAction();
//        controls.setFont(createFont("Cascadia Mono",15));
//        controls.addButton("pause")
//                .setValue(-1)
//                .setPosition(10,30)
//                .setSize(65,30).onClick(callbackEvent -> startFlag=false);
//        controls.addButton("resume")
//                .setValue(1)
//                .setPosition(80,30)
//                .setSize(65,30).onClick(callbackEvent -> startFlag=true);
//        controls.addButton("rand colors")
//                .setPosition(10,65)
//                .setSize(135,30)
//                .onClick(callbackEvent -> resetMovingCirclesColor());
//        controls.addButton("reset")
//                .setValue(0)
//                .setPosition(10,100)
//                .setSize(135,30)
//                .onClick(callbackEvent -> resetMovingCirclesAngle());
//        controls.addToggle("lines",false)
//                .setPosition(10,135)
//                .setSize(135,30)
//                .setMode(ControlP5.SWITCH);

    }

    @Override
    public void draw() {
        background(0);
        drawPathCircle();
        drawMovableCircles();
        if (checkMovableCircles() && startFlag) {
            try {
                parallelExecutor.invokeAll(taskList);
            } catch (InterruptedException exc) {
                throw new RuntimeException(exc);
            }
        }
        if (toggleSupplier.getAsBoolean()) {
            drawLines();
        }
    }

    @Override
    public void initialize() {
        int circlesAmount = 3;
        this.dt = 0.1f;
        this.startFlag = true;
        this.draggedCircle = Optional.empty();
        this.movableCircles = new ArrayList<>();
        this.randomGenerator = new Random();
        this.mousePos = new PVector();
        this.singleExecutor = Executors.newSingleThreadExecutor();
        this.parallelExecutor = Executors.newWorkStealingPool(8);
        createPathCircle();
        createMovableCircles(pathCircle,circlesAmount);
        this.taskList = createMoveTaskList();
        this.controlsView = new ControlsView(this);
    }


}
