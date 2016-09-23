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
import org.thethingsnetwork.java.app.lib.Message;

public class Test {

    public static void main(String[] args) throws MqttException, MalformedURLException, URISyntaxException {

        String region = System.getenv("region");
        String appId = System.getenv("appId");
        String accessKey = System.getenv("accessKey");

        Client client = new Client(region, appId, accessKey);

        client.registerMessageHandler((Message t) -> {
            System.out.println("New message: " + t);
            // Respond to every third message
            if (t.getInt("counter") % 3 == 0) {
                try {
                    // Toggle the LED
                    JSONObject response = new JSONObject().put("led", !t.getBoolean("led"));

                    /**
                     * If you don't have an encoder payload function:
                     * client.send(t.getString("dev_id"), t.getBoolean("led") ? new byte[]{0x00} : new byte[]{0x01}, t.getInt("port"));
                     */
                    System.out.println("Sending: " + response);
                    client.send(t.getString("dev_id"), response, t.getInt("port"));
                } catch (MqttException ex) {
                    System.out.println("Response failed: " + ex.getMessage());
                }
            }
        });

        client.registerActivationHandler((Message t) -> System.out.println("Activation: " + t));

        client.registerErrorHandler((Throwable t) -> System.err.println("error: " + t.getMessage()));

        client.registerConnectHandler((MqttClient t) -> System.out.println("connected !"));

        client.start();
    }

}
