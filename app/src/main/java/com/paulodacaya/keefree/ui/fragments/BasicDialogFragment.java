package com.paulodacaya.keefree.ui.fragments;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.paulodacaya.keefree.R;
import com.paulodacaya.keefree.utilities.Constants;

public class BasicDialogFragment extends DialogFragment {
  
  private String mTitle;
  private String mBody;
  
  private boolean mIsRequestingPermission = false;
  private boolean mHasBleSupport = false;
  
  public BasicDialogFragment() {
  }
  
  @SuppressLint( "ValidFragment" )
  public BasicDialogFragment( String title, String body, boolean isRequestingPermissions, boolean hasBleSupport ) {
    mTitle = title;
    mBody = body;
    mIsRequestingPermission = isRequestingPermissions;
    mHasBleSupport = hasBleSupport;
  }
  
  @NonNull
  @Override
  public Dialog onCreateDialog( Bundle savedInstanceState ) {
  
    AlertDialog.Builder builder = new AlertDialog.Builder( getActivity() );
    
    // Get the layout inflater
    LayoutInflater inflater = getActivity().getLayoutInflater();
  
    View dialogView = inflater.inflate( R.layout.fragment_basic_dialog, null );
    
    builder.setView( dialogView ).setPositiveButton( R.string.ok, new DialogInterface.OnClickListener() {
      @Override
      public void onClick( DialogInterface dialog, int which ) {
  
        // dismisses dialog automatically.
        
        if( mIsRequestingPermission ) {
          requestPermissions( new String[] { Manifest.permission.ACCESS_FINE_LOCATION}, Constants.REQUEST_PERMISSIONS_CODE );
        }
  
        if( !mHasBleSupport ) {
          getActivity().finish(); // exit activity!
        }
        
      }
    } );
    
    TextView title = dialogView.findViewById( R.id.basicDialogTitle );
    title.setText( mTitle );
    TextView body = dialogView.findViewById( R.id.basicDialogBody );
    body.setText( mBody );
    
    if( !mHasBleSupport ) {
      ImageView titleBackground = dialogView.findViewById( R.id.basicDialogTitleBackground );
      titleBackground.setColorFilter( getContext().getColor( R.color.colorWarningRed ) );
    }
    
    
    return builder.create();
  }
  
}
