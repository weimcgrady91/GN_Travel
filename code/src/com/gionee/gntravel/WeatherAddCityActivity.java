package com.gionee.gntravel;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.gionee.gntravel.entity.ProvinceCity;
import com.gionee.gntravel.entity.WeatherBean;
import com.gionee.gntravel.entity.WeatherLocal;

public class WeatherAddCityActivity extends BaseActivity implements OnClickListener {
	private String[] hotCityList;
	private String[] provinces;
	private GridView activity_weather_addcity_gv_hotcity;
	private GridView activity_weather_addcity_gv_province;
	HashMap<String, List<ProvinceCity>> mapOfData = new HashMap<String, List<ProvinceCity>>();
	private ImageView activity_weather_img_delete;
	private EditText activity_weather_addcity_et_searchkey;
	private ListView activity_weather_addcity_lv_searchcity;
	private List<String> cityList = new ArrayList<String>();
	private ScrollView activity_weather_addcity_sv;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_weather_addcity);
		TextView tv_title = (TextView) findViewById(R.id.tv_title);
		activity_weather_img_delete = (ImageView) findViewById(R.id.activity_weather_img_delete);
		activity_weather_addcity_sv = (ScrollView) findViewById(R.id.activity_weather_addcity_sv);
		activity_weather_addcity_lv_searchcity = (ListView) findViewById(R.id.activity_weather_addcity_lv_searchcity);
		activity_weather_addcity_et_searchkey = (EditText) findViewById(R.id.activity_weather_addcity_et_searchkey);
		tv_title.setText("选择城市");
		initData();
		findViewById(R.id.tv_title).setOnClickListener(this);
		activity_weather_img_delete.setOnClickListener(this);
		activity_weather_addcity_et_searchkey.addTextChangedListener(watcher);
		activity_weather_addcity_et_searchkey.setOnClickListener(this);
		adapter = new SearcCityListAdapter();
		activity_weather_addcity_lv_searchcity.setAdapter(adapter);
		activity_weather_addcity_gv_hotcity = (GridView) findViewById(R.id.activity_weather_addcity_gv_hotcity);
		activity_weather_addcity_gv_province = (GridView) findViewById(R.id.activity_weather_addcity_gv_province);
		activity_weather_addcity_gv_hotcity.setAdapter(new HotCityAndProvinceAdapter(hotCityList, "city"));
		activity_weather_addcity_gv_province.setAdapter(new HotCityAndProvinceAdapter(provinces, "province"));
	}

	private TextWatcher watcher = new TextWatcher() {

		@Override
		public void onTextChanged(CharSequence s, int start, int before, int count) {

		}

		@Override
		public void beforeTextChanged(CharSequence s, int start, int count, int after) {
		}

		@Override
		public void afterTextChanged(Editable s) {
			String searchKey = activity_weather_addcity_et_searchkey.getText().toString();
			if (!TextUtils.isEmpty(searchKey)) {
				activity_weather_img_delete.setVisibility(View.VISIBLE);
			} else {
				activity_weather_img_delete.setVisibility(View.INVISIBLE);
			}
			cityList.clear();

			if (s.toString().length() != 0) {
				cityList.addAll(searchCity(searchKey));
			}

			if (cityList != null && cityList.size() > 0) {
				activity_weather_addcity_sv.setVisibility(View.GONE);
				activity_weather_addcity_lv_searchcity.setVisibility(View.VISIBLE);
			} else {
				activity_weather_addcity_sv.setVisibility(View.VISIBLE);
				activity_weather_addcity_lv_searchcity.setVisibility(View.GONE);
			}
			adapter.notifyDataSetChanged();
			Toast.makeText(WeatherAddCityActivity.this, searchKey, Toast.LENGTH_LONG).show();
		}
	};
	private SearcCityListAdapter adapter;

	/**
	 * 按照关键字查询城市列表
	 * @param searchKey
	 * @return
	 */
	private List<String> searchCity(String searchKey) {
		Iterator iter = mapOfData.entrySet().iterator();
		List<String> cityList = new ArrayList<String>();
		while (iter.hasNext()) {
			Map.Entry<String, List<ProvinceCity>> entry = (Entry<String, List<ProvinceCity>>) iter.next();
			List<ProvinceCity> cities = entry.getValue();
			for (ProvinceCity city : cities) {
				String name = city.getName();
				String pinyin = city.getPinyin();
				String pinyin_suo = city.getSuoxiePinyin();
				if (name.contains(searchKey) || pinyin.contains(searchKey) || pinyin_suo.contains(searchKey)) {
					cityList.add(name);
				}
			}
		}
		return cityList;
	}

	private void initData() {
		hotCityList = getResources().getStringArray(R.array.hotcity);
		provinces = getResources().getStringArray(R.array.province);
		// 获取城市
		try {
			InputStream is = getAssets().open("city.txt");
			BufferedReader br = new BufferedReader(new InputStreamReader(is));
			String line = null;
			while ((line = br.readLine()) != null) {
				String data[] = line.split(",");
				ProvinceCity city = new ProvinceCity();
				city.setName(data[0]);
				city.setPinyin(data[1]);
				city.setSuoxiePinyin(data[2]);
				String province = data[3];
				city.setProvince(province);
				List<ProvinceCity> list = mapOfData.get(province);
				if (list == null) {
					list = new ArrayList<ProvinceCity>();
					mapOfData.put(province, list);
				}
				list.add(city);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.tv_title:
			finish();
			break;
		case R.id.activity_weather_img_delete:
			Toast.makeText(this, "sd", Toast.LENGTH_SHORT).show();
			activity_weather_addcity_et_searchkey.setText("");
			break;
		case R.id.activity_weather_addcity_et_searchkey:
			if (activity_weather_addcity_et_searchkey.getText().toString().equals(getResources().getString(R.string.weather_search_city))) {
				activity_weather_addcity_et_searchkey.setText("");
			}
			break;
		default:
			break;
		}

	}

	/**
	 * 热门城市和身份adapter
	 * @author yangxy
	 *
	 */
	public class HotCityAndProvinceAdapter extends BaseAdapter {
		private LayoutInflater inflater = LayoutInflater.from(WeatherAddCityActivity.this);
		private String[] mStrings;
		private String mFlag;
		private List<WeatherLocal> locals = null;

		public HotCityAndProvinceAdapter(String[] strings, String flag) {
			mStrings = strings;
			mFlag = flag;
			locals = WeatherActivity.getLocals(WeatherAddCityActivity.this);
		}

		@Override
		public int getCount() {
			return mStrings.length;
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
			View view = inflater.inflate(R.layout.item_city, null);
			final TextView tv = (TextView) view.findViewById(R.id.city_tv);
			final String text = mStrings[position];
			tv.setText(text);
			tv.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					//点击城市item跳转到城市天气预报页面
					if (mFlag.equals("city")) {
						getWeather(text);
					} else {
						String province = tv.getText().toString();
						Intent intent = new Intent(WeatherAddCityActivity.this, WeatherProvinceActivity.class);
						Bundle bundle = new Bundle();
						ArrayList list = new ArrayList();
						list.add(mapOfData.get(province));
						bundle.putStringArrayList("list", list);
						intent.putExtras(bundle);
						startActivity(intent);
					}
				}
			});
			boolean enable = true;
			if (mFlag.equalsIgnoreCase("city")) {
				for (int i = 0; locals != null && i < locals.size(); i++) {
					WeatherLocal wl = locals.get(i);
					if (mStrings[position].equalsIgnoreCase(wl.getCity())) {
						enable = false;
						break;
					}

				}
			}
			view.setEnabled(enable);
			tv.setEnabled(enable);
			if (!enable) {
				view.setOnClickListener(null);
				tv.setTextColor(Color.GRAY);
			}
			return view;
		}

	}
	public void getWeather(final String text) {
		WeatherBean bean = new WeatherBean();
		bean.setCity(text);
		int indexOfOld = WeatherActivity.updateOrAddLocalsByWeatherBean(WeatherAddCityActivity.this, bean, 0, true);
		Intent i = new Intent(WeatherAddCityActivity.this, WeatherActivity.class);
		i.putExtra("needLocation", false);
		i.putExtra("city", text);
		WeatherActivity.indexOfCurrentCity = indexOfOld;
		i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		startActivity(i);
	}
	public class SearcCityListAdapter extends BaseAdapter {
		private LayoutInflater inflater = LayoutInflater.from(WeatherAddCityActivity.this);

		@Override
		public int getCount() {
			return cityList.size();
		}

		@Override
		public Object getItem(int arg0) {
			return cityList.get(arg0);
		}

		@Override
		public long getItemId(int arg0) {
			return arg0;
		}

		@Override
		public View getView(final int position, View convertView, ViewGroup arg2) {
			View view;
			if (convertView == null) {
				view = inflater.inflate(R.layout.city_item_index, null);
			} else {
				view = convertView;
			}
			final TextView tv = (TextView) view.findViewById(R.id.indexTv);
			final String text = cityList.get(position);
			tv.setText(text);
			view.setBackground(null);
			view.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					getWeather(text);
				}
			});
			return view;
		}
	}
}
