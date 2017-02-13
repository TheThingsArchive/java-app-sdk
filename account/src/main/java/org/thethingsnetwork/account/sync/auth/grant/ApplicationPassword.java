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
package org.thethingsnetwork.account.sync.auth.grant;

import java.net.URI;
import org.thethingsnetwork.account.async.auth.grant.AsyncApplicationPassword;
import org.thethingsnetwork.account.async.auth.token.AsyncJsonWebToken;
import org.thethingsnetwork.account.common.GrantType;
import org.thethingsnetwork.account.sync.auth.token.JsonWebToken;

/**
 * This token provider uses application credentials (id + access-key) to generate a token only usable for the owning application
 *
 * @author Romain Cambier
 */
public class ApplicationPassword extends GrantType {

    private final AsyncApplicationPassword wrapped;

    /**
     * Create an instance of this token provider using fully-customized settings
     *
     * @param _appId The application id
     * @param _key The application access-key
     * @param _clientId The client id you received from the account server
     * @param _clientSecret The client secret you received from the account server
     * @param _accountServer The account server to be used
     */
    public ApplicationPassword(String _appId, String _key, String _clientId, String _clientSecret, URI _accountServer) {
        wrapped = new AsyncApplicationPassword(_appId, _key, _clientId, _clientSecret, _accountServer);
    }

    /**
     * Create an instance of this token provider using default account server
     *
     * @param _appId The application id
     * @param _key The application access-key
     * @param _clientId The client id you received from the account server
     * @param _clientSecret The client secret you received from the account server
     */
    public ApplicationPassword(String _appId, String _key, String _clientId, String _clientSecret) {
        wrapped = new AsyncApplicationPassword(_appId, _key, _clientId, _clientSecret);
    }

    @Override
    public URI getAccountServer() {
        return wrapped.getAccountServer();
    }

    /**
     * Create a token using the settings provided in the constructor
     *
     * @return the JsonWebToken as an Observable stream
     */
    public JsonWebToken getToken() {
        return wrapped.getToken()
                .map((AsyncJsonWebToken t) -> new JsonWebToken(t))
                .toBlocking()
                .single();
    }

}
