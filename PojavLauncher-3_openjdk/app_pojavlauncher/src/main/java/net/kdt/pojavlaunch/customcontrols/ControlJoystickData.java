package net.kdt.pojavlaunch.customcontrols;

import androidx.annotation.Keep;

@Keep
public class ControlJoystickData extends ControlData {
    public boolean forwardLock;
    public boolean absolute;

    public ControlJoystickData() {
        super("Joystick", new int[]{}, "${margin}", "${bottom} - ${margin}", 90, 90, false);
        this.forwardLock = true;
        this.absolute = false;
    }

    public ControlJoystickData(ControlJoystickData data) {
        super(data);
        this.forwardLock = data.forwardLock;
        this.absolute = data.absolute;
    }
}
