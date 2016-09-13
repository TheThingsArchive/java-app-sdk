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
package org.thethingsnetwork.java.app.lib;

import java.io.IOException;
import java.util.Base64;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttClientPersistence;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.json.JSONObject;
import org.thethingsnetwork.java.app.lib.handlers.ActivationHandler;
import org.thethingsnetwork.java.app.lib.handlers.ConnectHandler;
import org.thethingsnetwork.java.app.lib.handlers.ErrorHandler;
import org.thethingsnetwork.java.app.lib.handlers.MessageHandler;

/**
 * This is the base class to be used to interact with The Things Network Handler
 *
 * @author Romain Cambier <me@romaincambier.be>
 */
public class Client {

    /**
     * Connection settings
     */
    private final String broker;
    private final String appId;
    private MqttClientPersistence persistence = new MemoryPersistence();
    private final MqttConnectOptions connOpts = new MqttConnectOptions();

    /**
     * Event handlers
     */
    private ConnectHandler connectHandler;
    private ErrorHandler errorHandler;
    private ActivationHandler activationHandler;
    private MessageHandler messageHandler;

    /**
     * Runtime vars
     */
    private MqttClient mqttClient;

    /**
     * Create a new Client from an official TTN broker
     *
     * @param _region The broker region to be used
     * @param _appId Your appId (or appEUI for staging)
     * @param _appAccessKey Your appAccessKey
     */
    public Client(Region _region, String _appId, String _appAccessKey) {
        this(_region, _appId, _appAccessKey, false);
    }

    /**
     * Create a new Client from an official TTN broker
     *
     * @param _region The broker region to be used
     * @param _appId Your appId (or appEUI for staging)
     * @param _appAccessKey Your appAccessKey
     * @param _ssl whether to use ssl for the connection
     */
    public Client(Region _region, String _appId, String _appAccessKey, boolean _ssl) {
        this((_ssl ? "ssl://" : "tcp://") + _region.toUrl() + (_ssl ? ":8883" : ":1883"), _appId, _appAccessKey);
    }

    /**
     * Create a new Client from a custom broker
     *
     * @param _broker The broker address, including protocol and port
     * @param _appId Your appId (or appEUI for staging)
     * @param _appAccessKey Your appAccessKey
     */
    public Client(String _broker, String _appId, String _appAccessKey) {
        broker = _broker;
        appId = _appId;
        connOpts.setUserName(_appId);
        connOpts.setPassword(_appAccessKey.toCharArray());
    }

    /**
     * Register a connection event handler
     *
     * @param _handler The connection event handler
     * @return the Client instance
     */
    public Client registerHandler(ConnectHandler _handler) {
        if (mqttClient != null) {
            throw new RuntimeException("Can not be called while client is running");
        }
        connectHandler = _handler;
        return this;
    }

    /**
     * Register an error event handler
     *
     * @param _handler The error event handler
     * @return the Client instance
     */
    public Client registerHandler(ErrorHandler _handler) {
        if (mqttClient != null) {
            throw new RuntimeException("Can not be called while client is running");
        }
        errorHandler = _handler;
        return this;
    }

    /**
     * Register an activation event handler
     *
     * @param _handler The activation event handler
     * @return the Client instance
     */
    public Client registerHandler(ActivationHandler _handler) {
        if (mqttClient != null) {
            throw new RuntimeException("Can not be called while client is running");
        }
        activationHandler = _handler;
        return this;
    }

    /**
     * Register a message event handler
     *
     * @param _handler The message event handler
     * @return the Client instance
     */
    public Client registerHandler(MessageHandler _handler) {
        if (mqttClient != null) {
            throw new RuntimeException("Can not be called while client is running");
        }
        messageHandler = _handler;
        return this;
    }

    /**
     * Change the default Mqtt persistence settings
     *
     * @param _persistence A custom persistence setting
     * @return the Client instance
     */
    public Client setMqttPersistence(MqttClientPersistence _persistence) {
        if (mqttClient != null) {
            throw new RuntimeException("Can not be called while client is running");
        }
        persistence = _persistence;
        return this;
    }

    /**
     * Access to the connection options used for mqtt
     *
     * @return the connection options
     */
    public MqttConnectOptions getMqttConnectOptions() {
        if (mqttClient != null) {
            throw new RuntimeException("Can not be called while client is running");
        }
        return connOpts;
    }

    /**
     * Start the client and subscribe to provided topics
     *
     * @param _topics The topics to subscribe to
     * @return the Client instance
     * @throws MqttException in case something goes wrong
     */
    public Client start(String... _topics) throws MqttException {
        if (mqttClient != null) {
            throw new RuntimeException("Already connected");
        }
        mqttClient = new MqttClient(broker, MqttClient.generateClientId(), persistence);
        mqttClient.connect(connOpts);
        mqttClient.setCallback(new MqttCallback() {
            @Override
            public void connectionLost(Throwable cause) {
                mqttClient = null;
                if (errorHandler != null) {
                    errorHandler.accept(new IOException("Connection lost", cause));
                }
            }

            @Override
            public void messageArrived(String topic, MqttMessage message) throws Exception {
                String[] tokens = topic.split("\\/");
                if (tokens.length != 4) {
                    if (errorHandler != null) {
                        errorHandler.accept(new IllegalArgumentException("Wrong topic received: " + topic));
                    }
                    return;
                }
                switch (tokens[3]) {
                    case "up":
                        if (messageHandler != null) {
                            messageHandler.accept(new Mesage(tokens[0], tokens[2], new JSONObject(new String(message.getPayload()))));
                        }
                        break;
                    case "activations":
                        if (activationHandler != null) {
                            activationHandler.accept(new Mesage(tokens[0], tokens[2], new JSONObject(new String(message.getPayload()))));
                        }
                        break;
                    default:
                        if (errorHandler != null) {
                            errorHandler.accept(new IllegalArgumentException("Wrong topic received: " + topic));
                        }
                }
            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken token) {
                /**
                 * Not supported for now
                 */
            }
        });
        if (connectHandler != null) {
            connectHandler.accept(mqttClient);
        }
        mqttClient.subscribe(_topics);
        return this;
    }

    /**
     * Start the client and subscribe to activations and uplink
     *
     * @return the Client instance
     * @throws MqttException in case something goes wrong
     */
    public Client start() throws MqttException {
        return start("+/devices/+/activations", "+/devices/+/up");
    }

    /**
     * Stop the client after max. 5000 ms
     *
     * @return the Client instance
     * @throws MqttException in case something goes wrong
     */
    public Client stop() throws MqttException {
        if (mqttClient == null) {
            throw new RuntimeException("Not connected");
        }
        return stop(5000);
    }

    /**
     * Stop the client after max. the provided timeout
     *
     * @param _timeout The disconnect timeout
     * @return the Client instance
     * @throws MqttException in case something goes wrong
     */
    public Client stop(long _timeout) throws MqttException {
        if (mqttClient == null) {
            throw new RuntimeException("Not connected");
        }
        mqttClient.disconnect(_timeout);
        if (!mqttClient.isConnected()) {
            mqttClient = null;
        }
        return this;
    }

    /**
     * Force destroy the client in case stop() does not work.
     *
     * @return the Client instance
     * @throws MqttException in case something goes wrong
     */
    public Client stopNow() throws MqttException {
        if (mqttClient == null) {
            throw new RuntimeException("Not connected");
        }
        mqttClient.disconnectForcibly(0, 0);
        mqttClient = null;
        return this;
    }

    /**
     * Send a downlink message
     *
     * @param _devId The devId (devEUI for staging) to send the message to
     * @param _payload The payload to be sent
     * @param _port The port to use for the message
     * @throws MqttException in case something goes wrong
     */
    public void send(String _devId, byte[] _payload, int _port) throws MqttException {
        JSONObject data = new JSONObject();
        data.put("payload_raw", Base64.getEncoder().encodeToString(_payload));
        data.put("port", _port != 0 ? _port : 1);
        mqttClient.publish(appId + "/devices/" + _devId + "/down", data.toString().getBytes(), 0, false);
    }

}
