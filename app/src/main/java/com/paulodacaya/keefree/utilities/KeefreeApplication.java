package com.paulodacaya.keefree.utilities;

import android.app.Activity;
import android.app.Application;
import android.content.pm.ActivityInfo;
import android.os.Bundle;

/**
 * This execute whenever any activity in your app is started (or stopped, or resumed, or whatever)
 * so it's a good place to set all activity to PORTRAIT mode. One place to rule them all.
 * This class is set in the AndroidManifest.xml under root application.
 */

public class KeefreeApplication extends Application {
  
  @Override
  public void onCreate() {
    super.onCreate();
    
    registerActivityLifecycleCallbacks( new ActivityLifecycleCallbacks() {
      @Override
      public void onActivityCreated( Activity activity, Bundle savedInstanceState ) {
        
        // this get called every time an activity is created, it forces portrait orientation ONLY.
        activity.setRequestedOrientation( ActivityInfo.SCREEN_ORIENTATION_PORTRAIT );
      }
      
      @Override
      public void onActivityStarted( Activity activity ) {
      
      }
      
      @Override
      public void onActivityResumed( Activity activity ) {
      
      }
      
      @Override
      public void onActivityPaused( Activity activity ) {
      
      }
      
      @Override
      public void onActivityStopped( Activity activity ) {
      
      }
      
      @Override
      public void onActivitySaveInstanceState( Activity activity, Bundle outState ) {
      
      }
      
      @Override
      public void onActivityDestroyed( Activity activity ) {
      
      }
    } );
  }
}
