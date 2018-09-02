package com.paulodacaya.keefree.ui.fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;

import com.paulodacaya.keefree.R;

public class LoadingDialogFragment extends DialogFragment {
  
  public LoadingDialogFragment() {
  }
  
  @NonNull
  @Override
  public Dialog onCreateDialog( Bundle savedInstanceState ) {
  
    AlertDialog.Builder builder = new AlertDialog.Builder( getActivity() );
  
    // Get the layout inflater
    LayoutInflater inflater = getActivity().getLayoutInflater();
    
    View dialogView = inflater.inflate( R.layout.fragment_loading_dialog, null );
    
    builder.setView( dialogView );
    
    AlertDialog dialog = builder.create();
    dialog.setCancelable( false );
    dialog.setCanceledOnTouchOutside( false );
    
    return dialog;
  }
  
}
