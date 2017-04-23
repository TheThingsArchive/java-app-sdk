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

import java.util.concurrent.CountDownLatch;
import org.thethingsnetwork.account.async.AsyncApplication;
import org.thethingsnetwork.account.async.auth.grant.AsyncApplicationAccessKey;
import org.thethingsnetwork.account.async.auth.token.AsyncAccessKey;
import rx.Observable;
import rx.functions.Action0;
import rx.functions.Action1;
import rx.functions.Func1;

/**
 *
 * @author Romain Cambier
 */
public class ApplicationAccessKeyAsync {
    
    public static void run(App.Config _conf) throws Exception {

        if (_conf.applicationKey == null) {
            throw new NullPointerException("missing applicationKey");
        }

        AsyncApplicationAccessKey tokenProvider = new AsyncApplicationAccessKey(_conf.applicationKey);

        /**
         * I won't use lambdas just so that everybody can ready this code !
         */
        CountDownLatch cdl = new CountDownLatch(1);
        tokenProvider
                .getToken()
                .flatMap(new Func1<AsyncAccessKey, Observable<AsyncApplication>>() {
                    @Override
                    public Observable<AsyncApplication> call(AsyncAccessKey t) {
                        return AsyncApplication.findOne(t, _conf.applicationId);
                    }
                })
                .flatMap(new Func1<AsyncApplication, Observable<String>>() {
                    @Override
                    public Observable<String> call(AsyncApplication app) {
                        return app
                                .getAccessKeys()
                                .count()
                                .map(new Func1<Integer, String>() {
                                    @Override
                                    public String call(Integer count) {
                                        return "\tapplication " + app.getName() + " has " + count + " keys";
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
                .getToken()
                .flatMap((AsyncAccessKey t) -> AsyncApplication.findOne(t, _conf.applicationId))
                .flatMap((AsyncApplication app) -> app
                        .getAccessKeys()
                        .count()
                        .map((Integer count) -> "\tapplication " + app.getName() + " has " + count + " keys")
                )
                .doOnNext(System.out::println)
                .doOnCompleted(cdl::countDown)
                .doOnError(Throwable::printStackTrace); //I removed the subscribe() to avoid running the code twice

    }
    
}
