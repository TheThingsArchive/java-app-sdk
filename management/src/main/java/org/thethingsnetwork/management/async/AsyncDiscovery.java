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

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import java.io.ByteArrayInputStream;
import org.thethingsnetwork.account.auth.token.OAuth2Token;
import org.thethingsnetwork.management.proto.DiscoveryGrpc;
import org.thethingsnetwork.management.proto.DiscoveryOuterClass;
import org.thethingsnetwork.management.proto.DiscoveryOuterClass.GetRequest;
import rx.Observable;
import rx.Subscriber;
import rx.schedulers.Schedulers;

/**
 *
 * @author Romain Cambier
 */
public class AsyncDiscovery {

    private static final String HOST = "discovery.thethingsnetwork.org";
    private static final int PORT = 1900;

    private final DiscoveryGrpc.DiscoveryFutureStub stub;

    private AsyncDiscovery(DiscoveryGrpc.DiscoveryFutureStub _stub) {
        stub = _stub;
    }

    public static Observable<AsyncDiscovery> from(String _host, int _port) {
        return Observable
                .create((Subscriber<? super AsyncDiscovery> t) -> {
                    try {
                        ManagedChannel ch = ManagedChannelBuilder
                                .forAddress(_host, _port)
                                .usePlaintext(true)
                                .build();
                        DiscoveryGrpc.DiscoveryFutureStub stub1 = DiscoveryGrpc.newFutureStub(ch);
                        t.onNext(new AsyncDiscovery(stub1));
                        t.onCompleted();
                    } catch (Exception ex) {
                        t.onError(ex);
                    }
                });
    }

    public static Observable<AsyncDiscovery> getDefault() {
        return from(HOST, PORT);
    }

    public Observable<AsyncHandler> getHandler(OAuth2Token _creds, String _handlerId) {
        return Observable
                .from(stub.get(GetRequest.newBuilder().setId(_handlerId).setServiceName(Services.HANDLER.name().toLowerCase()).build()), Schedulers.io())
                .flatMap((DiscoveryOuterClass.Announcement t) -> from(_creds, t));
    }

    public Observable<AsyncHandler> getHandlers(OAuth2Token _creds) {
        return Observable
                .from(stub.getAll(DiscoveryOuterClass.GetServiceRequest.newBuilder().setServiceName(Services.HANDLER.name().toLowerCase()).build()), Schedulers.io())
                .flatMap((DiscoveryOuterClass.AnnouncementsResponse t) -> Observable.from(t.getServicesList()))
                .flatMap((DiscoveryOuterClass.Announcement t) -> from(_creds, t));
    }

    private Observable<AsyncHandler> from(OAuth2Token _creds, DiscoveryOuterClass.Announcement _announcement) {
        return Observable.from(_announcement.getNetAddress().split(","))
                .flatMap((String tt) -> Observable
                        .create((Subscriber<? super Server> t) -> {
                            try {
                                t.onNext(new Server(tt));
                                t.onCompleted();
                            } catch (Exception ex) {
                                t.onError(ex);
                            }
                        })
                )
                .flatMap((Server t) -> AsyncHandler.from(_creds, t.host, t.port, new ByteArrayInputStream(_announcement.getCertificate().getBytes())));
    }

    private static class Server {

        public String host;
        public int port;

        public Server(String _s) {
            String[] tokens = _s.split(":");
            if (tokens.length != 2) {
                throw new IllegalArgumentException("Server address shout be formatted as: <host>:<port>");
            }
            host = tokens[0];
            port = Integer.parseInt(tokens[1]);
        }

    }

    public static enum Services {
        HANDLER,
        BROKER,
        ROUTER
    }
}
