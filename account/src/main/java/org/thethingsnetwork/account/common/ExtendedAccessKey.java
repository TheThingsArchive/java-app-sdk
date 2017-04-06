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
import java.util.Collections;
import java.util.List;

/**
 *
 * @author Romain Cambier
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class ExtendedAccessKey {

    private String name;
    private String key;
    private List<ApplicationRights> rights;

    /**
     * Create an empty AccessKey. Only used by jackson.
     */
    public ExtendedAccessKey() {

    }

    /**
     * Create a new AccessKey
     *
     * @param _name The key name
     * @param _rights The key rights
     */
    public ExtendedAccessKey(String _name, List<ApplicationRights> _rights) {
        name = _name;
        rights = _rights;
    }

    /**
     * Get the key name
     *
     * @return The key name
     */
    public String getName() {
        return name;
    }

    /**
     * Get the key secret
     *
     * @return The key secret
     */
    public String getKey() {
        return key;
    }

    /**
     * Get the key rights
     *
     * @return The key rights
     */
    public List<ApplicationRights> getRights() {
        return Collections.unmodifiableList(rights);
    }
}
