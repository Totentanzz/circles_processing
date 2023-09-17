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

    public void setCenter(PVector center) {
        this.center = center;
    }

    public int getRadius() {
        return this.radius;
    }

    public int getR() {
        return r;
    }

    public void setR(int r) {
        this.r = r;
    }

    public int getG() {
        return g;
    }

    public void setG(int g) {
        this.g = g;
    }

    public int getB() {
        return b;
    }

    public void setB(int b) {
        this.b = b;
    }

    @Override
    public boolean equals(Object instance) {
        if (this == instance) return true;
        if (instance == null || getClass() != instance.getClass()) return false;
        Circle circle = (Circle) instance;
        return radius == circle.radius && Objects.equals(center, circle.center);
    }

}
