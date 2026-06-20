// Copyright (c) 2026 Driven by Avoid contributors
// Derived from DrivenByMoss, copyright (c) 2017-2026 Jürgen Moßgraber
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.dualsense;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.Test;


/**
 * Tests for the DualSense controller definition.
 *
 * @author Driven by Avoid contributors
 */
class DualSenseControllerDefinitionTest
{
    @Test
    void shouldExposeMidiInputWithoutRequiringBitwigUsbHardwareAssignment ()
    {
        final DualSenseControllerDefinition definition = new DualSenseControllerDefinition ();

        assertEquals (1, definition.getNumMidiInPorts ());
        assertEquals (0, definition.getNumMidiOutPorts ());
        assertNull (definition.claimUSBDevice ());
    }
}
