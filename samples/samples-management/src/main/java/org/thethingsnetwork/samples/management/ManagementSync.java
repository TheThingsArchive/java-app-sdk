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

import java.util.List;
import java.util.concurrent.CountDownLatch;
import org.thethingsnetwork.account.sync.auth.grant.ApplicationPassword;
import org.thethingsnetwork.account.sync.auth.token.JsonWebToken;
import org.thethingsnetwork.management.HandlerApplication;
import org.thethingsnetwork.management.HandlerDevice;
import org.thethingsnetwork.management.sync.Discovery;
import org.thethingsnetwork.management.sync.Handler;

/**
 *
 * @author Romain Cambier
 */
public class ManagementSync {

    public static void run(App.Config _conf) {
        if (_conf.cliendId == null) {
            throw new NullPointerException("missing cliendId");
        }

        if (_conf.clientSecret == null) {
            throw new NullPointerException("missing clientSecret");
        }

        if (_conf.applicationId == null) {
            throw new NullPointerException("missing applicationId");
        }

        if (_conf.applicationKey == null) {
            throw new NullPointerException("missing applicationKey");
        }

        if (_conf.handlerId == null) {
            throw new NullPointerException("missing handlerId");
        }

        ApplicationPassword tokenProvider = new ApplicationPassword(_conf.applicationId, _conf.applicationKey, _conf.cliendId, _conf.clientSecret);

        CountDownLatch cdl = new CountDownLatch(1);

        JsonWebToken token = tokenProvider.getToken();

        Discovery discoveryServer = Discovery.getDefault();

        Handler handler = discoveryServer.getHandler(token, _conf.handlerId);

        HandlerApplication application = handler.getApplication(_conf.applicationId);

        System.out.println("## HandlerApplication " + application.getAppId() + " has a decoder function being: \n" + application.getDecoder());
        System.out.println("## HandlerApplication " + application.getAppId() + " device list:");

        List<HandlerDevice> devices = handler.getDevices(application);
        
        for(HandlerDevice device:devices){
            System.out.println("\t HandlerDevice " + device.getDevId() + " has EUI " + _conf.printArray(device.getLorawan().getDevEui()));
        }
    }
}
