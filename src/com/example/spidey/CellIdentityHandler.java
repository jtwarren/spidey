package com.example.spidey;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class CellIdentityHandler extends SQLiteOpenHelper {

	private static final int DATABASE_VERSION = 4;
	private static final String DATABASE_NAME = "cellDB.db";
	private static final String TABLE_CELL_INFO = "cell_identity";
	private static final String TABLE_SCANS = "scans";

	public static final String COLUMN_ID = "_id";
	public static final String COLUMN_SCAN_ID = "scan_id";
	public static final String COLUMN_INFO = "tower_info";

	public CellIdentityHandler(Context context, String name,
			CursorFactory factory, int version) {
		super(context, DATABASE_NAME, factory, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		String CREATE_CELL_INFO_TABLE = "CREATE TABLE " + TABLE_CELL_INFO + "("
				+ COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," + COLUMN_SCAN_ID
				+ " INTEGER, " + COLUMN_INFO + " TEXT, " + " FOREIGN KEY ("
				+ COLUMN_SCAN_ID + ")" + "REFERENCES " + TABLE_SCANS + " ("
				+ COLUMN_ID + "));";
		db.execSQL(CREATE_CELL_INFO_TABLE);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_CELL_INFO);
		onCreate(db);
	}
	
	
	public void addTower(CellIdentity cell) {
		ContentValues values = new ContentValues();
		values.put(COLUMN_SCAN_ID, cell.getScanID());
		values.put(COLUMN_INFO, cell.getJSON().toString());
		
		SQLiteDatabase db = this.getWritableDatabase();

		db.insert(TABLE_CELL_INFO, null, values);
		db.close();
	}
	
	public List<CellIdentity> findTowersForScanID(long scan_id) {
		String query = "Select * FROM " + TABLE_CELL_INFO + " WHERE "
				+ COLUMN_SCAN_ID + " =  " + scan_id;

		SQLiteDatabase db = this.getWritableDatabase();

		Cursor cursor = db.rawQuery(query, null);
		List<CellIdentity> cell_towers = new ArrayList<CellIdentity>();
		
		while (cursor.moveToNext()) {
			int id = cursor.getInt(0);
			int scan_from_table = cursor.getInt(1);
			JSONObject cell_details = null;
			try {
				cell_details = new JSONObject(cursor.getString(2));
			} catch (JSONException e) {
				e.printStackTrace();
			}
			
			CellIdentity cell = new CellIdentity(id, scan_from_table, cell_details);
			cell_towers.add(cell);
		}
		
		cursor.close();
		db.close();
		
		return cell_towers;
	}
	
	public boolean deleteTowersForScanID(long scan_id) {

		boolean result = false;
		
		SQLiteDatabase db = this.getWritableDatabase();
		int deleted = db.delete(TABLE_CELL_INFO, COLUMN_SCAN_ID + " = ?",
				new String[] { String.valueOf(scan_id) });
		
		if (deleted > 0) {
			result = true;
		}

		db.close();
		return result;
	}
	
	public boolean deleteTower(long id) {

		boolean result = false;

		SQLiteDatabase db = this.getWritableDatabase();
	    int deleted = db.delete(TABLE_CELL_INFO, COLUMN_ID + " = ?",
	    		new String[] { String.valueOf(id) });
	 
	    if (deleted > 0) {
			result = true;
		}
	    
		db.close();
		return result;
	}

}
