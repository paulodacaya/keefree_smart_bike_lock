package com.paulodacaya.keefree.model;

import io.realm.RealmObject;

public class State extends RealmObject {
  
  private long mTime;
  private boolean mIsLocked;
  private double mLatitude;
  private double mLongitude;
  
  
  public long getTime() {
    
    // TODO: Retrieve as a string formatted correctly, use SimpleDateFormat.
    
    return mTime;
  }
  
  public void setTime( long time ) {
    this.mTime = time;
  }
  
  public boolean isLocked() {
    return mIsLocked;
  }
  
  public void setLocked( boolean locked ) {
    mIsLocked = locked;
  }
  
  public double getLatitude() {
    return mLatitude;
  }
  
  public void setLatitude( double latitude ) {
    this.mLatitude = latitude;
  }
  
  public double getLongitude() {
    return mLongitude;
  }
  
  public void setLongitude( double longitude ) {
    this.mLongitude = longitude;
  }
}
