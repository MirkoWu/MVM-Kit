package com.mirkowu.lib_bluetooth;

public class BtRequestImp {

    private static class Singleton {
        private static final BtRequestImp sInstance = new BtRequestImp();
    }

    private BtRequestImp() {
    }

    public static BtRequestImp getInstance() {
        return Singleton.sInstance;
    }


    public void init(){

    }

    public void connect(){

    }

    public
}
