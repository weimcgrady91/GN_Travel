package com.gionee.gntravel.fragment;

import java.util.Calendar;
import java.util.Date;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.TextView;

import com.gionee.gntravel.ListOfMessageActivity;
import com.gionee.gntravel.MainTabActivity;
import com.gionee.gntravel.NewRouteActivity;
import com.gionee.gntravel.R;
import com.gionee.gntravel.SelectCityActivity;
import com.gionee.gntravel.TravelApplication;
import com.gionee.gntravel.utils.DateUtils;
import com.gionee.gntravel.utils.OnBack;
import com.weiqun.customcalendar.CustomCalendar;
import com.weiqun.customcalendar.CustomCalendar.OnDateSelectedListener;

public class RouteFragment extends ListBaseFragment implements View.OnClickListener,
		OnBack {
	private static final int DEPARTCITY_REQUEST_CODE = 0x100;
	private static final int ARRIVECITY_REQUEST_CODE = 0x101;
	private CustomCalendar calendar;
	private TextView tv_departCity;
	private TextView tv_arriveCity;
	private View view;
	private TextView tv_takeOffTime;
	private TextView tv_week_day;
	private ViewGroup calendar_content;
	private String dateString;
	protected Date targetDate;
	private Animation outAnimation;
	private MainTabActivity mainTabActivity;
	private ViewGroup rl_date;
	private Button btn_startRoute;
	private TravelApplication app;
	private boolean isShowInMainTabActivity;

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		if (activity instanceof MainTabActivity) {
			isShowInMainTabActivity = true;
			mainTabActivity = (MainTabActivity) activity;
		}
		if (activity instanceof NewRouteActivity) {
			isShowInMainTabActivity = false;
		}
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.fragment_route, null);
		tv_departCity = (TextView) view.findViewById(R.id.tv_departCity);
		tv_departCity.setOnClickListener(this);
		tv_arriveCity = (TextView) view.findViewById(R.id.tv_arriveCity);
		tv_arriveCity.setOnClickListener(this);
		tv_takeOffTime = (TextView) view.findViewById(R.id.tv_takeOffTime);
		rl_date = (ViewGroup) view.findViewById(R.id.rl_date);
		rl_date.setOnClickListener(this);
		tv_week_day = (TextView) view.findViewById(R.id.tv_week_day);
		btn_startRoute = (Button) view.findViewById(R.id.btn_startRoute);
		btn_startRoute.setOnClickListener(this);
		calendar_content = (ViewGroup) view.findViewById(R.id.calendar_content);
		calendar_content.setOnClickListener(this);
		calendar = (CustomCalendar) view.findViewById(R.id.calendar_view);
		app = (TravelApplication) getActivity().getApplication();
		view.findViewById(R.id.iv_swap).setOnClickListener(this);

		return view;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		initDayOfWeek();
		initCalendar();
		initAnim();
		initCityInfo();
		super.onActivityCreated(savedInstanceState);
	}

	@Override
	public void onStart() {
		super.onStart();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
	}

	private void initDayOfWeek() {
		Calendar c = Calendar.getInstance();
		c.add(Calendar.DATE, 1);
		tv_week_day.setText(DateUtils.getDayOfWeek(c));
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

	private void initAnim() {
		outAnimation = AnimationUtils.loadAnimation(getActivity(),
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
			}
		});
	}

	public void selectCity(SelectCityActivity.SelectCityType selectType, int requestCode) {
		Intent intent = new Intent(getActivity(), SelectCityActivity.class);
		intent.putExtra("selectType", selectType);
		startActivityForResult(intent, requestCode);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
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

	private void swapCity() {
		String tmpCity = tv_departCity.getText().toString();
		tv_departCity.setText(tv_arriveCity.getText());
		app.getUserInfo_sp().putString("departCity", tv_arriveCity.getText().toString());
		tv_arriveCity.setText(tmpCity);
		app.getUserInfo_sp().putString("arriveCity", tmpCity);
	}

	@SuppressWarnings("static-access")
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == getActivity().RESULT_OK) {
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

	@Override
	public boolean onBack() {
		if (calendar_content.getVisibility() == View.VISIBLE) {
			calendar.startAnimation(outAnimation);
			return true;
		}
		return false;
	}

	private void startNewRoute() {
		Intent srcIntent = getActivity().getIntent();
		if (srcIntent.getExtras() != null) {
			boolean oldTrip = srcIntent.getExtras()
					.getBoolean("oldTrip", false);
			if (!oldTrip) {
				setTripInfo();
			}
		} else {
			setTripInfo();
		}
		
		Intent intent = new Intent(getActivity(), ListOfMessageActivity.class);
//		intent.putExtra("DepartCityCode", mDepartCityCode);
//		intent.putExtra("ArriveCityCode", mArriveCityCode);
		intent.putExtra("TakeOffTime", dateString);
		intent.putExtra("departCity", tv_departCity.getText().toString());
		intent.putExtra("arriveCity", tv_arriveCity.getText().toString());
		
		startActivity(intent);
//		if(!isShowInMainTabActivity) {
//			getActivity().finish();
//		}
	}

	private void setTripInfo() {
		app.setTripName(dateString + tv_departCity.getText() + "-"
				+ tv_arriveCity.getText());
		app.setDepartcity(tv_departCity.getText().toString());
		app.setArrivecity(tv_arriveCity.getText().toString());
//		app.setDepartCityCode(mDepartCityCode);
//		app.setArriveCityCode(mArriveCityCode);
	}

	private void showCalendar() {
		if (isShowInMainTabActivity) {
			mainTabActivity.setScanScroll(true);
		}
		if (calendar_content.getVisibility() == View.INVISIBLE) {
			calendar_content.setVisibility(View.VISIBLE);
			calendar.setAnimation(AnimationUtils.loadAnimation(getActivity(),
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

	@Override
	public void loadDate() {
		
	}

	@Override
	public boolean onKeyBackDown() {
		return hideCalendar();
	}
	
	
}