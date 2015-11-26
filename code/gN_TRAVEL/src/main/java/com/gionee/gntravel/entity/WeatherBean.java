package com.gionee.gntravel.entity;

import java.io.Serializable;
import java.util.List;

public class WeatherBean implements Serializable{
	private List<WeatherRepParam> weather;
	private String st;
	private String city;
	
	public String getCity() {
		return city;
	}
	public void setCity(String city) {
		this.city = city;
	}
	public List<WeatherRepParam> getWeather() {
		return weather;
	}
	public void setWeather(List<WeatherRepParam> weather) {
		this.weather = weather;
	}
	public String getSt() {
		return st;
	}
	public void setSt(String st) {
		this.st = st;
	}
}
