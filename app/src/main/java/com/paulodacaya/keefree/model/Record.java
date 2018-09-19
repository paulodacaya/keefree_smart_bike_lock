package com.paulodacaya.keefree.model;

import android.annotation.SuppressLint;

import com.paulodacaya.keefree.utilities.Constants;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import io.realm.RealmObject;

public class Record extends RealmObject {
  
  private int mType; // App code or Phone-less Access Record
  private long mTime;
  private int mCode;
  
  public Record() {
  }
  
  
  public Record( int type, int code ) {
    mType = type;
    mTime = System.currentTimeMillis();
    mCode = code;
  }
  
  public Record( int type ) {
    mType = type;
    mTime = System.currentTimeMillis();
    mCode = 0;
  }
  
  public String getTime() {
  
    @SuppressLint( "SimpleDateFormat" ) SimpleDateFormat simpleDateFormat = new SimpleDateFormat( "dd/MM/yyyy 'at' hh:mm a" );
    simpleDateFormat.setTimeZone( TimeZone.getTimeZone( Constants.TIMEZONE_ID ) );
  
    Date date = new Date( mTime );
  
    return simpleDateFormat.format( date );
  }
  
  public void setTime( long time ) {
    mTime = time;
  }
  
  public int getType() {
    return mType;
  }
  
  public void setType( int type ) {
    mType = type;
  }
  
  public int getCode() {
    return mCode;
  }
  
  public void setCode( int code ) {
    mCode = code;
  }
  
}
