package com.gionee.gntravel.adapter;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.ctrip.openapi.java.utils.ImageUtil;
import com.gionee.gntravel.HotelOrderActivity;
import com.gionee.gntravel.LoginActivity;
import com.gionee.gntravel.R;
import com.gionee.gntravel.TravelApplication;
import com.gionee.gntravel.entity.GuestRoom;

public class GuestRoomListAdapter extends BaseAdapter {

	private List<GuestRoom> mGuestRooms = new ArrayList<GuestRoom>();
	private LayoutInflater mInflater;
	private Activity mContext;
	private String mHotelName;
	private boolean bookable;
	private onMySelectDateListner onDateListner;

	public GuestRoomListAdapter(List<GuestRoom> guestRooms, Activity context,  String hotelName,boolean bookable) {
		this.mGuestRooms = guestRooms;
		this.mContext = context;
		mInflater = LayoutInflater.from(mContext);
		mHotelName = hotelName;
		this.bookable = bookable;
	}

	@Override
	public int getCount() {
		return mGuestRooms.size();
	}

	@Override
	public Object getItem(int position) {
		return mGuestRooms.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		if (convertView == null) {
			holder = new ViewHolder();
			convertView = mInflater.inflate(R.layout.item_guestroom, null);
			holder.room_iv_pic = (ImageView) convertView.findViewById(R.id.hotel_detail_iv_roompic);
			holder.room_tv_name = (TextView) convertView.findViewById(R.id.hotel_detail_tv_roomtypename);
			holder.room_tv_breakfast = (TextView) convertView.findViewById(R.id.hotel_detail_tv_room_breakfast);
			holder.room_tv_roomtype = (TextView) convertView.findViewById(R.id.hotel_detail_tv_room_type);
			holder.room_tv_guarantee = (TextView) convertView.findViewById(R.id.hotel_detail_tv_room_guarantee);
			holder.room_tv_price = (TextView) convertView.findViewById(R.id.hotel_detail_tv_room_price);
			holder.temp_tv_line = (TextView) convertView.findViewById(R.id.temp_tv_line);
			holder.room_book = (Button) convertView.findViewById(R.id.hotel_detail_dt_room_book);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		if (position==0) {
			holder.temp_tv_line.setVisibility(View.GONE);
		}
		final GuestRoom guestRoom = mGuestRooms.get(position);
		holder.room_book.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				onDateListner.selectDate(guestRoom, mHotelName);
			}
		});
		String status = guestRoom.getStatus();
		if ("Close".equals(status)) {
			holder.room_book.setBackgroundResource(R.drawable.noorder);
			holder.room_book.setText("订完");
			holder.room_book.setClickable(false);
		}
		final String name = guestRoom.getRoomTypeName();
		holder.room_tv_name.setText(name);
		String breakFast = guestRoom.getBreakfast();
		holder.room_tv_breakfast.setText(breakFast);
		String guaranteeCode = guestRoom.getGuaranteeCode();
		String ratePlangory = guestRoom.getRatePlanCategory();
		
		if (!TextUtils.isEmpty(guaranteeCode)) {
			if ("超时担保".equals(guaranteeCode)) {
				guaranteeCode = "";
			}else if ("一律担保".equals(guaranteeCode)) {
				guaranteeCode = mContext.getResources().getString(R.string.danbao);
			}
		}
		if (!TextUtils.isEmpty(ratePlangory)) {
			if ("501".equals(ratePlangory)||("502".equals(ratePlangory))) {
				holder.room_book.setText("预付");
			}
		}
		holder.room_tv_guarantee.setText(guaranteeCode);
		String price = guestRoom.getAmountBeforeTax()+"";
		if (TextUtils.isEmpty(price)) {
			holder.room_tv_price.setText("未知价格");
		} else {
			holder.room_tv_price.setText(""+(int)guestRoom.getAmountBeforeTax());
		}
		String url = guestRoom.getUrl();

		if (!TextUtils.isEmpty(url)) {
			url = url.replaceAll("_[\\d]+_[\\d]+", "_100_75");
		}
		if ( ((TravelApplication)mContext.getApplication()).isShowImgWithWifiState()) {
			ConnectivityManager connManager = (ConnectivityManager) mContext
					.getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo networkInfo = connManager.getActiveNetworkInfo();
			if (networkInfo.isConnected()) {
				if (networkInfo.getType() == ConnectivityManager.TYPE_WIFI) {
					ImageUtil.getInstance(mContext).display(url, holder.room_iv_pic, null);
				} 
			}
		} else {
			ImageUtil.getInstance(mContext).display(url, holder.room_iv_pic, null);
		}
		convertView.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				View view = mInflater.inflate(R.layout.activity_room_detail, null);
				String area = guestRoom.getRoomSize()+"m²";
				String floor = guestRoom.getFloor()+"层";
				String bedCode = guestRoom.getBedTypeCode();
				String bedSize = guestRoom.getSize();
				String netInfo = guestRoom.getNetInfo();
				String bed  = "未知";
				if (!TextUtils.isEmpty(bedSize)&&!TextUtils.isEmpty(bedSize)) {
					bed = bedCode + bedSize+"米";
				}
				
				String people = guestRoom.getStandardOccupancy()+"人";
				int price = (int)guestRoom.getAmountBeforeTax();
				
				String status = guestRoom.getStatus();
				TextView dialog_tv_roomname = (TextView) view.findViewById(R.id.dialog_tv_roomname);
				TextView dialog_tv_area = (TextView) view.findViewById(R.id.dialog_tv_area);
				TextView dialog_tv_floor = (TextView) view.findViewById(R.id.dialog_tv_floor);
				TextView dialog_tv_bed = (TextView) view.findViewById(R.id.dialog_tv_bed);
				ImageButton dialog_bt_close = (ImageButton) view.findViewById(R.id.dialog_bt_close);
				TextView dialog_tv_price = (TextView) view.findViewById(R.id.dialog_tv_price);
				ImageView dialog_iv_img = (ImageView) view.findViewById(R.id.dialog_iv_img);
				Button dialog_bt_book = (Button) view.findViewById(R.id.dialog_bt_book);
				TextView dialog_tv_people = (TextView) view.findViewById(R.id.dialog_tv_people);
				TextView dialog_tv_net = (TextView) view.findViewById(R.id.dialog_tv_net);
				if (!bookable) {
					dialog_bt_book.setVisibility(View.GONE);
				}
				dialog_tv_people.setText(people);
				dialog_tv_roomname.setText(name);
				dialog_tv_area.setText(area);
				dialog_tv_bed.setText(bed);
				dialog_tv_floor.setText(floor);
				dialog_tv_price.setText(price+"");
				dialog_tv_net.setText(netInfo);

				if ("Close".equals(status)) {
					dialog_bt_book.setBackgroundResource(R.drawable.noorder);
					dialog_bt_book.setText("订完");
					dialog_bt_book.setEnabled(false);
					dialog_bt_book.setClickable(false);
				}
				if (((TravelApplication)mContext.getApplication()).isShowImgWithWifiState()) {
					ConnectivityManager connManager = (ConnectivityManager) mContext
							.getSystemService(Context.CONNECTIVITY_SERVICE);
					NetworkInfo networkInfo = connManager.getActiveNetworkInfo();
					if (networkInfo.isConnected()) {
						if (networkInfo.getType() == ConnectivityManager.TYPE_WIFI) {
							ImageUtil.getInstance(mContext).display(guestRoom.getUrl(), dialog_iv_img, R.drawable.defaltimg307144,
									R.drawable.defaltimg307144);
						} 
					}
				} else {
					ImageUtil.getInstance(mContext).display(guestRoom.getUrl(), dialog_iv_img, R.drawable.defaltimg307144,
							R.drawable.defaltimg307144);
				}
				final Dialog d = new Dialog(mContext, R.style.MyDialog);
				d.getWindow().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#00000000")));
				d.setContentView(view);
				dialog_bt_close.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						d.dismiss();
					}
				});
				dialog_bt_book.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
//						final String indate = detail_tv_indate.getText().toString();
//						final String leavedate = detail_tv_leavedate.getText().toString();
						d.dismiss();
//						gotoHotelOrder(guestRoom, inDate, leaveDate);
						onDateListner.selectDate(guestRoom, mHotelName);
					}
				});
				d.show();
			}
		});

		if (!bookable) {
			holder.room_book.setVisibility(View.GONE);
		}
		return convertView;
	}
	private class ViewHolder {
		private ImageView room_iv_pic;
		private TextView room_tv_name;
		private TextView room_tv_breakfast;
		private TextView room_tv_roomtype;
		private TextView room_tv_guarantee;
		private TextView room_tv_price;
		private TextView temp_tv_line;
		private Button room_book;
	}
	public void setonSelectDateListner(onMySelectDateListner l){
		onDateListner = l;
	}
	public interface onMySelectDateListner{
		public void selectDate(GuestRoom guestRoom,String hotelName);
	}

}
