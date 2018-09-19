package com.paulodacaya.keefree.ui.fragments;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.paulodacaya.keefree.R;
import com.paulodacaya.keefree.utilities.Constants;

public class BasicDialogFragment extends DialogFragment {
  
  public interface IBasicDialogFragment {
    void onSecurityOffSelected();
    void onConnectToKeefreeSelected();
  }
  
  private String mTitle;
  private String mBody;
  
  private Constants.Status mStatus;
  
  public BasicDialogFragment() {
  }
  
  @SuppressLint( "ValidFragment" )
  public BasicDialogFragment( String title, String body, Constants.Status status ) {
    mTitle = title;
    mBody = body;
    mStatus = status;
  }
  
  @NonNull
  @Override
  public Dialog onCreateDialog( Bundle savedInstanceState ) {
    
    final IBasicDialogFragment listener = (IBasicDialogFragment) getActivity();
  
    AlertDialog.Builder builder = new AlertDialog.Builder( getActivity() );
    
    // Get the layout inflater
    LayoutInflater inflater = getActivity().getLayoutInflater();
  
    View dialogView = inflater.inflate( R.layout.fragment_basic_dialog, null );
    
    builder.setView( dialogView ).setPositiveButton( getString( R.string.ok ), new DialogInterface.OnClickListener() {
      @Override
      public void onClick( DialogInterface dialog, int which ) {
        
        switch( mStatus )
        {
          case REQUESTING_PERMISSION:
            requestPermissions( new String[] { Manifest.permission.ACCESS_FINE_LOCATION }, Constants.REQUEST_PERMISSIONS_CODE );
            break;
          
          case NO_BLE_SUPPORT:
            getActivity().finish(); // exit activity!
            break;
            
          case TURN_SECURITY_OFF:
            listener.onSecurityOffSelected();
            break;
            
          case CONNECT_TO_KEEFREE:
            listener.onConnectToKeefreeSelected();
            break;
        }
        
      }
    } );
    
    if( mStatus == Constants.Status.TURN_SECURITY_OFF || mStatus == Constants.Status.REQUESTING_PERMISSION ) {
      builder.setNegativeButton( R.string.cancel, new DialogInterface.OnClickListener() {
        @Override
        public void onClick( DialogInterface dialog, int which ) {
          // dismisses dialog automatically.
        }
      } );
    }
    
    TextView title = dialogView.findViewById( R.id.basicDialogTitle );
    title.setText( mTitle );
    TextView body = dialogView.findViewById( R.id.basicDialogBody );
    body.setText( mBody );
    
    if( mStatus == Constants.Status.NO_BLE_SUPPORT ) {
      ImageView titleBackground = dialogView.findViewById( R.id.basicDialogTitleBackground );
      titleBackground.setColorFilter( getContext().getColor( R.color.colorWarningRed ) );
    }
  
    AlertDialog dialog = builder.create();
    
    dialog.setOnKeyListener( new DialogInterface.OnKeyListener() {
      @Override
      public boolean onKey( DialogInterface dialog, int keyCode, KeyEvent event ) {
  
        // Prevent dialog close on back press button
        return keyCode == KeyEvent.KEYCODE_BACK;
      }
    } );
    
    dialog.setCancelable( false );
    dialog.setCanceledOnTouchOutside( false );
    
    return dialog;
  }
  
}
