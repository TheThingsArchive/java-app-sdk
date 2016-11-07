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

/**
 *
 * @author Romain Cambier
 */
@FunctionalInterface
public interface TriConsumer<T extends Object, U extends Object, V extends Object> {

    public void accept(T t, U u, V v);
}
