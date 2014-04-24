package com.example.spidey;

import java.io.IOException;

import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.graphics.Color;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;

import com.example.spidey.cloud.OpenCellIdLookup;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient.ConnectionCallbacks;
import com.google.android.gms.common.GooglePlayServicesClient.OnConnectionFailedListener;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;

public class MainActivity extends Activity {
	
	
	private final static String TAG = "Spidey";
	
	private final static int DEFAULT_MAP_ZOOM = 16;
	
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
//		 Intent intent = new Intent(this, DatabaseActivity.class);
//		 startActivity(intent);

	}

	@Override
	protected void onResume() {
		super.onResume();
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
		else if (id == R.id.action_scan) {
			runScan ();
		}
		else if (id == R.id.action_about) {
			showAbout ();
		}
		
		return super.onOptionsItemSelected(item);
	}

	private void showAbout ()
	{
		Intent intent = new Intent(this,AboutActivity.class);
		startActivity(intent);
	}
	
	private void runScan ()
	{

		startService(new Intent(this, ScanService.class));
		
		for (int i = 1; i < 6; i++)
			mMapFragment.addResult("Tower Scan " + i + "\nresult info");
		
		doCellLookup ();
		
	}
	
	private void doCellLookup ()
	{
		new CellLookupOperation().execute("");
		
	}
		
		
	private class CellLookupOperation extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            
        	try {
				OpenCellIdLookup.findMatchingCell("310","4384","65","2578");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
        }

        @Override
        protected void onPreExecute() {
        }

        @Override
        protected void onProgressUpdate(Void... values) {
        }
    }
	/**
	 * A placeholder fragment containing a simple view.
	 */
	public static class MapFragment extends Fragment implements LocationListener {

		MapView mMapView;
		Activity mActivity;
		LinearLayout mViewResults;
		View mRootView;
		
		public MapFragment() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			mRootView = inflater.inflate(R.layout.fragment_main, container,
					false);
			
			
			mViewResults = (LinearLayout)mRootView.findViewById(R.id.results);
			
			mActivity = this.getActivity();
			

	        doLocation();
			
			return mRootView;
		}
		
		public void addResult (String text)
		{
/**
 *  <Button
        android:text="Scan Result 1"
        android:textSize="24sp"
        android:textColor="#000000"
        
        android:background="#cc999999"
        android:layout_height="wrap_content"
        android:layout_width="match_parent"
        android:gravity="center"
    android:layout_margin="6dp"
    
 */
			Button btn = new Button(mActivity);
			btn.setText(text);
			btn.setTextSize(24);
			btn.setTextColor(Color.BLACK);
			btn.setPadding(3, 3, 3, 3);
			btn.setBackgroundColor(Color.parseColor("#CC999999"));
			LayoutParams lp = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
			lp.setMargins(6, 6, 6, 6);
			mViewResults.addView(btn,lp);
		}
		

		protected void setupMap (String lat, String lon, int zoomLevel, View view)
		{
			mMapView = (MapView)view.findViewById(R.id.mapview);
			mMapView.getOverlayManager().getTilesOverlay().setOvershootTileCache(300);
		      
			mMapView.setTileSource(TileSourceFactory.MAPNIK);
			mMapView.setBuiltInZoomControls(true);
			mMapView.setMultiTouchControls(true);
			
			mMapView.getController().setZoom(zoomLevel);
	        mMapView.getController().setCenter(GeoPoint.fromDoubleString(lat + ',' + lon,','));
	        
		}
		
		private LocationClient locationclient;
		private LocationRequest locationrequest;
		
		private void doLocation ()
		{
			locationclient = new LocationClient(this.getActivity().getApplicationContext(),new ConnectionCallbacks (){

				@Override
				public void onConnected(Bundle arg0) {

					locationrequest = LocationRequest.create();
					locationrequest.setInterval(100);
					locationclient.requestLocationUpdates(locationrequest, (LocationListener) MapFragment.this);
				}

				@Override
				public void onDisconnected() {
					
					
				}}, new OnConnectionFailedListener (){

					@Override
					public void onConnectionFailed(ConnectionResult arg0) {
						
						Log.d(TAG, "Could not connect to Google Play Services (location provider)");
						
					}});
			locationclient.connect();
	
		}
		
		@Override
		public void onLocationChanged(Location loc) {
			
			if (mMapView == null)
				setupMap(loc.getLatitude()+"",loc.getLongitude()+"",DEFAULT_MAP_ZOOM,mRootView);
			
			
		}	
		
	}
	

}
