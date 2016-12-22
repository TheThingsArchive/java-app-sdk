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

import java.util.List;
import org.thethingsnetwork.account.sync.auth.token.OAuth2Token;
import org.thethingsnetwork.management.async.AsyncDiscovery;
import org.thethingsnetwork.management.async.AsyncHandler;

/**
 *
 * @author Romain Cambier
 */
public class Discovery {

    private final AsyncDiscovery wrapped;

    protected Discovery(AsyncDiscovery _wrap) {
        wrapped = _wrap;
    }

    public static Discovery from(String _host, int _port) {
        return AsyncDiscovery.from(_host, _port)
                .map((AsyncDiscovery t) -> new Discovery(t))
                .toBlocking()
                .single();
    }

    public static Discovery getDefault() {
        return from(AsyncDiscovery.HOST, AsyncDiscovery.PORT);
    }

    public Handler getHandler(OAuth2Token _creds, String _handlerId) {
        return wrapped.getHandler(_creds.async(), _handlerId)
                .map((AsyncHandler t) -> new Handler(t))
                .toBlocking()
                .single();
    }

    public List<Handler> getHandlers(OAuth2Token _creds) {
        return wrapped.getHandlers(_creds.async())
                .map((AsyncHandler t) -> new Handler(t))
                .toList()
                .toBlocking()
                .single();
    }
}
