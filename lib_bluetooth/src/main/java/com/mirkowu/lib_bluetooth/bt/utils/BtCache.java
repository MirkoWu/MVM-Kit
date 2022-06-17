package com.mirkowu.lib_bluetooth.bt.utils;


import com.mirkowu.lib_util.utilcode.util.SPUtils;

public class BtCache {
    public static final String FILE_NAME = "bt_cache";
    public static final String KEY_LAST_CONNECT_ADDRESS = "bt_cache";

    public static SPUtils getInstance() {
        return SPUtils.getInstance(FILE_NAME);
    }

    public static String getLastConnectDevice() {
        return getInstance().getString(KEY_LAST_CONNECT_ADDRESS);
    }

    public static void putConnectDevice(String value) {
        getInstance().put(KEY_LAST_CONNECT_ADDRESS, value);
    }
}
