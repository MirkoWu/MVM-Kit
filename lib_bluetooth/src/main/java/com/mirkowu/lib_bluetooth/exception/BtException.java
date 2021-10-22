package com.mirkowu.lib_bluetooth.exception;

import java.io.Serializable;


/**
 *
 * Created by LiuLei on 2017/10/19.
 */

public class BtException extends RuntimeException implements Serializable{

    private static final long serialVersionUID = -3677084962477320584L;

    private Throwable ex;

    public BtException(){
        super();
    }

    public BtException(String message){
        super(message);
    }

    public BtException(String s, Throwable ex) {
        super(s, null);  //  Disallow initCause
        this.ex = ex;
    }

    public BtException(Throwable cause) {
        super(cause);
    }

    public Throwable getException() {
        return ex;
    }

    public Throwable getCause() {
        return ex;
    }
}
