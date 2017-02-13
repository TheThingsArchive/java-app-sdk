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
package org.thethingsnetwork.data.common.events;

import org.thethingsnetwork.data.common.Subscribable;
import org.thethingsnetwork.data.common.messages.DataMessage;

/**
 * Handler protoype for device uplink messages
 *
 * @author Romain Cambier
 */
public abstract class UplinkHandler implements EventHandler {

    public abstract void handle(String _devId, DataMessage _data);

    public abstract String getDevId();

    public abstract String getField();

    public boolean isField() {
        return getField() != null;
    }

    public boolean matches(String _devId, String _field) {
        if (getDevId() != null && !getDevId().equals(_devId)) {
            return false;
        }
        if (getField() != null) {
            return !(_field == null || !getField().equals(_field));
        } else {
            return _field == null;
        }
    }

    @Override
    public void subscribe(Subscribable _client) throws Exception {
        if (getField() == null) {
            _client.subscribe(new String[]{
                _client.getWordWildcard(),
                _client.getWordWildcard(),
                (getDevId() == null) ? _client.getWordWildcard() : getDevId(),
                "up"
            });
        } else {
            _client.subscribe(new String[]{
                _client.getWordWildcard(),
                _client.getWordWildcard(),
                (getDevId() == null) ? _client.getWordWildcard() : getDevId(),
                "up",
                getField()
            });
        }
    }
}
