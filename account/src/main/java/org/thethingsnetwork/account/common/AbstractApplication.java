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
package org.thethingsnetwork.account.common;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 *
 * @author Romain Cambier
 * @param <T> internal
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public interface AbstractApplication<T> {

    /**
     * Get the application ID
     *
     * @return the application ID
     */
    public String getId();

    /**
     * Get the application name
     *
     * @return the application name
     */
    public String getName();

    /**
     * Get the application creation time
     *
     * @return the application creation time
     */
    public String getCreated();

    /**
     * Update the application name
     * @param _name the new name to be set
     */
    public void setName(String _name);

    /**
     * Update the AsyncOAuth2Token to be used by this application wrapper
     * @param <R> internal
     * @param _creds the new AsyncOAuth2Token to be used
     */
    public <R extends T> void updateCredentials(R _creds);

}
