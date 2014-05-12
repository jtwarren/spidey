package com.spideyapp;

import java.util.List;

import com.spideyapp.sqlite.helper.DatabaseHelper;
import com.spideyapp.sqlite.model.CellInfo;
import com.spideyapp.sqlite.model.Scan;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

public class ScanDetailsActivity extends Activity{
	
	private static final String TAG = "ScanDetails";
	
	private DatabaseHelper mDb;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.activity_scan_details);
        
        TextView scanTitle = (TextView) findViewById(R.id.scan_title);
        
        mDb = DatabaseHelper.getInstance(this);
        
        Intent i = getIntent();
        long scan_id = i.getLongExtra("scan_id", -1);
        Log.d(TAG, "scan_id is: " + scan_id);
        
        if (scan_id == -1) {
        	scanTitle.setText("Invalid scan id");
        	return;
        }
        
        Scan scan = mDb.getScan(scan_id);
        scanTitle.setText(scan.getId() + ": " + scan.getLocation() + " " + scan.getCreatedAt());
        
        List<CellInfo> cells = mDb.getAllCellsByScanId(scan.getId());
        Log.d(TAG, "cells: " + cells);
       
         
    }
}