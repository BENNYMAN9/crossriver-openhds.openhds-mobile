package org.openhds.mobile.model;

import java.util.Arrays;
import java.util.List;

/**
 * This class specifies the fields which are prefilled when an xform is loaded.
 * The convention is based on these naming standards, all other fields will be ignored.
 */
public class FilledParams {
	
	public static final String visitId = "visitId";
	public static final String roundNumber = "roundNumber";
	public static final String visitDate = "visitDate";
	
	public static final String individualId = "individualId";
	public static final String firstName = "firstName";
	public static final String lastName = "lastName";
	public static final String gender = "gender";
	public static final String dob = "dob";
	
	public static final String houseId = "houseId";
	public static final String houseName = "houseName";
	public static final String longitude = "longitude";
	public static final String latitude = "latitude";
	
	public static final String householdId = "householdId";
	public static final String householdName = "householdName";
	
	public static final String fieldWorkerId = "fieldWorkerId";
			
	public static List<String> getParamsArray() {
		return Arrays.asList(visitId, roundNumber, visitDate, 
				individualId, firstName, lastName, gender, dob, 
				houseId, houseName, longitude, latitude, 
				householdId, householdName, fieldWorkerId);
	}
}
