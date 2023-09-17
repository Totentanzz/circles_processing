package app.model;

import app.utils.tools.MeasureTool;
import app.view.ControlsName;
import controlP5.Tab;
import processing.core.PVector;

import javax.xml.crypto.Data;
import java.util.ArrayList;
import java.util.Optional;
import java.util.Random;
import java.util.concurrent.Callable;

public class DataStorage {

    private static volatile DataStorage dataStorage;

    private Random randomGenerator;
    private StaticCircle pathCircle;
    private ArrayList<DynamicCircle> movableCircles;
    private ArrayList<Callable<Void>> taskList;

    private float dt, speedFactor;
    private int circlesAmount;

    private DataStorage() {
        this.randomGenerator = new Random();
        this.dt = 0.1f;
        this.speedFactor = 1;
        this.circlesAmount = 1;
        this.movableCircles = new ArrayList<>();
        this.taskList = new ArrayList<>();
    }

    public static DataStorage getInstance() {
        DataStorage localDataStorage = dataStorage;
        if (localDataStorage ==null){
            synchronized (DataStorage.class){
                localDataStorage = dataStorage;
                if (localDataStorage == null){
                    dataStorage = localDataStorage = new DataStorage();
                }
            }
        }
        return localDataStorage;
    }

    public StaticCircle getPathCircle() {
        return pathCircle;
    }

    public void setPathCircle(StaticCircle pathCircle) {
        this.pathCircle = pathCircle;
    }

    public ArrayList<DynamicCircle> getMovableCircles() {
        return movableCircles;
    }

    public void setMovableCircles(ArrayList<DynamicCircle> movableCircles) {
        this.movableCircles.clear();
        this.movableCircles.addAll(movableCircles);
    }

    public ArrayList<Callable<Void>> getTaskList() {
        return taskList;
    }

    public void setTaskList(ArrayList<Callable<Void>> taskList) {
        this.taskList.clear();
        this.taskList.addAll(taskList);
    }

    public float getDt() {
        return dt;
    }

    public void setDt(float dt) {
        this.dt = dt;
    }

    public float getSpeedFactor() {
        return speedFactor;
    }

    public void setSpeedFactor(float speedFactor) {
        this.speedFactor = speedFactor;
    }

    public int getCirclesAmount() {
        return circlesAmount;
    }

    public void setCirclesAmount(int circlesAmount) {
        this.circlesAmount = circlesAmount;
    }

    public void createPathCircle(int xOrigin, int yOrigin) {
        this.pathCircle = new StaticCircle(
                new PVector(xOrigin,yOrigin),
          300+randomGenerator.nextInt(41)
        );
    }

    public void createMovableCircles(StaticCircle path, int amount) {
        if (amount>0) {
            for (int i=0;i<amount;i++) {
                DynamicCircle circle = new DynamicCircle(path,
                        randomGenerator.nextFloat()*2* MeasureTool.PI,
                        30+randomGenerator.nextInt(21),
                        1+randomGenerator.nextInt(1000),
                        randomGenerator.nextFloat()*0.9999f
                );
                movableCircles.add(circle);
            }
            sortCirclesList();
        }
    }

    public ArrayList<Callable<Void>> createMoveTaskList(float speedFactor) {
        ArrayList<Callable<Void>> taskList = new ArrayList<>();
            for (int i = 0; i < movableCircles.size(); i++) {
                int finalI = i;
                taskList.add(()->{
                    DynamicCircle curCircle = movableCircles.get(finalI);
                    DynamicCircle leftCircle = finalI == 0 ?
                                               movableCircles.get(movableCircles.size() - 1) :
                                               movableCircles.get(finalI - 1);
                    DynamicCircle rightCircle = finalI == movableCircles.size() - 1 ?
                                               movableCircles.get(0) :
                                               movableCircles.get(finalI + 1);
                    int newDirection = MeasureTool.calculateLongestArcDirection(
                            pathCircle.getCenter(),
                            curCircle.getCenter(),
                            leftCircle.getCenter(),
                            rightCircle.getCenter()
                    );
                    if (newDirection != curCircle.getDirection()) {
                        curCircle.stopMoving();
                        curCircle.setDirection(newDirection);
                    }
                    curCircle.moveAroundCircle(dt,speedFactor);
                    return null;
                });
            }
        return taskList;
    }

    public void deleteMovableCircles(int amount) {
        if (amount>0 && amount<movableCircles.size()) {
            movableCircles.subList(movableCircles.size() - amount, movableCircles.size()).clear();
        }
    }

    public synchronized void sortCirclesList() {
        movableCircles.sort((obj1, obj2)-> {
            float angle1 = obj1.getAngle();
            float angle2 = obj2.getAngle();
            angle1 += angle1<0 ? 2*MeasureTool.PI : 0;
            obj1.setAngle(angle1);
            return Float.compare(angle1,angle2);
        });
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

    public void resetMovingCirclesNum(int newCirclesAmount) {
        if (newCirclesAmount>circlesAmount) {
            createMovableCircles(pathCircle,newCirclesAmount-circlesAmount);
        } else if (newCirclesAmount<circlesAmount) {
            deleteMovableCircles(circlesAmount-newCirclesAmount);
        }
        setTaskList(createMoveTaskList(speedFactor));
        circlesAmount = newCirclesAmount;
        movableCircles.get(0).setDirection(1);
    }

    public boolean checkMovableCircles() {
        synchronized (movableCircles) {
            return movableCircles.stream().anyMatch(obj->obj.getDirection()!=0);
        }
    }

}
