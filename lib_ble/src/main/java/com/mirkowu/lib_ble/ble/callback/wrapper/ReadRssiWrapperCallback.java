package com.mirkowu.lib_ble.ble.callback.wrapper;

/**
 * Created by LiuLei on 2017/10/23.
 */

public interface ReadRssiWrapperCallback<T> {

    void onReadRssiSuccess(T device, int rssi);
}
