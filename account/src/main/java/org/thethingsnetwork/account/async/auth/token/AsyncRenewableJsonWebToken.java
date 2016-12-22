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
package org.thethingsnetwork.account.async.auth.token;

import java.util.Arrays;
import java.util.List;
import okhttp3.HttpUrl;
import okhttp3.RequestBody;
import org.thethingsnetwork.account.async.auth.grant.AsyncAuthorizationCode;
import org.thethingsnetwork.account.util.HttpRequest;
import rx.Observable;
import rx.Subscriber;

/**
 *
 * @author Romain Cambier
 */
public class AsyncRenewableJsonWebToken extends AsyncJsonWebToken {

    private String refreshToken;
    private final AsyncAuthorizationCode provider;

    public AsyncRenewableJsonWebToken(String _token, long _expiration, String _refreshToken, AsyncAuthorizationCode _provider) {
        super(_token, _expiration, _provider.getAccountServer());
        if (_refreshToken == null) {
            throw new IllegalArgumentException("refreshToken can not be null");
        }
        if (_provider == null) {
            throw new IllegalArgumentException("provider can not be null");
        }
        refreshToken = _refreshToken;
        provider = _provider;
    }

    @Override
    public boolean hasRefresh() {
        return true;
    }

    protected void setRefreshToken(String _refreshToken) {
        refreshToken = _refreshToken;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    @Override
    public Observable<AsyncRenewableJsonWebToken> refresh() {
        return provider.refreshToken(this);
    }

    public Observable<? extends AsyncJsonWebToken> restrict(List<String> _claims) {
        AsyncRenewableJsonWebToken that = this;
        return Observable
                .create((Subscriber<? super HttpUrl> t) -> {
                    try {
                        t.onNext(new HttpUrl.Builder()
                                .host(getAccountServer().getHost())
                                .scheme(getAccountServer().getScheme())
                                .port(getAccountServer().getPort() == -1 ? (getAccountServer().getScheme().equals("http") ? 80 : 443) : getAccountServer().getPort())
                                .addPathSegments("users/restrict-token")
                                .build()
                        );
                        t.onCompleted();
                    } catch (Exception ex) {
                        t.onError(ex);
                    }
                })
                .flatMap(HttpRequest::from)
                .flatMap((HttpRequest t) -> t.inject(this))
                .flatMap((HttpRequest t) -> HttpRequest
                        .buildRequestBody(new RestrictRequest(_claims))
                        .map((RequestBody rb) -> {
                            t.getBuilder().post(rb);
                            return t;
                        })
                )
                .flatMap((HttpRequest t) -> t.doExecuteForType(RestrictResponse.class))
                .map((RestrictResponse t) -> new AsyncRenewableJsonWebToken(t.accessToken, getExpiration(), "", provider) {

                    @Override
                    public Observable<AsyncRenewableJsonWebToken> refresh() {
                        return that.refresh()
                                .map((AsyncOAuth2Token t1) -> (AsyncRenewableJsonWebToken) t1)
                                .flatMap((AsyncRenewableJsonWebToken t1) -> t1
                                        .restrict(_claims)
                                        .map((AsyncJsonWebToken t2) -> {
                                            refresh("", t2.getRawToken(), t1.getExpiration());
                                            return this;
                                        }));
                    }

                });
    }

    public Observable<? extends AsyncJsonWebToken> restrict(String... _claims) {
        return restrict(Arrays.asList(_claims));
    }

    public AsyncRenewableJsonWebToken refresh(String _refreshToken, String _accessToken, long _expiration) {
        setRefreshToken(_refreshToken);
        setToken(_accessToken);
        setExpiration(_expiration);
        return this;
    }

    private class RestrictRequest {

        public List<String> scope;

        public RestrictRequest(List<String> _scope) {
            scope = _scope;
        }
    }

    private static class RestrictResponse {

        public String accessToken;
    }
}
