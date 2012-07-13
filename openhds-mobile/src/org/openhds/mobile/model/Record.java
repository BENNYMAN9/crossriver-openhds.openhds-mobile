package org.openhds.mobile.model;

/**
 * This class acts as a wrapper for holding entities that were selected
 */
public class Record {

	String fieldWorkerId;
	LocationHierarchy village;
	Location location;
	Individual individual;
	SocialGroup socialgroup;
	Round round;
	Visit visit;
	
	public Record(String username, LocationHierarchy village, Location location, Round round, 
			Individual individual, SocialGroup socialgroup, Visit visit) {
		this.fieldWorkerId = username;
		this.village = village;
		this.location = location;
		this.individual = individual;
		this.socialgroup = socialgroup;
		this.round = round;
		this.visit = visit;
	}
	
	public String getFieldWorkerId() {
		return fieldWorkerId;
	}

	public void setFieldWorkerId(String fieldWorkerId) {
		this.fieldWorkerId = fieldWorkerId;
	}
	
	public LocationHierarchy getVillage() {
		return village;
	}
	
	public void setVillage(LocationHierarchy village) {
		this.village = village;
	}
	
	public Location getLocation() {
		return location;
	}
	
	public void setLocation(Location location) {
		this.location = location;
	}
	
	public Individual getIndividual() {
		return individual;
	}
	
	public void setIndividual(Individual individual) {
		this.individual = individual;
	}
	
	public SocialGroup getSocialgroup() {
		return socialgroup;
	}

	public void setSocialgroup(SocialGroup socialgroup) {
		this.socialgroup = socialgroup;
	}
	
	public Round getRound() {
		return round;
	}
	
	public void setRound(Round round) {
		this.round = round;
	}
	
	public Visit getVisit() {
		return visit;
	}

	public void setVisit(Visit visit) {
		this.visit = visit;
	}
}
