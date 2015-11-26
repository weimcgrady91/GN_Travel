package com.gionee.gntravel.entity;


public class TripSimpleItem {
	private String date;
	private String type;
	private String name;

	public String getDate() {
//		return DateUtils.getDateWithFormat(date);
//		return DateUtils.fomatStrDate(date).replace("-", "/");
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

}
