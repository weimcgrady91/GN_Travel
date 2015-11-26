package com.gionee.gntravel.entity;


/**
 * @Author:yangxy
 * @Create 2014-5-29
 */
public class Area {
	private Integer areaId;
	private String Location;
	private String LocationName;
	private String LocationEName;
	private int LocationCity;
	
	public Integer getAreaId() {
		return areaId;
	}
	public void setAreaId(Integer areaId) {
		this.areaId = areaId;
	}
	public String getLocation() {
		return Location;
	}
	public void setLocation(String location) {
		Location = location;
	}
	public String getLocationName() {
		return LocationName;
	}
	public void setLocationName(String locationName) {
		LocationName = locationName;
	}
	public String getLocationEName() {
		return LocationEName;
	}
	public void setLocationEName(String locationEName) {
		LocationEName = locationEName;
	}
	public int getLocationCity() {
		return LocationCity;
	}
	public void setLocationCity(int locationCity) {
		LocationCity = locationCity;
	}

}

