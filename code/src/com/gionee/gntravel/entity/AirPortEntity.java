package com.gionee.gntravel.entity;

public class AirPortEntity extends FlightSearchEntityBase {
	public String tagName;
	
	public AirPortEntity() {
	};

	public AirPortEntity(String tagName) {
		this.tagName = tagName;
	}
	
	public void init() {
		keys.clear();
		values.clear();
		keys.add("不限");
		values.add(true);
	}
	
	@Override
	public String getTagName() {
		// TODO Auto-generated method stub
		return getClass().getSimpleName()+tagName;
	}
}
