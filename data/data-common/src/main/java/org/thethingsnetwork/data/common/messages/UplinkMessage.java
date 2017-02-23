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

import java.util.Base64;
import java.util.Collections;
import java.util.Map;
import org.thethingsnetwork.data.common.Metadata;

/**
 * This is a wrapper class for JSONObject to provide support for base64 encoded payload
 *
 * @author Romain Cambier
 */
public class UplinkMessage implements DataMessage {

    private String appId;
    private String devId;
    private String hardwareSerial;
    private boolean isRetry;
    private int port;
    private int counter;
    private String payloadRaw;
    private Map<String, Object> payloadFields;
    private Metadata metadata;

    private UplinkMessage() {

    }

    /**
     * Get the encryption port
     *
     * @return the encryption port
     */
    public int getPort() {
        return port;
    }

    /**
     * Get the uplink frame counter
     *
     * @return the uplink frame counter
     */
    public int getCounter() {
        return counter;
    }

    /**
     * Get the raw payload
     *
     * @return the raw payload as a byte array
     */
    public byte[] getPayloadRaw() {
        return Base64.getDecoder().decode(payloadRaw);
    }

    /**
     * Get the payload fields. Only if you have a decoder function
     *
     * @return the payload fields as a Map where keys are strings, and values are any json-valid entity
     */
    public Map<String, Object> getPayloadFields() {
        return Collections.unmodifiableMap(payloadFields);
    }

    /**
     * Get the metadata of this uplink packet
     *
     * @return the metadata
     */
    public Metadata getMetadata() {
        return metadata;
    }

    /**
     * Get the application id
     *
     * @return the application id
     */
    public String getAppId() {
        return appId;
    }

    /**
     * Get the device id
     *
     * @return the device id
     */
    public String getDevId() {
        return devId;
    }

    /**
     * Get the hardware serial
     *
     * @return the hardware serial
     */
    public String getHardwareSerial() {
        return hardwareSerial;
    }

    /**
     * Check if this message is a retry
     *
     * @return true if the message is a retry
     */
    public boolean isRetry() {
        return isRetry;
    }

}
