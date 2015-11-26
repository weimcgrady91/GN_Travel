package com.gionee.gntravel.receiver;

import org.json.JSONObject;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.util.Log;

import com.gionee.gntravel.MessageCenterActivity;
import com.gionee.gntravel.R;
import com.gionee.gntravel.TravelApplication;
import com.gionee.gntravel.service.SendRidToAPS;
import com.gionee.push.assist.EmptyService;
import com.gionee.push.assist.InfoSendThread;
import com.gionee.push.assist.ReceiverNotifier;

/**
 * Author: wangxin <br/>
 * Date: 12-7-4 <br/>
 */
public class PushReceiver extends BroadcastReceiver {
	public void onReceive(Context context, Intent intent) {

		String action = intent.getAction();
		EmptyService.keepApplication(context);
		if ("com.gionee.cloud.intent.REGISTRATION".equals(action)) {
			String rid;
			// Register RID success
			if (intent.getStringExtra("registration_id") != null) {
				rid = intent.getStringExtra("registration_id");
				// Toast.makeText(context, "rid:" + rid,
				// Toast.LENGTH_LONG).show();

				writeRid(context, rid);

				Intent sendIntent = new Intent(context, SendRidToAPS.class);
				context.startService(sendIntent);
				ReceiverNotifier.getInstance().notifyRidGot(rid);
				new InfoSendThread(context, rid).start();

				// Cancel RID success
			} else if (intent.getStringExtra("cancel_RID") != null) {
				rid = intent.getStringExtra("cancel_RID");
				ReceiverNotifier.getInstance().notifyRidCanceled(rid);
				EmptyService.cancelKeep(context);

				// Failed on Registering RID or Canceling RID.
			} else if (intent.getStringExtra("error") != null) {
				String error = intent.getStringExtra("error");
				ReceiverNotifier.getInstance().notifyRidError(error);
				EmptyService.cancelKeep(context);
			}

			// Have received push messages.
		} else if ("com.gionee.cloud.intent.RECEIVE".equals(action)) {
			try {
				TravelApplication app = (TravelApplication) context
						.getApplicationContext();
				app.getUserInfo_sp().putBoolean("newFlag", true);
				String message = intent.getStringExtra("message");
				JSONObject responseJson;
				responseJson = new JSONObject(message);
				String title = responseJson.getString("title");
				ReceiverNotifier.getInstance().notifyMessage(title);
				showInNotification(context, title);
				EmptyService.cancelKeep(context);
			} catch (Exception e) {
				e.printStackTrace();
			}

		}
	}

	@SuppressWarnings("deprecation")
	public void showInNotification(Context context, String message) {

		NotificationManager notificationManager = (NotificationManager) context
				.getSystemService(Context.NOTIFICATION_SERVICE);

		Notification notification = new Notification();
		notification.icon = R.drawable.travel_launch;
		notification.defaults = Notification.DEFAULT_LIGHTS
				| Notification.DEFAULT_SOUND | Notification.FLAG_AUTO_CANCEL;
		notification.when = System.currentTimeMillis();
		notification.tickerText = message;
		Intent intent = new Intent(context, MessageCenterActivity.class);
		PendingIntent pendingIntent = PendingIntent.getActivity(context, 100,
				intent, PendingIntent.FLAG_UPDATE_CURRENT);
		notification.setLatestEventInfo(context, "商旅通提醒您:", message,
				pendingIntent);
		notificationManager.notify(R.string.app_name, notification);

	}

	public void writeRid(Context mContext, String value) {
		SharedPreferences mPreference = mContext.getSharedPreferences(
				"config_sp", Context.MODE_PRIVATE);
		Editor mEditor = mPreference.edit();
		mEditor.putString("rid", value);
		mEditor.commit();
	}
}
