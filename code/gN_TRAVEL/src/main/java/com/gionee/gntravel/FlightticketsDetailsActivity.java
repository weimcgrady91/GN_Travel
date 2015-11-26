package com.gionee.gntravel;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.net.MailTo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.gionee.gntravel.entity.DepositBean;
import com.gionee.gntravel.entity.Flight;
import com.gionee.gntravel.entity.FlightInfoRequest;
import com.gionee.gntravel.entity.WeatherInfo;
import com.gionee.gntravel.utils.BeanUtil;
import com.gionee.gntravel.utils.DateUtils;
import com.gionee.gntravel.utils.FinalString;
import com.gionee.gntravel.utils.HttpConnCallback;
import com.gionee.gntravel.utils.HttpConnUtil4Gionee;
import com.gionee.gntravel.utils.JSONUtil;
import com.gionee.gntravel.utils.NetWorkUtil;
import com.gionee.gntravel.utils.WeatherTypeUtil;

public class FlightticketsDetailsActivity extends DetailsActivity implements
		HttpConnCallback, OnClickListener {

	private ArrayList<Flight> flightList;
	private TextView fltNum;
	private TextView tv_takeOffDate;
	private TextView tv_takeOffTime;
	private TextView tv_dportCode;
	private TextView tv_dPortBuildingID;
	private TextView tv_arriveTime;
	private TextView tv_aportCode;
	private TextView tv_aPortBuildingID;
	private TextView tv_mealType;
	private TextView tv_punctualityRate;
	private TextView tv_craftType;
	private ListView listView;
	private Flight mFlight;
	private HttpConnUtil4Gionee task;
	private String flightUrl;
	private BaseAdapter myAdapter;
	private ResultHandler handler = new ResultHandler();
	private FlightInfoRequest requestInfo;
	private HashMap<String, String> params;
	private ArrayList<WeatherInfo> weatherList = new ArrayList<WeatherInfo>();
	private TextView tv_aPortCityTmp;
	private TextView tv_dPortCityTmp;
	private ViewGroup rl_failed;
	private TextView tvRefresh;
	private RelativeLayout rl_loading;
	private ImageView img_loading;
	private String mDepartDate;
	private String mDepartCity;
	private String mArriveCity;
	private View rl_container;
	private TravelApplication app;
	private boolean stopThreadFlag;
	private boolean loadFinishFlag;
	private ImageView iv_dportWeatherType;
	private ImageView iv_aportWeatherType;
	private boolean rl_failed_flag;
	private boolean mIsFinish;
	private boolean mCancelLoad;
	private String mTakeOffTime;
	private String mAirLineCode;
	
	private class ResultHandler extends Handler {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			mIsFinish = true;
			switch (msg.what) {
			case FinalString.DATA_FINISH:
				loadDataFinish();
				break;
			case FinalString.NET_ERROR:
				showNetErrorMsg();
				break;
			case FinalString.NOT_FOUND:
				showNotFoundMsg();
				break;
			default:
				break;
			}
		}
	}

	public FlightticketsDetailsActivity() {
		super(R.layout.activity_flightticket_details);
	}


	public String formatDate(String date) {
		Date d = DateUtils.StringToDate(date.replace("T", " "),
				"yyyy-MM-dd HH:mm");
		return DateUtils.dateToTime(d);
	}

	private void findViews() {
		tvRefresh=(TextView) findViewById(R.id.btn_refresh);
		fltNum = (TextView) findViewById(R.id.tv_flightNum);
		tv_takeOffDate = (TextView) findViewById(R.id.tv_takeOffDate);
		tv_takeOffTime = (TextView) findViewById(R.id.tv_takeOffTime);
		tv_dportCode = (TextView) findViewById(R.id.tv_dportCode);
		tv_dPortBuildingID = (TextView) findViewById(R.id.tv_dPortBuildingID);
		tv_arriveTime = (TextView) findViewById(R.id.tv_arriveTime);
		tv_aportCode = (TextView) findViewById(R.id.tv_aportCode);
		tv_aPortBuildingID = (TextView) findViewById(R.id.tv_aPortBuildingID);
		tv_mealType = (TextView) findViewById(R.id.tv_mealType);
		tv_punctualityRate = (TextView) findViewById(R.id.tv_punctualityRate);
		tv_craftType = (TextView) findViewById(R.id.tv_craftType);
		tv_dPortCityTmp = (TextView) findViewById(R.id.tv_dPortCityTmp);
		tv_aPortCityTmp = (TextView) findViewById(R.id.tv_aPortCityTmp);
		listView = (ListView) findViewById(R.id.listView);
		rl_failed = (ViewGroup) findViewById(R.id.rl_failed);
		rl_failed.setOnClickListener(this);
		rl_loading = (RelativeLayout) findViewById(R.id.rl_loading);
		img_loading = (ImageView) findViewById(R.id.img_loading);
		rl_container = (View) findViewById(R.id.rl_container);
		iv_dportWeatherType = (ImageView) findViewById(R.id.iv_dportWeatherType);
		iv_aportWeatherType = (ImageView) findViewById(R.id.iv_aportWeatherType);
	}
	
	private void initData() {
		app = (TravelApplication)getApplication();
		Intent intent = this.getIntent();
		mFlight = (Flight) intent.getSerializableExtra("flight");
		mDepartCity = mFlight.getDepartCity();
		mArriveCity = mFlight.getArriveCity();
		fltNum.setText(mFlight.getFlight());
		mDepartDate = DateUtils.fomatStrDate(mFlight.getTakeOffTime());
		
		mTakeOffTime = mFlight.getTakeOffTime();
		mAirLineCode = mFlight.getAirlineCode();
		
		
		tv_takeOffDate.setText(mDepartDate + " " + DateUtils.getDayofWeek(mDepartDate));
		tv_takeOffTime.setText(DateUtils.dateToTime(mFlight.getTakeOffTime()));
		tv_dportCode.setText(mFlight.getdPortName());
		
		tv_arriveTime.setText(DateUtils.dateToTime(mFlight.getArriveTime()));
		tv_aportCode.setText(mFlight.getaPortName());
		
//		tv_dPortBuildingID.setText(mFlight.getdPortBuildingID());
//		tv_aPortBuildingID.setText(mFlight.getaPortBuildingID());
		
		tv_mealType.setText(mFlight.getMealType());
		tv_punctualityRate.setText("准点" + mFlight.getPunctualityRate());
		tv_craftType.setText(mFlight.getCraftType());
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		findViews();
		initData();
		initParams();
	}

	@Override
	protected void onStart() {
		if(!mIsFinish) {
			loadingData();
		}
		super.onStart();
	}
	
	@Override
	protected void onResume() {
		super.onResume();
	}


	@Override
	protected void onStop() {
		super.onStop();
		stopProgressDialog();
		if (task != null) {
			task.cancel(true);
		}
		mCancelLoad = true;
	}


	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}
	
	public void initParams() {
		flightUrl = getString(R.string.gionee_host) + FinalString.FLIGHT_INFO_URL;
		requestInfo = BeanUtil.createFlightInfoRequest(mDepartDate,mDepartCity, mArriveCity, mFlight.getFlight(), null, null,mTakeOffTime,mAirLineCode);
		params = new HashMap<String, String>();
		params.put("flightInfo", JSONUtil.getInstance().toJSON(requestInfo));
		flightList = new ArrayList<Flight>();
		myAdapter = new MyAdapter();
		listView.setAdapter(myAdapter);
	}


	public void loadingData() {
		mCancelLoad = false;
		startProgressDialog();
		if (!NetWorkUtil.isNetworkConnected(this)) {
			Message msg = Message.obtain();
			msg.what = FinalString.NET_ERROR;
			handler.sendMessageDelayed(msg, 1000);
			return;
		}
		
		task = new HttpConnUtil4Gionee(this);
		task.execute(flightUrl, params, HttpConnUtil4Gionee.HttpMethod.POST);
	}

	@Override
	public void execute(String result) {
		if (TextUtils.isEmpty(result)) {
			Message msg = Message.obtain();
			msg.what = FinalString.NET_ERROR; ;
			handler.sendMessage(msg);
			return;
		}
		handler.post(new DoResult(result));
 	}

	private class DoResult implements Runnable {
		private String result;

		public DoResult(String result) {
			this.result = result;
		}

		@Override
		public void run() {
			try {
				
				JSONObject responseJson = new JSONObject(result);
				if(!FinalString.ERRORCODE.equals(responseJson.getString("errorCode"))) {
					if(!mCancelLoad) {
						Message msg = Message.obtain();
						msg.what = FinalString.NOT_FOUND;
						msg.obj = responseJson.getString("errorMsg");
						handler.sendMessage(msg);
					}
					return;
				}
				
				JSONObject content = new JSONObject(result);
				JSONObject jsonAll = new JSONObject(
						content.getString("content"));
				String takeOffTime = jsonAll.getString("takeOffTime");
				String arriveTime = jsonAll.getString("arriveTime");
				String flight = jsonAll.getString("flight");
				String craftType = jsonAll.getString("craftType");
				String dPortBuildingID = jsonAll.getString("dPortBuildingID");
				String aPortBuildingID = jsonAll.getString("aPortBuildingID");
				String dPortCode = jsonAll.getString("dPortCode");
				String aPortCode = jsonAll.getString("aPortCode");
				String departCityCode = jsonAll.getString("departCityCode");
				String arriveCityCode = jsonAll.getString("arriveCityCode");
				String airlineCode = jsonAll.getString("airlineCode");
				String adultTax = jsonAll.getString("adultTax");
				String adultOilFee = jsonAll.getString("adultOilFee");
//				String dPortName = jsonAll.getString("dPortName");
//				String aPortName = jsonAll.getString("aPortName");
				String airlineName = jsonAll.getString("airlineName");
				JSONArray weatherInfos = new JSONArray(
						jsonAll.getString("weatherInfo"));
				for (int j = 0; j < weatherInfos.length(); j++) {
					JSONObject weatherInfo = (JSONObject) weatherInfos.get(j);
					String wd = weatherInfo.getString("wd");
					String tp = weatherInfo.getString("tp");
					String w = weatherInfo.getString("w");
					weatherList.add(new WeatherInfo(wd, tp, w));
				}

				JSONArray classTypes = new JSONArray(
						jsonAll.getString("classTypes"));
				for (int j = 0; j < classTypes.length(); j++) {
					Flight fltObj = new Flight();
					JSONObject jsonClassTypes = (JSONObject) classTypes.get(j);
					String classType = jsonClassTypes.getString("classType");
					String rate = jsonClassTypes.getString("rate");
					String price = jsonClassTypes.getString("price");
					String quantity = jsonClassTypes.getString("quantity");
					String rernote = jsonClassTypes.getString("rernote");
					String endnote = jsonClassTypes.getString("endnote");
					String refnote = jsonClassTypes.getString("refnote");
					fltObj.setClassType(classType);
					fltObj.setRate(rate);
					fltObj.setPrice(price);
					fltObj.setQuantity(quantity);
					fltObj.setDepartCity(mDepartCity);
					fltObj.setArriveCity(mArriveCity);
					fltObj.setTakeOffTime(takeOffTime);
					fltObj.setArriveTime(arriveTime);
					fltObj.setFlight(flight);
					fltObj.setCraftType(craftType);
					fltObj.setdPortBuildingID(dPortBuildingID);
					fltObj.setaPortBuildingID(aPortBuildingID);
					fltObj.setdPortCode(dPortCode);
					fltObj.setaPortCode(aPortCode);
					fltObj.setAirlineCode(airlineCode);
					fltObj.setAdultTax(adultTax);
					fltObj.setRernote(rernote);
					fltObj.setEndnote(endnote);
					fltObj.setRefnote(refnote);
					fltObj.setAdultOilFee(adultOilFee);
					fltObj.setdPortName(mFlight.getdPortName());
					fltObj.setaPortName(mFlight.getaPortName());
					fltObj.setAirlineName(airlineName);
					fltObj.setDepartCityCode(departCityCode);
					fltObj.setArriveCityCode(arriveCityCode);
					flightList.add(fltObj);
				}
				if(!mCancelLoad) {
					Message msg = Message.obtain();
					msg.what = FinalString.DATA_FINISH;
					handler.sendMessage(msg);
				}
			} catch (JSONException e) {
				e.printStackTrace();
				if(!mCancelLoad) {
					Message msg = Message.obtain();
					msg.what = FinalString.NOT_FOUND;
					handler.sendMessage(msg);
				}
			}
		}
	}

	@Override
	protected String curstomTitle() {
		return getString(R.string.flightticketsDetails);
	}

	@Override
	protected void payment() {
	}

	private class MyAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			return flightList.size();
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
		public View getView(final int position, View convertView,
				ViewGroup parent) {
			ViewHolder viewHolder = null;
			if (convertView == null) {
				convertView = getLayoutInflater().inflate(
						R.layout.classtype_item, null);
				viewHolder = new ViewHolder();
				viewHolder.classType = (TextView) convertView
						.findViewById(R.id.classType);
				viewHolder.rate = (TextView) convertView
						.findViewById(R.id.rate);
				viewHolder.price = (TextView) convertView
						.findViewById(R.id.price);
				viewHolder.schedule = (Button) convertView
						.findViewById(R.id.schedule);
				viewHolder.dollar = (TextView) convertView.findViewById(R.id.tv_dollar);
				convertView.setTag(viewHolder);
			} else {
				viewHolder = (ViewHolder) convertView.getTag();
			}
			viewHolder.classType.setText(flightList.get(position)
					.getClassType());
			String rate = flightList.get(position).getRate();
			if (rate.indexOf("折") != -1) {
				if (Float.parseFloat(rate.substring(0, rate.length() - 1)) <= 5.0f) {
					viewHolder.price.setTextColor(getResources().getColor(R.color.text_color_v8));
					viewHolder.dollar.setTextColor(getResources().getColor(R.color.text_color_v8));
				} else {
					viewHolder.price.setTextColor(getResources().getColor(R.color.text_color_v7));
					viewHolder.dollar.setTextColor(getResources().getColor(R.color.text_color_v7));
				}
			} else {
				viewHolder.price.setTextColor(getResources().getColor(R.color.text_color_v7));
				viewHolder.dollar.setTextColor(getResources().getColor(R.color.text_color_v7));
			}
			viewHolder.rate.setText(flightList.get(position).getRate());
			viewHolder.price.setText(flightList.get(position).getPrice());
			viewHolder.schedule.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					Bundle bundle = new Bundle();
					bundle.putSerializable("Flight", flightList.get(position));
					
					if(app.isLoginState()) {
						Intent intent = new Intent();
						intent.setClass(FlightticketsDetailsActivity.this,
								FlightOrderActivity.class);
						intent.putExtras(bundle);
						startActivity(intent);
					} else {
						bundle.putString("target", "com.gionee.gntravel.FlightOrderActivity");						
						Intent intent = new Intent();
						intent.setClass(FlightticketsDetailsActivity.this, LoginActivity.class);
						intent.putExtras(bundle);
						startActivity(intent);
						finish();
					}
				}
			});
			return convertView;

		}
	}

	static class ViewHolder {
		public TextView classType;
		public TextView rate;
		public TextView price;
		public Button schedule;
		public TextView dollar;
	}

	public void initWeather() {
		WeatherInfo dPortWeather = weatherList.get(0);
		WeatherInfo aPortWeather = weatherList.get(1);
		tv_dPortCityTmp.setText(dPortWeather.getWeatherType() + " "
				+ dPortWeather.getTemperature() + "℃");
		WeatherTypeUtil weatherTypeUtil = new WeatherTypeUtil(this);
		int dPortWeatherImageId = weatherTypeUtil.getWeatherTypeImage(dPortWeather.getWeatherType());
		if( dPortWeatherImageId != -1) {
			iv_dportWeatherType.setBackgroundResource(dPortWeatherImageId);
		}
		
		tv_aPortCityTmp.setText(aPortWeather.getWeatherType() + " "
				+ aPortWeather.getTemperature() + "℃");
		int aPortWeatherImageId = weatherTypeUtil.getWeatherTypeImage(aPortWeather.getWeatherType());
		if( aPortWeatherImageId != -1) {
			iv_aportWeatherType.setBackgroundResource(aPortWeatherImageId);
		}
	}

	private void startProgressDialog() {
		rl_container.setVisibility(View.GONE);
		rl_failed.setVisibility(View.GONE);
		rl_loading.setVisibility(View.VISIBLE);
		flightList.clear();
		myAdapter.notifyDataSetChanged();
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

	public void showNetErrorMsg() {
		stopProgressDialog();
		rl_container.setVisibility(View.INVISIBLE);
		rl_failed.setBackground(getResources().getDrawable(R.drawable.net_error_bg));
		rl_failed.setVisibility(View.VISIBLE);
		rl_failed_flag = true;
	}

	private void showNotFoundMsg() {
		stopProgressDialog();
		rl_container.setVisibility(View.INVISIBLE);
		rl_failed.setBackground(getResources().getDrawable(R.drawable.notfound_bg));
		tvRefresh.setText(getString(R.string.not_find));
		rl_failed.setVisibility(View.VISIBLE);
	}
	
	public void loadDataFinish() {
		stopProgressDialog();
		initWeather();
		if (flightList.size() == 0) {
			showNetErrorMsg();
			return;
		}
		String dPortBuildingName = flightList.get(0).getdPortBuildingID();
		String aPortBuildingName = flightList.get(0).getaPortBuildingID();
		if("null".equals(dPortBuildingName)) {
			tv_dPortBuildingID.setText(mFlight.getdPortName());
		} else {
			tv_dPortBuildingID.setText(dPortBuildingName);
		}
		
		if("null".equals(aPortBuildingName)) {
			tv_aPortBuildingID.setText(mFlight.getaPortName());
		} else {
			tv_aPortBuildingID.setText(aPortBuildingName);
		}
		
//		tv_aPortBuildingID.setText(flightList.get(0).getaPortBuildingID());
		myAdapter.notifyDataSetChanged();
		rl_container.setVisibility(View.VISIBLE);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.rl_failed:
			mIsFinish = false;
			loadingData();
			break;
		default:
			super.onClick(v);
			break;
		}
	}

	@Override
	protected void onPause() {
		super.onPause();
		if(task!=null) {
			task.cancel(true);
		}
	}
	
	
}
