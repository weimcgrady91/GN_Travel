package com.gionee.gntravel;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.location.AMapLocation;
import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.gionee.gntravel.entity.WeatherBean;
import com.gionee.gntravel.entity.WeatherLocal;
import com.gionee.gntravel.entity.WeatherRepParam;
import com.gionee.gntravel.utils.JSONUtil;
import com.gionee.gntravel.utils.LocationManager;
import com.gionee.gntravel.utils.LocationManager.LocationListener;
import com.gionee.gntravel.utils.LocationManagerImpl;
import com.gionee.gntravel.utils.SpUtil;
import com.gionee.gntravel.utils.ToastUtil;
import com.google.gson.reflect.TypeToken;

public class WeatherActivity extends BaseActivity implements OnClickListener ,LocationListener {
	private static final String KEY_WEATHER_LOCAL = "KEY_WEATHER_LOCAL";

	private TextView tv_title;

	private ViewPager item_weather_vp;
	public LocationClient mLocationClient = null;
	public BDLocationListener myListener = new MyLocationListener();
	private Button activity_weather_btn_refresh;
	private Button activity_weather_btn_city;
	private PagerAdapter adapter;
	private LinearLayout hotel_weather_ll_dots;
	private HashMap<String, Integer> tianqi_iv_map;
	private static String locateCityName;// 当前定位城市
	private String currentPageCity;
	public static int indexOfCurrentCity = 0;// 当前page页要显示哪个城市
	private boolean needLocation = true;// 默认刚刚进来需要定位你所在城市
	// 给adapter使用的，要保持这个和本地sp同步
	private static List<WeatherLocal> weatherLocals;

	private boolean needLoad = true;
	private String city;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_weather);
		findViewById(R.id.tv_title).setOnClickListener(this);
		findViews();
		initData();
		needLocation = getIntent().getBooleanExtra("needLocation", needLocation);
		city = getIntent().getStringExtra("city");
		// 获取本地存储的城市信息(adapter使用weatherLocals展示数据)
		if (needLocation) {
			// 请求定位
			reqLocation();
		}
		weatherLocals = getLocals(this);
		if (weatherLocals != null && weatherLocals.size() != 0) {
			currentPageCity = weatherLocals.get(0).getCity();
		}
		indexOfCurrentCity = getIntent().getIntExtra("indexOfCurrentCity", indexOfCurrentCity);
		if (needLocation) {
			// 设置所有城市都需要更新
			setAllNeedUpdate(weatherLocals);
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		init(indexOfCurrentCity);
	}

	private void setAllNeedUpdate(List<WeatherLocal> weatherLocals) {
		if (weatherLocals != null && weatherLocals.size() != 0) {
			for (WeatherLocal l : weatherLocals) {
				l.setShouldUpdate(true);
			}
		}
	}

	/**
	 * 得到本地缓存城市天气的数据
	 * 
	 * @param context
	 * @return
	 */
	public static List<WeatherLocal> getLocals(Context context) {
		SpUtil sp = new SpUtil(context);
		String jsonLocal = sp.getString(KEY_WEATHER_LOCAL);
		if (jsonLocal == null) {
			return null;
		}
		return JSONUtil.getInstance().fromJSON(new TypeToken<ArrayList<WeatherLocal>>() {
		}.getType(), jsonLocal);
	}

	/**
	 * 城市天气数据同步到本地
	 * 
	 * @param context
	 */
	public static void synLocalsToSp(Context context) {
		SpUtil sp = new SpUtil(context);
		if (weatherLocals != null && weatherLocals.size() != 0) {
			for (int i = 0; i < weatherLocals.size(); i++) {
				if (weatherLocals.get(i).getShowIndex() != -1) {
					weatherLocals.get(i).setShowIndex(i);
				}
			}
			String json = JSONUtil.getInstance().toJSON(weatherLocals);
			sp.putString(KEY_WEATHER_LOCAL, json);
		} else {
			sp.clearSp();
		}
	}

	/**
	 * 初始化天气文字描述和天气图标的对应关系
	 */
	private void initData() {
		tianqi_iv_map = new HashMap<String, Integer>();
		tianqi_iv_map.put(getResources().getString(R.string.weather_qing), R.drawable.yangguang);
		tianqi_iv_map.put(getResources().getString(R.string.weather_baoxue), R.drawable.baoxue);
		tianqi_iv_map.put(getResources().getString(R.string.weather_baoydaodabaoy), R.drawable.baoyu);//
		tianqi_iv_map.put(getResources().getString(R.string.weather_baoyu), R.drawable.baoyu);
		tianqi_iv_map.put(getResources().getString(R.string.weather_dabaoydaotedabaoy), R.drawable.dabaoyu);//
		tianqi_iv_map.put(getResources().getString(R.string.weather_dabaoyu), R.drawable.dabaoyu);
		tianqi_iv_map.put(getResources().getString(R.string.weather_dadaobaox), R.drawable.dadaobx);
		tianqi_iv_map.put(getResources().getString(R.string.weather_dadaobaoy), R.drawable.dadaoby);
		tianqi_iv_map.put(getResources().getString(R.string.weather_daxue), R.drawable.daxue);
		tianqi_iv_map.put(getResources().getString(R.string.weather_dayu), R.drawable.dayu);
		tianqi_iv_map.put(getResources().getString(R.string.weather_dongyu), R.drawable.dabaoyu);//
		tianqi_iv_map.put(getResources().getString(R.string.weather_duoyun), R.drawable.duoyun);
		tianqi_iv_map.put(getResources().getString(R.string.weather_fuchen), R.drawable.fuchen);
		tianqi_iv_map.put(getResources().getString(R.string.weather_leizhenyu), R.drawable.leizhenyu);
		tianqi_iv_map.put(getResources().getString(R.string.weather_leizhy_bingbao), R.drawable.leizhy_bingbao);
		tianqi_iv_map.put(getResources().getString(R.string.weather_mai), R.drawable.mai);
		tianqi_iv_map.put(getResources().getString(R.string.weather_qiangshachenbao), R.drawable.qiangshachenbao);
		tianqi_iv_map.put(getResources().getString(R.string.weather_shachenbao), R.drawable.shachenbao);
		tianqi_iv_map.put(getResources().getString(R.string.weather_tedabaoy), R.drawable.tedabaoyu);
		tianqi_iv_map.put(getResources().getString(R.string.weather_wu), R.drawable.wu);
		tianqi_iv_map.put(getResources().getString(R.string.weather_xiaodaozhx), R.drawable.xiaodaozhx);
		tianqi_iv_map.put(getResources().getString(R.string.weather_xiaodaozhy), R.drawable.xiaodaozhy);
		tianqi_iv_map.put(getResources().getString(R.string.weather_xiaoxue), R.drawable.xiaoxue);
		tianqi_iv_map.put(getResources().getString(R.string.weather_xiaoyu), R.drawable.xiaoyu);
		tianqi_iv_map.put(getResources().getString(R.string.weather_yangsha), R.drawable.yangsha);
		tianqi_iv_map.put(getResources().getString(R.string.weather_yin), R.drawable.yin);
		tianqi_iv_map.put(getResources().getString(R.string.weather_yujiaxue), R.drawable.yujiaxue);
		tianqi_iv_map.put(getResources().getString(R.string.weather_zhdaodax), R.drawable.zhongdaodx);
		tianqi_iv_map.put(getResources().getString(R.string.weather_zhongxue), R.drawable.zhongxue);
		tianqi_iv_map.put(getResources().getString(R.string.weather_zhenxue), R.drawable.zhenxue);
		tianqi_iv_map.put(getResources().getString(R.string.weather_zhenyu), R.drawable.zhenyu);
		tianqi_iv_map.put(getResources().getString(R.string.weather_zhongdaoday), R.drawable.zhongdaoday);
		tianqi_iv_map.put(getResources().getString(R.string.weather_zhongyu), R.drawable.zhongyu);
	}

	public class MyLocationListener implements BDLocationListener {

		@Override
		public void onReceiveLocation(BDLocation location) {
			if (location == null) {
				Toast.makeText(WeatherActivity.this, getResources().getString(R.string.weather_addcity), 0).show();
				return;
			}
			// 定位成功
			String city = location.getCity();
			if (city == null) {
				Toast.makeText(WeatherActivity.this, getResources().getString(R.string.weather_addcity), 0).show();
				return;
			}
			String cityName = city.substring(0, city.indexOf("市"));
			locateCityName = cityName;
			Toast.makeText(WeatherActivity.this, "定位到:" + locateCityName, 1).show();
			if (weatherLocals!=null&&weatherLocals.size()>0) {
				for (WeatherLocal weatherLocal : weatherLocals) {
					String city_local = weatherLocal.getCity();
					if (city_local.equals(locateCityName)) {
						needLoad = false;
						break;
					}
				}
			}
			if (needLoad) {
				reqWeatherData(cityName);
			}
		}

		public void onReceivePoi(BDLocation poiLocation) {

		}
	}

	private void reqLocation() {
		LocationManager manager = new LocationManagerImpl(this);
		manager.requestLocation();
		manager.setLocationManager(this);
//		mLocationClient = new LocationClient(getApplicationContext());
//		mLocationClient.registerLocationListener(myListener);
//		mLocationClient.setAK("eWeT5sSsDuzlGzfaVGyShoeG");
//		LocationClientOption option = new LocationClientOption();
//		option.setOpenGps(false);
//		option.setAddrType("all");
//		option.setServiceName("com.baidu.location.service_v2.9");
//		// 返回国测局经纬度坐标系 coor=gcj02
//		// 返回百度墨卡托坐标系 coor=bd09
//		// 返回百度经纬度坐标系 coor=bd09ll
//		option.setCoorType("gcj02");
//		option.setScanSpan(900);
//		option.disableCache(true);
//		option.setPriority(LocationClientOption.NetWorkFirst);
//		mLocationClient.setLocOption(option);
//		if (mLocationClient != null) {
//			mLocationClient.start();
//			mLocationClient.requestLocation();
//		} else {
//		}
	}

	private void findViews() {

		hotel_weather_ll_dots = (LinearLayout) findViewById(R.id.hotel_weather_ll_dots);
		tv_title = (TextView) findViewById(R.id.tv_title);
		activity_weather_btn_refresh = (Button) findViewById(R.id.activity_weather_btn_refresh);
		activity_weather_btn_refresh.setOnClickListener(this);
		activity_weather_btn_city = (Button) findViewById(R.id.activity_weather_btn_city);
		activity_weather_btn_city.setOnClickListener(this);
		tv_title.setText(R.string.weather);

		item_weather_vp = (ViewPager) findViewById(R.id.item_weather_vp);

	}

	private void init(int showIndex) {
		initDot();
		initAdapter(showIndex);
	}

	private void initAdapter(int showIndex) {
		adapter = new PagerAdapter() {

			@Override
			public boolean isViewFromObject(View arg0, Object arg1) {
				return arg0 == arg1;
			}

			@Override
			public int getCount() {
				return weatherLocals == null ? 0 : weatherLocals.size();
			}

			@Override
			public void destroyItem(ViewGroup container, int position, Object object) {
				item_weather_vp.removeView((View) object);
			}

			@Override
			public Object instantiateItem(ViewGroup container, int position) {
				WeatherItemHolder item = new WeatherItemHolder(WeatherActivity.this);
				item.update(weatherLocals.get(position));
				View view = item.getLayout();
				item_weather_vp.addView(view);
				return view;
			}
		};
		item_weather_vp.setAdapter(adapter);
		selectDot(hotel_weather_ll_dots, 0);
		item_weather_vp.setOnPageChangeListener(new OnPageChangeListener() {

			@Override
			public void onPageSelected(int position) {
				indexOfCurrentCity = position;
				currentPageCity = weatherLocals.get(position).getCity();
				// 选择了的
				selectDot(hotel_weather_ll_dots, position);
			}

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {

			}

			@Override
			public void onPageScrollStateChanged(int arg0) {

			}
		});
		if (showIndex != -1) {
			item_weather_vp.setCurrentItem(showIndex);
		} else {
			item_weather_vp.setCurrentItem(indexOfCurrentCity);
		}
	}

	private void initDot() {
		if (weatherLocals == null) {
			return;
		}
		hotel_weather_ll_dots.removeAllViews();
		for (int i = 0; i < weatherLocals.size(); i++) {
			View dot = LayoutInflater.from(this).inflate(R.layout.item_vp_dot, null);
			LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT,
					LayoutParams.WRAP_CONTENT);
			lp.gravity = Gravity.CENTER;
			dot.setLayoutParams(lp);
			hotel_weather_ll_dots.addView(dot);
		}
	}

	private void reqWeatherData(String cityName) {
		if (cityName == null) {
			return;
		}
		showProgress();
		String REQ_WEATHER_UTL;
		REQ_WEATHER_UTL = getString(R.string.gionee_host) + "/GioneeTrip/tripAction_tripWeatherInfo.action?fc=4&cy="
				+ cityName;
		post.doGet(REQ_WEATHER_UTL);
	}

	@Override
	public void onReqSuc(String requestUrl, String json) {
		super.onReqSuc(requestUrl, json);
		dismissProgress();
		WeatherBean wb = JSONUtil.getInstance().fromJSON(WeatherBean.class, json);

		if (isCurrentCity(wb)) {
			updateOrAddLocalsByWeatherBean(this, wb, -1, false);
		} else {
			updateOrAddLocalsByWeatherBean(this, wb, 0, false);
		}
		init(-1);
	}

	public static boolean isCurrentCity(WeatherBean wb) {
		return locateCityName != null && locateCityName.equals(wb.getCity());
	}

	/**
	 * 更新相同的城市数据，或者添加新的数据。
	 * 
	 * @param bean
	 * @param showIndex
	 *            是定位数据，使用-1，使用0 程序自动决定（如果有就替换，没有就追加到最后），其他值非0值，用户决定天气显示位置。
	 *            如果是-2，就是删除该条目
	 * @param shouldUpdate
	 */
	public static int updateOrAddLocalsByWeatherBean(Context ctx, WeatherBean bean, int showIndex, boolean shouldUpdate) {
		int indexOfOld = 0;
		if (bean == null || bean.getCity() == null) {
			return -1;
		}
		WeatherLocal l = new WeatherLocal();
		l.setCity(bean.getCity());
		l.setLastUpdateTime(System.currentTimeMillis());
		l.setShouldUpdate(shouldUpdate);
		l.setWeatherBean(bean);

		if (weatherLocals == null) {
			weatherLocals = new ArrayList<WeatherLocal>();
		}
		if (showIndex == -2) {
			if (weatherLocals.remove(l)) {
				// 同步更新到本地
				synLocalsToSp(ctx);
			}
		} else {
			// 返回-1说明列表中原来没有
			indexOfOld = weatherLocals.indexOf(l);

			if (indexOfOld == -1) {
				weatherLocals.add(l);
				indexOfOld = weatherLocals.size() - 1;
			} else {
				weatherLocals.set(indexOfOld, l);
			}

			if (showIndex == -1) {
				l.setShowIndex(-1);
			} else if (showIndex == 0) {
				if (indexOfOld == -1) {
					l.setShowIndex(weatherLocals.size() - 1);
				} else {
					l.setShowIndex(indexOfOld);
				}
			} else {
				l.setShowIndex(showIndex);
			}

			// 重新排序，大的showIndex往后面排
			Collections.sort(weatherLocals, new Comparator<WeatherLocal>() {

				@Override
				public int compare(WeatherLocal lhs, WeatherLocal rhs) {
					return lhs.getShowIndex() > rhs.getShowIndex() ? 1 : -1;
				}

			});
			// 同步更新到本地
			synLocalsToSp(ctx);
		}
		return indexOfOld;

	}

	@Override
	public void onReqFail(String requestUrl, Exception ex, int errorCode) {
		super.onReqFail(requestUrl, ex, errorCode);
		dismissProgress();
		String exclass = ex.getClass().toString();
		if (exclass.equals("class org.apache.http.conn.HttpHostConnectException")) {
			Toast.makeText(this, "连接服务器失败！", 0).show();
		}
	}

	/**
	 * 顶部圆点的状态
	 * @Author: yangxy
	 * @Version: V1.0
	 * @Create Date: 2014-11-12
	 * @param hotel_detail_ll_dots
	 * @param toBeSelected
	 */
	protected void selectDot(LinearLayout hotel_detail_ll_dots, int toBeSelected) {
		if (hotel_detail_ll_dots.getChildCount() <= 1) {
			hotel_detail_ll_dots.setVisibility(View.INVISIBLE);
		} else {
			hotel_detail_ll_dots.setVisibility(View.VISIBLE);
		}
		for (int i = 0; i < hotel_detail_ll_dots.getChildCount(); i++) {
			LinearLayout dot = (LinearLayout) hotel_detail_ll_dots.getChildAt(i);

			ImageView iv = (ImageView) dot.findViewById(R.id.item_vp_dot_iv);
			if (i == toBeSelected) {
				iv.setImageResource(R.drawable.weather_dot_selected);
			} else {
				iv.setImageResource(R.drawable.weather_dot_normal);
			}
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.tv_title:
			finish();
			break;
		case R.id.activity_weather_btn_refresh:
			reqWeatherData(currentPageCity);
			break;
		case R.id.activity_weather_btn_city:
			addCity();
			break;
		default:
			break;
		}

	}
	/**
	 * 添加城市
	 * @Author: yangxy
	 * @Version: V1.0
	 * @Create Date: 2014-11-12
	 */

	private void addCity() {
		if (weatherLocals != null && weatherLocals.size() != 0) {
			Intent weatherCityManageActivity = new Intent(this, WeatherCityManageActivity.class);
			Bundle bundle = new Bundle();
			ArrayList list = new ArrayList();
			list.add(weatherLocals);
			bundle.putStringArrayList("list", list);
			weatherCityManageActivity.putExtras(bundle);
			startActivity(weatherCityManageActivity);
		} else {
			Intent weatherAddCityActivity = new Intent(this, WeatherAddCityActivity.class);
			startActivity(weatherAddCityActivity);
		}
	}

	public class WeatherItemHolder {
		private View layout;

		public View getLayout() {
			return layout;
		}

		private TextView activity_weather_tv_city;
		private TextView activity_weather_tv_week;
		private TextView activity_weather_tv_pm;
		private TextView activity_weather_tv_wendu_current;
		private TextView activity_weather_tv_weather;
		private TextView activity_weather_tv_wendu;
		private TextView activity_weather_tv_fengli;
		private TextView activity_weather_tv_shidu;
		private TextView activity_weather_tv_zhuozhuang;
		private TextView activity_weather_tv_ziwaixian;
		private TextView activity_weather_tv_week_first;
		private ImageView activity_weather_iv_tianqi_first;
		private TextView activity_weather_tv_tianqi_first;
		private TextView activity_weather_tv_dushu_first;
		private TextView activity_weather_tv_week_second;
		private ImageView activity_weather_iv_tianqi_second;
		private TextView activity_weather_tv_tianqi_second;
		private TextView activity_weather_tv_dushu_second;
		private TextView activity_weather_tv_week_three;
		private ImageView activity_weather_iv_tianqi_three;
		private TextView activity_weather_tv_tianqi_three;
		private TextView activity_weather_tv_dushu_three;
		private TextView activity_weather_tv_updatetime;
		private TextView pm_tv_bottom;

		public WeatherItemHolder(Activity context) {
			layout = context.getLayoutInflater().inflate(R.layout.item_weather, null);
			activity_weather_tv_city = (TextView) layout.findViewById(R.id.activity_weather_tv_city);
			// 设置默认城市
			activity_weather_tv_city.setText(city);
			activity_weather_tv_week = (TextView) layout.findViewById(R.id.activity_weather_tv_week);
			// 设置默认星期
			Calendar cal = Calendar.getInstance();
			cal.setTime(new Date());
			int week = cal.get(Calendar.WEEK_OF_MONTH);
			String weekStr = null;
			switch (week) {
			case 1:
				weekStr = "星期一";
				break;
			case 2:
				weekStr = "星期二";
				break;
			case 3:
				weekStr = "星期三";
				break;
			case 4:
				weekStr = "星期四";
				break;
			case 5:
				weekStr = "星期五";
				break;
			case 6:
				weekStr = "星期六";
				break;
			case 7:
				weekStr = "星期天";
				break;

			default:
				break;
			}
			activity_weather_tv_week.setText(weekStr);
			activity_weather_tv_pm = (TextView) layout.findViewById(R.id.activity_weather_tv_pm);
			activity_weather_tv_wendu_current = (TextView) layout.findViewById(R.id.activity_weather_tv_wendu_current);
			activity_weather_tv_weather = (TextView) layout.findViewById(R.id.activity_weather_tv_weather);
			activity_weather_tv_wendu = (TextView) layout.findViewById(R.id.activity_weather_tv_wendu);
			activity_weather_tv_fengli = (TextView) layout.findViewById(R.id.activity_weather_tv_fengli);
			activity_weather_tv_shidu = (TextView) layout.findViewById(R.id.activity_weather_tv_shidu);
			activity_weather_tv_zhuozhuang = (TextView) layout.findViewById(R.id.activity_weather_tv_zhuozhuang);
			activity_weather_tv_ziwaixian = (TextView) layout.findViewById(R.id.activity_weather_tv_ziwaixian);

			activity_weather_tv_week_first = (TextView) layout.findViewById(R.id.activity_weather_tv_week_first);
			activity_weather_iv_tianqi_first = (ImageView) layout.findViewById(R.id.activity_weather_iv_tianqi_first);
			activity_weather_tv_tianqi_first = (TextView) layout.findViewById(R.id.activity_weather_tv_tianqi_first);
			activity_weather_tv_dushu_first = (TextView) layout.findViewById(R.id.activity_weather_tv_dushu_first);

			activity_weather_tv_week_second = (TextView) layout.findViewById(R.id.activity_weather_tv_week_second);
			activity_weather_iv_tianqi_second = (ImageView) layout.findViewById(R.id.activity_weather_iv_tianqi_second);
			activity_weather_tv_tianqi_second = (TextView) layout.findViewById(R.id.activity_weather_tv_tianqi_second);
			activity_weather_tv_dushu_second = (TextView) layout.findViewById(R.id.activity_weather_tv_dushu_second);

			activity_weather_tv_week_three = (TextView) layout.findViewById(R.id.activity_weather_tv_week_three);
			activity_weather_iv_tianqi_three = (ImageView) layout.findViewById(R.id.activity_weather_iv_tianqi_three);
			activity_weather_tv_tianqi_three = (TextView) layout.findViewById(R.id.activity_weather_tv_tianqi_three);
			activity_weather_tv_dushu_three = (TextView) layout.findViewById(R.id.activity_weather_tv_dushu_three);
			activity_weather_tv_updatetime = (TextView) layout.findViewById(R.id.activity_weather_tv_updatetime);

			pm_tv_bottom = (TextView) layout.findViewById(R.id.activity_weather_tv_pm_bottom);

		}

		/**
		 * 更新每个城市的天气信息
		 * 
		 * @Author: yangxy
		 * @Version: V1.0
		 * @Create Date: 2014-11-12
		 * @param lc
		 */
		public void update(WeatherLocal lc) {

			if (lc==null) {
				return;
			}
			WeatherBean weatherBean = lc.getWeatherBean();
			if (weatherBean==null) {
				return;
			}
			String updateTime = weatherBean.getSt();
			if (lc.isShouldUpdate()) {
				// 更新操作
				reqWeatherData(lc.getCity());
			}
			List<WeatherRepParam> weatherRepParams = weatherBean.getWeather();
			if (weatherRepParams == null) {
				return;
			}
			WeatherRepParam weather0 = weatherRepParams.get(0);
			WeatherRepParam weather1 = weatherRepParams.get(1);
			WeatherRepParam weather2 = weatherRepParams.get(2);
			WeatherRepParam weather3 = weatherRepParams.get(3);
			activity_weather_tv_city.setText(weatherBean.getCity());
			activity_weather_tv_updatetime.setText("更新于 " + updateTime + "(来自ami天气)");
			activity_weather_tv_week.setText(weather0.getXq());

			String pm = weather0.getP();
			activity_weather_tv_pm.setText(pm);
			// pm一个字和多个字的背景不同
			if (pm.length() > 1) {
				activity_weather_tv_pm.setBackgroundResource(R.drawable.pm_bg_top);
				pm_tv_bottom.setBackgroundResource(R.drawable.pm_bg_bottom);
			} else {
				activity_weather_tv_pm.setBackgroundResource(R.drawable.pm_top);
				pm_tv_bottom.setBackgroundResource(R.drawable.pm_bottom);
			}
			activity_weather_tv_wendu_current.setText(weather0.getSkt());
			activity_weather_tv_weather.setText(weather0.getW());
			activity_weather_tv_wendu.setText(weather0.getFc().replace(".0", "") + "°C");
			activity_weather_tv_fengli.setText(weather0.getWd());
			activity_weather_tv_shidu.setText(weather0.getRh() + "%");
			activity_weather_tv_zhuozhuang.setText(weather0.getC());
			activity_weather_tv_ziwaixian.setText(weather0.getZ());

			activity_weather_tv_week_first.setText(weather1.getXq());
			activity_weather_tv_tianqi_first.setText(weather1.getW());
			activity_weather_tv_dushu_first.setText(weather1.getFc().replace(".0", "") + "°C");
			activity_weather_iv_tianqi_first.setBackgroundResource(tianqi_iv_map.get(weather1.getW()));

			activity_weather_tv_week_second.setText(weather2.getXq());
			activity_weather_tv_tianqi_second.setText(weather2.getW());
			activity_weather_tv_dushu_second.setText(weather2.getFc().replace(".0", "") + "°C");
			activity_weather_iv_tianqi_second.setBackgroundResource(tianqi_iv_map.get(weather2.getW()));

			activity_weather_tv_week_three.setText(weather3.getXq());
			activity_weather_tv_tianqi_three.setText(weather3.getW());
			activity_weather_tv_dushu_three.setText(weather3.getFc().replace(".0", "") + "°C");
			activity_weather_iv_tianqi_three.setBackgroundResource(tianqi_iv_map.get(weather3.getW()));
		}

	}

	@Override
	public void onLocated(AMapLocation location) {
		ToastUtil.show(this, "当前所在城市："+location.getCity());
	}
	@Override
	public void onLocatFail(String s) {
		ToastUtil.show(this, s);
	}
}
