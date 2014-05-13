package com.spideyapp.sqlite.helper;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import com.spideyapp.sqlite.model.CellInfo;
import com.spideyapp.sqlite.model.Scan;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DatabaseHelper extends SQLiteOpenHelper {
	
	private static final String TAG = "DatabaseHelper";
	
	private static DatabaseHelper mInstance = null;

	// Database Version
	private static final int DATABASE_VERSION = 2;

	// Database Name
	private static final String DATABASE_NAME = "scanInfo";

	// Table Names
	private static final String TABLE_SCANS = "scans";
	private static final String TABLE_CELLS = "cells";
	private static final String TABLE_SCAN_CELL = "scan_cells";

	// Common column names
	public static final String COLUMN_ID = "_id";
	private static final String COLUMN_CREATED_AT = "created_at";

	// SCANS Table - column names
	public static final String COLUMN_LOCATION = "location";
	public static final String COLUMN_LATITUDE = "latitude";
	public static final String COLUMN_LONGITUDE = "longitude";

	// CELLS Table - column names
	public static final String COLUMN_CID = "cid";
	public static final String COLUMN_LAC = "lac";
	public static final String COLUMN_MCC = "mcc";
	public static final String COLUMN_MNC = "mnc";
	public static final String COLUMN_DBM = "dbm";
	

	// SCAN_CELLS Table - column names
	private static final String COLUMN_SCAN_ID = "scan_id";
	private static final String COLUMN_CELL_ID = "cell_id";

	// Scan table create statement
	private static final String CREATE_TABLE_SCAN = "CREATE TABLE "
			+ TABLE_SCANS + "(" + COLUMN_ID + " INTEGER PRIMARY KEY,"
			+ COLUMN_LOCATION + " TEXT, " + COLUMN_LATITUDE + " DECIMAL, "
			+ COLUMN_LONGITUDE + " DECIMAL, " + COLUMN_CREATED_AT + " DATETIME"
			+ ")";
	
	// Cell table create statement
	private static final String CREATE_TABLE_CELL = "CREATE TABLE "
			+ TABLE_CELLS + "(" + COLUMN_ID + " INTEGER PRIMARY KEY,"
			+ COLUMN_CID + " INTEGER, " + COLUMN_LAC + " INTEGER, "
			+ COLUMN_MCC + " INTEGER, " + COLUMN_MNC + " INTEGER, "
			+ COLUMN_CREATED_AT + " DATETIME,"
			+ COLUMN_DBM + " INTEGER, "
			+ ")";

	// scan_cell table create statement
	private static final String CREATE_TABLE_SCAN_CELL = "CREATE TABLE "
			+ TABLE_SCAN_CELL + "(" + COLUMN_ID + " INTEGER PRIMARY KEY,"
			+ COLUMN_SCAN_ID + " INTEGER," + COLUMN_CELL_ID + " INTEGER,"
			+ COLUMN_CREATED_AT + " DATETIME" + ")";

	private DatabaseHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}
	
	 public static DatabaseHelper getInstance(Context context) {
	        if (mInstance == null) {
	            mInstance = new DatabaseHelper(context.getApplicationContext());
	        }
	        return mInstance;
	    }

	@Override
	public void onCreate(SQLiteDatabase db) {
		// creating required tables
		db.execSQL(CREATE_TABLE_SCAN);
		db.execSQL(CREATE_TABLE_CELL);
		db.execSQL(CREATE_TABLE_SCAN_CELL);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// on upgrade drop older tables
	//	db.execSQL("DROP TABLE IF EXISTS " + TABLE_SCANS);
	//	db.execSQL("DROP TABLE IF EXISTS " + TABLE_CELLS);
	//	db.execSQL("DROP TABLE IF EXISTS " + TABLE_SCAN_CELL);

		// create new tables
		//onCreate(db);
		
		if (newVersion == 2)
		{
			if (oldVersion == 1)
				db.execSQL("ALTER TABLE " + TABLE_CELLS + " ADD COLUMN " + COLUMN_DBM + " INTEGER");
		}
	}

	/**
	 * Creating a scan
	 */
	public long createScan(Scan scan) {
		SQLiteDatabase db = this.getWritableDatabase();

		ContentValues values = new ContentValues();
		values.put(COLUMN_LOCATION, scan.getLocation());
		values.put(COLUMN_LATITUDE, scan.getLatitude());
		values.put(COLUMN_LONGITUDE, scan.getLongitude());
		values.put(COLUMN_CREATED_AT, getDateTime());

		// insert row
		long scan_id = db.insert(TABLE_SCANS, null, values);

		return scan_id;
	}

	/**
	 * get single scan
	 */
	public Scan getScan(long scan_id) {
		SQLiteDatabase db = this.getReadableDatabase();

		String selectQuery = "SELECT  * FROM " + TABLE_SCANS + " WHERE "
				+ COLUMN_ID + " = " + scan_id;

		Log.d(TAG, selectQuery);

		Cursor c = db.rawQuery(selectQuery, null);

		if (c != null)
			c.moveToFirst();

		Scan scan = new Scan();
		scan.setId(c.getInt(c.getColumnIndex(COLUMN_ID)));
		scan.setLocation(c.getString(c.getColumnIndex(COLUMN_LOCATION)));
		scan.setLatitude(c.getDouble(c.getColumnIndex(COLUMN_LATITUDE)));
		scan.setLongitude(c.getDouble(c.getColumnIndex(COLUMN_LONGITUDE)));
		scan.setCreatedAt(c.getString(c.getColumnIndex(COLUMN_CREATED_AT)));

		return scan;
	}

	/**
	 * getting all scans
	 * */
	public List<Scan> getAllScans() {
		List<Scan> scans = new ArrayList<Scan>();
		String selectQuery = "SELECT  * FROM " + TABLE_SCANS;

		Log.d(TAG, selectQuery);

		SQLiteDatabase db = this.getReadableDatabase();
		Cursor c = db.rawQuery(selectQuery, null);

		// looping through all rows and adding to list
		if (c.moveToFirst()) {
			do {
				Scan scan = new Scan();
				scan.setId(c.getInt(c.getColumnIndex(COLUMN_ID)));
				scan.setLocation(c.getString(c.getColumnIndex(COLUMN_LOCATION)));
				scan.setLatitude(c.getDouble(c.getColumnIndex(COLUMN_LATITUDE)));
				scan.setLongitude(c.getDouble(c
						.getColumnIndex(COLUMN_LONGITUDE)));
				scan.setCreatedAt(c.getString(c
						.getColumnIndex(COLUMN_CREATED_AT)));

				scans.add(scan);
			} while (c.moveToNext());
		}

		return scans;
	}

	/**
	 * getting scan count
	 */
	public int getScanCount() {
		String countQuery = "SELECT  * FROM " + TABLE_SCANS;
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.rawQuery(countQuery, null);

		int count = cursor.getCount();
		cursor.close();

		// return count
		return count;
	}

	/**
	 * Updating a scan
	 */
	public int updateScan(Scan scan) {
		SQLiteDatabase db = this.getWritableDatabase();

		ContentValues values = new ContentValues();
		values.put(COLUMN_LOCATION, scan.getLocation());
		values.put(COLUMN_LATITUDE, scan.getLatitude());
		values.put(COLUMN_LONGITUDE, scan.getLongitude());

		// updating row
		return db.update(TABLE_SCANS, values, COLUMN_ID + " = ?",
				new String[] { String.valueOf(scan.getId()) });
	}

	/**
	 * Deleting a scan
	 */
	public void deleteScan(long scan_id) {
		SQLiteDatabase db = this.getWritableDatabase();

		// before deleting scan
		// delete all cells that correspond
		List<CellInfo> cells = getAllCellsByScanId(scan_id);

		// delete all cells
		for (CellInfo cell : cells) {
			// delete celll
			deleteCell(cell.getId());
		}

		db.delete(TABLE_SCANS, COLUMN_ID + " = ?",
				new String[] { String.valueOf(scan_id) });
	}

	// closing database
	public void closeDB() {
		SQLiteDatabase db = this.getReadableDatabase();
		if (db != null && db.isOpen())
			db.close();
	}

	/**
	 * Creating cell
	 * @param scan_id 
	 */
	public long createCell(CellInfo cell, long scan_id) {
		SQLiteDatabase db = this.getWritableDatabase();

		ContentValues values = new ContentValues();
		values.put(COLUMN_CID, cell.getCID());
		values.put(COLUMN_LAC, cell.getLAC());
		values.put(COLUMN_MCC, cell.getMCC());
		values.put(COLUMN_MNC, cell.getMNC());
		values.put(COLUMN_CREATED_AT, getDateTime());
		values.put(COLUMN_DBM, cell.getDBM());
		

		// insert row
		long cell_id = db.insert(TABLE_CELLS, null, values);
		
		// create join
		createScanCell(scan_id, cell_id);

		return cell_id;
	}

	/**
	 * getting all cells
	 * */
	public List<CellInfo> getAllCells() {
		List<CellInfo> cells = new ArrayList<CellInfo>();
		String selectQuery = "SELECT  * FROM " + TABLE_CELLS;

		Log.d(TAG, selectQuery);

		SQLiteDatabase db = this.getReadableDatabase();
		Cursor c = db.rawQuery(selectQuery, null);

		// looping through all rows and adding to list
		if (c.moveToFirst()) {
			do {
				CellInfo cell = new CellInfo();
				cell.setId(c.getInt((c.getColumnIndex(COLUMN_ID))));
				cell.setCID(c.getInt(c.getColumnIndex(COLUMN_CID)));
				cell.setLAC(c.getInt(c.getColumnIndex(COLUMN_LAC)));
				cell.setMCC(c.getInt(c.getColumnIndex(COLUMN_MCC)));
				cell.setMNC(c.getInt(c.getColumnIndex(COLUMN_MNC)));
				cell.setDBM(c.getInt(c.getColumnIndex(COLUMN_DBM)));

				cells.add(cell);
			} while (c.moveToNext());
		}
		return cells;
	}

	/**
	 * Updating a cell
	 */
	public int updateCell(CellInfo cell) {
		SQLiteDatabase db = this.getWritableDatabase();

		ContentValues values = new ContentValues();
		values.put(COLUMN_CID, cell.getCID());
		values.put(COLUMN_LAC, cell.getLAC());
		values.put(COLUMN_MCC, cell.getMCC());
		values.put(COLUMN_MNC, cell.getMNC());
		values.put(COLUMN_DBM, cell.getDBM());

		// updating row
		return db.update(TABLE_CELLS, values, COLUMN_ID + " = ?",
				new String[] { String.valueOf(cell.getId()) });
	}

	/**
	 * Deleting a cell
	 */
	public void deleteCell(long cell_id) {
		SQLiteDatabase db = this.getWritableDatabase();

		db.delete(TABLE_CELLS, COLUMN_ID + " = ?",
				new String[] { String.valueOf(cell_id) });
	}

	/**
	 * getting all cells for scan
	 * */
	public List<CellInfo> getAllCellsByScanId(long scan_id) {
		List<CellInfo> cells = new ArrayList<CellInfo>();

		String selectQuery = "SELECT  * FROM " + TABLE_CELLS + " tc, "
				+ TABLE_SCANS + " ts, " + TABLE_SCAN_CELL + " tt WHERE ts."
				+ COLUMN_ID + " = '" + scan_id + "'" + " AND ts." + COLUMN_ID
				+ " = " + "tt." + COLUMN_SCAN_ID + " AND tc." + COLUMN_ID + " = "
				+ "tt." + COLUMN_CELL_ID;

		Log.d(TAG, selectQuery);

		SQLiteDatabase db = this.getReadableDatabase();
		Cursor c = db.rawQuery(selectQuery, null);

		// looping through all rows and adding to list
		if (c.moveToFirst()) {
			do {
				CellInfo cell = new CellInfo();
				cell.setId(c.getInt((c.getColumnIndex(COLUMN_ID))));
				cell.setCID(c.getInt(c.getColumnIndex(COLUMN_CID)));
				cell.setLAC(c.getInt(c.getColumnIndex(COLUMN_LAC)));
				cell.setMCC(c.getInt(c.getColumnIndex(COLUMN_MCC)));
				cell.setMNC(c.getInt(c.getColumnIndex(COLUMN_MNC)));
				cell.setDBM(c.getInt(c.getColumnIndex(COLUMN_DBM)));
				

				cells.add(cell);
			} while (c.moveToNext());
		}

		return cells;
	}

	/**
	 * Creating scan_cell
	 */
	public long createScanCell(long scan_id, long cell_id) {
		SQLiteDatabase db = this.getWritableDatabase();

		ContentValues values = new ContentValues();
		values.put(COLUMN_SCAN_ID, scan_id);
		values.put(COLUMN_CELL_ID, cell_id);
		values.put(COLUMN_CREATED_AT, getDateTime());

		long id = db.insert(TABLE_SCAN_CELL, null, values);

		return id;
	}

	/**
	 * get datetime
	 * */
	private String getDateTime() {
		SimpleDateFormat dateFormat = new SimpleDateFormat(
				"yyyy-MM-dd HH:mm:ss", Locale.getDefault());
		Date date = new Date();
		return dateFormat.format(date);
	}
}
