package com.gionee.gntravel.receiver;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.gionee.gntravel.R;
import com.gionee.gntravel.TripFormActivity;

public class TripAlarmReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		String msg = intent.getStringExtra("alarm_msg");
		
		Intent targetIntent = new Intent();
		targetIntent.setClass(context, TripFormActivity.class);
		targetIntent.putExtra("tripName", intent.getStringExtra("alarm_tripName"));
		targetIntent.putExtra("userName", intent.getStringExtra("alarm_userName"));
		
		PendingIntent pendingIntent2 = PendingIntent.getActivity(context, 0,targetIntent, 0);
		Notification notify = new Notification.Builder(context)
				.setSmallIcon(R.drawable.travel_launch) 
				.setTicker("商旅通提醒您!")
				.setContentTitle(intent.getStringExtra("alarm_flight"))
				.setContentText(msg)
				.setContentIntent(pendingIntent2) 
				.setNumber(1) 
				.build();
		notify.defaults=Notification.DEFAULT_SOUND;
		notify.flags |= Notification.FLAG_AUTO_CANCEL;
		NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
		manager.notify(1, notify);
		
	}

}
