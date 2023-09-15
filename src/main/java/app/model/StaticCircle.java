package app.model;

import processing.core.PApplet;
import processing.core.PVector;

import java.util.ArrayList;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class StaticCircle extends Circle {

    ArrayList<PVector> circlePoints;

    public StaticCircle(PVector center, int radius) {
        super(center,radius);
    }

    public ArrayList<PVector> getPoints() {
        ArrayList<PVector> pointsList;
        if (circlePoints==null) {
            pointsList = IntStream.rangeClosed(0, 360)
                    .mapToObj(num -> new PVector(
                            this.center.x + this.radius * PApplet.cos(PApplet.radians(num)),
                            this.center.y + this.radius * PApplet.sin(PApplet.radians(num))
                    )).collect(Collectors.toCollection(ArrayList::new));
        }
        else {
            pointsList = circlePoints;
        }
        return pointsList;
    }

}
