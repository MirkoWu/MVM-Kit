package com.mirkowu.mvm.classicsbluetooth;

import android.util.Log;

import com.mirkowu.lib_util.LogUtil;

public class BtLog {
    private static final String TAG = "BtLog";
    private static boolean sDebug;

    private BtLog() {
        throw new IllegalStateException("you can't instantiate me!");
    }

    public static boolean isDebug() {
        return sDebug;
    }

    public static void setDebug(boolean debug) {
        sDebug = debug;
    }
    public static void i(String tag, String message) {
        if (sDebug) {
            LogUtil.i(tag, message);
        }
    }

    public static void i(String message) {
        if (sDebug) {
            LogUtil.i(TAG, message);
        }
    }

    public static void d(String tag, String message) {
        if (sDebug) {
            LogUtil.d(tag, message);
        }
    }

    public static void d(String message) {
        if (sDebug) {
            LogUtil.d(TAG, message);
        }
    }

    public static void w(String tag, String message) {
        if (sDebug) {
            LogUtil.w(tag, message);
        }
    }

    public static void w(String message) {
        if (sDebug) {
            LogUtil.w(TAG, message);
        }
    }

    public static void e(String tag, String message) {
        if (sDebug) {
            LogUtil.e(tag, message);
        }
    }

    public static void e(String message) {
        if (sDebug) {
            LogUtil.e(TAG, message);
        }
    }
}