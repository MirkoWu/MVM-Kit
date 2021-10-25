package com.mirkowu.mvm.ble

import android.bluetooth.BluetoothDevice
import com.mirkowu.lib_widget.adapter.BaseRVHolder
import com.mirkowu.lib_widget.adapter.SimpleRVAdapter
import com.mirkowu.mvm.R

class DeviceAdapter : SimpleRVAdapter<BluetoothDevice>(R.layout.item_device) {
    override fun onBindHolder(holder: BaseRVHolder, item: BluetoothDevice, position: Int) {
        holder.setText(R.id.tvName, "${item?.name} \n ${item?.address} \n" +
                "是否绑定：${item.bondState == BluetoothDevice.BOND_BONDED} ")
    }

}