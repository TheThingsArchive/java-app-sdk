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
package org.thethingsnetwork.data.common.messages;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import java.nio.ByteBuffer;
import java.util.Base64;

/**
 * Wrapper for a downlink message
 *
 * @author Romain Cambier
 */
@JsonInclude(Include.NON_NULL)
public class DownlinkMessage {

    private int port;
    private String payloadRaw;
    private Object payloadFields;

    /**
     * Constructor for a base64-encoded payload
     *
     * @param _port the port to be used while encrypting the message
     * @param _payload the actual base64 string
     */
    public DownlinkMessage(int _port, String _payload) {
        port = _port;
        payloadRaw = _payload;
    }

    /**
     * Constructor for a byte array
     *
     * @param _port the port to be used while encrypting the message
     * @param _payload the actual data
     */
    public DownlinkMessage(int _port, byte[] _payload) {
        port = _port;
        payloadRaw = Base64.getEncoder().encodeToString(_payload);
    }

    /**
     * Constructor for a byte buffer
     *
     * @param _port the port to be used while encrypting the message
     * @param _payload the actual data
     */
    public DownlinkMessage(int _port, ByteBuffer _payload) {
        port = _port;
        _payload.rewind();
        byte[] payload = new byte[_payload.capacity() - _payload.remaining()];
        _payload.get(payload);
        payloadRaw = Base64.getEncoder().encodeToString(payload);
    }

    /**
     * Constructor for any kind of java object that will be json-serialized by jackson
     *
     * @param _port the port to be used while encrypting the message
     * @param _payload the actual object
     */
    public DownlinkMessage(int _port, Object _payload) {
        port = _port;
        payloadFields = _payload;
    }

}
