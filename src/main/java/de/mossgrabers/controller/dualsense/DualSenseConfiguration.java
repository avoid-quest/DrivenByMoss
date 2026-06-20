// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2026
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.dualsense;

import de.mossgrabers.framework.configuration.AbstractConfiguration;
import de.mossgrabers.framework.configuration.IEnumSetting;
import de.mossgrabers.framework.configuration.ISettingsUI;
import de.mossgrabers.framework.controller.valuechanger.IValueChanger;
import de.mossgrabers.framework.daw.IHost;
import de.mossgrabers.framework.daw.midi.ArpeggiatorMode;
import de.mossgrabers.framework.daw.midi.MidiConstants;
import de.mossgrabers.framework.scale.Scales;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;


/**
 * Configuration settings for a DualSense.
 *
 * @author Jürgen Moßgraber
 */
public class DualSenseConfiguration extends AbstractConfiguration
{
    /** The index of Off (no function selected). */
    public static final int                           FUNCTION_OFF                  = 0;
    /** The index of the first note (MIDI note = 0). */
    public static final int                           FUNCTION_NOTE_0               = 1;
    /** The index of the last note (MIDI note = 127). */
    public static final int                           FUNCTION_NOTE_127             = 128;
    /** The index of the first CC. */
    public static final int                           FUNCTION_CC_0                 = 129;
    /** The index of the last CC. */
    public static final int                           FUNCTION_CC_127               = 256;
    /** The index of pitchbend. */
    public static final int                           FUNCTION_PITCHBEND            = 257;
    /** The index of note repeat on/off. */
    public static final int                           FUNCTION_NOTE_REPEAT_ENABLE   = 258;
    /** The index of note repeat period. */
    public static final int                           FUNCTION_NOTE_REPEAT_PERIOD   = 259;
    /** The index of note repeat length. */
    public static final int                           FUNCTION_NOTE_REPEAT_LENGTH   = 260;
    /** The index of select the previous track. */
    public static final int                           FUNCTION_TRACK_PREVIOUS       = 261;
    /** The index of select the next track. */
    public static final int                           FUNCTION_TRACK_NEXT           = 262;
    /** The index of select the previous clip. */
    public static final int                           FUNCTION_CLIP_PREVIOUS        = 263;
    /** The index of select the next clip. */
    public static final int                           FUNCTION_CLIP_NEXT            = 264;
    /** The index of create new clip. */
    public static final int                           FUNCTION_NEW_CLIP             = 265;
    /** The index of play a clip. */
    public static final int                           FUNCTION_PLAY_CLIP            = 266;
    /** The index of transport play/stop. */
    public static final int                           FUNCTION_TRANSPORT_PLAY       = 267;
    /** The index of transport metronome. */
    public static final int                           FUNCTION_TRANSPORT_METRONOME  = 268;

    /** The range is 0-127 on both sides. */
    public static final int                           FUNCTION_RANGE_127            = 0;
    /** The range is 0-64 to the left/top and 64-127 on the right/bottom. */
    public static final int                           FUNCTION_RANGE_CENTER_64      = 1;
    /** Like previous but flipped. */
    public static final int                           FUNCTION_RANGE_CENTER_64_FLIP = 2;

    private static final String                       CATEGORY_DUALSENSE            = "DualSense";

    private static final List<String>                 FUNCTIONS                     = new ArrayList<> ();
    private static final List<String>                 FUNCTION_RANGES               = new ArrayList<> ();
    private static final Map<DualSenseControl, Integer> DEFAULTS                    = new EnumMap<> (DualSenseControl.class);

    static
    {
        FUNCTIONS.add ("Off");
        for (int i = 0; i < 128; i++)
            FUNCTIONS.add ("Note " + Scales.formatNoteAndOctave (i, -3));
        final String [] ccNames = MidiConstants.getCCNames ();
        for (int i = 0; i < 128; i++)
            FUNCTIONS.add ("CC " + ccNames[i]);
        FUNCTIONS.add ("Pitchbend");
        FUNCTIONS.add ("Note Repeat: On/Off");
        FUNCTIONS.add ("Note Repeat: Period (only for axis)");
        FUNCTIONS.add ("Note Repeat: Length (only for axis)");
        FUNCTIONS.add ("Track: Select Previous");
        FUNCTIONS.add ("Track: Select Next");
        FUNCTIONS.add ("Clip: Select Previous");
        FUNCTIONS.add ("Clip: Select Next");
        FUNCTIONS.add ("Clip: New (only for buttons)");
        FUNCTIONS.add ("Clip: Play (only for buttons)");
        FUNCTIONS.add ("Transport: Play/Stop (only for buttons)");
        FUNCTIONS.add ("Transport: Metronome (only for buttons)");

        FUNCTION_RANGES.add ("127 - 0  :  0 - 127");
        FUNCTION_RANGES.add ("0   - 64 : 64 - 127");
        FUNCTION_RANGES.add ("127 - 64 : 64 - 0");

        DEFAULTS.put (DualSenseControl.DPAD_UP, Integer.valueOf (FUNCTION_TRACK_PREVIOUS));
        DEFAULTS.put (DualSenseControl.DPAD_DOWN, Integer.valueOf (FUNCTION_TRACK_NEXT));
        DEFAULTS.put (DualSenseControl.DPAD_LEFT, Integer.valueOf (FUNCTION_CLIP_PREVIOUS));
        DEFAULTS.put (DualSenseControl.DPAD_RIGHT, Integer.valueOf (FUNCTION_CLIP_NEXT));
        DEFAULTS.put (DualSenseControl.OPTIONS, Integer.valueOf (FUNCTION_TRANSPORT_PLAY));
        DEFAULTS.put (DualSenseControl.CREATE, Integer.valueOf (FUNCTION_PLAY_CLIP));
        DEFAULTS.put (DualSenseControl.CROSS, Integer.valueOf (FUNCTION_NOTE_REPEAT_ENABLE));
        DEFAULTS.put (DualSenseControl.CIRCLE, Integer.valueOf (FUNCTION_NEW_CLIP));

        DEFAULTS.put (DualSenseControl.LEFT_X, Integer.valueOf (FUNCTION_PITCHBEND));
        DEFAULTS.put (DualSenseControl.LEFT_Y, Integer.valueOf (FUNCTION_CC_0 + 1));
        DEFAULTS.put (DualSenseControl.RIGHT_X, Integer.valueOf (FUNCTION_CC_0 + 71));
        DEFAULTS.put (DualSenseControl.RIGHT_Y, Integer.valueOf (FUNCTION_CC_0 + 74));
        DEFAULTS.put (DualSenseControl.TOUCH0_X, Integer.valueOf (FUNCTION_CC_0 + 12));
        DEFAULTS.put (DualSenseControl.TOUCH0_Y, Integer.valueOf (FUNCTION_CC_0 + 13));
        DEFAULTS.put (DualSenseControl.TOUCH1_X, Integer.valueOf (FUNCTION_CC_0 + 14));
        DEFAULTS.put (DualSenseControl.TOUCH1_Y, Integer.valueOf (FUNCTION_CC_0 + 15));
    }

    private final Map<DualSenseControl, Integer>      functions        = new EnumMap<> (DualSenseControl.class);
    private final Map<DualSenseControl, Integer>      ranges           = new EnumMap<> (DualSenseControl.class);


    /**
     * Constructor.
     *
     * @param host The DAW host
     * @param valueChanger The value changer
     * @param arpeggiatorModes The available arpeggiator modes
     */
    public DualSenseConfiguration (final IHost host, final IValueChanger valueChanger, final List<ArpeggiatorMode> arpeggiatorModes)
    {
        super (host, valueChanger, arpeggiatorModes);

        for (final DualSenseControl control: DualSenseControl.values ())
        {
            this.functions.put (control, DEFAULTS.getOrDefault (control, Integer.valueOf (FUNCTION_OFF)));
            if (control.isContinuous ())
                this.ranges.put (control, Integer.valueOf (FUNCTION_RANGE_127));
        }
    }


    /** {@inheritDoc} */
    @Override
    public void init (final ISettingsUI globalSettings, final ISettingsUI documentSettings)
    {
        final String buttonCategory = CATEGORY_DUALSENSE + " - Buttons";
        final String continuousCategory = CATEGORY_DUALSENSE + " - Continuous";

        for (final DualSenseControl control: DualSenseControl.values ())
        {
            final int initialFunction = this.functions.get (control).intValue ();
            final String category = control.isButton () ? buttonCategory : continuousCategory;
            final IEnumSetting functionSetting = globalSettings.getEnumSetting (control.getLabel (), category, FUNCTIONS, FUNCTIONS.get (initialFunction));
            functionSetting.addValueObserver (value -> this.functions.put (control, Integer.valueOf (FUNCTIONS.indexOf (value))));

            if (control.isContinuous ())
            {
            final IEnumSetting rangeSetting = globalSettings.getEnumSetting (control.getLabel () + " Range (CC only)", continuousCategory, FUNCTION_RANGES,
                FUNCTION_RANGES.get (FUNCTION_RANGE_127));
                rangeSetting.addValueObserver (value -> this.ranges.put (control, Integer.valueOf (FUNCTION_RANGES.indexOf (value))));
            }
        }

        this.activateNoteRepeatSetting (documentSettings);
    }


    /**
     * Get the selected function for a DualSense control.
     *
     * @param control The control
     * @return The selected function index
     */
    public int getFunction (final DualSenseControl control)
    {
        final Integer function = this.functions.get (control);
        return function == null ? -1 : function.intValue ();
    }


    /**
     * Get the selected continuous range for a DualSense control.
     *
     * @param control The control
     * @return The selected range index
     */
    public int getFunctionRange (final DualSenseControl control)
    {
        final Integer range = this.ranges.get (control);
        return range == null ? -1 : range.intValue ();
    }
}
