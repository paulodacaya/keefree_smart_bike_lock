package com.paulodacaya.keefree.utilities;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.paulodacaya.keefree.R;

import io.realm.RealmConfiguration;

public class Utilities {
  
  // Shared preferences
  public static String getSharedPreferencePinCode( Context context ) {
    
    SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences( context );
    
    String value = sharedPreferences.getString( context.getString( R.string.pref_key_pin_code ),
            context.getString( R.string.pref_default_pin_code ) );
    
    return value;
  }
  
  public static void setSharedPreferencePinCode( Context context, String newPinCode ) {
    
    SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences( context );
    SharedPreferences.Editor editor = sharedPreferences.edit();
    
    editor.putString( context.getString( R.string.pref_key_pin_code ), newPinCode ).apply();
  }
  
  public static void hideKeyboard( Activity activity ) {
    
    InputMethodManager inputMethodManager = (InputMethodManager) activity.getSystemService( Context.INPUT_METHOD_SERVICE );
    
    //Find the currently focused view, so we can grab the correct window token from it
    View view = activity.getCurrentFocus();
    
    //If no view currently has focus, create a new one, just so we can grab a window token from it
    if( view == null ) {
      view = new View( activity );
    }
    
    if( inputMethodManager != null )
      inputMethodManager.hideSoftInputFromWindow( view.getWindowToken(), 0 );
    
  }
}
