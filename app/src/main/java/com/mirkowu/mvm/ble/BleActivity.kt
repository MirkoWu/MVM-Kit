package com.mirkowu.mvm.ble

import android.bluetooth.BluetoothGatt
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.provider.Settings
import android.text.TextUtils
import android.util.Log
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import com.mirkowu.lib_base.mediator.EmptyMediator
import com.mirkowu.lib_base.util.bindingView
import com.mirkowu.lib_ble.ble.BleManager
import com.mirkowu.lib_ble.ble.callback.BleConnectCallback
import com.mirkowu.lib_ble.ble.callback.BleScanCallback
import com.mirkowu.lib_ble.ble.model.ScanRecord
import com.mirkowu.lib_ble.ble.utils.Utils
import com.mirkowu.lib_util.LogUtil
import com.mirkowu.lib_util.ktxutil.click
import com.mirkowu.lib_util.utilcode.util.ToastUtils
import com.mirkowu.lib_widget.decoration.GridDecoration
import com.mirkowu.lib_widget.decoration.LinearDecoration
import com.mirkowu.lib_widget.dialog.PromptDialog
import com.mirkowu.mvm.R
import com.mirkowu.mvm.base.BaseActivity
import com.mirkowu.mvm.databinding.ActivityBleBinding

class BleActivity : BaseActivity<EmptyMediator>() {

    val REQUEST_GPS = 4

    companion object {
        @JvmStatic
        fun start(context: Context) {
            val starter = Intent(context, BleActivity::class.java)
//            .putExtra()
            context.startActivity(starter)
        }
    }

    private val mBleManager: BleManager<BleRssiDevice> = BleManager.getInstance()
    private lateinit var mAdapter: DeviceRssiAdapter
    private val TAG = javaClass.simpleName

    private val mBinding by bindingView(ActivityBleBinding::inflate)
    override fun getLayoutId() = R.layout.activity_ble


    override fun initialize() {
        mAdapter = DeviceRssiAdapter()
        mBinding.rvSearch.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = mAdapter
            addItemDecoration(LinearDecoration(context).setSpace(1f).setSpaceColor(Color.GRAY))
        }
        mAdapter.setOnItemClickListener { view, item, position ->
            val device = mAdapter.getItem(position)
            connectDevice(device)
        }

        mBinding.btnScan.click {
            rescan()
        }

        mBinding.btnCancelScan.click {
            if (mBleManager.isScanning) {
                mBleManager.stopScan()
            }
        }
    }

    private fun connectDevice(device: BleRssiDevice) {
        PromptDialog()
            .setContent("确定要连接此设备吗?")
            .setOnButtonClickListener { dialog, isPositiveClick ->
                if (isPositiveClick) {
                    mBleManager.connect(device.address, connectCallback)
                }
            }.show(supportFragmentManager)
    }


    //检查蓝牙是否支持及打开
    private fun checkBlueStatus() {
        if (!mBleManager.isSupportBle(this)) {
            ToastUtils.showShort("当前设备不支持Ble")
            finish()
            return
        }
        if (!mBleManager.isBleEnable()) {
            mBleManager.turnOnBlueToothNo()
        } else {
            checkGpsStatus()
        }
    }

    private fun checkGpsStatus() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
            && !Utils.isGpsOpen(this@BleActivity)
        ) {
            AlertDialog.Builder(this@BleActivity)
                .setTitle("提示")
                .setMessage("为了更精确的扫描到Bluetooth LE设备,请打开GPS定位")
                .setPositiveButton("确定") { dialog, which ->
                    val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                    startActivityForResult(intent, REQUEST_GPS)
                }
                .setNegativeButton("取消", null)
                .create()
                .show()
        } else {
            mBleManager.startScan(scanCallback)
        }
    }

    private fun rescan() {
        if (!mBleManager.isScanning()) {
            mAdapter.clearAll()
            checkBlueStatus()
        }
    }

    private val connectCallback = object : BleConnectCallback<BleRssiDevice>() {
        override fun onConnectSuccess(device: BleRssiDevice?) {
            ToastUtils.showShort("连接成功")
            SendRecActivity.start(this@BleActivity, device!!)
        }

        override fun onConnectionChanged(device: BleRssiDevice, state: Int) {
            LogUtil.e("onConnectionChanged $state")
            var text = ""
            when (state) {
                0 -> text = "未连接"
                1 -> text = "连接中"
                2 -> text = "连接成功"
            }
            mBinding.tvConnectStat.text = text
        }

        override fun onServicesDiscovered(device: BleRssiDevice?, gatt: BluetoothGatt?) {
            super.onServicesDiscovered(device, gatt)
        }

        override fun onConnectCancel(device: BleRssiDevice?) {
            super.onConnectCancel(device)
            ToastUtils.showShort("连接取消")
            mBinding.tvConnectStat.text = "连接取消"
        }

        override fun onConnectFailed(device: BleRssiDevice?, errorCode: Int) {
            ToastUtils.showShort("连接失败")
            mBinding.tvConnectStat.text = "连接失败"
        }

    }
    private val scanCallback = object : BleScanCallback<BleRssiDevice>() {
        override fun onLeScan(device: BleRssiDevice, rssi: Int, scanRecord: ByteArray) {
            synchronized(mBleManager.getLocker()) {
                for (i in mAdapter.data.indices) {
                    val rssiDevice: BleRssiDevice = mAdapter.data.get(i)
                    if (TextUtils.equals(rssiDevice.address, device.address)) {
                        if (rssiDevice.getRssi() !== rssi && System.currentTimeMillis() - rssiDevice.getRssiUpdateTime() > 1000L) {
                            rssiDevice.setRssiUpdateTime(System.currentTimeMillis())
                            rssiDevice.setRssi(rssi)
                            mAdapter.updateData(i, rssiDevice)
                        }
                        return@synchronized
                    }
                }
                device.scanRecord = ScanRecord.parseFromBytes(scanRecord)
                device.setRssi(rssi)
                mAdapter.addData(device)
            }
        }

        override fun onStart() {
            super.onStart()
            //startBannerLoadingAnim()
        }

        override fun onStop() {
            super.onStop()
            //stopBannerLoadingAnim()
        }

        override fun onScanFailed(errorCode: Int) {
            super.onScanFailed(errorCode)
            Log.e(TAG, "onScanFailed: $errorCode")
        }
    }

//    override fun onResume() {
//        super.onResume()
//        registerReceiver(bleReceiver, BLEClient.makeGattUpdateIntentFilter())
//    }
//
//    override fun onPause() {
//        super.onPause()
//        unregisterReceiver(bleReceiver)
//    }
//
//    override fun onDestroy() {
//        super.onDestroy()
//    }

//    private fun startLeScan() {
//        if (bleManager.isConnected) {
//            bleManager.disconnect()
//        }
//        if (bleManager.isSupportBLE(context)) {
//            PermissionsUtils.getInstance()
//                .requestPermissions(this, arrayOf(
//                    Manifest.permission.ACCESS_COARSE_LOCATION,
//                    Manifest.permission.BLUETOOTH
//                ),
//                    object : PermissionsUtils.OnPermissionsListener {
//                        override fun onPermissionGranted(requestCode: Int) {
//                            if (bleManager.isEnable()) {
//                                val scanning = bleManager.startLeScan { device, rssi, scanRecord ->
//                                    //type=1传统蓝牙  2BLE 3 双模蓝牙 0未知
//                                    if (!mAdapter.getData()
//                                            .contains(device) /*&& device.type >= 2*/) {
//                                        LogUtil.e(TAG, "发现设备：" + device.name)
//                                        mAdapter.addData(device)
//                                    } else {
//                                        val index = mAdapter.data.indexOf(device)
//                                        mAdapter.updateData(index, device)
//                                    }
//                                }
//                                LogUtil.e("蓝牙scanning" + scanning)
//
//                            } else {
//                                val isEnable = bleManager.enable()
//                                LogUtil.e("蓝牙是否可用" + isEnable)
//                            }
//                        }
//
//                        override fun onPermissionShowRationale(
//                            requestCode: Int,
//                            permissions: Array<out String>
//                        ) {
//                            ToastUtils.showShort("检测到GPS/位置服务功能未开启，请开启 111111")
//                            startLeScan()
//                        }
//
//                        override fun onPermissionDenied(requestCode: Int) {
//                            ToastUtils.showShort("检测到GPS/位置服务功能未开启，请开启")
//                        }
//                    })
//        } else {
//            ToastUtils.showShort("当前设备不支持BLE")
//        }
//    }

}