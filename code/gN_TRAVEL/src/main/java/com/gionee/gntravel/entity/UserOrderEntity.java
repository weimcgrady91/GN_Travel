package com.gionee.gntravel.entity;


public class UserOrderEntity {
	private String flight;
	private String dAirPortName;
	private String aAirPortName;
	private String orderId;
	private String startDate;
	private String price;
	private String status;
	private String type;
	private String hotelName;
	private OrderType orderType;
	
	public String getFlight() {
		return flight;
	}
	public void setFlight(String flight) {
		this.flight = flight;
	}
	public String getdAirPortName() {
		return dAirPortName;
	}
	public void setdAirPortName(String dAirPortName) {
		this.dAirPortName = dAirPortName;
	}
	public String getaAirPortName() {
		return aAirPortName;
	}
	public void setaAirPortName(String aAirPortName) {
		this.aAirPortName = aAirPortName;
	}
	public String getOrderId() {
		return orderId;
	}
	public void setOrderId(String orderId) {
		this.orderId = orderId;
	}
	public String getStartDate() {
		return startDate;
	}
	public void setStartDate(String startDate) {
		this.startDate = startDate;
	}
	public String getPrice() {
		return price;
	}
	public void setPrice(String price) {
		this.price = price;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
		if("flight".equals(type)) {
			setOrderType(OrderType.FLIGHT);
		}
		if("hotel".equals(type)) {
			setOrderType(OrderType.HOTEL);
		}
	}
	public String getHotelName() {
		return hotelName;
	}
	public void setHotelName(String hotelName) {
		this.hotelName = hotelName;
	}
	public OrderType getOrderType() {
		return orderType;
	}
	public void setOrderType(OrderType orderType) {
		this.orderType = orderType;
	}
	
	
	
	
	
}
