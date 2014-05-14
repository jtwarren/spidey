package com.spideyapp;

import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.fima.cardsui.views.CardUI;
import com.spideyapp.sqlite.helper.DatabaseHelper;
import com.spideyapp.sqlite.model.CellInfo;
import com.spideyapp.sqlite.model.Scan;
import com.spideyapp.views.ScanDetailCard;

public class ScanDetailsActivity extends Activity{
	
	private DatabaseHelper mDb;
	private CardUI mCardView;
	
	private Scan mScan;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.activity_scan_details);

		mCardView = (CardUI) findViewById(R.id.cardsview);
		mCardView.setSwipeable(false);
		
        mDb = DatabaseHelper.getInstance(this);
        
        Intent i = getIntent();
        long scan_id = i.getLongExtra("scan_id", -1);
        
        mScan = mDb.getScan(scan_id);
        
        if (mScan != null)
        {
	        setTitle(mScan.getLocation() + " " + mScan.getCreatedAt());
	        
	        List<CellInfo> cells = mDb.getAllCellsByScanId(mScan.getId());
	        
	        for (CellInfo cInfo : cells)
	        {
	        	String cTitle = "Cell Identifier (CID) " + cInfo.getCID();
	        	String cContent = cInfo.toString();
	        	
		    	mCardView
				.addCard(new ScanDetailCard(
						cTitle,
						cContent,
						"#00ff00", "#333333", false, false));
	        }
	        
	
			// draw cards
			mCardView.refresh();
        }
        else
        {
        	finish();
        }
         
    }
    
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.menu_scan_details, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		
		if (id == R.id.action_map) {

	    	Intent i = new Intent(getApplicationContext(), MainActivity.class);	    	
	    	i.putExtra("scan_id", mScan.getId());	    	
	    	startActivity(i);
		}

		return super.onOptionsItemSelected(item);
	}

    

}