/*
 * The MIT License
 *
 * Copyright (c) 2016 The Things Network
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
 *
 * @author Romain Cambier
 */
public class Handler {

    private final AsyncHandler wrapped;

    protected Handler(AsyncHandler _wrap) {
        wrapped = _wrap;
    }

    public static Handler from(OAuth2Token _credentials, String _host, int _port, InputStream _certificate) {
        return AsyncHandler.from(_credentials.async(), _host, _port, _certificate)
                .map((AsyncHandler t) -> new Handler(t))
                .toBlocking()
                .single();
    }

    public HandlerApplication registerApplication(AbstractApplication _application) {
        return wrapped.registerApplication(_application)
                .toBlocking()
                .single();
    }

    public HandlerApplication getApplication(String _applicationId) {
        return wrapped.getApplication(_applicationId)
                .toBlocking()
                .single();
    }

    public HandlerApplication setApplication(HandlerApplication _application) {
        return wrapped.setApplication(_application)
                .toBlocking()
                .single();
    }

    public HandlerApplication deleteApplication(HandlerApplication _application) {
        return wrapped.deleteApplication(_application)
                .toBlocking()
                .single();
    }

    public List<HandlerDevice> getDevices(HandlerApplication _application) {
        return wrapped.getDevices(_application)
                .toList()
                .toBlocking()
                .single();
    }

    public HandlerDevice getDevice(HandlerApplication _application, String _deviceId) {
        return wrapped.getDevice(_application, _deviceId)
                .toBlocking()
                .single();
    }

    public HandlerDevice setDevice(HandlerDevice _device) {
        return wrapped.setDevice(_device)
                .toBlocking()
                .single();

    }

    public HandlerDevice deleteDevice(HandlerDevice _device) {
        return wrapped.deleteDevice(_device)
                .toBlocking()
                .single();
    }

}
