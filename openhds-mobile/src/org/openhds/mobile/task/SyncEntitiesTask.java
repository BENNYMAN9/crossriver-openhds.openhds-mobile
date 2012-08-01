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
		 HttpConnectionParams.setSocketBufferSize(httpParameters, 240);
		 client = new DefaultHttpClient(httpParameters);
		 
		 try {		 
			databaseAdapter.open();
			databaseAdapter.getDatabase().beginTransaction();
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
			
			processUrl(baseurl + "/socialgroup");
			resetDialogParams();
			
			processUrl(baseurl + "/relationship");
			resetDialogParams();
		 } 
		 catch (Exception e) {
			databaseAdapter.getDatabase().endTransaction();
			databaseAdapter.close();
			return false;
		 }
		 finally {
			 databaseAdapter.getDatabase().endTransaction();
			 databaseAdapter.close();
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
		databaseAdapter.getDatabase().delete("individual", null, null);
		databaseAdapter.getDatabase().delete("location", null, null);
		databaseAdapter.getDatabase().delete("hierarchy", null, null);
		databaseAdapter.getDatabase().delete("round", null, null);
		databaseAdapter.getDatabase().delete("visit", null, null);
		databaseAdapter.getDatabase().delete("relationship", null, null);
		databaseAdapter.getDatabase().delete("socialgroup", null, null);
		databaseAdapter.getDatabase().delete("individual_socialgroup", null, null);
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
                    else if (name.equalsIgnoreCase("socialgroup")) {
                    	processSocialGroupParams(parser);
                    	activity.runOnUiThread(changeMessageSocialGroup);
                    }
                    else if (name.equalsIgnoreCase("relationship")) {
                    	processRelationshipParams(parser);
                    	activity.runOnUiThread(changeMessageRelationship);
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
	
	private Runnable changeMessageSocialGroup = new Runnable() {
	    public void run() {
	        dialog.setMessage("Downloading Social Groups");
	    }
	};
	
	private Runnable changeMessageRelationship = new Runnable() {
	    public void run() {
	        dialog.setMessage("Downloading Relationships");
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
        paramMap.put("status", parser.nextText());
        parser.nextTag();
        name = parser.getName();
        paramMap.put("uuid", parser.nextText());
        parser.nextTag();
        
        saveLocationToDB(paramMap.get("uuid"), paramMap.get("extId"), paramMap.get("name"), 
        		paramMap.get("latitude"), paramMap.get("longitude"), paramMap.get("hierarchy"), paramMap.get("status"));
        
        dialog.incrementProgressBy(1);
	}
	
	private void processIndividualParams(XmlPullParser parser) throws XmlPullParserException, IOException {
		String[] groups;
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
        groups = parser.nextText().split(",");
        parser.nextTag();
        name = parser.getName();
        paramMap.put("lastName", parser.nextText());
        parser.nextTag();
        name = parser.getName();
        paramMap.put("mother", parser.nextText());
        parser.nextTag();
        name = parser.getName();
        paramMap.put("status", parser.nextText());       
        parser.nextTag();
        name = parser.getName();
        paramMap.put("uuid", parser.nextText());
        parser.nextTag();
        
        for (String item : groups) {
        	databaseAdapter.createIndividualSocialGroupLink(paramMap.get("extId"), item);
        }
        
        saveIndividualToDB(paramMap.get("uuid"), paramMap.get("extId"), paramMap.get("firstName"), 
        		paramMap.get("lastName"), paramMap.get("gender"), paramMap.get("dob"), paramMap.get("mother"),
        		paramMap.get("father"), paramMap.get("currentResidence"), paramMap.get("status"));
        
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
        paramMap.put("status", parser.nextText());
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
        		paramMap.get("visitDate"), paramMap.get("visitLocation"), paramMap.get("status"));
        
        dialog.incrementProgressBy(1);
	}
	
	private void processSocialGroupParams(XmlPullParser parser) throws XmlPullParserException, IOException {
		String name = "";
		Map<String, String> paramMap = new HashMap<String, String>();
        parser.nextTag();
        name = parser.getName();
        paramMap.put("extId", parser.nextText());
        parser.nextTag();
        name = parser.getName();
        paramMap.put("groupHead", parser.nextText());
        parser.nextTag();
        name = parser.getName();
        paramMap.put("groupName", parser.nextText());
        parser.nextTag();
        name = parser.getName();
        paramMap.put("status", parser.nextText());
        parser.nextTag();
        name = parser.getName();
        paramMap.put("uuid", parser.nextText());
        parser.nextTag();
        
        saveSocialGroupToDB(paramMap.get("uuid"), paramMap.get("extId"), paramMap.get("groupHead"), 
        		paramMap.get("groupName"), paramMap.get("status"));
        
        dialog.incrementProgressBy(1);
	}
	
	private void processRelationshipParams(XmlPullParser parser) throws XmlPullParserException, IOException {
		String name = "";
		Map<String, String> paramMap = new HashMap<String, String>();
        parser.nextTag();
        name = parser.getName();
        paramMap.put("femaleIndividual", parser.nextText());
        parser.nextTag();
        name = parser.getName();
        paramMap.put("maleIndividual", parser.nextText());
        parser.nextTag();
        name = parser.getName();
        paramMap.put("startDate", parser.nextText());
        parser.nextTag();
                
        saveRelationshipToDB(paramMap.get("femaleIndividual"), paramMap.get("maleIndividual"), paramMap.get("startDate"));
        dialog.incrementProgressBy(1);
	}
	
	private void resetDialogParams() {
		dialog.setProgress(0);
		dialog.setMax(0);
	}
	
	protected void onPostExecute(final Boolean result) {
		if (databaseAdapter.getDatabase().isOpen())
			databaseAdapter.close();
		dialog.setProgress(0);
	    listener.collectionComplete(result);
	}
	
	public void saveIndividualToDB(String uuid, String extId, String firstName, String lastName, String gender, String dob, 
			String mother, String father, String residence, String status) {
	    databaseAdapter.createIndividual(uuid, extId, firstName, lastName, gender, dob, mother, father, residence, status);
	}
	
	public void saveLocationToDB(String uuid, String extId, String name, String latitude, String longitude, 
			String hierarchy, String status) {
	    databaseAdapter.createLocation(uuid, extId, name, latitude, longitude, hierarchy, status);
	}
	
	public void saveHierarchyToDB(String uuid, String extId, String name, String parent, String level) {
	    databaseAdapter.createHierarchy(uuid, extId, name, parent, level);
	}
	
	public void saveRoundToDB(String uuid, String startDate, String endDate, String roundNumber, String remarks) {
	    databaseAdapter.createRound(uuid, startDate, endDate, roundNumber, remarks);
	}
	
	public void saveVisitToDB(String uuid, String extId, String roundNumber, String date, String location, String status) {
	    databaseAdapter.createVisit(uuid, extId, roundNumber, date, location, status);
	}
	
	public void saveSocialGroupToDB(String uuid, String extId, String groupHead, String groupName, String status) {
	    databaseAdapter.createSocialGroup(uuid, extId, groupName, groupHead, status);
	}
	
	public void saveRelationshipToDB(String femaleIndividual, String maleIndividual, String startDate) {
	    databaseAdapter.createRelationship(maleIndividual, femaleIndividual, startDate);
	}
}
