package org.openhds.mobile.fragment;

import java.util.List;

import org.openhds.mobile.Converter;
import org.openhds.mobile.Queries;
import org.openhds.mobile.R;
import org.openhds.mobile.database.DatabaseAdapter;
import org.openhds.mobile.model.Individual;
import org.openhds.mobile.model.Location;
import org.openhds.mobile.model.LocationHierarchy;
import org.openhds.mobile.model.UpdateParams;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class SelectionFilterFragment extends Fragment {
	
	private DatabaseAdapter databaseAdapter;
	
	private LocationHierarchy region;
	private LocationHierarchy subRegion;
	private LocationHierarchy village;
	private Location location;
	private Individual individual;
	
	private String firstName;
	private String lastName;
	private String gender;
	
	private List<LocationHierarchy> regions;
	private List<LocationHierarchy> subRegions;
	private List<LocationHierarchy> villages;
	private List<Location> locations;
	private List<Individual> individuals;
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        region = new LocationHierarchy();
        subRegion = new LocationHierarchy();
        village = new LocationHierarchy();
        location = new Location();
        individual = new Individual();
       
        databaseAdapter = new DatabaseAdapter(getActivity().getBaseContext());
        return inflater.inflate(R.layout.selection_filter, container, false);
    }
	
	public CharSequence[] getRegionsDialog() {
	    Cursor cursor = Queries.getHierarchysByLevel(getActivity().getContentResolver(), UpdateParams.HIERARCHY_TOP_LEVEL);
		regions = Converter.toHierarchyList(cursor);
		CharSequence[] names = new CharSequence[regions.size()];
		for (int i = 0; i < regions.size(); i++) 
			names[i] = regions.get(i).getName();
		
		return names;
	}
	
	public CharSequence[] getSubRegionsDialog() {
	    Cursor cursor = Queries.getHierarchysByParent(getActivity().getContentResolver(), region.getExtId());
		subRegions = Converter.toHierarchyList(cursor);
		CharSequence[] names = new CharSequence[subRegions.size()];
		for (int i = 0; i < subRegions.size(); i++) 
			names[i] = subRegions.get(i).getName();
		
		return names;
	}
	
	public CharSequence[] getVillagesDialog() {
	    Cursor cursor = Queries.getHierarchysByParent(getActivity().getContentResolver(), subRegion.getExtId());
		villages = Converter.toHierarchyList(cursor);
		CharSequence[] names = new CharSequence[villages.size()];
		for (int i = 0; i < villages.size(); i++) 
			names[i] = villages.get(i).getName();
		
		return names;
	}
	
	public CharSequence[] getLocationsDialog() {
	    Cursor cursor = Queries.getLocationsByHierachy(getActivity().getContentResolver(), village.getExtId());
		locations = Converter.toLocationList(cursor);
		CharSequence[] names = new CharSequence[locations.size()];
		for (int i = 0; i < locations.size(); i++) 
			names[i] = locations.get(i).getName();
		
		return names;
	}
	
	public void setRegionDialogSelection(int index) {
		this.region = regions.get(index);
	}
	
	public void setSubRegionDialogSelection(int index) {
		this.subRegion = subRegions.get(index);
	}
	
	public void setVillageDialogSelection(int index) {
		this.village = villages.get(index);
	}
	
	public void setLocationDialogSelection(int index) {
		this.location = locations.get(index);
	}
	
	public DatabaseAdapter getDatabaseAdapter() {
		return databaseAdapter;
	}

	public void setDatabaseAdapter(DatabaseAdapter databaseAdapter) {
		this.databaseAdapter = databaseAdapter;
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
	
	public List<LocationHierarchy> getRegions() {
		return regions;
	}

	public void setRegions(List<LocationHierarchy> regions) {
		this.regions = regions;
	}

	public List<LocationHierarchy> getSubRegions() {
		return subRegions;
	}

	public void setSubRegions(List<LocationHierarchy> subRegions) {
		this.subRegions = subRegions;
	}

	public List<LocationHierarchy> getVillages() {
		return villages;
	}

	public void setVillages(List<LocationHierarchy> villages) {
		this.villages = villages;
	}

	public List<Location> getLocations() {
		return locations;
	}

	public void setLocations(List<Location> locations) {
		this.locations = locations;
	}

	public List<Individual> getIndividuals() {
		return individuals;
	}

	public void setIndividuals(List<Individual> individuals) {
		this.individuals = individuals;
	}
}
