package com.gionee.gntravel.db.dao.impl;

import java.util.ArrayList;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.gionee.gntravel.db.dao.PassengerDao;
import com.gionee.gntravel.entity.ContentBean;

public class PassengerDaoImpl implements PassengerDao {

	@Override
	public ArrayList<ContentBean> findPassengersByUserId(SQLiteDatabase db,
			String userId) {
		String sql = "select name,code,id from passenger where userid = ? ";
		String[] selectionArgs = new String[] { userId };
		ArrayList<ContentBean> contentList = new ArrayList<ContentBean>();
		if (db.isOpen()) {
			Cursor cursor = db.rawQuery(sql, selectionArgs);
			while (cursor.moveToNext()) {
				ContentBean contentBean = new ContentBean();
				contentBean.setName(cursor.getString(0));
				contentBean.setCode(cursor.getString(1));
				String id = cursor.getString(2);
				contentBean.setId(cursor.getString(2));
				contentList.add(contentBean);
			}
		}
		return contentList;

	}
	//先清空此userid下的记录 
	@Override
	public void delectPassengersByUserId(SQLiteDatabase db, String userId) {

		if (db.isOpen()) {
			db.delete("passenger", "userid=?", new String[]{userId});
		}
	}
	//删除一条记录id下的记录 
			@Override
			public void delectPassengersById(SQLiteDatabase db, String id) {

				if (db.isOpen()) {
					db.delete("passenger", "id=?", new String[]{id});
				}
			}
    //插入记录
	@Override
	public long insertPassengersByUserId(SQLiteDatabase db, String userId ,String id,String name,String code) {

		ContentValues values = new ContentValues();
		values.put("userid", userId);
		values.put("name", name);
		values.put("code", code);
		values.put("id", id);
		if (db.isOpen()) {
			long i=db.insert("passenger", null, values);
			 return i;
		}
		return -1;
		
	}

	@Override
	public void updatePassengersByUserIdandId(SQLiteDatabase db, String userId,
			String id, String name, String code) {
		
		ContentValues values = new ContentValues();
	
		values.put("name", name);
		values.put("code", code);
		
		if (db.isOpen()) {
			db.update("passenger", values, "userid=? and id=?", new String[]{userId,id});
		}
		
	}
	
}
