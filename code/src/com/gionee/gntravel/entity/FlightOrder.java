package com.gionee.gntravel.entity;

import java.io.Serializable;
import java.util.ArrayList;

public class FlightOrder implements Serializable {
	private Flight filght;
	private ArrayList<Passenger> passengerList;
	private ContactInfo contactInfo;
	private DeliverInfo deliverInfo;
	
	
	
	public DeliverInfo getDeliverInfo() {
		return deliverInfo;
	}

	public void setDeliverInfo(DeliverInfo deliverInfo) {
		this.deliverInfo = deliverInfo;
	}

	public Flight getFilght() {
		return filght;
	}

	public void setFilght(Flight filght) {
		this.filght = filght;
	}

	public ArrayList<Passenger> getPassengerList() {
		return passengerList;
	}

	public void setPassengerList(ArrayList<Passenger> passengerList) {
		this.passengerList = passengerList;
	}

	public ContactInfo getContactInfo() {
		return contactInfo;
	}

	public void setContactInfo(ContactInfo contactInfo) {
		this.contactInfo = contactInfo;
	}

}
