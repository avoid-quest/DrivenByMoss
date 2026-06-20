// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2026
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.dualsense;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;


/**
 * Parsed DualSense input state.
 *
 * @author Jürgen Moßgraber
 */
public class DualSenseState
{
    private final EnumMap<DualSenseControl, Boolean> buttons;
    private final EnumMap<DualSenseControl, Double>  values;
    private final EnumMap<DualSenseControl, Integer> rawValues;


    /**
     * Constructor.
     *
     * @param buttons The button states
     * @param values The normalized continuous values
     * @param rawValues The raw decoded values
     */
    public DualSenseState (final EnumMap<DualSenseControl, Boolean> buttons, final EnumMap<DualSenseControl, Double> values, final EnumMap<DualSenseControl, Integer> rawValues)
    {
        this.buttons = new EnumMap<> (buttons);
        this.values = new EnumMap<> (values);
        this.rawValues = new EnumMap<> (rawValues);
    }


    /**
     * Get all button states.
     *
     * @return The button states
     */
    public Map<DualSenseControl, Boolean> getButtons ()
    {
        return Collections.unmodifiableMap (this.buttons);
    }


    /**
     * Get all normalized continuous values.
     *
     * @return The normalized values
     */
    public Map<DualSenseControl, Double> getValues ()
    {
        return Collections.unmodifiableMap (this.values);
    }


    /**
     * Test if a button is currently pressed.
     *
     * @param control The button control
     * @return True if pressed
     */
    public boolean isPressed (final DualSenseControl control)
    {
        final Boolean pressed = this.buttons.get (control);
        return pressed != null && pressed.booleanValue ();
    }


    /**
     * Get a normalized continuous value.
     *
     * @param control The continuous control
     * @return The normalized value
     */
    public double getValue (final DualSenseControl control)
    {
        final Double value = this.values.get (control);
        return value == null ? 0 : value.doubleValue ();
    }


    /**
     * Get a raw decoded value.
     *
     * @param control The control
     * @return The raw value
     */
    public int getRawValue (final DualSenseControl control)
    {
        final Integer value = this.rawValues.get (control);
        return value == null ? 0 : value.intValue ();
    }


    /**
     * Get changed button states compared to a previous state.
     *
     * @param previous The previous state
     * @return The button events
     */
    public List<DualSenseButtonEvent> getButtonEvents (final DualSenseState previous)
    {
        if (previous == null)
            return Collections.emptyList ();

        final List<DualSenseButtonEvent> events = new ArrayList<> ();
        for (final DualSenseControl control: DualSenseControl.values ())
        {
            if (!control.isButton ())
                continue;

            final boolean pressed = this.isPressed (control);
            if (pressed != previous.isPressed (control))
                events.add (new DualSenseButtonEvent (control, pressed));
        }
        return events;
    }


    /**
     * Get changed continuous values compared to a previous state.
     *
     * @param previous The previous state
     * @return The continuous events
     */
    public List<DualSenseContinuousEvent> getContinuousEvents (final DualSenseState previous)
    {
        if (previous == null)
            return Collections.emptyList ();

        final List<DualSenseContinuousEvent> events = new ArrayList<> ();
        for (final DualSenseControl control: DualSenseControl.values ())
        {
            if (!control.isContinuous ())
                continue;

            final double value = this.getValue (control);
            if (Double.compare (value, previous.getValue (control)) != 0)
                events.add (new DualSenseContinuousEvent (control, value));
        }
        return events;
    }
}
