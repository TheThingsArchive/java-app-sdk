/*
 * Shareif.com CONFIDENTIAL
 * ________________________
 *
 * Copyright 2016 Shareif.com SPRL
 * All Rights Reserved.
 *
 * NOTICE:  All information contained herein is, and remains
 * the property of Shareif.com SPRL and its suppliers,
 * if any.  The intellectual and technical concepts contained
 * herein are proprietary to Shareif.com SPRL
 * and its suppliers and may be covered by Belgian and Foreign Patents,
 * patents in process, and are protected by trade secret or copyright law.
 * Dissemination of this information or reproduction of this material
 * is strictly forbidden unless prior written permission is obtained
 * from Shareif.com SPRL.
 */
package org.thethingsnetwork.data.common.messages;

import java.io.IOException;
import org.thethingsnetwork.data.common.AbstractClient;

/**
 *
 * @author Romain Cambier
 */
public abstract class RawMessage implements DataMessage {

    public abstract String asString();

    public int asInt() {
        return Integer.parseInt(asString());
    }

    public double asDouble() {
        return Double.parseDouble(asString());
    }

    public boolean asBoolean() {
        return Boolean.parseBoolean(asString());
    }

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
