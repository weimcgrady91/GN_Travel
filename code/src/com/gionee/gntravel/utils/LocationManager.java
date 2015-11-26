package com.gionee.gntravel.utils;

import com.amap.api.location.AMapLocation;

/**
 * @Author:yangxy
 * @Create 2015-4-6
 */
public interface LocationManager {
	// 请求定位
	public void requestLocation();

	// 监听定位成功
	public void setLocationManager(LocationListener l);

	public static interface LocationListener {
		public void onLocated(AMapLocation location);

		public void onLocatFail(String s);
	}
}
