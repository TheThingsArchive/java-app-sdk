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

    /**
     * Get the RX (uplink) or TX (downlink) time of this packet
     *
     * @return the time as a String
     */
    public String getTime() {
        return time;
    }

    /**
     * Get the frequency of this packet
     *
     * @return the frequency, in MHz
     */
    public double getFrequency() {
        return frequency;
    }

    /**
     * Get the Modulation of this packet
     *
     * @return the modulation
     */
    public String getModulation() {
        return modulation;
    }

    /**
     * Get the data rate of this packet
     *
     * @return the data rate
     */
    public String getDataRate() {
        return dataRate;
    }

    /**
     * Get the bit rate of this packet
     *
     * @return the bit rate
     */
    public String getBitRate() {
        return bitRate;
    }

    /**
     * Get the coding rate of this packet
     *
     * @return the coding rate
     */
    public String getCodingRate() {
        return codingRate;
    }

    /**
     * Get the list of gateways that received this packet
     *
     * @return a List of Gateway
     */
    public List<Gateway> getGateways() {
        if (gateways == null) {
            return null;
        }
        return Collections.unmodifiableList(gateways);
    }

    public static class Gateway {

        private String gtwId;
        private long timestamp;
        private String time;
        private int channel;
        private double rssi;
        private double snr;
        private int rfChain;
        private double latitude;
        private double longitude;
        private double altitude;

        private Gateway() {

        }

        /**
         * Get the Gateway ID as registered in TheThingsNetwork
         *
         * @return the gateway id
         */
        public String getId() {
            return gtwId;
        }

        /**
         * Get the Gateway internal reception time
         *
         * @return the gateway internal reception time
         */
        public long getTimestamp() {
            return timestamp;
        }

        /**
         * Get the Gateway absolute reception time
         *
         * @return the gateway absolute reception time
         */
        public String getTime() {
            return time;
        }

        /**
         * Get the channel this packet was sent on
         *
         * @return the channel this packet was sent on
         */
        public int getChannel() {
            return channel;
        }

        /**
         * Get the RX rssi of this packet
         *
         * @return the RX rssi of this packet
         */
        public double getRssi() {
            return rssi;
        }

        /**
         * Get the RX snr of this packet
         *
         * @return the RX snr of this packet
         */
        public double getSnr() {
            return snr;
        }

        /**
         * Get the RF chain of this packet
         *
         * @return the RF chain of this packet
         */
        public int getRfChain() {
            return rfChain;
        }

        /**
         * Get the gateway latitude
         *
         * @return the gateway latitude
         */
        public double getLatitude() {
            return latitude;
        }

        /**
         * Get the gateway longitude
         *
         * @return the gateway longitude
         */
        public double getLongitude() {
            return longitude;
        }

        /**
         * Get the gateway altitude
         *
         * @return the gateway altitude
         */
        public double getAltitude() {
            return altitude;
        }
    }
}
