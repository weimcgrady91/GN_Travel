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
import android.widget.TextView;

import com.gionee.gntravel.R;

public class ChooseHotelDistanceAdapter extends BaseAdapter {

	private Activity mContext;
	private List<String> mList = new ArrayList<String>();
	private LayoutInflater mInflater;
	private onDistanceSelectListner l;
	private HashMap<Integer, Boolean> checkMap = new HashMap<Integer, Boolean>();
	public ChooseHotelDistanceAdapter(Activity context, List<String> list) {
		mContext = context;
		mList = list;
		mInflater = LayoutInflater.from(mContext);
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
		ViewHolder holder = null;
		if (convertView == null) {
			holder = new ViewHolder();
			convertView = mInflater.inflate(R.layout.item_hotel_distance, null);
			holder.item_hotel_distance_tv = (TextView) convertView.findViewById(R.id.item_hotel_distance_tv);
//			holder.temp_tv_line = (TextView) convertView.findViewById(R.id.temp_tv_line);
			holder.item_hotel_distance_checktv = (CheckedTextView) convertView.findViewById(R.id.item_hotel_distance_checktv);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		Boolean check = checkMap.get(position);
		if (check == null) {
			check = false;
		}
		if (position==0) {
//			holder.temp_tv_line.setVisibility(View.GONE);
			holder.item_hotel_distance_checktv.setChecked(true);
		}
		final String distance = mList.get(position);
		holder.item_hotel_distance_tv.setText(distance);
		final View temConverView = convertView;
		convertView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				ViewGroup vg = (ViewGroup) (temConverView.getParent());

				for (int i = 0; i < vg.getChildCount(); i++) {
					ViewGroup item = (ViewGroup) vg.getChildAt(i);
					CheckedTextView tvCheck = (CheckedTextView) item.findViewById(R.id.item_hotel_distance_checktv);
					if (i != position) {
						tvCheck.setChecked(false);
					} else {
						tvCheck.setChecked(true);
						
						if (l!=null) {
							l.selectDistance(position,distance);
						}
					}
					checkMap.put(position, tvCheck.isChecked());
				}

			}
		});
		return convertView;
	}
	private class ViewHolder {
		private TextView item_hotel_distance_tv;
		private TextView temp_tv_line;
		private CheckedTextView item_hotel_distance_checktv;
	}
	public void setOnDistanceSelectListner(onDistanceSelectListner l) {
		this.l = l;
	}
	public interface onDistanceSelectListner {
		public void selectDistance(int position,String distance);
	}
}
