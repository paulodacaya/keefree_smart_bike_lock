package com.paulodacaya.keefree.ui.fragments;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import com.paulodacaya.keefree.R;
import com.paulodacaya.keefree.utilities.Constants;

public class LoadingDialogFragment extends DialogFragment {
  
  Constants.Status mStatus;
  
  public LoadingDialogFragment() {
    mStatus = Constants.Status.NORMAL;
  }
  
  @SuppressLint( "ValidFragment" )
  public LoadingDialogFragment( Constants.Status status ) {
    mStatus = status;
  }
  
  @NonNull
  @Override
  public Dialog onCreateDialog( Bundle savedInstanceState ) {
  
    AlertDialog.Builder builder = new AlertDialog.Builder( getActivity() );
  
    // Get the layout inflater
    LayoutInflater inflater = getActivity().getLayoutInflater();
    
    View dialogView = inflater.inflate( R.layout.fragment_loading_dialog, null );
  
    // Different loading text when unlocking device
    if( mStatus == Constants.Status.UNLOCKING_KEEFREE ) {
      
      TextView textView = dialogView.findViewById( R.id.loadingTextView );
      textView.setText( R.string.unlocking_loading_dialog_text );
      
    }
    
    builder.setView( dialogView );
    
    AlertDialog dialog = builder.create();
    dialog.setCancelable( false );
    dialog.setCanceledOnTouchOutside( false );
    
    return dialog;
  }
  
}
