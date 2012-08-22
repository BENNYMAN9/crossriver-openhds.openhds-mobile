package org.openhds.mobile.dialog;

import org.openhds.mobile.R;
import org.openhds.mobile.activity.UpdateActivity;
import org.openhds.mobile.fragment.SelectionFragment;
import org.openhds.mobile.model.Individual;
import android.app.Dialog;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.view.View;

public class HouseholdListDialog extends Dialog {
	
	private ListView list;
	private EditText filterText = null;
	private ArrayAdapter<String> adapter = null;
	private String event;
	
	public HouseholdListDialog(final UpdateActivity context, final SelectionFragment sf, final String event) {
		super(context);
		this.event = event;

		setContentView(R.layout.household_dialog);
	    this.setTitle("Select Household");
	    
	    filterText = (EditText) findViewById(R.id.EditBox);
	    filterText.addTextChangedListener(filterTextWatcher);

	    list = (ListView) findViewById(R.id.List);
	    adapter = new ArrayAdapter<String>(context, android.R.layout.simple_list_item_1, sf.getAllSocialGroupsForDialog());
	    list.setAdapter(adapter);
	   
	    list.setOnItemClickListener(new OnItemClickListener() {
	        public void onItemClick(AdapterView<?> a, View v, int position, long id) {
	           String groupName = (String) list.getItemAtPosition(position);
	           sf.setSocialGroup(groupName);
	           
	       	   String extId = sf.generateId(01, sf.getSocialgroup().getExtId());
			
	       	   Individual individual = new Individual();
	       	   individual.setExtId(extId);
	       	   sf.setIndividual(individual);
			
	           HouseholdListDialog.this.dismiss();
	           context.loadForm(event);
	        }
	    });
	}

	private TextWatcher filterTextWatcher = new TextWatcher() {
	    public void afterTextChanged(Editable s) { }

	    public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

	    public void onTextChanged(CharSequence s, int start, int before, int count) {
	        adapter.getFilter().filter(s);
	    }
	};
}
