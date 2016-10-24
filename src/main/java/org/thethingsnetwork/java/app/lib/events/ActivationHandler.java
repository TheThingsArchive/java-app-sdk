/*
 * Shareif.com CONFIDENTIAL
 * ________________________
 *
 * Copyright 2016 Shareif.com SPRL
 * All Rights Reserved.
 *
 * NOTICE:  All information contained herein is, and remains
 * the property of Shareif.com SPRL and its suppliers,
 * if any.  The intellectual and technical concepts contained
 * herein are proprietary to Shareif.com SPRL
 * and its suppliers and may be covered by Belgian and Foreign Patents,
 * patents in process, and are protected by trade secret or copyright law.
 * Dissemination of this information or reproduction of this material
 * is strictly forbidden unless prior written permission is obtained
 * from Shareif.com SPRL.
 */
package org.thethingsnetwork.java.app.lib.events;

import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.json.JSONObject;

/**
 *
 * @author Romain Cambier
 */
public abstract class ActivationHandler implements EventHandler {

    public abstract void handle(String _devId, JSONObject _data);

    public String getDevId() {
        return null;
    }

    public boolean matches(String _devId) {
        return getDevId() == null || _devId.equals(getDevId());
    }

    @Override
    public void doSubscribe(MqttClient _mqtt) throws MqttException {
        _mqtt.subscribe("+/" + ((getDevId() == null) ? "+" : getDevId()) + "/events/activations");
    }

}
