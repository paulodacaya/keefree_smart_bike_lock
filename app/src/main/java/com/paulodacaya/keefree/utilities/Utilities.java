package com.paulodacaya.keefree.utilities;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.paulodacaya.keefree.R;

public class Utilities {
  
  // Shared preferences
  public static final String getSharedPreferencePinCode( Context context ) {
    
    SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences( context );
    
    String value = sharedPreferences.getString( context.getString( R.string.pref_key_pin_code ),
            context.getString( R.string.pref_default_pin_code ) );
    
    return value;
  }
  
  public static final void setSharedPreferencePinCode( Context context, String newPinCode ) {
    
    SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences( context );
    SharedPreferences.Editor editor = sharedPreferences.edit();
    
    editor.putString( context.getString( R.string.pref_key_pin_code ), newPinCode ).apply();
  }
}
