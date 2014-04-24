package com.example.spidey;

import java.util.List;

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
	private ScanDatabaseHandler scanDbHandler = new ScanDatabaseHandler(this, null, null, 3);
	private CellIdentityHandler cellDbHandler = new CellIdentityHandler(this, null, null, 3);

	// TODO: Think about running scans multiple times over a minute or two.

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		this.telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1)
			startScan();

		else
			startSimpleScan();

		return Service.START_NOT_STICKY;
	}

	@TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
	private void startScan() {
		logMessage("starting tower scan... ");
		Scan scan = new Scan();

		// TODO: Get location from user?
		scan.setLocation("MIT");

		// TODO: use actual GPS Coordinates
		scan.setLatitude(42.360091);
		scan.setLongitude(-71.09416);

		scan.setTimestamp(System.currentTimeMillis());

		long scan_id = scanDbHandler.addScan(scan);

		List<CellInfo> cellInfos = (List<CellInfo>) this.telephonyManager
				.getAllCellInfo();

		// TODO: better error handling of null cellinfos
		if (cellInfos != null) {
			for (CellInfo cellInfo : cellInfos) {

				if (cellInfo instanceof CellInfoGsm)
				{
					CellInfoGsm cellInfoGsm = (CellInfoGsm) cellInfo;
	
					CellIdentityGsm cellIdentity = cellInfoGsm.getCellIdentity();
					CellSignalStrengthGsm cellSignalStrengthGsm = cellInfoGsm.getCellSignalStrength();
	
					CellIdentity cell = new CellIdentity(scan_id,
							cellIdentity.getCid(), cellIdentity.getLac(),
							cellIdentity.getMcc(), cellIdentity.getMnc());
					
					cellDbHandler.addTower(cell);
	
					logMessage("registered: " + cellInfoGsm.isRegistered()
							+ ",cellId: " + cellIdentity.toString()
							+ ",cellSignalStrength: "
							+ cellSignalStrengthGsm.toString());
				}
			}
		}

	}

	private void startSimpleScan() {
		logMessage("starting tower scan (old stylie)... ");

		String mccMnc = telephonyManager.getNetworkOperator();
		
		if (telephonyManager.getPhoneType() == TelephonyManager.PHONE_TYPE_GSM) {
		    final GsmCellLocation location = (GsmCellLocation) telephonyManager.getCellLocation();
		    if (location != null) {
		        logMessage("mccMnc: " + mccMnc + ",LAC: " + location.getLac() + " CID: " + location.getCid());
		    }
		}
		
		List<NeighboringCellInfo> cellInfos = telephonyManager
				.getNeighboringCellInfo();
		if (cellInfos != null) {
			for (NeighboringCellInfo cellInfo : cellInfos) {

				logMessage("cell-id:" + cellInfo.getCid() + ",lac: "
						+ cellInfo.getLac() + ",Received Signal Strength: "
						+ cellInfo.getRssi() + ",LAC: " + cellInfo.getLac());

			}
		}
	}

	private void logMessage(String msg) {
		Log.d(TAG, msg);
		Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
	}

	@Override
	public IBinder onBind(Intent intent) {
		// TODO for communication return IBinder implementation
		return null;
	}
}
