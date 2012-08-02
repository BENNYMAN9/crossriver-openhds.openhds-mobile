package org.openhds.mobile.fragment;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import org.openhds.mobile.R;
import org.openhds.mobile.database.DatabaseAdapter;
import org.openhds.mobile.model.FieldWorker;
import org.openhds.mobile.model.Individual;
import org.openhds.mobile.model.Location;
import org.openhds.mobile.model.LocationHierarchy;
import org.openhds.mobile.model.PregnancyOutcome;
import org.openhds.mobile.model.Relationship;
import org.openhds.mobile.model.Round;
import org.openhds.mobile.model.SocialGroup;
import org.openhds.mobile.model.Visit;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class SelectionFragment extends Fragment {
	
	private DatabaseAdapter databaseAdapter;
	
	private FieldWorker fieldWorker;
	private LocationHierarchy region;
	private LocationHierarchy subRegion;
	private LocationHierarchy village;
	private Round round;
	private Location location;
	private Individual individual;
	private SocialGroup socialgroup;
	private Visit visit;
	private PregnancyOutcome pregnancyOutcome;
	private Relationship relationship;
	
	private List<LocationHierarchy> regions;
	private List<LocationHierarchy> subRegions;
	private List<LocationHierarchy> villages;
	private List<Round> rounds;
	private List<Location> locations;
	private List<Individual> individuals;
	private List<SocialGroup> socialgroups;
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		fieldWorker = new FieldWorker();
        region = new LocationHierarchy();
        subRegion = new LocationHierarchy();
        village = new LocationHierarchy();
        round = new Round();
        location = new Location();
        individual = new Individual();
        socialgroup = new SocialGroup();
        visit = new Visit();
        pregnancyOutcome = new PregnancyOutcome();
        relationship = new Relationship();

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
	
	// this logic is specific for the Cross River birth registration
	public boolean createPregnancyOutcome() {
		
		Individual father = determinePregnancyOutcomeFather(individual);
		this.getPregnancyOutcome().setMother(individual);
		this.getPregnancyOutcome().setFather(father);

		// generation of child ids
		String motherId;
		String childId;
		try {
			motherId = individual.getExtId();
			String householdSectionId = motherId.substring(9, 11);
			String locationId = individual.getCurrentResidence();
			childId = locationId + householdSectionId;
		} catch (Exception e) {
			return false;
		}
		
		String baseString = childId;
		Integer partToIncrement = Integer.parseInt(motherId.substring(11, 13));
				
		String child1Id = generateId(partToIncrement, baseString);
		partToIncrement = Integer.parseInt(child1Id.substring(11, 13));
		String child2Id = generateId(partToIncrement, baseString);
		
		this.getPregnancyOutcome().setChild1ExtId(child1Id);
		this.getPregnancyOutcome().setChild2ExtId(child2Id);
		return true;
	}
	
	private String generateId(Integer partToIncrement, String baseString) {
		String temp = "";
		do {
			StringBuilder builder = new StringBuilder();
			partToIncrement++;
			if (partToIncrement.toString().length() < 2) 
				builder.append("0").append(partToIncrement.toString());
			if (partToIncrement.toString().length() == 2)
				builder.append(partToIncrement.toString());
			temp = baseString.concat(builder.toString());
		} while (databaseAdapter.getIndividualByExtId(temp) != null);
		
		baseString = temp;
		return baseString;
	}
	
	private Individual determinePregnancyOutcomeFather(Individual mother) {
		
		List<Relationship> rels = databaseAdapter.getAllRelationshipsForFemale(mother.getExtId());
		DateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
		
		Relationship current = null;
		// must find the most current relationship
		for (Relationship rel : rels) {
			if (current == null)
				current = rel;
			
			else {
				try {
					Date currentDate = formatter.parse(current.getStartDate());
					Date relDate = formatter.parse(rel.getStartDate());	
					if (currentDate.before(relDate))
						current = rel;
					
				} catch (ParseException e) {
					return null;
				}
			}
		}
		if (current == null)
			return null;
		else  {
			String fatherId = current.getMaleIndividual();
			return databaseAdapter.getIndividualByExtId(fatherId);
		}
	}
	
	public CharSequence[] getSocialGroupsForDialog() {
		CharSequence[] names = new CharSequence[socialgroups.size()];
		for (int i = 0; i < socialgroups.size(); i++) 
			names[i] = socialgroups.get(i).getGroupName();
		
		return names;
	}
	
	public void setSocialGroupDialogSelection(int index) {
		this.socialgroup = socialgroups.get(index);
	}
	
	public FieldWorker getFieldWorker() {
		return fieldWorker;
	}

	public void setFieldWorker(FieldWorker fieldWorker) {
		this.fieldWorker = fieldWorker;
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
	
	public SocialGroup getSocialgroup() {
		return socialgroup;
	}

	public void setSocialgroup(SocialGroup socialgroup) {
		this.socialgroup = socialgroup;
	}

	public List<SocialGroup> getSocialgroups() {
		return socialgroups;
	}

	public void setSocialgroups(List<SocialGroup> socialgroups) {
		this.socialgroups = socialgroups;
	}
	
	public PregnancyOutcome getPregnancyOutcome() {
		return pregnancyOutcome;
	}

	public void setPregnancyOutcome(PregnancyOutcome pregnancyOutcome) {
		this.pregnancyOutcome = pregnancyOutcome;
	}
	
	public Relationship getRelationship() {
		return relationship;
	}

	public void setRelationship(Relationship relationship) {
		this.relationship = relationship;
	}
}
