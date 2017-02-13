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
package org.thethingsnetwork.samples.management;

import org.thethingsnetwork.account.util.HttpRequest;

/**
 *
 * @author Romain Cambier
 */
public class App {

    public static void main(String[] args) throws Exception {
        Config cfg = new Config();
        cfg.cliendId = System.getenv("cliendId");
        cfg.clientSecret = System.getenv("clientSecret");
        cfg.handlerId = System.getenv("handlerId");
        cfg.applicationId = System.getenv("applicationId");
        cfg.applicationKey = System.getenv("applicationKey");

        System.out.println("Starting ManagementAsync");
        ManagementAsync.run(cfg);
        System.out.println("Starting ManagementSync");
        ManagementSync.run(cfg);

        HttpRequest.shutdown();

    }

    public static class Config {

        public String cliendId;
        public String clientSecret;
        //for app password
        public String applicationId;
        public String applicationKey;
        //for handler choice
        public String handlerId;

        private final static char[] hexChars = "0123456789ABCDEF".toCharArray();

        public String printArray(byte[] _data) {
            char[] hexChars = new char[2 * _data.length];
            for (int i = 0; i < _data.length; i++) {
                int j = _data[i] & 0xFF;
                hexChars[i * 2] = hexChars[j >>> 4];
                hexChars[i * 2 + 1] = hexChars[j & 0x0F];
            }
            return new String(hexChars);
        }
    }

}
