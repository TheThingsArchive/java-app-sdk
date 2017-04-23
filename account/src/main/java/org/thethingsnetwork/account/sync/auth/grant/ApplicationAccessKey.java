/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.thethingsnetwork.account.sync.auth.grant;

import java.net.URI;
import org.thethingsnetwork.account.async.auth.grant.AsyncApplicationAccessKey;
import org.thethingsnetwork.account.async.auth.token.AsyncAccessKey;
import org.thethingsnetwork.account.common.GrantType;
import org.thethingsnetwork.account.sync.auth.token.AccessKey;

/**
 * This token provider uses application credentials (access-key) to generate a token only usable for the owning application.
 * It will always use access keys for authentication.
 *
 * @author Romain Cambier
 */
public class ApplicationAccessKey extends GrantType {

    private final AsyncApplicationAccessKey wrapped;

    /**
     * Create an instance of this token provider using fully-customized settings
     *
     * @param _key The application access-key
     * @param _accountServer The account server to be used
     */
    public ApplicationAccessKey(String _key, URI _accountServer) {
        wrapped = new AsyncApplicationAccessKey(_key, _accountServer);
    }

    /**
     * Create an instance of this token provider using default account server
     *
     * @param _key The application access-key
     */
    public ApplicationAccessKey(String _key) {
        wrapped = new AsyncApplicationAccessKey(_key, GrantType.DEFAULT_ACCOUNT_SERVER);
    }

    @Override
    public URI getAccountServer() {
        return wrapped.getAccountServer();
    }

    /**
     * Create a token using the settings provided in the constructor
     *
     * @return the AccessKey
     */
    public AccessKey getToken() {
        return wrapped.getToken()
                .map((AsyncAccessKey t) -> new AccessKey(t))
                .toBlocking()
                .single();
    }

}
