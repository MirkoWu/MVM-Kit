package com.mirkowu.lib_ble.ble.request;

import android.bluetooth.BluetoothGattCharacteristic;

import com.mirkowu.lib_ble.ble.BleRequestImpl;
import com.mirkowu.lib_ble.ble.annotation.Implement;
import com.mirkowu.lib_ble.ble.BleManager;
import com.mirkowu.lib_ble.ble.callback.BleNotifyCallback;
import com.mirkowu.lib_ble.ble.callback.wrapper.BleWrapperCallback;
import com.mirkowu.lib_ble.ble.callback.wrapper.NotifyWrapperCallback;
import com.mirkowu.lib_ble.ble.model.BleDevice;

import java.util.UUID;



/**
 * Created by LiuLei on 2017/10/23.
 */
@Implement(NotifyRequest.class)
public class NotifyRequest<T extends BleDevice> implements NotifyWrapperCallback<T> {

    private static final String TAG = "NotifyRequest";
    private BleNotifyCallback<T> notifyCallback;
    private final BleWrapperCallback<T> bleWrapperCallback = BleManager.options().getBleWrapperCallback();
    private final BleRequestImpl<T> bleRequest = BleRequestImpl.getBleRequest();

    public void notify(T device, boolean enable, BleNotifyCallback<T> callback) {
        notifyCallback = callback;
        bleRequest.setCharacteristicNotification(device.getAddress(), enable);
    }

    public void notifyByUuid(T device, boolean enable, UUID serviceUUID, UUID characteristicUUID, BleNotifyCallback<T> callback) {
        notifyCallback = callback;
        bleRequest.setCharacteristicNotificationByUuid(device.getAddress(),enable, serviceUUID, characteristicUUID);
    }

    @Deprecated
    public void cancelNotify(T device, BleNotifyCallback<T> callback) {
        notifyCallback = callback;
        bleRequest.setCharacteristicNotification(device.getAddress(), false);
    }

    @Override
    public void onChanged(final T device, final BluetoothGattCharacteristic characteristic) {
        if (null != notifyCallback){
            notifyCallback.onChanged(device, characteristic);
        }

        if (bleWrapperCallback != null){
            bleWrapperCallback.onChanged(device, characteristic);
        }
    }

    @Override
    public void onNotifySuccess(final T device) {
        if (null != notifyCallback){
            notifyCallback.onNotifySuccess(device);
        }
        if (bleWrapperCallback != null){
            bleWrapperCallback.onNotifySuccess(device);
        }
    }

    @Override
    public void onNotifyFailed(T device, int failedCode) {
        if (null != notifyCallback){
            notifyCallback.onNotifyFailed(device, failedCode);
        }

        if (bleWrapperCallback != null){
            bleWrapperCallback.onNotifyFailed(device, failedCode);
        }
    }

    @Override
    public void onNotifyCanceled(T device) {
        if (null != notifyCallback){
            notifyCallback.onNotifyCanceled(device);
        }

        if (bleWrapperCallback != null){
            bleWrapperCallback.onNotifyCanceled(device);
        }
    }
}
