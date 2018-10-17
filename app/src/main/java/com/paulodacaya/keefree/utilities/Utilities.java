package com.paulodacaya.keefree.utilities;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.preference.PreferenceManager;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.text.Layout;
import android.view.Gravity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;
import android.widget.Toast;

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
  
  public static String getSharedPreferenceIsFirstTimeUser( Context context ) {
    
    SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences( context );
    
    String value = sharedPreferences.getString( context.getString( R.string.pref_key_is_first_time_user ), context.getString( R.string.pref_is_first_time_user_true ) );
    
    return value;
  }
  
  public static void setSharedPreferencePinCode( Context context, String newPinCode ) {
    
    SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences( context );
    SharedPreferences.Editor editor = sharedPreferences.edit();
    
    editor.putString( context.getString( R.string.pref_key_pin_code ), newPinCode ).apply();
  }
  
  public static void setSharedPreferenceIsFirstTimeUserToFalse( Context context ) {
    
    SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences( context );
    SharedPreferences.Editor editor = sharedPreferences.edit();
    
    editor.putString( context.getString( R.string.pref_key_is_first_time_user ), context.getString( R.string.pref_is_first_time_user_false ) ).apply();
  }
  
  public static void promptSnackbar( Activity activity, String message ) {
    
    View bottomNavigationView = activity.findViewById( R.id.bottomNavigation );
    
    Snackbar snackbar = Snackbar.make( activity.findViewById( R.id.rootLayout ), message, Snackbar.LENGTH_LONG );
    
    View snackbarView = snackbar.getView();
    snackbarView.setBackgroundColor( activity.getColor( R.color.colorAccentDark ) );
    snackbarView.setMinimumHeight( bottomNavigationView.getHeight() );
    
    TextView snackBarText = snackbarView.findViewById( android.support.design.R.id.snackbar_text );
    snackBarText.setHeight( bottomNavigationView.getHeight() );
    snackBarText.setTypeface( Typeface.createFromAsset( activity.getAssets(), "alegreya_sans_medium.ttf" ) );
    snackBarText.setTextSize( 16f );
    //snackBarText.setTextAlignment( View.TEXT_ALIGNMENT_CENTER );
    snackBarText.setTextColor( activity.getColor( R.color.colorWhite ) );
    snackBarText.setGravity( Gravity.CENTER_VERTICAL );
    
    snackbar.show();
    
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
