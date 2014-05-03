package com.spideyapp;

import java.util.List;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.spideyapp.sqlite.helper.DatabaseHelper;
import com.spideyapp.sqlite.model.Scan;

public class ScansActivity extends ListActivity {

	private static final String TAG = "ScanDetails";

	private DatabaseHelper mDb;

	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        mDb = DatabaseHelper.getInstance(this);
        List<Scan> scans = mDb.getAllScans();

        // use the SimpleCursorAdapter to show the
        // elements in a ListView
        ArrayAdapter<Scan> adapter = new ArrayAdapter<Scan>(this,
        		R.layout.activity_scans, scans);
        setListAdapter(adapter);
        
        ListView lv = getListView();
        
        // listening to single list item on click
        lv.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
	              Intent i = new Intent(getApplicationContext(), ScanDetails.class);
	              i.putExtra("scan_id", id);
	              startActivity(i);
			}
        });
         
    }
}