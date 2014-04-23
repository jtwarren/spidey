package com.example.spidey;

import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient.ConnectionCallbacks;
import com.google.android.gms.common.GooglePlayServicesClient.OnConnectionFailedListener;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;

public class MainActivity extends Activity {
	
	
	private final static String TAG = "Spidey";
	
	private final static int DEFAULT_MAP_ZOOM = 17;
	
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
		
		return super.onOptionsItemSelected(item);
	}

	private void runScan ()
	{

		startService(new Intent(this, ScanService.class));
	}
	/**
	 * A placeholder fragment containing a simple view.
	 */
	public static class MapFragment extends Fragment implements LocationListener {

		MapView mMapView;
		Activity mActivity;
		
		public MapFragment() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_main, container,
					false);
			
			setupMap("0","0",DEFAULT_MAP_ZOOM,rootView);
			
			mActivity = this.getActivity();
			
			return rootView;
		}
		

		protected void setupMap (String lat, String lon, int zoomLevel, View view)
		{
			mMapView = (MapView)view.findViewById(R.id.mapview);
			mMapView.setTileSource(TileSourceFactory.MAPNIK);
			mMapView.setBuiltInZoomControls(true);
			mMapView.getController().setZoom(zoomLevel);
	        mMapView.getController().setCenter(GeoPoint.fromDoubleString(lat + ',' + lon,','));
			
	        doLocation();
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
			
			if (mMapView != null)
			  mMapView.getController().setCenter(new GeoPoint(loc.getLatitude(),loc.getLongitude()));
				
			
		}	
		
	}
	

}
