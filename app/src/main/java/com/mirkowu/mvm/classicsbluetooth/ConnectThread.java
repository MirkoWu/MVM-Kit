package com.mirkowu.mvm.classicsbluetooth;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.util.Log;

import java.io.IOException;

public class ConnectThread extends Thread {
    public static final String TAG = "ConnectThread";
    private final BluetoothSocket mSocket;
    private final BluetoothDevice mDevice;

    public ConnectThread(BluetoothDevice device) {
        mDevice = device;
        BluetoothSocket tmp = null;

        // Get a BluetoothSocket for a connection with the
        // given BluetoothDevice
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

            // Make a connection to the BluetoothSocket
            try {
                // This is a blocking call and will only return on a
                // successful connection or an exception
                mSocket.connect();
            } catch (Exception e) {
                // Close the socket
                try {
                    mSocket.close();
                } catch (IOException e2) {
                    Log.e(TAG, "unable to close() socket during connection failure", e2);
                }
                BtClient.getInstance().connectionFailed();
                return;
            }


            // Start the connected thread
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