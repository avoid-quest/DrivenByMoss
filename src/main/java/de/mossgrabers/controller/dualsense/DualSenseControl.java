// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2026
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.dualsense;


/**
 * Controls exposed by the DualSense wired USB input report.
 *
 * @author Jürgen Moßgraber
 */
public enum DualSenseControl
{
    /** Cross button. */
    CROSS ("Cross", true),
    /** Circle button. */
    CIRCLE ("Circle", true),
    /** Square button. */
    SQUARE ("Square", true),
    /** Triangle button. */
    TRIANGLE ("Triangle", true),
    /** Direction pad up. */
    DPAD_UP ("D-pad Up", true),
    /** Direction pad down. */
    DPAD_DOWN ("D-pad Down", true),
    /** Direction pad left. */
    DPAD_LEFT ("D-pad Left", true),
    /** Direction pad right. */
    DPAD_RIGHT ("D-pad Right", true),
    /** L1 button. */
    L1 ("L1", true),
    /** R1 button. */
    R1 ("R1", true),
    /** L2 digital button. */
    L2_BUTTON ("L2 Button", true),
    /** R2 digital button. */
    R2_BUTTON ("R2 Button", true),
    /** Create button. */
    CREATE ("Create", true),
    /** Options button. */
    OPTIONS ("Options", true),
    /** L3 button. */
    L3 ("L3", true),
    /** R3 button. */
    R3 ("R3", true),
    /** PlayStation button. */
    PS ("PS", true),
    /** Mute button. */
    MUTE ("Mute", true),
    /** Touchpad click button. */
    TOUCHPAD_BUTTON ("Touchpad Button", true),
    /** First touch point active state. */
    TOUCH0_ACTIVE ("Touch 0 Active", true),
    /** Second touch point active state. */
    TOUCH1_ACTIVE ("Touch 1 Active", true),

    /** Left stick X axis. */
    LEFT_X ("Left X", false),
    /** Left stick Y axis. */
    LEFT_Y ("Left Y", false),
    /** Right stick X axis. */
    RIGHT_X ("Right X", false),
    /** Right stick Y axis. */
    RIGHT_Y ("Right Y", false),
    /** L2 analog trigger. */
    L2_ANALOG ("L2 Analog", false),
    /** R2 analog trigger. */
    R2_ANALOG ("R2 Analog", false),
    /** First touch point X coordinate. */
    TOUCH0_X ("Touch 0 X", false),
    /** First touch point Y coordinate. */
    TOUCH0_Y ("Touch 0 Y", false),
    /** Second touch point X coordinate. */
    TOUCH1_X ("Touch 1 X", false),
    /** Second touch point Y coordinate. */
    TOUCH1_Y ("Touch 1 Y", false),
    /** Gyroscope X axis. */
    GYRO_X ("Gyro X", false),
    /** Gyroscope Y axis. */
    GYRO_Y ("Gyro Y", false),
    /** Gyroscope Z axis. */
    GYRO_Z ("Gyro Z", false),
    /** Accelerometer X axis. */
    ACCELEROMETER_X ("Accelerometer X", false),
    /** Accelerometer Y axis. */
    ACCELEROMETER_Y ("Accelerometer Y", false),
    /** Accelerometer Z axis. */
    ACCELEROMETER_Z ("Accelerometer Z", false);

    private final String  label;
    private final boolean button;


    /**
     * Constructor.
     *
     * @param label The display label
     * @param button True if this is a button control
     */
    DualSenseControl (final String label, final boolean button)
    {
        this.label = label;
        this.button = button;
    }


    /**
     * Get the display label.
     *
     * @return The label
     */
    public String getLabel ()
    {
        return this.label;
    }


    /**
     * Is this a button control?
     *
     * @return True if this is a button control
     */
    public boolean isButton ()
    {
        return this.button;
    }


    /**
     * Is this a continuous control?
     *
     * @return True if this is a continuous control
     */
    public boolean isContinuous ()
    {
        return !this.button;
    }
}
