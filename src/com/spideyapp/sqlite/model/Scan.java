package com.spideyapp.sqlite.model;

import java.util.List;

import com.spideyapp.CellIdentity;

public class Scan {
	private long _id;
	private String _location;
	private double _latitude;
	private double _longitude;
	private String _created_at;

	public Scan() {
	}

	public Scan(String location) {
		this._location = location;
	}

	public Scan(long id, String location, float latitude, float longitude, String created_at) {
		this._id = id;
		this._location = location;
		this._latitude = latitude;
		this._longitude = longitude;
		this._created_at = created_at;
	}

	public void setId(int id) {
		this._id = id;
	}

	public void setLocation(String location) {
		this._location = location;
	}

	public void setLatitude(double latitude) {
		this._latitude = latitude;
	}

	public void setLongitude(double longitude) {
		this._longitude = longitude;
	}

	public void setCreatedAt(String created_at){
        this._created_at = created_at;
    }

	// Getters

	public long getId() {
		return this._id;
	}

	public String getLocation() {
		return this._location;
	}

	public double getLatitude() {
		return this._latitude;
	}

	public double getLongitude() {
		return this._longitude;
	}

	public String getCreatedAt() {
		return this._created_at;
	}
}