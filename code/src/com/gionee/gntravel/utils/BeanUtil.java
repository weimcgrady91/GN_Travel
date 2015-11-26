package com.gionee.gntravel.utils;

import com.gionee.gntravel.entity.FlightInfoRequest;

public class BeanUtil {
	public static FlightInfoRequest createFlightInfoRequest(String departDate, String departCity, String arriveCity,
			String flightCode, String classType, String price, String takeOffTime, String airlineCode) {
		FlightInfoRequest requestInfo = new FlightInfoRequest();
		requestInfo.setDepartDate(departDate);
		requestInfo.setDepartCity(departCity);
		requestInfo.setArriveCity(arriveCity);
		requestInfo.setFlightCode(flightCode);
		requestInfo.setClassType(classType);
		requestInfo.setPrice(price);
		requestInfo.setTakeOffTime(takeOffTime);
		requestInfo.setAirlineCode(airlineCode);
		return requestInfo;
	}
}
