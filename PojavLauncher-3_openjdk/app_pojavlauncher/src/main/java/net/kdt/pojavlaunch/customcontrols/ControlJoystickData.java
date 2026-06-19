package net.kdt.pojavlaunch.customcontrols;

public class ControlJoystickData extends ControlData {
    public boolean forwardLock = false;
    public boolean absolute = false;

    public ControlJoystickData() {
        super("joystick", new int[]{}, 50, 50);
        setWidth(80);
        setHeight(80);
        isHideable = true;
    }

    public ControlJoystickData(ControlJoystickData other) {
        super(other.name, other.keycodes, 50, 50);
        this.dynamicX = other.dynamicX;
        this.dynamicY = other.dynamicY;
        this.isToggle = other.isToggle;
        this.passThruEnabled = other.passThruEnabled;
        this.name = other.name;
        this.keycodes = other.keycodes;
        this.opacity = other.opacity;
        this.bgColor = other.bgColor;
        this.strokeColor = other.strokeColor;
        this.strokeWidth = other.strokeWidth;
        this.cornerRadius = other.cornerRadius;
        this.isSwipeable = other.isSwipeable;
        this.displayInGame = other.displayInGame;
        this.displayInMenu = other.displayInMenu;
        this.bitmapTag = other.bitmapTag;
        this.forwardLock = other.forwardLock;
        this.absolute = other.absolute;
        setWidth(other.getWidth());
        setHeight(other.getHeight());
    }
}
