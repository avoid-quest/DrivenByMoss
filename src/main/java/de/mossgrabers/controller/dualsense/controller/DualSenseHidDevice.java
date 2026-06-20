// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2026
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.dualsense.controller;

import de.mossgrabers.controller.dualsense.DualSenseButtonEvent;
import de.mossgrabers.controller.dualsense.DualSenseContinuousEvent;
import de.mossgrabers.controller.dualsense.DualSenseReportParser;
import de.mossgrabers.controller.dualsense.DualSenseReportParser.ParseResult;
import de.mossgrabers.controller.dualsense.DualSenseState;
import de.mossgrabers.framework.daw.IHost;
import de.mossgrabers.framework.utils.ButtonEvent;

import purejavahidapi.HidDevice;
import purejavahidapi.HidDeviceInfo;
import purejavahidapi.PureJavaHidApi;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;


/**
 * HID bridge for a wired USB DualSense.
 *
 * @author Jürgen Moßgraber
 */
public class DualSenseHidDevice
{
    private static final short          VENDOR_ID                = 0x054C;
    private static final short          PRODUCT_ID               = 0x0CE6;

    private final IHost                 host;
    private final DualSenseReportParser parser = new DualSenseReportParser ();
    private final Set<String>           loggedUnsupportedReports = new HashSet<> ();
    private final IDualSenseCallback    callback;

    private HidDevice                   hidDevice;
    private DualSenseState              previousState;


    /**
     * Constructor.
     *
     * @param host The controller host
     * @param callback Callback for events coming from the selected DualSense
     */
    public DualSenseHidDevice (final IHost host, final IDualSenseCallback callback)
    {
        this.host = host;
        this.callback = callback;

        try
        {
            final Optional<HidDeviceInfo> deviceInfo = lookupDevice ();
            if (deviceInfo.isEmpty ())
            {
                host.error ("Could not find a connected DualSense HID device.");
                return;
            }

            this.hidDevice = PureJavaHidApi.openDevice (deviceInfo.get ());
            if (this.hidDevice == null)
                throw new IOException ("openDevice returned null.");

            this.hidDevice.setInputReportListener ( (source, id, data, length) -> this.processHIDMessage (id, data, length));
            this.hidDevice.setDeviceRemovalListener (source -> this.hidDevice = null);
        }
        catch (final IOException ex)
        {
            this.hidDevice = null;
            host.error ("Could not open DualSense HID connection: " + ex.getMessage ());
        }
    }


    /**
     * Stop receiving HID data and close USB device.
     */
    public void shutdown ()
    {
        if (this.hidDevice == null)
            return;

        final HidDevice device = this.hidDevice;
        this.hidDevice = null;
        device.close ();
    }


    private synchronized void processHIDMessage (final byte reportID, final byte [] data, final int length)
    {
        final ParseResult result = this.parser.parse (reportID, data, length);
        if (!result.isSupported ())
        {
            this.logUnsupportedReport (result);
            return;
        }

        final DualSenseState state = result.getState ();
        if (this.previousState != null)
            this.processEvents (this.previousState, state);

        this.previousState = state;
    }


    private void processEvents (final DualSenseState previousState, final DualSenseState state)
    {
        final IDualSenseCallback currentCallback = this.callback;
        if (currentCallback == null)
            return;

        final List<DualSenseButtonEvent> buttonEvents = state.getButtonEvents (previousState);
        final List<DualSenseContinuousEvent> continuousEvents = state.getContinuousEvents (previousState);

        for (final DualSenseButtonEvent event: buttonEvents)
        {
            if (currentCallback.isMapped (event.control ()))
                this.host.scheduleTask ( () -> currentCallback.process (event.control (), event.pressed () ? ButtonEvent.DOWN : ButtonEvent.UP), 0);
        }

        for (final DualSenseContinuousEvent event: continuousEvents)
        {
            if (currentCallback.isMapped (event.control ()))
                this.host.scheduleTask ( () -> currentCallback.process (event.control (), (float) event.value ()), 0);
        }
    }


    private void logUnsupportedReport (final ParseResult result)
    {
        final String key = result.getReportID () + ":" + result.getLength ();
        if (this.loggedUnsupportedReports.add (key))
            this.host.println ("DualSense: Unsupported HID report " + result.getReportID () + " length " + result.getLength ());
    }


    private static Optional<HidDeviceInfo> lookupDevice ()
    {
        for (final HidDeviceInfo info: PureJavaHidApi.enumerateDevices ())
        {
            if (info.getVendorId () == VENDOR_ID && info.getProductId () == PRODUCT_ID)
                return Optional.of (info);
        }
        return Optional.empty ();
    }
}
