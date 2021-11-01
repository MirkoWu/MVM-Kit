package com.mirkowu.lib_bluetooth.bt.utils;

import android.util.Log;


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
            Log.i(tag, message);
        }
    }

    public static void i(String message) {
        if (sDebug) {
            Log.i(TAG, message);
        }
    }

    public static void d(String tag, String message) {
        if (sDebug) {
            Log.d(tag, message);
        }
    }

    public static void d(String message) {
        if (sDebug) {
            Log.d(TAG, message);
        }
    }

    public static void w(String tag, String message) {
        if (sDebug) {
            Log.w(tag, message);
        }
    }

    public static void w(String message) {
        if (sDebug) {
            Log.w(TAG, message);
        }
    }

    public static void e(String tag, String message) {
        if (sDebug) {
            Log.e(tag, message);
        }
    }

    public static void e(String message) {
        if (sDebug) {
            Log.e(TAG, message);
        }
    }
}