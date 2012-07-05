package org.openhds.mobile.fragment;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import org.openhds.mobile.R;
import org.openhds.mobile.database.DatabaseAdapter;
import org.openhds.mobile.model.Individual;
import org.openhds.mobile.model.Location;
import org.openhds.mobile.model.LocationHierarchy;
import org.openhds.mobile.model.Round;
import org.openhds.mobile.model.Visit;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class SelectionFragment extends Fragment {
	
	private DatabaseAdapter databaseAdapter;
	
	private LocationHierarchy region;
	private LocationHierarchy subRegion;
	private LocationHierarchy village;
	private Round round;
	private Location location;
	private Individual individual;
	private Visit visit;
	
	private List<LocationHierarchy> regions;
	private List<LocationHierarchy> subRegions;
	private List<LocationHierarchy> villages;
	private List<Round> rounds;
	private List<Location> locations;
	private List<Individual> individuals;
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        region = new LocationHierarchy();
        subRegion = new LocationHierarchy();
        village = new LocationHierarchy();
        round = new Round();
        location = new Location();
        individual = new Individual();
        visit = new Visit();
        
        databaseAdapter = new DatabaseAdapter(getActivity().getBaseContext());
        return inflater.inflate(R.layout.selection, container, false);
    }
	
	// this logic is specific for Cross River
	public void createVisit() {
		StringBuilder builder;
		int increment = 0;
		boolean result = false;
		
		do {
			builder = new StringBuilder();
			increment++;
			builder.append("V" + location.getExtId().substring(0, 6) + 
					round.getRoundNumber() + Integer.toString(increment) + location.getExtId().substring(6));
			result = databaseAdapter.findVisitByExtId(builder.toString());
		} while (!result);	
		
		visit.setExtId(builder.toString());
		
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd");  
        String date = df.format(new Date());
        
        visit.setDate(date);
	}
	
	public LocationHierarchy getRegion() {
		return region;
	}

	public LocationHierarchy getSubRegion() {
		return subRegion;
	}

	public LocationHierarchy getVillage() {
		return village;
	}

	public Round getRound() {
		return round;
	}

	public Location getLocation() {
		return location;
	}

	public Individual getIndividual() {
		return individual;
	}

	public List<LocationHierarchy> getRegions() {
		return regions;
	}

	public List<LocationHierarchy> getSubRegions() {
		return subRegions;
	}

	public List<LocationHierarchy> getVillages() {
		return villages;
	}

	public List<Round> getRounds() {
		return rounds;
	}

	public List<Location> getLocations() {
		return locations;
	}

	public List<Individual> getIndividuals() {
		return individuals;
	}
	
	public void setRegion(LocationHierarchy region) {
		this.region = region;
	}

	public void setSubRegion(LocationHierarchy subRegion) {
		this.subRegion = subRegion;
	}

	public void setVillage(LocationHierarchy village) {
		this.village = village;
	}

	public void setRound(Round round) {
		this.round = round;
	}

	public void setLocation(Location location) {
		this.location = location;
	}

	public void setIndividual(Individual individual) {
		this.individual = individual;
	}
	
	public Visit getVisit() {
		return visit;
	}

	public void setVisit(Visit visit) {
		this.visit = visit;
	}

	public void setRegions(List<LocationHierarchy> regions) {
		this.regions = regions;
	}

	public void setSubRegions(List<LocationHierarchy> subRegions) {
		this.subRegions = subRegions;
	}

	public void setVillages(List<LocationHierarchy> villages) {
		this.villages = villages;
	}

	public void setRounds(List<Round> rounds) {
		this.rounds = rounds;
	}

	public void setLocations(List<Location> locations) {
		this.locations = locations;
	}

	public void setIndividuals(List<Individual> individuals) {
		this.individuals = individuals;
	}
}
