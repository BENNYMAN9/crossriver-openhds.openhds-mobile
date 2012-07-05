package org.openhds.mobile.activity;

import java.util.ArrayList;
import java.util.List;

import org.openhds.mobile.InstanceProviderAPI;
import org.openhds.mobile.R;
import org.openhds.mobile.cell.ValueFragmentCell;
import org.openhds.mobile.database.DatabaseAdapter;
import org.openhds.mobile.fragment.EventFragment;
import org.openhds.mobile.fragment.SelectionFragment;
import org.openhds.mobile.fragment.ValueFragment;
import org.openhds.mobile.listener.OdkFormLoadListener;
import org.openhds.mobile.listener.ValueSelectedListener;
import org.openhds.mobile.model.Individual;
import org.openhds.mobile.model.Location;
import org.openhds.mobile.model.LocationHierarchy;
import org.openhds.mobile.model.Record;
import org.openhds.mobile.model.Round;
import org.openhds.mobile.model.UpdateEvent;
import org.openhds.mobile.model.Visit;
import org.openhds.mobile.task.OdkFormLoadTask;
import android.app.ActionBar;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.support.v4.app.FragmentActivity;

public class UpdateActivity extends FragmentActivity implements OnClickListener, ValueSelectedListener, OdkFormLoadListener {
	
	private DatabaseAdapter databaseAdapter;
	
	private TextView regionNameText, regionExtIdText, regionName, regionExtId, 
					 subRegionNameText, subRegionExtIdText, subRegionName, subRegionExtId,
					 villageNameText, villageExtIdText, villageName, villageExtId,
					 roundNumberText, roundStartDateText, roundEndDateText, roundNumber, roundStartDate, roundEndDate, 
					 locationNameText, locationExtIdText, locationLatitudeText, locationLongitudeText, locationName, locationExtId, locationLatitude, locationLongitude,
					 individualFirstNameText, individualLastNameText, individualExtIdText, individualDobText, individualFirstName, individualLastName, individualExtId, individualDob;	
	private Button regionBtn, subRegionBtn, villageBtn, roundBtn, locationBtn, individualBtn, 
	 			   createVisitBtn, clearLocationBtn, resetBtn, deathBtn;
	
	SelectionFragment selectionFragment;
	ValueFragment valueFragment;
	EventFragment eventFragment;
	
	private final int SELECTED_XFORM = 1;
	
	private Uri contentUri;
	
	private boolean REGION_PHASE = true;
	private boolean SUB_REGION_PHASE = false;
	private boolean VILLAGE_PHASE = false;
	private boolean ROUND_PHASE = false;
	private boolean LOCATION_PHASE = false;
	private boolean VISIT_PHASE = false;
	private boolean INDIVIDUAL_PHASE = false;
	private boolean FINISH_PHASE = false;
				
    @Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.main);
	    
        databaseAdapter = new DatabaseAdapter(getBaseContext());
        
        resetBtn = (Button) findViewById(R.id.resetBtn);
        resetBtn.setOnClickListener(this);
        
        clearLocationBtn = (Button) findViewById(R.id.clearLocationBtn);
        clearLocationBtn.setOnClickListener(this);
        
        createVisitBtn = (Button) findViewById(R.id.createVisitBtn);
        createVisitBtn.setOnClickListener(this);
        
        deathBtn = (Button) findViewById(R.id.deathBtn);
        deathBtn.setOnClickListener(this);

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
        
    	selectionFragment = (SelectionFragment)getSupportFragmentManager().findFragmentById(R.id.selectionFragment);
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
    	List<LocationHierarchy> regions = databaseAdapter.getAllRegions("LGA");
    	selectionFragment.setRegions(regions);
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
    	List<LocationHierarchy> subRegions = databaseAdapter.getAllSubRegionsOfRegion(selectionFragment.getRegion());
      	selectionFragment.setSubRegions(subRegions);
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
      	List<LocationHierarchy> villages = databaseAdapter.getAllSubRegionsOfRegion(selectionFragment.getSubRegion());
    	selectionFragment.setVillages(villages);
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
    	List<Round> rounds = databaseAdapter.getAllRounds();
      	selectionFragment.setRounds(rounds);
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
    	List<Location> locations = databaseAdapter.getAllLocationsOfVillage(selectionFragment.getVillage());
      	selectionFragment.setLocations(locations);
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
    	List<Individual> individuals = databaseAdapter.getIndividualsAtLocation(selectionFragment.getLocation());
      	selectionFragment.setIndividuals(individuals);
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
			case R.id.regionBtn: {
				loadRegionValueData();
				break;
			}
			case R.id.subRegionBtn: {
				loadSubRegionValueData();
				break;
			}
			case R.id.villageBtn: {
				loadVillageValueData();
				break;
			}
			case R.id.roundBtn: {
				loadRoundValueData();
				break;
			}
			case R.id.locationBtn: {
				loadLocationValueData();
				break;
			}
			case R.id.createVisitBtn: {
				selectionFragment.createVisit();
				Record record = new Record(selectionFragment.getVillage(), selectionFragment.getLocation(),
						selectionFragment.getRound(), selectionFragment.getIndividual(), selectionFragment.getVisit());
				new OdkFormLoadTask(this, getContentResolver(), record, UpdateEvent.VISIT).execute();				
				break;
			}
			case R.id.individualBtn: {
				loadIndividualValueData();
				break;
			}
			case R.id.clearLocationBtn: {
				setPhase("LOCATION");
				break;
			}
			case R.id.resetBtn: {
				reset();
				break;
			}
			case R.id.deathBtn: {
				Record record = new Record(selectionFragment.getVillage(), selectionFragment.getLocation(),
						selectionFragment.getRound(), selectionFragment.getIndividual(), selectionFragment.getVisit());
				new OdkFormLoadTask(this, getContentResolver(), record, UpdateEvent.DEATH).execute();
			}
		}	
	}
	
	public void onSuccess(Uri contentUri) {
		this.contentUri = contentUri;
		startActivityForResult(new Intent(Intent.ACTION_EDIT, contentUri), SELECTED_XFORM);
	}
	
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch(requestCode) {
			case SELECTED_XFORM: {
			
				if (resultCode == RESULT_OK) {
					Cursor cursor = getContentResolver().query(contentUri, null, 
							InstanceProviderAPI.InstanceColumns.STATUS + "=?", new String[] {InstanceProviderAPI.STATUS_COMPLETE}, null);
					if (cursor.moveToNext()) {
						setPhase("INDIVIDUAL");
					}
				}
			
			}
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
			selectionFragment.setRegion(selectionFragment.getRegions().get(position));
			regionName.setVisibility(View.VISIBLE);
			regionExtId.setVisibility(View.VISIBLE);
			regionNameText.setText(selectionFragment.getRegion().getName());
			regionExtIdText.setText(selectionFragment.getRegion().getExtId());
			setPhase("SUB_REGION");
		}
		else if (phase.equals("SUB_REGION")) {
			selectionFragment.setSubRegion(selectionFragment.getSubRegions().get(position));
			subRegionName.setVisibility(View.VISIBLE);
			subRegionExtId.setVisibility(View.VISIBLE);
			subRegionNameText.setText(selectionFragment.getSubRegion().getName());
			subRegionExtIdText.setText(selectionFragment.getSubRegion().getExtId());
			setPhase("VILLAGE");
		}
		else if (phase.equals("VILLAGE")) {
			selectionFragment.setVillage(selectionFragment.getVillages().get(position));
			villageName.setVisibility(View.VISIBLE);
			villageExtId.setVisibility(View.VISIBLE);
			villageNameText.setText(selectionFragment.getVillage().getName());
			villageExtIdText.setText(selectionFragment.getVillage().getExtId());
			setPhase("ROUND");
		}
		else if (phase.equals("ROUND")) {
			selectionFragment.setRound(selectionFragment.getRounds().get(position));
			roundNumber.setVisibility(View.VISIBLE);
			roundStartDate.setVisibility(View.VISIBLE);
			roundEndDate.setVisibility(View.VISIBLE);
			roundNumberText.setText(selectionFragment.getRound().getRoundNumber());
			roundStartDateText.setText(selectionFragment.getRound().getStartDate());
			roundEndDateText.setText(selectionFragment.getRound().getEndDate());
			setPhase("LOCATION");
		}
		else if (phase.equals("LOCATION")) {
			selectionFragment.setLocation(selectionFragment.getLocations().get(position));
			locationName.setVisibility(View.VISIBLE);
			locationExtId.setVisibility(View.VISIBLE);
			locationLatitude.setVisibility(View.VISIBLE);
			locationLongitude.setVisibility(View.VISIBLE);
			locationNameText.setText(selectionFragment.getLocation().getName());
			locationExtIdText.setText(selectionFragment.getLocation().getExtId());
			locationLatitudeText.setText(selectionFragment.getLocation().getLatitude());
			locationLongitudeText.setText(selectionFragment.getLocation().getLongitude());
			setPhase("VISIT");
		}
		else if (phase.equals("INDIVIDUAL")) {
			selectionFragment.setIndividual(selectionFragment.getIndividuals().get(position));
			individualExtId.setVisibility(View.VISIBLE);
			individualFirstName.setVisibility(View.VISIBLE);
			individualLastName.setVisibility(View.VISIBLE);
			individualDob.setVisibility(View.VISIBLE);
			individualExtIdText.setText(selectionFragment.getIndividual().getExtId());
			individualFirstNameText.setText(selectionFragment.getIndividual().getFirstName());
			individualLastNameText.setText(selectionFragment.getIndividual().getLastName());
			individualDobText.setText(selectionFragment.getIndividual().getDob());
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
			
			resetBtn.setEnabled(false);
			createVisitBtn.setEnabled(false);
			clearLocationBtn.setEnabled(false);
			deathBtn.setEnabled(false);
			regionBtn.setEnabled(true);
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
		
			selectionFragment.setRegion(new LocationHierarchy());
			selectionFragment.setSubRegion(new LocationHierarchy());
			selectionFragment.setVillage(new LocationHierarchy());
			selectionFragment.setRound(new Round());
			selectionFragment.setLocation(new Location());
			selectionFragment.setIndividual(new Individual());
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
			
			resetBtn.setEnabled(true);
			createVisitBtn.setEnabled(false);
			clearLocationBtn.setEnabled(false);
			deathBtn.setEnabled(false);
			regionBtn.setEnabled(false);
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
			
			selectionFragment.setSubRegion(new LocationHierarchy());
			selectionFragment.setVillage(new LocationHierarchy());
			selectionFragment.setRound(new Round());
			selectionFragment.setLocation(new Location());
			selectionFragment.setIndividual(new Individual());
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

			resetBtn.setEnabled(true);
			createVisitBtn.setEnabled(false);
			clearLocationBtn.setEnabled(false);
			deathBtn.setEnabled(false);
			regionBtn.setEnabled(false);
			subRegionBtn.setEnabled(false);
			villageBtn.setEnabled(true);
			roundBtn.setEnabled(false);
			locationBtn.setEnabled(false);
			individualBtn.setEnabled(false);
			
			clearVillageTextFields();
			clearRoundTextFields();
			clearLocationTextFields();
			clearIndividualTextFields();
			
			selectionFragment.setVillage(new LocationHierarchy());
			selectionFragment.setRound(new Round());
			selectionFragment.setLocation(new Location());
			selectionFragment.setIndividual(new Individual());
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
			
			resetBtn.setEnabled(true);
			createVisitBtn.setEnabled(false);
			clearLocationBtn.setEnabled(false);
			deathBtn.setEnabled(false);
			regionBtn.setEnabled(false);
			subRegionBtn.setEnabled(false);
			villageBtn.setEnabled(false);
			roundBtn.setEnabled(true);
			locationBtn.setEnabled(false);
			individualBtn.setEnabled(false);
			
			clearRoundTextFields();
			clearLocationTextFields();
			clearIndividualTextFields();
			
			selectionFragment.setRound(new Round());
			selectionFragment.setLocation(new Location());
			selectionFragment.setIndividual(new Individual());
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
			
			resetBtn.setEnabled(true);
			createVisitBtn.setEnabled(false);
			clearLocationBtn.setEnabled(false);
			deathBtn.setEnabled(false);
			regionBtn.setEnabled(false);
			subRegionBtn.setEnabled(false);
			villageBtn.setEnabled(false);
			roundBtn.setEnabled(false);
			locationBtn.setEnabled(true);
			individualBtn.setEnabled(false);
			
			clearLocationTextFields();
			clearIndividualTextFields();
			
			selectionFragment.setLocation(new Location());
			selectionFragment.setIndividual(new Individual());
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
			
			resetBtn.setEnabled(true);
			createVisitBtn.setEnabled(true);
			clearLocationBtn.setEnabled(true);
			deathBtn.setEnabled(false);
			regionBtn.setEnabled(false);
			subRegionBtn.setEnabled(false);
			villageBtn.setEnabled(false);
			roundBtn.setEnabled(false);
			locationBtn.setEnabled(false);
			individualBtn.setEnabled(false);
			
			clearIndividualTextFields();
			
			selectionFragment.setIndividual(new Individual());
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
			
			resetBtn.setEnabled(true);
			createVisitBtn.setEnabled(false);
			clearLocationBtn.setEnabled(true);
			deathBtn.setEnabled(false);
			regionBtn.setEnabled(false);
			subRegionBtn.setEnabled(false);
			villageBtn.setEnabled(false);
			roundBtn.setEnabled(false);
			locationBtn.setEnabled(false);
			individualBtn.setEnabled(true);
			
			clearIndividualTextFields();
			
			selectionFragment.setIndividual(new Individual());
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
			deathBtn.setEnabled(true);
		}
	}
}
