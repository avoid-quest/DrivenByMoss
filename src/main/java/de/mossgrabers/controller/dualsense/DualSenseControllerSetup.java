// Copyright (c) 2026 Driven by Avoid contributors
// Derived from DrivenByMoss, copyright (c) 2017-2026 Jürgen Moßgraber
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.dualsense;

import de.mossgrabers.controller.dualsense.controller.DualSenseControlSurface;
import de.mossgrabers.framework.configuration.ISettingsUI;
import de.mossgrabers.framework.controller.AbstractControllerSetup;
import de.mossgrabers.framework.controller.ISetupFactory;
import de.mossgrabers.framework.controller.valuechanger.TwosComplementValueChanger;
import de.mossgrabers.framework.daw.IHost;
import de.mossgrabers.framework.daw.ModelSetup;
import de.mossgrabers.framework.daw.midi.IMidiAccess;
import de.mossgrabers.framework.daw.midi.IMidiInput;


/**
 * Support for Sony DualSense controllers.
 *
 * @author Driven by Avoid contributors
 */
public class DualSenseControllerSetup extends AbstractControllerSetup<DualSenseControlSurface, DualSenseConfiguration>
{
    /**
     * Constructor.
     *
     * @param host The DAW host
     * @param factory The factory
     * @param globalSettings The global settings
     * @param documentSettings The document (project) specific settings
     */
    public DualSenseControllerSetup (final IHost host, final ISetupFactory factory, final ISettingsUI globalSettings, final ISettingsUI documentSettings)
    {
        super (factory, host, globalSettings, documentSettings);

        // This is currently not used but necessary to prevent crashes with parameters
        this.valueChanger = new TwosComplementValueChanger (1024, 10);

        this.configuration = new DualSenseConfiguration (host, this.valueChanger, factory.getArpeggiatorModes ());
    }


    /** {@inheritDoc} */
    @Override
    protected void createScales ()
    {
        // Not used
    }


    /** {@inheritDoc} */
    @Override
    protected void createModel ()
    {
        final ModelSetup ms = new ModelSetup ();
        ms.enableMainDrumDevice (false);
        ms.setNumTracks (1);
        ms.setNumScenes (100);
        ms.setNumSends (0);
        ms.setNumDevicesInBank (0);
        ms.setNumDeviceLayers (0);
        ms.setNumParamPages (0);
        ms.setNumParams (0);
        ms.setNumDrumPadLayers (0);
        ms.setNumFilterColumnEntries (0);
        ms.setNumResults (0);

        this.model = this.factory.createModel (this.configuration, this.colorManager, this.valueChanger, this.scales, ms);
    }


    /** {@inheritDoc} */
    @Override
    protected void createSurface ()
    {
        final IMidiAccess midiAccess = this.factory.createMidiAccess ();
        final IMidiInput input = midiAccess.createInput ("DualSense");
        this.surfaces.add (new DualSenseControlSurface (this.host, this.configuration, this.colorManager, input, this.model));
    }


    /** {@inheritDoc} */
    @Override
    protected void createObservers ()
    {
        super.createObservers ();

        this.createNoteRepeatObservers (this.configuration, this.getSurface ());
    }


    /** {@inheritDoc} */
    @Override
    public void startup ()
    {
        // Intentionally empty
    }
}
