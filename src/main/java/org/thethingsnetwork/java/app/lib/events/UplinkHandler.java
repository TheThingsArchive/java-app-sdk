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
package org.thethingsnetwork.java.app.lib.events;

import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.thethingsnetwork.java.app.lib.Message;

/**
 *
 * @author Romain Cambier
 */
public abstract class UplinkHandler implements EventHandler {

    public abstract void handle(String _devId, Object _data);

    /**
     * Add a device filter
     *
     * @return the devId we want
     */
    public String getDevId() {
        return null;
    }

    /**
     * Add a field filter
     *
     * @return the field we want
     */
    public String getField() {
        return null;
    }

    public boolean matches(String _devId) {
        return getDevId() == null || _devId.equals(getDevId());
    }

    public Object transform(String _data) {
        if (getField() == null) {
            return new Message(_data);
        }
        return _data;
    }

    @Override
    public void subscribe(MqttClient _mqtt) throws MqttException {
        _mqtt.subscribe("+/+/" + ((getDevId() == null) ? "+" : getDevId()) + "/up" + ((getField() == null) ? "" : ("/" + getField())));
    }

}
