// Copyright (c) 2026 Driven by Avoid contributors
// Derived from DrivenByMoss, copyright (c) 2017-2026 Jürgen Moßgraber
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.dualsense;


/**
 * A changed DualSense continuous value.
 *
 * @param control The changed control
 * @param value The normalized value
 *
 * @author Driven by Avoid contributors
 */
public record DualSenseContinuousEvent (DualSenseControl control, double value)
{
    // Intentionally empty
}
