package com.example.spidey;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class ScanDatabaseHandler extends SQLiteOpenHelper {

	private static final int DATABASE_VERSION = 2;
	private static final String DATABASE_NAME = "scanDB.db";
	private static final String TABLE_SCANS = "scans";

	public static final String COLUMN_ID = "_id";
	public static final String COLUMN_LOCATION = "location";
	public static final String COLUMN_LATITUDE = "latitude";
	public static final String COLUMN_LONGITUDE = "longitude";
	public static final String COLUMN_TIMESTAMP = "timestamp";

	public ScanDatabaseHandler(Context context, String name,
			CursorFactory factory, int version) {
		super(context, DATABASE_NAME, factory, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {

		String CREATE_SCANS_TABLE = "CREATE TABLE " + TABLE_SCANS + "("
				+ COLUMN_ID + " INTEGER PRIMARY KEY," + COLUMN_LOCATION
				+ " TEXT, " + COLUMN_LATITUDE + " DECIMAL, " + COLUMN_LONGITUDE
				+ " DECIMAL, " + COLUMN_TIMESTAMP + " TEXT" + ")";
		db.execSQL(CREATE_SCANS_TABLE);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_SCANS);
		onCreate(db);
	}

	/**
	 * Convenience method for inserting a scan into the database.
	 * 
	 * @param scan
	 *            the scan to insert.
	 * @return the row ID of the newly inserted row, or -1 if an error occurred
	 */
	public long addScan(Scan scan) {

		ContentValues values = new ContentValues();
		values.put(COLUMN_LOCATION, scan.getLocation());
		values.put(COLUMN_LATITUDE, scan.getLatitude());
		values.put(COLUMN_LONGITUDE, scan.getLongitude());
		values.put(COLUMN_TIMESTAMP, "2010-03-08 14:59:30.252");// scan.getTimestamp().toString());

		SQLiteDatabase db = this.getWritableDatabase();

		long id = db.insert(TABLE_SCANS, null, values);
		db.close();

		return id;
	}

	public Scan findScan(String location) {
		String query = "Select * FROM " + TABLE_SCANS + " WHERE "
				+ COLUMN_LOCATION + " =  \"" + location + "\"";

		SQLiteDatabase db = this.getWritableDatabase();

		Cursor cursor = db.rawQuery(query, null);

		Scan scan = new Scan();

		if (cursor.moveToFirst()) {
			cursor.moveToFirst();
			scan.setID(Integer.parseInt(cursor.getString(0)));
			scan.setLocation(cursor.getString(1));
			scan.setLatitude(cursor.getFloat(2));
			scan.setLongitude(cursor.getFloat(3));
			scan.setTimestamp(cursor.getLong(4));
			cursor.close();
		} else {
			scan = null;
		}
		db.close();
		return scan;
	}

	public boolean deleteScan(String location) {

		boolean result = false;

		String query = "Select * FROM " + TABLE_SCANS + " WHERE "
				+ COLUMN_LOCATION + " =  \"" + location + "\"";

		SQLiteDatabase db = this.getWritableDatabase();

		Cursor cursor = db.rawQuery(query, null);

		Scan scan = new Scan();

		if (cursor.moveToFirst()) {
			scan.setID(Integer.parseInt(cursor.getString(0)));
			db.delete(TABLE_SCANS, COLUMN_ID + " = ?",
					new String[] { String.valueOf(scan.getID()) });
			cursor.close();
			result = true;
		}
		db.close();
		return result;
	}
}