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
import org.openhds.mobile.model.FieldWorker;
import org.openhds.mobile.model.Individual;
import org.openhds.mobile.model.Location;
import org.openhds.mobile.model.LocationHierarchy;
import org.openhds.mobile.model.Record;
import org.openhds.mobile.model.Round;
import org.openhds.mobile.model.SocialGroup;
import org.openhds.mobile.model.UpdateEvent;
import org.openhds.mobile.model.UpdateParams;
import org.openhds.mobile.model.Visit;
import org.openhds.mobile.task.OdkFormLoadTask;
import android.app.ActionBar;
import android.app.AlertDialog;
import android.content.DialogInterface;
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
import android.widget.Toast;
import android.support.v4.app.FragmentActivity;

public class UpdateActivity extends FragmentActivity implements OnClickListener, ValueSelectedListener, OdkFormLoadListener {
	
	// datasource
	private DatabaseAdapter databaseAdapter;
	
	// text widgets, these become enabled and disabled according to the current phase
	private TextView loginGreetingText,
					 regionNameText, regionExtIdText, regionName, regionExtId, 
					 subRegionNameText, subRegionExtIdText, subRegionName, subRegionExtId,
					 villageNameText, villageExtIdText, villageName, villageExtId,
					 roundNumberText, roundStartDateText, roundEndDateText, roundNumber, roundStartDate, roundEndDate, 
					 locationNameText, locationExtIdText, locationLatitudeText, locationLongitudeText, locationName, locationExtId, locationLatitude, locationLongitude,
					 individualFirstNameText, individualLastNameText, individualExtIdText, individualDobText, individualFirstName, individualLastName, individualExtId, individualDob;	
	private Button regionBtn, subRegionBtn, villageBtn, roundBtn, locationBtn, individualBtn, 
	 			   createVisitBtn, clearLocationBtn, deathBtn, finishVisitBtn;
	
	// logged in fieldworker
	private FieldWorker fieldWorker;
	
	// this activity manages three fragments
	private SelectionFragment selectionFragment;
	private ValueFragment valueFragment;
	private EventFragment eventFragment;
	
	private final int SELECTED_XFORM = 1;
	
	// the uri of the last viewed xform
	private Uri contentUri;
	
	// status flags indicating a dialog, used for restoring the activity
	private boolean formUnFinished = false;
	private boolean invalidStatus = false;
	private boolean householdSelection = false;
	
	// the workflow for this activity is arranged into multiple phases
	private boolean REGION_PHASE = true;
	private boolean SUB_REGION_PHASE = false;
	private boolean VILLAGE_PHASE = false;
	private boolean ROUND_PHASE = false;
	private boolean LOCATION_PHASE = false;
	private boolean VISIT_PHASE = false;
	private boolean INDIVIDUAL_PHASE = false;
	private boolean XFORMS_PHASE = false;
				
    @Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.main);    	   
	    processExtras();
	   
        databaseAdapter = new DatabaseAdapter(getBaseContext());
                
        finishVisitBtn = (Button) findViewById(R.id.finishVisitBtn);
        finishVisitBtn.setOnClickListener(this);
        
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
	    
	    restoreState(savedInstanceState);
	}
    		
    /**
     * The main menu, showing multiple options
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {	
    	MenuInflater inflater = getMenuInflater();
    	inflater.inflate(R.menu.mainmenu, menu);
        return true;
    }
    
    /**
     * Defining what happens when a main menu item is selected
     */
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
    
    /**
     * This is called after transitioning from any of the ODK xforms.
     * It's used in determining if an xform instance is complete or not.
     */
    @Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch(requestCode) {
			case SELECTED_XFORM: {
			
				if (resultCode == RESULT_OK) {
					Cursor cursor = getContentResolver().query(contentUri, null, 
							InstanceProviderAPI.InstanceColumns.STATUS + "=?", new String[] {InstanceProviderAPI.STATUS_COMPLETE}, null);
					if (cursor.moveToNext()) {
						setPhase(UpdateEvent.INDIVIDUAL);
						formUnFinished = false;
						invalidStatus = false;
					}
					else {
						createUnfinishedFormDialog();
					}
				}
			}
		}
	}
    
    /**
     * At any given point in time, the screen can be rotated.
     * This method is responsible for saving the screen state.
     */
    @Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		
		outState.putSerializable("region", selectionFragment.getRegion());
		outState.putSerializable("subRegion", selectionFragment.getSubRegion());
		outState.putSerializable("village", selectionFragment.getVillage());
		outState.putSerializable("round", selectionFragment.getRound());
		outState.putSerializable("location", selectionFragment.getLocation());
		outState.putSerializable("visit", selectionFragment.getVisit());
		outState.putSerializable("individual", selectionFragment.getIndividual());
		outState.putSerializable("socialgroup", selectionFragment.getSocialgroup());
		outState.putString("phase", getPhase());
		outState.putBoolean("unfinishedFormDialog", formUnFinished);
		outState.putBoolean("invalidStatus", invalidStatus);
		outState.putBoolean("householdSelection", householdSelection);
		
		if (contentUri != null)
			outState.putString("uri", contentUri.toString());
	}
    
    /**
     * This method is responsible for restoring the screen state.
     */
    private void restoreState(Bundle state) {
    	
    	if (state != null) {
	    	selectionFragment.setRegion((LocationHierarchy)state.get("region"));
	    	selectionFragment.setSubRegion((LocationHierarchy)state.get("subRegion"));
	    	selectionFragment.setVillage((LocationHierarchy)state.get("village"));
	    	selectionFragment.setRound((Round)state.get("round"));
	    	selectionFragment.setLocation((Location)state.get("location"));
	    	selectionFragment.setVisit((Visit)state.get("visit"));
	    	selectionFragment.setIndividual((Individual)state.get("individual"));
	    	selectionFragment.setSocialgroup((SocialGroup)state.get("socialgroup"));
	    	restorePhase(state.getString("phase"));
	    	
	    	String uri = state.getString("uri");
	    	if (uri != null)
	    		contentUri = Uri.parse(uri);
	    		    	
	    	if (state.getBoolean("unfinishedFormDialog"))
	    		createUnfinishedFormDialog();
	    	if (state.getBoolean("invalidStatus"))
	    		createInvalidStatusDialog();
	    	if (state.getBoolean("householdSelection")) {
	    		selectionFragment.setSocialgroups(databaseAdapter.getSocialGroupsForIndividual(selectionFragment.getIndividual().getUuid()));
	    		createHouseholdSelectionDialog();
	    	}
	    	
    	}
	}
    
    /**
     * Responsible for displaying the Hello Greeting to the fieldworker after logging in.
     */
    private void processExtras() {
   	 	fieldWorker = (FieldWorker) getIntent().getExtras().getSerializable("fieldWorker");
   	 	loginGreetingText = (TextView) findViewById(R.id.loginGreetingText);
        loginGreetingText.setText("Hello, " + fieldWorker.getFirstName() + " " + fieldWorker.getLastName());
    }
        
    /**
     * Creates the 'Configure Server' option in the action menu.
     */
    private void createPreferencesMenu() {
        Intent i = new Intent(this, ServerPreferencesActivity.class);
        startActivity(i);
    }
    
    /**
     * Creates the 'Sync Database' option in the action menu.
     */
    private void createSyncDatabaseMenu() {
        Intent i = new Intent(this, SyncDatabaseActivity.class);
        startActivity(i);
    }
    
    /**
     * Loads the value fragment with a list of Regions.
     */
    private void loadRegionValueData() {
    	List<LocationHierarchy> regions = databaseAdapter.getAllRegions(UpdateParams.HIERARCHY_TOP_LEVEL);
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
    
    /**
     * Loads the value fragment with a list of Sub Regions within the selected Region.
     */
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
    
    /**
     * Loads the value fragment with a list of Villages within the selection Sub Region.
     */
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
        
    /**
     * Loads the value fragment with a list of Locations within the selected Village.
     */
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
    
    /**
     * Loads the value fragment with a list of Rounds.
     */
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
    
    /**
     * Loads the value fragment with a list of Individuals within the selected Location.
     */
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
    
    /**
     * A dialog indicating that an Xform instance was not completed.
     */
    private void createUnfinishedFormDialog() {
    	formUnFinished = true;
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
		alertDialogBuilder.setTitle("Warning");
		alertDialogBuilder.setMessage("Form started but not saved. " +
				"This form instance will be deleted. What do you want to do?");
		alertDialogBuilder.setCancelable(true);
		alertDialogBuilder.setPositiveButton("Delete form", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				formUnFinished = false;
				getContentResolver().delete(contentUri, 
						InstanceProviderAPI.InstanceColumns.STATUS + "=?", new String[] {InstanceProviderAPI.STATUS_INCOMPLETE});
				
				if (getPhase().equals(UpdateEvent.XFORMS))
					setPhase(UpdateEvent.INDIVIDUAL);
			}
		});	
		alertDialogBuilder.setNegativeButton("Edit form", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				formUnFinished = false;
				startActivityForResult(new Intent(Intent.ACTION_EDIT, contentUri), SELECTED_XFORM);				
			}
		});
		AlertDialog alertDialog = alertDialogBuilder.create();
		alertDialog.show();
    }
    
    /**
     * A dialog indicating that a selected entity has an invalid status code.
     */
    private void createInvalidStatusDialog() {
    	invalidStatus = true;
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
		alertDialogBuilder.setTitle("Warning");
		alertDialogBuilder.setMessage("The selected entity does not have a valid status. \n Please fix the error(s) before proceeding.");
		alertDialogBuilder.setCancelable(true);
		alertDialogBuilder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				invalidStatus = false;
				dialog.dismiss();
			}
		});	
		AlertDialog alertDialog = alertDialogBuilder.create();
		alertDialog.show();
    }
    
    
    /**
     * This is specific for Cross River.
     * A dialog displaying a selection of multiple Households for an Individual.
     */
    private void createHouseholdSelectionDialog() {
    	householdSelection = true;
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
		alertDialogBuilder.setTitle("This Individual lives within multiple Households.\nSelect the Household to be used for this Individual.");
		alertDialogBuilder.setSingleChoiceItems(selectionFragment.getSocialGroupsForDialog(), -1, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int clicked) {
				householdSelection = false;
				selectionFragment.setSocialGroupDialogSelection(clicked);
				dialog.dismiss();
			}
		});
	
		AlertDialog alertDialog = alertDialogBuilder.create();
		alertDialog.show();
    }
        
    /**
     * Defining what happens when a button is pressed, each button corresponds to a phase.
     */
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
				selectionFragment.createVisit();
				Record visitRecord = new Record(fieldWorker.getExtId(), selectionFragment.getVillage(), selectionFragment.getLocation(),
						selectionFragment.getRound(), selectionFragment.getIndividual(), selectionFragment.getSocialgroup(), selectionFragment.getVisit());
				new OdkFormLoadTask(this, getContentResolver(), visitRecord, UpdateEvent.VISIT).execute();		
				break;
			case R.id.individualBtn: 
				loadIndividualValueData();
				break;
			case R.id.clearLocationBtn: 
				setPhase(UpdateEvent.LOCATION);
				break;
			case R.id.finishVisitBtn: 
				reset();
				break;
			case R.id.deathBtn: 
				Record deathRecord = new Record(fieldWorker.getExtId(), selectionFragment.getVillage(), selectionFragment.getLocation(),
						selectionFragment.getRound(), selectionFragment.getIndividual(), selectionFragment.getSocialgroup(), selectionFragment.getVisit());
				new OdkFormLoadTask(this, getContentResolver(), deathRecord, UpdateEvent.DEATH).execute();
		}	
	}
	
	/**
	 * This is called after the OdkFormLoadTask has created an instance of the Xform.
	 * It returns the content uri which is used to start the ODK Activity to load that Xform instance.
	 */
	public void onSuccess(Uri contentUri) {
		this.contentUri = contentUri;
		startActivityForResult(new Intent(Intent.ACTION_EDIT, contentUri), SELECTED_XFORM);
	}
		
	/**
	 * This is called when an item has been selected from the selection fragment.
	 * Based on what was selected, various widgets will be enabled or disabled and progress the phase one step further.
	 */
	public void onValueSelected(int position) {
		String phase = getPhase();
		if (phase.equals(UpdateEvent.REGION)) {
			selectionFragment.setRegion(selectionFragment.getRegions().get(position));
			regionName.setVisibility(View.VISIBLE);
			regionExtId.setVisibility(View.VISIBLE);
			regionNameText.setText(selectionFragment.getRegion().getName());
			regionExtIdText.setText(selectionFragment.getRegion().getExtId());
			setPhase(UpdateEvent.SUBREGION);
		}
		else if (phase.equals(UpdateEvent.SUBREGION)) {
			selectionFragment.setSubRegion(selectionFragment.getSubRegions().get(position));
			subRegionName.setVisibility(View.VISIBLE);
			subRegionExtId.setVisibility(View.VISIBLE);
			subRegionNameText.setText(selectionFragment.getSubRegion().getName());
			subRegionExtIdText.setText(selectionFragment.getSubRegion().getExtId());
			setPhase(UpdateEvent.VILLAGE);
		}
		else if (phase.equals(UpdateEvent.VILLAGE)) {
			selectionFragment.setVillage(selectionFragment.getVillages().get(position));
			villageName.setVisibility(View.VISIBLE);
			villageExtId.setVisibility(View.VISIBLE);
			villageNameText.setText(selectionFragment.getVillage().getName());
			villageExtIdText.setText(selectionFragment.getVillage().getExtId());
			setPhase(UpdateEvent.ROUND);
		}
		else if (phase.equals(UpdateEvent.ROUND)) {
			selectionFragment.setRound(selectionFragment.getRounds().get(position));
			roundNumber.setVisibility(View.VISIBLE);
			roundStartDate.setVisibility(View.VISIBLE);
			roundEndDate.setVisibility(View.VISIBLE);
			roundNumberText.setText(selectionFragment.getRound().getRoundNumber());
			roundStartDateText.setText(selectionFragment.getRound().getStartDate());
			roundEndDateText.setText(selectionFragment.getRound().getEndDate());
			setPhase(UpdateEvent.LOCATION);
		}
		else if (phase.equals(UpdateEvent.LOCATION)) {
			
			// it's possible for the selected location to have an invalid status.
			// must signal that here.
			if (!databaseAdapter.isLocationStatusValid(selectionFragment.getLocations().get(position).getUuid())) {
				createInvalidStatusDialog();
			}
			else {
				selectionFragment.setLocation(selectionFragment.getLocations().get(position));
				locationName.setVisibility(View.VISIBLE);
				locationExtId.setVisibility(View.VISIBLE);
				locationLatitude.setVisibility(View.VISIBLE);
				locationLongitude.setVisibility(View.VISIBLE);
				locationNameText.setText(selectionFragment.getLocation().getName());
				locationExtIdText.setText(selectionFragment.getLocation().getExtId());
				locationLatitudeText.setText(selectionFragment.getLocation().getLatitude());
				locationLongitudeText.setText(selectionFragment.getLocation().getLongitude());
				setPhase(UpdateEvent.VISIT);
			}
		}
		else if (phase.equals(UpdateEvent.INDIVIDUAL)) {
			
			// it's possible for the selected individual to have an invalid status
			// must signal that here
			if (!databaseAdapter.isIndividualStatusValid(selectionFragment.getIndividuals().get(position).getUuid())) {
				createInvalidStatusDialog();
			}
			else {
				invalidStatus = false;
				selectionFragment.setIndividual(selectionFragment.getIndividuals().get(position));
				individualExtId.setVisibility(View.VISIBLE);
				individualFirstName.setVisibility(View.VISIBLE);
				individualLastName.setVisibility(View.VISIBLE);
				individualDob.setVisibility(View.VISIBLE);
				individualExtIdText.setText(selectionFragment.getIndividual().getExtId());
				individualFirstNameText.setText(selectionFragment.getIndividual().getFirstName());
				individualLastNameText.setText(selectionFragment.getIndividual().getLastName());
				individualDobText.setText(selectionFragment.getIndividual().getDob());
				
				// get the socialgroups the individual is a part of
				List<SocialGroup> list = databaseAdapter.getSocialGroupsForIndividual(selectionFragment.getIndividual().getUuid());
				
				if (list.size() > 0) {
					selectionFragment.setSocialgroups(list);
				
					// if the individual is in more that one social group then the socialgroup must be specified
					if (list.size() > 1) {
						createHouseholdSelectionDialog();
						setPhase(UpdateEvent.XFORMS);
					}
					else if (list.size() == 1) {
						selectionFragment.setSocialGroupDialogSelection(0);
						setPhase(UpdateEvent.XFORMS);
					}
					else {
						Toast.makeText(getApplicationContext(),	getString(R.string.household_not_found), Toast.LENGTH_SHORT).show();
					}
				}
			}
		}
	}
	
	/**
	 * Clears all state and returns the phase to Location.
	 */
	public void reset() {
		setPhase(UpdateEvent.LOCATION);	
		if (valueFragment != null) {
			valueFragment.reset();
		}
	}
	
	/**
	 * Returns the phase you're currently in. 
	 */
	private String getPhase() {
		if (XFORMS_PHASE)
			return UpdateEvent.XFORMS;
		else if (INDIVIDUAL_PHASE)
			return UpdateEvent.INDIVIDUAL;
		else if (VISIT_PHASE)
			return UpdateEvent.VISIT;
		else if (LOCATION_PHASE)
			return UpdateEvent.LOCATION;
		else if (ROUND_PHASE)
			return UpdateEvent.ROUND;
		else if (VILLAGE_PHASE)
			return UpdateEvent.VILLAGE;
		else if (SUB_REGION_PHASE)
			return UpdateEvent.SUBREGION;
		else
			return UpdateEvent.REGION;
	}
	
	/**
	 * Restores the state to the phase specified.
	 */
	private void restorePhase(String phase) {
		if (phase.equals(UpdateEvent.REGION)) {
			setStateRegion();
		}
		else if (phase.equals(UpdateEvent.SUBREGION)) {
			setStateSubRegion();
			restoreRegionTextFields();
		}
		else if (phase.equals(UpdateEvent.VILLAGE)) {
			setStateVillage();
			
			restoreRegionTextFields();
			restoreSubRegionTextFields();
		}
		else if (phase.equals(UpdateEvent.ROUND)) {
			setStateRound();
			
			restoreRegionTextFields();
			restoreSubRegionTextFields();
			restoreVillageTextFields();
		}
		else if (phase.equals(UpdateEvent.LOCATION)) {
			setStateLocation();
			
			restoreRegionTextFields();
			restoreSubRegionTextFields();
			restoreVillageTextFields();
			restoreRoundTextFields();
		}
		else if (phase.equals(UpdateEvent.VISIT)) {
			setStateVisit();
			
			restoreRegionTextFields();
			restoreSubRegionTextFields();
			restoreVillageTextFields();
			restoreRoundTextFields();
			restoreLocationTextFields();
		}
		else if (phase.equals(UpdateEvent.INDIVIDUAL)) {
			setStateIndividual();
			
			restoreRegionTextFields();
			restoreSubRegionTextFields();
			restoreVillageTextFields();
			restoreRoundTextFields();
			restoreLocationTextFields();
		}
		else if (phase.equals(UpdateEvent.XFORMS)) {
			setStateFinish();
			
			restoreRegionTextFields();
			restoreSubRegionTextFields();
			restoreVillageTextFields();
			restoreRoundTextFields();
			restoreLocationTextFields();
			restoreIndividualTextFields();
		}
	}
	
	/**
	 * Sets the state to the phase specified.
	 */
	private void setPhase(String phase) {
		if (phase.equals(UpdateEvent.REGION)) {
			setStateRegion();
			
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
		else if (phase.equals(UpdateEvent.SUBREGION)) {		
			setStateSubRegion();
			
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
		else if (phase.equals(UpdateEvent.VILLAGE)) {
			setStateVillage();
			
			clearVillageTextFields();
			clearRoundTextFields();
			clearLocationTextFields();
			clearIndividualTextFields();
			
			selectionFragment.setVillage(new LocationHierarchy());
			selectionFragment.setRound(new Round());
			selectionFragment.setLocation(new Location());
			selectionFragment.setIndividual(new Individual());
		}
		else if (phase.equals(UpdateEvent.ROUND)) {
			setStateRound();
			
			clearRoundTextFields();
			clearLocationTextFields();
			clearIndividualTextFields();
			
			selectionFragment.setRound(new Round());
			selectionFragment.setLocation(new Location());
			selectionFragment.setIndividual(new Individual());
		}
		else if (phase.equals(UpdateEvent.LOCATION)) {
			setStateLocation();
			
			clearLocationTextFields();
			clearIndividualTextFields();
			
			selectionFragment.setLocation(new Location());
			selectionFragment.setIndividual(new Individual());
		}
		else if (phase.equals(UpdateEvent.VISIT)) {
			setStateVisit();
			
			clearIndividualTextFields();
			
			selectionFragment.setIndividual(new Individual());
		}
		else if (phase.equals(UpdateEvent.INDIVIDUAL)) {
			setStateIndividual();
			
			clearIndividualTextFields();
			
			selectionFragment.setIndividual(new Individual());
		}
		else if (phase.equals(UpdateEvent.XFORMS)) {
			setStateFinish();
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
	
	private void restoreRegionTextFields() {
		regionNameText.setText(selectionFragment.getRegion().getName());
		regionExtIdText.setText(selectionFragment.getRegion().getExtId());
		regionName.setVisibility(View.VISIBLE);
		regionExtId.setVisibility(View.VISIBLE);
	}
	
	private void restoreSubRegionTextFields() {
		subRegionNameText.setText(selectionFragment.getSubRegion().getName());
		subRegionExtIdText.setText(selectionFragment.getSubRegion().getExtId());
		subRegionName.setVisibility(View.VISIBLE);
		subRegionExtId.setVisibility(View.VISIBLE);
	}
	
	private void restoreVillageTextFields() {
		villageNameText.setText(selectionFragment.getVillage().getName());
		villageExtIdText.setText(selectionFragment.getVillage().getExtId());
		villageName.setVisibility(View.VISIBLE);
		villageExtId.setVisibility(View.VISIBLE);
	}
	
	private void restoreRoundTextFields() {
		roundNumberText.setText(selectionFragment.getRound().getRoundNumber());
		roundStartDateText.setText(selectionFragment.getRound().getStartDate());
		roundEndDateText.setText(selectionFragment.getRound().getEndDate());
		roundNumber.setVisibility(View.VISIBLE);
		roundStartDate.setVisibility(View.VISIBLE);
		roundEndDate.setVisibility(View.VISIBLE);
	}
	
	private void restoreLocationTextFields() {
		locationNameText.setText(selectionFragment.getLocation().getName());
		locationExtIdText.setText(selectionFragment.getLocation().getExtId());
		locationLatitudeText.setText(selectionFragment.getLocation().getLatitude());
		locationLongitudeText.setText(selectionFragment.getLocation().getLongitude());
		locationName.setVisibility(View.VISIBLE);
		locationExtId.setVisibility(View.VISIBLE);
		locationLatitude.setVisibility(View.VISIBLE);
		locationLongitude.setVisibility(View.VISIBLE);
	}
	
	private void restoreIndividualTextFields() {
		individualExtIdText.setText(selectionFragment.getIndividual().getExtId());
		individualFirstNameText.setText(selectionFragment.getIndividual().getFirstName());
		individualLastNameText.setText(selectionFragment.getIndividual().getLastName());
		individualDobText.setText(selectionFragment.getIndividual().getDob());
		individualExtId.setVisibility(View.VISIBLE);
		individualFirstName.setVisibility(View.VISIBLE);
		individualLastName.setVisibility(View.VISIBLE);
		individualDob.setVisibility(View.VISIBLE);
	}
	
	private void setStateRegion() {
		REGION_PHASE = true;
		SUB_REGION_PHASE = false;
		VILLAGE_PHASE = false;
		ROUND_PHASE = false;
		LOCATION_PHASE = false;
		VISIT_PHASE = false;
		INDIVIDUAL_PHASE = false;
		XFORMS_PHASE = false;
		
		finishVisitBtn.setEnabled(false);
		createVisitBtn.setEnabled(false);
		clearLocationBtn.setEnabled(false);
		deathBtn.setEnabled(false);
		regionBtn.setEnabled(true);
		subRegionBtn.setEnabled(false);
		villageBtn.setEnabled(false);
		roundBtn.setEnabled(false);
		locationBtn.setEnabled(false);
		individualBtn.setEnabled(false);
	}
	
	private void setStateSubRegion() {
		REGION_PHASE = false;
		SUB_REGION_PHASE = true;
		VILLAGE_PHASE = false;
		ROUND_PHASE = false;
		LOCATION_PHASE = false;
		VISIT_PHASE = false;
		INDIVIDUAL_PHASE = false;
		XFORMS_PHASE = false;
		
		finishVisitBtn.setEnabled(false);
		createVisitBtn.setEnabled(false);
		clearLocationBtn.setEnabled(false);
		deathBtn.setEnabled(false);
		regionBtn.setEnabled(false);
		subRegionBtn.setEnabled(true);
		villageBtn.setEnabled(false);
		roundBtn.setEnabled(false);
		locationBtn.setEnabled(false);
		individualBtn.setEnabled(false);
	}
	
	private void setStateVillage() {
		REGION_PHASE = false;
		SUB_REGION_PHASE = false;
		VILLAGE_PHASE = true;
		ROUND_PHASE = false;
		LOCATION_PHASE = false;
		VISIT_PHASE = false;
		INDIVIDUAL_PHASE = false;
		XFORMS_PHASE = false;

		finishVisitBtn.setEnabled(false);
		createVisitBtn.setEnabled(false);
		clearLocationBtn.setEnabled(false);
		deathBtn.setEnabled(false);
		regionBtn.setEnabled(false);
		subRegionBtn.setEnabled(false);
		villageBtn.setEnabled(true);
		roundBtn.setEnabled(false);
		locationBtn.setEnabled(false);
		individualBtn.setEnabled(false);
	}
	
	private void setStateRound() {
		REGION_PHASE = false;
		SUB_REGION_PHASE = false;
		VILLAGE_PHASE = false;
		ROUND_PHASE = true;
		LOCATION_PHASE = false;
		VISIT_PHASE = false;
		INDIVIDUAL_PHASE = false;
		XFORMS_PHASE = false;
		
		finishVisitBtn.setEnabled(false);
		createVisitBtn.setEnabled(false);
		clearLocationBtn.setEnabled(false);
		deathBtn.setEnabled(false);
		regionBtn.setEnabled(false);
		subRegionBtn.setEnabled(false);
		villageBtn.setEnabled(false);
		roundBtn.setEnabled(true);
		locationBtn.setEnabled(false);
		individualBtn.setEnabled(false);
	}
	
	private void setStateLocation() {
		REGION_PHASE = false;
		SUB_REGION_PHASE = false;
		VILLAGE_PHASE = false;
		ROUND_PHASE = false;
		LOCATION_PHASE = true;
		VISIT_PHASE = false;
		INDIVIDUAL_PHASE = false;
		XFORMS_PHASE = false;
		
		finishVisitBtn.setEnabled(false);
		createVisitBtn.setEnabled(false);
		clearLocationBtn.setEnabled(false);
		deathBtn.setEnabled(false);
		regionBtn.setEnabled(false);
		subRegionBtn.setEnabled(false);
		villageBtn.setEnabled(false);
		roundBtn.setEnabled(false);
		locationBtn.setEnabled(true);
		individualBtn.setEnabled(false);
	}
	
	private void setStateVisit() { 
		REGION_PHASE = false;
		SUB_REGION_PHASE = false;
		VILLAGE_PHASE = false;
		ROUND_PHASE = false;
		LOCATION_PHASE = false;
		VISIT_PHASE = true;
		INDIVIDUAL_PHASE = false;
		XFORMS_PHASE = false;
		
		finishVisitBtn.setEnabled(false);
		createVisitBtn.setEnabled(true);
		clearLocationBtn.setEnabled(true);
		deathBtn.setEnabled(false);
		regionBtn.setEnabled(false);
		subRegionBtn.setEnabled(false);
		villageBtn.setEnabled(false);
		roundBtn.setEnabled(false);
		locationBtn.setEnabled(false);
		individualBtn.setEnabled(false);
	}
	
	private void setStateIndividual() {
		REGION_PHASE = false;
		SUB_REGION_PHASE = false;
		VILLAGE_PHASE = false;
		ROUND_PHASE = false;
		LOCATION_PHASE = false;
		VISIT_PHASE = false;
		INDIVIDUAL_PHASE = true;
		XFORMS_PHASE = false;
		
		finishVisitBtn.setEnabled(true);
		createVisitBtn.setEnabled(false);
		clearLocationBtn.setEnabled(false);
		deathBtn.setEnabled(false);
		regionBtn.setEnabled(false);
		subRegionBtn.setEnabled(false);
		villageBtn.setEnabled(false);
		roundBtn.setEnabled(false);
		locationBtn.setEnabled(false);
		individualBtn.setEnabled(true);
	}
	
	private void setStateFinish() {
		REGION_PHASE = false;
		SUB_REGION_PHASE = false;
		VILLAGE_PHASE = false;
		ROUND_PHASE = false;
		LOCATION_PHASE = false;
		VISIT_PHASE = false;
		INDIVIDUAL_PHASE = false;
		XFORMS_PHASE = true;
		
		regionBtn.setEnabled(false);
		finishVisitBtn.setEnabled(true);
		createVisitBtn.setEnabled(false);
		clearLocationBtn.setEnabled(false);
		subRegionBtn.setEnabled(false);
		villageBtn.setEnabled(false);
		roundBtn.setEnabled(false);
		locationBtn.setEnabled(false);
		individualBtn.setEnabled(false);
		deathBtn.setEnabled(true);
	}
}
