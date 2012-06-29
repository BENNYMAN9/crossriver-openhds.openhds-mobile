package org.openhds.activity;

import java.util.ArrayList;
import java.util.List;
import org.openhds.activity.R;
import org.openhds.cell.ValueFragmentCell;
import org.openhds.database.DatabaseAdapter;
import org.openhds.fragment.EventFragment;
import org.openhds.fragment.ValueFragment;
import org.openhds.listener.ValueSelectedListener;
import org.openhds.model.Individual;
import org.openhds.model.Location;
import org.openhds.model.LocationHierarchy;
import org.openhds.model.Round;
import android.app.ActionBar;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.support.v4.app.FragmentActivity;

public class UpdateActivity extends FragmentActivity implements OnClickListener, ValueSelectedListener {
	
	private DatabaseAdapter databaseAdapter;
	
	private TextView regionNameText, regionExtIdText, regionName, regionExtId, 
					 subRegionNameText, subRegionExtIdText, subRegionName, subRegionExtId,
					 villageNameText, villageExtIdText, villageName, villageExtId,
					 roundNumberText, roundStartDateText, roundEndDateText, roundNumber, roundStartDate, roundEndDate, 
					 locationNameText, locationExtIdText, locationLatitudeText, locationLongitudeText, locationName, locationExtId, locationLatitude, locationLongitude,
					 individualFirstNameText, individualLastNameText, individualExtIdText, individualDobText, individualFirstName, individualLastName, individualExtId, individualDob;	
	private Button regionBtn, subRegionBtn, villageBtn, roundBtn, locationBtn, createVisitBtn, individualBtn, resetBtn, clearLocationBtn;
	
	ValueFragment valueFragment;
	EventFragment eventFragment;
	
	private boolean REGION_PHASE = true;
	private boolean SUB_REGION_PHASE = false;
	private boolean VILLAGE_PHASE = false;
	private boolean ROUND_PHASE = false;
	private boolean LOCATION_PHASE = false;
	private boolean VISIT_PHASE = false;
	private boolean INDIVIDUAL_PHASE = false;
	private boolean FINISH_PHASE = false;
	
	private LocationHierarchy region;
	private LocationHierarchy subRegion;
	private LocationHierarchy village;
	private Round round;
	private Location location;
	private Individual individual;
	
	private List<LocationHierarchy> regions;
	private List<LocationHierarchy> subRegions;
	private List<LocationHierarchy> villages;
	private List<Round> rounds;
	private List<Location> locations;
	private List<Individual> individuals;
	
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.main);
	    
        databaseAdapter = new DatabaseAdapter(getBaseContext());
        region = new LocationHierarchy();
        subRegion = new LocationHierarchy();
        village = new LocationHierarchy();
        round = new Round();
        location = new Location();
        individual = new Individual();
        
        resetBtn = (Button) findViewById(R.id.resetBtn);
        resetBtn.setOnClickListener(this);
        
        clearLocationBtn = (Button) findViewById(R.id.clearLocationBtn);
        clearLocationBtn.setOnClickListener(this);
        
        createVisitBtn = (Button) findViewById(R.id.createVisitBtn);
        createVisitBtn.setOnClickListener(this);

        regionBtn = (Button) findViewById(R.id.regionBtn);
        regionBtn.setOnClickListener(this);
        regionNameText = (TextView) findViewById(R.id.regionNameText);
        regionExtIdText = (TextView) findViewById(R.id.regionExtIdText);
        regionName = (TextView) findViewById(R.id.regionName);
        regionExtId = (TextView) findViewById(R.id.regionExtId);
	    
        subRegionBtn = (Button) findViewById(R.id.subRegionBtn);
        subRegionBtn.setOnClickListener(this);
        subRegionNameText = (TextView) findViewById(R.id.subRegionNameText);
        subRegionExtIdText = (TextView) findViewById(R.id.subRegionExtIdText);
        subRegionName = (TextView) findViewById(R.id.subRegionName);
        subRegionExtId = (TextView) findViewById(R.id.subRegionExtId);
	    
        villageBtn = (Button) findViewById(R.id.villageBtn);
        villageBtn.setOnClickListener(this);
        villageNameText = (TextView) findViewById(R.id.villageNameText);
        villageExtIdText = (TextView) findViewById(R.id.villageExtIdText);
        villageName = (TextView) findViewById(R.id.villageName);
        villageExtId = (TextView) findViewById(R.id.villageExtId);
        
        roundBtn = (Button) findViewById(R.id.roundBtn);
        roundBtn.setOnClickListener(this);
        roundNumberText = (TextView) findViewById(R.id.roundNumberText);
        roundStartDateText = (TextView) findViewById(R.id.roundStartDateText);
        roundEndDateText = (TextView) findViewById(R.id.roundEndDateText);
        roundNumber = (TextView) findViewById(R.id.roundNumber);
        roundStartDate = (TextView) findViewById(R.id.roundStartDate);
        roundEndDate = (TextView) findViewById(R.id.roundEndDate);
        
        locationBtn = (Button) findViewById(R.id.locationBtn);
        locationBtn.setOnClickListener(this);
        locationNameText = (TextView) findViewById(R.id.locationNameText);
        locationExtIdText = (TextView) findViewById(R.id.locationExtIdText);
        locationLatitudeText = (TextView) findViewById(R.id.locationLatitudeText);
        locationLongitudeText = (TextView) findViewById(R.id.locationLongitudeText);
        locationName = (TextView) findViewById(R.id.locationName);
        locationExtId = (TextView) findViewById(R.id.locationExtId);
        locationLatitude = (TextView) findViewById(R.id.locationLatitude);
        locationLongitude = (TextView) findViewById(R.id.locationLongitude);
        
        individualBtn = (Button) findViewById(R.id.individualBtn);
        individualBtn.setOnClickListener(this);
        individualExtIdText = (TextView) findViewById(R.id.individualExtIdText);
        individualFirstNameText = (TextView) findViewById(R.id.individualFirstNameText);
        individualLastNameText = (TextView) findViewById(R.id.individualLastNameText);
        individualDobText = (TextView) findViewById(R.id.individualDobText);
        individualExtId = (TextView) findViewById(R.id.individualExtId);
        individualFirstName = (TextView) findViewById(R.id.individualFirstName);
        individualLastName = (TextView) findViewById(R.id.individualLastName);
        individualDob = (TextView) findViewById(R.id.individualDob);
        
		valueFragment = (ValueFragment)getSupportFragmentManager().findFragmentById(R.id.valueFragment);
		eventFragment = (EventFragment)getSupportFragmentManager().findFragmentById(R.id.eventFragment);
	    	       	    
	    ActionBar actionBar = getActionBar();
	    actionBar.show();
	}
	
	public void reset() {
		setPhase("REGION");
        		
		if (valueFragment != null) {
			valueFragment.reset();
		}
	}
		
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {	
    	MenuInflater inflater = getMenuInflater();
    	inflater.inflate(R.menu.mainmenu, menu);
        return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.configure_server:
                createPreferencesMenu();
                return true;
            case R.id.sync_database:
                createSyncDatabaseMenu();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
    
    private void createPreferencesMenu() {
        Intent i = new Intent(this, ServerPreferencesActivity.class);
        startActivity(i);
    }
    
    private void createSyncDatabaseMenu() {
        Intent i = new Intent(this, SyncDatabaseActivity.class);
        startActivity(i);
    }
    
    private void loadRegionValueData() {
    	regions = databaseAdapter.getAllRegions("LGA");
    	if (valueFragment != null) {
    		List<ValueFragmentCell> list = new ArrayList<ValueFragmentCell>();
    		for (LocationHierarchy item : regions) {
    			ValueFragmentCell cell = new ValueFragmentCell(item.getName(), item.getExtId());
    			list.add(cell);
    		}
    		valueFragment.setContent(list);
    	}
    }
    
    private void loadSubRegionValueData() {
      	subRegions = databaseAdapter.getAllSubRegionsOfRegion(region);
    	if (valueFragment != null) {
    		List<ValueFragmentCell> list = new ArrayList<ValueFragmentCell>();
    		for (LocationHierarchy item : subRegions) {
    			ValueFragmentCell cell = new ValueFragmentCell(item.getName(), item.getExtId());
    			list.add(cell);
    		}
    		valueFragment.setContent(list);
    	}
    }
    
    private void loadVillageValueData() {
      	villages = databaseAdapter.getAllSubRegionsOfRegion(subRegion);
    	if (valueFragment != null) {
    		List<ValueFragmentCell> list = new ArrayList<ValueFragmentCell>();
    		for (LocationHierarchy item : villages) {
    			ValueFragmentCell cell = new ValueFragmentCell(item.getName(), item.getExtId());
    			list.add(cell);
    		}
    		valueFragment.setContent(list);
    	}
    }
    
    private void loadRoundValueData() {
      	rounds = databaseAdapter.getAllRounds();
    	if (valueFragment != null) {
    		List<ValueFragmentCell> list = new ArrayList<ValueFragmentCell>();
    		for (Round item : rounds) {
    			ValueFragmentCell cell = new ValueFragmentCell("Round: " + item.getRoundNumber(), "");
    			list.add(cell);
    		}
    		valueFragment.setContent(list);
    	}
    }
    
    private void loadLocationValueData() {
      	locations = databaseAdapter.getAllLocationsOfVillage(village);
    	if (valueFragment != null) {
    		List<ValueFragmentCell> list = new ArrayList<ValueFragmentCell>();
    		for (Location item : locations) {
    			ValueFragmentCell cell = new ValueFragmentCell(item.getName(), item.getExtId());
    			list.add(cell);
    		}
    		valueFragment.setContent(list);
    	}
    }
    
    private void loadIndividualValueData() {
      	individuals = databaseAdapter.getIndividualsAtLocation(location);
    	if (valueFragment != null) {
    		List<ValueFragmentCell> list = new ArrayList<ValueFragmentCell>();
    		for (Individual item : individuals) {
    			ValueFragmentCell cell = new ValueFragmentCell(item.getFirstName() + " " + item.getLastName(), item.getExtId());
    			list.add(cell);
    		}
    		valueFragment.setContent(list);
    	}
    }
        
	public void onClick(View view) {
		switch (view.getId()) {
			case R.id.regionBtn:
				loadRegionValueData();
				break;
			case R.id.subRegionBtn:
				loadSubRegionValueData();
				break;
			case R.id.villageBtn:
				loadVillageValueData();
				break;
			case R.id.roundBtn:
				loadRoundValueData();
				break;
			case R.id.locationBtn:
				loadLocationValueData();
				break;
			case R.id.createVisitBtn:
				setPhase("INDIVIDUAL");
				break;
			case R.id.individualBtn:
				loadIndividualValueData();
				break;
			case R.id.clearLocationBtn:
				setPhase("LOCATION");
				break;
			case R.id.resetBtn:
				reset();
		}	
	}
	
	private String getPhase() {
		if (FINISH_PHASE)
			return "FINISH";
		else if (INDIVIDUAL_PHASE)
			return "INDIVIDUAL";
		else if (VISIT_PHASE)
			return "VISIT";
		else if (LOCATION_PHASE)
			return "LOCATION";
		else if (ROUND_PHASE)
			return "ROUND";
		else if (VILLAGE_PHASE)
			return "VILLAGE";
		else if (SUB_REGION_PHASE)
			return "SUB_REGION";
		else
			return "REGION";
	}

	public void onValueSelected(int position) {
		String phase = getPhase();
		if (phase.equals("REGION")) {
			region = regions.get(position);
			regionName.setVisibility(View.VISIBLE);
			regionExtId.setVisibility(View.VISIBLE);
			regionNameText.setText(region.getName());
			regionExtIdText.setText(region.getExtId());
			setPhase("SUB_REGION");
		}
		else if (phase.equals("SUB_REGION")) {
			subRegion = subRegions.get(position);
			subRegionName.setVisibility(View.VISIBLE);
			subRegionExtId.setVisibility(View.VISIBLE);
			subRegionNameText.setText(subRegion.getName());
			subRegionExtIdText.setText(subRegion.getExtId());
			setPhase("VILLAGE");
		}
		else if (phase.equals("VILLAGE")) {
			village = villages.get(position);
			villageName.setVisibility(View.VISIBLE);
			villageExtId.setVisibility(View.VISIBLE);
			villageNameText.setText(village.getName());
			villageExtIdText.setText(village.getExtId());
			setPhase("ROUND");
		}
		else if (phase.equals("ROUND")) {
			round = rounds.get(position);
			roundNumber.setVisibility(View.VISIBLE);
			roundStartDate.setVisibility(View.VISIBLE);
			roundEndDate.setVisibility(View.VISIBLE);
			roundNumberText.setText(round.getRoundNumber());
			roundStartDateText.setText(round.getStartDate());
			roundEndDateText.setText(round.getEndDate());
			setPhase("LOCATION");
		}
		else if (phase.equals("LOCATION")) {
			location = locations.get(position);
			locationName.setVisibility(View.VISIBLE);
			locationExtId.setVisibility(View.VISIBLE);
			locationLatitude.setVisibility(View.VISIBLE);
			locationLongitude.setVisibility(View.VISIBLE);
			locationNameText.setText(location.getName());
			locationExtIdText.setText(location.getExtId());
			locationLatitudeText.setText(location.getLatitude());
			locationLongitudeText.setText(location.getLongitude());
			setPhase("VISIT");
		}
		else if (phase.equals("INDIVIDUAL")) {
			individual = individuals.get(position);
			individualExtId.setVisibility(View.VISIBLE);
			individualFirstName.setVisibility(View.VISIBLE);
			individualLastName.setVisibility(View.VISIBLE);
			individualDob.setVisibility(View.VISIBLE);
			individualExtIdText.setText(individual.getExtId());
			individualFirstNameText.setText(individual.getFirstName());
			individualLastNameText.setText(individual.getLastName());
			individualDobText.setText(individual.getDob());
			setPhase("FINISH");
		}
	}
	
	private void clearRegionTextFields() {
		regionNameText.setText("");
		regionExtIdText.setText("");
		regionName.setVisibility(View.INVISIBLE);
		regionExtId.setVisibility(View.INVISIBLE);
	}
	
	private void clearSubRegionTextFields() {
		subRegionNameText.setText("");
		subRegionExtIdText.setText("");
		subRegionName.setVisibility(View.INVISIBLE);
		subRegionExtId.setVisibility(View.INVISIBLE);
	}
	
	private void clearVillageTextFields() {
		villageNameText.setText("");
		villageExtIdText.setText("");
		villageName.setVisibility(View.INVISIBLE);
		villageExtId.setVisibility(View.INVISIBLE);
	}
	
	private void clearRoundTextFields() {
		roundNumberText.setText("");
		roundStartDateText.setText("");
		roundEndDateText.setText("");
		roundNumber.setVisibility(View.INVISIBLE);
		roundStartDate.setVisibility(View.INVISIBLE);
		roundEndDate.setVisibility(View.INVISIBLE);
	}
	
	private void clearLocationTextFields() {
		locationNameText.setText("");
		locationExtIdText.setText("");
		locationLatitudeText.setText("");
		locationLongitudeText.setText("");
		locationName.setVisibility(View.INVISIBLE);
		locationExtId.setVisibility(View.INVISIBLE);
		locationLatitude.setVisibility(View.INVISIBLE);
		locationLongitude.setVisibility(View.INVISIBLE);
	}
	
	private void clearIndividualTextFields() {
		individualExtIdText.setText("");
		individualFirstNameText.setText("");
		individualLastNameText.setText("");
		individualDobText.setText("");
		individualExtId.setVisibility(View.INVISIBLE);
		individualFirstName.setVisibility(View.INVISIBLE);
		individualLastName.setVisibility(View.INVISIBLE);
		individualDob.setVisibility(View.INVISIBLE);
	}
	
	private void setPhase(String phase) {
		if (phase.equals("REGION")) {
			REGION_PHASE = true;
			SUB_REGION_PHASE = false;
			VILLAGE_PHASE = false;
			ROUND_PHASE = false;
			LOCATION_PHASE = false;
			VISIT_PHASE = false;
			INDIVIDUAL_PHASE = false;
			FINISH_PHASE = false;
			
			regionBtn.setEnabled(true);
			resetBtn.setEnabled(false);
			createVisitBtn.setEnabled(false);
			clearLocationBtn.setEnabled(false);
			subRegionBtn.setEnabled(false);
			villageBtn.setEnabled(false);
			roundBtn.setEnabled(false);
			locationBtn.setEnabled(false);
			individualBtn.setEnabled(false);
			
			clearRegionTextFields();
			clearSubRegionTextFields();
			clearVillageTextFields();
			clearRoundTextFields();
			clearLocationTextFields();
			clearIndividualTextFields();
		
			region = new LocationHierarchy();
		    subRegion = new LocationHierarchy();
		    village = new LocationHierarchy();
		    round = new Round();
		    location = new Location();
		    individual = new Individual();
		}
		else if (phase.equals("SUB_REGION")) {
			REGION_PHASE = false;
			SUB_REGION_PHASE = true;
			VILLAGE_PHASE = false;
			ROUND_PHASE = false;
			LOCATION_PHASE = false;
			VISIT_PHASE = false;
			INDIVIDUAL_PHASE = false;
			FINISH_PHASE = false;
			
			regionBtn.setEnabled(false);
			resetBtn.setEnabled(true);
			createVisitBtn.setEnabled(false);
			clearLocationBtn.setEnabled(false);
			subRegionBtn.setEnabled(true);
			villageBtn.setEnabled(false);
			roundBtn.setEnabled(false);
			locationBtn.setEnabled(false);
			individualBtn.setEnabled(false);
			
			clearSubRegionTextFields();
			clearVillageTextFields();
			clearRoundTextFields();
			clearLocationTextFields();
			clearIndividualTextFields();
			
		    subRegion = new LocationHierarchy();
		    village = new LocationHierarchy();
		    round = new Round();
		    location = new Location();
		    individual = new Individual();
		}
		else if (phase.equals("VILLAGE")) {
			REGION_PHASE = false;
			SUB_REGION_PHASE = false;
			VILLAGE_PHASE = true;
			ROUND_PHASE = false;
			LOCATION_PHASE = false;
			VISIT_PHASE = false;
			INDIVIDUAL_PHASE = false;
			FINISH_PHASE = false;
			
			regionBtn.setEnabled(false);
			resetBtn.setEnabled(true);
			createVisitBtn.setEnabled(false);
			clearLocationBtn.setEnabled(false);
			subRegionBtn.setEnabled(false);
			villageBtn.setEnabled(true);
			roundBtn.setEnabled(false);
			locationBtn.setEnabled(false);
			individualBtn.setEnabled(false);
			
			clearVillageTextFields();
			clearRoundTextFields();
			clearLocationTextFields();
			clearIndividualTextFields();
			
		    village = new LocationHierarchy();
		    round = new Round();
		    location = new Location();
		    individual = new Individual();
		}
		else if (phase.equals("ROUND")) {
			REGION_PHASE = false;
			SUB_REGION_PHASE = false;
			VILLAGE_PHASE = false;
			ROUND_PHASE = true;
			LOCATION_PHASE = false;
			VISIT_PHASE = false;
			INDIVIDUAL_PHASE = false;
			FINISH_PHASE = false;
			
			regionBtn.setEnabled(false);
			resetBtn.setEnabled(true);
			createVisitBtn.setEnabled(false);
			clearLocationBtn.setEnabled(false);
			subRegionBtn.setEnabled(false);
			villageBtn.setEnabled(false);
			roundBtn.setEnabled(true);
			locationBtn.setEnabled(false);
			individualBtn.setEnabled(false);
			
			clearRoundTextFields();
			clearLocationTextFields();
			clearIndividualTextFields();
			
			round = new Round();
		    location = new Location();
		    individual = new Individual();
		}
		else if (phase.equals("LOCATION")) {
			REGION_PHASE = false;
			SUB_REGION_PHASE = false;
			VILLAGE_PHASE = false;
			ROUND_PHASE = false;
			LOCATION_PHASE = true;
			VISIT_PHASE = false;
			INDIVIDUAL_PHASE = false;
			FINISH_PHASE = false;
			
			regionBtn.setEnabled(false);
			resetBtn.setEnabled(true);
			createVisitBtn.setEnabled(false);
			clearLocationBtn.setEnabled(false);
			subRegionBtn.setEnabled(false);
			villageBtn.setEnabled(false);
			roundBtn.setEnabled(false);
			locationBtn.setEnabled(true);
			individualBtn.setEnabled(false);
			
			clearLocationTextFields();
			clearIndividualTextFields();
			
			location = new Location();
		    individual = new Individual();
		}
		else if (phase.equals("VISIT")) {
			REGION_PHASE = false;
			SUB_REGION_PHASE = false;
			VILLAGE_PHASE = false;
			ROUND_PHASE = false;
			LOCATION_PHASE = false;
			VISIT_PHASE = true;
			INDIVIDUAL_PHASE = false;
			FINISH_PHASE = false;
			
			regionBtn.setEnabled(false);
			resetBtn.setEnabled(true);
			createVisitBtn.setEnabled(true);
			clearLocationBtn.setEnabled(true);
			subRegionBtn.setEnabled(false);
			villageBtn.setEnabled(false);
			roundBtn.setEnabled(false);
			locationBtn.setEnabled(false);
			individualBtn.setEnabled(false);
			
			clearIndividualTextFields();
			
		    individual = new Individual();
		}
		else if (phase.equals("INDIVIDUAL")) {
			REGION_PHASE = false;
			SUB_REGION_PHASE = false;
			VILLAGE_PHASE = false;
			ROUND_PHASE = false;
			LOCATION_PHASE = false;
			VISIT_PHASE = false;
			INDIVIDUAL_PHASE = true;
			FINISH_PHASE = false;
			
			regionBtn.setEnabled(false);
			resetBtn.setEnabled(true);
			createVisitBtn.setEnabled(false);
			clearLocationBtn.setEnabled(true);
			subRegionBtn.setEnabled(false);
			villageBtn.setEnabled(false);
			roundBtn.setEnabled(false);
			locationBtn.setEnabled(false);
			individualBtn.setEnabled(true);
			
			clearIndividualTextFields();
			
		    individual = new Individual();
		}
		else if (phase.equals("FINISH")) {
			REGION_PHASE = false;
			SUB_REGION_PHASE = false;
			VILLAGE_PHASE = false;
			ROUND_PHASE = false;
			LOCATION_PHASE = false;
			VISIT_PHASE = false;
			INDIVIDUAL_PHASE = false;
			FINISH_PHASE = true;
			
			regionBtn.setEnabled(false);
			resetBtn.setEnabled(true);
			createVisitBtn.setEnabled(false);
			clearLocationBtn.setEnabled(true);
			subRegionBtn.setEnabled(false);
			villageBtn.setEnabled(false);
			roundBtn.setEnabled(false);
			locationBtn.setEnabled(false);
			individualBtn.setEnabled(false);
		}
	}
}
