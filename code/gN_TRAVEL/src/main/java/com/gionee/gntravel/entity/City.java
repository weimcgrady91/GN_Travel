package com.gionee.gntravel.entity;


public class City implements Comparable<City>{
	private String name;
	private String pinYinName;
	private String cityCode;
	private int cityActive;


	public City(String cityName) {
		this.name = cityName;
	}
	
	public City(String cityName, String pinYinName) {
		this.name = cityName;
		this.pinYinName = pinYinName;
	}
	
	public City(String name, String pinYinName, String cityCode) {
		super();
		this.name = name;
		this.pinYinName = pinYinName;
		this.cityCode = cityCode;
	}

	public City(String name, String pinYinName, String cityCode, int cityActive) {
		super();
		this.name = name;
		this.pinYinName = pinYinName;
		this.cityCode = cityCode;
		this.cityActive = cityActive;
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPinYinName() {
		return pinYinName;
	}

	public void setPinYinName(String pinYinName) {
		this.pinYinName = pinYinName;
	}

	public String getCityCode() {
		return cityCode;
	}

	public void setCityCode(String cityCode) {
		this.cityCode = cityCode;
	}

	public int getCityActive() {
		return cityActive;
	}

	public void setCityActive(int cityActive) {
		this.cityActive = cityActive;
	}

	@Override
	public int compareTo(City another) {
		int value = getCityActive();
		int value2 = another.getCityActive();
		if(value < value2) {
			return -1;
		} else if(value > value2) {
			return 1;
		} 
		return 0;
	}
	



}
