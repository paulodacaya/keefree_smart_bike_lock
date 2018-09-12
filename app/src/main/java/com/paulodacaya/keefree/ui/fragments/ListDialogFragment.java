package com.paulodacaya.keefree.ui.fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.DialogInterface;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.paulodacaya.keefree.R;
import com.paulodacaya.keefree.databinding.BluetoothLeListItemBinding;
import com.paulodacaya.keefree.model.BleDevice;
import com.paulodacaya.keefree.ui.MainActivity;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * What you can get out of BluetoothDevice:
 *
 * BluetoothDevice: Name and address
 * RSSI: Received signal strength indication
 * Advertisement Flags: Discoverable mode and cababilities of the device
 * Manufacturer Specific Data: Info useful when filtering
 * GATT Service UUIDs
 */

public class ListDialogFragment extends DialogFragment {
  
  // Interface to handle clicks
  public interface onDeviceSelectedInterface {
    void onListDeviceSelected( BluetoothDevice device );
  }
  
  private Set<BluetoothDevice> mBluetoothDevices;
  
  public ListDialogFragment() {
    mBluetoothDevices = new HashSet<>(  );
  }
  
  @NonNull
  @Override
  public Dialog onCreateDialog( Bundle savedInstanceState ) {
    
    // Create lister for interface
    onDeviceSelectedInterface listener = (onDeviceSelectedInterface) getActivity();
    
    AlertDialog.Builder builder = new AlertDialog.Builder( getActivity() );
    
    // Get the layout inflater
    LayoutInflater inflater = getActivity().getLayoutInflater();
    
    View dialogView = inflater.inflate( R.layout.fragment_list_dialog, null );
    
    builder.setView( dialogView ).setNegativeButton( getString( R.string.cancel ), new DialogInterface.OnClickListener() {
      @Override
      public void onClick( DialogInterface dialog, int which ) {
        // Dismiss dialog automatically
      }
    } );
    
    AlertDialog dialog = builder.create();
    
    // Bind to Model (copy it over to model class)
    List<BleDevice> bleDevices = new ArrayList<>(  );
    for( BluetoothDevice device : mBluetoothDevices ) {
      BleDevice bleDevice = new BleDevice( device );
      bleDevices.add( bleDevice );
    }
    
    // Initialise recycler view
    RecyclerView listDialogRecyclerView = dialogView.findViewById( R.id.listDialogRecyclerView );
    
    // TODO: change to data binding library?? Currently having issues...
    // BluetoothLeListItemBinding binding = DataBindingUtil.setContentView( getActivity(), R.layout.fragment_list_dialog );
    
    ScanResultAdapter adapter = new ScanResultAdapter( getContext(), listener, bleDevices );
    Log.d( MainActivity.TAG, adapter.getItemCount() + "" );
    
    // Get RecyclerView from binding library
    listDialogRecyclerView.setAdapter( adapter );
    listDialogRecyclerView.setHasFixedSize( true );
    listDialogRecyclerView.addItemDecoration( new DividerItemDecoration( getContext(), DividerItemDecoration.VERTICAL ) ); // adds divider lines
    listDialogRecyclerView.setLayoutManager( new LinearLayoutManager( getContext() ) );
    
    return dialog;
  }
  
  public void addDevice( BluetoothDevice device ) {
    mBluetoothDevices.add( device );
  }
  
  public void clearDevices() {
    mBluetoothDevices.clear();
  }
  
  public int getDevicesCount() {
    return mBluetoothDevices.size();
  }
  
}

/**
 * Recycler View Adapter for List Dialog
 * Using DataBinding Library
 */
class ScanResultAdapter extends RecyclerView.Adapter<ScanResultAdapter.ViewHolder> {
  
  private Context mContext;
  private ListDialogFragment.onDeviceSelectedInterface mListener;
  private List<BleDevice> mBleDevices;
  

  ScanResultAdapter( Context context, ListDialogFragment.onDeviceSelectedInterface listener, List<BleDevice> bleDevices ) {
    mContext = context;
    mListener = listener;
    mBleDevices = bleDevices;
  }

  @NonNull
  @Override
  public ViewHolder onCreateViewHolder( @NonNull ViewGroup parent, int viewType ) {

    BluetoothLeListItemBinding binding = DataBindingUtil.inflate(
            LayoutInflater.from( parent.getContext() ),
            R.layout.bluetooth_le_list_item,
            parent,
            false
    );

    return new ViewHolder( binding.getRoot() );
  }

  @Override
  public void onBindViewHolder( @NonNull ViewHolder holder, int position ) {
    BleDevice bleDevice = mBleDevices.get( position );
    holder.bluetoothListItemBinding.setBleDevice( bleDevice );
  }

  @Override
  public int getItemCount() {
    return mBleDevices.size();
  }
  
  
  class ViewHolder extends RecyclerView.ViewHolder {

    // binding variables that are being used inside the view
    BluetoothLeListItemBinding bluetoothListItemBinding;
  
    ViewHolder( View itemView ) {
      super( itemView );
      
      bluetoothListItemBinding = DataBindingUtil.bind( itemView );
      
      itemView.setOnClickListener( new View.OnClickListener() {
        @Override
        public void onClick( View view ) {
  
          // Pass to listener
          BluetoothDevice device = bluetoothListItemBinding.getBleDevice().getBluetoothDevice();
          mListener.onListDeviceSelected( device );
          
        }
      } );
      
    }
    
  }

}
