package com.gionee.gntravel.db.dao.impl;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.gionee.gntravel.db.dao.ContactsDao;
import com.gionee.gntravel.entity.ContentBean;

public class ContactsDaoImpl implements ContactsDao {

	@Override
	public ContentBean findContactsByUserId(SQLiteDatabase db, String userId,
			String type) {
		String sql = "select name,mobile,id from contact where userid = ? and type=?";
		String[] selectionArgs = new String[] { userId, type };
		ContentBean contentBean;
		if (db.isOpen()) {
			Cursor cursor = db.rawQuery(sql, selectionArgs);
			if (cursor.moveToNext()) {
				contentBean = new ContentBean();
				contentBean.setName(cursor.getString(0));
				contentBean.setCode(cursor.getString(1));
				contentBean.setId(cursor.getString(2));
				return contentBean;
			}
		}
		return null;

	}

	// 插入记录
	@Override
	public long insertContactsByUserId(SQLiteDatabase db, String userId,
			String name, String code, String id, String type) {

		ContentValues values = new ContentValues();
		values.put("userid", userId);
		values.put("name", name);
		values.put("mobile", code);
		values.put("id", id);
		values.put("type", type);
		if (db.isOpen()) {
			long i = db.insert("contact", null, values);
			return i;
		}
		return -1;

	}

	@Override
	public void updateContactsByUserIdandId(SQLiteDatabase db, String userId,
			String id, String name, String mobile) {

		ContentValues values = new ContentValues();

		values.put("name", name);
		values.put("mobile", mobile);

		if (db.isOpen()) {
			db.update("contact", values, "userid=? and id=?", new String[] {
					userId, id });
		}

	}

	@Override
	public void delectContactsByUserId(SQLiteDatabase db, String userId,
			String id) {

		if (db.isOpen()) {
			db.delete("contact", "userid=? and id=? ", new String[] { userId,
					id });
		}
	}

	@Override
	public void delectContactsByType(SQLiteDatabase db, String userId,
			String type) {

		if (db.isOpen()) {
			if (type == null) {
				db.delete("contact", "userid=?", new String[] { userId });
			} else {
				db.delete("contact", "userid=? and type=? ", new String[] {
						userId, type });
			}
		}
	}

}
