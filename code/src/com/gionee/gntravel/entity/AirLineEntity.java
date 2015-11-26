package com.gionee.gntravel.entity;

public class AirLineEntity extends FlightSearchEntityBase {
	public AirLineEntity() {
	};

	public void init() {
		keys.clear();
		values.clear();
		keys.add("不限");
		values.add(true);
	}
	
	@Override
	public String getTagName() {
		// TODO Auto-generated method stub
		return getClass().getSimpleName();
	}
}