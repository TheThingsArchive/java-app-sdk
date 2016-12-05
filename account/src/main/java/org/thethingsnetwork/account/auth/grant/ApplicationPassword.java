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
package org.thethingsnetwork.account.auth.grant;

import java.net.URI;
import java.util.Base64;
import okhttp3.HttpUrl;
import okhttp3.RequestBody;
import org.thethingsnetwork.account.auth.token.JsonWebToken;
import org.thethingsnetwork.account.common.HttpRequest;
import rx.Observable;
import rx.Subscriber;

/**
 *
 * @author Romain Cambier
 */
public class ApplicationPassword extends GrantType {

    private final String clientId;
    private final String clientSecret;
    private final String appId;
    private final String key;
    private final URI accountServer;

    public ApplicationPassword(String _appId, String _key, String _clientId, String _clientSecret, URI _accountServer) {
        if (_key == null) {
            throw new IllegalArgumentException("key can not be null");
        }
        if (_appId == null) {
            throw new IllegalArgumentException("appId can not be null");
        }
        if (_clientId == null) {
            throw new IllegalArgumentException("clientId can not be null");
        }
        if (_clientSecret == null) {
            throw new IllegalArgumentException("clientSecret can not be null");
        }
        if (_accountServer == null) {
            throw new IllegalArgumentException("accountServer can not be null");
        }
        appId = _appId;
        key = _key;
        accountServer = _accountServer;
        clientId = _clientId;
        clientSecret = _clientSecret;
    }

    public ApplicationPassword(String _appId, String _key, String _clientId, String _clientSecret) {
        this(_appId, _key, _clientId, _clientSecret, GrantType.DEFAULT_ACCOUNT_SERVER);
    }

    @Override
    public URI getAccountServer() {
        return accountServer;
    }

    private String getBasicAuthHeader() {
        return "Basic " + Base64.getEncoder().encodeToString((clientId + ":" + clientSecret).getBytes());
    }

    public Observable<JsonWebToken> getToken() {
        return Observable
                .create((Subscriber<? super HttpUrl> t) -> {
                    try {
                        t.onNext(new HttpUrl.Builder()
                                .host(accountServer.getHost())
                                .scheme(accountServer.getScheme())
                                .port(accountServer.getPort() == -1 ? (accountServer.getScheme().equals("http") ? 80 : 443) : accountServer.getPort())
                                .addPathSegments("applications/token")
                                .build()
                        );
                        t.onCompleted();
                    } catch (Exception ex) {
                        t.onError(ex);
                    }
                })
                .flatMap(HttpRequest::from)
                .flatMap((HttpRequest t) -> HttpRequest
                        .buildRequestBody(new TokenRequest())
                        .map((RequestBody rb) -> {
                            t.getBuilder().post(rb);
                            return t;
                        })
                )
                .doOnNext((HttpRequest t) -> {
                    t.getBuilder().header("Authorization", getBasicAuthHeader());
                })
                .flatMap((HttpRequest t) -> t.doExecuteForType(TokenResponse.class))
                .map((TokenResponse t) -> new JsonWebToken(t.accessToken, System.currentTimeMillis() + 1000 * t.expiresIn, accountServer));
    }

    private class TokenRequest {

        private final String username = ApplicationPassword.this.appId;
        private final String password = ApplicationPassword.this.key;
        private final String grantType = "password";
    }

    private static class TokenResponse {

        private String accessToken;
        private long expiresIn;
    }
}
