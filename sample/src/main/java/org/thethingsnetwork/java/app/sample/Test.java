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
package org.thethingsnetwork.java.app.sample;

import java.net.MalformedURLException;
import java.net.URISyntaxException;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.json.JSONObject;
import org.thethingsnetwork.java.app.lib.Client;
import org.thethingsnetwork.java.app.lib.events.ActivationHandler;
import org.thethingsnetwork.java.app.lib.events.ConnectHandler;
import org.thethingsnetwork.java.app.lib.events.ErrorHandler;
import org.thethingsnetwork.java.app.lib.events.UplinkHandler;

public class Test {

    public static void main(String[] args) throws MqttException, MalformedURLException, URISyntaxException {

        String region = System.getenv("region");
        String appId = System.getenv("appId");
        String accessKey = System.getenv("accessKey");

        Client client = new Client(region, appId, accessKey);

        client.onUplink(new UplinkHandler() {
            @Override
            public void handle(String _devId, Object _data) {
                System.out.println("New message from " + _devId + ": " + _data);
                try {
                    // Toggle the LED
                    JSONObject response = new JSONObject().put("led", !(boolean) _data);

                    /**
                     * If you don't have an encoder payload function:
                     * client.send(_devId, ((boolean) _data) ? new byte[]{0x00} : new byte[]{0x01}, 0);
                     */
                    System.out.println("Sending: " + response);
                    client.send(_devId, response, 0);
                } catch (MqttException ex) {
                    System.out.println("Response failed: " + ex.getMessage());
                }

            }

            @Override
            public String getDevId() {
                return "0102030405060708";
            }

            @Override
            public String getField() {
                return "led";
            }
        });

        client.onActivation(new ActivationHandler() {
            @Override
            public void handle(String _devId, JSONObject _data) {
                System.out.println("Activation: " + _devId + ", data: " + _data);
            }
        });

        client.onError(new ErrorHandler() {
            @Override
            public void handle(Throwable _error) {
                System.err.println("error: " + _error.getMessage());
            }
        });

        client.onConnected(new ConnectHandler() {
            @Override
            public void handle(MqttClient _client) {
                System.out.println("connected !");
            }
        });

        client.start();
    }

}
