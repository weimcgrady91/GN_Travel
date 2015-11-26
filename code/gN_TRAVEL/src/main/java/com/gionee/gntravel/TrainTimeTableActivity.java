package com.gionee.gntravel;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.gionee.gntravel.entity.Station;
import com.gionee.gntravel.entity.TrainNum;
import com.gionee.gntravel.entity.TrainNumberRequestInfo;
import com.gionee.gntravel.utils.FinalString;
import com.gionee.gntravel.utils.HttpConnCallback;
import com.gionee.gntravel.utils.HttpConnUtil4Gionee;
import com.gionee.gntravel.utils.JSONUtil;
import com.gionee.gntravel.utils.NetWorkUtil;
import com.gionee.gntravel.utils.Utils;

public class TrainTimeTableActivity extends Activity implements
		OnClickListener, HttpConnCallback {
	private static final int DATA_FINISH = 0x1000;
	private HashMap<String, String> params;
	private BaseAdapter myAdapter;
	private ResultHandler handler = new ResultHandler();
	private ListView listView;
	private String trainName;
	private String totalTimeStr;
	private TextView tvTrainName;
	private TextView tvTotalTime;
	private TextView tvTotalName;
	private RelativeLayout rl_loading;
	private ImageView img_loading;
	private View rl_failed;
	//private Button btn_refresh;
	private TrainNumberRequestInfo trainNumberRequestInfo;
	ArrayList<Station> stationList;

	private HttpConnUtil4Gionee task;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_train_time_table);
		setupView();
		setupDate();
		loadingData();

	}

	private void setupDate() {
		trainNumberRequestInfo = new TrainNumberRequestInfo();
		trainNumberRequestInfo.setTrainName(trainName);
	}

	public void loadingData() {
		startProgressDialog();
		if (!NetWorkUtil.isNetworkConnected(this)) {
			// 无网络连接 提示
			Toast.makeText(this, R.string.no_network, Toast.LENGTH_LONG).show();
			stopProgressDialog();
			return;
		}
		String trainTableUrl = getString(R.string.gionee_host)
				+ FinalString.TRAIN_STATION_TABLE_URL;
		params = new HashMap<String, String>();
		params.put("json",URLEncoder.encode(JSONUtil.getInstance().toJSON(trainNumberRequestInfo)));
		task = new HttpConnUtil4Gionee(this);
		task.execute(trainTableUrl, params, HttpConnUtil4Gionee.HttpMethod.GET);

	}

	private void setupView() {
		Intent intent = getIntent();
		trainName = intent.getStringExtra("trainName");
		tvTotalName = (TextView) findViewById(R.id.tv_title);
		tvTotalName.setOnClickListener(this);
		tvTotalName.setText(getString(R.string.traintabletime));
		totalTimeStr = intent.getStringExtra("totalTimeStr");
		tvTrainName = (TextView) findViewById(R.id.tv_train_detail_name);
		tvTrainName.setText(trainName);
		tvTotalTime = (TextView) findViewById(R.id.tv_train_total_time);
		tvTotalTime.setText(totalTimeStr);
		listView = (ListView) findViewById(R.id.lv_train_station);
		rl_loading = (RelativeLayout) findViewById(R.id.rl_loading);
		img_loading = (ImageView) findViewById(R.id.img_loading);
		rl_failed = (View) findViewById(R.id.rl_failed);
		//btn_refresh = (Button) findViewById(R.id.btn_refresh);
		//btn_refresh.setOnClickListener(this);
	}

	private class ResultHandler extends Handler {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case DATA_FINISH:
				listView.setAdapter(myAdapter);
				myAdapter.notifyDataSetChanged();
				stopProgressDialog();
				break;
			case FinalString.NOT_FOUND:
				Toast.makeText(TrainTimeTableActivity.this,R.string.service_error, 1).show();
				break;
			}
		}

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.tv_title:
			this.finish();
			break;

		default:
			break;
		}
	}

	@Override
	public void execute(String result) {
		if (TextUtils.isEmpty(result)) {
			Toast.makeText(this, R.string.network_timeout, Toast.LENGTH_SHORT).show();
			stopProgressDialog();
			return;
		}

		if (stationList != null) {
			stationList.clear();
		} else {
			stationList = new ArrayList<Station>();
		}
		if (myAdapter != null) {
		} else {
			myAdapter = new MyAdapter();
		}
		new Thread(new DoResult(result)).start();

	}

	private class DoResult implements Runnable {
		private String result;

		public DoResult(String result) {
			this.result = result;
		}

		@Override
		public void run() {
			try {

				JSONObject all = new JSONObject(result);
				if (!FinalString.ERRORCODE.equals(all.getString("errorCode"))) {
					Message msg = Message.obtain();
					msg.what = FinalString.NOT_FOUND;
					msg.obj = all.getString("errorMsg");
					handler.sendMessage(msg);
					return;
				}
				JSONObject trainInfo = all.getJSONObject("TrainInfo");

				TrainNum trainNum = new TrainNum();

				JSONArray stationItems = new JSONArray(
						trainInfo.getString("StationItems"));
				stationList = new ArrayList<Station>();
				for (int j = 0; j < stationItems.length(); j++) {
					JSONObject stationItem = stationItems.getJSONObject(j);
					Station station = new Station();
					station.setArrivalTime(stationItem.getString("ArrivalTime"));
					station.setDepartureTime(stationItem
							.getString("DepartureTime"));
					station.setDuringFromStart(stationItem
							.getString("DuringFromStart"));
					station.setStationName(stationItem.getString("StationName"));
					stationList.add(station);
				}
				trainNum.setTicketList(stationList);

			} catch (JSONException e) {
				e.printStackTrace();
			}
			Message msg = Message.obtain();
			msg.what = DATA_FINISH;
			handler.sendMessage(msg);
		}
	}

	private class MyAdapter extends BaseAdapter {
		@Override
		public int getCount() {
			return stationList.size();
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
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder viewHolder = null;
			if (convertView == null) {
				convertView = getLayoutInflater().inflate(R.layout.train_station_item, null);
				viewHolder = new ViewHolder();
				viewHolder.stationName = (TextView) convertView.findViewById(R.id.tv_train_station);
				viewHolder.departureTime = (TextView) convertView.findViewById(R.id.tv_train_departure_time);
				viewHolder.arrivalTime = (TextView) convertView.findViewById(R.id.tv_train_arrival_time);
				viewHolder.stayTime = (TextView) convertView.findViewById(R.id.tv_train_stay_time);
				convertView.setTag(viewHolder);
			} else {
				viewHolder = (ViewHolder) convertView.getTag();
			}
			if (position == 0) {
				viewHolder.departureTime.setText(stationList.get(position).getDepartureTime());
				viewHolder.arrivalTime.setText("-----");
				viewHolder.stayTime.setText("-----");
			} else if (position == stationList.size() - 1) {
				viewHolder.departureTime.setText("-----");
				viewHolder.arrivalTime.setText(stationList.get(position).getArrivalTime());
				viewHolder.stayTime.setText("-----");
			} else {
				viewHolder.departureTime.setText(stationList.get(position).getDepartureTime());
				viewHolder.arrivalTime.setText(stationList.get(position).getArrivalTime());
				String[] stayTime = Utils.getTimes(stationList.get(position).getArrivalTime(), stationList.get(position).getDepartureTime(), 0);
				if (stayTime[1].startsWith("0")) {
					stayTime[1] = stayTime[1].substring(1, stayTime[1].length());
				}
				viewHolder.stayTime.setText(stayTime[1]+ getString(R.string.min));
			}
			viewHolder.stationName.setText(stationList.get(position).getStationName());
			return convertView;
		}
	}

	static class ViewHolder {
		public TextView stationName;// 车站名称
		public TextView departureTime;// 离开站点时间
		public TextView arrivalTime;// 到达站时间
		public TextView stayTime;// 停留多长时间
	}

	private void startProgressDialog() {
		rl_failed.setVisibility(View.GONE);
		rl_loading.setVisibility(View.VISIBLE);
		AnimationDrawable animationDrawable = (AnimationDrawable) img_loading.getBackground();
		animationDrawable.start();
	}

	private void stopProgressDialog() {
		AnimationDrawable animationDrawable = (AnimationDrawable) img_loading
				.getBackground();
		animationDrawable.stop();
		rl_loading.setVisibility(View.GONE);
	}
}
