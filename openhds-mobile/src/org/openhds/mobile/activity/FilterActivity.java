package org.openhds.mobile.activity;

import java.util.ArrayList;
import java.util.List;
import org.openhds.mobile.R;
import org.openhds.mobile.cell.ValueFragmentCell;
import org.openhds.mobile.database.DatabaseAdapter;
import org.openhds.mobile.fragment.SelectionFilterFragment;
import org.openhds.mobile.fragment.ValueFragment;
import org.openhds.mobile.listener.ValueSelectedListener;
import org.openhds.mobile.model.Location;
import org.openhds.mobile.model.LocationHierarchy;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.view.View;
import android.view.View.OnClickListener;

public class FilterActivity extends FragmentActivity implements OnClickListener, ValueSelectedListener {
	
	private DatabaseAdapter databaseAdapter;
	
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
	    
	    databaseAdapter = new DatabaseAdapter(getBaseContext());
	    
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

	public void onValueSelected(int position) {
		String name = valueFragment.getItems().get(position).getItem1();
		String extId = valueFragment.getItems().get(position).getItem2();
		Intent i = new Intent();
		i.putExtra("name", name);
		i.putExtra("extId", extId);
		i.putExtra("type", type);
		setResult(Activity.RESULT_OK, i);
		finish();
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
		List<ValueFragmentCell> list = new ArrayList<ValueFragmentCell>();
		String location = locationTxt.getText().toString();
		String firstName = firstNameTxt.getText().toString();
		String lastName = lastNameTxt.getText().toString();
		
		String gender = "";
		if (maleBtn.isChecked() || femaleBtn.isChecked()) {
			gender = maleBtn.isChecked() ? "Male" : "Female";
		}
		
		StringBuilder builder = new StringBuilder();
		
		if (!location.equals("")) {
			builder.append("currentResidence = '" + location + "' ");
		}
		if (!firstName.equals("")) {
			if (builder.length() > 0)
				builder.append("and ");
			builder.append("firstName like '%" + firstName + "%' ");
		}
		if (!lastName.equals("")) {
			if (builder.length() > 0)
				builder.append("and ");
			builder.append("lastName like '%" + lastName + "%' ");
		}
		if (!gender.equals("")) {
			if (builder.length() > 0)
				builder.append("and ");
			builder.append("gender = '" + gender + "' ");
		}
		
		Cursor cursor = null;
		databaseAdapter.open();

		if (builder.length() > 0) 
			cursor = databaseAdapter.getDatabase().query("individual", new String[] {"extId", "firstName", "lastName"}, builder.toString(), null, null, null, "extId");
		else 
			cursor = databaseAdapter.getDatabase().query("individual", new String[] {"extId", "firstName", "lastName"}, null, null, null, null, "extId");
		
		cursor.moveToFirst();
	    while (!cursor.isAfterLast()) {
	    	ValueFragmentCell cell = new ValueFragmentCell(cursor.getString(1) + " " + cursor.getString(2), cursor.getString(0));
	    	list.add(cell);
	    	cursor.moveToNext();
	    }
	    valueFragment.setContent(list);
	    
		databaseAdapter.close();
		cursor.close();
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
}
