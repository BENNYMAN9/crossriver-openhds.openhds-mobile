package org.openhds.fragment;

import java.util.List;
import org.openhds.activity.R;
import org.openhds.listener.ValueSelectedListener;
import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class ValueFragment extends ListFragment {
	
	ValueSelectedListener mCallback;
	List<String> items;
	
	@Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        reset();
        
        // This makes sure that the container activity has implemented
        // the callback interface. If not, it throws an exception
        try {
            mCallback = (ValueSelectedListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement ValueSelectedListener");
        }
    }
		
	@Override
    public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
    }
	
	@Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        // Send the event to the host activity
        mCallback.onValueSelected(items.get(position));
        this.setListAdapter(new ArrayAdapter<String>(getActivity(), R.layout.value, R.id.textValue, new String[0]));	
    }
	
	public void setContent(List<String> list) {
		this.items = list;
		this.setListAdapter(new ArrayAdapter<String>(getActivity(), R.layout.value, R.id.textValue, list));	
	} 
	
	public void reset() {
		 this.setListAdapter(new ArrayAdapter<String>(getActivity(), R.layout.value, R.id.textValue, new String[0]));	
	}
}
