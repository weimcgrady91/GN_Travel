package com.gionee.gntravel.fragment;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.PopupWindow.OnDismissListener;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.gionee.gntravel.H5Activity;
import com.gionee.gntravel.HotelDetailActivity;
import com.gionee.gntravel.HotelSearchActivity;
import com.gionee.gntravel.ListOfMessageActivity;
import com.gionee.gntravel.HotelMapActivity;
import com.gionee.gntravel.R;
import com.gionee.gntravel.SelectCityActivity;
import com.gionee.gntravel.TravelApplication;
import com.gionee.gntravel.adapter.ChooseConditionAdapter;
import com.gionee.gntravel.adapter.ChooseConditionAdapter.onSelectListner;
import com.gionee.gntravel.adapter.ChooseHotelAdressAdapter;
import com.gionee.gntravel.adapter.ChooseHotelAdressAdapter.onAddressSelectListner;
import com.gionee.gntravel.adapter.ChooseHotelMultiAdapter;
import com.gionee.gntravel.adapter.ChooseHotelMultiAdapter.onMultiSelectListner;
import com.gionee.gntravel.adapter.ChooseSingleAdapter;
import com.gionee.gntravel.adapter.ChooseSingleAdapter.onFirstClickListner;
import com.gionee.gntravel.adapter.ChooseSingleAdapter.onSingleSelectListner;
import com.gionee.gntravel.adapter.HotelListAdapter;
import com.gionee.gntravel.entity.Area;
import com.gionee.gntravel.entity.Brand;
import com.gionee.gntravel.entity.Hotel;
import com.gionee.gntravel.entity.HotelInfoRequest;
import com.gionee.gntravel.entity.HotelSearchKeyBean;
import com.gionee.gntravel.entity.Zone;
import com.gionee.gntravel.fragment.DateSelectorFragment.DateSelectedListener;
import com.gionee.gntravel.utils.FinalString;
import com.gionee.gntravel.utils.JSONUtil;
import com.gionee.gntravel.utils.NetWorkUtil;
import com.gionee.gntravel.utils.OnBack;
import com.gionee.gntravel.utils.SpUtil;
import com.google.gson.reflect.TypeToken;
import com.youju.statistics.YouJuAgent;

public class HotelListFragment extends BaseFragment implements DateSelectedListener, AdapterView.OnItemClickListener,
		OnClickListener, onSelectListner, onSingleSelectListner, onMultiSelectListner, onAddressSelectListner,
		OnScrollListener, OnBack {
	private ListOfMessageActivity parentActivity;
	private static final String DEFALT_PRICE_ASC = "priceAsc";
	private static final String DEFALT_PRICE_DESC = "priceDesc";
	private static final String DEFALT_DISTANCE_ASC = "distanceAsc";
	private static final String DEFALT_COMM_DESC = "commDesc";
	private static final String KEY_ZONE = "zone";
	private static final String KEY_AREA = "area";
	private static final String KEY_BRAND = "brand";
	private static final String KEY_STAR = "star";
	private static final String kEY_DISTANCE = "distance";;
	private DateSelectorFragment dateSelectorFragment;
	private ListView hotel_lv;
	private List<Hotel> list = new ArrayList<Hotel>();
	public static String FETCH_HOTELLIST_URL;
	public static String FETCH_BRAND_URL;
	public static String FETCH_AREA_URL;
	public static String FETCH_ZONE_URL;
	private String fetchzoneurl;
	private String fetchareaurl;
	public static final int PAGESIZE = 10;
	private String order = "asc";
	private RelativeLayout orderbyprice;
	private HotelListAdapter adapter;
	private LinearLayout bottom;
	private ImageView fragment_list_hotel_iv_defalt;
	private ImageView fragment_list_hotel_iv_price;
	private ImageView fragment_list_hotel_iv_screening;
	private RelativeLayout orderbydefalt;
	private RelativeLayout screening;
	private PopupWindow mPopupWindow;
	private View view;
	private int mPosition = 0;
	private int listviewCount;
	private int lastItem;
	private List<Hotel> finalList = new ArrayList<Hotel>();
	public static List<String> hotel_filter_List = new ArrayList<String>();
	public static List<String> hotel_address_List = new ArrayList<String>();
	public static List<String> hotel_star_List = new ArrayList<String>();
	public static List<String> hotel_distance_List = new ArrayList<String>();
	public static List<String> hotel_zone_List = new ArrayList<String>();
	public static List<String> hotel_area_List = new ArrayList<String>();
	public static List<String> hotel_brand_List = new ArrayList<String>();
	public static List<String> hotel_price_List = new ArrayList<String>();
	private RelativeLayout address_ll;
	private RelativeLayout brand_ll;
	private RelativeLayout star_ll;
	private RelativeLayout distance_ll;
	private View popupView;
	private View moreView;
	private String current_flag = "";
	private LinearLayout window_filter_ll_condition;
	private TextView window_filter_bt_cancle;
	private TextView window_filter_bt_clear;
	private TextView window_filter_bt_sure;
	private ChooseHotelMultiAdapter adapterMulty;
	private int currentSelectedIndex;
	public static int page = 1;
	public static HotelInfoRequest hotelInfoRequest;
	private TextView hotel_et_search;
	private TextView fragment_hotel_tv_defalt;
	private TextView fragment_hotel_tv_price;
	private ImageView fragment_hotel_iv_arrowdefalt;
	private TextView fragment_hotel_tv_filter;
	private Integer tabSelectFlag = 1;// 1:默认排序 2：价格 3：筛选
	private String date;
	private static String cityName;
	private String initDate;
	private View fragment_list_hotel_calendar_ll_bg;
	private RelativeLayout rl_failed;
	private ImageView iv_failed;
	private RelativeLayout rl_loading;
	private ImageView img_loading;
	private boolean isPopDismissing;

	public static Map<String, String> tempConditionMap = new HashMap<String, String>();
	public static Map<String, String> conditionMap = new HashMap<String, String>();

	public static HotelListFragment newInstance() {
		HotelListFragment hotelListFragment = new HotelListFragment();
		Bundle args = new Bundle();
		hotelListFragment.setArguments(args);
		return hotelListFragment;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = findViews(inflater);
		// 初始化数据
		initurl();
		initHotel();
		app = (TravelApplication) this.getActivity().getApplication();
		return view;
	}

	private void initurl() {
		String url_head = getString(R.string.gionee_host) + FinalString.HOTEL_ACTION;
		FETCH_HOTELLIST_URL = url_head + "findhotelList.action";
		FETCH_BRAND_URL = url_head + "brandList.action";
		FETCH_AREA_URL = url_head + "findAreaByCityName.action";
		FETCH_ZONE_URL = url_head + "findZoonByCityName.action";
	}

	private static final int INITCALENDAR = 0x1004;
	private ResultHandler handler = new ResultHandler();
	private ImageView fragment_hotel_list_iv_filterbiao;
	private TextView fragmeng_hotel_tv_citylist;
	public static boolean needResume;
	private boolean needLoad = true;
	private boolean clearBrand = false;
	private TextView btn_refresh;
	private static int CITY_REQUEST_CODE = 0x200;

	private class ResultHandler extends Handler {

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case INITCALENDAR:
				if (getActivity() == null) {
					return;
				}
				replaceDateSelectFragment();
				break;
			default:
				break;
			}
		}
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		initAddressData();
		initDistanceData();
		initStarData();
		initPriceData();
		initDefaltConditionData();
		clearCondition();
		// loadHotel();
		Message msg = Message.obtain();
		msg.what = INITCALENDAR;
		handler.sendMessageDelayed(msg, 500);
		super.onActivityCreated(savedInstanceState);
	}

	private void initDefaltConditionData() {
		defaltList = new ArrayList<String>();
		defaltList.add("默认排序");
		defaltList.add("评价由高到低");
		defaltList.add("价格由高到低");
		defaltList.add("价格由低到高");
	}

	private void initPriceData() {
		hotel_price_List.clear();
		hotel_price_List.add("不限");
		hotel_price_List.add("150元以下");
		hotel_price_List.add("151-300元");
		hotel_price_List.add("301-450元");
		hotel_price_List.add("451-600元");
		hotel_price_List.add("601-1000元");
		hotel_price_List.add("1000元以上");
	}

	private void initHotel() {
		hotelInfoRequest = new HotelInfoRequest();
		hotelsearchKeybean = new HotelSearchKeyBean();
		initDate = getActivity().getIntent().getStringExtra("TakeOffTime");
		cityName = getActivity().getIntent().getStringExtra("arriveCity");
		fragmeng_hotel_tv_citylist.setText(cityName);
		try {
			String cityName_utf8 = URLEncoder.encode(cityName, "utf-8");
			hotelInfoRequest.setCityName(cityName_utf8);
		} catch (UnsupportedEncodingException e1) {
			e1.printStackTrace();
		}
		try {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			Date d = sdf.parse(initDate);
			SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy/MM/dd");
			date = sdf1.format(d);
			hotelInfoRequest.setDate(date);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		page = 1;
		hotelInfoRequest.setPage(page);
		hotelInfoRequest.setPageSize(PAGESIZE);
	}

	private void showCalendar(final boolean show) {
		// add by weiqun
		if (dateSelectorFragment == null)
			return;
		// add end weiqun
		final View cldView = dateSelectorFragment.getCalendarView();
		final View fragment_ll_calendar = dateSelectorFragment.getFragment_ll_calendar();
		if (show) {
			fragment_list_hotel_calendar_ll_bg.setVisibility(View.VISIBLE);
			Animation a_in = AnimationUtils.loadAnimation(getActivity(), R.anim.hotel_calendar_in);
			fragment_ll_calendar.setVisibility(View.VISIBLE);
			cldView.startAnimation(a_in);
		} else {
			final Animation a_out = AnimationUtils.loadAnimation(getActivity(), R.anim.hotel_calendar_out);
			a_out.setAnimationListener(new AnimationListener() {

				@Override
				public void onAnimationStart(Animation animation) {
				}

				@Override
				public void onAnimationRepeat(Animation animation) {
				}

				@Override
				public void onAnimationEnd(Animation animation) {
					fragment_list_hotel_calendar_ll_bg.setVisibility(View.INVISIBLE);
					fragment_ll_calendar.setVisibility(View.GONE);
					parentActivity.setScanScroll(true);
				}
			});
			cldView.startAnimation(a_out);
		}
	}

	public static boolean isLocationCity() {
		String locationCityName = app.getLocationCity();
		if (!TextUtils.isEmpty(cityName) && !TextUtils.isEmpty(locationCityName)
				&& (cityName + "市").equals(locationCityName)) {
			return true;
		}
		return false;
	}

	private View findViews(LayoutInflater inflater) {
		view = inflater.inflate(R.layout.fragment_list_hotel, null);
		fragmeng_hotel_tv_citylist = (TextView) view.findViewById(R.id.fragmeng_hotel_tv_citylist);
		fragmeng_hotel_tv_citylist.setOnClickListener(this);
		fragmeng_hotel_tv_citylist.setFocusable(true);
		rl_failed = (RelativeLayout) view.findViewById(R.id.rl_failed);
		iv_failed = (ImageView) view.findViewById(R.id.iv_failed);
		rl_failed.setOnClickListener(this);
		rl_loading = (RelativeLayout) view.findViewById(R.id.rl_loading);
		img_loading = (ImageView) view.findViewById(R.id.img_loading);
		btn_refresh = (TextView) view.findViewById(R.id.btn_refresh);
		fragment_hotel_list_iv_filterbiao = (ImageView) view.findViewById(R.id.fragment_hotel_list_iv_filterbiao);
		moreView = getActivity().getLayoutInflater().inflate(R.layout.load, null);
		more_progress_img = (ImageView) moreView.findViewById(R.id.hotelmore_pb_img);
		fragment_list_hotel_calendar_ll_bg = view.findViewById(R.id.fragment_list_hotel_calendar_ll_bg);
		fragment_list_hotel_calendar_ll_bg.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				dateSelectorFragment.doTodayClick();
			}
		});
		hotel_bt_map = (Button) view.findViewById(R.id.hotel_bt_map);
		hotel_lv = (ListView) view.findViewById(R.id.hotel_lv);
		hotel_lv.addFooterView(moreView);
		moreView.setClickable(false);
		adapter = new HotelListAdapter(list, getActivity());
		hotel_lv.setAdapter(adapter);
		hotel_lv.removeFooterView(moreView);
		orderbyprice = (RelativeLayout) view.findViewById(R.id.orderbyprice);
		orderbydefalt = (RelativeLayout) view.findViewById(R.id.orderbydefalt);
		screening = (RelativeLayout) view.findViewById(R.id.screening);
		bottom = (LinearLayout) view.findViewById(R.id.bottom);
		hotel_bt_map.setFocusable(true);
		hotel_bt_map.setOnClickListener(this);
		orderbyprice.setOnClickListener(this);
		orderbyprice.setFocusable(true);
		screening.setOnClickListener(this);
		screening.setFocusable(true);
		orderbydefalt.setOnClickListener(this);
		orderbydefalt.setFocusable(true);
		hotel_lv.setOnItemClickListener(this);
		hotel_lv.setOnScrollListener(this);
		fragment_list_hotel_iv_defalt = (ImageView) view.findViewById(R.id.fragment_list_hotel_iv_defalt);
		fragment_list_hotel_iv_price = (ImageView) view.findViewById(R.id.fragment_list_hotel_iv_price);
		fragment_list_hotel_iv_screening = (ImageView) view.findViewById(R.id.fragment_list_hotel_iv_screening);
		fragment_hotel_iv_arrowdefalt = (ImageView) view.findViewById(R.id.fragment_hotel_iv_arrowdefalt);
		fragment_hotel_tv_defalt = (TextView) view.findViewById(R.id.fragment_hotel_tv_defalt);
		fragment_hotel_tv_price = (TextView) view.findViewById(R.id.fragment_hotel_tv_price);
		fragment_hotel_tv_filter = (TextView) view.findViewById(R.id.fragment_hotel_tv_filter);
		hotel_et_search = (TextView) view.findViewById(R.id.hotel_et_search);
		hotel_et_search.setOnClickListener(this);
		hotel_et_search.setFocusable(true);
		// 添加日期控件
		return view;
	}

	private void startProgressDialog() {
		hotel_lv.setEnabled(false);
		rl_failed.setVisibility(View.GONE);
		hotel_lv.setVisibility(View.VISIBLE);
		rl_loading.setVisibility(View.VISIBLE);
		bottom.setClickable(false);
		AnimationDrawable animationDrawable = (AnimationDrawable) img_loading.getBackground();
		animationDrawable.start();
	}

	private void stopProgressDialog() {
		bottom.setClickable(true);
		if (hotel_lv != null) {
			hotel_lv.setEnabled(true);
		}
		if (img_loading != null) {
			AnimationDrawable animationDrawable = (AnimationDrawable) img_loading.getBackground();
			animationDrawable.stop();
		}
		if (rl_loading != null) {
			rl_loading.setVisibility(View.GONE);
		}
	}

	private void loadHotel() {
		if (TextUtils.isEmpty(hotelInfoRequest.getCityName())) {
			cityName = getActivity().getIntent().getStringExtra("arriveCity");
			try {
				String cityName_utf8 = URLEncoder.encode(cityName, "utf-8");
				hotelInfoRequest.setCityName(cityName_utf8);
			} catch (UnsupportedEncodingException e1) {
				e1.printStackTrace();
			}
		}
		startProgressDialog();
		new Handler().post(new Runnable() {

			@Override
			public void run() {
				if (!NetWorkUtil.isNetworkConnected(getActivity())) {
					showNetErrorMsg();
					return;
				}
				// 当前城市，并且没有选择具体的地址，需要计算距离我的距离
				if (isLocationCity() && !isSearchAddress) {
					double lat = app.getLocation().getLatitude();
					double lng = app.getLocation().getLongitude();
					hotelInfoRequest.setLat(lat + "");
					hotelInfoRequest.setLng(lng + "");
				}
				HashMap<String, String> params = new HashMap<String, String>();
				if (hotelsearchKeybean != null) {
					String hotelsearchKeybeanJson = JSONUtil.getInstance().toJSON(hotelsearchKeybean);
					params.put("hotelsearchKeybean", hotelsearchKeybeanJson);
				}
				if (hotelInfoRequest != null) {
					String hotelInfoRequestJson = JSONUtil.getInstance().toJSON(hotelInfoRequest);
					params.put("hotelInfoRequest", hotelInfoRequestJson);
				}
				post.doPost(FETCH_HOTELLIST_URL, params);
			}
		});
	}

	@SuppressWarnings("unchecked")
	@Override
	public void onReqSuc(String requestUrl, String json) {
		super.onReqSuc(requestUrl, json);
		if (hotel_lv.getFooterViewsCount() > 0) {
			dissmissPb(more_progress_img);
			hotel_lv.removeFooterView(moreView); // 移除底部视图
		}
		if (requestUrl.equals(fechBrandUrl)) {
			inFetchingBrandData = false;
			dissmissPb(window_filter_pb);
			window_filter_brand_lv.setVisibility(View.VISIBLE);
			// 持久化json
			SpUtil sp = new SpUtil(getActivity());
			sp.putString(KEY_BRAND + cityName, json);
			updateBrandPopWindow(json);
		}
		if ((requestUrl).equals(fetchzoneurl)) {
			inFetchingZoneData = false;
			dissmissPb(window_filter_pb);
			window_filter_address_lv.setVisibility(View.VISIBLE);
			// 持久化json
			SpUtil sp = new SpUtil(getActivity());
			sp.putString(KEY_ZONE + cityName, json);

			updateZoomPopWindow(json);
		}
		if (requestUrl.equals(fetchareaurl)) {
			inFetchingAreaData = false;
			dissmissPb(window_filter_pb);
			window_filter_area_lv.setVisibility(View.VISIBLE);
			// 持久化json
			SpUtil sp = new SpUtil(getActivity());
			sp.putString(KEY_AREA + cityName, json);
			updateAreaPopWindow(json);
		}
		if (requestUrl.equals(FETCH_HOTELLIST_URL)) {
			stopProgressDialog();
			finalList = JSONUtil.getInstance().fromJSON(new TypeToken<ArrayList<Hotel>>() {
			}.getType(), json);

			if (shouldClearList) {
				list.clear();
			}
			list.addAll(finalList);
			listviewCount = list.size();
			adapter.notifyDataSetChanged();
			changeTabStatus();
			if (!needLoad) {
				needLoad = true;
			}
		}
	}

	private void clearBg() {
		address_ll.setBackgroundResource(0);
		brand_ll.setBackgroundResource(0);
		star_ll.setBackgroundResource(0);
		distance_ll.setBackgroundResource(0);
		distance_line.setVisibility(View.GONE);
		address_line.setVisibility(View.GONE);
		brand_line.setVisibility(View.GONE);
		star_line.setVisibility(View.GONE);
	}

	private void updateZoomPopWindow(String json) {
		zones = JSONUtil.getInstance().fromJSON(new TypeToken<ArrayList<Zone>>() {
		}.getType(), json);
		if (zones == null || zones.size() == 0) {
			return;
		}
		Zone zone = new Zone();
		zone.setZoneName("不限");
		hotel_zone_List.clear();
		zones.add(0, zone);
		Zone zone1 = new Zone();
		zone1.setZoneName("商业圈");
		zones.add(0, zone1);
		for (Zone business : zones) {
			hotel_zone_List.add(business.getZoneName());
		}
		showSingleList(KEY_ZONE, false, new onFirstClickListner() {

			@Override
			public void onClick(View view) {
				filterSelect(currentSelectedIndex);
			}
		});
	}

	private void updateBrandPopWindow(String json) {
		brands = JSONUtil.getInstance().fromJSON(new TypeToken<ArrayList<Brand>>() {
		}.getType(), json);

		if (brands == null || brands.size() == 0) {
			return;
		}

		Brand brand = new Brand();
		brand.setBrandName("不限");
		brands.add(0, brand);
		hotel_brand_List.clear();
		for (Brand b : brands) {
			hotel_brand_List.add(b.getBrandName());
		}
		adapterMulty = new ChooseHotelMultiAdapter(getActivity(), brands);
		if (clearBrand) {
			adapterMulty.removeCondition(0);
			clearBrand = false;
		}
		if (mPopupWindow != null && mPopupWindow.isShowing() && current_flag.equals(KEY_BRAND)) {
			window_filter_brand_lv.setAdapter(adapterMulty);
			adapterMulty.setOnMultiSelectListner(this);
		}

	}

	public void showFailedMsg() {
		dismissProgress();
		rl_failed.setVisibility(View.VISIBLE);
		hotel_lv.setVisibility(View.GONE);
	}

	private void showNotFoundMsg() {
		stopProgressDialog();
		if (rl_loading != null && getActivity() != null) {
			iv_failed.setBackground(getActivity().getResources().getDrawable(R.drawable.face));
			btn_refresh.setText(getResources().getString(R.string.not_find));
			rl_failed.setVisibility(View.VISIBLE);
		}
		if (hotel_lv != null) {
			hotel_lv.setVisibility(View.GONE);
		}
	}

	public void showNetErrorMsg() {
		hotel_lv.setVisibility(View.GONE);
		stopProgressDialog();
		iv_failed.setBackground(getActivity().getResources().getDrawable(R.drawable.wifi));
		btn_refresh.setText(getResources().getString(R.string.net_refresh));
		rl_failed.setVisibility(View.VISIBLE);
	}

	@Override
	public void onReqFail(String requestUrl, Exception ex, int errorCode) {
		super.onReqFail(requestUrl, ex, errorCode);
		if (requestUrl.equals(FETCH_HOTELLIST_URL)) {
			// errorCode == 2说明返回数据是null
			if (shouldClearList) {
				list.clear();
			}
			adapter.notifyDataSetChanged();
			changeTabStatus();
			hotel_lv.removeFooterView(moreView); // 移除底部视图
			if (page > 1) {
				Toast.makeText(getActivity(), "没有更多酒店了！", Toast.LENGTH_LONG).show();
				stopProgressDialog();
				return;
			}
			if (!needLoad) {
				needLoad = true;
			}
			showNotFoundMsg();
			stopProgressDialog();
		}

		if (requestUrl.equals(fechBrandUrl)) {
			inFetchingBrandData = false;
			dissmissPb(window_filter_pb);
			window_filter_brand_lv.setVisibility(View.VISIBLE);
		}
		if (requestUrl.equals(fetchzoneurl)) {
			inFetchingZoneData = false;
			dissmissPb(window_filter_pb);
			window_filter_address_lv.setVisibility(View.VISIBLE);
			if ("return cotent of BaseBean is empty".equals(ex.getMessage())) {
				Toast.makeText(getActivity(), "该城市无商业圈", 0).show();
			}
		}
		if (requestUrl.equals(fetchareaurl)) {
			inFetchingAreaData = false;
			dissmissPb(window_filter_pb);
			window_filter_address_lv.setVisibility(View.VISIBLE);
			if ("return cotent of BaseBean is empty".equals(ex.getMessage())) {
				Toast.makeText(getActivity(), "该城市无行政区", 0).show();
			}
		}
	}

	private void changeTabStatus() {
		if (!TextUtils.isEmpty(defaltCondition)) {
			fragment_hotel_iv_arrowdefalt.setVisibility(View.VISIBLE);
			if (defaltCondition.equals(DEFALT_COMM_DESC)) {
				fragment_hotel_tv_defalt.setText("评价");
				if (tabSelectFlag == 1) {
					fragment_hotel_iv_arrowdefalt.setBackground(getResources()
							.getDrawable(R.drawable.desc_arrow_select));
				} else {
					fragment_hotel_iv_arrowdefalt.setBackground(getResources()
							.getDrawable(R.drawable.desc_arrow_normal));
				}
			} else if (defaltCondition.equals(DEFALT_DISTANCE_ASC)) {
				fragment_hotel_tv_defalt.setText("距离");
				if (tabSelectFlag == 1) {
					fragment_hotel_iv_arrowdefalt
							.setBackground(getResources().getDrawable(R.drawable.asc_arrow_select));
				} else {
					fragment_hotel_iv_arrowdefalt
							.setBackground(getResources().getDrawable(R.drawable.asc_arrow_normal));
				}
			} else if (defaltCondition.equals(DEFALT_PRICE_ASC)) {
				fragment_hotel_tv_defalt.setText("价格");
				if (tabSelectFlag == 1) {
					fragment_hotel_iv_arrowdefalt
							.setBackground(getResources().getDrawable(R.drawable.asc_arrow_select));
				} else {
					fragment_hotel_iv_arrowdefalt
							.setBackground(getResources().getDrawable(R.drawable.asc_arrow_normal));
				}
			} else if (defaltCondition.equals(DEFALT_PRICE_DESC)) {
				fragment_hotel_tv_defalt.setText("价格");
				if (tabSelectFlag == 1) {
					fragment_hotel_iv_arrowdefalt.setBackground(getResources()
							.getDrawable(R.drawable.desc_arrow_select));
				} else {
					fragment_hotel_iv_arrowdefalt.setBackground(getResources()
							.getDrawable(R.drawable.desc_arrow_normal));
				}
			}
		} else {
			fragment_hotel_tv_defalt.setText("默认排序");
			fragment_hotel_iv_arrowdefalt.setVisibility(View.GONE);
		}
		if (!TextUtils.isEmpty(min_price) && !TextUtils.isEmpty(max_price)) {
			if (min_price.equals("0")) {
				fragment_hotel_tv_price.setText("150元以下");
			} else if (max_price != null) {
				fragment_hotel_tv_price.setText(min_price + "-" + max_price + "元");
			}
		} else if (!TextUtils.isEmpty(min_price) && TextUtils.isEmpty(max_price)) {
			fragment_hotel_tv_price.setText(min_price + "元以上");
		} else {
			fragment_hotel_tv_price.setText("价格不限");
		}
		changeTabBg(tabSelectFlag);
	}

	// 增加日期控件
	private void replaceDateSelectFragment() {
		Bundle bd = new Bundle();
		bd.putLong("currentDate", new Date().getTime());
		bd.putString("date", initDate);
		dateSelectorFragment = (DateSelectorFragment) Fragment.instantiate(getActivity(),
				DateSelectorFragment.class.getName(), bd);
		dateSelectorFragment.setOnTodayClick(new Runnable() {

			@Override
			public void run() {
				showCalendar(dateSelectorFragment.isVisiable());
				parentActivity.setScanScroll(false);
			}
		});

		getActivity().getSupportFragmentManager().beginTransaction()
				.replace(R.id.hotel_ll_dateselector_holder, dateSelectorFragment).commit();

		dateSelectorFragment.setDateSelectedListener(this);
	}

	@Override
	public void onDateChange(Date newDate, String stringNewDate) {
		if (hotelInfoRequest == null) {
			hotelInfoRequest = new HotelInfoRequest();
			page = 1;
			hotelInfoRequest.setPage(page);
			hotelInfoRequest.setPageSize(PAGESIZE);
			try {
				String cityName_utf8 = URLEncoder.encode(cityName, "utf-8");
				hotelInfoRequest.setCityName(cityName_utf8);
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
		}
		Calendar cld = Calendar.getInstance();
		cld.setTime(newDate);
		int year = cld.get(Calendar.YEAR);
		currentDate = stringNewDate;
		date = year + "/" + stringNewDate;
		hotelInfoRequest.setDate(date);
		shouldClearList = true;
		page = 1;
		hotelInfoRequest.setPage(page);
		loadHotel();
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		if (position > 0) {
			Intent intent = new Intent();
			intent.setClass(getActivity(), HotelDetailActivity.class);
			Bundle bundle = new Bundle();
			bundle.putSerializable("hotelInfo", (Serializable) list.get(position - 1));
			bundle.putString("currentDate", date);
			intent.putExtras(bundle);
			intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
			this.startActivity(intent);
			YouJuAgent.onEvent(getActivity(), getActivity().getString(R.string.youju_hoteldetails));
		} else {
			loadH5Activity(getString(R.string.tujia_url), getString(R.string.tujia));
		}
	}

	public void loadH5Activity(String url, String title) {
		Intent intent = new Intent();
		intent.putExtra("url", url);
		intent.putExtra("title", title);
		intent.setClass(getActivity(), H5Activity.class);
		startActivity(intent);
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == FinalString.HOTEL_SEARCHKEY_REQCODE && resultCode == FinalString.HOTEL_SEARCHKEY_RESCODE) {
			hotelInfoRequest = new HotelInfoRequest();
			hotelsearchKeybean = (HotelSearchKeyBean) data.getSerializableExtra("searchKeyBean");
			latlng = data.getStringExtra("latlng");
			needLoad = true;
			String key = hotelsearchKeybean.getName();
			if (!TextUtils.isEmpty(key)) {
				try {
					String key_utf8_decode = URLDecoder.decode(key, "utf-8");
					hotel_et_search.setText(key_utf8_decode);
					String key_utf8_encode = URLEncoder.encode(key, "utf-8");
					hotelsearchKeybean.setName(key_utf8_encode);

				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				}
			} else {
				hotel_et_search.setText("");
			}

			if (TextUtils.isEmpty(key)) {
				hotelsearchKeybean = null;
			} else {
				String flag = hotelsearchKeybean.getType();
				if (TextUtils.isEmpty(flag)) {
					try {
						hotelInfoRequest.setKey(URLEncoder.encode(key, "utf-8"));
					} catch (UnsupportedEncodingException e) {
						e.printStackTrace();
					}
				} else {
					hotelInfoRequest.setKey(null);
				}
			}
			if (!TextUtils.isEmpty(latlng)) {
				isSearchAddress = true;
				String[] latlngs = latlng.split(",");
				String lat = latlngs[0];
				String lng = latlngs[1];
				hotelInfoRequest.setLat(lat);
				hotelInfoRequest.setLng(lng);
			} else {
				isSearchAddress = false;
				hotelInfoRequest.setLat(null);
				hotelInfoRequest.setLng(null);
			}
			hotelInfoRequest.setDate(date);
			try {
				String cityName_utf8 = URLEncoder.encode(cityName, "utf-8");
				hotelInfoRequest.setCityName(cityName_utf8);
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
			page = 1;
			hotelInfoRequest.setPage(page);
			hotelInfoRequest.setPageSize(PAGESIZE);
			shouldClearList = true;
			clearCondition();
		}
		if (resultCode == getActivity().RESULT_OK) {
			hotelsearchKeybean = new HotelSearchKeyBean();
			hotel_et_search.setText("");
			shouldClearList = true;
			needLoad = false;
			cityName = data.getStringExtra("cityName");
			fragmeng_hotel_tv_citylist.setText(cityName);
			clearCondition();
			clearBrand = true;
			hotelInfoRequest = new HotelInfoRequest();
			try {
				String cityName_utf8 = URLEncoder.encode(cityName, "UTF-8");
				hotelInfoRequest.setCityName(cityName_utf8);
			} catch (UnsupportedEncodingException e1) {
				e1.printStackTrace();
			}
			hotelInfoRequest.setDate(date);
			page = 1;
			hotelInfoRequest.setPage(page);
			hotelInfoRequest.setPageSize(PAGESIZE);
			loadHotel();
		}
	}

	private void clearCondition() {
		// 清除默认筛选条件
		defaltCondition = null;
		selectDefalt(0);
		// 清除价格筛选条件
		tabSelectFlag = 1;
		selectPrice(0);
		// 清除筛选条件
		conditionMap.clear();
		if (brands != null && brands.size() != 0) {
			if (adapterMulty == null) {
				adapterMulty = new ChooseHotelMultiAdapter(getActivity(), brands);
			}
			adapterMulty.clickFirst();
			adapterMulty.removeCondition(0);
		}
	}

	public void selectCity(SelectCityActivity.SelectCityType selectType, int requestCode) {
		Intent intent = new Intent(getActivity(), SelectCityActivity.class);
		intent.putExtra("selectType", selectType);
		startActivityForResult(intent, requestCode);
	}

	@SuppressWarnings("unchecked")
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.fragmeng_hotel_tv_citylist:
			selectCity(SelectCityActivity.SelectCityType.SELECTCITY, CITY_REQUEST_CODE);
			break;
		case R.id.orderbydefalt:
			orderbydefalt.setClickable(false);
			showSelectDefa();
			tabSelectFlag = 1;
			break;
		case R.id.hotel_et_search:
			Intent searchIntent = new Intent(getActivity(), HotelSearchActivity.class);
			String searchKey = "";
			searchKey = hotel_et_search.getText().toString();
			searchIntent.putExtra("searchKey", searchKey);
			searchIntent.putExtra("cityName", cityName);
			startActivityForResult(searchIntent, FinalString.HOTEL_SEARCHKEY_REQCODE);
			break;
		case R.id.hotel_bt_map:
			Intent i_map = new Intent();
			i_map.putExtra("hotelInfo", (Serializable) list);
			i_map.putExtra("page", page);
			i_map.putExtra("cityName", cityName);
			i_map.putExtra("date", date);
			i_map.putExtra("hotel_searchKeyBean", hotelsearchKeybean);
			i_map.setClass(getActivity(), HotelMapActivity.class);
			startActivity(i_map);
			break;
		case R.id.orderbyprice:
			orderbyprice.setClickable(false);
			showSelectPrice();
			tabSelectFlag = 2;
			break;
		case R.id.screening:
			screening.setClickable(false);
			showFilter();
			tabSelectFlag = 3;
			break;
		case R.id.address_ll:
			// 酒店位置
			filterSelect(0);
			break;
		case R.id.brand_ll:
			// 酒店品牌
			filterSelect(1);
			break;
		case R.id.star_ll:
			// 酒店星级
			filterSelect(2);
			break;
		case R.id.distance_ll:
			// 酒店距离
			filterSelect(3);
			break;
		case R.id.window_filter_bt_cancle:
			popwindowDismiss();
			break;
		case R.id.window_filter_bt_clear:
			if (brands != null && brands.size() != 0) {
				if (adapterMulty == null) {
					adapterMulty = new ChooseHotelMultiAdapter(getActivity(), brands);
				}
				adapterMulty.clickFirst();
				adapterMulty.removeCondition(0);
			}
			window_filter_ll_condition.removeAllViews();
			window_filter_ll_condition.setVisibility(View.GONE);

			resetConditionPosition();
			if (tempConditionMap != null && tempConditionMap.size() > 0) {
				tempConditionMap.clear();
			}
			filterSelect(currentSelectedIndex);
			break;
		case R.id.window_filter_bt_sure:
			wrapHotelInfo();
			loadHotel();
			popwindowDismiss();
			break;
		case R.id.rl_failed:
			loadHotel();
			break;
		}
	}

	private void resetConditionPosition() {
		areaPosition = 1;
		zonePosition = 1;
		starPosition = 0;
		distancePosition = 0;
	}

	public static void wrapHotelInfo() {
		shouldClearList = true;
		page = 1;
		conditionMap.clear();
		hotelInfoRequest.setZoneId(null);
		hotelInfoRequest.setAreaId(null);
		hotelInfoRequest.setBrandId(null);
		hotelInfoRequest.setHotelStarRate(0);
		hotelInfoRequest.setDistance(0);
		for (Iterator it = tempConditionMap.entrySet().iterator(); it.hasNext();) {
			Map.Entry entry = (Entry) it.next();
			String key = entry.getKey().toString();
			String value = entry.getValue().toString();
			if ((KEY_ZONE.equals(key) && "0".equals(value)) || (KEY_AREA.equals(key) && "0".equals(value))) {
				continue;
			}
			if ((KEY_ZONE.equals(key) && "1".equals(value)) || (KEY_AREA.equals(key) && "1".equals(value))) {
				continue;
			}
			conditionMap.put(key, value);
			if (KEY_ZONE.equals(key)) {
				int v = Integer.parseInt(value);
				Zone z = zones.get(v);
				String zoneid = z.getZone();
				hotelInfoRequest.setZoneId(zoneid);
			}
			if (KEY_AREA.equals(key)) {
				int v = Integer.parseInt(value);
				Area area = areas.get(v);
				String areaId = area.getLocation();
				hotelInfoRequest.setAreaId(areaId);
			}
			if (KEY_BRAND.equals(key)) {
				String[] strs = value.split(",");
				String brandId = "";
				for (int i = 0; i < strs.length; i++) {
					int index = Integer.parseInt(strs[i]);
					brandId = brandId + brands.get(index).getBrandCode() + ",";
				}
				brandId = brandId.substring(0, brandId.length() - 1);
				// if (hotelsearchKeybean!=null) {
				// String flag = hotelsearchKeybean.getType();
				// if (!TextUtils.isEmpty(flag)&&flag.equals("brand")) {
				// String brandId_search = hotelsearchKeybean.getId();
				// brandId = brandId+","+brandId_search;
				// }
				// }
				hotelInfoRequest.setBrandId(brandId);
			}
			if (KEY_STAR.equals(key)) {
				Integer po = 0;
				po = Integer.parseInt(value);
				switch (po) {
				case 1:
					po = 5;
					break;
				case 2:
					po = 4;
					break;
				case 3:
					po = 3;
					break;
				case 4:
					po = 2;
					break;
				}
				hotelInfoRequest.setHotelStarRate(po);
			}
			if (kEY_DISTANCE.equals(key)) {
				Integer distance = 0;
				distance = Integer.parseInt(value);
				switch (distance) {
				case 1:
					distance = 500;
					break;
				case 2:
					distance = 1000;
					break;
				case 3:
					distance = 2000;
					break;
				case 4:
					distance = 4000;
					break;
				case 5:
					distance = 8000;
					break;
				}
				// TODO yangxy选择距离的时候 经纬度设置不准确
				// hotelInfoRequest.setLat(cityLat + "");
				// hotelInfoRequest.setLng(cityLng + "");
				hotelInfoRequest.setDistance(distance);
			}
		}
		hotelInfoRequest.setPage(page);
	}

	private void changeTabBg(Integer flag) {
		if (isAdded()) {
			int color_normal = getActivity().getResources().getColor(R.color.text_color_v9);
			int color_focus = getActivity().getResources().getColor(R.color.text_color_v1);
			fragment_hotel_tv_defalt.setTextColor(color_normal);
			fragment_hotel_tv_price.setTextColor(color_normal);
			fragment_hotel_tv_filter.setTextColor(color_normal);
			fragment_list_hotel_iv_defalt.setImageDrawable(getResources().getDrawable(R.drawable.hotel_defalt_normal));
			fragment_list_hotel_iv_price.setImageDrawable(getResources().getDrawable(R.drawable.price_normal));
			fragment_list_hotel_iv_screening.setImageDrawable(getResources().getDrawable(R.drawable.screening_normal));
			switch (flag) {
			case 1:
				fragment_hotel_tv_defalt.setTextColor(color_focus);
				fragment_list_hotel_iv_defalt.setImageDrawable(getResources()
						.getDrawable(R.drawable.hotel_defalt_click));
				break;
			case 2:
				fragment_list_hotel_iv_price.setImageDrawable(getResources().getDrawable(R.drawable.price_selected));
				fragment_hotel_tv_price.setTextColor(color_focus);
				break;
			case 3:
				fragment_list_hotel_iv_screening.setImageDrawable(getResources().getDrawable(
						R.drawable.screening_selected));
				fragment_hotel_tv_filter.setTextColor(color_focus);
				break;
			}
		}
	}

	private void showSelectDefa() {
		if (isSearchAddress || isLocationCity()) {
			String distance_str = "距离由近到远";
			int index = defaltList.indexOf(distance_str);
			if (index < 0) {
				defaltList.add(2, distance_str);
			}
		} else {
			int index = defaltList.indexOf("距离由近到远");
			if (index != -1) {
				defaltList.remove(index);
			}
		}
		popupView = getActivity().getLayoutInflater().inflate(R.layout.window_price, null);
		ListView window_price_lv = (ListView) popupView.findViewById(R.id.window_price_lv);
		ChooseConditionAdapter adapter = new ChooseConditionAdapter(getActivity(), defaltList, null, "defalt",
				mPosition);
		adapter.setOnSelectListner(this);
		window_price_lv.setAdapter(adapter);
		mPopupWindow = new PopupWindow(popupView, LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT, true);
		mPopupWindow.setTouchable(true);
		mPopupWindow.setOutsideTouchable(true);
		popupView.setFocusable(true);
		popupView.setFocusableInTouchMode(true);
		popupView.setOnKeyListener(new View.OnKeyListener() {
			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				if (keyCode == KeyEvent.KEYCODE_BACK) {
					popwindowDismiss();
				}
				return false;
			}
		});
		popupView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (mPopupWindow.isShowing()) {
					popwindowDismiss();
				}
			}
		});
		// showpop
		mPopupWindow.showAtLocation(view, Gravity.BOTTOM, 0, 0);
		mPopupWindow.update();
		popupView.findViewById(R.id.window_filter_animPart).startAnimation(
				AnimationUtils.loadAnimation(getActivity(), R.anim.menu_bottombar_in));
		setWindowCloseListener(mPopupWindow);
	}

	private void filterSelect(int i) {
		this.currentSelectedIndex = i;
		switch (i) {
		case 0:
			clearBg();
			address_ll.setBackground(getActivity().getResources().getDrawable(R.drawable.search_left_table));
			address_line.setVisibility(View.VISIBLE);
			showAddressList();
			lvGone(0);
			
			break;
		case 1:
			clearBg();
			brand_ll.setBackground(getActivity().getResources().getDrawable(R.drawable.search_left_table));
			brand_line.setVisibility(View.VISIBLE);
			current_flag = KEY_BRAND;
			lvGone(3);
			showMultiList();
			break;
		case 2:
			clearBg();
			star_ll.setBackground(getActivity().getResources().getDrawable(R.drawable.search_left_table));
			star_line.setVisibility(View.VISIBLE);
			current_flag = KEY_STAR;
			showSingleList(current_flag);
			lvGone(4);
			break;
		case 3:
			clearBg();
			distance_ll.setBackground(getActivity().getResources().getDrawable(R.drawable.search_left_table));
			distance_line.setVisibility(View.VISIBLE);
			current_flag = kEY_DISTANCE;
			showSingleList(current_flag);
			lvGone(5);
			break;

		}
	}

	private boolean inFetchingBrandData = false;
	private boolean inFetchingZoneData = false;
	private boolean inFetchingAreaData = false;
	private ImageView window_filter_pb;
	// private ChooseSingleAdapter adapterSingle;
	private ChooseSingleAdapter starAdapterSingle;
	private ChooseSingleAdapter distanceAdapterSingle;
	private ChooseSingleAdapter zoneAdapterSingle;
	private ChooseSingleAdapter areaAdapterSingle;
	// private int singlePosition;
	private int starPosition;
	private int distancePosition;
	private int areaPosition;
	private int zonePosition;
	public static List<Brand> brands;
	public static List<Area> areas;
	public static List<Zone> zones;
	private Button hotel_bt_map;
	private String defaltCondition;
	private int firstVisibleItem;
	private int visibleItemCount;
	private int totalItemCount;
	private ProgressDialog pd;
	public static int mPricePosition;
	private int currentRequestType;
	public static boolean shouldClearList;
	private TextView address_line;
	private TextView brand_line;
	private TextView star_line;
	private TextView distance_line;
	public static String min_price;
	public static String max_price;
	private String currentDate;
	private Animation out_anim;
	private String fechBrandUrl;
	private ImageView more_progress_img;
	public static HotelSearchKeyBean hotelsearchKeybean;
	private static String latlng;
	private ListView window_filter_address_lv;
	private ListView window_filter_brand_lv;
	private ListView window_filter_star_lv;
	private ListView window_filter_distance_lv;
	private ListView window_filter_zone_lv;
	private ListView window_filter_area_lv;
	private FrameLayout window_fl;
	public static boolean isSearchAddress;
	private static TravelApplication app;
	private List<String> defaltList;
	public static int addressPosition;

	public static boolean isSearchAddress() {
		return isSearchAddress;
	}

	public static HotelSearchKeyBean getHotel_searchKey() {
		return hotelsearchKeybean;
	}

	private void fetchBrandData() {
		if (inFetchingBrandData) {
			return;
		}
		inFetchingBrandData = true;
		showPb(window_filter_pb);
		window_filter_brand_lv.setVisibility(View.GONE);
		fechBrandUrl = FETCH_BRAND_URL + "?cityName=" + cityName;
		post.doGet(fechBrandUrl);
	}

	private void initDistanceData() {
		hotel_distance_List.clear();
		hotel_distance_List.add("不限");
		hotel_distance_List.add("500M内");
		hotel_distance_List.add("1公里内");
		hotel_distance_List.add("2公里内");
		hotel_distance_List.add("4公里内");
		hotel_distance_List.add("8公里内");
	}

	private void showMultiList() {
		// 如果本地有数据
		String jsonLocal = new SpUtil(getActivity()).getString(KEY_BRAND + cityName);
		if (TextUtils.isEmpty(jsonLocal)) {
			fetchBrandData();
		} else {
			updateBrandPopWindow(jsonLocal);
		}
	}

	private void showSingleList(String flag) {
		this.showSingleList(flag, true, null);
	}

	private void showSingleList(String flag, boolean isFirstClickable, onFirstClickListner l) {
		if (KEY_STAR.equals(flag)) {
			hotel_filter_List = hotel_star_List;
			starAdapterSingle = new ChooseSingleAdapter(getActivity(), hotel_star_List, starPosition, isFirstClickable);
			starAdapterSingle.setOnFirstClickListner(l);
			window_filter_star_lv.setAdapter(starAdapterSingle);
			lvGone(4);
			starAdapterSingle.setOnSingleSelectListner(this);
		} else if (kEY_DISTANCE.equals(flag)) {
			hotel_filter_List = hotel_distance_List;
			distanceAdapterSingle = new ChooseSingleAdapter(getActivity(), hotel_distance_List, distancePosition,
					isFirstClickable);
			distanceAdapterSingle.setOnFirstClickListner(l);
			window_filter_distance_lv.setAdapter(distanceAdapterSingle);
			distanceAdapterSingle.setOnSingleSelectListner(this);
			lvGone(5);
		} else if (KEY_ZONE.equals(flag)) {
			hotel_filter_List = hotel_zone_List;
			zoneAdapterSingle = new ChooseSingleAdapter(getActivity(), hotel_zone_List, zonePosition, isFirstClickable);
			zoneAdapterSingle.setOnFirstClickListner(l);
			window_filter_zone_lv.setAdapter(zoneAdapterSingle);
			zoneAdapterSingle.setOnSingleSelectListner(this);
			lvGone(1);
		} else if (KEY_AREA.equals(flag)) {
			hotel_filter_List = hotel_area_List;
			areaAdapterSingle = new ChooseSingleAdapter(getActivity(), hotel_area_List, areaPosition, isFirstClickable);
			areaAdapterSingle.setOnFirstClickListner(l);
			window_filter_area_lv.setAdapter(areaAdapterSingle);
			areaAdapterSingle.setOnSingleSelectListner(this);
			lvGone(2);
		}
	}

	/**
	 * 显示showIndex的listview并隐藏其他的listview
	 * @Author: yangxy
	 * @Version: V1.0
	 * @Create Date: 2015-3-1
	 * @param showIndex
	 */
	private void lvGone(int showIndex) {
		for (int i = 0; i < window_fl.getChildCount(); i++) {
			View v = window_fl.getChildAt(i);
			v.setVisibility(i == showIndex ? View.VISIBLE : View.GONE);
		}
	}

	@Override
	public void selectSingle(int position, String condition) {
		if (window_filter_ll_condition == null) {
			window_filter_ll_condition = (LinearLayout) popupView.findViewById(R.id.window_filter_ll_condition);
		}
		if (window_filter_ll_condition.getVisibility() == View.GONE) {
			window_filter_ll_condition.setVisibility(View.VISIBLE);
		}
		int count = window_filter_ll_condition.getChildCount();
		removeAllCondition(window_filter_ll_condition, count);
		if (current_flag.equals(KEY_AREA)) {
			areaPosition = position;
		} else if (current_flag.equals(KEY_STAR)) {
			starPosition = position;
		} else if (current_flag.equals(KEY_ZONE)) {
			zonePosition = position;
		} else if (current_flag.equals(kEY_DISTANCE)) {
			distancePosition = position;
		}

		if (position == 0 || (KEY_ZONE.equals(current_flag) && position == 1)
				|| (KEY_AREA.equals(current_flag) && position == 1)) {
			if (KEY_ZONE.equals(current_flag) && position == 1) {// 表示操作的是商业圈
				zonePosition = 1;
			}
			if (KEY_AREA.equals(current_flag) && position == 1) {// 表示操作的是行政区
				areaPosition = 1;
			}
			tempConditionMap.remove(current_flag);
			count = window_filter_ll_condition.getChildCount();
			if (count == 0) {
				window_filter_ll_condition.setVisibility(View.GONE);
			}
			return;
		}
		tempConditionMap.put(current_flag, position + "");
		add_condition_tv2condition_ll(current_flag, condition, window_filter_ll_condition);

	}

	private void add_condition_tv2condition_ll(final String flag, String condition,
			final LinearLayout window_filter_ll_condition) {
		View v = getActivity().getLayoutInflater().inflate(R.layout.item_hotel_filter, null);
		final TextView condition_tv = (TextView) v.findViewById(R.id.item_hotel_filter_tv);
		condition_tv.setText(condition);
		condition_tv.setTag(flag);
		window_filter_ll_condition.addView(v, 0);
		v.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				removeCondition(flag, window_filter_ll_condition, v);
			}
		});
	}

	private void removeCondition(String flagTag, LinearLayout window_filter_ll_condition2, View condition_tv) {
		window_filter_ll_condition2.removeView(condition_tv);
		int count = window_filter_ll_condition2.getChildCount();
		if (count <= 0) {
			window_filter_ll_condition2.setVisibility(View.GONE);
		}

		if (flagTag.equals(KEY_BRAND)) {
			String brandposition = tempConditionMap.get(flagTag);
			if (TextUtils.isEmpty(brandposition)) {
				return;
			}
			String[] positions = brandposition.split(",");
			int pos = 0;
			if (positions.length > 1) {
				for (int i = 0; i < positions.length; i++) {
					pos = Integer.parseInt(positions[i]);
					Brand b = brands.get(pos);
					String b_name = b.getBrandName();
					if (((TextView) condition_tv.findViewById(R.id.item_hotel_filter_tv)).getText().toString()
							.equals(b_name)) {
						String[] tempStrs = new String[positions.length];
						int j = 0;
						for (String s : positions) {
							String position = pos + "";
							if (!position.equals(s)) {
								tempStrs[j] = s;
								j++;
							}
						}
						brandposition = "";
						for (String s : tempStrs) {
							if (!TextUtils.isEmpty(s)) {
								brandposition = brandposition + s + ",";
							}
						}
						brandposition = brandposition.substring(0, brandposition.length() - 1);
						tempConditionMap.put(flagTag, brandposition);
						adapterMulty.removeCondition(pos);
					}
				}
			} else {
				tempConditionMap.remove(flagTag);
				adapterMulty.removeCondition(pos);
				brandposition = "";
			}
			if (pos == 0) {
				tempConditionMap.remove(flagTag);
				adapterMulty.removeCondition(pos);
				brandposition = "";
			}
			if (current_flag.equals(KEY_BRAND)) {
				filterSelect(1);
			}
		} else {
			tempConditionMap.remove(flagTag);
			if (KEY_AREA.equals(flagTag)) {
				areaPosition = 1;
				if (KEY_AREA.equals(current_flag)) {
					this.selectAddress(2);
				}
			} else if (KEY_ZONE.equals(flagTag)) {
				zonePosition = 1;
				if (KEY_ZONE.equals(current_flag)) {
					this.selectAddress(1);
				}
			} else if (KEY_STAR.equals(flagTag)) {
				starPosition = 0;
				if (KEY_STAR.equals(current_flag)) {
					starAdapterSingle.clickFirst();
				}
			} else if (kEY_DISTANCE.equals(flagTag)) {
				distancePosition = 0;
				if (kEY_DISTANCE.equals(current_flag)) {
					distanceAdapterSingle.clickFirst();
				}
			}
		}
	}

	@Override
	public void selectMulti(int position, String condition, HashMap<Integer, Boolean> checkMap, boolean isCheck) {
		hotel_filter_List = hotel_brand_List;
		if (window_filter_ll_condition == null) {
			window_filter_ll_condition = (LinearLayout) popupView.findViewById(R.id.window_filter_ll_condition);
		}
		if (window_filter_ll_condition.getVisibility() == View.GONE) {
			window_filter_ll_condition.setVisibility(View.VISIBLE);
		}
		String brandId = "";
		String brandPosition = "";
		if (checkMap.size() > 0) {
			Iterator iter = checkMap.entrySet().iterator();
			while (iter.hasNext()) {
				Map.Entry<Integer, Boolean> entry = (Entry<Integer, Boolean>) iter.next();
				Integer key = entry.getKey();

				boolean value = entry.getValue();
				if (value) {
					Brand brand = brands.get(key);
					brandId = brandId + brand.getBrandCode() + ",";
					brandPosition = brandPosition + key + ",";
				}
			}
			brandPosition = brandPosition.substring(0, brandPosition.length() - 1);
			if (!"0".equals(brandPosition)) {
				tempConditionMap.put(current_flag, brandPosition);
			}
			brandId = brandId.substring(0, brandId.length() - 1);
		}
		int count = window_filter_ll_condition.getChildCount();
		if (checkMap.get(0)) {
			removeAllCondition(window_filter_ll_condition, count);
			count = window_filter_ll_condition.getChildCount();
			if (count == 0) {
				window_filter_ll_condition.setVisibility(View.GONE);
			}
		} else {
			if (isCheck) {
				for (int i = 0; i < count; i++) {
					View view = (View) window_filter_ll_condition.getChildAt(i);
					TextView tv = (TextView) view.findViewById(R.id.item_hotel_filter_tv);
					if (tv != null) {
						String condition_txt = tv.getText().toString();
						if (condition_txt.equals(condition)) {
							return;
						}
					}
				}
				add_condition_tv2condition_ll(current_flag, condition, window_filter_ll_condition);
			} else {
				removeMultiCondition(window_filter_ll_condition, condition, position);
			}
		}
	}

	private void removeMultiCondition(LinearLayout window_filter_ll_condition, String condition, int count) {
		for (int i = 0; i < count; i++) {
			View v = window_filter_ll_condition.getChildAt(i);
			if (v != null) {
				TextView tv = (TextView) v.findViewById(R.id.item_hotel_filter_tv);
				String multi_condition_tv = tv.getText().toString();
				if (multi_condition_tv.equals(condition)) {
					window_filter_ll_condition.removeViewAt(i);
				}
			}
		}
	}

	private void removeAllCondition(LinearLayout window_filter_ll_condition, int count) {
		for (int i = 0; i < window_filter_ll_condition.getChildCount(); i++) {
			View view = (View) window_filter_ll_condition.getChildAt(i);
			TextView tv = (TextView) view.findViewById(R.id.item_hotel_filter_tv);
			if (tv != null) {
				String single_condition_tv = tv.getText().toString();
				for (int j = 0; j < hotel_filter_List.size(); j++) {
					if (single_condition_tv.equals(hotel_filter_List.get(j))) {
						window_filter_ll_condition.removeViewAt(i);
						i--;
						break;
					}
				}
			}
		}
	}

	private void initStarData() {
		hotel_star_List.clear();
		hotel_star_List.add("不限");
		hotel_star_List.add("五星/豪华");
		hotel_star_List.add("四星/高档");
		hotel_star_List.add("三星/舒适");
		hotel_star_List.add("二星及以下/经济");
	}

	private void showAddressList() {
		addressPosition = 0;
		if (tempConditionMap.get(KEY_AREA) != null || tempConditionMap.get(KEY_ZONE) != null) {
			addressPosition = 1;
		}
		ChooseHotelAdressAdapter addressAdapter = new ChooseHotelAdressAdapter(getActivity(), hotel_address_List,
				addressPosition);
		window_filter_address_lv.setAdapter(addressAdapter);
		addressAdapter.setOnAddressSelectListner(this);
	}

	// 筛选条件选择
	private void showFilter() {
		hotel_filter_List = hotel_address_List;
		popupView = getActivity().getLayoutInflater().inflate(R.layout.window_filter, null);
		// 要显示的第一个页面的view
		findViewOfPageOne();
		if (isSearchAddress || isLocationCity()) {
			distance_ll.setVisibility(View.VISIBLE);
		}
		// 默认点击的酒店
		filterSelect(0);
		mPopupWindow = new PopupWindow(popupView, LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT, true);
		mPopupWindow.setTouchable(true);
		mPopupWindow.setOutsideTouchable(true);
		mPopupWindow.showAtLocation(view, Gravity.BOTTOM, 0, 0);
		mPopupWindow.update();
		popupView.setFocusable(true);
		popupView.setFocusableInTouchMode(true);
		popupView.setOnKeyListener(new View.OnKeyListener() {
			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				if (keyCode == KeyEvent.KEYCODE_BACK) {
					popwindowDismiss();
				}
				return false;
			}
		});
		popupView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (mPopupWindow.isShowing()) {
					popwindowDismiss();
				}
			}
		});
		setWindowCloseListener(mPopupWindow);
		mPopupWindow.setBackgroundDrawable(getActivity().getResources().getDrawable(R.drawable.date_bg_normal));
		popupView.findViewById(R.id.window_filter_animPart).startAnimation(
				AnimationUtils.loadAnimation(getActivity(), R.anim.menu_bottombar_in));
		window_filter_ll_condition = (LinearLayout) popupView.findViewById(R.id.window_filter_ll_condition);
		echoFilterData(window_filter_ll_condition);
		filterSelect(0);
	}

	private void popwindowDismiss() {
		if (!isPopDismissing) {
			screening.setClickable(true);
			orderbydefalt.setClickable(true);
			orderbyprice.setClickable(true);
			if (tempConditionMap.size() > 0) {
				showFilterBiao();
			} else {
				dismissFilterBiao();
			}
			out_anim = AnimationUtils.loadAnimation(getActivity(), R.anim.menu_bottombar_out);
			popupView.findViewById(R.id.window_filter_animPart).startAnimation(out_anim);
			out_anim.setAnimationListener(new AnimationListener() {

				@Override
				public void onAnimationStart(Animation animation) {
				}

				@Override
				public void onAnimationRepeat(Animation animation) {
				}

				@Override
				public void onAnimationEnd(Animation animation) {
					mPopupWindow.dismiss();
				}
			});
			isPopDismissing = true;
		}
	}

	public void showFilterBiao() {
		fragment_hotel_list_iv_filterbiao.setVisibility(View.VISIBLE);
	}

	public void dismissFilterBiao() {
		fragment_hotel_list_iv_filterbiao.setVisibility(View.GONE);
	}

	@Override
	public void onResume() {
		isPopDismissing = false;
		tempConditionMap.clear();
		if (conditionMap.size() > 0) {
			showFilterBiao();
			for (Iterator it = conditionMap.entrySet().iterator(); it.hasNext();) {
				Map.Entry entry = (Entry) it.next();
				String key = entry.getKey().toString();
				String value = entry.getValue().toString();
				tempConditionMap.put(key, value);
			}
			if (tempConditionMap != null && tempConditionMap.size() > 0) {
				String areaPositionStr = tempConditionMap.get(KEY_AREA);
				String zonePositionStr = tempConditionMap.get(KEY_ZONE);
				String distancePositionStr = tempConditionMap.get(kEY_DISTANCE);
				String starPositionStr = tempConditionMap.get(KEY_STAR);
				if (!TextUtils.isEmpty(areaPositionStr)) {
					areaPosition = Integer.parseInt(areaPositionStr);
				}
				if (!TextUtils.isEmpty(starPositionStr)) {
					starPosition = Integer.parseInt(starPositionStr);
				}
				if (!TextUtils.isEmpty(zonePositionStr)) {
					zonePosition = Integer.parseInt(zonePositionStr);
				}
				if (!TextUtils.isEmpty(distancePositionStr)) {
					distancePosition = Integer.parseInt(distancePositionStr);
				}
			}
		} else {
			areaPosition = 1;
			starPosition = 0;
			zonePosition = 1;
			distancePosition = 0;
			dismissFilterBiao();
		}
		if (needResume) {
			if (hotelsearchKeybean != null) {
				String key = hotelsearchKeybean.getName();
				if (!TextUtils.isEmpty(key)) {
					String key_utf8;
					try {
						key_utf8 = URLDecoder.decode(key, "utf-8");
						hotel_et_search.setText(key_utf8);
					} catch (UnsupportedEncodingException e) {
						e.printStackTrace();
					}
				} else {
					hotel_et_search.setText("");
					hotelInfoRequest.setKey(null);
				}
			} else {
				hotel_et_search.setText("");
				hotelInfoRequest.setKey(null);
			}
			loadHotel();
			needResume = false;
		}
		super.onResume();
	}

	/**
	 * 回显筛选条件
	 * 
	 * @param window_filter_ll_condition
	 */
	private void echoFilterData(LinearLayout window_filter_ll_condition) {
		String zone = tempConditionMap.get(KEY_ZONE);
		String area = tempConditionMap.get(KEY_AREA);
		String brand = tempConditionMap.get(KEY_BRAND);
		String star = tempConditionMap.get(KEY_STAR);
		String distance = tempConditionMap.get(kEY_DISTANCE);
		if (!TextUtils.isEmpty(zone) && !"0".equals(zone) && !"1".equals(zone)) {
			String zoneCondition = zones.get(Integer.parseInt(zone)).getZoneName();
			add_condition_tv2condition_ll(KEY_ZONE, zoneCondition, window_filter_ll_condition);
		}else{
			//如果没有商业圈条件，要置zonePosition = 1,这样才能选中不限。
			zonePosition = 1;
		}
		if (!TextUtils.isEmpty(area) && !"0".equals(area) && !"1".equals(area)) {
			String areaCondition = areas.get(Integer.parseInt(area)).getLocationName();
			add_condition_tv2condition_ll(KEY_AREA, areaCondition, window_filter_ll_condition);
		}else{
			//如果没有行政区条件，要置areaPosition = 1,这样才能选中不限。
			areaPosition = 1;
		}
		if (!TextUtils.isEmpty(brand) && !"0".equals(brand)) {
			String[] brandStr = brand.split(",");
			for (int i = 0; i < brandStr.length; i++) {
				String brandCondition = brands.get(Integer.parseInt(brandStr[i])).getBrandName();
				add_condition_tv2condition_ll(KEY_BRAND, brandCondition, window_filter_ll_condition);
			}
		}
		if (!TextUtils.isEmpty(star) && !"0".equals(star)) {
			String starCondition = hotel_star_List.get(Integer.parseInt(star));
			add_condition_tv2condition_ll(KEY_STAR, starCondition, window_filter_ll_condition);
		}else{
			starPosition = 0;
		}
		if (!TextUtils.isEmpty(distance) && !"0".equals(distance)) {
			String distanceCondition = hotel_distance_List.get(Integer.parseInt(distance));
			add_condition_tv2condition_ll(kEY_DISTANCE, distanceCondition, window_filter_ll_condition);
		}else{
			distancePosition = 0;
		}
		if (window_filter_ll_condition.getChildCount() > 0) {
			window_filter_ll_condition.setVisibility(View.VISIBLE);
		}
	}

	private void findViewOfPageOne() {
		window_fl = (FrameLayout) popupView.findViewById(R.id.window_fl);
		window_filter_address_lv = (ListView) popupView.findViewById(R.id.window_filter_address_lv);
		window_filter_brand_lv = (ListView) popupView.findViewById(R.id.window_filter_brand_lv);
		window_filter_star_lv = (ListView) popupView.findViewById(R.id.window_filter_star_lv);
		window_filter_distance_lv = (ListView) popupView.findViewById(R.id.window_filter_distance_lv);
		window_filter_zone_lv = (ListView) popupView.findViewById(R.id.window_filter_zone_lv);
		window_filter_area_lv = (ListView) popupView.findViewById(R.id.window_filter_area_lv);
		window_filter_pb = (ImageView) popupView.findViewById(R.id.window_filter_pb);
		address_ll = (RelativeLayout) popupView.findViewById(R.id.address_ll);
		brand_ll = (RelativeLayout) popupView.findViewById(R.id.brand_ll);
		star_ll = (RelativeLayout) popupView.findViewById(R.id.star_ll);
		distance_ll = (RelativeLayout) popupView.findViewById(R.id.distance_ll);
		window_filter_bt_cancle = (TextView) popupView.findViewById(R.id.window_filter_bt_cancle);
		window_filter_bt_clear = (TextView) popupView.findViewById(R.id.window_filter_bt_clear);
		window_filter_bt_sure = (TextView) popupView.findViewById(R.id.window_filter_bt_sure);
		address_line = (TextView) popupView.findViewById(R.id.address_line);
		brand_line = (TextView) popupView.findViewById(R.id.brand_line);
		star_line = (TextView) popupView.findViewById(R.id.star_line);
		distance_line = (TextView) popupView.findViewById(R.id.distance_line);

		window_filter_bt_cancle.setOnClickListener(this);
		window_filter_bt_clear.setOnClickListener(this);
		window_filter_bt_sure.setOnClickListener(this);
		address_ll.setOnClickListener(this);
		brand_ll.setOnClickListener(this);
		star_ll.setOnClickListener(this);
		distance_ll.setOnClickListener(this);
	}

	private void initAddressData() {
		hotel_address_List.clear();
		hotel_address_List.add("不限");
		hotel_address_List.add("商业圈");
		hotel_address_List.add("行政区");
	}

	// 价格选择
	private void showSelectPrice() {
		popupView = getActivity().getLayoutInflater().inflate(R.layout.window_price, null);
		ListView window_price_lv = (ListView) popupView.findViewById(R.id.window_price_lv);
		ChooseConditionAdapter adapter = new ChooseConditionAdapter(getActivity(), hotel_price_List, null, "price",
				mPricePosition);
		adapter.setOnSelectListner(this);
		window_price_lv.setAdapter(adapter);
		mPopupWindow = new PopupWindow(popupView, LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT, true);
		mPopupWindow.setTouchable(true);
		mPopupWindow.setOutsideTouchable(true);
		popupView.setFocusable(true);
		popupView.setFocusableInTouchMode(true);
		popupView.setOnKeyListener(new View.OnKeyListener() {
			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				if (keyCode == KeyEvent.KEYCODE_BACK) {
					popwindowDismiss();
				}
				return false;
			}
		});
		popupView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (mPopupWindow.isShowing()) {
					popwindowDismiss();
				}
			}
		});
		// showpop
		mPopupWindow.showAtLocation(view, Gravity.BOTTOM, 0, 0);
		mPopupWindow.update();
		popupView.findViewById(R.id.window_filter_animPart).startAnimation(
				AnimationUtils.loadAnimation(getActivity(), R.anim.menu_bottombar_in));
		setWindowCloseListener(mPopupWindow);

	}

	private void setWindowCloseListener(PopupWindow mPopupWindow) {

		mPopupWindow.setOnDismissListener(new OnDismissListener() {

			@Override
			public void onDismiss() {
				isPopDismissing = false;
			}
		});
	}

	class MyComparator implements Comparator {
		public int compare(Object o1, Object o2) {
			Hotel h1 = (Hotel) o1;
			Hotel h2 = (Hotel) o2;
			String p1 = h1.getPrice();
			String p2 = h2.getPrice();
			if (order.equals("asc")) {
				return p1.compareTo(p2);
			} else {
				return p2.compareTo(p1);
			}
		}
	}

	@Override
	public void selectPrice(int position) {
		// clearCondition();
		min_price = null;
		max_price = null;
		switch (position) {
		case 0:
			min_price = null;
			max_price = null;
			break;
		case 1:
			min_price = "0";
			max_price = "150";
			break;
		case 2:
			min_price = "151";
			max_price = "300";
			break;
		case 3:
			min_price = "301";
			max_price = "450";
			break;
		case 4:
			min_price = "451";
			max_price = "600";
			break;
		case 5:
			min_price = "601";
			max_price = "1000";
			break;
		case 6:
			min_price = "1000";
			max_price = null;
			break;
		}
		mPricePosition = position;
		if (popupView != null && popupView.getVisibility() == View.VISIBLE) {
			popwindowDismiss();
		}
		if (hotelInfoRequest == null) {
			hotelInfoRequest = new HotelInfoRequest();
		}
		hotelInfoRequest.setMinPrice(min_price);
		hotelInfoRequest.setMaxPrice(max_price);
		// 请求网络
		page = 1;
		hotelInfoRequest.setPage(page);
		shouldClearList = true;
		if (needLoad) {
			loadHotel();
			needLoad = false;
		}
	}

	@Override
	public void selectAddress(int position) {
		switch (position) {
		case 0:
			// 选择了不限，要把地址条件都清空
			clearAddressCondition();
			break;
		case 1:
			lvGone(1);
			current_flag = KEY_ZONE;
			SpUtil sp_zone = new SpUtil(getActivity());
			String json_zone = sp_zone.getString(KEY_ZONE + cityName);
			if (TextUtils.isEmpty(json_zone)) {
				inFetchingZoneData = true;
				showPb(window_filter_pb);
				window_filter_zone_lv.setVisibility(View.GONE);
				fetchzoneurl = FETCH_ZONE_URL + "?cityName=" + cityName;
				post.doGet(fetchzoneurl);
			} else {
				updateZoomPopWindow(json_zone);
			}
			break;
		case 2:
			lvGone(2);
			current_flag = KEY_AREA;
			SpUtil sp_area = new SpUtil(getActivity());
			String json_area = sp_area.getString(KEY_AREA + cityName);
			if (TextUtils.isEmpty(json_area)) {
				inFetchingAreaData = true;
				showPb(window_filter_pb);
				window_filter_area_lv.setVisibility(View.GONE);
				fetchareaurl = FETCH_AREA_URL + "?cityName=" + cityName;
				post.doGet(fetchareaurl);
			} else {
				updateAreaPopWindow(json_area);
			}
			break;
		}
	}

	private void clearAddressCondition() {
		int count = window_filter_ll_condition.getChildCount();
		for (int i = 0; i < count; i++) {
			View view = (View) window_filter_ll_condition.getChildAt(i);
			if (view == null) {
				return;
			}
			TextView tv = (TextView) view.findViewById(R.id.item_hotel_filter_tv);
			if (tv != null) {
				String condition_txt = tv.getText().toString();
				if (hotel_area_List != null && hotel_area_List.size() > 0) {
					for (String s : hotel_area_List) {
						if (condition_txt.equals(s)) {
							window_filter_ll_condition.removeViewAt(i);
							tempConditionMap.remove(KEY_AREA);
							areaPosition = 1;
						}
					}
				}
			}
		}
		count = window_filter_ll_condition.getChildCount();
		for (int i = 0; i < count; i++) {
			View view = (View) window_filter_ll_condition.getChildAt(i);
			if (view == null) {
				return;
			}
			TextView tv = (TextView) view.findViewById(R.id.item_hotel_filter_tv);
			if (tv != null) {
				String condition_txt = tv.getText().toString();
				if (hotel_zone_List != null && hotel_zone_List.size() > 0) {
					for (String s : hotel_zone_List) {
						if (condition_txt.equals(s)) {
							window_filter_ll_condition.removeViewAt(i);
							tempConditionMap.remove(KEY_ZONE);
							zonePosition = 1;
						}
					}
				}
			}
		}
		count = window_filter_ll_condition.getChildCount();
		if (count == 0) {
			window_filter_ll_condition.setVisibility(View.GONE);
		}
	}

	private void updateAreaPopWindow(String json) {
		areas = JSONUtil.getInstance().fromJSON(new TypeToken<ArrayList<Area>>() {
		}.getType(), json);
		if (areas == null || areas.size() == 0) {
			return;
		}
		Area a = new Area();
		a.setLocationName("不限");
		areas.add(0, a);
		Area a1 = new Area();
		a1.setLocationName("行政区");
		areas.add(0, a1);
		hotel_area_List.clear();
		for (Area area : areas) {
			hotel_area_List.add(area.getLocationName());
		}
		showSingleList(KEY_AREA, false, new onFirstClickListner() {
			@Override
			public void onClick(View view) {
				filterSelect(currentSelectedIndex);
			}
		});
	}

	@Override
	public void selectDefalt(int position) {
		String defalt_condition = defaltList.get(position);
		if (!TextUtils.isEmpty(defalt_condition)) {
			if (defalt_condition.equals("默认排序")) {
				defaltCondition = null;
			} else if (defalt_condition.equals("评价由高到低")) {
				defaltCondition = DEFALT_COMM_DESC;
			} else if (defalt_condition.equals("距离由近到远")) {
				BDLocation location = app.getLocation();
				if (location != null && location.getCity().equals(app.getArrivecity() + "市")) {
					hotelInfoRequest.setLat(location.getLatitude() + "");
					hotelInfoRequest.setLng(location.getLongitude() + "");
				}
				defaltCondition = DEFALT_DISTANCE_ASC;
			} else if (defalt_condition.equals("价格由高到低")) {
				defaltCondition = DEFALT_PRICE_DESC;
			} else if (defalt_condition.equals("价格由低到高")) {
				defaltCondition = DEFALT_PRICE_ASC;
			}
		}
		this.mPosition = position;
		if (popupView != null && popupView.getVisibility() == View.VISIBLE) {
			popwindowDismiss();
		}
		reOrderHotelList(defaltCondition);
	}

	private void reOrderHotelList(String defaltCondition) {
		startProgressDialog();
		list.clear();
		hotelInfoRequest.setDefaltCondition(defaltCondition);
		if (DEFALT_DISTANCE_ASC.equals(defaltCondition)) {
			BDLocation location = app.getLocation();
			if (location != null && location.getCity().equals(app.getArrivecity() + "市")) {
				hotelInfoRequest.setLat(location.getLatitude() + "");
				hotelInfoRequest.setLng(location.getLongitude() + "");
			}
		}
		page = 1;
		hotelInfoRequest.setPage(page);
		if (needLoad) {
			loadHotel();
			needLoad = false;
		}
	}

	@Override
	public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
		lastItem = firstVisibleItem + visibleItemCount - 2;
	}

	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {
		if (lastItem == listviewCount - 1 && scrollState == this.SCROLL_STATE_IDLE) {
			hotel_lv.addFooterView(moreView);
			showPb(more_progress_img);
			if (moreView != null) {
				moreView.setClickable(false);
			}
			page++;
			loadMoreData();
		}
	}

	private void loadMoreData() { // 加载更多数据
		hotelInfoRequest.setPage(page);
		shouldClearList = false;
		String sre = JSONUtil.getInstance().toJSON(hotelInfoRequest);
		loadHotel();
	}

	@Override
	public void onAttach(Activity activity) {
		parentActivity = (ListOfMessageActivity) activity;
		super.onAttach(activity);
	}

	@Override
	public boolean onBack() {
		return false;
	}

	@Override
	public void selectBank(int position) {

	}

	private void showPb(ImageView iv) {
		iv.setVisibility(View.VISIBLE);
		AnimationDrawable animationDrawable = (AnimationDrawable) iv.getBackground();
		animationDrawable.start();
	}

	private void dissmissPb(ImageView iv) {
		AnimationDrawable animationDrawable = (AnimationDrawable) iv.getBackground();
		animationDrawable.stop();
		iv.setVisibility(View.GONE);
	}

	// add by weiqun 140928
	public void hideCalendar() {
		showCalendar(false);
	}

	@Override
	public boolean onKeyBackDown() {
		if (dateSelectorFragment != null && dateSelectorFragment.isVisiable()) {
			dateSelectorFragment.setVisiable(false);
			hideCalendar();
			return true;
		}
		return super.onKeyBackDown();
	}
	// add end weiqun 140928
}
