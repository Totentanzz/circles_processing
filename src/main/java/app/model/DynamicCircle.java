package app.model;

import app.utils.tools.MeasureTool;
import processing.core.PApplet;
import processing.core.PVector;

public class DynamicCircle extends Circle {

    private StaticCircle pathCircle;
    private int mass;
    private float force;
    private int direction;
    private float angle;
    private float angularVelocity;
    private final float angularAcceleration;


    public DynamicCircle(StaticCircle pathCircle, PVector center, int radius, int mass, float force) {
        super(center,radius);
        this.pathCircle = pathCircle;
        this.mass = mass;
        this.force = force;
        this.direction = 1;
        this.angle = MeasureTool.calculateAngleAtan2(pathCircle.getCenter(),center);
        this.angularVelocity = 0f;
        this.angularAcceleration = force/mass;
    }

    public DynamicCircle(StaticCircle pathCircle, float angle, int radius, int mass, float force) {
        super(new PVector(
                pathCircle.center.x + pathCircle.radius * PApplet.cos(angle),
                pathCircle.center.y + pathCircle.radius * PApplet.sin(angle)
                ), radius);
        this.pathCircle = pathCircle;
        this.mass = mass;
        this.force = force;
        this.direction = 1;
        this.angle = angle;
        this.angularVelocity = 0f;
        this.angularAcceleration = force/mass;
    }

    public float getAngle() {
        return this.angle;
    }

    public void setAngle(float angle) {
        this.angle = angle;
        this.center.set(pathCircle.center.x + pathCircle.radius * PApplet.cos(angle),pathCircle.center.y + pathCircle.radius * PApplet.sin(angle));
    }

    public int getDirection() {
        return this.direction;
    }

    public void setDirection(int newDirection) {
        this.direction = newDirection;
    }

    public void moveAroundCircle(float dt) {
        angularVelocity += angularAcceleration*direction*dt;
        angle += angularVelocity*dt;
//        System.out.println("velocity of moving circle: " + angularVelocity);
//        System.out.println("Current angle from (0,0): " + angle);
        center.x = pathCircle.center.x + pathCircle.radius * PApplet.cos(angle);
        center.y = pathCircle.center.y + pathCircle.radius * PApplet.sin(angle);
    }

    public void stopMoving() {
        angularVelocity = 0;
    }
}
