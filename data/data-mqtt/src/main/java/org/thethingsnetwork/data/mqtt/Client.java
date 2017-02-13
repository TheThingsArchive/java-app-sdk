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
package org.thethingsnetwork.data.mqtt;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.StringJoiner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttClientPersistence;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.thethingsnetwork.data.common.AbstractClient;
import org.thethingsnetwork.data.common.Connection;
import org.thethingsnetwork.data.common.Subscribable;
import org.thethingsnetwork.data.common.TriConsumer;
import org.thethingsnetwork.data.common.events.AbstractEventHandler;
import org.thethingsnetwork.data.common.events.ActivationHandler;
import org.thethingsnetwork.data.common.events.ConnectHandler;
import org.thethingsnetwork.data.common.events.ErrorHandler;
import org.thethingsnetwork.data.common.events.EventHandler;
import org.thethingsnetwork.data.common.events.UplinkHandler;
import org.thethingsnetwork.data.common.messages.ActivationMessage;
import org.thethingsnetwork.data.common.messages.DataMessage;
import org.thethingsnetwork.data.common.messages.DownlinkMessage;
import org.thethingsnetwork.data.common.messages.RawMessage;
import org.thethingsnetwork.data.common.messages.UplinkMessage;

/**
 * This is the base class to be used to interact with The Things Network Handler
 *
 * @author Romain Cambier
 */
public class Client extends AbstractClient {

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
            } else {
                return tempBroker.toString();
            }
        } else if ("ssl".equals(tempBroker.getScheme())) {
            if (tempBroker.getPort() == -1) {
                return tempBroker.toString() + ":8883";
            } else {
                return tempBroker.toString();
            }
        } else if (tempBroker.getPort() != -1) {
            return "tcp://" + tempBroker.toString();
        } else {
            return "tcp://" + tempBroker.toString() + ":1883";
        }
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

    @Override
    public Client start() throws MqttException, Exception {
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
                    handlers.get(ErrorHandler.class).stream().forEach((handler) -> {
                        executor.submit(() -> {
                            ((ErrorHandler) handler).safelyHandle(cause);
                        });
                    });
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
                            String field;
                            if (tokens.length > 4) {
                                field = concat(4, tokens);
                            } else {
                                field = null;
                            }
                            handlers.get(UplinkHandler.class).stream()
                                    .forEach((handler) -> {
                                        executor.submit(() -> {
                                            try {
                                                UplinkHandler uh = (UplinkHandler) handler;
                                                if (uh.matches(tokens[2], field)) {
                                                    if (uh.isField()) {
                                                        uh.handle(tokens[2], new RawMessage() {
                                                            String str = new String(message.getPayload());

                                                            @Override
                                                            public String asString() {
                                                                return str;
                                                            }
                                                        });
                                                    } else {
                                                        uh.handle(tokens[2], MAPPER.readValue(message.getPayload(), UplinkMessage.class));
                                                    }
                                                }
                                            } catch (Exception ex) {
                                                if (handlers.containsKey(ErrorHandler.class)) {
                                                    handlers.get(ErrorHandler.class).stream().forEach((handler1) -> {
                                                        executor.submit(() -> {
                                                            ((ErrorHandler) handler1).safelyHandle(ex);
                                                        });
                                                    });
                                                }
                                            }
                                        });
                                    });
                        }
                        break;
                    case "events":
                        if (tokens.length > 5) {
                            switch (tokens[4]) {
                                case "activations":
                                    if (handlers.containsKey(ActivationHandler.class)) {
                                        handlers.get(ActivationHandler.class).stream().forEach((handler) -> {
                                            executor.submit(() -> {
                                                try {
                                                    ActivationHandler ah = (ActivationHandler) handler;
                                                    if (ah.matches(tokens[2])) {
                                                        ah.handle(tokens[2], MAPPER.readValue(message.getPayload(), ActivationMessage.class));
                                                    }
                                                } catch (Exception ex) {
                                                    if (handlers.containsKey(ErrorHandler.class)) {
                                                        handlers.get(ErrorHandler.class).stream().forEach((handler1) -> {
                                                            executor.submit(() -> {
                                                                ((ErrorHandler) handler1).safelyHandle(ex);
                                                            });
                                                        });
                                                    }
                                                }
                                            });
                                        });
                                    }
                                    break;
                                default:
                                    if (handlers.containsKey(AbstractEventHandler.class)) {
                                        handlers.get(AbstractEventHandler.class).stream().forEach((handler) -> {
                                            executor.submit(() -> {
                                                try {
                                                    AbstractEventHandler aeh = (AbstractEventHandler) handler;
                                                    String event = concat(4, tokens);
                                                    if (aeh.matches(tokens[2], event)) {
                                                        aeh.handle(tokens[2], event, new RawMessage() {
                                                            String str = new String(message.getPayload());

                                                            @Override
                                                            public String asString() {
                                                                return str;
                                                            }
                                                        });
                                                    }
                                                } catch (Exception ex) {
                                                    if (handlers.containsKey(ErrorHandler.class)) {
                                                        handlers.get(ErrorHandler.class).stream().forEach((handler1) -> {
                                                            executor.submit(() -> {
                                                                ((ErrorHandler) handler1).safelyHandle(ex);
                                                            });
                                                        });
                                                    }
                                                }
                                            });
                                        });
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
                eh.subscribe(new Subscribable() {

                    private static final String WILDCARD_WORD = "+";
                    private static final String WILDCARD_PATH = "#";

                    @Override
                    public void subscribe(String[] _key) throws Exception {
                        StringJoiner sj = new StringJoiner("/");
                        for (String key : _key) {
                            sj.add(key);
                        }
                        mqttClient.subscribe(sj.toString());
                    }

                    @Override
                    public String getWordWildcard() {
                        return WILDCARD_WORD;
                    }

                    @Override
                    public String getPathWildcard() {
                        return WILDCARD_PATH;
                    }
                });
            }
        }

        if (handlers.containsKey(ConnectHandler.class)) {
            handlers.get(ConnectHandler.class).stream().forEach((handler) -> {
                executor.submit(() -> {
                    try {
                        ((ConnectHandler) handler).handle(() -> mqttClient);
                    } catch (Exception ex) {
                        if (handlers.containsKey(ErrorHandler.class)) {
                            handlers.get(ErrorHandler.class).stream().forEach((handler1) -> {
                                executor.submit(() -> {
                                    ((ErrorHandler) handler1).safelyHandle(ex);
                                });
                            });
                        }
                    }
                });
            });
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

    @Override
    public Client end() throws MqttException, InterruptedException {
        if (mqttClient == null) {
            throw new RuntimeException("Not connected");
        }
        return end(5000);
    }

    @Override
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

    @Override
    public Client endNow() throws MqttException {
        if (mqttClient == null) {
            throw new RuntimeException("Not connected");
        }
        mqttClient.disconnectForcibly(0, 0);
        mqttClient = null;
        return this;
    }

    @Override
    public void send(String _devId, DownlinkMessage _payload) throws Exception {
        mqttClient.publish(appId + "/devices/" + _devId + "/down", MAPPER.writeValueAsBytes(_payload), 0, false);
    }

    @Override
    public Client onConnected(Consumer<Connection> _handler) {
        if (mqttClient != null) {
            throw new RuntimeException("Already connected");
        }
        if (!handlers.containsKey(ConnectHandler.class)) {
            handlers.put(ConnectHandler.class, new LinkedList<>());
        }
        handlers.get(ConnectHandler.class).add(new ConnectHandler() {
            @Override
            public void handle(Connection _client) {
                _handler.accept(_client);
            }
        });
        return this;
    }

    @Override
    public Client onError(Consumer<Throwable> _handler) {
        if (mqttClient != null) {
            throw new RuntimeException("Already connected");
        }
        if (!handlers.containsKey(ErrorHandler.class)) {
            handlers.put(ErrorHandler.class, new LinkedList<>());
        }
        handlers.get(ErrorHandler.class).add(new ErrorHandler() {
            @Override
            public void handle(Throwable _error) {
                _handler.accept(_error);
            }
        });
        return this;
    }

    @Override
    public Client onMessage(String _devId, String _field, BiConsumer<String, DataMessage> _handler) {
        if (mqttClient != null) {
            throw new RuntimeException("Already connected");
        }
        if (!handlers.containsKey(UplinkHandler.class)) {
            handlers.put(UplinkHandler.class, new LinkedList<>());
        }
        handlers.get(UplinkHandler.class).add(new UplinkHandler() {
            @Override
            public void handle(String _devId, DataMessage _data) {
                _handler.accept(_devId, _data);
            }

            @Override
            public String getDevId() {
                return _devId;
            }

            @Override
            public String getField() {
                return _field;
            }
        });
        return this;
    }

    @Override
    public Client onMessage(String _devId, BiConsumer<String, DataMessage> _handler) {
        return onMessage(_devId, null, _handler);
    }

    @Override
    public Client onMessage(BiConsumer<String, DataMessage> _handler) {
        return onMessage(null, null, _handler);
    }

    @Override
    public Client onActivation(String _devId, BiConsumer<String, ActivationMessage> _handler) {
        if (mqttClient != null) {
            throw new RuntimeException("Already connected");
        }
        if (!handlers.containsKey(ActivationHandler.class)) {
            handlers.put(ActivationHandler.class, new LinkedList<>());
        }
        handlers.get(ActivationHandler.class).add(new ActivationHandler() {
            @Override
            public void handle(String _devId, ActivationMessage _data) {
                _handler.accept(_devId, _data);
            }

            @Override
            public String getDevId() {
                return _devId;
            }
        });
        return this;
    }

    @Override
    public Client onActivation(BiConsumer<String, ActivationMessage> _handler) {
        return onActivation(null, _handler);
    }

    @Override
    public Client onDevice(String _devId, String _event, TriConsumer<String, String, RawMessage> _handler) {
        if (mqttClient != null) {
            throw new RuntimeException("Already connected");
        }
        if (!handlers.containsKey(AbstractEventHandler.class)) {
            handlers.put(AbstractEventHandler.class, new LinkedList<>());
        }
        handlers.get(AbstractEventHandler.class).add(new AbstractEventHandler() {
            @Override
            public void handle(String _devId, String _event, RawMessage _data) {
                _handler.accept(_devId, _event, _data);
            }

            @Override
            public String getDevId() {
                return _devId;
            }

            @Override
            public String getEvent() {
                return _event;
            }
        });
        return this;
    }

    @Override
    public Client onDevice(String _devId, TriConsumer<String, String, RawMessage> _handler) {
        return onDevice(_devId, null, _handler);
    }

    @Override
    public Client onDevice(TriConsumer<String, String, RawMessage> _handler) {
        return onDevice(null, null, _handler);
    }

}
