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
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended;
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

    public Client(Region _region, String _appId, String _appAccessKey) {
        this(_region, _appId, _appAccessKey, false);
    }

    public Client(Region _region, String _appId, String _appAccessKey, boolean _ssl) {
        this((_ssl ? "ssl://" : "tcp://") + _region.toUrl() + (_ssl ? ":8883" : ":1883"), _appId, _appAccessKey);
    }

    public Client(String _broker, String _appId, String _appAccessKey) {
        broker = _broker;
        appId = _appId;
        connOpts.setUserName(_appId);
        connOpts.setPassword(_appAccessKey.toCharArray());
    }

    public Client registerHandler(ConnectHandler _handler) {
        if (mqttClient != null) {
            throw new RuntimeException("Can not be called while client is running");
        }
        connectHandler = _handler;
        return this;
    }

    public Client registerHandler(ErrorHandler _handler) {
        if (mqttClient != null) {
            throw new RuntimeException("Can not be called while client is running");
        }
        errorHandler = _handler;
        return this;
    }

    public Client registerHandler(ActivationHandler _handler) {
        if (mqttClient != null) {
            throw new RuntimeException("Can not be called while client is running");
        }
        activationHandler = _handler;
        return this;
    }

    public Client registerHandler(MessageHandler _handler) {
        if (mqttClient != null) {
            throw new RuntimeException("Can not be called while client is running");
        }
        messageHandler = _handler;
        return this;
    }

    public Client setMqttPersistence(MqttClientPersistence _persistence) {
        if (mqttClient != null) {
            throw new RuntimeException("Can not be called while client is running");
        }
        persistence = _persistence;
        return this;
    }

    public MqttConnectOptions getMqttConnectOptions() {
        if (mqttClient != null) {
            throw new RuntimeException("Can not be called while client is running");
        }
        return connOpts;
    }

    public Client start(String... _topics) throws MqttException {
        if (mqttClient != null) {
            throw new RuntimeException("Already connected");
        }
        mqttClient = new MqttClient(broker, MqttClient.generateClientId(), persistence);
        mqttClient.connect(connOpts);
        mqttClient.setCallback(new MqttCallbackExtended() {
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

            @Override
            public void connectComplete(boolean reconnect, String serverURI) {
                if (connectHandler != null) {
                    connectHandler.accept(mqttClient);
                }
            }
        });
        mqttClient.subscribe(_topics);
        return this;
    }

    public Client start() throws MqttException {
        return start("+/devices/+/activations", "+/devices/+/up");
    }

    public Client stop() throws MqttException {
        if (mqttClient == null) {
            throw new RuntimeException("Not connected");
        }
        return stop(5000);
    }

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

    public Client stopNow() throws MqttException {
        if (mqttClient == null) {
            throw new RuntimeException("Not connected");
        }
        mqttClient.disconnectForcibly(0, 0);
        mqttClient = null;
        return this;
    }

    public void send(String _devId, byte[] _payload, int _port) throws MqttException {
        JSONObject data = new JSONObject();
        data.put("payload_raw", Base64.getEncoder().encodeToString(_payload));
        data.put("port", _port != 0 ? _port : 1);
        mqttClient.publish(appId + "/devices/" + _devId + "/down", data.toString().getBytes(), 0, false);
    }

}
