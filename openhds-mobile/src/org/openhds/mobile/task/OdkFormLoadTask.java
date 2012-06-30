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

import android.content.ContentResolver;
import android.content.ContentValues;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;

public class OdkFormLoadTask extends AsyncTask<Void, Void, Boolean>  {
	
	private OdkFormLoadListener listener;
	private ContentResolver resolver;
	private Uri odkUri;
	private Record record;
	
	public OdkFormLoadTask(OdkFormLoadListener listener, ContentResolver resolver, Record record) {
		this.listener = listener;
		this.resolver = resolver;
		this.record = record;
	}

	@Override
	protected Boolean doInBackground(Void... params) {
		
		String xml = "<data id=\"death_registration_v4\">" + "\r\n";
		xml += "<basicInformation>" + "\r\n";
		xml += "<fieldWorker>" + "FWEK1" + "</fieldWorker>" + "\r\n";
		xml += "<dateOfInterview />" + "\r\n";
		xml += "<permanentId />" + "\r\n";
		xml += "<houseId>" + record.getLocation().getExtId() + "</houseId>" + "\r\n";
		xml += "<householdName />" + "\r\n";
		xml += "<householdId />" + "\r\n";
		xml += "<dateOfDeath />" + "\r\n";
		xml += "<deceasedName>" + record.getIndividual().getFirstName() + " " + record.getIndividual().getLastName() + "</deceasedName>" + "\r\n";
		xml += "<sex>" + record.getIndividual().getGender() + "</sex>" + "\r\n";
		xml += "<placeOfDeath />" + "\r\n";
		xml += "<placeOfDeathOther />" + "\r\n";
		xml += "</basicInformation>" + "\r\n";
		xml += "<sourceOfInformation>" + "\r\n";
		xml += "<reportedBy />" + "\r\n";
		xml += "</sourceOfInformation>" + "\r\n";
		xml += "</data>" + "\r\n";

		File root = Environment.getExternalStorageDirectory();
        String destinationPath = root.getAbsolutePath() + File.separator
                        + "Android" + File.separator + "data" + File.separator
                        + "org.openhds.activity" + File.separator + "files";

        File baseDir = new File(destinationPath);
        if (!baseDir.exists()) {
            boolean created = baseDir.mkdirs();
            if (!created) {
                return false;
            }
        }

        DateFormat df = new SimpleDateFormat("yyyy-MM-dd_hh:mm:ss");  
        df.setTimeZone(TimeZone.getTimeZone("EST"));  
        String date = df.format(new Date());
        
        destinationPath += File.separator + date + ".xml";
        File targetFile = new File(destinationPath);
        if (!targetFile.exists()) {
	        try {
	            FileWriter writer = new FileWriter(targetFile);
	            writer.write(xml);
	            writer.close();
	        } catch (IOException e) {
	                return false;
	        }
        }
        
        ContentValues values = new ContentValues();
        values.put(InstanceProviderAPI.InstanceColumns.INSTANCE_FILE_PATH, targetFile.getAbsolutePath());
        values.put(InstanceProviderAPI.InstanceColumns.DISPLAY_NAME, "death");
        values.put(InstanceProviderAPI.InstanceColumns.JR_FORM_ID, "death_registration_v4");
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
