package app.controller;

import app.model.DynamicCircle;
import app.model.StaticCircle;
import app.utils.tools.MeasureTool;
import processing.core.PApplet;
import processing.core.PVector;

import java.util.ArrayList;
import java.util.Random;

public class Controller extends PApplet implements Initializable {

    public static PApplet processing;
    private Random randomGenerator;
    private StaticCircle pathCircle;
    private ArrayList<DynamicCircle> movableCircles;
    private float dt;
    private int newDirection;

    public Controller() {
        processing = this;
    }


    public void calcCirclesPosition() {
        for (int i = 0; i< movableCircles.size(); i++) {
            DynamicCircle curCircle = movableCircles.get(i);
            DynamicCircle leftCircle = i == 0 ? movableCircles.get(movableCircles.size() - 1) : movableCircles.get(i - 1);
            DynamicCircle rightCircle = i == movableCircles.size() - 1 ? movableCircles.get(0) : movableCircles.get(i + 1);
            curCircle.moveAroundCircle(dt);
            newDirection=MeasureTool.calculateLongestArcDirection(pathCircle.getCenter(),curCircle.getCenter(),leftCircle.getCenter(),rightCircle.getCenter());
            if (newDirection!=curCircle.getDirection()) {
                curCircle.stopMoving();
                curCircle.setDirection(newDirection);
            }
        }
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

    public void drawPathCircle() {
        noFill();
        stroke(255,255,255);
        pathCircle.create();
    }

    public void drawMovableCircles() {
        for (int i=0;i<movableCircles.size();i++) {
            DynamicCircle circle = movableCircles.get(i);
            stroke(circle.getR(),circle.getG(),circle.getB());
            fill(circle.getR(),circle.getG(),circle.getB());
            circle.create();
        }
    }

    private boolean checkMovableCircles() {
        return movableCircles.stream().anyMatch(obj->obj.getDirection()!=0);
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
        if (checkMovableCircles()) {
            thread("calcCirclesPosition");
        }
    }

    @Override
    public void initialize() {
        int circlesAmount = 20;
        dt = 0.1f;
        movableCircles = new ArrayList<>();
        randomGenerator = new Random();
        createPathCircle();
        createMovableCircles(this.pathCircle,circlesAmount);
    }

}
