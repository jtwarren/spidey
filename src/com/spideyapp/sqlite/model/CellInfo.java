package com.spideyapp.sqlite.model;

public class CellInfo {
	private long _id;
	private long _scan_id;
	private int _cid;
	private int _lac;
	private int _mcc;
	private int _mnc;
	private int _dbm;
	
	private String _created_at;

	public CellInfo() {

	}

	public CellInfo(int cid, int lac, int mcc, int mnc, int dbm) {
		this._cid = cid;
		this._lac = lac;
		this._mcc = mcc;
		this._mnc = mnc;
		this._dbm = dbm;
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

	public void setCreatedAt(String created_at) {
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

	public int getDBM() {
		return _dbm;
	}

	public void setDBM(int _dbm) {
		this._dbm = _dbm;
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

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + _cid;
		result = prime * result + _lac;
		result = prime * result + _mcc;
		result = prime * result + _mnc;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		CellInfo other = (CellInfo) obj;
		if (this._cid != other.getCID())
			return false;
		if (this._lac != other.getLAC())
			return false;
		if (this._mcc != other.getMCC())
			return false;
		if (this._mnc != other.getMNC())
			return false;
		return true;
	}
	
	public String toString ()
	{
		StringBuffer result = new StringBuffer();
		
		result.append("Cell Identifier (CID): " + _cid).append("\n");
		result.append("Location Code (LAC): " + _lac).append("\n");
		result.append("Country Code (MCC): " + _mcc).append("\n");
		result.append("Network Code (MNC): " + _mnc).append("\n");
		result.append("Signal Strength (DBM): " + _dbm).append("\n");
		
		
		return result.toString();
	}
}