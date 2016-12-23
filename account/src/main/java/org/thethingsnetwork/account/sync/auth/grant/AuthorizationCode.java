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
package org.thethingsnetwork.account.sync.auth.grant;

import java.net.URI;
import okhttp3.HttpUrl;
import org.thethingsnetwork.account.async.auth.grant.AsyncAuthorizationCode;
import org.thethingsnetwork.account.async.auth.token.AsyncRenewableJsonWebToken;
import org.thethingsnetwork.account.common.GrantType;
import org.thethingsnetwork.account.sync.auth.token.RenewableJsonWebToken;

/**
 *
 * @author Romain Cambier
 */
public class AuthorizationCode extends GrantType{

    private final AsyncAuthorizationCode wrapped;

    public AuthorizationCode(String _clientId, String _clientSecret, URI _accountServer) {
        wrapped = new AsyncAuthorizationCode(_clientId, _clientSecret, _accountServer);
    }

    public AuthorizationCode(String _clientId, String _clientSecret) {
        wrapped = new AsyncAuthorizationCode(_clientId, _clientSecret);
    }

    @Override
    public URI getAccountServer() {
        return wrapped.getAccountServer();
    }

    public HttpUrl buildAuthorizationURL(URI _redirect) {
        return wrapped.buildAuthorizationURL(_redirect);
    }

    public RenewableJsonWebToken getToken(String _authorizationCode) {
        return wrapped.getToken(_authorizationCode)
                .map((AsyncRenewableJsonWebToken t) -> new RenewableJsonWebToken(t))
                .toBlocking()
                .single();
    }

}
