package com.mirkowu.mvm.ble

import com.mirkowu.lib_widget.adapter.BaseRVHolder
import com.mirkowu.lib_widget.adapter.SimpleRVAdapter
import com.mirkowu.mvm.R

class DeviceRssiAdapter : SimpleRVAdapter<BleRssiDevice>(R.layout.item_device) {
    override fun onBindHolder(holder: BaseRVHolder, item: BleRssiDevice, position: Int) {
        holder.setText(R.id.tvName, "${item?.name} \n ${item?.address}")
    }

}