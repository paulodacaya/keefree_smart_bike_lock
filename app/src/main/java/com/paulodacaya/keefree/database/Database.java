package com.paulodacaya.keefree.database;

import android.util.Log;

import com.paulodacaya.keefree.model.Code;
import com.paulodacaya.keefree.model.State;
import com.paulodacaya.keefree.utilities.Constants;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmResults;


/**
 * NOTES:
 *  * Writing to database will succeed/fail silently, however there will be debug log.
 */
public class Database {
  
  private static final String tag = Database.class.getSimpleName();
  
  public static void writeState( final State state ) {
  
    try( Realm realm = Realm.getInstance( getRealmConfiguration() ) ) {
    
      realm.executeTransactionAsync( new Realm.Transaction() {
        @Override
        public void execute( Realm bgRealm ) {
        
          // NOTE: automatically handles begin/commit, and cancel
          
          State newState = bgRealm.createObject( State.class );
          newState.setTime( state.getTime() );
          newState.setLocked( state.isLocked() );
          newState.setLatitude( state.getLatitude() );
          newState.setLongitude( state.getLongitude() );
        
        }
      }, new Realm.Transaction.OnSuccess() {
        @Override
        public void onSuccess() {
  
          Log.d( tag, "Successfully added State to realm database." );
          
        }
      }, new Realm.Transaction.OnError() {
        @Override
        public void onError( Throwable error ) {
  
          Log.d( tag, error.getMessage() );
    
        }
      } );
    }
  
  }
  
  public static List<State> getAllState() {
    
    List<State> states = new ArrayList<>();
  
    try( Realm realm = Realm.getInstance( getRealmConfiguration() ) ) {
  
      RealmResults<State> results = realm.where( State.class )
              .findAll();
      
      if( results.size() != 0 ) {
        for( State result : results ) {
          states.add( result );
        }
      }
      
    }
  
    return states;
  }
  
  public static void writeCode( final Code code ) {
  
    try( Realm realm = Realm.getInstance( getRealmConfiguration() ) ) {
    
      realm.executeTransactionAsync( new Realm.Transaction() {
        @Override
        public void execute( Realm bgRealm ) {
        
          // NOTE: automatically handles begin/commit, and cancel
        
          Code newCode = bgRealm.createObject( Code.class );
          newCode.setTime( code.getTime() );
          newCode.setType( code.getType() );
        
        }
      }, new Realm.Transaction.OnSuccess() {
        @Override
        public void onSuccess() {
        
          if( code.getType().equals( Constants.APP_CODE ) )
            Log.d( tag, "Successfully added new app code to realm database." );
          else if( code.getType().equals( Constants.PHONELESS_ACCESS_CODE ) )
            Log.d( tag, "Successfully added new phone-less access code to realm database." );
          
        }
      }, new Realm.Transaction.OnError() {
        @Override
        public void onError( Throwable error ) {
  
          Log.d( tag, error.getMessage() );
          
        }
      } );
    }
  
  }
  
  public static List<Code> getAllCode() {
    
    List<Code> codes = new ArrayList<>();
    
    try( Realm realm = Realm.getInstance( getRealmConfiguration() ) ) {
      
      RealmResults<Code> results = realm.where( Code.class )
              .findAll();
      
      if( results.size() != 0 ) {
        for( Code result : results ) {
          codes.add( result );
        }
      }
    }
    
    return codes;
  }
  
  private static RealmConfiguration getRealmConfiguration() {
    
    RealmConfiguration config = new RealmConfiguration.Builder()
            .name( "keefree.realm" )
            .deleteRealmIfMigrationNeeded() // development purposes only
            //.readOnly() // Makes database readonly
            //.inMemory() // Realm entirely loaded in memory
            .build();
    
    return config;
  }
  
  public static void logRealmFilePath() {
    
    try ( Realm realm = Realm.getInstance( getRealmConfiguration() ) ) {
      Log.d( tag, "Realm path - " + realm.getPath() );
    }
    
  }
}
