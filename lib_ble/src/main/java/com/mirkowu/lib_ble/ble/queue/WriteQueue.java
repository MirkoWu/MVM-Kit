package com.mirkowu.lib_ble.ble.queue;


import androidx.annotation.NonNull;

import com.mirkowu.lib_ble.ble.BleRequestImpl;
import com.mirkowu.lib_ble.ble.model.BleDevice;

public final class WriteQueue extends Queue{

    private static volatile WriteQueue sInstance;
    protected BleRequestImpl bleRequest;

    private WriteQueue() {
        bleRequest = BleRequestImpl.getBleRequest();
    }

    @NonNull
    public static WriteQueue getInstance() {
        if (sInstance != null) {
            return sInstance;
        }
        synchronized (WriteQueue.class) {
            if (sInstance == null) {
                sInstance = new WriteQueue();
            }
        }
        return sInstance;
    }

    @Override
    public void execute(RequestTask requestTask) {
        BleDevice[] devices = requestTask.getDevices();
        for (BleDevice device: devices) {
            bleRequest.writeCharacteristic(device.getAddress(), requestTask.getData());
        }
    }

}
