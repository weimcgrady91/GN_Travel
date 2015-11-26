package com.gionee.gntravel;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.amap.api.location.AMapLocation;
import com.amap.api.maps2d.AMap;
import com.amap.api.maps2d.AMap.InfoWindowAdapter;
import com.amap.api.maps2d.AMap.OnMarkerClickListener;
import com.amap.api.maps2d.MapView;
import com.amap.api.maps2d.UiSettings;
import com.amap.api.maps2d.model.Marker;
import com.amap.api.maps2d.overlay.BusRouteOverlay;
import com.amap.api.maps2d.overlay.DrivingRouteOverlay;
import com.amap.api.maps2d.overlay.WalkRouteOverlay;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.route.BusPath;
import com.amap.api.services.route.BusRouteResult;
import com.amap.api.services.route.DrivePath;
import com.amap.api.services.route.DriveRouteResult;
import com.amap.api.services.route.RouteSearch;
import com.amap.api.services.route.RouteSearch.BusRouteQuery;
import com.amap.api.services.route.RouteSearch.DriveRouteQuery;
import com.amap.api.services.route.RouteSearch.OnRouteSearchListener;
import com.amap.api.services.route.RouteSearch.WalkRouteQuery;
import com.amap.api.services.route.WalkPath;
import com.amap.api.services.route.WalkRouteResult;
import com.gionee.gntravel.utils.LocationManager;
import com.gionee.gntravel.utils.LocationManager.LocationListener;
import com.gionee.gntravel.utils.LocationManagerImpl;
import com.gionee.gntravel.utils.ToastUtil;

public class MapRouteActivity extends BaseActivity implements OnMarkerClickListener, InfoWindowAdapter,
		OnRouteSearchListener, OnClickListener, LocationListener {
	private AMap aMap;
	private MapView mapView;
	private Button drivingButton;
	private Button busButton;
	private Button walkButton;
	private int busMode = RouteSearch.BusDefault;// 公交默认模式
	private int drivingMode = RouteSearch.DrivingDefault;// 驾车默认模式
	private int walkMode = RouteSearch.WalkDefault;// 步行默认模式
	private BusRouteResult busRouteResult;// 公交模式查询结果
	private DriveRouteResult driveRouteResult;// 驾车模式查询结果
	private WalkRouteResult walkRouteResult;// 步行模式查询结果
	private int routeType = 1;// 1代表公交模式，2代表驾车模式，3代表步行模式
	private LatLonPoint startPoint = null;
	private LatLonPoint endPoint = null;
	private RouteSearch routeSearch;
	public ArrayAdapter<String> aAdapter;
	private TextView activity_map_route_distance;
	private TextView routedetail;
	private float endLat;
	private float endLon;
	private List<Marker> markers;
	private String currentCity;

	@Override
	protected void onCreate(Bundle bundle) {
		super.onCreate(bundle);
		setContentView(R.layout.activity_map_route);
		mapView = (MapView) findViewById(R.id.map);
		mapView.onCreate(bundle);// 此方法必须重写
		init();
		locate();// 去定位
	}

	private void locate() {
		showProgress();
		LocationManager locationManager = new LocationManagerImpl(this);
		locationManager.requestLocation();
		locationManager.setLocationManager(this);
	}

	/**
	 * 初始化AMap对象
	 */
	private void init() {
		if (aMap == null) {
			aMap = mapView.getMap();
			registerListener();
		}
		UiSettings mUISettings = aMap.getUiSettings();
		mUISettings.setZoomControlsEnabled(false);
		routeSearch = new RouteSearch(this);
		routeSearch.setRouteSearchListener(this);
		busButton = (Button) findViewById(R.id.transit);
		busButton.setOnClickListener(this);
		drivingButton = (Button) findViewById(R.id.drive);
		drivingButton.setOnClickListener(this);
		walkButton = (Button) findViewById(R.id.walk);
		walkButton.setOnClickListener(this);
		activity_map_route_distance = (TextView) findViewById(R.id.route_distance);
		routedetail = (TextView) findViewById(R.id.routedetail);
		routedetail.setOnClickListener(this);
		findViewById(R.id.iv_back).setOnClickListener(this);
		String latStr = getIntent().getStringExtra("latitude");
		if (!TextUtils.isEmpty(latStr)) {
			endLat = Float.parseFloat(latStr);
		}
		String lonStr = getIntent().getStringExtra("longitude");
		if (!TextUtils.isEmpty(lonStr)) {
			endLon = Float.parseFloat(lonStr);
		}
	}

	/**
	 * 选择公交模式
	 */
	private void busRoute() {
		routeType = 1;// 标识为公交模式
		busMode = RouteSearch.BusDefault;
		drivingButton.setBackgroundResource(0);
		busButton.setBackgroundResource(R.drawable.title_btn_pressed_line);
		walkButton.setBackgroundResource(0);

	}

	/**
	 * 选择驾车模式
	 */
	private void drivingRoute() {
		routeType = 2;// 标识为驾车模式
		drivingButton.setBackgroundResource(R.drawable.title_btn_pressed_line);
		busButton.setBackgroundResource(0);
		walkButton.setBackgroundResource(0);
	}

	/**
	 * 选择步行模式
	 */
	private void walkRoute() {
		routeType = 3;// 标识为步行模式
		walkMode = RouteSearch.WalkMultipath;
		drivingButton.setBackgroundResource(0);
		busButton.setBackgroundResource(0);
		walkButton.setBackgroundResource(R.drawable.title_btn_pressed_line);
	}

	@Override
	public boolean onMarkerClick(Marker marker) {
		if (marker.isInfoWindowShown()) {
			marker.hideInfoWindow();
		} else {
			marker.showInfoWindow();
		}
		return false;
	}

	@Override
	public View getInfoContents(Marker marker) {
		return null;
	}

	@Override
	public View getInfoWindow(Marker marker) {
		return null;
	}

	/**
	 * 注册监听
	 */
	private void registerListener() {
		aMap.setOnMarkerClickListener(MapRouteActivity.this);
		aMap.setInfoWindowAdapter(MapRouteActivity.this);
	}


	/**
	 * 开始搜索路径规划方案
	 */
	
	public void searchRouteResult(LatLonPoint startPoint, LatLonPoint endPoint) {
		showProgress();
		final RouteSearch.FromAndTo fromAndTo = new RouteSearch.FromAndTo(startPoint, endPoint);
		if (routeType == 1) {// 公交路径规划
			BusRouteQuery query = new BusRouteQuery(fromAndTo, busMode, currentCity, 0);// 第一个参数表示路径规划的起点和终点，第二个参数表示公交查询模式，第三个参数表示公交查询城市区号，第四个参数表示是否计算夜班车，0表示不计算
			routeSearch.calculateBusRouteAsyn(query);// 异步路径规划公交模式查询
		} else if (routeType == 2) {// 驾车路径规划
			DriveRouteQuery query = new DriveRouteQuery(fromAndTo, drivingMode, null, null, "");// 第一个参数表示路径规划的起点和终点，第二个参数表示驾车模式，第三个参数表示途经点，第四个参数表示避让区域，第五个参数表示避让道路
			routeSearch.calculateDriveRouteAsyn(query);// 异步路径规划驾车模式查询
		} else if (routeType == 3) {// 步行路径规划
			WalkRouteQuery query = new WalkRouteQuery(fromAndTo, walkMode);
			routeSearch.calculateWalkRouteAsyn(query);// 异步路径规划步行模式查询
		}
	}

	/**
	 * 公交路线查询回调
	 */
	@Override
	public void onBusRouteSearched(BusRouteResult result, int rCode) {
		dismissProgress();
		if (rCode == 0) {
			if (result != null && result.getPaths() != null && result.getPaths().size() > 0) {
				busRouteResult = result;
				BusPath busPath = busRouteResult.getPaths().get(0);
				aMap.clear();// 清理地图上的所有覆盖物
				BusRouteOverlay routeOverlay = new BusRouteOverlay(this, aMap, busPath, busRouteResult.getStartPos(),
						busRouteResult.getTargetPos());
				routeOverlay.removeFromMap();
				routeOverlay.addToMap();
				routeOverlay.zoomToSpan();
				float dis = busPath.getDistance();
				setDistance(dis);
				markers = aMap.getMapScreenMarkers();
			} else {
				ToastUtil.show(MapRouteActivity.this, R.string.no_result);
			}
		} else if (rCode == 27) {
			ToastUtil.show(MapRouteActivity.this, R.string.error_network);
		} else if (rCode == 32) {
			ToastUtil.show(MapRouteActivity.this, R.string.error_key);
		} else {
			ToastUtil.show(MapRouteActivity.this, getString(R.string.error_other) + rCode);
		}
	}

	/**
	 * 驾车结果回调
	 */
	@Override
	public void onDriveRouteSearched(DriveRouteResult result, int rCode) {
		dismissProgress();
		if (rCode == 0) {
			if (result != null && result.getPaths() != null && result.getPaths().size() > 0) {
				driveRouteResult = result;
				DrivePath drivePath = driveRouteResult.getPaths().get(0);
				aMap.clear();// 清理地图上的所有覆盖物
				DrivingRouteOverlay drivingRouteOverlay = new DrivingRouteOverlay(this, aMap, drivePath,
						driveRouteResult.getStartPos(), driveRouteResult.getTargetPos());
				drivingRouteOverlay.removeFromMap();
				drivingRouteOverlay.addToMap();
				drivingRouteOverlay.zoomToSpan();
				float dis = drivePath.getDistance();
				setDistance(dis);
				markers = aMap.getMapScreenMarkers();
			} else {
				ToastUtil.show(MapRouteActivity.this, R.string.no_result);
			}
		} else if (rCode == 27) {
			ToastUtil.show(MapRouteActivity.this, R.string.error_network);
		} else if (rCode == 32) {
			ToastUtil.show(MapRouteActivity.this, R.string.error_key);
		} else {
			ToastUtil.show(MapRouteActivity.this, getString(R.string.error_other) + rCode);
		}
	}

	private void setDistance(Float dis) {
		String disStr = "--";
		if (dis >= 1000) {
			DecimalFormat format = new DecimalFormat("#.0");
			disStr = format.format((float) (dis / 1000)) + "公里";
		} else {
			disStr = dis + "米";
		}
		activity_map_route_distance.setText(disStr);
	}

	/**
	 * 步行路线结果回调
	 */
	@Override
	public void onWalkRouteSearched(WalkRouteResult result, int rCode) {
		dismissProgress();
		if (rCode == 0) {
			if (result != null && result.getPaths() != null && result.getPaths().size() > 0) {
				walkRouteResult = result;
				WalkPath walkPath = walkRouteResult.getPaths().get(0);
				aMap.clear();// 清理地图上的所有覆盖物
				WalkRouteOverlay walkRouteOverlay = new WalkRouteOverlay(this, aMap, walkPath,
						walkRouteResult.getStartPos(), walkRouteResult.getTargetPos());
				walkRouteOverlay.removeFromMap();
				walkRouteOverlay.addToMap();
				walkRouteOverlay.zoomToSpan();
				float dis = walkPath.getDistance();
				setDistance(dis);
				markers = aMap.getMapScreenMarkers();
			} else {
				ToastUtil.show(MapRouteActivity.this, R.string.no_result);
			}
		} else if (rCode == 27) {
			ToastUtil.show(MapRouteActivity.this, R.string.error_network);
		} else if (rCode == 32) {
			ToastUtil.show(MapRouteActivity.this, R.string.error_key);
		} else {
			ToastUtil.show(MapRouteActivity.this, getString(R.string.error_other) + rCode);
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.transit:
			busRoute();
			searchRouteResult(startPoint, endPoint);
			break;
		case R.id.drive:
			drivingRoute();
			searchRouteResult(startPoint, endPoint);
			break;
		case R.id.walk:
			walkRoute();
			searchRouteResult(startPoint, endPoint);
			break;
		case R.id.iv_back:
			finish();
			break;
		case R.id.routedetail:
			Intent routeDetailIntent = new Intent(this, MapRouteDetailActivity.class);
			ArrayList<String> routeDetailList = new ArrayList<String>();
			for (int i = 0; i < markers.size() - 2; i++) {
				Marker marker = markers.get(i);
				routeDetailList.add(marker.getTitle() + marker.getSnippet());
			}
			Bundle b = new Bundle();
			b.putStringArrayList("routeList", routeDetailList);
			routeDetailIntent.putExtras(b);
			startActivity(routeDetailIntent);
			break;

		default:
			break;
		}
	}

	@Override
	public void onLocated(AMapLocation location) {
		startPoint = new LatLonPoint(location.getLatitude(), location.getLongitude());
		endPoint = new LatLonPoint(endLat, endLon);
		currentCity = location.getCity();
		searchRouteResult(startPoint, endPoint);// 进行路径规划搜索
	}

	@Override
	public void onLocatFail(String s) {
		ToastUtil.show(this, s);
	}

	/**
	 * 方法必须重写
	 */
	@Override
	protected void onResume() {
		super.onResume();
		mapView.onResume();
	}

	/**
	 * 方法必须重写
	 */
	@Override
	protected void onPause() {
		super.onPause();
		mapView.onPause();
	}

	/**
	 * 方法必须重写
	 */
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		mapView.onSaveInstanceState(outState);
	}

	/**
	 * 方法必须重写
	 */
	@Override
	protected void onDestroy() {
		super.onDestroy();
		mapView.onDestroy();
	}

}
