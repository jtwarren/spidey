package com.example.spidey;

public class Scan {
	private int _id;
	private String _Scanname;

	public Scan() {
	}

	public Scan(int id, String Scanname) {
		this._id = id;
		this._Scanname = Scanname;
	}

	public Scan(String Scanname) {
		this._Scanname = Scanname;
	}

	public void setID(int id) {
		this._id = id;
	}

	public int getID() {
		return this._id;
	}

	public void setScanName(String Scanname) {
		this._Scanname = Scanname;
	}

	public String getScanName() {
		return this._Scanname;
	}
}