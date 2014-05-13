package com.spideyapp;

import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.fima.cardsui.views.CardUI;
import com.spideyapp.sqlite.helper.DatabaseHelper;
import com.spideyapp.sqlite.model.CellInfo;
import com.spideyapp.sqlite.model.Scan;
import com.spideyapp.views.ScanDetailCard;

public class ScanDetailsActivity extends Activity{
	
	private static final String TAG = "ScanDetails";
	
	private DatabaseHelper mDb;
	private CardUI mCardView;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.activity_scan_details);

		mCardView = (CardUI) findViewById(R.id.cardsview);
		mCardView.setSwipeable(false);
		
        mDb = DatabaseHelper.getInstance(this);
        
        Intent i = getIntent();
        long scan_id = i.getLongExtra("scan_id", -1);
        
        Scan scan = mDb.getScan(scan_id);
        setTitle(scan.getId() + ": " + scan.getLocation() + " " + scan.getCreatedAt());
        
        List<CellInfo> cells = mDb.getAllCellsByScanId(scan.getId());
        
        for (CellInfo cInfo : cells)
        {
        	String cTitle = cInfo.getCreatedAt();
        	String cContent = "CellID: " + cInfo.getCID();
        	
	    	mCardView
			.addCard(new ScanDetailCard(
					cTitle,
					cContent,
					"#00ff00", "#777777", false, false));
        }
        

		// draw cards
		mCardView.refresh();
         
    }
    

}