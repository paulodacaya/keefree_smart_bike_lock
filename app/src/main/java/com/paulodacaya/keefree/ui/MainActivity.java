package com.paulodacaya.keefree.ui;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattServer;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.bluetooth.le.BluetoothLeAdvertiser;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanFilter;
import android.bluetooth.le.ScanResult;
import android.bluetooth.le.ScanSettings;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.databinding.adapters.ToolbarBindingAdapter;
import android.location.LocationManager;
import android.nfc.Tag;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.ParcelUuid;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.paulodacaya.keefree.R;
import com.paulodacaya.keefree.ui.fragments.BasicDialogFragment;
import com.paulodacaya.keefree.ui.fragments.ListDialogFragment;
import com.paulodacaya.keefree.ui.fragments.LoadingDialogFragment;
import com.paulodacaya.keefree.ui.fragments.SettingsFragment;
import com.paulodacaya.keefree.utilities.Constants;

import java.io.UnsupportedEncodingException;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnLongClick;

public class MainActivity extends AppCompatActivity
        implements OnMapReadyCallback, ListDialogFragment.onDeviceSelectedInterface {
  
  public static final String TAG = MainActivity.class.getSimpleName();
  
  @BindView( R.id.bottomNavigation ) BottomNavigationView mBottomNavigationView;
  @BindView( R.id.mapContainer ) ConstraintLayout mMapContainer;
  @BindView( R.id.keefreeContainer ) ConstraintLayout mKeefeeContainer;
  @BindView( R.id.settingsContainer ) FrameLayout mSettingsContainer;
  @BindView( R.id.lockedStateImage ) ImageView mLockedStateImage;
  @BindView( R.id.unlockedStateImage ) ImageView mUnlockedStateImage;
  @BindView( R.id.connectedImage ) ImageView mConnectedImage;
  @BindView( R.id.connectedPrompt ) TextView mConnectedPrompt;
  @BindView( R.id.bleProgressBar ) ProgressBar mBleProgressBar;
  
  private GoogleMap mMap;
  private LocationManager mLocationManager; // used to manage location
  
  private Handler mHandler;
  private boolean mScanning;
  private boolean mInitialised;
  
  private static final int STATE_DISCONNECTED = 0;
  private static final int STATE_CONNECTING = 1;
  private static final int STATE_CONNECTED = 2;
  private int mConnectionState = STATE_DISCONNECTED;
  
  private ListDialogFragment mScannedDevicesDialog;
  private LoadingDialogFragment mLoadingDialog;
  
  private BluetoothAdapter mBluetoothAdapter;
  private BluetoothManager mBluetoothManager;
  private BluetoothGatt mBluetoothGatt;
  private BluetoothLeScanner mBluetoothLeScanner;
  private final ScanCallback mLeScanCallBack = new ScanCallback() {
    @Override
    public void onScanResult( int callbackType, ScanResult scanResult ) {
      super.onScanResult( callbackType, scanResult );
      
      List<ParcelUuid> Uuids = scanResult.getScanRecord().getServiceUuids();
      for( ParcelUuid uuid : Uuids ) {
        Log.d( TAG, "UUID:" + uuid.toString() );
      }
      
      // Add scanned device to dialog
      mScannedDevicesDialog.addDevice( scanResult.getDevice() );
      
    }
    
    @Override
    public void onScanFailed( int errorCode ) {
      super.onScanFailed( errorCode );
      
      // Prompt failure to scan
      DialogFragment failureToScanDialog = new BasicDialogFragment(
              getString( R.string.ble_scan_failed_title ),
              getString( R.string.ble_scan_failed_body ) + "\nError code: " + errorCode,
              false,
              true );
  
      failureToScanDialog.show( getSupportFragmentManager(), Constants.TAG_BASIC_DIALOG );
      
    }
  };
  private final BluetoothGattCallback mBluetoothGattCallback = new BluetoothGattCallback() {
  
    /**
     * Callback indicating when GATT client has connected/disconnected to/from a remote GATT server.
     */
    @Override
    public void onConnectionStateChange( BluetoothGatt gatt, int status, int newState ) {
      
      if( newState == BluetoothProfile.STATE_CONNECTED ) {
        
        mConnectionState = STATE_CONNECTED;
        
        runOnUiThread( new Runnable() {
          @Override
          public void run() {
            showConnected();
          }
        } );
        
        Log.d( TAG, "Connected to keefree device..." );
        mBluetoothGatt.discoverServices();
        Log.d( TAG, "Discovering services..." );
      
      } else if( newState == BluetoothProfile.STATE_DISCONNECTED ) {
        
        Log.d( TAG, "newState == state_disconnected" );
        disconnectGattServer();
      
      } else if( status == BluetoothGatt.GATT_FAILURE || status != BluetoothGatt.GATT_SUCCESS ) {
  
        Log.d( TAG, "status == gatt_failure || status == gatt_success" );
        disconnectGattServer();
        
      }
    
    }
  
    /**
     * Callback invoked when the list of remote services, characteristics and descriptors for the remote device have been updated
     * ie new services have been discovered.
     */
    @Override
    public void onServicesDiscovered( BluetoothGatt gatt, int status ) {
    
      if( status == BluetoothGatt.GATT_SUCCESS ) {
        
        BluetoothGattService service = gatt.getService( Constants.BLE_DEVICE_SERVICE_UUID );
        
        List<BluetoothGattCharacteristic> bluetoothGattCharacteristics = service.getCharacteristics();
        for( BluetoothGattCharacteristic characteristic : bluetoothGattCharacteristics ) {
          Log.d( TAG, characteristic.getUuid().toString() );
        }
        
        BluetoothGattCharacteristic characteristic = service.getCharacteristic( Constants.BLE_DEVICE_CHARACTERISTICS_UUID );
        
        characteristic.setWriteType( BluetoothGattCharacteristic.WRITE_TYPE_DEFAULT );
        mInitialised = gatt.setCharacteristicNotification( characteristic, true );
        
      } else {
  
        Log.d( TAG, "onServicesDiscovered received: " + status );
  
      }
      
    }
    
    /**
     * Callback indicating the result of a characteristic write operation.
     */
    @Override
    public void onCharacteristicWrite( BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status ) {
      super.onCharacteristicWrite( gatt, characteristic, status );
  
      byte[] bytes = characteristic.getValue();
      String message;
  
      try {
        message = new String( bytes, "UTF-8" );
        Log.d( TAG, "onCharacteristicWrite : '" + message + "'" );
        
      } catch( UnsupportedEncodingException error ) {
        error.printStackTrace();
      }
      
    }
  
    /**
     * Callback reporting the result of a characteristic read operation.
     */
    @Override
    public void onCharacteristicRead( BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status ) {
      super.onCharacteristicRead( gatt, characteristic, status );
  
      byte[] bytes = characteristic.getValue();
      String message = null;
      
      try {
        message = new String( bytes, "UTF-8" );
      } catch( UnsupportedEncodingException error ) {
        error.printStackTrace();
      }
  
      Log.d( TAG, "onCharacteristicRead : '" + message + "'" );
      
    }
  
    /**
     * Callback triggered as a result of a remote characteristic notification.
     */
    @Override
    public void onCharacteristicChanged( BluetoothGatt gatt, BluetoothGattCharacteristic characteristic ) {
      super.onCharacteristicChanged( gatt, characteristic );
    
      byte[] bytes = characteristic.getValue();
      String message = null;
    
      try {
        message = new String( bytes, "UTF-8" );
      
        // TODO: Handle different message received from device:
        if( message.equals( Constants.RECEIVED_UNLOCK ) ) {
          mLoadingDialog.dismiss();
          toggleULockingImage();
        }
      
      } catch( UnsupportedEncodingException error ) {
        error.printStackTrace();
      }
    
      Log.d( TAG, "onCharacteristicChanged : '" + message + "'" );
    
    }
  
    
    private void disconnectGattServer() {
      
      mConnectionState = STATE_DISCONNECTED;
      
      if( mBluetoothGatt != null ) {
        
        mBluetoothGatt.disconnect();
        mBluetoothGatt.close();
        showNotConnected();
        
      }
      
    }
    
  };
  
  @Override
  protected void onCreate( Bundle savedInstanceState ) {
    super.onCreate( savedInstanceState );
    setContentView( R.layout.activity_main );
    ButterKnife.bind( this );
  
    setActionBar();
  
    // GOOGLE MAPS
    SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById( R.id.map );
    mapFragment.getMapAsync( this );
  
    // BOTTOM NAVIGATION
    mBottomNavigationView.setOnNavigationItemSelectedListener( mOnNavigationItemSelectedListener );
    toggleContent( R.id.bottom_navigation_keefree );
  
    // Set shared preferences default values
    PreferenceManager.setDefaultValues( this, R.xml.preference, false );
  
    // Settings fragment
    SettingsFragment settingsFragment = new SettingsFragment();
    getFragmentManager().beginTransaction()
            .add( R.id.settingsContainer, settingsFragment, Constants.SETTINGS_FRAGMENT )
            .commit();
  
    // Bluetooth BLE
    mHandler = new Handler();
    if( hasBluetoothSupport() ) {
      setUpBluetooth();
    }
  
  }
  
  @Override
  protected void onResume() {
    super.onResume();
    
    // Bluetooth may of been disabled when app was paused
    if( hasBluetoothSupport() && isBluetoothActive() ) {
      Log.d(  TAG ,"Still Working..." );
    }
    
  }
  
  private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener = new BottomNavigationView.OnNavigationItemSelectedListener() {
    
    @Override
    public boolean onNavigationItemSelected( @NonNull MenuItem item ) {
      
      // toggle content depending on selected bottom navigation
      toggleContent( item.getItemId() );
      
      switch( item.getItemId() ) {
        case R.id.bottom_navigation_activity:
          return true;
        
        case R.id.bottom_navigation_location:
          return true;
        
        case R.id.bottom_navigation_keefree:
          return true;
        
        case R.id.bottom_navigation_settings:
          return true;
      }
      
      return false;
    }
    
  };
  
  private void toggleContent( int id ) {
    
    switch( id ) {
      case R.id.bottom_navigation_keefree:
        setActionBarTitle( this.getString( R.string.actionbar_title_keefree ) );
        
        mKeefeeContainer.setVisibility( View.VISIBLE );
        mMapContainer.setVisibility( View.INVISIBLE );
        mSettingsContainer.setVisibility( View.INVISIBLE );
        break;
      
      case R.id.bottom_navigation_location:
        setActionBarTitle( this.getString( R.string.actionbar_title_location ) );
  
        mMapContainer.setVisibility( View.VISIBLE );
        mKeefeeContainer.setVisibility( View.INVISIBLE );
        mSettingsContainer.setVisibility( View.INVISIBLE );
        break;
      
      case R.id.bottom_navigation_activity:
        setActionBarTitle( this.getString( R.string.actionbar_title_activity ) );
        
        mKeefeeContainer.setVisibility( View.INVISIBLE );
        mMapContainer.setVisibility( View.INVISIBLE );
        mSettingsContainer.setVisibility( View.INVISIBLE );
        break;
      
      case R.id.bottom_navigation_settings:
        setActionBarTitle( this.getString( R.string.actionbar_title_settings ) );
  
        mSettingsContainer.setVisibility( View.VISIBLE );
        mKeefeeContainer.setVisibility( View.INVISIBLE );
        mMapContainer.setVisibility( View.INVISIBLE );
        break;
    }
    
  }
  
  private void setActionBar() {
    
    getSupportActionBar().setDisplayOptions( ActionBar.DISPLAY_SHOW_CUSTOM );
    getSupportActionBar().setCustomView( R.layout.custom_actionbar_layout );
    
  }
  
  private void setActionBarTitle( String title ) {
    
    View view = getSupportActionBar().getCustomView();
    TextView titleTextView = view.findViewById( R.id.actionBarText );
    titleTextView.setText( title );
    
  }
  
  private void requestPermissions() {
  
    // Permissions granting were added in Marshmallow, check for sdk version
    if( Build.VERSION.SDK_INT >= Build.VERSION_CODES.M ) {
      
      if( !hasPermissions() ) {
        
        // If user has denied permissions previously
        if( shouldShowRequestPermissionRationale( Manifest.permission.ACCESS_FINE_LOCATION ) ) {
          
          promptBasicDialog( getString( R.string.permission_location_title ),
                  getString( R.string.permission_location_body ),
                  true,
                  true );
        
        } else {
          
          requestPermissions( new String[] { Manifest.permission.ACCESS_FINE_LOCATION}, Constants.REQUEST_PERMISSIONS_CODE );
          
        }
        
      }
      
    }
  
  }
  
  private boolean hasPermissions() {
    return checkSelfPermission( Manifest.permission.ACCESS_FINE_LOCATION ) == PackageManager.PERMISSION_GRANTED;
  }
  
  @Override
  public void onRequestPermissionsResult( int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults ) {
  
    if( requestCode == Constants.REQUEST_PERMISSIONS_CODE ) {
      
      // Check if location permission was granted
      if( grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED ) {
        
        // TODO: Maybe scanForBLEDevice? Though it may not be wanted.
        
      }
      
    }
    
  }
  
  private boolean hasBluetoothSupport() {
    
    if( !getPackageManager().hasSystemFeature( PackageManager.FEATURE_BLUETOOTH_LE ) ) {
      
      promptBasicDialog( getString( R.string.ble_not_supported_title ),
              getString( R.string.ble_not_supported_body ),
              false,
              false );
      
      return false;
    }
    
    return true;
  }
  
  private void setUpBluetooth() {
    
    mBluetoothManager = (BluetoothManager) getSystemService( Context.BLUETOOTH_SERVICE );
    mBluetoothAdapter = mBluetoothManager.getAdapter();
    mBluetoothLeScanner = mBluetoothAdapter.getBluetoothLeScanner();
    
    mScannedDevicesDialog = new ListDialogFragment(); // Initialise dialog here?
    
    if( isBluetoothActive() ) {
      Log.d( TAG, "Bluetooth is active on device..." );
    }
    
  }
  
  private boolean isBluetoothActive() {
    
    if( mBluetoothAdapter == null || !mBluetoothAdapter.isEnabled() ) {
      
      // Request to turn bluetooth on
      Intent enableBluetoothIntent = new Intent( BluetoothAdapter.ACTION_REQUEST_ENABLE );
      startActivityForResult( enableBluetoothIntent, Constants.REQUEST_ENABLE_BT );
      
      return false;
      
    }
    
    return true;
  }
  
  private void scanForBLEDevice() {
    
    if( hasBluetoothSupport() ) {
      
      // Stop scanning after pre-defined scanning period.
      mHandler.postDelayed( new Runnable() {
        @Override
        public void run() {
  
          Log.d( TAG, "Stopped scanning after scanning period" );
          
          showNotConnected();
          
          mBluetoothLeScanner.stopScan( mLeScanCallBack );
          
          // Display after scanning completes
          if( mScannedDevicesDialog != null && mScannedDevicesDialog.getDevicesCount() > 0 ) {
  
            mScannedDevicesDialog.show( getSupportFragmentManager(), Constants.TAG_LIST_DIALOG  );
          
          } else {
          
            promptBasicDialog( getString( R.string.ble_no_devices_found_title ),
                    getString( R.string.ble_no_devices_found_body ),
                    false,
                    true );
            
          }
          
          // Variable Cleanup
          mScanning = false;
    
        }
      }, Constants.SCAN_PERIOD );
      
      mScannedDevicesDialog.clearDevices(); // Clear the list when devices have been scanned previously.
      mScanning = true;
      showLoading();
      
      // Start scan with filter and setting
      mBluetoothLeScanner.startScan(
        
              Collections.singletonList(
                      new ScanFilter.Builder().setDeviceName( Constants.BLE_DEVICE_NAME )
                      .setDeviceAddress( Constants.BLE_DEVICE_MAC_ADDRESS )
                      .build()
              ),
              
              new ScanSettings.Builder()
              .setCallbackType( ScanSettings.CALLBACK_TYPE_ALL_MATCHES )
              .setNumOfMatches( ScanSettings.MATCH_NUM_ONE_ADVERTISEMENT )
              .setScanMode( ScanSettings.SCAN_MODE_LOW_POWER )
              .build(),
        
              mLeScanCallBack
              
      );
      
    } else {
      
      mScanning = false;
      mBluetoothLeScanner.stopScan( mLeScanCallBack );
      showNotConnected();
      
    }
    
  }
  
  @Override
  public void onListDeviceSelected( BluetoothDevice device ) {
    
    mScannedDevicesDialog.dismiss();
    mConnectionState = STATE_CONNECTING;
    showLoading();
    
    // Connect device to Bluetooth Gatt
    // TODO: AUTO connect is set to false, may want to change this for testing.
    mBluetoothGatt = device.connectGatt( this,false, mBluetoothGattCallback );
    
  }
  
  @Override
  public void onMapReady( GoogleMap googleMap ) {
    
    mMap = googleMap;
    mLocationManager = (LocationManager) this.getSystemService( LOCATION_SERVICE ); // instantiate LocationManager
    
    // Add a marker in Sydney and move the camera
    LatLng sydney = new LatLng( -37.843964, 144.968183 );
    MarkerOptions markerOptions = new MarkerOptions().position( sydney )
            .icon( BitmapDescriptorFactory.fromResource( R.drawable.keefree_map_icon ) )
            .title( "Bike location" );
    mMap.addMarker( markerOptions );
    mMap.moveCamera( CameraUpdateFactory.newLatLngZoom( sydney, 15 ) );
    
  }
  
  @OnClick( R.id.linkImage )
  public void linkImageClick() {
    
    if( !isBluetoothActive() && !hasPermissions() ) {
      requestPermissions();
      return;
    }
    
    if ( mScanning ) {
      promptToast( "Already scanning..." );
      return;
    }
    
    if( mConnectionState == STATE_CONNECTED ) {
      promptToast( "Already connected..." );
      return;
    }
    
    if( isBluetoothActive() && hasPermissions() ) {
      scanForBLEDevice();
    } else  {
      requestPermissions();
    }
  
  }
  
  /**
   *  1. Change Method name appropriately
   *  2. Unlock/Lock bike lock:
   *    2.1 Check if there is bluetooth connection
   *      2.1.1 Display prompt if there is no image
   *    2.2 Send unlock request
   *      2.2.1 Display a spinner to hold UI
   *    2.3 Receive response from bike
   *  3. Update UI accordingly to response
   */
  @OnLongClick( R.id.keefreeButton )
  public boolean UnlockKeefree() {
    
    if( mConnectionState == STATE_CONNECTED && mInitialised ) {
      
      promptLoadingDialog();
      
      BluetoothGattService service = mBluetoothGatt.getService( Constants.BLE_DEVICE_SERVICE_UUID );
      BluetoothGattCharacteristic characteristic = service.getCharacteristic( Constants.BLE_DEVICE_CHARACTERISTICS_UUID );
      
      sendCharacter( characteristic, Constants.SEND_UNLOCK );
    
    } else {
      
      promptToast( "You are not connected..." );
      
    }
    
    return true;
  }
  
  private void sendCharacter( BluetoothGattCharacteristic characteristic, String message ) {
    
    byte[] messageBytes = new byte[0];
    
    try {
      messageBytes = message.getBytes( "UTF-8" );
    } catch( UnsupportedEncodingException error ) {
      error.printStackTrace();
    }
  
    characteristic.setValue( messageBytes );
    mBluetoothGatt.writeCharacteristic( characteristic );
    
  }
  
  private void promptBasicDialog( String title, String body, boolean isRequestingPermission, boolean hasBleSupport ) {
    
    DialogFragment dialog = new BasicDialogFragment(
            getString( R.string.ble_no_devices_found_title ),
            getString( R.string.ble_no_devices_found_body ),
            false,
            true );
    
    dialog.show( getSupportFragmentManager(), Constants.TAG_BASIC_DIALOG );
    
  }
  
  private void promptLoadingDialog() {
  
    mLoadingDialog = new LoadingDialogFragment();
    mLoadingDialog.show( getSupportFragmentManager(), Constants.TAG_LOADING_DIALOG );
  
  }
  
  private void promptToast( String message ) {
  
    Toast toast = Toast.makeText( this, message, Toast.LENGTH_LONG );
    View toastView = toast.getView();
    
    toastView.setBackground( getDrawable( R.drawable.bg_rounded_dark ) );
    
    TextView textView = toastView.findViewById( android.R.id.message );
    textView.setTextColor( getColor( R.color.colorWhite ) );
    
    toast.setGravity( Gravity.CENTER_HORIZONTAL | Gravity.TOP, 0, 400 );
    
    toast.show();
    
  }
  
  private void toggleULockingImage() {
    
    if( mLockedStateImage.getVisibility() == View.VISIBLE ) {
      
      mLockedStateImage.setVisibility( View.INVISIBLE );
      mUnlockedStateImage.setVisibility( View.VISIBLE );
      
    } else {
      
      mLockedStateImage.setVisibility( View.VISIBLE );
      mUnlockedStateImage.setVisibility( View.INVISIBLE );
      
    }
    
  }
  
  public void showLoading() {
    
    mBleProgressBar.setVisibility( View.VISIBLE );
    mConnectedImage.setVisibility( View.INVISIBLE );
    
  }
  
  public void showNotConnected() {
    
    mBleProgressBar.setVisibility( View.INVISIBLE );
    mConnectedPrompt.setText( getString( R.string.ble_not_connected ) );
    mConnectedImage.setVisibility( View.VISIBLE );
    mConnectedImage.setImageResource( R.drawable.ic_circle_x );
    
  }
  
  public void showConnected() {
  
    mBleProgressBar.setVisibility( View.INVISIBLE );
    mConnectedPrompt.setText( getString( R.string.ble_connected ) );
    mConnectedImage.setVisibility( View.VISIBLE );
    mConnectedImage.setImageResource( R.drawable.ic_circle_check );
    
  }
  
}
