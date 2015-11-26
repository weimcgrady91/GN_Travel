package com.gionee.gntravel.entity;

import java.util.ArrayList;

public class SaveTripFromSms {
	private String tripName;
	private ArrayList<FlightSimple4Sms> flights;
	
	public String getTripName() {
		return tripName;
	}
	public void setTripName(String tripName) {
		this.tripName = tripName;
	}
	public ArrayList<FlightSimple4Sms> getFlights() {
		return flights;
	}
	public void setFlights(ArrayList<FlightSimple4Sms> flights) {
		this.flights = flights;
	}
	
	
}
