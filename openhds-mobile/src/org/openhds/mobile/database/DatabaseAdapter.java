package org.openhds.mobile.database;

import java.util.ArrayList;
import java.util.List;

import org.openhds.mobile.model.Individual;
import org.openhds.mobile.model.Location;
import org.openhds.mobile.model.LocationHierarchy;
import org.openhds.mobile.model.Round;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
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
	private static final String INDIVIDUAL_RESIDENCE = "currentResidence";  
	
	private static final String DATABASE_TABLE_LOCATION = "location";
	private static final String LOCATION_UUID = "uuid";  
	private static final String LOCATION_EXTID = "extId";  
	private static final String LOCATION_NAME = "name";  
	private static final String LOCATION_LATITUDE = "latitude";  
	private static final String LOCATION_LONGITUDE = "longitude";  
	private static final String LOCATION_HIERARCHY = "hierarchy";	
	
	private static final String DATABASE_TABLE_HIERARCHY = "hierarchy";
	private static final String HIERARCHY_UUID = "uuid";  
	private static final String HIERARCHY_EXTID = "extId";  
	private static final String HIERARCHY_NAME = "name";  
	private static final String HIERARCHY_PARENT = "parent";  
	private static final String HIERARCHY_LEVEL = "level";
	
	private static final String DATABASE_TABLE_ROUND = "round";
	private static final String ROUND_UUID = "uuid";  
	private static final String ROUND_STARTDATE = "startDate";  
	private static final String ROUND_ENDDATE = "endDate";  
	private static final String ROUND_NUMBER = "roundNumber";  
	private static final String ROUND_REMARKS = "remarks"; 

	private static final String DATABASE_TABLE_SOCIALGROUP = "socialgroup";
	private static final String DATABASE_TABLE_VISIT = "visit";
	private static final String DATABASE_TABLE_RESIDENCY = "residency";
	private static final String DATABASE_TABLE_HIERARCHYLEVEL = "hierarchyLevel";
	 
	private static final int DATABASE_VERSION = 2;
	 
	private static final String INDIVIDUAL_CREATE =
	        "create table individual (uuid text primary key, " + 
	        "extId text not null, firstname text not null, lastname text not null, " +
	        "dob text not null, gender text not null, mother text not null, " +
	        "father text not null, currentResidence text not null, " +
	        "foreign key(mother) references individual(uuid), " +
	        "foreign key(father) references individual(uuid), " +
	        "foreign key(currentResidence) references location(uuid));";
	
	private static final String LOCATION_CREATE =
        "create table location (uuid text primary key, " + 
        "extId text not null, name text not null, latitude text, " +
        "longitude text, hierarchy text not null);";
	
	private static final String HIERARCHY_CREATE =
	        "create table hierarchy (uuid text primary key, " + 
	        "extId text not null, name text not null, parent text not null, " +
	        "level text not null, foreign key(parent) references hierarchy(uuid));";
	
	private static final String ROUND_CREATE =
	        "create table round (uuid text primary key, " + 
	        "startDate text not null, endDate text not null, roundNumber text not null, " +
	        "remarks text not null);";
	 
	private DatabaseHelper dbHelper;
	private SQLiteDatabase database;
	private Context context;
	 
	public DatabaseAdapter(Context context) {
		this.context = context;
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
				String lastName, String gender, String dob, String mother, String father, String residence) {
		 		
		 ContentValues values = new ContentValues();
		 values.put(INDIVIDUAL_UUID, uuid);
		 values.put(INDIVIDUAL_EXTID, extId);
		 values.put(INDIVIDUAL_FIRSTNAME, firstName);
		 values.put(INDIVIDUAL_LASTNAME, lastName);
		 values.put(INDIVIDUAL_DOB, dob);
		 values.put(INDIVIDUAL_GENDER, gender);
		 values.put(INDIVIDUAL_MOTHER, mother);
		 values.put(INDIVIDUAL_FATHER, father);
		 values.put(INDIVIDUAL_RESIDENCE, residence);
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
	 
	 public long createHierarchy(String uuid, String extId, String name, String parent, 
			 String level) {
		 
		 ContentValues values = new ContentValues();
		 values.put(HIERARCHY_UUID, uuid);
		 values.put(HIERARCHY_EXTID, extId);
		 values.put(HIERARCHY_NAME, name);
		 values.put(HIERARCHY_PARENT, parent);
		 values.put(HIERARCHY_LEVEL, level);
		 Log.i(TAG, "inserting into hierarchy with extId " + extId);
		 return database.insert(DATABASE_TABLE_HIERARCHY, null, values);
	 }
	 
	 public long createRound(String uuid, String startDate, String endDate, String roundNumber, 
			 String remarks) {
		 
		 ContentValues values = new ContentValues();
		 values.put(ROUND_UUID, uuid);
		 values.put(ROUND_STARTDATE, startDate);
		 values.put(ROUND_ENDDATE, endDate);
		 values.put(ROUND_NUMBER, roundNumber);
		 values.put(ROUND_REMARKS, remarks);
		 Log.i(TAG, "inserting into round with roundNumber " + roundNumber);
		 return database.insert(DATABASE_TABLE_ROUND, null, values);
	 }
	 
	 public List<LocationHierarchy> getAllRegions(String levelName) {
		 open();
		 List<LocationHierarchy> regions = new ArrayList<LocationHierarchy>();
		 
		 String query = "select * from hierarchy where level = '" + levelName + "';";
		 Cursor cursor = database.rawQuery(query, null);
		 
		 if (cursor.moveToFirst()) {
			 do {
				 LocationHierarchy region = new LocationHierarchy();
				 region.setUuid(cursor.getString(0));
				 region.setExtId(cursor.getString(1));
				 region.setName(cursor.getString(2));
				 region.setParent(cursor.getString(3));
				 region.setLevel(cursor.getString(4));
				 regions.add(region);
			 } while (cursor.moveToNext());
		 }
		 cursor.close();
		 close();
		 return regions;
	 } 	 
	 
	 public List<LocationHierarchy> getAllSubRegionsOfRegion(LocationHierarchy region) {
		 open();
		 List<LocationHierarchy> subRegions = new ArrayList<LocationHierarchy>();
		 
		 String query = "select * from hierarchy where parent = ?;";
		 Cursor cursor = database.rawQuery(query, new String[] {region.getUuid()});
		 
		 if (cursor.moveToFirst()) {
			 do {
				 LocationHierarchy subRegion = new LocationHierarchy();
				 subRegion.setUuid(cursor.getString(0));
				 subRegion.setExtId(cursor.getString(1));
				 subRegion.setName(cursor.getString(2));
				 subRegion.setParent(cursor.getString(3));
				 subRegion.setLevel(cursor.getString(4));
				 subRegions.add(subRegion);
			 } while (cursor.moveToNext());
		 }
		 cursor.close();
		 close();
		 return subRegions;
	 } 	 
	 
	 public List<Location> getAllLocationsOfVillage(LocationHierarchy village) {
		 open();
		 List<Location> locations = new ArrayList<Location>();
		 
		 String query = "select * from location where hierarchy = ?;";
		 Cursor cursor = database.rawQuery(query, new String[] {village.getUuid()});
		 
		 if (cursor.moveToFirst()) {
			 do {
				 Location loc = new Location();
				 loc.setUuid(cursor.getString(0));
				 loc.setExtId(cursor.getString(1));
				 loc.setName(cursor.getString(2));
				 loc.setLatitude(cursor.getString(3));
				 loc.setLongitude(cursor.getString(4));
				 loc.setHierarchy(cursor.getString(5));
				 locations.add(loc);
			 } while (cursor.moveToNext());
		 }
		 cursor.close();
		 close();
		 return locations;
	 } 	 
	  
	 public List<Individual> getIndividualsAtLocation(Location location) {
		 open();
		 List<Individual> individuals = new ArrayList<Individual>();
		 
		 String query = "select * from individual where currentResidence = ?;"; 
		 Cursor cursor = database.rawQuery(query, new String[] {location.getUuid()});
		 
		 if (cursor.moveToFirst()) {
			 do {
				 Individual individual = new Individual();
				 individual.setUuid(cursor.getString(0));
				 individual.setExtId(cursor.getString(1));
				 individual.setFirstName(cursor.getString(2));
				 individual.setLastName(cursor.getString(3));
				 individual.setDob(cursor.getString(4));
				 individual.setGender(cursor.getString(5));
				 individual.setMother(cursor.getString(6));
				 individual.setFather(cursor.getString(7));
				 individual.setCurrentResidence(cursor.getString(8));
				 individuals.add(individual);
			 } while (cursor.moveToNext());
		 }
		 cursor.close();
		 close(); 
		 return individuals;
	 }
	 
	 public List<Round> getAllRounds() {
		 open();
		 List<Round> rounds = new ArrayList<Round>();
		 
		 String query = "select * from round;";
		 Cursor cursor = database.rawQuery(query, null);
		 
		 if (cursor.moveToFirst()) {
			 do {
				 Round round = new Round();
				 round.setUuid(cursor.getString(0));
				 round.setStartDate(cursor.getString(1));
				 round.setEndDate(cursor.getString(2));
				 round.setRoundNumber(cursor.getString(3));
				 round.setRemarks(cursor.getString(4));
				 rounds.add(round);
			 } while (cursor.moveToNext());
		 }
		 cursor.close();
		 close(); 
		 return rounds;
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
			 db.execSQL(HIERARCHY_CREATE);
			 db.execSQL(ROUND_CREATE);
		 }
		 	
		 @Override
		 public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			 db.execSQL("drop table if exists " + DATABASE_TABLE_INDIVIDUAL);
			 db.execSQL("drop table if exists " + DATABASE_TABLE_LOCATION);
			 db.execSQL("drop table if exists " + DATABASE_TABLE_HIERARCHY);
			 db.execSQL("drop table if exists " + DATABASE_TABLE_ROUND);
		     onCreate(db);
		 }
	 }
}
