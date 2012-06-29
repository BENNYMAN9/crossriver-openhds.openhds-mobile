package org.openhds.model;

/**
 * This class acts as a wrapper for holding entities that were selected
 */
public class Record {

	LocationHierarchy village;
	Location location;
	Individual individual;
	Round round;
	
	public Record(LocationHierarchy village, Location location, Round round, Individual individual) {
		this.village = village;
		this.location = location;
		this.individual = individual;
		this.round = round;
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
	
	public Round getRound() {
		return round;
	}
	
	public void setRound(Round round) {
		this.round = round;
	}
}
