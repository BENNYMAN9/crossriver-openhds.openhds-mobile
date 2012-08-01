package org.openhds.mobile.model;

import java.io.Serializable;

/**
 * This class acts as a wrapper for holding entities that were selected
 */
public class Record implements Serializable {

	private static final long serialVersionUID = -1097088542398911428L;
	
	private String fieldWorkerId;
	private LocationHierarchy region;
	private LocationHierarchy subRegion;
	private LocationHierarchy village;
	private Location location;
	private Individual individual;
	private SocialGroup socialgroup;
	private Round round;
	private Visit visit;
	private PregnancyOutcome pregnancyOutcome;
	private Relationship relationship;
	
	public Record(String username, LocationHierarchy region, LocationHierarchy subRegion, 
			LocationHierarchy village, Location location, Round round, 
			Individual individual, SocialGroup socialgroup, Visit visit) {
		this.fieldWorkerId = username;
		this.region = region;
		this.subRegion = subRegion;
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
	
	public LocationHierarchy getRegion() {
		return region;
	}

	public void setRegion(LocationHierarchy region) {
		this.region = region;
	}

	public LocationHierarchy getSubRegion() {
		return subRegion;
	}

	public void setSubRegion(LocationHierarchy subRegion) {
		this.subRegion = subRegion;
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
	
	public PregnancyOutcome getPregnancyOutcome() {
		return pregnancyOutcome;
	}

	public void setPregnancyOutcome(PregnancyOutcome pregnancyOutcome) {
		if (pregnancyOutcome.getFather() == null)
			pregnancyOutcome.setFather(new Individual());
		this.pregnancyOutcome = pregnancyOutcome;
	}
	
	public Relationship getRelationship() {
		return relationship;
	}

	public void setRelationship(Relationship relationship) {
		this.relationship = relationship;
	}
}
