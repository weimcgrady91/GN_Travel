package com.gionee.gntravel.entity;

import java.io.Serializable;

/**
 * @Author:yangxy
 * @Create 2014-7-3
 */
public class RoomStayOrder implements Serializable{
	private String ratePlanCode;
	private String hotelCode;
	public String getHotelCode() {
		return hotelCode;
	}
	public void setHotelCode(String hotelCode) {
		this.hotelCode = hotelCode;
	}
	public String getRatePlanCode() {
		return ratePlanCode;
	}
	public void setRatePlanCode(String ratePlanCode) {
		this.ratePlanCode = ratePlanCode;
	}
	
}

