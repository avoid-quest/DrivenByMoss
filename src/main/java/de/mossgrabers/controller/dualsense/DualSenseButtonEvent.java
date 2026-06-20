// Copyright (c) 2026 Driven by Avoid contributors
// Derived from DrivenByMoss, copyright (c) 2017-2026 Jürgen Moßgraber
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.dualsense;


/**
 * A changed DualSense button state.
 *
 * @param control The changed control
 * @param pressed True if the button is pressed
 *
 * @author Driven by Avoid contributors
 */
public record DualSenseButtonEvent (DualSenseControl control, boolean pressed)
{
    // Intentionally empty
}
