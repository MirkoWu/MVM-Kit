package com.mirkowu.lib_bluetooth.bt.core;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.util.Log;


import com.mirkowu.lib_bluetooth.bt.BtManager;

import java.io.IOException;

public class ConnectThread extends Thread {
    public static final String TAG = "ConnectThread";
    private final BluetoothSocket mSocket;
    private final BluetoothDevice mDevice;

    public ConnectThread(BluetoothDevice device) {
        mDevice = device;
        BluetoothSocket tmp = null;
        try {
            tmp = device.createRfcommSocketToServiceRecord(BtClient.MY_UUID);
        } catch (Exception e) {
            Log.e(TAG, "Socket create() failed", e);
        }
        mSocket = tmp;
    }

    public void run() {
        try {
            setName("ConnectThread");

            // Always cancel discovery because it will slow down a connection
            BtManager.getInstance().cancelDiscovery();

            try {
                mSocket.connect();
            } catch (Exception e) {
                try {
                    mSocket.close();
                } catch (IOException e2) {
                    Log.e(TAG, "unable to close() socket during connection failure", e2);
                }
                BtClient.getInstance().connectionFailed();
                return;
            }

            BtClient.getInstance().connected(mSocket, mDevice);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void cancel() {
        try {
            mSocket.close();
        } catch (Exception e) {
            Log.e(TAG, "close() of connect socket failed", e);
        }
    }
}