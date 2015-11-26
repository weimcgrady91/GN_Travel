package com.gionee.gntravel.utils;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.gionee.gntravel.R;

public class DBUtils {
	private Context context;
	public DBUtils(Context context) {
		this.context = context;
	}
	
	public SQLiteDatabase openReadOnlyDatabase() {
		SQLiteDatabase db = SQLiteDatabase.openDatabase(
				context.getResources().getString(R.string.datapath)+"/gntravel.db", 
				null, SQLiteDatabase.OPEN_READONLY);
		return db;
	}
	public SQLiteDatabase openReadWriteDatabase() {
		SQLiteDatabase db = SQLiteDatabase.openDatabase(
				context.getResources().getString(R.string.datapath)+"/gntravel.db", 
				null, SQLiteDatabase.OPEN_READWRITE);
		return db;
	}
}
