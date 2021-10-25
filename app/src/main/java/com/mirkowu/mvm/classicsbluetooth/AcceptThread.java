package com.mirkowu.mvm.classicsbluetooth;

import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.util.Log;

import java.io.IOException;

public class AcceptThread extends Thread {
    public static final String TAG = "AcceptThread";
    public static final String NAME = "BtClient";
    // The local server socket
    private final BluetoothServerSocket mServerSocket;
    private int mState;

    public AcceptThread() {
        BluetoothServerSocket serverSocket = null;

        // Create a new listening server socket
        try {
            serverSocket = BtManager.getInstance().listenUsingRfcommWithServiceRecord(NAME, BtService.MY_UUID);
        } catch (Exception e) {
            Log.e(TAG, "Socket listen() failed", e);
        }
        mServerSocket = serverSocket;
    }

    public void setState(int state) {
        mState = state;
    }

    public void run() {
        try {
            Log.d(TAG, "Socket  BEGIN mAcceptThread" + this);
            setName("AcceptThread");

            BluetoothSocket socket = null;

            // Listen to the server socket if we're not connected
            while (mState != BtClient.STATE_CONNECTED) {
                try {
                    // This is a blocking call and will only return on a
                    // successful connection or an exception
                    socket = mServerSocket.accept();
                } catch (Exception e) {
                    Log.e(TAG, "Socket accept() failed", e);
                    break;
                }

                // If a connection was accepted
                if (socket != null) {
                    synchronized (AcceptThread.this) {
                        switch (mState) {
                            case BtClient.STATE_LISTEN:
                            case BtClient.STATE_CONNECTING:
                                // Situation normal. Start the connected thread.
                                BtClient.getInstance().connected(socket, socket.getRemoteDevice());
                                break;
                            case BtClient.STATE_NONE:
                            case BtClient.STATE_CONNECTED:
                                // Either not ready or already connected. Terminate
                                // new socket.
                                try {
                                    socket.close();
                                } catch (IOException e) {
                                    Log.e(TAG, "Could not close unwanted socket", e);
                                }
                                break;
                        }
                    }
                }
            }
            Log.i(TAG, "END mAcceptThread");
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void cancel() {
        Log.d(TAG, "Socket cancel " + this);
        try {
            mServerSocket.close();
        } catch (Exception e) {
            Log.e(TAG, "Socket close() of server failed", e);
        }
    }
}