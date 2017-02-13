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
package org.thethingsnetwork.management;

import com.google.protobuf.ByteString;
import org.thethingsnetwork.management.proto.DeviceOuterClass;
import rx.Observable;
import rx.Subscriber;

/**
 * This class is a representation of a The Things Network LoraWan device
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

    /**
     * Create OTAA (over the air activation) LoraWan data
     *
     * @param _appId The application id
     * @param _devId The device id
     * @param _appEui The application EUI
     * @param _devEui The device EUI
     * @param _appKey The application key
     * @return The corresponding LoraWan data
     */
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

    /**
     * Create ABP (activation by personalization) LoraWan data
     *
     * @param _appId The application id
     * @param _devId The device id
     * @param _appEui The application EUI
     * @param _devEui The device EUI
     * @param _devAddr The device address
     * @param _nwkSKey The network session key
     * @param _appSKey The application session key
     * @param _disableFCntCheck Whether if you want to disable fCnt check or not
     * @param _uses32BitFCnt Whether to use 32 bits frame counters or not
     * @return The corresponding LoraWan data
     */
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

    /**
     * Build a LorawanDevice instance from a grpc representation
     *
     * @param _proto The grpc representation
     * @return An Observable LorawanDevice containing the LorawanDevice instance
     */
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

    /**
     * Convert this device to the grpc representation
     *
     * @return The grpc representation
     */
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

    /**
     * Get the application EUI
     *
     * @return the application EUI
     */
    public byte[] getAppEui() {
        return appEui;
    }

    /**
     * Set the application EUI
     *
     * @param _appEui the application EUI
     */
    public void setAppEui(byte[] _appEui) {
        if (_appEui.length != 8) {
            throw new IllegalArgumentException("appEui should be 8 bytes long");
        }
        appEui = _appEui;
    }

    /**
     * Get the device EUI
     *
     * @return The device EUI
     */
    public byte[] getDevEui() {
        return devEui;
    }

    /**
     * Set the device EUI
     *
     * @param _devEui the device EUI
     */
    public void setDevEui(byte[] _devEui) {
        if (_devEui.length != 8) {
            throw new IllegalArgumentException("devEui should be 8 bytes long");
        }
        devEui = _devEui;
    }

    /**
     * Get the application id
     *
     * @return The application id
     */
    public String getAppId() {
        return appId;
    }

    /**
     * Get the device id
     *
     * @return The device id
     */
    public String getDevId() {
        return devId;
    }

    /**
     * Get the device address
     *
     * @return The device address
     */
    public byte[] getDevAddr() {
        return devAddr;
    }

    /**
     * Set the device address
     *
     * @param _devAddr The device address
     */
    public void setDevAddr(byte[] _devAddr) {
        if (_devAddr.length != 4) {
            throw new IllegalArgumentException("devAddr should be 4 bytes long");
        }
        devAddr = _devAddr;
    }

    /**
     * Get the network session key
     *
     * @return The network session key
     */
    public byte[] getNwkSKey() {
        return nwkSKey;
    }

    /**
     * Set the network session key
     *
     * @param _nwkSKey The network session key
     */
    public void setNwkSKey(byte[] _nwkSKey) {
        if (_nwkSKey.length != 16) {
            throw new IllegalArgumentException("nwkSKey should be 16 bytes long");
        }
        nwkSKey = _nwkSKey;
    }

    /**
     * Get the application session key
     *
     * @return The application session key
     */
    public byte[] getAppSKey() {
        return appSKey;
    }

    /**
     * Set the application session key
     *
     * @param _appSKey The application session key
     */
    public void setAppSKey(byte[] _appSKey) {
        if (_appSKey.length != 16) {
            throw new IllegalArgumentException("appSKey should be 16 bytes long");
        }
        appSKey = _appSKey;
    }

    /**
     * Get the application key
     *
     * @return The application key
     */
    public byte[] getAppKey() {
        return appKey;
    }

    /**
     * Set the application key
     *
     * @param _appKey The application key
     */
    public void setAppKey(byte[] _appKey) {
        if (_appKey.length != 16) {
            throw new IllegalArgumentException("appKey should be 16 bytes long");
        }
        appKey = _appKey;
    }

    /**
     * Get the uplink frame counter
     *
     * @return The uplink frame counter
     */
    public int getfCntUp() {
        return fCntUp;
    }

    /**
     * Get the downlink frame counter
     *
     * @return The downlink frame counter
     */
    public int getfCntDown() {
        return fCntDown;
    }

    /**
     * Check if fCnt check is enabled
     *
     * @return True if fCnt check is enabled
     */
    public boolean isDisableFCntCheck() {
        return disableFCntCheck;
    }

    /**
     * Enable/Disable fCnt check
     *
     * @param _disableFCntCheck True to enable, false to disable
     */
    public void setDisableFCntCheck(boolean _disableFCntCheck) {
        disableFCntCheck = _disableFCntCheck;
    }

    /**
     * Check if 32 bit frame counter are used
     *
     * @return True if 32 bit frame counter are used
     */
    public boolean isUses32BitFCnt() {
        return uses32BitFCnt;
    }

    /**
     * Enable/Disable 32 bit frame counter
     *
     * @param _uses32BitFCnt True to enable 32 bit frame counter
     */
    public void setUses32BitFCnt(boolean _uses32BitFCnt) {
        uses32BitFCnt = _uses32BitFCnt;
    }

    /**
     * Get the activation constraints
     *
     * @return The activation constraints
     */
    public String getActivationConstraints() {
        return activationConstraints;
    }

    /**
     * Set the activation constraints
     *
     * @param _activationConstraints The activation constraints
     */
    public void setActivationConstraints(String _activationConstraints) {
        activationConstraints = _activationConstraints;
    }

    /**
     * Get the last time device was seen
     *
     * @return The last time device was seen
     */
    public long getLastSeen() {
        return lastSeen;
    }

    /**
     * Reset uplink frame counter
     */
    public void resetFCntUp() {
        fCntUp = 0;
    }

    /**
     * Reset downlink frame counter
     */
    public void resetFCntDown() {
        fCntDown = 0;
    }

}
