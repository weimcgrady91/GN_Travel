package com.gionee.gntravel.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.webkit.WebView.FindListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.gionee.gntravel.HotelMapDetailActivity;
import com.gionee.gntravel.MapRouteActivity;
import com.gionee.gntravel.OrderDetailActivity;
import com.gionee.gntravel.R;
import com.gionee.gntravel.WeatherActivity;
import com.gionee.gntravel.db.dao.impl.TripAlarmDaoImpl;
import com.gionee.gntravel.entity.AlarmEntity;
import com.gionee.gntravel.entity.Hotel;
import com.gionee.gntravel.entity.OrderFormDetailEntity;
import com.gionee.gntravel.entity.WeatherBean;
import com.gionee.gntravel.utils.DBUtils;
import com.gionee.gntravel.utils.DateUtils;
import com.gionee.gntravel.utils.TripAlarmControl;

public class TripFormAdapter extends BaseAdapter {
	private final int FLIGHT_TYPE = 1;
	private final int HOTEL_TYPE = 2;
	private final int WEATHER_TYPE = 3;
	private LayoutInflater mInflater;
	private Context mContext;
	private ArrayList<OrderFormDetailEntity> mList;
	private ArrayList<String> mCityNameList;
	private String mTripName;
	private String mUserName;

	public TripFormAdapter(Context context,
			ArrayList<OrderFormDetailEntity> list,
			ArrayList<String> cityNameList, String tripName, String userName) {
		this.mContext = context;
		mInflater = LayoutInflater.from(context);
		mList = list;
		mCityNameList = cityNameList;
		mTripName = tripName;
		mUserName = userName;
	}

	@Override
	public int getCount() {
		return mList.size();
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
		FlightViewHolder flightViewHolder = null;
		HotelViewHolder hotelViewHolder = null;
		int type = getItemViewType(position);
		if (convertView == null) {
			switch (type) {
			case FLIGHT_TYPE:
				convertView = mInflater.inflate(
						R.layout.tripform_flightdetail_item, null);
				flightViewHolder = new FlightViewHolder();
				flightViewHolder.reOrderNumber = (RelativeLayout) convertView
						.findViewById(R.id.re_order_form_number);
				flightViewHolder.airLineName = (TextView) convertView
						.findViewById(R.id.tv_order_form_airline_name);
				flightViewHolder.orderNumber = (TextView) convertView
						.findViewById(R.id.tv_order_form_number);
				flightViewHolder.takeOffTime = (TextView) convertView
						.findViewById(R.id.tv_takeOffTime);
				flightViewHolder.arriveWay = (TextView) convertView
						.findViewById(R.id.btn_order_arrive);
				flightViewHolder.selectWay = (ViewGroup) convertView
						.findViewById(R.id.way);
				flightViewHolder.airstartAirPortName = (TextView) convertView
						.findViewById(R.id.tv_order_form_d_port_name);
				flightViewHolder.airstopAirPortName = (TextView) convertView
						.findViewById(R.id.tv_order_form_a_port_name);
				flightViewHolder.personName = (TextView) convertView
						.findViewById(R.id.tv_order_form_passenger);
				flightViewHolder.imgTravelWarn = (ImageView) convertView
						.findViewById(R.id.img_order_form_message_warn);
				flightViewHolder.takeOffDate = (TextView) convertView
						.findViewById(R.id.tv_takeOffDate);
				flightViewHolder.arriveTime = (TextView) convertView
						.findViewById(R.id.tv_arriveTime);
				flightViewHolder.trip_warn = (TextView) convertView
						.findViewById(R.id.tv_trip_warn);
				flightViewHolder.imgFlightOrderArrow = (ImageView) convertView
						.findViewById(R.id.img_flight_order_arrow);
				convertView.setTag(flightViewHolder);
				break;
			case HOTEL_TYPE:
				convertView = mInflater.inflate(
						R.layout.tripform_hotaldetail_item, null);
				hotelViewHolder = new HotelViewHolder();
				hotelViewHolder.position = (TextView) convertView
						.findViewById(R.id.btn_order_my_location);
				hotelViewHolder.reOrderNumber = (RelativeLayout) convertView
						.findViewById(R.id.re_order_form_number);
				hotelViewHolder.reHotalTel = (RelativeLayout) convertView
						.findViewById(R.id.re_order_form_hotal_tel);
				hotelViewHolder.rehotelAddress = (RelativeLayout) convertView
						.findViewById(R.id.re_hotel_address);
				hotelViewHolder.hotalAddress = (TextView) convertView
						.findViewById(R.id.tv_order_address);
				hotelViewHolder.orderNumber = (TextView) convertView
						.findViewById(R.id.tv_order_form_hotal_number);
				hotelViewHolder.hotalName = (TextView) convertView
						.findViewById(R.id.tv_order_form_hotal_name);
				hotelViewHolder.hotalroomTypeName = (TextView) convertView
						.findViewById(R.id.tv_order_form_hotal_type);
				hotelViewHolder.hotalWarning = (TextView) convertView
						.findViewById(R.id.tv_order_form_hotal_warning);
				hotelViewHolder.hotalfromTime = (TextView) convertView
						.findViewById(R.id.tv_order_form_hotal_ruzhu_from_time);
				hotelViewHolder.hotalToTime = (TextView) convertView
						.findViewById(R.id.tv_order_form_hotal_ruzhu_to_time);
				hotelViewHolder.arriveWay = (TextView) convertView
						.findViewById(R.id.btn_order_arrive);
				hotelViewHolder.selectWay = (ViewGroup) convertView
						.findViewById(R.id.way);
				hotelViewHolder.personName = (TextView) convertView
						.findViewById(R.id.tv_order_form_hotal_person);
				hotelViewHolder.imgHotelOrderArrow = (ImageView) convertView
						.findViewById(R.id.img_hotel_order_arrow);
				hotelViewHolder.imgHotelAddressArrow = (ImageView) convertView
						.findViewById(R.id.img_hotel_address_arrow);
				convertView.setTag(hotelViewHolder);
				break;

			}
		} else {
			switch (type) {
			case FLIGHT_TYPE:
				flightViewHolder = (FlightViewHolder) convertView.getTag();
				break;
			case HOTEL_TYPE:
				hotelViewHolder = (HotelViewHolder) convertView.getTag();
				break;
			}
		}

		switch (type) {
		case FLIGHT_TYPE:
			final String flightLongitude = mList.get(position).getLongitude();
			final String flightLatitude = mList.get(position).getLatitude();
			flightViewHolder.airLineName.setText(mList.get(position)
					.getAirLineName());
			String takeOffDate = DateUtils.getTimeWithSlash(mList.get(position)
					.getTakeOffTime());
			flightViewHolder.takeOffDate.setText(takeOffDate);
			flightViewHolder.takeOffTime.setText(DateUtils.dateToTime(mList
					.get(position).getTakeOffTime()));
			flightViewHolder.arriveTime.setText(DateUtils.dateToTime(mList.get(
					position).getArriveTime()));
			flightViewHolder.arriveWay.setText(mList.get(position)
					.getAirstartAirPortName());
			flightViewHolder.airstartAirPortName.setText(mList.get(position)
					.getAirstartAirPortName());
			flightViewHolder.airstopAirPortName.setText(mList.get(position)
					.getAirstopAirPortName());
			flightViewHolder.personName
					.setText(mList.get(position).getDjName());
			flightViewHolder.orderNumber.setText(mList.get(position)
					.getOrderCode());
			initAlarmSwitchState(position, flightViewHolder.imgTravelWarn,
					flightViewHolder.trip_warn);
			if (isExample()) {

				final ImageView imgTravelWarn = flightViewHolder.imgTravelWarn;
				final TextView tripWarn = flightViewHolder.trip_warn;
				flightViewHolder.imgTravelWarn
						.setOnClickListener(new OnClickListener() {
							@Override
							public void onClick(View v) {
								setAlarmSwitch(position, imgTravelWarn,
										tripWarn);
							}
						});

				flightViewHolder.selectWay
						.setOnClickListener(new OnClickListener() {

							@Override
							public void onClick(View arg0) {
								showMapRoute(flightLongitude, flightLatitude);
							}
						});

				flightViewHolder.reOrderNumber
						.setOnClickListener(new OnClickListener() {

							@Override
							public void onClick(View arg0) {
								Intent intent = new Intent(mContext,
										OrderDetailActivity.class);
								intent.putExtra("orderType", "flight");
								intent.putExtra("orderId", mList.get(position)
										.getOrderCode());
								intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
								mContext.startActivity(intent);
							}
						});
			} else {
				flightViewHolder.imgFlightOrderArrow.setVisibility(View.GONE);
			}
			break;
		case HOTEL_TYPE:
			final String orderId = mList.get(position).getOrderCode();
			final String hotelName = mList.get(position).getHotalName();
			final String hotelTel = mList.get(position).getHotaltelephone();
			final String hotelLongitude = mList.get(position).getLongitude();
			final String hotelLatitude = mList.get(position).getLatitude();
			hotelViewHolder.hotalName.setText(mList.get(position)
					.getHotalName());
			hotelViewHolder.hotalroomTypeName.setText(mList.get(position)
					.getHotalroomTypeName());
			hotelViewHolder.hotalWarning.setText(mList.get(position)
					.getHotaltelephone());
			hotelViewHolder.hotalfromTime.setText(mList.get(position)
					.getHotalfromTime());
			hotelViewHolder.hotalToTime.setText(mList.get(position)
					.getHotalToTime());
			hotelViewHolder.arriveWay.setText(mList.get(position)
					.getHotalName());
			hotelViewHolder.personName.setText(mList.get(position).getDjName());
			hotelViewHolder.hotalAddress.setText(mList.get(position)
					.getHotalAddress());
			hotelViewHolder.position.setText(mList.get(position).getLocation());
			hotelViewHolder.orderNumber.setText(mList.get(position)
					.getOrderCode());
			if (isExample()) {

				hotelViewHolder.reOrderNumber
						.setOnClickListener(new OnClickListener() {

							@Override
							public void onClick(View arg0) {
								showOrderDetail(orderId);
							}

						});
				hotelViewHolder.selectWay
						.setOnClickListener(new OnClickListener() {

							@Override
							public void onClick(View arg0) {
								showMapRoute(hotelLongitude, hotelLatitude);
							}
						});

				hotelViewHolder.reHotalTel
						.setOnClickListener(new OnClickListener() {

							@Override
							public void onClick(View arg0) {
								callHotalService(hotelTel);
							}
						});
				hotelViewHolder.rehotelAddress
						.setOnClickListener(new OnClickListener() {

							@Override
							public void onClick(View arg0) {

								Hotel hotel = new Hotel();
								OrderFormDetailEntity orderFormDetailEntity = mList
										.get(position);
								if (orderFormDetailEntity != null) {
									String address = orderFormDetailEntity
											.getHotalAddress();
									String hotelName = orderFormDetailEntity
											.getHotalName();
									String lat = orderFormDetailEntity
											.getLatitude();
									String lng = orderFormDetailEntity
											.getLongitude();
									if (!TextUtils.isEmpty(address)) {
										hotel.setAddressLine(address);
									}
									if (!TextUtils.isEmpty(hotelName)) {
										hotel.setHotelName(hotelName);
									}
									if (!TextUtils.isEmpty(lat)) {
										hotel.setLatitude(lat);
									}
									if (!TextUtils.isEmpty(lng)) {
										hotel.setLongitude(lng);
									}
								}
								Intent detailMapIntent = new Intent();
								detailMapIntent.setClass(mContext,
										HotelMapDetailActivity.class);
								detailMapIntent
										.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
								detailMapIntent.putExtra("hotelDetail", hotel);
								mContext.startActivity(detailMapIntent);
							}
						});
			} else {
				hotelViewHolder.imgHotelOrderArrow.setVisibility(View.GONE);
				hotelViewHolder.imgHotelAddressArrow.setVisibility(View.GONE);
			}
			break;
		default:
			convertView = mInflater.inflate(R.layout.order_form_detail_weather,
					null);
			LinearLayout ll = (LinearLayout) convertView
					.findViewById(R.id.ll_add_weather);
			for (int i = 0; i < mCityNameList.size(); i++) {
				final View view = (View) mInflater.inflate(
						R.layout.cityname_item, null);
				final TextView tvName = (TextView) view
						.findViewById(R.id.tv_city_name);
				final ImageView imgWeatherArrow=(ImageView) view.findViewById(R.id.img_weather_arrow);
				tvName.setText(mCityNameList.get(i));
				if (isExample()) {
					view.setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View v) {
							String cityName = tvName.getText().toString();
							getWeather(cityName);
						}
					});
				}else{
					imgWeatherArrow.setVisibility(View.GONE);
				}
				ll.addView(view);
			}
			break;
		}

		return convertView;
	}

	@Override
	public int getViewTypeCount() {
		return 4;
	}

	@Override
	public int getItemViewType(int position) {
		String itemType = mList.get(position).getType();
		if ("flight".equals(itemType)) {
			return FLIGHT_TYPE;
		}
		if ("hotel".equals(itemType)) {
			return HOTEL_TYPE;
		}
		if ("weather".equals(itemType)) {
			return WEATHER_TYPE;
		}
		return super.getItemViewType(position);

	}

	class FlightViewHolder {
		RelativeLayout reOrderNumber;
		TextView airLineName;
		TextView orderNumber;
		TextView airtime;
		TextView takeOffTime;
		TextView arriveWay;
		ViewGroup selectWay;
		TextView arriveTime;
		TextView airstartAirPortName;
		TextView airstopAirPortName;
		TextView personName;
		ImageView imgTravelWarn;
		TextView takeOffDate;
		TextView trip_warn;
		ImageView imgFlightOrderArrow;
	}

	class HotelViewHolder {
		TextView position;
		RelativeLayout reOrderNumber;
		RelativeLayout reHotalTel;
		RelativeLayout rehotelAddress;
		TextView hotalAddress;
		TextView orderNumber;
		TextView hotalName;
		TextView hotalroomTypeName;
		TextView hotalWarning;
		TextView hotalfromTime;
		TextView hotalToTime;
		TextView arriveWay;
		ViewGroup selectWay;
		TextView personName;
		ImageView imgHotelOrderArrow;
		ImageView imgHotelAddressArrow;
	}

	class WeatherViewHolder {

	}

	/**
	 * 是否是真实的数据 ，还是火星到地球的事例
	 * 
	 * @return
	 */
	private boolean isExample() {
		if (!"1990-01-01T这是一段新的旅程(示例)".equals(mTripName)) {
			return true;
		}
		return false;
	}

	public void getWeather(String cityName) {
		WeatherBean bean = new WeatherBean();
		bean.setCity(cityName);
		int indexOfOld = WeatherActivity.updateOrAddLocalsByWeatherBean(
				mContext, bean, 0, true);
		Intent intent = new Intent(mContext, WeatherActivity.class);
		intent.putExtra("needLocation", false);
		intent.putExtra("city", cityName);
		WeatherActivity.indexOfCurrentCity = indexOfOld;
		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
				| Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
		mContext.startActivity(intent);
	}

	private void initAlarmSwitchState(int position, ImageView alarmSwitch,
			TextView trip_warn) {
		DBUtils dbUitls = new DBUtils(mContext);
		SQLiteDatabase db = dbUitls.openReadOnlyDatabase();
		TripAlarmDaoImpl tadl = new TripAlarmDaoImpl();
		String flight = mList.get(position).getAirLineName();
		String takeOffTime = mList.get(position).getTakeOffTime();
		ArrayList<AlarmEntity> list = tadl.findAlarm(db, takeOffTime, flight);
		if (list.size() != 0) {
			alarmSwitch.setBackgroundResource(R.drawable.slt_common_switch_on);
			trip_warn.setVisibility(View.VISIBLE);
		} else {
			alarmSwitch.setBackgroundResource(R.drawable.slt_common_switch_off);
			trip_warn.setVisibility(View.GONE);
		}
		db.close();
	}

	private void setAlarmSwitch(int position, ImageView imgTravelWarn,
			TextView trip_warn) {
		DBUtils dbUitls = new DBUtils(mContext);
		SQLiteDatabase db = dbUitls.openReadWriteDatabase();
		TripAlarmDaoImpl tadl = new TripAlarmDaoImpl();
		String flight = mList.get(position).getAirLineName();
		String takeOffTime = mList.get(position).getTakeOffTime();
		ArrayList<AlarmEntity> alarmList = tadl.findAlarm(db, takeOffTime,
				flight);
		if (alarmList.size() != 0) {
			imgTravelWarn
					.setBackgroundResource(R.drawable.slt_common_switch_off);
			trip_warn.setVisibility(View.GONE);
			TripAlarmControl control = new TripAlarmControl(mContext);
			control.cancelNotification(takeOffTime, flight, alarmList.get(0)
					.get_id(), mTripName, mUserName);
			tadl.deleteAlarm(db, takeOffTime, flight);
			db.close();
		} else {
			imgTravelWarn
					.setBackgroundResource(R.drawable.slt_common_switch_on);
			trip_warn.setVisibility(View.VISIBLE);
			int _id = (int) tadl.addAlarm(db, takeOffTime, flight);
			TripAlarmControl control = new TripAlarmControl(mContext);
			control.addNotification(takeOffTime, flight, _id, mTripName,
					mUserName);
		}
	}

	private void showOrderDetail(final String orderId) {
		Intent intent = new Intent(mContext, OrderDetailActivity.class);
		intent.putExtra("orderType", "hotel");
		intent.putExtra("orderId", orderId);
		intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
		mContext.startActivity(intent);
	}

	private void showMapRoute(String longitude, String latitude) {
		Intent intent = new Intent(mContext, MapRouteActivity.class);
		intent.putExtra("longitude", longitude);
		intent.putExtra("latitude", latitude);
		intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
		mContext.startActivity(intent);
	}

	/**
	 * 拨打酒店电话
	 */
	private void callHotalService(String hotelPhone) {
		hotelPhone = hotelPhone.replaceAll("-", "");
		Intent phoneIntent = new Intent("android.intent.action.CALL",
				Uri.parse("tel:" + hotelPhone));
		phoneIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		mContext.startActivity(phoneIntent);
	}
}
