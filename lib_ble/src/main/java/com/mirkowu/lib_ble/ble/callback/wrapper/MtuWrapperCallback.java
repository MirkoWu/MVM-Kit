package com.mirkowu.lib_ble.ble.callback.wrapper;

/**
 * Created by LiuLei on 2018/6/2.
 */

public interface MtuWrapperCallback<T> {

    void onMtuChanged(T device, int mtu, int status);

}
