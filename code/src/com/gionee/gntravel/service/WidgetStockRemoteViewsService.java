package com.gionee.gntravel.service;

import android.content.Intent;
import android.widget.RemoteViewsService;

public class WidgetStockRemoteViewsService extends RemoteViewsService{

	@Override
	public RemoteViewsFactory onGetViewFactory(Intent intent) {
		return new WidgetStockRemoteViewsFactory(getApplicationContext(),
				intent);
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		return START_REDELIVER_INTENT;
	}

	@Override
	public void onCreate() {
		super.onCreate();
	}

}
