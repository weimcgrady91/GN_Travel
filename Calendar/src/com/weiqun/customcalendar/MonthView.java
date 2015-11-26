// Copyright 2012 Square, Inc.
package com.weiqun.customcalendar;

import static java.util.Calendar.HOUR_OF_DAY;
import static java.util.Calendar.MILLISECOND;
import static java.util.Calendar.MINUTE;
import static java.util.Calendar.SECOND;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.weiqun.customcalendar.R;


public class MonthView extends LinearLayout {
	CalendarGridView grid;
	private Listener listener;
	public String title;
	private Calendar mToday;
	private static LayoutInflater mInflater;

	public static MonthView create(ViewGroup parent, LayoutInflater inflater,
			DateFormat weekdayNameFormat, Listener listener, Calendar today) {
		final MonthView view = (MonthView) inflater.inflate(R.layout.month,null, false);
		view.listener = listener;
		mInflater = inflater;
		return view;
	}

	public MonthView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	@Override
	protected void onFinishInflate() {
		super.onFinishInflate();
		grid = (CalendarGridView) findViewById(R.id.calendar_grid);
		mToday = Calendar.getInstance();
		setMidnight(mToday);
	}

	public void init(MonthDescriptor month,
			List<List<MonthCellDescriptor>> cells, boolean displayOnly) {
		title = month.getLabel();
		final int numRows = cells.size();
		grid.setNumRows(numRows);
		for (int i = 0; i < 6; i++) {
			
			
			
			CalendarRowView weekRow = (CalendarRowView) grid.getChildAt(i);
			
			
			weekRow.setListener(listener);
			if (i < numRows) {
				weekRow.setVisibility(VISIBLE);
				List<MonthCellDescriptor> week = cells.get(i);
				for (int c = 0; c < week.size(); c++) {
					MonthCellDescriptor cell = week.get(c);
					
					
					
					
//					CalendarCellView cellView = (CalendarCellView) weekRow.getChildAt(c);
					
					RelativeLayout cellViewContainer = (RelativeLayout) weekRow.getChildAt(c);
//					
					CalendarCellView cellView  = (CalendarCellView )cellViewContainer.getChildAt(0);
					cellView.setText(Integer.toString(cell.getValue()));
					cellView.setEnabled(cell.isCurrentMonth() && cell.isSelectable());
//					cellView.setClickable(!displayOnly);
					cellView.setClickable(false);
					cellView.setSelectable(cell.isSelectable());
					cellView.setSelected(cell.isSelected());
					cellView.setCurrentMonth(cell.isCurrentMonth());
					cellView.setToday(cell.isToday());
					cellView.setRangeState(cell.getRangeState());
					cellView.setHighlighted(cell.isHighlighted());
//					cellView.setTag(cell);
					cellViewContainer.setTag(cell);
					
					if(cell.getDate().before(mToday.getTime())) {
						cellView.setHighlighted(true);
					}
					int date = cell.getValue();
//					if(date == 1 && cell.isCurrentMonth() == true ) {
//						cellView.setText((month.getMonth()+1)+"月");
//						cellView.setTextColor(getResources().getColor(R.color.today_text_color));
//					}
					if(cell.isToday()) {
						TextView str_date_src = (TextView) (cellViewContainer.getChildAt(0));
						RelativeLayout rl = (RelativeLayout)mInflater.inflate(R.layout.spec_week_item, null);
						TextView str_date = (TextView) rl.findViewById(R.id.spec_num);
						str_date.setTextColor(getResources().getColor(R.color.today_text_color));
						str_date.setText(str_date_src.getText());
						TextView dis = (TextView)rl.findViewById(R.id.spec_des);
						dis.setText("今天");
						cellViewContainer.removeAllViews();
						LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(LinearLayout.
		                        LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.MATCH_PARENT);
						cellViewContainer.addView(rl,param);
//						cellViewContainer.setTag(0x11111,rl);
					} 
					if(cell.isSelected() && cell.isToday()) {
						TextView str_date_src = (TextView) (cellViewContainer.findViewById(R.id.spec_num));
						RelativeLayout rl = (RelativeLayout)mInflater.inflate(R.layout.spec_week_item, null);
						TextView str_date = (TextView) rl.findViewById(R.id.spec_num);
						str_date.setTextColor(getResources().getColor(R.color.state_selected_text_color));
						str_date.setText(str_date_src.getText());
						TextView dis = (TextView)rl.findViewById(R.id.spec_des);
						dis.setText("出发");
						dis.setTextColor(getResources().getColor(R.color.state_selected_text_color));
						cellViewContainer.removeAllViews();
						LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(LinearLayout.
		                        LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.MATCH_PARENT);
						cellViewContainer.addView(rl, param);
						cellViewContainer.setBackgroundColor(getResources().getColor(R.color.common_blue));
//						cellViewContainer.setTag(0x11111,rl);

					} else if(cell.isSelected()) {
							TextView str_date_src = (TextView) (cellViewContainer.getChildAt(0));
							RelativeLayout rl = (RelativeLayout)mInflater.inflate(R.layout.spec_week_item, null);
							TextView str_date = (TextView) rl.findViewById(R.id.spec_num);
							str_date.setTextColor(getResources().getColor(R.color.state_selected_text_color));
							str_date.setText(str_date_src.getText());
							TextView dis = (TextView)rl.findViewById(R.id.spec_des);
							dis.setText("出发");
							dis.setTextColor(getResources().getColor(R.color.state_selected_text_color));
							cellViewContainer.removeAllViews();
							LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(LinearLayout.
			                        LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.MATCH_PARENT);
							cellViewContainer.addView(rl, param);
							cellViewContainer.setBackgroundColor(getResources().getColor(R.color.common_blue));
//							cellViewContainer.setTag(0x11111,rl);
					}
					
//					if(cell.isBeforeTodady()) {
//						cellViewContainer.setClickable(false);
//					} else {
//						cellViewContainer.setClickable(true);
//					}
					
					if(!cell.isCurrentMonth()) {
						cellView.setText("");
						cellView.setClickable(false);
						cellViewContainer.setClickable(false);
					} else {
					}

					if(cell.isSelectable()) {
						cellView.setTextColor(getResources().getColor(R.color.date_can_press));
					} else {
						cellView.setTextColor(getResources().getColor(R.color.date_not_press));
					}
					
					if(date == 1 && cell.isCurrentMonth() == true ) {
						cellView.setText((month.getMonth()+1)+"月");
						cellView.setTextColor(getResources().getColor(R.color.today_text_color));
					}
				}
			} else {
				weekRow.setVisibility(GONE);
			}
		}
	}
	
	public void setMidnight(Calendar cal) {
		cal.set(HOUR_OF_DAY, 0);
		cal.set(MINUTE, 0);
		cal.set(SECOND, 0);
		cal.set(MILLISECOND, 0);
	}
	
	public interface Listener {
		void handleClick(MonthCellDescriptor cell);
	}
}
