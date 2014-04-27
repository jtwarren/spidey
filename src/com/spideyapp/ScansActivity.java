package com.spideyapp;

import java.util.ArrayList;
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
	
	private DatabaseHelper mDb;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        mDb = DatabaseHelper.getInstance(this);
        
        List<Scan> scans = mDb.getAllScans();
        
        List<String> stringsList = new ArrayList<String>(scans.size()); 
        for (Scan scan : scans) {
            stringsList.add(scan.getLocation() + " - " + scan.getCreatedAt());   
        }
         
        // Binding resources Array to ListAdapter
        this.setListAdapter(new ArrayAdapter<String>(this, R.layout.activity_scans, R.id.label, stringsList));
        
        ListView lv = getListView();
        
        // listening to single list item on click
        lv.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
	              Intent i = new Intent(getApplicationContext(), ScanDetails.class);
	              i.putExtra("id", 0);
	              startActivity(i);
				
			}
        });
         
    }
}