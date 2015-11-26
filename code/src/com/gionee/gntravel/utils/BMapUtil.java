package com.gionee.gntravel.utils;

import android.app.Activity;
import android.graphics.Bitmap;
import android.view.View;
import android.widget.Toast;

import com.amap.api.maps2d.AMapUtils;
import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.geocode.GeoCodeOption;
import com.baidu.mapapi.search.geocode.GeoCodeResult;
import com.baidu.mapapi.search.geocode.GeoCoder;
import com.baidu.mapapi.search.geocode.OnGetGeoCoderResultListener;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;
import com.baidu.mapapi.utils.CoordinateConverter;
import com.baidu.mapapi.utils.CoordinateConverter.CoordType;
import com.gionee.gntravel.TravelApplication;

public class BMapUtil implements OnGetGeoCoderResultListener {
	static GeoCoder mSearch = null; // 搜索模块，也可去掉地图模块独立使用
	BaiduMap mBaiduMap = null;
	private Activity mContext;
	private GeoCodeListner codeListner;
	public static LocationClient mLocationClient = null;
	private final static double x_pi = 3.14159265358979324 * 3000.0 / 180.0;
	public BDLocationListener myListener = new MyLocationListener();

	/**
	 * 从view 得到图片
	 * 
	 * @param view
	 * @return
	 */
	public BMapUtil(Activity context) {
		try {
			mContext = context;
			mSearch = GeoCoder.newInstance();
			mSearch.setOnGetGeoCodeResultListener(this);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public class MyLocationListener implements BDLocationListener {
		private double lat;
		private double lnt;

		@Override
		public void onReceiveLocation(BDLocation location) {
			if (location == null) {
				return;
			}
			if (location.getLocType() == BDLocation.TypeNetWorkLocation) {
				String city = location.getCity();
				String adrStr = location.getAddrStr();
				String street = location.getStreet();
				TravelApplication app = (TravelApplication) mContext
						.getApplication();
				app.setLocation(location);
				app.setLocationCity(city);
			}
		}

		public void onReceivePoi(BDLocation poiLocation) {
		}
	}

	public static BitmapDescriptor getBitmapFromView(View view) {
		view.destroyDrawingCache();
		view.measure(View.MeasureSpec.makeMeasureSpec(0,
				View.MeasureSpec.UNSPECIFIED), View.MeasureSpec
				.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
		view.layout(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight());
		view.setDrawingCacheEnabled(true);
		Bitmap bitmap = view.getDrawingCache(true);
		BitmapDescriptor bd = BitmapDescriptorFactory.fromBitmap(bitmap);
		return bd;
	}

	public static com.amap.api.maps2d.model.BitmapDescriptor getBitmapFromViewGaode(View view) {
		view.destroyDrawingCache();
		view.measure(View.MeasureSpec.makeMeasureSpec(0,
				View.MeasureSpec.UNSPECIFIED), View.MeasureSpec
				.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
		view.layout(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight());
		view.setDrawingCacheEnabled(true);
		Bitmap bitmap = view.getDrawingCache(true);
		com.amap.api.maps2d.model.BitmapDescriptor bd = com.amap.api.maps2d.model.BitmapDescriptorFactory
				.fromBitmap(bitmap);
		return bd;
	}

	public static LatLng GD2BD(LatLng ll) {
		CoordinateConverter c = new CoordinateConverter();
		c.from(CoordType.COMMON);
		c.coord(ll);
		LatLng bd_ll = c.convert();
		return bd_ll;
	}

	/**
	 * 发起搜索
	 * 
	 * @param v
	 */
	public void SearchButtonProcess(String city, String address) {
		// Geo搜索
		mSearch.geocode(new GeoCodeOption().city(city).address(address));
	}

	@Override
	public void onGetGeoCodeResult(GeoCodeResult result) {
		if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
			Toast.makeText(mContext, "抱歉，未能找到结果", Toast.LENGTH_LONG).show();
		}
		double lat = result.getLocation().latitude;
		double lng = result.getLocation().longitude;

		codeListner.onGeoCode(lat, lng);
	}

	@Override
	public void onGetReverseGeoCodeResult(ReverseGeoCodeResult arg0) {

	}

	public void setGeoCodeListner(GeoCodeListner l) {
		this.codeListner = l;
	}

	public interface GeoCodeListner {
		public void onGeoCode(double lat, double lng);
	}

	/**
	 * 高德坐标转为百度坐标
	 */
	public static Double[] gg2bd(double gg_lat, double gg_lon) {
		double x = gg_lon, y = gg_lat;
		double z = Math.sqrt(x * x + y * y) + 0.00002 * Math.sin(y * x_pi);
		double theta = Math.atan2(y, x) + 0.000003 * Math.cos(x * x_pi);
		double bd_lon = z * Math.cos(theta) + 0.0065; // 经度
		double bd_lat = z * Math.sin(theta) + 0.006; // 纬度
		Double[] doubles_bd = new Double[] { bd_lon, bd_lat };
		return doubles_bd;
	}

	/**
	 * 百度坐标转为高德坐标
	 */
	public static Double[] bd2gg(double bd_lat, double bd_lon) {
		// 39.967451 116,328847//百度坐标
		// [39.961660, 116.32227]//转换后坐标
		// 39.964084 116.321102//实际坐标
		double x = bd_lon - 0.0065, y = bd_lat - 0.006;
		double z = Math.sqrt(x * x + y * y) - 0.00002 * Math.sin(y * x_pi);
		double theta = Math.atan2(y, x) - 0.000003 * Math.cos(x * x_pi);
		double gg_lon = z * Math.cos(theta); // 经度
		double gg_lat = z * Math.sin(theta); // 纬度
		Double[] doubles_gg = new Double[] { gg_lat, gg_lon };
		return doubles_gg;
	}

	public static Double getDistance(com.amap.api.maps2d.model.LatLng latlng1,
			com.amap.api.maps2d.model.LatLng latlng2) {
		double dis = AMapUtils.calculateLineDistance(latlng1, latlng2);
		return dis;
	}

	/**
	 * 定位当前位置
	 * 
	 * @return
	 */
	public BDLocation getLocation() {
		mLocationClient = new LocationClient(mContext.getApplicationContext());
		// mLocationClient.registerLocationListener(myListener);
		// mLocationClient.setAK("eWeT5sSsDuzlGzfaVGyShoeG");
		// LocationClientOption option = new LocationClientOption();
		// option.setOpenGps(false);
		// option.setAddrType("all");
		// option.setServiceName("com.baidu.location.service_v2.9");

		mLocationClient.registerLocationListener(myListener);
		// mLocationClient.setAK("eWeT5sSsDuzlGzfaVGyShoeG");
		LocationClientOption option = new LocationClientOption();
		option.setOpenGps(false);
		option.setAddrType("all");
		option.setServiceName("com.baidu.location.service_v2.9");
		// 返回国测局经纬度坐标系 coor=gcj02
		// 返回百度墨卡托坐标系 coor=bd09
		// 返回百度经纬度坐标系 coor=bd09ll
		option.setCoorType("gcj02");
		option.setScanSpan(900);
		option.disableCache(true);
		option.setPriority(LocationClientOption.NetWorkFirst);
		mLocationClient.setLocOption(option);
		if (mLocationClient != null) {
			mLocationClient.start();
			mLocationClient.requestLocation();
		} else {

		}
		return null;
	}
}
