package com.gionee.gntravel.service;

import android.content.Intent;
import android.widget.RemoteViewsService;

public class WidgetTravelRemoteViewsService extends RemoteViewsService {
	@Override
	public RemoteViewsFactory onGetViewFactory(Intent intent) {
		return new WidgetTravelRemoteViewsFactory(getApplicationContext(),
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
