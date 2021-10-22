package com.mirkowu.lib_ble.ble.request;

import com.mirkowu.lib_ble.ble.annotation.Implement;
import com.mirkowu.lib_ble.ble.BleRequestImpl;
import com.mirkowu.lib_ble.ble.callback.BleReadRssiCallback;
import com.mirkowu.lib_ble.ble.callback.wrapper.ReadRssiWrapperCallback;
import com.mirkowu.lib_ble.ble.model.BleDevice;

/**
 *
 * Created by LiuLei on 2017/10/23.
 */
@Implement(ReadRssiRequest.class)
public class ReadRssiRequest<T extends BleDevice> implements ReadRssiWrapperCallback<T> {

    private BleReadRssiCallback<T> readRssiCallback;
    private final BleRequestImpl<T> bleRequest = BleRequestImpl.getBleRequest();

    public boolean readRssi(T device, BleReadRssiCallback<T> callback){
        this.readRssiCallback = callback;
        boolean result = false;
        if (bleRequest != null) {
            result = bleRequest.readRssi(device.getAddress());
        }
        return result;
    }

    @Override
    public void onReadRssiSuccess(T device, int rssi) {
        if(readRssiCallback != null){
            readRssiCallback.onReadRssiSuccess(device, rssi);
        }
    }
}
