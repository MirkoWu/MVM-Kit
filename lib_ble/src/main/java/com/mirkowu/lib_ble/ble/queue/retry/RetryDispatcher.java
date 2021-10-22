package com.mirkowu.lib_ble.ble.queue.retry;

import com.mirkowu.lib_ble.ble.BleManager;
import com.mirkowu.lib_ble.ble.BleLog;
import com.mirkowu.lib_ble.ble.BleStates;
import com.mirkowu.lib_ble.ble.callback.BleConnectCallback;
import com.mirkowu.lib_ble.ble.model.BleDevice;
import com.mirkowu.lib_ble.ble.request.ConnectRequest;
import com.mirkowu.lib_ble.ble.request.Rproxy;

import java.util.HashMap;
import java.util.Map;


/**
 * author: jerry
 * date: 21-1-8
 * email: superliu0911@gmail.com
 * des:
 */
public class RetryDispatcher<T extends BleDevice> extends BleConnectCallback<T> implements RetryCallback<T> {
    private static final String TAG = "RetryDispatcher";
    private static RetryDispatcher retryDispatcher;
    private final Map<String, Integer> deviceRetryMap = new HashMap<>();

    public static <T extends BleDevice>RetryDispatcher<T> getInstance() {
        if (retryDispatcher == null){
            retryDispatcher = new RetryDispatcher();
        }
        return retryDispatcher;
    }

    @Override
    public void retry(T device) {
        BleLog.i(TAG, "正在尝试重试连接第"+deviceRetryMap.get(device.getAddress())+"次重连: "+device.getName());
        if (!device.isAutoConnect()){
            ConnectRequest<T> connectRequest = Rproxy.getRequest(ConnectRequest.class);
            connectRequest.connect(device);
        }
    }

    @Override
    public void onConnectionChanged(BleDevice device, int state) {
        BleLog.i(TAG, "onConnectionChanged:"+device.getName()+"---连接状态:"+device.isConnected());
        if (device.isConnected()){
            String key = device.getAddress();
            deviceRetryMap.remove(key);
        }
    }

    @Override
    public void onConnectFailed(T device, int errorCode) {
        super.onConnectFailed(device, errorCode);
        if (errorCode == BleStates.ConnectError || errorCode == BleStates.ConnectFailed){
            String key = device.getAddress();
            int lastRetryCount = BleManager.options().connectFailedRetryCount;
            if (lastRetryCount <= 0)return;
            if (deviceRetryMap.containsKey(key)){
                lastRetryCount = deviceRetryMap.get(key);
            }
            if (lastRetryCount <= 0){
                deviceRetryMap.remove(key);
                return;
            }
            deviceRetryMap.put(key, lastRetryCount-1);
            retry(device);
        }
    }
}
