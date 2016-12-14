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
import io.wethings.gateway.ttn.proto.ApplicationManagerGrpc;
import java.io.InputStream;
import java.net.URI;
import org.thethingsnetwork.account.AbstractApplication;
import org.thethingsnetwork.account.auth.grant.ApplicationPassword;
import org.thethingsnetwork.account.auth.token.OAuth2Token;
import org.thethingsnetwork.management.HandlerApplication;
import org.thethingsnetwork.management.HandlerDevice;
import rx.Observable;
import rx.Subscriber;

/**
 *
 * @author Romain Cambier
 */
public class AsyncHandler {

    private final ApplicationManagerGrpc.ApplicationManagerFutureStub stub;

    private AsyncHandler(ApplicationManagerGrpc.ApplicationManagerFutureStub _stub) {
        stub = _stub;
    }

    public static Observable<AsyncHandler> from(OAuth2Token _credentials, String _host, int _port, InputStream _certificate) {

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
        return null;
    }

    public Observable<HandlerApplication> getApplication(String _applicationId) {
        return null;
    }

    public Observable<HandlerApplication> setApplication(HandlerApplication _application) {
        return null;
    }

    public Observable<HandlerApplication> deleteApplication(HandlerApplication _application) {
        return null;
    }

    public Observable<HandlerDevice> getDevices(HandlerApplication _application) {
        return null;
    }

    public Observable<HandlerDevice> getDevice(String _deviceId) {
        return null;
    }

    public Observable<HandlerDevice> setDevice(HandlerDevice _device) {
        return null;
    }

    public Observable<HandlerDevice> deleteDevice(HandlerDevice _device) {
        return null;
    }

    public static void main(String[] args) throws Exception {

        ApplicationPassword ac = new ApplicationPassword("shareif", "ttn-account-preview.r_1_KeoiZyRT7CRZp_MutO7HePdvJzuGnh5CtvG-eZE", "cambierr-dev", "1dd9593a1007e492357a61ca9e802fcaf2d6cc2ac03a77d5abe8edc397e615e9", new URI("https://preview.account.thethingsnetwork.org"));

        OAuth2Token token = ac.getToken().toBlocking().single();

        AsyncDiscovery ad = AsyncDiscovery.getDefault().toBlocking().single();

        AsyncHandler ah = ad.getHandler(token, "ttn-handler-eu").toBlocking().single();

    }

}
