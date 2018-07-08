package com.paulodacaya.keefree.utilities;

import android.content.Context;
import android.content.SharedPreferences;

public class Utilities {
  
  // Shared preferences
  public static final String getSharedPreferencePinCode( Context context ) {
    SharedPreferences sharedPreferences = context.getSharedPreferences(
            Constants.SETTINGS_SHARED_PREFERENCES, Context.MODE_PRIVATE );
    
    return sharedPreferences.getString( Constants.PIN_CODE, "1234" );
  }
}
