package app.view;

import app.model.DataStorage;
import app.model.DynamicCircle;
import app.model.StaticCircle;
import processing.core.PApplet;
import processing.core.PVector;

import java.util.ArrayList;

public class ObjectsView {

    PApplet processing;
    DataStorage dataStorage;


    public ObjectsView(PApplet processing) {
        this.processing = processing;
        this.dataStorage = DataStorage.getInstance();
        processing.strokeWeight(3);
    }

    public void drawBackground(int rgb) {
        processing.background(rgb);
    }

    public void setStrokeWeight(float weight) {
        processing.strokeWeight(weight);
    }

    public void drawPathCircle() {
        StaticCircle pathCircle = dataStorage.getPathCircle();
        processing.noFill();
        processing.stroke(255,255,255);
        processing.circle(pathCircle.getCenter().x,pathCircle.getCenter().y,pathCircle.getRadius()*2);
    }

    public void drawMovableCircles() {
        ArrayList<DynamicCircle> circlesList;
        synchronized (circlesList = dataStorage.getMovableCircles()) {
           circlesList.forEach(circle->{
                processing.stroke(circle.getR(), circle.getG(), circle.getB());
                processing.fill(circle.getR(), circle.getG(), circle.getB());
                processing.circle(circle.getCenter().x,circle.getCenter().y,circle.getRadius()*2);
            });
        }
    }

    public void drawLines() {
        ArrayList<DynamicCircle> circlesList = dataStorage.getMovableCircles();
        processing.stroke(210,0,0);
        for (int i=0;i<circlesList.size();i++) {
            PVector curCircleCenter = circlesList.get(i).getCenter();
            PVector nextCircleCenter = i !=  circlesList.size()-1 ? circlesList.get(i+1).getCenter() :  circlesList.get(0).getCenter();
            processing.line(curCircleCenter.x,curCircleCenter.y,nextCircleCenter.x,nextCircleCenter.y);
        }
    }

}
