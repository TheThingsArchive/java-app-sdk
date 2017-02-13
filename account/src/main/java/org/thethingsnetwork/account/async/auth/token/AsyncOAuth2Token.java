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
 * Base interface of any async Oauth2 token
 * @author Romain Cambier
 */
public interface AsyncOAuth2Token {

    /**
     * Wether or not this token has a refresh method
     * @return True if it has
     */
    public boolean hasRefresh();

    /**
     * Refresh this token
     * @return The refreshed token as an Observable stream
     */
    public Observable<? extends AsyncOAuth2Token> refresh();
    
    /**
     * Whether this token has expired
     * @return True if this token has expired
     */
    public boolean isExpired();

    /**
     * Get the http token
     * @return The http token
     */
    public String getToken();
    
    /**
     * Get the raw token
     * @return The raw token
     */
    public String getRawToken();
    
    /**
     * Get the account server URI
     * @return The account server URI
     */
    public URI getAccountServer();

}
