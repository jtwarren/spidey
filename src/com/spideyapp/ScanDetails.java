package com.spideyapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

public class ScanDetails extends Activity{
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.activity_scan_details);
         
        TextView scanTitle = (TextView) findViewById(R.id.scan_title);
        scanTitle.setText("Test Scan Title");
         
        // TODO: use this to get scan information and display details
        Intent i = getIntent();
        long id = i.getLongExtra("id", -1);
         
    }
}