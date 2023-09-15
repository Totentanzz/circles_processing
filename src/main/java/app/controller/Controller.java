package app.controller;

import app.model.DynamicCircle;
import app.model.StaticCircle;
import app.utils.tools.MeasureTool;
import processing.core.PApplet;
import processing.core.PVector;

import java.util.*;
import java.util.concurrent.*;

import controlP5.*;

public class Controller extends PApplet implements Initializable {

    public static PApplet processing;
    private Random randomGenerator;
    private StaticCircle pathCircle;
    private ArrayList<DynamicCircle> movableCircles;
    private Optional<DynamicCircle> draggedCircle;
    private ExecutorService singleExecutor, parallelExecutor;
    private ArrayList<Callable<Void>> taskList;
    private ControlP5 controls;

    private float dt;
    private int newDirection;
    private boolean startFlag;
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
            movableCircles.sort((obj1, obj2)-> Float.compare(obj1.getAngle(),obj2.getAngle()));
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

    private boolean checkMovableCircles() {
        return movableCircles.stream().anyMatch(obj->obj.getDirection()!=0);
    }

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

    public void mouseReleased() {
        draggedCircle.ifPresent((circle)-> {
            singleExecutor.execute(() -> {
                movableCircles.sort((obj1, obj2) -> Float.compare(obj1.getAngle(), obj2.getAngle()));
                movableCircles.forEach(DynamicCircle::stopMoving);
                circle.setDirection(1);
                draggedCircle = Optional.empty();
                startFlag = true;
            });
        });
    }

    public void settings() {
        size(1600,800);
    }

    public void setup() {
        background(0);
        strokeWeight(3);
        initialize();
    }

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
        this.controls = new ControlP5(this);
    }


}
