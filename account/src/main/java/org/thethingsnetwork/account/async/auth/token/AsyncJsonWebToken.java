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
package org.thethingsnetwork.account.async.auth.token;

import java.net.URI;
import rx.Observable;

/**
 * Async Json Web Token wrapper
 *
 * @author Romain Cambier
 */
public class AsyncJsonWebToken implements AsyncOAuth2Token {

    private String token;
    private long expiration;
    private final URI accountServer;

    /**
     * Build an instance
     *
     * @param _token The raw token
     * @param _expiration The token expiration (millis)
     * @param _accountServer The account server
     */
    public AsyncJsonWebToken(String _token, long _expiration, URI _accountServer) {
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
    public Observable<? extends AsyncJsonWebToken> refresh() {
        return Observable.error(new UnsupportedOperationException("Not supported."));
    }

    /**
     * Get the expiration
     *
     * @return The expiration
     */
    public long getExpiration() {
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

    /**
     * Set the expiration
     *
     * @param _expiration The expiration
     */
    protected void setExpiration(long _expiration) {
        expiration = _expiration;
    }

    /**
     * Set the raw token
     *
     * @param _token The raw token
     */
    protected void setToken(String _token) {
        token = _token;
    }

    @Override
    public URI getAccountServer() {
        return accountServer;
    }

    @Override
    public String getRawToken() {
        return token;
    }

}
