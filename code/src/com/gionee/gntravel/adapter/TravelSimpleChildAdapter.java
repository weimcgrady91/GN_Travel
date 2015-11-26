package com.gionee.gntravel.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.gionee.gntravel.R;
import com.gionee.gntravel.entity.TripSimpleItem;
import com.gionee.gntravel.utils.DateUtils;

public class TravelSimpleChildAdapter extends BaseAdapter {
	public ArrayList<TripSimpleItem> tripSimpleItems;
	public Context context;
	public LayoutInflater mInflater;
	private final int FLIGHT_TYPE = 0;
	private final int HOTEL_TYPE = 1;
	
	public TravelSimpleChildAdapter(Context context,
			ArrayList<TripSimpleItem> tripSimpleItems) {
		this.context = context;
		this.tripSimpleItems = tripSimpleItems;
		mInflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	@Override
	public int getCount() {
		return tripSimpleItems.size();
	}

	@Override
	public Object getItem(int position) {
		return tripSimpleItems.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	
	
	@Override
	public int getItemViewType(int position) {
		String itemType = tripSimpleItems.get(position).getType();
		if("flight".equals(itemType)) {
			return FLIGHT_TYPE;
		} else {
			return HOTEL_TYPE;
		}
	}

	@Override
	public int getViewTypeCount() {
		return 2;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		HotelViewHolder hotelViewHolder = null;
		FlightViewHolder flightViewHolder = null;
		int type = getItemViewType(position);
		if(convertView  == null) {
			switch(type) {
			case FLIGHT_TYPE:
				convertView = mInflater.inflate(R.layout.group_flight_item, null);
				flightViewHolder = new FlightViewHolder();
				flightViewHolder.takeOffDate = (TextView) convertView.findViewById(R.id.tv_takeOffDate);
				flightViewHolder.takeOffTime = (TextView) convertView.findViewById(R.id.tv_takeOffTime);
				flightViewHolder.name = (TextView) convertView.findViewById(R.id.tv_name);
				convertView.setTag(flightViewHolder);
				break;
			case HOTEL_TYPE:
				convertView = mInflater.inflate(R.layout.group_hotel_item, null);
				hotelViewHolder = new HotelViewHolder();
				hotelViewHolder.checkInDate = (TextView) convertView.findViewById(R.id.tv_checkInDate);
				hotelViewHolder.name = (TextView) convertView.findViewById(R.id.tv_name);
				convertView.setTag(hotelViewHolder);
				break;
			}
		} else {
			switch(type) {
			case FLIGHT_TYPE:
				flightViewHolder = (FlightViewHolder) convertView.getTag();
				break;
			case HOTEL_TYPE:
				hotelViewHolder = (HotelViewHolder) convertView.getTag();
				break;
			}
		}
		
		switch(type) {
		case FLIGHT_TYPE:
			flightViewHolder.takeOffDate.setText(DateUtils.getDateWithFormat(tripSimpleItems.get(position).getDate()));
			flightViewHolder.takeOffTime.setText(DateUtils.dateToTime(tripSimpleItems.get(position).getDate()) + "起飞");
			flightViewHolder.name.setText(tripSimpleItems.get(position).getName());
			break;
		case HOTEL_TYPE:
			hotelViewHolder.checkInDate.setText(DateUtils.getDateWithFormat(tripSimpleItems.get(position).getDate()));
			hotelViewHolder.name.setText(tripSimpleItems.get(position).getName());
			break;
		}

		return convertView;
	}

	private static class HotelViewHolder {
		public TextView checkInDate;
		public TextView name;
	}

	private static class FlightViewHolder {
		public TextView takeOffDate;
		public TextView takeOffTime;
		public TextView name;
	}
	
	@Override
	public boolean isEnabled(int position) {
		return true;
	}
	
}
