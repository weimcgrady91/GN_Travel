package com.gionee.gntravel.adapter;


import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.gionee.gntravel.R;
import com.gionee.gntravel.entity.DomesticCity;

public class CityListViewAdapter extends BaseAdapter {
	private static final String HOT = "常选城市";
	private static final String GETLOCATION = "定位当前城市";
	private static final String LOCATIONFAILED = "定位失败";
	private Context context;
	private List<DomesticCity> list;
	private ViewHolder viewHolder;

	public CityListViewAdapter(Context context, List<DomesticCity> list) {
		this.context = context;
		this.list = list;
	}

	@Override
	public int getCount() {
		return list.size();
	}

	@Override
	public Object getItem(int position) {
		return list.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public boolean isEnabled(int position) {
		if (list.get(position).getCityName().length() == 1 
				|| list.get(position).getCityName().equals(HOT)
				|| list.get(position).getCityName().equals(GETLOCATION)
				|| list.get(position).getCityName().equals(LOCATIONFAILED))
			return false;
		return super.isEnabled(position);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		String item = list.get(position).getCityName();
		viewHolder = new ViewHolder();
		if (item.length() == 1 || item.equals(HOT) || item.equals(GETLOCATION)) {
			convertView = LayoutInflater.from(context).inflate(R.layout.city_item_index,
					null);
			viewHolder.indexTv = (TextView) convertView
					.findViewById(R.id.indexTv);
		} else {
			convertView = LayoutInflater.from(context).inflate(R.layout.city_item,
					null);
			viewHolder.itemTv = (TextView) convertView
					.findViewById(R.id.itemTv);
		}
		if (item.length() == 1 || item.equals(HOT) || item.equals(GETLOCATION)) {
			viewHolder.indexTv.setText(list.get(position).getCityName());
		} else {
			viewHolder.itemTv.setText(list.get(position).getCityName());
		}
		return convertView;
	}

	private class ViewHolder {
		private TextView indexTv;
		private TextView itemTv;
	}

}
