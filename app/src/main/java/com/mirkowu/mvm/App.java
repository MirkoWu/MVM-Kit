package com.mirkowu.mvm;

import android.app.Application;

import com.mirkowu.lib_ble.ble.BleLog;
import com.mirkowu.lib_ble.ble.BleManager;
import com.mirkowu.lib_ble.ble.model.BleFactory;
import com.mirkowu.lib_ble.ble.utils.UuidUtils;
import com.mirkowu.lib_screen.AutoSizeManager;
import com.mirkowu.lib_util.LogUtil;
import com.mirkowu.mvm.ble.BleRssiDevice;
import com.mirkowu.mvm.ble.MyBleWrapperCallback;

import java.util.UUID;

public class App extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        LogUtil.init(BuildConfig.DEBUG);
        AutoSizeManager.init(this);

        initBle();
    }

    /**
     * 6E:88:B7:1D:94:35
     * 00001800-0000-1000-8000-00805f9b34fb
     * 00002a00-0000-1000-8000-00805f9b34fb
     * 00002a01-0000-1000-8000-00805f9b34fb
     * 00001801-0000-1000-8000-00805f9b34fb
     * 0000180a-0000-1000-8000-00805f9b34fb
     * d0611e78-bbb4-4591-a5f8-487910ae4366
     * 9fa480e0-4967-4542-9390-d343dc5d04ae
     */
    //初始化蓝牙
    private void initBle() {
        BleManager.options()
                .setLogBleEnable(true)//设置是否输出打印蓝牙日志
                .setLogTAG("AndroidBLE")//设置全局蓝牙操作日志TAG
                .setThrowBleException(true)//设置是否抛出蓝牙异常
                .setAutoConnect(false)//设置是否自动连接
                .setIgnoreRepeat(false)//设置是否过滤扫描到的设备(已扫描到的不会再次扫描)
                .setConnectFailedRetryCount(3)//连接异常时（如蓝牙协议栈错误）,重新连接次数
                .setConnectTimeout(10 * 1000)//设置连接超时时长
                .setScanPeriod(12 * 1000)//设置扫描时长
                .setMaxConnectNum(7)//最大连接数量
                .setUuidService(UUID.fromString("00001800-0000-1000-8000-00805f9b34fb"))//设置主服务的uuid
                .setUuidWriteCha(UUID.fromString("00002a00-0000-1000-8000-00805f9b34fb"))//设置可写特征的uuid
                .setUuidReadCha(UUID.fromString("00002a01-0000-1000-8000-00805f9b34fb"))//设置可读特征的uuid （选填）
                .setUuidNotifyCha(UUID.fromString("00001801-0000-1000-8000-00805f9b34fb"))//设置可通知特征的uuid （选填，库中默认已匹配可通知特征的uuid）
                .setFactory(new BleFactory<BleRssiDevice>() {//实现自定义BleDevice时必须设置
                    @Override
                    public BleRssiDevice create(String address, String name) {
                        return new BleRssiDevice(address, name);//自定义BleDevice的子类
                    }
                })
                .setBleWrapperCallback(new MyBleWrapperCallback())
                .create(this, new BleManager.InitCallback() {
                    @Override
                    public void success() {
                        BleLog.e("MainApplication", "初始化成功");
                    }

                    @Override
                    public void failed(int failedCode) {
                        BleLog.e("MainApplication", "初始化失败：" + failedCode);
                    }
                });
    }
}
