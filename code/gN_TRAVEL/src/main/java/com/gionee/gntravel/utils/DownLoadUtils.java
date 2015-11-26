package com.gionee.gntravel.utils;

import com.gionee.gntravel.TravelApplication;

import android.app.DownloadManager;
import android.content.Context;
import android.net.Uri;
import android.os.Environment;

public class DownLoadUtils {
	
	public static void download(Context context,String url) {
		DownloadManager downloadManager = null;
		DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
		String name = url.substring(url.lastIndexOf("/"), url.length());
		request.setDestinationInExternalFilesDir(context,Environment.DIRECTORY_DOWNLOADS, name);
		request.setMimeType("application/vnd.android.package-archive");
		request.allowScanningByMediaScanner();
		request.setVisibleInDownloadsUi(true);
		request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
		if(downloadManager == null) {
			downloadManager = (DownloadManager)context.getSystemService(Context.DOWNLOAD_SERVICE);
		}
		long downloadId = downloadManager.enqueue(request);
		TravelApplication app = (TravelApplication)context.getApplicationContext();
		app.getConfig_sp().putLong("downloadId", downloadId);
	}
}
