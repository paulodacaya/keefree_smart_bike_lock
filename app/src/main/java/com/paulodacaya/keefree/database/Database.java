package com.paulodacaya.keefree.database;

import android.util.Log;

import com.paulodacaya.keefree.model.Keefree;
import com.paulodacaya.keefree.model.Record;

import java.util.List;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmResults;
import io.realm.Sort;


/**
 * NOTES:
 * * Writing to database will succeed/fail silently, however there will be debug log.
 */
public class Database {
  
  private static final String TAG = Database.class.getSimpleName();
  private static RealmConfiguration mRealmConfiguration;
  
  public static void writeRecord( final Realm realm, final Record record ) {
    
    realm.executeTransaction( new Realm.Transaction() {
      
      @Override
      public void execute( Realm realm ) {
        
        Record newRecord = realm.createObject( Record.class );
        newRecord.setType( record.getType() );
        newRecord.setTime( System.currentTimeMillis() );
        newRecord.setCode( record.getCode() );
        
      }
      
    } );
    
  }
  
  public static void writeKeefreeLocation( final Realm realm, final double latitude, final double longitude ) {
    
    realm.executeTransaction( new Realm.Transaction() {
      @Override
      public void execute( Realm realm ) {
  
        Keefree keefree = getKeefree( realm );
  
        if( keefree == null ) {
    
          Keefree newKeefree = realm.createObject( Keefree.class );
          newKeefree.setLatitude( latitude );
          newKeefree.setLongitude( longitude );
    
        } else {
  
          keefree.setLatitude( latitude );
          keefree.setLongitude( longitude );
    
        }
        
      }
    } );
    
  }
  
  public static void writeKeefreeSpeed( final Realm realm, final float speed ) {
  
    realm.executeTransaction( new Realm.Transaction() {
      @Override
      public void execute( Realm realm ) {
  
        Keefree keefree = getKeefree( realm );
        
        if( keefree == null ) {
          
          Keefree newKeefree = realm.createObject( Keefree.class );
          newKeefree.setSpeed( speed );
          
        } else {
          
          keefree.setSpeed( speed );
          
        }
        
      }
    } );
  
  }
  
  public static void writeKeefreeAltitude( final Realm realm, final double altitude ) {
  
    realm.executeTransaction( new Realm.Transaction() {
      @Override
      public void execute( Realm realm ) {
      
        Keefree keefree = getKeefree( realm );
  
        if( keefree == null ) {
    
          Keefree newKeefree = realm.createObject( Keefree.class );
          newKeefree.setAltitude( altitude );
    
        } else {
    
          keefree.setAltitude( altitude );
    
        }
      
      }
    } );
  
  }
  
  public static List<Record> getAllRecord( final Realm realm ) {
    
    List<Record> result = realm.where( Record.class ).sort( "mTime", Sort.DESCENDING ).findAll();
    
    return result;
  }
  
  public static Keefree getKeefree( final Realm realm ) {
  
    Keefree keefree = realm.where( Keefree.class ).equalTo( Keefree.ID, 1 ).findFirst();
  
    if( keefree == null ) {
      
      return null;
    
    } else {
      
      return keefree;
    
    }
    
  }
  
  public static RealmConfiguration getRealmConfiguration() {
    
    if( mRealmConfiguration == null ) {
      mRealmConfiguration = new RealmConfiguration.Builder().name( "keefree.realm" )
              .deleteRealmIfMigrationNeeded() // development purposes only
              //.readOnly() // Makes database readonly
              //.inMemory() // Realm entirely loaded in memory
              .build();
    }
    
    return mRealmConfiguration;
  }
  
  public static void logRealmFilePath() {
    
    Realm realm = Realm.getInstance( getRealmConfiguration() );
    Log.d( TAG, "Realm path -> " + realm.getPath() );
    realm.close();
    
  }
}
