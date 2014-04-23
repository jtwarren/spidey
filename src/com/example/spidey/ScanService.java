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
import android.telephony.TelephonyManager;
import android.util.Log;

public class ScanService extends Service {
	
	private TelephonyManager telephonyManager;

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		this.telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);

		if (Build.VERSION.SDK_INT == Build.VERSION_CODES.JELLY_BEAN_MR1)
			startScan();
		

		return Service.START_NOT_STICKY;
	}
	
	@TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1) private void startScan ()
	{
		List<CellInfo> cellInfos = (List<CellInfo>) this.telephonyManager.getAllCellInfo();
		
		// TODO: better error handling of null cellinfos
		if (cellInfos != null) {
			for (CellInfo cellInfo : cellInfos) {

				CellInfoGsm cellInfoGsm = (CellInfoGsm) cellInfo;

				CellIdentityGsm cellIdentity = cellInfoGsm.getCellIdentity();
				CellSignalStrengthGsm cellSignalStrengthGsm = cellInfoGsm.getCellSignalStrength();

				Log.d("cell", "registered: " + cellInfoGsm.isRegistered());
				Log.d("cell", cellIdentity.toString());
				Log.d("cell", cellSignalStrengthGsm.toString());
				
			}
		}
		
	}

	@Override
	public IBinder onBind(Intent intent) {
		// TODO for communication return IBinder implementation
		return null;
	}
}
