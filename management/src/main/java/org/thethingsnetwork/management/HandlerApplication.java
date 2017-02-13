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
package org.thethingsnetwork.management;

import org.thethingsnetwork.management.proto.HandlerOuterClass;
import rx.Observable;
import rx.Subscriber;

/**
 * This class is a representation of a The Things Network application
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

    /**
     * Build a HandlerApplication instance from a grpc representation
     *
     * @param _proto The grpc representation
     * @return An Observable HandlerApplication containing the HandlerApplication instance
     */
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

    /**
     * Convert this HandlerApplication instance to the grpc representation
     *
     * @return The grpc representation
     */
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

    /**
     * Get the application id
     *
     * @return The application id
     */
    public String getAppId() {
        return appId;
    }

    /**
     * Get the application decoder function
     *
     * @return The applicationn decoder function
     */
    public String getDecoder() {
        return decoder;
    }

    /**
     * Get the application converter function
     *
     * @return The applicationn converter function
     */
    public String getConverter() {
        return converter;
    }

    /**
     * Get the application validator function
     *
     * @return The applicationn validator function
     */
    public String getValidator() {
        return validator;
    }

    /**
     * Get the application encoder function
     *
     * @return The applicationn encoder function
     */
    public String getEncoder() {
        return encoder;
    }

    /**
     * Set the application decoder function
     *
     * @param _decoder The applicationn decoder function
     */
    public void setDecoder(String _decoder) {
        decoder = _decoder;
    }

    /**
     * Set the application converter function
     *
     * @param _converter The converter function
     */
    public void setConverter(String _converter) {
        converter = _converter;
    }

    /**
     * Set the application validator function
     *
     * @param _validator The validator function
     */
    public void setValidator(String _validator) {
        validator = _validator;
    }

    /**
     * Set the application encoder function
     *
     * @param _encoder The encoder function
     */
    public void setEncoder(String _encoder) {
        encoder = _encoder;
    }
}
