package com.mirkowu.mvm.ble;


import android.os.Parcel;

import com.mirkowu.lib_ble.ble.model.BleDevice;
import com.mirkowu.lib_ble.ble.model.ScanRecord;

public class BleRssiDevice extends BleDevice {
    private ScanRecord scanRecord;
    private int rssi;
    private long rssiUpdateTime;

    public BleRssiDevice(String address, String name) {
        super(address, name);
    }

    /*public BleRssiDevice(BleDevice device, ScanRecord scanRecord, int rssi) {
        this.device = device;
        this.scanRecord = scanRecord;
        this.rssi = rssi;
    }*/

    public ScanRecord getScanRecord() {
        return scanRecord;
    }

    public void setScanRecord(ScanRecord scanRecord) {
        this.scanRecord = scanRecord;
    }

    public int getRssi() {
        return rssi;
    }

    public void setRssi(int rssi) {
        this.rssi = rssi;
    }

    public long getRssiUpdateTime() {
        return rssiUpdateTime;
    }

    public void setRssiUpdateTime(long rssiUpdateTime) {
        this.rssiUpdateTime = rssiUpdateTime;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeParcelable(this.scanRecord, flags);
        dest.writeInt(this.rssi);
        dest.writeLong(this.rssiUpdateTime);
    }

    public void readFromParcel(Parcel source) {
        this.scanRecord = source.readParcelable(ScanRecord.class.getClassLoader());
        this.rssi = source.readInt();
        this.rssiUpdateTime = source.readLong();
    }

    protected BleRssiDevice(Parcel in) {
        super(in);
        this.scanRecord = in.readParcelable(ScanRecord.class.getClassLoader());
        this.rssi = in.readInt();
        this.rssiUpdateTime = in.readLong();
    }

    public static final Creator<BleRssiDevice> CREATOR = new Creator<BleRssiDevice>() {
        @Override
        public BleRssiDevice createFromParcel(Parcel source) {
            return new BleRssiDevice(source);
        }

        @Override
        public BleRssiDevice[] newArray(int size) {
            return new BleRssiDevice[size];
        }
    };
}
