package com.mirkowu.mvm.classicsbluetooth;

import android.bluetooth.BluetoothSocket;
import android.util.Log;

import com.mirkowu.lib_util.LogUtil;
import com.mirkowu.mvm.classicsbluetooth.callback.OnDataReceiveCallback;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class ReceiveThread extends Thread {
    public static final String TAG = "ReceiveThread";
    private final BluetoothSocket mSocket;
    private final InputStream mInStream;
    private final OutputStream mOutStream;
    private OnDataReceiveCallback mOnDataReceiveCallback;

    public ReceiveThread(BluetoothSocket socket,OnDataReceiveCallback onDataReceiveCallback) {
        mSocket = socket;
        mOnDataReceiveCallback = onDataReceiveCallback;
        InputStream tmpIn = null;
        OutputStream tmpOut = null;

        // Get the BluetoothSocket input and output streams
        try {
            tmpIn = socket.getInputStream();
            tmpOut = socket.getOutputStream();
        } catch (Exception e) {
            Log.e(TAG, "temp sockets not created", e);
        }

        mInStream = tmpIn;
        mOutStream = tmpOut;
    }

    public void run() {
        Log.i(TAG, "BEGIN mConnectedThread");
        byte[] buffer = new byte[1024];
        int bytes;

        // Keep listening to the InputStream while connected
        while (true) {
            try {
                // Read from the InputStream
                bytes = mInStream.read(buffer);
                // send data
                byte[] data = getBytes(buffer, 0, bytes);
                String dataStr = new String(data);
                LogUtil.e(TAG, "收到数据 = " + dataStr);
                if (mOnDataReceiveCallback != null) {
                    mOnDataReceiveCallback.onDataReceive(data);
                }
            } catch (IOException e) {
                Log.e(TAG, "disconnected", e);
                // Start the service over to restart listening mode
                BtClient.getInstance().connectionLost();
//                BtClient.getInstance().start();
                break;
            } catch (Exception e) {
                BtClient.getInstance().connectionLost();
//                BtClient.getInstance().start();
                break;
            }
        }
    }

    /**
     * Write to the connected OutStream.
     *
     * @param buffer The bytes to write
     */
    public void write(byte[] buffer) {
        try {
            if (mOutStream != null) {
                mOutStream.write(buffer);
            }
        } catch (Exception e) {
            Log.e(TAG, "Exception during write", e);
        }
    }

    /**
     * Write to the connected OutStream after sleep
     *
     * @param buffer The bytes to write
     */
    public void write(byte[] buffer, long sleepTime) {
        try {
            Thread.sleep(sleepTime);
            if (mOutStream != null) {
                mOutStream.write(buffer);
            }
        } catch (Exception e) {
            Log.e(TAG, "Exception during write", e);
        }
    }


    public void cancel() {
        try {
            if (mSocket != null) {
                mSocket.close();
            }
        } catch (Exception e) {
            Log.e(TAG, "close() of connect socket failed", e);
        }
    }


    public static byte[] getBytes(byte[] bytes, int offset, int len) {
        byte[] result = new byte[len];
        System.arraycopy(bytes, offset, result, 0, len);
        return result;
    }
}
