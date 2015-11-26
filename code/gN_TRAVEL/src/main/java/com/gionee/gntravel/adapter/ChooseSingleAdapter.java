package com.gionee.gntravel.adapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckedTextView;
import android.widget.ImageView;
import android.widget.TextView;

import com.gionee.gntravel.R;

public class ChooseSingleAdapter extends BaseAdapter {

	private Activity mContext;
	private List<String> mList = new ArrayList<String>();
	private LayoutInflater mInflater;
	private onSingleSelectListner l;
	private HashMap<Integer, Boolean> checkMap = new HashMap<Integer, Boolean>();
	private int mPosition = 0;
	private List<View> views = new ArrayList<View>();
	private boolean isFirstSelectable;
	private onFirstClickListner onFirstClickListner;

	public ChooseSingleAdapter(Activity context, List<String> list, int position) {
		this(context, list, position, true);
	}

	public ChooseSingleAdapter(Activity context, List<String> list, int position, boolean isFirstSelectable) {
		mContext = context;
		mList = list;
		this.isFirstSelectable = isFirstSelectable;
		mInflater = LayoutInflater.from(mContext);
		this.mPosition = position;
	}

	@Override
	public int getCount() {
		return mList.size();
	}

	@Override
	public Object getItem(int position) {
		return null;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		if (views.size() > position) {
			return views.get(position);
		}
		ViewHolder holder = new ViewHolder();
		View layout = mInflater.inflate(R.layout.item_hotel_star, null);
		holder.item_hotel_single_tv = (TextView) layout.findViewById(R.id.item_hotel_single_tv);
		holder.item_hotel_filter_iv_back = (ImageView) layout.findViewById(R.id.item_hotel_filter_iv_back);
		holder.temp_tv_line = (TextView) layout.findViewById(R.id.temp_tv_line);
		holder.item_hotel_single_checktv = (CheckedTextView) layout.findViewById(R.id.item_hotel_single_checktv);

		if (mPosition == position) {
			checkMap.put(position, true);
		} else {
			checkMap.put(position, false);
		}

		holder.item_hotel_single_checktv.setChecked(checkMap.get(position));

		final String single_condition = mList.get(position);
		if (single_condition.equals("商业圈")||single_condition.equals("行政区")) {
			holder.item_hotel_filter_iv_back.setVisibility(View.VISIBLE);
		}
		holder.item_hotel_single_tv.setText(single_condition);

		if (position == 0) {
			if (isFirstSelectable) {
				setClickSelect(layout, single_condition);
			} else {
				holder.item_hotel_single_checktv.setVisibility(View.INVISIBLE);
				layout.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						if (onFirstClickListner != null) {
							onFirstClickListner.onClick(v);
						}
					}
				});
			}
		}else{
			setClickSelect(layout, single_condition);
		}
		views.add(layout);
		layout.setTag(position);
		return layout;
	}

	private void setClickSelect(View layout, final String single_condition) {
		layout.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				int position = (Integer) v.getTag();
				for (int i = 0; i < views.size(); i++) {
					ViewGroup item = (ViewGroup) views.get(i);
					CheckedTextView tvCheck = (CheckedTextView) item.findViewById(R.id.item_hotel_single_checktv);
					if (i != position) {
						tvCheck.setChecked(false);
					} else {
						tvCheck.setChecked(true);

						if (l != null) {
							l.selectSingle(position, single_condition);
						}
					}
					checkMap.put(position, tvCheck.isChecked());
				}

			}
		});
	}

	private class ViewHolder {
		private TextView item_hotel_single_tv;
		private TextView temp_tv_line;
		private CheckedTextView item_hotel_single_checktv;
		private ImageView item_hotel_filter_iv_back;
	}

	public void setOnSingleSelectListner(onSingleSelectListner l) {
		this.l = l;
	}

	public interface onSingleSelectListner {
		public void selectSingle(int position, String single_condition);
	}

	public interface onFirstClickListner {
		public void onClick(View view);
	}

	public void setOnFirstClickListner(onFirstClickListner l) {
		this.onFirstClickListner = l;
	}
	public void clickFirst() {
		isFirstSelectable = false;
		ViewGroup item0 = (ViewGroup) getView(0, null, null);
		item0.performClick();
	}

}
