package com.weiqun.customcalendar;

import java.util.List;

import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.TextView;

public class CalendarAdapter extends PagerAdapter {
	List<MonthView> viewLists;
    public CalendarAdapter(List<MonthView> lists)
    {
        viewLists = lists;
    }
	
	@Override
	public void destroyItem(View arg0, int arg1, Object arg2) {
		((ViewPager)arg0).removeView(viewLists.get(arg1));
	}

	@Override
	public void finishUpdate(View arg0) {

	}

	@Override
	public int getCount() {
		 return viewLists.size();
	}

	@Override
	public Object instantiateItem(View arg0, int arg1) {
		((ViewPager)arg0).addView(viewLists.get(arg1), 0);
        return viewLists.get(arg1);
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

}
