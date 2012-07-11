package org.openhds.mobile.task;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;
import org.openhds.mobile.InstanceProviderAPI;
import org.openhds.mobile.listener.OdkFormLoadListener;
import org.openhds.mobile.model.Record;
import org.openhds.mobile.model.UpdateEvent;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;

public class OdkFormLoadTask extends AsyncTask<Void, Void, Boolean>  {
	
	private OdkFormLoadListener listener;
	private ContentResolver resolver;
	private Uri odkUri;
	private Record record;
	private String event;
	
	public OdkFormLoadTask(OdkFormLoadListener listener, ContentResolver resolver, Record record, String event) {
		this.listener = listener;
		this.resolver = resolver;
		this.record = record;
		this.event = event;
	}

	@Override
	protected Boolean doInBackground(Void... params) {
		
		String xml = "";
		if (event == UpdateEvent.VISIT) {
			
			xml = "<data id=\"visit_registration_v2\">" + "\r\n";
			xml += "<visitId>" + record.getVisit().getExtId() + "</visitId>" + "\r\n";
			xml += "<fieldWorkerId>" + record.getFieldWorkerId() + "</fieldWorkerId>" + "\r\n";
			xml += "<locationId>" + record.getLocation().getExtId() + "</locationId>" + "\r\n";
			xml += "<visitDate>" + record.getVisit().getDate() + "</visitDate>" + "\r\n";
			xml += "<roundNumber>" + record.getRound().getRoundNumber() + "</roundNumber>" + "\r\n";
			xml += "<derivedFromUri />" + "\r\n";
			xml += "<supervisorStatus />" + "\r\n";
			xml += "<processedByMirth />" + "\r\n";
			xml += "<validationFailed>" + "0" + "</validationFailed>" + "\r\n";
			xml += "</data>" + "\r\n";
			
			File targetFile = saveFile(xml);
			if (targetFile != null) {
				return writeContent(targetFile, "visit", "visit_registration_v2");
			}
			
		}
		else if (event == UpdateEvent.DEATH) {
			
			xml = "<data id=\"death_registration_v4\">" + "\r\n";
			xml += "<basicInformation>" + "\r\n";
			xml += "<fieldWorker>" + record.getFieldWorkerId() + "</fieldWorker>" + "\r\n";
			xml += "<dateOfInterview>" + record.getVisit().getDate() + "</dateOfInterview>" + "\r\n";
			xml += "<permanentId>" + record.getIndividual().getExtId() + "</permanentId>"  + "\r\n";
			xml += "<houseId>" + record.getLocation().getExtId() + "</houseId>" + "\r\n";
			xml += "<householdName />" + "\r\n";
			xml += "<householdId />" + "\r\n";
			xml += "<dateOfDeath />" + "\r\n";
			xml += "<deceasedName>" + record.getIndividual().getFirstName() + " " + record.getIndividual().getLastName() + "</deceasedName>" + "\r\n";
			xml += "<sex>" + (record.getIndividual().getGender().equalsIgnoreCase("Male") ? "1" : "2") + "</sex>" + "\r\n";
			xml += "<placeOfDeath />" + "\r\n";
			xml += "<placeOfDeathOther />" + "\r\n";
			xml += "</basicInformation>" + "\r\n";
			xml += "<sourceOfInformation>" + "\r\n";
			xml += "<reportedBy />" + "\r\n";
			xml += "</sourceOfInformation>" + "\r\n";
			xml += "</data>" + "\r\n";
			
			File targetFile = saveFile(xml);
			if (targetFile != null) {
				return writeContent(targetFile, "death", "death_registration_v4");
			}
		}
		return false;
	}
		
	private File saveFile(String xml) {
		File root = Environment.getExternalStorageDirectory();
        String destinationPath = root.getAbsolutePath() + File.separator
                        + "Android" + File.separator + "data" + File.separator
                        + "org.openhds.mobile" + File.separator + "files";

        File baseDir = new File(destinationPath);
        if (!baseDir.exists()) {
            boolean created = baseDir.mkdirs();
            if (!created) {
                return null;
            }
        }

        DateFormat df = new SimpleDateFormat("yyyy-MM-dd_hh:mm:ss");  
        df.setTimeZone(TimeZone.getDefault());  
        String date = df.format(new Date());
        
        destinationPath += File.separator + date + ".xml";
        File targetFile = new File(destinationPath);
        if (!targetFile.exists()) {
	        try {
	            FileWriter writer = new FileWriter(targetFile);
	            writer.write(xml);
	            writer.close();
	        } catch (IOException e) {
	        	return null;
	        }
        }
        return targetFile;
	}
	
	private boolean writeContent(File targetFile, String displayName, String formId) {
		
        ContentValues values = new ContentValues();
        values.put(InstanceProviderAPI.InstanceColumns.INSTANCE_FILE_PATH, targetFile.getAbsolutePath());
        values.put(InstanceProviderAPI.InstanceColumns.DISPLAY_NAME, displayName);
        values.put(InstanceProviderAPI.InstanceColumns.JR_FORM_ID, formId);
        odkUri = resolver.insert(InstanceProviderAPI.InstanceColumns.CONTENT_URI, values);
        if (odkUri == null) {
        	return false;
        }
        return true;
	}
	
	@Override
	protected void onPostExecute(final Boolean result) {
	    listener.onSuccess(odkUri);
	}

}
