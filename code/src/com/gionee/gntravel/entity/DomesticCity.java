package com.gionee.gntravel.entity;

public class DomesticCity {
	private String cityCode;
	private String city;
	private String cityName;
	private String cityEName;
	private int weightFlag;
	private int hotFlag;

	public DomesticCity() {
		super();
	}

	public DomesticCity(String cityName) {
		this.cityName = cityName;
	}
	
	public String getCityCode() {
		return cityCode;
	}

	public void setCityCode(String cityCode) {
		this.cityCode = cityCode;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getCityName() {
		return cityName;
	}

	public void setCityName(String cityName) {
		this.cityName = cityName;
	}

	public String getCityEName() {
		return cityEName;
	}

	public void setCityEName(String cityEName) {
		this.cityEName = cityEName;
	}

	public int getWeightFlag() {
		return weightFlag;
	}

	public void setWeightFlag(int weightFlag) {
		this.weightFlag = weightFlag;
	}

	public int getHotFlag() {
		return hotFlag;
	}

	public void setHotFlag(int hotFlag) {
		this.hotFlag = hotFlag;
	}

}
