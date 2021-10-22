package com.mirkowu.lib_ble.ble.request;


import com.mirkowu.lib_ble.ble.BleRequestImpl;
import com.mirkowu.lib_ble.ble.annotation.Implement;
import com.mirkowu.lib_ble.ble.BleManager;
import com.mirkowu.lib_ble.ble.callback.BleMtuCallback;
import com.mirkowu.lib_ble.ble.callback.wrapper.BleWrapperCallback;
import com.mirkowu.lib_ble.ble.callback.wrapper.MtuWrapperCallback;
import com.mirkowu.lib_ble.ble.model.BleDevice;

/**
 *
 * Created by LiuLei on 2017/10/23.
 */
@Implement(MtuRequest.class)
public class MtuRequest<T extends BleDevice> implements MtuWrapperCallback<T> {

    private BleMtuCallback<T> bleMtuCallback;
    private final BleWrapperCallback<T> bleWrapperCallback = BleManager.options().getBleWrapperCallback();
    private final BleRequestImpl<T> bleRequest = BleRequestImpl.getBleRequest();

    public boolean setMtu(String address, int mtu, BleMtuCallback<T> callback){
        this.bleMtuCallback = callback;
        return bleRequest.setMtu(address, mtu);
    }

    @Override
    public void onMtuChanged(T device, int mtu, int status) {
        if(null != bleMtuCallback){
            bleMtuCallback.onMtuChanged(device, mtu, status);
        }

        if (bleWrapperCallback != null){
            bleWrapperCallback.onMtuChanged(device, mtu, status);
        }
    }
}
