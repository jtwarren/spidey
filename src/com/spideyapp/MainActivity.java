package com.spideyapp;

import java.io.IOException;

import net.hockeyapp.android.CrashManager;
import net.hockeyapp.android.UpdateManager;

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
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.telephony.TelephonyManager;
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
import com.spideyapp.sqlite.helper.DatabaseHelper;
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
		Intent i = getIntent();
		
		if (i.hasExtra("scan_id"))
		{
			final long scan_id = i.getLongExtra("scan_id", -1);
			final Handler handler = new Handler();
			handler.postDelayed(new Runnable() {
			    @Override
			    public void run() {
			        // Do something after 5s = 5000ms
			    	showScanMap(scan_id);
			    }
			}, 2000);
			
			
		}
		else
			showWelcome();
	}
	
	private void showScanMap (long scan_id)
	{
		
        
        Scan scan = DatabaseHelper.getInstance(this).getScan(scan_id);
        
        mMapFragment.setLocation(scan.getLatitude()+"", scan.getLongitude()+"");
        
        for (CellInfo ci : scan.getCellInfos())
        {
        	mMapFragment.addCircleOverlay(ci.getCID()+"", scan.getLatitude(),scan.getLongitude(),ci.getDBM());
        }
        
	}
	
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

		 checkForCrashes();
		 checkForUpdates();
	}
	
	@Override
	  protected void onPause() {
	    super.onPause();
	    unregisterReceiver(receiver);
	  }


	/**HockeyAPp**/
	  private void checkForCrashes() {
	    CrashManager.register(this, "86494125c282a86b3f7f30ac76bd3129");
	  }

	  private void checkForUpdates() {
	    // Remove this for store builds!
	    UpdateManager.register(this, "86494125c282a86b3f7f30ac76bd3129");
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
		
		if (id == R.id.action_scan) {
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

	
	private void runScan() {

		if (this.isProperNetworkState())
		{

			mMapFragment.startLocationLookup();
			mMapFragment.clearOverlays();
			
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
		else
		{
			
			new AlertDialog.Builder(this)
		    .setTitle(R.string.required_mobile_settings)
		    .setMessage(R.string.please_uncheck_data_enabled_and_set_preferred_network_type_to_2g_on_the_mobile_network_settings_screen_click_ok_to_open_it_now_)		    
		    .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
		        public void onClick(DialogInterface dialog, int whichButton) {
		        	Intent intent = new Intent();
		        	intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		        	intent.setAction(android.provider.Settings.ACTION_DATA_ROAMING_SETTINGS);
		        	startActivity(intent);
		    		
		        }
		    }).setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
		        public void onClick(DialogInterface dialog, int whichButton) {
		            // Do nothing.
		        }
		    }).show();

	      
		}
		

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
		SimpleLocationOverlay mMapMan;
		
		private LocationClient locationclient;
		private LocationRequest locationrequest;
		
		private String defaultLatitude = "0";
		private String defaultLongitude = "0";
		
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

			setupMap(defaultLatitude,defaultLongitude,DEFAULT_MAP_ZOOM, mRootView);
			
			startLocationLookup();

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
		
		protected void setupMap(String lat, String lon, int zoomLevel, View view) {
			
			GeoPoint gp = GeoPoint.fromDoubleString(lat + ',' + lon, ',');
			
			if (mMapView == null)
			{
				mMapView = (MapView) view.findViewById(R.id.mapview);
				mMapView.getOverlayManager().getTilesOverlay()
						.setOvershootTileCache(300);
	
				mMapView.setTileSource(TileSourceFactory.MAPNIK);
				mMapView.setBuiltInZoomControls(true);
				mMapView.setMultiTouchControls(true);
			}
			
			mMapView.getController().setZoom(zoomLevel);
			mMapView.getController().setCenter(
					gp);
			
			mMapMan = new SimpleLocationOverlay(mActivity);
			mMapMan.setLocation(gp);
			
			mMapView.getOverlayManager().add(mMapMan);

			mMapView.postInvalidate();
		}
		
		public void addCircleOverlay (String label, double lat, double lon, int level)
		{
			GeoPoint gp = new GeoPoint(lat, lon);
			
			CircleOverlay co = new CircleOverlay(mActivity, label, gp, level);
			
			mMapView.getOverlayManager().add(co);

			mMapView.postInvalidate();
		}
		
		public void clearOverlays ()
		{
			mMapView.getOverlayManager().clear();
			mMapView.getOverlayManager().add(mMapMan);
		}

		public void addCircleOverlay (String test, int level)
		{
			addCircleOverlay (test, lastLocation.getLongitude(), lastLocation.getLatitude(), level);
		}
		
		public void setLocation (String latitude, String longitude)		
		{
			
			GeoPoint gp = GeoPoint.fromDoubleString(latitude + ',' + longitude, ',');
			
			if (mMapView != null)
			{
				mMapView.getController().setCenter(gp);
				mMapMan.setLocation(gp);
			}
			else
			{
				defaultLatitude = latitude;
				defaultLongitude = longitude;
			}
		}
		
		public void startLocationLookup() {
			
			if (locationclient == null)
			{
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
			}
			
			if (!locationclient.isConnected())
				locationclient.connect();

		}

		@Override
		public void onLocationChanged(Location loc) {

			setLocation(loc.getLatitude()+"",loc.getLongitude()+"");
			lastLocation = loc;
		}

		class CircleOverlay extends SimpleLocationOverlay
		{
			private GeoPoint gp;
			private Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
			private int radius;
			private String label;
			
			public CircleOverlay(Context ctx, String label, GeoPoint gp, int radius) {
				super(ctx);
				this.gp = gp;
				this.radius = radius;
				this.label = label;
				
				paint.setAlpha(60);
				
			}

			@Override
			public void draw(Canvas canvas, MapView map, boolean shadow) {
				Point mapCenterPoint = new Point();
				 map.getProjection().toPixels(gp, mapCenterPoint);
				
				 GeoPoint gp2 = gp.destinationPoint(radius*30, 60);
				 Point mapCirclePoint = new Point();
				 map.getProjection().toPixels(gp2, mapCirclePoint);
				 
				int newRadius = mapCirclePoint.x - mapCenterPoint.x;
				
				// double metersPerPixel = TileSystem.GroundResolution(map.getMapCenter().getLatitude(), map.getZoomLevel());
				 
				 //int newRadius = (int)(metersPerPixel/radius);				 
				 
				 paint.setStyle(Paint.Style.STROKE);
				 paint.setColor(Color.RED);
				 paint.setStrokeWidth(5);
				 canvas.drawCircle(mapCenterPoint.x, mapCenterPoint.y, newRadius, paint);
				 
				 paint.setStyle(Paint.Style.FILL_AND_STROKE);
				 paint.setColor(Color.DKGRAY);
				 paint.setStrokeWidth(2);
				 paint.setTextSize(40);
				 canvas.drawText(label, mapCenterPoint.x, mapCenterPoint.y+newRadius, paint);
				 
			}
			
		}


	}

	private BroadcastReceiver receiver = new BroadcastReceiver() {

	    @Override
	    public void onReceive(Context context, Intent intent) {
	      Bundle bundle = intent.getExtras();
	      if (bundle != null) {
	    	
	    	  int cellId = bundle.getInt("cid");
	    	  int dbm = bundle.getInt("dbm");
	       
	    	  mMapFragment.addCircleOverlay(cellId+"", mMapFragment.getLastLocation().getLatitude(), mMapFragment.getLastLocation().getLongitude(), dbm);
	      }
	    }
	  };
	  
	  //we want 3G/LTE off
	  private boolean isProperNetworkState ()
	  {
		  ConnectivityManager mgr = (ConnectivityManager)getSystemService(CONNECTIVITY_SERVICE);

	      //boolean is3G = mgr.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).isConnectedOrConnecting();
	      
		  NetworkInfo[] networkInfos = mgr.getAllNetworkInfo();
		  
		  for (NetworkInfo netInfo : networkInfos)
		  {
		      if(netInfo.getType()==ConnectivityManager.TYPE_MOBILE)
		      {
		    	  int subType = netInfo.getSubtype();
		    	  
		    	  switch(subType){
		          case TelephonyManager.NETWORK_TYPE_1xRTT:
		              return false; // ~ 50-100 kbps
		          case TelephonyManager.NETWORK_TYPE_CDMA:
		              return false; // ~ 14-64 kbps
		          case TelephonyManager.NETWORK_TYPE_EDGE:
		              return true; // ~ 50-100 kbps
		          case TelephonyManager.NETWORK_TYPE_EVDO_0:
		              return false; // ~ 400-1000 kbps
		          case TelephonyManager.NETWORK_TYPE_EVDO_A:
		              return false; // ~ 600-1400 kbps
		          case TelephonyManager.NETWORK_TYPE_GPRS:
		              return true; // ~ 100 kbps
		          case TelephonyManager.NETWORK_TYPE_HSDPA:
		              return false; // ~ 2-14 Mbps
		          case TelephonyManager.NETWORK_TYPE_HSPA:
		              return false; // ~ 700-1700 kbps
		          case TelephonyManager.NETWORK_TYPE_HSUPA:
		              return false; // ~ 1-23 Mbps
		          case TelephonyManager.NETWORK_TYPE_UMTS:
		              return false; // ~ 400-7000 kbps
		          /*
		           * Above API level 7, make sure to set android:targetSdkVersion 
		           * to appropriate level to use these
		           */
		          case TelephonyManager.NETWORK_TYPE_EHRPD: // API level 11 
		              return false; // ~ 1-2 Mbps
		          case TelephonyManager.NETWORK_TYPE_EVDO_B: // API level 9
		              return false; // ~ 5 Mbps
		          case TelephonyManager.NETWORK_TYPE_HSPAP: // API level 13
		              return false; // ~ 10-20 Mbps
		          case TelephonyManager.NETWORK_TYPE_IDEN: // API level 8
		              return false; // ~25 kbps 
		          case TelephonyManager.NETWORK_TYPE_LTE: // API level 11
		              return false; // ~ 10+ Mbps
		          // Unknown
		          case TelephonyManager.NETWORK_TYPE_UNKNOWN:
		          default:
		              return true;
		          }
		      }
		  }
	  
	      return true;
	  }
	  
	  
	  
}
