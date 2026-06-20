// Copyright (c) 2026 Driven by Avoid contributors
// Derived from DrivenByMoss, copyright (c) 2017-2026 Jürgen Moßgraber
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.dualsense.controller;

import de.mossgrabers.controller.dualsense.DualSenseConfiguration;
import de.mossgrabers.controller.dualsense.DualSenseFunctionHandler;
import de.mossgrabers.framework.controller.AbstractControlSurface;
import de.mossgrabers.framework.controller.color.ColorManager;
import de.mossgrabers.framework.daw.IHost;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.midi.IMidiInput;


/**
 * The DualSense control surface.
 *
 * @author Driven by Avoid contributors
 */
public class DualSenseControlSurface extends AbstractControlSurface<DualSenseConfiguration>
{
    private final DualSenseHidDevice hidDevice;


    /**
     * Constructor.
     *
     * @param host The host
     * @param configuration The configuration
     * @param colorManager The color manager
     * @param input The MIDI input
     * @param model The model
     */
    public DualSenseControlSurface (final IHost host, final DualSenseConfiguration configuration, final ColorManager colorManager, final IMidiInput input, final IModel model)
    {
        super (host, configuration, colorManager, null, input, null, 10, 10);

        this.hidDevice = new DualSenseHidDevice (host, new DualSenseFunctionHandler (this, model));
    }


    /** {@inheritDoc} */
    @Override
    public void flush ()
    {
        // Intentionally empty
    }


    /** {@inheritDoc} */
    @Override
    protected void internalShutdown ()
    {
        this.hidDevice.shutdown ();
    }
}
