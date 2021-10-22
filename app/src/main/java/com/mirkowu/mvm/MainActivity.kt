package com.mirkowu.mvm

import android.view.View
import com.mirkowu.lib_base.mediator.EmptyMediator
import com.mirkowu.mvm.base.BaseActivity
import com.mirkowu.mvm.ble.BleActivity
import com.mirkowu.mvm.ble.SearchBleActivity
import com.mirkowu.mvm.classicsbluetooth.ClassicsBluetoothActivity

class MainActivity : BaseActivity<EmptyMediator>() {


    override fun getLayoutId() = R.layout.activity_main
    override fun initialize() {
    }

    fun searchBle(view: View) {
//        SearchBleActivity.start(this)
        BleActivity.start(this)
    }

    fun searchBluetooth(view: android.view.View) {
        ClassicsBluetoothActivity.start(this)
    }
}