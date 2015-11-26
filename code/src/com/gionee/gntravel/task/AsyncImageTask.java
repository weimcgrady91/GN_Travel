package com.gionee.gntravel.task;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.widget.RemoteViews;

import com.gionee.gntravel.R;
import com.gionee.gntravel.utils.MD5Util;
import com.gionee.gntravel.widget.TravelWidget;


public class AsyncImageTask extends AsyncTask<String, Integer, Uri> {
	private RemoteViews remoteViews;
	private File cache;
	private Context mContext;
	
	public AsyncImageTask(Context context, RemoteViews remoteViews) {
		 this.mContext = context;
		this.remoteViews = remoteViews;
		cache = new File(context.getExternalCacheDir(), "img");
		if (!cache.exists()) {
			cache.mkdirs();
		} else {
		}
	}

	@Override
	protected Uri doInBackground(String... params) {
		try {
			return getImageURI(params[0], cache);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	protected void onPostExecute(Uri result) {
		super.onPostExecute(result);
		if (remoteViews != null && result != null) {
			remoteViews.setImageViewUri(R.id.img_newsImg, result);
			AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(mContext);
			int[] appWidgetIds =  appWidgetManager.getAppWidgetIds(new ComponentName(mContext, TravelWidget.class));
			appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetIds, R.id.newsListView);
		}
//		Intent updateNews = new Intent(FinalString.UPDATE_WIDGET_NEWS);
//		mContext.sendBroadcast(updateNews);
	}

	public Uri getImageURI(String path, File cache) throws Exception {
		String name = MD5Util.MD5Encrypt(path)+ path.substring(path.lastIndexOf("."));
		File file = new File(cache, name);
		if (file.exists()) {
			return Uri.fromFile(file);
		} else {
			URL url = new URL(path);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setConnectTimeout(5000);
			conn.setRequestMethod("GET");
			conn.setDoInput(true);
			if (conn.getResponseCode() == 200) {
				InputStream is = conn.getInputStream();
				FileOutputStream fos = new FileOutputStream(file);
				byte[] buffer = new byte[1024];
				int len = 0;
				while ((len = is.read(buffer)) != -1) {
					fos.write(buffer, 0, len);
				}
				is.close();
				fos.close();
				return Uri.fromFile(file);
			}
		}
		return null;
	}
}
