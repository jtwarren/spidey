package com.spideyapp;

import java.util.List;

import com.spideyapp.sqlite.helper.DatabaseHelper;
import com.spideyapp.sqlite.model.Scan;

import android.annotation.TargetApi;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.telephony.CellIdentityGsm;
import android.telephony.CellInfo;
import android.telephony.CellInfoGsm;
import android.telephony.CellSignalStrengthGsm;
import android.telephony.NeighboringCellInfo;
import android.telephony.TelephonyManager;
import android.telephony.gsm.GsmCellLocation;
import android.util.Log;
import android.widget.Toast;

public class ScanService extends Service {

	private TelephonyManager telephonyManager;
	private final static String TAG = "SpideyScan";
	private DatabaseHelper db;

	private String lastScanName;
	private double lastScanLat;
	private double lastScanLon;

	public final static boolean DEBUG = true;
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		this.telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);

		lastScanName = intent.getStringExtra("scanname");
		lastScanLat = intent.getDoubleExtra("lat", 0.0);
		lastScanLon = intent.getDoubleExtra("lon", 0.0);
		
		db = DatabaseHelper.getInstance(getApplicationContext());

		/**
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1)
			startScan();
		else
			startSimpleScan();
			*/
		
		startLegacyScan();

		return Service.START_NOT_STICKY;
	}

	@TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
	private void startScan() {
		
		logMessage("starting tower scan... ");
		Scan scan = new Scan();

		// TODO: Get location from user?
		scan.setLocation(lastScanName);

		// TODO: use actual GPS Coordinates
		scan.setLatitude(lastScanLat);
		scan.setLongitude(lastScanLon);

		long scan_id = db.createScan(scan);

		List<CellInfo> cellInfos = (List<CellInfo>) this.telephonyManager
				.getAllCellInfo();

		// TODO: better error handling of null cellinfos
		if (cellInfos != null) {
			for (CellInfo cellInfo : cellInfos) {

				if (cellInfo instanceof CellInfoGsm) {
					CellInfoGsm cellInfoGsm = (CellInfoGsm) cellInfo;

					CellIdentityGsm cellIdentity = cellInfoGsm
							.getCellIdentity();
					CellSignalStrengthGsm cellSignalStrengthGsm = cellInfoGsm
							.getCellSignalStrength();

					int dbmLevel = cellSignalStrengthGsm.getDbm();
					
					com.spideyapp.sqlite.model.CellInfo cell = new com.spideyapp.sqlite.model.CellInfo(
							cellIdentity.getCid(), cellIdentity.getLac(),
							cellIdentity.getMcc(), cellIdentity.getMnc(),dbmLevel);

					db.createCell(cell, scan_id);

					shareCellInfo (cell);
					
				}
			}
		}

	}
	
	@TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
	private void startDummyScan() {
		db = DatabaseHelper.getInstance(getApplicationContext());
		
		
		
		logMessage("starting tower scan... ");
		Scan scan = new Scan();

		// TODO: Get location from user?
		scan.setLocation(lastScanName);

		// TODO: use actual GPS Coordinates
		scan.setLatitude(lastScanLat);
		scan.setLongitude(lastScanLon);

		long scan_id = db.createScan(scan);

		for (int i = 0; i < 5; i++)
		{
			com.spideyapp.sqlite.model.CellInfo cell = new com.spideyapp.sqlite.model.CellInfo(
					i, 100,
					200, 300, 2*i);

			db.createCell(cell, scan_id);

			shareCellInfo (cell);
					
					
			
		}

	}


	private void startLegacyScan() {
		logMessage("starting tower scan (old stylie)... ");

		Scan scan = new Scan();

		// TODO: Get location from user?
		scan.setLocation(lastScanName);

		// TODO: use actual GPS Coordinates
		scan.setLatitude(lastScanLat);
		scan.setLongitude(lastScanLon);

		long scan_id = db.createScan(scan);
		
		String networkOperator = telephonyManager.getNetworkOperator();
		int mcc = 0;
		int mnc = 0;
		int level = 0;
		
	    if (networkOperator != null && networkOperator.length() > 0) {
	    	if (networkOperator.length() > 3)
	    	{
	    		mcc = Integer.parseInt(networkOperator.substring(0, 3));
	    		mnc = Integer.parseInt(networkOperator.substring(3));
	    	}
	    	else
	    		mcc = Integer.parseInt(networkOperator);
	    }			    			   
	
		if (telephonyManager.getPhoneType() == TelephonyManager.PHONE_TYPE_GSM) {
			final GsmCellLocation location = (GsmCellLocation) telephonyManager
					.getCellLocation();
			if (location != null) {
				
				
				com.spideyapp.sqlite.model.CellInfo cell = new com.spideyapp.sqlite.model.CellInfo(
						location.getCid(), location.getLac(),
						mcc, mnc, level);

				db.createCell(cell, scan_id);

				shareCellInfo (cell);
				
				
			}
		}

		List<NeighboringCellInfo> cellInfos = telephonyManager
				.getNeighboringCellInfo();
		if (cellInfos != null) {
			for (NeighboringCellInfo cellInfo : cellInfos) {

				
				com.spideyapp.sqlite.model.CellInfo cell = new com.spideyapp.sqlite.model.CellInfo(
						cellInfo.getCid(), cellInfo.getLac(),
						mcc, mnc, cellInfo.getRssi());
				
				db.createCell(cell, scan_id);

				shareCellInfo (cell);
			}
		}
	}

	private void logMessage(String msg) {
		
		if (DEBUG)
			Log.d(TAG, msg);
		
		Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
	}

	@Override
	public IBinder onBind(Intent intent) {
		// TODO for communication return IBinder implementation
		return null;
	}
	
	public static final String NOTIFICATION = "com.spideyapp.service.receiver";
	
	 private void shareCellInfo(com.spideyapp.sqlite.model.CellInfo cellInfo) {
		    Intent intent = new Intent(NOTIFICATION);
		    intent.putExtra("cid",cellInfo.getCID());
		    intent.putExtra("dbm",cellInfo.getDBM());
		    
		  
		    sendBroadcast(intent);
		  }
}
