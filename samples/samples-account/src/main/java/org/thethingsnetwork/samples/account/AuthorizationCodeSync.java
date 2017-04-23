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
package org.thethingsnetwork.samples.account;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.List;
import org.thethingsnetwork.account.common.ExtendedAccessKey;
import org.thethingsnetwork.account.sync.Application;
import org.thethingsnetwork.account.sync.auth.grant.AuthorizationCode;
import org.thethingsnetwork.account.sync.auth.token.RenewableJsonWebToken;

/**
 *
 * @author Romain Cambier
 */
public class AuthorizationCodeSync {

    public static void run(App.Config _conf) throws Exception {
        if (_conf.cliendId == null) {
            throw new NullPointerException("missing cliendId");
        }

        if (_conf.clientSecret == null) {
            throw new NullPointerException("missing clientSecret");
        }

        if (_conf.redirect == null) {
            throw new NullPointerException("missing redirect");
        }

        AuthorizationCode tokenProvider = new AuthorizationCode(_conf.cliendId, _conf.clientSecret);

        String redirect = tokenProvider.buildAuthorizationURL(_conf.redirect).toString();

        System.out.println("here is the authorization url: " + redirect);
        System.out.print("Enter code: ");
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        String code = br.readLine();

        if (code == null || code.equals("")) {
            throw new IllegalArgumentException("invalid code");
        }

        RenewableJsonWebToken token = tokenProvider.getToken(code);

        List<Application> apps = Application.findAll(token);

        for (Application app : apps) {

            RenewableJsonWebToken restrictedToken = token.restrict("apps:" + app.getId());

            app.updateCredentials(restrictedToken);

            List<ExtendedAccessKey> accessKeys = app.getAccessKeys();

            System.out.println("\tapplication " + app.getName() + " has " + accessKeys.size() + " keys");

        }
    }

}
