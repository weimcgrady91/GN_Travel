package com.gionee.gntravel.entity;


public class HotelInfoRequest {
	private String cityName;//城市name
	private String cityCode;//城市code
	private int page;//页码
	private int pageSize;//每页显示条数
	private String defaltCondition;
	private String minPrice;
	private String maxPrice;
	private String lat;
	private String lng;
	private String date;
	
	private String zoneId;//商业圈
	private String areaId;//行政区
	private String brandId;//品牌
	private int hotelStarRate;//星级
	private int distance;//距离
	private String key;
	
		
	public String getCityCode() {
		return cityCode;
	}
	public void setCityCode(String cityCode) {
		this.cityCode = cityCode;
	}
	public String getKey() {
		return key;
	}
	public void setKey(String key) {
		this.key = key;
	}
	
	public String getZoneId() {
		return zoneId;
	}
	public void setZoneId(String zoneId) {
		this.zoneId = zoneId;
	}
	public String getAreaId() {
		return areaId;
	}
	public void setAreaId(String areaId) {
		this.areaId = areaId;
	}
	public String getBrandId() {
		return brandId;
	}
	public void setBrandId(String brandId) {
		this.brandId = brandId;
	}
	public int getHotelStarRate() {
		return hotelStarRate;
	}
	public void setHotelStarRate(int hotelStarRate) {
		this.hotelStarRate = hotelStarRate;
	}
	public int getDistance() {
		return distance;
	}
	public void setDistance(int distance) {
		this.distance = distance;
	}
	public String getDate() {
		return date;
	}
	public void setDate(String date) {
		this.date = date;
	}
	public String getLat() {
		return lat;
	}
	public void setLat(String lat) {
		this.lat = lat;
	}
	public String getLng() {
		return lng;
	}
	public void setLng(String lng) {
		this.lng = lng;
	}
	public String getMinPrice() {
		return minPrice;
	}
	public void setMinPrice(String minPrice) {
		this.minPrice = minPrice;
	}
	public String getMaxPrice() {
		return maxPrice;
	}
	public void setMaxPrice(String maxPrice) {
		this.maxPrice = maxPrice;
	}
	public String getDefaltCondition() {
		return defaltCondition;
	}
	public void setDefaltCondition(String defaltCondition) {
		this.defaltCondition = defaltCondition;
	}
	
	public String getCityName() {
		return cityName;
	}
	public void setCityName(String cityName) {
		this.cityName = cityName;
	}
	public int getPage() {
		return page;
	}
	public void setPage(int page) {
		this.page = page;
	}
	public int getPageSize() {
		return pageSize;
	}
	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}

}
