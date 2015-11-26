package com.gionee.gntravel.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.ScrollView;

@SuppressLint("ClickableViewAccessibility")
public class MyScrollView extends ScrollView {
	private boolean shouldScroll = true;

	public MyScrollView(Context context) {
		super(context);
	}

	public MyScrollView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public MyScrollView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	@Override
	public boolean onTouchEvent(MotionEvent ev) {
		boolean result = shouldScroll ? super.onTouchEvent(ev) : false;
		if (!shouldScroll) {
			if (ev.getAction() == MotionEvent.ACTION_CANCEL || ev.getAction() == MotionEvent.ACTION_UP) {
				setShouldScroll(true);
			}
		}
		return false;
	}

	public boolean isShouldScroll() {
		return shouldScroll;
	}

	public void setShouldScroll(boolean shouldScroll) {
		this.shouldScroll = shouldScroll;
	}

}
