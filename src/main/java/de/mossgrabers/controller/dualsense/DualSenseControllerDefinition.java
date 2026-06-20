// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2026
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.dualsense;

import de.mossgrabers.framework.controller.DefaultControllerDefinition;

import java.util.UUID;


/**
 * Definition class for the Sony DualSense controller extension.
 *
 * @author Jürgen Moßgraber
 */
public class DualSenseControllerDefinition extends DefaultControllerDefinition
{
    private static final UUID EXTENSION_ID = UUID.fromString ("C8D2619F-3AF5-4617-A69C-D9252CCCE8D7");


    /**
     * Constructor.
     */
    public DualSenseControllerDefinition ()
    {
        super (EXTENSION_ID, "DualSense", "Sony", 1, 0);
    }
}
