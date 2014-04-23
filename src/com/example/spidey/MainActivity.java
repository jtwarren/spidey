package com.example.spidey;

import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

public class MainActivity extends Activity {
	
	
	private MapFragment mMapFragment;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		if (savedInstanceState == null) {
			mMapFragment = new MapFragment();
			
			getFragmentManager().beginTransaction()
					.add(R.id.container, mMapFragment).commit();
			
			
		}
	
		// Use this to test simple database CRUD
		 Intent intent = new Intent(this, DatabaseActivity.class);
		 startActivity(intent);

	}

	@Override
	protected void onResume() {
		super.onResume();
		startService(new Intent(this, ScanService.class));
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	/**
	 * A placeholder fragment containing a simple view.
	 */
	public static class MapFragment extends Fragment {

		MapView mMapView;
		
		public MapFragment() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_main, container,
					false);
			
			setupMap("0","0",5,rootView);
			
			return rootView;
		}
		

		protected void setupMap (String lat, String lon, int zoomLevel, View view)
		{
			mMapView = (MapView)view.findViewById(R.id.mapview);
			mMapView.setBuiltInZoomControls(true);
			mMapView.getController().setZoom(zoomLevel);
	        mMapView.getController().setCenter(GeoPoint.fromDoubleString(lat + ',' + lon,','));
			
		
		}
	}

}
