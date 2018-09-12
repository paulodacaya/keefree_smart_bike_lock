package com.paulodacaya.keefree.utilities;

import java.util.UUID;



public class Constants {
  
  public enum Status { REQUESTING_PERMISSION, NO_BLE_SUPPORT, HAS_BLE_SUPPORT, TURN_SECURITY_OFF }
  
  public static final int UNLOCKED = 1;
  public static final int SECURITY_ON = 2;
  public static final int SECURITY_OFF = 3;
  public static final int APP_CODE = 4;
  public static final int PHONELESS_APP_CODE = 5;
  
  
  public static final int SPLASH_DISPLAY_LENGTH = 500;
  
  public static final String TIMEZONE_ID = "Australia/Melbourne";
  
  public static final String SETTINGS_FRAGMENT = "settings_fragment";
  public static final String TAG_BASIC_DIALOG = "ble_basic_dialog";
  public static final String TAG_LIST_DIALOG = "ble_list_dialog";
  public static final String TAG_LOADING_DIALOG = "loading_dialog";
  
  public static final int REQUEST_ENABLE_BT = 10;
  public static final int REQUEST_PERMISSIONS_CODE = 11;
  public static final long SCAN_PERIOD = 5000; // 5 seconds
  
  public static final String BLE_DEVICE_NAME = "CC41-A";
  public static final String BLE_DEVICE_MAC_ADDRESS = "00:15:80:90:72:E7";
  public static final UUID BLE_DEVICE_SERVICE_UUID = UUID.fromString( "0000ffe0-0000-1000-8000-00805f9b34fb" );
  public static final UUID BLE_DEVICE_CHARACTERISTICS_UUID = UUID.fromString( "0000ffe1-0000-1000-8000-00805f9b34fb" );
  
  public static final String SEND_UNLOCK = "u";
  public static final String RECEIVED_UNLOCK = "unlock";
  
  public static final String SEND_SECURITY_ON = "s";
  public static final String RECEIVED_SECURITY_ON = "security_on";
  
  public static final String SEND_SECURITY_OFF = "e";
  public static final String RECEIVED_SECURITY_OFF = "security_off";
  
}

/**
 * BLE DETAILS
 *
 * ScanResult{
    device=00:15:80:90:72:E7,
    scanRecord=ScanRecord [
      mAdvertiseFlags=6,
      mServiceUuids=[
        0000ffe0-0000-1000-8000-00805f9b34fb
      ],
      mManufacturerSpecificData={},
      mServiceData={},
      mTxPowerLevel=-2147483648,
      mDeviceName=CC41-A
    ],
    rssi=-65,
    timestampNanos=129405457003459,
    eventType=27,
    primaryPhy=1,
    secondaryPhy=0,
    advertisingSid=255,
     txPower=127,
    periodicAdvertisingInterval=0
   }
 */
