package com.gionee.gntravel;

import java.util.Calendar;
import java.util.Date;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.baidu.location.BDLocation;
import com.gionee.gntravel.customview.CustomTextView;
import com.gionee.gntravel.utils.BMapUtil;
import com.gionee.gntravel.utils.DateUtils;
import com.weiqun.customcalendar.CustomCalendar;
import com.weiqun.customcalendar.CustomCalendar.OnDateSelectedListener;
import com.youju.statistics.YouJuAgent;

public class NewRouteActivity extends FragmentActivity implements
		View.OnClickListener {
	private static final int DEPARTCITY_REQUEST_CODE = 0x100;
	private static final int ARRIVECITY_REQUEST_CODE = 0x101;
	private CustomCalendar calendar;
	private CustomTextView tv_departCity;
	private CustomTextView tv_arriveCity;
	private TextView tv_takeOffTime;
	private TextView tv_week_day;
	private ViewGroup calendar_content;
	private String dateString;
	protected Date targetDate;
	private Animation outAnimation;
	private MainTabActivity mainTabActivity;
	private LinearLayout rl_date;
	private Button btn_startRoute;
	private TravelApplication app;
	private boolean isShowInMainTabActivity;
	private static final int LOCATIONA = 0x1111;
	private BDLocation location;
	private ResultHandler resultHandler = new ResultHandler();
	private TimeSyncReceiver timeSyncReceiver;
	private class ResultHandler extends Handler {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case LOCATIONA:
				TravelApplication app = (TravelApplication) NewRouteActivity.this.getApplication();
				location = app.getLocation();
				if (location==null) {
					BMapUtil bMapUtil = new BMapUtil(NewRouteActivity.this);
					bMapUtil.getLocation();
					location = app.getLocation();
				}
				break;
			default:
				break;
			}
		}
	}
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_newroute);
		app = (TravelApplication)getApplication();
		findViews();
		initDayOfWeek();
		initCalendar();
		initAnim();
		initCityInfo();
		getLocation();
		timeSyncReceiver = new TimeSyncReceiver();
		IntentFilter filter = new IntentFilter();
		filter.addAction(Intent.ACTION_TIMEZONE_CHANGED);
		filter.addAction(Intent.ACTION_DATE_CHANGED);
		filter.addAction(Intent.ACTION_TIME_CHANGED);
		filter.addAction(Intent.ACTION_TIME_TICK);
		registerReceiver(timeSyncReceiver, filter);
	}
	
	public class TimeSyncReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			initDayOfWeek();
			initCalendar();
		}
	}
	
	private void getLocation() {
		Message msg = Message.obtain();
		msg.what = LOCATIONA;
		resultHandler.sendMessage(msg);
	}

	private void findViews() {
		tv_departCity = (CustomTextView)findViewById(R.id.tv_departCity);
		tv_arriveCity = (CustomTextView)findViewById(R.id.tv_arriveCity);
		tv_takeOffTime = (TextView) findViewById(R.id.tv_takeOffTime);
		tv_departCity.setFocusable(true);
		tv_arriveCity.setFocusable(true);
		rl_date = (LinearLayout) findViewById(R.id.rl_date);
		rl_date.setFocusable(true);
		tv_week_day = (TextView) findViewById(R.id.tv_week_day);
		btn_startRoute = (Button) findViewById(R.id.btn_startRoute);
		calendar_content = (ViewGroup) findViewById(R.id.calendar_content);
		calendar = (CustomCalendar) findViewById(R.id.calendar_view);
		TextView tv_title = (TextView) findViewById(R.id.tv_title);
		tv_title.setOnClickListener(this);
		tv_title.setFocusable(true);
		tv_title.requestFocus();
		Intent intent = getIntent();
		if(intent.getBooleanExtra("oldTrip", false)) {
			tv_title.setText(getString(R.string.continueTravel));
		} else {
			tv_title.setText(getString(R.string.startRoute));
		}
		tv_departCity.setOnClickListener(this);
		tv_arriveCity.setOnClickListener(this);
		rl_date.setOnClickListener(this);
		btn_startRoute.setOnClickListener(this);
		calendar_content.setOnClickListener(this);
		findViewById(R.id.iv_swap).setOnClickListener(this);
		findViewById(R.id.iv_swap).setFocusable(true);
	}
	
	
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		unregisterReceiver(timeSyncReceiver);
	}

	private void initDayOfWeek() {
		Calendar c = Calendar.getInstance();
		c.add(Calendar.DATE, 1);
		tv_week_day.setText("(" + DateUtils.getDayOfWeek(c) +")");
		tv_takeOffTime.setText(DateUtils.dateToDayOfMonth(c.getTime()));
		dateString = DateUtils.dateToStr(c.getTime());
		targetDate = c.getTime();
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
				tv_takeOffTime.setText(DateUtils.dateToDayOfMonth(date));
				dateString = DateUtils.dateToStr(date);
				tv_week_day.setText(DateUtils.getDayOfWeek(c2));
				targetDate = date;
				hideCalendar();
				
			}
		});
	}
	
	private void initAnim() {
		outAnimation = AnimationUtils.loadAnimation(this,R.anim.anim_search_container_out);
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
			}
		});
	}
	
	public void initCityInfo() {
		String departCity = app.getUserInfo_sp().getString("departCity");
		if(!TextUtils.isEmpty(departCity) ) {
			tv_departCity.setText(departCity);
		} else {
			tv_departCity.setText("北京");
		}
		String arriveCity = app.getUserInfo_sp().getString("arriveCity");
		if(!TextUtils.isEmpty(arriveCity) ) {
			tv_arriveCity.setText(arriveCity);
		} else {
			tv_arriveCity.setText("上海");
		}
	}
	
	public void selectCity(SelectCityActivity.SelectCityType selectType, int requestCode) {
		Intent intent = new Intent(NewRouteActivity.this, SelectCityActivity.class);
		intent.putExtra("selectType", selectType);
		intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
		startActivityForResult(intent, requestCode);
	}
	
	private void swapCity() {
		String tmpCity = tv_departCity.getText().toString();
		tv_departCity.setText(tv_arriveCity.getText());
		app.getUserInfo_sp().putString("departCity", tv_arriveCity.getText().toString());
		tv_arriveCity.setText(tmpCity);
		app.getUserInfo_sp().putString("arriveCity", tmpCity);
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == NewRouteActivity.RESULT_OK) {
			switch (requestCode) {
			case DEPARTCITY_REQUEST_CODE:
				tv_departCity.setText(data.getStringExtra("cityName"));
				app.getUserInfo_sp().putString("departCity", data.getStringExtra("cityName"));
				break;
			case ARRIVECITY_REQUEST_CODE:
				tv_arriveCity.setText(data.getStringExtra("cityName"));
				app.getUserInfo_sp().putString("arriveCity", data.getStringExtra("cityName"));
				break;
			default:
			}
		}
		super.onActivityResult(requestCode, resultCode, data);
	}
	
	private void startNewRoute() {
		Intent srcIntent = getIntent();
//		if (srcIntent.getExtras() != null) {
//			boolean oldTrip = srcIntent.getExtras()
//					.getBoolean("oldTrip", false);
//			if (!oldTrip) {
//				setTripInfo();
//			}
//		} else {
//			setTripInfo();
//		}
		if(app.getTripState()) {
			setTripInfo();
		}
		
		app.setDepartcity(tv_departCity.getText().toString());
		app.setArrivecity(tv_arriveCity.getText().toString());
		app.setTakeOffDate(dateString);
		
		Intent intent = new Intent(this, ListOfMessageActivity.class);
		intent.putExtra("TakeOffTime", dateString);
		intent.putExtra("departCity", tv_departCity.getText().toString());
		intent.putExtra("arriveCity", tv_arriveCity.getText().toString());
		startActivity(intent);
		YouJuAgent.onEvent(this, getString(R.string.youju_flight_hotel));
	}
	
	private void setTripInfo() {
		app.setTripName(dateString + "T" + tv_departCity.getText() + "-"
				+ tv_arriveCity.getText());
	}
	
	private void showCalendar() {
		if (calendar_content.getVisibility() == View.INVISIBLE) {
			calendar_content.setVisibility(View.VISIBLE);
			calendar.setAnimation(AnimationUtils.loadAnimation(this,
					R.anim.anim_search_container_in));
		}

	}

	private boolean hideCalendar() {
		if (isShowInMainTabActivity) {
			mainTabActivity.setScanScroll(false);
		}
		if (calendar_content.getVisibility() == View.VISIBLE) {
			calendar.startAnimation(outAnimation);
			return true;
		}
		return false;
	}
	
	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.tv_title:
			finish();
			break;
		case R.id.tv_departCity:
			selectCity(SelectCityActivity.SelectCityType.DEPARTCITY, DEPARTCITY_REQUEST_CODE);
			break;
		case R.id.tv_arriveCity:
			selectCity(SelectCityActivity.SelectCityType.ARRIVECITY, ARRIVECITY_REQUEST_CODE);
			break;
		case R.id.btn_startRoute:
			startNewRoute();
			break;
		case R.id.rl_date:
			showCalendar();
			break;
		case R.id.calendar_content:
			hideCalendar();
			break;
		case R.id.iv_swap:
			swapCity();
			break;
		default:
			break;
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if(calendar_content.getVisibility() == View.VISIBLE) {
				hideCalendar();
				return true;
			}
		}
		return super.onKeyDown(keyCode, event);
	}
	
}
