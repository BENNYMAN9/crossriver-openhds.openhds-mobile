package org.openhds.mobile.task;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

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
import org.openhds.mobile.OpenHDS;
import org.openhds.mobile.activity.SyncDatabaseActivity;
import org.openhds.mobile.listener.CollectEntitiesListener;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.os.AsyncTask;

/**
 * AsyncTask responsible for downloading the OpenHDS "database", that is a subset of the OpenHDS database records. It
 * does the downloading incrementally, by downloading parts of the data one at a time. For example, it gets all
 * locations and then retrieves all individuals. Ordering is somewhat important here, because the database has a few
 * foreign key references that must be satisfied (e.g. individual references a location location)
 */
public class SyncEntitiesTask extends AsyncTask<Void, String, Boolean> {

    private CollectEntitiesListener listener;
    private SyncDatabaseActivity activity;
    private ContentResolver resolver;

    private UsernamePasswordCredentials creds;
    private ProgressDialog dialog;
    private HttpGet httpGet;
    private HttpClient client;

    private String baseurl;
    private String username;
    private String password;

    private final List<ContentValues> values = new ArrayList<ContentValues>();
    private final ContentValues[] emptyArray = new ContentValues[] {};

    public SyncEntitiesTask(String url, String username, String password, ProgressDialog dialog, Context context,
            CollectEntitiesListener listener) {
        this.baseurl = url;
        this.username = username;
        this.password = password;
        this.dialog = dialog;
        this.listener = listener;
        this.activity = (SyncDatabaseActivity) listener;
        this.resolver = context.getContentResolver();
    }

    @Override
    protected Boolean doInBackground(Void... params) {
        creds = new UsernamePasswordCredentials(username, password);

        HttpParams httpParameters = new BasicHttpParams();
        HttpConnectionParams.setConnectionTimeout(httpParameters, 100000);
        HttpConnectionParams.setSoTimeout(httpParameters, 100000);
        HttpConnectionParams.setSocketBufferSize(httpParameters, 240);
        client = new DefaultHttpClient(httpParameters);

        // at this point, we don't care to be smart about which data to download, we simply download it all
        deleteAllTables();

        try {
            processUrl(baseurl + "/locationhierarchy");
            resetDialogParams();

            processUrl(baseurl + "/location");
            resetDialogParams();

            processUrl(baseurl + "/round");
            resetDialogParams();

            processUrl(baseurl + "/visit");
            resetDialogParams();

            processUrl(baseurl + "/relationship");
            resetDialogParams();

            processUrl(baseurl + "/individual");
            resetDialogParams();

            processUrl(baseurl + "/socialgroup");
            resetDialogParams();
        } catch (Exception e) {
            return false;
        }

        return true;
    }

    private void deleteAllTables() {
        // ordering is somewhat important during delete. a few tables have foreign keys
        resolver.delete(OpenHDS.IndividualGroups.CONTENT_ID_URI_BASE, null, null);
        resolver.delete(OpenHDS.Rounds.CONTENT_ID_URI_BASE, null, null);
        resolver.delete(OpenHDS.Visits.CONTENT_ID_URI_BASE, null, null);
        resolver.delete(OpenHDS.Relationships.CONTENT_ID_URI_BASE, null, null);
        resolver.delete(OpenHDS.SocialGroups.CONTENT_ID_URI_BASE, null, null);
        resolver.delete(OpenHDS.HierarchyItems.CONTENT_ID_URI_BASE, null, null);
        resolver.delete(OpenHDS.Individuals.CONTENT_ID_URI_BASE, null, null);
        resolver.delete(OpenHDS.Locations.CONTENT_ID_URI_BASE, null, null);
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

    private void processXMLDocument(InputStream content) throws Exception {

        XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
        factory.setNamespaceAware(true);

        XmlPullParser parser = factory.newPullParser();
        parser.setInput(new InputStreamReader(content));

        int eventType = parser.getEventType();
        while (eventType != XmlPullParser.END_DOCUMENT && !isCancelled()) {
            String name = null;

            switch (eventType) {
            case XmlPullParser.START_TAG:
                name = parser.getName();
                if (name.equalsIgnoreCase("count")) {
                    parser.next();
                    int count = Integer.parseInt(parser.getText());
                    dialog.setMax(count);
                    parser.nextTag();
                } else if (name.equalsIgnoreCase("individual")) {
                    processIndividualParams(parser);
                    activity.runOnUiThread(changeMessageIndividual);
                } else if (name.equalsIgnoreCase("location")) {
                    processLocationParams(parser);
                    activity.runOnUiThread(changeMessageLocation);
                } else if (name.equalsIgnoreCase("hierarchy")) {
                    processHierarchyParams(parser);
                    activity.runOnUiThread(changeMessageHierarchy);
                } else if (name.equalsIgnoreCase("round")) {
                    processRoundParams(parser);
                    activity.runOnUiThread(changeMessageRound);
                } else if (name.equalsIgnoreCase("visit")) {
                    processVisitParams(parser);
                    activity.runOnUiThread(changeMessageVisit);
                } else if (name.equalsIgnoreCase("socialgroup")) {
                    processSocialGroupParams(parser);
                    activity.runOnUiThread(changeMessageSocialGroup);
                } else if (name.equalsIgnoreCase("relationship")) {
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
        values.clear();
        while (notEndOfXmlDoc("hierarchys", parser)) {
            ContentValues cv = new ContentValues();

            parser.nextTag();
            cv.put(OpenHDS.HierarchyItems.COLUMN_HIERARCHY_EXTID, parser.nextText());

            parser.nextTag();
            cv.put(OpenHDS.HierarchyItems.COLUMN_HIERARCHY_LEVEL, parser.nextText());

            parser.nextTag();
            cv.put(OpenHDS.HierarchyItems.COLUMN_HIERARCHY_NAME, parser.nextText());

            parser.nextTag();
            cv.put(OpenHDS.HierarchyItems.COLUMN_HIERARCHY_PARENT, parser.nextText());

            parser.nextTag();
            cv.put(OpenHDS.HierarchyItems.COLUMN_HIERARCHY_UUID, parser.nextText());

            values.add(cv);

            parser.nextTag(); // </hierarchy>
            parser.nextTag(); // <hierarchy> or </hiearchys>
        }

        resolver.bulkInsert(OpenHDS.HierarchyItems.CONTENT_URI, values.toArray(emptyArray));
    }

    private boolean notEndOfXmlDoc(String element, XmlPullParser parser) throws XmlPullParserException {
        return !element.equals(parser.getName()) && parser.getEventType() != XmlPullParser.END_TAG && !isCancelled();
    }

    private void processLocationParams(XmlPullParser parser) throws XmlPullParserException, IOException {
        values.clear();
        while (notEndOfXmlDoc("locations", parser)) {
            ContentValues cv = new ContentValues();
            parser.nextTag();
            cv.put(OpenHDS.Locations.COLUMN_LOCATION_EXTID, parser.nextText());

            parser.nextTag();
            cv.put(OpenHDS.Locations.COLUMN_LOCATION_HEAD, parser.nextText());

            parser.nextTag();
            cv.put(OpenHDS.Locations.COLUMN_LOCATION_HIERARCHY, parser.nextText());

            parser.nextTag();
            cv.put(OpenHDS.Locations.COLUMN_LOCATION_LATITUDE, parser.nextText());

            parser.nextTag();
            cv.put(OpenHDS.Locations.COLUMN_LOCATION_LONGITUDE, parser.nextText());

            parser.nextTag();
            cv.put(OpenHDS.Locations.COLUMN_LOCATION_NAME, parser.nextText());

            parser.nextTag();
            cv.put(OpenHDS.Locations.COLUMN_LOCATION_STATUS, parser.nextText());

            parser.nextTag();
            cv.put(OpenHDS.Locations.COLUMN_LOCATION_UUID, parser.nextText());

            values.add(cv);

            parser.nextTag(); // </location>
            parser.nextTag(); // <location> or </locations>
        }

        resolver.bulkInsert(OpenHDS.Locations.CONTENT_ID_URI_BASE, values.toArray(emptyArray));
    }

    private void processIndividualParams(XmlPullParser parser) throws XmlPullParserException, IOException {
        values.clear();
        List<ContentValues> individualSocialGroups = new ArrayList<ContentValues>();
        while (notEndOfXmlDoc("individuals", parser)) {
            ContentValues cv = new ContentValues();

            parser.nextTag();
            cv.put(OpenHDS.Individuals.COLUMN_INDIVIDUAL_RESIDENCE, parser.nextText());

            parser.nextTag();
            cv.put(OpenHDS.Individuals.COLUMN_INDIVIDUAL_DOB, parser.nextText());

            parser.nextTag();
            cv.put(OpenHDS.Individuals.COLUMN_INDIVIDUAL_EXTID, parser.nextText());

            parser.nextTag();
            cv.put(OpenHDS.Individuals.COLUMN_INDIVIDUAL_FATHER, parser.nextText());

            parser.nextTag();
            cv.put(OpenHDS.Individuals.COLUMN_INDIVIDUAL_FIRSTNAME, parser.nextText());

            parser.nextTag();
            cv.put(OpenHDS.Individuals.COLUMN_INDIVIDUAL_GENDER, parser.nextText());

            parser.nextTag();
            String[] groups = parser.nextText().split(",");

            parser.nextTag();
            cv.put(OpenHDS.Individuals.COLUMN_INDIVIDUAL_LASTNAME, parser.nextText());

            parser.nextTag();
            cv.put(OpenHDS.Individuals.COLUMN_INDIVIDUAL_MOTHER, parser.nextText());

            parser.nextTag();
            cv.put(OpenHDS.Individuals.COLUMN_INDIVIDUAL_STATUS, parser.nextText());

            parser.nextTag();
            cv.put(OpenHDS.Individuals.COLUMN_INDIVIDUAL_UUID, parser.nextText());

            values.add(cv);

            for (String item : groups) {
                ContentValues socialGroups = new ContentValues();
                socialGroups.put(OpenHDS.IndividualGroups.COLUMN_INDIVIDUALUUID, cv.getAsString(OpenHDS.Individuals.COLUMN_INDIVIDUAL_EXTID));
                socialGroups.put(OpenHDS.IndividualGroups.COLUMN_SOCIALGROUPUUID, item);
                individualSocialGroups.add(socialGroups);
            }

            parser.nextTag(); // </individual>
            parser.nextTag(); // </individuals> or <individual>
        }

        resolver.bulkInsert(OpenHDS.Individuals.CONTENT_ID_URI_BASE, values.toArray(emptyArray));
        resolver.bulkInsert(OpenHDS.IndividualGroups.CONTENT_ID_URI_BASE, individualSocialGroups.toArray(emptyArray));
    }

    private void processRoundParams(XmlPullParser parser) throws XmlPullParserException, IOException {
        values.clear();
        while (notEndOfXmlDoc("rounds", parser)) {
            ContentValues cv = new ContentValues();

            parser.nextTag();
            cv.put(OpenHDS.Rounds.COLUMN_ROUND_ENDDATE, parser.nextText());

            parser.nextTag();
            cv.put(OpenHDS.Rounds.COLUMN_ROUND_REMARKS, parser.nextText());

            parser.nextTag();
            cv.put(OpenHDS.Rounds.COLUMN_ROUND_NUMBER, parser.nextText());

            parser.nextTag();
            cv.put(OpenHDS.Rounds.COLUMN_ROUND_STARTDATE, parser.nextText());

            parser.nextTag();
            cv.put(OpenHDS.Rounds.COLUMN_ROUND_UUID, parser.nextText());

            values.add(cv);

            parser.nextTag(); // </round>
            parser.nextTag(); // </rounds> or <round>
        }

        resolver.bulkInsert(OpenHDS.Rounds.CONTENT_ID_URI_BASE, values.toArray(emptyArray));
    }

    private void processVisitParams(XmlPullParser parser) throws XmlPullParserException, IOException {
        values.clear();
        while (notEndOfXmlDoc("visits", parser)) {
            ContentValues cv = new ContentValues();

            parser.nextTag();
            cv.put(OpenHDS.Visits.COLUMN_VISIT_EXTID, parser.nextText());

            parser.nextTag();
            cv.put(OpenHDS.Visits.COLUMN_VISIT_ROUND, parser.nextText());

            parser.nextTag();
            cv.put(OpenHDS.Visits.COLUMN_VISIT_STATUS, parser.nextText());

            parser.nextTag();
            cv.put(OpenHDS.Visits.COLUMN_VISIT_UUID, parser.nextText());

            parser.nextTag();
            cv.put(OpenHDS.Visits.COLUMN_VISIT_DATE, parser.nextText());

            parser.nextTag();
            cv.put(OpenHDS.Visits.COLUMN_VISIT_LOCATION, parser.nextText());

            values.add(cv);

            parser.nextTag(); // </visit>
            parser.nextTag(); // </visits> or <visit>
        }

        resolver.bulkInsert(OpenHDS.Visits.CONTENT_ID_URI_BASE, values.toArray(emptyArray));
    }

    private void processSocialGroupParams(XmlPullParser parser) throws XmlPullParserException, IOException {
        values.clear();
        while (notEndOfXmlDoc("socialGroups", parser)) {
            ContentValues cv = new ContentValues();

            parser.nextTag();
            cv.put(OpenHDS.SocialGroups.COLUMN_SOCIALGROUP_EXTID, parser.nextText());

            parser.nextTag();
            cv.put(OpenHDS.SocialGroups.COLUMN_SOCIALGROUP_GROUPHEAD, parser.nextText());

            parser.nextTag();
            cv.put(OpenHDS.SocialGroups.COLUMN_SOCIALGROUP_GROUPNAME, parser.nextText());

            parser.nextTag();
            cv.put(OpenHDS.SocialGroups.COLUMN_SOCIALGROUP_STATUS, parser.nextText());

            parser.nextTag();
            cv.put(OpenHDS.SocialGroups.COLUMN_SOCIALGROUP_UUID, parser.nextText());

            values.add(cv);

            parser.nextTag(); // </socialGroup>
            parser.nextTag(); // </socialGroups> or <socialGroup>
        }

        resolver.bulkInsert(OpenHDS.SocialGroups.CONTENT_ID_URI_BASE, values.toArray(emptyArray));
    }

    private void processRelationshipParams(XmlPullParser parser) throws XmlPullParserException, IOException {
        values.clear();
        while (notEndOfXmlDoc("relationships", parser)) {
            ContentValues cv = new ContentValues();
            parser.nextTag();
            cv.put(OpenHDS.Relationships.COLUMN_RELATIONSHIP_FEMALEINDIVIDUAL, parser.nextText());

            parser.nextTag();
            cv.put(OpenHDS.Relationships.COLUMN_RELATIONSHIP_MALEINDIVIDUAL, parser.nextText());

            parser.nextTag();
            cv.put(OpenHDS.Relationships.COLUMN_RELATIONSHIP_STARTDATE, parser.nextText());

            values.add(cv);

            parser.nextTag(); // </relationship>
            parser.nextTag(); // </relationships> or <relationship>
        }

        resolver.bulkInsert(OpenHDS.Relationships.CONTENT_ID_URI_BASE, values.toArray(emptyArray));
    }

    private void resetDialogParams() {
        dialog.setProgress(0);
        dialog.setMax(0);
    }

    protected void onPostExecute(final Boolean result) {
        dialog.setProgress(0);
        listener.collectionComplete(result);
    }
}
