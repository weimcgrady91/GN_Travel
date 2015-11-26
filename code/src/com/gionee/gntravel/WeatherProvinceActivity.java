package com.gionee.gntravel;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.TextView;

import com.gionee.gntravel.entity.ProvinceCity;
import com.gionee.gntravel.entity.WeatherBean;
import com.gionee.gntravel.entity.WeatherLocal;

public class WeatherProvinceActivity extends BaseActivity implements OnClickListener {
	private ArrayList<ProvinceCity> ProvinceCitys;
	private TextView tv_title;
	private GridView activity_weather_gv_provincecity;
	private List<WeatherLocal> locals;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_weather_province);
		findviews();
		initData();
	}

	private void initData() {
		locals = WeatherActivity.getLocals(this);

		Bundle bundle = getIntent().getExtras();
		ArrayList list = bundle.getParcelableArrayList("list");
		ProvinceCitys = (ArrayList<ProvinceCity>) list.get(0);
		String province = ProvinceCitys.get(0).getProvince();
		tv_title.setText(province);
		activity_weather_gv_provincecity.setAdapter(new ProvinceCityAdapter());
	}

	private void findviews() {
		tv_title = (TextView) findViewById(R.id.tv_title);
		tv_title.setOnClickListener(this);
		activity_weather_gv_provincecity = (GridView) findViewById(R.id.activity_weather_gv_provincecity);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.tv_title:
			finish();
			break;
		default:
			break;
		}

	}

	public class ProvinceCityAdapter extends BaseAdapter {
		private LayoutInflater inflater = LayoutInflater.from(WeatherProvinceActivity.this);

		@Override
		public int getCount() {
			return ProvinceCitys.size();
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
			View view= inflater.inflate(R.layout.item_city, null);
			final TextView tv = (TextView) view.findViewById(R.id.city_tv);
			tv.setText(ProvinceCitys.get(position).getName());
			tv.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					WeatherBean bean = new WeatherBean();
					bean.setCity(ProvinceCitys.get(position).getName());
					int indexOfOld = WeatherActivity.updateOrAddLocalsByWeatherBean(WeatherProvinceActivity.this, bean, 0, true);
					Intent i = new Intent(WeatherProvinceActivity.this, WeatherActivity.class);
					i.putExtra("needLocation", false);
					i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
					WeatherActivity.indexOfCurrentCity = indexOfOld;
					startActivity(i);
				}
			});

			boolean enable = true;
			for (int i = 0; locals != null && i < locals.size(); i++) {
				WeatherLocal wl = locals.get(i);
				if (ProvinceCitys.get(position).getName().equalsIgnoreCase(wl.getCity())) {
					enable = false;
					break;
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
}
