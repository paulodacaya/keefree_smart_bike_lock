package com.paulodacaya.keefree.ui.fragments;

import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.support.annotation.Nullable;

import com.paulodacaya.keefree.R;

public class SettingsFragment extends PreferenceFragment {
  
  @Override
  public void onCreate( @Nullable Bundle savedInstanceState ) {
    super.onCreate( savedInstanceState );
    
    // Load the settings preference from an XML resource
    addPreferencesFromResource( R.xml.preference );
  }
}
