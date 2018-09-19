package com.paulodacaya.keefree.ui;

import android.Manifest;
import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanFilter;
import android.bluetooth.le.ScanResult;
import android.bluetooth.le.ScanSettings;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.ParcelUuid;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.DialogFragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.directions.route.Route;
import com.directions.route.RouteException;
import com.directions.route.Routing;
import com.directions.route.RoutingListener;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.paulodacaya.keefree.R;
import com.paulodacaya.keefree.database.Database;
import com.paulodacaya.keefree.model.Keefree;
import com.paulodacaya.keefree.model.Record;
import com.paulodacaya.keefree.ui.fragments.BasicDialogFragment;
import com.paulodacaya.keefree.ui.fragments.ListDialogFragment;
import com.paulodacaya.keefree.ui.fragments.LoadingDialogFragment;
import com.paulodacaya.keefree.ui.fragments.SettingsFragment;
import com.paulodacaya.keefree.utilities.Constants;
import com.paulodacaya.keefree.utilities.Utilities;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnLongClick;
import io.realm.Realm;


/**
 * Tasks TODO
 * * Handle most messages returned from Arduino
 * * Store information to database and display in activity
 * <p>
 * <p>
 * * Handle a timeout of unlocking, security ON and security OFF when no message is return from Arduino.
 * * Edge case: disconnection when sending/receiving messages.
 * <p>
 * <p>
 * MAPS
 * * Display line path connecting both points (THICK BLACK LINE)
 * * Information icon button to display relative info ( distance:KM, etc. )
 * * "Open on Google Maps" button to auto
 * <p>
 * <p>
 * STRETCH GOALS
 * * onBoarding for first time users.
 * * Auto connect to device when have connected previously.
 * * Use database to manage if connected previously?
 */
public class MainActivity extends AppCompatActivity
        implements OnMapReadyCallback, RoutingListener, ListDialogFragment.IListDialogFragment,
        BasicDialogFragment.IBasicDialogFragment, ActivityAdapter.IActivityAdapter {
  
  public static final String TAG = MainActivity.class.getSimpleName();
  
  @BindView( R.id.activitySwipeRefresh ) SwipeRefreshLayout mActivitySwipeRefresh;
  @BindView( R.id.bottomNavigation ) BottomNavigationView mBottomNavigationView;
  @BindView( R.id.activityContainer ) ConstraintLayout mActivityContainer;
  @BindView( R.id.mapContainer ) ConstraintLayout mMapContainer;
  @BindView( R.id.keefreeContainer ) ConstraintLayout mKeefreeContainer;
  @BindView( R.id.settingsContainer ) FrameLayout mSettingsContainer;
  @BindView( R.id.lockedStateImage ) ImageView mLockedStateImage;
  @BindView( R.id.unlockedStateImage ) ImageView mUnlockedStateImage;
  @BindView( R.id.linkImage ) ImageView mLinkImage;
  @BindView( R.id.connectedImage ) ImageView mConnectedImage;
  @BindView( R.id.connectedPrompt ) TextView mConnectedPrompt;
  @BindView( R.id.connectedProgressBar ) ProgressBar mConnectedProgressBar;
  @BindView( R.id.shieldImage ) ImageView mShieldImage;
  @BindView( R.id.securityImage ) ImageView mSecurityImage;
  @BindView( R.id.securityPrompt ) TextView mSecurityPrompt;
  @BindView( R.id.securityProgressBar ) ProgressBar mSecurityProgressBar;
  @BindView( R.id.activityRecyclerView ) RecyclerView mActivityRecyclerView;
  ImageView mActionReloadImage;
  
  Vibrator mVibrator;
  
  private Realm mRealm;
  Keefree mKeefree;
  
  private ActivityAdapter mActivityAdapter;
  private List<Record> mActivityRecords;
  
  private GoogleMap mMap;
  private List<Polyline> mPolylines;
  private FusedLocationProviderClient mFusedLocationProviderClient;
  private LocationRequest mLocationRequest;
  private boolean mRequestingLocationUpdates = false;
  private LocationCallback mLocationCallback = new LocationCallback() {
    
    @Override
    public void onLocationResult( LocationResult locationResult ) {
      
      if( locationResult == null ) {
        return;
      }
  
      clearPolyLines();
      drawRouteToKeeFree();
      
    }
    
  };
  
  
  private MarkerOptions mKeefreeMarkerOptions;
  private Marker mKeefreeMarker;
  private Location mCurrentLocation;
  
  private Handler mHandler;
  private boolean mIsScanning = false;
  private boolean mIsInitialised = false;
  private boolean mIsSecurityOn = false;
  
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
      
      List<ParcelUuid> Uuids = scanResult.getScanRecord()
              .getServiceUuids();
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
      promptBasicDialog( getString( R.string.ble_scan_failed_title ), getString( R.string.ble_scan_failed_body ) + "\nError code: " + errorCode, Constants.Status.HAS_BLE_SUPPORT );
      
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
            toggleShieldImage();
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
          Log.d( TAG, characteristic.getUuid()
                  .toString() );
        }
        
        BluetoothGattCharacteristic characteristic = service.getCharacteristic( Constants.BLE_DEVICE_CHARACTERISTICS_UUID );
        
        characteristic.setWriteType( BluetoothGattCharacteristic.WRITE_TYPE_DEFAULT );
        mIsInitialised = gatt.setCharacteristicNotification( characteristic, true );
        
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
        
        if( message.equals( Constants.RECEIVED_UNLOCK ) ) {
          
          mLoadingDialog.dismiss();
          Database.writeRecord( mRealm, new Record( Constants.TYPE_UNLOCKED ) );
          
          runOnUiThread( new Runnable() {
            @Override
            public void run() {
              toggleLockingImage();
            }
          } );
          
        } else if( message.equals( Constants.RECEIVED_SECURITY_ON ) ) {
          
          mIsSecurityOn = true;
          mLoadingDialog.dismiss();
          Database.writeRecord( mRealm, new Record( Constants.TYPE_SECURITY_ON ) );
          
          runOnUiThread( new Runnable() {
            @Override
            public void run() {
              showSecurityOn();
            }
          } );
          
        } else if( message.equals( Constants.RECEIVED_SECURITY_OFF ) ) {
          
          mIsSecurityOn = false;
          mLoadingDialog.dismiss();
          Database.writeRecord( mRealm, new Record( Constants.TYPE_SECURITY_OFF ) );
          
          runOnUiThread( new Runnable() {
            @Override
            public void run() {
              showSecurityOff();
            }
          } );
          
        } else if( message.matches( Constants.LAT_LONG_REGEX ) ) {
          
          // Split string from "," and store into variable
          String[] latLon = message.split( "," );
          Database.writeKeefreeLocation( mRealm, Double.parseDouble( latLon[0] ), Double.parseDouble( latLon[1] ) );
          
          // Update Map UI with
          if( mKeefree != null ) {
            mKeefreeMarker.remove();
          }
          
          showKeeFreeLocation();
          
        } else if( message.endsWith( "kmph" ) ) {
          
          // Speed of keefree (kmph)
          float speedFloat = Float.parseFloat( message.replaceFirst( "kmph", "" ) );
          Database.writeKeefreeSpeed( mRealm, speedFloat );
          
        } else if( message.endsWith( "m" ) ) {
          
          // Altitude of keefree (measurement above sea level)
          double altitudeDouble = Double.parseDouble( message.replaceFirst( "m", "" ) );
          Database.writeKeefreeAltitude( mRealm, altitudeDouble );
          
        }
        
      } catch( UnsupportedEncodingException error ) {
        error.printStackTrace();
      }
      
      Log.d( TAG, "onCharacteristicChanged : '" + message + "'" );
      
    }
    
  };
  
  @Override
  protected void onCreate( Bundle savedInstanceState ) {
    super.onCreate( savedInstanceState );
    setContentView( R.layout.activity_main );
    ButterKnife.bind( this );
    mVibrator = (Vibrator) getSystemService( Context.VIBRATOR_SERVICE );
    
    // OPEN REALM DATABASE
    Realm.deleteRealm( Database.getRealmConfiguration() ); // TODO: remove when deploying.
    mRealm = Realm.getInstance( Database.getRealmConfiguration() );
    
    // ACTION BAR
    setActionBar();
    
    // GOOGLE MAPS
    mPolylines = new ArrayList<>();
    mKeefree = Database.getKeefree( mRealm );
    mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient( this );
    SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById( R.id.map );
    mapFragment.getMapAsync( this );
    
    // BOTTOM NAVIGATION
    mBottomNavigationView.setOnNavigationItemSelectedListener( mOnNavigationItemSelectedListener );
    toggleContent( R.id.bottom_navigation_keefree );
    
    // Set shared preferences default values
    PreferenceManager.setDefaultValues( this, R.xml.preference, false );
    
    // Settings fragment
    SettingsFragment settingsFragment = new SettingsFragment( mRealm );
    getFragmentManager().beginTransaction()
            .add( R.id.settingsContainer, settingsFragment, Constants.SETTINGS_FRAGMENT )
            .commit();
    
    // Bluetooth BLE
    mHandler = new Handler();
    if( hasBluetoothSupport() ) {
      setUpBluetooth();
    }
    
    // Activity Section
    setActivitySection();
    
    // Swipe refresh handler
    setSwipeRefresh();
  }
  
  @Override
  protected void onResume() {
    super.onResume();
  
    // Bluetooth may of been disabled when app was paused
    if( hasBluetoothSupport() && isBluetoothActive() ) {
      Log.d( TAG, "Device has bluetooth support and is active..." );
    }
  
    if( !mRequestingLocationUpdates ) {
      
      Log.d( TAG, "Started Location Updates..." );
      startLocationUpdates();
      
    }
    
  }
  
  @Override
  protected void onPause() {
    super.onPause();
    
    if( mRequestingLocationUpdates ) {
      
      Log.d( TAG, "Stopped Location Updates..." );
      stopLocationUpdates();
      
    }
    
  }
  
  @Override
  protected void onDestroy() {
    super.onDestroy();
    
    // Close realm
    mRealm.close();
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
        
        mKeefreeContainer.setVisibility( View.VISIBLE );
        mMapContainer.setVisibility( View.INVISIBLE );
        mSettingsContainer.setVisibility( View.INVISIBLE );
        mActivityContainer.setVisibility( View.INVISIBLE );
        mActionReloadImage.setVisibility( View.INVISIBLE );
        break;
      
      case R.id.bottom_navigation_location:
        setActionBarTitle( this.getString( R.string.actionbar_title_location ) );
        
        mMapContainer.setVisibility( View.VISIBLE );
        mKeefreeContainer.setVisibility( View.INVISIBLE );
        mSettingsContainer.setVisibility( View.INVISIBLE );
        mActivityContainer.setVisibility( View.INVISIBLE );
        mActionReloadImage.setVisibility( View.INVISIBLE );
        
        if( mKeefree == null ) {
          promptBasicDialog( getString( R.string.connect_to_keefree_title ), getString( R.string.connect_to_keefree_body ), Constants.Status.CONNECT_TO_KEEFREE );
        }
        break;
      
      case R.id.bottom_navigation_activity:
        setActionBarTitle( this.getString( R.string.actionbar_title_activity ) );
        updateActivitySection();
        
        mActivityContainer.setVisibility( View.VISIBLE );
        mActionReloadImage.setVisibility( View.VISIBLE );
        mKeefreeContainer.setVisibility( View.INVISIBLE );
        mMapContainer.setVisibility( View.INVISIBLE );
        mSettingsContainer.setVisibility( View.INVISIBLE );
        break;
      
      case R.id.bottom_navigation_settings:
        setActionBarTitle( this.getString( R.string.actionbar_title_settings ) );
        
        mSettingsContainer.setVisibility( View.VISIBLE );
        mKeefreeContainer.setVisibility( View.INVISIBLE );
        mMapContainer.setVisibility( View.INVISIBLE );
        mActivityContainer.setVisibility( View.INVISIBLE );
        mActionReloadImage.setVisibility( View.INVISIBLE );
        break;
    }
    
  }
  
  private void setActionBar() {
    
    getSupportActionBar().setDisplayOptions( ActionBar.DISPLAY_SHOW_CUSTOM );
    getSupportActionBar().setCustomView( R.layout.custom_actionbar_layout );
    
    View customActionBar = getSupportActionBar().getCustomView();
    mActionReloadImage = customActionBar.findViewById( R.id.actionBarReloadImage );
    
    // Handle refresh button click
    mActionReloadImage.setOnClickListener( new View.OnClickListener() {
      @Override
      public void onClick( View v ) {
        
        Log.d( TAG, "Reload button clicked" );
        mActivitySwipeRefresh.setRefreshing( true );
        updateActivitySection();
        mActivitySwipeRefresh.setRefreshing( false );
        
      }
    } );
    
  }
  
  private void setActionBarTitle( String title ) {
    
    View view = getSupportActionBar().getCustomView();
    TextView titleTextView = view.findViewById( R.id.actionBarText );
    titleTextView.setText( title );
    
  }
  
  private void setActivitySection() {
    
    mActivityRecords = Database.getAllRecord( mRealm );
    mActivityAdapter = new ActivityAdapter( this, mActivityRecords );
    
    mActivityRecyclerView.setAdapter( mActivityAdapter );
    mActivityRecyclerView.setHasFixedSize( true );
    //mActivityRecyclerView.addItemDecoration( new DividerItemDecoration( this, DividerItemDecoration.VERTICAL ) ); // adds divider lines
    mActivityRecyclerView.setLayoutManager( new LinearLayoutManager( this ) );
    
  }
  
  private void setSwipeRefresh() {
    
    mActivitySwipeRefresh.setOnRefreshListener( new SwipeRefreshLayout.OnRefreshListener() {
      @Override
      public void onRefresh() {
        
        Log.d( TAG, "onRefresh called from SwipeRefreshLayout" );
        updateActivitySection();
        mActivitySwipeRefresh.setRefreshing( false );
        
      }
    } );
  }
  
  private void updateActivitySection() {
    mActivityRecords = Database.getAllRecord( mRealm );
    mActivityAdapter.notifyDataSetChanged();
  }
  
  private void requestPermissions() {
    
    // Permissions granting were added in Marshmallow, check for sdk version
    if( Build.VERSION.SDK_INT >= Build.VERSION_CODES.M ) {
      
      if( !hasPermissions() ) {
        
        // If user has denied permissions previously
        if( shouldShowRequestPermissionRationale( Manifest.permission.ACCESS_FINE_LOCATION ) ) {
          
          promptBasicDialog( getString( R.string.permission_location_title ), getString( R.string.permission_location_body ), Constants.Status.REQUESTING_PERMISSION );
          
        } else {
          
          requestPermissions( new String[] {Manifest.permission.ACCESS_FINE_LOCATION}, Constants.REQUEST_PERMISSIONS_CODE );
          
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
      
      promptBasicDialog( getString( R.string.ble_not_supported_title ), getString( R.string.ble_not_supported_body ), Constants.Status.NO_BLE_SUPPORT );
      
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
      mConnectionState = STATE_DISCONNECTED;
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
    
    // TODO: HANDLE AUTO CONNECTED IF ITS BEEN CONNECTED PREVIOUSLY!
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
            
            mScannedDevicesDialog.show( getSupportFragmentManager(), Constants.TAG_LIST_DIALOG );
            
          } else {
            
            promptBasicDialog( getString( R.string.ble_no_devices_found_title ), getString( R.string.ble_no_devices_found_body ), Constants.Status.HAS_BLE_SUPPORT );
            
          }
          
          // Variable Cleanup
          mIsScanning = false;
          mIsInitialised = false;
          
        }
      }, Constants.SCAN_PERIOD );
      
      mScannedDevicesDialog.clearDevices(); // Clear the list when devices have been scanned previously.
      mIsScanning = true;
      showScanLoading();
      
      // Start scan with filter and setting
      mBluetoothLeScanner.startScan(
              
              Collections.singletonList( new ScanFilter.Builder().setDeviceName( Constants.BLE_DEVICE_NAME )
                      .setDeviceAddress( Constants.BLE_DEVICE_MAC_ADDRESS )
                      .build() ),
              
              new ScanSettings.Builder().setCallbackType( ScanSettings.CALLBACK_TYPE_ALL_MATCHES )
                      .setNumOfMatches( ScanSettings.MATCH_NUM_ONE_ADVERTISEMENT )
                      .setScanMode( ScanSettings.SCAN_MODE_LOW_POWER )
                      .build(),
              
              mLeScanCallBack
      
      );
      
    } else {
      
      mIsScanning = false;
      mBluetoothLeScanner.stopScan( mLeScanCallBack );
      showNotConnected();
      
    }
    
  }
  
  @Override
  public void onMapReady( GoogleMap googleMap ) {
    
    mMap = googleMap;
    
    updateLocationUI();    // adds UI elements to google map e.g. focus location.
    
    setLocationRequest();  // Location Callback settings
    getDeviceLocation();
    
    startLocationUpdates();
    
    showKeeFreeLocation();
    
  }
  
  protected void setLocationRequest() {
    
    mLocationRequest = new LocationRequest();
    mLocationRequest.setInterval( Constants.LOCATION_REQUEST_INTERVAL );
    mLocationRequest.setFastestInterval( Constants.LOCATION_REQUEST_INTERVAL_FASTEST );
    mLocationRequest.setPriority( LocationRequest.PRIORITY_HIGH_ACCURACY );
    
  }
  
  private void updateLocationUI() {
    
    if( mMap == null ) {
      return;
    }
    
    try {
      
      if( hasPermissions() ) {
        
        mMap.setMyLocationEnabled( true );
        mMap.getUiSettings()
                .setMyLocationButtonEnabled( true );
        
      } else {
        
        mMap.setMyLocationEnabled( false );
        mMap.getUiSettings()
                .setMyLocationButtonEnabled( false );
        mCurrentLocation = null;
        requestPermissions();
        
      }
      
    } catch( SecurityException e ) {
      Log.e( TAG, "Exception: " + e.getMessage() );
    }
  }
  
  private void getDeviceLocation() {
    
    /*
     * Get the best and most recent location of the device, which may be null in rare
     * cases when a location is not available.
     */
    
    try {
      
      if( hasPermissions() ) {
        
        Task<Location> locationResult = mFusedLocationProviderClient.getLastLocation();
        
        locationResult.addOnCompleteListener( this, new OnCompleteListener<Location>() {
          @Override
          public void onComplete( @NonNull Task<Location> task ) {
            
            if( task.isSuccessful() ) {
              
              mCurrentLocation = task.getResult();
              
              mMap.moveCamera( CameraUpdateFactory.newLatLngZoom( new LatLng( mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude() ), Constants.DEFAULT_ZOOM ) );
              Log.d( TAG, "CURRENT LOCATION: " + mCurrentLocation.getLatitude() + ", " + mCurrentLocation.getLongitude() );
              
            } else {
              
              Log.d( TAG, "CURRENT LOCATION is NULL" );
              Log.d( TAG, "Exception: " + task.getException() );
              mMap.moveCamera( CameraUpdateFactory.newLatLngZoom( Constants.DEFAULT_LOCATION, Constants.DEFAULT_ZOOM ) );
              mMap.getUiSettings()
                      .setMyLocationButtonEnabled( false );
              
            }
            
          }
        } );
        
      }
      
    } catch( SecurityException error ) {
      Log.e( TAG, "Exception: " + error.getMessage() );
    }
    
  }
  
  private void showKeeFreeLocation() {
    
    if( mKeefree != null ) {
  
      mKeefreeMarkerOptions = new MarkerOptions().position( new LatLng( mKeefree.getLatitude(), mKeefree.getLongitude() ) )
              .icon( BitmapDescriptorFactory.fromResource( R.drawable.keefree_map_icon ) )
              .title( Constants.KEEFREE_TITLE );
      mKeefreeMarker = mMap.addMarker( mKeefreeMarkerOptions );
      
    }
    
  }
  
  private void drawRouteToKeeFree() {
    
    LatLng defaultLatLon = Constants.DEFAULT_LOCATION;
    
    Routing routing = new Routing.Builder().travelMode( Routing.TravelMode.WALKING )
            .withListener( this )
            .alternativeRoutes( false )
            .waypoints( new LatLng( mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude() ), new LatLng( defaultLatLon.latitude, defaultLatLon.longitude ) )
            .build();
    
    routing.execute();
    
  }
  
  private void clearPolyLines() {
    
    for( Polyline polyline : mPolylines ) {
      polyline.remove();
    }
    
    mPolylines.clear();
    
  }
  
  @SuppressLint( "MissingPermission" )
  private void startLocationUpdates() {
    
    if( hasPermissions() ) {
      
      mRequestingLocationUpdates = true;
      mFusedLocationProviderClient.requestLocationUpdates( mLocationRequest, mLocationCallback, null );
      
    }
    
  }
  
  private void stopLocationUpdates() {
    
    mRequestingLocationUpdates = false;
    mFusedLocationProviderClient.removeLocationUpdates( mLocationCallback );
    
  }
  
  @OnClick( {R.id.goToGoogleMapsBackground, R.id.goToGoogleMapsText} )
  public void onGetDirectionsClick() {
    
    mVibrator.vibrate( VibrationEffect.createOneShot( 50, 100 ) );
    
    if( mKeefree == null ) {
      
      promptBasicDialog( getString( R.string.connect_to_keefree_title),
              getString( R.string.connect_to_keefree_body),
              Constants.Status.CONNECT_TO_KEEFREE );
      
    } else {
      
      // Creates an Intent that will load a map of the KEEFREE location
      
      Uri googleMapIntentUri = Uri.parse( "google.navigation:q=" + mKeefree.getLatitude() + "," + mKeefree.getLongitude() + "&mode=w&avoid=tf" );
      Intent mapIntent = new Intent( Intent.ACTION_VIEW, googleMapIntentUri );
      
      mapIntent.setPackage( "com.google.android.apps.maps" );
      
      startActivity( mapIntent );
      
    }
    
  }
  
  @OnClick( R.id.linkImage )
  public void onLinkImageClick() {
    
    mVibrator.vibrate( VibrationEffect.createOneShot( 50, 100 ) );
    
    if( !isBluetoothActive() && !hasPermissions() ) {
      requestPermissions();
      return;
    }
    
    if( mIsScanning ) {
      Utilities.promptSnackbar( this, "Already scanning..." );
      return;
    }
    
    if( mConnectionState == STATE_CONNECTED ) {
      Utilities.promptSnackbar( this, "Already connected..." );
      return;
    }
    
    if( isBluetoothActive() && hasPermissions() ) {
      scanForBLEDevice();
    } else {
      requestPermissions();
    }
    
  }
  
  @OnLongClick( R.id.linkImage )
  public boolean OnLinkImageLongClick() {
    
    if( mConnectionState == STATE_DISCONNECTED && !mIsInitialised ) {
      return true;
    }
    
    PopupMenu popupMenu = new PopupMenu( this, mLinkImage );
    popupMenu.getMenuInflater()
            .inflate( R.menu.popup, popupMenu.getMenu() );
    
    popupMenu.setOnMenuItemClickListener( new PopupMenu.OnMenuItemClickListener() {
      @Override
      public boolean onMenuItemClick( MenuItem item ) {
        
        switch( item.getItemId() ) {
          
          case R.id.popup_disconnect:
            disconnectGattServer();
            return true;
        }
        
        return false;
      }
    } );
    popupMenu.show();
    
    return true;
  }
  
  @OnClick( R.id.shieldImage )
  public void onShieldImageClick() {
    
    mVibrator.vibrate( VibrationEffect.createOneShot( 50, 100 ) );
    
    if( mConnectionState != STATE_CONNECTED && !mIsInitialised ) {
      return; // Do nothing... low opacity image should suggest so.
    }
    
    if( mConnectionState == STATE_CONNECTED && mIsInitialised && mIsSecurityOn ) {
      
      promptBasicDialog( getString( R.string.security_off_title ), getString( R.string.security_off_body ), Constants.Status.TURN_SECURITY_OFF );
      return;
    }
    
    showSecurityLoading();
    promptLoadingDialog();
    sendCharacter( Constants.SEND_SECURITY_ON );
  }
  
  @OnLongClick( R.id.keefreeButton )
  public boolean onKeefreeButtonLongClick() {
    
    if( mConnectionState == STATE_CONNECTED && mIsInitialised ) {
      
      promptLoadingDialog();
      sendCharacter( Constants.SEND_UNLOCK );
      
    } else {
      
      Utilities.promptSnackbar( this, "You are not connected..." );
      
    }
    
    return true;
  }
  
  private void sendCharacter( String message ) {
    
    BluetoothGattService service = mBluetoothGatt.getService( Constants.BLE_DEVICE_SERVICE_UUID );
    BluetoothGattCharacteristic characteristic = service.getCharacteristic( Constants.BLE_DEVICE_CHARACTERISTICS_UUID );
    
    byte[] messageBytes = new byte[0];
    
    try {
      messageBytes = message.getBytes( "UTF-8" );
    } catch( UnsupportedEncodingException error ) {
      error.printStackTrace();
    }
    
    characteristic.setValue( messageBytes );
    mBluetoothGatt.writeCharacteristic( characteristic );
    
  }
  
  private void disconnectGattServer() {
    
    mConnectionState = STATE_DISCONNECTED;
    mIsInitialised = false;
    
    if( mBluetoothGatt != null ) {
      
      /*
       * Once you call close() you are done.
       * If you want to connect again you will have to call connectGatt() on the BluetoothDevice again;
       * close() will release any resources held by BluetoothGatt.
       */
      mBluetoothGatt.disconnect();
      mBluetoothGatt.close();
      
      runOnUiThread( new Runnable() {
        @Override
        public void run() {
          showNotConnected();
          showSecurityOff();
          if( mConnectionState == STATE_DISCONNECTED )
            toggleShieldImage();
        }
      } );
      
      
    }
    
  }
  
  private void promptBasicDialog( String title, String body, Constants.Status status ) {
    
    DialogFragment dialog = new BasicDialogFragment( title, body, status );
    dialog.show( getSupportFragmentManager(), Constants.TAG_BASIC_DIALOG );
    
  }
  
  private void promptLoadingDialog() {
    
    mLoadingDialog = new LoadingDialogFragment();
    mLoadingDialog.show( getSupportFragmentManager(), Constants.TAG_LOADING_DIALOG );
    
  }
  
  private void toggleLockingImage() {
    
    if( mLockedStateImage.getVisibility() == View.VISIBLE ) {
      
      mLockedStateImage.setVisibility( View.INVISIBLE );
      mUnlockedStateImage.setVisibility( View.VISIBLE );
      
    } else {
      
      mLockedStateImage.setVisibility( View.VISIBLE );
      mUnlockedStateImage.setVisibility( View.INVISIBLE );
      
    }
    
  }
  
  private void toggleShieldImage() {
    
    if( mConnectionState == STATE_CONNECTED ) {
      
      mShieldImage.setImageResource( R.drawable.keefree_security );
      
    } else {
      
      mShieldImage.setImageResource( R.drawable.keefree_security_null );
      
    }
  }
  
  public void showScanLoading() {
    
    mConnectedProgressBar.setVisibility( View.VISIBLE );
    mConnectedImage.setVisibility( View.INVISIBLE );
    
  }
  
  public void showSecurityLoading() {
    
    mSecurityProgressBar.setVisibility( View.VISIBLE );
    mSecurityImage.setVisibility( View.INVISIBLE );
    
  }
  
  public void showNotConnected() {
    
    mConnectedProgressBar.setVisibility( View.INVISIBLE );
    mConnectedPrompt.setText( getString( R.string.ble_not_connected ) );
    mConnectedImage.setVisibility( View.VISIBLE );
    mConnectedImage.setImageResource( R.drawable.ic_circle_x );
    
  }
  
  public void showConnected() {
    
    mConnectedProgressBar.setVisibility( View.INVISIBLE );
    mConnectedPrompt.setText( getString( R.string.ble_connected ) );
    mConnectedImage.setVisibility( View.VISIBLE );
    mConnectedImage.setImageResource( R.drawable.ic_circle_check );
    
  }
  
  public void showSecurityOff() {
    
    mSecurityProgressBar.setVisibility( View.INVISIBLE );
    mSecurityPrompt.setText( getString( R.string.security_off ) );
    mSecurityImage.setVisibility( View.VISIBLE );
    mSecurityImage.setImageResource( R.drawable.ic_circle_x );
    
  }
  
  public void showSecurityOn() {
    
    mSecurityProgressBar.setVisibility( View.INVISIBLE );
    mSecurityPrompt.setText( getString( R.string.security_on ) );
    mSecurityImage.setVisibility( View.VISIBLE );
    mSecurityImage.setImageResource( R.drawable.ic_circle_check );
    
  }
  
  
  /**
   * INTERFACE METHODS
   */
  
  //region IBasicDialogFragment
  @Override
  public void onSecurityOffSelected() {
    
    showSecurityLoading();
    promptLoadingDialog();
    sendCharacter( Constants.SEND_SECURITY_OFF );
    
  }
  
  @Override
  public void onConnectToKeefreeSelected() {
    
    mBottomNavigationView.setSelectedItemId( R.id.bottom_navigation_keefree );
    
  }
  //endregion
  
  //region IListDialogFragment
  @Override
  public void onListDeviceSelected( BluetoothDevice device ) {
    
    mScannedDevicesDialog.dismiss();
    mConnectionState = STATE_CONNECTING;
    showScanLoading();
    
    // Connect device to Bluetooth Gatt
    // TODO: AUTO connection?
    mBluetoothGatt = device.connectGatt( this, false, mBluetoothGattCallback );
    
  }
  //endregion
  
  //region IActivityAdapter
  @Override
  public void onActivityListInfoButtonSelected( Record record ) {
    
    switch( record.getType() ) {
      
      case Constants.TYPE_APP_CODE:
        promptBasicDialog( "App Code Changed", "Code was changed to: " + record.getCode() + "\nOn the " + record.getTime(), Constants.Status.HAS_BLE_SUPPORT );
        break;
      
      case Constants.TYPE_PHONELESS_APP_CODE:
        
        break;
      
    }
    
  }
  //endregion
  
  //region RoutingListener
  @Override
  public void onRoutingFailure( RouteException e ) {
    
    // NOTE: 2,500 request per day is allows or you MUST PAY for more
    // Utilities.promptSnackbar( MainActivity.this, "Error: Cannot draw route. Google Directions API error." );
    
  }
  
  @Override
  public void onRoutingStart() {
  
  }
  
  @Override
  public void onRoutingSuccess( ArrayList<Route> routes, int shortestRouteIndex ) {
    
    runOnUiThread( new Runnable() {
      @Override
      public void run() {
        clearPolyLines();
      }
    } );
    
    mPolylines = new ArrayList<>();
    
    // In this case there will only be a route (alternatives = false). Add route(s) to the map.
    for( int i = 0; i < routes.size(); i++ ) {
      
      //In case of more than 5 alternative routes
      //int colorIndex = i % COLORS.length;
      
      PolylineOptions polyOptions = new PolylineOptions();
      polyOptions.color( getColor( R.color.colorBlack ) );
      polyOptions.width( 8f );
      polyOptions.addAll( routes.get( i ).getPoints() );
      
      Polyline polyline = mMap.addPolyline( polyOptions );
      mPolylines.add( polyline );
      
      Log.d( TAG, "Route " + (i + 1) + ": distance - " + routes.get( i ).getDistanceValue() + ": duration - " +
              routes.get( i ).getDurationValue() );
    }
    
  }
  
  @Override
  public void onRoutingCancelled() {
  
  }
  //endregion
}


