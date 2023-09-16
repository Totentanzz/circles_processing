package app.view;

import controlP5.Button;
import controlP5.ControlP5;
import controlP5.Controller;
import controlP5.Toggle;
import processing.core.PApplet;

public class ControlsView {

    PApplet pApplet;
    ControlP5 controls;

    public ControlsView(PApplet pApplet) {
        this.pApplet = pApplet;
        this.controls = new ControlP5(pApplet);
    }

    public void createControls(int x, int y) {
        controls.setFont(pApplet.createFont("Cascadia Mono",15));
        addButton("pause",x+10,y+10,65,30);
        addButton("resume",x+80,y+10,65,30);
        addButton("rand colors",x+10,y+45,135,30);
        addButton("reset",x+10,y+80,135,30);
        addToggle("lines",x+10,y+115,135,30);
    }

    public Button addButton(String name, int posX, int posY, int sizeX, int sizeY) {
        return controls.addButton(name).setPosition(posX,posY).setSize(sizeX,sizeY);
    }

    public Toggle addToggle(String name,int posX,int posY, int sizeX, int sizeY) {
        return controls.addToggle(name,false).setPosition(posX,posY).setSize(sizeX,sizeY).setMode(ControlP5.SWITCH);
    }

    public Controller<?> getControl(String name) {
        return controls.getController(name);
    }

}
