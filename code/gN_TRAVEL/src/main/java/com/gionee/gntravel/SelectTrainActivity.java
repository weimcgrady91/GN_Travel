package com.gionee.gntravel;

import java.util.Calendar;
import java.util.Date;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.gionee.gntravel.provider.GNTravelProviderMetaData;
import com.gionee.gntravel.utils.DateUtils;
import com.gionee.gntravel.utils.NetWorkUtil;
import com.weiqun.customcalendar.CustomCalendar;
import com.weiqun.customcalendar.CustomCalendar.OnDateSelectedListener;

public class SelectTrainActivity extends Activity implements OnClickListener {
	private static final int DEPARTCITY_REQUEST_CODE = 0x100;
	private static final int ARRIVECITY_REQUEST_CODE = 0x101;
	private static final String TAG = "RouteFragment";
	private CustomCalendar calendar;
	private TextView departcity;
	private TextView arrivecity;
	private TextView takeOffTime;
	boolean isRound = false;
	private TextView weekDay;
	private ViewGroup calendar_content;
	private String dateString = null;
	public LocationClient mLocationClient = null;
	public BDLocationListener myListener = new MyLocationListener();
	protected Date targetDate = null;
	private Animation outAnimation;
	private ViewGroup rl_departcity;
	private ViewGroup rl_arrivecity;
	private ViewGroup rl_date;
	private View startRoute;

	ImageView imgBack;// 返回建
	TextView tvTitle;// 标题名称

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_select_train);
		setupView();

	}

	private void setupView() {
		tvTitle = (TextView) findViewById(R.id.tv_title);
		tvTitle.setText(R.string.train_lookup);
		tvTitle.setOnClickListener(this);
		departcity = (TextView) findViewById(R.id.departcity);
		rl_departcity = (ViewGroup) findViewById(R.id.rl_departcity);
		rl_departcity.setOnClickListener(this);
		arrivecity = (TextView) findViewById(R.id.arrivecity);
		rl_arrivecity = (ViewGroup) findViewById(R.id.rl_arrivecity);
		rl_arrivecity.setOnClickListener(this);
		takeOffTime = (TextView) findViewById(R.id.takeOffTime);
		rl_date = (ViewGroup) findViewById(R.id.rl_date);
		rl_date.setOnClickListener(this);
		weekDay = (TextView) findViewById(R.id.week_day);
		startRoute = (View) findViewById(R.id.startRoute);
		startRoute.setOnClickListener(this);
		calendar_content = (ViewGroup) findViewById(R.id.calendar_content);
		calendar_content.setOnClickListener(this);
		calendar = (CustomCalendar) findViewById(R.id.calendar_view);
		initDayOfWeek();
		initCityInfo();
		initCalendar();
		initAnim();
	}

	public class MyLocationListener implements BDLocationListener {
		@Override
		public void onReceiveLocation(BDLocation location) {
			if (location == null) {
				departcity.setText("北京");
				return;
			}
			if (location.getLocType() == BDLocation.TypeNetWorkLocation) {
				String city = location.getCity();
				String cityName = city.substring(0, city.indexOf("市"));
				if (departcity.getText().equals("定位中...")) {
					departcity.setText(cityName);
				}
			}
		}

		public void onReceivePoi(BDLocation poiLocation) {
		}
	}

	public String getCityCode(String cityName) {
		String result = null;
		Uri uri = GNTravelProviderMetaData.Citys_TableMetaData.CONTENT_URI;
		String[] projection = new String[] { GNTravelProviderMetaData.Citys_TableMetaData.CITY_CODE };
		String selection = GNTravelProviderMetaData.Citys_TableMetaData.CITY_NAME
				+ " = ? ";
		String[] selectionArgs = new String[] { cityName };
		Cursor c = this.getContentResolver().query(uri, projection, selection,
				selectionArgs, null);
		if (c.moveToNext() != false) {
			result = c.getString(0);
		} else {
			result = "";
		}
		if (c != null) {
			c.close();
			c = null;
		}
		return result;
	}

	private void initAnim() {
		outAnimation = AnimationUtils.loadAnimation(this,
				R.anim.anim_search_container_out);
		outAnimation.setAnimationListener(new AnimationListener() {

			@Override
			public void onAnimationStart(Animation animation) {

			}

			@Override
			public void onAnimationRepeat(Animation animation) {

			}

			@Override
			public void onAnimationEnd(Animation animation) {
				calendar_content.setVisibility(View.INVISIBLE);
				maintainStates(true);
			}
		});

	}

	public void initCityInfo() {
		if (!NetWorkUtil.isNetworkConnected(this)) {
			departcity.setText("北京");
		} else {
			getLocation();
		}
		arrivecity.setText("上海");
	}

	private void getLocation() {
		departcity.setText("定位中...");
		mLocationClient = new LocationClient(getApplicationContext());
		mLocationClient.registerLocationListener(myListener);
		mLocationClient.setAK("eWeT5sSsDuzlGzfaVGyShoeG");
		LocationClientOption option = new LocationClientOption();
		option.setOpenGps(true);
		option.setAddrType("all");
		option.setCoorType("bd09ll");
		option.setScanSpan(500);
		option.disableCache(true);
		option.setPriority(LocationClientOption.NetWorkFirst);
		mLocationClient.setLocOption(option);
		if (mLocationClient != null) {
			mLocationClient.start();
			mLocationClient.requestLocation();
		} else {
			departcity.setText("北京");
		}
	}

	private void initDayOfWeek() {
		Calendar c = Calendar.getInstance();
		c.add(Calendar.DATE, 1);
		weekDay.setText("( " + DateUtils.getDayOfWeek(c) + " )");
		takeOffTime.setText(DateUtils.dateToDayOfMonth(c.getTime()));
		dateString = DateUtils.dateToStr(c.getTime());
		targetDate = c.getTime();
	}

	public void onDestroy() {
		super.onDestroy();
	}

	public void selectCity(SelectCityActivity.SelectCityType selectType,
			int requestCode) {
		Intent intent = new Intent(this, SelectCityActivity.class);
		intent.putExtra("selectType", selectType);
		intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
		startActivityForResult(intent, requestCode);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.tv_title:
			this.finish();
			break;
		case R.id.rl_departcity:
			selectCity(SelectCityActivity.SelectCityType.DEPARTCITY,
					DEPARTCITY_REQUEST_CODE);
			break;
		case R.id.rl_arrivecity:
			selectCity(SelectCityActivity.SelectCityType.ARRIVECITY,
					ARRIVECITY_REQUEST_CODE);
			break;
		case R.id.startRoute:
			Intent routeIntent = new Intent(this, TrainActivity.class);
			routeIntent.putExtra("TakeOffTime", dateString);
			String departCity = departcity.getText().toString();
			if ("定位中...".equals(departCity)) {
				departCity = "北京";
			}
			routeIntent.putExtra("departcity", departCity);
			routeIntent.putExtra("arrivecity", arrivecity.getText().toString());
			startActivity(routeIntent);
			break;
		case R.id.rl_date:
			showCalendar();
			break;
		case R.id.calendar_content:
			hideCalendar();
			break;
		}
	}

	private void showCalendar() {
		if (calendar_content.getVisibility() == View.INVISIBLE) {
			calendar_content.setVisibility(View.VISIBLE);
			maintainStates(false);
			calendar.setAnimation(AnimationUtils.loadAnimation(this,
					R.anim.anim_search_container_in));
		}
	}

	private void hideCalendar() {
		if (calendar_content.getVisibility() == View.VISIBLE) {
			calendar.startAnimation(outAnimation);
		}
	}

	private void initCalendar() {
		final Calendar nextYear = Calendar.getInstance();
		nextYear.add(Calendar.MONTH, 5);
		final Calendar lastYear = Calendar.getInstance();
		lastYear.add(Calendar.MONTH, 0);
		calendar.initCalendar(lastYear.getTime(), nextYear.getTime())
				.withSelectedDates(targetDate);
		calendar.setOnDateSelectedListener(new OnDateSelectedListener() {

			@Override
			public void onDateUnselected(Date date) {

			}

			@Override
			public void onDateSelected(Date date) {
				Calendar c2 = Calendar.getInstance();
				c2.setTime(date);
				takeOffTime.setText(DateUtils.dateToDayOfMonth(date));
				dateString = DateUtils.dateToStr(date);
				weekDay.setText("( " + DateUtils.getDayOfWeek(c2) + " )");
				targetDate = date;
				calendar.startAnimation(outAnimation);
			}
		});

	}

	private void maintainStates(boolean flag) {
		rl_departcity.setEnabled(flag);
		rl_arrivecity.setEnabled(flag);
		rl_date.setEnabled(flag);
		startRoute.setEnabled(flag);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == event.KEYCODE_BACK) {
			if (calendar_content.getVisibility() == View.VISIBLE) {
				calendar.startAnimation(outAnimation);
				return true;
			}

		}
		return super.onKeyDown(keyCode, event);
	}

	@SuppressWarnings("static-access")
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == this.RESULT_OK) {
			switch (requestCode) {
			case DEPARTCITY_REQUEST_CODE:
				departcity.setText(data.getStringExtra("cityName"));
				break;
			case ARRIVECITY_REQUEST_CODE:
				arrivecity.setText(data.getStringExtra("cityName"));
				break;
			default:
			}
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

}
