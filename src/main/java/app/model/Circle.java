package app.model;

import app.controller.Controller;
import processing.core.PVector;

import java.util.Objects;
import java.util.Random;

public abstract class Circle {

    protected PVector center;
    protected int radius;
    protected int r,g,b;

    public Circle(PVector center, int radius) {
        Random random = new Random();
        this.center = center;
        this.radius = radius;
        this.r = 1+random.nextInt(255);
        this.g = 1+random.nextInt(255);
        this.b = 1+random.nextInt(255);
    }

    public PVector getCenter() {
        return this.center;
    }

    public int getRadius() {
        return this.radius;
    }

    public int getR() {
        return r;
    }

    public int getG() {
        return g;
    }

    public int getB() {
        return b;
    }

    public void create() {
        Controller.processing.circle(this.center.x,this.center.y,this.radius*2);
    }

    @Override
    public boolean equals(Object instance) {
        if (this == instance) return true;
        if (instance == null || getClass() != instance.getClass()) return false;
        Circle circle = (Circle) instance;
        return radius == circle.radius && Objects.equals(center, circle.center);
    }

}
