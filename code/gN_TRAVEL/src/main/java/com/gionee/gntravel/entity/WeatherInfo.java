package com.gionee.gntravel.entity;

public class WeatherInfo {
	public String wind;
	public String temperature;
	public String weatherType;

	public WeatherInfo() {
		super();
	}

	public WeatherInfo(String wind, String temperature, String weatherType) {
		super();
		this.wind = wind;
		this.temperature = temperature;
		this.weatherType = weatherType;
	}

	public String getWind() {
		return wind;
	}

	public void setWind(String wind) {
		this.wind = wind;
	}

	public String getTemperature() {
		return temperature;
	}

	public void setTemperature(String temperature) {
		this.temperature = temperature;
	}

	public String getWeatherType() {
		return weatherType;
	}

	public void setWeatherType(String weatherType) {
		this.weatherType = weatherType;
	}

}
