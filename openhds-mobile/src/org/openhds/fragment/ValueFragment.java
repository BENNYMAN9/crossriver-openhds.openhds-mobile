package org.openhds.fragment;

import java.util.ArrayList;
import java.util.List;
import org.openhds.activity.R;
import org.openhds.cell.ValueFragmentCell;
import org.openhds.listener.ValueSelectedListener;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class ValueFragment extends ListFragment {
	
	ValueSelectedListener mCallback;
	List<ValueFragmentCell> items;
	
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
        mCallback.onValueSelected(position);
        this.setListAdapter(new ArrayAdapter<String>(getActivity(), R.layout.value, R.id.textValue, new String[0]));	
    }
	
	public void setContent(List<ValueFragmentCell> list) {
		this.items = list;
		MyListAdapter adapter = new MyListAdapter(getActivity(), R.layout.custom_row, list);
		this.setListAdapter(adapter);
		//this.setListAdapter(new ArrayAdapter<String>(getActivity(), R.layout.value, R.id.textValue, list));	
	} 
	
	public void reset() {
		 this.setListAdapter(new ArrayAdapter<String>(getActivity(), R.layout.value, R.id.textValue, new String[0]));	
	}
	
	private class MyListAdapter extends ArrayAdapter<ValueFragmentCell> {
		
        private List<ValueFragmentCell> items;

        public MyListAdapter(Context context, int textViewResourceId, List<ValueFragmentCell> objects) {
        	super(context, textViewResourceId, objects);
            this.items = objects;
        }
        
        public View getView(int position, View convertView, ViewGroup parent) {
        	View v = convertView;
                        
	        if (v == null) {
	            LayoutInflater vi = (LayoutInflater)getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	            v = vi.inflate(R.layout.custom_row, null);
	        }
                        
	        ValueFragmentCell item = items.get(position);
            if (item != null) {
                    TextView tt = (TextView) v.findViewById(R.id.toptext);
                    TextView bt = (TextView) v.findViewById(R.id.bottomtext);
                    
                    if (tt != null) 
                    	tt.setText(item.getItem1());
                    if (bt != null)
                    	bt.setText(item.getItem2());
            }
            return v;
        }
    }
}
