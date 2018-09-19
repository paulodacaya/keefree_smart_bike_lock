package com.paulodacaya.keefree.model;

import io.realm.RealmObject;
import io.realm.annotations.Ignore;

public class Keefree extends RealmObject {
  
  private int mId;
  private double mLatitude;
  private double mLongitude;
  private float mSpeed;
  private double Altitude;
  
  @Ignore
  public static final String ID = "mId";
  
  public Keefree() {
  }
  
  public int getId() {
    return mId;
  }
  
  public void setId( int id ) {
    mId = id;
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
  
  public float getSpeed() {
    return mSpeed;
  }
  
  public void setSpeed( float speed ) {
    mSpeed = speed;
  }
  
  public double getAltitude() {
    return Altitude;
  }
  
  public void setAltitude( double altitude ) {
    Altitude = altitude;
  }
}
