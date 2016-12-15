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
package org.thethingsnetwork.account.auth.token;

import java.net.URI;
import java.util.Arrays;
import java.util.List;
import okhttp3.HttpUrl;
import okhttp3.RequestBody;
import org.thethingsnetwork.account.common.HttpRequest;
import rx.Observable;
import rx.Subscriber;

/**
 *
 * @author Romain Cambier
 */
public class JsonWebToken implements OAuth2Token {

    private String token;
    private long expiration;
    private final URI accountServer;

    public JsonWebToken(String _token, long _expiration, URI _accountServer) {
        if (_token == null) {
            throw new IllegalArgumentException("token can not be null");
        }
        if (_expiration < System.currentTimeMillis()) {
            throw new IllegalArgumentException("token already expired");
        }
        if (_accountServer == null) {
            throw new IllegalArgumentException("accountServer can not be null");
        }
        token = _token;
        expiration = _expiration;
        accountServer = _accountServer;
    }

    @Override
    public boolean hasRefresh() {
        return false;
    }

    @Override
    public Observable<? extends OAuth2Token> refresh() {
        return Observable.error(new UnsupportedOperationException("Not supported."));
    }

    protected long getExpiration() {
        return expiration;
    }

    @Override
    public boolean isExpired() {
        return expiration < (System.currentTimeMillis() + 30000);
    }

    @Override
    public String getToken() {
        return "Bearer " + token;
    }

    protected void setExpiration(long _expiration) {
        expiration = _expiration;
    }

    protected void setToken(String _token) {
        token = _token;
    }

    @Override
    public URI getAccountServer() {
        return accountServer;
    }

    public Observable<? extends JsonWebToken> restrict(List<String> _claims) {
        return Observable
                .create((Subscriber<? super HttpUrl> t) -> {
                    try {
                        t.onNext(new HttpUrl.Builder()
                                .host(accountServer.getHost())
                                .scheme(accountServer.getScheme())
                                .port(accountServer.getPort() == -1 ? (accountServer.getScheme().equals("http") ? 80 : 443) : accountServer.getPort())
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
                .map((RestrictResponse t) -> new JsonWebToken(t.accessToken, expiration, accountServer));
    }

    public Observable<? extends JsonWebToken> restrict(String... _claims) {
        return restrict(Arrays.asList(_claims));
    }

    @Override
    public String getRawToken() {
        return token;
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
