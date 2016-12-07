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

import java.util.Arrays;
import java.util.List;
import org.thethingsnetwork.account.auth.grant.AuthorizationCode;
import rx.Observable;

/**
 *
 * @author Romain Cambier
 */
public class RenewableJsonWebToken extends JsonWebToken {

    private String refreshToken;
    private final AuthorizationCode provider;

    public RenewableJsonWebToken(String _token, long _expiration, String _refreshToken, AuthorizationCode _provider) {
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
    public Observable<? extends OAuth2Token> refresh() {
        return provider.refreshToken(this);
    }

    @Override
    public Observable<? extends JsonWebToken> restrict(List<String> _claims) {
        RenewableJsonWebToken that = this;
        return super.restrict(_claims)
                .map((JsonWebToken t) -> new RenewableJsonWebToken(t.getToken().substring(7), t.getExpiration(), "", provider) {

                    @Override
                    public Observable<? extends OAuth2Token> refresh() {
                        return that.refresh()
                                .map((OAuth2Token t1) -> (RenewableJsonWebToken) t1)
                                .flatMap((RenewableJsonWebToken t1) -> t1
                                        .restrict(_claims)
                                        .map((JsonWebToken t2) -> {
                                            refresh("", t2.getToken().substring(7), t1.getExpiration());
                                            return this;
                                        }));
                    }

                });
    }

    @Override
    public Observable<? extends JsonWebToken> restrict(String... _claims) {
        return restrict(Arrays.asList(_claims));
    }

    public RenewableJsonWebToken refresh(String _refreshToken, String _accessToken, long _expiration) {
        setRefreshToken(_refreshToken);
        setToken(_accessToken);
        setExpiration(_expiration);
        return this;
    }
}
