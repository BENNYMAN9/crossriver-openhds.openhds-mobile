package org.openhds.mobile.database;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.openhds.mobile.model.FieldWorker;
import org.openhds.mobile.model.FormSubmissionRecord;
import org.openhds.mobile.model.Individual;
import org.openhds.mobile.model.Location;
import org.openhds.mobile.model.LocationHierarchy;
import org.openhds.mobile.model.Relationship;
import org.openhds.mobile.model.Round;
import org.openhds.mobile.model.SocialGroup;
import org.openhds.mobile.model.Supervisor;
import org.openhds.mobile.model.Visit;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
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
	private static final String INDIVIDUAL_STATUS = "status";
	
	private static final String DATABASE_TABLE_LOCATION = "location";
	private static final String LOCATION_UUID = "uuid";  
	private static final String LOCATION_EXTID = "extId";  
	private static final String LOCATION_HEAD = "head";
	private static final String LOCATION_NAME = "name"; 
	private static final String LOCATION_LATITUDE = "latitude";  
	private static final String LOCATION_LONGITUDE = "longitude";  
	private static final String LOCATION_HIERARCHY = "hierarchy";	
	private static final String LOCATION_STATUS = "status";
	
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
	
	private static final String DATABASE_TABLE_VISIT = "visit";
	private static final String VISIT_UUID = "uuid";  
	private static final String VISIT_EXTID = "extId";  
	private static final String VISIT_ROUND = "round";  
	private static final String VISIT_DATE = "date";  
	private static final String VISIT_LOCATION = "location"; 
	private static final String VISIT_STATUS = "status";
	
	private static final String DATABASE_TABLE_RELATIONSHIP = "relationship";
	private static final String RELATIONSHIP_MALEINDIVIDUAL = "maleIndividual";
	private static final String RELATIONSHIP_FEMALEINDIVIDUAL = "femaleIndividual";
	private static final String RELATIONSHIP_STARTDATE = "startDate";
	
	private static final String DATABASE_TABLE_SOCIALGROUP = "socialgroup";
	private static final String SOCIALGROUP_UUID = "uuid";
	private static final String SOCIALGROUP_EXTID = "extId";  
	private static final String SOCIALGROUP_GROUPNAME = "groupName";  
	private static final String SOCIALGROUP_GROUPHEAD = "groupHead";  
	private static final String SOCIALGROUP_STATUS = "status";  
	
	private static final String DATABASE_TABLE_INDIVIDUALSOCIALGROUP = "individual_socialgroup";
	private static final String INDIVIDUALSOCIALGROUP_INDIVIDUALUUID = "individual_extId";
	private static final String INDIVIDUALSOCIALGROUP_SOCIALGROUPUUID = "socialgroup_extId";  
	
	private static final String DATABASE_TABLE_FIELDWORKER = "fieldworker";
	private static final String FIELDWORKER_EXTID = "extId";  
	private static final String FIELDWORKER_PASSWORD = "password";  
	private static final String FIELDWORKER_FIRSTNAME = "firstName";  
	private static final String FIELDWORKER_LASTNAME = "lastName";  
	 
	private static final int DATABASE_VERSION = 16;
		 
	private static final String INDIVIDUAL_CREATE =
	        "create table individual (uuid text primary key, " + 
	        "extId text unique, firstname text not null, lastname text not null, " +
	        "dob text not null, gender text not null, mother text not null, " +
	        "father text not null, currentResidence text not null, status text not null," +
	        "foreign key(currentResidence) references location(uuid));";
	
	private static final String LOCATION_CREATE =
        "create table location (uuid text primary key, " + 
        "extId text not null, head text not null, name text not null, latitude text, " +
        "longitude text, hierarchy text not null, status text not null);";
	
	private static final String HIERARCHY_CREATE =
	        "create table hierarchy (uuid text primary key, " + 
	        "extId text not null, name text not null, parent text not null, " +
	        "level text not null, foreign key(parent) references hierarchy(uuid));";
	
	private static final String ROUND_CREATE =
	        "create table round (uuid text primary key, " + 
	        "startDate text not null, endDate text not null, roundNumber text not null, " +
	        "remarks text not null);";
	
	private static final String VISIT_CREATE =
	        "create table visit (uuid text primary key, " + 
	        "extId text not null, date text not null, round text not null, " +
	        "location text not null, status text not null, " +
	        "foreign key(location) references location(uuid));";
	
	private static final String RELATIONSHIP_CREATE =
        "create table relationship (maleIndividual text not null, " + 
        "femaleIndividual text not null, startDate text not null);";
	
	private static final String SOCIALGROUP_CREATE =
	        "create table socialgroup (uuid text primary key, " + 
	        "extId text not null, groupName text not null, groupHead text not null, " +
	        "status text not null, " +
	        "foreign key(groupHead) references individual(uuid));";
	
	private static final String FIELDWORKER_CREATE =
        "create table fieldworker (extId text primary key, " + 
        "password text not null, firstName text not null, lastName text not null);";
	
	private static final String INDIVIDUAL_SOCIALGROUP_CREATE =
	        "create table individual_socialgroup (individual_extId text not null, " + 
	        "socialgroup_extId text not null, " +
	        "foreign key(individual_extId) references individual(extId), " +
	        "foreign key(socialgroup_extId) references socialgroup(extId));";
	
	// -------------------------------------------------------------------------------------------
	// supervisory workflow tables
	// -------------------------------------------------------------------------------------------
	
	private static final String KEY_ID = "_id";
	
	private static final String FORM_TABLE_NAME = "formsubmission";
	public static final String KEY_REMOTE_ID = "remote_id";
	public static final String KEY_FORMOWNER_ID = "form_owner_id";
	public static final String KEY_FORM_TYPE = "form_type";
	public static final String KEY_FORM_INSTANCE = "form_instance";
	public static final String KEY_FORM_DATETIME = "form_datetime";
	public static final String KEY_ODK_URI = "odk_uri";
	public static final String KEY_ODK_FORM_ID = "form_id";
	public static final String KEY_FORM_COMPLETED = "form_completed";
	public static final String KEY_REVIEW = "form_review";
	
	private static final String ERROR_TABLE_NAME = "formsubmission_msg";
	public static final String KEY_FORM_ID = "form_id";
	public static final String KEY_FORM_MSG = "message";
	
	private static final String SUPERVISOR_TABLE_NAME = "openhds_supervisor";
	public static final String KEY_SUPERVISOR_NAME = "username";
	public static final String KEY_SUPERVISOR_PASS = "password";
	
	private static final String FORM_DB_CREATE = "CREATE TABLE "
		+ FORM_TABLE_NAME + " (" + KEY_ID + " INTEGER PRIMARY KEY, "
		+ KEY_FORMOWNER_ID + " TEXT, " + KEY_FORM_TYPE + " TEXT, "
		+ KEY_FORM_INSTANCE + " TEXT, " + KEY_FORM_DATETIME + " TEXT, "
		+ KEY_REMOTE_ID + " INTEGER, " + KEY_ODK_URI + " TEXT, "
		+ KEY_ODK_FORM_ID + " TEXT, " + KEY_FORM_COMPLETED
		+ " INTEGER DEFAULT 0, " + KEY_REVIEW + " INTEGER DEFAULT 0)";
	
	private static final String MESSAGE_DB_CREATE = "CREATE TABLE "
			+ ERROR_TABLE_NAME + " (" + KEY_FORM_ID + " INTEGER, "
			+ KEY_FORM_MSG + " TEXT)";
	
	private static final String USER_DB_CREATE = "CREATE TABLE "
		+ SUPERVISOR_TABLE_NAME + " (" + KEY_ID + " INTEGER PRIMARY KEY, "
		+ KEY_SUPERVISOR_NAME + " TEXT, " + KEY_SUPERVISOR_PASS + " TEXT)";
	
	private static final SimpleDateFormat dateFormat = new SimpleDateFormat(
		"yyyy-MM-dd_HH_mm_ss_SSS");
	
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
		 
	public long createIndividual(String uuid, String extId, String firstName, String lastName, 
			String gender, String dob, String mother, String father, String residence, String status) {
		 		
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
		 values.put(INDIVIDUAL_STATUS, status);
		 Log.i(TAG, "inserting into individual with extId " + extId);
		 return database.insert(DATABASE_TABLE_INDIVIDUAL, null, values);	
	}
	 
	public long createLocation(String uuid, String extId, String head, String name, String latitude, 
			 String longitude, String hierarchy, String status) {
		 
		 ContentValues values = new ContentValues();
		 values.put(LOCATION_UUID, uuid);
		 values.put(LOCATION_EXTID, extId);
		 values.put(LOCATION_HEAD, head);
		 values.put(LOCATION_NAME, name);
		 values.put(LOCATION_LONGITUDE, longitude);
		 values.put(LOCATION_LATITUDE, latitude);
		 values.put(LOCATION_HIERARCHY, hierarchy);
		 values.put(LOCATION_STATUS, status);
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
		 Log.i(TAG, "inserting into hierarchy with extId " + extId + " at level " + level);
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
	 
	 public long createVisit(String uuid, String extId, String roundNumber, String date, 
			 String location, String status) {
		 
		 ContentValues values = new ContentValues();
		 values.put(VISIT_UUID, uuid);
		 values.put(VISIT_EXTID, extId);
		 values.put(VISIT_DATE, date);
		 values.put(VISIT_ROUND, roundNumber);
		 values.put(VISIT_LOCATION, location);
		 values.put(VISIT_STATUS, status);
		 Log.i(TAG, "inserting into visit with extId " + extId);
		 return database.insert(DATABASE_TABLE_VISIT, null, values);
	 }
	 
	 public long createRelationship(String maleIndividual, String femaleIndividual, String startDate) {
		 ContentValues values = new ContentValues();
		 values.put(RELATIONSHIP_MALEINDIVIDUAL, maleIndividual);
		 values.put(RELATIONSHIP_FEMALEINDIVIDUAL, femaleIndividual);
		 values.put(RELATIONSHIP_STARTDATE, startDate);
		 Log.i(TAG, "inserting into relationship with maleIndividual " + maleIndividual + " and femaleIndividual " + femaleIndividual);
		 return database.insert(DATABASE_TABLE_RELATIONSHIP, null, values);
	 }
	 
	 public long createSocialGroup(String uuid, String extId, String groupName, String groupHead, 
			 String status) {
		 
		 ContentValues values = new ContentValues();
		 values.put(SOCIALGROUP_UUID, uuid);
		 values.put(SOCIALGROUP_EXTID, extId);
		 values.put(SOCIALGROUP_GROUPNAME, groupName);
		 values.put(SOCIALGROUP_GROUPHEAD, groupHead);
		 values.put(SOCIALGROUP_STATUS, status);
		 Log.i(TAG, "inserting into socialgroup with extId " + extId);
		 return database.insert(DATABASE_TABLE_SOCIALGROUP, null, values);
	 }
	 
	 public long createIndividualSocialGroupLink(String individual, String socialGroup) {
		 ContentValues values = new ContentValues();
		 values.put(INDIVIDUALSOCIALGROUP_INDIVIDUALUUID, individual);
		 values.put(INDIVIDUALSOCIALGROUP_SOCIALGROUPUUID, socialGroup);
		 Log.i(TAG, "inserting into individual_socialgroup with individual_extId " + individual + " and socialgroup_extId " + socialGroup);
		 long result = database.insert(DATABASE_TABLE_INDIVIDUALSOCIALGROUP, null, values);
		 return result;
	 }
	 
	 public boolean createFieldWorker(String extId, String password, String firstName, String lastName) {
		 if (findFieldWorker(extId, password) == false) {
			 open();
			 ContentValues values = new ContentValues();
			 values.put(FIELDWORKER_EXTID, extId);
			 values.put(FIELDWORKER_PASSWORD, password);
			 values.put(FIELDWORKER_FIRSTNAME, firstName);
			 values.put(FIELDWORKER_LASTNAME, lastName);
			 Log.i(TAG, "inserting into fieldworker with extId " + extId + " and password " + password);
			 database.insert(DATABASE_TABLE_FIELDWORKER, null, values);
			 close();
			 return true;
		 }
		return false;
	 }
	 
	 public boolean findVisitByExtId(String extId) {
		 open();
		 String query = "select * from visit where extId = ?;";
		 Cursor cursor = database.rawQuery(query, new String[] {extId});
		 
		 if (cursor.moveToFirst())
			 return false;
		 		 
		 cursor.close();
		 close();
		 return true;
	 }
	 	 
	 public boolean findFieldWorker(String extId, String password) {
		 open();
		 String query = "select * from fieldworker where extId = ? and password = ?;";
		 Cursor cursor = database.rawQuery(query, new String[] {extId, password});
		 
		 if (cursor.moveToFirst())
			 return true;
		 		 
		 cursor.close();
		 close();
		 return false;
	 }
	 	 
	 public Individual getIndividualByExtId(String extId) {
		 open();
		 String query = "select * from individual where extId = ?;";
		 Cursor cursor = database.rawQuery(query, new String[] {extId});
		 
		 Individual individual = null;
		 if (cursor.moveToFirst()) {
			 individual = new Individual();
			 individual.setUuid(cursor.getString(0));
			 individual.setExtId(cursor.getString(1));
			 individual.setFirstName(cursor.getString(2));
			 individual.setLastName(cursor.getString(3));
			 individual.setDob(cursor.getString(4));
			 individual.setGender(cursor.getString(5));
			 individual.setMother(cursor.getString(6));
			 individual.setFather(cursor.getString(7));
			 individual.setCurrentResidence(cursor.getString(8));
		 }
		 		 
		 cursor.close();
		 close();
		 return individual;
	 }
	 	 	 
	 public FieldWorker getFieldWorker(String extId, String password) {
		 open();
		 String query = "select * from fieldworker where extId = ? and password = ?;";
		 Cursor cursor = database.rawQuery(query, new String[] {extId, password});
		 
		 FieldWorker fieldWorker = null;
		 if (cursor.moveToFirst()) {
			 fieldWorker = new FieldWorker();
			 fieldWorker.setExtId(cursor.getString(0));
			 fieldWorker.setPassword(cursor.getString(1));
			 fieldWorker.setFirstName(cursor.getString(2));
			 fieldWorker.setLastName(cursor.getString(3));
		 }
		 		 
		 cursor.close();
		 close();
		 return fieldWorker;
	 }
	 
	 public SocialGroup getSocialGroupByGroupName(String groupName) {
		 open();
		 String query = "select * from socialgroup where groupName = ?;";
		 Cursor cursor = database.rawQuery(query, new String[] {groupName});
		 
		 SocialGroup group = null;
		 if (cursor.moveToFirst()) {
			 group = new SocialGroup();
			 group.setUuid(cursor.getString(0));
			 group.setExtId(cursor.getString(1));
			 group.setGroupName(cursor.getString(2));
			 group.setGroupHead(cursor.getString(3));
			 group.setStatus(cursor.getString(4));
		 }
		 		 
		 cursor.close();
		 close();
		 return group;
	 }
	 	 	 
	 public List<SocialGroup> getSocialGroupsForIndividual(String extId) {
		 open();
		 List<SocialGroup> socialgroups = new ArrayList<SocialGroup>();
		 String query = "select * from socialgroup s " +
		 		"inner join individual_socialgroup x on s.extId = x.socialgroup_extId " +
		 		"where x.individual_extId = ?;";
		 Cursor cursor = database.rawQuery(query, new String[] {extId});
		 
		 if (cursor.moveToFirst()) {
			 do {
				 SocialGroup group = new SocialGroup();
				 group.setUuid(cursor.getString(0));
				 group.setExtId(cursor.getString(1));
				 group.setGroupName(cursor.getString(2));
				 group.setGroupHead(cursor.getString(3));
				 group.setStatus(cursor.getString(4));
				 socialgroups.add(group);
			 } while(cursor.moveToNext());
		 }
		 		 
		 cursor.close();
		 close();
		 return socialgroups;
	 }
	  	 
	 public List<LocationHierarchy> getAllRegions(String levelName) {
		 open();
		 List<LocationHierarchy> regions = new ArrayList<LocationHierarchy>();
		 
		 String query = "select * from hierarchy where level = ?;";
		 Cursor cursor = database.rawQuery(query, new String[] {levelName});
		 
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
	 
	 public List<Location> getAllLocationsOfVillage(String villageId) {
		 open();
		 List<Location> locations = new ArrayList<Location>();
		 
		 String query = "select * from location where hierarchy = ?;";
		 Cursor cursor = database.rawQuery(query, new String[] {villageId});
		 
		 if (cursor.moveToFirst()) {
			 do {
				 Location loc = new Location();
				 loc.setUuid(cursor.getString(0));
				 loc.setExtId(cursor.getString(1));
				 loc.setHead(cursor.getString(2));
				 loc.setName(cursor.getString(3));
				 loc.setLatitude(cursor.getString(4));
				 loc.setLongitude(cursor.getString(5));
				 loc.setHierarchy(cursor.getString(6));
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
		 Cursor cursor = database.rawQuery(query, new String[] {location.getExtId()});
		 
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
	 
	 public List<SocialGroup> getAllSocialGroups() {
		 open();
		 List<SocialGroup> groups = new ArrayList<SocialGroup>();
		 
		 String query = "select * from socialgroup;";
		 Cursor cursor = database.rawQuery(query, null);
		 
		 if (cursor.moveToFirst()) {
			 do {
				 SocialGroup group = new SocialGroup();
				 group.setUuid(cursor.getString(0));
				 group.setExtId(cursor.getString(1));
				 group.setGroupName(cursor.getString(2));
				 group.setGroupHead(cursor.getString(3));
				 group.setStatus(cursor.getString(4));
				 groups.add(group);
			 } while (cursor.moveToNext());
		 }
		 cursor.close();
		 close(); 
		 return groups;
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
	 
	 public List<Visit> getAllVisits() {
		 open();
		 List<Visit> visits = new ArrayList<Visit>();
		 
		 String query = "select * from visit;";
		 Cursor cursor = database.rawQuery(query, null);
		 
		 if (cursor.moveToFirst()) {
			 do {
				 Visit visit = new Visit();
				 visit.setUuid(cursor.getString(0));
				 visit.setExtId(cursor.getString(1));
				 visit.setDate(cursor.getString(2));
				 visit.setRound(cursor.getString(3));
				 visit.setLocation(cursor.getString(4));
				 visits.add(visit);
			 } while (cursor.moveToNext());
		 }
		 cursor.close();
		 close(); 
		 return visits;
	 }
	 
	 public List<Relationship> getAllRelationshipsForFemale(String extId) {
		 open();
		 List<Relationship> rels = new ArrayList<Relationship>();
		 
		 String query = "select * from relationship where femaleIndividual = ?;";
		 Cursor cursor = database.rawQuery(query, new String [] {extId});
		 
		 if (cursor.moveToFirst()) {
			 do {
				 Relationship rel = new Relationship();
				 rel.setMaleIndividual(cursor.getString(0));
				 rel.setFemaleIndividual(cursor.getString(1));
				 rel.setStartDate(cursor.getString(2));
				 rels.add(rel);
			 } while (cursor.moveToNext());
		 }
		 cursor.close();
		 close(); 
		 return rels;
	 }
	 
	 public void saveFormSubmission(FormSubmissionRecord fs) {
		 SQLiteDatabase db = dbHelper.getWritableDatabase();

		 long cnt = DatabaseUtils.longForQuery(db, "SELECT COUNT(_id) FROM "
				 + FORM_TABLE_NAME + " WHERE " + KEY_REMOTE_ID + " = ?",
				 new String[] { fs.getRemoteId() + "" });
		 if (cnt > 0) {
			 db.close();
			 return;
		 }
		
		 if (fs.getErrors().size() == 0) {
			 fs.setNeedReview(true);
		 }

		 db.beginTransaction();
		 try {
			 ContentValues cv = new ContentValues();
			 cv.put(KEY_FORMOWNER_ID, fs.getFormOwnerId());
			 cv.put(KEY_FORM_TYPE, fs.getFormType());
			 cv.put(KEY_FORM_INSTANCE, fs.getPartialForm());
			 cv.put(KEY_FORM_DATETIME, getCurrentDateTime());
			 cv.put(KEY_ODK_FORM_ID, fs.getFormId());
			 cv.put(KEY_REMOTE_ID, fs.getRemoteId());
			 cv.put(KEY_REVIEW, fs.isNeedReview() ? 1 : 0);
			 long rowId = db.insert(FORM_TABLE_NAME, null, cv);

			 for (String error : fs.getErrors()) {
				 cv = new ContentValues();
				 cv.put(KEY_FORM_ID, rowId);
				 cv.put(KEY_FORM_MSG, error);
				 db.insert(ERROR_TABLE_NAME, null, cv);
			 }
			 db.setTransactionSuccessful();
		 } finally {
			 db.endTransaction();
		 }
		 db.close();
	 }
	 
	 public Supervisor findSupervisorByUsername(String username) {
		 SQLiteDatabase db = dbHelper.getReadableDatabase();
		 Supervisor user = null;
		 try {
			Cursor c = db.query(SUPERVISOR_TABLE_NAME, new String[] {KEY_ID, KEY_SUPERVISOR_NAME, KEY_SUPERVISOR_PASS},
					KEY_SUPERVISOR_NAME + " = ?", new String[] {username}, null,
					null, null);
			boolean found = c.moveToNext();
			if (!found) {
				c.close();
				return null;
			}

			user = new Supervisor();
			user.setId(c.getLong(c.getColumnIndex(KEY_ID)));
			user.setName(c.getString(c.getColumnIndex(KEY_SUPERVISOR_NAME)));
			user.setPassword(c.getString(c.getColumnIndex(KEY_SUPERVISOR_PASS)));
			c.close();
		} catch (Exception e) {
			Log.w("findUserByUsername", e.getMessage());
		} finally {
			db.close();
		}
		return user;
	 }
	 
	 public long supervisorCount() {
		 SQLiteDatabase db = dbHelper.getReadableDatabase();
		 long rows = DatabaseUtils.queryNumEntries(db, SUPERVISOR_TABLE_NAME);
		 db.close();
		 return rows;
	 }
	 
	 public long addSupervisor(Supervisor u) {
		 long id = -1;
		 SQLiteDatabase db = dbHelper.getWritableDatabase();
		 db.beginTransaction();
		 try {
			 ContentValues cv = new ContentValues();
			 cv.put(KEY_SUPERVISOR_NAME, u.getName());
			 cv.put(KEY_SUPERVISOR_PASS, u.getPassword());

			 id = db.insert(SUPERVISOR_TABLE_NAME, null, cv);
			 db.setTransactionSuccessful();
		 } finally {
			 db.endTransaction();
		 }

		 db.close();
		 return id;
	 }
	 
	 public Cursor getFormsForUsername(String user) {
		 SQLiteDatabase db = dbHelper.getReadableDatabase();
		 Cursor cursor = null;
		 cursor = db.query(FORM_TABLE_NAME, new String[] {KEY_ID, KEY_FORM_TYPE, KEY_FORMOWNER_ID, KEY_REVIEW}, KEY_FORMOWNER_ID
					+ " = ?", new String[] { user }, null, null, null);
		 return cursor;
	 }
	 
	 public FormSubmissionRecord findSubmissionById(long id) {
		 SQLiteDatabase db = dbHelper.getReadableDatabase();
		 Cursor cursor = db.query(FORM_TABLE_NAME, null, KEY_ID + " = ?",
					new String[] { id + "" }, null, null, null);
		 cursor.moveToNext();
		 FormSubmissionRecord record = new FormSubmissionRecord();
		 record.setId(cursor.getLong(cursor.getColumnIndex(KEY_ID)));
		 record.setFormOwnerId(cursor.getString(cursor
				.getColumnIndex(KEY_FORMOWNER_ID)));
		 record.setFormType(cursor.getString(cursor
				.getColumnIndex(KEY_FORM_TYPE)));
		 record.setPartialForm(cursor.getString(cursor
				.getColumnIndex(KEY_FORM_INSTANCE)));
		 record.setSaveDate(cursor.getString(cursor
				.getColumnIndex(KEY_FORM_DATETIME)));
		 record.setOdkUri(cursor.getString(cursor.getColumnIndex(KEY_ODK_URI)));
		 record.setFormId(cursor.getString(cursor
				.getColumnIndex(KEY_ODK_FORM_ID)));
		 record.setCompleted(cursor.getInt(cursor
				.getColumnIndex(KEY_FORM_COMPLETED)) == 0 ? false : true);
		 record.setNeedReview(cursor.getInt(cursor
				.getColumnIndex(KEY_REVIEW)) == 0 ? false : true);
		 record.setRemoteId(cursor.getInt(cursor.getColumnIndex(KEY_REMOTE_ID)));
		 cursor.close();

		 cursor = db.query(ERROR_TABLE_NAME, null, KEY_FORM_ID + " = ?",
				new String[] { id + "" }, null, null, null);
		 while (cursor.moveToNext()) {
			record.addErrorMessage(cursor.getString(cursor
				.getColumnIndex(KEY_FORM_MSG)));
		 }
		 cursor.close();
		 db.close();
		return record;
	 }
	 
	 public void updateOdkUri(long id, Uri uri) {
		 ContentValues cv = new ContentValues();
		 cv.put(KEY_ODK_URI, uri.toString());

		 updateFormSubmission(id, cv);
	 }
	 
	 private void updateFormSubmission(long id, ContentValues values) {
		 SQLiteDatabase db = dbHelper.getWritableDatabase();
		 db.beginTransaction();
		 db.update(FORM_TABLE_NAME, values, KEY_ID + " = ?", new String[] { id + "" });
		 db.setTransactionSuccessful();
		 db.endTransaction();
		 db.close();
	 }
	 
	 public void updateCompleteStatus(long id, boolean completed) {
		 ContentValues cv = new ContentValues();
		 cv.put(KEY_FORM_COMPLETED, completed ? 1 : 0);

		 updateFormSubmission(id, cv);
	 }

	 public void deleteSubmission(long id) {
		 SQLiteDatabase db = dbHelper.getWritableDatabase();
		 db.beginTransaction();
		 db.delete(FORM_TABLE_NAME, KEY_ID + " = ?", new String[] { "" + id });
		 db.delete(ERROR_TABLE_NAME, KEY_FORM_ID + " = ?", new String[] {"" + id});
		 db.setTransactionSuccessful();
		 db.endTransaction();
		 db.close();		
	 }
	 	
	 private String getCurrentDateTime() {
		 return dateFormat.format(new Date());
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
			 db.execSQL(RELATIONSHIP_CREATE);
			 db.execSQL(VISIT_CREATE);
			 db.execSQL(SOCIALGROUP_CREATE);
			 db.execSQL(FIELDWORKER_CREATE);
			 db.execSQL(INDIVIDUAL_SOCIALGROUP_CREATE);
			 db.execSQL(FORM_DB_CREATE);
			 db.execSQL(MESSAGE_DB_CREATE);
			 db.execSQL(USER_DB_CREATE);
		 }
		 	
		 @Override
		 public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			 db.execSQL("drop table if exists " + DATABASE_TABLE_INDIVIDUALSOCIALGROUP);
			 db.execSQL("drop table if exists " + DATABASE_TABLE_INDIVIDUAL);
			 db.execSQL("drop table if exists " + DATABASE_TABLE_LOCATION);
			 db.execSQL("drop table if exists " + DATABASE_TABLE_HIERARCHY);
			 db.execSQL("drop table if exists " + DATABASE_TABLE_ROUND);
			 db.execSQL("drop table if exists " + DATABASE_TABLE_RELATIONSHIP);
			 db.execSQL("drop table if exists " + DATABASE_TABLE_VISIT);
			 db.execSQL("drop table if exists " + DATABASE_TABLE_SOCIALGROUP);
			 db.execSQL("drop table if exists " + DATABASE_TABLE_FIELDWORKER);
		     onCreate(db);
		 }
	 }
}
