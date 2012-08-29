package org.openhds.mobile.task;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.openhds.mobile.FormsProviderAPI;
import org.openhds.mobile.InstanceProviderAPI;
import org.openhds.mobile.fragment.SelectionFragment;
import org.openhds.mobile.listener.OdkFormLoadListener;
import org.openhds.mobile.model.FilledParams;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;

public class OdkGeneratedFormLoadTask extends AsyncTask<Void, Void, Boolean>  {
	
	private OdkFormLoadListener listener;
	private ContentResolver resolver;
	private Uri odkUri;
	private SelectionFragment sf;
	private String event;
		
	public OdkGeneratedFormLoadTask(OdkFormLoadListener listener, ContentResolver resolver, SelectionFragment sf, String event) {
		this.listener = listener;
		this.resolver = resolver;
		this.sf = sf;
		this.event = event;
	}

	@Override
	protected Boolean doInBackground(Void... params) {

		Cursor cursor = getCursorForFormsProvider(event.toLowerCase());
		if (cursor.moveToFirst()) {
			String jrFormId = cursor.getString(0);
			String formFilePath = cursor.getString(1);
			String xml = processXml(jrFormId, formFilePath);
			
			File targetFile = saveFile(xml);
			if (targetFile != null) {
				return writeContent(targetFile, event, jrFormId);
			}
		}
		cursor.close();

		return false;
	}
	
	private Cursor getCursorForFormsProvider(String name) {
		return resolver.query(FormsProviderAPI.FormsColumns.CONTENT_URI, 
				new String[] {FormsProviderAPI.FormsColumns.JR_FORM_ID, FormsProviderAPI.FormsColumns.FORM_FILE_PATH},
				FormsProviderAPI.FormsColumns.JR_FORM_ID + " like ?", new String[] {name + "%"}, null);
	}
	
	private String processXml(String jrFormId, String formFilePath) {
		
		StringBuilder sbuilder = new StringBuilder();
		
		try {
		    DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
	        DocumentBuilder builder = factory.newDocumentBuilder();
	        Document doc = builder.parse(new FileInputStream(formFilePath));
	        
	        Node node = doc.getElementsByTagName("data").item(0);
	        sbuilder.append("<data id=\"" + jrFormId + "\">" + "\r\n");
	        
	        processNodeChildren(node, sbuilder);
	        
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		}
		
        return sbuilder.toString();
	}
	
	private void processNodeChildren(Node node, StringBuilder sbuilder) {
		NodeList childElements = node.getChildNodes();
	        
        List<String> params = FilledParams.getParamsArray();
        for (int i = 0; i < childElements.getLength(); i++) {
        	Node n = childElements.item(i);
        	if (n.getNodeType() == Node.ELEMENT_NODE) {
        		String name = n.getNodeName();
        		
        		if (params.contains(name)) {
        			if (name.equals(FilledParams.visitId)) {
        				sbuilder.append(sf.getVisit().getExtId() == null ?
        					"<visitId />" + "\r\n" : "<visitId>" + sf.getVisit().getExtId() + "</visitId>" + "\r\n");
        			}
        			else if (name.equals(FilledParams.roundNumber)) {
        				sbuilder.append(sf.getRound().getRoundNumber() == null ?
        					"<roundNumber />" + "\r\n" : "<roundNumber>" + sf.getRound().getRoundNumber() + "</roundNumber>" + "\r\n");
        			}
        			else if (name.equals(FilledParams.visitDate)) {
        				sbuilder.append(sf.getVisit().getDate() == null ?
        					"<visitDate />" + "\r\n" : "<visitDate>" + sf.getVisit().getDate() + "</visitDate>" + "\r\n");
        			}
        			else if (name.equals(FilledParams.individualId)) {
        				sbuilder.append(sf.getIndividual().getExtId() == null ?
        					"<individualId />" + "\r\n" : "<individualId>" + sf.getIndividual().getExtId() + "</individualId>" + "\r\n");
        			}
        			else if (name.equals(FilledParams.motherId) && !sf.isExternalInMigration()) {
        				sbuilder.append(sf.getIndividual().getMother() == null ?
        					"<motherId />" + "\r\n" : "<motherId>" + sf.getIndividual().getMother() + "</motherId>" + "\r\n");
        			}
        			else if (name.equals(FilledParams.fatherId) && !sf.isExternalInMigration()) {
        				sbuilder.append(sf.getIndividual().getFather() == null ?
        						"<fatherId />" + "\r\n" : "<fatherId>" + sf.getIndividual().getFather() + "</fatherId>" + "\r\n");
        			}
        			else if (name.equals(FilledParams.firstName)) {
        				sbuilder.append(sf.getIndividual().getFirstName() == null ? 
        					"<firstName />" + "\r\n" : "<firstName>" + sf.getIndividual().getFirstName() + "</firstName>" + "\r\n");
        			}
        			else if (name.equals(FilledParams.lastName)) {
        				sbuilder.append(sf.getIndividual().getLastName() == null ?
        					"<lastName />" + "\r\n" : "<lastName>" + sf.getIndividual().getLastName() + "</lastName>" + "\r\n");
        			}
        			else if (name.equals(FilledParams.gender)) {
        				sbuilder.append(sf.getIndividual().getGender() == null ?
        					"<gender />" + "\r\n" : "<gender>" + (sf.getIndividual().getGender().equalsIgnoreCase("Male") ? "1" : "2") + "</gender>" + "\r\n");
        			}
        			else if (name.equals(FilledParams.dob)) {
        				sbuilder.append(sf.getIndividual().getDob() == null ?
        					"<dob />" + "\r\n" : "<dob>" + sf.getIndividual().getDob() + "</dob>" + "\r\n");
        			}
        			else if (name.equals(FilledParams.houseId)) {
        				sbuilder.append(sf.getLocation().getExtId() == null ?
        					"<houseId />" + "\r\n" : "<houseId>" + sf.getLocation().getExtId() + "</houseId>" + "\r\n");
        			}
        			else if (name.equals(FilledParams.houseName)) {
        				sbuilder.append(sf.getLocation().getName() == null ?
        					"<houseName />" + "\r\n" : "<houseName>" + sf.getLocation().getName() + "</houseName>" + "\r\n");
        			}
        			else if (name.equals(FilledParams.hierarchyId)) {
        				sbuilder.append(sf.getLocation().getHierarchy() == null ?
        					"<hierarchyId />" + "\r\n" : "<hierarchyId>" + sf.getLocation().getHierarchy() + "</hierarchyId>" + "\r\n");
        			}
        			else if (name.equals(FilledParams.latlong)) 
        				sbuilder.append("<latlong />" + "\r\n");
        			else if (name.equals(FilledParams.householdId)) {
        				sbuilder.append(sf.getSocialgroup().getExtId() == null ?
        					"<householdId />" + "\r\n" : "<householdId>" + sf.getSocialgroup().getExtId() + "</householdId>" + "\r\n");
        			}
        			else if (name.equals(FilledParams.householdName)) {
        				sbuilder.append(sf.getSocialgroup().getGroupName() == null ?
        					"<householdName />" + "\r\n" : "<householdName>" + sf.getSocialgroup().getGroupName() + "</householdName>" + "\r\n");
        			}
        			else if (name.equals(FilledParams.fieldWorkerId)) {
        				sbuilder.append(sf.getFieldWorker().getExtId() == null ?
        					"<fieldWorkerId />" + "\r\n" : "<fieldWorkerId>" + sf.getFieldWorker().getExtId() + "</fieldWorkerId>" + "\r\n");	
        			}
        			else if (name.equals(FilledParams.child1Id)) {
        				sbuilder.append(sf.getPregnancyOutcome().getChild1ExtId() == null ?
        					"<child1Id />" + "\r\n" : "<child1Id>" + sf.getPregnancyOutcome().getChild1ExtId() + "</child1Id>" + "\r\n");
        			}
        			else if (name.equals(FilledParams.child2Id)) {
        				sbuilder.append(sf.getPregnancyOutcome().getChild2ExtId() == null ?
        					"<child2Id />" + "\r\n" : "<child2Id>" + sf.getPregnancyOutcome().getChild2ExtId() + "</child2Id>" + "\r\n");
        			}
        			else if (name.equals(FilledParams.childFatherId)) {
        				sbuilder.append(sf.getPregnancyOutcome().getFather().getExtId() == null ?
        					"<childFatherId />" + "\r\n" : "<childFatherId>" + sf.getPregnancyOutcome().getFather().getExtId() + "</childFatherId>" + "\r\n");
        			}
        			else if (name.equals(FilledParams.childFatherFirstName)) {
        				sbuilder.append(sf.getPregnancyOutcome().getFather().getFirstName() == null ?
        					"<childFatherFirstName />" + "\r\n" : "<childFatherFirstName>" + sf.getPregnancyOutcome().getFather().getFirstName() + "</childFatherFirstName>" + "\r\n");
        			}
        			else if (name.equals(FilledParams.childFatherLastName)) {
        				sbuilder.append(sf.getPregnancyOutcome().getFather().getLastName() == null ?
        					"<childFatherLastName />" + "\r\n" : "<childFatherLastName>" + sf.getPregnancyOutcome().getFather().getLastName() + "</childFatherLastName>" + "\r\n");
        			}
        			else if (name.equals(FilledParams.manId)) {
        				sbuilder.append(sf.getRelationship().getMaleIndividual() == null ?
        					"<manId />" + "\r\n" : "<manId>" + sf.getRelationship().getMaleIndividual() + "</manId>" + "\r\n");
        			}
        			else if (name.equals(FilledParams.womanId)) {
        				sbuilder.append(sf.getRelationship().getFemaleIndividual() == null ?
        					"<womanId />" + "\r\n" : "<womanId>" + sf.getRelationship().getFemaleIndividual() + "</womanId>" + "\r\n");
        			}
        				
        		}
        		else {
        			if (!n.hasChildNodes())
        				sbuilder.append("<" + name + " />" + "\r\n");
        			else {
        				sbuilder.append("<" + name + ">" + "\r\n");
            			processNodeChildren(n, sbuilder);
        			}
        		}
        	}
        }
        sbuilder.append("</" + node.getNodeName() + ">" + "\r\n");
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
		if (result) 
			listener.onOdkFormLoadSuccess(odkUri);
		else 
			listener.onOdkFormLoadFailure();
	}
}
