package com.spideyapp.sqlite.model;

public class CellInfo {
	private long _id;
	private long _scan_id;
	private int _cid;
	private int _lac;
	private int _mcc;
	private int _mnc;
	private String _created_at;

	public CellInfo() {

	}

	public CellInfo(int cid, int lac, int mcc, int mnc) {
		this._cid = cid;
		this._lac = lac;
		this._mcc = mcc;
		this._mnc = mnc;
	}

	// Setters

	public void setId(int id) {
		this._id = id;
	}

	public void setCID(int cid) {
		this._cid = cid;
	}

	public void setLAC(int lac) {
		this._lac = lac;
	}

	public void setMCC(int mcc) {
		this._mcc = mcc;
	}

	public void setMNC(int mnc) {
		this._mnc = mnc;
	}
	
	public void setCreatedAt(String created_at){
        this._created_at = created_at;
    }

	// Getters

	public long getId() {
		return this._id;
	}

	public long getScanID() {
		return this._scan_id;
	}

	public int getCID() {
		return this._cid;
	}

	public int getLAC() {
		return this._lac;
	}

	public int getMCC() {
		return this._mcc;
	}

	public int getMNC() {
		return this._mnc;
	}
	
	public String getCreatedAt() {
		return this._created_at;
	}
}