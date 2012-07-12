package org.openhds.mobile.model;

import java.io.Serializable;

public class Individual implements Serializable {

	private static final long serialVersionUID = -799404570247633403L;
	private String uuid;
	private String extId;
	private String firstName;
	private String lastName;
	private String gender;
	private String dob;
	private String mother;
	private String father;
	private String currentResidence;
	private String status;
	
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
	
	public String getGender() {
		return gender;
	}
	
	public void setGender(String gender) {
		this.gender = gender;
	}
	
	public String getDob() {
		return dob;
	}
	
	public void setDob(String dob) {
		this.dob = dob;
	}
	public String getMother() {
		return mother;
	}
	
	public void setMother(String mother) {
		this.mother = mother;
	}
	
	public String getFather() {
		return father;
	}
	
	public void setFather(String father) {
		this.father = father;
	}
	
	public String getCurrentResidence() {
		return currentResidence;
	}
	
	public void setCurrentResidence(String currentResidence) {
		this.currentResidence = currentResidence;
	}
	
	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}
}
