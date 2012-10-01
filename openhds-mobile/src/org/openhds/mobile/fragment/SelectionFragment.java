package org.openhds.mobile.fragment;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.openhds.mobile.FieldWorkerProvider;
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

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

public class SelectionFragment extends Fragment implements OnClickListener {
    
    public static interface Listener {
        void onRegion();
        
        void onSubRegion();
        
        void onVillage();
        
        void onLocation();
        
        void onRound();
        
        void onIndividual();
    }
	
	private DatabaseAdapter databaseAdapter;
	private Listener listener;
	
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
	
	private boolean isExternalInMigration = false;
	
	private Button regionBtn, subRegionBtn, villageBtn, locationBtn, roundBtn, individualBtn;

	// text widgets, these become enabled and disabled according to the current phase
	private TextView loginGreetingText,
					 regionNameText, regionExtIdText, regionName, regionExtId, 
					 subRegionNameText, subRegionExtIdText, subRegionName, subRegionExtId,
					 villageNameText, villageExtIdText, villageName, villageExtId,
					 roundNumberText, roundStartDateText, roundEndDateText, roundNumber, roundStartDate, roundEndDate, 
					 locationNameText, locationExtIdText, locationLatitudeText, locationLongitudeText, locationName, locationExtId, locationLatitude, locationLongitude,
					 individualFirstNameText, individualLastNameText, individualExtIdText, individualDobText, individualFirstName, individualLastName, individualExtId, individualDob;	
	
	
	@Override
	public void onAttach(Activity activity) {
	    super.onAttach(activity);
	    listener = (Listener)activity;
	}
	
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
        View view = inflater.inflate(R.layout.selection, container, false);
        bindViews(view);
        setFieldWorkerText(view);
        return view;
    }

    private void setFieldWorkerText(View view) {
        FieldWorkerProvider provider = (FieldWorkerProvider) getActivity();
        fieldWorker = provider.getFieldWorker();
        loginGreetingText = (TextView) view.findViewById(R.id.loginGreetingText);
        loginGreetingText.setText("Hello, " + fieldWorker.getFirstName() + " " + fieldWorker.getLastName());
    }

	private void bindViews(View view) {
        regionBtn = (Button) view.findViewById(R.id.regionBtn);
        regionBtn.setOnClickListener(this);
        regionNameText = (TextView) view.findViewById(R.id.regionNameText);
        regionExtIdText = (TextView) view.findViewById(R.id.regionExtIdText);
        regionName = (TextView) view.findViewById(R.id.regionName);
        regionExtId = (TextView) view.findViewById(R.id.regionExtId);
	    
        subRegionBtn = (Button) view.findViewById(R.id.subRegionBtn);
        subRegionBtn.setOnClickListener(this);
        subRegionNameText = (TextView) view.findViewById(R.id.subRegionNameText);
        subRegionExtIdText = (TextView) view.findViewById(R.id.subRegionExtIdText);
        subRegionName = (TextView) view.findViewById(R.id.subRegionName);
        subRegionExtId = (TextView) view.findViewById(R.id.subRegionExtId);
	    
        villageBtn = (Button) view.findViewById(R.id.villageBtn);
        villageBtn.setOnClickListener(this);
        villageNameText = (TextView) view.findViewById(R.id.villageNameText);
        villageExtIdText = (TextView) view.findViewById(R.id.villageExtIdText);
        villageName = (TextView) view.findViewById(R.id.villageName);
        villageExtId = (TextView) view.findViewById(R.id.villageExtId);
                
        locationBtn = (Button) view.findViewById(R.id.locationBtn);
        locationBtn.setOnClickListener(this);
        locationNameText = (TextView) view.findViewById(R.id.locationNameText);
        locationExtIdText = (TextView) view.findViewById(R.id.locationExtIdText);
        locationLatitudeText = (TextView) view.findViewById(R.id.locationLatitudeText);
        locationLongitudeText = (TextView) view.findViewById(R.id.locationLongitudeText);
        locationName = (TextView) view.findViewById(R.id.locationName);
        locationExtId = (TextView) view.findViewById(R.id.locationExtId);
        locationLatitude = (TextView) view.findViewById(R.id.locationLatitude);
        locationLongitude = (TextView) view.findViewById(R.id.locationLongitude);
        
        roundBtn = (Button) view.findViewById(R.id.roundBtn);
        roundBtn.setOnClickListener(this);
        roundNumberText = (TextView) view.findViewById(R.id.roundNumberText);
        roundStartDateText = (TextView) view.findViewById(R.id.roundStartDateText);
        roundEndDateText = (TextView) view.findViewById(R.id.roundEndDateText);
        roundNumber = (TextView) view.findViewById(R.id.roundNumber);
        roundStartDate = (TextView) view.findViewById(R.id.roundStartDate);
        roundEndDate = (TextView) view.findViewById(R.id.roundEndDate);
        
        individualBtn = (Button) view.findViewById(R.id.individualBtn);
        individualBtn.setOnClickListener(this);
        individualExtIdText = (TextView) view.findViewById(R.id.individualExtIdText);
        individualFirstNameText = (TextView) view.findViewById(R.id.individualFirstNameText);
        individualLastNameText = (TextView) view.findViewById(R.id.individualLastNameText);
        individualDobText = (TextView) view.findViewById(R.id.individualDobText);
        individualExtId = (TextView) view.findViewById(R.id.individualExtId);
        individualFirstName = (TextView) view.findViewById(R.id.individualFirstName);
        individualLastName = (TextView) view.findViewById(R.id.individualLastName);
        individualDob = (TextView) view.findViewById(R.id.individualDob);
		
	}
	
	// called when the household_dialog is displayed and is used for external inmigrations
	public void setSocialGroup(String groupName) {
		SocialGroup group = databaseAdapter.getSocialGroupByGroupName(groupName);
		this.socialgroup = group;
	}
	
	// an option to create a new location rather than to reference an existing one
	public void createLocation(String headId, String groupName) {
				
		StringBuilder builder = new StringBuilder();
		builder.append(headId.substring(0, 12));
		
		String baseString = builder.toString().substring(0, 9);
		Integer partToIncrement = Integer.parseInt(builder.toString().substring(9, 12));
		String locationId = generateLocationId(partToIncrement, baseString);

		location.setExtId(locationId);
		location.setHierarchy(village.getExtId());
		location.setName(groupName);
		location.setHead(headId);
	}
	
	// an option to create a new social group rather than to reference an existing one
	public void createSocialGroup() {
		
		String headId = individual.getExtId();
		String baseString = headId.substring(0, 12);
		Integer partToIncrement = Integer.parseInt(headId.substring(12, 14));
							
		String socialgroupId = generateSocialGroupId(partToIncrement, baseString);

		socialgroup.setExtId(socialgroupId);
	}
	
	// this logic is specific for Cross River
	public void createVisit() {
		StringBuilder builder;
		int increment = 0;
		boolean result = false;
		
		do {
			builder = new StringBuilder();
			increment++;
			builder.append("V" + location.getExtId().substring(0, 9) + 
					round.getRoundNumber() + Integer.toString(increment) + location.getExtId().substring(9));
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
			String householdSectionId = motherId.substring(12, 14);
			String locationId = individual.getCurrentResidence();
			childId = locationId + householdSectionId;
		} catch (Exception e) {
			return false;
		}
		
		String baseString = childId;
		Integer partToIncrement = Integer.parseInt(motherId.substring(14, 16));
				
		String child1Id = generateIndividualId(partToIncrement, baseString);
		partToIncrement = Integer.parseInt(child1Id.substring(14, 16));
		String child2Id = generateIndividualId(partToIncrement, baseString);
		
		this.getPregnancyOutcome().setChild1ExtId(child1Id);
		this.getPregnancyOutcome().setChild2ExtId(child2Id);
		return true;
	}
	
	public String generateIndividualId(Integer partToIncrement, String baseString) {
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
	
	public String generateLocationId(Integer partToIncrement, String baseString) {
		String temp = "";
		do {
			StringBuilder builder = new StringBuilder();
			partToIncrement++;
			if (partToIncrement.toString().length() == 1) 
				builder.append("00").append(partToIncrement.toString());
			else if (partToIncrement.toString().length() == 2)
				builder.append("0").append(partToIncrement.toString());
			else if (partToIncrement.toString().length() == 3)
				builder.append(partToIncrement.toString());
			temp = baseString.concat(builder.toString());
		} while (databaseAdapter.getLocationByExtId(temp) != null);
		
		baseString = temp;
		return baseString;
	}
	
	public String generateSocialGroupId(Integer partToIncrement, String baseString) {
		String temp = "";
		do {
			StringBuilder builder = new StringBuilder();
			partToIncrement++;
			if (partToIncrement.toString().length() == 1) 
				builder.append("0").append(partToIncrement.toString());
			else if (partToIncrement.toString().length() == 2)
				builder.append(partToIncrement.toString());
			temp = baseString.concat(builder.toString());
		} while (databaseAdapter.getSocialGroupByExtId(temp) != null);
		
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
	
	public String[] getAllSocialGroupsForDialog() {
		List<SocialGroup> groups = databaseAdapter.getAllSocialGroups();
		String[] names = new String[groups.size()];
		for (int i = 0; i < groups.size(); i++) 
			names[i] = groups.get(i).getGroupName();
		
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
        regionName.setVisibility(View.VISIBLE);
        regionExtId.setVisibility(View.VISIBLE);
        regionNameText.setText(region.getName());
        regionExtIdText.setText(region.getExtId());
	}

	public void setSubRegion(LocationHierarchy subRegion) {
		this.subRegion = subRegion;
        subRegionName.setVisibility(View.VISIBLE);
        subRegionExtId.setVisibility(View.VISIBLE);
        subRegionNameText.setText(subRegion.getName());
        subRegionExtIdText.setText(subRegion.getExtId());
	}

	public void setVillage(LocationHierarchy village) {
		this.village = village;
        villageName.setVisibility(View.VISIBLE);
        villageExtId.setVisibility(View.VISIBLE);
        villageNameText.setText(village.getName());
        villageExtIdText.setText(village.getExtId());    
	}

	public void setRound(Round round) {
		this.round = round;
        roundNumber.setVisibility(View.VISIBLE);
        roundStartDate.setVisibility(View.VISIBLE);
        roundEndDate.setVisibility(View.VISIBLE);
        roundNumberText.setText(round.getRoundNumber());
        roundStartDateText.setText(round.getStartDate());
        roundEndDateText.setText(round.getEndDate());
	}

	public void setLocation(Location location) {
		this.location = location;
        locationName.setVisibility(View.VISIBLE);
        locationExtId.setVisibility(View.VISIBLE);
        locationLatitude.setVisibility(View.VISIBLE);
        locationLongitude.setVisibility(View.VISIBLE);
        displayLocationInfo();
	}

	public void setIndividual(Individual individual) {
		this.individual = individual;
        individualExtId.setVisibility(View.VISIBLE);
        individualFirstName.setVisibility(View.VISIBLE);
        individualLastName.setVisibility(View.VISIBLE);
        individualDob.setVisibility(View.VISIBLE);
        individualExtIdText.setText(individual.getExtId());
        individualFirstNameText.setText(individual.getFirstName());
        individualLastNameText.setText(individual.getLastName());
        individualDobText.setText(individual.getDob());
	}
	
    public void clearRegionTextFields() {
        regionNameText.setText("");
        regionExtIdText.setText("");
        regionName.setVisibility(View.INVISIBLE);
        regionExtId.setVisibility(View.INVISIBLE);
    }
    
    public void clearSubRegionTextFields() {
        subRegionNameText.setText("");
        subRegionExtIdText.setText("");
        subRegionName.setVisibility(View.INVISIBLE);
        subRegionExtId.setVisibility(View.INVISIBLE);
    }
    
    public void clearVillageTextFields() {
        villageNameText.setText("");
        villageExtIdText.setText("");
        villageName.setVisibility(View.INVISIBLE);
        villageExtId.setVisibility(View.INVISIBLE);
    }
    
    public void clearRoundTextFields() {
        roundNumberText.setText("");
        roundStartDateText.setText("");
        roundEndDateText.setText("");
        roundNumber.setVisibility(View.INVISIBLE);
        roundStartDate.setVisibility(View.INVISIBLE);
        roundEndDate.setVisibility(View.INVISIBLE);
    }
    
    public void clearLocationTextFields() {
        locationNameText.setText("");
        locationExtIdText.setText("");
        locationLatitudeText.setText("");
        locationLongitudeText.setText("");
        locationName.setVisibility(View.INVISIBLE);
        locationExtId.setVisibility(View.INVISIBLE);
        locationLatitude.setVisibility(View.INVISIBLE);
        locationLongitude.setVisibility(View.INVISIBLE);
    }
    
    public void clearIndividualTextFields() {
        individualExtIdText.setText("");
        individualFirstNameText.setText("");
        individualLastNameText.setText("");
        individualDobText.setText("");
        individualExtId.setVisibility(View.INVISIBLE);
        individualFirstName.setVisibility(View.INVISIBLE);
        individualLastName.setVisibility(View.INVISIBLE);
        individualDob.setVisibility(View.INVISIBLE);
    }
    
    public void restoreRegionTextFields() {
        regionNameText.setText(region.getName());
        regionExtIdText.setText(region.getExtId());
        regionName.setVisibility(View.VISIBLE);
        regionExtId.setVisibility(View.VISIBLE);
    }
    
    public void restoreSubRegionTextFields() {
        subRegionNameText.setText(subRegion.getName());
        subRegionExtIdText.setText(subRegion.getExtId());
        subRegionName.setVisibility(View.VISIBLE);
        subRegionExtId.setVisibility(View.VISIBLE);
    }
    
    public void restoreVillageTextFields() {
        villageNameText.setText(village.getName());
        villageExtIdText.setText(village.getExtId());
        villageName.setVisibility(View.VISIBLE);
        villageExtId.setVisibility(View.VISIBLE);
    }
    
    public void restoreRoundTextFields() {
        roundNumberText.setText(round.getRoundNumber());
        roundStartDateText.setText(round.getStartDate());
        roundEndDateText.setText(round.getEndDate());
        roundNumber.setVisibility(View.VISIBLE);
        roundStartDate.setVisibility(View.VISIBLE);
        roundEndDate.setVisibility(View.VISIBLE);
    }
    
    public void restoreLocationTextFields() {
        locationNameText.setText(location.getName());
        locationExtIdText.setText(location.getExtId());
        locationLatitudeText.setText(location.getLatitude());
        locationLongitudeText.setText(location.getLongitude());
        locationName.setVisibility(View.VISIBLE);
        locationExtId.setVisibility(View.VISIBLE);
        locationLatitude.setVisibility(View.VISIBLE);
        locationLongitude.setVisibility(View.VISIBLE);
    }
    
    public void restoreIndividualTextFields() {
        individualExtIdText.setText(individual.getExtId());
        individualFirstNameText.setText(individual.getFirstName());
        individualLastNameText.setText(individual.getLastName());
        individualDobText.setText(individual.getDob());
        individualExtId.setVisibility(View.VISIBLE);
        individualFirstName.setVisibility(View.VISIBLE);
        individualLastName.setVisibility(View.VISIBLE);
        individualDob.setVisibility(View.VISIBLE);
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
	
	public boolean isExternalInMigration() {
		return isExternalInMigration;
	}

	public void setExternalInMigration(boolean isExternalInMigration) {
		this.isExternalInMigration = isExternalInMigration;
	}

	public void onClick(View view) {
        switch (view.getId()) {
        case R.id.regionBtn: 
            listener.onRegion();
            break;
        case R.id.subRegionBtn: 
            listener.onSubRegion();
            break;
        case R.id.villageBtn: 
            listener.onVillage();
            break;
        case R.id.locationBtn: 
            listener.onLocation();
            break;
        case R.id.roundBtn: 
            listener.onRound();
            break;
        case R.id.individualBtn: 
            listener.onIndividual();
            break;
        }		
	}

    public void setRegionState() {
        regionBtn.setEnabled(true);
        subRegionBtn.setEnabled(false);
        villageBtn.setEnabled(false);
        roundBtn.setEnabled(false);
        locationBtn.setEnabled(false);
        individualBtn.setEnabled(false);        
    }

    public void setSubRegionState() {
        regionBtn.setEnabled(false);
        subRegionBtn.setEnabled(true);
        villageBtn.setEnabled(false);
        roundBtn.setEnabled(false);
        locationBtn.setEnabled(false);
        individualBtn.setEnabled(false);        
    }

    public void setVillageState() {
        regionBtn.setEnabled(false);
        subRegionBtn.setEnabled(false);
        villageBtn.setEnabled(true);
        roundBtn.setEnabled(false);
        locationBtn.setEnabled(false);
        individualBtn.setEnabled(false);        
    }

    public void setLocationState() {
        regionBtn.setEnabled(false);
        subRegionBtn.setEnabled(false);
        villageBtn.setEnabled(false);
        roundBtn.setEnabled(false);
        locationBtn.setEnabled(true);
        individualBtn.setEnabled(false);        
    }

    public void setRoundState() {
        regionBtn.setEnabled(false);
        subRegionBtn.setEnabled(false);
        villageBtn.setEnabled(false);
        roundBtn.setEnabled(true);
        locationBtn.setEnabled(false);
        individualBtn.setEnabled(false);        
    }

    public void setVisitState() {
        regionBtn.setEnabled(false);
        subRegionBtn.setEnabled(false);
        villageBtn.setEnabled(false);
        roundBtn.setEnabled(false);
        locationBtn.setEnabled(false);
        individualBtn.setEnabled(false);        
    }

    public void setIndividualState() {
        regionBtn.setEnabled(false);
        subRegionBtn.setEnabled(false);
        villageBtn.setEnabled(false);
        roundBtn.setEnabled(false);
        locationBtn.setEnabled(false);
        individualBtn.setEnabled(true);        
    }

    public void setFinishState() {
        regionBtn.setEnabled(false);
        subRegionBtn.setEnabled(false);
        villageBtn.setEnabled(false);
        roundBtn.setEnabled(false);
        locationBtn.setEnabled(false);
        individualBtn.setEnabled(false);        
    }

    public void displayLocationInfo() {
        locationNameText.setText(location.getName());
        locationExtIdText.setText(location.getExtId());
        locationLatitudeText.setText(location.getLatitude());
        locationLongitudeText.setText(location.getLongitude());
    }
}
