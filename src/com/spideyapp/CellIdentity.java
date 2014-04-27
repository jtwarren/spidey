package com.spideyapp;

import org.json.JSONException;
import org.json.JSONObject;

public class CellIdentity {
	private long _id;
	private long _scan_id;
	private int _cid;
	private int _lac;
	private int _mcc;
	private int _mnc;

	public CellIdentity() {

	}

	public CellIdentity(long scan_id, int cid, int lac, int mcc, int mnc) {
		this._scan_id = scan_id;
		this._cid = cid;
		this._lac = lac;
		this._mcc = mcc;
		this._mnc = mnc;
	}

	public CellIdentity(long id, long scan_id, JSONObject cell_info) {
		this._id = id;
		this._scan_id = scan_id;

		try {
			this._cid = cell_info.getInt("_cid");
			this._lac = cell_info.getInt("_lac");
			this._mcc = cell_info.getInt("_mcc");
			this._mnc = cell_info.getInt("_mnc");
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	public JSONObject getJSON() {
		JSONObject cell_info = new JSONObject();
		try {
			cell_info.put("id", this._id);
			cell_info.put("scan_id", this._scan_id);
			cell_info.put("cid", this._cid);
			cell_info.put("lac", this._lac);
			cell_info.put("mcc", this._mcc);
			cell_info.put("mnc", this._mnc);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return cell_info;
	}
	
	public void setID(int id) {
		this._id = id;
	}

	public long getID() {
		return this._id;
	}
	
	public void setScanID(int scan_id) {
		this._scan_id = scan_id;
	}

	public long getScanID() {
		return this._scan_id;
	}
	
	public void setCID(int cid) {
		this._cid = cid;
	}

	public int getCID() {
		return this._cid;
	}
	
	public void setLAC(int lac) {
		this._lac = lac;
	}

	public int getLAC() {
		return this._lac;
	}
	
	public void setMCC(int mcc) {
		this._mcc = mcc;
	}

	public int getMCC() {
		return this._mcc;
	}
	
	public void setMNC(int mnc) {
		this._mnc = mnc;
	}

	public int getMNC() {
		return this._mnc;
	}
}
