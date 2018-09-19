package com.paulodacaya.keefree.ui;

import android.app.Activity;
import android.databinding.DataBindingUtil;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.paulodacaya.keefree.R;
import com.paulodacaya.keefree.databinding.RecordListItemBinding;
import com.paulodacaya.keefree.model.Record;

import java.util.List;

public class ActivityAdapter extends RecyclerView.Adapter<ActivityAdapter.ViewHolder> {
  
  // Interface to info button click
  public interface IActivityAdapter {
    void onActivityListInfoButtonSelected( Record record );
  }
  
  private Activity mActivity;
  private List<Record> mRecords;
  
  public ActivityAdapter( Activity activity, List<Record> records ) {
    mActivity = activity;
    mRecords = records;
  }
  
  @NonNull
  @Override
  public ViewHolder onCreateViewHolder( @NonNull ViewGroup parent, int viewType ) {
    
    RecordListItemBinding codeBindings = DataBindingUtil.inflate(
            LayoutInflater.from( parent.getContext() ),
            R.layout.record_list_item,
            parent,
            false
    );
    
    
    return new ViewHolder( mActivity, codeBindings.getRoot() );
  }
  
  @Override
  public void onBindViewHolder( @NonNull ViewHolder holder, int position ) {
    
    Record record = mRecords.get( position );
    holder.recordListItemBinding.setRecord( record );
    
  }
  
  @Override
  public int getItemCount() {
    return mRecords.size();
  }
  
  
  public class ViewHolder extends RecyclerView.ViewHolder {
  
    RecordListItemBinding recordListItemBinding;
    Activity activity;
  
    public ViewHolder( Activity activity, View itemView ) {
      super( itemView );
      recordListItemBinding = DataBindingUtil.bind( itemView );
      this.activity = activity;
      final IActivityAdapter listener = (IActivityAdapter) activity;
      
      // Handle info button click
      Button infoButton = itemView.findViewById( R.id.recordInfoButton );
      infoButton.setOnClickListener( new View.OnClickListener() {
        @Override
        public void onClick( View v ) {
  
          Record record = recordListItemBinding.getRecord();
          listener.onActivityListInfoButtonSelected( record );
    
        }
      } );
      
    }
  
  }

}

