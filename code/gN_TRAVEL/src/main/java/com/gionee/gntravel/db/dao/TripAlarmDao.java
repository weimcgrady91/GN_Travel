package com.gionee.gntravel.db.dao;

import java.util.ArrayList;

import android.database.sqlite.SQLiteDatabase;

import com.gionee.gntravel.entity.AlarmEntity;

public interface TripAlarmDao {
	public ArrayList<AlarmEntity> findAlarm(SQLiteDatabase db, String takeOffTime,String flight);
	public int deleteAlarm(SQLiteDatabase db, String takeOffTime, String flight);
	public long addAlarm(SQLiteDatabase db, String takeOffTime, String flight);
}
