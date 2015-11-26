package com.gionee.gntravel.widget;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;

public class MyViewPager extends ViewPager {

	private MyScrollView sv;

	public MyViewPager(Context context) {
		super(context);
	}

	public MyViewPager(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public void setMyScrollView(MyScrollView sv) {
		this.sv = sv;
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			sv.setShouldScroll(false);
			Log.e("viewpager onTouch", "ACTION_DOWN");
			break;
		case MotionEvent.ACTION_MOVE:
			sv.setShouldScroll(false);
			Log.e("viewpager onTouch", "ACTION_MOVE");
			break;
		case MotionEvent.ACTION_UP:
			sv.setShouldScroll(true);
			Log.e("viewpager onTouch", "ACTION_UP");
			break;
		}
		return super.onTouchEvent(event);
	}

}
