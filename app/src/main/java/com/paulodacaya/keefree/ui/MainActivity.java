package com.paulodacaya.keefree.ui;

import android.location.LocationManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.transition.Slide;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.paulodacaya.keefree.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnLongClick;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback {
  
  @BindView( R.id.bottomNavigation ) BottomNavigationView mBottomNavigationView;
  @BindView( R.id.mapContainer ) ConstraintLayout mMapContainer;
  @BindView( R.id.keefreeContainer ) ConstraintLayout mKeefeeContainer;
  @BindView( R.id.settingsContainer ) ConstraintLayout mSettingsContainer;
  @BindView( R.id.lockedStateImage ) ImageView mLockedStateImage;
  @BindView( R.id.unlockedStateImage ) ImageView mUnlockedStateImage;
  
  private GoogleMap mMap;
  private LocationManager mLocationManager; // used to manage location
  
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
  
  @Override
  protected void onCreate( Bundle savedInstanceState ) {
    super.onCreate( savedInstanceState );
    setContentView( R.layout.activity_main );
    ButterKnife.bind( this );
  
    setActionBar();
    setupTransitions();
    
    // GOOGLE MAPS
    SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById( R.id.map );
    mapFragment.getMapAsync( this );
    
    // BOTTOM NAVIGATION
    mBottomNavigationView.setOnNavigationItemSelectedListener( mOnNavigationItemSelectedListener );
    toggleContent( R.id.bottom_navigation_keefree );
    
    // Set shared preferences default values
    PreferenceManager.setDefaultValues( this, R.xml.preference, false );
    
    // TODO: Modify for Bluetooth module integration
    mUnlockedStateImage.setVisibility( View.INVISIBLE );
  }
  
  private void setupTransitions() {
    Slide slide = new Slide( Gravity.BOTTOM );
//    slide.excludeTarget( android.R.id.statusBarBackground, true );
//    slide.excludeTarget( android.R.id.navigationBarBackground, true );
//    slide.excludeTarget( android.R.id.background, true );
    getWindow().setEnterTransition( slide );
  }
  
  private void toggleContent( int id ) {
    switch( id ) {
      case R.id.bottom_navigation_keefree:
        setActionBarTitle( "KEEFREE" );
        
        mKeefeeContainer.setVisibility( View.VISIBLE );
        mMapContainer.setVisibility( View.INVISIBLE );
        mSettingsContainer.setVisibility( View.INVISIBLE );
        break;
      
      case R.id.bottom_navigation_location:
        setActionBarTitle( "LOCATION" );
  
        mMapContainer.setVisibility( View.VISIBLE );
        mKeefeeContainer.setVisibility( View.INVISIBLE );
        mSettingsContainer.setVisibility( View.INVISIBLE );
        break;
      
      case R.id.bottom_navigation_activity:
        setActionBarTitle( "ACTIVITY" );
        
        mKeefeeContainer.setVisibility( View.INVISIBLE );
        mMapContainer.setVisibility( View.INVISIBLE );
        mSettingsContainer.setVisibility( View.INVISIBLE );
        break;
      
      case R.id.bottom_navigation_settings:
        setActionBarTitle( "SETTINGS" );
  
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
  
  @Override
  public void onMapReady( GoogleMap googleMap ) {
    mMap = googleMap;
    mLocationManager = (LocationManager) this.getSystemService( LOCATION_SERVICE ); // instantiate LocationManager
    
    // Add a marker in Sydney and move the camera
    LatLng sydney = new LatLng( -37.843964, 144.968183 );
    mMap.addMarker( new MarkerOptions().position( sydney )
            .title( "Bike location" ) );
    mMap.moveCamera( CameraUpdateFactory.newLatLngZoom( sydney, 4 ) );
  }
  
  // toggle unlocked and locked state
  @OnLongClick( R.id.keefreeButton )
  public boolean toggleUnlockedLocked() {
    if( mLockedStateImage.getVisibility() == View.VISIBLE ) {
      mLockedStateImage.setVisibility( View.INVISIBLE );
      mUnlockedStateImage.setVisibility( View.VISIBLE );
    } else {
      mLockedStateImage.setVisibility( View.VISIBLE );
      mUnlockedStateImage.setVisibility( View.INVISIBLE );
    }
    return true;
  }
}
