package com.mirkowu.lib_ble.ble.request;

import android.bluetooth.BluetoothGattCharacteristic;

import com.mirkowu.lib_ble.ble.BleRequestImpl;
import com.mirkowu.lib_ble.ble.annotation.Implement;
import com.mirkowu.lib_ble.ble.BleManager;
import com.mirkowu.lib_ble.ble.callback.BleReadCallback;
import com.mirkowu.lib_ble.ble.callback.wrapper.BleWrapperCallback;
import com.mirkowu.lib_ble.ble.callback.wrapper.ReadWrapperCallback;
import com.mirkowu.lib_ble.ble.model.BleDevice;

import java.util.UUID;



/**
 *
 * Created by LiuLei on 2017/10/23.
 */
@Implement(ReadRequest.class)
public class ReadRequest<T extends BleDevice> implements ReadWrapperCallback<T> {

    private BleReadCallback<T> bleReadCallback;
    private final BleWrapperCallback<T> bleWrapperCallback = BleManager.options().getBleWrapperCallback();
    private final BleRequestImpl<T> bleRequest = BleRequestImpl.getBleRequest();

    public boolean read(T device, BleReadCallback<T> callback){
        this.bleReadCallback = callback;
        return bleRequest.readCharacteristic(device.getAddress());
    }

    public boolean readByUuid(T device, UUID serviceUUID, UUID characteristicUUID, BleReadCallback<T> callback){
        this.bleReadCallback = callback;
        return bleRequest.readCharacteristicByUuid(device.getAddress(), serviceUUID, characteristicUUID);
    }

    @Override
    public void onReadSuccess(T device, BluetoothGattCharacteristic characteristic) {
        if(bleReadCallback != null){
            bleReadCallback.onReadSuccess(device, characteristic);
        }

        if (bleWrapperCallback != null){
            bleWrapperCallback.onReadSuccess(device, characteristic);
        }
    }

    @Override
    public void onReadFailed(T device, int failedCode) {
        if(bleReadCallback != null){
            bleReadCallback.onReadFailed(device, failedCode);
        }

        if (bleWrapperCallback != null){
            bleWrapperCallback.onReadFailed(device, failedCode);
        }
    }
}
