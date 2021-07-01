package com.mirkowu.mvm.ble;

import android.bluetooth.BluetoothDevice;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.mirkowu.lib_base.adapter.BaseAdapter;
import com.mirkowu.mvm.R;

import org.jetbrains.annotations.NotNull;

public class DeviceAdapter extends BaseAdapter<BluetoothDevice, DeviceAdapter.ViewHolder> {

    @Override
    public void onBindHolder(@NonNull @NotNull ViewHolder holder, BluetoothDevice item, int position) {
        holder.tvName.setText(item.getName() + "\n" + item.getAddress());
    }

    @NonNull
    @NotNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_device, parent, false));
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView tvName;

        public ViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvName);

        }
    }
}
