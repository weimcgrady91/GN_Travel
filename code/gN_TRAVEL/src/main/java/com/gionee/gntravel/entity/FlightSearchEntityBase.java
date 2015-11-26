package com.gionee.gntravel.entity;

import java.util.ArrayList;

import android.widget.BaseAdapter;

public abstract class FlightSearchEntityBase {
	public ArrayList<String> keys = new ArrayList<String>();
	public ArrayList<Boolean> values = new ArrayList<Boolean>();
	public String[] strKeys ;
	public boolean[] booleanValues;
	public BaseAdapter adapter;
	public FlightSearchEntityBase() {}
	public FlightSearchEntityBase(String[] strKeys , boolean[] booleBalues) {
		this.strKeys = strKeys;
		this.booleanValues = booleBalues;
	}
	public ArrayList<String> getKeys() {
		return keys;
	}
	public BaseAdapter getAdapter() {
		return adapter;
	}
	public void setAdapter(BaseAdapter adapter) {
		this.adapter = adapter;
	}
	public void setKeys(ArrayList<String> keys) {
		this.keys = keys;
	}
	public ArrayList<Boolean> getValues() {
		return values;
	}
	public void setValues(ArrayList<Boolean> values) {
		this.values = values;
	}
	
	public void init() {
		for (int index = 0; index < strKeys.length; index++) {
			keys.add(strKeys[index]);
			values.add(booleanValues[index]);
		}
	}
	public void add(String key) {
		if(!keys.contains(key)) {
			keys.add(key);
			values.add(false);
		}
	}
	
	public abstract String getTagName();
}
