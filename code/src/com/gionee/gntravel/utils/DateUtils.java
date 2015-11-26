package com.gionee.gntravel.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DateUtils {

	public static String dateToStr(Date date) {
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		String dateStr = dateFormat.format(date);
		return dateStr;
	}
	public static String dateToStr(Date date,String format) {
		SimpleDateFormat dateFormat = new SimpleDateFormat(format);
		String dateStr = dateFormat.format(date);
		return dateStr;
	}

	public static String dateToTime(Date date) {
		SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm");
		String dateStr = dateFormat.format(date);
		return dateStr;
	}

	public static String dateToTime(String dateWithT) {
		Date d = DateUtils.StringToDate(dateWithT.replace("T", " "),
				"yyyy-MM-dd HH:mm");
		return DateUtils.dateToTime(d);
	}
	
	public static Date StringToDate(String dateStr, String formatStr) {
		SimpleDateFormat sdf = new SimpleDateFormat(formatStr);
		Date date = null;
		try {
			date = sdf.parse(dateStr);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return date;
	}

	public static String dateDelYear(Date date) {
		try {
			SimpleDateFormat sdf = new SimpleDateFormat("MM/dd");
			return sdf.format(date);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "";
	}

	public static String getDayofWeek(String date) {
		Date d = DateUtils.StringToDate(date, "yyyy-MM-dd");
		Calendar c = Calendar.getInstance();
		c.setTime(d);
		int wd = c.get(Calendar.DAY_OF_WEEK);

		String x = "";
		switch (wd) {
		case 1:
			x = "星期日";
			break;
		case 2:
			x = "星期一";
			break;
		case 3:
			x = "星期二";
			break;
		case 4:
			x = "星期三";
			break;
		case 5:
			x = "星期四";
			break;
		case 6:
			x = "星期五";
			break;
		case 7:
			x = "星期六";
			break;
		}
		return x;
	}
	
	public static String getDayofWeek(Date date) {
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		int wd = c.get(Calendar.DAY_OF_WEEK);

		String x = "";
		switch (wd) {
		case 1:
			x = "星期日";
			break;
		case 2:
			x = "星期一";
			break;
		case 3:
			x = "星期二";
			break;
		case 4:
			x = "星期三";
			break;
		case 5:
			x = "星期四";
			break;
		case 6:
			x = "星期五";
			break;
		case 7:
			x = "星期六";
			break;
		}
		return x;
	}
	
	public static String getDayOfWeek(Calendar c) {
		int wd = c.get(Calendar.DAY_OF_WEEK);

		String x = "";
		switch (wd) {
		case 1:
			x = "星期日";
			break;
		case 2:
			x = "星期一";
			break;
		case 3:
			x = "星期二";
			break;
		case 4:
			x = "星期三";
			break;
		case 5:
			x = "星期四";
			break;
		case 6:
			x = "星期五";
			break;
		case 7:
			x = "星期六";
			break;
		}
		return x;
	}
	public static String getDayOfWeek2(Calendar c) {
		int wd = c.get(Calendar.DAY_OF_WEEK);

		String x = "";
		switch (wd) {
		case 1:
			x = "日";
			break;
		case 2:
			x = "一";
			break;
		case 3:
			x = "二";
			break;
		case 4:
			x = "三";
			break;
		case 5:
			x = "四";
			break;
		case 6:
			x = "五";
			break;
		case 7:
			x = "六";
			break;
		}
		return x;
	}
	
	public static String dateToDayOfMonth(Date date) {
		SimpleDateFormat sdf = new SimpleDateFormat("MM月dd日");
		return sdf.format(date);
	}
	
	public static String fomatStrDate(String targetDate) {
		String[] date = targetDate.split("T");
		return date[0];
	}
	
	public static String dateToTimeWithSpit_T(Date date) {
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SS");
		String dateStr = dateFormat.format(date);
		return dateStr;
	}
	
	public static String dateFromPudDate(long pudDate) {
		SimpleDateFormat dateFormat = new SimpleDateFormat("MM.dd    HH:mm");
		Date date = new Date(pudDate);
		String dateStr = dateFormat.format(date);
		return dateStr;
	}
	
	public static String getDateWithFormat(String strDate) {
		String targetDateStr = fomatStrDate(strDate);
		Date targetDate = StringToDate(targetDateStr, "yyyy-MM-dd");

		Calendar lastCalendar = Calendar.getInstance();
		lastCalendar.add(Calendar.YEAR, -1);
		int lastYear = lastCalendar.get(Calendar.YEAR);
		
		Calendar nextCalendar = Calendar.getInstance();
		nextCalendar.add(Calendar.YEAR ,+1);
		int nextYear = nextCalendar.get(Calendar.YEAR);
		
		//target
		Calendar nowCalendar = Calendar.getInstance();
		nowCalendar.setTime(targetDate);
		int nowYear = nowCalendar.get(Calendar.YEAR);
		
		if(lastYear < nowYear && nowYear < nextYear) {
			return dateToStr(targetDate, "MM/dd");
		} else {
			return dateToStr(targetDate, "yyyy/MM/dd");
		}
	}
	public static boolean isSameDay(Date dateA,Date dateB) {
	    Calendar calDateA = Calendar.getInstance();
	    calDateA.setTime(dateA);

	    Calendar calDateB = Calendar.getInstance();
	    calDateB.setTime(dateB);

	    return calDateA.get(Calendar.YEAR) == calDateB.get(Calendar.YEAR)
	            && calDateA.get(Calendar.MONTH) == calDateB.get(Calendar.MONTH)
	            &&  calDateA.get(Calendar.DAY_OF_MONTH) == calDateB.get(Calendar.DAY_OF_MONTH);
	}
	
	
	public static String getTimeWithSlash(String dateWithT) {
		return DateUtils.fomatStrDate(dateWithT).replace("-", "/");
	}
}
