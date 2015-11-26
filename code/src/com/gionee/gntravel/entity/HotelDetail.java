
package com.gionee.gntravel.entity;

import java.io.Serializable;
import java.util.List;

/**
 * @Author:yangxy
 * @Create 2014-5-15
 */
public class HotelDetail implements Serializable{
//	private String address;
//	private String desc;
	private int imageNum;
//	private String latitude;
//	private String longitude;
	private String hotelCode;
	private List<GuestRoom> guestRooms;
	private List<HotelImage> images;
	
	public String getHotelCode() {
		return hotelCode;
	}
	public void setHotelCode(String hotelCode) {
		this.hotelCode = hotelCode;
	}
//	public String getLatitude() {
//		return latitude;
//	}
//	public void setLatitude(String latitude) {
//		this.latitude = latitude;
//	}
//	public String getLongitude() {
//		return longitude;
//	}
//	public void setLongitude(String longitude) {
//		this.longitude = longitude;
//	}
	public int getImageNum() {
		return imageNum;
	}
	public void setImageNum(int imageNum) {
		this.imageNum = imageNum;
	}
//	public String getAddress() {
//		return address;
//	}
//	public void setAddress(String address) {
//		this.address = address;
//	}
//	public String getDesc() {
//		return desc;
//	}
//	public void setDesc(String desc) {
//		this.desc = desc;
//	}
	
	public List<GuestRoom> getGuestRooms() {
		return guestRooms;
	}
	public void setGuestRooms(List<GuestRoom> guestRooms) {
		this.guestRooms = guestRooms;
	}
	public List<HotelImage> getImages() {
		return images;
	}
	public void setImages(List<HotelImage> images) {
		this.images = images;
	}

}

