// Copyright (c) 2026 Driven by Avoid contributors
// Derived from DrivenByMoss, copyright (c) 2017-2026 Jürgen Moßgraber
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.dualsense;

import de.mossgrabers.controller.dualsense.controller.DualSenseControlSurface;
import de.mossgrabers.controller.dualsense.controller.IDualSenseCallback;
import de.mossgrabers.framework.command.trigger.clip.NewCommand;
import de.mossgrabers.framework.command.trigger.transport.PlayCommand;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.constants.Resolution;
import de.mossgrabers.framework.daw.data.ISlot;
import de.mossgrabers.framework.daw.midi.IMidiInput;
import de.mossgrabers.framework.daw.midi.INoteInput;
import de.mossgrabers.framework.daw.midi.INoteRepeat;
import de.mossgrabers.framework.daw.midi.MidiConstants;
import de.mossgrabers.framework.utils.ButtonEvent;

import java.util.Optional;


/**
 * Handles configured DualSense functions.
 *
 * @author Driven by Avoid contributors
 */
public class DualSenseFunctionHandler implements IDualSenseCallback
{
    private static final double                                             NOTE_ON_THRESHOLD         = 0.9;
    private static final double                                             NOTE_OFF_THRESHOLD        = 0.09;
    private static final int                                                MIDI_MAX_VALUE            = 127;
    private static final int                                                MIDI_CENTER_VALUE         = 64;
    private static final int                                                MIDI_CENTER_MAX_OFFSET    = MIDI_CENTER_VALUE - 1;
    private static final int                                                PITCHBEND_CENTER_VALUE    = 8192;
    private static final int                                                PITCHBEND_MAX_VALUE       = 16383;

    private final DualSenseControlSurface                                      surface;
    private final IModel                                                       model;
    private final DualSenseConfiguration                                       configuration;
    private final IMidiInput                                                   input;
    private final INoteRepeat                                                  noteRepeat;

    private final NewCommand<DualSenseControlSurface, DualSenseConfiguration>  newCommand;
    private final PlayCommand<DualSenseControlSurface, DualSenseConfiguration> playCommand;

    private final boolean []                                                   playingNotes = new boolean [128];


    /**
     * Constructor.
     *
     * @param surface The surface
     * @param model The model
     */
    public DualSenseFunctionHandler (final DualSenseControlSurface surface, final IModel model)
    {
        this.surface = surface;
        this.model = model;
        this.configuration = this.surface.getConfiguration ();
        this.input = this.surface.getMidiInput ();
        final INoteInput defaultNoteInput = this.input == null ? null : this.input.getDefaultNoteInput ();
        this.noteRepeat = defaultNoteInput == null ? null : defaultNoteInput.getNoteRepeat ();

        this.newCommand = new NewCommand<> (model, surface);
        this.playCommand = new PlayCommand<> (model, surface);
    }


    /** {@inheritDoc} */
    @Override
    public boolean isMapped (final DualSenseControl control)
    {
        return this.configuration.getFunction (control) > DualSenseConfiguration.FUNCTION_OFF;
    }


    /** {@inheritDoc} */
    @Override
    public void process (final DualSenseControl control, final ButtonEvent event)
    {
        final boolean isDown = event == ButtonEvent.DOWN;
        final int function = this.configuration.getFunction (control);
        if (function <= DualSenseConfiguration.FUNCTION_OFF)
            return;

        if (function <= DualSenseConfiguration.FUNCTION_NOTE_127)
        {
            this.handleMidiNote (function, isDown ? 1 : 0);
            return;
        }

        if (function <= DualSenseConfiguration.FUNCTION_CC_127)
        {
            final double value = isDown ? 1 : 0;
            this.handleMidiCC (function, DualSenseConfiguration.FUNCTION_RANGE_127, value, value);
            return;
        }

        switch (function)
        {
            case DualSenseConfiguration.FUNCTION_PITCHBEND:
                this.handleMidiPitchbend (isDown ? 1 : 0);
                break;

            case DualSenseConfiguration.FUNCTION_NOTE_REPEAT_ENABLE:
                if (this.noteRepeat != null)
                    this.noteRepeat.setActive (isDown);
                break;
            case DualSenseConfiguration.FUNCTION_NOTE_REPEAT_PERIOD:
            case DualSenseConfiguration.FUNCTION_NOTE_REPEAT_LENGTH:
                // Not supported for buttons
                break;

            case DualSenseConfiguration.FUNCTION_TRACK_PREVIOUS:
                if (isDown)
                    this.model.getCursorTrack ().selectPrevious ();
                break;
            case DualSenseConfiguration.FUNCTION_TRACK_NEXT:
                if (isDown)
                    this.model.getCursorTrack ().selectNext ();
                break;
            case DualSenseConfiguration.FUNCTION_CLIP_PREVIOUS:
                if (isDown)
                    this.model.getCursorTrack ().getSlotBank ().selectPreviousItem ();
                break;
            case DualSenseConfiguration.FUNCTION_CLIP_NEXT:
                if (isDown)
                    this.model.getCursorTrack ().getSlotBank ().selectNextItem ();
                break;
            case DualSenseConfiguration.FUNCTION_PLAY_CLIP:
                final Optional<ISlot> selectedItem = this.model.getCursorTrack ().getSlotBank ().getSelectedItem ();
                if (selectedItem.isPresent ())
                    selectedItem.get ().launch (isDown, false);
                break;

            case DualSenseConfiguration.FUNCTION_NEW_CLIP:
                if (isDown)
                    this.newCommand.execute ();
                break;

            case DualSenseConfiguration.FUNCTION_TRANSPORT_PLAY:
                this.playCommand.execute (event, isDown ? MIDI_MAX_VALUE : 0);
                break;
            case DualSenseConfiguration.FUNCTION_TRANSPORT_METRONOME:
                if (isDown)
                    this.model.getTransport ().toggleMetronome ();
                break;

            default:
                // No more
                break;
        }
    }


    /** {@inheritDoc} */
    @Override
    public void process (final DualSenseControl control, final float value)
    {
        final int function = this.configuration.getFunction (control);
        if (function <= DualSenseConfiguration.FUNCTION_OFF)
            return;

        final double positive = Math.abs (value);

        if (function <= DualSenseConfiguration.FUNCTION_NOTE_127)
        {
            this.handleMidiNote (function, positive);
            return;
        }

        if (function <= DualSenseConfiguration.FUNCTION_CC_127)
        {
            final int functionRange = this.configuration.getFunctionRange (control);
            this.handleMidiCC (function, functionRange, value, positive);
            return;
        }

        switch (function)
        {
            case DualSenseConfiguration.FUNCTION_PITCHBEND:
                this.handleMidiPitchbend (value);
                break;

            case DualSenseConfiguration.FUNCTION_NOTE_REPEAT_ENABLE:
                if (this.noteRepeat != null)
                    this.noteRepeat.setActive (positive > 0.5);
                break;
            case DualSenseConfiguration.FUNCTION_NOTE_REPEAT_PERIOD:
                if (this.noteRepeat != null)
                    this.noteRepeat.setPeriod (getResolutionValue (positive, false));
                break;
            case DualSenseConfiguration.FUNCTION_NOTE_REPEAT_LENGTH:
                if (this.noteRepeat != null)
                    this.noteRepeat.setNoteLength (getResolutionValue (positive, true));
                break;

            case DualSenseConfiguration.FUNCTION_PLAY_CLIP:
            case DualSenseConfiguration.FUNCTION_NEW_CLIP:
            case DualSenseConfiguration.FUNCTION_TRANSPORT_PLAY:
            case DualSenseConfiguration.FUNCTION_TRANSPORT_METRONOME:
                // Not supported for continuous controls
                break;

            default:
                // No more
                break;
        }
    }


    private static double getResolutionValue (final double position, final boolean reverse)
    {
        final Resolution [] values = Resolution.values ();
        final int index = (int) Math.round (position * (values.length - 1));
        return values[reverse ? values.length - 1 - index : index].getValue ();
    }


    private void handleMidiNote (final int index, final double value)
    {
        if (this.input == null)
            return;

        final int note = index - DualSenseConfiguration.FUNCTION_NOTE_0;

        // Prevent re-trigger with continuous control
        final boolean isOn = value > NOTE_ON_THRESHOLD;
        final boolean isOff = value < NOTE_OFF_THRESHOLD;
        if ((!isOn && !isOff) || isOn == this.playingNotes[note])
            return;

        this.playingNotes[note] = isOn;
        this.input.sendRawMidiEvent (isOn ? MidiConstants.CMD_NOTE_ON : MidiConstants.CMD_NOTE_OFF, note, isOn ? MIDI_MAX_VALUE : 0);
    }


    private void handleMidiCC (final int function, final int functionRange, final double value, final double positive)
    {
        if (this.input == null)
            return;

        final int midiValue;

        switch (functionRange)
        {
            case DualSenseConfiguration.FUNCTION_RANGE_127:
                midiValue = positive < NOTE_OFF_THRESHOLD ? 0 : toMidiValue (positive, MIDI_MAX_VALUE);
                break;
            case DualSenseConfiguration.FUNCTION_RANGE_CENTER_64:
                midiValue = toCenteredMidiValue (value, positive, false);
                break;
            case DualSenseConfiguration.FUNCTION_RANGE_CENTER_64_FLIP:
                midiValue = toCenteredMidiValue (value, positive, true);
                break;
            default:
                // No more
                return;
        }

        this.input.sendRawMidiEvent (MidiConstants.CMD_CC, function - DualSenseConfiguration.FUNCTION_CC_0, midiValue);
    }


    /**
     * Send a MIDI pitchbend message.
     *
     * @param value Value is in the range of [-1,1]
     */
    private void handleMidiPitchbend (final float value)
    {
        if (this.input == null)
            return;

        final int midiValue = toPitchbendValue (value);
        final int msb = midiValue >> 7;
        final int lsb = midiValue & 0x7F;
        this.input.sendRawMidiEvent (MidiConstants.CMD_PITCHBEND, lsb, msb);
    }


    private static int toMidiValue (final double value, final int maximum)
    {
        return (int) Math.min (maximum, Math.max (0, Math.round (value * maximum)));
    }


    private static int toPitchbendValue (final float value)
    {
        if (Math.abs (value) < NOTE_OFF_THRESHOLD)
            return PITCHBEND_CENTER_VALUE;

        return (int) Math.max (Math.min (PITCHBEND_MAX_VALUE, Math.round ((1 + value) / 2.0 * PITCHBEND_MAX_VALUE)), 0);
    }


    private static int toCenteredMidiValue (final double value, final double positive, final boolean flip)
    {
        if (positive < NOTE_OFF_THRESHOLD)
            return MIDI_CENTER_VALUE;

        final int offset = toMidiValue (positive, MIDI_CENTER_MAX_OFFSET);
        if (flip)
            return value < 0 ? MIDI_CENTER_VALUE + offset : MIDI_CENTER_MAX_OFFSET - offset;

        return value < 0 ? MIDI_CENTER_MAX_OFFSET - offset : MIDI_CENTER_VALUE + offset;
    }


}
