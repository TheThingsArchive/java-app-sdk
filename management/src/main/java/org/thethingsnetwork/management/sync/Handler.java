/*
 * The MIT License
 *
 * Copyright (c) 2017 The Things Network
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package org.thethingsnetwork.management.sync;

import java.io.InputStream;
import java.util.List;
import org.thethingsnetwork.account.common.AbstractApplication;
import org.thethingsnetwork.account.sync.auth.token.OAuth2Token;
import org.thethingsnetwork.management.HandlerApplication;
import org.thethingsnetwork.management.HandlerDevice;
import org.thethingsnetwork.management.async.AsyncHandler;

/**
 * This class is a wrapper for the The Things Network handler service
 *
 * @author Romain Cambier
 */
public class Handler {

    private final AsyncHandler wrapped;

    protected Handler(AsyncHandler _wrap) {
        wrapped = _wrap;
    }

    /**
     * Build a Handler wrapper instance
     *
     * @param _credentials A valid authentication token
     * @param _host The handler host
     * @param _port The handler port
     * @param _certificate The handler certificate
     * @return The newly built Handler
     */
    public static Handler from(OAuth2Token _credentials, String _host, int _port, InputStream _certificate) {
        return AsyncHandler.from(_credentials.async(), _host, _port, _certificate)
                .map((AsyncHandler t) -> new Handler(t))
                .toBlocking()
                .single();
    }

    /**
     * Register an application to The Things Network
     *
     * @param _application the application to register
     * @return The newly registered HandlerApplication
     */
    public HandlerApplication registerApplication(AbstractApplication _application) {
        return wrapped.registerApplication(_application)
                .toBlocking()
                .single();
    }

    /**
     * Get an application from the handler service
     *
     * @param _applicationId The id of the application
     * @return The requested HandlerApplication
     */
    public HandlerApplication getApplication(String _applicationId) {
        return wrapped.getApplication(_applicationId)
                .toBlocking()
                .single();
    }

    /**
     * Update (or create) an application on the handler service
     *
     * @param _application The application (new or updated)
     * @return The updated HandlerApplication
     */
    public HandlerApplication setApplication(HandlerApplication _application) {
        return wrapped.setApplication(_application)
                .toBlocking()
                .single();
    }

    /**
     * Delete an application from the handler service
     *
     * @param _application The application
     * @return The deleted HandlerApplication
     */
    public HandlerApplication deleteApplication(HandlerApplication _application) {
        return wrapped.deleteApplication(_application)
                .toBlocking()
                .single();
    }

    /**
     * Get all devices from the handler service
     *
     * @param _application The application to list devices of
     * @return The List of HandlerDevice objects
     */
    public List<HandlerDevice> getDevices(HandlerApplication _application) {
        return wrapped.getDevices(_application)
                .toList()
                .toBlocking()
                .single();
    }

    /**
     * Get a device from the handler service
     *
     * @param _application The application containing the device
     * @param _deviceId The device id
     * @return The HandlerDevice object
     */
    public HandlerDevice getDevice(HandlerApplication _application, String _deviceId) {
        return wrapped.getDevice(_application, _deviceId)
                .toBlocking()
                .single();
    }

    /**
     * Update (or create) a device on the handler service
     *
     * @param _device The device
     * @return The updated HandlerDevice
     */
    public HandlerDevice setDevice(HandlerDevice _device) {
        return wrapped.setDevice(_device)
                .toBlocking()
                .single();

    }

    /**
     * Delete a device on the handler service
     *
     * @param _device The device
     * @return The deleted HandlerDevice
     */
    public HandlerDevice deleteDevice(HandlerDevice _device) {
        return wrapped.deleteDevice(_device)
                .toBlocking()
                .single();
    }

}
