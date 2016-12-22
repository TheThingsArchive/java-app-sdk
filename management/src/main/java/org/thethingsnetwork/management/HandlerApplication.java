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

import org.thethingsnetwork.management.proto.HandlerOuterClass;
import rx.Observable;
import rx.Subscriber;

/**
 *
 * @author Romain Cambier
 */
public class HandlerApplication {

    private final String appId;
    private String decoder;
    private String converter;
    private String validator;
    private String encoder;

    private HandlerApplication(String _appId, String _decoder, String _converter, String _validator, String _encoder) {
        appId = _appId;
        decoder = _decoder;
        converter = _converter;
        validator = _validator;
        encoder = _encoder;
    }

    public static Observable<HandlerApplication> from(HandlerOuterClass.Application _proto) {

        return Observable
                .create((Subscriber<? super HandlerApplication> t) -> {
                    try {
                        t.onNext(new HandlerApplication(
                                _proto.getAppId(),
                                _proto.getDecoder(),
                                _proto.getConverter(),
                                _proto.getValidator(),
                                _proto.getEncoder()
                        ));
                        t.onCompleted();
                    } catch (Exception ex) {
                        t.onError(ex);
                    }
                });

    }

    public Observable<HandlerOuterClass.Application> toProto() {

        return Observable
                .create((Subscriber<? super HandlerOuterClass.Application> t) -> {
                    try {
                        t.onNext(HandlerOuterClass.Application.newBuilder()
                                .setAppId(appId)
                                .setDecoder(decoder)
                                .setConverter(converter)
                                .setValidator(validator)
                                .setEncoder(encoder)
                                .build()
                        );
                        t.onCompleted();
                    } catch (Exception ex) {
                        t.onError(ex);
                    }
                });

    }

    public String getAppId() {
        return appId;
    }

    public String getDecoder() {
        return decoder;
    }

    public String getConverter() {
        return converter;
    }

    public String getValidator() {
        return validator;
    }

    public String getEncoder() {
        return encoder;
    }

    public void setDecoder(String _decoder) {
        decoder = _decoder;
    }

    public void setConverter(String _converter) {
        converter = _converter;
    }

    public void setValidator(String _validator) {
        validator = _validator;
    }

    public void setEncoder(String _encoder) {
        encoder = _encoder;
    }
}
