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
package org.thethingsnetwork.data.messages;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import java.nio.ByteBuffer;
import java.util.Base64;

/**
 *
 * @author Romain Cambier
 */
@JsonInclude(Include.NON_NULL)
public class DownlinkMessage {

    private int port;
    private String payloadRaw;
    private Object payloadFields;

    public DownlinkMessage(int _port, String _payload) {
        port = _port;
        payloadRaw = _payload;
    }

    public DownlinkMessage(int _port, byte[] _payload) {
        port = _port;
        payloadRaw = Base64.getEncoder().encodeToString(_payload);
    }

    public DownlinkMessage(int _port, ByteBuffer _payload) {
        port = _port;
        _payload.rewind();
        byte[] payload = new byte[_payload.capacity() - _payload.remaining()];
        _payload.get(payload);
        payloadRaw = Base64.getEncoder().encodeToString(payload);
    }

    public DownlinkMessage(int _port, Object _payload) {
        port = _port;
        payloadFields = _payload;
    }

}
