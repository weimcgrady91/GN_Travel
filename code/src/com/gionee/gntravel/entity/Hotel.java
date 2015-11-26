
package com.gionee.gntravel.entity;

import java.io.Serializable;

public class Hotel implements Serializable{
	private String id;
	private String addressLine;
	private String hotelCode;
	private String hotelName;//酒店名称
	private String zoneName;//商业圈
	private String price;//最低价
	private String latitude;//经度
	private String longitude;//纬度
	private String imageUrl;
	private String commRate;//酒店得分
	private String hotelStarRate;// 星级
	private String distance;// 星级
	
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getDistance() {
		return distance;
	}
	public void setDistance(String distance) {
		this.distance = distance;
	}
	public String getAddressLine() {
		return addressLine;
	}
	public void setAddressLine(String addressLine) {
		this.addressLine = addressLine;
	}
	public String getCommRate() {
		return commRate;
	}
	public void setCommRate(String commRate) {
		this.commRate = commRate;
	}
	public String getHotelCode() {
		return hotelCode;
	}
	public void setHotelCode(String hotelCode) {
		this.hotelCode = hotelCode;
	}
	public String getImageUrl() {
		return imageUrl;
	}
	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}
	public String getHotelStarRate() {
		return hotelStarRate;
	}
	public void setHotelStarRate(String hotelStarRate) {
		this.hotelStarRate = hotelStarRate;
	}
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
	public String getHotelName() {
		return hotelName;
	}
	public void setHotelName(String hotelName) {
		this.hotelName = hotelName;
	}
	public String getZoneName() {
		return zoneName;
	}
	public void setZoneName(String zoneName) {
		this.zoneName = zoneName;
	}
	public String getPrice() {
		return price;
	}
	public void setPrice(String price) {
		this.price = price;
	}

}
