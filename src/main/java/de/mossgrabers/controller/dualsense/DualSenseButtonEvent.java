// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2026
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.dualsense;


/**
 * A changed DualSense button state.
 *
 * @param control The changed control
 * @param pressed True if the button is pressed
 *
 * @author Jürgen Moßgraber
 */
public record DualSenseButtonEvent (DualSenseControl control, boolean pressed)
{
    // Intentionally empty
}
