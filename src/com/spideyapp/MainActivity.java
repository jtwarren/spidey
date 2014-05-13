package com.spideyapp;

import java.io.IOException;

import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.SimpleLocationOverlay;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

import com.espian.showcaseview.OnShowcaseEventListener;
import com.espian.showcaseview.ShowcaseView;
import com.espian.showcaseview.targets.ViewTarget;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient.ConnectionCallbacks;
import com.google.android.gms.common.GooglePlayServicesClient.OnConnectionFailedListener;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.spideyapp.cloud.OpenCellIdLookup;
import com.spideyapp.sqlite.model.CellInfo;
import com.spideyapp.sqlite.model.Scan;

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

		//setupTabs();
		
		showWelcome();
	}
	
	/**
	private void setupTabs ()
	{
	
		ActionBar actionBar = this.getActionBar();
		
	    // Specify that tabs should be displayed in the action bar.
	    actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

	    // Create a tab listener that is called when the user changes tabs.
	    ActionBar.TabListener tabListener = new ActionBar.TabListener() {

			@Override
			public void onTabReselected(Tab tab,
					android.app.FragmentTransaction ft) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onTabSelected(Tab tab,
					android.app.FragmentTransaction ft) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onTabUnselected(Tab tab,
					android.app.FragmentTransaction ft) {
				// TODO Auto-generated method stub
				
			}
	    };

        actionBar.addTab(
                actionBar.newTab()
                        .setText("Scan Map")
                        .setTabListener(tabListener));
        
        actionBar.addTab(
                actionBar.newTab()
                        .setText("History")
                        .setTabListener(tabListener));
        
        actionBar.addTab(
                actionBar.newTab()
                        .setText("About Spidey")
                        .setTabListener(tabListener));
    
	}*/
	
	private void showWelcome ()
	{

        ShowcaseView.ConfigOptions co = new ShowcaseView.ConfigOptions();        	
		co.shotType = ShowcaseView.TYPE_NO_LIMIT;
		co.hideOnClickOutside = true;
		
        ViewTarget target = new ViewTarget(R.id.container, this);
        final ShowcaseView sv = ShowcaseView.insertShowcaseView(target, this, getString(R.string.activate_your_spidey_sense),getString(R.string.spidey_is_an_app_), co);
        sv.setHardwareAccelerated(true);
        
        // set black background
        sv.setBackgroundColor(Color.BLACK);
        // make background a bit transparent
        sv.setAlpha(0.75f);
        
        sv.setOnShowcaseEventListener(new OnShowcaseEventListener ()
        {

			@Override
			public void onShowcaseViewHide(ShowcaseView showcaseView) {
				
			}

			@Override
			public void onShowcaseViewDidHide(ShowcaseView showcaseView) {
			
				
			}

			@Override
			public void onShowcaseViewShow(ShowcaseView showcaseView) {
				
			}
        	
        });
        
        sv.setOnClickListener(new OnClickListener ()
        {

			@Override
			public void onClick(View v) {
				sv.hide();
			}
        	
        });
		
	}

	@Override
	protected void onResume() {
		super.onResume();
		 registerReceiver(receiver, new IntentFilter(ScanService.NOTIFICATION));
	}
	
	@Override
	  protected void onPause() {
	    super.onPause();
	    unregisterReceiver(receiver);
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
		} else if (id == R.id.action_scan) {
			runScan();
		} else if (id == R.id.action_about) {
			showAbout();
		} else if (id == R.id.action_scans) {
			showScans();
		}

		return super.onOptionsItemSelected(item);
	}

	private void showAbout() {
		Intent intent = new Intent(this, AboutActivity.class);
		startActivity(intent);
	}
	
	private void showScans() {
		Intent intent = new Intent(this, ScansActivity.class);
		startActivity(intent);
	}

	private void showDialog (String msg)
	{

		final TextView tv = new TextView(this);
		tv.setText("Welcome to Spidey! Welcome to Spidey! Welcome to Spidey!Welcome to Spidey!");
		
		mMapFragment.addView(tv, 36);
		
		tv.setOnClickListener(new OnClickListener()
		{

			@Override
			public void onClick(View v) {
				mMapFragment.removeView(tv);
			}
			
		});
	}
	
	private void runScan() {


		// Set an EditText view to get user input 
		final EditText input = new EditText(this);
		
		new AlertDialog.Builder(this)
	    .setTitle(R.string.scan_name)
	    .setMessage(R.string.please_enter_a_name_for_this_scan_your_location_place_etc_)
	    .setView(input)
	    .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
	        public void onClick(DialogInterface dialog, int whichButton) {
	            Editable value = input.getText(); 
	            
	            Intent intent = new Intent(MainActivity.this, ScanService.class); 
	            intent.putExtra("scanname", value.toString());
	            
	            Location loc = mMapFragment.getLastLocation();
	            if (loc != null)
	            {
	            	intent.putExtra("lat",loc.getLatitude());
	            	intent.putExtra("lon",loc.getLongitude());
	            }
	    		//start the background scan
	    		startService(intent);
	    		
	        }
	    }).setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
	        public void onClick(DialogInterface dialog, int whichButton) {
	            // Do nothing.
	        }
	    }).show();
		

	}

	private void doCellLookup(Scan scan) {
		
		CellInfo cInfo = scan.getCellInfos().get(0);		
		new CellLookupOperation().execute(cInfo.getMCC()+"",cInfo.getMNC()+"",cInfo.getLAC()+"",cInfo.getCID()+"");

	}

	private class CellLookupOperation extends AsyncTask<String, Void, String> {
		@Override
		protected String doInBackground(String... params) {

			try {
				String mcc, mnc, lac, cellId;
				
				mcc = params[0];
				mnc = params[1];
				lac = params[2];
				cellId = params[3];
				
				OpenCellIdLookup.findMatchingCell(mcc, mnc, lac, cellId);
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
	public static class MapFragment extends Fragment implements
			LocationListener {

		MapView mMapView;
		Activity mActivity;
		LinearLayout mViewResults;
		View mRootView;
		Location lastLocation;
		
		public MapFragment() {
		}

		public Location getLastLocation ()
		{
			return lastLocation;
		}
		
		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			mRootView = inflater.inflate(R.layout.fragment_main, container,
					false);

			mViewResults = (LinearLayout) mRootView.findViewById(R.id.results);

			mActivity = this.getActivity();

			doLocation();

			return mRootView;
		}

		public void addView(View view, int margin) {
			/**
			 * <Button android:text="Scan Result 1" android:textSize="24sp"
			 * android:textColor="#000000"
			 * 
			 * android:background="#cc999999"
			 * android:layout_height="wrap_content"
			 * android:layout_width="match_parent" android:gravity="center"
			 * android:layout_margin="6dp"
			 */
			
			LayoutParams lp = new LayoutParams(LayoutParams.MATCH_PARENT,
					LayoutParams.WRAP_CONTENT);
			lp.setMargins(margin,margin,margin,margin);
			mViewResults.addView(view, lp);
			
			
		}
		
		public void removeView (View view)
		{
			mViewResults.removeView(view);
		}

		/**
		public void addResult(String text) {
			
			Button btn = new Button(mActivity);
			btn.setText(text);
			btn.setTextSize(24);
			btn.setTextColor(Color.BLACK);
			btn.setPadding(3, 3, 3, 3);
			btn.setBackgroundColor(Color.parseColor("#CC999999"));
			LayoutParams lp = new LayoutParams(LayoutParams.MATCH_PARENT,
					LayoutParams.WRAP_CONTENT);
			lp.setMargins(6, 6, 6, 6);
			mViewResults.addView(btn, lp);
		}
		*/

		protected void setupMap(String lat, String lon, int zoomLevel, View view) {
			
			GeoPoint gp = GeoPoint.fromDoubleString(lat + ',' + lon, ',');
			
			mMapView = (MapView) view.findViewById(R.id.mapview);
			mMapView.getOverlayManager().getTilesOverlay()
					.setOvershootTileCache(300);

			mMapView.setTileSource(TileSourceFactory.MAPNIK);
			mMapView.setBuiltInZoomControls(true);
			mMapView.setMultiTouchControls(true);

			mMapView.getController().setZoom(zoomLevel);
			mMapView.getController().setCenter(
					gp);
			
			SimpleLocationOverlay so = new SimpleLocationOverlay(mActivity);
			so.setLocation(gp);
			
			mMapView.getOverlayManager().add(so);

			mMapView.postInvalidate();
		}
		
		public void addCircleOverlay (String test, double lat, double lon, int level)
		{
			GeoPoint gp = new GeoPoint(lat, lon);
			
			CircleOverlay co = new CircleOverlay(mActivity, gp, level);
			
			mMapView.getOverlayManager().add(co);

			mMapView.postInvalidate();
		}

		public void addCircleOverlay (String test, int level)
		{
			addCircleOverlay (test, lastLocation.getLongitude(), lastLocation.getLatitude(), level);
		}
		
		private LocationClient locationclient;
		private LocationRequest locationrequest;

		private void doLocation() {
			locationclient = new LocationClient(this.getActivity()
					.getApplicationContext(), new ConnectionCallbacks() {

				@Override
				public void onConnected(Bundle arg0) {

					locationrequest = LocationRequest.create();
					locationrequest.setInterval(100);

					locationclient.requestLocationUpdates(locationrequest, (LocationListener) MapFragment.this);
				}

				@Override
				public void onDisconnected() {

				}
			}, new OnConnectionFailedListener() {

				@Override
				public void onConnectionFailed(ConnectionResult arg0) {

					Log.d(TAG,
							"Could not connect to Google Play Services (location provider)");

				}
			});
			locationclient.connect();

		}

		@Override
		public void onLocationChanged(Location loc) {

			if (mMapView == null)
				setupMap(loc.getLatitude() + "", loc.getLongitude() + "",
						DEFAULT_MAP_ZOOM, mRootView);

			lastLocation = loc;
		}

		class CircleOverlay extends SimpleLocationOverlay
		{
			private GeoPoint gp;
			private Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
			private int radius;
			
			public CircleOverlay(Context ctx, GeoPoint gp, int radius) {
				super(ctx);
				this.gp = gp;
				this.radius = radius;

				paint.setColor(Color.GREEN);
				paint.setAlpha(50);
			}

			@Override
			public void draw(Canvas canvas, MapView map, boolean shadow) {
				Point mapCenterPoint = new Point();
				 map.getProjection().toPixels(gp, mapCenterPoint);
				 canvas.drawCircle(mapCenterPoint.x, mapCenterPoint.y, radius, this.paint);
			}
			
		}


	}

	private BroadcastReceiver receiver = new BroadcastReceiver() {

	    @Override
	    public void onReceive(Context context, Intent intent) {
	      Bundle bundle = intent.getExtras();
	      if (bundle != null) {
	    	
	    	  int cellId = bundle.getInt("cid");
	    	  int dbm = bundle.getInt("dbm")*2;
	       
	    	  mMapFragment.addCircleOverlay(cellId+"", mMapFragment.getLastLocation().getLatitude(), mMapFragment.getLastLocation().getLongitude(), dbm);
	      }
	    }
	  };
}
