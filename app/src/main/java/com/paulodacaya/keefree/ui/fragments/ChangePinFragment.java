package com.paulodacaya.keefree.ui.fragments;

import android.app.Fragment;
import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.transition.Slide;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.paulodacaya.keefree.R;
import com.paulodacaya.keefree.database.Database;
import com.paulodacaya.keefree.model.Code;
import com.paulodacaya.keefree.model.State;
import com.paulodacaya.keefree.utilities.Constants;
import com.paulodacaya.keefree.utilities.Utilities;

import java.time.Instant;

import butterknife.BindColor;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ChangePinFragment extends Fragment {
  
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
  
  @Nullable
  @Override
  public View onCreateView( LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState ) {
    
    View view = inflater.inflate( R.layout.fragment_changepin, container, false );
    ButterKnife.bind( this, view );
    
    handlePinCode();
    setupTransitions();
    
    return view;
  }
  
  private void setupTransitions() {
    
      Slide slide = new Slide( Gravity.END );
      slide.excludeTarget( android.R.id.statusBarBackground, true );
      slide.excludeTarget( android.R.id.navigationBarBackground, true );
      slide.excludeTarget( android.R.id.background, true );
      
      getActivity().getWindow().setEnterTransition( slide );
      
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
          storedPinCode = Utilities.getSharedPreferencePinCode( getActivity() );
          
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
        
        Utilities.setSharedPreferencePinCode( getActivity(), reEnteredPinCode ); // Save to shared preferences
        
        Snackbar.make( getView(), "Pin code has successfully changed.", Snackbar.LENGTH_LONG )
                .show();
        
        mPromptLabel.setText( R.string.successful_pin_code_change_prompt );
        mPromptLabel.setTextColor( successGreen );
        
        // Store in database
        Code code = new Code( Instant.now().toEpochMilli(), Constants.APP_CODE );
        Database.writeCode( code );
        
        resetPinCodeFields();
        Utilities.hideKeyboard( getActivity() );
        
      } else {
        
        resetPinCodeFields();
        isChangingPassword = true;
        mPromptLabel.setText( R.string.try_again_prompt );
        
      }
    }
  }
  
  
}
