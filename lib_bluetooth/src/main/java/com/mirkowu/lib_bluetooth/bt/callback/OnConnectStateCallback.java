package com.mirkowu.lib_bluetooth.bt.callback;

import android.bluetooth.BluetoothDevice;

public interface OnConnectStateCallback {
    void onConnectSuccess(BluetoothDevice device);

    void onDisConnect(BluetoothDevice device);

    void onConnectFailed();

    void onConnecting();

    void onWaitConnect();

    void onConnectStateChanged(int state);
}
