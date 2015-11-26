package com.gionee.gntravel.db.dao.impl;

import java.util.ArrayList;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.gionee.gntravel.db.dao.NewsInfoDao;
import com.gionee.gntravel.entity.WidgetNewsEntity;

public class NewsInfoDaoImpl implements NewsInfoDao {

	@Override
	public ArrayList<WidgetNewsEntity> findNews(SQLiteDatabase db) {
		String sql = "select id,long_title,pic,pubDate,comment,link from news_info";
		ArrayList<WidgetNewsEntity> list = new ArrayList<WidgetNewsEntity>();
		if(db.isOpen()) {
			Cursor cursor = db.rawQuery(sql, null);
			while(cursor.moveToNext()) {
				WidgetNewsEntity news = new WidgetNewsEntity();
				news.setId(cursor.getString(0));
				news.setLong_title(cursor.getString(1));
				news.setPic(cursor.getString(2));
				news.setPubDate(cursor.getString(3));
				news.setComment(cursor.getString(4));
				news.setLink(cursor.getString(5));
				list.add(news);
			}
			db.close();
		}
		return list;
	}

	@Override
	public int insertNews(SQLiteDatabase db, ArrayList<WidgetNewsEntity> list) {
		if(db.isOpen()) {
			db.beginTransaction();
			String truncateSql = "DELETE FROM news_info";
			db.execSQL(truncateSql);
			for(int index=0;index<list.size();index++) {
				ContentValues values = new ContentValues();
				values.put("id", list.get(index).getId());
				values.put("long_title", list.get(index).getLong_title());
				values.put("pic", list.get(index).getPic());
				values.put("pubDate", list.get(index).getPubDate());
				values.put("comment", list.get(index).getComment());
				values.put("link", list.get(index).getLink());
				db.insert("news_info", "id", values);
			}
			db.setTransactionSuccessful();
			db.endTransaction();
			db.close();
		}
		return 0;
	}


}
