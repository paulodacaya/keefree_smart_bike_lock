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
  private double mLatitude;
  private double mLongitude;
  
  public Record() {
  }
  
  public Record( int type, long time, int code, double latitude, double longitude ) {
    mType = type;
    mTime = time;
    mCode = code;
    mLatitude = latitude;
    mLongitude = longitude;
  }
  
  public Record( int type, long time, int code ) {
    mType = type;
    mTime = time;
    mCode = code;
    mLatitude = 0.0;
    mLongitude = 0.0;
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
  
  public double getLatitude() {
    return mLatitude;
  }
  
  public void setLatitude( double latitude ) {
    mLatitude = latitude;
  }
  
  public double getLongitude() {
    return mLongitude;
  }
  
  public void setLongitude( double longitude ) {
    mLongitude = longitude;
  }
}
