package app.view;

public enum ControlsName {
    resumeBtn("resume"),
    pauseBtn("pause"),
    randBtn("rand colors"),
    resetBtn("reset"),
    linesTgl("lines"),
    circlesSld("circles"),
    speedSld("speed");

    private String value;

    ControlsName(String value) {
        this.value = value;
    }

    public String getValue(){
        return value;
    }
}
