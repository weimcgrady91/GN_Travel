package com.gionee.gntravel.db.dao;

import java.util.ArrayList;

import android.database.sqlite.SQLiteDatabase;

import com.gionee.gntravel.entity.WidgetNewsEntity;

public interface NewsInfoDao {
	public ArrayList<WidgetNewsEntity> findNews(SQLiteDatabase db);
	public int insertNews(SQLiteDatabase db,ArrayList<WidgetNewsEntity> list);
}
