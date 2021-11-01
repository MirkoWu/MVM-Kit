package com.mirkowu.lib_bluetooth.exception;

/**
 * Created by LiuLei on 2017/10/19.
 */

public class BtWriteException extends BtException {

    private static final long serialVersionUID = -6886122979840622897L;

    public BtWriteException() {
    }

    public BtWriteException(String message) {
        super(message);
    }

    public BtWriteException(String s, Throwable ex) {
        super(s, ex);
    }
}
