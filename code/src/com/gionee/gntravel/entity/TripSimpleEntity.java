package com.gionee.gntravel.entity;

import java.util.ArrayList;

public class TripSimpleEntity {
	private String tripName;
	private boolean state;
	private String tripId;
	private ArrayList<TripSimpleItem> TripSimpleItems;

	public String getTripName() {
		return tripName;
	}

	public void setTripName(String tripName) {
		this.tripName = tripName;
	}

	public boolean isState() {
		return state;
	}

	public void setState(boolean state) {
		this.state = state;
	}

	public ArrayList<TripSimpleItem> getTripSimpleItems() {
		return TripSimpleItems;
	}

	public void setTripSimpleItems(ArrayList<TripSimpleItem> tripSimpleItems) {
		TripSimpleItems = tripSimpleItems;
	}

	public String getTripId() {
		return tripId;
	}

	public void setTripId(String tripId) {
		this.tripId = tripId;
	}

	
}
