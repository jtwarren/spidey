package com.example.spidey;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

public class DatabaseActivity extends Activity {

	TextView idView;
	EditText scanBox;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_database);

		idView = (TextView) findViewById(R.id.scanID);
		scanBox = (EditText) findViewById(R.id.scanName);

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
//		getMenuInflater().inflate(R.menu.activity_database, menu);
		return true;
	}

	public void newScan(View view) {
		DatabaseHandler dbHandler = new DatabaseHandler(this, null, null, 1);

		Scan scan = new Scan(scanBox.getText().toString());

		dbHandler.addScan(scan);
		scanBox.setText("");
	}

	public void lookupScan(View view) {
		DatabaseHandler dbHandler = new DatabaseHandler(this, null, null, 1);

		Scan scan = dbHandler.findScan(scanBox.getText().toString());

		if (scan != null) {
			idView.setText(String.valueOf(scan.getID()));
		} else {
			idView.setText("No Match Found");
		}
	}

	public void removeScan(View view) {
		DatabaseHandler dbHandler = new DatabaseHandler(this, null, null, 1);

		boolean result = dbHandler.deleteScan(scanBox.getText().toString());

		if (result) {
			idView.setText("Record Deleted");
			scanBox.setText("");
		} else
			idView.setText("No Match Found");
	}
}