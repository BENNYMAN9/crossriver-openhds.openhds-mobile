package org.openhds.model;

public class Round {
	
	String uuid;
	String roundNumber;
	String startDate;
	String endDate;
	String remarks;
	
	public Round() { }
	
	public String getUuid() {
		return uuid;
	}
	
	public void setUuid(String uuid) {
		this.uuid = uuid;
	}
	
	public String getRoundNumber() {
		return roundNumber;
	}
	
	public void setRoundNumber(String roundNumber) {
		this.roundNumber = roundNumber;
	}

	public String getStartDate() {
		return startDate;
	}
	
	public void setStartDate(String startDate) {
		this.startDate = startDate;
	}
	
	public String getEndDate() {
		return endDate;
	}
	
	public void setEndDate(String endDate) {
		this.endDate = endDate;
	}
	
	public String getRemarks() {
		return remarks;
	}
	
	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}
}
