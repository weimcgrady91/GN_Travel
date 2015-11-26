package com.gionee.gntravel.entity;

import java.io.Serializable;

public class RoomStay implements Serializable{
	private String availabilityStatus;
	private String RoomType;
	private String RoomTypeCode;
	private String description;
	
	private String ratePlanCode;
	private String ratePlanName;
	private String prepaidIndicator;
	private String breakfast;
	
	private GuaranteePayment guaranteePayment;
	private CancelPenaltyAvail cancelPenalty;
	
	private String amountBeforeTax;
	private String currencyCode;
	public String getAmountBeforeTax() {
		return amountBeforeTax;
	}
	public String getRatePlanCode() {
		return ratePlanCode;
	}
	public void setRatePlanCode(String ratePlanCode) {
		this.ratePlanCode = ratePlanCode;
	}
	public String getRatePlanName() {
		return ratePlanName;
	}
	public void setRatePlanName(String ratePlanName) {
		this.ratePlanName = ratePlanName;
	}
	public String getPrepaidIndicator() {
		return prepaidIndicator;
	}
	public void setPrepaidIndicator(String prepaidIndicator) {
		this.prepaidIndicator = prepaidIndicator;
	}
	public String getBreakfast() {
		return breakfast;
	}
	public void setBreakfast(String breakfast) {
		this.breakfast = breakfast;
	}
	public GuaranteePayment getGuaranteePayment() {
		return guaranteePayment;
	}
	public void setGuaranteePayment(GuaranteePayment guaranteePayment) {
		this.guaranteePayment = guaranteePayment;
	}
	public CancelPenaltyAvail getCancelPenalty() {
		return cancelPenalty;
	}
	public void setCancelPenalty(CancelPenaltyAvail cancelPenalty) {
		this.cancelPenalty = cancelPenalty;
	}
	public void setAmountBeforeTax(String amountBeforeTax) {
		this.amountBeforeTax = amountBeforeTax;
	}
	public String getCurrencyCode() {
		return currencyCode;
	}
	public void setCurrencyCode(String currencyCode) {
		this.currencyCode = currencyCode;
	}
	public String getAvailabilityStatus() {
		return availabilityStatus;
	}
	public void setAvailabilityStatus(String availabilityStatus) {
		this.availabilityStatus = availabilityStatus;
	}
	public String getRoomType() {
		return RoomType;
	}
	public void setRoomType(String roomType) {
		RoomType = roomType;
	}
	public String getRoomTypeCode() {
		return RoomTypeCode;
	}
	public void setRoomTypeCode(String roomTypeCode) {
		RoomTypeCode = roomTypeCode;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}

}
