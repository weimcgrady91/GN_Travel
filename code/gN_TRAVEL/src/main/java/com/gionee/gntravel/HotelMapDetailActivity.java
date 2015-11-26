package com.gionee.gntravel;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.amap.api.maps2d.AMap;
import com.amap.api.maps2d.CameraUpdateFactory;
import com.amap.api.maps2d.MapView;
import com.amap.api.maps2d.UiSettings;
import com.amap.api.maps2d.AMap.InfoWindowAdapter;
import com.amap.api.maps2d.model.BitmapDescriptor;
import com.amap.api.maps2d.model.BitmapDescriptorFactory;
import com.amap.api.maps2d.model.LatLng;
import com.amap.api.maps2d.model.Marker;
import com.amap.api.maps2d.model.MarkerOptions;
import com.gionee.gntravel.entity.Hotel;

/**
 * 地图
 * 
 * @Author: yangxy
 * @Create Date: 2015-4-6
 */
public class HotelMapDetailActivity extends BaseActivity implements OnClickListener,InfoWindowAdapter {
	private static MapView mMapView;
	private static Hotel currentHotel;
	private AMap mAMap;
	private UiSettings mUiSettings;
	BitmapDescriptor bdA = BitmapDescriptorFactory.fromResource(R.drawable.icon_marka);
	BitmapDescriptor bdGround = BitmapDescriptorFactory.fromResource(R.drawable.ground_overlay);
	private TextView tv_title;
	private LinearLayout activity_hotelmap_ll_bottom;
	private LinearLayout activity_map_detail_ll_top;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		try {
			// http://lbs.amap.com/api/android-sdk/guide/overlay/文档地址
			setContentView(R.layout.activity_map);
			mMapView = (com.amap.api.maps2d.MapView) findViewById(R.id.amapsView);
			mMapView.onCreate(savedInstanceState);// 此方法必须重写
			mAMap = mMapView.getMap();
			mAMap.setInfoWindowAdapter(this);
			mUiSettings = mAMap.getUiSettings();
			// 是否启用缩放手势
			mUiSettings.setZoomGesturesEnabled(true);
			// 是否启用平移手势
			mUiSettings.setScrollGesturesEnabled(true);
			// 是否启用指南针图层
			mUiSettings.setCompassEnabled(false);
			findViews();
			initOverLay();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void findViews() {
		currentHotel = (Hotel) getIntent().getSerializableExtra("hotelDetail");
		tv_title = (TextView) findViewById(R.id.tv_title);
		tv_title.setOnClickListener(this);
		tv_title.setText("酒店地图");
		activity_hotelmap_ll_bottom = (LinearLayout) findViewById(R.id.activity_hotelmap_ll_bottom);
		activity_hotelmap_ll_bottom.setVisibility(View.GONE);
		activity_map_detail_ll_top = (LinearLayout) findViewById(R.id.activity_map_detail_ll_top);
		activity_map_detail_ll_top.setVisibility(View.GONE);
	}

	private void initOverLay() {
		if (!TextUtils.isEmpty(currentHotel.getLatitude())) {
			double lat = Double.parseDouble(currentHotel.getLatitude());
			double lng = Double.parseDouble(currentHotel.getLongitude());
			LatLng ll = new LatLng(lat, lng);
			BitmapDescriptor bd = BitmapDescriptorFactory.fromResource(R.drawable.icon_gcoding);
			Marker marker = mAMap.addMarker(
					new MarkerOptions().position(ll).icon(bd).snippet(currentHotel.getAddressLine())
							.title(currentHotel.getHotelName()));
			marker.setObject(currentHotel);
			marker.showInfoWindow();
			mAMap.animateCamera(CameraUpdateFactory.newLatLngZoom(ll, 18));
		}
	}
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.tv_title:
			finish();
			break;

		default:
			break;
		}
		
	}
	/**
	 * 自定义infowinfow窗口
	 */
	public void render(Marker marker, View view) {
		String title = marker.getTitle();
		TextView titleUi = ((TextView) view.findViewById(R.id.title));
		if (title != null) {
			SpannableString titleText = new SpannableString(title);
			titleUi.setText(titleText);

		} else {
			titleUi.setText("");
		}
		String snippet = marker.getSnippet();
		TextView snippetUi = ((TextView) view.findViewById(R.id.snippet));
		if (snippet != null) {
			SpannableString snippetText = new SpannableString(snippet);
			snippetUi.setText(snippetText);
		} else {
			snippetUi.setText("");
		}
	}
	@Override
	public View getInfoContents(Marker marker) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public View getInfoWindow(Marker marker) {
		View infoWindow = getLayoutInflater().inflate(R.layout.custom_info_window, null);
		render(marker, infoWindow);
		return infoWindow;
	}
	@Override
	protected void onPause() {
		// MapView的生命周期与Activity同步，当activity挂起时需调用MapView.onPause()
		if (mMapView != null) {
			mMapView.onPause();
		}
		super.onPause();
	}

	@Override
	protected void onResume() {
		// MapView的生命周期与Activity同步，当activity恢复时需调用MapView.onResume()
		if (mMapView != null) {
			mMapView.onResume();
		}
		super.onResume();
	}

	/**
	 * 方法必须重写
	 */
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		mMapView.onSaveInstanceState(outState);
	}

	@Override
	protected void onDestroy() {
		// MapView的生命周期与Activity同步，当activity销毁时需调用MapView.destroy()
		if (mMapView != null) {
			mMapView.onDestroy();
		}
		super.onDestroy();
	}

}
