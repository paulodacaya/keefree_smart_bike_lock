package com.paulodacaya.keefree.ui;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.paulodacaya.keefree.R;
import com.paulodacaya.keefree.databinding.RecordListItemBinding;
import com.paulodacaya.keefree.model.Record;

import java.util.List;

public class ActivityAdapter extends RecyclerView.Adapter<ActivityAdapter.ViewHolder> {
  
  private Context mContext;
  private List<Record> mRecords;
  
  public ActivityAdapter( Context context, List<Record> records ) {
    mContext = context;
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
    
    return new ViewHolder( codeBindings.getRoot() );
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
  
    public ViewHolder( View itemView ) {
      super( itemView );
      recordListItemBinding = DataBindingUtil.bind( itemView );
    }
  
  }

}

