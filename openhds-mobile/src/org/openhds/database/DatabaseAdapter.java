package org.openhds.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DatabaseAdapter {
	
	private static final String TAG = "DatabaseAdapter";
		
	private static final String DATABASE_NAME = "entityData";
	
	private static final String DATABASE_TABLE_INDIVIDUAL = "individual";
	private static final String INDIVIDUAL_UUID = "uuid";  
	private static final String INDIVIDUAL_EXTID = "extId";  
	private static final String INDIVIDUAL_FIRSTNAME = "firstName";  
	private static final String INDIVIDUAL_LASTNAME = "lastName";  
	private static final String INDIVIDUAL_DOB = "dob";  
	private static final String INDIVIDUAL_GENDER = "gender";
	private static final String INDIVIDUAL_MOTHER = "mother";  
	private static final String INDIVIDUAL_FATHER = "father";  
	
	private static final String DATABASE_TABLE_LOCATION = "location";
	private static final String LOCATION_UUID = "uuid";  
	private static final String LOCATION_EXTID = "extId";  
	private static final String LOCATION_NAME = "name";  
	private static final String LOCATION_LATITUDE = "latitude";  
	private static final String LOCATION_LONGITUDE = "longitude";  
	private static final String LOCATION_HIERARCHY = "hierarchy";	

	private static final String DATABASE_TABLE_SOCIALGROUP = "socialgroup";
	private static final String DATABASE_TABLE_VISIT = "visit";
	private static final String DATABASE_TABLE_RESIDENCY = "residency";
	private static final String DATABASE_TABLE_HIERARCHY = "hierarchy";
	private static final String DATABASE_TABLE_HIERARCHYLEVEL = "hierarchyLevel";
	 
	private static final int DATABASE_VERSION = 1;
	 
	private static final String INDIVIDUAL_CREATE =
	        "create table individual (uuid text primary key, " + 
	        "extId text not null, firstname text not null, lastname text not null, " +
	        "dob text not null, gender text not null, mother text not null, " +
	        "father text not null, foreign key(mother) references individual(uuid), " +
	        "foreign key(father) references individual(uuid));";
	
	private static final String LOCATION_CREATE =
        "create table location (uuid text primary key, " + 
        "extId text not null, name text not null, latitude text, " +
        "longitude text, hierarchy text not null);";
	 
	 private DatabaseHelper dbHelper;
	 private SQLiteDatabase database;
	 
	 public DatabaseAdapter(Context context) {
		 dbHelper = new DatabaseHelper(context);
	 }
	
	 public DatabaseAdapter open() throws SQLException {
	     database = dbHelper.getWritableDatabase();
	     return this;
	 }

	 public void close() {
		 dbHelper.close();
	     database.close();
	 }
	 
	 public long createIndividual(String uuid, String extId, String firstName, 
				String lastName, String dob, String gender, String mother, String father) {
		 		
		 ContentValues values = new ContentValues();
		 values.put(INDIVIDUAL_UUID, uuid);
		 values.put(INDIVIDUAL_EXTID, extId);
		 values.put(INDIVIDUAL_FIRSTNAME, firstName);
		 values.put(INDIVIDUAL_LASTNAME, lastName);
		 values.put(INDIVIDUAL_DOB, dob);
		 values.put(INDIVIDUAL_GENDER, gender);
		 values.put(INDIVIDUAL_MOTHER, mother);
		 values.put(INDIVIDUAL_FATHER, father);
		 Log.i(TAG, "inserting into individual with extId " + extId);
		 return database.insert(DATABASE_TABLE_INDIVIDUAL, null, values);	
	}
	 
	 public long createLocation(String uuid, String extId, String name, String latitude, 
			 String longitude, String hierarchy) {
		 
		 ContentValues values = new ContentValues();
		 values.put(LOCATION_UUID, uuid);
		 values.put(LOCATION_EXTID, extId);
		 values.put(LOCATION_NAME, name);
		 values.put(LOCATION_LONGITUDE, longitude);
		 values.put(LOCATION_LATITUDE, latitude);
		 values.put(LOCATION_HIERARCHY, hierarchy);
		 Log.i(TAG, "inserting into location with extId " + extId);
		 return database.insert(DATABASE_TABLE_LOCATION, null, values);
	 }
	 	 
	 public SQLiteDatabase getDatabase() {
		 return database;
	 }

	 public void setDatabase(SQLiteDatabase database) {
		 this.database = database;
	 }

	 private static class DatabaseHelper extends SQLiteOpenHelper {
				 	 
		 public DatabaseHelper(Context context) {
			 super(context, DATABASE_NAME, null, DATABASE_VERSION);
		 }
		 
		 @Override
		 public void onCreate(SQLiteDatabase db) {
			 db.execSQL(INDIVIDUAL_CREATE);
			 db.execSQL(LOCATION_CREATE);
		 }
	
		 @Override
		 public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			 db.execSQL("drop table if exists " + DATABASE_TABLE_INDIVIDUAL);
			 db.execSQL("drop table if exists " + DATABASE_TABLE_LOCATION);
		     onCreate(db);
		 }
	 }
}
