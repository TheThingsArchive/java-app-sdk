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
package org.thethingsnetwork.account.sync;

import java.util.List;
import org.thethingsnetwork.account.async.AsyncApplication;
import org.thethingsnetwork.account.common.AbstractApplication;
import org.thethingsnetwork.account.common.ExtendedAccessKey;
import org.thethingsnetwork.account.common.ApplicationRights;
import org.thethingsnetwork.account.common.Collaborator;
import org.thethingsnetwork.account.sync.auth.token.OAuth2Token;

/**
 * This class is a wrapper for an TTN application.
 *
 * @author Romain Cambier
 */
public class Application implements AbstractApplication<OAuth2Token> {

    private final AsyncApplication wrapped;

    /**
     * Create a new application
     *
     * @param _id the new application ID
     * @param _name the new application name
     */
    public Application(String _id, String _name) {
        wrapped = new AsyncApplication(_id, _name);
    }

    private Application(AsyncApplication _wrap) {
        wrapped = _wrap;
    }

    /**
     * List all applications available with this token
     *
     * @param _creds the OAuth2Token to be used for authentication
     * @return the list of Application
     */
    public static List<Application> findAll(OAuth2Token _creds) {
        return AsyncApplication.findAll(_creds.async())
                .map((AsyncApplication t) -> new Application(t))
                .toList()
                .toBlocking()
                .single();
    }

    /**
     * Create an application
     *
     * @param _creds the OAuth2Token to be used for authentication
     * @param _app the Application template
     * @return the new Application
     */
    public static Application create(OAuth2Token _creds, AbstractApplication _app) {
        return AsyncApplication.create(_creds.async(), _app)
                .map((AsyncApplication t) -> new Application(t))
                .toBlocking()
                .single();
    }

    /**
     * Fetch an application
     *
     * @param _creds the OAuth2Token to be used for authentication
     * @param _id the application ID to fetch
     * @return the Application
     */
    public static Application findOne(OAuth2Token _creds, String _id) {
        return AsyncApplication.findOne(_creds.async(), _id)
                .map((AsyncApplication t) -> new Application(t))
                .toBlocking()
                .singleOrDefault(null);
    }

    /**
     * Update this application
     *
     * @return the updated Application
     */
    public Application save() {
        return wrapped.save()
                .map((AsyncApplication t) -> this)
                .toBlocking()
                .single();
    }

    /**
     * Delete this application
     *
     * @return the updated Application
     */
    public Application delete() {
        return wrapped.delete()
                .map((AsyncApplication t) -> this)
                .toBlocking()
                .single();
    }

    /**
     * List all EUIs of this application
     *
     * @return the EUIs of this Application
     */
    public List<String> findAllEUIs() {
        return wrapped.findAllEUIs()
                .toList()
                .toBlocking()
                .single();
    }

    /**
     * Create a random EUI on this application
     *
     * @return the new EUI
     */
    public String createEUI() {
        return wrapped.createEUI()
                .toBlocking()
                .single();
    }

    /**
     * Create a defined EUI on this application
     *
     * @param _eui the new EUI
     * @return the updated Application
     */
    public Application addEUI(String _eui) {
        return wrapped.addEUI(_eui)
                .map((i) -> this)
                .toBlocking()
                .single();
    }

    /**
     * Delete an EUI from this application
     *
     * @param _eui the EUI to be deleted
     * @return the updated Application
     */
    public Application deleteEUI(String _eui) {
        return wrapped.deleteEUI(_eui)
                .map((i) -> this)
                .toBlocking()
                .single();
    }

    /**
     * List all collaborators of this application
     *
     * @return the list of Collaborator of this Application
     */
    public List<Collaborator> getCollaborators() {
        return wrapped.getCollaborators()
                .toList()
                .toBlocking()
                .single();
    }

    /**
     * Fetch one collaborator from this application
     *
     * @param _username the username of the Collaborator
     * @return the Collaborator
     */
    public Collaborator findOneCollaborator(String _username) {
        return wrapped.findOneCollaborator(_username)
                .toBlocking()
                .singleOrDefault(null);
    }

    /**
     * Add a collaborator to this application
     *
     * @param _collaborator the Collaborator to be added
     * @return the updated Application
     */
    public Application addCollaborator(Collaborator _collaborator) {
        return wrapped.addCollaborator(_collaborator)
                .map((i) -> this)
                .toBlocking()
                .single();
    }

    /**
     * Remove a collaborator from this application
     *
     * @param _collaborator the Collaborator to be removed
     * @return the updated Application
     */
    public Application removeCollaborator(Collaborator _collaborator) {
        return wrapped.removeCollaborator(_collaborator)
                .map((i) -> this)
                .toBlocking()
                .single();
    }

    /**
     * List all access-keys of this application
     *
     * @return the list of AccessKey of this Application
     */
    public List<ExtendedAccessKey> getAccessKeys() {
        return wrapped.getAccessKeys()
                .toList()
                .toBlocking()
                .single();
    }

    /**
     * Fetch one access-key of this application
     *
     * @param _keyname the name of the AccessKey
     * @return the AccessKey
     */
    public ExtendedAccessKey findOneAccessKey(String _keyname) {
        return wrapped.findOneAccessKey(_keyname)
                .toBlocking()
                .singleOrDefault(null);
    }

    /**
     * Add an access-key to this application
     *
     * @param _key the AccessKey template
     * @return the new AccessKey
     */
    public ExtendedAccessKey addAccessKey(ExtendedAccessKey _key) {
        return wrapped.addAccessKey(_key)
                .toBlocking()
                .single();
    }

    /**
     * Remove an access-key from this application
     *
     * @param _key the AccessKey
     * @return the updated Application
     */
    public Application removeAccessKey(ExtendedAccessKey _key) {
        return wrapped.removeAccessKey(_key)
                .map((i) -> this)
                .toBlocking()
                .single();
    }

    /**
     * Refresh this local application
     *
     * @return the updated Application
     */
    public Application refresh() {
        return wrapped.refresh()
                .map((AsyncApplication app) -> this)
                .toBlocking()
                .single();
    }

    /**
     * List all rights of this application and token
     *
     * @return the list of ApplicationRights of this Application
     */
    public List<ApplicationRights> getRights() {
        return wrapped.getRights()
                .toList()
                .toBlocking()
                .single();
    }

    /**
     * List all rights of the provided token on this application
     *
     * @param _creds the OAuth2Token to check right of
     * @return the list of ApplicationRights of this Application
     */
    public List<ApplicationRights> getRights(OAuth2Token _creds) {
        return wrapped.getRights(_creds.async())
                .toList()
                .toBlocking()
                .single();
    }

    @Override
    public String getId() {
        return wrapped.getId();
    }

    @Override
    public String getName() {
        return wrapped.getName();
    }

    @Override
    public String getCreated() {
        return wrapped.getCreated();
    }

    @Override
    public void setName(String _name) {
        wrapped.setName(_name);
    }

    @Override
    public void updateCredentials(OAuth2Token _creds) {
        wrapped.updateCredentials(_creds.async());
    }

}
