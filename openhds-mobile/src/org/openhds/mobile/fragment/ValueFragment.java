package org.openhds.mobile.fragment;

import java.util.ArrayList;
import java.util.List;

import org.openhds.mobile.Converter;
import org.openhds.mobile.OpenHDS;
import org.openhds.mobile.model.Individual;
import org.openhds.mobile.model.Location;
import org.openhds.mobile.model.LocationHierarchy;
import org.openhds.mobile.model.Round;

import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.text.TextUtils;
import android.view.View;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

/**
 * ValueFragment is responsible for showing a list of entities, and then notifying the activity using this fragment
 * which entity has been selected. An entity can be defined as: Region, Sub Region, Village, Round, Location and
 * Individual
 */
public class ValueFragment extends ListFragment implements LoaderManager.LoaderCallbacks<Cursor> {

    // loader identifiers
    private static final int HIERARCHY_LOADER = 0;
    private static final int REGION_LOADER = 1;
    private static final int ROUND_LOADER = 2;
    private static final int LOCATION_LOADER = 3;
    private static final int INDIVIDUAL_LOADER = 4;
    private static final int INDIVIDUAL_FILTER_LOADER = 5;

    private static final String[] REGION_COLUMNS = new String[] { OpenHDS.HierarchyItems.COLUMN_HIERARCHY_NAME,
            OpenHDS.HierarchyItems.COLUMN_HIERARCHY_EXTID };
    private static final String[] ROUNDS_COLUMNS = new String[] { OpenHDS.Rounds.COLUMN_ROUND_NUMBER,
            OpenHDS.Rounds.COLUMN_ROUND_REMARKS };
    private static final String[] LOCATION_COLUMNS = new String[] { OpenHDS.Locations.COLUMN_LOCATION_NAME,
            OpenHDS.Locations.COLUMN_LOCATION_EXTID };
    private static final String[] INDIVIDUAL_COLUMNS = new String[] { OpenHDS.Individuals.COLUMN_INDIVIDUAL_FULLNAME,
            OpenHDS.Individuals.COLUMN_INDIVIDUAL_EXTID };

    private static final int[] VIEW_BINDINGS = new int[] { android.R.id.text1, android.R.id.text2 };

    private SimpleCursorAdapter adapter;

    // since this fragment displays different types of entities, it needs to
    // keep track of which one is currently showing
    private Displayed listCurrentlyDisplayed;
    private ValueListener listener;

    private enum Displayed {
        HIERARCHY, SUBREGION, VILLAGE, ROUND, LOCATION, INDIVIDUAL;
    }
    
    public interface ValueListener {
        void onHierarchySelected(LocationHierarchy hierarchy);

        void onSubRegionSelected(LocationHierarchy subregion);

        void onVillageSelected(LocationHierarchy village);

        void onRoundSelected(Round round);

        void onLocationSelected(Location location);
        
        void onIndividualSelected(Individual individual);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        try {
            listener = (ValueListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement OnArticleSelectedListener");
        }

        adapter = new SimpleCursorAdapter(getActivity(), android.R.layout.simple_list_item_2, null, REGION_COLUMNS,
                VIEW_BINDINGS, 0);
        setListAdapter(adapter);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        Cursor cursor = (Cursor) adapter.getItem(position);

        switch (listCurrentlyDisplayed) {
        case HIERARCHY:
            LocationHierarchy region = Converter.convertToHierarchy(cursor);
            listener.onHierarchySelected(region);
            break;
        case SUBREGION:
            LocationHierarchy subregion = Converter.convertToHierarchy(cursor);
            listener.onSubRegionSelected(subregion);
            break;
        case VILLAGE:
            LocationHierarchy village = Converter.convertToHierarchy(cursor);
            listener.onVillageSelected(village);
            break;
        case ROUND:
            Round round = Converter.convertToRound(cursor);
            listener.onRoundSelected(round);
            break;
        case LOCATION:
            Location location = Converter.convertToLocation(cursor);
            listener.onLocationSelected(location);
            break;
        case INDIVIDUAL:
            Individual individual = Converter.convertToIndividual(cursor);
            listener.onIndividualSelected(individual);
            break;
        }

        adapter.swapCursor(null);
    }

    public void loadLocationHierarchy() {
        listCurrentlyDisplayed = Displayed.HIERARCHY;
        getLoaderManager().restartLoader(HIERARCHY_LOADER, null, this);
    }

    public void loadSubRegion(String parentExtId) {
        listCurrentlyDisplayed = Displayed.SUBREGION;
        loadHierarchyItemsFromParent(parentExtId);
    }

    private void loadHierarchyItemsFromParent(String parentExtId) {
        Bundle bundle = new Bundle();
        bundle.putString("parentExtId", parentExtId);
        getLoaderManager().restartLoader(REGION_LOADER, bundle, this);
    }

    public void loadVillage(String parentExtId) {
        listCurrentlyDisplayed = Displayed.VILLAGE;
        loadHierarchyItemsFromParent(parentExtId);
    }

    public Loader<Cursor> onCreateLoader(int arg0, Bundle arg1) {
        switch (arg0) {
        case HIERARCHY_LOADER:
            return new CursorLoader(getActivity(), OpenHDS.HierarchyItems.CONTENT_ID_URI_BASE, null,
                    OpenHDS.HierarchyItems.COLUMN_HIERARCHY_LEVEL + " = ?", new String[] { "LGA" }, null);
        case REGION_LOADER:
            adapter.changeCursorAndColumns(null, REGION_COLUMNS, VIEW_BINDINGS);
            return new CursorLoader(getActivity(), OpenHDS.HierarchyItems.CONTENT_ID_URI_BASE, null,
                    OpenHDS.HierarchyItems.COLUMN_HIERARCHY_PARENT + " = ?",
                    new String[] { arg1.getString("parentExtId") }, null);
        case ROUND_LOADER:
            adapter.changeCursorAndColumns(null, ROUNDS_COLUMNS, VIEW_BINDINGS);
            return new CursorLoader(getActivity(), OpenHDS.Rounds.CONTENT_ID_URI_BASE, null, null, null, null);
        case LOCATION_LOADER:
            adapter.changeCursorAndColumns(null, LOCATION_COLUMNS, VIEW_BINDINGS);
            return new CursorLoader(getActivity(), OpenHDS.Locations.CONTENT_ID_URI_BASE, null,
                    OpenHDS.Locations.COLUMN_LOCATION_HIERARCHY + " = ?",
                    new String[] { arg1.getString("hierarchyExtId") }, null);
        case INDIVIDUAL_LOADER:
            adapter.changeCursorAndColumns(null, INDIVIDUAL_COLUMNS, VIEW_BINDINGS);
            return new CursorLoader(getActivity(), OpenHDS.Individuals.CONTENT_ID_URI_BASE, null,
                    OpenHDS.Individuals.COLUMN_INDIVIDUAL_RESIDENCE + " = ?",
                    new String[] { arg1.getString("locationExtId") }, null);
        case INDIVIDUAL_FILTER_LOADER:
            adapter.changeCursorAndColumns(null, INDIVIDUAL_COLUMNS, VIEW_BINDINGS);
            
            String filter = buildFitler(arg1);
            String[] args = buildArguments(arg1);
            
            return new CursorLoader(getActivity(), OpenHDS.Individuals.CONTENT_ID_URI_BASE, null, filter, args,
                    OpenHDS.Individuals.COLUMN_INDIVIDUAL_EXTID + " ASC");
        }

        return null;
    }

    private String[] buildArguments(Bundle arg1) {
        List<String> args = new ArrayList<String>();
        
        if (!TextUtils.isEmpty(arg1.getString("location"))) {
            args.add(arg1.getString("location"));
        }
        if (!TextUtils.isEmpty(arg1.getString("firstName"))) {
            args.add("%" + arg1.getString("firstName") + "%");
        }
        if (!TextUtils.isEmpty(arg1.getString("lastName"))) {
            args.add("%" + arg1.getString("lastName") + "%");
        }
        if (!TextUtils.isEmpty(arg1.getString("gender"))) {
            args.add(arg1.getString("gender"));
        }
        
        return args.toArray(new String[]{});
    }

    private String buildFitler(Bundle arg1) {
        StringBuilder builder = new StringBuilder();
        
        if (!TextUtils.isEmpty(arg1.getString("location"))) {
            builder.append(OpenHDS.Individuals.COLUMN_INDIVIDUAL_RESIDENCE + " = ?");
        }
        if (!TextUtils.isEmpty(arg1.getString("firstName"))) {
            if (builder.length() > 0)
                builder.append(" AND ");
            builder.append(OpenHDS.Individuals.COLUMN_INDIVIDUAL_FIRSTNAME + " LIKE ?");
        }
        if (!TextUtils.isEmpty(arg1.getString("lastName"))) {
            if (builder.length() > 0)
                builder.append(" AND ");
            builder.append(OpenHDS.Individuals.COLUMN_INDIVIDUAL_LASTNAME +  " LIKE ?");
        }
        if (!TextUtils.isEmpty(arg1.getString("gender"))) {
            if (builder.length() > 0)
                builder.append(" AND ");
            builder.append(OpenHDS.Individuals.COLUMN_INDIVIDUAL_GENDER + " = ?");
        }
        
        return builder.toString();
    }

    public void onLoadFinished(Loader<Cursor> arg0, Cursor arg1) {
        adapter.swapCursor(arg1);
    }

    public void onLoaderReset(Loader<Cursor> arg0) {
        adapter.swapCursor(null);
    }

    public void loadRounds() {
        listCurrentlyDisplayed = Displayed.ROUND;
        getLoaderManager().restartLoader(ROUND_LOADER, null, this);
    }

    public void loadLocations(String hierarchyExtId) {
        listCurrentlyDisplayed = Displayed.LOCATION;
        Bundle bundle = new Bundle();
        bundle.putString("hierarchyExtId", hierarchyExtId);
        getLoaderManager().restartLoader(LOCATION_LOADER, bundle, this);
    }

    public void loadIndividuals(String extId) {
        listCurrentlyDisplayed = Displayed.INDIVIDUAL;
        Bundle bundle = new Bundle();
        bundle.putString("locationExtId", extId);
        getLoaderManager().restartLoader(INDIVIDUAL_LOADER, bundle, this);
    }
    
    public void loadFilteredIndividuals(String location, String firstName, String lastName, String gender) {
        listCurrentlyDisplayed = Displayed.INDIVIDUAL;
        Bundle bundle = new Bundle();
        bundle.putString("location", location);
        bundle.putString("firstName", firstName);
        bundle.putString("lastName", lastName);
        bundle.putString("gender", gender);
        getLoaderManager().restartLoader(INDIVIDUAL_FILTER_LOADER, bundle, this);
    }
}
