package com.gionee.gntravel;

import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.gionee.gntravel.customview.CustomProgressDialog;
import com.gionee.gntravel.entity.Ticket;
import com.gionee.gntravel.entity.Train;
import com.gionee.gntravel.entity.Weather;
import com.gionee.gntravel.entity.WeatherInfo;
import com.gionee.gntravel.utils.FinalString;
import com.gionee.gntravel.utils.HttpConnCallback;
import com.gionee.gntravel.utils.HttpConnUtil4Gionee;
import com.gionee.gntravel.utils.JSONUtil;
import com.gionee.gntravel.utils.NetWorkUtil;
import com.gionee.gntravel.utils.Utils;
import com.gionee.gntravel.utils.WeatherTypeUtil;

public class TrainticketsDetailsActivity extends DetailsActivity implements
		OnClickListener, OnItemClickListener ,HttpConnCallback{
	private CustomProgressDialog progressDialog;
	private HttpConnUtil4Gionee task;
	ArrayList<WeatherInfo> weatherList = new ArrayList<WeatherInfo>();
	Train train;
	ImageButton imgBack; // 返回
	Button btnKefu; // 客服电话
	TextView tvTrainName; // 车次
	TextView tvTrainTotalTime; // 耗时
	TextView tvTrainStart; // 出发站
	TextView tvTrainEnd; // 到达站
	TextView tvTrainStartTime; // 出发时间
	TextView tvTrainEndTime; // 到达时间
	TextView tvTrainTimeTable; // 列车时刻表按钮
	TextView tvWeatherStartC; // 出发城市天气温度
	TextView tvWeatherEndC; // 到达城市天气温度
	ImageView imgWeatherStartIcon; // 出发城市天气图标
	ImageView imgWeatherEndIcon; // 到达城市天气图标
	ImageView imgLeft; // 左键头
	ImageView imgRight; // 右键头
	TextView tvTime; // 左右键头中间的时间
	String time="";
	LinearLayout llPerdy;
	LinearLayout llToday;
	LinearLayout llNextDay;
	TextView tvPredy;
	TextView tvToday;
	TextView tvNextDay;
	List<Ticket> ticketDetailList= new ArrayList<Ticket>();
	List<Ticket> ticketBookAbleList= null;
	ListView lvDetail;
	String trainName;
	String totalTimeStr;
	String arriveCity;
	String departCity;
	private BaseAdapter trainDetailAdapter;
	public TrainticketsDetailsActivity() {
		super(R.layout.activity_trainticket_details);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setupView();//实例化控件
		initDate();//设置数据
		loadingData();
	}
	/*
	 * 从上个界面拿到数据
	 */
	
	private void initDate(){

		Intent intent = this.getIntent();
		train = (Train) intent.getSerializableExtra("train");
		time=intent.getStringExtra("time");
		 arriveCity=intent.getStringExtra("arriveCity");
		 departCity=intent.getStringExtra("departCity");
		String start=train.getStart();
		String stop=train.getStop();
		String startTime=train.getStartTime();
		String arriveTime=train.getArrivalTime();
		 trainName=train.getTrainShortName();
		String days=train.getTakeDays();
		if("".equals(days)||days==null){
			days="0";
		}
		String totalTime[]=Utils.getTimes(startTime, arriveTime, new Integer(days));
		ticketDetailList=train.getTicketList();
		tvTrainName.setText(trainName);
		totalTimeStr="耗时"+totalTime[0]+"小时"+totalTime[1]+"分";
		tvTrainTotalTime.setText(totalTimeStr);
		tvTrainStart.setText(start);
		tvTrainEnd.setText(stop);
		tvTrainStartTime.setText(startTime);
		tvTrainEndTime.setText(arriveTime);
		trainDetailAdapter=new TrainDetailAdapter();
		lvDetail.setAdapter(trainDetailAdapter);
		tvToday.setText(time);
	}

	public void loadingData() {
		//startProgressDialog();
		if (!NetWorkUtil.isNetworkConnected(this)) {
			// 无网络连接 提示
			Toast.makeText(this, R.string.no_network, Toast.LENGTH_LONG).show();
		//	stopProgressDialog();
			return;
		}
	String host=getString(R.string.gionee_host)+FinalString.TRAIN_STATION_WEATHER_URL;
	
		HashMap<String , String> params=new HashMap<String, String>();
		Weather weatherRequestInfo=new Weather();
		weatherRequestInfo.setStart(departCity);
		weatherRequestInfo.setStop(arriveCity);
		params.put("json",
				URLEncoder.encode(JSONUtil.getInstance().toJSON(weatherRequestInfo)));
		task = new HttpConnUtil4Gionee(this);
		task.execute(host, params, HttpConnUtil4Gionee.HttpMethod.GET);
	}
	private void setupView() {
		tvTrainName = (TextView) findViewById(R.id.tv_train_detail_name);
		tvTrainTotalTime = (TextView) findViewById(R.id.tv_train_total_time);
		tvTrainEndTime = (TextView) findViewById(R.id.tv_tarin_detail_end_time);
		tvTrainStart = (TextView) findViewById(R.id.tv_tarin_detail_start);
		tvTrainEnd = (TextView) findViewById(R.id.tv_tarin_detail_stop);
		tvTrainStartTime = (TextView) findViewById(R.id.tv_tarin_detail_start_time);
		tvTrainName = (TextView) findViewById(R.id.tv_train_detail_name);
		tvTrainTimeTable = (TextView) findViewById(R.id.tv_train_time_table);
		tvTrainTimeTable.setOnClickListener(this);
		imgWeatherStartIcon = (ImageView) findViewById(R.id.img_train_detail_start_weather_icon);
		imgWeatherEndIcon = (ImageView) findViewById(R.id.img_train_detail_end_weather_icon);
		tvWeatherEndC = (TextView) findViewById(R.id.tv_train_detail_end_weather_c);
		tvWeatherStartC = (TextView) findViewById(R.id.tv_train_detail_start_weather_c);
		llToday=(LinearLayout) findViewById(R.id.ll_today_train_detail);
		tvToday=(TextView) findViewById(R.id.tv_today_train_detail);
		lvDetail=(ListView) findViewById(R.id.lv_detail);
	}

	@Override
	protected String curstomTitle() {
		return getString(R.string.trainticketsDetails);
	}

	@Override
	protected void payment() {
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.tv_train_time_table://列车时刻表
			Intent intent=new Intent(this,TrainTimeTableActivity.class);
			intent.putExtra("trainName", trainName);
			intent.putExtra("totalTimeStr", totalTimeStr);
			startActivity(intent);
			break;
		default:
			break;
		}
		super.onClick(v);
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
	}

	private class TrainDetailAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			
			return ticketDetailList.size();
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
						R.layout.train_detail_item, null);
				viewHolder = new ViewHolder();
				viewHolder.seatTypeName = (TextView) convertView
						.findViewById(R.id.tv_train_detail_seat_type);
				viewHolder.price = (TextView) convertView
						.findViewById(R.id.tv_train_detail_price);
				convertView.setTag(viewHolder);
			} else {
				viewHolder = (ViewHolder) convertView.getTag();
			}
			viewHolder.seatTypeName.setText(ticketDetailList.get(position)
					.getSeatTypeName());
			
			viewHolder.price.setText(ticketDetailList.get(position).getPrice());
			
			return convertView;
		}
	}

	static class ViewHolder {
		public TextView seatTypeName;
		public TextView count;
		public TextView price;
		public Button bookable;
	}
	public String dateFormat(Date date) {
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		String dateStr = dateFormat.format(date);
		return dateStr;
	}

	@Override
	public void execute(String result) {
		if (TextUtils.isEmpty(result)) {
			Toast.makeText(this, R.string.network_timeout, 1).show();
			return;
		}
		/**
		 * {"content":[{"w":"东北风","t":1.405135821604E12,"tp":"30","wt":"晴"},{"w":"西北风","t":1.405135821712E12,"tp":"32","wt":"大雨"}],"errorCode":0,"errorMsg":null}
		 */
		try {
			JSONObject all = new JSONObject(result);
			if (!FinalString.ERRORCODE.equals(all
					.getString("errorCode"))) {
				Toast.makeText(this, R.string.service_error, 1).show();
				return;
			}
			String weatherArray=all.getString("content");
			JSONArray jsonArray=new JSONArray(weatherArray);
			 
			for(int j=0; j<jsonArray.length();j++) {
				JSONObject weatherInfo = (JSONObject) jsonArray.get(j);
				String wd = weatherInfo.getString("wd");
				String tp = weatherInfo.getString("tp");
				String w = weatherInfo.getString("w");
				weatherList.add(new WeatherInfo(wd,tp,w));
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		initWeather() ;
	}	
	
	private void initWeather() {
		WeatherInfo dPortWeather = weatherList.get(0);
		WeatherInfo aPortWeather = weatherList.get(1);
		tvWeatherStartC.setText(dPortWeather.getWeatherType() + " "
				+ dPortWeather.getTemperature() + "℃");
		WeatherTypeUtil weatherTypeUtil = new WeatherTypeUtil(this);
		int dPortWeatherImageId = weatherTypeUtil.getWeatherTypeImage(dPortWeather.getWeatherType());
		if( dPortWeatherImageId != -1) {
			imgWeatherStartIcon.setBackgroundResource(dPortWeatherImageId);
		}
		
		tvWeatherEndC.setText(aPortWeather.getWeatherType() + " "
				+ aPortWeather.getTemperature() + "℃");
		int aPortWeatherImageId = weatherTypeUtil.getWeatherTypeImage(aPortWeather.getWeatherType());
		if( aPortWeatherImageId != -1) {
			imgWeatherEndIcon.setBackgroundResource(aPortWeatherImageId);
		}
	}
}
