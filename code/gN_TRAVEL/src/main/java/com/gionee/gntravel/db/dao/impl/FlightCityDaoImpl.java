package com.gionee.gntravel.db.dao.impl;

import java.util.ArrayList;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.gionee.gntravel.db.dao.FlightCityDao;
import com.gionee.gntravel.entity.DomesticCity;
public class FlightCityDaoImpl implements FlightCityDao{

	@Override
	public ArrayList<DomesticCity> findCitysByFirstLetter(SQLiteDatabase db,
			String firstLetter) {
		String sql = "select cityName,weightFlag from domestic_city where firstLetter = ? ";
		String[] selectionArgs = new String[]{firstLetter};
		ArrayList<DomesticCity> citys = new ArrayList<DomesticCity>();
		if(db.isOpen()) {
			Cursor cursor = db.rawQuery(sql, selectionArgs);
			while(cursor.moveToNext()) {
				DomesticCity city = new DomesticCity();
				city.setCityName(cursor.getString(0));
				city.setWeightFlag(cursor.getInt(1));
				citys.add(city);
			}
		}
		return citys;
	}

	@Override
	public ArrayList<DomesticCity> findAllCitys(SQLiteDatabase db) {
		String sql = " select cityName from domestic_city";
		ArrayList<DomesticCity> citys = new ArrayList<DomesticCity>();
		if(db.isOpen()) {
			Cursor cursor = db.rawQuery(sql, null);
			while(cursor.moveToNext()) {
				DomesticCity city = new DomesticCity();
				city.setCityName(cursor.getString(0));
				citys.add(city);
			}
		}
		return citys;
	}

	@Override
	public ArrayList<String> findFirstLetter(SQLiteDatabase db) {
		String sql = "select firstLetter from domestic_city group by firstLetter";
		ArrayList<String> firstLetters = new ArrayList<String>();
		if(db.isOpen()) {
			Cursor cursor = db.rawQuery(sql, null);
			while(cursor.moveToNext()) {
				firstLetters.add(cursor.getString(0));
			}
		}
		return firstLetters;
	}

	@Override
	public ArrayList<DomesticCity> findAllHotCity(SQLiteDatabase db) {
		String sql = "select cityName,weightFlag from domestic_city where weightFlag > 0 order by weightFlag";
		ArrayList<DomesticCity> citys = new ArrayList<DomesticCity>();
		if(db.isOpen()) {
			Cursor cursor = db.rawQuery(sql, null);
			while(cursor.moveToNext()) {
				DomesticCity city = new DomesticCity();
				city.setCityName(cursor.getString(0));
				city.setWeightFlag(cursor.getInt(1));
				citys.add(city);
			}
		}
		return citys;
	}

	@Override
	public void updateCityWeightFlag(SQLiteDatabase db, String cityName, int newWeightFlag) {
		ContentValues values = new ContentValues();
		values.put("weightFlag", newWeightFlag);
		if(db.isOpen()) {
			int count = db.update("domestic_city", values, "cityName=?", new String[]{cityName});
		}
	}

	@Override
	public ArrayList<DomesticCity> findCitysWithSelection(SQLiteDatabase db,
			String selection) {
		String sql = "select cityName,weightFlag from domestic_city where cityName like ? " +
									" or cityNamePY like ? " +
									" or cityNameJP like ? ";
		String[] selectionArgs = new String[]{selection+"%",selection+"%",selection+"%"};
		ArrayList<DomesticCity> citys = new ArrayList<DomesticCity>();
		if(db.isOpen()) {
			Cursor cursor = db.rawQuery(sql, selectionArgs);
			while(cursor.moveToNext()) {
				DomesticCity city = new DomesticCity();
				city.setCityName(cursor.getString(0));
				city.setWeightFlag(cursor.getInt(1));
				citys.add(city);
			}
		}
		return citys;
	}

	
	
	
}
