package org.openhds.mobile.activity;

import org.openhds.mobile.Converter;
import org.openhds.mobile.FieldWorkerProvider;
import org.openhds.mobile.InstanceProviderAPI;
import org.openhds.mobile.OpenHDS;
import org.openhds.mobile.Queries;
import org.openhds.mobile.R;
import org.openhds.mobile.fragment.SelectionFragment;
import org.openhds.mobile.fragment.ValueFragment;
import org.openhds.mobile.listener.OdkFormLoadListener;
import org.openhds.mobile.model.FieldWorker;
import org.openhds.mobile.model.Individual;
import org.openhds.mobile.model.Location;
import org.openhds.mobile.model.LocationHierarchy;
import org.openhds.mobile.model.PregnancyOutcome;
import org.openhds.mobile.model.Relationship;
import org.openhds.mobile.model.Round;
import org.openhds.mobile.model.SocialGroup;
import org.openhds.mobile.model.UpdateEvent;
import org.openhds.mobile.model.Visit;
import org.openhds.mobile.task.OdkGeneratedFormLoadTask;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.LoaderManager.LoaderCallbacks;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;

public class UpdateActivity extends Activity implements OnClickListener, ValueFragment.ValueListener,
        OdkFormLoadListener, LoaderCallbacks<Cursor>, FieldWorkerProvider, SelectionFragment.Listener {
	
	private Button findLocationGeoPointBtn, createLocationBtn, createVisitBtn, clearLocationBtn, clearIndividualBtn,
	 			   householdBtn, membershipBtn, relationshipBtn, inMigrationBtn, outMigrationBtn, pregRegBtn, birthRegBtn, deathBtn, 
	 			   finishVisitBtn;
		
	// this activity manages three fragments
	private SelectionFragment sf;
	private ValueFragment vf;
	
	private final int SELECTED_XFORM = 1;
	private final int FILTER = 2;
	private final int LOCATION_GEOPOINT = 3;
	
	// the uri of the last viewed xform
	private Uri contentUri;
	
	// status flags indicating a dialog, used for restoring the activity
	private boolean formUnFinished = false;
	private boolean xFormNotFound = false;
	
	// the workflow for this activity is arranged into multiple phases
	private boolean SUB_REGION_PHASE = false;
	private boolean VILLAGE_PHASE = false;
	private boolean LOCATION_PHASE = false;
	private boolean ROUND_PHASE = false;
	private boolean VISIT_PHASE = false;
	private boolean INDIVIDUAL_PHASE = false;
	private boolean XFORMS_PHASE = false;

    private ProgressDialog progDialog;
    private AlertDialog householdDialog;
				
    @Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.main);    	   
	   
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

    	sf = (SelectionFragment)getFragmentManager().findFragmentById(R.id.selectionFragment);
		vf = (ValueFragment)getFragmentManager().findFragmentById(R.id.valueFragment);
		
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
						    sf.displayLocationInfo();
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
						Individual individual = (Individual) data.getExtras().getSerializable("individual");
						sf.setIndividual(individual);
					    showSocialGroupLoadingDialog();
						getLoaderManager().restartLoader(0, null, this);
					}
				}
				break;
			}
			case LOCATION_GEOPOINT: {
				if (resultCode == RESULT_OK) {
					String extId = data.getExtras().getString("extId");
					ContentResolver resolver = getContentResolver();
					// a few things need to happen here:
					// * get the location by extId
					Cursor cursor = Queries.getLocationByExtId(resolver, extId);
					Location location = Converter.toLocation(cursor);
					
					// * figure out the parent location hierarchy
					cursor = Queries.getHierarchyByExtId(resolver, location.getHierarchy());
					LocationHierarchy village = Converter.toHierarhcy(cursor, true);
					
					cursor = Queries.getHierarchyByExtId(resolver, village.getParent());
					LocationHierarchy subRegion = Converter.toHierarhcy(cursor, true);
					
					cursor = Queries.getHierarchyByExtId(resolver, subRegion.getParent());
					LocationHierarchy region = Converter.toHierarhcy(cursor, true);
										
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

    private void showSocialGroupLoadingDialog() {
        progDialog = ProgressDialog.show(this, "Loading Social Groups...", "Loading Social Groups for this Location");
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
    
    private void loadRegionValueData() {
        vf.loadLocationHierarchy();
    }
    
    private void loadSubRegionValueData() {
        vf.loadSubRegion(sf.getRegion().getExtId());
    }
    
    private void loadVillageValueData() {
        vf.loadVillage(sf.getSubRegion().getExtId());
    }

    private void loadLocationValueData() {
        vf.loadLocations(sf.getVillage().getExtId());
    }
    
    private void loadRoundValueData() {
        vf.loadRounds();
    }
    
    private void loadIndividualValueData() {
        vf.loadIndividuals(sf.getLocation().getExtId());
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
				showSocialGroupLoadingDialog();
				getLoaderManager().restartLoader(0, null, UpdateActivity.this);
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
	 * Clears all state and returns the phase to Location.
	 */
	public void reset() {
		setPhase(UpdateEvent.LOCATION);	
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
			sf.restoreRegionTextFields();
		}
		else if (phase.equals(UpdateEvent.VILLAGE)) {
			setStateVillage();
			
			sf.restoreRegionTextFields();
			sf.restoreSubRegionTextFields();
		}
		else if (phase.equals(UpdateEvent.LOCATION)) {
			setStateLocation();
			
			sf.restoreRegionTextFields();
			sf.restoreSubRegionTextFields();
			sf.restoreVillageTextFields();
		}
		else if (phase.equals(UpdateEvent.ROUND)) {
			setStateRound();
			
			sf.restoreRegionTextFields();
			sf.restoreSubRegionTextFields();
			sf.restoreVillageTextFields();
			sf.restoreLocationTextFields();
		}
		else if (phase.equals(UpdateEvent.VISIT)) {
			setStateVisit();
			
			sf.restoreRegionTextFields();
			sf.restoreSubRegionTextFields();
			sf.restoreVillageTextFields();
			sf.restoreLocationTextFields();
			sf.restoreRoundTextFields();
		}
		else if (phase.equals(UpdateEvent.INDIVIDUAL)) {
			setStateIndividual();
			
			sf.restoreRegionTextFields();
			sf.restoreSubRegionTextFields();
			sf.restoreVillageTextFields();
			sf.restoreLocationTextFields();
			sf.restoreRoundTextFields();
		}
		else if (phase.equals(UpdateEvent.XFORMS)) {
			setStateFinish();
			
			sf.restoreRegionTextFields();
			sf.restoreSubRegionTextFields();
			sf.restoreVillageTextFields();
			sf.restoreLocationTextFields();
			sf.restoreRoundTextFields();
			sf.restoreIndividualTextFields();
		}
	}
	
	/**
	 * Sets the state to the phase specified.
	 */
	private void setPhase(String phase) {
		if (phase.equals(UpdateEvent.REGION)) {
			setStateRegion();
			
			sf.clearRegionTextFields();
			sf.clearSubRegionTextFields();
			sf.clearVillageTextFields();
			sf.clearLocationTextFields();
			sf.clearRoundTextFields();
			sf.clearIndividualTextFields();
		
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
			
			sf.clearSubRegionTextFields();
			sf.clearVillageTextFields();
			sf.clearLocationTextFields();
			sf.clearRoundTextFields();
			sf.clearIndividualTextFields();
			
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
			
			sf.clearVillageTextFields();
			sf.clearLocationTextFields();
			sf.clearRoundTextFields();
			sf.clearIndividualTextFields();
			
			sf.setVillage(new LocationHierarchy());
			sf.setLocation(new Location());
			sf.setRound(new Round());
			sf.setIndividual(new Individual());
			sf.setRelationship(new Relationship());
			sf.setPregnancyOutcome(new PregnancyOutcome());
		}
		else if (phase.equals(UpdateEvent.LOCATION)) {
			setStateLocation();
			
			sf.clearLocationTextFields();
			sf.clearIndividualTextFields();
			
			sf.setLocation(new Location());
			sf.setIndividual(new Individual());
			sf.setRelationship(new Relationship());
			sf.setPregnancyOutcome(new PregnancyOutcome());
		}
		else if (phase.equals(UpdateEvent.ROUND)) {
			setStateRound();
			
			sf.clearRoundTextFields();
			sf.clearIndividualTextFields();
			
			sf.setRound(new Round());
			sf.setIndividual(new Individual());
			sf.setRelationship(new Relationship());
			sf.setPregnancyOutcome(new PregnancyOutcome());
		}
		else if (phase.equals(UpdateEvent.VISIT)) {
			setStateVisit();
			
			sf.clearIndividualTextFields();
			
			sf.setIndividual(new Individual());
			sf.setRelationship(new Relationship());
			sf.setPregnancyOutcome(new PregnancyOutcome());
		}
		else if (phase.equals(UpdateEvent.INDIVIDUAL)) {
			setStateIndividual();
			
			sf.clearIndividualTextFields();
			
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
		sf.setRegionState();
		inMigrationBtn.setEnabled(false);
		toggleUpdateEventButtons(false);
	}
	
	private void setStateSubRegion() {
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
		sf.setSubRegionState();
		inMigrationBtn.setEnabled(false);
		toggleUpdateEventButtons(false);
	}
	
	private void setStateVillage() {
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
		sf.setVillageState();
		inMigrationBtn.setEnabled(false);
		toggleUpdateEventButtons(false);
	}
		
	private void setStateLocation() {
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
		sf.setLocationState();
		inMigrationBtn.setEnabled(false);
		toggleUpdateEventButtons(false);
	}
	
	private void setStateRound() {
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
		sf.setRoundState();
		inMigrationBtn.setEnabled(false);
		toggleUpdateEventButtons(false);
	}
	
	private void setStateVisit() { 
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
		sf.setVisitState();
		inMigrationBtn.setEnabled(false);
		toggleUpdateEventButtons(false);
	}
	
	private void setStateIndividual() {
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
		sf.setIndividualState();
		inMigrationBtn.setEnabled(true);
		toggleUpdateEventButtons(false);
	}
	
	private void setStateFinish() {
		SUB_REGION_PHASE = false;
		VILLAGE_PHASE = false;
		ROUND_PHASE = false;
		LOCATION_PHASE = false;
		VISIT_PHASE = false;
		INDIVIDUAL_PHASE = false;
		XFORMS_PHASE = true;
		
		findLocationGeoPointBtn.setEnabled(false);
		finishVisitBtn.setEnabled(true);
		createLocationBtn.setEnabled(false);
		createVisitBtn.setEnabled(false);
		clearLocationBtn.setEnabled(false);
		clearIndividualBtn.setEnabled(true);
		sf.setFinishState();
		inMigrationBtn.setEnabled(false);
		toggleUpdateEventButtons(true);
	}

    public FieldWorker getFieldWorker() {
        return (FieldWorker) getIntent().getExtras().getSerializable("fieldWorker");
    }

    public void onRegion() {
        loadRegionValueData();
    }

    public void onSubRegion() {
        loadSubRegionValueData();
    }

    public void onVillage() {
        loadVillageValueData();
    }

    public void onLocation() {
        loadLocationValueData();
    }

    public void onRound() {
        loadRoundValueData();
    }

    public void onIndividual() {
        loadIndividualValueData();
    }

    public void onHierarchySelected(LocationHierarchy hierarchy) {
        sf.setRegion(hierarchy);
        setPhase(UpdateEvent.SUBREGION);
    }

    public void onSubRegionSelected(LocationHierarchy subregion) {
        sf.setSubRegion(subregion);
        setPhase(UpdateEvent.VILLAGE);
    }

    public void onVillageSelected(LocationHierarchy village) {
        sf.setVillage(village);
        setPhase(UpdateEvent.ROUND);        
    }

    public void onRoundSelected(Round round) {
        sf.setRound(round);
        setPhase(UpdateEvent.LOCATION);
    }

    public void onLocationSelected(Location location) {
        sf.setLocation(location);
        setPhase(UpdateEvent.VISIT);        
    }

    public void onIndividualSelected(Individual individual) {
        sf.setIndividual(individual);
        setPhase(UpdateEvent.XFORMS);
    }

    public Loader<Cursor> onCreateLoader(int arg0, Bundle arg1) {
        Loader<Cursor> loader = null;
        switch (arg0) {
        case 0:
            Uri uri = OpenHDS.SocialGroups.CONTENT_LOCATION_ID_URI_BASE.buildUpon()
                    .appendPath(sf.getLocation().getExtId()).build();
            loader = new CursorLoader(this, uri, null, null, null, null);
            break;
        }

        return loader;
    }

    public void onLoadFinished(Loader<Cursor> arg0, Cursor arg1) {
        progDialog.dismiss();
        progDialog = null;
        
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Select a Household for the Individual");
        SimpleCursorAdapter adapter = new SimpleCursorAdapter(this, android.R.layout.simple_list_item_2, arg1,
                new String[] { OpenHDS.SocialGroups.COLUMN_SOCIALGROUP_GROUPNAME,
                        OpenHDS.SocialGroups.COLUMN_SOCIALGROUP_EXTID }, new int[] { android.R.id.text1,
                        android.R.id.text2 }, 0);
        builder.setAdapter(adapter, new DialogInterface.OnClickListener() {
            
            public void onClick(DialogInterface dialog, int which) {
                Cursor cursor = (Cursor)householdDialog.getListView().getItemAtPosition(which);
                SocialGroup sg = Converter.convertToSocialGroup(cursor);
                sf.setSocialgroup(sg);
                loadForm(UpdateEvent.INMIGRATION);
            }
        });
        builder.setNegativeButton("Cancel", null);
        householdDialog = builder.create();
        householdDialog.show();
    }

    public void onLoaderReset(Loader<Cursor> arg0) {
        householdDialog.dismiss();
        householdDialog = null;
    }

}
