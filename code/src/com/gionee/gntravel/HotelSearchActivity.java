package com.gionee.gntravel;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.amap.api.services.geocoder.GeocodeAddress;
import com.amap.api.services.geocoder.GeocodeQuery;
import com.amap.api.services.geocoder.GeocodeResult;
import com.amap.api.services.geocoder.GeocodeSearch;
import com.amap.api.services.geocoder.GeocodeSearch.OnGeocodeSearchListener;
import com.amap.api.services.geocoder.RegeocodeResult;
import com.gionee.gntravel.adapter.SearchAdapter;
import com.gionee.gntravel.adapter.SearchAdapter.OnKeyItemClickListner;
import com.gionee.gntravel.entity.HotelSearchKeyBean;
import com.gionee.gntravel.entity.SearchKeyBean;
import com.gionee.gntravel.utils.FinalString;
import com.gionee.gntravel.utils.GNConnUtil;
import com.gionee.gntravel.utils.GNConnUtil.GNConnListener;
import com.gionee.gntravel.utils.GdUtil;
import com.gionee.gntravel.utils.JSONUtil;
import com.gionee.gntravel.utils.SpUtil;
import com.google.gson.reflect.TypeToken;

/**
 * 
 * @Author: yangxy
 * @Create Date: 2014-6-24
 */
public class HotelSearchActivity extends BaseActivity implements GNConnListener, OnClickListener,
		OnGeocodeSearchListener, OnKeyItemClickListner {
	private EditText hotel_search_et_content;
	private Button activity_hotel_bt_search;
	private String content;
	private ProgressBar pb_search;
	private GNConnUtil post;
	private String FETCH_HOTEL_KEY_URL;
	private ListView activity_hotel_search_lv_names;
	private ListView activity_hotel_search_lv_history;
	private ImageView activity_hotel_search_clearcontent;
	private String cityName;
	private String searchKey;
	private GeocodeSearch geocoderSearch;
	private HotelSearchKeyBean hotelSearchKeyBean;
	private List<HotelSearchKeyBean> hotelSearchKeyBeanList = new ArrayList<HotelSearchKeyBean>();
	private static final int LATLONKEY = 0x1100;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_hotel_search);
		init();
		findViews();
		showHistoryFromXML();
	}

	private void init() {
		FETCH_HOTEL_KEY_URL = getString(R.string.gionee_host) + FinalString.HOTEL_ACTION
				+ "findNameByKeyAndCityName.action";
		cityName = getIntent().getStringExtra("cityName");
		searchKey = getIntent().getStringExtra("searchKey");
		geocoderSearch = new GeocodeSearch(this);
		geocoderSearch.setOnGeocodeSearchListener(this);
	}

	private void showHistoryFromXML() {
		SpUtil sp = new SpUtil(this);
		String json = sp.getString(key_history + cityName);
		ArrayList<HotelSearchKeyBean> list = null;
		if (!TextUtils.isEmpty(json)) {
			list = JSONUtil.getInstance().fromJSON(new TypeToken<ArrayList<HotelSearchKeyBean>>() {
			}.getType(), json);
		}
		if (list != null && list.size() != 0) {
			showHistory(true, list);
		}
	}

	private void findViews() {
		post = new GNConnUtil();
		final SearchKeyBean bean = new SearchKeyBean();
		String cityName_utf8;
		try {
			cityName_utf8 = URLEncoder.encode(cityName, "UTF-8");
			bean.setCityName(cityName_utf8);
		} catch (UnsupportedEncodingException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		post.setGNConnListener(this);
		pb_search = (ProgressBar) findViewById(R.id.pb_search);
		activity_hotel_search_clearcontent = (ImageView) findViewById(R.id.activity_hotel_search_clearcontent);
		hotel_search_et_content = (EditText) findViewById(R.id.activity_hotel_search_et_content);
		if (!TextUtils.isEmpty(searchKey)) {
			hotel_search_et_content.setText(searchKey);
			activity_hotel_search_clearcontent.setVisibility(View.VISIBLE);
		}
		activity_hotel_bt_search = (Button) findViewById(R.id.activity_hotel_bt_search);
		activity_hotel_search_lv_names = (ListView) findViewById(R.id.activity_hotel_search_lv_names);
		activity_hotel_search_lv_history = (ListView) findViewById(R.id.activity_hotel_search_lv_history);
		findViewById(R.id.activity_hotel_search_iv_back).setOnClickListener(this);
		activity_hotel_search_clearcontent.setOnClickListener(this);
		content = hotel_search_et_content.getText().toString();
		if (TextUtils.isEmpty(content)) {
			activity_hotel_search_clearcontent.setVisibility(View.GONE);
		}
		hotel_search_et_content.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				content = hotel_search_et_content.getText().toString();
				try {
					String content_utf8 = URLEncoder.encode(content, "UTF-8");
					bean.setKey(content_utf8);
					if (!TextUtils.isEmpty(content_utf8)) {
						post.doPost(FETCH_HOTEL_KEY_URL, bean);
						pb_search.setVisibility(View.VISIBLE);
						activity_hotel_search_clearcontent.setVisibility(View.VISIBLE);
					} else {
						showHistoryFromXML();
						activity_hotel_search_clearcontent.setVisibility(View.GONE);
					}
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				}
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
			}

			@Override
			public void afterTextChanged(Editable s) {
			}
		});
		activity_hotel_bt_search.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				HotelSearchKeyBean bean = new HotelSearchKeyBean();
				bean.setType(null);
				bean.setName(content);
				bean.setId(null);
				doSearchHotel(bean);
			}
		});
	}

	private void doSearchHotel(HotelSearchKeyBean bean) {
		hotelSearchKeyBean = bean;
		showProgress();
		String flag = bean.getType();
		saveHist(bean);
		if (!TextUtils.isEmpty(flag)) {
			if (flag.equals("zone") || flag.equals("area")) {
				// 如是商业圈或者行政区，需要定位计算距离
				if (!TextUtils.isEmpty(bean.getName())) {
					getLatlon(bean);
				}
			} else if ("brand".equals(flag)||"hotel".equals(flag)) {
				// 品牌，酒店
				Intent i = new Intent();
				i.putExtra("searchKeyBean", hotelSearchKeyBean);
				setResult(FinalString.HOTEL_SEARCHKEY_RESCODE, i);
				finish();
			}
		} else {
			// 自定义关键字
			if (!TextUtils.isEmpty(bean.getName())) {
				getLatlon(bean);
			}else{
				Intent i = new Intent();
				i.putExtra("searchKeyBean", hotelSearchKeyBean);
				setResult(FinalString.HOTEL_SEARCHKEY_RESCODE, i);
				finish();
			}
		}

	}

	/**
	 * 响应地理编码
	 */
	public void getLatlon(final HotelSearchKeyBean bean) {
		GdUtil gdUtil = new GdUtil(this);
		Map<String, String> map = gdUtil.getCityCodeMap();
		String code = map.get(cityName);
		GeocodeQuery query = new GeocodeQuery(bean.getName(), code);// 第一个参数表示地址，第二个参数表示查询城市，中文或者中文全拼，citycode、adcode，
		geocoderSearch.getFromLocationNameAsyn(query);// 设置同步地理编码请求
	}

	@Override
	public void onReqSuc(String requestUrl, String json) {
		try {
			hotelSearchKeyBeanList = JSONUtil.getInstance().fromJSON(new TypeToken<ArrayList<HotelSearchKeyBean>>() {
			}.getType(), json);
			showHistory(false, hotelSearchKeyBeanList);
		} catch (Exception e) {
			e.printStackTrace();
		}
		pb_search.setVisibility(View.GONE);
	}

	private void showHistory(boolean showHistory, List<HotelSearchKeyBean> items) {
		histAdapter = new SearchAdapter(this, items);
		histAdapter.setOnKeyItemClickListner(this);
		if (items == null) {
			return;
		}
		if (!showHistory) {
			activity_hotel_search_lv_history.setVisibility(View.GONE);
			activity_hotel_search_lv_names.setVisibility(View.VISIBLE);
			activity_hotel_search_lv_names.setAdapter(histAdapter);
		} else {
			activity_hotel_search_lv_names.setVisibility(View.GONE);
			activity_hotel_search_lv_history.setVisibility(View.VISIBLE);
			View footer = (View) activity_hotel_search_lv_history.getTag();
			if (footer == null) {
				footer = LayoutInflater.from(this).inflate(R.layout.item_hotel_search_clearhis, null);
				activity_hotel_search_lv_history.setTag(footer);
				activity_hotel_search_lv_history.addFooterView(footer);
				footer.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						SpUtil sp = new SpUtil(HotelSearchActivity.this);
						sp.putString(key_history + cityName, "");
						if (histAdapter != null) {
							histAdapter.clear();
							histAdapter.notifyDataSetChanged();
							activity_hotel_search_lv_history.setVisibility(View.GONE);
						}

					}
				});
			}
			activity_hotel_search_lv_history.setAdapter(histAdapter);
			if (items.size() == 0) {
				footer.setVisibility(View.GONE);
			}
		}
	}

	private String key_history = "search_history";
	private SearchAdapter histAdapter;

	private void saveHist(HotelSearchKeyBean bean) {
		if (bean == null||TextUtils.isEmpty(bean.getName())) {
			return;
		}
		SpUtil sp = new SpUtil(this);
		String json = sp.getString(key_history + cityName);
		ArrayList<HotelSearchKeyBean> list = null;
		if (!TextUtils.isEmpty(json)) {
			list = JSONUtil.getInstance().fromJSON(new TypeToken<ArrayList<HotelSearchKeyBean>>() {
			}.getType(), json);
		} else {
			list = new ArrayList<HotelSearchKeyBean>();
		}
		if (!list.contains(bean)) {
			list.add(0, bean);
			sp.putString(key_history + cityName, JSONUtil.getInstance().toJSON(list));
		}
	}

	@Override
	public void onReqFail(String requestUrl, Exception ex, int errorCode) {
		pb_search.setVisibility(View.GONE);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.activity_hotel_search_clearcontent:
			hotel_search_et_content.setText("");
			break;
		case R.id.activity_hotel_search_iv_back:
			finish();
			break;

		default:
			break;
		}

	}

	@Override
	public void onGeocodeSearched(GeocodeResult result, int rCode) {
		dismissProgress();
		String latlng = "";
		Intent i = new Intent();
		if (rCode == 0) {
			if (result != null && result.getGeocodeAddressList() != null && result.getGeocodeAddressList().size() > 0) {
				GeocodeAddress address = result.getGeocodeAddressList().get(0);
				latlng = address.getLatLonPoint() + "";
				if (!TextUtils.isEmpty(latlng)) {
					i.putExtra("latlng", latlng);
				}
			} else {
				i.putExtra("latlng", "");
//				Toast.makeText(this, R.string.no_result, Toast.LENGTH_LONG).show();
			}

		} else if (rCode == 27) {
			i.putExtra("latlng", "");
			Toast.makeText(this, R.string.error_network, Toast.LENGTH_LONG).show();
		} else if (rCode == 32) {
			i.putExtra("latlng", "");
			Toast.makeText(this, R.string.error_key, Toast.LENGTH_LONG).show();
		} else {
			i.putExtra("latlng", "");
			Toast.makeText(this, R.string.error_other + rCode, Toast.LENGTH_LONG).show();
		}
		i.putExtra("searchKeyBean", hotelSearchKeyBean);
		setResult(FinalString.HOTEL_SEARCHKEY_RESCODE, i);
		finish();
	}

	@Override
	public void onRegeocodeSearched(RegeocodeResult arg0, int arg1) {
	}

	@Override
	public void selectKeyItem(HotelSearchKeyBean bean) {
		doSearchHotel(bean);
	}

}
