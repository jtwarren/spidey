package com.example.spidey;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHandler extends SQLiteOpenHelper {

	private static final int DATABASE_VERSION = 1;
	private static final String DATABASE_NAME = "scanDB.db";
	private static final String TABLE_SCANS = "scans";

	public static final String COLUMN_ID = "_id";
	public static final String COLUMN_SCANNAME = "scanname";

	public DatabaseHandler(Context context, String name, CursorFactory factory,
			int version) {
		super(context, DATABASE_NAME, factory, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		String CREATE_SCANS_TABLE = "CREATE TABLE " + TABLE_SCANS + "("
				+ COLUMN_ID + " INTEGER PRIMARY KEY," + COLUMN_SCANNAME
				+ " TEXT" + ")";
		db.execSQL(CREATE_SCANS_TABLE);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_SCANS);
		onCreate(db);
	}

	public void addScan(Scan scan) {

		ContentValues values = new ContentValues();
		values.put(COLUMN_SCANNAME, scan.getScanName());

		SQLiteDatabase db = this.getWritableDatabase();

		db.insert(TABLE_SCANS, null, values);
		db.close();
	}

	public Scan findScan(String scanname) {
		String query = "Select * FROM " + TABLE_SCANS + " WHERE "
				+ COLUMN_SCANNAME + " =  \"" + scanname + "\"";

		SQLiteDatabase db = this.getWritableDatabase();

		Cursor cursor = db.rawQuery(query, null);

		Scan scan = new Scan();

		if (cursor.moveToFirst()) {
			cursor.moveToFirst();
			scan.setID(Integer.parseInt(cursor.getString(0)));
			scan.setScanName(cursor.getString(1));
			cursor.close();
		} else {
			scan = null;
		}
		db.close();
		return scan;
	}

	public boolean deleteScan(String scanId) {

		boolean result = false;

		String query = "Select * FROM " + TABLE_SCANS + " WHERE "
				+ COLUMN_SCANNAME + " =  \"" + scanId + "\"";

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