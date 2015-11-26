package com.gionee.gntravel.entity;

import java.io.Serializable;

/**
 * 
 * @Author: yangxy
 * @Create Date: 2014-7-2
 */
public class HotelOrderBean implements Serializable{
	private int numberOfUnits;
	private String ratePlanCode;
	private String hotelCode;
	private String customName;
	private String contactName;
	private String phoneNumber;
	private String lateArrivalTime;
	private int count;
	private String start;
	private String end;
	private String userId;
	private String u;
	private String guaranteecode;
	private String ratePlanCategory;//16 ：现付 ；501 / 502：预付;
	
	public String getRatePlanCategory() {
		return ratePlanCategory;
	}
	public void setRatePlanCategory(String ratePlanCategory) {
		this.ratePlanCategory = ratePlanCategory;
	}
	private double amountBeforeTax;
	public String getGuaranteecode() {
		return guaranteecode;
	}
	public void setGuaranteecode(String guaranteecode) {
		this.guaranteecode = guaranteecode;
	}
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	public String getU() {
		return u;
	}
	public void setU(String u) {
		this.u = u;
	}
	public int getNumberOfUnits() {
		return numberOfUnits;
	}
	public void setNumberOfUnits(int numberOfUnits) {
		this.numberOfUnits = numberOfUnits;
	}
	public String getRatePlanCode() {
		return ratePlanCode;
	}
	public void setRatePlanCode(String ratePlanCode) {
		this.ratePlanCode = ratePlanCode;
	}
	public String getHotelCode() {
		return hotelCode;
	}
	public void setHotelCode(String hotelCode) {
		this.hotelCode = hotelCode;
	}
	public String getCustomName() {
		return customName;
	}
	public void setCustomName(String customName) {
		this.customName = customName;
	}
	public String getContactName() {
		return contactName;
	}
	public void setContactName(String contactName) {
		this.contactName = contactName;
	}
	public String getPhoneNumber() {
		return phoneNumber;
	}
	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}
	public String getLateArrivalTime() {
		return lateArrivalTime;
	}
	public void setLateArrivalTime(String lateArrivalTime) {
		this.lateArrivalTime = lateArrivalTime;
	}
	public int getCount() {
		return count;
	}
	public void setCount(int count) {
		this.count = count;
	}
	public String getStart() {
		return start;
	}
	public void setStart(String start) {
		this.start = start;
	}
	public String getEnd() {
		return end;
	}
	public void setEnd(String end) {
		this.end = end;
	}
	public double getAmountBeforeTax() {
		return amountBeforeTax;
	}
	public void setAmountBeforeTax(double amountBeforeTax) {
		this.amountBeforeTax = amountBeforeTax;
	}

}
