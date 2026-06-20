// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2026
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.dualsense;


/**
 * A changed DualSense continuous value.
 *
 * @param control The changed control
 * @param value The normalized value
 *
 * @author Jürgen Moßgraber
 */
public record DualSenseContinuousEvent (DualSenseControl control, double value)
{
    // Intentionally empty
}
