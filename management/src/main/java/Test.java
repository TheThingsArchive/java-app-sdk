
import java.util.concurrent.TimeUnit;
import rx.Observable;
import rx.Producer;
import rx.Subscriber;
import rx.schedulers.Schedulers;

/*
 * Shareif.com CONFIDENTIAL
 * ________________________
 *
 * Copyright 2016 Shareif.com SPRL
 * All Rights Reserved.
 *
 * NOTICE: All information contained herein is, and remains
 * the property of Shareif.com SPRL and its suppliers,
 * if any. The intellectual and technical concepts contained
 * herein are proprietary to Shareif.com SPRL
 * and its suppliers and may be covered by Belgian and Foreign Patents,
 * patents in process, and are protected by trade secret or copyright law.
 * Dissemination of this information or reproduction of this material
 * is strictly forbidden unless prior written permission is obtained
 * from Shareif.com SPRL.
 */
/**
 *
 * @author Romain Cambier
 */
public class Test {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws InterruptedException {

        Observable<Integer> s1 = serie(0, 10);
        Observable<Integer> s2 = serie(1, 10);

        Observable<Integer>[] s = new Observable[]{s1, s2};

        Observable
                .create(new Observable.OnSubscribe<Integer>() {
                    int i = 0;

                    @Override
                    public void call(Subscriber<? super Integer> t) {

                        t.setProducer(new Producer() {
                            @Override
                            public void request(long n) {
                                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
                            }
                        });

                        for (Observable<Integer> obs : s) {
                            obs.subscribe(new Subscriber<Integer>() {
                                @Override
                                public void onCompleted() {
                                    if (t.isUnsubscribed()) {
                                        unsubscribe();
                                        return;
                                    }
                                    i++;
                                    if (i == s.length) {
                                        t.onCompleted();
                                    }
                                }

                                @Override
                                public void onError(Throwable e) {
                                    t.onError(e);
                                }

                                @Override
                                public void onNext(Integer tt) {
                                    if (t.isUnsubscribed()) {
                                        unsubscribe();
                                        return;
                                    }
                                    t.onNext(tt);
                                }

                                @Override
                                public void onStart() {

                                }
                            });
                        }

                    }
                }).subscribe(new Subscriber<Integer>() {
            @Override
            public void onCompleted() {
                System.out.println("got complete");
            }

            @Override
            public void onError(Throwable e) {
                e.printStackTrace();
            }

            @Override
            public void onNext(Integer t) {
                System.out.println("got " + t);
            }
        });

        TimeUnit.SECONDS.sleep(5);

    }

    public static Observable<Integer> serie(int _offset, int _max) {
        return Observable
                .create(new Observable.OnSubscribe<Integer>() {
                    @Override
                    public void call(Subscriber<? super Integer> t) {
                        t.setProducer(new Producer() {
                            int i = 0;

                            @Override
                            public void request(long n) {
                                for (int j = 0; j < n; j++) {
                                    if (t.isUnsubscribed()) {
                                        System.out.println("broken");
                                        break;
                                    }
                                    try {
                                        TimeUnit.MILLISECONDS.sleep(10);
                                    } catch (Exception ex) {

                                    }
                                    t.onNext(100 - (_offset + 5 * i++));
                                    if (i > _max) {
                                        t.onCompleted();
                                        break;
                                    }
                                }
                            }
                        });
                    }
                })
                .subscribeOn(Schedulers.io());
    }

}
