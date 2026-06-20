// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2026
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.bitwig.controller.dualsense;

import de.mossgrabers.bitwig.framework.BitwigSetupFactory;
import de.mossgrabers.bitwig.framework.configuration.SettingsUIImpl;
import de.mossgrabers.bitwig.framework.daw.HostImpl;
import de.mossgrabers.bitwig.framework.extension.AbstractControllerExtensionDefinition;
import de.mossgrabers.controller.dualsense.DualSenseConfiguration;
import de.mossgrabers.controller.dualsense.DualSenseControllerDefinition;
import de.mossgrabers.controller.dualsense.DualSenseControllerSetup;
import de.mossgrabers.controller.dualsense.controller.DualSenseControlSurface;
import de.mossgrabers.framework.controller.IControllerSetup;

import com.bitwig.extension.controller.api.ControllerHost;


/**
 * Definition class for the DualSense controller.
 *
 * @author Jürgen Moßgraber
 */
public class DualSenseControllerExtensionDefinition extends AbstractControllerExtensionDefinition<DualSenseControlSurface, DualSenseConfiguration>
{
    /**
     * Constructor.
     */
    public DualSenseControllerExtensionDefinition ()
    {
        super (new DualSenseControllerDefinition ());
    }


    /** {@inheritDoc} */
    @Override
    protected IControllerSetup<DualSenseControlSurface, DualSenseConfiguration> getControllerSetup (final ControllerHost host)
    {
        return new DualSenseControllerSetup (new HostImpl (host), new BitwigSetupFactory (host), new SettingsUIImpl (host, host.getPreferences ()),
            new SettingsUIImpl (host, host.getDocumentState ()));
    }
}
