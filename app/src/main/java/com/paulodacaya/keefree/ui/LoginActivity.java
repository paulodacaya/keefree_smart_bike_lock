package com.paulodacaya.keefree.ui;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.TextView;

import com.paulodacaya.keefree.R;

import butterknife.BindColor;
import butterknife.BindView;
import butterknife.ButterKnife;

public class LoginActivity extends AppCompatActivity {

  @BindView( R.id.pinCodeLabel ) TextView mPinCodeLabel;
  @BindView( R.id.pinCode1 ) EditText mPinCode1;
  @BindView( R.id.pinCode2 ) EditText mPinCode2;
  @BindView( R.id.pinCode3 ) EditText mPinCode3;
  @BindView( R.id.pinCode4 ) EditText mPinCode4;

  @BindColor( R.color.colorWarningRed ) int warningRed;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_login);
    ButterKnife.bind( this );

    /** Pin 1 */
    mPinCode1.addTextChangedListener(new TextWatcher() {
      @Override
      public void beforeTextChanged(CharSequence s, int start, int count, int after) {

      }

      @Override
      public void onTextChanged(CharSequence s, int start, int before, int count) {

      }

      @Override
      public void afterTextChanged(Editable s) {
        if( s.length() == 1 ) {
          mPinCode2.requestFocus();
        }
      }
    });

    /** Pin 2 */
    mPinCode2.addTextChangedListener(new TextWatcher() {
      @Override
      public void beforeTextChanged(CharSequence s, int start, int count, int after) {

      }

      @Override
      public void onTextChanged(CharSequence s, int start, int before, int count) {

      }

      @Override
      public void afterTextChanged(Editable s) {
        if( s.length() == 1 ) {
          mPinCode3.requestFocus();
        }
      }
    });


    /** Pin 3 */
    mPinCode3.addTextChangedListener(new TextWatcher() {
      @Override
      public void beforeTextChanged(CharSequence s, int start, int count, int after) {

      }

      @Override
      public void onTextChanged(CharSequence s, int start, int before, int count) {

      }

      @Override
      public void afterTextChanged(Editable s) {
        if( s.length() == 1 ) {
          mPinCode4.requestFocus();
        }
      }
    });

    /** Pin 4 */
    mPinCode4.addTextChangedListener(new TextWatcher() {
      @Override
      public void beforeTextChanged(CharSequence s, int start, int count, int after) {

      }

      @Override
      public void onTextChanged(CharSequence s, int start, int before, int count) {

      }

      @Override
      public void afterTextChanged(Editable s) {
        /** Validate PIN and start next Activity if successful */
        String pinCode = mPinCode1.getText().toString() + mPinCode2.getText() +
                mPinCode3.getText() + mPinCode4.getText();

        if( !pinCode.equals( "1234" ) ) {
          mPinCodeLabel.setText( R.string.wrong_pin_warning_message );
          mPinCodeLabel.setTextColor( warningRed );
          clearEditTextFields();
          mPinCode1.requestFocus();
        } else {
          startActivity( new Intent( LoginActivity.this, MainActivity.class ));
        }
      }
    });
  }

  // TODO: Handle reset when user taps/clicks on one od edit texts
  private void resetEditTextFields() {
    clearEditTextFields();
    mPinCode1.requestFocus();
  }

  private void clearEditTextFields() {
    mPinCode1.getText().clear();
    mPinCode2.getText().clear();
    mPinCode3.getText().clear();
    mPinCode4.getText().clear();
  }
}
