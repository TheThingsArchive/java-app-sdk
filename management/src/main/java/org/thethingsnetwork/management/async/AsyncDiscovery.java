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
package org.thethingsnetwork.management.async;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import java.io.ByteArrayInputStream;
import org.thethingsnetwork.account.async.auth.token.AsyncOAuth2Token;
import org.thethingsnetwork.management.proto.DiscoveryGrpc;
import org.thethingsnetwork.management.proto.DiscoveryOuterClass;
import org.thethingsnetwork.management.proto.DiscoveryOuterClass.GetRequest;
import rx.Observable;
import rx.Subscriber;
import rx.schedulers.Schedulers;

/**
 * This class is an async wrapper for the The Things Network discovery service
 *
 * @author Romain Cambier
 */
public class AsyncDiscovery {

    /**
     * Main The Things Network discovery server host
     */
    public static final String HOST = "discovery.thethingsnetwork.org";
    /**
     * Main The Things Network discovery server port
     */
    public static final int PORT = 1900;

    private final DiscoveryGrpc.DiscoveryFutureStub stub;

    private AsyncDiscovery(DiscoveryGrpc.DiscoveryFutureStub _stub) {
        stub = _stub;
    }

    /**
     * Build an AsyncDiscovery wrapper from Host and Port
     *
     * @param _host The server host
     * @param _port The server port
     * @return An Observable stream containing the newly built AsyncDiscovery wrapper
     */
    public static Observable<AsyncDiscovery> from(String _host, int _port, Boolean useSecureConnection) {
        return Observable
                .create((Subscriber<? super AsyncDiscovery> t) -> {
                    try {
                        ManagedChannel ch = ManagedChannelBuilder
                                .forAddress(_host, _port)
                                .usePlaintext(!useSecureConnection)
                                .build();
                        DiscoveryGrpc.DiscoveryFutureStub stub1 = DiscoveryGrpc.newFutureStub(ch);
                        t.onNext(new AsyncDiscovery(stub1));
                        t.onCompleted();
                    } catch (Exception ex) {
                        t.onError(ex);
                    }
                });
    }

    /**
     * Build an AsyncDiscovery wrapper using default servers
     *
     * @return An Observable stream containing the newly built AsyncDiscovery wrapper
     */
    public static Observable<AsyncDiscovery> getDefault() {
        return from(HOST, PORT, false);
    }
    
    public static Observable<AsyncDiscovery> getDefault(Boolean useSecureConnection) {
        return from(HOST, PORT, useSecureConnection);
    }

    /**
     * Fetch discovery service for the specified handler
     *
     * @param _creds A valid authentication token
     * @param _handlerId The handler id
     * @return An Observable stream containing the AsyncHandler wrapper
     */
    public Observable<AsyncHandler> getHandler(AsyncOAuth2Token _creds, String _handlerId) {
        return Observable
                .from(stub.get(GetRequest.newBuilder().setId(_handlerId).setServiceName(Services.HANDLER.name().toLowerCase()).build()), Schedulers.io())
                .flatMap((DiscoveryOuterClass.Announcement t) -> from(_creds, t));
    }

    /**
     * Fetch discovery service for all handlers
     *
     * @param _creds A valid authentication token
     * @return An Observable stream containing the AsyncHandler wrappers
     */
    public Observable<AsyncHandler> getHandlers(AsyncOAuth2Token _creds) {
        return Observable
                .from(stub.getAll(DiscoveryOuterClass.GetServiceRequest.newBuilder().setServiceName(Services.HANDLER.name().toLowerCase()).build()), Schedulers.io())
                .flatMap((DiscoveryOuterClass.AnnouncementsResponse t) -> Observable.from(t.getServicesList()))
                .flatMap((DiscoveryOuterClass.Announcement t) -> from(_creds, t));
    }

    private Observable<AsyncHandler> from(AsyncOAuth2Token _creds, DiscoveryOuterClass.Announcement _announcement) {
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

    /**
     * List of known The Things Network services
     */
    public static enum Services {
        /**
         * Handler Service. Responsible of device management
         */
        HANDLER,
        /**
         * Broker Service. Responsible of data deduplication and Handler routing
         */
        BROKER,
        /**
         * Router Service. Responsible of global routing
         */
        ROUTER
    }
}
