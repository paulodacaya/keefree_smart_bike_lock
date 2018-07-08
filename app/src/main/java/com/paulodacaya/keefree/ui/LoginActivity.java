package com.paulodacaya.keefree.ui;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
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
  @BindView( R.id.changePinCodeLabel ) TextView mChangePinCodeLabel;
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
          Intent intent = new Intent( LoginActivity.this, MainActivity.class );
          startActivity( intent );
          finish();
        } else {
          mPinCodeLabel.setText( R.string.wrong_pin_warning_message );
          mPinCodeLabel.setTextColor( warningRed );
          clearPinCodeFields();
          mPinCode1.requestFocus();
        }
      }
    } );
  }
  
  // TODO: Handle reset when user taps/clicks on one edit texts
  private void resetEditTextFields() {
    clearPinCodeFields();
    mPinCode1.requestFocus();
  }
  
  private void clearPinCodeFields() {
    mPinCode1.getText().clear();
    mPinCode2.getText().clear();
    mPinCode3.getText().clear();
    mPinCode4.getText().clear();
  }
  
  private void setupTransitions() {
    
//    Fade fade = new Fade();
//    getWindow().setEnterTransition( fade );
  }
  
  @OnClick( R.id.changePinCodeLabel )
  public void onChangePinCodeLabelClick() {
    Intent intent = new Intent( LoginActivity.this, ChangePinActivity.class );
    ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation( this );
    
    startActivity( intent, options.toBundle() );
  }
}
