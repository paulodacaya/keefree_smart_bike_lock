package com.paulodacaya.keefree.model;

import io.realm.RealmObject;

public class Code extends RealmObject {
  
  private long mTime;
  private String mType; // App code or Phone-less Access Code
  
  public Code() {
  }
  
  public Code( long time, String type ) {
    mTime = time;
    mType = type;
  }
  
  public long getTime() {
    return mTime;
  }
  
  public void setTime( long time ) {
    this.mTime = time;
  }
  
  public String getType() {
    return mType;
  }
  
  public void setType( String type ) {
    this.mType = type;
  }
}
