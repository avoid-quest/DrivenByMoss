// Copyright (c) 2026 Driven by Avoid contributors
// Derived from DrivenByMoss, copyright (c) 2017-2026 Jürgen Moßgraber
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.dualsense;

import java.util.EnumMap;


/**
 * Parser for the wired USB DualSense input report 0x01.
 *
 * @author Driven by Avoid contributors
 */
public class DualSenseReportParser
{
    /** USB input report ID. */
    public static final int REPORT_ID_USB_INPUT = 0x01;

    private static final int USB_INPUT_PAYLOAD_LENGTH = 63;
    private static final int USB_INPUT_REPORT_LENGTH  = USB_INPUT_PAYLOAD_LENGTH + 1;

    private static final int TOUCHPAD_WIDTH           = 1920;
    private static final int TOUCHPAD_HEIGHT          = 1080;

    private static final int OFFSET_LEFT_X            = 0;
    private static final int OFFSET_LEFT_Y            = 1;
    private static final int OFFSET_RIGHT_X           = 2;
    private static final int OFFSET_RIGHT_Y           = 3;
    private static final int OFFSET_L2_ANALOG         = 4;
    private static final int OFFSET_R2_ANALOG         = 5;
    private static final int OFFSET_BUTTONS0          = 7;
    private static final int OFFSET_BUTTONS1          = 8;
    private static final int OFFSET_BUTTONS2          = 9;
    private static final int OFFSET_GYRO              = 15;
    private static final int OFFSET_ACCELEROMETER     = 21;
    private static final int OFFSET_TOUCH0            = 32;
    private static final int OFFSET_TOUCH1            = 36;


    /**
     * Parse a HID input report callback.
     *
     * @param reportID The callback report ID
     * @param data The callback data
     * @param length The callback data length
     * @return The parse result
     */
    public ParseResult parse (final byte reportID, final byte [] data, final int length)
    {
        final int safeLength = data == null || length < 0 ? 0 : Math.min (length, data.length);
        final int payloadStart = getPayloadStart (reportID, data, safeLength);
        if (payloadStart < 0 || safeLength - payloadStart < USB_INPUT_PAYLOAD_LENGTH)
            return ParseResult.unsupported (Byte.toUnsignedInt (reportID), safeLength);

        final EnumMap<DualSenseControl, Boolean> buttons = new EnumMap<> (DualSenseControl.class);
        final EnumMap<DualSenseControl, Double> values = new EnumMap<> (DualSenseControl.class);
        final EnumMap<DualSenseControl, Integer> rawValues = new EnumMap<> (DualSenseControl.class);

        this.parseButtons (data, payloadStart, buttons, rawValues);
        this.parseContinuous (data, payloadStart, buttons, values, rawValues);

        return ParseResult.supported (new DualSenseState (buttons, values, rawValues), Byte.toUnsignedInt (reportID), safeLength);
    }


    private static int getPayloadStart (final byte reportID, final byte [] data, final int length)
    {
        final int id = Byte.toUnsignedInt (reportID);
        if (data == null || length <= 0)
            return -1;

        if (length >= USB_INPUT_REPORT_LENGTH && Byte.toUnsignedInt (data[0]) == REPORT_ID_USB_INPUT)
            return 1;

        if (id == REPORT_ID_USB_INPUT && length >= USB_INPUT_PAYLOAD_LENGTH)
            return 0;

        return -1;
    }


    private void parseButtons (final byte [] data, final int payloadStart, final EnumMap<DualSenseControl, Boolean> buttons, final EnumMap<DualSenseControl, Integer> rawValues)
    {
        final int buttons0 = unsignedByte (data, payloadStart + OFFSET_BUTTONS0);
        final int buttons1 = unsignedByte (data, payloadStart + OFFSET_BUTTONS1);
        final int buttons2 = unsignedByte (data, payloadStart + OFFSET_BUTTONS2);

        final int hat = buttons0 & 0x0F;
        putButton (buttons, rawValues, DualSenseControl.DPAD_UP, hat == 0 || hat == 1 || hat == 7);
        putButton (buttons, rawValues, DualSenseControl.DPAD_RIGHT, hat >= 1 && hat <= 3);
        putButton (buttons, rawValues, DualSenseControl.DPAD_DOWN, hat >= 3 && hat <= 5);
        putButton (buttons, rawValues, DualSenseControl.DPAD_LEFT, hat >= 5 && hat <= 7);

        putButton (buttons, rawValues, DualSenseControl.SQUARE, (buttons0 & 0x10) != 0);
        putButton (buttons, rawValues, DualSenseControl.CROSS, (buttons0 & 0x20) != 0);
        putButton (buttons, rawValues, DualSenseControl.CIRCLE, (buttons0 & 0x40) != 0);
        putButton (buttons, rawValues, DualSenseControl.TRIANGLE, (buttons0 & 0x80) != 0);

        putButton (buttons, rawValues, DualSenseControl.L1, (buttons1 & 0x01) != 0);
        putButton (buttons, rawValues, DualSenseControl.R1, (buttons1 & 0x02) != 0);
        putButton (buttons, rawValues, DualSenseControl.L2_BUTTON, (buttons1 & 0x04) != 0);
        putButton (buttons, rawValues, DualSenseControl.R2_BUTTON, (buttons1 & 0x08) != 0);
        putButton (buttons, rawValues, DualSenseControl.CREATE, (buttons1 & 0x10) != 0);
        putButton (buttons, rawValues, DualSenseControl.OPTIONS, (buttons1 & 0x20) != 0);
        putButton (buttons, rawValues, DualSenseControl.L3, (buttons1 & 0x40) != 0);
        putButton (buttons, rawValues, DualSenseControl.R3, (buttons1 & 0x80) != 0);

        putButton (buttons, rawValues, DualSenseControl.PS, (buttons2 & 0x01) != 0);
        putButton (buttons, rawValues, DualSenseControl.TOUCHPAD_BUTTON, (buttons2 & 0x02) != 0);
        putButton (buttons, rawValues, DualSenseControl.MUTE, (buttons2 & 0x04) != 0);
    }


    private void parseContinuous (final byte [] data, final int payloadStart, final EnumMap<DualSenseControl, Boolean> buttons,
        final EnumMap<DualSenseControl, Double> values, final EnumMap<DualSenseControl, Integer> rawValues)
    {
        putCenteredByte (data, payloadStart + OFFSET_LEFT_X, values, rawValues, DualSenseControl.LEFT_X);
        putCenteredByte (data, payloadStart + OFFSET_LEFT_Y, values, rawValues, DualSenseControl.LEFT_Y);
        putCenteredByte (data, payloadStart + OFFSET_RIGHT_X, values, rawValues, DualSenseControl.RIGHT_X);
        putCenteredByte (data, payloadStart + OFFSET_RIGHT_Y, values, rawValues, DualSenseControl.RIGHT_Y);

        putUnsignedByte (data, payloadStart + OFFSET_L2_ANALOG, values, rawValues, DualSenseControl.L2_ANALOG);
        putUnsignedByte (data, payloadStart + OFFSET_R2_ANALOG, values, rawValues, DualSenseControl.R2_ANALOG);

        this.parseTouchPoint (data, payloadStart + OFFSET_TOUCH0, DualSenseControl.TOUCH0_ACTIVE, DualSenseControl.TOUCH0_X, DualSenseControl.TOUCH0_Y, buttons, values, rawValues);
        this.parseTouchPoint (data, payloadStart + OFFSET_TOUCH1, DualSenseControl.TOUCH1_ACTIVE, DualSenseControl.TOUCH1_X, DualSenseControl.TOUCH1_Y, buttons, values, rawValues);

        putMotionValue (data, payloadStart + OFFSET_GYRO, values, rawValues, DualSenseControl.GYRO_X);
        putMotionValue (data, payloadStart + OFFSET_GYRO + 2, values, rawValues, DualSenseControl.GYRO_Y);
        putMotionValue (data, payloadStart + OFFSET_GYRO + 4, values, rawValues, DualSenseControl.GYRO_Z);
        putMotionValue (data, payloadStart + OFFSET_ACCELEROMETER, values, rawValues, DualSenseControl.ACCELEROMETER_X);
        putMotionValue (data, payloadStart + OFFSET_ACCELEROMETER + 2, values, rawValues, DualSenseControl.ACCELEROMETER_Y);
        putMotionValue (data, payloadStart + OFFSET_ACCELEROMETER + 4, values, rawValues, DualSenseControl.ACCELEROMETER_Z);
    }


    private void parseTouchPoint (final byte [] data, final int offset, final DualSenseControl activeControl, final DualSenseControl xControl,
        final DualSenseControl yControl, final EnumMap<DualSenseControl, Boolean> buttons, final EnumMap<DualSenseControl, Double> values,
        final EnumMap<DualSenseControl, Integer> rawValues)
    {
        final int contact = unsignedByte (data, offset);
        final boolean active = (contact & 0x80) == 0;
        putButton (buttons, rawValues, activeControl, active);

        int x = 0;
        int y = 0;
        if (active)
        {
            final int xLow = unsignedByte (data, offset + 1);
            final int packed = unsignedByte (data, offset + 2);
            final int yHigh = unsignedByte (data, offset + 3);
            x = xLow | ((packed & 0x0F) << 8);
            y = (packed >> 4) | (yHigh << 4);
        }

        putContinuous (values, rawValues, xControl, normalizeTouch (x, TOUCHPAD_WIDTH), x);
        putContinuous (values, rawValues, yControl, normalizeTouch (y, TOUCHPAD_HEIGHT), y);
    }


    private static void putButton (final EnumMap<DualSenseControl, Boolean> buttons, final EnumMap<DualSenseControl, Integer> rawValues,
        final DualSenseControl control, final boolean pressed)
    {
        buttons.put (control, Boolean.valueOf (pressed));
        rawValues.put (control, Integer.valueOf (pressed ? 1 : 0));
    }


    private static void putContinuous (final EnumMap<DualSenseControl, Double> values, final EnumMap<DualSenseControl, Integer> rawValues,
        final DualSenseControl control, final double value, final int rawValue)
    {
        values.put (control, Double.valueOf (value));
        rawValues.put (control, Integer.valueOf (rawValue));
    }


    private static void putCenteredByte (final byte [] data, final int offset, final EnumMap<DualSenseControl, Double> values,
        final EnumMap<DualSenseControl, Integer> rawValues, final DualSenseControl control)
    {
        final int rawValue = unsignedByte (data, offset);
        putContinuous (values, rawValues, control, normalizeCenteredByte (rawValue), rawValue);
    }


    private static void putUnsignedByte (final byte [] data, final int offset, final EnumMap<DualSenseControl, Double> values,
        final EnumMap<DualSenseControl, Integer> rawValues, final DualSenseControl control)
    {
        final int rawValue = unsignedByte (data, offset);
        putContinuous (values, rawValues, control, normalizeUnsignedByte (rawValue), rawValue);
    }


    private static void putMotionValue (final byte [] data, final int offset, final EnumMap<DualSenseControl, Double> values,
        final EnumMap<DualSenseControl, Integer> rawValues, final DualSenseControl control)
    {
        final int rawValue = signed16 (data, offset);
        putContinuous (values, rawValues, control, normalizeSigned16 (rawValue), rawValue);
    }


    private static int unsignedByte (final byte [] data, final int index)
    {
        return Byte.toUnsignedInt (data[index]);
    }


    private static int signed16 (final byte [] data, final int index)
    {
        return (short) (unsignedByte (data, index) | unsignedByte (data, index + 1) << 8);
    }


    private static double normalizeCenteredByte (final int value)
    {
        return clamp (value >= 128 ? (value - 128) / 127.0 : (value - 128) / 128.0);
    }


    private static double normalizeUnsignedByte (final int value)
    {
        return clamp (value / 255.0);
    }


    private static double normalizeTouch (final int value, final int range)
    {
        return clamp (value / (double) (range - 1));
    }


    private static double normalizeSigned16 (final int value)
    {
        return clamp (value < 0 ? value / 32768.0 : value / 32767.0);
    }


    private static double clamp (final double value)
    {
        return Math.max (-1, Math.min (1, value));
    }


    /**
     * Result of a parse attempt.
     */
    public static class ParseResult
    {
        private final boolean         supported;
        private final DualSenseState  state;
        private final int             reportID;
        private final int             length;


        private ParseResult (final boolean supported, final DualSenseState state, final int reportID, final int length)
        {
            this.supported = supported;
            this.state = state;
            this.reportID = reportID;
            this.length = length;
        }


        /**
         * Create a supported result.
         *
         * @param state The parsed state
         * @param reportID The callback report ID
         * @param length The callback data length
         * @return The parse result
         */
        public static ParseResult supported (final DualSenseState state, final int reportID, final int length)
        {
            return new ParseResult (true, state, reportID, length);
        }


        /**
         * Create an unsupported result.
         *
         * @param reportID The callback report ID
         * @param length The callback data length
         * @return The parse result
         */
        public static ParseResult unsupported (final int reportID, final int length)
        {
            return new ParseResult (false, null, reportID, length);
        }


        /**
         * Is the report supported?
         *
         * @return True if supported
         */
        public boolean isSupported ()
        {
            return this.supported;
        }


        /**
         * Get the parsed state.
         *
         * @return The parsed state
         */
        public DualSenseState getState ()
        {
            return this.state;
        }


        /**
         * Get the callback report ID.
         *
         * @return The report ID
         */
        public int getReportID ()
        {
            return this.reportID;
        }


        /**
         * Get the callback data length.
         *
         * @return The data length
         */
        public int getLength ()
        {
            return this.length;
        }
    }
}
