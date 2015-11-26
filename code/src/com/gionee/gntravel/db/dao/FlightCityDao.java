package com.gionee.gntravel.db.dao;

import java.util.ArrayList;

import android.database.sqlite.SQLiteDatabase;

import com.gionee.gntravel.entity.DomesticCity;
public interface FlightCityDao {
	public ArrayList<DomesticCity> findAllCitys(SQLiteDatabase db);
	public ArrayList<DomesticCity> findCitysByFirstLetter(SQLiteDatabase db, String firstLetter);
	public ArrayList<String> findFirstLetter(SQLiteDatabase db);
	public ArrayList<DomesticCity> findAllHotCity(SQLiteDatabase db);
	public void updateCityWeightFlag(SQLiteDatabase db, String cityName, int newWeightFlag);
	public ArrayList<DomesticCity> findCitysWithSelection(SQLiteDatabase db, String selection);
}
