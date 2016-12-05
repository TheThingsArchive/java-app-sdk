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
package org.thethingsnetwork.data.amqp;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.ByteBuffer;
import java.util.Base64;
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
import org.json.JSONObject;
import org.thethingsnetwork.data.common.Subscribable;
import org.thethingsnetwork.data.common.TriConsumer;
import org.thethingsnetwork.data.common.events.AbstractEventHandler;
import org.thethingsnetwork.data.common.events.ActivationHandler;
import org.thethingsnetwork.data.common.events.ConnectHandler;
import org.thethingsnetwork.data.common.events.ErrorHandler;
import org.thethingsnetwork.data.common.events.EventHandler;
import org.thethingsnetwork.data.common.events.UplinkHandler;

/**
 *
 * @author Romain Cambier
 */
public class Client {

    /**
     * Connection settings
     */
    private final String appId;
    private final ConnectionFactory factory;
    private final String exchange;

    /**
     * Event settings
     */
    private final ExecutorService executor = Executors.newCachedThreadPool();
    private final Map<Class, List<EventHandler>> handlers = new HashMap<>();

    /**
     * Runtime vars
     */
    private Connection connection;
    private Channel channel;

    /**
     * Create a new Client from a custom broker
     *
     * @param _broker The broker address, including protocol and port
     * @param _appId Your appId (or appEUI for staging)
     * @param _appAccessKey Your appAccessKey
     * @param _exchange The exchange to subscribe on
     * @throws java.net.URISyntaxException if the provided broker address is malformed
     */
    public Client(String _broker, String _appId, String _appAccessKey, String _exchange) throws URISyntaxException {
        appId = _appId;
        factory = new ConnectionFactory();
        factory.setHost(_broker);
        factory.setUsername(_appId);
        factory.setPassword(_appAccessKey);
        exchange = _exchange;
    }

    /**
     * Create a new Client from a custom broker
     *
     * @param _broker The broker address, including protocol and port
     * @param _appId Your appId (or appEUI for staging)
     * @param _appAccessKey Your appAccessKey
     * @throws java.net.URISyntaxException if the provided broker address is malformed
     */
    public Client(String _broker, String _appId, String _appAccessKey) throws URISyntaxException {
        this(_broker, _appId, _appAccessKey, "ttn.handler");
    }

    /**
     * Return the connection factory so that it can be tuned
     *
     * @return the connection factoiry that will be used to open the connection
     */
    public ConnectionFactory getConnectionFactory() {
        if (connection != null) {
            throw new RuntimeException("Can not be called while client is running");
        }
        return factory;
    }

    /**
     * Start the client
     *
     * @return the Client instance
     * @throws java.lang.Exception in case something went wrong
     */
    public Client start() throws Exception {
        if (connection != null) {
            throw new RuntimeException("Already connected");
        }
        connection = factory.newConnection();
        channel = connection.createChannel();

        final String queue = channel.queueDeclare().getQueue();

        channel.basicConsume(queue, new DefaultConsumer(channel) {
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, final byte[] body) throws IOException {
                final String[] tokens = envelope.getRoutingKey().split("\\.");
                if (tokens.length < 4) {
                    return;
                }
                switch (tokens[3]) {
                    case "up":
                        if (handlers.containsKey(UplinkHandler.class)) {
                            handlers.get(UplinkHandler.class).stream().forEach((handler) -> {
                                executor.submit(() -> {
                                    try {
                                        UplinkHandler uh = (UplinkHandler) handler;
                                        if (uh.matches(tokens[2])) {
                                            uh.handle(tokens[2], uh.transform(new String(body)));
                                        }
                                    } catch (final Exception ex) {
                                        if (handlers.containsKey(ErrorHandler.class)) {
                                            handlers.get(ErrorHandler.class).stream().forEach((org.thethingsnetwork.data.common.events.EventHandler handler1) -> {
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
                                                        ah.handle(tokens[2], new JSONObject(new String(body)));
                                                    }
                                                } catch (final Exception ex) {
                                                    if (handlers.containsKey(ErrorHandler.class)) {
                                                        handlers.get(ErrorHandler.class).stream().forEach((org.thethingsnetwork.data.common.events.EventHandler handler1) -> {
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
                                                        aeh.handle(tokens[2], event, new JSONObject(new String(body)));
                                                    }
                                                } catch (final Exception ex) {
                                                    if (handlers.containsKey(ErrorHandler.class)) {
                                                        handlers.get(ErrorHandler.class).stream().forEach((org.thethingsnetwork.data.common.events.EventHandler handler1) -> {
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
        });

        for (List<EventHandler> ehl : handlers.values()) {
            for (EventHandler eh : ehl) {
                eh.subscribe(new Subscribable() {

                    private static final String WILDCARD_WORD = "*";
                    private static final String WILDCARD_PATH = "#";

                    @Override
                    public void subscibe(String[] _key) throws Exception {
                        StringJoiner sj = new StringJoiner(".");
                        for (String key : _key) {
                            sj.add(key);
                        }
                        channel.queueBind(queue, exchange, sj.toString());
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
                        ((ConnectHandler) handler).handle(() -> channel);
                    } catch (final Exception ex) {
                        if (handlers.containsKey(ErrorHandler.class)) {
                            handlers.get(ErrorHandler.class).stream().forEach((org.thethingsnetwork.data.common.events.EventHandler handler1) -> {
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
        StringJoiner sj = new StringJoiner(".");
        for (int i = _ignore; i < _tokens.length; i++) {
            sj.add(_tokens[i]);
        }
        return sj.toString();
    }

    /**
     * Stop the client after max. 5000 ms
     *
     * @return the Client instance
     * @throws java.lang.InterruptedException
     * @throws IOException in case something goes wrong
     */
    public Client end() throws InterruptedException, IOException {
        if (connection == null) {
            throw new RuntimeException("Not connected");
        }
        return end(5000);
    }

    /**
     * Stop the client after max. the provided timeout
     *
     * @param _timeout The disconnect timeout
     * @return the Client instance
     * @throws java.lang.InterruptedException
     * @throws IOException in case something goes wrong
     */
    public Client end(long _timeout) throws InterruptedException, IOException {
        if (connection == null) {
            throw new RuntimeException("Not connected");
        }
        executor.awaitTermination(_timeout, TimeUnit.MILLISECONDS);
        connection.close((int) _timeout);
        if (!connection.isOpen()) {
            connection = null;
        }
        return this;
    }

    /**
     * Force destroy the client in case stop() does not work.
     *
     * @return the Client instance
     * @throws IOException in case something goes wrong
     */
    public Client endNow() throws IOException {
        if (connection == null) {
            throw new RuntimeException("Not connected");
        }
        connection.abort();
        connection = null;
        return this;
    }

    /**
     * Send a downlink message using raw data
     *
     * @param _devId The devId (devEUI for staging) to send the message to
     * @param _payload The payload to be sent
     * @param _port The port to use for the message
     * @throws IOException in case something goes wrong
     */
    public void send(String _devId, byte[] _payload, int _port) throws IOException {
        JSONObject data = new JSONObject();
        data.put("payload_raw", Base64.getEncoder().encodeToString(_payload));
        data.put("port", _port != 0 ? _port : 1);
        channel.basicPublish(exchange, appId + "/devices/" + _devId + "/down", null, data.toString().getBytes());
    }

    /**
     * Send a downlink message using pre-registered encoder
     *
     * @param _devId The devId (devEUI for staging) to send the message to
     * @param _payload The payload to be sent
     * @param _port The port to use for the message
     * @throws IOException in case something goes wrong
     */
    public void send(String _devId, JSONObject _payload, int _port) throws IOException {
        JSONObject data = new JSONObject();
        data.put("payload_fields", _payload);
        data.put("port", _port != 0 ? _port : 1);
        channel.basicPublish(exchange, appId + "/devices/" + _devId + "/down", null, data.toString().getBytes());
    }

    /**
     * Send a downlink message using pre-registered encoder
     *
     * @param _devId The devId (devEUI for staging) to send the message to
     * @param _payload The payload to be sent
     * @param _port The port to use for the message
     * @throws IOException in case something goes wrong
     */
    public void send(String _devId, ByteBuffer _payload, int _port) throws IOException {
        JSONObject data = new JSONObject();
        _payload.rewind();
        byte[] payload = new byte[_payload.capacity() - _payload.remaining()];
        _payload.get(payload);
        data.put("payload_fields", Base64.getEncoder().encodeToString(payload));
        data.put("port", _port != 0 ? _port : 1);
        channel.basicPublish(exchange, appId + "/devices/" + _devId + "/down", null, data.toString().getBytes());
    }

    /**
     * Register a connection event handler
     *
     * @param _handler The connection event handler
     * @return the Client instance
     */
    public Client onConnected(final Consumer<org.thethingsnetwork.data.common.Connection> _handler) {
        if (connection != null) {
            throw new RuntimeException("Already connected");
        }
        if (!handlers.containsKey(ConnectHandler.class)) {
            handlers.put(ConnectHandler.class, new LinkedList<>());
        }
        handlers.get(ConnectHandler.class)
                .add(new ConnectHandler() {
                    @Override
                    public void handle(org.thethingsnetwork.data.common.Connection _client) {
                        _handler.accept(_client);
                    }
                });
        return this;
    }

    /**
     * Register an error event handler
     *
     * @param _handler The error event handler
     * @return the Client instance
     */
    public Client onError(final Consumer<Throwable> _handler) {
        if (connection != null) {
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

    /**
     * Register an uplink event handler
     *
     * @param _handler The uplink event handler
     * @param _devId The devId you want to filter on
     * @param _field the field you want to get
     * @return the Client instance
     */
    public Client onMessage(final String _devId, final String _field, final BiConsumer<String, Object> _handler) {
        if (connection != null) {
            throw new RuntimeException("Already connected");
        }
        if (!handlers.containsKey(UplinkHandler.class)) {
            handlers.put(UplinkHandler.class, new LinkedList<>());
        }
        handlers.get(UplinkHandler.class).add(new UplinkHandler() {
            @Override
            public void handle(String _devId, Object _data) {
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

    /**
     * Register an uplink event handler
     *
     * @param _handler The uplink event handler
     * @param _devId The devId you want to filter on
     * @return the Client instance
     */
    public Client onMessage(final String _devId, final BiConsumer<String, Object> _handler) {
        return onMessage(_devId, null, _handler);
    }

    /**
     * Register an uplink event handler
     *
     * @param _handler The uplink event handler
     * @return the Client instance
     */
    public Client onMessage(final BiConsumer<String, Object> _handler) {
        return onMessage(null, null, _handler);
    }

    /**
     * Register an activation event handler
     *
     * @param _handler The activation event handler
     * @param _devId The devId you want to filter on
     * @return the Client instance
     */
    public Client onActivation(final String _devId, final BiConsumer<String, JSONObject> _handler) {
        if (connection != null) {
            throw new RuntimeException("Already connected");
        }
        if (!handlers.containsKey(ActivationHandler.class)) {
            handlers.put(ActivationHandler.class, new LinkedList<>());
        }
        handlers.get(ActivationHandler.class).add(new ActivationHandler() {
            @Override
            public void handle(String _devId, JSONObject _data) {
                _handler.accept(_devId, _data);
            }

            @Override
            public String getDevId() {
                return _devId;
            }
        });
        return this;
    }

    /**
     * Register an activation event handler
     *
     * @param _handler The activation event handler
     * @return the Client instance
     */
    public Client onActivation(final BiConsumer<String, JSONObject> _handler) {
        return onActivation(null, _handler);
    }

    /**
     * Register a default event handler
     *
     * @param _handler The default event handler
     * @param _devId The devId you want to filter on
     * @param _event The event you want to filter on
     * @return the Client instance
     */
    public Client onOtherEvent(final String _devId, final String _event, final TriConsumer<String, String, JSONObject> _handler) {
        if (connection != null) {
            throw new RuntimeException("Already connected");
        }
        if (!handlers.containsKey(AbstractEventHandler.class)) {
            handlers.put(AbstractEventHandler.class, new LinkedList<>());
        }
        handlers.get(AbstractEventHandler.class).add(new AbstractEventHandler() {
            @Override
            public void handle(String _devId, String _event, JSONObject _data) {
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

    /**
     * Register a default event handler
     *
     * @param _handler The default event handler
     * @param _devId The devId you want to filter on
     * @return the Client instance
     */
    public Client onOtherEvent(final String _devId, final TriConsumer<String, String, JSONObject> _handler) {
        return onOtherEvent(_devId, null, _handler);
    }

    /**
     * Register a default event handler
     *
     * @param _handler The default event handler
     * @return the Client instance
     */
    public Client onDevice(final TriConsumer<String, String, JSONObject> _handler) {
        return onOtherEvent(null, null, _handler);
    }
}
