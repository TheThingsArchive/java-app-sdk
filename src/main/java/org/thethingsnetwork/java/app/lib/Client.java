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

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Base64;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.StringJoiner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttClientPersistence;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.json.JSONObject;
import org.thethingsnetwork.java.app.lib.events.AbstractEventHandler;
import org.thethingsnetwork.java.app.lib.events.ActivationHandler;
import org.thethingsnetwork.java.app.lib.events.ConnectHandler;
import org.thethingsnetwork.java.app.lib.events.ErrorHandler;
import org.thethingsnetwork.java.app.lib.events.EventHandler;
import org.thethingsnetwork.java.app.lib.events.UplinkHandler;

/**
 * This is the base class to be used to interact with The Things Network Handler
 *
 * @author Romain Cambier
 */
public class Client {

    /**
     * Connection settings
     */
    private final String broker;
    private final String appId;
    private MqttClientPersistence persistence = new MemoryPersistence();
    private final MqttConnectOptions connOpts;

    /**
     * Event settings
     */
    private final ExecutorService executor = Executors.newCachedThreadPool();
    private final Map<Class, List<EventHandler>> handlers = new HashMap<>();

    /**
     * Runtime vars
     */
    private MqttClient mqttClient;

    /**
     * Create a new Client from a custom broker
     *
     * @param _broker The broker address, including protocol and port
     * @param _appId Your appId (or appEUI for staging)
     * @param _appAccessKey Your appAccessKey
     * @throws java.net.URISyntaxException if the provided broker address is malformed
     */
    public Client(String _broker, String _appId, String _appAccessKey) throws URISyntaxException {
        this(_broker, _appId, _appAccessKey, null);
    }

    /**
     * Create a new Client from a custom broker
     *
     * @param _broker The broker address, including protocol and port
     * @param _appId Your appId (or appEUI for staging)
     * @param _appAccessKey Your appAccessKey
     * @param _connOpts Connection options to be used
     * @throws java.net.URISyntaxException if the provided broker address is malformed
     */
    public Client(String _broker, String _appId, String _appAccessKey, MqttConnectOptions _connOpts) throws URISyntaxException {
        broker = validateBroker(_broker);
        appId = _appId;
        if (_connOpts != null) {
            connOpts = _connOpts;
        } else {
            connOpts = new MqttConnectOptions();
        }
        connOpts.setUserName(_appId);
        connOpts.setPassword(_appAccessKey.toCharArray());
    }

    private String validateBroker(String _source) throws URISyntaxException {

        URI tempBroker = new URI(_source.contains(".") ? _source : (_source + ".thethings.network"));

        if ("tcp".equals(tempBroker.getScheme())) {
            if (tempBroker.getPort() == -1) {
                return tempBroker.toString() + ":1883";
            }
        } else if ("ssl".equals(tempBroker.getScheme())) {
            if (tempBroker.getPort() == -1) {
                return tempBroker.toString() + ":8883";
            }
        } else {
            return "tcp://" + tempBroker.getPath() + ":1883";
        }

        return tempBroker.toString();
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
     * Start the client
     *
     * @return the Client instance
     * @throws MqttException in case something goes wrong
     */
    public Client start() throws MqttException {
        if (mqttClient != null) {
            throw new RuntimeException("Already connected");
        }
        mqttClient = new MqttClient(broker, MqttClient.generateClientId(), persistence);
        mqttClient.connect(connOpts);
        mqttClient.setCallback(new MqttCallback() {
            @Override
            public void connectionLost(Throwable cause) {
                mqttClient = null;
                if (handlers.containsKey(ErrorHandler.class)) {
                    for (EventHandler handler : handlers.get(ErrorHandler.class)) {
                        executor.submit(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    ((ErrorHandler) handler).handle(cause);
                                } catch (Exception ex) {
                                    ex.printStackTrace();
                                }
                            }
                        });
                    }
                }
            }

            @Override
            public void messageArrived(String topic, MqttMessage message) throws Exception {
                String[] tokens = topic.split("\\/");
                if (tokens.length < 4) {
                    return;
                }
                switch (tokens[3]) {
                    case "up":
                        if (handlers.containsKey(UplinkHandler.class)) {
                            for (EventHandler handler : handlers.get(UplinkHandler.class)) {
                                executor.submit(new Runnable() {
                                    @Override
                                    public void run() {
                                        try {
                                            UplinkHandler uh = (UplinkHandler) handler;
                                            if (uh.matches(tokens[2])) {
                                                uh.handle(tokens[2], uh.transform(new String(message.getPayload())));
                                            }
                                        } catch (Exception ex) {
                                            ex.printStackTrace();
                                        }
                                    }
                                });
                            }
                        }
                        break;
                    case "events":
                        if (tokens.length > 5) {
                            switch (tokens[4]) {
                                case "activations":
                                    if (handlers.containsKey(ActivationHandler.class)) {
                                        for (EventHandler handler : handlers.get(ActivationHandler.class)) {
                                            executor.submit(new Runnable() {
                                                @Override
                                                public void run() {
                                                    try {
                                                        ActivationHandler ah = (ActivationHandler) handler;
                                                        if (ah.matches(tokens[2])) {
                                                            ah.handle(tokens[2], new JSONObject(new String(message.getPayload())));
                                                        }
                                                    } catch (Exception ex) {
                                                        ex.printStackTrace();
                                                    }
                                                }
                                            });
                                        }
                                    }
                                    break;
                                default:
                                    if (handlers.containsKey(AbstractEventHandler.class)) {
                                        for (EventHandler handler : handlers.get(AbstractEventHandler.class)) {
                                            executor.submit(new Runnable() {
                                                @Override
                                                public void run() {
                                                    try {
                                                        AbstractEventHandler aeh = (AbstractEventHandler) handler;
                                                        String event = concat(4, tokens);
                                                        if (aeh.matches(tokens[2], event)) {
                                                            aeh.handle(tokens[2], event, new JSONObject(new String(message.getPayload())));
                                                        }
                                                    } catch (Exception ex) {
                                                        ex.printStackTrace();
                                                    }
                                                }
                                            });
                                        }
                                    }
                            }
                        }
                        break;
                }
            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken token) {
                /**
                 * Not supported for now
                 */
            }
        });

        for (List<EventHandler> ehl : handlers.values()) {
            for (EventHandler eh : ehl) {
                eh.getTopic(mqttClient);
            }
        }

        if (handlers.containsKey(ConnectHandler.class)) {
            for (EventHandler handler : handlers.get(ConnectHandler.class)) {
                executor.submit(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            ((ConnectHandler) handler).handle(mqttClient);
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                    }
                });
            }
        }
        return this;
    }

    private String concat(int _ignore, String[] _tokens) {
        StringJoiner sj = new StringJoiner("/");
        for (int i = _ignore; i < _tokens.length; i++) {
            sj.add(_tokens[i]);
        }
        return sj.toString();
    }

    /**
     * Stop the client after max. 5000 ms
     *
     * @return the Client instance
     * @throws MqttException in case something goes wrong
     */
    public Client end() throws MqttException, InterruptedException {
        if (mqttClient == null) {
            throw new RuntimeException("Not connected");
        }
        return end(5000);
    }

    /**
     * Stop the client after max. the provided timeout
     *
     * @param _timeout The disconnect timeout
     * @return the Client instance
     * @throws MqttException in case something goes wrong
     */
    public Client end(long _timeout) throws MqttException, InterruptedException {
        if (mqttClient == null) {
            throw new RuntimeException("Not connected");
        }
        executor.awaitTermination(_timeout, TimeUnit.MILLISECONDS);
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
    public Client endNow() throws MqttException {
        if (mqttClient == null) {
            throw new RuntimeException("Not connected");
        }
        mqttClient.disconnectForcibly(0, 0);
        mqttClient = null;
        return this;
    }

    /**
     * Send a downlink message using raw data
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

    /**
     * Send a downlink message using pre-registered encoder
     *
     * @param _devId The devId (devEUI for staging) to send the message to
     * @param _payload The payload to be sent
     * @param _port The port to use for the message
     * @throws MqttException in case something goes wrong
     */
    public void send(String _devId, JSONObject _payload, int _port) throws MqttException {
        JSONObject data = new JSONObject();
        data.put("payload_fields", _payload);
        data.put("port", _port != 0 ? _port : 1);
        mqttClient.publish(appId + "/devices/" + _devId + "/down", data.toString().getBytes(), 0, false);
    }

    /**
     * Register a connection event handler
     *
     * @param _handler The connection event handler
     * @return the Client instance
     */
    public Client onConnected(ConnectHandler _handler) {
        if (mqttClient != null) {
            throw new RuntimeException("Already connected");
        }
        if (!handlers.containsKey(ConnectHandler.class)) {
            handlers.put(ConnectHandler.class, new LinkedList<>());
        }
        handlers.get(ConnectHandler.class).add(_handler);
        return this;
    }

    /**
     * Register an error event handler
     *
     * @param _handler The error event handler
     * @return the Client instance
     */
    public Client onError(ErrorHandler _handler) {
        if (mqttClient != null) {
            throw new RuntimeException("Already connected");
        }
        if (!handlers.containsKey(ErrorHandler.class)) {
            handlers.put(ErrorHandler.class, new LinkedList<>());
        }
        handlers.get(ErrorHandler.class).add(_handler);
        return this;
    }

    /**
     * Register an uplink event handler
     *
     * @param _handler The uplink event handler
     * @return the Client instance
     */
    public Client onUplink(UplinkHandler _handler) {
        if (mqttClient != null) {
            throw new RuntimeException("Already connected");
        }
        if (!handlers.containsKey(UplinkHandler.class)) {
            handlers.put(UplinkHandler.class, new LinkedList<>());
        }
        handlers.get(UplinkHandler.class).add(_handler);
        return this;
    }

    /**
     * Register an activation event handler
     *
     * @param _handler The activation event handler
     * @return the Client instance
     */
    public Client onActivation(ActivationHandler _handler) {
        if (mqttClient != null) {
            throw new RuntimeException("Already connected");
        }
        if (!handlers.containsKey(ActivationHandler.class)) {
            handlers.put(ActivationHandler.class, new LinkedList<>());
        }
        handlers.get(ActivationHandler.class).add(_handler);
        return this;
    }

    /**
     * Register a default event handler
     *
     * @param _handler The default event handler
     * @return the Client instance
     */
    public Client onOtherEvent(AbstractEventHandler _handler) {
        if (mqttClient != null) {
            throw new RuntimeException("Already connected");
        }
        if (!handlers.containsKey(AbstractEventHandler.class)) {
            handlers.put(AbstractEventHandler.class, new LinkedList<>());
        }
        handlers.get(AbstractEventHandler.class).add(_handler);
        return this;
    }

}
