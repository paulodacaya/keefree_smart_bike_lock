package com.paulodacaya.keefree.ui;

/** Simple splash screen (loading screen) */

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.transition.Explode;
import android.view.View;
import android.widget.ImageView;

import com.paulodacaya.keefree.R;
import com.paulodacaya.keefree.utilities.Constants;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SplashActivity extends AppCompatActivity {
  
  @BindView( R.id.keefreeLogoImage ) ImageView mKeefreeLogoImage;
  
  @Override
  protected void onCreate( Bundle savedInstanceState ) {
    super.onCreate( savedInstanceState );
    setContentView( R.layout.activity_splash );
    ButterKnife.bind( this );
  
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
    final ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(
            this,
            mKeefreeLogoImage,
            getResources().getString( R.string.logo_transition_name )
    );
    
    // Delay splash screen for 1 second
    new Handler().postDelayed( new Runnable() {
      @Override
      public void run() {
        startActivity( intent, options.toBundle() );
      }
    }, Constants.SPLASH_DISPLAY_LENGTH );
  }
}
