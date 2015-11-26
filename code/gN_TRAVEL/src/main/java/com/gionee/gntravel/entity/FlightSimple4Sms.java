package com.gionee.gntravel.entity;

public class FlightSimple4Sms {
	private String orderId;
	private String takeOffDate;
	private String takeOffTime;
	private String arriveTime;
	private String flightNum;
	private String dportCity;
	private String arriveCity;
	private String createTime;

	public String getCreateTime() {
		return createTime;
	}

	public void setCreateTime(String createTime) {
		this.createTime = createTime;
	}

	public String getOrderId() {
		return orderId;
	}

	public void setOrderId(String orderId) {
		this.orderId = orderId;
	}

	public String getTakeOffDate() {
		return takeOffDate;
	}

	public void setTakeOffDate(String takeOffDate) {
		this.takeOffDate = takeOffDate;
	}

	public String getTakeOffTime() {
		return takeOffTime;
	}

	public void setTakeOffTime(String takeOffTime) {
		this.takeOffTime = takeOffTime;
	}

	public String getArriveTime() {
		return arriveTime;
	}

	public void setArriveTime(String arriveTime) {
		this.arriveTime = arriveTime;
	}

	public String getFlightNum() {
		return flightNum;
	}

	public void setFlightNum(String flightNum) {
		this.flightNum = flightNum;
	}

	public String getDportCity() {
		return dportCity;
	}

	public void setDportCity(String dportCity) {
		this.dportCity = dportCity;
	}

	public String getArriveCity() {
		return arriveCity;
	}

	public void setArriveCity(String arriveCity) {
		this.arriveCity = arriveCity;
	}

}
