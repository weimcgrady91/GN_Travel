package com.gionee.gntravel.entity;

import java.util.ArrayList;

public class TravelEntity {
	private String travelName;
	private ArrayList<TravelSimpleEntity> list;
	public String getTravelName() {
		return travelName;
	}
	public void setTravelName(String travelName) {
		this.travelName = travelName;
	}
	public ArrayList<TravelSimpleEntity> getList() {
		return list;
	}
	public void setList(ArrayList<TravelSimpleEntity> list) {
		this.list = list;
	}
	
	
	
}
