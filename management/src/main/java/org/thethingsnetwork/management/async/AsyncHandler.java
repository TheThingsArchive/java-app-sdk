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
package org.thethingsnetwork.management.async;

import io.grpc.CallOptions;
import io.grpc.Channel;
import io.grpc.ClientCall;
import io.grpc.ClientInterceptor;
import io.grpc.ForwardingClientCall;
import io.grpc.Metadata;
import io.grpc.MethodDescriptor;
import io.grpc.netty.GrpcSslContexts;
import io.grpc.netty.NegotiationType;
import io.grpc.netty.NettyChannelBuilder;
import java.io.InputStream;
import org.thethingsnetwork.account.async.auth.token.AsyncOAuth2Token;
import org.thethingsnetwork.account.common.AbstractApplication;
import org.thethingsnetwork.management.HandlerApplication;
import org.thethingsnetwork.management.HandlerDevice;
import org.thethingsnetwork.management.proto.ApplicationManagerGrpc;
import org.thethingsnetwork.management.proto.HandlerOuterClass;
import rx.Observable;
import rx.Subscriber;
import rx.schedulers.Schedulers;

/**
 *
 * @author Romain Cambier
 */
public class AsyncHandler {

    private final ApplicationManagerGrpc.ApplicationManagerFutureStub stub;

    private AsyncHandler(ApplicationManagerGrpc.ApplicationManagerFutureStub _stub) {
        stub = _stub;
    }

    public static Observable<AsyncHandler> from(AsyncOAuth2Token _credentials, String _host, int _port, InputStream _certificate) {

        return Observable
                .create((Subscriber<? super AsyncHandler> t) -> {
                    try {
                        t.onNext(new AsyncHandler(
                                ApplicationManagerGrpc.newFutureStub(
                                        NettyChannelBuilder
                                        .forAddress(_host, _port)
                                        .negotiationType(NegotiationType.TLS)
                                        .sslContext(GrpcSslContexts
                                                .forClient()
                                                .trustManager(_certificate)
                                                .build()
                                        )
                                        .intercept(new ClientInterceptor() {
                                            @Override
                                            public <ReqT, RespT> ClientCall<ReqT, RespT> interceptCall(MethodDescriptor<ReqT, RespT> method, CallOptions callOptions, Channel next) {
                                                return new ForwardingClientCall.SimpleForwardingClientCall<ReqT, RespT>(next.newCall(method, callOptions)) {

                                                    @Override
                                                    public void start(ClientCall.Listener<RespT> responseListener, Metadata headers) {
                                                        /**
                                                         * Add auth header here
                                                         */
                                                        headers.put(Metadata.Key.of("token", Metadata.ASCII_STRING_MARSHALLER), _credentials.getRawToken());
                                                        super.start(responseListener, headers);
                                                    }
                                                };
                                            }
                                        })
                                        .build()
                                )
                        ));
                        t.onCompleted();
                    } catch (Exception ex) {
                        t.onError(ex);
                    }
                });

    }

    public Observable<HandlerApplication> registerApplication(AbstractApplication _application) {
        return Observable
                .from(stub.registerApplication(
                        HandlerOuterClass.ApplicationIdentifier
                        .newBuilder()
                        .setAppId(_application.getId())
                        .build()
                ), Schedulers.io())
                .flatMap((ignore) -> getApplication(_application.getId()));
    }

    public Observable<HandlerApplication> getApplication(String _applicationId) {
        return Observable.from(stub.getApplication(HandlerOuterClass.ApplicationIdentifier.newBuilder().setAppId(_applicationId).build()), Schedulers.io())
                .flatMap(HandlerApplication::from);
    }

    public Observable<HandlerApplication> setApplication(HandlerApplication _application) {
        return _application
                .toProto()
                .flatMap((HandlerOuterClass.Application t) -> Observable.from(stub.setApplication(t), Schedulers.io()))
                .map((ignore) -> _application);
    }

    public Observable<HandlerApplication> deleteApplication(HandlerApplication _application) {
        return Observable
                .from(stub.deleteApplication(
                        HandlerOuterClass.ApplicationIdentifier
                        .newBuilder()
                        .setAppId(_application.getAppId())
                        .build()
                ), Schedulers.io())
                .map((ignore) -> _application);
    }

    public Observable<HandlerDevice> getDevices(HandlerApplication _application) {
        return Observable
                .from(stub.getDevicesForApplication(
                        HandlerOuterClass.ApplicationIdentifier
                        .newBuilder()
                        .setAppId(_application.getAppId())
                        .build()
                ), Schedulers.io())
                .flatMap((HandlerOuterClass.DeviceList t) -> Observable.from(t.getDevicesList()))
                .flatMap((HandlerOuterClass.Device t) -> HandlerDevice.from(t));
    }

    public Observable<HandlerDevice> getDevice(HandlerApplication _application, String _deviceId) {
        return Observable
                .from(stub.getDevice(
                        HandlerOuterClass.DeviceIdentifier
                        .newBuilder()
                        .setAppId(_application.getAppId())
                        .setDevId(_deviceId)
                        .build()
                ), Schedulers.io())
                .flatMap((HandlerOuterClass.Device t) -> HandlerDevice.from(t));
    }

    public Observable<HandlerDevice> setDevice(HandlerDevice _device) {
        return _device.toProto()
                .flatMap((HandlerOuterClass.Device tt) -> Observable
                        .from(stub.setDevice(tt), Schedulers.io())
                        .map((t) -> _device));

    }

    public Observable<HandlerDevice> deleteDevice(HandlerDevice _device) {
        return Observable
                .from(stub.deleteDevice(
                        HandlerOuterClass.DeviceIdentifier
                        .newBuilder()
                        .setAppId(_device.getAppId())
                        .setDevId(_device.getDevId())
                        .build()
                ), Schedulers.io())
                .map((t) -> _device);
    }

}
