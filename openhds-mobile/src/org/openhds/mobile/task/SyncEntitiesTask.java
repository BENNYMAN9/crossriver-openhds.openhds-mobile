package org.openhds.mobile.task;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.auth.AuthenticationException;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.auth.BasicScheme;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.openhds.mobile.activity.SyncDatabaseActivity;
import org.openhds.mobile.database.DatabaseAdapter;
import org.openhds.mobile.listener.CollectEntitiesListener;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;

/**
 * An AsyncTask to upload all OpenHDS data from the web service configured in ServerPreferences.
 */
public class SyncEntitiesTask extends AsyncTask<Void, String, Boolean> {
		
	private CollectEntitiesListener listener;
	private SyncDatabaseActivity activity;
	private DatabaseAdapter databaseAdapter;
	
	private UsernamePasswordCredentials creds;
	private ProgressDialog dialog;
	private HttpGet httpGet;
	private HttpClient client;
	
	private String baseurl;
	private String username;
	private String password;
	
	public SyncEntitiesTask(String url, String username, String password, ProgressDialog dialog,
			Context context, CollectEntitiesListener listener) {
		this.baseurl = url;
		this.username = username;
		this.password = password;
		this.dialog = dialog;
		this.listener = listener;
		this.activity = (SyncDatabaseActivity) listener;
		databaseAdapter = new DatabaseAdapter(context);
	}

	@Override
	protected Boolean doInBackground(Void... params) {
		
		 creds = new UsernamePasswordCredentials(username, password);
		 
		 HttpParams httpParameters = new BasicHttpParams();
		 HttpConnectionParams.setConnectionTimeout(httpParameters, 100000);
		 HttpConnectionParams.setSoTimeout(httpParameters, 100000);
		 client = new DefaultHttpClient(httpParameters);
		 
		 try {	
			 
			PowerManager pm = (PowerManager) activity.getSystemService(Context.POWER_SERVICE);
			WakeLock wl = pm.newWakeLock(PowerManager.FULL_WAKE_LOCK, "DoNotDimScreen");
			wl.acquire();
			 
			setupDB();
						 
			processUrl(baseurl + "/individual");	
			resetDialogParams();
			 
			processUrl(baseurl + "/locationhierarchy");
			resetDialogParams();

			processUrl(baseurl + "/location");
			resetDialogParams();
			
			processUrl(baseurl + "/round");
			resetDialogParams();
			
			processUrl(baseurl + "/visit");
			resetDialogParams();
			
			wl.release();
		 } 
		 catch (Exception e) {
			e.printStackTrace();
			return false;
		 }
		 return true;
	}
	
	private void processUrl(String url) throws Exception {
		httpGet = new HttpGet(url);
		processResponse();
	}
		
    protected void onProgressUpdate(Integer... progress) {
    	dialog.incrementProgressBy(progress[0]);
    	if (dialog.getProgress() > dialog.getMax()) {
    		dialog.dismiss();
    		dialog.setProgress(0);
    		dialog.setMax(0);
    	}
    }
	
	private void processResponse() throws Exception {
		InputStream inputStream = getResponse();
        if (inputStream != null)
        	processXMLDocument(inputStream);
	}
	
    private InputStream getResponse() throws AuthenticationException, ClientProtocolException, IOException {
        HttpResponse response = null;
        
        httpGet.addHeader(new BasicScheme().authenticate(creds, httpGet));
        httpGet.addHeader("content-type", "application/xml");
        response = client.execute(httpGet);
       
        HttpEntity entity = response.getEntity();  
        return entity.getContent();
    }
		
	private void setupDB() {	
		databaseAdapter.open();
		databaseAdapter.getDatabase().delete("individual", null, null);
		databaseAdapter.getDatabase().delete("location", null, null);
		databaseAdapter.getDatabase().delete("hierarchy", null, null);
		databaseAdapter.getDatabase().delete("round", null, null);
		databaseAdapter.getDatabase().delete("visit", null, null);
		databaseAdapter.close();
	}
	
	/**
	 * The response from the OpenHDS web service is an xml document
	 * so it must be parsed.
	 */
	private void processXMLDocument(InputStream content) throws Exception  {
		
	    XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
        factory.setNamespaceAware(true);
       
        XmlPullParser parser = factory.newPullParser();
        parser.setInput(new InputStreamReader(content));
                      
        int eventType = parser.getEventType();
        while (eventType != XmlPullParser.END_DOCUMENT && !isCancelled()) {
            String name = null;
           
            switch(eventType) {            		
                case XmlPullParser.START_TAG:
                    name = parser.getName();
                              
                    if (name.equalsIgnoreCase("count")) {
                    	parser.next();
                    	int count = Integer.parseInt(parser.getText());
                    	dialog.setMax(count);
                    	parser.nextTag();
                    }
                    else if (name.equalsIgnoreCase("individual")) {
                    	processIndividualParams(parser);
                    	activity.runOnUiThread(changeMessageIndividual);
                    }
                    else if (name.equalsIgnoreCase("location")) {
                    	processLocationParams(parser);
                    	activity.runOnUiThread(changeMessageLocation);
                    }
                    else if (name.equalsIgnoreCase("hierarchy")) {
                    	processHierarchyParams(parser);
                    	activity.runOnUiThread(changeMessageHierarchy);
                    }
                    else if (name.equalsIgnoreCase("round")) {
                    	processRoundParams(parser);
                    	activity.runOnUiThread(changeMessageRound);
                    }
                    else if (name.equalsIgnoreCase("visit")) {
                    	processVisitParams(parser);
                    	activity.runOnUiThread(changeMessageVisit);
                    }
                    break;
            }
            eventType = parser.next();
        }
	}
	
	private Runnable changeMessageIndividual = new Runnable() {
	    public void run() {
	        dialog.setMessage("Downloading Individuals");
	    }
	};
	
	private Runnable changeMessageLocation = new Runnable() {
	    public void run() {
	        dialog.setMessage("Downloading Locations");
	    }
	};
	
	private Runnable changeMessageHierarchy = new Runnable() {
	    public void run() {
	        dialog.setMessage("Downloading Hierarchy");
	    }
	};
	
	private Runnable changeMessageRound = new Runnable() {
	    public void run() {
	        dialog.setMessage("Downloading Rounds");
	    }
	};
	
	private Runnable changeMessageVisit = new Runnable() {
	    public void run() {
	        dialog.setMessage("Downloading Visits");
	    }
	};
	
	private void processHierarchyParams(XmlPullParser parser) throws XmlPullParserException, IOException {
		String name = "";
		Map<String, String> paramMap = new HashMap<String, String>();
        parser.nextTag();
        name = parser.getName();
        paramMap.put("extId", parser.nextText());
        parser.nextTag();
        name = parser.getName();
        paramMap.put("level", parser.nextText());
        parser.nextTag();
        name = parser.getName();
        paramMap.put("name", parser.nextText());
        parser.nextTag();
        name = parser.getName();
        paramMap.put("parent", parser.nextText());
        parser.nextTag();
        name = parser.getName();
        paramMap.put("uuid", parser.nextText());
        parser.nextTag();
        
        saveHierarchyToDB(paramMap.get("uuid"), paramMap.get("extId"), paramMap.get("name"), 
        		paramMap.get("parent"), paramMap.get("level"));
        
        dialog.incrementProgressBy(1);
	}

	private void processLocationParams(XmlPullParser parser) throws XmlPullParserException, IOException {
		String name = "";
		Map<String, String> paramMap = new HashMap<String, String>();
        parser.nextTag();
        name = parser.getName();
        paramMap.put("extId", parser.nextText());
        parser.nextTag();
        name = parser.getName();
        paramMap.put("hierarchy", parser.nextText());
        parser.nextTag();
        name = parser.getName();
        paramMap.put("latitude", parser.nextText());
        parser.nextTag();
        name = parser.getName();
        paramMap.put("longitude", parser.nextText());
        parser.nextTag();
        name = parser.getName();
        paramMap.put("name", parser.nextText());
        parser.nextTag();
        name = parser.getName();
        paramMap.put("uuid", parser.nextText());
        parser.nextTag();
        
        saveLocationToDB(paramMap.get("uuid"), paramMap.get("extId"), paramMap.get("name"), 
        		paramMap.get("latitude"), paramMap.get("longitude"), paramMap.get("hierarchy"));
        
        dialog.incrementProgressBy(1);
	}
	
	private void processIndividualParams(XmlPullParser parser) throws XmlPullParserException, IOException {
		String name = "";
		Map<String, String> paramMap = new HashMap<String, String>();
		parser.nextTag();
		name = parser.getName();
		paramMap.put("currentResidence", parser.nextText());
        parser.nextTag();
        name = parser.getName();
        paramMap.put("dob", parser.nextText());
        parser.nextTag();
        name = parser.getName();
        paramMap.put("extId", parser.nextText());
        parser.nextTag();
        name = parser.getName();
        paramMap.put("father", parser.nextText());
        parser.nextTag();
        name = parser.getName();
        paramMap.put("firstName", parser.nextText());
        parser.nextTag();
        name = parser.getName();
        paramMap.put("gender", parser.nextText());
        parser.nextTag();
        name = parser.getName();
        paramMap.put("lastName", parser.nextText());
        parser.nextTag();
        name = parser.getName();
        paramMap.put("mother", parser.nextText());
        parser.nextTag();
        name = parser.getName();
        paramMap.put("uuid", parser.nextText());
        parser.nextTag();
        
        saveIndividualToDB(paramMap.get("uuid"), paramMap.get("extId"), paramMap.get("firstName"), 
        		paramMap.get("lastName"), paramMap.get("gender"), paramMap.get("dob"), paramMap.get("mother"),
        		paramMap.get("father"), paramMap.get("currentResidence"));
        
        dialog.incrementProgressBy(1);
	}
	
	private void processRoundParams(XmlPullParser parser) throws XmlPullParserException, IOException {
		String name = "";
		Map<String, String> paramMap = new HashMap<String, String>();
        parser.nextTag();
        name = parser.getName();
        paramMap.put("endDate", parser.nextText());
        parser.nextTag();
        name = parser.getName();
        paramMap.put("remarks", parser.nextText());
        parser.nextTag();
        name = parser.getName();
        paramMap.put("roundNumber", parser.nextText());
        parser.nextTag();
        name = parser.getName();
        paramMap.put("startDate", parser.nextText());
        parser.nextTag();
        name = parser.getName();
        paramMap.put("uuid", parser.nextText());
        parser.nextTag();
          
        saveRoundToDB(paramMap.get("uuid"), paramMap.get("startDate"), paramMap.get("endDate"), 
        		paramMap.get("roundNumber"), paramMap.get("remarks"));
        
        dialog.incrementProgressBy(1);
	}
	
	private void processVisitParams(XmlPullParser parser) throws XmlPullParserException, IOException {
		String name = "";
		Map<String, String> paramMap = new HashMap<String, String>();
        parser.nextTag();
        name = parser.getName();
        paramMap.put("extId", parser.nextText());
        parser.nextTag();
        name = parser.getName();
        paramMap.put("roundNumber", parser.nextText());
        parser.nextTag();
        name = parser.getName();
        paramMap.put("uuid", parser.nextText());
        parser.nextTag();
        name = parser.getName();
        paramMap.put("visitDate", parser.nextText());
        parser.nextTag();
        name = parser.getName();
        paramMap.put("visitLocation", parser.nextText());
        parser.nextTag();
          
        saveVisitToDB(paramMap.get("uuid"), paramMap.get("extId"), paramMap.get("roundNumber"), 
        		paramMap.get("visitDate"), paramMap.get("visitLocation"));
        
        dialog.incrementProgressBy(1);
	}
	
	private void resetDialogParams() {
		dialog.setProgress(0);
		dialog.setMax(0);
	}
	
	protected void onPostExecute(final Boolean result) {
		dialog.setProgress(0);
	    listener.collectionComplete(result);
	}
	
	public void saveIndividualToDB(String uuid, String extId, String firstName, String lastName, String gender, String dob, String mother, String father, String residence) {
	    databaseAdapter.open();
	    databaseAdapter.createIndividual(uuid, extId, firstName, lastName, gender, dob, mother, father, residence);
	    databaseAdapter.close();
	}
	
	public void saveLocationToDB(String uuid, String extId, String name, String latitude, String longitude, String hierarchy) {
	    databaseAdapter.open();
	    databaseAdapter.createLocation(uuid, extId, name, latitude, longitude, hierarchy);
	    databaseAdapter.close();
	}
	
	public void saveHierarchyToDB(String uuid, String extId, String name, String parent, String level) {
	    databaseAdapter.open();
	    databaseAdapter.createHierarchy(uuid, extId, name, parent, level);
	    databaseAdapter.close();
	}
	
	public void saveRoundToDB(String uuid, String startDate, String endDate, String roundNumber, String remarks) {
	    databaseAdapter.open();
	    databaseAdapter.createRound(uuid, startDate, endDate, roundNumber, remarks);
	    databaseAdapter.close();
	}
	
	public void saveVisitToDB(String uuid, String extId, String roundNumber, String date, String location) {
	    databaseAdapter.open();
	    databaseAdapter.createVisit(uuid, extId, roundNumber, date, location);
	    databaseAdapter.close();
	}
}
