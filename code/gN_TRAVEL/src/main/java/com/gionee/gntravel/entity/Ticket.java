package com.gionee.gntravel.entity;

import java.io.Serializable;

public class Ticket implements Serializable {
	private String TicketID;
	private String SeatTypeName;
	private String Price;
	private String Bookable;
	public String getTicketID() {
		return TicketID;
	}
	public void setTicketID(String ticketID) {
		TicketID = ticketID;
	}
	public String getSeatTypeName() {
		return SeatTypeName;
	}
	public void setSeatTypeName(String seatTypeName) {
		SeatTypeName = seatTypeName;
	}
	public String getPrice() {
		return Price;
	}
	public void setPrice(String price) {
		Price = price;
	}
	public String getBookable() {
		return Bookable;
	}
	public void setBookable(String bookable) {
		Bookable = bookable;
	}
	
	
}
