package com.mirkowu.lib_bluetooth.exception;

/**
 * Created by LiuLei on 2017/10/19.
 */

public class BleWriteException extends BtException {

    private static final long serialVersionUID = -6886122979840622897L;

    public BleWriteException() {
    }

    public BleWriteException(String message) {
        super(message);
    }

    public BleWriteException(String s, Throwable ex) {
        super(s, ex);
    }
}
