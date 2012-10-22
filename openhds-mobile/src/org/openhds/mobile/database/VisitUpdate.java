package org.openhds.mobile.database;

import org.openhds.mobile.OpenHDS;
import org.openhds.mobile.model.LocationVisit;

import android.content.ContentResolver;
import android.content.ContentValues;

public class VisitUpdate implements Updatable {

    private String locationUuid;
    private String visitExtId;

    public VisitUpdate(LocationVisit locationVisit) {
        locationUuid = locationVisit.getLocation().getUuid();
        visitExtId = locationVisit.getVisit().getExtId();
    }

    public void updateDatabase(ContentResolver resolver) {
        ContentValues cv = new ContentValues();
        cv.put(OpenHDS.Visits.COLUMN_VISIT_EXTID, visitExtId);
        cv.put(OpenHDS.Visits.COLUMN_VISIT_LOCATION, locationUuid);
        cv.put(OpenHDS.Visits.COLUMN_VISIT_DATE, "");
        cv.put(OpenHDS.Visits.COLUMN_VISIT_ROUND, "");
        cv.put(OpenHDS.Visits.COLUMN_VISIT_STATUS, "");
        cv.put(OpenHDS.Visits.COLUMN_VISIT_UUID, "");
        
        resolver.insert(OpenHDS.Visits.CONTENT_ID_URI_BASE, cv);
    }

}
