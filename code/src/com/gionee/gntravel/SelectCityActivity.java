package com.gionee.gntravel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.gionee.gntravel.adapter.CityListViewAdapter;
import com.gionee.gntravel.db.dao.FlightCityDao;
import com.gionee.gntravel.db.dao.impl.FlightCityDaoImpl;
import com.gionee.gntravel.entity.DomesticCity;
import com.gionee.gntravel.utils.DBUtils;
import com.youju.statistics.YouJuAgent;

public class SelectCityActivity extends Activity implements
		View.OnClickListener, View.OnLongClickListener {
	private HashMap<String, Integer> selector;
	private LinearLayout layoutIndex;
	private ListView listView;
	private TextView tv_show;
	private CityListViewAdapter adapter;
	private ResultHandler handler = new ResultHandler();
	private static final int DATA_FINISH = 0x1000;
	private static final int UPDATE_FINISH = 0x1001;
	private ArrayList<String> firstLetters = new ArrayList<String>();
	private ArrayList<DomesticCity> mCitys = new ArrayList<DomesticCity>();
	private List<DomesticCity> searchCitys = new ArrayList<DomesticCity>();
	private ImageView img_delete;
	private EditText searchET;
	private ListView searchLv;
	private int height;
	private String mCityName;
	private SelectCityType mSelectType = SelectCityType.SELECTCITY;
	private TextView tv_title;
	private View rl_container;
	public LocationClient mLocationClient = null;
	public BDLocationListener myListener = new MyLocationListener();
	private class ResultHandler extends Handler {

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case DATA_FINISH:
				listView.setAdapter(adapter);
				getIndexView();
				switch(mSelectType) {
				case DEPARTCITY:
					getLocation();
				case SELECTCITY:
					break;
				case ARRIVECITY:
					tv_title.setText(getString(R.string.arrivecity));
					break;
				}
				break;
			case UPDATE_FINISH:
				Intent data = new Intent();
				data.putExtra("cityName", mCityName);
				setResult(RESULT_OK, data);
				finish();
				break;
			}
		}
	}
	private ArrayList<DomesticCity> hotCitys;
	private class LoadingData implements Runnable {

		

		@Override
		public void run() {
			selector = new HashMap<String, Integer>();
			DBUtils dbUitls = new DBUtils(SelectCityActivity.this);
			SQLiteDatabase db = dbUitls.openReadOnlyDatabase();
			FlightCityDao flightCityDao = new FlightCityDaoImpl();
			firstLetters = flightCityDao.findFirstLetter(db);
			
			switch(mSelectType) {
			case DEPARTCITY:
				firstLetters.add(0, "定位");
				firstLetters.add(1, "常选");
				selector.put("定位", mCitys.size());
				mCitys.add(new DomesticCity("定位当前城市"));
				mCitys.add(new DomesticCity("定位中..."));
				break;
			case SELECTCITY:
				firstLetters.add(0, "常选");
				break;
			case ARRIVECITY:
				firstLetters.add(0, "常选");
				break;
			}
			
			//add hotCity list
			selector.put("常选", mCitys.size());
			hotCitys = flightCityDao.findAllHotCity(db);
			mCitys.add(new DomesticCity("常选城市"));
			mCitys.addAll(hotCitys);
			
			//add FirstLetterCity list

			for(int i = 0;i<firstLetters.size();i++) {
				ArrayList<DomesticCity> tmpCitys = flightCityDao.findCitysByFirstLetter(db, firstLetters.get(i));
				if(!tmpCitys.isEmpty()) {
					selector.put(firstLetters.get(i), mCitys.size());
					mCitys.add(new DomesticCity(firstLetters.get(i)));
					mCitys.addAll(tmpCitys);
				}
			}
			db.close();
			adapter = new CityListViewAdapter(SelectCityActivity.this,mCitys);
			Message msg = Message.obtain();
			msg.what = DATA_FINISH;
			handler.sendMessage(msg);
		}

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.img_delete:
			deleteAchar();
			break;
		case R.id.tv_title:
			finish();
			break;
		default:
			break;
		}
	}



	private class TextChange implements TextWatcher {

		@Override
		public void beforeTextChanged(CharSequence s, int start, int count,
				int after) {

		}

		@Override
		public void onTextChanged(CharSequence s, int start, int before,
				int count) {

		}

		@Override
		public void afterTextChanged(Editable s) {
			if (s.length() == 0) {
				searchLv.setVisibility(View.GONE);
				img_delete.setVisibility(View.INVISIBLE);
				rl_container.setVisibility(View.VISIBLE);
			} else {
				img_delete.setVisibility(View.VISIBLE);
				rl_container.setVisibility(View.INVISIBLE);
				searchLv.setVisibility(View.VISIBLE);
				if (!TextUtils.isEmpty(s.toString())) {
					searchCitys.clear();
					
					DBUtils dbUitls = new DBUtils(SelectCityActivity.this);
					SQLiteDatabase db = dbUitls.openReadOnlyDatabase();
					FlightCityDao flightCityDao = new FlightCityDaoImpl();
					searchCitys = flightCityDao.findCitysWithSelection(db, s.toString());
					
					CityListViewAdapter adapter = new CityListViewAdapter(
							SelectCityActivity.this, searchCitys);
					searchLv.setAdapter(adapter);
				}
			}

		}

	}

	private class UpdateWeightFlag implements Runnable {
		private String cityName;
		private int weightFlag;
		public UpdateWeightFlag(String cityName, int weightFlag) {
			this.cityName = cityName;
			this.weightFlag = weightFlag;
		}

		@Override
		public void run() {
			upDate(weightFlag);
			upDate(cityName, 1);
			Message msg = Message.obtain();
			msg.what = UPDATE_FINISH;
			handler.sendMessage(msg);
		}

	}




	public void upDate(int weightFlag) {
		if(weightFlag == 0) {
			for (int i = 0; i < hotCitys.size(); i++) {
				DomesticCity city = hotCitys.get(i);
				int newWeightFlag = city.getWeightFlag() + 1;
				String cityName = city.getCityName();
				if (newWeightFlag > 14) {
					upDate(cityName, 0);
				} else {
					upDate(cityName, newWeightFlag);
				}
			}
		} else {
			for(int i=0;i<weightFlag;i++) {
				DomesticCity city = hotCitys.get(i);
				int newWeightFlag = city.getWeightFlag() + 1;
				String cityName = city.getCityName();
				upDate(cityName, newWeightFlag);
			}
		}
		


	}

	public void upDate(String cityName, int newWeightFlag) {
		DBUtils dbUitls = new DBUtils(SelectCityActivity.this);
		SQLiteDatabase db = dbUitls.openReadWriteDatabase();
		FlightCityDao flightCityDao = new FlightCityDaoImpl();
		flightCityDao.updateCityWeightFlag(db, cityName, newWeightFlag);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_choose_city);
		findViews();
		initViews();
		loadData();
	}

	private void findViews() {
		tv_title = (TextView) findViewById(R.id.tv_title);
		tv_title.setOnClickListener(this);
		findViewById(R.id.ll_search).setOnClickListener(this);
		img_delete = (ImageView) findViewById(R.id.img_delete);
		img_delete.setOnClickListener(this);
		img_delete.setOnLongClickListener(this);
		searchET = (EditText) findViewById(R.id.et_search);
		searchET.addTextChangedListener(new TextChange());
		searchLv = (ListView) findViewById(R.id.searchLv);
		layoutIndex = (LinearLayout) this.findViewById(R.id.ll_right_index);
		listView = (ListView) findViewById(R.id.listView);
		tv_show = (TextView) findViewById(R.id.tv);
		tv_show.setVisibility(View.GONE);
		rl_container = (View) findViewById(R.id.rl_container);
	}
	
	private void initViews() {
		Intent intent = getIntent();
		if(intent != null) {
			mSelectType = (SelectCityType)intent.getExtras().get("selectType");
		}
		switch(mSelectType) {
		case DEPARTCITY:
			tv_title.setText(getString(R.string.departcity));
			break;
		case ARRIVECITY:
			tv_title.setText(getString(R.string.arrivecity));
			break;
		case SELECTCITY:
			tv_title.setText(getString(R.string.selectcity));
			break;
		}
		
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				if(mSelectType == SelectCityType.DEPARTCITY && position == 1) {
					if(mCitys.get(1).getCityName().equals("定位中...")) {
						Toast.makeText(getApplicationContext(), "正在定位", 0).show();
					} else if(mCitys.get(1).getCityName().equals("定位失败")){
//						Toast.makeText(getApplicationContext(), "定位失败", 0).show();
					} else {
						Intent data = new Intent();
						data.putExtra("cityName", mCitys.get(1).getCityName());
						setResult(RESULT_OK, data);
						finish();
					}

				} else {
					String cityName = mCitys.get(position).getCityName();
					mCityName = cityName;
					int weightFlag = mCitys.get(position).getWeightFlag();
					handler.post(new UpdateWeightFlag(cityName,weightFlag));
				}
			}
		});
		
		searchLv.setOnItemClickListener(new  OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				String cityName = searchCitys.get(position).getCityName();
				mCityName = cityName;
				int weightFlag = searchCitys.get(position).getWeightFlag();
				handler.post(new UpdateWeightFlag(cityName,weightFlag));
			}
			
		});
	}
		
	private void loadData() {
		Thread thread = new Thread(new LoadingData());
		thread.start();
	}

	/**
	 * 绘制索引列表
	 */
	public void getIndexView() {
		height = layoutIndex.getHeight() / firstLetters.size();
		LinearLayout.LayoutParams params = new LayoutParams(
				LayoutParams.WRAP_CONTENT, height);
		for (int i = 0; i < firstLetters.size(); i++) {
			final TextView tv = new TextView(this);
			tv.setLayoutParams(params);
			tv.setText(firstLetters.get(i));
			tv.setTextAppearance(this, R.style.base_v5);
			tv.setPadding(0, 0, 8, 0);
			layoutIndex.addView(tv);
			layoutIndex.setOnTouchListener(new OnTouchListener() {

				@Override
				public boolean onTouch(View v, MotionEvent event)

				{
					float y = event.getY();
					int index = (int) (y / height);
					if (index > -1 && index < firstLetters.size()) {
						String key = firstLetters.get(index);
						if (selector.containsKey(key)) {
							int pos = selector.get(key);
							if (listView.getHeaderViewsCount() > 0) {
								listView.setSelectionFromTop(
										pos + listView.getHeaderViewsCount(), 0);
							} else {
								listView.setSelectionFromTop(pos, 0);
							}
							tv_show.setVisibility(View.VISIBLE);
							tv_show.setText(firstLetters.get(index));
						}
					}
					switch (event.getAction()) {
					case MotionEvent.ACTION_DOWN:
						break;
					case MotionEvent.ACTION_MOVE:
						break;
					case MotionEvent.ACTION_UP:
						tv_show.setVisibility(View.GONE);
						break;
					}
					return true;
				}
			});
		}
	}

	private void deleteAchar() {
		// delete a char
//		if (searchET.getSelectionStart() > 0) {
//			int index = searchET.getSelectionStart();
//			Editable editable = searchET.getText();
//			editable.delete(index - 1, index);
//		}
		// clear 
		searchET.setText("");
	}

	@Override
	public boolean onLongClick(View v) {
		switch (v.getId()) {
		case R.id.img_delete:
			searchET.setText("");
			return true;
		default:
			break;
		}
		return false;
	}

	public class MyLocationListener implements BDLocationListener {

		@Override
		public void onReceiveLocation(BDLocation location) {
			if (location == null) {
				mCitys.get(1).setCityName("定位失败");
				return;
			}

			try {
				if (location.getLocType() == BDLocation.TypeNetWorkLocation) {
					String city = location.getCity();
					String cityName = "定位失败";
					if(city.indexOf("市") != -1) {
						cityName = city.substring(0, city.indexOf("市"));
					} else {
						cityName = city;
					}
					mCitys.get(1).setCityName(cityName);
					
				} else {
					mCitys.get(1).setCityName("定位失败");
				}
				adapter.notifyDataSetChanged();
			} catch (Exception e) {
				e.printStackTrace();
				YouJuAgent.onEvent(SelectCityActivity.this, "定位异常:"+location.getCity());
				mCitys.get(1).setCityName("定位失败");
				adapter.notifyDataSetChanged();
			}
		}

		public void onReceivePoi(BDLocation poiLocation) {
		}
	}

	private void getLocation() {
		mLocationClient = new LocationClient(getApplicationContext());
		mLocationClient.registerLocationListener(myListener);
//		mLocationClient.setAK("eWeT5sSsDuzlGzfaVGyShoeG");
		LocationClientOption option = new LocationClientOption();
		option.setOpenGps(false);
		option.setAddrType("all");
		option.setServiceName("com.baidu.location.service_v2.9");
		// 返回国测局经纬度坐标系 coor=gcj02
		// 返回百度墨卡托坐标系 coor=bd09
		// 返回百度经纬度坐标系 coor=bd09ll
		option.setCoorType("gcj02");
		option.setScanSpan(900);
		option.disableCache(true);
		option.setPriority(LocationClientOption.NetWorkFirst);
		mLocationClient.setLocOption(option);
		if (mLocationClient != null) {
			mLocationClient.start();
			mLocationClient.requestLocation();
		} else {
			mCitys.get(1).setCityName("定位失败");
			adapter.notifyDataSetChanged();
		}
	}
	
	public static enum SelectCityType {
		DEPARTCITY,ARRIVECITY,SELECTCITY
	}
}
