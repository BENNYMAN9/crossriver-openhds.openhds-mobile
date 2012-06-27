package org.openhds.activity;

import java.util.ArrayList;
import java.util.List;
import org.openhds.activity.R;
import org.openhds.database.DatabaseAdapter;
import org.openhds.fragment.ValueFragment;
import org.openhds.listener.ValueSelectedListener;
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
	
	private TextView regionText;
	private TextView subRegionText;
	private TextView villageText;
	
	private Button regionBtn;
	private Button subRegionBtn;
	private Button villageBtn;
	private Button resetBtn;
	
	ValueFragment valueFragment;
	
	private boolean REGION_PHASE = true;
	private boolean SUB_REGION_PHASE = false;
	private boolean VILLAGE_PHASE = false;
	
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.main);
	    
        databaseAdapter = new DatabaseAdapter(getBaseContext());
        
        resetBtn = (Button) findViewById(R.id.resetBtn);
        resetBtn.setOnClickListener(this);

        regionBtn = (Button) findViewById(R.id.regionBtn);
        regionBtn.setOnClickListener(this);
        regionText = (TextView) findViewById(R.id.regionText);
	    
        subRegionBtn = (Button) findViewById(R.id.subRegionBtn);
        subRegionBtn.setOnClickListener(this);
        subRegionText = (TextView) findViewById(R.id.subRegionText);
	    
        villageBtn = (Button) findViewById(R.id.villageBtn);
        villageBtn.setOnClickListener(this);
        villageText = (TextView) findViewById(R.id.villageText);
        
		valueFragment = (ValueFragment)getSupportFragmentManager().findFragmentById(R.id.valueFragment);
	    	       	    
	    ActionBar actionBar = getActionBar();
	    actionBar.show();
	}
	
	public void reset() {
		REGION_PHASE = true;
		SUB_REGION_PHASE = false;
		VILLAGE_PHASE = false;
		
		regionText.setText("");
		subRegionText.setText("");
		villageText.setText("");
		
		regionBtn.setEnabled(true);
		subRegionBtn.setEnabled(false);
		villageBtn.setEnabled(false);
		resetBtn.setEnabled(false);
		
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
    	List<String> regions = databaseAdapter.getAllRegions("LGA");
    	if (valueFragment != null) {
    		valueFragment.setContent(regions);
    	}
    }
    
    private void loadSubRegionValueData() {
      	List<String> subRegions = databaseAdapter.getAllRegions("Ward");
    	if (valueFragment != null) {
    		valueFragment.setContent(subRegions);
    	}
    }
    
    private void loadVillageValueData() {
      	List<String> villages = databaseAdapter.getAllRegions("Village");
    	if (valueFragment != null) {
    		valueFragment.setContent(villages);
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
			case R.id.resetBtn:
				reset();
		}	
	}
	
	private String getPhase() {
		if (VILLAGE_PHASE)
			return "VILLAGE";
		else if (SUB_REGION_PHASE)
			return "SUB_REGION";
		else
			return "REGION";
	}

	public void onValueSelected(String value) {
		String phase = getPhase();
		if (phase.equals("REGION")) {
			regionText.setText(value);
			REGION_PHASE = false;
			SUB_REGION_PHASE = true;
			regionBtn.setEnabled(false);
			resetBtn.setEnabled(true);
			subRegionBtn.setEnabled(true);
		}
		else if (phase.equals("SUB_REGION")) {
			subRegionText.setText(value);
			SUB_REGION_PHASE = false;
			VILLAGE_PHASE = true;
			subRegionBtn.setEnabled(false);
			villageBtn.setEnabled(true);
		}
		else if (phase.equals("VILLAGE")) {
			villageText.setText(value);
			VILLAGE_PHASE = false;
			villageBtn.setEnabled(false);
		}
			
		
	}
}
