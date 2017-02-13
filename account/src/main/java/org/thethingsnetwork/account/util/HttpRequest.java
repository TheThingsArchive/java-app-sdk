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
package org.thethingsnetwork.account.util;

import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import java.io.IOException;
import java.net.URL;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.thethingsnetwork.account.async.auth.token.AsyncOAuth2Token;
import rx.Observable;
import rx.Subscriber;
import rx.schedulers.Schedulers;

/**
 *
 * @author Romain Cambier
 */
public class HttpRequest {

    private static final OkHttpClient client = new OkHttpClient();
    private static final ObjectMapper mapper = new ObjectMapper();
    protected static final MediaType mediatypeJson = MediaType.parse("application/json");

    static {
        mapper.setVisibility(PropertyAccessor.ALL, Visibility.NONE)
                .setVisibility(PropertyAccessor.FIELD, Visibility.ANY)
                .setPropertyNamingStrategy(PropertyNamingStrategy.SNAKE_CASE);
    }

    private final Request.Builder builder;

    private HttpRequest(Request.Builder _builder) {
        builder = _builder;
    }

    public static Observable<HttpRequest> from(String _url) {
        return Observable
                .create((Subscriber<? super HttpRequest> t) -> {
                    try {
                        t.onNext(new HttpRequest(new Request.Builder().url(_url)));
                        t.onCompleted();
                    } catch (Exception ex) {
                        t.onError(ex);
                    }
                });
    }

    public static Observable<HttpRequest> from(URL _url) {
        return Observable
                .create((Subscriber<? super HttpRequest> t) -> {
                    try {
                        t.onNext(new HttpRequest(new Request.Builder().url(_url)));
                        t.onCompleted();
                    } catch (Exception ex) {
                        t.onError(ex);
                    }
                });
    }

    public static Observable<HttpRequest> from(HttpUrl _url) {
        return Observable
                .create((Subscriber<? super HttpRequest> t) -> {
                    try {
                        t.onNext(new HttpRequest(new Request.Builder().url(_url)));
                        t.onCompleted();
                    } catch (Exception ex) {
                        t.onError(ex);
                    }
                });
    }

    public Observable<HttpRequest> inject(AsyncOAuth2Token _creds) {
        if (_creds == null) {
            return Observable.just(this);
        }
        if (_creds.isExpired()) {
            if (_creds.hasRefresh()) {
                return _creds.refresh().flatMap((AsyncOAuth2Token t) -> inject(t));
            } else {
                return Observable.error(new Exception("non-renewable token expired"));
            }
        }
        return Observable
                .create((Subscriber<? super HttpRequest> t) -> {
                    try {
                        HttpRequest.this.builder.addHeader("Authorization", _creds.getToken());
                        t.onNext(HttpRequest.this);
                        t.onCompleted();
                    } catch (Exception ex) {
                        t.onError(ex);
                    }
                });
    }

    public static <T> Observable<RequestBody> buildRequestBody(T _body) {
        return Observable
                .create((Subscriber<? super RequestBody> t) -> {
                    try {
                        t.onNext(RequestBody.create(mediatypeJson, mapper.writeValueAsBytes(_body)));
                        t.onCompleted();
                    } catch (Exception ex) {
                        t.onError(ex);
                    }
                });
    }

    public Observable<Request> build() {
        return Observable
                .create((Subscriber<? super Request> t) -> {
                    try {
                        t.onNext(builder.build());
                        t.onCompleted();
                    } catch (Exception ex) {
                        t.onError(ex);
                    }
                });
    }

    public Observable<Response> doExecute() {
        return build()
                .flatMap((Request r) -> Observable
                        .create((Subscriber<? super Response> t) -> {
                            client.newCall(r).enqueue(new SubscriberCallback(t));
                        })
                        .subscribeOn(Schedulers.io())
                )
                .flatMap((Response r) -> Observable
                        .create((Subscriber<? super Response> t) -> {
                            try {
                                if (!r.isSuccessful()) {
                                    t.onError(new HttpException(r.code(), r.message(), new String(r.body().bytes())));
                                    return;
                                }
                                t.onNext(r);
                                t.onCompleted();
                            } catch (IOException ex) {
                                t.onError(ex);
                            }
                        })
                        .subscribeOn(Schedulers.io())
                );

    }

    public <T> Observable<T> doExecuteForType(Class<T> _type) {
        return build()
                .flatMap((Request r) -> Observable
                        .create((Subscriber<? super Response> t) -> {
                            client.newCall(r).enqueue(new SubscriberCallback(t));
                        })
                        .subscribeOn(Schedulers.io())
                )
                .flatMap((Response r) -> Observable
                        .create((Subscriber<? super byte[]> t) -> {
                            try {
                                byte[] bytes = r.body().bytes();
                                if (!r.isSuccessful()) {
                                    t.onError(new HttpException(r.code(), r.message(), new String(bytes)));
                                    return;
                                }
                                t.onNext(bytes);
                                t.onCompleted();
                            } catch (IOException ex) {
                                t.onError(ex);
                            }
                        })
                        .subscribeOn(Schedulers.io())
                )
                .flatMap((byte[] data) -> Observable
                        .create((Subscriber<? super T> t) -> {
                            try {
                                t.onNext(mapper.readValue(data, _type));
                                t.onCompleted();
                            } catch (Exception ex) {
                                t.onError(ex);
                            }
                        })
                        .subscribeOn(Schedulers.computation())
                );

    }

    public static void shutdown() {
        client.dispatcher().executorService().shutdown();
    }

    public Request.Builder getBuilder() {
        return builder;
    }

    private static class SubscriberCallback implements Callback {

        private final Subscriber<? super Response> sub;

        public SubscriberCallback(Subscriber<? super Response> _sub) {
            sub = _sub;
        }

        @Override
        public void onFailure(Call call, IOException ioe) {
            sub.onError(ioe);
        }

        @Override
        public void onResponse(Call call, Response rspns) throws IOException {
            sub.onNext(rspns);
            sub.onCompleted();
        }

    }
}
