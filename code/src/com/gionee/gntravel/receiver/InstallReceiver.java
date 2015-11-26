package com.gionee.gntravel.receiver;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;

public class InstallReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		long downloadID = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);
		SharedPreferences sPreferences = context.getSharedPreferences("config_sp", Context.MODE_APPEND);
		long refernece = sPreferences.getLong("downloadId", 0);
		if (refernece == downloadID) {
			String serviceString = Context.DOWNLOAD_SERVICE;
			DownloadManager dManager = (DownloadManager) context.getSystemService(serviceString);
			Intent install = new Intent(Intent.ACTION_VIEW);
			Uri downloadFileUri = dManager.getUriForDownloadedFile(downloadID);
			install.setDataAndType(downloadFileUri,"application/vnd.android.package-archive");
			install.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			context.startActivity(install);
		}
	}
}
