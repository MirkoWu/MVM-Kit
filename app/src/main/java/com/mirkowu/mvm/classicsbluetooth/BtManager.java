/*
 * Copyright (C)  aicareles, Android-BLE Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.mirkowu.mvm.classicsbluetooth;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.text.TextUtils;

import androidx.annotation.NonNull;

import com.mirkowu.lib_util.LogUtil;
import com.mirkowu.mvm.classicsbluetooth.callback.OnConnectStateCallback;
import com.mirkowu.mvm.classicsbluetooth.callback.OnDataReceiveCallback;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


/**
 * provides all bluetooth operation apis
 *
 * @author aicareles
 * @since 2016/12/7
 */
public final class BtManager {

    private final static String TAG = "Ble";
    //    private static Options options;
    private Context context;
    //打开蓝牙标志位
    public static final int REQUEST_ENABLE_BT = 1;
    private BluetoothAdapter bluetoothAdapter;


//    private RequestListener<T> request;
//    private final Object locker = new Object();
//    private BleRequestImpl<T> bleRequestImpl;
//
//    private BluetoothChangedObserver bleObserver;

    public interface InitCallback {
        void success();

        void failed(int failedCode);
    }

    private static volatile BtManager sInstance;


    public static BtManager getInstance() {
        if (sInstance == null) {
            synchronized (BtManager.class) {
                if (sInstance == null) {
                    sInstance = new BtManager();
                }
            }
        }
        return sInstance;
    }

    private BtManager() {
        init();
    }

    /**
     * bluetooth initialization
     */
    public void init(/*Context context, InitCallback callback*/) {
//        if (context == null) {
//            throw new BtException("context is null");
//        }
//        if (this.context != null) {
//            BtLog.e(TAG, "Ble is Initialized!");
//            if (callback != null) {
//                callback.failed(BtStates.InitAlready);
//            }
//            return;
//        }
//        this.context = context;
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
//        if (bluetoothAdapter == null) {
//            if (callback != null) {
//                BtLog.e(TAG, "bluetoothAdapter is not available!");
//                callback.failed(BtStates.NotAvailable);
//            }
//            return;
//        }
//        if (!isSupportBle(context)) {
//            if (callback != null) {
//                BtLog.e(TAG, "not support ble!");
//                callback.failed(BtStates.NotSupportBLE);
//            }
//            return;
//        }
        //  BtManager.options = (options == null ? options() : options);
//        BtLog.init(BtManager.options);
//        request = (RequestListener<T>) RequestProxy.newProxy().bindProxy(context, RequestImpl.newRequestImpl());
//        bleRequestImpl = BleRequestImpl.getBleRequest();
//        bleRequestImpl.initialize(context);
//        initBleObserver();
//        BtLog.d(TAG, "Ble init success");
//        if (callback != null) {
//            callback.success();
//        }
    }

    @NonNull
    public List<BluetoothDevice> getBondedDevices() {
        List<BluetoothDevice> list = new ArrayList<>();
        list.addAll(getBluetoothAdapter().getBondedDevices());
        return list;
    }

    public boolean startDiscovery() {
        return getBluetoothAdapter().startDiscovery();
    }

    public boolean cancelDiscovery() {
        return getBluetoothAdapter().cancelDiscovery();
    }

    public boolean isDiscovering() {
        return getBluetoothAdapter().isDiscovering();
    }

    public BluetoothSocket connectRf(BluetoothDevice device, UUID uuid) throws IOException {
        return device.createRfcommSocketToServiceRecord(uuid);//明文传输(不安全)，无需配对
    }

    public BluetoothSocket connectInsecureRf(BluetoothDevice device, UUID uuid) throws IOException {
        return device.createInsecureRfcommSocketToServiceRecord(uuid);//加密传输，Android强制执行配对，弹窗显示配对码
    }

    public BluetoothServerSocket listenUsingRfcommWithServiceRecord(String name, UUID uuid) throws IOException {
        return getBluetoothAdapter().listenUsingRfcommWithServiceRecord(name, uuid);
    }

    public void connect(BluetoothDevice device, OnConnectStateCallback onConnectStateCallback) {
        BtClient.getInstance().connect(device, onConnectStateCallback);
    }

    public void connect(BluetoothSocket socket,
                        BluetoothDevice device) {
        BtClient.getInstance().connected(socket, device);
    }

    public BluetoothDevice getConnectDevice() {
        return BtClient.getInstance().getConnectDevice();
    }

    /**
     * 自动连接上次连接成功的设备
     *
     * @param onConnectStateCallback
     */
    public void autoConnect(OnConnectStateCallback onConnectStateCallback) {
        String address = getAutoConnectAddress();
        if (!TextUtils.isEmpty(address)) {
            LogUtil.e(TAG, "自动连接");
            BluetoothDevice device = getBluetoothAdapter().getRemoteDevice(address);
            connect(device, onConnectStateCallback);
        }
    }

    /**
     * 是否能自动连接
     *
     * @return
     */
    public boolean enableAutoConnect() {
        return !TextUtils.isEmpty(getAutoConnectAddress());
    }

    /**
     * 获取自动连接的地址
     *
     * @return
     */
    private String getAutoConnectAddress() {
        String address = BtCache.getLastConnectDevice();
        if (TextUtils.isEmpty(address)) {
            return "";
        }
        List<BluetoothDevice> list = getBondedDevices();
        if (list.isEmpty()) {
            return "";
        }
        for (BluetoothDevice device : list) {
            if (TextUtils.equals(address, device.getAddress())) {
                return address;
            }
        }
        return "";
    }

    public void start() {
        BtClient.getInstance().start();
    }

    public void stop() {
        BtClient.getInstance().stop();
    }

    public void write(byte[] bytes) {
        BtClient.getInstance().write(bytes);
    }

    public void write(byte[] bytes, long delayTime) {
        BtClient.getInstance().write(bytes, delayTime);
    }

    public void setOnDataReceiveCallback(OnDataReceiveCallback onDataReceiveCallback) {
        BtClient.getInstance().setOnDataReceiveCallback(onDataReceiveCallback);
    }
//    public static <T extends BleDevice> BleManager<T> create(Context context, InitCallback callback) {
//        return create(context, options(), callback);
//    }
//
//    public static <T extends BleDevice> BleManager<T> create(Context context, Options options, InitCallback callback) {
//        BleManager<T> bleManager = getInstance();
//        bleManager.init(context, options, callback);
//        return bleManager;
//    }
//
//    /**
//     * global bluetooth on off monitoring
//     */
//    public void setBleStatusCallback(BleStatusCallback callback) {
//        if (bleObserver != null) {
//            bleObserver.setBleScanCallbackInner(callback);
//        }
//    }
//
    //    /**
//     * set whether to automatically connect
//     */
//    public void autoConnect(T device, boolean autoConnect) {
//        DefaultReConnectHandler.provideReconnectHandler().resetAutoConnect(device, autoConnect);
//    }
//
//    public void cancelAutoConnects() {
//        DefaultReConnectHandler.provideReconnectHandler().cancelAutoConnect();
//    }


    private BluetoothAdapter getBluetoothAdapter() {
        if (bluetoothAdapter == null) {
            bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        }
        return bluetoothAdapter;
    }

    /**
     * @return 是否支持蓝牙
     */
    public boolean isSupportBle(Context context) {
        return (getBluetoothAdapter() != null && context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE));
    }

    /**
     * @return 蓝牙是否打开
     */
    public boolean isBleEnable() {
        BluetoothAdapter bluetoothAdapter = getBluetoothAdapter();
        return bluetoothAdapter != null && bluetoothAdapter.isEnabled();
    }

    /**
     * 打开蓝牙(默认模式--带系统弹出框)
     *
     * @param activity 上下文对象
     */
    public void turnOnBlueTooth(Activity activity) {
        // Ensures Bluetooth is enabled on the device.  If Bluetooth is not currently enabled,
        // fire an intent to display a dialog asking the user to grant permission to enable it.
        if (!isBleEnable()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            activity.startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }
    }

    /**
     * 强制打开蓝牙（不弹出系统弹出框）
     */
    public void turnOnBlueToothNo() {
        if (!isBleEnable()) {
            if (bluetoothAdapter != null) {
                bluetoothAdapter.enable();
            }
        }
    }

    /**
     * 关闭蓝牙
     */
    public boolean turnOffBlueTooth() {
        if (isBleEnable()) {
            return bluetoothAdapter.disable();
        }
        return true;
    }


    /**
     * register bluetooth receiver
     *
     * @param receiver bluetooth broadcast receiver
     * @param activity activity
     */
    public static void registerBluetoothReceiver(BroadcastReceiver receiver, Activity activity) {
        if (null == receiver || null == activity) {
            return;
        }
        IntentFilter intentFilter = new IntentFilter();
        //start discovery
        intentFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
        //finish discovery
        intentFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        //bluetooth status change
        intentFilter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
        //found device
        intentFilter.addAction(BluetoothDevice.ACTION_FOUND);
        //bond status change
        intentFilter.addAction(BluetoothDevice.ACTION_BOND_STATE_CHANGED);
        //pairing device
        intentFilter.addAction("android.bluetooth.device.action.PAIRING_REQUEST");
        activity.registerReceiver(receiver, intentFilter);
    }

    /**
     * unregister bluetooth receiver
     *
     * @param receiver bluetooth broadcast receiver
     * @param activity activity
     */
    public static void unregisterBluetoothReceiver(BroadcastReceiver receiver, Activity activity) {
        if (null == receiver || null == activity) {
            return;
        }
        activity.unregisterReceiver(receiver);
    }

    public void release(){
        BtClient.getInstance().release();
    }

}
