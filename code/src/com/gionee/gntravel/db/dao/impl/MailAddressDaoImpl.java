package com.gionee.gntravel.db.dao.impl;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.gionee.gntravel.db.dao.MaiingAddressDao;
import com.gionee.gntravel.entity.AddressEntity;

public class MailAddressDaoImpl  implements MaiingAddressDao{

	@Override
	public AddressEntity findMailAddressByUserId(SQLiteDatabase db,
			String userId) {
		String sql = "select name,mobile,adderss,mail_code from mailing_address where userid = ? ";
		String[] selectionArgs = new String[] { userId };
		AddressEntity addressEntity;
		if (db.isOpen()) {
			Cursor cursor = db.rawQuery(sql, selectionArgs);
			if (cursor.moveToNext()) {
				addressEntity = new AddressEntity();
				addressEntity.setName(cursor.getString(0));
				addressEntity.setMobile(cursor.getString(1));
				addressEntity.setAddress(cursor.getString(2));
				addressEntity.setMailCode(cursor.getString(3));
				return addressEntity;
			}
		}
		return null;

	}

	@Override
	public void delectMailAddressByUserId(SQLiteDatabase db, String userId) {

		if (db.isOpen()) {
			db.delete("mailing_address", "userid=? ", new String[] { userId });
		}
	}

	@Override
	public long insertMailAddressByUserId(SQLiteDatabase db, String userId,
			String name, String mobile, String id, String address,
			String mailCode) {

		ContentValues values = new ContentValues();
		values.put("userid", userId);
		values.put("name", name);
		values.put("mobile", mobile);
		values.put("id", id);
		values.put("mail_code", mailCode);
		values.put("adderss", address);
		if (db.isOpen()) {
			long i = db.insert("mailing_address", null, values);
			return i;
		}
		return -1;
	}

}
