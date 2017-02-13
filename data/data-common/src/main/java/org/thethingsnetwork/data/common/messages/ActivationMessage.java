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

import org.thethingsnetwork.data.common.Metadata;

/**
 * This is a wrapper for activation messages
 *
 * @author Romain Cambier
 */
public class ActivationMessage {

    private String appEui;
    private String devEui;
    private String devAddr;
    private Metadata metadata;

    private ActivationMessage() {

    }

    /**
     * Get the LoraWan Application EUI
     *
     * @return the LoraWan Application EUI
     */
    public String getAppEui() {
        return appEui;
    }

    /**
     * Get the LoraWan Device EUI
     *
     * @return the LoraWan Device EUI
     */
    public String getDevEui() {
        return devEui;
    }

    /**
     * Get the LoraWan Device address
     *
     * @return the LoraWan Device address
     */
    public String getDevAddr() {
        return devAddr;
    }

    /**
     * Get the metadata of this uplink packet
     *
     * @return the metadata
     */
    public Metadata getMetadata() {
        return metadata;
    }

}
