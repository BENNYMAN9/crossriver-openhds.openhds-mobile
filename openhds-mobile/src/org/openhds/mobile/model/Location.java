package org.openhds.mobile.model;

import java.io.Serializable;

public class Location implements Serializable {

	private static final long serialVersionUID = -8462273781331229805L;
	private String uuid;
	private String extId;
	private String name;
	private String latitude;
	private String longitude;
	private String hierarchy;
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
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getLatitude() {
		return latitude;
	}
	
	public void setLatitude(String latitude) {
		this.latitude = latitude;
	}
	
	public String getLongitude() {
		return longitude;
	}
	
	public void setLongitude(String longitude) {
		this.longitude = longitude;
	}
	
	public String getHierarchy() {
		return hierarchy;
	}
	
	public void setHierarchy(String hierarchy) {
		this.hierarchy = hierarchy;
	}
	
	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}
}
