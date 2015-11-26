package com.weiqun.customcalendar;

import static java.util.Calendar.DATE;
import static java.util.Calendar.DAY_OF_MONTH;
import static java.util.Calendar.DAY_OF_WEEK;
import static java.util.Calendar.HOUR_OF_DAY;
import static java.util.Calendar.MILLISECOND;
import static java.util.Calendar.MINUTE;
import static java.util.Calendar.MONTH;
import static java.util.Calendar.SECOND;
import static java.util.Calendar.YEAR;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Parcelable;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.weiqun.customcalendar.MonthCellDescriptor.RangeState;

public class CustomCalendar extends LinearLayout {
	private Context context;
	private View wei_Calendar;
	private TextView tv_title;
	private LinearLayout ll_weekTitle;
	private DateFormat monthNameFormat;
	private DateFormat weekdayNameFormat;
	@SuppressLint("NewApi")
	public CustomCalendar(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public CustomCalendar(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.context = context;
		wei_Calendar = LayoutInflater.from(context).inflate(R.layout.calendar,
				this, true);
	}

	public CustomCalendar(Context context) {
		super(context);
	}

	@Override
	protected void onFinishInflate() {
		super.onFinishInflate();
		RelativeLayout rl_title = (RelativeLayout) wei_Calendar.findViewById(R.id.rl_title);
		rl_title.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				
			}
		});
		tv_title = (TextView) wei_Calendar.findViewById(R.id.tv_title);
		ll_weekTitle = (LinearLayout) findViewById(R.id.ll_weekTitle);
		ll_weekTitle.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				
			}
		});
//		mVerticalViewPager = (VerticalViewPager) wei_Calendar.findViewById(R.id.pager);
		mVerticalViewPager = (ViewPager) wei_Calendar.findViewById(R.id.pager);
		
	}
	
	private Locale locale;
	private Calendar minCal;
	private Calendar maxCal;
	private Calendar monthCounter;
	private SelectionMode selectionMode;
	final List<MonthDescriptor> months = new ArrayList<MonthDescriptor>();
	final List<MonthCellDescriptor> selectedCells = new ArrayList<MonthCellDescriptor>();
	final List<MonthCellDescriptor> highlightedCells = new ArrayList<MonthCellDescriptor>();
	final List<Calendar> selectedCals = new ArrayList<Calendar>();
	final List<Calendar> highlightedCals = new ArrayList<Calendar>();
	private final List<List<List<MonthCellDescriptor>>> cells = new ArrayList<List<List<MonthCellDescriptor>>>();
	private boolean displayOnly;
	private Calendar today;
	private OnDateSelectedListener dateListener;
	private DateSelectableFilter dateConfiguredListener;
	private OnInvalidDateSelectedListener invalidDateListener = new DefaultOnInvalidDateSelectedListener();
	final MonthView.Listener listener = new CellClickedListener();
//	private CalendarAdapter calendarAdapter;
	private CalendarAdapter calendarAdapter;
	
	private List<MonthView> lists;
//	private VerticalViewPager mVerticalViewPager;
	
	private ViewPager mVerticalViewPager;
	
	public enum SelectionMode {
		/**
		 * Only one date will be selectable. If there is already a selected date
		 * and you select a new one, the old date will be unselected.
		 */
		SINGLE,
		/**
		 * Multiple dates will be selectable. Selecting an already-selected date
		 * will un-select it.
		 */
		MULTIPLE,
		/**
		 * Allows you to select a date range. Previous selections are cleared
		 * when you either:
		 * <ul>
		 * <li>Have a range selected and select another date (even if it's in
		 * the current range).</li>
		 * <li>Have one date selected and then select an earlier date.</li>
		 * </ul>
		 */
		RANGE
	}
	
	public void hitTitle(boolean flag) {
		if(flag)
			tv_title.setVisibility(View.GONE);
		else 
			tv_title.setVisibility(View.VISIBLE);
	}
	
	public CustomCalendar initCalendar(Date minDate, Date maxDate) {
		today = Calendar.getInstance();
		int firstDayOfWeek = today.getFirstDayOfWeek();
		for (int offset = 0; offset < 7; offset++) {
			today.set(Calendar.DAY_OF_WEEK, firstDayOfWeek + offset);
			final TextView textView = (TextView) ll_weekTitle.getChildAt(offset);
			textView.setText(getDayOfWeek(today));
		}
		// Make sure that all calendar instances use the same locale.
		this.locale =  Locale.getDefault();
		today = Calendar.getInstance(locale);
		minCal = Calendar.getInstance(locale);
		maxCal = Calendar.getInstance(locale);
		monthCounter = Calendar.getInstance(locale);
		monthNameFormat = new SimpleDateFormat(getContext().getString(
				R.string.month_name_format), locale);
		for (MonthDescriptor month : months) {
			month.setLabel(monthNameFormat.format(month.getDate()));
		}
		weekdayNameFormat = new SimpleDateFormat(getContext().getString(
				R.string.day_name_format), locale);

		this.selectionMode = SelectionMode.SINGLE;
		// Clear out any previously-selected dates/cells.
		selectedCals.clear();
		selectedCells.clear();
		highlightedCells.clear();

		// Clear previous state.
		cells.clear();
		months.clear();
		minCal.setTime(minDate);
		maxCal.setTime(maxDate);
		setMidnight(minCal);
		setMidnight(maxCal);
		displayOnly = false;

		// maxDate is exclusive: bump back to the previous day so if maxDate is
		// the first of a month,
		// we don't accidentally include that month in the view.
		maxCal.add(MINUTE, -1);

		// Now iterate between minCal and maxCal and build up our list of months
		// to show.
		monthCounter.setTime(minCal.getTime());
		final int maxMonth = maxCal.get(MONTH);
		final int maxYear = maxCal.get(YEAR);
		while ((monthCounter.get(MONTH) <= maxMonth // Up to, including the
													// month.
				|| monthCounter.get(YEAR) < maxYear) // Up to the year.
				&& monthCounter.get(YEAR) < maxYear + 1) { // But not > next yr.
			Date date = monthCounter.getTime();
			MonthDescriptor month = new MonthDescriptor(
					monthCounter.get(MONTH), monthCounter.get(YEAR), date,
					monthNameFormat.format(date));
			cells.add(getMonthCells(month, monthCounter));
			months.add(month);
			monthCounter.add(MONTH, 1);
		}
//		Log.e("weiqun12345", "months.size()" + months.size());
//		Log.e("weiqun12345", "============================month==================");
//		for(MonthDescriptor month : months) {
//			Log.e("weiqun12345", "month=" + month.toString());
//		}
//		
//		Log.e("weiqun12345", "============================month==================");
//		
//		Log.e("weiqun12345", "cells.size()" + cells.size());
//		for(int i=0;i<cells.size();i++) {
//			Log.e("weiqun12345", "====month start======");
//			List<List<MonthCellDescriptor>> list1 = cells.get(i);
//			for(int ii=0;ii<list1.size();ii++) {
//				List<MonthCellDescriptor> list2 = list1.get(ii);
//				for(int iii=0;iii<list2.size();iii++) {
//					Log.e("weiqun12345", list2.get(iii).toString());
//				}
//				Log.e("weiqun12345", "row end");
//			}
//			Log.e("weiqun12345", "===month end======");
//		}
		lists = new ArrayList<MonthView>();
		LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		for(int index=0;index<months.size();index++) {
			MonthView monthView = MonthView.create(null, inflater,
					weekdayNameFormat, listener, today);
			monthView.init(months.get(index), cells.get(index),
					displayOnly);
			lists.add(monthView);
		}
		calendarAdapter = new CalendarAdapter();
		mVerticalViewPager.setAdapter(calendarAdapter);
		tv_title.setText(lists.get(0).title);
//		mVerticalViewPager.setOnPageChangeListener(new VerticalViewPager.OnPageChangeListener() {
//			
//			@Override
//			public void onPageSelected(int position) {
//				// TODO Auto-generated method stub
//				tv_title.setText(lists.get(position).title);
//			}
//			
//			@Override
//			public void onPageScrolled(int position, float positionOffset,
//					int positionOffsetPixels) {
//				// TODO Auto-generated method stub
//				
//			}
//			
//			@Override
//			public void onPageScrollStateChanged(int state) {
//				// TODO Auto-generated method stub
//				
//			}
//		});
		
		mVerticalViewPager.setOnPageChangeListener(new OnPageChangeListener() {
			
			@Override
			public void onPageSelected(int position) {
				tv_title.setText(lists.get(position).title);
				
			}
			
			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onPageScrollStateChanged(int arg0) {
				// TODO Auto-generated method stub
				
			}
		});
		
		return this;
		
	}
	public class CalendarAdapter extends android.support.v4.view.PagerAdapter {
		private LayoutInflater inflater;

		List<MonthView> viewLists = new ArrayList<MonthView>();
		Map<Integer,View> maps = new HashMap<Integer,View>();
	    public CalendarAdapter(List<MonthView> lists)
	    {
	    	viewLists = lists;
	    }
	    public CalendarAdapter(){
	    	
	    	inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	    }
		
		@Override
		public void destroyItem(View arg0, int position, Object arg2) {
//			((com.weiqun.customcalendar.VerticalViewPager)arg0).removeView(maps.get(position));
			((android.support.v4.view.ViewPager)arg0).removeView(maps.get(position));
		}

		@Override
		public void finishUpdate(View arg0) {

		}

		@Override
		public int getCount() {
			return months.size();
//			return lists.size();
		}

		@Override
		public Object instantiateItem(View arg0, int position) {
			
			MonthView monthView = MonthView.create(null, inflater,weekdayNameFormat, listener, today);
			monthView.init(months.get(position), cells.get(position),displayOnly);
			maps.put(position, monthView);
//			((com.weiqun.customcalendar.VerticalViewPager)arg0).addView(monthView);
			((android.support.v4.view.ViewPager)arg0).addView(monthView);
			return monthView;
//			((com.weiqun.customcalendar.VerticalViewPager)arg0).addView(lists.get(position));
//			return lists.get(position);
			
		}

		@Override
		public boolean isViewFromObject(View arg0, Object arg1) {
			return arg0 == arg1;
		}

		@Override
		public void restoreState(Parcelable arg0, ClassLoader arg1) {

		}

		@Override
		public Parcelable saveState() {
			return null;
		}

		@Override
		public void startUpdate(View arg0) {

		}
		
		@Override
        public int getItemPosition(Object object) {
                return POSITION_NONE;
        }

	}
//	public class CalendarAdapter extends com.weiqun.customcalendar.PagerAdapter {
//		private LayoutInflater inflater;
//
//		List<MonthView> viewLists = new ArrayList<MonthView>();
//		Map<Integer,View> maps = new HashMap<Integer,View>();
//	    public CalendarAdapter(List<MonthView> lists)
//	    {
//	    	viewLists = lists;
//	    }
//	    public CalendarAdapter(){
//	    	
//	    	inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//	    }
//		
//		@Override
//		public void destroyItem(View arg0, int position, Object arg2) {
//			((com.weiqun.customcalendar.VerticalViewPager)arg0).removeView(maps.get(position));
//		}
//
//		@Override
//		public void finishUpdate(View arg0) {
//
//		}
//
//		@Override
//		public int getCount() {
//			return months.size();
////			return lists.size();
//		}
//
//		@Override
//		public Object instantiateItem(View arg0, int position) {
//			
//			MonthView monthView = MonthView.create(null, inflater,weekdayNameFormat, listener, today);
//			monthView.init(months.get(position), cells.get(position),displayOnly);
//			maps.put(position, monthView);
//			((com.weiqun.customcalendar.VerticalViewPager)arg0).addView(monthView);
//			return monthView;
////			((com.weiqun.customcalendar.VerticalViewPager)arg0).addView(lists.get(position));
////			return lists.get(position);
//			
//		}
//
//		@Override
//		public boolean isViewFromObject(View arg0, Object arg1) {
//			return arg0 == arg1;
//		}
//
//		@Override
//		public void restoreState(Parcelable arg0, ClassLoader arg1) {
//
//		}
//
//		@Override
//		public Parcelable saveState() {
//			return null;
//		}
//
//		@Override
//		public void startUpdate(View arg0) {
//
//		}
//		
//		@Override
//        public int getItemPosition(Object object) {
//                return POSITION_NONE;
//        }
//
//	}
	
	
	
	
	private List<List<MonthCellDescriptor>> getMonthCells(MonthDescriptor month,
			Calendar startCal) {
		Calendar cal = Calendar.getInstance(locale);
		cal.setTime(startCal.getTime()); 
		List<List<MonthCellDescriptor>> cells = new ArrayList<List<MonthCellDescriptor>>();
		cal.set(DAY_OF_MONTH, 1); 
		int firstDayOfWeek = cal.get(DAY_OF_WEEK); 
		int offset = cal.getFirstDayOfWeek() - firstDayOfWeek;
		if (offset > 0) {
			offset -= 7;
		}
		cal.add(Calendar.DATE, offset);

		Calendar minSelectedCal = minDate(selectedCals);
		Calendar maxSelectedCal = maxDate(selectedCals);

		while ((cal.get(MONTH) < month.getMonth() + 1 || cal.get(YEAR) < month
				.getYear()) //
				&& cal.get(YEAR) <= month.getYear()) {
			List<MonthCellDescriptor> weekCells = new ArrayList<MonthCellDescriptor>();
			cells.add(weekCells);
			for (int c = 0; c < 7; c++) {
				Date date = cal.getTime();
				boolean isCurrentMonth = cal.get(MONTH) == month.getMonth();
				boolean isSelected = isCurrentMonth
						&& containsDate(selectedCals, cal);
				boolean isSelectable = isCurrentMonth
						&& betweenDates(cal, minCal, maxCal)
						&& isDateSelectable(date);
				boolean isToday = sameDate(cal, today);
				boolean isHighlighted = containsDate(highlightedCals, cal);
				int value = cal.get(DAY_OF_MONTH);

				MonthCellDescriptor.RangeState rangeState = MonthCellDescriptor.RangeState.NONE;
				if (selectedCals.size() > 1) {
					if (sameDate(minSelectedCal, cal)) {
						rangeState = MonthCellDescriptor.RangeState.FIRST;
					} else if (sameDate(maxDate(selectedCals), cal)) {
						rangeState = MonthCellDescriptor.RangeState.LAST;
					} else if (betweenDates(cal, minSelectedCal, maxSelectedCal)) {
						rangeState = MonthCellDescriptor.RangeState.MIDDLE;
					}
				}

				weekCells.add(new MonthCellDescriptor(date, isCurrentMonth,
						isSelectable, isSelected, isToday, isHighlighted,
						value, rangeState));
				cal.add(DATE, 1);
			}
		}
		if(cells.size() == 5) {
			List<MonthCellDescriptor> weekCells = new ArrayList<MonthCellDescriptor>();
			cells.add(weekCells);
			for (int c = 0; c < 7; c++) {
				Date date = cal.getTime();
				boolean isCurrentMonth = cal.get(MONTH) == month.getMonth();
				boolean isSelected = isCurrentMonth
						&& containsDate(selectedCals, cal);
				boolean isSelectable = isCurrentMonth
						&& betweenDates(cal, minCal, maxCal)
						&& isDateSelectable(date);
				boolean isToday = sameDate(cal, today);
				boolean isHighlighted = containsDate(highlightedCals, cal);
				int value = cal.get(DAY_OF_MONTH);
				MonthCellDescriptor.RangeState rangeState = MonthCellDescriptor.RangeState.NONE;
				weekCells.add(new MonthCellDescriptor(date, isCurrentMonth,
						isSelectable, isSelected, isToday, isHighlighted,
						value, rangeState));
				cal.add(DATE, 1);
			}
		}
		return cells;
	}
	
	static void setMidnight(Calendar cal) {
		cal.set(HOUR_OF_DAY, 0);
		cal.set(MINUTE, 0);
		cal.set(SECOND, 0);
		cal.set(MILLISECOND, 0);
	}
	
	private static boolean containsDate(List<Calendar> selectedCals,
			Calendar cal) {
		for (Calendar selectedCal : selectedCals) {
			if (sameDate(cal, selectedCal)) {
				return true;
			}
		}
		return false;
	}

	private static Calendar minDate(List<Calendar> selectedCals) {
		if (selectedCals == null || selectedCals.size() == 0) {
			return null;
		}
		Collections.sort(selectedCals);
		return selectedCals.get(0);
	}

	private static Calendar maxDate(List<Calendar> selectedCals) {
		if (selectedCals == null || selectedCals.size() == 0) {
			return null;
		}
		Collections.sort(selectedCals);
		return selectedCals.get(selectedCals.size() - 1);
	}

	private static boolean sameDate(Calendar cal, Calendar selectedDate) {
		return cal.get(MONTH) == selectedDate.get(MONTH)
				&& cal.get(YEAR) == selectedDate.get(YEAR)
				&& cal.get(DAY_OF_MONTH) == selectedDate.get(DAY_OF_MONTH);
	}

	private static boolean betweenDates(Calendar cal, Calendar minCal,
			Calendar maxCal) {
		final Date date = cal.getTime();
		return betweenDates(date, minCal, maxCal);
	}

	static boolean betweenDates(Date date, Calendar minCal, Calendar maxCal) {
		final Date min = minCal.getTime();
		return (date.equals(min) || date.after(min)) // >= minCal
				&& date.before(maxCal.getTime()); // && < maxCal
	}

	private static boolean sameMonth(Calendar cal, MonthDescriptor month) {
		return (cal.get(MONTH) == month.getMonth() && cal.get(YEAR) == month
				.getYear());
	}

	private boolean isDateSelectable(Date date) {
		return dateConfiguredListener == null
				|| dateConfiguredListener.isDateSelectable(date);
	}

	public void setOnDateSelectedListener(OnDateSelectedListener listener) {
		dateListener = listener;
	}
	
	/**
	 * Set a listener to react to user selection of a disabled date.
	 * 
	 * @param listener
	 *            the listener to set, or null for no reaction
	 */
	public void setOnInvalidDateSelectedListener(
			OnInvalidDateSelectedListener listener) {
		invalidDateListener = listener;
	}

	/**
	 * Set a listener used to discriminate between selectable and unselectable
	 * dates. Set this to disable arbitrary dates as they are rendered.
	 * <p>
	 * Important: set this before you call {@link #init(Date, Date)} methods. If
	 * called afterwards, it will not be consistently applied.
	 */
	public void setDateSelectableFilter(DateSelectableFilter listener) {
		dateConfiguredListener = listener;
	}

	/**
	 * Interface to be notified when a new date is selected or unselected. This
	 * will only be called when the user initiates the date selection. If you
	 * call {@link #selectDate(Date)} this listener will not be notified.
	 * 
	 * @see #setOnDateSelectedListener(OnDateSelectedListener)
	 */
	public interface OnDateSelectedListener {
		void onDateSelected(Date date);

		void onDateUnselected(Date date);
	}

	/**
	 * Interface to be notified when an invalid date is selected by the user.
	 * This will only be called when the user initiates the date selection. If
	 * you call {@link #selectDate(Date)} this listener will not be notified.
	 * 
	 * @see #setOnInvalidDateSelectedListener(OnInvalidDateSelectedListener)
	 */
	public interface OnInvalidDateSelectedListener {
		void onInvalidDateSelected(Date date);
	}

	/**
	 * Interface used for determining the selectability of a date cell when it
	 * is configured for display on the calendar.
	 * 
	 * @see #setDateSelectableFilter(DateSelectableFilter)
	 */
	public interface DateSelectableFilter {
		boolean isDateSelectable(Date date);
	}

	private class DefaultOnInvalidDateSelectedListener implements
			OnInvalidDateSelectedListener {
		@Override
		public void onInvalidDateSelected(Date date) {
//			 Toast.makeText(getContext(), "无效的日期",
//			 Toast.LENGTH_SHORT).show();
		}
	}
	
	private class CellClickedListener implements MonthView.Listener {
		@Override
		public void handleClick(MonthCellDescriptor cell) {
			Date clickedDate = cell.getDate();

			if (!betweenDates(clickedDate, minCal, maxCal)
					|| !isDateSelectable(clickedDate)) {
				if (invalidDateListener != null) {
					invalidDateListener.onInvalidDateSelected(clickedDate);
				}
			} else {
				boolean wasSelected = doSelectDate(clickedDate, cell);
				if (dateListener != null) {
					if (wasSelected) {
						dateListener.onDateSelected(clickedDate);
					} else {
						dateListener.onDateUnselected(clickedDate);
					}
				}
			}
		}
	}
	
	private boolean doSelectDate(Date date, MonthCellDescriptor cell) {
		Calendar newlySelectedCal = Calendar.getInstance(locale);
		newlySelectedCal.setTime(date);
		// Sanitize input: clear out the hours/minutes/seconds/millis.
		setMidnight(newlySelectedCal);

		// Clear any remaining range state.
		for (MonthCellDescriptor selectedCell : selectedCells) {
			selectedCell.setRangeState(RangeState.NONE);
		}

		switch (selectionMode) {
		case RANGE:
			if (selectedCals.size() > 1) {
				// We've already got a range selected: clear the old one.
				clearOldSelections();
			} else if (selectedCals.size() == 1
					&& newlySelectedCal.before(selectedCals.get(0))) {
				// We're moving the start of the range back in time: clear the
				// old start date.
				clearOldSelections();
			}
			break;

		case MULTIPLE:
			date = applyMultiSelect(date, newlySelectedCal);
			break;

		case SINGLE:
			clearOldSelections();
			break;
		default:
			throw new IllegalStateException("Unknown selectionMode "
					+ selectionMode);
		}

		if (date != null) {
			// Select a new cell.
			if (selectedCells.size() == 0 || !selectedCells.get(0).equals(cell)) {
				selectedCells.add(cell);
				cell.setSelected(true);
			}
			selectedCals.add(newlySelectedCal);

			if (selectionMode == SelectionMode.RANGE
					&& selectedCells.size() > 1) {
				// Select all days in between start and end.
				Date start = selectedCells.get(0).getDate();
				Date end = selectedCells.get(1).getDate();
				selectedCells.get(0).setRangeState(
						MonthCellDescriptor.RangeState.FIRST);
				selectedCells.get(1).setRangeState(
						MonthCellDescriptor.RangeState.LAST);

				for (List<List<MonthCellDescriptor>> month : cells) {
					for (List<MonthCellDescriptor> week : month) {
						for (MonthCellDescriptor singleCell : week) {
							if (singleCell.getDate().after(start)
									&& singleCell.getDate().before(end)
									&& singleCell.isSelectable()) {
								singleCell.setSelected(true);
								singleCell
										.setRangeState(MonthCellDescriptor.RangeState.MIDDLE);
								selectedCells.add(singleCell);
							}
						}
					}
				}
			}
		}

//		// Update the adapter.
		validateAndUpdate();
		return date != null;
	}
	
	private void validateAndUpdate() {
		calendarAdapter.notifyDataSetChanged();
	}
	
	private void clearOldSelections() {
		for (MonthCellDescriptor selectedCell : selectedCells) {
			// De-select the currently-selected cell.
			selectedCell.setSelected(false);
		}
		selectedCells.clear();
		selectedCals.clear();
	}
	
	private Date applyMultiSelect(Date date, Calendar selectedCal) {
		for (MonthCellDescriptor selectedCell : selectedCells) {
			if (selectedCell.getDate().equals(date)) {
				// De-select the currently-selected cell.
				selectedCell.setSelected(false);
				selectedCells.remove(selectedCell);
				date = null;
				break;
			}
		}
		for (Calendar cal : selectedCals) {
			if (sameDate(cal, selectedCal)) {
				selectedCals.remove(cal);
				break;
			}
		}
		return date;
	}
	
	public void withSelectedDates(Date selectedDates) {
		this.withSelectedDates(Arrays.asList(selectedDates));
	}
	
	public void withSelectedDates(
			Collection<Date> selectedDates) {
		if (selectionMode == SelectionMode.SINGLE
				&& selectedDates.size() > 1) {
			throw new IllegalArgumentException(
					"SINGLE mode can't be used with multiple selectedDates");
		}
		if (selectedDates != null) {
			for (Date date : selectedDates) {
				selectDate(date);
			}
		}
//		scrollToSelectedDates();

		validateAndUpdate();
//		return this;
	}
	
	public boolean selectDate(Date date) {
		validateDate(date);

		MonthCellWithMonthIndex monthCellWithMonthIndex = getMonthCellWithIndexByDate(date);
		if (monthCellWithMonthIndex == null || !isDateSelectable(date)) {
			return false;
		}
		boolean wasSelected = doSelectDate(date, monthCellWithMonthIndex.cell);
//		if (wasSelected) {
//			scrollToSelectedMonth(monthCellWithMonthIndex.monthIndex);
//		}
		return wasSelected;
	}
	
	/** Hold a cell with a month-index. */
	private static class MonthCellWithMonthIndex {
		public MonthCellDescriptor cell;
		public int monthIndex;

		public MonthCellWithMonthIndex(MonthCellDescriptor cell, int monthIndex) {
			this.cell = cell;
			this.monthIndex = monthIndex;
		}
	}

	/** Return cell and month-index (for scrolling) for a given Date. */
	private MonthCellWithMonthIndex getMonthCellWithIndexByDate(Date date) {
		int index = 0;
		Calendar searchCal = Calendar.getInstance(locale);
		searchCal.setTime(date);
		Calendar actCal = Calendar.getInstance(locale);

		for (List<List<MonthCellDescriptor>> monthCells : cells) {
			for (List<MonthCellDescriptor> weekCells : monthCells) {
				for (MonthCellDescriptor actCell : weekCells) {
					actCal.setTime(actCell.getDate());
					if (sameDate(actCal, searchCal) && actCell.isSelectable()) {
						return new MonthCellWithMonthIndex(actCell, index);
					}
				}
			}
			index++;
		}
		return null;
	}
	
	private void validateDate(Date date) {
		if (date == null) {
			throw new IllegalArgumentException(
					"Selected date must be non-null.");
		}
		if (date.getTime() == 0) {
			throw new IllegalArgumentException(
					"Selected date must be non-zero.  " + date);
		}
		if (date.before(minCal.getTime()) || date.after(maxCal.getTime())) {
			throw new IllegalArgumentException(String.format(
					"SelectedDate must be between minDate and maxDate."
							+ "%nminDate: %s%nmaxDate: %s%nselectedDate: %s",
					minCal.getTime(), maxCal.getTime(), date));
		}
	}
	
	public String getDayOfWeek(Calendar c) {
		int wd = c.get(Calendar.DAY_OF_WEEK);
		String x = "";
		switch (wd) {
		case 1:
			x = "日";
			break;
		case 2:
			x = "一";
			break;
		case 3:
			x = "二";
			break;
		case 4:
			x = "三";
			break;
		case 5:
			x = "四";
			break;
		case 6:
			x = "五";
			break;
		case 7:
			x = "六";
			break;
		}
		return x;
	}
}
