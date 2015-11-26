package com.gionee.gntravel.entity;

import java.io.Serializable;

public class GuestRoom implements Serializable {
	private String hotelCode;
	private String hotelName;
	private String roomTypeName;
	private String roomSize;
	private String floor;
	private String bedTypeCode;
	private String size;
	private Integer id;
	private String roomTypeCode;
	private String status;
	private String breakfast;
	private double amountBeforeTax;
	private String guaranteeCode;
	private String standardOccupancy;
	private String url;
	private String netInfo;
	private String ratePlanCategory;//16 ：现付 ；501 / 502：预付
	
	
	public String getRatePlanCategory() {
		return ratePlanCategory;
	}
	public void setRatePlanCategory(String ratePlanCategory) {
		this.ratePlanCategory = ratePlanCategory;
	}
	public String getNetInfo() {
		return netInfo;
	}
	public void setNetInfo(String netInfo) {
		this.netInfo = netInfo;
	}
	public String getStandardOccupancy() {
		return standardOccupancy;
	}
	public void setStandardOccupancy(String standardOccupancy) {
		this.standardOccupancy = standardOccupancy;
	}
	public String getHotelCode() {
		return hotelCode;
	}
	public void setHotelCode(String hotelCode) {
		this.hotelCode = hotelCode;
	}
	public String getHotelName() {
		return hotelName;
	}
	public void setHotelName(String hotelName) {
		this.hotelName = hotelName;
	}
	
	public String getRoomTypeName() {
		return roomTypeName;
	}
	public void setRoomTypeName(String roomTypeName) {
		this.roomTypeName = roomTypeName;
	}
	public String getRoomSize() {
		return roomSize;
	}
	public void setRoomSize(String roomSize) {
		this.roomSize = roomSize;
	}
	public String getFloor() {
		return floor;
	}
	public void setFloor(String floor) {
		this.floor = floor;
	}
	public String getBedTypeCode() {
		return bedTypeCode;
	}
	public void setBedTypeCode(String bedTypeCode) {
		this.bedTypeCode = bedTypeCode;
	}
	public String getSize() {
		return size;
	}
	public void setSize(String size) {
		this.size = size;
	}
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getRoomTypeCode() {
		return roomTypeCode;
	}
	public void setRoomTypeCode(String roomTypeCode) {
		this.roomTypeCode = roomTypeCode;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getBreakfast() {
		return breakfast;
	}
	public void setBreakfast(String breakfast) {
		this.breakfast = breakfast;
	}
	
	public double getAmountBeforeTax() {
		return amountBeforeTax;
	}
	public void setAmountBeforeTax(double amountBeforeTax) {
		this.amountBeforeTax = amountBeforeTax;
	}
	public String getGuaranteeCode() {
		return guaranteeCode;
	}
	public void setGuaranteeCode(String guaranteeCode) {
		this.guaranteeCode = guaranteeCode;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	
	
}
