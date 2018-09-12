package com.paulodacaya.keefree.database;

import android.util.Log;

import com.paulodacaya.keefree.model.Record;

import java.util.List;

import io.realm.Realm;
import io.realm.RealmConfiguration;


/**
 * NOTES:
 *  * Writing to database will succeed/fail silently, however there will be debug log.
 */
public class Database {
  
  private static final String TAG = Database.class.getSimpleName();
  
  private static RealmConfiguration mRealmConfiguration;
  
  public static void writeRecord( final Realm realm, final Record record ) {
    
    realm.executeTransactionAsync( new Realm.Transaction() {
      @Override
      public void execute( Realm bgRealm ) {
      
        // NOTE: automatically handles begin/commit, and cancel
        
        Record newRecord = bgRealm.createObject( Record.class );
        newRecord.setType( record.getType() );
        newRecord.setTime( System.currentTimeMillis() );
        newRecord.setCode( record.getCode() );
        newRecord.setLatitude( record.getLatitude() );
        newRecord.setLongitude( record.getLongitude() );
      
      }
    }, new Realm.Transaction.OnSuccess() {
      @Override
      public void onSuccess() {

        Log.d( TAG, "Successfully added State to realm database." );
        
      }
    }, new Realm.Transaction.OnError() {
      @Override
      public void onError( Throwable error ) {

        Log.d( TAG, error.getMessage() );
  
      }
    } );
  
  }
  
  public static List<Record> getAllRecord( final Realm realm ) {
    
    List<Record> result = realm.where( Record.class ).findAll();
  
    return result;
  }
  
  public static RealmConfiguration getRealmConfiguration() {
    
    if( mRealmConfiguration == null )
    {
      mRealmConfiguration = new RealmConfiguration.Builder()
              .name( "keefree.realm" )
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
