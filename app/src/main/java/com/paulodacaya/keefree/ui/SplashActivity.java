package com.paulodacaya.keefree.ui;

/** Simple splash screen (loading screen) */

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.paulodacaya.keefree.R;
import com.paulodacaya.keefree.utilities.Constants;

import butterknife.ButterKnife;

public class SplashActivity extends AppCompatActivity {

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_splash);
    ButterKnife.bind( this );

    final Intent intent = new Intent( SplashActivity.this , LoginActivity.class );

    Thread timer = new Thread() {
      public void run() {
        try {
          sleep( Constants.SPLASH_DISPLAY_LENGTH );
        } catch(InterruptedException error) {
          error.printStackTrace();
        }
        finally {
          startActivity( intent );
          finish();
        }
      }
    };

    timer.start();
  }
}
