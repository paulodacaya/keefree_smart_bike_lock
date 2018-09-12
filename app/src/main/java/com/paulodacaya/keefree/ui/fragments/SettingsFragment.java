package com.paulodacaya.keefree.ui.fragments;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceScreen;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.widget.Toast;

import com.paulodacaya.keefree.R;

import io.realm.Realm;

public class SettingsFragment extends PreferenceFragment {
  
  Realm mRealm;
  
  public SettingsFragment() {
  }
  
  @SuppressLint( "ValidFragment" )
  public SettingsFragment( Realm realm ) {
    mRealm = realm;
  }
  
  @Override
  public void onCreate( @Nullable Bundle savedInstanceState ) {
    super.onCreate( savedInstanceState );
    
    // Load the settings preference from an XML resource
    addPreferencesFromResource( R.xml.preference );
    
//    // Handle App Record PreferenceScreen click
//    PreferenceScreen preferenceScreen = (PreferenceScreen) getPreferenceManager().findPreference( this.getString( R.string.pref_key_pin_code ) );
//    preferenceScreen.setOnPreferenceClickListener( new Preference.OnPreferenceClickListener() {
//      @Override
//      public boolean onPreferenceClick( Preference preference ) {
//
//        Toast.makeText( getActivity(), preference.getKey(), Toast.LENGTH_SHORT )
//                .show();
//
//        return false;
//      }
//    } );
    
  }
  
  @Override
  public boolean onPreferenceTreeClick( PreferenceScreen preferenceScreen, Preference preference ) {
    
    String appCodeKey = this.getString( R.string.pref_key_pin_code );
    String phonelessAccessKey = this.getString( R.string.pref_key_phoneless_pin_code );
  
    if( preference.getKey().equals( appCodeKey ) ) {
  
      // App Record
      ChangePinFragment changePinFragment = new ChangePinFragment( mRealm );
      getFragmentManager().beginTransaction()
              .replace( R.id.settingsContainer, changePinFragment )
              .addToBackStack( null )
              .commit();
      
    } else if( preference.getKey().equals( phonelessAccessKey ) ) {
  
      Toast.makeText( getActivity(), "YOLO", Toast.LENGTH_SHORT )
              .show();
    
    }
    
    
    return super.onPreferenceTreeClick( preferenceScreen, preference );
  }
}
