// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2026
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.dualsense.controller;

import de.mossgrabers.controller.dualsense.DualSenseControl;
import de.mossgrabers.framework.utils.ButtonEvent;


/**
 * Interface for a callback when data from a DualSense controller is received.
 *
 * @author Jürgen Moßgraber
 */
public interface IDualSenseCallback
{
    /**
     * Test if the control is mapped to a function.
     *
     * @param control The control
     * @return True if mapped
     */
    boolean isMapped (DualSenseControl control);


    /**
     * Called when ready to process a button result.
     *
     * @param control The pressed or released DualSense button
     * @param event The button event
     */
    void process (DualSenseControl control, ButtonEvent event);


    /**
     * Called when ready to process a continuous result.
     *
     * @param control The continuous DualSense control
     * @param value The new normalized value
     */
    void process (DualSenseControl control, float value);
}
