package com.mirkowu.mvm.ble

import android.bluetooth.BluetoothGattCharacteristic
import com.mirkowu.mvm.ble.BleRssiDevice

import android.content.Context
import android.content.Intent
import android.text.TextUtils
import com.mirkowu.lib_base.mediator.EmptyMediator
import com.mirkowu.lib_base.util.bindingView
import com.mirkowu.lib_ble.ble.BleManager
import com.mirkowu.lib_ble.ble.callback.BleReadCallback
import com.mirkowu.lib_ble.ble.callback.BleWriteEntityCallback
import com.mirkowu.lib_ble.ble.model.EntityData
import com.mirkowu.lib_util.LogUtil
import com.mirkowu.lib_util.ktxutil.click
import com.mirkowu.lib_util.utilcode.util.ToastUtils
import com.mirkowu.mvm.R
import com.mirkowu.mvm.base.BaseActivity
import com.mirkowu.mvm.databinding.ActivitySendRecBinding

class SendRecActivity : BaseActivity<EmptyMediator>() {

    companion object {
        const val KEY_DEVICE = "KEY_DEVICE"

        @JvmStatic
        fun start(context: Context, device: BleRssiDevice) {
            val starter = Intent(context, SendRecActivity::class.java)
                .putExtra(KEY_DEVICE, device)
            context.startActivity(starter)
        }
    }

    private var mBleManager: BleManager<BleRssiDevice> = BleManager.getInstance()
    val mBinding by bindingView(ActivitySendRecBinding::inflate)
    val TAG: String = "SendAct"

    private lateinit var mDevice: BleRssiDevice
    override fun getLayoutId() = R.layout.activity_send_rec

    override fun initialize() {
        mDevice = intent.getParcelableExtra<BleRssiDevice>(KEY_DEVICE)!!
        if (mDevice == null) {
            finish()
            return
        }
        mBinding.btnSend.click { sendText() }

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
        val data = EntityData(mDevice.address, bytes, bytes.size)
        LogUtil.e(TAG, "发送地址：" + mDevice.address)
        mBleManager.writeEntity(data, object : BleWriteEntityCallback<BleRssiDevice>() {
            override fun onWriteSuccess() {
                ToastUtils.showShort("发送成功")

            }

            override fun onWriteFailed() {
                ToastUtils.showShort("发送失败")
            }
        })
        mBleManager.read(mDevice, object : BleReadCallback<BleRssiDevice>() {
            override fun onReadSuccess(
                dedvice: BleRssiDevice?,
                characteristic: BluetoothGattCharacteristic?
            ) {
                ToastUtils.showShort("Read 成功 = " + characteristic?.value.toString())
            }

            override fun onReadFailed(device: BleRssiDevice?, failedCode: Int) {
                ToastUtils.showShort("Read  失败")
            }
        })
    }

}