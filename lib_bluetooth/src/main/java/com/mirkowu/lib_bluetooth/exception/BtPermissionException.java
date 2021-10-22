package com.mirkowu.lib_bluetooth.exception;

/**
 * Created by LiuLei on 2017/10/19.
 */

public class BtPermissionException extends BtException {

    private static final long serialVersionUID = -6791491579172360482L;

    public BtPermissionException() {
    }

    public BtPermissionException(String message) {
        super(message);
    }

    public BtPermissionException(String s, Throwable ex) {
        super(s, ex);
    }
}
