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

import java.util.Collections;
import java.util.List;

/**
 *
 * @author Romain Cambier
 */
public class Metadata {

    private String time;
    private double frequency;
    private String modulation;
    private String dataRate;
    private String bitRate;
    private String codingRate;
    private List<Gateway> gateways;

    private Metadata() {

    }

    public String getTime() {
        return time;
    }

    public double getFrequency() {
        return frequency;
    }

    public String getModulation() {
        return modulation;
    }

    public String getDataRate() {
        return dataRate;
    }

    public String getBitRate() {
        return bitRate;
    }

    public String getCodingRate() {
        return codingRate;
    }

    public List<Gateway> getGateways() {
        if (gateways == null) {
            return null;
        }
        return Collections.unmodifiableList(gateways);
    }

    public static class Gateway {

        private String id;
        private long timestamp;
        private String time;
        private int channel;
        private double rssi;
        private double snr;
        private int rfChain;

        private Gateway() {

        }

        public String getId() {
            return id;
        }

        public long getTimestamp() {
            return timestamp;
        }

        public String getTime() {
            return time;
        }

        public int getChannel() {
            return channel;
        }

        public double getRssi() {
            return rssi;
        }

        public double getSnr() {
            return snr;
        }

        public int getRfChain() {
            return rfChain;
        }
    }
}
