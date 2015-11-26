
package com.gionee.gntravel.entity;
/**
 * @Author:yangxy
 * @Create 2014-7-25
 */
public class SimpleHotelBean {
	private String hotelCode;
	private String addressLine;
	private String hotelName;
	private String ctripCommRate;
	private String latitude;//经度
	private String longitude;//纬度
	
	public String getLatitude() {
		return latitude;
	}
	public void setLatitude(String latitude) {
		this.latitude = latitude;
	}
	public String getLongitude() {
		return longitude;
	}
	public void setLongitude(String longitude) {
		this.longitude = longitude;
	}
	public String getHotelCode() {
		return hotelCode;
	}
	public void setHotelCode(String hotelCode) {
		this.hotelCode = hotelCode;
	}
	public String getAddressLine() {
		return addressLine;
	}
	public void setAddressLine(String addressLine) {
		this.addressLine = addressLine;
	}
	public String getHotelName() {
		return hotelName;
	}
	public void setHotelName(String hotelName) {
		this.hotelName = hotelName;
	}
	public String getCtripCommRate() {
		return ctripCommRate;
	}
	public void setCtripCommRate(String ctripCommRate) {
		this.ctripCommRate = ctripCommRate;
	}
	

}

