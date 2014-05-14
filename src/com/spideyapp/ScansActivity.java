package com.spideyapp;

import java.util.List;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.spideyapp.sqlite.helper.DatabaseHelper;
import com.spideyapp.sqlite.model.CellInfo;
import com.spideyapp.sqlite.model.Scan;

public class ScansActivity extends ListActivity {

	private static final String TAG = "ScanDetails";

	private DatabaseHelper mDb;
	private ListView mListView;
	
	private ActionMode mActionMode;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        setTitle(R.string.history_of_spidey_scans);
        
        mDb = DatabaseHelper.getInstance(this);
        List<Scan> scans = mDb.getAllScans();

        mListView = getListView();
        
        //add on long click listener to start action mode
        getListView().setOnItemLongClickListener(new OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int position, long id) {
                onListItemCheck(position);
                return true;
            }
        });
        
     // use the SimpleCursorAdapter to show the
        // elements in a ListView
        ScanAdapter adapter = new ScanAdapter(this,scans);
        setListAdapter(adapter);
         
    }
	
	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {     
	    if(mActionMode == null) {
	        
	    	Intent i = new Intent(getApplicationContext(), ScanDetailsActivity.class);	    	
	    	i.putExtra("scan_id", id);	    	
	    	startActivity(i);
	    	
	    } else
	        // add or remove selection for current list item
	        onListItemCheck(position);      
	}   
	 
	private void onListItemCheck(int position) {
		ScanAdapter adapter = (ScanAdapter) getListAdapter();
		adapter.toggleSelection(position);
		boolean hasCheckedItems = adapter.getSelectedCount() > 0;        
		 
		if (hasCheckedItems && mActionMode == null)
		    // there are some selected items, start the actionMode
		    mActionMode = startActionMode(new ActionModeCallback());
		else if (!hasCheckedItems && mActionMode != null)
		    // there no selected items, finish the actionMode
		    mActionMode.finish();
		         
		 
		if(mActionMode != null)
		    mActionMode.setTitle(String.valueOf(adapter.getSelectedCount()) + " selected");
	}
	
	class ScanAdapter extends BaseAdapter {

	    Context context;
	    List<Scan> scans;
	    LayoutInflater inflater = null;
	    private SparseBooleanArray mSelectedItemsIds;

	    public ScanAdapter(Context context, List<Scan> scans) {
	        this.context = context;
	        this.scans = scans;
	        inflater = (LayoutInflater) context
	                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	        mSelectedItemsIds = new SparseBooleanArray();

	    }

	    @Override
	    public int getCount() {
	        return scans.size();
	    }

	    public void toggleSelection(int position)
	    {
	        selectView(position, !mSelectedItemsIds.get(position));
	    }
	    

		public void removeSelection() {
		    mSelectedItemsIds = new SparseBooleanArray();
		    notifyDataSetChanged();
		}
		 
	    
	    public void selectView(int position, boolean value)
	    {
	        if(value)
	            mSelectedItemsIds.put(position, value);
	        else
	            mSelectedItemsIds.delete(position);
	                 
	        notifyDataSetChanged();
	    }
	             
	    public int getSelectedCount() {
	        return mSelectedItemsIds.size();// mSelectedCount;
	    }
	             
	    public SparseBooleanArray getSelectedIds() {
	        return mSelectedItemsIds;
	    }
	    
	    @Override
	    public Scan getItem(int position) {
	        return scans.get(position);
	    }

	    @Override
	    public long getItemId(int position) {
	        return scans.get(position).getId();
	    }

	    @Override
	    public View getView(int position, View convertView, ViewGroup parent) {

	    	View vi = convertView;
	        if (vi == null)
	            vi = inflater.inflate(R.layout.view_scanrow, null);
	        
	        Scan scan = scans.get(position);
	        
	        TextView text = (TextView) vi.findViewById(R.id.row1);	        
	        String row1 = scan.getLocation() + " (" + scan.getCellInfos().size() + " cell towers found)";	        
	        text.setText(row1);
	        
	        text = (TextView) vi.findViewById(R.id.row2);	        
	        String row2 = scan.getCreatedAt(); 
	        text.setText(row2);
	        
	        vi.setBackgroundColor(mSelectedItemsIds.get(position)? 0x9934B5E4: Color.TRANSPARENT);         

	        
	        return vi;
	    }
	}
	
	private void shareText (String shareBody)
	{
	
	    Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
	        sharingIntent.setType("text/plain");
	        sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Spidey Scan Report");
	        sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
	        startActivity(Intent.createChooser(sharingIntent, "Share Spidey Scan..."));
	}
	
	private void showDiffReport (ScanDiff diff)
	{
		StringBuffer msg = new StringBuffer();
		
		if (diff.getAdditions().size() == 0)
		{
			msg.append("There were no NEW cell towers found");
		}
		else
		{
			msg.append("There were " + diff.getAdditions().size() + " NEW cell towers found:\n\n");
			
			for (CellInfo cid : diff.getAdditions())
			{
				msg.append("CellID: " + cid.getCID() + " LAC:" + cid.getLAC() + " DBM:" + cid.getDBM());
				msg.append("\n");
			}
			
		}
		
		msg.append("\n\n");
		
		if (diff.getSubtractions().size() == 0)
		{
			msg.append("There were no MISSING cell towers");
		}
		else
		{
			msg.append("There were " + diff.getSubtractions().size() + " MISSING cell towers:\n\n");
			
			for (CellInfo cid : diff.getSubtractions())
			{
				msg.append("CellID: " + cid.getCID() + " LAC:" + cid.getLAC() + " DBM:" + cid.getDBM());
				msg.append("\n");
			}
		}
		
		
		new AlertDialog.Builder(this)
	    .setTitle("Scan Compare Results")
	    .setMessage(msg.toString())
	    .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
	        public void onClick(DialogInterface dialog, int which) { 
	            // continue with delete
	        }
	     })	    
	    .setIcon(android.R.drawable.ic_dialog_info)
	     .show();
	}
	
	private class ActionModeCallback implements ActionMode.Callback {
		 
		@Override
		public boolean onCreateActionMode(ActionMode mode, Menu menu) {
		    // inflate contextual menu  
		    mode.getMenuInflater().inflate(R.menu.scans_action_menu, menu);
		    return true;
		}
		 
		@Override
		public boolean onPrepareActionMode(ActionMode mode, Menu menu) {            
		    return false;
		}
		 
		@Override
		public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
		    // retrieve selected items and print them out
		    ScanAdapter adapter = (ScanAdapter) ScansActivity.this.getListAdapter();
		    SparseBooleanArray selected = adapter.getSelectedIds();
		    
		    if (item.getItemId() == R.id.compare_item)
		    {
		    
			    if (selected.size() == 2)
			    {
			    	Scan scanOne = adapter.getItem(selected.keyAt(0));
			    	Scan scanTwo = adapter.getItem(selected.keyAt(1));
			    	
			    	ScanDiff diff = ScanDiff.compare(scanOne, scanTwo);
			    	
			    	showDiffReport(diff);
			    	
			    }
			    else
			    {
			        Toast.makeText(ScansActivity.this, "Please select two scans to compare", Toast.LENGTH_LONG).show();
			    }
			    
		    }
		    else if (item.getItemId() == R.id.share_item)
		    {
			    for (int i = 0; i < selected.size(); i++){               
			        if (selected.valueAt(i)) {
			            
			        	Scan selectedItem = adapter.getItem(selected.keyAt(i));
			        	
			        	
			        	String shareText = selectedItem.toString();
			        	shareText(shareText);
			            
			        }
			    }
		    }
		    
		    
		 
		    // close action mode
		    mode.finish();
		    return false;
		}
		 
		@Override
		public void onDestroyActionMode(ActionMode mode) {
		    // remove selection 
		    ScanAdapter adapter = (ScanAdapter) getListAdapter();
		    adapter.removeSelection();
		    mActionMode = null;
		}
		 
		}
}
