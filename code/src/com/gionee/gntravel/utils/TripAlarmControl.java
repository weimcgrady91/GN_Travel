package com.gionee.gntravel.utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

public class TripAlarmControl {
	private Context context;

	public TripAlarmControl(Context context) {
		this.context = context;
	}

	public void addNotification(String takeOffTime, String flight, int _id,
			String tripName, String userName) {
		AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
		Date date = DateUtils.StringToDate(takeOffTime.replace("T", " "),"yyyy-MM-dd HH:mm");
		
		Calendar c = Calendar.getInstance();
		c.setTime(date);

		Calendar today = Calendar.getInstance();
		if(c.before(today)) {
			return;
		}
		
		String pattern = "yyyy/MM/dd HH:mm";
		SimpleDateFormat stf = new SimpleDateFormat(pattern);
		
		Intent intent = new Intent(FinalString.TRIP_ALARM_ACTION);
		intent.putExtra("alarm_msg", "将于"+stf.format(c.getTime())+"起飞");
		intent.putExtra("alarm_takeOffTime", takeOffTime);
		intent.putExtra("alarm_flight", flight);
		intent.putExtra("alarm_tripName", tripName);
		intent.putExtra("alarm_userName", userName);
		
		PendingIntent pendIntent = PendingIntent.getBroadcast(context, _id,intent, 0);
		
		// test 起飞前2小时提醒;
		c.add(Calendar.HOUR_OF_DAY, -2);
		am.set(AlarmManager.RTC_WAKEUP, c.getTimeInMillis(), pendIntent);
		
//		intent.putExtra("alarm_msg", "你预定的" + flight + "将于24小时后起飞");
//		c.add(Calendar.HOUR_OF_DAY,-24);
//		am.set(AlarmManager.RTC_WAKEUP, c.getTimeInMillis(), pendIntent);

	}
	
	public void cancelNotification(String takeOffTime, String flight, int _id,
			String tripName, String userName) {
		AlarmManager am = (AlarmManager) context
				.getSystemService(Context.ALARM_SERVICE);
		Date date = DateUtils.StringToDate(takeOffTime.replace("T", " "),
				"yyyy-MM-dd HH:mm");
		Calendar c = Calendar.getInstance();
		c.setTime(date);

		Calendar today = Calendar.getInstance();
		if(c.before(today)) {
			return;
		}
		Intent intent = new Intent(FinalString.TRIP_ALARM_ACTION);
		intent.putExtra("alarm_msg", "你预定的" + flight + "将于24小时后起飞");
		intent.putExtra("alarm_takeOffTime", takeOffTime);
		intent.putExtra("alarm_flight", flight);
		intent.putExtra("alarm_tripName", tripName);
		intent.putExtra("alarm_userName", userName);
		PendingIntent pendIntent = PendingIntent.getBroadcast(context, _id,
				intent, PendingIntent.FLAG_NO_CREATE);
		
		if(pendIntent != null) {
			am.cancel(pendIntent);
		}
	}
}
