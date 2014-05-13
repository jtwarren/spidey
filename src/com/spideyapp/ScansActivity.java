package com.spideyapp;

import java.util.ArrayList;
import java.util.List;

import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.spideyapp.sqlite.helper.DatabaseHelper;
import com.spideyapp.sqlite.model.Scan;

public class ScansActivity extends ListActivity {

	private static final String TAG = "ScanDetails";

	private DatabaseHelper mDb;

	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        setTitle(R.string.history_of_spidey_scans);
        
        mDb = DatabaseHelper.getInstance(this);
        List<Scan> scans = mDb.getAllScans();
        
        for (Scan scan : scans) {
        	Log.d(TAG, "" + scan.getId());
        }

        // use the SimpleCursorAdapter to show the
        // elements in a ListView
        ScanAdapter adapter = new ScanAdapter(this,scans);
        setListAdapter(adapter);
        
        ListView lv = getListView();
        
        // listening to single list item on click
        lv.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
	              Intent i = new Intent(getApplicationContext(), ScanDetailsActivity.class);
	              // TODO: The id is currently off by one so quick fix below.
	              i.putExtra("scan_id", id + 1);
	              startActivity(i);
			}
        });
        
         
    }
	
	class ScanAdapter extends BaseAdapter {

	    Context context;
	    List<Scan> scans;
	    LayoutInflater inflater = null;

	    public ScanAdapter(Context context, List<Scan> scans) {
	        // TODO Auto-generated constructor stub
	        this.context = context;
	        this.scans = scans;
	        inflater = (LayoutInflater) context
	                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	    }

	    @Override
	    public int getCount() {
	        // TODO Auto-generated method stub
	        return scans.size();
	    }

	    @Override
	    public Object getItem(int position) {
	        // TODO Auto-generated method stub
	        return scans.get(position);
	    }

	    @Override
	    public long getItemId(int position) {
	        // TODO Auto-generated method stub
	        return position;
	    }

	    @Override
	    public View getView(int position, View convertView, ViewGroup parent) {
	        // TODO Auto-generated method stub
	        View vi = convertView;
	        if (vi == null)
	            vi = inflater.inflate(R.layout.view_scanrow, null);
	        
	        Scan scan = scans.get(position);
	        
	        TextView text = (TextView) vi.findViewById(R.id.row1);	        
	        String row1 = scan.getLocation();	        
	        text.setText(row1);
	        
	        text = (TextView) vi.findViewById(R.id.row2);	        
	        String row2 = scan.getCreatedAt(); 
	        text.setText(row2);
	        
	        return vi;
	    }
	}
}
