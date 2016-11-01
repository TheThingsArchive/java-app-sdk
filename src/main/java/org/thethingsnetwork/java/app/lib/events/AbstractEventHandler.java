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
import org.json.JSONObject;

/**
 *
 * @author Romain Cambier
 */
public abstract class AbstractEventHandler implements EventHandler {

    public abstract void handle(String _devId, String _event, JSONObject _data);

    public String getDevId() {
        return null;
    }

    public String getEvent() {
        return null;
    }

    public boolean matches(String _devId, String _event) {
        if (getDevId() != null && !_devId.equals(getDevId())) {
            return false;
        }
        if (getEvent() != null && !_event.equals(getEvent())) {
            return false;
        }
        return true;
    }

    @Override
    public void doSubscribe(MqttClient _mqtt) throws MqttException {
        _mqtt.subscribe("+/" + ((getDevId() == null) ? "+" : getDevId()) + "/events/" + ((getEvent() == null) ? "+" : getEvent()));
    }

}
