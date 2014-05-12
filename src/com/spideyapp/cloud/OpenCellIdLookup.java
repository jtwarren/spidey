package com.spideyapp.cloud;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

import android.util.Log;

public class OpenCellIdLookup {

	//http://opencellid.org/measure/getByCellData?mcc=310&mnc=4384&lac=65&cellid=2578&full=true&format=csv/
		
	private final static String QUERY_BASE = "http://opencellid.org/measure/getByCellData?";
	private final static String QUERY_END = "full=true&format=csv/";
	
	public static String findMatchingCell (String mcc, String mnc, String lac, String cellId) throws IOException
	{
		StringBuffer query = new StringBuffer();
		query.append(QUERY_BASE);
		query.append("mcc=").append(mcc).append('&');
		query.append("mnc=").append(mnc).append('&');
		query.append("lac=").append(lac).append('&');
		query.append("cellid=").append(cellId).append('&');
		query.append(QUERY_END);
		
		URL url = new URL(query.toString());
		URLConnection conn = url.openConnection();
		
		BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
		StringBuffer result = new StringBuffer();
		String line = null;
		
		while ((line = reader.readLine())!=null)
		{
			result.append(line).append('\n');
		}
		
		reader.close();
		
		return result.toString();
		
	}
	
	public static void main (String[] args) throws IOException
	{
	
		String result = OpenCellIdLookup.findMatchingCell("310","4384","65","2578");
		
		System.out.println(result);
	}
	
	//id	mcc	mnc	lac	cellid	lat	lon	created_at	measured_at	signal	rating	speed	direction
//	528ffc2bec5ea6166f324ec8	310	4384	65	2578	41.88437	87.67694	1309934773000	1309934773000	0	0	0	0

}
