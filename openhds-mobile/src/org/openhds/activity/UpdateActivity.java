package org.openhds.activity;

import java.util.ArrayList;
import java.util.List;
import org.openhds.activity.R;
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
	
	private TextView regionText, subRegionText, villageText, roundText, locationText, individualText;	
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
        regionText = (TextView) findViewById(R.id.regionText);
	    
        subRegionBtn = (Button) findViewById(R.id.subRegionBtn);
        subRegionBtn.setOnClickListener(this);
        subRegionText = (TextView) findViewById(R.id.subRegionText);
	    
        villageBtn = (Button) findViewById(R.id.villageBtn);
        villageBtn.setOnClickListener(this);
        villageText = (TextView) findViewById(R.id.villageText);
        
        roundBtn = (Button) findViewById(R.id.roundBtn);
        roundBtn.setOnClickListener(this);
        roundText = (TextView) findViewById(R.id.roundText);
        
        locationBtn = (Button) findViewById(R.id.locationBtn);
        locationBtn.setOnClickListener(this);
        locationText = (TextView) findViewById(R.id.locationText);
        
        individualBtn = (Button) findViewById(R.id.individualBtn);
        individualBtn.setOnClickListener(this);
        individualText = (TextView) findViewById(R.id.individualText);
        
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
    		List<String> list = new ArrayList<String>();
    		for (LocationHierarchy item : regions) {
    			list.add(item.getName());
    		}
    		valueFragment.setContent(list);
    	}
    }
    
    private void loadSubRegionValueData() {
      	subRegions = databaseAdapter.getAllSubRegionsOfRegion(region);
    	if (valueFragment != null) {
    		List<String> list = new ArrayList<String>();
    		for (LocationHierarchy item : subRegions) {
    			list.add(item.getName());
    		}
    		valueFragment.setContent(list);
    	}
    }
    
    private void loadVillageValueData() {
      	villages = databaseAdapter.getAllSubRegionsOfRegion(subRegion);
    	if (valueFragment != null) {
    		List<String> list = new ArrayList<String>();
    		for (LocationHierarchy item : villages) {
    			list.add(item.getName());
    		}
    		valueFragment.setContent(list);
    	}
    }
    
    private void loadRoundValueData() {
      	rounds = databaseAdapter.getAllRounds();
    	if (valueFragment != null) {
    		List<String> list = new ArrayList<String>();
    		for (Round item : rounds) {
    			list.add(item.getRoundNumber());
    		}
    		valueFragment.setContent(list);
    	}
    }
    
    private void loadLocationValueData() {
      	locations = databaseAdapter.getAllLocationsOfVillage(village);
    	if (valueFragment != null) {
    		List<String> list = new ArrayList<String>();
    		for (Location item : locations) {
    			list.add(item.getName());
    		}
    		valueFragment.setContent(list);
    	}
    }
    
    private void loadIndividualValueData() {
      	individuals = databaseAdapter.getIndividualsAtLocation(location);
    	if (valueFragment != null) {
    		List<String> list = new ArrayList<String>();
    		for (Individual item : individuals) {
    			list.add(item.getExtId());
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
			regionText.setText(region.getName());
			setPhase("SUB_REGION");
		}
		else if (phase.equals("SUB_REGION")) {
			subRegion = subRegions.get(position);
			subRegionText.setText(subRegion.getName());
			setPhase("VILLAGE");
		}
		else if (phase.equals("VILLAGE")) {
			village = villages.get(position);
			villageText.setText(village.getName());
			setPhase("ROUND");
		}
		else if (phase.equals("ROUND")) {
			round = rounds.get(position);
			roundText.setText(round.getRoundNumber());
			setPhase("LOCATION");
		}
		else if (phase.equals("LOCATION")) {
			location = locations.get(position);
			locationText.setText(location.getName());
			setPhase("VISIT");
		}
		else if (phase.equals("INDIVIDUAL")) {
			individual = individuals.get(position);
			individualText.setText(individual.getExtId());
			setPhase("FINISH");
		}
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
			
			regionText.setText("");
			subRegionText.setText("");
			villageText.setText("");
			roundText.setText("");
			locationText.setText("");
			individualText.setText("");
			
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
			
			subRegionText.setText("");
			villageText.setText("");
			roundText.setText("");
			locationText.setText("");
			individualText.setText("");
			
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
			
			villageText.setText("");
			roundText.setText("");
			locationText.setText("");
			individualText.setText("");
			
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
			
			roundText.setText("");
			locationText.setText("");
			individualText.setText("");
			
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
			
			locationText.setText("");
			individualText.setText("");
			
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
			
			individualText.setText("");
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
			
			individualText.setText("");
			
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
