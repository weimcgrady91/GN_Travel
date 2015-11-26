// Copyright 2012 Square, Inc.
package com.weiqun.customcalendar;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import static android.view.View.MeasureSpec.AT_MOST;
import static android.view.View.MeasureSpec.EXACTLY;
import static android.view.View.MeasureSpec.makeMeasureSpec;

/**
 * TableRow that draws a divider between each cell. To be used with
 * {@link CalendarGridView}.
 */
public class CalendarRowView extends LinearLayout implements View.OnClickListener {
	private boolean isHeaderRow;
	private MonthView.Listener listener;
	private int cellSize;

	public CalendarRowView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}
	
	@Override
	public void addView(View child, int index, ViewGroup.LayoutParams params) {
		child.setOnClickListener(this);
		super.addView(child, index, params);
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
//		long start = System.currentTimeMillis();
//		final int totalWidth = MeasureSpec.getSize(widthMeasureSpec);
//		cellSize = totalWidth / 7;
//		int cellWidthSpec = makeMeasureSpec(cellSize, EXACTLY);
//		int cellHeightSpec = isHeaderRow ? makeMeasureSpec(heightMeasureSpec, EXACTLY)
//				: cellWidthSpec;
//		int rowHeight = 0;
//		for (int c = 0, numChildren = getChildCount(); c < numChildren; c++) {
//			final View child = getChildAt(c);
//			child.measure(cellWidthSpec, cellHeightSpec);
//			// The row height is the height of the tallest cell.
//			if (child.getMeasuredHeight() > rowHeight) {
//				rowHeight = child.getMeasuredHeight();
//			}
//		}
//		final int widthWithPadding = totalWidth + getPaddingLeft()
//				+ getPaddingRight();
//		final int heightWithPadding = rowHeight + getPaddingTop()
//				+ getPaddingBottom();
//		setMeasuredDimension(widthWithPadding, heightWithPadding);
//		setMeasuredDimension(widthMeasureSpec, heightMeasureSpec);
//		Logr.d("Row.onMeasure %d ms", System.currentTimeMillis() - start);
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
	}

//	@Override
//	protected void onLayout(boolean changed, int left, int top, int right,
//			int bottom) {
//		long start = System.currentTimeMillis();
//		int cellHeight = bottom - top;
//		for (int c = 0, numChildren = getChildCount(); c < numChildren; c++) {
//			final View child = getChildAt(c);
//			child.layout(c * cellSize, 0, (c + 1) * cellSize, cellHeight);
//		}
//		Logr.d("Row.onLayout %d ms", System.currentTimeMillis() - start);
//	}
//
//	public void setIsHeaderRow(boolean isHeaderRow) {
//		this.isHeaderRow = isHeaderRow;
//	}
//
	@Override
	public void onClick(View v) {
		// Header rows don't have a click listener
		if (listener != null) {
			listener.handleClick((MonthCellDescriptor) v.getTag());
		}
	}

	public void setListener(MonthView.Listener listener) {
		this.listener = listener;
	}
}
