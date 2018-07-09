package com.paulodacaya.keefree.ui;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.transition.Slide;
import android.view.Gravity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.paulodacaya.keefree.R;
import com.paulodacaya.keefree.utilities.Constants;
import com.paulodacaya.keefree.utilities.Utilities;

import butterknife.BindColor;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ChangePinActivity extends AppCompatActivity {
  
  @BindView( R.id.clearButton ) Button mClearButton;
  @BindView( R.id.confirmButton ) Button mConfirmButton;
  @BindView( R.id.promptLabel ) TextView mPromptLabel;
  @BindView( R.id.changePinCode1 ) EditText mChangePinCode1;
  @BindView( R.id.changePinCode2 ) EditText mChangePinCode2;
  @BindView( R.id.changePinCode3 ) EditText mChangePinCode3;
  @BindView( R.id.changePinCode4 ) EditText mChangePinCode4;
  
  @BindColor( R.color.colorWarningRed ) int warningRed;
  @BindColor( R.color.colorPrimaryDark ) int primaryDark;
  @BindColor( R.color.colorSuccessGreen ) int successGreen;
  
  boolean isChangingPassword = false;
  boolean isReenteringNewPassword = false;
  boolean isConfirmButtonActive = false;
  
  String inputPinCode;
  String reEnteredPinCode;
  String storedPinCode;
  
  @Override
  protected void onCreate( Bundle savedInstanceState ) {
    super.onCreate( savedInstanceState );
    setContentView( R.layout.activity_change_pin );
    ButterKnife.bind( this );
  
    // Set shared preferences default values
    PreferenceManager.setDefaultValues( this, R.xml.preference, false );
    
    setupTransitions();
    handlePinCode();
    setActionBar();
  }
  
  private void handlePinCode() {
    /** Pin 1 */
    mChangePinCode1.addTextChangedListener( new TextWatcher() {
      @Override
      public void beforeTextChanged( CharSequence s, int start, int count, int after ) {
      }
      
      @Override
      public void onTextChanged( CharSequence s, int start, int before, int count ) {
      }
      
      @Override
      public void afterTextChanged( Editable s ) {
        if( s.length() == 1 ) {
          mChangePinCode2.requestFocus();
        }
      }
    } );
    
    /** Pin 2 */
    mChangePinCode2.addTextChangedListener( new TextWatcher() {
      @Override
      public void beforeTextChanged( CharSequence s, int start, int count, int after ) {
      
      }
      
      @Override
      public void onTextChanged( CharSequence s, int start, int before, int count ) {
      
      }
      
      @Override
      public void afterTextChanged( Editable s ) {
        if( s.length() == 1 ) {
          mChangePinCode3.requestFocus();
        }
      }
    } );
    
    /** Pin 3 */
    mChangePinCode3.addTextChangedListener( new TextWatcher() {
      @Override
      public void beforeTextChanged( CharSequence s, int start, int count, int after ) {
      
      }
      
      @Override
      public void onTextChanged( CharSequence s, int start, int before, int count ) {
      
      }
      
      @Override
      public void afterTextChanged( Editable s ) {
        if( s.length() == 1 ) {
          mChangePinCode4.requestFocus();
        }
      }
    } );
    
    /** Pin 4 */
    mChangePinCode4.addTextChangedListener( new TextWatcher() {
      @Override
      public void beforeTextChanged( CharSequence s, int start, int count, int after ) {
      
      }
      
      @Override
      public void onTextChanged( CharSequence s, int start, int before, int count ) {
      
      }
      
      @Override
      public void afterTextChanged( Editable s ) {
        
        if( !isChangingPassword ) {
          
          /** Validating with stored password */
          
          inputPinCode = getInputPinCode();
          storedPinCode = Utilities.getSharedPreferencePinCode( ChangePinActivity.this );
          
          // If stored pin code and input pin code match
          if( inputPinCode.equals( storedPinCode ) ) {
            
            clearPinCodeFields();
            mChangePinCode1.requestFocus();
            isChangingPassword = true;
            mPromptLabel.setText( R.string.successful_pin_code_prompt );
            mPromptLabel.setTextColor( primaryDark );
            mPromptLabel.setTypeface( null, Typeface.BOLD );
            
          } else {
            
            clearPinCodeFields();
            mChangePinCode1.requestFocus();
            mPromptLabel.setText( R.string.wrong_pin_warning_message );
            mPromptLabel.setTextColor( warningRed );
            
          }
          
        } else {
          
          if( !isReenteringNewPassword ) {
            
            /** Storing new password  */
            mPromptLabel.setText( R.string.reenter_pin_code_prompt );
            
            inputPinCode = getInputPinCode();
            
            isReenteringNewPassword = true;
            clearPinCodeFields();
            mChangePinCode1.requestFocus();
            
          } else {
            
            /** Activate confirm button to finalise */
            mConfirmButton.setAlpha( 1f );
            isConfirmButtonActive = true;
          }
        }
      }
    } );
  }
  
  private void clearPinCodeFields() {
    mChangePinCode1.getText().clear();
    mChangePinCode2.getText().clear();
    mChangePinCode3.getText().clear();
    mChangePinCode4.getText().clear();
  }
  
  private void resetPinCodeFields() {
    clearPinCodeFields();
    mChangePinCode1.requestFocus();
    mConfirmButton.setAlpha( 0.1f );
    isReenteringNewPassword = false;
    isChangingPassword = false;
    isConfirmButtonActive = false;
  }
  
  private String getInputPinCode() {
    return mChangePinCode1.getText().toString() + mChangePinCode2.getText() +
            mChangePinCode3.getText() + mChangePinCode4.getText();
  }
  
  private void setupTransitions() {
    Slide slide = new Slide( Gravity.END );
    slide.excludeTarget( android.R.id.statusBarBackground, true );
    slide.excludeTarget( android.R.id.navigationBarBackground, true );
    slide.excludeTarget( android.R.id.background, true );
    getWindow().setEnterTransition( slide );
  }
  
  @OnClick( R.id.clearButton )
  public void onClearButtonClick() {
    clearPinCodeFields();
    mChangePinCode1.requestFocus();
  }
  
  @OnClick( R.id.confirmButton )
  public void onConfirmButtonClick() {
    
    if( isConfirmButtonActive ) {
      
      reEnteredPinCode = getInputPinCode();
      
      if( inputPinCode.equals( reEnteredPinCode ) ) {
        
        Utilities.setSharedPreferencePinCode( this, reEnteredPinCode ); // Save to shared preferences
        
        Toast.makeText( ChangePinActivity.this,
                "PIN CODE HAS BEEN CHANGED", Toast.LENGTH_SHORT )
                .show();
        
        mPromptLabel.setText( R.string.successful_pin_code_change_prompt );
        mPromptLabel.setTextColor( successGreen );
        
        resetPinCodeFields();
        hideKeyboard();
        
      } else {
        
        resetPinCodeFields();
        isChangingPassword = true;
        mPromptLabel.setText( R.string.try_again_prompt );
        
      }
    }
  }
  
  private void hideKeyboard() {
    
    InputMethodManager imm = (InputMethodManager) this.getSystemService( INPUT_METHOD_SERVICE );
    
    //Find the currently focused view, so we can grab the correct window token from it.
    View view = this.getCurrentFocus();
    
    //If no view currently has focus, create a new one, just so we can grab a window token from it
    if( view == null ) {
      view = new View( this );
    }
  
    if( imm != null )
      imm.hideSoftInputFromWindow( view.getWindowToken(), 0 );
  }
  
  private void setActionBar() {
    getSupportActionBar().setDisplayOptions( ActionBar.DISPLAY_SHOW_CUSTOM );
    getSupportActionBar().setCustomView( R.layout.custom_actionbar_layout );
  
    View view = getSupportActionBar().getCustomView();
    TextView titleTextView = view.findViewById( R.id.actionBarText );
    titleTextView.setText( this.getString( R.string.title_activity_settings ) );
  }
}
