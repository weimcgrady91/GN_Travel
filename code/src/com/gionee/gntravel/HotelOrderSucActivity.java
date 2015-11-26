package com.gionee.gntravel;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Calendar;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.gionee.gntravel.entity.HotelOrderBean;
import com.gionee.gntravel.entity.HotelReservation;
import com.gionee.gntravel.utils.FinalString;
import com.youju.statistics.YouJuAgent;

public class HotelOrderSucActivity extends BaseActivity implements OnClickListener {
	private TravelApplication app;
	private Button btn_finish;
	private TextView activity_hotelordersuc_tv_back;
	private TextView ordersuc_tv_travel;
	private HotelReservation hotelReservation;
	private String indate;
	private String leavedate;
	private String roomName;
	private HotelOrderBean orderBean;
	private String hotelName;
	private TextView ordersuc_tv_orderid;
	private TextView ordersuc_tv_cusname;
	private TextView ordersuc_tv_contactName;
	private TextView ordersuc_tv_phone;
	private TextView ordersuc_tv_hotelname;
	private TextView ordersuc_tv_roomName;
	private TextView ordersuc_tv_price;
	private TextView ordersuc_tv_indate;
	private TextView ordersuc_tv_leavedate;
	private String orderId;
	private String cusName;
	private String conName;
	private String phone;
	private String price;
//	private Button ordersuc_bt_flight;
	private Button ordersuc_bt_flight_ori;
	private Button ordersuc_bt_flight_des;
	private String mIndate;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_hotel_ordersuc);
		YouJuAgent.onEvent(this, getString(R.string.youju_hotel_pay_order_succ));
		app = (TravelApplication) getApplication();
		findviews();
		initData();
		setData();
	}

	private void initData() {
		Intent i = getIntent();
		Bundle b = i.getExtras();
		hotelReservation = (HotelReservation) b.getSerializable("hotelReservation");
		orderBean = (HotelOrderBean) b.getSerializable("orderBean");
		roomName = b.getString("roomName");
		hotelName = b.getString("hotelName");
		orderId = hotelReservation.getResGlobalInfo().getResID_Value();
		cusName = orderBean.getCustomName();
		conName = orderBean.getContactName();
		phone = orderBean.getPhoneNumber();
		price = hotelReservation.getResGlobalInfo().getAmountAfterTax();
		indate = orderBean.getStart();
		leavedate = orderBean.getEnd();
	}

	private void findviews() {
		btn_finish = (Button) findViewById(R.id.btn_finish);
		findViewById(R.id.tv_title).setOnClickListener(this);
		ordersuc_bt_flight_ori = (Button) findViewById(R.id.activity_hotel_ordersuc_bt_flight_ori);
		ordersuc_bt_flight_des = (Button) findViewById(R.id.activity_hotel_ordersuc_bt_flight_des);
		btn_finish.setOnClickListener(this);
		ordersuc_bt_flight_ori.setOnClickListener(this);
		ordersuc_bt_flight_des.setOnClickListener(this);
		TextView tv_title = (TextView) findViewById(R.id.tv_title);
		tv_title.setText(getString(R.string.hotel_order_suc_title));
		ordersuc_tv_travel = (TextView) findViewById(R.id.activity_hotel_ordersuc_tv_travel);
		ordersuc_tv_orderid = (TextView) findViewById(R.id.activity_hotel_ordersuc_tv_orderid);
		ordersuc_tv_cusname = (TextView) findViewById(R.id.activity_hotel_ordersuc_tv_cusname);
		ordersuc_tv_contactName = (TextView) findViewById(R.id.activity_hotel_ordersuc_tv_contactName);
		ordersuc_tv_phone = (TextView) findViewById(R.id.activity_hotel_ordersuc_tv_phone);
		ordersuc_tv_hotelname = (TextView) findViewById(R.id.activity_hotel_ordersuc_tv_hotelname);
		ordersuc_tv_roomName = (TextView) findViewById(R.id.activity_hotel_ordersuc_tv_roomName);
		ordersuc_tv_price = (TextView) findViewById(R.id.activity_hotel_ordersuc_tv_prive);
		ordersuc_tv_indate = (TextView) findViewById(R.id.activity_hotel_ordersuc_tv_indate);
		ordersuc_tv_leavedate = (TextView) findViewById(R.id.activity_hotel_ordersuc_tv_leavedate);
	}

	private void setData() {
		try {
			indate = indate.split("T")[0];
			mIndate = indate;
			Calendar c = Calendar.getInstance();
			String thisyear = c.get(Calendar.YEAR) + "";
			String inyear = indate.split("-")[0];
			ordersuc_tv_indate.setText(indate+"入住");
			if (thisyear.equals(inyear)) {
				indate = indate.substring(indate.indexOf("-") + 1);
			}
			TravelApplication app = (TravelApplication) this.getApplication();
			String arriveCity = app.getArrivecity();
			String departCity =  app.getDepartcity();
			String tripName = app.getTripName();
			ordersuc_tv_travel.setText('"'+tripName.replaceAll("T", " ")+'"');
			ordersuc_tv_orderid.setText(orderId);
			ordersuc_tv_cusname.setText(URLDecoder.decode(cusName, "utf-8"));
			ordersuc_tv_contactName.setText(URLDecoder.decode(conName, "utf-8"));
			ordersuc_tv_phone.setText(phone);
			ordersuc_tv_hotelname.setText(hotelName);
			ordersuc_tv_roomName.setText(roomName);
			ordersuc_tv_price.setText((int)Double.parseDouble(price)+"");
			leavedate = leavedate.split("T")[0];
			ordersuc_tv_leavedate.setText(leavedate+"离店");
			ordersuc_bt_flight_des.setText("订去"+arriveCity+"的机票");
			ordersuc_bt_flight_ori.setText("订回"+departCity+"的机票");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_finish:
			gotoMainTab();
			break;
		case R.id.tv_title:
			gotoMainTab();
			break;
		case R.id.activity_hotel_ordersuc_bt_flight_des:
			gotoDepartCity();
			break;
		case R.id.activity_hotel_ordersuc_bt_flight_ori:
			gotoArriveCity();
			break;
		default:
			break;
		}
	}
	
	private void gotoDepartCity() {
		Intent intent = new Intent(this, ListOfMessageActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		String departCity = app.getDepartcity();
		String arriveCity = app.getArrivecity();
		intent.putExtra("TakeOffTime", mIndate);
		intent.putExtra("departCity", departCity);
		intent.putExtra("arriveCity", arriveCity);
		startActivity(intent);
		finish();
	}
	
	private void gotoArriveCity() {
		Intent intent = new Intent(this, ListOfMessageActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		String departCity = app.getDepartcity();
		String arriveCity = app.getArrivecity();
		intent.putExtra("TakeOffTime", leavedate);
		intent.putExtra("departCity", arriveCity);
		intent.putExtra("arriveCity", departCity);
		startActivity(intent);
		finish();
	}

	private void gotoMainTab() {
		Intent intent = new Intent();
		intent.setClass(this, MainTabActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		intent.putExtra("tripload", true);
		
		Intent tripload = new Intent(FinalString.REFRESH_TRIP_ACTION);
		sendBroadcast(tripload);
		startActivity(intent);
		finish();
	}
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
	if(keyCode==KeyEvent.KEYCODE_BACK&&event.getRepeatCount()==0){
		gotoMainTab();
		return true;
	}
		return false;
	}
}
