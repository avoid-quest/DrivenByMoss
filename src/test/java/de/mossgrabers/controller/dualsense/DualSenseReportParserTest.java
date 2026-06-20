// Copyright (c) 2026 Driven by Avoid contributors
// Derived from DrivenByMoss, copyright (c) 2017-2026 Jürgen Moßgraber
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.dualsense;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;

import org.junit.jupiter.api.Test;


/**
 * Tests for the DualSense USB input report parser.
 *
 * @author Driven by Avoid contributors
 */
class DualSenseReportParserTest
{
    private static final double EPSILON = 0.000001;


    @Test
    void shouldParseNeutralReportWithReportIdInData ()
    {
        final DualSenseState state = parseFullReport (neutralReport (), (byte) 0);

        assertFalse (state.isPressed (DualSenseControl.CROSS));
        assertFalse (state.isPressed (DualSenseControl.DPAD_UP));
        assertFalse (state.isPressed (DualSenseControl.TOUCH0_ACTIVE));
        assertEquals (0, state.getValue (DualSenseControl.LEFT_X), EPSILON);
        assertEquals (0, state.getValue (DualSenseControl.LEFT_Y), EPSILON);
        assertEquals (0, state.getValue (DualSenseControl.L2_ANALOG), EPSILON);
        assertEquals (0, state.getValue (DualSenseControl.TOUCH0_X), EPSILON);
    }


    @Test
    void shouldParseReportIdProvidedByCallback ()
    {
        final byte [] report = neutralReport ();
        report[8] = (byte) (0x08 | 0x20);
        final byte [] payload = Arrays.copyOfRange (report, 1, report.length);

        final DualSenseReportParser parser = new DualSenseReportParser ();
        final DualSenseReportParser.ParseResult result = parser.parse ((byte) DualSenseReportParser.REPORT_ID_USB_INPUT, payload, payload.length);

        assertTrue (result.isSupported ());
        assertTrue (result.getState ().isPressed (DualSenseControl.CROSS));
    }


    @Test
    void shouldParseButtonsAndHat ()
    {
        final byte [] report = neutralReport ();
        report[8] = (byte) (0x01 | 0x10 | 0x20 | 0x40 | 0x80);
        report[9] = (byte) 0xFF;
        report[10] = 0x07;

        final DualSenseState state = parseFullReport (report, (byte) DualSenseReportParser.REPORT_ID_USB_INPUT);

        assertTrue (state.isPressed (DualSenseControl.DPAD_UP));
        assertTrue (state.isPressed (DualSenseControl.DPAD_RIGHT));
        assertFalse (state.isPressed (DualSenseControl.DPAD_DOWN));
        assertFalse (state.isPressed (DualSenseControl.DPAD_LEFT));
        assertTrue (state.isPressed (DualSenseControl.SQUARE));
        assertTrue (state.isPressed (DualSenseControl.CROSS));
        assertTrue (state.isPressed (DualSenseControl.CIRCLE));
        assertTrue (state.isPressed (DualSenseControl.TRIANGLE));
        assertTrue (state.isPressed (DualSenseControl.L1));
        assertTrue (state.isPressed (DualSenseControl.R1));
        assertTrue (state.isPressed (DualSenseControl.L2_BUTTON));
        assertTrue (state.isPressed (DualSenseControl.R2_BUTTON));
        assertTrue (state.isPressed (DualSenseControl.CREATE));
        assertTrue (state.isPressed (DualSenseControl.OPTIONS));
        assertTrue (state.isPressed (DualSenseControl.L3));
        assertTrue (state.isPressed (DualSenseControl.R3));
        assertTrue (state.isPressed (DualSenseControl.PS));
        assertTrue (state.isPressed (DualSenseControl.TOUCHPAD_BUTTON));
        assertTrue (state.isPressed (DualSenseControl.MUTE));
    }


    @Test
    void shouldNormalizeSticksAndTriggers ()
    {
        final byte [] report = neutralReport ();
        report[1] = 0x00;
        report[2] = (byte) 0xFF;
        report[3] = (byte) 0x80;
        report[4] = 0x40;
        report[5] = 0x40;
        report[6] = (byte) 0xFF;

        final DualSenseState state = parseFullReport (report, (byte) DualSenseReportParser.REPORT_ID_USB_INPUT);

        assertEquals (-1, state.getValue (DualSenseControl.LEFT_X), EPSILON);
        assertEquals (1, state.getValue (DualSenseControl.LEFT_Y), EPSILON);
        assertEquals (0, state.getValue (DualSenseControl.RIGHT_X), EPSILON);
        assertEquals (-0.5, state.getValue (DualSenseControl.RIGHT_Y), EPSILON);
        assertEquals (64 / 255.0, state.getValue (DualSenseControl.L2_ANALOG), EPSILON);
        assertEquals (1, state.getValue (DualSenseControl.R2_ANALOG), EPSILON);
    }


    @Test
    void shouldParseTouchPoints ()
    {
        final byte [] report = neutralReport ();
        setTouchPoint (report, 33, 0x345, 0x2A3);
        setTouchPoint (report, 37, 0x120, 0x210);

        final DualSenseState state = parseFullReport (report, (byte) DualSenseReportParser.REPORT_ID_USB_INPUT);

        assertTrue (state.isPressed (DualSenseControl.TOUCH0_ACTIVE));
        assertTrue (state.isPressed (DualSenseControl.TOUCH1_ACTIVE));
        assertEquals (0x345, state.getRawValue (DualSenseControl.TOUCH0_X));
        assertEquals (0x2A3, state.getRawValue (DualSenseControl.TOUCH0_Y));
        assertEquals (0x120, state.getRawValue (DualSenseControl.TOUCH1_X));
        assertEquals (0x210, state.getRawValue (DualSenseControl.TOUCH1_Y));
        assertEquals (0x345 / 1919.0, state.getValue (DualSenseControl.TOUCH0_X), EPSILON);
        assertEquals (0x2A3 / 1079.0, state.getValue (DualSenseControl.TOUCH0_Y), EPSILON);
    }


    @Test
    void shouldDecodeSignedMotionValues ()
    {
        final byte [] report = neutralReport ();
        putSigned16 (report, 16, 0x1234);
        putSigned16 (report, 18, -32768);
        putSigned16 (report, 20, 32767);
        putSigned16 (report, 22, -2);
        putSigned16 (report, 24, 256);
        putSigned16 (report, 26, -1024);

        final DualSenseState state = parseFullReport (report, (byte) DualSenseReportParser.REPORT_ID_USB_INPUT);

        assertEquals (0x1234, state.getRawValue (DualSenseControl.GYRO_X));
        assertEquals (-32768, state.getRawValue (DualSenseControl.GYRO_Y));
        assertEquals (32767, state.getRawValue (DualSenseControl.GYRO_Z));
        assertEquals (-2, state.getRawValue (DualSenseControl.ACCELEROMETER_X));
        assertEquals (256, state.getRawValue (DualSenseControl.ACCELEROMETER_Y));
        assertEquals (-1024, state.getRawValue (DualSenseControl.ACCELEROMETER_Z));
        assertEquals (-1, state.getValue (DualSenseControl.GYRO_Y), EPSILON);
        assertEquals (1, state.getValue (DualSenseControl.GYRO_Z), EPSILON);
    }


    @Test
    void shouldRejectUnsupportedReports ()
    {
        final DualSenseReportParser parser = new DualSenseReportParser ();
        final DualSenseReportParser.ParseResult result = parser.parse ((byte) 2, new byte [10], 10);

        assertFalse (result.isSupported ());
    }


    private static DualSenseState parseFullReport (final byte [] report, final byte reportID)
    {
        final DualSenseReportParser parser = new DualSenseReportParser ();
        final DualSenseReportParser.ParseResult result = parser.parse (reportID, report, report.length);

        assertTrue (result.isSupported ());
        return result.getState ();
    }


    private static byte [] neutralReport ()
    {
        final byte [] report = new byte [64];
        report[0] = DualSenseReportParser.REPORT_ID_USB_INPUT;
        report[1] = (byte) 0x80;
        report[2] = (byte) 0x80;
        report[3] = (byte) 0x80;
        report[4] = (byte) 0x80;
        report[8] = 0x08;
        report[33] = (byte) 0x80;
        report[37] = (byte) 0x80;
        return report;
    }


    private static void setTouchPoint (final byte [] report, final int offset, final int x, final int y)
    {
        report[offset] = 0;
        report[offset + 1] = (byte) (x & 0xFF);
        report[offset + 2] = (byte) ((x >> 8 & 0x0F) | (y & 0x0F) << 4);
        report[offset + 3] = (byte) (y >> 4 & 0xFF);
    }


    private static void putSigned16 (final byte [] report, final int offset, final int value)
    {
        report[offset] = (byte) (value & 0xFF);
        report[offset + 1] = (byte) (value >> 8 & 0xFF);
    }
}
