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
package org.thethingsnetwork.management;

import org.thethingsnetwork.management.proto.DeviceOuterClass;
import org.thethingsnetwork.management.proto.HandlerOuterClass;
import rx.Observable;
import rx.Subscriber;

/**
 *
 * @author Romain Cambier
 */
public class HandlerDevice {

    private final String appId;
    private final String devId;
    private LorawanDevice lorawan;

    private HandlerDevice(String _appId, String _devId, LorawanDevice _lorawan) {
        appId = _appId;
        devId = _devId;
        lorawan = _lorawan;
    }

    public String getAppId() {
        return appId;
    }

    public String getDevId() {
        return devId;
    }

    public LorawanDevice getLorawan() {
        return lorawan;
    }

    public static Observable<HandlerDevice> from(HandlerOuterClass.Device _proto) {

        return LorawanDevice.from(_proto.getLorawanDevice())
                .flatMap((LorawanDevice tt) -> Observable
                        .create((Subscriber<? super HandlerDevice> t) -> {
                            try {
                                t.onNext(new HandlerDevice(
                                        _proto.getAppId(),
                                        _proto.getDevId(),
                                        tt
                                ));
                                t.onCompleted();
                            } catch (Exception ex) {
                                t.onError(ex);
                            }
                        })
                );

    }

    public Observable<HandlerOuterClass.Device> toProto() {

        return lorawan.toProto()
                .flatMap((DeviceOuterClass.Device tt) -> Observable
                        .create((Subscriber<? super HandlerOuterClass.Device> t) -> {
                            try {
                                t.onNext(HandlerOuterClass.Device.newBuilder()
                                        .setAppId(appId)
                                        .setDevId(devId)
                                        .setLorawanDevice(tt)
                                        .build()
                                );
                                t.onCompleted();
                            } catch (Exception ex) {
                                t.onError(ex);
                            }
                        }));

    }

}
