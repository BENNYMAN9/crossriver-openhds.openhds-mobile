package org.openhds.activity;

import org.openhds.activity.R;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

public class UpdateActivity extends Activity {
	
    private static final int MENU_PREFERENCES = Menu.FIRST;
    private static final int MENU_SYNC_DATABASE = Menu.CATEGORY_SECONDARY;

	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.main);

	}
	
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        menu.add(0, MENU_PREFERENCES, 0, getString(R.string.configureDatabase)).setIcon(android.R.drawable.ic_menu_preferences);
        menu.add(1, MENU_SYNC_DATABASE, 0, getString(R.string.syncDatabase)).setIcon(android.R.drawable.ic_menu_preferences);
        return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case MENU_PREFERENCES:
                createPreferencesMenu();
                return true;
            case MENU_SYNC_DATABASE:
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
}
