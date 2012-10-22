package org.openhds.mobile.database;

import org.openhds.mobile.OpenHDS;
import org.openhds.mobile.model.Location;

import android.content.ContentResolver;
import android.content.ContentValues;

public class LocationUpdate implements Updatable {

    private String locationExtId;
    private String headId;
    private String hierarchy;
    private String name;
    private String uuid;

    public LocationUpdate(Location location) {
        this.locationExtId = location.getExtId();
        this.headId = location.getHead();
        this.hierarchy = location.getHierarchy();
        this.name = location.getName();
        this.uuid = location.getUuid();
    }

    public void updateDatabase(ContentResolver resolver) {
        ContentValues cv = new ContentValues();
        
        cv.put(OpenHDS.Locations.COLUMN_LOCATION_EXTID, locationExtId);
        cv.put(OpenHDS.Locations.COLUMN_LOCATION_HEAD, headId);
        cv.put(OpenHDS.Locations.COLUMN_LOCATION_HIERARCHY, hierarchy);
        cv.put(OpenHDS.Locations.COLUMN_LOCATION_NAME, name);
        cv.put(OpenHDS.Locations.COLUMN_LOCATION_UUID, uuid);
        
        cv.put(OpenHDS.Locations.COLUMN_LOCATION_LATITUDE, "");
        cv.put(OpenHDS.Locations.COLUMN_LOCATION_LONGITUDE, "");
        cv.put(OpenHDS.Locations.COLUMN_LOCATION_STATUS, "");

        resolver.insert(OpenHDS.Locations.CONTENT_ID_URI_BASE, cv);
    }

}
