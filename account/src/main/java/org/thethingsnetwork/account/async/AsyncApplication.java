/*
 * The MIT License
 *
 * Copyright (c) 2016 The Things Network
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
import org.thethingsnetwork.account.AbstractApplication;
import org.thethingsnetwork.account.AccessKey;
import org.thethingsnetwork.account.Collaborator;
import org.thethingsnetwork.account.auth.token.OAuth2Token;
import org.thethingsnetwork.account.common.HttpRequest;
import rx.Observable;

/**
 *
 * @author Romain Cambier
 */
public class AsyncApplication implements AbstractApplication {

    private String id;
    private String name;
    private String created;
    @JsonIgnore
    protected OAuth2Token creds;

    public AsyncApplication() {
    }

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
    public void updateCredentials(OAuth2Token _creds) {
        creds = _creds;
    }

    private void refresh(AsyncApplication _other) {
        name = _other.name;
        id = _other.id;
        created = _other.created;
        creds = _other.creds;
    }

    public static Observable<AsyncApplication> findAll(OAuth2Token _creds) {
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

    public static Observable<AsyncApplication> create(OAuth2Token _creds, AbstractApplication _app) {
        /**
         * POST /applications
         */
        return HttpRequest
                .from(_creds.getAccountServer() + "/applications/" + _app.getId())
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

    public static Observable<AsyncApplication> findOne(OAuth2Token _creds, String _id) {
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

    public Observable<AsyncApplication> save() {
        /**
         * PATCH /applications/{app_id}
         */
        /**
         * @todo: implement
         */
        return Observable.error(new UnsupportedOperationException("Not supported yet."));
    }

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

    public Observable<String> addEUI(String _eui) {
        /**
         * PUT /applications/{app_id}/euis/{eui}
         */
        return HttpRequest
                .from(creds.getAccountServer() + "/applications/" + getId() + "/euis/" + _eui)
                .flatMap((HttpRequest t) -> t.inject(creds))
                .doOnNext((HttpRequest t) -> t.getBuilder().put(RequestBody.create(null, new byte[0])))
                .flatMap((HttpRequest t) -> t.doExecute())
                .map((Response r) -> _eui);
    }

    public Observable<String> deleteEUI(String _eui) {
        /**
         * DELETE /applications/{app_id}/euis/{eui}
         */
        return HttpRequest
                .from(creds.getAccountServer() + "/applications/" + getId() + "/euis/" + _eui)
                .flatMap((HttpRequest t) -> t.inject(creds))
                .doOnNext((HttpRequest t) -> t.getBuilder().delete())
                .flatMap((HttpRequest t) -> t.doExecute())
                .map((Response r) -> _eui);
    }

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

    public Observable<Collaborator> addCollaborator(Collaborator _collaborator) {
        /**
         * PUT /applications/{app_id}/collaborators/{username}
         */
        return HttpRequest
                .from(creds.getAccountServer() + "/applications/" + getId() + "/collaborators/" + _collaborator.getUsername())
                .flatMap((HttpRequest t) -> t.inject(creds))
                .flatMap((HttpRequest t) -> HttpRequest
                        .buildRequestBody(_collaborator)
                        .map((RequestBody rb) -> {
                            t.getBuilder().post(rb);
                            return t;
                        })
                )
                .flatMap((HttpRequest t) -> t.doExecute())
                .map((Response c) -> _collaborator);
    }

    public Observable<Collaborator> removeCollaborator(Collaborator _collaborator) {
        /**
         * DELETE /applications/{app_id}/euis/{eui}
         */
        return HttpRequest
                .from(creds.getAccountServer() + "/applications/" + getId() + "/collaborators/" + _collaborator.getUsername())
                .flatMap((HttpRequest t) -> t.inject(creds))
                .doOnNext((HttpRequest t) -> t.getBuilder().delete())
                .flatMap((HttpRequest t) -> t.doExecute())
                .map((Response c) -> _collaborator);
    }

    public Observable<AccessKey> getAccessKeys() {
        /**
         * GET /applications/{app_id}/access-keys
         */
        return HttpRequest
                .from(creds.getAccountServer() + "/applications/" + getId() + "/access-keys")
                .flatMap((HttpRequest t) -> t.inject(creds))
                .doOnNext((HttpRequest t) -> t.getBuilder().get())
                .flatMap((HttpRequest t) -> t.doExecuteForType(AccessKey[].class))
                .flatMap((AccessKey[] cs) -> Observable.from(cs));
    }

    public Observable<AccessKey> findOneAccessKey(String _keyname) {
        /**
         * GET /applications/{app_id}/access-keys/{keyname}
         */
        return HttpRequest
                .from(creds.getAccountServer() + "/applications/" + getId() + "/access-keys/" + _keyname)
                .flatMap((HttpRequest t) -> t.inject(creds))
                .doOnNext((HttpRequest t) -> t.getBuilder().get())
                .flatMap((HttpRequest t) -> t.doExecuteForType(AccessKey.class));
    }

    public Observable<AccessKey> addAccessKey(AccessKey _key) {
        /**
         * POST /applications/{app_id}/access-keys/{username}
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
                .flatMap((HttpRequest t) -> t.doExecute())
                .map((Response c) -> _key);
    }

    public Observable<AccessKey> removeAccessKey(AccessKey _key) {
        /**
         * DELETE /applications/{app_id}/access-keys/{keyname}
         */
        return HttpRequest
                .from(creds.getAccountServer() + "/applications/" + getId() + "/access-keys/" + _key.getName())
                .flatMap((HttpRequest t) -> t.inject(creds))
                .doOnNext((HttpRequest t) -> t.getBuilder().delete())
                .flatMap((HttpRequest t) -> t.doExecute())
                .map((Response c) -> _key);
    }

    public Observable<AsyncApplication> refresh() {
        return findOne(creds, getId())
                .doOnNext((AsyncApplication t) -> refresh(t))
                .map((AsyncApplication app) -> this);
    }

    public Observable<String> getRights() {
        return getRights(creds);
    }

    public Observable<String> getRights(OAuth2Token _creds) {
        /**
         * GET /applications/{app_id}/rights
         */
        return HttpRequest
                .from(creds.getAccountServer() + "/applications/" + getId() + "/rights")
                .flatMap((HttpRequest t) -> t.inject(_creds))
                .doOnNext((HttpRequest t) -> t.getBuilder().get())
                .flatMap((HttpRequest t) -> t.doExecuteForType(String[].class))
                .flatMap(Observable::from);
    }

    private static class EuiCreationResponse {

        public String eui;
    }
}
