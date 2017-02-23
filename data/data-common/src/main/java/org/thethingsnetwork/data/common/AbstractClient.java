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
package org.thethingsnetwork.data.common;

import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import org.thethingsnetwork.data.common.messages.ActivationMessage;
import org.thethingsnetwork.data.common.messages.DataMessage;
import org.thethingsnetwork.data.common.messages.DownlinkMessage;
import org.thethingsnetwork.data.common.messages.RawMessage;

/**
 * This is an abstract representation of the methods any real-time TTN client should provide
 *
 * @author Romain Cambier
 */
public abstract class AbstractClient {

    public static final ObjectMapper MAPPER = new ObjectMapper();

    static {
        MAPPER
                .setVisibility(PropertyAccessor.ALL, Visibility.NONE)
                .setVisibility(PropertyAccessor.FIELD, Visibility.ANY)
                .setPropertyNamingStrategy(PropertyNamingStrategy.SNAKE_CASE)
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    /**
     * Start the client
     *
     * @return the Client instance
     * @throws Exception in case something goes wrong
     */
    public abstract AbstractClient start() throws Exception;

    /**
     * Stop the client, waiting for max 5000 ms
     *
     * @return the Client instance
     * @throws Exception in case something goes wrong
     */
    public abstract AbstractClient end() throws Exception;

    /**
     * Stop the client, waiting for max the provided timeout
     *
     * @param _timeout The max waiting time
     * @return the Client instance
     * @throws Exception in case something goes wrong
     */
    public abstract AbstractClient end(long _timeout) throws Exception;

    /**
     * Force-stop the client in case end() does not work.
     *
     * @return the Client instance
     * @throws Exception in case something goes wrong
     */
    public abstract AbstractClient endNow() throws Exception;

    /**
     * Send a downlink message using raw data
     *
     * @param _devId The devId to send the message to
     * @param _payload The payload to be sent
     * @throws Exception in case something goes wrong
     */
    public abstract void send(String _devId, DownlinkMessage _payload) throws Exception;

    /**
     * Register a connection event handler
     *
     * @param _handler The connection event handler
     * @return the Connection instance
     * @throws Exception in case something goes wrong
     */
    public abstract AbstractClient onConnected(Consumer<Connection> _handler) throws Exception;

    /**
     * Register an error event handler
     *
     * @param _handler The error event handler
     * @return the Client instance
     * @throws Exception in case something goes wrong
     */
    public abstract AbstractClient onError(Consumer<Throwable> _handler) throws Exception;

    /**
     * Register an uplink event handler using device and field filters
     *
     * @param _handler The uplink event handler
     * @param _devId The devId you want to filter on
     * @param _field the only field you want to get
     * @return the Client instance
     * @throws Exception in case something goes wrong
     */
    public abstract AbstractClient onMessage(String _devId, String _field, BiConsumer<String, DataMessage> _handler) throws Exception;

    /**
     * Register an uplink event handler using device filter
     *
     * @param _handler The uplink event handler
     * @param _devId The devId you want to filter on
     * @return the Client instance
     * @throws Exception in case something goes wrong
     */
    public abstract AbstractClient onMessage(String _devId, BiConsumer<String, DataMessage> _handler) throws Exception;

    /**
     * Register an uplink event handler
     *
     * @param _handler The uplink event handler
     * @return the Client instance
     * @throws Exception in case something goes wrong
     */
    public abstract AbstractClient onMessage(BiConsumer<String, DataMessage> _handler) throws Exception;

    /**
     * Register an activation event handler using device filter
     *
     * @param _handler The activation event handler
     * @param _devId The devId you want to filter on
     * @return the Client instance
     * @throws Exception in case something goes wrong
     */
    public abstract AbstractClient onActivation(String _devId, BiConsumer<String, ActivationMessage> _handler) throws Exception;

    /**
     * Register an activation event handler
     *
     * @param _handler The activation event handler
     * @return the Client instance
     * @throws Exception in case something goes wrong
     */
    public abstract AbstractClient onActivation(BiConsumer<String, ActivationMessage> _handler) throws Exception;

    /**
     * Register a default event handler using device and event filters
     *
     * @param _handler The default event handler
     * @param _devId The devId you want to filter on
     * @param _event The event you want to filter on
     * @return the Client instance
     * @throws Exception in case something goes wrong
     */
    public abstract AbstractClient onDevice(String _devId, String _event, TriConsumer<String, String, RawMessage> _handler) throws Exception;

    /**
     * Register a default event handler using device filter
     *
     * @param _handler The default event handler
     * @param _devId The devId you want to filter on
     * @return the Client instance
     * @throws Exception in case something goes wrong
     */
    public abstract AbstractClient onDevice(String _devId, TriConsumer<String, String, RawMessage> _handler) throws Exception;

    /**
     * Register a default event handler
     *
     * @param _handler The default event handler
     * @return the Client instance
     * @throws Exception in case something goes wrong
     */
    public abstract AbstractClient onDevice(TriConsumer<String, String, RawMessage> _handler) throws Exception;

}
