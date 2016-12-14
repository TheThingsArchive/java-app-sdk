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
package org.thethingsnetwork.data.common;

import java.nio.ByteBuffer;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import org.json.JSONObject;

/**
 * This is an abstract representation of the methods any real-time TTN client should provide
 *
 * @author Romain Cambier
 */
public interface AbstractClient {

    /**
     * Start the client
     *
     * @return the Client instance
     * @throws Exception in case something goes wrong
     */
    public AbstractClient start() throws Exception;

    /**
     * Stop the client, waiting for max 5000 ms
     *
     * @return the Client instance
     * @throws Exception in case something goes wrong
     */
    public AbstractClient end() throws Exception;

    /**
     * Stop the client, waiting for max the provided timeout
     *
     * @param _timeout The max waiting time
     * @return the Client instance
     * @throws Exception in case something goes wrong
     */
    public AbstractClient end(long _timeout) throws Exception;

    /**
     * Force-stop the client in case end() does not work.
     *
     * @return the Client instance
     * @throws Exception in case something goes wrong
     */
    public AbstractClient endNow() throws Exception;

    /**
     * Send a downlink message using raw data
     *
     * @param _devId The devId to send the message to
     * @param _payload The payload to be sent
     * @param _port The port to use for the message
     * @throws Exception in case something goes wrong
     */
    public void send(String _devId, byte[] _payload, int _port) throws Exception;

    /**
     * Send a downlink message using pre-registered encoder
     *
     * @param _devId The devId to send the message to
     * @param _payload The payload to be sent
     * @param _port The port to use for the message
     * @throws Exception in case something goes wrong
     */
    public void send(String _devId, JSONObject _payload, int _port) throws Exception;

    /**
     * Send a downlink message using raw data
     *
     * @param _devId The devId to send the message to
     * @param _payload The payload to be sent
     * @param _port The port to use for the message
     * @throws Exception in case something goes wrong
     */
    public void send(String _devId, ByteBuffer _payload, int _port) throws Exception;

    /**
     * Register a connection event handler
     *
     * @param _handler The connection event handler
     * @return the Client instance
     * @throws Exception in case something goes wrong
     */
    public AbstractClient onConnected(final Consumer<Connection> _handler) throws Exception;

    /**
     * Register an error event handler
     *
     * @param _handler The error event handler
     * @return the Client instance
     * @throws Exception in case something goes wrong
     */
    public AbstractClient onError(final Consumer<Throwable> _handler) throws Exception;

    /**
     * Register an uplink event handler using device and field filters
     *
     * @param _handler The uplink event handler
     * @param _devId The devId you want to filter on
     * @param _field the only field you want to get
     * @return the Client instance
     * @throws Exception in case something goes wrong
     */
    public AbstractClient onMessage(final String _devId, final String _field, final BiConsumer<String, Object> _handler) throws Exception;

    /**
     * Register an uplink event handler using device filter
     *
     * @param _handler The uplink event handler
     * @param _devId The devId you want to filter on
     * @return the Client instance
     * @throws Exception in case something goes wrong
     */
    public AbstractClient onMessage(final String _devId, final BiConsumer<String, Object> _handler) throws Exception;

    /**
     * Register an uplink event handler
     *
     * @param _handler The uplink event handler
     * @return the Client instance
     * @throws Exception in case something goes wrong
     */
    public AbstractClient onMessage(final BiConsumer<String, Object> _handler) throws Exception;

    /**
     * Register an activation event handler using device filter
     *
     * @param _handler The activation event handler
     * @param _devId The devId you want to filter on
     * @return the Client instance
     * @throws Exception in case something goes wrong
     */
    public AbstractClient onActivation(final String _devId, final BiConsumer<String, JSONObject> _handler) throws Exception;

    /**
     * Register an activation event handler
     *
     * @param _handler The activation event handler
     * @return the Client instance
     * @throws Exception in case something goes wrong
     */
    public AbstractClient onActivation(final BiConsumer<String, JSONObject> _handler) throws Exception;

    /**
     * Register a default event handler using device and event filters
     *
     * @param _handler The default event handler
     * @param _devId The devId you want to filter on
     * @param _event The event you want to filter on
     * @return the Client instance
     * @throws Exception in case something goes wrong
     */
    public AbstractClient onDevice(final String _devId, final String _event, final TriConsumer<String, String, JSONObject> _handler) throws Exception;

    /**
     * Register a default event handler using device filter
     *
     * @param _handler The default event handler
     * @param _devId The devId you want to filter on
     * @return the Client instance
     * @throws Exception in case something goes wrong
     */
    public AbstractClient onDevice(final String _devId, final TriConsumer<String, String, JSONObject> _handler) throws Exception;

    /**
     * Register a default event handler
     *
     * @param _handler The default event handler
     * @return the Client instance
     * @throws Exception in case something goes wrong
     */
    public AbstractClient onDevice(final TriConsumer<String, String, JSONObject> _handler) throws Exception;

}
