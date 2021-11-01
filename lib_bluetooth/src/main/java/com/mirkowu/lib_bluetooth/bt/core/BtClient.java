package com.mirkowu.lib_bluetooth.bt.core;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import androidx.annotation.NonNull;


import com.mirkowu.lib_bluetooth.bt.callback.OnConnectStateCallback;
import com.mirkowu.lib_bluetooth.bt.callback.OnDataReceiveCallback;
import com.mirkowu.lib_bluetooth.bt.callback.OnDataWriteCallback;
import com.mirkowu.lib_bluetooth.bt.utils.BtCache;
import com.mirkowu.lib_bluetooth.bt.utils.BtLog;

import java.util.UUID;


/**
 * Created by liuguirong on 8/1/17.
 * This class does all the work for setting up and managing Bluetooth
 * connections with other devices. It has a thread that listens for incoming
 * connections, a thread for connecting with a device, and a thread for
 * performing data transmissions when connected.
 * 这个类完成所有的设置和管理蓝牙的工作
 *   *与其他设备的连接。 它有一个线程来侦听来电
 *   *连接，用于与设备连接的线程和线程
 *   *连接时执行数据传输。
 */
public class BtClient {

    // Constants that indicate the current connection state
    public static final int STATE_NONE = 0; // we're doing nothing
    public static final int STATE_LISTEN = 1; // now listening for incoming connections
    public static final int STATE_CONNECTING = 2; // now initiating an outgoing connection

    // INSECURE "8ce255c0-200a-11e0-ac64-0800200c9a66"
    // SECURE "fa87c0d0-afac-11de-8a39-0800200c9a66"
    // SPP "0001101-0000-1000-8000-00805F9B34FB"
    public static final int STATE_CONNECTED = 3; // now connected to a remote device
    public static final int STATE_DISCONNECT = 4; // 断开连接

    // Debugging
    private static final String TAG = "BtClient";
    // Name for the SDP record when creating server socket
    private static final String NAME = "BtClient";
    // Unique UUID for this application
    public static final UUID MY_UUID = UUID.fromString("0001101-0000-1000-8000-00805F9B34FB");
    // Member fields
    private final BluetoothAdapter mAdapter;
    private AcceptThread mAcceptThread;
    private ConnectThread mConnectThread;
    private ReceiveThread mReceiveThread;
    private int mState;
    // context
//    private Context mContext;
    private static volatile BtClient sInstance;
    BluetoothDevice mDevice;
    private Handler mHandler;


    private OnDataWriteCallback mOnDataWriteCallback;
    private OnDataReceiveCallback mOnDataReceiveCallback;
    private OnConnectStateCallback mOnConnectStateCallback;

    public static BtClient getInstance() {
        if (sInstance == null) {
            synchronized (BtClient.class) {
                if (sInstance == null) {
                    sInstance = new BtClient();
                }
            }
        }
        return sInstance;
    }

    /**
     * Constructor. Prepares a new BluetoothChat session.
     */
    public BtClient() {
        mAdapter = BluetoothAdapter.getDefaultAdapter();
        mState = STATE_NONE;
        mHandler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(@NonNull Message msg) {

            }
        };
    }

    /**
     * Return the current connection state.
     */
    public synchronized int getState() {
        return mState;
    }

    /**
     * Set the current state of the chat connection
     *
     * @param state An integer defining the current connection state
     */
    synchronized void setState(int state) {
        Log.e(TAG, "setState() " + mState + " -> " + state);
        mState = state;

        if (mOnConnectStateCallback != null) {
            mOnConnectStateCallback.onConnectStateChanged(state);
        }
        if (mAcceptThread != null) {
            mAcceptThread.setState(mState);
        }
        // if need to show bt status, use the code below
        // Give the new state to the Handler so the UI Activity can update
        switch (mState) {
            case STATE_LISTEN:
                BtLog.e(TAG, "等待连接");
//                EventBus.getDefault().post(new PrintMsgEvent(PrinterMsgType.MESSAGE_STATE_CHANGE, "等待连接"));
                if (mOnConnectStateCallback != null) {
                    mOnConnectStateCallback.onWaitConnect();
                }
                break;
            case STATE_CONNECTING:
                BtLog.e(TAG, "正在连接");
//                EventBus.getDefault().post(new PrintMsgEvent(PrinterMsgType.MESSAGE_STATE_CHANGE, "正在连接"));
                if (mOnConnectStateCallback != null) {
                    mOnConnectStateCallback.onConnecting();
                }
                break;
            case STATE_CONNECTED:
                BtLog.e(TAG, "已连接");
//                EventBus.getDefault().post(new PrintMsgEvent(PrinterMsgType.MESSAGE_STATE_CHANGE, "已连接"));
                if (mOnConnectStateCallback != null) {
                    mOnConnectStateCallback.onConnectSuccess(mDevice);
                }
                break;
            case STATE_DISCONNECT:
                BtLog.e(TAG, "断开连接");
//                EventBus.getDefault().post(new PrintMsgEvent(PrinterMsgType.MESSAGE_STATE_CHANGE, "断开连接"));
                if (mOnConnectStateCallback != null) {
                    mOnConnectStateCallback.onDisConnect(mDevice);
                }
                break;
            default:
                BtLog.e(TAG, "未连接");
//                EventBus.getDefault().post(new PrintMsgEvent(PrinterMsgType.MESSAGE_STATE_CHANGE, "未连接"));
                if (mOnConnectStateCallback != null) {
                    mOnConnectStateCallback.onConnectFailed();
                }
                break;
        }


    }


    public void setOnDataWriteCallback(OnDataWriteCallback onDataWriteCallback) {
        this.mOnDataWriteCallback = onDataWriteCallback;
    }

    public void setOnDataReceiveCallback(OnDataReceiveCallback onDataReceiveCallback) {
        this.mOnDataReceiveCallback = onDataReceiveCallback;
    }

    public OnDataReceiveCallback getOnDataReceiveCallback() {
        return mOnDataReceiveCallback;
    }
    //    public void setOnConnectStateCallback(OnConnectStateCallback onConnectStateCallback) {
//        this.mOnConnectStateCallback = onConnectStateCallback;
//    }

//    private OnDataReceiveCallback mWrapperOnDataReceiveCallback = new OnDataReceiveCallback() {
//        @Override
//        public void onDataReceive(byte[] data) {
//            if (mOnDataReceiveCallback != null) {
//                mOnDataReceiveCallback.onDataReceive(data);
//            }
//        }
//    };

    public BluetoothDevice getConnectDevice() {
        return mDevice;
    }

    public synchronized void connect(BluetoothDevice device, OnConnectStateCallback onConnectStateCallback) {
        this.mOnConnectStateCallback = onConnectStateCallback;
        connect(device);
    }

    /**
     * Start the ConnectThread to initiate a connection to a remote device.
     *
     * @param device The BluetoothDevice to connect
     */
    public synchronized void connect(BluetoothDevice device) {
        mDevice = device;
        Log.d(TAG, "connect to: " + device);
        BtLog.e(TAG, "正在连接蓝牙设备");
//        EventBus.getDefault().post(new PrintMsgEvent(PrinterMsgType.MESSAGE_TOAST, "正在连接蓝牙设备"));
        // Cancel any thread attempting to make a connection
        if (mState == STATE_CONNECTING) {
            if (mConnectThread != null) {
                mConnectThread.cancel();
                mConnectThread = null;
            }
        }

        // Cancel any thread currently running a connection
        if (mReceiveThread != null) {
            mReceiveThread.cancel();
            mReceiveThread = null;
        }

        // Start the thread to connect with the given device
        mConnectThread = new ConnectThread(device);
        mConnectThread.start();
        setState(STATE_CONNECTING);
    }

    /**
     * Start the ConnectedThread to begin managing a Bluetooth connection
     *
     * @param socket The BluetoothSocket on which the connection was made
     * @param device The BluetoothDevice that has been connected
     */
    synchronized void connected(BluetoothSocket socket,
                                BluetoothDevice device) {
        Log.d(TAG, "connected, Socket ");
        mDevice = device;
        mConnectThread = null; //先置空 防止关闭socket
        release();

        // Start the thread to manage the connection and perform transmissions
        mReceiveThread = new ReceiveThread(socket);
        mReceiveThread.start();

        // Send the name of the connected device back to the UI Activity
//        EventBus.getDefault().post(new PrintMsgEvent(PrinterMsgType.MESSAGE_TOAST, "蓝牙设备连接成功"));


        // call print queue to print
        //   PrintQueue.getQueue(mContext).print();

        BtLog.e(TAG, "蓝牙设备连接成功");
        setState(STATE_CONNECTED);

        //缓存 上次连接蓝牙地址
        BtCache.putConnectDevice(device.getAddress());
    }

    /**
     * Stop all threads
     */
    public synchronized void stop() {
        Log.d(TAG, "stop");

        release();
        setState(STATE_NONE);
    }

    public void release() {
        if (mConnectThread != null) {
            mConnectThread.cancel();
            mConnectThread = null;
        }

        if (mReceiveThread != null) {
            mReceiveThread.cancel();
            mReceiveThread = null;
        }

        if (mAcceptThread != null) {
            mAcceptThread.cancel();
            mAcceptThread = null;
        }
    }

    /**
     * Write to the ConnectedThread in an unsynchronized manner
     *
     * @param out The bytes to write
     */
    public void write(byte[] out) {
        // Create temporary object
        ReceiveThread r;
        // Synchronize a copy of the ConnectedThread
        synchronized (this) {
            if (mState != STATE_CONNECTED)
                return;
            r = mReceiveThread;
        }
        // Perform the write unsynchronized
        r.write(out);
    }

    /**
     * Write to the ConnectedThread in an unsynchronized manner after sleepTime
     *
     * @param out       The bytes to write
     * @param delayTime sleep time
     */
    public void write(byte[] out, long delayTime) {
        // Create temporary object
        ReceiveThread r;
        // Synchronize a copy of the ConnectedThread
        synchronized (this) {
            if (mState != STATE_CONNECTED)
                return;
            r = mReceiveThread;
        }
        // Perform the write unsynchronized
        r.write(out, delayTime);
    }

    /**
     * Indicate that the connection attempt failed and notify the UI Activity.
     */
    void connectionFailed() {
//         Send a failure message back to the Activity
        BtLog.e(TAG, "蓝牙连接失败");
//        EventBus.getDefault().post(new PrintMsgEvent(PrinterMsgType.MESSAGE_TOAST, "蓝牙连接失败,请重启打印机再试"));
        setState(STATE_NONE);
        // Start the service over to restart listening mode
        start();
    }

    /**
     * Indicate that the connection was lost and notify the UI Activity.
     */
    void connectionLost() {
//         Send a failure message back to the Activity
        // EventBus.getDefault().post(new PrintMsgEvent(PrinterMsgType.MESSAGE_TOAST, "蓝牙连接断开"));
        BtLog.e(TAG, "蓝牙连接断开");
        setState(STATE_DISCONNECT);
        // Start the service over to restart listening mode
        start();
    }

    /**
     * Start the chat service. Specifically start AcceptThread to begin a
     * session in listening (server) mode. Called by the Activity onResume()
     * 开始聊天服务。 具体启动AcceptThread开始一个
     *  会话在监听（服务器）模式。 由Activity onResume（）调用
     */
    public synchronized void start() {
        Log.e(TAG, "start");

        // Cancel any thread attempting to make a connection
        if (mConnectThread != null) {
            mConnectThread.cancel();
            mConnectThread = null;
        }

        // Cancel any thread currently running a connection
        if (mReceiveThread != null) {
            mReceiveThread.cancel();
            mReceiveThread = null;
        }

        setState(STATE_LISTEN);

        // Start the thread to listen on a BluetoothServerSocket
        if (mAcceptThread == null) {
            mAcceptThread = new AcceptThread(mState);
            mAcceptThread.start();
        }
    }


}
