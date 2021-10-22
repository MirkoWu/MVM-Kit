package com.mirkowu.lib_ble.ble.proxy;
import com.mirkowu.lib_ble.ble.callback.BleConnectCallback;
import com.mirkowu.lib_ble.ble.callback.BleMtuCallback;
import com.mirkowu.lib_ble.ble.callback.BleNotifyCallback;
import com.mirkowu.lib_ble.ble.callback.BleReadCallback;
import com.mirkowu.lib_ble.ble.callback.BleReadRssiCallback;
import com.mirkowu.lib_ble.ble.callback.BleScanCallback;
import com.mirkowu.lib_ble.ble.callback.BleWriteCallback;
import com.mirkowu.lib_ble.ble.callback.BleWriteEntityCallback;
import com.mirkowu.lib_ble.ble.model.EntityData;

import java.util.UUID;

/**
 *
 * Created by LiuLei on 2017/10/30.
 */

public interface RequestListener<T> {

    void startScan(BleScanCallback<T> callback, long scanPeriod);

    void stopScan();

    boolean connect(T device, BleConnectCallback<T> callback);

    boolean connect(String address, BleConnectCallback<T> callback);

    void notify(T device, BleNotifyCallback<T> callback);

    void cancelNotify(T device, BleNotifyCallback<T> callback);

    void enableNotify(T device, boolean enable, BleNotifyCallback<T> callback);

    void enableNotifyByUuid(T device, boolean enable, UUID serviceUUID, UUID characteristicUUID, BleNotifyCallback<T> callback);

    void disconnect(T device);

    void disconnect(T device, BleConnectCallback<T> callback);

    boolean read(T device, BleReadCallback<T> callback);

    boolean readByUuid(T device, UUID serviceUUID, UUID characteristicUUID, BleReadCallback<T> callback);

    boolean readRssi(T device, BleReadRssiCallback<T> callback);

    boolean write(T device, byte[]data, BleWriteCallback<T> callback);

    boolean writeByUuid(T device, byte[]data, UUID serviceUUID, UUID characteristicUUID, BleWriteCallback<T> callback);

    void writeEntity(T device, final byte[]data, int packLength, int delay, BleWriteEntityCallback<T> callback);

    void writeEntity(EntityData entityData, BleWriteEntityCallback<T> callback);

    void cancelWriteEntity();

    boolean setMtu(String address, int mtu, BleMtuCallback<T> callback);

}
