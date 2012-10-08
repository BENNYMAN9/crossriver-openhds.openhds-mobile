package org.openhds.mobile.activity;

import org.openhds.mobile.R;
import org.openhds.mobile.fragment.SelectionFilterFragment;
import org.openhds.mobile.fragment.ValueFragment;
import org.openhds.mobile.fragment.ValueFragment.ValueListener;
import org.openhds.mobile.model.Individual;
import org.openhds.mobile.model.Location;
import org.openhds.mobile.model.LocationHierarchy;
import org.openhds.mobile.model.Round;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;

/**
 * This activity is only used in searching for an Individual.
 * This activity is launched before creating Relationship and Internal In Migration events.
 * It's also launched before creating a new Location.
 */
public class FilterActivity extends FragmentActivity implements OnClickListener, ValueListener {
	
	private EditText regionTxt, subRegionTxt, villageTxt, locationTxt,
					 firstNameTxt, lastNameTxt;
	private RadioButton maleBtn, femaleBtn;
	private Button clearBtn, searchBtn;
	
	private SelectionFilterFragment selectionFilterFragment;
	private ValueFragment valueFragment;
	
	private String type;
			
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	    setContentView(R.layout.filter); 
	    
	    regionTxt = (EditText) findViewById(R.id.regionTxt);
	    regionTxt.setOnClickListener(this);
	    
	    subRegionTxt = (EditText) findViewById(R.id.subRegionTxt);
	    subRegionTxt.setOnClickListener(this);
	    
	    villageTxt = (EditText) findViewById(R.id.villageTxt);
	    villageTxt.setOnClickListener(this);
	    
	    locationTxt = (EditText) findViewById(R.id.locationTxt);
	    locationTxt.setOnClickListener(this);
	    
	    firstNameTxt = (EditText) findViewById(R.id.firstNameTxt);
	    firstNameTxt.setOnClickListener(this);
	    
	    lastNameTxt = (EditText) findViewById(R.id.lastNameTxt);
	    lastNameTxt.setOnClickListener(this);
	    
	    maleBtn = (RadioButton) findViewById(R.id.maleBtn);
	    maleBtn.setOnClickListener(this);
	    
	    femaleBtn = (RadioButton) findViewById(R.id.femaleBtn); 
	    femaleBtn.setOnClickListener(this);
	    
	    clearBtn = (Button) findViewById(R.id.clearFilterBtn);
	    clearBtn.setOnClickListener(this);
	    
	    searchBtn = (Button) findViewById(R.id.searchFilterBtn);
	    searchBtn.setOnClickListener(this);   
	    
	    selectionFilterFragment = (SelectionFilterFragment)getSupportFragmentManager().findFragmentById(R.id.selectionFilterFragment);
		valueFragment = (ValueFragment)getSupportFragmentManager().findFragmentById(R.id.valueFragment);

		processExtras();
	}
	
	private void processExtras() {	
		LocationHierarchy region = (LocationHierarchy) getIntent().getExtras().getSerializable("region");
		LocationHierarchy subRegion = (LocationHierarchy) getIntent().getExtras().getSerializable("subRegion");
		LocationHierarchy village = (LocationHierarchy) getIntent().getExtras().getSerializable("village");
		Location location = (Location) getIntent().getExtras().getSerializable("location");
		type = getIntent().getExtras().getString("type");
		
		regionTxt.setText(region.getExtId());
		subRegionTxt.setText(subRegion.getExtId());
		villageTxt.setText(village.getExtId());
		locationTxt.setText(location.getExtId());
		selectionFilterFragment.setRegion(region);
		selectionFilterFragment.setSubRegion(subRegion);
		selectionFilterFragment.setVillage(village);
		selectionFilterFragment.setLocation(location);
	}

	public void onClick(View view) {
		switch (view.getId()) {
			case R.id.regionTxt:
				createRegionSelectionDialog();
				break;
			case R.id.subRegionTxt:
				createSubRegionSelectionDialog();
				break;
			case R.id.villageTxt:
				createVillageSelectionDialog();
				break;
			case R.id.locationTxt:
				createLocationSelectionDialog();
				break;
			case R.id.searchFilterBtn:
				search();
				break;
			case R.id.clearFilterBtn:
				clear();
				break;
		}
	}
	
	private void clear() {
		selectionFilterFragment.setFirstName("");
		selectionFilterFragment.setLastName("");
		selectionFilterFragment.setGender("");
		firstNameTxt.setText("");
		lastNameTxt.setText("");
		maleBtn.setChecked(false);
		femaleBtn.setChecked(false);
	}
	
	private void search() {
		String location = locationTxt.getText().toString();
		String firstName = firstNameTxt.getText().toString();
		String lastName = lastNameTxt.getText().toString();
		
		String gender = "";
		if (maleBtn.isChecked() || femaleBtn.isChecked()) {
			gender = maleBtn.isChecked() ? "Male" : "Female";
		}
		
		valueFragment.loadFilteredIndividuals(location, firstName, lastName, gender);
	}
	
	private void createRegionSelectionDialog() {
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
		alertDialogBuilder.setTitle("Select Region");
		alertDialogBuilder.setSingleChoiceItems(selectionFilterFragment.getRegionsDialog(), -1, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int clicked) {
				selectionFilterFragment.setRegionDialogSelection(clicked);
				regionTxt.setText(selectionFilterFragment.getRegion().getExtId());
				dialog.dismiss();
			}
		});
	
		AlertDialog alertDialog = alertDialogBuilder.create();
		alertDialog.show();
	}
	
	private void createSubRegionSelectionDialog() {
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
		alertDialogBuilder.setTitle("Select Sub Region");
		alertDialogBuilder.setSingleChoiceItems(selectionFilterFragment.getSubRegionsDialog(), -1, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int clicked) {
				selectionFilterFragment.setSubRegionDialogSelection(clicked);
				subRegionTxt.setText(selectionFilterFragment.getSubRegion().getExtId());
				dialog.dismiss();
			}
		});
	
		AlertDialog alertDialog = alertDialogBuilder.create();
		alertDialog.show();
	}
	
	private void createVillageSelectionDialog() {
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
		alertDialogBuilder.setTitle("Select Village");
		alertDialogBuilder.setSingleChoiceItems(selectionFilterFragment.getVillagesDialog(), -1, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int clicked) {
				selectionFilterFragment.setVillageDialogSelection(clicked);
				villageTxt.setText(selectionFilterFragment.getVillage().getExtId());
				dialog.dismiss();
			}
		});
	
		AlertDialog alertDialog = alertDialogBuilder.create();
		alertDialog.show();
	}
	
	private void createLocationSelectionDialog() {
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
		alertDialogBuilder.setTitle("Select Location");
		alertDialogBuilder.setSingleChoiceItems(selectionFilterFragment.getLocationsDialog(), -1, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int clicked) {
				selectionFilterFragment.setLocationDialogSelection(clicked);
				locationTxt.setText(selectionFilterFragment.getLocation().getExtId());
				dialog.dismiss();
			}
		});
	
		AlertDialog alertDialog = alertDialogBuilder.create();
		alertDialog.show();
	}
	   
    public void onIndividualSelected(Individual individual) {
        Intent i = new Intent();
        i.putExtra("name", individual.getFirstName() + " " + individual.getLastName());
        i.putExtra("extId", individual.getExtId());
        i.putExtra("type", type);
        setResult(Activity.RESULT_OK, i);
        finish();
    }
	
    public void onHierarchySelected(LocationHierarchy hierarchy) {
        
    }

    public void onSubRegionSelected(LocationHierarchy subregion) {
        
    }

    public void onVillageSelected(LocationHierarchy village) {
        
    }

    public void onRoundSelected(Round round) {
        
    }

    public void onLocationSelected(Location location) {
        
    }
}
