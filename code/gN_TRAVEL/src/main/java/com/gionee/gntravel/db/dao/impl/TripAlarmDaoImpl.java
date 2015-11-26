package com.gionee.gntravel.db.dao.impl;


import java.util.ArrayList;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.gionee.gntravel.db.dao.TripAlarmDao;
import com.gionee.gntravel.entity.AlarmEntity;
public class TripAlarmDaoImpl implements TripAlarmDao{

	@Override
	public ArrayList<AlarmEntity> findAlarm(SQLiteDatabase db,
			String takeOffTime, String flight) {
		String sql = "select _id,takeOffTime,flight from trip_alarm where takeOffTime = ? and flight=? ";
		String[] selectionArgs = new String[]{takeOffTime,flight};
		ArrayList<AlarmEntity> list = new ArrayList<AlarmEntity>();
		if(db.isOpen()) {
			Cursor cursor = db.rawQuery(sql, selectionArgs);
			while(cursor.moveToNext()) {
				AlarmEntity alarmEntity = new AlarmEntity();
				alarmEntity.set_id(cursor.getInt(0));
				alarmEntity.setTakeOffTime(cursor.getString(1));
				alarmEntity.setFlight(cursor.getString(2));
				list.add(alarmEntity);
			}
		}
		return list;
	}

	@Override
	public int deleteAlarm(SQLiteDatabase db, String takeOffTime, String flight) {
		String sql = "Delete from trip_alarm where takeOffTime = ? and flight=? ";
		String[] selectionArgs = new String[]{takeOffTime,flight};
		if(db.isOpen()) {
			int row = db.delete("trip_alarm", "takeOffTime = ? and flight=?", selectionArgs);
			return row;
		}
		return 0;
	}

	@Override
	public long addAlarm(SQLiteDatabase db, String takeOffTime, String flight) {
		
		String sql = "Insert into trip_alarm ('_id','takeOffTime' ,'flight' ) values(null,'"+takeOffTime+"','"+flight+"')";
		String[] selectionArgs = new String[]{takeOffTime,flight};
		if(db.isOpen()) {
//			db.execSQL(sql);
			ContentValues values = new ContentValues();
			values.put("takeOffTime", takeOffTime);
			values.put("flight", flight);
			return db.insert("trip_alarm", "takeOffTime", values);
		}
		return 0;
	}

	
}
