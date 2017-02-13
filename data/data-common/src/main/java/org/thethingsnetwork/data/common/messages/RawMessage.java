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
package org.thethingsnetwork.data.common.messages;

import java.io.IOException;
import org.thethingsnetwork.data.common.AbstractClient;

/**
 * Wrapper for a filtered message payload (ex: only one field of the decoded json)
 *
 * @author Romain Cambier
 */
public abstract class RawMessage implements DataMessage {

    /**
     * Get the payload as a String
     *
     * @return the payload as a String
     */
    public abstract String asString();

    /**
     * Get the payload as an Integer
     *
     * @return the payload as an Integer
     */
    public int asInt() {
        return Integer.parseInt(asString());
    }

    /**
     * Get the payload as a Double
     *
     * @return the payload as a Double
     */
    public double asDouble() {
        return Double.parseDouble(asString());
    }

    /**
     * Get the payload as a Boolean
     *
     * @return the payload as a Boolean
     */
    public boolean asBoolean() {
        return Boolean.parseBoolean(asString());
    }

    /**
     * Get the payload as a custom Object
     * The custom object can be a default one as Boolean, Integer, Double, or String.
     * In case it's something different, jackson will be used to parse the payload
     *
     * @param <T> a Boolean, Integer, Double, String, or the custom user-provided class
     * @param _class the type of object to deserialize the payload to.
     * @return the payload as a custom Object
     * @throws java.io.IOException in case the deserialization could not be done
     */
    public <T> T as(Class<T> _class) throws IOException {
        if (_class == null) {
            throw new NullPointerException();
        }
        if (_class.equals(Boolean.class)) {
            return (T) (Boolean) asBoolean();
        }
        if (_class.equals(Integer.class)) {
            return (T) (Integer) asInt();
        }
        if (_class.equals(Double.class)) {
            return (T) (Double) asDouble();
        }
        if (_class.equals(String.class)) {
            return (T) asString();
        }
        return AbstractClient.MAPPER.readValue(asString(), _class);
    }

}
