package com.paulodacaya.keefree.utilities;

import com.google.android.gms.maps.model.LatLng;

import java.util.UUID;



public class Constants {
  
  public enum Status { REQUESTING_PERMISSION, NO_BLE_SUPPORT, NORMAL, TURN_SECURITY_OFF, CONNECT_TO_KEEFREE, UNLOCKING_KEEFREE }
  
  public static final int TYPE_UNLOCKED = 1;
  public static final int TYPE_SECURITY_ON = 2;
  public static final int TYPE_SECURITY_OFF = 3;
  public static final int TYPE_APP_CODE = 4;
  public static final int TYPE_PHONELESS_APP_CODE = 5;
  
  public static final int SPLASH_DISPLAY_LENGTH = 500; // 0.5 seconds
  
  public static final String LAT_LONG_REGEX = "^[-+]?([1-8]?\\d(\\.\\d+)?|90(\\.0+)?),\\s*[-+]?(180(\\.0+)?|((1[0-7]\\d)|([1-9]?\\d))(\\.\\d+)?)$";
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
  
  public static final int SENDING_MESSAGE_TIMEOUT = 8000; // 8 seconds
  
  public static final String SEND_CONNECTED = "c";
  public static final String RECEIVED_CONNECTED = "connected";
  
  public static final String SEND_UNLOCK = "u";
  public static final String RECEIVED_UNLOCK = "unlock";
  
  public static final String SEND_SECURITY_ON = "s";
  public static final String RECEIVED_SECURITY_ON = "security_on";
  
  public static final String SEND_SECURITY_OFF = "e";
  public static final String RECEIVED_SECURITY_OFF = "security_off";
  
  public static final long LOCATION_REQUEST_INTERVAL = 10000;
  public static final long LOCATION_REQUEST_INTERVAL_FASTEST = 6000;
  public static final LatLng DEFAULT_LOCATION = new LatLng( -37.751514, 144.742874 ); // PAULO's HOUSE .. TODO: remove when deploying
  public static final float DEFAULT_ZOOM = 15;
  public static final String PROVIDER_KEEFREE = "keefree_provider";
  public static final String KEEFREE_TITLE = "KEEFREE location";
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
