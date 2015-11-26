package com.gionee.gntravel.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.gionee.gntravel.OrderDetailActivity;
import com.gionee.gntravel.R;
import com.gionee.gntravel.entity.UserOrderEntity;
import com.gionee.gntravel.utils.DateUtils;

public class MyOrderAdapter extends BaseAdapter {
	
	private Context mContext;
	private ArrayList<UserOrderEntity> list;
	private LayoutInflater mInflater ;
	
	public MyOrderAdapter(Context context, ArrayList<UserOrderEntity> list) {
		this.mContext =  context;
		this.list = list;
		this.mInflater = LayoutInflater.from(context);
	}
	
	public void setList(ArrayList<UserOrderEntity> list) {
		this.list = list;
	}
	
	@Override
	public int getCount() {
		return list.size();
	}

	@Override
	public Object getItem(int position) {
		return position;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		ViewHolder viewHolder = null;
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.orderlist_flight_item, null);
			viewHolder = new ViewHolder();
			viewHolder.tv_name = (TextView) convertView.findViewById(R.id.tv_name);
			viewHolder.tv_orderAirPort = (TextView) convertView.findViewById(R.id.tv_orderAirPort);
			viewHolder.tv_orderId = (TextView) convertView.findViewById(R.id.tv_orderId);
			viewHolder.tv_orderTakeOffDate = (TextView) convertView.findViewById(R.id.tv_orderTakeOffDate);
			viewHolder.tv_orderPrice = (TextView) convertView.findViewById(R.id.tv_orderPrice);
			viewHolder.tv_orderState = (TextView) convertView.findViewById(R.id.tv_orderState);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		switch (list.get(position).getOrderType()) {
		case FLIGHT:
			viewHolder.tv_name.setText(list.get(position).getFlight());
			viewHolder.tv_orderAirPort.setText(list.get(position).getdAirPortName()+ "-" + list.get(position).getaAirPortName());
			viewHolder.tv_orderAirPort.setVisibility(View.VISIBLE);
			viewHolder.tv_orderTakeOffDate.setText("出发日期:"+ DateUtils.fomatStrDate(list.get(position).getStartDate()));
			break;
		case HOTEL:
			viewHolder.tv_name.setText(list.get(position).getHotelName());
			viewHolder.tv_orderAirPort.setVisibility(View.GONE);
			viewHolder.tv_orderTakeOffDate.setText("入住日期:"+ DateUtils.fomatStrDate(list.get(position).getStartDate()));
			break;
		default:
			break;
		}
		viewHolder.tv_orderPrice.setText(list.get(position).getPrice());
		viewHolder.tv_orderState.setText(list.get(position).getStatus());
		viewHolder.tv_orderId.setText("订单编号:" + list.get(position).getOrderId());

		convertView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				String orderId=list.get(position).getOrderId();
				String orderType=list.get(position).getType();
				Intent intent = new Intent(mContext,OrderDetailActivity.class);
				intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
				intent.putExtra("orderId", orderId);
				intent.putExtra("orderType", orderType);
				intent.putExtra("price", list.get(position).getPrice());
				intent.putExtra("orderStatus", list.get(position).getStatus());
				mContext.startActivity(intent);
			}
		});

		return convertView;
	}

	static class ViewHolder {
		public TextView tv_name;
		public TextView tv_orderAirPort;
		public TextView tv_orderId;
		public TextView tv_orderTakeOffDate;
		public TextView tv_orderPrice;
		public TextView tv_orderState;
	}

}
