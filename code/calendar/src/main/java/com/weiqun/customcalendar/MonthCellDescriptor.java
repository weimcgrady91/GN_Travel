// Copyright 2012 Square, Inc.

package com.weiqun.customcalendar;

import java.util.Calendar;
import java.util.Date;

/** Describes the state of a particular date cell in a {@link MonthView}. */
public class MonthCellDescriptor {
	public enum RangeState {
		NONE, FIRST, MIDDLE, LAST
	}

	private final Date date;
	private final int value;
	private final boolean isCurrentMonth;
	private boolean isSelected;
	private final boolean isToday;
	private final boolean isSelectable;
	private boolean isHighlighted;
	private RangeState rangeState;

	MonthCellDescriptor(Date date, boolean currentMonth, boolean selectable,
			boolean selected, boolean today, boolean highlighted, int value,
			RangeState rangeState) {
		this.date = date;
		isCurrentMonth = currentMonth;
		isSelectable = selectable;
		isHighlighted = highlighted;
		isSelected = selected;
		isToday = today;
		this.value = value;
		this.rangeState = rangeState;
	}

	public Date getDate() {
		return date;
	}

	public boolean isCurrentMonth() {
		return isCurrentMonth;
	}

	public boolean isSelectable() {
		return isSelectable;
	}

	public boolean isSelected() {
		return isSelected;
	}

	public void setSelected(boolean selected) {
		isSelected = selected;
	}

	boolean isHighlighted() {
		return isHighlighted;
	}

	void setHighlighted(boolean highlighted) {
		isHighlighted = highlighted;
	}

	public boolean isToday() {
		return isToday;
	}

	public RangeState getRangeState() {
		return rangeState;
	}

	public void setRangeState(RangeState rangeState) {
		this.rangeState = rangeState;
	}

	public int getValue() {
		return value;
	}

	@Override
	public String toString() {
		return "MonthCellDescriptor{" + "date=" + date + ", value=" + value
				+ ", isCurrentMonth=" + isCurrentMonth + ", isSelected="
				+ isSelected + ", isToday=" + isToday + ", isSelectable="
				+ isSelectable + ", isHighlighted=" + isHighlighted
				+ ", rangeState=" + rangeState + '}';
	}
	
	public boolean isBeforeTodady() {
		Calendar toady = Calendar.getInstance();
		Date todayDate = toady.getTime();
		if(date.before(todayDate)) {
			return true;
		} else  {
			return false;
		}
		
	}
}