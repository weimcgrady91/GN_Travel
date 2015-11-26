package com.gionee.gntravel;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;

import org.apache.http.ParseException;
import org.json.JSONArray;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.CalendarContract.Colors;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.gionee.gntravel.adapter.TripFormAdapter;
import com.gionee.gntravel.entity.OrderFormDetailEntity;
import com.gionee.gntravel.task.GeneralGetInfoTask;
import com.gionee.gntravel.utils.FinalString;
import com.gionee.gntravel.utils.HttpConnUtil4Gionee;
import com.gionee.gntravel.utils.NetWorkUtil;
import com.gionee.gntravel.utils.TaskCallBack;
import com.gionee.gntravel.utils.Utils;

public class TripFormActivity extends Activity implements OnClickListener,GeneralGetInfoTask.ParseTool
		,TaskCallBack{
	private boolean isFinish = false;
	private static final String ISEXPLAME = "-1";
	private View rl_failed;
	private RelativeLayout rl_loading;
	private ImageView img_loading;
	private ResultHandler handler = new ResultHandler();
	private String mUserName;
	private String mTripName;
	private ArrayList<OrderFormDetailEntity> list;
	private ListView listView;
	private GeneralGetInfoTask task;
	private int mPosition;
	private String mTripId;
	private TextView tvGotrip;
	private class ResultHandler extends Handler {

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			isFinish = true;
			switch (msg.what) {
			case FinalString.DATA_FINISH:
				loadDataFinish();
				break;
			case FinalString.NET_ERROR:
				showNetErrorMsg();
				break;
			default:
				break;
			}
		}
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_tripform_detail);
		setupView();
	}

	private void setupView() {
		Intent intent = getIntent();
		mTripName = intent.getStringExtra("tripName");
		mUserName = intent.getStringExtra("userName");
		mPosition = intent.getExtras().getInt("position");
		mTripId = intent.getStringExtra("tripId");
		findViewById(R.id.tv_title).setOnClickListener(this);
		listView = (ListView) findViewById(R.id.lv_order_form);
		rl_failed = (View) findViewById(R.id.rl_failed);
		rl_failed.setOnClickListener(this);
		rl_loading = (RelativeLayout) findViewById(R.id.rl_loading);
		img_loading = (ImageView) findViewById(R.id.img_loading);
		img_loading.setOnClickListener(this);
		tvGotrip=(TextView) findViewById(R.id.tv_gotrip);
		tvGotrip.setOnClickListener(this);
		TextView formTitle = (TextView) findViewById(R.id.tv_formtitle);
		formTitle.setText(mTripName.substring(mTripName.indexOf("T")+1, mTripName.length()));
	}
	
	@Override
	protected void onStart() {
		super.onStart();
		if(!isFinish) {
			loadData();
		}
	}

	@Override
	protected void onStop() {
		super.onStop();
		if(task!= null && !task.isCancelled()) {
			task.cancel(true);
		}
		if(!isFinish) {
			stopProgressDialog();
		}
	}
	
	private void loadTripFormExample() {
		InputStream is = null;
		try {
			tvGotrip.setTextColor(getResources().getColor(R.color.text_color_v2));
					tvGotrip.setBackgroundResource(R.drawable.bg_transparent);
			is = getResources().getAssets().open("tripformexample.txt");
			String content = inputStream2String(is);
			list = parseResult(content);
			Message msg = Message.obtain();
			if(list == null) {
				msg.what = FinalString.NET_ERROR;
			} else if(list.isEmpty()) {
				msg.what = FinalString.NOT_FOUND;
			} else {
				msg.what = FinalString.DATA_FINISH;
			}
			handler.sendMessage(msg);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	public String inputStream2String(InputStream is) throws IOException{ 
        ByteArrayOutputStream baos = new ByteArrayOutputStream(); 
        int i=-1; 
        while((i=is.read())!=-1){ 
        	baos.write(i); 
        } 
       return baos.toString(); 
	}
	
	private void loadData() {
		isFinish = false;
		startProgressDialog();
		
		if(ISEXPLAME.equals(mTripId)) {
			loadTripFormExample();
			return;
		} 
		
		String url = getString(R.string.gionee_host)+ FinalString.TRIP_FORM;
		if (!NetWorkUtil.isNetworkConnected(this)) {
			Message msg = Message.obtain();
			msg.what = FinalString.NET_ERROR;
			handler.sendMessageDelayed(msg, 1000);
			return;
		}
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("tripId", mTripId);
		task = new GeneralGetInfoTask(this,this,this);
		task.execute(url, params,HttpConnUtil4Gionee.HttpMethod.POST);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.tv_title:
			this.finish();
			break;
		case R.id.rl_failed:
			loadData();
			break;
		case R.id.tv_gotrip:
			if(!ISEXPLAME.equals(mTripId)) {
				goTrip();
			}
			break;
		default:
			break;
		}
	}
	
	private void startProgressDialog() {
		rl_failed.setVisibility(View.GONE);
		rl_loading.setVisibility(View.VISIBLE);
		AnimationDrawable animationDrawable = (AnimationDrawable) img_loading
				.getBackground();
		animationDrawable.start();
	}

	private void stopProgressDialog() {
		AnimationDrawable animationDrawable = (AnimationDrawable) img_loading
				.getBackground();
		animationDrawable.stop();
		rl_loading.setVisibility(View.GONE);
	}

	@Override
	public void execute(Object result) {
		Message msg = Message.obtain();
		list = (ArrayList<OrderFormDetailEntity>)result;
		if(list == null) {
			msg.what = FinalString.NET_ERROR;
		} else if(list.isEmpty()) {
			msg.what = FinalString.NOT_FOUND;
		} else {
			msg.what = FinalString.DATA_FINISH;
		}
		handler.sendMessage(msg);
	}

	public void loadDataFinish() {
		stopProgressDialog();
		TripFormAdapter adapter = new TripFormAdapter(this, list, list.get(list.size()-1).getListCitys(), mTripName, mUserName);
		listView.setAdapter(adapter);
		listView.setSelection(mPosition);
	}
	
	public void showNetErrorMsg() {
		rl_failed.setBackground(getResources().getDrawable(R.drawable.net_error_bg));
		rl_failed.setVisibility(View.VISIBLE);
		stopProgressDialog();
	}
	
	public ArrayList<OrderFormDetailEntity> parseResult(String result) {
		try {
			JSONObject resultJson = new JSONObject(result);
			if(!FinalString.ERRORCODE.equals(resultJson.getString("errorCode"))) {
				return null;
			}
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		
		ArrayList<OrderFormDetailEntity> list = new ArrayList<OrderFormDetailEntity>();
		try {
			JSONObject responseJson = new JSONObject(result);
			String content = responseJson.getString("content");
			JSONObject contentJson = new JSONObject(content);
			JSONArray arr = new JSONArray(contentJson.getString("tripOrderDetailaBeans"));
			JSONArray arrcitys = new JSONArray(contentJson.getString("citys"));
			ArrayList<String> cityNameList = new ArrayList<String>();
			for (int j = 0; j < arrcitys.length(); j++) {
				String name = arrcitys.getString(j);
				cityNameList.add(name);
			}
			for (int i = 0; i < arr.length(); i++) {
				OrderFormDetailEntity detailEntity = new OrderFormDetailEntity();
				JSONObject json = (JSONObject) arr.get(i);
				String type = json.getString("type");
				detailEntity.setType(type);
				if ("flight".equals(type)) {
					String airLineName = json.getString("airLineName");
					String takeOffTime = json.getString("startDate");
					detailEntity.setTakeOffTime(takeOffTime);
					String arriveTime = json.getString("endDate");
					detailEntity.setArriveTime(arriveTime);
					String departPortName = json.getString("departPortName");
					String arrivalPortName = json.getString("arrivalPortName");
					String flight = json.getString("flight");
					String orderId = json.getString("orderId");
					String checkInPerson = json.getString("checkInPerson");
					String longitude = json.getString("longitude");
					String latitude = json.getString("latitude");
					detailEntity.setAirLineName(airLineName + flight);
					detailEntity.setOrderCode(orderId);
					detailEntity.setAirstartAirPortName(departPortName);
					detailEntity.setAirstopAirPortName(arrivalPortName);
					detailEntity.setDjName(checkInPerson);
					detailEntity.setLongitude(longitude);
					detailEntity.setLatitude(latitude);
				} else {
					String orderId = json.getString("orderId");
					String hotelName = json.getString("hotelName");
					String roomTypeName = json.getString("roomTypeName");
					String phoneNumber = json.getString("phoneNumber");
					String hotelAddress = json.getString("address");
					String location = json.getString("position");
					String lateArrivalTime = json.getString("lateArrivalTime");
					String hotelStart = json.getString("hotelStart");
					String hotelEnd = json.getString("hotelEnd");
					String checkInPerson = json.getString("checkInPerson");
					String hotelLongitude = json.getString("longitude");
					String hotelLatitude = json.getString("latitude");
					if (hotelEnd != null && hotelStart != null && lateArrivalTime != null) {
						int hotalindex = lateArrivalTime.indexOf("T");
						String hotaldateStrFrom = lateArrivalTime.substring(0, hotalindex);
						String hotaldateFrom = Utils.getMonthAndDay(hotaldateStrFrom,this);
						String hotaldateTo = hotelEnd.substring(0,hotalindex);
						hotaldateTo = Utils.getMonthAndDay(hotaldateTo,this);
						String hour = lateArrivalTime.substring(hotalindex + 1, hotalindex + 3);
						String lateArrivalTimeArray = hotaldateFrom + hour;// 警告最晚到店时间
						detailEntity.setHotalfromTime(hotaldateFrom);
						detailEntity.setHotalToTime(hotaldateTo);
						detailEntity.setHotalLasttime(lateArrivalTimeArray);
					}
					detailEntity.setOrderCode(orderId);
					detailEntity.setDjName(checkInPerson);
					detailEntity.setHotalName(hotelName);
					detailEntity.setHotalroomTypeName(roomTypeName);
					detailEntity.setHotaltelephone(phoneNumber);
					detailEntity.setHotalAddress(hotelAddress);// 酒店地址
					detailEntity.setLocation(location);
					detailEntity.setLongitude(hotelLongitude);
					detailEntity.setLatitude(hotelLatitude);
				}

				list.add(detailEntity);
			}
			OrderFormDetailEntity detailEntityWeather = new OrderFormDetailEntity();
			detailEntityWeather.setType("weather");
			detailEntityWeather.setListCitys(cityNameList);
			list.add(detailEntityWeather);
			return list;
		} catch (Exception e) {
			throw new ParseException("parse TripForm error");
		}
	}
	
	private void goTrip() {
		TravelApplication app = (TravelApplication) this.getApplication();
		app.setTripName(mTripName);
		Intent intent = new Intent(this,NewRouteActivity.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
//		intent.putExtra("oldTrip", true);
		app.setTripState(false);
		startActivity(intent);
	}
}
