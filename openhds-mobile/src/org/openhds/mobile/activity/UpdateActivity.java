package org.openhds.mobile.activity;

import java.util.ArrayList;
import java.util.List;
import org.openhds.mobile.InstanceProviderAPI;
import org.openhds.mobile.R;
import org.openhds.mobile.cell.ValueFragmentCell;
import org.openhds.mobile.database.DatabaseAdapter;
import org.openhds.mobile.dialog.HouseholdListDialog;
import org.openhds.mobile.fragment.EventFragment;
import org.openhds.mobile.fragment.SelectionFragment;
import org.openhds.mobile.fragment.ValueFragment;
import org.openhds.mobile.listener.OdkFormLoadListener;
import org.openhds.mobile.listener.ValueSelectedListener;
import org.openhds.mobile.model.FieldWorker;
import org.openhds.mobile.model.Individual;
import org.openhds.mobile.model.Location;
import org.openhds.mobile.model.LocationHierarchy;
import org.openhds.mobile.model.PregnancyOutcome;
import org.openhds.mobile.model.Relationship;
import org.openhds.mobile.model.Round;
import org.openhds.mobile.model.SocialGroup;
import org.openhds.mobile.model.UpdateEvent;
import org.openhds.mobile.model.UpdateParams;
import org.openhds.mobile.model.Visit;
import org.openhds.mobile.task.OdkGeneratedFormLoadTask;
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
	private Button regionBtn, subRegionBtn, villageBtn, locationBtn, roundBtn, individualBtn, 
	 			   findLocationGeoPointBtn, createLocationBtn, createVisitBtn, clearLocationBtn, clearIndividualBtn,
	 			   householdBtn, membershipBtn, relationshipBtn, inMigrationBtn, outMigrationBtn, pregRegBtn, birthRegBtn, deathBtn, 
	 			   finishVisitBtn;
	
	// logged in fieldworker
	private FieldWorker fieldWorker;
		
	// this activity manages three fragments
	private SelectionFragment sf;
	private ValueFragment vf;
	private EventFragment ef;
	
	private final int SELECTED_XFORM = 1;
	private final int FILTER = 2;
	private final int LOCATION_GEOPOINT = 3;
	
	// the uri of the last viewed xform
	private Uri contentUri;
	
	// status flags indicating a dialog, used for restoring the activity
	private boolean formUnFinished = false;
	private boolean xFormNotFound = false;
	
	// the workflow for this activity is arranged into multiple phases
	private boolean REGION_PHASE = true;
	private boolean SUB_REGION_PHASE = false;
	private boolean VILLAGE_PHASE = false;
	private boolean LOCATION_PHASE = false;
	private boolean ROUND_PHASE = false;
	private boolean VISIT_PHASE = false;
	private boolean INDIVIDUAL_PHASE = false;
	private boolean XFORMS_PHASE = false;
				
    @Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.main);    	   
	   
        databaseAdapter = new DatabaseAdapter(getBaseContext());
                
        finishVisitBtn = (Button) findViewById(R.id.finishVisitBtn);
        finishVisitBtn.setOnClickListener(this);
        
        clearLocationBtn = (Button) findViewById(R.id.clearLocationBtn);
        clearLocationBtn.setOnClickListener(this);
        
        clearIndividualBtn = (Button) findViewById(R.id.clearIndividualBtn);
        clearIndividualBtn.setOnClickListener(this);
        
        findLocationGeoPointBtn = (Button) findViewById(R.id.findLocationGeoPointBtn);
        findLocationGeoPointBtn.setOnClickListener(this);
        
        createLocationBtn = (Button) findViewById(R.id.createLocationBtn);
        createLocationBtn.setOnClickListener(this);
        
        createVisitBtn = (Button) findViewById(R.id.createVisitBtn);
        createVisitBtn.setOnClickListener(this);
        
        householdBtn = (Button) findViewById(R.id.householdBtn);
        householdBtn.setOnClickListener(this);
        
        membershipBtn = (Button) findViewById(R.id.membershipBtn);
        membershipBtn.setOnClickListener(this);
        
        relationshipBtn = (Button) findViewById(R.id.relationshipBtn);
        relationshipBtn.setOnClickListener(this);
        
        inMigrationBtn = (Button) findViewById(R.id.inMigrationBtn);
        inMigrationBtn.setOnClickListener(this);
        
        outMigrationBtn = (Button) findViewById(R.id.outMigrationBtn);
        outMigrationBtn.setOnClickListener(this);
        
        pregRegBtn = (Button) findViewById(R.id.pregRegBtn);
        pregRegBtn.setOnClickListener(this);
        
        birthRegBtn = (Button) findViewById(R.id.birthRegBtn);
        birthRegBtn.setOnClickListener(this);
        
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
        
        roundBtn = (Button) findViewById(R.id.roundBtn);
        roundBtn.setOnClickListener(this);
        roundNumberText = (TextView) findViewById(R.id.roundNumberText);
        roundStartDateText = (TextView) findViewById(R.id.roundStartDateText);
        roundEndDateText = (TextView) findViewById(R.id.roundEndDateText);
        roundNumber = (TextView) findViewById(R.id.roundNumber);
        roundStartDate = (TextView) findViewById(R.id.roundStartDate);
        roundEndDate = (TextView) findViewById(R.id.roundEndDate);
        
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
        
    	sf = (SelectionFragment)getSupportFragmentManager().findFragmentById(R.id.selectionFragment);
		vf = (ValueFragment)getSupportFragmentManager().findFragmentById(R.id.valueFragment);
		ef = (EventFragment)getSupportFragmentManager().findFragmentById(R.id.eventFragment);
		
	    processExtras();
	    	       	    
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
						
						if (getPhase().equals(UpdateEvent.LOCATION)) {
							displayLocationState();
							setPhase(UpdateEvent.VISIT);
						}
						else {
							setPhase(UpdateEvent.INDIVIDUAL);
						}
						formUnFinished = false;
					}
					else {
						createUnfinishedFormDialog();
					}
				}
				break;
			}
			case FILTER: {
				if (resultCode == RESULT_OK) {
					String type = data.getExtras().getString("type");
					
					if (type.equals(UpdateEvent.RELATIONSHIP)) {
						Individual currentIndividual = sf.getIndividual();
						String individualExtId = data.getExtras().getString("extId");
						
						if (sf.getIndividual().getGender().equals("Male")) {
							sf.getRelationship().setMaleIndividual(currentIndividual.getExtId());
							sf.getRelationship().setFemaleIndividual(individualExtId);
						}
						else {
							sf.getRelationship().setMaleIndividual(individualExtId);
							sf.getRelationship().setFemaleIndividual(currentIndividual.getExtId());
						}
						loadForm(UpdateEvent.RELATIONSHIP);
					}
					else if (type.equals(UpdateEvent.LOCATION)) {
						String name = data.getExtras().getString("name");
						String individualExtId = data.getExtras().getString("extId");
						
						sf.createLocation(individualExtId, name);
						sf.getIndividual().setExtId(individualExtId);
						
						loadForm(UpdateEvent.LOCATION);
					}
					else if (type.equals(UpdateEvent.INMIGRATION)) {
						String extId = data.getExtras().getString("extId");
						Individual individual = databaseAdapter.getIndividualByExtId(extId);
						sf.setIndividual(individual);
						boolean result = determineSocialGroupForIndividual();
						
						if (result)
							createHouseholdSelectionDialog(UpdateEvent.INMIGRATION);
						else
							loadForm(UpdateEvent.INMIGRATION);
					}
				}
				break;
			}
			case LOCATION_GEOPOINT: {
				if (resultCode == RESULT_OK) {
					String extId = data.getExtras().getString("extId");
					// a few things need to happen here:
					// * get the location by extId
					Location location = databaseAdapter.getLocationByExtId(extId);
					
					// * figure out the parent location hierarchy
					LocationHierarchy village = databaseAdapter.getLocationHierarchyByExtId(location.getHierarchy());
					LocationHierarchy subRegion = databaseAdapter.getLocationHierarchyByExtId(village.getParent());
					LocationHierarchy region = databaseAdapter.getLocationHierarchyByExtId(subRegion.getParent());
										
					// * set the location hierarchy region, district, and village in selectionFragment
					sf.setRegion(region);
					sf.setSubRegion(subRegion);
					sf.setVillage(village);
					sf.setLocation(location);
	
					restorePhase(UpdateEvent.ROUND);
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
		
		outState.putSerializable("region", sf.getRegion());
		outState.putSerializable("subRegion", sf.getSubRegion());
		outState.putSerializable("village", sf.getVillage());
		outState.putSerializable("round", sf.getRound());
		outState.putSerializable("location", sf.getLocation());
		outState.putSerializable("visit", sf.getVisit());
		outState.putSerializable("individual", sf.getIndividual());
		outState.putSerializable("socialgroup", sf.getSocialgroup());
		outState.putString("phase", getPhase());
		outState.putBoolean("unfinishedFormDialog", formUnFinished);
		outState.putBoolean("xFormNotFound", xFormNotFound);

		if (contentUri != null)
			outState.putString("uri", contentUri.toString());
	}
    
    /**
     * This method is responsible for restoring the screen state.
     */
    private void restoreState(Bundle state) {
    	
    	if (state != null) {
	    	sf.setRegion((LocationHierarchy)state.get("region"));
	    	sf.setSubRegion((LocationHierarchy)state.get("subRegion"));
	    	sf.setVillage((LocationHierarchy)state.get("village"));
	    	sf.setRound((Round)state.get("round"));
	    	sf.setLocation((Location)state.get("location"));
	    	sf.setVisit((Visit)state.get("visit"));
	    	sf.setIndividual((Individual)state.get("individual"));
	    	sf.setSocialgroup((SocialGroup)state.get("socialgroup"));
	    	restorePhase(state.getString("phase"));
	    	
	    	String uri = state.getString("uri");
	    	if (uri != null)
	    		contentUri = Uri.parse(uri);
	    		    	
	    	if (state.getBoolean("xFormNotFound"))
	    		createXFormNotFoundDialog();
	    	if (state.getBoolean("unfinishedFormDialog"))
	    		createUnfinishedFormDialog();
    	}
	}
    
    /**
     * Responsible for displaying the Hello Greeting to the fieldworker after logging in.
     */
    private void processExtras() {
   	 	fieldWorker = (FieldWorker) getIntent().getExtras().getSerializable("fieldWorker");
   	 	sf.setFieldWorker(fieldWorker);
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
	 * Method used for starting the activity for filtering for individuals
	 */
	private void startFilterActivity(String type) {
		Intent i = new Intent(this, FilterActivity.class);
		i.putExtra("region", sf.getRegion());
		i.putExtra("subRegion", sf.getSubRegion());
		i.putExtra("village", sf.getVillage());
		i.putExtra("location", sf.getLocation());
		i.putExtra("type", type);
		startActivityForResult(i, FILTER);
	}
    
    /**
     * Loads the value fragment with a list of Regions.
     */
    private void loadRegionValueData() {
    	List<LocationHierarchy> regions = databaseAdapter.getAllRegions(UpdateParams.HIERARCHY_TOP_LEVEL);
    	sf.setRegions(regions);
    	if (vf != null) {
    		List<ValueFragmentCell> list = new ArrayList<ValueFragmentCell>();
    		for (LocationHierarchy item : regions) {
    			ValueFragmentCell cell = new ValueFragmentCell(item.getName(), item.getExtId());
    			list.add(cell);
    		}
    		vf.setContent(list);
    	}
    }
    
    /**
     * Loads the value fragment with a list of Sub Regions within the selected Region.
     */
    private void loadSubRegionValueData() {
    	List<LocationHierarchy> subRegions = databaseAdapter.getAllSubRegionsOfRegion(sf.getRegion());
      	sf.setSubRegions(subRegions);
    	if (vf != null) {
    		List<ValueFragmentCell> list = new ArrayList<ValueFragmentCell>();
    		for (LocationHierarchy item : subRegions) {
    			ValueFragmentCell cell = new ValueFragmentCell(item.getName(), item.getExtId());
    			list.add(cell);
    		}
    		vf.setContent(list);
    	}
    }
    
    /**
     * Loads the value fragment with a list of Villages within the selection Sub Region.
     */
    private void loadVillageValueData() {
      	List<LocationHierarchy> villages = databaseAdapter.getAllSubRegionsOfRegion(sf.getSubRegion());
    	sf.setVillages(villages);
      	if (vf != null) {
    		List<ValueFragmentCell> list = new ArrayList<ValueFragmentCell>();
    		for (LocationHierarchy item : villages) {
    			ValueFragmentCell cell = new ValueFragmentCell(item.getName(), item.getExtId());
    			list.add(cell);
    		}
    		vf.setContent(list);
    	}
    }
        
    /**
     * Loads the value fragment with a list of Locations within the selected Village.
     */
    private void loadLocationValueData() {
    	List<Location> locations = databaseAdapter.getAllLocationsOfVillage(sf.getVillage().getExtId());
      	sf.setLocations(locations);
    	if (vf != null) {
    		List<ValueFragmentCell> list = new ArrayList<ValueFragmentCell>();
    		for (Location item : locations) {
    			ValueFragmentCell cell = new ValueFragmentCell(item.getName(), item.getExtId());
    			list.add(cell);
    		}
    		vf.setContent(list);
    	}
    }
    
    /**
     * Loads the value fragment with a list of Rounds.
     */
    private void loadRoundValueData() {
    	List<Round> rounds = databaseAdapter.getAllRounds();
      	sf.setRounds(rounds);
    	if (vf != null) {
    		List<ValueFragmentCell> list = new ArrayList<ValueFragmentCell>();
    		for (Round item : rounds) {
    			ValueFragmentCell cell = new ValueFragmentCell("Round: " + item.getRoundNumber(), "");
    			list.add(cell);
    		}
    		vf.setContent(list);
    	}
    }
    
    /**
     * Loads the value fragment with a list of Individuals within the selected Location.
     */
    private void loadIndividualValueData() {
    	List<Individual> individuals = databaseAdapter.getIndividualsAtLocation(sf.getLocation());
      	sf.setIndividuals(individuals);
    	if (vf != null) {
    		List<ValueFragmentCell> list = new ArrayList<ValueFragmentCell>();
    		for (Individual item : individuals) {
    			ValueFragmentCell cell = new ValueFragmentCell(item.getFirstName() + " " + item.getLastName(), item.getExtId());
    			list.add(cell);
    		}
    		vf.setContent(list);
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
				
				if (getPhase().equals(UpdateEvent.LOCATION)) {
					setPhase(UpdateEvent.LOCATION);
				}
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
     * A dialog for selecting in an InMigration is Internal or External.
     */
    private void createInMigrationFormDialog() {
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
		alertDialogBuilder.setTitle("In Migration");
		alertDialogBuilder.setMessage("Is this an Internal or External In Migration event?");
		alertDialogBuilder.setCancelable(true);
		alertDialogBuilder.setPositiveButton("Internal", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				sf.setExternalInMigration(false);	
				startFilterActivity(UpdateEvent.INMIGRATION);
			}
		});	
		alertDialogBuilder.setNegativeButton("External", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				sf.setExternalInMigration(true);	
				HouseholdListDialog householdDialog = new HouseholdListDialog(UpdateActivity.this, sf, UpdateEvent.INMIGRATION);
				householdDialog.show();
			}
		});
		AlertDialog alertDialog = alertDialogBuilder.create();
		alertDialog.show();
    } 
    
    /**
     * This is specific for Cross River.
     * A dialog displaying a selection of multiple Households for an Individual.
     */
    private void createHouseholdSelectionDialog(final String event) {
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
		alertDialogBuilder.setTitle("Select the Household to be used for this Individual.");
		alertDialogBuilder.setSingleChoiceItems(sf.getSocialGroupsForDialog(), -1, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int clicked) {
				sf.setSocialGroupDialogSelection(clicked);
				dialog.dismiss();
				
				if (event != null)
					loadForm(event);
			}
		});
	
		AlertDialog alertDialog = alertDialogBuilder.create();
		alertDialog.show();
    }
                
    /**
     * A dialog indicating that an Xform instance could not be found.
     */
    private void createXFormNotFoundDialog() {
    	xFormNotFound = true;
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
		alertDialogBuilder.setTitle("Warning");
		alertDialogBuilder.setMessage("The XForm could not be found within Open Data Kit Collect. " +
				"Please make sure that it exists and it's named correctly.");
		alertDialogBuilder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				xFormNotFound = false;
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
			case R.id.locationBtn: 
				loadLocationValueData();
				break;
			case R.id.roundBtn: 
				loadRoundValueData();
				break;
			case R.id.findLocationGeoPointBtn:
				Intent intent = new Intent(getApplicationContext(), ShowMapActivity.class);
				startActivityForResult(intent, LOCATION_GEOPOINT);
				break;
			case R.id.createLocationBtn:
				startFilterActivity(UpdateEvent.LOCATION);
				break;
			case R.id.createVisitBtn: 
				sf.createVisit();
				loadForm(UpdateEvent.VISIT);	
				break;
			case R.id.individualBtn: 
				loadIndividualValueData();
				break;
			case R.id.clearLocationBtn: 
				setPhase(UpdateEvent.LOCATION);
				break;
			case R.id.clearIndividualBtn:
				setPhase(UpdateEvent.INDIVIDUAL);
				break;
			case R.id.finishVisitBtn: 
				reset();
				break;
			case R.id.householdBtn:
				sf.createSocialGroup();
				loadForm(UpdateEvent.SOCIALGROUP);
				break;
			case R.id.membershipBtn:
				loadForm(UpdateEvent.MEMBERSHIP);
				break;
			case R.id.relationshipBtn:
				startFilterActivity(UpdateEvent.RELATIONSHIP);
				break;
			case R.id.inMigrationBtn:
				createInMigrationFormDialog();
				break;
			case R.id.outMigrationBtn:
				loadForm(UpdateEvent.OUTMIGRATION);
				break;
			case R.id.pregRegBtn:
				loadForm(UpdateEvent.PREGNANCYOBSERVATION);
				break;
			case R.id.birthRegBtn:
				boolean result = sf.createPregnancyOutcome();
				if (sf.getPregnancyOutcome().getFather() == null) 
					Toast.makeText(getApplicationContext(),	getString(R.string.fatherNotFound), Toast.LENGTH_SHORT).show();

				sf.setPregnancyOutcome(sf.getPregnancyOutcome());
				if (result == false)
					Toast.makeText(getApplicationContext(),	getString(R.string.idGenerationFailure), Toast.LENGTH_SHORT).show();
				
				loadForm(UpdateEvent.BIRTH);
				break;
			case R.id.deathBtn: 
				loadForm(UpdateEvent.DEATH);
		}	
	}
	
	/**
	 * Launches the OdkFormLoadTask depending on the specified UpdateEvent
	 */
    public void loadForm(String event) {
   		new OdkGeneratedFormLoadTask(this, getContentResolver(), sf, event).execute();
    }
		
	/**
	 * This is called after the OdkFormLoadTask has created an instance of the Xform.
	 * It returns the content uri which is used to start the ODK Activity to load that Xform instance.
	 */
	public void onOdkFormLoadSuccess(Uri contentUri) {
		this.contentUri = contentUri;
		startActivityForResult(new Intent(Intent.ACTION_EDIT, contentUri), SELECTED_XFORM);
	}
	
	/**
	 * This is called when the OdkFormLoadTask is unable to locate an xform.
	 * It's possible for this to happen if the form doesn't exist.
	 */
	public void onOdkFormLoadFailure() {
		createXFormNotFoundDialog();
	}
		
	/**
	 * This is called when an item has been selected from the selection fragment.
	 * Based on what was selected, various widgets will be enabled or disabled and progress the phase one step further.
	 */
	public void onValueSelected(int position) {
		String phase = getPhase();
		if (phase.equals(UpdateEvent.REGION)) {
			sf.setRegion(sf.getRegions().get(position));
			regionName.setVisibility(View.VISIBLE);
			regionExtId.setVisibility(View.VISIBLE);
			regionNameText.setText(sf.getRegion().getName());
			regionExtIdText.setText(sf.getRegion().getExtId());
			setPhase(UpdateEvent.SUBREGION);
		}
		else if (phase.equals(UpdateEvent.SUBREGION)) {
			sf.setSubRegion(sf.getSubRegions().get(position));
			subRegionName.setVisibility(View.VISIBLE);
			subRegionExtId.setVisibility(View.VISIBLE);
			subRegionNameText.setText(sf.getSubRegion().getName());
			subRegionExtIdText.setText(sf.getSubRegion().getExtId());
			setPhase(UpdateEvent.VILLAGE);
		}
		else if (phase.equals(UpdateEvent.VILLAGE)) {
			sf.setVillage(sf.getVillages().get(position));
			villageName.setVisibility(View.VISIBLE);
			villageExtId.setVisibility(View.VISIBLE);
			villageNameText.setText(sf.getVillage().getName());
			villageExtIdText.setText(sf.getVillage().getExtId());
			setPhase(UpdateEvent.ROUND);
		}
		else if (phase.equals(UpdateEvent.LOCATION)) {
			sf.setLocation(sf.getLocations().get(position));
			displayLocationState();
			setPhase(UpdateEvent.VISIT);
		}
		else if (phase.equals(UpdateEvent.ROUND)) {
			sf.setRound(sf.getRounds().get(position));
			roundNumber.setVisibility(View.VISIBLE);
			roundStartDate.setVisibility(View.VISIBLE);
			roundEndDate.setVisibility(View.VISIBLE);
			roundNumberText.setText(sf.getRound().getRoundNumber());
			roundStartDateText.setText(sf.getRound().getStartDate());
			roundEndDateText.setText(sf.getRound().getEndDate());
			setPhase(UpdateEvent.LOCATION);
		}
		else if (phase.equals(UpdateEvent.INDIVIDUAL)) {
			
			sf.setIndividual(sf.getIndividuals().get(position));
			individualExtId.setVisibility(View.VISIBLE);
			individualFirstName.setVisibility(View.VISIBLE);
			individualLastName.setVisibility(View.VISIBLE);
			individualDob.setVisibility(View.VISIBLE);
			individualExtIdText.setText(sf.getIndividual().getExtId());
			individualFirstNameText.setText(sf.getIndividual().getFirstName());
			individualLastNameText.setText(sf.getIndividual().getLastName());
			individualDobText.setText(sf.getIndividual().getDob());
			
			boolean result = determineSocialGroupForIndividual();
			if (result)
				createHouseholdSelectionDialog(null);
			
			setPhase(UpdateEvent.XFORMS);
		}
	}
	
	private boolean determineSocialGroupForIndividual() {
		// get the socialgroups the individual is a part of
		List<SocialGroup> list = databaseAdapter.getSocialGroupsForIndividual(sf.getIndividual().getExtId());	

		sf.setSocialgroups(list);
	
		// if the individual is in more that one social group then the socialgroup must be specified
		if (list.size() > 1) 
			return true;
		else if (list.size() == 1) 
			sf.setSocialGroupDialogSelection(0);
		else {
			Toast.makeText(getApplicationContext(),	getString(R.string.household_not_found), Toast.LENGTH_SHORT).show();
			sf.setSocialgroup(new SocialGroup());
		}
		return false;
	}
		
	/**
	 * Clears all state and returns the phase to Location.
	 */
	public void reset() {
		setPhase(UpdateEvent.LOCATION);	
		if (vf != null) {
			vf.reset();
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
		else if (ROUND_PHASE)
			return UpdateEvent.ROUND;
		else if (LOCATION_PHASE)
			return UpdateEvent.LOCATION;
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
		else if (phase.equals(UpdateEvent.LOCATION)) {
			setStateLocation();
			
			restoreRegionTextFields();
			restoreSubRegionTextFields();
			restoreVillageTextFields();
		}
		else if (phase.equals(UpdateEvent.ROUND)) {
			setStateRound();
			
			restoreRegionTextFields();
			restoreSubRegionTextFields();
			restoreVillageTextFields();
			restoreLocationTextFields();
		}
		else if (phase.equals(UpdateEvent.VISIT)) {
			setStateVisit();
			
			restoreRegionTextFields();
			restoreSubRegionTextFields();
			restoreVillageTextFields();
			restoreLocationTextFields();
			restoreRoundTextFields();
		}
		else if (phase.equals(UpdateEvent.INDIVIDUAL)) {
			setStateIndividual();
			
			restoreRegionTextFields();
			restoreSubRegionTextFields();
			restoreVillageTextFields();
			restoreLocationTextFields();
			restoreRoundTextFields();
		}
		else if (phase.equals(UpdateEvent.XFORMS)) {
			setStateFinish();
			
			restoreRegionTextFields();
			restoreSubRegionTextFields();
			restoreVillageTextFields();
			restoreLocationTextFields();
			restoreRoundTextFields();
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
			clearLocationTextFields();
			clearRoundTextFields();
			clearIndividualTextFields();
		
			sf.setRegion(new LocationHierarchy());
			sf.setSubRegion(new LocationHierarchy());
			sf.setVillage(new LocationHierarchy());
			sf.setLocation(new Location());
			sf.setRound(new Round());
			sf.setIndividual(new Individual());
			sf.setRelationship(new Relationship());
			sf.setPregnancyOutcome(new PregnancyOutcome());
		}
		else if (phase.equals(UpdateEvent.SUBREGION)) {		
			setStateSubRegion();
			
			clearSubRegionTextFields();
			clearVillageTextFields();
			clearLocationTextFields();
			clearRoundTextFields();
			clearIndividualTextFields();
			
			sf.setSubRegion(new LocationHierarchy());
			sf.setVillage(new LocationHierarchy());
			sf.setLocation(new Location());
			sf.setRound(new Round());
			sf.setIndividual(new Individual());
			sf.setRelationship(new Relationship());
			sf.setPregnancyOutcome(new PregnancyOutcome());
		}
		else if (phase.equals(UpdateEvent.VILLAGE)) {
			setStateVillage();
			
			clearVillageTextFields();
			clearLocationTextFields();
			clearRoundTextFields();
			clearIndividualTextFields();
			
			sf.setVillage(new LocationHierarchy());
			sf.setLocation(new Location());
			sf.setRound(new Round());
			sf.setIndividual(new Individual());
			sf.setRelationship(new Relationship());
			sf.setPregnancyOutcome(new PregnancyOutcome());
		}
		else if (phase.equals(UpdateEvent.LOCATION)) {
			setStateLocation();
			
			clearLocationTextFields();
			clearIndividualTextFields();
			
			sf.setLocation(new Location());
			sf.setIndividual(new Individual());
			sf.setRelationship(new Relationship());
			sf.setPregnancyOutcome(new PregnancyOutcome());
		}
		else if (phase.equals(UpdateEvent.ROUND)) {
			setStateRound();
			
			clearRoundTextFields();
			clearIndividualTextFields();
			
			sf.setRound(new Round());
			sf.setIndividual(new Individual());
			sf.setRelationship(new Relationship());
			sf.setPregnancyOutcome(new PregnancyOutcome());
		}
		else if (phase.equals(UpdateEvent.VISIT)) {
			setStateVisit();
			
			clearIndividualTextFields();
			
			sf.setIndividual(new Individual());
			sf.setRelationship(new Relationship());
			sf.setPregnancyOutcome(new PregnancyOutcome());
		}
		else if (phase.equals(UpdateEvent.INDIVIDUAL)) {
			setStateIndividual();
			
			clearIndividualTextFields();
			
			sf.setIndividual(new Individual());
			sf.setRelationship(new Relationship());
			sf.setPregnancyOutcome(new PregnancyOutcome());
		}
		else if (phase.equals(UpdateEvent.XFORMS)) {
			setStateFinish();
			sf.setRelationship(new Relationship());
			sf.setPregnancyOutcome(new PregnancyOutcome());
		}
	}
	
	/**
	 * This is responsible for displaying location state data
	 */
	private void displayLocationState() {
		locationName.setVisibility(View.VISIBLE);
		locationExtId.setVisibility(View.VISIBLE);
		locationLatitude.setVisibility(View.VISIBLE);
		locationLongitude.setVisibility(View.VISIBLE);
		locationNameText.setText(sf.getLocation().getName());
		locationExtIdText.setText(sf.getLocation().getExtId());
		locationLatitudeText.setText(sf.getLocation().getLatitude());
		locationLongitudeText.setText(sf.getLocation().getLongitude());
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
		regionNameText.setText(sf.getRegion().getName());
		regionExtIdText.setText(sf.getRegion().getExtId());
		regionName.setVisibility(View.VISIBLE);
		regionExtId.setVisibility(View.VISIBLE);
	}
	
	private void restoreSubRegionTextFields() {
		subRegionNameText.setText(sf.getSubRegion().getName());
		subRegionExtIdText.setText(sf.getSubRegion().getExtId());
		subRegionName.setVisibility(View.VISIBLE);
		subRegionExtId.setVisibility(View.VISIBLE);
	}
	
	private void restoreVillageTextFields() {
		villageNameText.setText(sf.getVillage().getName());
		villageExtIdText.setText(sf.getVillage().getExtId());
		villageName.setVisibility(View.VISIBLE);
		villageExtId.setVisibility(View.VISIBLE);
	}
	
	private void restoreRoundTextFields() {
		roundNumberText.setText(sf.getRound().getRoundNumber());
		roundStartDateText.setText(sf.getRound().getStartDate());
		roundEndDateText.setText(sf.getRound().getEndDate());
		roundNumber.setVisibility(View.VISIBLE);
		roundStartDate.setVisibility(View.VISIBLE);
		roundEndDate.setVisibility(View.VISIBLE);
	}
	
	private void restoreLocationTextFields() {
		locationNameText.setText(sf.getLocation().getName());
		locationExtIdText.setText(sf.getLocation().getExtId());
		locationLatitudeText.setText(sf.getLocation().getLatitude());
		locationLongitudeText.setText(sf.getLocation().getLongitude());
		locationName.setVisibility(View.VISIBLE);
		locationExtId.setVisibility(View.VISIBLE);
		locationLatitude.setVisibility(View.VISIBLE);
		locationLongitude.setVisibility(View.VISIBLE);
	}
	
	private void restoreIndividualTextFields() {
		individualExtIdText.setText(sf.getIndividual().getExtId());
		individualFirstNameText.setText(sf.getIndividual().getFirstName());
		individualLastNameText.setText(sf.getIndividual().getLastName());
		individualDobText.setText(sf.getIndividual().getDob());
		individualExtId.setVisibility(View.VISIBLE);
		individualFirstName.setVisibility(View.VISIBLE);
		individualLastName.setVisibility(View.VISIBLE);
		individualDob.setVisibility(View.VISIBLE);
	}
	
	private void toggleUpdateEventButtons(Boolean value) {
		householdBtn.setEnabled(value); 
		relationshipBtn.setEnabled(value);
		membershipBtn.setEnabled(value);
		outMigrationBtn.setEnabled(value);
		deathBtn.setEnabled(value);
		
		if (value == true && sf.getIndividual().getGender().equalsIgnoreCase("Female")) {
			pregRegBtn.setEnabled(true);
			birthRegBtn.setEnabled(true);
		}
		else {
			pregRegBtn.setEnabled(false);
			birthRegBtn.setEnabled(false);
		}
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
		
		findLocationGeoPointBtn.setEnabled(true);
		finishVisitBtn.setEnabled(false);
		createVisitBtn.setEnabled(false);
		clearLocationBtn.setEnabled(false);
		clearIndividualBtn.setEnabled(false);
		regionBtn.setEnabled(true);
		subRegionBtn.setEnabled(false);
		villageBtn.setEnabled(false);
		roundBtn.setEnabled(false);
		locationBtn.setEnabled(false);
		individualBtn.setEnabled(false);
		inMigrationBtn.setEnabled(false);
		toggleUpdateEventButtons(false);
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
		
		findLocationGeoPointBtn.setEnabled(false);
		finishVisitBtn.setEnabled(false);
		createVisitBtn.setEnabled(false);
		clearLocationBtn.setEnabled(false);
		clearIndividualBtn.setEnabled(false);
		regionBtn.setEnabled(false);
		subRegionBtn.setEnabled(true);
		villageBtn.setEnabled(false);
		roundBtn.setEnabled(false);
		locationBtn.setEnabled(false);
		individualBtn.setEnabled(false);
		inMigrationBtn.setEnabled(false);
		toggleUpdateEventButtons(false);
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
	
		findLocationGeoPointBtn.setEnabled(false);
		finishVisitBtn.setEnabled(false);
		createVisitBtn.setEnabled(false);
		clearLocationBtn.setEnabled(false);
		clearIndividualBtn.setEnabled(false);
		regionBtn.setEnabled(false);
		subRegionBtn.setEnabled(false);
		villageBtn.setEnabled(true);
		roundBtn.setEnabled(false);
		locationBtn.setEnabled(false);
		individualBtn.setEnabled(false);
		inMigrationBtn.setEnabled(false);
		toggleUpdateEventButtons(false);
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
		
		findLocationGeoPointBtn.setEnabled(false);
		finishVisitBtn.setEnabled(false);
		createLocationBtn.setEnabled(true);
		createVisitBtn.setEnabled(false);
		clearLocationBtn.setEnabled(false);
		clearIndividualBtn.setEnabled(false);
		regionBtn.setEnabled(false);
		subRegionBtn.setEnabled(false);
		villageBtn.setEnabled(false);
		roundBtn.setEnabled(false);
		locationBtn.setEnabled(true);
		individualBtn.setEnabled(false);
		inMigrationBtn.setEnabled(false);
		toggleUpdateEventButtons(false);
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
		
		findLocationGeoPointBtn.setEnabled(false);
		finishVisitBtn.setEnabled(false);
		createLocationBtn.setEnabled(false);
		createVisitBtn.setEnabled(false);
		clearLocationBtn.setEnabled(false);
		clearIndividualBtn.setEnabled(false);
		regionBtn.setEnabled(false);
		subRegionBtn.setEnabled(false);
		villageBtn.setEnabled(false);
		roundBtn.setEnabled(true);
		locationBtn.setEnabled(false);
		individualBtn.setEnabled(false);
		inMigrationBtn.setEnabled(false);
		toggleUpdateEventButtons(false);
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
		
		findLocationGeoPointBtn.setEnabled(false);
		finishVisitBtn.setEnabled(false);
		createLocationBtn.setEnabled(false);
		createVisitBtn.setEnabled(true);
		clearLocationBtn.setEnabled(true);
		clearIndividualBtn.setEnabled(false);
		regionBtn.setEnabled(false);
		subRegionBtn.setEnabled(false);
		villageBtn.setEnabled(false);
		roundBtn.setEnabled(false);
		locationBtn.setEnabled(false);
		individualBtn.setEnabled(false);
		inMigrationBtn.setEnabled(false);
		toggleUpdateEventButtons(false);
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
		
		findLocationGeoPointBtn.setEnabled(false);
		finishVisitBtn.setEnabled(true);
		createLocationBtn.setEnabled(false);
		createVisitBtn.setEnabled(false);
		clearLocationBtn.setEnabled(false);
		clearIndividualBtn.setEnabled(false);
		regionBtn.setEnabled(false);
		subRegionBtn.setEnabled(false);
		villageBtn.setEnabled(false);
		roundBtn.setEnabled(false);
		locationBtn.setEnabled(false);
		individualBtn.setEnabled(true);
		inMigrationBtn.setEnabled(true);
		toggleUpdateEventButtons(false);
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
		
		findLocationGeoPointBtn.setEnabled(false);
		regionBtn.setEnabled(false);
		finishVisitBtn.setEnabled(true);
		createLocationBtn.setEnabled(false);
		createVisitBtn.setEnabled(false);
		clearLocationBtn.setEnabled(false);
		clearIndividualBtn.setEnabled(true);
		subRegionBtn.setEnabled(false);
		villageBtn.setEnabled(false);
		roundBtn.setEnabled(false);
		locationBtn.setEnabled(false);
		individualBtn.setEnabled(false);
		inMigrationBtn.setEnabled(false);
		toggleUpdateEventButtons(true);
	}
}
