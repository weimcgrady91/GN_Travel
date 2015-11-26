package com.gionee.gntravel.utils;

import android.content.Context;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.location.LocationManagerProxy;
import com.amap.api.location.LocationProviderProxy;

/**
 * @Author:yangxy
 * @Create 2015-4-6
 */
public class LocationManagerImpl implements LocationManager, AMapLocationListener, Runnable {
	private AMapLocation aMapLocation;// 用于判断定位超时
	private LocationManagerProxy aMapLocManager = null;
	private Context mContext;
	private Handler handler = new Handler();

	public LocationManagerImpl(Context context) {
		this.mContext = context;
		aMapLocManager = LocationManagerProxy.getInstance(mContext);
	}

	@Override
	public void requestLocation() {
		aMapLocManager.requestLocationData(LocationProviderProxy.AMapNetwork, 2000, 10, this);
		aMapLocManager.setGpsEnable(true);
		handler.postDelayed(this, 12000);// 设置超过12秒还没有定位到就停止定位
		
	}

	private LocationListener listener;

	@Override
	public void setLocationManager(LocationListener l) {
		this.listener = l;
	}

	/**
	 * 销毁定位
	 */
	private void stopLocation() {
		if (aMapLocManager != null) {
			aMapLocManager.removeUpdates(this);
			aMapLocManager.destory();
		}
		aMapLocManager = null;
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
	}

	@Override
	public void onProviderEnabled(String provider) {
	}

	@Override
	public void onProviderDisabled(String provider) {
	}

	/**
	 * 定位完成回调
	 */
	@Override
	public void onLocationChanged(AMapLocation amapLocation) {
		if (amapLocation != null && amapLocation.getAMapException().getErrorCode() == 0) {
			this.aMapLocation = amapLocation;
			listener.onLocated(amapLocation);
			stopLocation();
		}
	}

	
	@Override
	public void run() {
		if (aMapLocation == null) {
			listener.onLocatFail("定位失败!");
			stopLocation();// 销毁掉定位
		}
	}

	@Override
	public void onLocationChanged(Location location) {
		
	}

}
