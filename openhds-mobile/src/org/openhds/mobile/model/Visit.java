package org.openhds.mobile.model;

import java.io.Serializable;

public class Visit implements Serializable {
	
	private static final long serialVersionUID = -1429712555458116315L;
	private String uuid;
	private String extId;
	private String location;
	private String date;
	private String round;
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
	
	public String getLocation() {
		return location;
	}
	
	public void setLocation(String location) {
		this.location = location;
	}
	
	public String getDate() {
		return date;
	}
	
	public void setDate(String date) {
		this.date = date;
	}
	
	public String getRound() {
		return round;
	}
	
	public void setRound(String round) {
		this.round = round;
	}
	
	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}
}
