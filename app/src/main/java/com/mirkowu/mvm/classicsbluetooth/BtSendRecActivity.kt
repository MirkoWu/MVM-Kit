package com.mirkowu.mvm.classicsbluetooth

import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothGattCharacteristic
import com.mirkowu.mvm.ble.BleRssiDevice

import android.content.Context
import android.content.Intent
import android.text.TextUtils
import com.mirkowu.lib_base.mediator.EmptyMediator
import com.mirkowu.lib_base.util.bindingView
import com.mirkowu.lib_ble.ble.callback.BleReadCallback
import com.mirkowu.lib_ble.ble.callback.BleWriteEntityCallback
import com.mirkowu.lib_ble.ble.model.EntityData
import com.mirkowu.lib_util.LogUtil
import com.mirkowu.lib_util.ktxutil.click
import com.mirkowu.lib_util.utilcode.util.ToastUtils
import com.mirkowu.mvm.R
import com.mirkowu.mvm.base.BaseActivity
import com.mirkowu.mvm.databinding.ActivitySendRecBinding

class BtSendRecActivity : BaseActivity<EmptyMediator>() {

    companion object {
        const val KEY_DEVICE = "KEY_DEVICE"

        @JvmStatic
        fun start(context: Context, device: BluetoothDevice) {
            val starter = Intent(context, BtSendRecActivity::class.java)
                .putExtra(KEY_DEVICE, device)
            context.startActivity(starter)
        }
    }

    val mBinding by bindingView(ActivitySendRecBinding::inflate)
    val TAG: String = "SendAct"

    private lateinit var mDevice: BluetoothDevice
    private val mBtManager = BtManager.getInstance()
    override fun getLayoutId() = R.layout.activity_send_rec

    override fun initialize() {
        mDevice = intent.getParcelableExtra<BluetoothDevice>(KEY_DEVICE)!!
        if (mDevice == null) {
            finish()
            return
        }
        mBinding.btnSend.click { sendText() }

        mBtManager.setOnDataReceiveCallback { bytes ->
            runOnUiThread {
                val textStr = mBinding.tvReceiveText.text.toString()
                mBinding.tvReceiveText.setText(textStr + "\n" + String(bytes))
            }
        }
    }

    override fun onResume() {
        super.onResume()
        // registerReceiver(bleReceiver, BLEClient.makeGattUpdateIntentFilter())
    }

    override fun onPause() {
        super.onPause()
        // unregisterReceiver(bleReceiver)
    }

    override fun onDestroy() {
        super.onDestroy()
//        bleManager.stopLeScan()
//        bleManager.close()
    }

    private fun sendText() {
        val text = mBinding.edSend.text.toString()
        if (TextUtils.isEmpty(text)) {
            return
        }
        val bytes = text.toByteArray()
        LogUtil.e(TAG, "发送地址：" + mDevice.address)
        mBtManager.write(bytes)
    }

}