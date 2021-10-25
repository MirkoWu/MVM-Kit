package com.mirkowu.mvm.classicsbluetooth.callback;

public interface OnConnectStateCallback {
    void onConnectSuccess();

    void onConnectFailed();

    void onConnecting();

    void onWaitConnect();

    void onConnectStateChanged(int state);
}
