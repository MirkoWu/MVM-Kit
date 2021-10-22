package com.mirkowu.lib_ble.ble.callback;


import com.mirkowu.lib_ble.ble.model.BleDevice;

/**
 * Created by LiuLei on 2018/6/2.
 */

public abstract class BleMtuCallback<T> {

    public void onMtuChanged(BleDevice device, int mtu, int status){}

}
