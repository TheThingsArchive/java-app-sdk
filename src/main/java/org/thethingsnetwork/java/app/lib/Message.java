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
package org.thethingsnetwork.java.app.lib;

import java.util.Base64;
import org.json.JSONException;
import org.json.JSONObject;
import static org.json.JSONObject.quote;

/**
 * This is a wrapper class for JsonObject to provide support for base64-encoded data
 * @author Romain Cambier <romain@shareif.com>
 */
public class Message extends JSONObject {

    public byte[] getBinary(String _key) {
        Object object = get(_key);
        if (object instanceof String) {
            return Base64.getDecoder().decode((String) object);
        }
        throw new JSONException("JSONObject[" + quote(_key) + "] is not a base64 decodable string.");
    }

    public Message(String _source) {
        super(_source);
    }

}
