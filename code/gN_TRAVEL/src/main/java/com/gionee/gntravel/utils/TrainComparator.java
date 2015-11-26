package com.gionee.gntravel.utils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.Date;

import com.gionee.gntravel.entity.Train;

public class TrainComparator {
	public static Comparator<Train> getTrainTimeAsc() {
		return new TrainTimeAsc();
	}

	public static Comparator<Train> getTrainTimeDesc() {
		return new TrainTimeDesc();
	}
	public static Comparator<Train> getTrainTotalTimeAsc() {
		return new TrainTotalTimeAsc();
	}
	public static Comparator<Train> getTrainTotalTimeDesc() {
		return new TrainTotalTimeDesc();
	}
	
	public static Comparator<Train> getTrainPriceAsc() {
		return new TrainPriceAsc();
	}
	
	public static Comparator<Train> getTrainPriceDesc() {
		return new TrainPriceDesc();
	}
	
	private static class TrainPriceAsc implements Comparator<Train> {

		@Override
		public int compare(Train lhs, Train rhs) {
			if(Double.parseDouble(lhs.getTicketList().get(0).getPrice()) > Double.parseDouble(rhs.getTicketList().get(0).getPrice())) {
				return 1;
			} else if(Double.parseDouble(lhs.getTicketList().get(0).getPrice()) == Double.parseDouble(rhs.getTicketList().get(0).getPrice())){
				return 0;
			}
			return -1;
		}
		
	}
	
	private static class TrainPriceDesc implements Comparator<Train> {

		@Override
		public int compare(Train lhs, Train rhs) {
			if(Double.parseDouble(lhs.getTicketList().get(0).getPrice()) > Double.parseDouble(rhs.getTicketList().get(0).getPrice())) {
				return -1;
			} else if(Double.parseDouble(lhs.getTicketList().get(0).getPrice()) == Double.parseDouble(rhs.getTicketList().get(0).getPrice())){
				return 0;
			}
			return 1;
		}
		
	}
	
	private static class TrainTimeAsc implements Comparator<Train> {

		@Override
		public int compare(Train lhs, Train rhs) {
			int result = 0;
	        DateFormat df = new SimpleDateFormat("HH:mm");
	        try {
	        	
	            Date dt1 = df.parse(lhs.getStartTime());
	            Date dt2 = df.parse(rhs.getStartTime());
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

	private static class TrainTimeDesc implements Comparator<Train> {
		int result = 0;
		@Override
		public int compare(Train lhs, Train rhs) {
	        DateFormat df = new SimpleDateFormat("HH:mm");
	        try {
	            Date dt1 = df.parse(lhs.getStartTime());
	            Date dt2 = df.parse(rhs.getStartTime());
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
	private static class TrainTotalTimeAsc implements Comparator<Train> {
		int result = 0;
		@Override
		public int compare(Train lhs, Train rhs) {
	      

	            if (lhs.getTotalTime() > rhs.getTotalTime()) {
	            	result =  1;
	            } else if (lhs.getTotalTime() < rhs.getTotalTime()) {
	            	result =  -1;
	            } else {
	            	result = 0;
	            }
	       
	        return result;
		}
	}
	private static class TrainTotalTimeDesc implements Comparator<Train> {
		int result = 0;
		@Override
		public int compare(Train lhs, Train rhs) {

            if (lhs.getTotalTime() > rhs.getTotalTime()) {
            	result =  -1;
            } else if (lhs.getTotalTime() < rhs.getTotalTime()) {
            	result =  1;
            } else {
            	result = 0;
            }
       
        return result;
		}
	}
}
