package com.spideyapp.cloud;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.StringTokenizer;

import com.spideyapp.sqlite.model.CellInfo;

public class OpenCellIdLookup {

	//http://opencellid.org/measure/getByCellData?mcc=310&mnc=4384&lac=65&cellid=2578&full=true&format=csv/
		
	private final static String QUERY_BASE = "http://opencellid.org/measure/getByCellData?";
	private final static String QUERY_END = "full=true&format=csv/";
	
	public static CellInfo findMatchingCell (String mcc, String mnc, String lac, String cellId) throws IOException
	{
		CellInfo result = null;
		
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
		
		String line = null;
		
		//skip header line
		//id	mcc	mnc	lac	cellid	lat	lon	created_at	measured_at	signal	rating	speed	direction
		line = reader.readLine();

//		528ffc2bec5ea6166f324ec8	310	4384	65	2578	41.88437	87.67694	1309934773000	1309934773000	0	0	0	0
		while ((line = reader.readLine())!=null)
		{
			result = new CellInfo();
			StringTokenizer st = new StringTokenizer(line," ");
			
			st.nextToken();//id
			st.nextToken();//mcc
			st.nextToken();//mnc
			st.nextToken();//cellid
			st.nextToken();//lat
			st.nextToken();//lon
			st.nextToken();//created_at
			st.nextToken();//measured_at
			st.nextToken();//signal
			st.nextToken();//rating
			st.nextToken();//speed
			st.nextToken();//direction
			
		}
		
		reader.close();
		
		return result;
		
	}
	
	public static void main (String[] args) throws IOException
	{
	
		CellInfo result = OpenCellIdLookup.findMatchingCell("310","4384","65","2578");
		
	}
	



}
