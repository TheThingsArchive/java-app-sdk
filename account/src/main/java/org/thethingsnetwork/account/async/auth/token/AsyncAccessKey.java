/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.thethingsnetwork.account.async.auth.token;

import java.net.URI;
import rx.Observable;

/**
 * Async Access Key wrapper
 * 
 * @author Romain Cambier
 */
public class AsyncAccessKey implements AsyncOAuth2Token {

    private final String accessKey;
    private final URI accountServer;

    public AsyncAccessKey(String _accessKey, URI _accountServer) {
        accessKey = _accessKey;
        accountServer = _accountServer;
    }

    @Override
    public boolean hasRefresh() {
        return false;
    }

    @Override
    public Observable<? extends AsyncOAuth2Token> refresh() {
        return Observable.error(new UnsupportedOperationException("Not supported."));
    }

    @Override
    public boolean isExpired() {
        return false;
    }

    @Override
    public String getToken() {
        return "Key " + accessKey;
    }

    @Override
    public String getRawToken() {
        return accessKey;
    }

    @Override
    public URI getAccountServer() {
        return accountServer;
    }

}
