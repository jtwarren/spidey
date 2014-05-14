package com.spideyapp;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import com.spideyapp.sqlite.helper.DatabaseHelper;
import com.spideyapp.sqlite.model.Scan;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class HistoryActivity extends Activity implements OnItemClickListener {
	
	private final static String TAG = "HistoryActivity";

	private ListView historyListView;
	private ArrayList<Scan> mScans;
	
	private DatabaseHelper mDb;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scans);
        
        historyListView = (ListView) findViewById(R.id.historyListView);
        
        mScans = new ArrayList<Scan>();
        
        mDb = DatabaseHelper.getInstance(this);
		mScans = (ArrayList<Scan>) mDb.getAllScans();
        
        // Initialize our Adapter and plug it to the ListView
		ScanAdapter customAdapter = new ScanAdapter(this, mScans);
        historyListView.setAdapter(customAdapter);     
        
        // Activate the Click even of the List items
        historyListView.setOnItemClickListener(this);
    }

    // Regular inner class which act as the Adapter
	public class ScanAdapter extends BaseAdapter {
		
		Context context;
		List<Scan> scans;
		LayoutInflater inflater = null;
		
		HashSet<Scan> checkedScans;

		public ScanAdapter(Context context, ArrayList<Scan> scans) {
			this.context = context;
			this.scans = scans;
			this.checkedScans = new HashSet<Scan>();
			inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		}

		@Override
		public int getCount() {
			return scans.size();
		}

		@Override
		public Object getItem(int position) {
			return scans.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			
			View vi = convertView;
			if (vi == null)
				vi = inflater.inflate(R.layout.view_scanrow, null);

			final Scan scan = scans.get(position);

			TextView text = (TextView) vi.findViewById(R.id.row1);
			String row1 = scan.getLocation();
			text.setText(row1);

			text = (TextView) vi.findViewById(R.id.row2);
			String row2 = scan.getCreatedAt();
			text.setText(row2);
			
			CheckBox checkBox = (CheckBox) vi.findViewById(R.id.checkbox1);
			
			checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			       @Override
			       public void onCheckedChanged(CompoundButton buttonView,boolean isChecked) {
			    	   if (isChecked) {
			    		   if (ScanAdapter.this.checkedScans.size() == 1) {
			    			   Scan scan1 = ScanAdapter.this.checkedScans.iterator().next();
			    			   ScanDiff diff = ScanDiff.compare(scan1, scan);
			    			   Log.d(TAG, diff.toString());
			    		   }
			    		   ScanAdapter.this.checkedScans.add(scan);
			    	   } else {
			    		   ScanAdapter.this.checkedScans.remove(scan);
			    	   }			    	   
			    	   Log.d(TAG, ScanAdapter.this.checkedScans.toString());
			       }
			   }
			);

			return vi;
		}		
	}


	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		Intent i = new Intent(getApplicationContext(),
				ScanDetailsActivity.class);
		// TODO: The id is currently off by one so quick fix below.
		i.putExtra("scan_id", id + 1);
		startActivity(i);
	}
}