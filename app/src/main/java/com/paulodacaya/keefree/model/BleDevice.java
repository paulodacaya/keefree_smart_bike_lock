package com.paulodacaya.keefree.model;

import android.bluetooth.BluetoothDevice;

public class BleDevice {
  
  private BluetoothDevice mBluetoothDevice;
  
  public BleDevice() {
  }
  
  public BleDevice( BluetoothDevice bluetoothDevice ) {
    mBluetoothDevice = bluetoothDevice;
  }
  
  public BluetoothDevice getBluetoothDevice() {
    return mBluetoothDevice;
  }
  
  public String getDeviceName() {
    return mBluetoothDevice.getName();
  }
  
  public String getDeviceMacAddress() {
    return mBluetoothDevice.getAddress();
  }
  
}
