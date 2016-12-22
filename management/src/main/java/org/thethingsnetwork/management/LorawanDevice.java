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
package org.thethingsnetwork.management;

import com.google.protobuf.ByteString;
import org.thethingsnetwork.management.proto.DeviceOuterClass;
import rx.Observable;
import rx.Subscriber;

/**
 *
 * @author Romain Cambier
 */
public class LorawanDevice {

    private byte[] appEui;
    private byte[] devEui;
    private final String appId;
    private final String devId;
    private byte[] devAddr;
    private byte[] nwkSKey;
    private byte[] appSKey;
    private byte[] appKey;
    private int fCntUp;
    private int fCntDown;
    private boolean disableFCntCheck;
    private boolean uses32BitFCnt;
    private String activationConstraints;
    private final long lastSeen;

    private LorawanDevice(byte[] _appEui, byte[] _devEui, String _appId, String _devId, byte[] _devAddr, byte[] _nwkSKey, byte[] _appSKey, byte[] _appKey, int _fCntUp, int _fCntDown, boolean _disableFCntCheck, boolean _uses32BitFCnt, String _activationConstraints, long _lastSeen) {
        appEui = _appEui;
        devEui = _devEui;
        appId = _appId;
        devId = _devId;
        devAddr = _devAddr;
        nwkSKey = _nwkSKey;
        appSKey = _appSKey;
        appKey = _appKey;
        fCntUp = _fCntUp;
        fCntDown = _fCntDown;
        disableFCntCheck = _disableFCntCheck;
        uses32BitFCnt = _uses32BitFCnt;
        activationConstraints = _activationConstraints;
        lastSeen = _lastSeen;
    }

    public static LorawanDevice createOTAA(String _appId, String _devId, byte[] _appEui, byte[] _devEui, byte[] _appKey) {
        if (_appEui.length != 8) {
            throw new IllegalArgumentException("appEui should be 8 bytes long");
        }
        if (_devEui.length != 8) {
            throw new IllegalArgumentException("devEui should be 8 bytes long");
        }
        if (_appKey.length != 16) {
            throw new IllegalArgumentException("appKey should be 16 bytes long");
        }
        return new LorawanDevice(_appEui, _devEui, _appId, _devId, null, null, null, _appKey, 0, 0, false, true, "otaa", 0);
    }

    public static LorawanDevice createABP(String _appId, String _devId, byte[] _appEui, byte[] _devEui, byte[] _devAddr, byte[] _nwkSKey, byte[] _appSKey, boolean _disableFCntCheck, boolean _uses32BitFCnt) {
        if (_appEui.length != 8) {
            throw new IllegalArgumentException("appEui should be 8 bytes long");
        }
        if (_devEui.length != 8) {
            throw new IllegalArgumentException("devEui should be 8 bytes long");
        }
        if (_devAddr.length != 4) {
            throw new IllegalArgumentException("devAddr should be 4 bytes long");
        }
        if (_nwkSKey.length != 16) {
            throw new IllegalArgumentException("nwkSKey should be 16 bytes long");
        }
        if (_appSKey.length != 16) {
            throw new IllegalArgumentException("appSKey should be 16 bytes long");
        }
        return new LorawanDevice(_appEui, _devEui, _appId, _devId, _devAddr, _nwkSKey, _appSKey, null, 0, 0, _disableFCntCheck, _uses32BitFCnt, "abp", 0);
    }

    public static Observable<LorawanDevice> from(DeviceOuterClass.Device _proto) {

        return Observable
                .create((Subscriber<? super LorawanDevice> t) -> {
                    try {
                        t.onNext(new LorawanDevice(
                                _proto.getAppEui().toByteArray(),
                                _proto.getDevEui().toByteArray(),
                                _proto.getAppId(),
                                _proto.getDevId(),
                                _proto.getDevAddr().toByteArray(),
                                _proto.getNwkSKey().toByteArray(),
                                _proto.getAppSKey().toByteArray(),
                                _proto.getAppKey().toByteArray(),
                                _proto.getFCntUp(),
                                _proto.getFCntDown(),
                                _proto.getDisableFCntCheck(),
                                _proto.getUses32BitFCnt(),
                                _proto.getActivationConstraints(),
                                _proto.getLastSeen()
                        ));
                        t.onCompleted();
                    } catch (Exception ex) {
                        t.onError(ex);
                    }
                });
    }

    public Observable<DeviceOuterClass.Device> toProto() {

        return Observable
                .create((Subscriber<? super DeviceOuterClass.Device> t) -> {
                    try {
                        t.onNext(DeviceOuterClass.Device.newBuilder()
                                .setAppEui(ByteString.copyFrom(appEui))
                                .setDevEui(ByteString.copyFrom(devEui))
                                .setAppId(appId)
                                .setDevId(appId)
                                .setDevAddr(ByteString.copyFrom(devAddr))
                                .setNwkSKey(ByteString.copyFrom(nwkSKey))
                                .setAppSKey(ByteString.copyFrom(appSKey))
                                .setAppKey(ByteString.copyFrom(appKey))
                                .setFCntUp(fCntUp)
                                .setFCntDown(fCntDown)
                                .setDisableFCntCheck(disableFCntCheck)
                                .setUses32BitFCnt(uses32BitFCnt)
                                .setActivationConstraints(activationConstraints)
                                .setLastSeen(lastSeen)
                                .build()
                        );
                        t.onCompleted();
                    } catch (Exception ex) {
                        t.onError(ex);
                    }
                });

    }

    public byte[] getAppEui() {
        return appEui;
    }

    public void setAppEui(byte[] _appEui) {
        if (_appEui.length != 8) {
            throw new IllegalArgumentException("appEui should be 8 bytes long");
        }
        appEui = _appEui;
    }

    public byte[] getDevEui() {
        return devEui;
    }

    public void setDevEui(byte[] _devEui) {
        if (_devEui.length != 8) {
            throw new IllegalArgumentException("devEui should be 8 bytes long");
        }
        devEui = _devEui;
    }

    public String getAppId() {
        return appId;
    }

    public String getDevId() {
        return devId;
    }

    public byte[] getDevAddr() {
        return devAddr;
    }

    public void setDevAddr(byte[] _devAddr) {
        if (_devAddr.length != 4) {
            throw new IllegalArgumentException("devAddr should be 4 bytes long");
        }
        devAddr = _devAddr;
    }

    public byte[] getNwkSKey() {
        return nwkSKey;
    }

    public void setNwkSKey(byte[] _nwkSKey) {
        if (_nwkSKey.length != 16) {
            throw new IllegalArgumentException("nwkSKey should be 16 bytes long");
        }
        nwkSKey = _nwkSKey;
    }

    public byte[] getAppSKey() {
        return appSKey;
    }

    public void setAppSKey(byte[] _appSKey) {
        if (_appSKey.length != 16) {
            throw new IllegalArgumentException("appSKey should be 16 bytes long");
        }
        appSKey = _appSKey;
    }

    public byte[] getAppKey() {
        return appKey;
    }

    public void setAppKey(byte[] _appKey) {
        if (_appKey.length != 16) {
            throw new IllegalArgumentException("appKey should be 16 bytes long");
        }
        appKey = _appKey;
    }

    public int getfCntUp() {
        return fCntUp;
    }

    public int getfCntDown() {
        return fCntDown;
    }

    public boolean isDisableFCntCheck() {
        return disableFCntCheck;
    }

    public void setDisableFCntCheck(boolean _disableFCntCheck) {
        disableFCntCheck = _disableFCntCheck;
    }

    public boolean isUses32BitFCnt() {
        return uses32BitFCnt;
    }

    public void setUses32BitFCnt(boolean _uses32BitFCnt) {
        uses32BitFCnt = _uses32BitFCnt;
    }

    public String getActivationConstraints() {
        return activationConstraints;
    }

    public void setActivationConstraints(String _activationConstraints) {
        activationConstraints = _activationConstraints;
    }

    public long getLastSeen() {
        return lastSeen;
    }

    public void resetFCntUp() {
        fCntUp = 0;
    }

    public void resetFCntDown() {
        fCntDown = 0;
    }

}
