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
public class Collaborator {

    private String username;
    private String email;
    private List<String> rights;

    /**
     * Create an empty Collaborator. Used only by jackson
     */
    public Collaborator() {

    }

    /**
     * Create a new Collaborator
     *
     * @param _username The username of the Collaborator
     * @param _rights The rights of the Collaborator
     */
    public Collaborator(String _username, List<String> _rights) {
        username = _username;
        email = null;
        rights = _rights;
    }

    /**
     * Get the username
     *
     * @return The username
     */
    public String getUsername() {
        return username;
    }

    /**
     * Get the email address
     *
     * @return The email address
     */
    public String getEmail() {
        return email;
    }

    /**
     * Get the rights
     *
     * @return The rights
     */
    public List<String> getRights() {
        return Collections.unmodifiableList(rights);
    }
}
