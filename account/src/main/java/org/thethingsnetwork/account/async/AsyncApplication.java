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
package org.thethingsnetwork.account.async;

import com.fasterxml.jackson.annotation.JsonIgnore;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.thethingsnetwork.account.async.auth.token.AsyncOAuth2Token;
import org.thethingsnetwork.account.common.AbstractApplication;
import org.thethingsnetwork.account.common.ExtendedAccessKey;
import org.thethingsnetwork.account.common.ApplicationRights;
import org.thethingsnetwork.account.common.Collaborator;
import org.thethingsnetwork.account.sync.Application;
import org.thethingsnetwork.account.util.HttpRequest;
import rx.Observable;

/**
 * This class is an async wrapper for an TTN application.
 *
 * @see Application for a sync version
 * @author Romain Cambier
 */
public class AsyncApplication implements AbstractApplication<AsyncOAuth2Token> {

    private String id;
    private String name;
    private String created;
    @JsonIgnore
    protected AsyncOAuth2Token creds;

    private AsyncApplication() {
    }

    /**
     * Create a new application
     *
     * @param _id the new application ID
     * @param _name the new application name
     */
    public AsyncApplication(String _id, String _name) {
        id = _id;
        name = _name;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String _name) {
        name = _name;
    }

    @Override
    public String getCreated() {
        return created;
    }

    @Override
    public void updateCredentials(AsyncOAuth2Token _creds) {
        creds = _creds;
    }

    private void refresh(AsyncApplication _other) {
        name = _other.name;
        id = _other.id;
        created = _other.created;
        creds = _other.creds;
    }

    /**
     * List all applications available with this token
     *
     * @param _creds the AsyncOAuth2Token to be used for authentication
     * @return the list of AsyncApplication as an Observable stream
     */
    public static Observable<AsyncApplication> findAll(AsyncOAuth2Token _creds) {
        /**
         * GET /applications
         */
        return HttpRequest
                .from(_creds.getAccountServer() + "/applications")
                .flatMap((HttpRequest t) -> t.inject(_creds))
                .doOnNext((HttpRequest t) -> t.getBuilder().get())
                .flatMap((HttpRequest t) -> t.doExecuteForType(AsyncApplication[].class))
                .flatMap(Observable::from)
                .doOnNext((AsyncApplication app) -> app.updateCredentials(_creds));
    }

    /**
     * Create an application
     *
     * @param _creds the AsyncOAuth2Token to be used for authentication
     * @param _app the AsyncApplication template
     * @return the new AsyncApplication as an Observable stream.
     */
    public static Observable<AsyncApplication> create(AsyncOAuth2Token _creds, AbstractApplication _app) {
        /**
         * POST /applications
         */
        return HttpRequest
                .from(_creds.getAccountServer() + "/applications")
                .flatMap((HttpRequest t) -> t.inject(_creds))
                .flatMap((HttpRequest t) -> HttpRequest
                        .buildRequestBody(_app)
                        .map((RequestBody rb) -> {
                            t.getBuilder().post(rb);
                            return t;
                        })
                )
                .flatMap((HttpRequest t) -> t.doExecuteForType(AsyncApplication.class))
                .doOnNext((AsyncApplication ap) -> ap.updateCredentials(_creds));
    }

    /**
     * Fetch an application
     *
     * @param _creds the AsyncOAuth2Token to be used for authentication
     * @param _id the application ID to fetch
     * @return the AsyncApplication as an Observable stream.
     */
    public static Observable<AsyncApplication> findOne(AsyncOAuth2Token _creds, String _id) {
        /**
         * GET /applications/{app_id}
         */
        return HttpRequest
                .from(_creds.getAccountServer() + "/applications/" + _id)
                .flatMap((HttpRequest t) -> t.inject(_creds))
                .doOnNext((HttpRequest t) -> t.getBuilder().get())
                .flatMap((HttpRequest t) -> t.doExecuteForType(AsyncApplication.class))
                .doOnNext((AsyncApplication app) -> app.updateCredentials(_creds));
    }

    /**
     * Update this application
     *
     * @return the updated AsyncApplication as an Observable stream.
     */
    public Observable<AsyncApplication> save() {
        /**
         * PATCH /applications/{app_id}
         */
        return HttpRequest
                .from(creds.getAccountServer() + "/applications/" + id)
                .flatMap((HttpRequest t) -> t.inject(creds))
                .flatMap((HttpRequest t) -> HttpRequest
                        .buildRequestBody(this)
                        .map((RequestBody rb) -> {
                            t.getBuilder().patch(rb);
                            return t;
                        })
                )
                .flatMap((HttpRequest t) -> t.doExecute())
                .map((i) -> this);
    }

    /**
     * Delete this application
     *
     * @return the updated AsyncApplication as an Observable stream.
     */
    public Observable<AsyncApplication> delete() {
        /**
         * DELETE /applications/{app_id}
         */
        return HttpRequest
                .from(creds.getAccountServer() + "/applications/" + getId())
                .flatMap((HttpRequest t) -> t.inject(creds))
                .doOnNext((HttpRequest t) -> t.getBuilder().delete())
                .flatMap((HttpRequest t) -> t.doExecute())
                .map((Response r) -> this);
    }

    /**
     * List all EUIs of this application
     *
     * @return the EUIs of this AsyncApplication as an Observable stream.
     */
    public Observable<String> findAllEUIs() {
        /**
         * GET /applications/{app_id}/euis
         */
        return HttpRequest
                .from(creds.getAccountServer() + "/applications/" + getId() + "/euis")
                .flatMap((HttpRequest t) -> t.inject(creds))
                .doOnNext((HttpRequest t) -> t.getBuilder().get())
                .flatMap((HttpRequest t) -> t.doExecuteForType(String[].class))
                .flatMap(Observable::from);
    }

    /**
     * Create a random EUI on this application
     *
     * @return the new EUI as an Observable stream.
     */
    public Observable<String> createEUI() {
        /**
         * POST /applications/{app_id}/euis
         */
        return HttpRequest
                .from(creds.getAccountServer() + "/applications/" + getId() + "/euis")
                .flatMap((HttpRequest t) -> t.inject(creds))
                .doOnNext((HttpRequest t) -> t.getBuilder().post(RequestBody.create(null, new byte[0])))
                .flatMap((HttpRequest t) -> t.doExecuteForType(EuiCreationResponse.class))
                .map((EuiCreationResponse t) -> t.eui);
    }

    /**
     * Create a defined EUI on this application
     *
     * @param _eui the new EUI
     * @return the updated AsyncApplication as an Observable stream.
     */
    public Observable<AsyncApplication> addEUI(String _eui) {
        /**
         * PUT /applications/{app_id}/euis/{eui}
         */
        return HttpRequest
                .from(creds.getAccountServer() + "/applications/" + getId() + "/euis/" + _eui)
                .flatMap((HttpRequest t) -> t.inject(creds))
                .doOnNext((HttpRequest t) -> t.getBuilder().put(RequestBody.create(null, new byte[0])))
                .flatMap((HttpRequest t) -> t.doExecute())
                .map((Response r) -> this);
    }

    /**
     * Delete an EUI from this application
     *
     * @param _eui the EUI to be deleted
     * @return the updated AsyncApplication as an Observable stream.
     */
    public Observable<AsyncApplication> deleteEUI(String _eui) {
        /**
         * DELETE /applications/{app_id}/euis/{eui}
         */
        return HttpRequest
                .from(creds.getAccountServer() + "/applications/" + getId() + "/euis/" + _eui)
                .flatMap((HttpRequest t) -> t.inject(creds))
                .doOnNext((HttpRequest t) -> t.getBuilder().delete())
                .flatMap((HttpRequest t) -> t.doExecute())
                .map((Response r) -> this);
    }

    /**
     * List all collaborators of this application
     *
     * @return the list of Collaborator of this AsyncApplication as an Observable stream.
     */
    public Observable<Collaborator> getCollaborators() {
        /**
         * GET /applications/{app_id}/collaborators
         */
        return HttpRequest
                .from(creds.getAccountServer() + "/applications/" + getId() + "/collaborators")
                .flatMap((HttpRequest t) -> t.inject(creds))
                .doOnNext((HttpRequest t) -> t.getBuilder().get())
                .flatMap((HttpRequest t) -> t.doExecuteForType(Collaborator[].class))
                .flatMap((Collaborator[] cs) -> Observable.from(cs));
    }

    /**
     * Fetch one collaborator from this application
     *
     * @param _username the username of the Collaborator
     * @return the Collaborator as an Observable stream.
     */
    public Observable<Collaborator> findOneCollaborator(String _username) {
        /**
         * GET /applications/{app_id}/collaborators/{username}
         */
        return HttpRequest
                .from(creds.getAccountServer() + "/applications/" + getId() + "/collaborators/" + _username)
                .flatMap((HttpRequest t) -> t.inject(creds))
                .doOnNext((HttpRequest t) -> t.getBuilder().get())
                .flatMap((HttpRequest t) -> t.doExecuteForType(Collaborator.class));
    }

    /**
     * Add a collaborator to this application
     *
     * @param _collaborator the Collaborator to be added
     * @return the updated AsyncApplication as an Observable stream.
     */
    public Observable<AsyncApplication> addCollaborator(Collaborator _collaborator) {
        /**
         * PUT /applications/{app_id}/collaborators/{username}
         */
        return HttpRequest
                .from(creds.getAccountServer() + "/applications/" + getId() + "/collaborators/" + _collaborator.getUsername())
                .flatMap((HttpRequest t) -> t.inject(creds))
                .flatMap((HttpRequest t) -> HttpRequest
                        .buildRequestBody(_collaborator)
                        .map((RequestBody rb) -> {
                            t.getBuilder().put(rb);
                            return t;
                        })
                )
                .flatMap((HttpRequest t) -> t.doExecute())
                .map((Response c) -> this);
    }

    /**
     * Remove a collaborator from this application
     *
     * @param _collaborator the Collaborator to be removed
     * @return the updated AsyncApplication as an Observable stream.
     */
    public Observable<AsyncApplication> removeCollaborator(Collaborator _collaborator) {
        /**
         * DELETE /applications/{app_id}/euis/{eui}
         */
        return HttpRequest
                .from(creds.getAccountServer() + "/applications/" + getId() + "/collaborators/" + _collaborator.getUsername())
                .flatMap((HttpRequest t) -> t.inject(creds))
                .doOnNext((HttpRequest t) -> t.getBuilder().delete())
                .flatMap((HttpRequest t) -> t.doExecute())
                .map((Response c) -> this);
    }

    /**
     * List all access-keys of this application
     *
     * @return the list of AccessKey of this AsyncApplication as an Observable stream.
     */
    public Observable<ExtendedAccessKey> getAccessKeys() {
        /**
         * GET /applications/{app_id}/access-keys
         */
        return HttpRequest
                .from(creds.getAccountServer() + "/applications/" + getId() + "/access-keys")
                .flatMap((HttpRequest t) -> t.inject(creds))
                .doOnNext((HttpRequest t) -> t.getBuilder().get())
                .flatMap((HttpRequest t) -> t.doExecuteForType(ExtendedAccessKey[].class))
                .flatMap((ExtendedAccessKey[] cs) -> Observable.from(cs));
    }

    /**
     * Fetch one access-key of this application
     *
     * @param _keyname the name of the AccessKey
     * @return the AccessKey as an Observable stream.
     */
    public Observable<ExtendedAccessKey> findOneAccessKey(String _keyname) {
        /**
         * GET /applications/{app_id}/access-keys/{keyname}
         */
        return HttpRequest
                .from(creds.getAccountServer() + "/applications/" + getId() + "/access-keys/" + _keyname)
                .flatMap((HttpRequest t) -> t.inject(creds))
                .doOnNext((HttpRequest t) -> t.getBuilder().get())
                .flatMap((HttpRequest t) -> t.doExecuteForType(ExtendedAccessKey.class));
    }

    /**
     * Add an access-key to this application
     *
     * @param _key the AccessKey template
     * @return the new AccessKey as an Observable stream.
     */
    public Observable<ExtendedAccessKey> addAccessKey(ExtendedAccessKey _key) {
        /**
         * POST /applications/{app_id}/access-keys
         */
        return HttpRequest
                .from(creds.getAccountServer() + "/applications/" + getId() + "/access-keys")
                .flatMap((HttpRequest t) -> t.inject(creds))
                .flatMap((HttpRequest t) -> HttpRequest
                        .buildRequestBody(_key)
                        .map((RequestBody rb) -> {
                            t.getBuilder().post(rb);
                            return t;
                        })
                )
                .flatMap((HttpRequest t) -> t.doExecuteForType(ExtendedAccessKey.class))
                .map((ExtendedAccessKey c) -> c);
    }

    /**
     * Remove an access-key from this application
     *
     * @param _key the AccessKey
     * @return the updated AsyncApplication as an Observable stream.
     */
    public Observable<AsyncApplication> removeAccessKey(ExtendedAccessKey _key) {
        /**
         * DELETE /applications/{app_id}/access-keys/{keyname}
         */
        return HttpRequest
                .from(creds.getAccountServer() + "/applications/" + getId() + "/access-keys/" + _key.getName())
                .flatMap((HttpRequest t) -> t.inject(creds))
                .doOnNext((HttpRequest t) -> t.getBuilder().delete())
                .flatMap((HttpRequest t) -> t.doExecute())
                .map((Response c) -> this);
    }

    /**
     * Refresh this local application
     *
     * @return the updated AsyncApplication as an Observable stream.
     */
    public Observable<AsyncApplication> refresh() {
        return findOne(creds, getId())
                .doOnNext((AsyncApplication t) -> refresh(t))
                .map((AsyncApplication app) -> this);
    }

    /**
     * List all rights of this application and token
     *
     * @return the list of ApplicationRights of this AsyncApplication as an Observable stream.
     */
    public Observable<ApplicationRights> getRights() {
        return getRights(creds);
    }

    /**
     * List all rights of the provided token on this application
     *
     * @param _creds the AsyncOAuth2Token to check right of
     * @return the list of ApplicationRights of this AsyncApplication as an Observable stream.
     */
    public Observable<ApplicationRights> getRights(AsyncOAuth2Token _creds) {
        /**
         * GET /applications/{app_id}/rights
         */
        return HttpRequest
                .from(creds.getAccountServer() + "/applications/" + getId() + "/rights")
                .flatMap((HttpRequest t) -> t.inject(_creds))
                .doOnNext((HttpRequest t) -> t.getBuilder().get())
                .flatMap((HttpRequest t) -> t.doExecuteForType(ApplicationRights[].class))
                .flatMap(Observable::from);
    }

    @Override
    public String toString() {
        return "Application \"" + name + "\" (" + id + ")";
    }

    private static class EuiCreationResponse {

        public String eui;
    }
}
