package com.mirkowu.lib_ble.ble.request;

import android.bluetooth.BluetoothGattDescriptor;

import com.mirkowu.lib_ble.ble.BleRequestImpl;
import com.mirkowu.lib_ble.ble.annotation.Implement;
import com.mirkowu.lib_ble.ble.BleManager;
import com.mirkowu.lib_ble.ble.callback.BleReadDescCallback;
import com.mirkowu.lib_ble.ble.callback.BleWriteDescCallback;
import com.mirkowu.lib_ble.ble.callback.wrapper.BleWrapperCallback;
import com.mirkowu.lib_ble.ble.callback.wrapper.DescWrapperCallback;
import com.mirkowu.lib_ble.ble.model.BleDevice;

import java.util.UUID;


/**
 *
 * Created by LiuLei on 2017/10/23.
 */
@Implement(DescriptorRequest.class)
public class DescriptorRequest<T extends BleDevice> implements DescWrapperCallback<T> {

    private BleReadDescCallback<T> bleReadDescCallback;
    private BleWriteDescCallback<T> bleWriteDescCallback;
    private final BleWrapperCallback<T> bleWrapperCallback = BleManager.options().getBleWrapperCallback();
    private final BleRequestImpl<T> bleRequest = BleRequestImpl.getBleRequest();

    public boolean readDes(T device, UUID serviceUUID, UUID characteristicUUID, UUID descriptorUUID, BleReadDescCallback<T> callback){
        this.bleReadDescCallback = callback;
        return bleRequest.readDescriptor(device.getAddress(), serviceUUID, characteristicUUID, descriptorUUID);
    }

    public boolean writeDes(T device, byte[] data, UUID serviceUUID, UUID characteristicUUID, UUID descriptorUUID, BleWriteDescCallback<T> callback){
        this.bleWriteDescCallback = callback;
        return bleRequest.writeDescriptor(device.getAddress(), data, serviceUUID, characteristicUUID, descriptorUUID);
    }


    @Override
    public void onDescReadSuccess(T device, BluetoothGattDescriptor descriptor) {
        if (bleReadDescCallback != null){
            bleReadDescCallback.onDescReadSuccess(device, descriptor);
        }

        if (bleWrapperCallback != null){
            bleWrapperCallback.onDescReadSuccess(device, descriptor);
        }
    }

    @Override
    public void onDescReadFailed(T device, int failedCode) {
        if (bleReadDescCallback != null){
            bleReadDescCallback.onDescReadFailed(device, failedCode);
        }

        if (bleWrapperCallback != null){
            bleWrapperCallback.onDescReadFailed(device, failedCode);
        }
    }

    @Override
    public void onDescWriteSuccess(T device, BluetoothGattDescriptor descriptor) {
        if (bleWriteDescCallback != null){
            bleWriteDescCallback.onDescWriteSuccess(device, descriptor);
        }

        if (bleWrapperCallback != null){
            bleWrapperCallback.onDescWriteSuccess(device, descriptor);
        }
    }

    @Override
    public void onDescWriteFailed(T device, int failedCode) {
        if (bleWriteDescCallback != null){
            bleWriteDescCallback.onDescWriteFailed(device, failedCode);
        }

        if (bleWrapperCallback != null){
            bleWrapperCallback.onDescWriteFailed(device, failedCode);
        }
    }
}
