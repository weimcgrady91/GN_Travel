package com.gionee.gntravel.fragment;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.gionee.gntravel.R;
import com.gionee.gntravel.TravelApplication;
import com.gionee.gntravel.utils.DateSelectUtils;
import com.gionee.gntravel.utils.DateUtils;
import com.weiqun.customcalendar.CustomCalendar;
import com.weiqun.customcalendar.CustomCalendar.OnDateSelectedListener;

public class DateSelectorFragment extends Fragment implements OnClickListener {

	private ViewGroup vg;
	private View ll_preday;
	private View ll_nextday;
	private View ll_today;
	private TextView tv_today;
	private LinearLayout fragment_ll_calendar;
	private CustomCalendar cv_list;
	private DateSelectedListener l;
	private Calendar currrentSelectdCalendar;
//	private Calendar originCalendar;
	private Date dateD;
	private TextView tv_nextDay;
	private TextView tv_preday;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initCurrentCalendar();
		findViews();
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		initVars();
	}
	private void initCurrentCalendar() {
		Long currentDate = (Long) getArguments().get("currentDate");
		String date = (String) getArguments().get("date");
		dateD = DateUtils.StringToDate(date, "yyyy-MM-dd");
		Long dateL = dateD.getTime();
		if (currentDate == null) {
			currentDate = new Date().getTime();
		}

		currrentSelectdCalendar = Calendar.getInstance();
		currrentSelectdCalendar.clear();
		currrentSelectdCalendar.setTimeInMillis(dateL);

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		if (vg.getParent() != null) {
			ViewGroup vgParent = (ViewGroup) vg.getParent();
			vgParent.removeView(vg);
		}

		currrentSelectdCalendar.add(Calendar.DAY_OF_YEAR, 0);
		SimpleDateFormat sdf = new SimpleDateFormat("MM/dd", Locale.getDefault());
		String time = sdf.format(currrentSelectdCalendar.getTime());
		tv_today.setText(time + DateUtils.getDayOfWeek(currrentSelectdCalendar));
		// fragment_ll_calendar.setVisibility(View.INVISIBLE);
		return vg;
	}

	public View getCalendarView() {
		return cv_list;
	}

	public View getFragment_ll_calendar() {
		return fragment_ll_calendar;
	}

	private void initVars() {
		ll_preday.setOnClickListener(this);
		ll_nextday.setOnClickListener(this);
		ll_today.setOnClickListener(this);
		initCalendar();
	}

	private void initCalendar() {
	    fromCalendar = Calendar.getInstance();
		fromCalendar.clear();
		fromCalendar.setTimeInMillis(new Date().getTime());

	    toCalendar = Calendar.getInstance();
		toCalendar.clear();
		toCalendar.setTimeInMillis(fromCalendar.getTimeInMillis());
		toCalendar.add(Calendar.MONTH, 5);

		
		final Calendar nextCalendar = Calendar.getInstance();
		nextCalendar.add(Calendar.MONTH, 5);

		final Calendar lastCalendar = Calendar.getInstance();
		lastCalendar.add(Calendar.MONTH, 0);
		
		cv_list.initCalendar(lastCalendar.getTime(), nextCalendar.getTime()).withSelectedDates(dateD);
		cv_list.hitTitle(true);
		cv_list.setOnDateSelectedListener(new OnDateSelectedListener() {

			@Override
			public void onDateUnselected(Date date) {

			}

			@Override
			public void onDateSelected(Date date) {
				isCalendarVisible = !isCalendarVisible;
				runnable.run();
				currrentSelectdCalendar.clear();
				currrentSelectdCalendar.setTime(date);
				addDay(0);
				String dateStr = DateUtils.dateToStr(date);
				TravelApplication app = (TravelApplication) getActivity().getApplication();
				app.replaceTripName(dateStr);
				
			}
		});
	
	}

	private void findViews() {
		vg = (ViewGroup) LayoutInflater.from(getActivity()).inflate(R.layout.fragment_date_selector, null);
		ll_preday = vg.findViewById(R.id.ll_preday);
		tv_preday = (TextView) vg.findViewById(R.id.tv_preday);
		tv_nextDay = (TextView) vg.findViewById(R.id.tv_nextDay);
		ll_nextday = vg.findViewById(R.id.ll_nextday);
		ll_today = vg.findViewById(R.id.ll_today);
		tv_today = (TextView) vg.findViewById(R.id.tv_today);
		ll_preday.setFocusable(true);
		ll_nextday.setFocusable(true);
		ll_today.setFocusable(true);
		fragment_ll_calendar = (LinearLayout) vg.findViewById(R.id.fragment_ll_calendar);
		cv_list = (CustomCalendar) vg.findViewById(R.id.picker_cakendar);
		fragment_ll_calendar.setVisibility(View.INVISIBLE);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.ll_preday:
			addDay(-1);
			break;
		case R.id.ll_nextday:
			addDay(1);
			break;
		case R.id.ll_today:
			doTodayClick();
			break;
		}
	}

	private Runnable runnable;
	private boolean isCalendarVisible = false;
	private Calendar fromCalendar;
	private Calendar toCalendar;

	public void setOnTodayClick(Runnable callback) {
		runnable = callback;
	}

	public void doTodayClick() {
		isCalendarVisible = !isCalendarVisible;
		runnable.run();
		setText();
	}

	public boolean isVisiable() {
		return isCalendarVisible;
	}

	public void setVisiable(boolean calendarVisible) {
		isCalendarVisible = calendarVisible;
	}
	
	private void addDay(int day) {
		currrentSelectdCalendar.add(Calendar.DAY_OF_YEAR, day);
		SimpleDateFormat sdf = new SimpleDateFormat("MM/dd", Locale.getDefault());
		String time = sdf.format(currrentSelectdCalendar.getTime());

		tv_today.setText(time + DateUtils.getDayOfWeek(currrentSelectdCalendar));
		if (l != null) {
			l.onDateChange(currrentSelectdCalendar.getTime(), time);
		}
		if (currrentSelectdCalendar.get(Calendar.DAY_OF_YEAR) == fromCalendar.get(Calendar.DAY_OF_YEAR)) {
			ll_preday.setEnabled(false);
			tv_preday.setTextColor(Color.GRAY);
		} else {
			ll_preday.setEnabled(true);
			tv_preday.setTextColor(getResources().getColor(R.color.text_color_v4));
		}
		if (currrentSelectdCalendar.get(Calendar.DAY_OF_YEAR) == toCalendar.get(Calendar.DAY_OF_YEAR)-1) {
			ll_nextday.setEnabled(false);
			tv_nextDay.setTextColor(Color.GRAY);
		} else {
			ll_nextday.setEnabled(true);
			tv_nextDay.setTextColor(getResources().getColor(R.color.text_color_v4));
		}
		cv_list.selectDate(currrentSelectdCalendar.getTime());
		TravelApplication app = (TravelApplication) getActivity().getApplication();
		app.replaceTripName(DateUtils.dateToStr(currrentSelectdCalendar.getTime()));
		if (isCalendarVisible) {
			doTodayClick();
		}
		setText();
	}

	private void setText() {
		DateSelectUtils.setCalendarCellText(cv_list, "入住");
	}

	public void setDateSelectedListener(DateSelectedListener l) {
		this.l = l;
	}

	public static interface DateSelectedListener {
		/**
		 * 
		 * @param newDate
		 *            新的时间
		 * @param stringDate
		 *            yyyy-MM-dd 格式的新的时间
		 */
		public void onDateChange(Date newDate, String stringNewDate);
	}
}
