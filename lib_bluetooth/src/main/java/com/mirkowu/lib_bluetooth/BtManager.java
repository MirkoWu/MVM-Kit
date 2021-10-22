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
package com.mirkowu.lib_bluetooth;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;


import com.mirkowu.lib_bluetooth.exception.BtException;


/**
 * provides all bluetooth operation apis
 *
 * @author aicareles
 * @since 2016/12/7
 */
public final class BtManager {

    private final static String TAG = "Ble";
    private static volatile BtManager sInstance;
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

    /**
     * Initializes a newly created {@code Ble} object so that it represents
     * a bluetooth management class .  Note that use of this constructor is
     * unnecessary since Can not be externally constructed.
     */
    private BtManager() {
    }

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

    /**
     * bluetooth initialization
     *
     * @param context context object
     */
    public void init(Context context, InitCallback callback) {
        if (context == null) {
            throw new BtException("context is null");
        }
        if (this.context != null) {
            BtLog.e(TAG, "Ble is Initialized!");
            if (callback != null) {
                callback.failed(BtStates.InitAlready);
            }
            return;
        }
        this.context = context;
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter == null) {
            if (callback != null) {
                BtLog.e(TAG, "bluetoothAdapter is not available!");
                callback.failed(BtStates.NotAvailable);
            }
            return;
        }
        if (!isSupportBle(context)) {
            if (callback != null) {
                BtLog.e(TAG, "not support ble!");
                callback.failed(BtStates.NotSupportBLE);
            }
            return;
        }
      //  BtManager.options = (options == null ? options() : options);
//        BtLog.init(BtManager.options);
//        request = (RequestListener<T>) RequestProxy.newProxy().bindProxy(context, RequestImpl.newRequestImpl());
//        bleRequestImpl = BleRequestImpl.getBleRequest();
//        bleRequestImpl.initialize(context);
//        initBleObserver();
        BtLog.d(TAG, "Ble init success");
        if (callback != null) {
            callback.success();
        }
    }

    public void startDiscovery(){
        bluetoothAdapter.startDiscovery();
    }

    public void cancelDiscovery(){
        bluetoothAdapter.cancelDiscovery();
    }

    public void isDiscovering(){
        bluetoothAdapter.isDiscovering();
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
//     * start scanning
//     */
//    public void startScan(BleScanCallback<T> callback) {
//        request.startScan(callback, options().scanPeriod);
//    }
//
//    public void startScan(BleScanCallback<T> callback, long scanPeriod) {
//        request.startScan(callback, scanPeriod);
//    }
//
//    /**
//     * stop scanning
//     */
//    public void stopScan() {
//        request.stopScan();
//    }
//
//    /**
//     * connect bluetooth
//     */
//    public void connect(T device, BleConnectCallback<T> callback) {
//        synchronized (locker) {
//            request.connect(device, callback);
//        }
//    }
//
//    /**
//     * connect to the device through the mac address
//     */
//    public void connect(String address, BleConnectCallback<T> callback) {
//        synchronized (locker) {
//            request.connect(address, callback);
//        }
//    }
//
//    public void connects(List<T> devices, BleConnectCallback<T> callback) {
//        ConnectRequest<T> request = Rproxy.getRequest(ConnectRequest.class);
//        request.connect(devices, callback);
//    }
//
//    public void cancelConnecting(T device) {
//        ConnectRequest<T> request = Rproxy.getRequest(ConnectRequest.class);
//        request.cancelConnecting(device);
//    }
//
//    public void cancelConnectings(List<T> devices) {
//        ConnectRequest<T> request = Rproxy.getRequest(ConnectRequest.class);
//        request.cancelConnectings(devices);
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
//
//    /**
//     * disconnect
//     *
//     * @param device
//     */
//    public void disconnect(T device) {
//        request.disconnect(device);
//    }
//
//    public void disconnect(T device, BleConnectCallback<T> callback) {
//        request.disconnect(device, callback);
//    }
//
//    public void disconnectAll() {
//        Collection<T> connectedDevices = getConnectedDevices();
//        if (!connectedDevices.isEmpty()) {
//            for (T device : connectedDevices) {
//                request.disconnect(device);
//            }
//        }
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

}
