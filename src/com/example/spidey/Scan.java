package com.example.spidey;

import java.sql.Timestamp;

public class Scan {
	private int _id;
	private String _location;
	private float _latitude;
	private float _longitude;
	private Timestamp _timestamp;

	public Scan() {
	}
	
	public Scan(String location) {
		this._location = location;
	}

	public Scan(int id, String location, float latitude, float longitude, Timestamp timestamp) {
		this._id = id;
		this._location = location;
		this._latitude = latitude;
		this._longitude = longitude;
		this._timestamp = timestamp;
	}

	public void setID(int id) {
		this._id = id;
	}

	public int getID() {
		return this._id;
	}
	
	public void setLocation(String location) {
		this._location = location;
	}

	public String getLocation() {
		return this._location;
	}
	
	public void setLatitude(float latitude) {
		this._latitude = latitude;
	}

	public float getLatitude() {
		return this._latitude;
	}
	
	public void setLongitude(float longitude) {
		this._longitude = longitude;
	}

	public float getLongitude() {
		return this._longitude;
	}
	
	public void setTimestamp(Timestamp timestamp) {
		this._timestamp = timestamp;
	}

	public Timestamp getTimestamp() {
		return this._timestamp;
	}
}