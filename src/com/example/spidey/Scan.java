package com.example.spidey;


public class Scan {
	private long _id;
	private String _location;
	private double _latitude;
	private double _longitude;
	private long _timestamp;

	public Scan() {
	}
	
	public Scan(String location) {
		this._location = location;
	}

	public Scan(long id, String location, float latitude, float longitude, long timestamp) {
		this._id = id;
		this._location = location;
		this._latitude = latitude;
		this._longitude = longitude;
		this._timestamp = timestamp;
	}

	public void setID(int id) {
		this._id = id;
	}

	public long getID() {
		return this._id;
	}
	
	public void setLocation(String location) {
		this._location = location;
	}

	public String getLocation() {
		return this._location;
	}
	
	public void setLatitude(double latitude) {
		this._latitude = latitude;
	}

	public double getLatitude() {
		return this._latitude;
	}
	
	public void setLongitude(double longitude) {
		this._longitude = longitude;
	}

	public double getLongitude() {
		return this._longitude;
	}
	
	public void setTimestamp(long timestamp) {
		this._timestamp = timestamp;
	}

	public long getTimestamp() {
		return this._timestamp;
	}
}