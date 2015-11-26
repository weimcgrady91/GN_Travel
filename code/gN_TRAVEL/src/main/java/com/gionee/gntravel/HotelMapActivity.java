package com.gionee.gntravel;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.text.SpannableString;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.amap.api.maps2d.AMap;
import com.amap.api.maps2d.AMap.InfoWindowAdapter;
import com.amap.api.maps2d.AMap.OnInfoWindowClickListener;
import com.amap.api.maps2d.AMap.OnMapClickListener;
import com.amap.api.maps2d.AMap.OnMarkerClickListener;
import com.amap.api.maps2d.CameraUpdateFactory;
import com.amap.api.maps2d.MapView;
import com.amap.api.maps2d.UiSettings;
import com.amap.api.maps2d.model.BitmapDescriptor;
import com.amap.api.maps2d.model.BitmapDescriptorFactory;
import com.amap.api.maps2d.model.LatLng;
import com.amap.api.maps2d.model.LatLngBounds;
import com.amap.api.maps2d.model.Marker;
import com.amap.api.maps2d.model.MarkerOptions;
import com.gionee.gntravel.entity.Hotel;
import com.gionee.gntravel.entity.HotelSearchKeyBean;
import com.gionee.gntravel.fragment.HotelMapBottomFragment;
import com.gionee.gntravel.fragment.HotelMapBottomFragment.onLoadMoreListner;
import com.gionee.gntravel.utils.BMapUtil;
import com.gionee.gntravel.utils.FinalString;
import com.gionee.gntravel.utils.GNConnUtil;
import com.gionee.gntravel.utils.GNConnUtil.GNConnListener;
import com.gionee.gntravel.utils.JSONUtil;
import com.google.gson.reflect.TypeToken;

/**
 * 地图
 * 
 * @Author: yangxy
 * @Create Date: 2015-4-6
 */
public class HotelMapActivity extends BaseActivity implements OnClickListener, GNConnListener, OnMarkerClickListener,
		OnMapClickListener, OnInfoWindowClickListener, InfoWindowAdapter {
	private GNConnUtil post;
	private static MapView mMapView;
	private static List<Hotel> hotelMapInfos;
	private static Hotel currentHotel;
	private AMap mAMap;
	private UiSettings mUiSettings;
	BitmapDescriptor bdA = BitmapDescriptorFactory.fromResource(R.drawable.icon_marka);
	BitmapDescriptor bdGround = BitmapDescriptorFactory.fromResource(R.drawable.ground_overlay);
	private LinearLayout activity_map_ll_search;
	private TextView activity_hotel_map_tv_searchkey;
	private TextView tv_title;
	private HotelMapBottomFragment hotefrg;
	private Marker mMarker;
	private String cityName;
	private HotelSearchKeyBean hotel_searchKeyBean;
	private String date;
	private double maxlat;
	private double minlat;
	private double maxlnt;
	private double minlnt;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		try {
			// http://lbs.amap.com/api/android-sdk/guide/overlay/文档地址
			setContentView(R.layout.activity_map);
			mMapView = (com.amap.api.maps2d.MapView) findViewById(R.id.amapsView);
			mMapView.onCreate(savedInstanceState);// 此方法必须重写
			mAMap = mMapView.getMap();
			mUiSettings = mAMap.getUiSettings();
			// 是否启用缩放手势
			mUiSettings.setZoomGesturesEnabled(true);
			// 隐藏地图默认的缩放按钮
			mUiSettings.setZoomControlsEnabled(false);
			// 是否启用平移手势
			mUiSettings.setScrollGesturesEnabled(true);
			// 是否启用指南针图层
			mUiSettings.setCompassEnabled(false);
			initData();
			findViews();
			initMap();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void initMap() {
		refreshOverlay();
		initOverLay();
		mAMap.setOnMarkerClickListener(this);
		mAMap.setOnMapClickListener(this);
		mAMap.setOnInfoWindowClickListener(this);
		mAMap.setInfoWindowAdapter(this);
	}

	private void refreshOverlay() {
		LatLngBounds.Builder builder = new LatLngBounds.Builder();
		builder.include(new LatLng(maxlat, maxlnt));
		builder.include(new LatLng(minlat, minlnt));

		LatLngBounds bounds = builder.build();
		mAMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 18, 0, 30));
	}

	private void findViews() {
		tv_title = (TextView) findViewById(R.id.tv_title);
		tv_title.setText("酒店地图");
		tv_title.setOnClickListener(this);
		activity_map_ll_search = (LinearLayout) findViewById(R.id.activity_map_ll_search);
		activity_map_ll_search.setOnClickListener(this);
		activity_hotel_map_tv_searchkey = (TextView) findViewById(R.id.activity_hotel_map_tv_searchkey);
		if (hotel_searchKeyBean != null) {
			String seatchKeyName = hotel_searchKeyBean.getName();
			if (!TextUtils.isEmpty(seatchKeyName)) {
				String name_utf8;
				try {
					name_utf8 = URLDecoder.decode(seatchKeyName, "utf-8");
					activity_hotel_map_tv_searchkey.setText(name_utf8);
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				}
			}
		}
	}

	private void initOverLay() {
		for (Hotel hotel : hotelMapInfos) {
			if (!TextUtils.isEmpty(hotel.getLatitude())) {
				double lat = Double.parseDouble(hotel.getLatitude());
				double lng = Double.parseDouble(hotel.getLongitude());
				LatLng ll = new LatLng(lat, lng);
				TextView tv = new TextView(this);
				tv.setTextColor(getResources().getColor(R.color.text_color_v4));
				String price = hotel.getPrice();
				if (!TextUtils.isEmpty("price")) {
					price = "￥" + price.split("\\.")[0] + "起";
					tv.setTextColor(getResources().getColor(R.color.text_color_v1));
					tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 9);
					tv.setGravity(Gravity.CENTER);
					tv.setText(price);
				}
				tv.setBackgroundResource(R.drawable.mappricebg);
				BitmapDescriptor bd = BMapUtil.getBitmapFromViewGaode(tv);
				mAMap.addMarker(
						new MarkerOptions().position(ll).icon(bd).snippet(hotel.getAddressLine())
								.title(hotel.getHotelName())).setObject(hotel);
			}
		}
	}

	private void initData() {
		post = new GNConnUtil();
		post.setGNConnListener(this);
		hotelMapInfos = (List<Hotel>) getIntent().getSerializableExtra("hotelInfo");
		getCenterLoc(hotelMapInfos);
		hotel_searchKeyBean = (HotelSearchKeyBean) getIntent().getSerializableExtra("hotel_searchKeyBean");
		cityName = getIntent().getStringExtra("cityName");
		date = getIntent().getStringExtra("date");
		if (hotefrg == null) {
			hotefrg = new HotelMapBottomFragment();
			hotefrg.setLoadMoreListner(new onLoadMoreListner() {
				@Override
				public void loadMoreSuc(List<Hotel> hotelList, String key) {
					dismissmProgress();
					mAMap.clear();
					if (hotelList != null && hotelList.size() != 0) {
						hotelMapInfos = hotelList;
					}
					if (!TextUtils.isEmpty(key)) {
						activity_hotel_map_tv_searchkey.setText(key);
					}
					getCenterLoc(hotelList);
					refreshOverlay();
					initOverLay();
				}

				@Override
				public void showmProgress() {
					showProgress();
				}

				@Override
				public void dismissmProgress() {
					dismissProgress();
				}

			});
		}
		replaceBottom(hotefrg);
	}

	@Override
	public boolean onMarkerClick(final Marker marker) {
		mMarker = marker;
		mMarker.showInfoWindow();
		return false;
	}

	/**
	 * 得到边界坐标
	 * 
	 * @param hotelList
	 */
	private void getCenterLoc(List<Hotel> hotelList) {
		maxlat = 0;
		minlat = 0;
		maxlnt = 0;
		minlnt = 0;
		for (int i = 0; hotelList != null && hotelList.size() > 0 && i < hotelList.size(); i++) {
			double lat = Double.parseDouble(hotelList.get(i).getLatitude());
			double lgt = Double.parseDouble(hotelList.get(i).getLongitude());
			if (i == 0) {
				maxlat = lat;
				minlat = lat;
			} else {
				if (lat > maxlat) {
					maxlat = lat;
				}
				if (lat < minlat) {
					minlat = lat;
				}
			}
			if (i == 0) {
				maxlnt = lgt;
				minlnt = lgt;
			} else {
				if (lgt > maxlnt) {
					maxlnt = lgt;
				}
				if (lgt < minlnt) {
					minlnt = lgt;
				}
			}
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.tv_title:
			finish();
			break;
		case R.id.activity_map_ll_search:
			Intent searchIntent = new Intent(this, HotelSearchActivity.class);
			String key = activity_hotel_map_tv_searchkey.getText().toString();
			searchIntent.putExtra("searchKey", key);
			searchIntent.putExtra("cityName", cityName);
			searchIntent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
			hotefrg.startActivityForResult(searchIntent, FinalString.HOTEL_MAP_SEARCHKEY_REQCODE);
			break;
		default:
			break;
		}
	}

	@Override
	public void onMapClick(LatLng arg0) {
		if (mMarker != null) {
			mMarker.hideInfoWindow();
			mMarker = null;
		}
	}

	private void replaceBottom(Fragment frg) {
		FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
		if (!frg.isAdded()) {
			ft.replace(R.id.activity_hotelmap_ll_bottom, frg);
			ft.attach(frg);
			ft.commit();
		} else {
			ft.show(frg);
		}
	}

	@Override
	public void onReqSuc(String requestUrl, String json) {
		dismissDialog();
		mAMap.clear();
		hotelMapInfos = JSONUtil.getInstance().fromJSON(new TypeToken<ArrayList<Hotel>>() {
		}.getType(), json);
		initOverLay();
	}

	@Override
	public void onReqFail(String requestUrl, Exception ex, int errorCode) {
		dismissDialog();
	}

	@Override
	public void onInfoWindowClick(Marker marker) {
		currentHotel = (Hotel) marker.getObject();
		Intent intent = new Intent();
		intent.setClass(HotelMapActivity.this, HotelDetailActivity.class);
		Bundle bundle = new Bundle();
		bundle.putSerializable("hotelInfo", currentHotel);
		bundle.putString("currentDate", date);
		intent.putExtras(bundle);
		HotelMapActivity.this.startActivity(intent);
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
	public View getInfoContents(Marker arg0) {
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
