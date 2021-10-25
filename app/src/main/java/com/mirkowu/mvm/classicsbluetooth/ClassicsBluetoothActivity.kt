package com.mirkowu.mvm.classicsbluetooth

import android.bluetooth.BluetoothDevice
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Color
import android.os.Build
import android.provider.Settings
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import com.mirkowu.lib_base.mediator.EmptyMediator
import com.mirkowu.lib_base.util.bindingView
import com.mirkowu.lib_ble.ble.utils.Utils
import com.mirkowu.lib_util.LogUtil
import com.mirkowu.lib_util.ktxutil.click
import com.mirkowu.lib_util.utilcode.util.ToastUtils
import com.mirkowu.lib_widget.decoration.LinearDecoration
import com.mirkowu.lib_widget.dialog.PromptDialog
import com.mirkowu.mvm.R
import com.mirkowu.mvm.base.BaseActivity
import com.mirkowu.mvm.ble.DeviceAdapter
import com.mirkowu.mvm.classicsbluetooth.callback.OnConnectStateCallback
import com.mirkowu.mvm.databinding.ActivityClassicsBluetoothBinding
import java.util.*

class ClassicsBluetoothActivity : BaseActivity<EmptyMediator>() {
    companion object {
        @JvmStatic
        fun start(context: Context) {
            val starter = Intent(context, ClassicsBluetoothActivity::class.java)
//            .putExtra()
            context.startActivity(starter)
        }
    }

    val REQUEST_GPS = 4
    private val mBinding by bindingView(ActivityClassicsBluetoothBinding::inflate)
    private val mBtManager: BtManager = BtManager.getInstance()
    public lateinit var mAdapter: DeviceAdapter

    // val mBtService = BtService.getInstance()
    override fun getLayoutId() = R.layout.activity_classics_bluetooth
    override fun initialize() {
        mAdapter = DeviceAdapter()
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
            if (mBtManager.isDiscovering) {
                mBtManager.cancelDiscovery()
            }
        }

        //如果能自动连接，就自动连接
        if (mBtManager.enableAutoConnect()) {
            mBtManager.autoConnect(mConnectStateListener)
        }


    }

    private val mConnectStateListener = object : OnConnectStateCallback {
        override fun onConnectSuccess() {
            runOnUiThread {
                BtSendRecActivity.start(context, mBtManager.connectDevice)
            }

        }

        override fun onConnectFailed() {
        }

        override fun onConnecting() {
        }

        override fun onWaitConnect() {
        }

        override fun onConnectStateChanged(state: Int) {
        }

    }

    private fun connectDevice(device: BluetoothDevice) {
        PromptDialog()
            .setContent("确定要连接此设备吗?")
            .setOnButtonClickListener { dialog, isPositiveClick ->
                if (isPositiveClick) {
                    mBtManager.connect(device, mConnectStateListener)
                }
            }.show(supportFragmentManager)
    }


    //检查蓝牙是否支持及打开
    private fun checkBlueStatus() {
        if (!mBtManager.isSupportBle(this)) {
            ToastUtils.showShort("当前设备不支持Ble")
            finish()
            return
        }
        if (!mBtManager.isBleEnable()) {
            mBtManager.turnOnBlueToothNo()
        } else {
            checkGpsStatus()
        }
    }

    private fun checkGpsStatus() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
            && !Utils.isGpsOpen(this@ClassicsBluetoothActivity)
        ) {
            AlertDialog.Builder(this@ClassicsBluetoothActivity)
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
            mBtManager.startDiscovery()
        }
    }

    private fun rescan() {
        if (!mBtManager.isDiscovering()) {
            mAdapter.clearAll()
            checkBlueStatus()
        }

        val list = mBtManager.bondedDevices
        for (item in list) {
            mAdapter.addData(0, item)
            LogUtil.e("已绑定设备= " + item.name + "  " + item.address)
        }
    }

    override fun onResume() {
        super.onResume()
        registerReceiver()
    }

    override fun onPause() {
        super.onPause()
        unregisterReceiver()
    }

    private var mReceiver: BluetoothSearchReceiver? = null
    private fun registerReceiver() {
        if (mReceiver == null) {
            mReceiver =
                object : BluetoothSearchReceiver() {
                    override fun onDeviceFound(device: BluetoothDevice) {
                        if (device.bondState == BluetoothDevice.BOND_BONDED) {
                            mAdapter.addData(0, device)
                        } else {
                            mAdapter.addData(device)
                        }
                    }
                }
            registerReceiver(
                mReceiver,
                IntentFilter(BluetoothDevice.ACTION_FOUND)
            )
        }
    }

    private fun unregisterReceiver() {
        if (mReceiver != null) {
            unregisterReceiver(mReceiver)
            mReceiver = null
        }
    }

    private open class BluetoothSearchReceiver : BroadcastReceiver() {

        override fun onReceive(context: Context, intent: Intent) {
            if (intent.action == BluetoothDevice.ACTION_FOUND) {
                val device = intent
                    .getParcelableExtra<BluetoothDevice>(BluetoothDevice.EXTRA_DEVICE)
                val rssi = intent.getShortExtra(BluetoothDevice.EXTRA_RSSI, Short.MIN_VALUE).toInt()
                onDeviceFound(device!!)
            }
        }

        open fun onDeviceFound(device: BluetoothDevice) {


        }
    }

    override fun onDestroy() {
        super.onDestroy()
        mBtManager.release()
    }
}

