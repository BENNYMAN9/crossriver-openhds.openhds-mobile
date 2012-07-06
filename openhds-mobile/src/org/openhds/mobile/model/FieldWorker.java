package org.openhds.mobile.model;

public class FieldWorker {
	
	String uuid;
	String extId;
	String firstName;
	String lastName;
	
	public FieldWorker(String uuid, String extId, String firstName, String lastName) {
		this.uuid = uuid;
		this.extId = extId;
		this.firstName = firstName;
		this.lastName = lastName;
	}
	
	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}
	
	public String getExtId() {
		return extId;
	}
	
	public void setExtId(String extId) {
		this.extId = extId;
	}
	
	public String getFirstName() {
		return firstName;
	}
	
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}
	
	public String getLastName() {
		return lastName;
	}
	
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}
}
