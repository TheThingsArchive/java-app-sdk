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

import java.util.concurrent.CountDownLatch;
import org.thethingsnetwork.account.async.auth.grant.AsyncApplicationPassword;
import org.thethingsnetwork.account.async.auth.token.AsyncJsonWebToken;
import org.thethingsnetwork.management.HandlerApplication;
import org.thethingsnetwork.management.HandlerDevice;
import org.thethingsnetwork.management.async.AsyncDiscovery;
import org.thethingsnetwork.management.async.AsyncHandler;

/**
 *
 * @author Romain Cambier
 */
public class ManagementAsync {

    public static void run(App.Config _conf) throws Exception {

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

        AsyncApplicationPassword tokenProvider = new AsyncApplicationPassword(_conf.applicationId, _conf.applicationKey, _conf.cliendId, _conf.clientSecret);

        CountDownLatch cdl = new CountDownLatch(1);

        tokenProvider
                .getToken()
                .flatMap((AsyncJsonWebToken token) -> AsyncDiscovery
                        .getDefault()
                        .flatMap((AsyncDiscovery t) -> t.getHandler(token, _conf.handlerId))
                )
                .single()
                .flatMap((AsyncHandler handler) -> handler
                        .getApplication(_conf.applicationId)
                        .doOnNext((HandlerApplication app) -> {
                            System.out.println("## HandlerApplication " + app.getAppId() + " has a decoder function being: \n" + app.getDecoder());
                            System.out.println("## HandlerApplication " + app.getAppId() + " device list:");
                        })
                        .flatMap(handler::getDevices)
                )
                .doOnNext((HandlerDevice t) -> {
                    System.out.println("\t HandlerDevice " + t.getDevId() + " has EUI " + _conf.printArray(t.getLorawan().getDevEui()));
                })
                .doOnCompleted(cdl::countDown)
                .doOnError(Throwable::printStackTrace)
                .subscribe();

        cdl.await();

    }

}
