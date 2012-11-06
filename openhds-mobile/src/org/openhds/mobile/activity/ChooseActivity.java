package org.openhds.mobile.activity;

import org.openhds.mobile.R;
import org.openhds.mobile.model.FieldWorker;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

/**
  * User: Ime
 * Date: 11/5/12

 */
public class ChooseActivity extends AbstractActivity {
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chooseactivity);
        
        final FieldWorker fw = (FieldWorker) getIntent().getExtras().getSerializable("fieldWorker");
        
        Button updateBtn = (Button) findViewById(R.id.updateBtn);
        updateBtn.setOnClickListener(new OnClickListener() {
			public void onClick(View arg0) {
				startUpdateActivity(fw);
			}
		});

		Button commBtn = (Button) findViewById(R.id.commBtn);
		commBtn.setOnClickListener(new OnClickListener() {
			public void onClick(View arg0) {
				Intent intent = new Intent(getApplicationContext(), CommActivity.class);
		        intent.putExtra("fieldWorker", fw);
		        startActivity(intent);
			}
		});
    }
    
    private void startUpdateActivity(FieldWorker fieldWorker) {
		Intent intent = new Intent(getApplicationContext(), UpdateActivity.class);
        intent.putExtra("fieldWorker", fieldWorker);
        startActivity(intent);
	}
}