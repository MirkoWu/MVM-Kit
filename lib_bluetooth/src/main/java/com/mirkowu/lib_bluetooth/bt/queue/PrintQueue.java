package com.mirkowu.lib_bluetooth.bt.queue;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.text.TextUtils;


import com.mirkowu.lib_bluetooth.bt.BtManager;
import com.mirkowu.lib_bluetooth.bt.core.BtClient;
import com.mirkowu.lib_bluetooth.bt.utils.BtCache;

import java.util.ArrayList;

/**
 * Created by liuguirong on 8/1/17.
 * <p/>
 * this is print queue.
 * you can simple add print bytes to queue. and this class will send those bytes to bluetooth device
 * 这是打印队列。
 *   你可以简单地添加打印字节来排队。 并且这个类将这些字节发送到蓝牙设备
 */
public class PrintQueue {

    /**
     * instance
     */
    private static volatile PrintQueue sInstance;
    /**
     * context
     */
//    private static Context mContext;
    /**
     * print queue
     */
    private ArrayList<byte[]> mQueue;
    /**
     * bluetooth adapter
     */
    private BluetoothAdapter mAdapter;
    /**
     * bluetooth service
     */
    private BtClient mBtClient;


    private PrintQueue() {
    }

    public static PrintQueue getInstance() {
        if (sInstance == null) {
            synchronized (PrintQueue.class) {
                if (sInstance == null) {
                    sInstance = new PrintQueue();
                }
            }
        }
        return sInstance;
    }

    /**
     * add print bytes to queue. and call print
     *
     * @param bytes bytes
     */
    public synchronized void add(byte[] bytes) {
        if (null == mQueue) {
            mQueue = new ArrayList<byte[]>();
        }
        if (null != bytes) {
            mQueue.add(bytes);
        }
        print();
    }

    /**
     * add print bytes to queue. and call print
     */
    public synchronized void add(ArrayList<byte[]> bytesList) {
        if (null == mQueue) {
            mQueue = new ArrayList<byte[]>();
        }
        if (null != bytesList) {
            mQueue.addAll(bytesList);
        }
        print();
    }

    /**
     * print queue
     */
    public synchronized void print() {
        try {
            if (null == mQueue || mQueue.size() <= 0) {
                return;
            }
            if (null == mAdapter) {
                mAdapter = BluetoothAdapter.getDefaultAdapter();
            }
            if (null == mBtClient) {
                mBtClient = BtClient.getInstance();
            }
            if (mBtClient.getState() != BtClient.STATE_CONNECTED) {
                String btAddress = BtCache.getLastConnectDevice();
                if (!TextUtils.isEmpty(btAddress)) {
                    BluetoothDevice device = mAdapter.getRemoteDevice(btAddress);
                    mBtClient.connect(device);
                    return;
                }
            }
            while (mQueue.size() > 0) {
                mBtClient.write(mQueue.get(0));
                mQueue.remove(0);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * disconnect remote device
     */
    public void disconnect() {
        try {
            if (null != mBtClient) {
                mBtClient.stop();
                mBtClient = null;
            }
            if (null != mAdapter) {
                mAdapter = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * when bluetooth status is changed, if the printer is in use,
     * connect it,else do nothing
     */
    public void tryConnect() {
        try {
            String btAddress = BtCache.getLastConnectDevice();
            if (TextUtils.isEmpty(btAddress)) {
                return;
            }
            mAdapter = BtManager.getInstance().getBluetoothAdapter();
            if (null == mAdapter) {
                return;
            }
            if (null == mBtClient) {
                mBtClient = BtClient.getInstance();
            }
            if (mBtClient.getState() != BtClient.STATE_CONNECTED) {
                if (!TextUtils.isEmpty(btAddress)) {
                    BluetoothDevice device = mAdapter.getRemoteDevice(btAddress);
                    mBtClient.connect(device);
                    return;
                }
            } else {


            }
        } catch (Exception e) {
            e.printStackTrace();
        } catch (Error e) {
            e.printStackTrace();
        }
    }

    /**
     * 将打印命令发送给打印机
     *
     * @param bytes bytes
     */
    public void write(byte[] bytes) {
        try {
            String btAddress = BtCache.getLastConnectDevice();
            if (TextUtils.isEmpty(btAddress)) {
                return;
            }
            if (null == bytes || bytes.length <= 0) {
                return;
            }
            mAdapter = BtManager.getInstance().getBluetoothAdapter();
            mBtClient = BtClient.getInstance();
            if (mBtClient.getState() != BtClient.STATE_CONNECTED) {
                if (!TextUtils.isEmpty(btAddress)) {
                    BluetoothDevice device = mAdapter.getRemoteDevice(btAddress);
                    mBtClient.connect(device);
                    return;
                }
            }
            mBtClient.write(bytes);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
