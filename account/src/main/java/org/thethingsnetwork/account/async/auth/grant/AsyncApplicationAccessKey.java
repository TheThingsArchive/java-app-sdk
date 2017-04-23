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
package org.thethingsnetwork.account.async.auth.grant;

import java.net.URI;
import org.thethingsnetwork.account.async.auth.token.AsyncAccessKey;
import org.thethingsnetwork.account.common.GrantType;
import rx.Observable;

/**
 * This token provider uses application credentials (access-key) to generate a token only usable for the owning application.
 * It will always use access keys for authentication.
 *
 * @author Romain Cambier
 */
public class AsyncApplicationAccessKey extends GrantType {

    private final String key;
    private final URI accountServer;

    /**
     * Create an instance of this token provider using fully-customized settings
     *
     * @param _key The application access-key
     * @param _accountServer The account server to be used
     */
    public AsyncApplicationAccessKey(String _key, URI _accountServer) {
        if (_key == null) {
            throw new IllegalArgumentException("key can not be null");
        }
        if (_accountServer == null) {
            throw new IllegalArgumentException("accountServer can not be null");
        }
        key = _key;
        accountServer = _accountServer;
    }

    /**
     * Create an instance of this token provider using default account server
     *
     * @param _key The application access-key
     */
    public AsyncApplicationAccessKey(String _key) {
        this(_key, GrantType.DEFAULT_ACCOUNT_SERVER);
    }

    @Override
    public URI getAccountServer() {
        return accountServer;
    }

    /**
     * Create a token using the settings provided in the constructor
     *
     * @return the AsyncAccessKey as an Observable stream
     */
    public Observable<AsyncAccessKey> getToken() {
        return Observable.just(new AsyncAccessKey(key, accountServer));
    }

}
