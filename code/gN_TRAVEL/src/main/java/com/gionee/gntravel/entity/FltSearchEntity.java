package com.gionee.gntravel.entity;

public class FltSearchEntity {
	String searchType;
	String departCity;
	String arriveCity;
	String departDate;
	String orderBy;
	String asc;
	
	public FltSearchEntity(String searchType, String departCity, String arriveCity, String departDate) {
		this.searchType = searchType;
		this.departCity = departCity;
		this.arriveCity = arriveCity;
		this.departDate = departDate;
	}
	
	public String getSearchType() {
		return searchType;
	}

	public void setSearchType(String searchType) {
		this.searchType = searchType;
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

	public String getDepartDate() {
		return departDate;
	}

	public void setDepartDate(String departDate) {
		this.departDate = departDate;
	}

	public String getOrderBy() {
		return "TakeOffTime";
	}

	public void setOrderBy(String orderBy) {
		this.orderBy = orderBy;
	}

	public String getAsc() {
		return "ASC";
	}

	public void setAsc(String asc) {
		this.asc = asc;
	}
}
