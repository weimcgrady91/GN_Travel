package com.gionee.gntravel.utils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.Date;

import com.gionee.gntravel.entity.Flight;

public class MyComparator {
	public static Comparator<Flight> getFlightTimeAsc() {
		return new FlightTimeAsc();
	}

	public static Comparator<Flight> getFlightTimeDesc() {
		return new FlightTimeDesc();
	}

	public static Comparator<Flight> getFlightPriceAsc() {
		return new FlightPriceAsc();
	}
	
	public static Comparator<Flight> getFlightPriceDesc() {
		return new FlightPriceDesc();
	}
	
	private static class FlightPriceAsc implements Comparator<Flight> {

		@Override
		public int compare(Flight lhs, Flight rhs) {
			if(Double.parseDouble(lhs.getPrice()) > Double.parseDouble(rhs.getPrice())) {
				return 1;
			} else if(Double.parseDouble(lhs.getPrice()) == Double.parseDouble(rhs.getPrice())){
				return 0;
			}
			return -1;
		}
		
	}
	
	private static class FlightPriceDesc implements Comparator<Flight> {

		@Override
		public int compare(Flight lhs, Flight rhs) {
			if(Double.parseDouble(lhs.getPrice()) > Double.parseDouble(rhs.getPrice())) {
				return -1;
			} else if(Double.parseDouble(lhs.getPrice()) == Double.parseDouble(rhs.getPrice())){
				return 0;
			}
			return 1;
		}
		
	}
	
	private static class FlightTimeAsc implements Comparator<Flight> {

		@Override
		public int compare(Flight lhs, Flight rhs) {
			int result = 0;
	        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm");
	        try {
	            Date dt1 = df.parse(lhs.getTakeOffTime().replace("T", " "));
	            Date dt2 = df.parse(rhs.getTakeOffTime().replace("T", " "));
	            if (dt1.getTime() > dt2.getTime()) {
	            	result = 1;
	            } else if (dt1.getTime() < dt2.getTime()) {
	            	result =  -1;
	            } else {
	            	result =  0;
	            }
	        } catch (ParseException exception) {
	            exception.printStackTrace();
	        }
	        return result;
		}
	}

	private static class FlightTimeDesc implements Comparator<Flight> {
		int result = 0;
		@Override
		public int compare(Flight lhs, Flight rhs) {
	        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm");
	        try {
	            Date dt1 = df.parse(lhs.getTakeOffTime().replace("T", " "));
	            Date dt2 = df.parse(rhs.getTakeOffTime().replace("T", " "));
	            if (dt1.getTime() > dt2.getTime()) {
	            	result =  -1;
	            } else if (dt1.getTime() < dt2.getTime()) {
	            	result =  1;
	            } else {
	            	result = 0;
	            }
	        } catch (ParseException exception) {
	            exception.printStackTrace();
	        }
	        return result;
		}
	}
}
