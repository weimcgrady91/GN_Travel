package com.gionee.gntravel.utils;

import java.util.Calendar;
import java.util.Date;

import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.gionee.gntravel.R;
import com.gionee.gntravel.fragment.DateSelectorFragment.DateSelectedListener;
import com.weiqun.customcalendar.CalendarGridView;
import com.weiqun.customcalendar.CalendarRowView;
import com.weiqun.customcalendar.CustomCalendar;
import com.weiqun.customcalendar.MonthCellDescriptor;
import com.weiqun.customcalendar.MonthView;
import com.weiqun.customcalendar.VerticalViewPager;

public class DateSelectUtils {
	public static void setCalendarCellText(CustomCalendar calendar, String string) {
		// 找到选择的那天
		// String dateSelected = getFormater().format(date);
		ViewPager vp = (ViewPager) calendar.findViewById(R.id.pager);
		outter: for (int i = 0; i < vp.getChildCount(); i++) {
			// 得到显示的月
			MonthView monthView = (MonthView) vp.getChildAt(i);
			// 得到每行，就是一个星期
			for (int k = 0; k < monthView.getChildCount(); k++) {
				CalendarGridView gv = (CalendarGridView) monthView.getChildAt(k);
				for (int l = 0; l < gv.getChildCount(); l++) {
					// 每个星期
					CalendarRowView row = (CalendarRowView) gv.getChildAt(l);

					for (int b = 0; b < row.getChildCount(); b++) {
						RelativeLayout rlDay = (RelativeLayout) row.getChildAt(b);
						TextView tvDay = (TextView) rlDay.findViewById(R.id.spec_des);
						// 得到每天上的时间描述
						MonthCellDescriptor cell = (MonthCellDescriptor) rlDay.getTag();
						if (cell!=null) {
							if (cell.isSelected()) {
								tvDay.setText(string);
								break outter;
							}
						}
					}
				}
			}
		}
	}

	private static DateSelectedListener l;

	public static Calendar initCurrentCalendar() {
		Long currentDate = new Date().getTime();
		Calendar currrentSelectdCalendar = Calendar.getInstance();
		currrentSelectdCalendar.clear();
		currrentSelectdCalendar.setTimeInMillis(currentDate);
		return currrentSelectdCalendar;
	}

	public static void initCalendar(final CustomCalendar picker_v, final Calendar currrentSelectdCalendar) {

		Calendar fromCalendar = Calendar.getInstance();
		fromCalendar.clear();
		fromCalendar.setTimeInMillis(new Date().getTime());

		Calendar toCalendar = Calendar.getInstance();
		toCalendar.clear();
		toCalendar.setTimeInMillis(fromCalendar.getTimeInMillis());
		toCalendar.add(Calendar.MONTH, 5);

		picker_v.initCalendar(fromCalendar.getTime(), toCalendar.getTime()).withSelectedDates(
				currrentSelectdCalendar.getTime());

	}
	public static void initCalendar(final CustomCalendar picker_v) {
		
		Calendar fromCalendar = Calendar.getInstance();
		fromCalendar.clear();
		fromCalendar.setTimeInMillis(new Date().getTime());
		
		Calendar toCalendar = Calendar.getInstance();
		toCalendar.clear();
		toCalendar.setTimeInMillis(fromCalendar.getTimeInMillis());
		toCalendar.add(Calendar.MONTH, 5);
		picker_v.initCalendar(fromCalendar.getTime(), toCalendar.getTime());
		
	}
}
