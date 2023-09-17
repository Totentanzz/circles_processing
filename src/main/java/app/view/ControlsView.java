package app.view;

import controlP5.*;
import processing.core.PApplet;
import processing.core.PFont;

public class ControlsView {

    PApplet pApplet;
    ControlP5 controls;

    public ControlsView(PApplet pApplet) {
        this.pApplet = pApplet;
        this.controls = new ControlP5(pApplet);
        initControls(0,0);
    }

    private void initControls(int x, int y) {
        PFont font = controls.papplet.createFont("Cascadia Mono",15,false);
        controls.setFont(font);
        addButton("pause",x+10,y+10,65,30);
        addButton("resume",x+80,y+10,65,30);
        addButton("rand colors",x+10,y+45,135,30);
        addButton("reset",x+10,y+80,135,30);
        addSlider("circles",x+10,y+115,135,30,1,36,36);
        getControl("circles").getCaptionLabel().align(ControlP5.CENTER,ControlP5.BOTTOM_OUTSIDE);
        addToggle("lines",x+10,y+167,135,30);
        getControl("lines").getCaptionLabel().align(ControlP5.CENTER,ControlP5.BOTTOM_OUTSIDE);
        addSlider("speed",x+150,y+10,30,187,1,10,40);
    }

    public Button addButton(String name, int posX, int posY, int sizeX, int sizeY) {
        return controls.addButton(name).setPosition(posX,posY).setSize(sizeX,sizeY);
    }

    public Toggle addToggle(String name, int posX, int posY, int sizeX, int sizeY) {
        return controls.addToggle(name).setPosition(posX,posY).setSize(sizeX,sizeY).setMode(ControlP5.SWITCH);
    }

    public Slider addSlider(String name, int posX, int posY, int sizeX, int sizeY, int rangeO, int range1, int ticksNum) {
        return controls.addSlider(name).setPosition(posX,posY).setSize(sizeX,sizeY).setRange(rangeO,range1)
                       .setNumberOfTickMarks(ticksNum).setSliderMode(Slider.FLEXIBLE).showTickMarks(false);
    }

    public Controller<?> getControl(String name) {
        return controls.getController(name);
    }

}
