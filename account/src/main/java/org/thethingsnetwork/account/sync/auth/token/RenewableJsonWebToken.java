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
package org.thethingsnetwork.account.sync.auth.token;

import java.net.URI;
import java.util.Arrays;
import java.util.List;
import org.thethingsnetwork.account.async.auth.token.AsyncRenewableJsonWebToken;

/**
 * Renewable Json Web Token wrapper
 *
 * @author Romain Cambier
 */
public class RenewableJsonWebToken implements OAuth2Token {

    private final AsyncRenewableJsonWebToken wrapped;

    public RenewableJsonWebToken(AsyncRenewableJsonWebToken _wrap) {
        wrapped = _wrap;
    }

    @Override
    public boolean hasRefresh() {
        return wrapped.hasRefresh();
    }

    @Override
    public RenewableJsonWebToken refresh() {
        return wrapped
                .refresh()
                .map((AsyncRenewableJsonWebToken t) -> this)
                .toBlocking()
                .single();
    }

    @Override
    public boolean isExpired() {
        return wrapped.isExpired();
    }

    @Override
    public String getToken() {
        return wrapped.getToken();
    }

    @Override
    public String getRawToken() {
        return wrapped.getRawToken();
    }

    @Override
    public URI getAccountServer() {
        return wrapped.getAccountServer();
    }

    /**
     * Restrict this token to a finer claims list
     *
     * @param _claims The claims to restrict this token to
     * @return A new RenewableJsonWebToken
     */
    public RenewableJsonWebToken restrict(List<String> _claims) {
        return wrapped.restrict(_claims)
                .map((AsyncRenewableJsonWebToken t) -> new RenewableJsonWebToken(t))
                .toBlocking()
                .single();
    }

    /**
     * Restrict this token to a finer claims list
     *
     * @param _claims The claims to restrict this token to
     * @return A new RenewableJsonWebToken
     */
    public RenewableJsonWebToken restrict(String... _claims) {
        return restrict(Arrays.asList(_claims));
    }

    @Override
    public AsyncRenewableJsonWebToken async() {
        return wrapped;
    }

}
