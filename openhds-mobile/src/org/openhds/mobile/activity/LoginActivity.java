package org.openhds.mobile.activity;

import org.openhds.mobile.R;
import org.openhds.mobile.database.DatabaseAdapter;
import org.openhds.mobile.listener.RetrieveFieldWorkersListener;
import org.openhds.mobile.model.Result;
import org.openhds.mobile.task.LoginTask;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.DialogInterface.OnCancelListener;
import android.os.Bundle;
import android.os.AsyncTask.Status;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

public class LoginActivity extends Activity implements OnClickListener, RetrieveFieldWorkersListener {
	
    private static final int LOGIN_ACTIVITY = 1;

	private TextView extIdText;
	private TextView passwordText;
	private Button loginButton;
	private CheckBox registerChkBox;
	
	private ProgressDialog dialog;
	private DatabaseAdapter databaseAdapter;
	private SharedPreferences settings;
	
    private LoginTask loginTask = null;
	
	@Override
    protected void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.login);
	    
	    initializeProgressDialog();
	    
        databaseAdapter = new DatabaseAdapter(getBaseContext());
		settings = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
	    
        extIdText = (TextView) findViewById(R.id.extIdText);
        passwordText = (TextView) findViewById(R.id.passwordText);
        
	    loginButton = (Button) findViewById(R.id.loginBtn);
	    loginButton.setOnClickListener(this);
	    
	    registerChkBox = (CheckBox) findViewById(R.id.registerChkBox);
	    registerChkBox.setOnClickListener(this);
    }
		
	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.registerChkBox: 
			if (registerChkBox.isChecked()) 
				loginButton.setText("Register");
			else 
				loginButton.setText("Login");
			break;
		case R.id.loginBtn: 
			
			String extId = extIdText.getText().toString();
			String password = passwordText.getText().toString();
			
			if (registerChkBox.isChecked()) {
				dialog.show();
				SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
				if (loginTask == null)
					loginTask = new LoginTask(databaseAdapter, settings, this, dialog, extId, password, true);
				
	    		if (loginTask.getStatus() == Status.PENDING) 
	    			loginTask.execute();	
			}
			else {
				boolean result = databaseAdapter.findFieldWorker(extId, password);
				if (result) {
					startUpdateActivity();
				}
				else {
					Toast.makeText(getApplicationContext(),	getString(R.string.bad_authentication), Toast.LENGTH_SHORT).show();
				}
			}
			break;
		}
	}
	
	private void startUpdateActivity() {
		Intent intent = new Intent(getApplicationContext(), UpdateActivity.class);
        intent.putExtra("username", extIdText.getText().toString());
        intent.putExtra("password", passwordText.getText().toString());
        passwordText.setText("");
        startActivity(intent);
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		if (loginTask != null)
			loginTask.cancel(true);
	}
		
	@Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
    	initializeProgressDialog();

        if (resultCode == RESULT_CANCELED) {
            // request was canceled, so do nothing
            return;
        }

        switch (requestCode) {
            case LOGIN_ACTIVITY:
            	break;
        }
    }
	
	private void initializeProgressDialog() {
        dialog = new ProgressDialog(this);
        dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        dialog.setTitle("Working...");
        dialog.setMessage("Do not interrupt");
        dialog.setCancelable(true);
        dialog.setOnCancelListener(new MyOnCancelListener());
	}

	public void retrieveFieldWorkersComplete(Result result) {
		switch (result) {
			case CREATED_FIELDWORKER_SUCCESS:
				Toast.makeText(getApplicationContext(),	getString(R.string.field_worker_created), Toast.LENGTH_SHORT).show();
				break;
			case BAD_AUTHENTICATION:
				Toast.makeText(getApplicationContext(),	getString(R.string.bad_authentication), Toast.LENGTH_SHORT).show();
				break;
			case BAD_XML:
				Toast.makeText(getApplicationContext(),	getString(R.string.bad_xml), Toast.LENGTH_SHORT).show();
				break;
			case FIELDWORKER_ALREADY_EXISTS:
				Toast.makeText(getApplicationContext(),	getString(R.string.field_worker_already_exists), Toast.LENGTH_SHORT).show();
		}
		dialog.dismiss();
		loginTask = null;
	}
	
	private class MyOnCancelListener implements OnCancelListener {
		public void onCancel(DialogInterface dialog) {
			if (loginTask != null)
				loginTask.cancel(true);
			finish();
			Toast.makeText(getApplicationContext(),	getString(R.string.retrieving_fieldworkers_interrupted), Toast.LENGTH_SHORT).show();
		}	
	}
}
