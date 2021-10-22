package com.mirkowu.mvm.ble

import android.bluetooth.BluetoothAdapter.LeScanCallback
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.mirkowu.lib_base.mediator.EmptyMediator
import com.mirkowu.lib_ble.service.BLEClient
import com.mirkowu.lib_ble.service.BLEService
import com.mirkowu.lib_ble.service.OnServiceConnectListener
import com.mirkowu.lib_util.LogUtil
import com.mirkowu.lib_util.PermissionsUtil
import com.mirkowu.lib_util.utilcode.util.ConvertUtils
import com.mirkowu.lib_util.utilcode.util.ToastUtils
import com.mirkowu.mvm.R
import com.mirkowu.mvm.base.BaseActivity

class SearchBleActivity : BaseActivity<EmptyMediator>() {

    companion object {
        @JvmStatic
        fun start(context: Context) {
            val starter = Intent(context, SearchBleActivity::class.java)
//            .putExtra()
            context.startActivity(starter)
        }
    }

//    val binding by binding(ActivitySearchBleBinding::inflate)

    private lateinit var mAdapter: DeviceAdapter
    private lateinit var client: BLEClient
    private val TAG = javaClass.simpleName
    private val bleReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val action = intent.action
            if (BLEService.ACTION_GATT_CONNECTED.equals(action)) {  //gatt 仅仅连接蓝牙
                LogUtil.e(TAG, "Only gatt, just wait")
            } else if (BLEService.ACTION_GATT_DISCONNECTED.equals(action)) { //断开连接
                LogUtil.e(TAG, "action_gatt_disconnected")
            } else if (BLEService.ACTION_GATT_SERVICES_DISCOVERED.equals(action)) { //发现蓝牙服务，可以进行通信了
                LogUtil.e(TAG, "In what we need 发现蓝牙服务，可以进行通信了")
            } else if (BLEService.ACTION_DATA_AVAILABLE.equals(action)) { //收到数据
                LogUtil.e(TAG, "RECV DATA")
                val data = intent.getByteArrayExtra(BLEService.EXTRA_DATA)
                if (data != null) {
                    val rec: String = ConvertUtils.bytes2HexString(data)
                    LogUtil.d("接收数据$rec")
                }
            }
        }
    }

    override fun getLayoutId() = R.layout.activity_search_ble


    override fun initialize() {
        val rvSearch: RecyclerView = findViewById(R.id.rvSearch)
        rvSearch.layoutManager = LinearLayoutManager(this)
        mAdapter = DeviceAdapter();
        rvSearch.setAdapter(mAdapter);
        mAdapter.setOnItemClickListener { view, item, position ->
            val device = mAdapter.getItem(position);
            client.connect(device.getAddress(), true);
            client.startMonitor(getContext(), bleReceiver, BLEClient.makeGattUpdateIntentFilter());
        };
        client = BLEClient.getInstance()
        startLeScan();

    }


    private fun startLeScan() {
        if (client.isSupportBLE(context)) {
            PermissionsUtil.getInstance()
                .requestPermissions(this, PermissionsUtil.GROUP_BLUETOOTH,
                    object : PermissionsUtil.OnPermissionsListener {
                        override fun onPermissionGranted(requestCode: Int) {
                            if (client.isEnable()) {
                                client.bindService(context, object :
                                    OnServiceConnectListener {
                                    override fun onServiceConnected() {
                                        /**
                                         * 请在BLEService中修改需要的服务值
                                         * UUID_SERVICE
                                         * UUID_NOTIFY
                                         */
                                        client.startLeScan(LeScanCallback { device, rssi, scanRecord -> //type=1传统蓝牙  2BLE 3 双模蓝牙 0未知
                                            if (/*!mAdapter.getData()
                                                    .contains(device) &&*/ device.type >= 2
                                            ) {
                                                LogUtil.e(TAG, "发现设备：" + device.name)
                                                mAdapter.addData(device )
                                                mAdapter.notifyDataSetChanged()
                                            }
                                        })
                                    }

                                    override fun onServiceDisconnected() {}
                                })
                            } else {
                                val isEnable = client.enable()
                                LogUtil.e("蓝牙是否可用" + isEnable)
                            }
                        }

                        override fun onPermissionShowRationale(
                            requestCode: Int,
                            permissions: Array<out String>
                        ) {
                            ToastUtils.showShort("检测到GPS/位置服务功能未开启，请开启 111111")
                            startLeScan()
                        }

                        override fun onPermissionDenied(requestCode: Int) {
                            ToastUtils.showShort("检测到GPS/位置服务功能未开启，请开启")
                        }
                    })
        } else {
            ToastUtils.showShort("当前设备不支持BLE")
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        client.unbindService(this)
    }
}