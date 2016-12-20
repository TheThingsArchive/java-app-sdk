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
package org.thethingsnetwork.account.sync;

import java.util.List;
import org.thethingsnetwork.account.common.AbstractApplication;
import org.thethingsnetwork.account.common.AccessKey;
import org.thethingsnetwork.account.common.Collaborator;
import org.thethingsnetwork.account.async.AsyncApplication;
import org.thethingsnetwork.account.async.auth.token.AsyncOAuth2Token;

/**
 *
 * @author Romain Cambier
 */
public class Application implements AbstractApplication{

    private final AsyncApplication wrapped;

    public Application(String _id, String _name) {
        wrapped = new AsyncApplication(_id, _name);
    }

    private Application(AsyncApplication _wrap) {
        wrapped = _wrap;
    }

    public static List<Application> findAll(AsyncOAuth2Token _creds) {
        return AsyncApplication.findAll(_creds)
                .map((AsyncApplication t) -> new Application(t))
                .toList()
                .toBlocking()
                .single();
    }

    public static Application create(AsyncOAuth2Token _creds, AbstractApplication _app) {
        return AsyncApplication.create(_creds, _app)
                .map((AsyncApplication t) -> new Application(t))
                .toBlocking()
                .single();
    }

    public static Application findOne(AsyncOAuth2Token _creds, String _id) {
        return AsyncApplication.findOne(_creds, _id)
                .map((AsyncApplication t) -> new Application(t))
                .toBlocking()
                .singleOrDefault(null);
    }

    public Application save() {
        return wrapped.save()
                .map((AsyncApplication t) -> this)
                .toBlocking()
                .single();
    }

    public Application delete() {
        return wrapped.delete()
                .map((AsyncApplication t) -> this)
                .toBlocking()
                .single();
    }

    public List<String> findAllEUIs() {
        return wrapped.findAllEUIs()
                .toList()
                .toBlocking()
                .single();
    }

    public String createEUI() {
        return wrapped.createEUI()
                .toBlocking()
                .single();
    }

    public String addEUI(String _eui) {
        return wrapped.addEUI(_eui)
                .toBlocking()
                .single();
    }

    public String deleteEUI(String _eui) {
        return wrapped.deleteEUI(_eui)
                .toBlocking()
                .single();
    }

    public List<Collaborator> getCollaborators() {
        return wrapped.getCollaborators()
                .toList()
                .toBlocking()
                .single();
    }

    public Collaborator findOneCollaborator(String _username) {
        return wrapped.findOneCollaborator(_username)
                .toBlocking()
                .singleOrDefault(null);
    }

    public Collaborator addCollaborator(Collaborator _collaborator) {
        return wrapped.addCollaborator(_collaborator)
                .toBlocking()
                .single();
    }

    public Collaborator removeCollaborator(Collaborator _collaborator) {
        return wrapped.removeCollaborator(_collaborator)
                .toBlocking()
                .single();
    }

    public List<AccessKey> getAccessKeys() {
        return wrapped.getAccessKeys()
                .toList()
                .toBlocking()
                .single();
    }

    public AccessKey findOneAccessKey(String _keyname) {
        return wrapped.findOneAccessKey(_keyname)
                .toBlocking()
                .singleOrDefault(null);
    }

    public AccessKey addAccessKey(AccessKey _key) {
        return wrapped.addAccessKey(_key)
                .toBlocking()
                .single();
    }

    public AccessKey removeAccessKey(AccessKey _key) {
        return wrapped.removeAccessKey(_key)
                .toBlocking()
                .single();
    }

    public Application refresh() {
        return wrapped.refresh()
                .map((AsyncApplication app) -> this)
                .toBlocking()
                .single();
    }

    public List<String> getRights() {
        return wrapped.getRights()
                .toList()
                .toBlocking()
                .single();
    }

    public List<String> getRights(AsyncOAuth2Token _creds) {
        return wrapped.getRights(_creds)
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
    public void updateCredentials(AsyncOAuth2Token _creds) {
        wrapped.updateCredentials(_creds);
    }

}
