package com.paulodacaya.keefree.ui;

/** Simple splash screen (loading screen) */

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;

import com.paulodacaya.keefree.R;
import com.paulodacaya.keefree.database.Database;
import com.paulodacaya.keefree.utilities.Constants;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.Realm;

public class SplashActivity extends AppCompatActivity {
  
  @BindView( R.id.keefreeLogoImage ) ImageView mKeefreeLogoImage;
  
  private static final String tag = SplashActivity.class.getSimpleName();
  
  @Override
  protected void onCreate( Bundle savedInstanceState ) {
    super.onCreate( savedInstanceState );
    setContentView( R.layout.activity_splash );
    ButterKnife.bind( this );
  
    // Set shared preferences default values
    PreferenceManager.setDefaultValues( this, R.xml.preference, false );
    
    // Initialize Realm (only done once) and log path
    Realm.init( getApplicationContext() );
    Database.logRealmFilePath();
    
    setupTransitions();
    startDelay();
  }
  
  @Override
  protected void onStop() {
    super.onStop();
    finish();
  }
  
  private void setupTransitions() {
  }
  
  private void startDelay() {
    
    final Intent intent = new Intent( SplashActivity.this, LoginActivity.class );
    final ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation( SplashActivity.this );
    
    // Delay splash screen for 1 second
    new Handler().postDelayed( new Runnable() {
      @Override
      public void run() {
        startActivity( intent, options.toBundle() );
      }
    }, Constants.SPLASH_DISPLAY_LENGTH );
    
  }
  
}
