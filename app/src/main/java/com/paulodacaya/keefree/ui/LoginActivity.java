package com.paulodacaya.keefree.ui;

import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.transition.Fade;
import android.transition.Slide;
import android.view.Gravity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import com.paulodacaya.keefree.R;
import com.paulodacaya.keefree.utilities.Utilities;

import butterknife.BindColor;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class LoginActivity extends AppCompatActivity {
  
  @BindView( R.id.pinCodeLabel ) TextView mPinCodeLabel;
  @BindView( R.id.clearPinCodeLabel ) TextView mClearPinCodeLabel;
  @BindView( R.id.pinCode1 ) EditText mPinCode1;
  @BindView( R.id.pinCode2 ) EditText mPinCode2;
  @BindView( R.id.pinCode3 ) EditText mPinCode3;
  @BindView( R.id.pinCode4 ) EditText mPinCode4;
  
  @BindColor( R.color.colorWarningRed ) int warningRed;
  
  @Override
  protected void onCreate( Bundle savedInstanceState ) {
    super.onCreate( savedInstanceState );
    setContentView( R.layout.activity_login );
    ButterKnife.bind( this );
    
    handlePinCode();
    setupTransitions();
  }
  
  @Override
  protected void onStop() {
    super.onStop();
    finish(); // allow transition to complete before removing
  }
  
  private void handlePinCode() {
    /** Pin 1 */
    mPinCode1.addTextChangedListener( new TextWatcher() {
      @Override
      public void beforeTextChanged( CharSequence s, int start, int count, int after ) {
      }
    
      @Override
      public void onTextChanged( CharSequence s, int start, int before, int count ) {
      }
    
      @Override
      public void afterTextChanged( Editable s ) {
        if( s.length() == 1 ) {
          mPinCode2.requestFocus();
        }
      }
    } );
  
    /** Pin 2 */
    mPinCode2.addTextChangedListener( new TextWatcher() {
      @Override
      public void beforeTextChanged( CharSequence s, int start, int count, int after ) {
      
      }
    
      @Override
      public void onTextChanged( CharSequence s, int start, int before, int count ) {
      
      }
    
      @Override
      public void afterTextChanged( Editable s ) {
        if( s.length() == 1 ) {
          mPinCode3.requestFocus();
        }
      }
    } );
  
    /** Pin 3 */
    mPinCode3.addTextChangedListener( new TextWatcher() {
      @Override
      public void beforeTextChanged( CharSequence s, int start, int count, int after ) {
      
      }
    
      @Override
      public void onTextChanged( CharSequence s, int start, int before, int count ) {
      
      }
    
      @Override
      public void afterTextChanged( Editable s ) {
        if( s.length() == 1 ) {
          mPinCode4.requestFocus();
        }
      }
    } );
  
    /** Pin 4 */
    mPinCode4.addTextChangedListener( new TextWatcher() {
      @Override
      public void beforeTextChanged( CharSequence s, int start, int count, int after ) {
      
      }
    
      @Override
      public void onTextChanged( CharSequence s, int start, int before, int count ) {
      
      }
    
      @Override
      public void afterTextChanged( Editable s ) {
        
        /** Validate PIN and start next Activity if successful */
        
        String inputPinCode = mPinCode1.getText().toString()
                + mPinCode2.getText() + mPinCode3.getText() + mPinCode4.getText();
        
        String storedPinCode = Utilities.getSharedPreferencePinCode( LoginActivity.this );
        
        if( inputPinCode.equals( storedPinCode ) ) {
  
          Utilities.hideKeyboard( LoginActivity.this );
          
          // Send user to onBoarding Activity if its users first time
          if( Utilities.getSharedPreferenceIsFirstTimeUser( LoginActivity.this ).equals( getString( R.string.pref_is_first_time_user_true ) ) ) {
  
            Intent intent = new Intent( LoginActivity.this, OnBoardingActivity.class );
            ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation( LoginActivity.this );
            startActivity( intent, options.toBundle() );
          
          } else {
  
            Intent intent = new Intent( LoginActivity.this, OnBoardingActivity.class );
            ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation( LoginActivity.this );
            startActivity( intent, options.toBundle() );
            
//            Intent intent = new Intent( LoginActivity.this, MainActivity.class );
//            startActivity( intent );
            
          }
          
        } else {
          
          mPinCodeLabel.setText( R.string.wrong_pin_warning_message );
          mPinCodeLabel.setTextColor( warningRed );
          clearPinCodeFields();
          mPinCode1.requestFocus();
          
        }
        
      }
      
    } );
    
  }
  
  private void clearPinCodeFields() {
    
    mPinCode1.getText().clear();
    mPinCode2.getText().clear();
    mPinCode3.getText().clear();
    mPinCode4.getText().clear();
    
  }
  
  private void setupTransitions() {
    
    Fade fade = new Fade();
    fade.excludeTarget( android.R.id.navigationBarBackground, true );
    fade.excludeTarget( android.R.id.statusBarBackground, true );
    fade.setDuration( 800l ); // 1 second
    
    getWindow().setEnterTransition( fade );
    getWindow().setExitTransition( null );
    getWindow().setReenterTransition( null );
    getWindow().setReturnTransition( null );
    
  }
  
  @OnClick( R.id.clearPinCodeLabel )
  public void onClearPinCodeLabelClick() {
    
    clearPinCodeFields();
    mPinCode1.requestFocus();
    
  }
}
