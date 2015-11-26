package com.gionee.gntravel.entity;

import java.io.Serializable;


public class Flight implements Serializable {
	private String takeOffTime;
	private String arriveTime;
	private String flight;
	private String craftType;//机型(333大)
	private String flightType;//机型（大型机，小型机,中型机）
	private String dPortBuildingID;
	private String aPortBuildingID;
	private String dPortCode;
	private String aPortCode;
	private String airlineCode;
	private String departCity;
	private String arriveCity;
	private String mealType;
	private String punctualityRate;
	private String classType;
	private String rate;
	private String price;
	private String quantity;
	private String adultTax;
	private String rernote;
	private String endnote;
	private String refnote;
	private String adultOilFee;
	private String airlineName;
	private String airlineShortName;
	private String dPortName;
	private String aPortName;
	private String departCityCode;
	private String arriveCityCode;
	
	public Flight() {
		super();
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

	public String getFlight() {
		return flight;
	}

	public void setFlight(String flight) {
		this.flight = flight;
	}

	public String getCraftType() {
		return craftType;
	}

	public void setCraftType(String craftType) {
		this.craftType = craftType;
	}

	public String getdPortBuildingID() {
		return dPortBuildingID;
	}

	public void setdPortBuildingID(String dPortBuildingID) {
		this.dPortBuildingID = dPortBuildingID;
	}

	public String getaPortBuildingID() {
		return aPortBuildingID;
	}

	public void setaPortBuildingID(String aPortBuildingID) {
		this.aPortBuildingID = aPortBuildingID;
	}

	public String getdPortCode() {
		return dPortCode;
	}

	public void setdPortCode(String dPortCode) {
		this.dPortCode = dPortCode;
	}

	public String getaPortCode() {
		return aPortCode;
	}

	public void setaPortCode(String aPortCode) {
		this.aPortCode = aPortCode;
	}

	public String getAirlineCode() {
		return airlineCode;
	}

	public void setAirlineCode(String airlineCode) {
		this.airlineCode = airlineCode;
	}

	public String getMealType() {
		return mealType;
	}

	public void setMealType(String mealType) {
		this.mealType = mealType;
	}

	public String getPunctualityRate() {
		return punctualityRate;
	}

	public void setPunctualityRate(String punctualityRate) {
		this.punctualityRate = punctualityRate;
	}

	public String getClassType() {
		return classType;
	}

	public void setClassType(String classType) {
		this.classType = classType;
	}

	public String getRate() {
		return rate;
	}

	public void setRate(String rate) {
		this.rate = rate;
	}

	public String getPrice() {
		return price;
	}

	public void setPrice(String price) {
		this.price = price;
	}

	public String getQuantity() {
		return quantity;
	}

	public void setQuantity(String quantity) {
		this.quantity = quantity;
	}

	public String getDepartCity() {
		return departCity;
	}

	public void setDepartCity(String departCity) {
		this.departCity = departCity;
	}

	public String getArriveCity() {
		return arriveCity;
	}

	public void setArriveCity(String arriveCity) {
		this.arriveCity = arriveCity;
	}

	public String getAdultTax() {
		return adultTax;
	}

	public void setAdultTax(String adultTax) {
		this.adultTax = adultTax;
	}

	public String getRernote() {
		return rernote;
	}

	public void setRernote(String rernote) {
		this.rernote = rernote;
	}

	public String getEndnote() {
		return endnote;
	}

	public void setEndnote(String endnote) {
		this.endnote = endnote;
	}

	public String getRefnote() {
		return refnote;
	}

	public void setRefnote(String refnote) {
		this.refnote = refnote;
	}

	public String getAdultOilFee() {
		return adultOilFee;
	}

	public void setAdultOilFee(String adultOilFee) {
		this.adultOilFee = adultOilFee;
	}

	public String getAirlineName() {
		return airlineName;
	}

	public void setAirlineName(String airlineName) {
		this.airlineName = airlineName;
	}

	public String getdPortName() {
		return dPortName;
	}

	public void setdPortName(String dPortName) {
		this.dPortName = dPortName;
	}

	public String getaPortName() {
		return aPortName;
	}

	public void setaPortName(String aPortName) {
		this.aPortName = aPortName;
	}

	public String getDepartCityCode() {
		return departCityCode;
	}

	public void setDepartCityCode(String departCityCode) {
		this.departCityCode = departCityCode;
	}

	public String getArriveCityCode() {
		return arriveCityCode;
	}

	public void setArriveCityCode(String arriveCityCode) {
		this.arriveCityCode = arriveCityCode;
	}

	public String getFlightType() {
		return flightType;
	}

	public void setFlightType(String flightType) {
		this.flightType = flightType;
	}

	public String getAirlineShortName() {
		return airlineShortName;
	}

	public void setAirlineShortName(String airlineShortName) {
		this.airlineShortName = airlineShortName;
	}
	
	
}


