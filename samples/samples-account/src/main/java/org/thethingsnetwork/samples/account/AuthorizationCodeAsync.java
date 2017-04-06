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
import java.util.concurrent.CountDownLatch;
import org.thethingsnetwork.account.async.AsyncApplication;
import org.thethingsnetwork.account.async.auth.grant.AsyncAuthorizationCode;
import org.thethingsnetwork.account.async.auth.token.AsyncRenewableJsonWebToken;
import org.thethingsnetwork.account.common.ExtendedAccessKey;
import rx.Observable;
import rx.functions.Action0;
import rx.functions.Action1;
import rx.functions.Func1;

/**
 *
 * @author Romain Cambier
 */
public class AuthorizationCodeAsync {

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

        AsyncAuthorizationCode tokenProvider = new AsyncAuthorizationCode(_conf.cliendId, _conf.clientSecret);

        String redirect = tokenProvider.buildAuthorizationURL(_conf.redirect).toString();

        System.out.println("here is the authorization url: " + redirect);
        System.out.print("Enter code: ");
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        String code = br.readLine();

        if (code == null || code.equals("")) {
            throw new IllegalArgumentException("invalid code");
        }

        /**
         * I won't use lambdas just so that everybody can ready this code !
         */
        CountDownLatch cdl = new CountDownLatch(1);
        tokenProvider
                .getToken(code)
                .flatMap(new Func1<AsyncRenewableJsonWebToken, Observable<String>>() {
                    @Override
                    public Observable<String> call(AsyncRenewableJsonWebToken token) {
                        return AsyncApplication
                                .findAll(token)
                                .flatMap(new Func1<AsyncApplication, Observable<String>>() {
                                    @Override
                                    public Observable<String> call(AsyncApplication application) {
                                        return token
                                                .restrict("apps:" + application.getId())
                                                .doOnNext(new Action1<AsyncRenewableJsonWebToken>() {
                                                    @Override
                                                    public void call(AsyncRenewableJsonWebToken restrictedToken) {
                                                        application.updateCredentials(restrictedToken);
                                                    }
                                                })
                                                .flatMap(new Func1<AsyncRenewableJsonWebToken, Observable<ExtendedAccessKey>>() {
                                                    @Override
                                                    public Observable<ExtendedAccessKey> call(AsyncRenewableJsonWebToken restrictedToken) {
                                                        return application.getAccessKeys();
                                                    }
                                                })
                                                .count()
                                                .map(new Func1<Integer, String>() {
                                                    @Override
                                                    public String call(Integer accessKeysCount) {
                                                        return "\tapplication " + application.getName() + " has " + accessKeysCount + " keys";
                                                    }
                                                });
                                    }
                                });
                    }
                })
                .doOnNext(new Action1<String>() {
                    @Override
                    public void call(String t) {
                        System.out.println(t);
                    }
                })
                .doOnCompleted(new Action0() {
                    @Override
                    public void call() {
                        cdl.countDown();
                    }
                })
                .doOnError(new Action1<Throwable>() {
                    @Override
                    public void call(Throwable t) {
                        t.printStackTrace();
                    }
                })
                .subscribe();
        /**
         * this is to prevent exiting the run() before the async code complete.
         * In a real async flow, you don't block stuff !
         */
        cdl.await();

        /**
         * Just so that you know how lambdas looks:
         */
        tokenProvider
                .getToken(code)
                .flatMap((AsyncRenewableJsonWebToken token) -> AsyncApplication
                        .findAll(token)
                        .flatMap((AsyncApplication application) -> token
                                .restrict("apps:" + application.getId())
                                .doOnNext(application::updateCredentials)
                                .flatMap((ignore) -> application.getAccessKeys())
                                .count()
                                .map((Integer accessKeysCount) -> "\tapplication " + application.getName() + " has " + accessKeysCount + " keys")
                        )
                )
                .doOnNext(System.out::println)
                .doOnCompleted(cdl::countDown)
                .doOnError(Throwable::printStackTrace); //I removed the subscribe() to avoid running the code twice

    }

}
