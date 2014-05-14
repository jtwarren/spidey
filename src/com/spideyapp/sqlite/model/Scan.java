package com.spideyapp.sqlite.model;

import java.util.ArrayList;
import java.util.List;

public class Scan {
	private long mId;
	private String mLocation;
	private double mLatitude;
	private double mLongitude;
	private String mCreatedAt;
	private List<CellInfo> mCellInfos = new ArrayList<CellInfo>();

	public Scan() {
	}

	public Scan(String location) {
		this.mLocation = location;
	}

	public Scan(long id, String location, float latitude, float longitude, String createdAt) {
		this.mId = id;
		this.mLocation = location;
		this.mLatitude = latitude;
		this.mLongitude = longitude;
		this.mCreatedAt = createdAt;
	}

	public void setId(int id) {
		this.mId = id;
	}

	public void setLocation(String location) {
		this.mLocation = location;
	}

	public void setLatitude(double latitude) {
		this.mLatitude = latitude;
	}

	public void setLongitude(double longitude) {
		this.mLongitude = longitude;
	}

	public void setCreatedAt(String createdAt){
        this.mCreatedAt = createdAt;
    }
	
	public void addCellInfo(CellInfo cellInfo) {
		this.mCellInfos.add(cellInfo);
	}

	public void setCellInfos (List<CellInfo> mCellInfos)
	{
		this.mCellInfos = mCellInfos;
	}
 

	public long getId() {
		return this.mId;
	}

	public String getLocation() {
		return this.mLocation;
	}

	public double getLatitude() {
		return this.mLatitude;
	}

	public double getLongitude() {
		return this.mLongitude;
	}

	public String getCreatedAt() {
		return this.mCreatedAt;
	}
	
	public List<CellInfo> getCellInfos() {
		return this.mCellInfos;
	}
	
	public String toString ()
	{
		StringBuffer result = new StringBuffer();
		
		result.append("Location: ").append(getLocation()).append("\n");
		result.append("Created: ").append(getCreatedAt()).append("\n");
		result.append("Latitude: ").append(getLatitude()).append("\n");
		result.append("Longitude: ").append(getLongitude()).append("\n");
		result.append("\n\n");
		
		for (CellInfo ci : mCellInfos)
		{
			result.append(ci.toString()).append("\n");
		}
		
		
		return result.toString();
		
	}
}