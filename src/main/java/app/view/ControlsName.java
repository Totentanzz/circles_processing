package app.view;

public enum ControlsName {
    resume("resume"),
    pause("pause"),
    rand("rand colors"),
    reset("reset"),
    lines("lines");

    private String value;

    ControlsName(String value) {
        this.value = value;
    }

    public String getValue(){
        return value;
    }
}
