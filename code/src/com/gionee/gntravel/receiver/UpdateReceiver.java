package com.gionee.gntravel.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.gionee.gntravel.UpdateActivity;

public class UpdateReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		Log.e("weiqun12345","UpdateReceiver");
		Intent updateIntent = new Intent();
		intent.setClass(context, UpdateActivity.class);
		updateIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		context.startActivity(updateIntent);
	}

}
