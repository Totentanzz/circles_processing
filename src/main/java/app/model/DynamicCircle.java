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
    private float angularAcceleration;


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

    public int getMass() {
        return mass;
    }

    public void setMass(int mass) {
        if (this.mass>0) {
            this.mass = mass;
            this.angularAcceleration = force/mass;
        }
    }

    public float getForce() {
        return force;
    }

    public void setForce(float force) {
        if (this.force>0) {
            this.force = force;
            this.angularAcceleration = force/mass;
        }
    }

    public float getAngle() {
        return this.angle;
    }

    public void setAngle(float angle) {
        this.angle = angle;
        this.center.set(
                pathCircle.center.x + pathCircle.radius * PApplet.cos(angle),
                pathCircle.center.y + pathCircle.radius * PApplet.sin(angle)
        );
    }

    public int getDirection() {
        return this.direction;
    }

    public void setDirection(int newDirection) {
        this.direction = newDirection;
    }

    public float getAngularAcceleration() {
        return angularAcceleration;
    }

    public void moveAroundCircle(float dt, float speedFactor) {
        angularVelocity += angularAcceleration*direction*speedFactor*dt;
        angle += angularVelocity*dt;
        angle += (angle<0) ? 2*MeasureTool.PI : 0;
        center.x = pathCircle.center.x + pathCircle.radius * PApplet.cos(angle);
        center.y = pathCircle.center.y + pathCircle.radius * PApplet.sin(angle);
    }

    public void stopMoving() {
        angularVelocity = 0;
    }
}
