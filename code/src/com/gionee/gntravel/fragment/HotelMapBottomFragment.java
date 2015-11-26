package com.gionee.gntravel.fragment;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
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

import com.gionee.gntravel.R;
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
import com.gionee.gntravel.entity.Area;
import com.gionee.gntravel.entity.Brand;
import com.gionee.gntravel.entity.Hotel;
import com.gionee.gntravel.entity.HotelInfoRequest;
import com.gionee.gntravel.entity.HotelSearchKeyBean;
import com.gionee.gntravel.entity.Zone;
import com.gionee.gntravel.utils.FinalString;
import com.gionee.gntravel.utils.JSONUtil;
import com.gionee.gntravel.utils.NetWorkUtil;
import com.gionee.gntravel.utils.SpUtil;
import com.google.gson.reflect.TypeToken;

public class HotelMapBottomFragment extends BaseFragment implements OnClickListener, onSelectListner,
		onSingleSelectListner, onMultiSelectListner, onAddressSelectListner {
	private static final String KEY_ZONE = "zone";
	private static final String KEY_AREA = "area";
	private static final String KEY_BRAND = "brand";
	private static final String KEY_STAR = "star";
	private static final String kEY_DISTANCE = "distance";;
	private List<Hotel> list = new ArrayList<Hotel>();
	private String fetchzoneurl;
	private String fetchareaurl;
	private RelativeLayout orderbyprice;
	private ImageView fragment_list_hotel_iv_defalt;
	private ImageView fragment_list_hotel_iv_price;
	private ImageView fragment_list_hotel_iv_screening;
	private RelativeLayout orderbydefalt;
	private RelativeLayout fragment_hotel_bottom_rl_more;
	private RelativeLayout screening;
	private PopupWindow mPopupWindow;
	private View view;
	private List<Hotel> finalList = new ArrayList<Hotel>();
	private RelativeLayout address_rl;
	private RelativeLayout brand_rl;
	private RelativeLayout star_rl;
	private RelativeLayout distance_rl;
	private View popupView;
	private String current_flag = "";
	private LinearLayout window_filter_ll_condition;
	private TextView window_filter_bt_cancle;
	private TextView window_filter_bt_clear;
	private TextView window_filter_bt_sure;
	private ChooseHotelMultiAdapter adapterMulty;
	private int currentSelectedIndex;
	private int page = 1;;
	private TextView fragment_hotel_tv_defalt;
	private TextView fragment_hotel_tv_price;
	private TextView fragment_hotel_tv_filter;
	private Integer tabSelectFlag = 4;// 1:默认排序 2：价格 3：筛选 4:加载更多
	private TextView fragment_hotelmap_tv_more;
	private ImageView fragment_list_hotel_iv_more;
	private String cityName;
	private String date;
	private HotelSearchKeyBean bean = new HotelSearchKeyBean();
	private ImageView fragment_hotel_list_iv_filterbiao;
	private boolean clearBrand = false;
	public static Map<String, String> tempConditionMap = new HashMap<String, String>();
	private static TravelApplication app;
	private boolean isPopDismissing;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		HotelListFragment.needResume = true;
		cityName = getActivity().getIntent().getStringExtra("cityName");
		date = getActivity().getIntent().getStringExtra("date");
		app = (TravelApplication) this.getActivity().getApplication();
		for (Iterator it = HotelListFragment.conditionMap.entrySet().iterator(); it.hasNext();) {
			Map.Entry entry = (Entry) it.next();
			String key = entry.getKey().toString();
			String value = entry.getValue().toString();
			tempConditionMap.put(key, value);
		}
		if (HotelListFragment.conditionMap.size()==0) {
		}
		if (tempConditionMap != null && tempConditionMap.size() > 0) {
			String areaPositionStr = tempConditionMap.get(KEY_AREA);
			String zonePositionStr = tempConditionMap.get(KEY_ZONE);
			String distancePositionStr = tempConditionMap.get(kEY_DISTANCE);
			String starPositionStr = tempConditionMap.get(KEY_STAR);
			if (!TextUtils.isEmpty(areaPositionStr)) {
				areaPosition = Integer.parseInt(areaPositionStr);
			}
			if (!TextUtils.isEmpty(zonePositionStr)) {
				zonePosition = Integer.parseInt(zonePositionStr);
			}
			if (!TextUtils.isEmpty(distancePositionStr)) {
				distancePosition = Integer.parseInt(distancePositionStr);
			}
			if (!TextUtils.isEmpty(starPositionStr)) {
				starPosition = Integer.parseInt(starPositionStr);
			}
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = findViews(inflater);
		fragment_hotel_tv_price.setText(HotelListFragment.hotel_price_List.get(HotelListFragment.mPricePosition));
		DisplayMetrics outMetrics = new DisplayMetrics();
		getActivity().getWindowManager().getDefaultDisplay().getMetrics(outMetrics);
		LinearLayout.LayoutParams ll = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT,
				48 * (int) (outMetrics.widthPixels / outMetrics.density + 0.5f));
		ll.weight = 1;
		view.setLayoutParams(ll);
		return view;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void onReqSuc(String requestUrl, String json) {
		super.onReqSuc(requestUrl, json);
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
		if (requestUrl.equals(HotelListFragment.FETCH_HOTELLIST_URL)) {
			loadMoreListner.dismissmProgress();
			finalList = JSONUtil.getInstance().fromJSON(new TypeToken<ArrayList<Hotel>>() {
			}.getType(), json);

			if (shouldClearList) {
				list.clear();
			}
			list.addAll(finalList);
			changeTabStatus();
			if (tabSelectFlag == 4 || tabSelectFlag == 2 || tabSelectFlag == 3) {
				loadMoreListner.loadMoreSuc(list, null);
			}
		}
	}

	private void clearBg() {
		address_rl.setBackgroundResource(0);
		brand_rl.setBackgroundResource(0);
		star_rl.setBackgroundResource(0);
		distance_rl.setBackgroundResource(0);
		distance_line.setVisibility(View.GONE);
		address_line.setVisibility(View.GONE);
		brand_line.setVisibility(View.GONE);
		star_line.setVisibility(View.GONE);
	}

	private void updateZoomPopWindow(String json) {
		HotelListFragment.zones = JSONUtil.getInstance().fromJSON(new TypeToken<ArrayList<Zone>>() {
		}.getType(), json);
		if (HotelListFragment.zones == null || HotelListFragment.zones.size() == 0) {
			return;
		}
		HotelListFragment.hotel_zone_List.clear();
		Zone zone = new Zone();
		zone.setZoneName("不限");
		HotelListFragment.zones.add(0, zone);
		Zone zone1 = new Zone();
		zone1.setZoneName("商业圈");
		HotelListFragment.zones.add(0, zone1);
		for (Zone business : HotelListFragment.zones) {
			HotelListFragment.hotel_zone_List.add(business.getZoneName());
		}
		showSingleList(KEY_ZONE, false, new onFirstClickListner() {

			@Override
			public void onClick(View view) {
				filterSelect(currentSelectedIndex);
			}
		});
	}

	private void updateBrandPopWindow(String json) {
		HotelListFragment.brands = JSONUtil.getInstance().fromJSON(new TypeToken<ArrayList<Brand>>() {
		}.getType(), json);
		if (HotelListFragment.brands == null || HotelListFragment.brands.size() == 0) {
			return;
		}
		adapterMulty = new ChooseHotelMultiAdapter(getActivity(), HotelListFragment.brands);
		Brand brand = new Brand();
		brand.setBrandName("不限");
		HotelListFragment.brands.add(0, brand);
		HotelListFragment.hotel_brand_List.clear();
		for (Brand b : HotelListFragment.brands) {
			HotelListFragment.hotel_brand_List.add(b.getBrandName());
		}
		adapterMulty = new ChooseHotelMultiAdapter(getActivity(), HotelListFragment.brands);
		if (clearBrand) {
			adapterMulty.removeCondition(0);
			clearBrand = false;
		}
		if (mPopupWindow != null && mPopupWindow.isShowing() && current_flag.equals(KEY_BRAND)) {
			window_filter_brand_lv.setAdapter(adapterMulty);
			adapterMulty.setOnMultiSelectListner(this);
		}
	}

	/**
	 * 请求失败时回调
	 * 
	 * @Author: yangxy
	 * @Version: V1.0
	 * @Create Date: 2014-11-2
	 * @param requestUrl
	 *            发起请求的url
	 * @param ex
	 *            异常信息
	 * @param errorCode
	 */
	@Override
	public void onReqFail(String requestUrl, Exception ex, int errorCode) {
		super.onReqFail(requestUrl, ex, errorCode);
		if (requestUrl.equals(HotelListFragment.FETCH_HOTELLIST_URL)) {
			Toast.makeText(getActivity(), ex.getMessage(), Toast.LENGTH_LONG).show();
			loadMoreListner.loadMoreSuc(null, null);
			loadMoreListner.dismissmProgress();
			changeTabStatus();
		}
		if (requestUrl.equals(HotelListFragment.FETCH_BRAND_URL)) {
			inFetchingBrandData = false;
			window_filter_pb.setVisibility(View.GONE);
			window_filter_brand_lv.setVisibility(View.VISIBLE);
		}
		if (requestUrl.equals(HotelListFragment.FETCH_ZONE_URL)) {
			inFetchingZoneData = false;
			window_filter_pb.setVisibility(View.GONE);
			window_filter_zone_lv.setVisibility(View.VISIBLE);
		}
		if (requestUrl.equals(HotelListFragment.FETCH_AREA_URL)) {
			inFetchingAreaData = false;
			window_filter_pb.setVisibility(View.GONE);
			window_filter_area_lv.setVisibility(View.VISIBLE);
		}
	}

	/**
	 * 选完价格之后底部tab上面的文本变化
	 * 
	 * @Author: yangxy
	 * @Version: V1.0
	 * @Create Date: 2014-11-2
	 */
	private void changeTabStatus() {
		if (!TextUtils.isEmpty(HotelListFragment.min_price) && !TextUtils.isEmpty(HotelListFragment.max_price)) {
			if (HotelListFragment.min_price.equals("0")) {
				fragment_hotel_tv_price.setText("150元以下");
			} else if (HotelListFragment.max_price != null) {
				fragment_hotel_tv_price.setText(HotelListFragment.min_price + "-" + HotelListFragment.max_price + "元");
			}
		} else if (!TextUtils.isEmpty(HotelListFragment.min_price) && TextUtils.isEmpty(HotelListFragment.max_price)) {

			fragment_hotel_tv_price.setText(HotelListFragment.min_price + "元以上");
		} else {
			fragment_hotel_tv_price.setText("价格不限");
		}
		changeTabBg(tabSelectFlag);
	}

	private View findViews(LayoutInflater inflater) {
		view = inflater.inflate(R.layout.fragment_hotemap_bottom, null);
		orderbyprice = (RelativeLayout) view.findViewById(R.id.orderbyprice);
		orderbydefalt = (RelativeLayout) view.findViewById(R.id.orderbydefalt);
		fragment_hotel_bottom_rl_more = (RelativeLayout) view.findViewById(R.id.fragment_hotel_bottom_rl_more);
		screening = (RelativeLayout) view.findViewById(R.id.screening);
		orderbyprice.setOnClickListener(this);
		fragment_hotel_bottom_rl_more.setOnClickListener(this);
		screening.setOnClickListener(this);
		orderbydefalt.setOnClickListener(this);
		fragment_list_hotel_iv_defalt = (ImageView) view.findViewById(R.id.fragment_list_hotel_iv_defalt);
		fragment_hotel_list_iv_filterbiao = (ImageView) view.findViewById(R.id.fragment_hotel_list_iv_filterbiao);
		fragment_list_hotel_iv_price = (ImageView) view.findViewById(R.id.fragment_list_hotel_iv_price);
		fragment_list_hotel_iv_screening = (ImageView) view.findViewById(R.id.fragment_list_hotel_iv_screening);
		fragment_list_hotel_iv_more = (ImageView) view.findViewById(R.id.fragment_list_hotel_iv_more);
		fragment_hotel_tv_defalt = (TextView) view.findViewById(R.id.fragment_hotel_tv_defalt);
		fragment_hotel_tv_price = (TextView) view.findViewById(R.id.fragment_hotel_tv_price);
		fragment_hotel_tv_filter = (TextView) view.findViewById(R.id.fragment_hotel_tv_filter);
		fragment_hotelmap_tv_more = (TextView) view.findViewById(R.id.fragment_hotelmap_tv_more);
		return view;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		// 按照价格查询
		case R.id.orderbyprice:
			showSelectPrice();
			tabSelectFlag = 2;
			orderbyprice.setClickable(false);
			break;
		// 筛选
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
			if (HotelListFragment.brands != null && HotelListFragment.brands.size() != 0) {
				if (adapterMulty == null) {
					adapterMulty = new ChooseHotelMultiAdapter(getActivity(), HotelListFragment.brands);
					adapterMulty.setOnMultiSelectListner(this);
				}
				adapterMulty.clickFirst();
				adapterMulty.removeCondition(0);
			}
			resetConditionPosition();
			window_filter_ll_condition.removeAllViews();
			window_filter_ll_condition.setVisibility(View.GONE);

			filterSelect(currentSelectedIndex);
			if (tempConditionMap != null && tempConditionMap.size() > 0) {
				tempConditionMap.clear();
			}
			break;
		case R.id.window_filter_bt_sure:
			wrapHotelInfo();
			loadHotel();
			popwindowDismiss();
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
		HotelListFragment.shouldClearList = true;
		HotelListFragment.page = 1;
		HotelListFragment.conditionMap.clear();
		HotelListFragment.hotelInfoRequest.setZoneId(null);
		HotelListFragment.hotelInfoRequest.setAreaId(null);
		HotelListFragment.hotelInfoRequest.setBrandId(null);
		HotelListFragment.hotelInfoRequest.setHotelStarRate(0);
		HotelListFragment.hotelInfoRequest.setDistance(0);
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
			HotelListFragment.conditionMap.put(key, value);
			if (KEY_ZONE.equals(key)) {
				int v = Integer.parseInt(value);
				Zone z = HotelListFragment.zones.get(v);
				String zoneid = z.getZone();
				HotelListFragment.hotelInfoRequest.setZoneId(zoneid);
			}
			if (KEY_AREA.equals(key)) {
				int v = Integer.parseInt(value);
				Area area = HotelListFragment.areas.get(v);
				String areaId = area.getLocation();
				HotelListFragment.hotelInfoRequest.setAreaId(areaId);
			}
			if (KEY_BRAND.equals(key)) {
				String[] strs = value.split(",");
				String brandId = "";
				for (int i = 0; i < strs.length; i++) {
					int index = Integer.parseInt(strs[i]);
					brandId = brandId + HotelListFragment.brands.get(index).getBrandCode() + ",";
				}
				brandId = brandId.substring(0, brandId.length() - 1);
				if (HotelListFragment.hotelsearchKeybean != null) {
					String flag = HotelListFragment.hotelsearchKeybean.getType();
					if (!TextUtils.isEmpty(flag) && flag.equals("brand")) {
						String brandId_search = HotelListFragment.hotelsearchKeybean.getId();
						brandId = brandId + "," + brandId_search;
					}
				}
				HotelListFragment.hotelInfoRequest.setBrandId(brandId);
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
				HotelListFragment.hotelInfoRequest.setHotelStarRate(po);
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
				HotelListFragment.hotelInfoRequest.setDistance(distance);
			}
		}
		HotelListFragment.hotelInfoRequest.setPage(HotelListFragment.page);
	}

	private void loadHotel() {
		loadMoreListner.showmProgress();
		if (!NetWorkUtil.isNetworkConnected(getActivity())) {
			return;
		}
		// 当前城市，并且没有选择具体的地址，需要计算距离我的距离
		if (HotelListFragment.isLocationCity() && !HotelListFragment.isSearchAddress()) {
			double lat = app.getLocation().getLatitude();
			double lng = app.getLocation().getLongitude();
			HotelListFragment.hotelInfoRequest.setLat(lat + "");
			HotelListFragment.hotelInfoRequest.setLng(lng + "");
		}
		HashMap<String, String> params = new HashMap<String, String>();
		if (HotelListFragment.getHotel_searchKey() != null) {
			String hotelsearchKeybeanJson = JSONUtil.getInstance().toJSON(HotelListFragment.hotelsearchKeybean);
			params.put("hotelsearchKeybean", hotelsearchKeybeanJson);
		}
		if (HotelListFragment.hotelInfoRequest != null) {
			String hotelInfoRequestJson = JSONUtil.getInstance().toJSON(HotelListFragment.hotelInfoRequest);
			params.put("hotelInfoRequest", hotelInfoRequestJson);
		}
		post.doPost(HotelListFragment.FETCH_HOTELLIST_URL, params);
	}

	private void changeTabBg(Integer flag) {
		fragment_hotel_tv_defalt.setTextColor(Color.parseColor("#a3d6e9"));
		fragment_hotel_tv_price.setTextColor(Color.parseColor("#a3d6e9"));
		fragment_hotel_tv_filter.setTextColor(Color.parseColor("#a3d6e9"));
		fragment_hotelmap_tv_more.setTextColor(Color.parseColor("#a3d6e9"));

		fragment_list_hotel_iv_defalt.setImageDrawable(getResources().getDrawable(R.drawable.hotel_defalt_normal));
		fragment_list_hotel_iv_price.setImageDrawable(getResources().getDrawable(R.drawable.price_normal));
		fragment_list_hotel_iv_screening.setImageDrawable(getResources().getDrawable(R.drawable.screening_normal));
		fragment_list_hotel_iv_more.setImageDrawable(getResources().getDrawable(R.drawable.map_more_normal));
		switch (flag) {
		case 1:
			fragment_hotel_tv_defalt.setTextColor(Color.parseColor("#ffffff"));
			fragment_list_hotel_iv_defalt.setImageDrawable(getResources().getDrawable(R.drawable.hotel_defalt_click));
			break;
		case 2:
			fragment_list_hotel_iv_price.setImageDrawable(getResources().getDrawable(R.drawable.price_selected));
			fragment_hotel_tv_price.setTextColor(Color.parseColor("#ffffff"));
			break;
		case 3:
			fragment_list_hotel_iv_screening
					.setImageDrawable(getResources().getDrawable(R.drawable.screening_selected));
			fragment_hotel_tv_filter.setTextColor(Color.parseColor("#ffffff"));
			break;
		case 4:
			fragment_list_hotel_iv_more.setImageDrawable(getResources().getDrawable(R.drawable.map_more_click));
			fragment_hotelmap_tv_more.setTextColor(Color.parseColor("#ffffff"));
			break;
		}
	}

	/**
	 * 筛选选项的选择：酒店位置；酒店品牌；酒店星级；酒店距离
	 * 
	 * @Author: yangxy
	 * @Version: V1.0
	 * @Create Date: 2014-11-10
	 * @param i
	 */
	private void filterSelect(int i) {
		this.currentSelectedIndex = i;
		switch (i) {
		case 0:
			// 酒店位置
			clearBg();
			address_rl.setBackground(getActivity().getResources().getDrawable(R.drawable.search_left_table));
			address_line.setVisibility(View.VISIBLE);
			showAddressList();
			lvGone(0);
			break;
		case 1:
			// 酒店品牌
			clearBg();
			brand_rl.setBackground(getActivity().getResources().getDrawable(R.drawable.search_left_table));
			brand_line.setVisibility(View.VISIBLE);
			current_flag = KEY_BRAND;
			lvGone(3);
			showMultiList();
			break;
		case 2:
			// 酒店星级
			clearBg();
			star_rl.setBackground(getActivity().getResources().getDrawable(R.drawable.search_left_table));
			star_line.setVisibility(View.VISIBLE);
			current_flag = KEY_STAR;
			showSingleList(current_flag);
			lvGone(4);
			break;
		case 3:
			// 酒店距离
			clearBg();
			distance_rl.setBackground(getActivity().getResources().getDrawable(R.drawable.search_left_table));
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
	private int starPosition;
	private int distancePosition;
	private int areaPosition;
	private int zonePosition;

	private boolean shouldClearList;
	private TextView address_line;
	private TextView brand_line;
	private TextView star_line;
	private TextView distance_line;
	private onLoadMoreListner loadMoreListner;
	private Animation out_anim;
	private String fechBrandUrl;
	private ListView window_filter_address_lv;
	private ListView window_filter_brand_lv;
	private ListView window_filter_star_lv;
	private ListView window_filter_distance_lv;
	private ListView window_filter_zone_lv;
	private ListView window_filter_area_lv;
	private FrameLayout window_fl;

	private void fetchBrandData() {
		if (inFetchingBrandData) {
			return;
		}
		inFetchingBrandData = true;
		window_filter_pb.setVisibility(View.VISIBLE);
		window_filter_brand_lv.setVisibility(View.GONE);
		fechBrandUrl = HotelListFragment.FETCH_BRAND_URL + "?cityName=" + cityName;
		post.doGet(fechBrandUrl);
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

	/**
	 * 筛选：显示单选的列表
	 * 
	 * @Author: yangxy
	 * @Version: V1.0
	 * @Create Date: 2014-11-10
	 * @param flag
	 * @param isFirstClickable
	 * @param l
	 */
	private void showSingleList(String flag, boolean isFirstClickable, onFirstClickListner l) {
		// 酒店星级列表
		if (KEY_STAR.equals(flag)) {
			HotelListFragment.hotel_filter_List = HotelListFragment.hotel_star_List;
			starAdapterSingle = new ChooseSingleAdapter(getActivity(), HotelListFragment.hotel_star_List, starPosition,
					isFirstClickable);
			starAdapterSingle.setOnFirstClickListner(l);
			window_filter_star_lv.setAdapter(starAdapterSingle);
			lvGone(4);
			starAdapterSingle.setOnSingleSelectListner(this);
		} else if (kEY_DISTANCE.equals(flag)) {
			// 酒店距离列表
			HotelListFragment.hotel_filter_List = HotelListFragment.hotel_distance_List;
			distanceAdapterSingle = new ChooseSingleAdapter(getActivity(), HotelListFragment.hotel_distance_List,
					distancePosition, isFirstClickable);
			distanceAdapterSingle.setOnFirstClickListner(l);
			window_filter_distance_lv.setAdapter(distanceAdapterSingle);
			distanceAdapterSingle.setOnSingleSelectListner(this);
			lvGone(5);
		} else if (KEY_ZONE.equals(flag)) {
			// 商业圈列表
			HotelListFragment.hotel_filter_List = HotelListFragment.hotel_zone_List;
			zoneAdapterSingle = new ChooseSingleAdapter(getActivity(), HotelListFragment.hotel_zone_List, zonePosition,
					isFirstClickable);
			zoneAdapterSingle.setOnFirstClickListner(l);
			window_filter_zone_lv.setAdapter(zoneAdapterSingle);
			zoneAdapterSingle.setOnSingleSelectListner(this);
			lvGone(1);
		} else if (KEY_AREA.equals(flag)) {
			// 行政区列表
			HotelListFragment.hotel_filter_List = HotelListFragment.hotel_area_List;
			areaAdapterSingle = new ChooseSingleAdapter(getActivity(), HotelListFragment.hotel_area_List, areaPosition,
					isFirstClickable);
			areaAdapterSingle.setOnFirstClickListner(l);
			window_filter_area_lv.setAdapter(areaAdapterSingle);
			areaAdapterSingle.setOnSingleSelectListner(this);
			lvGone(2);
		}
	}

	private void lvGone(int showIndex) {
		for (int i = 0; i < window_fl.getChildCount(); i++) {
			View v = window_fl.getChildAt(i);
			v.setVisibility(i == showIndex ? View.VISIBLE : View.GONE);
		}
	}

	/**
	 * 单选回调
	 * 
	 * @Author: yangxy
	 * @Version: V1.0
	 * @Create Date: 2014-11-10
	 * @param position
	 *            选中的位置索引
	 * @param condition
	 *            选中的文字描述
	 */
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
//			HotelListFragment.conditionMap.remove(current_flag);
			count = window_filter_ll_condition.getChildCount();
			if (count == 0) {
				window_filter_ll_condition.setVisibility(View.GONE);
			}
			return;
		}
		tempConditionMap.put(current_flag, position + "");

		add_condition_tv2condition_ll(current_flag, condition, window_filter_ll_condition);
	}

	/**
	 * 将选中的条件添加到上面的条目上
	 * 
	 * @Author: yangxy
	 * @Version: V1.0
	 * @Create Date: 2014-11-10
	 * @param flag
	 * @param condition
	 * @param window_filter_ll_condition
	 */
	private void add_condition_tv2condition_ll(final String flag, String condition,
			final LinearLayout window_filter_ll_condition) {
		View v = getActivity().getLayoutInflater().inflate(R.layout.item_hotel_filter, null);
		final TextView condition_tv = (TextView) v.findViewById(R.id.item_hotel_filter_tv);
		condition_tv.setText(condition);
		condition_tv.setTag(flag);
		window_filter_ll_condition.addView(v,0);
		v.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				removeCondition(flag, window_filter_ll_condition, v);
			}
		});
	}

	// 点击条件要取消该选项
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
					Brand b = HotelListFragment.brands.get(pos);
					String b_name = b.getBrandName();
					if (((TextView) condition_tv.findViewById(R.id.item_hotel_filter_tv)).getText().toString()
							.equals(b_name)) {
						String[] tempStrs = new String[positions.length];
						int j = 0;
						for(String s:positions){
							String position = pos+"";
							if (!position.equals(s)) {
								tempStrs[j]=s;
								j++;
							}
						}
						brandposition = "";
						for(String s:tempStrs){
							if (!TextUtils.isEmpty(s)) {
								brandposition = brandposition+s+",";
							}
						}
						brandposition = brandposition.substring(0, brandposition.length()-1);
						tempConditionMap.put(flagTag, brandposition);
						if (adapterMulty == null) {
							adapterMulty = new ChooseHotelMultiAdapter(getActivity(), HotelListFragment.brands);
						}
						adapterMulty.removeCondition(pos);
					}
				}
			} else {
				tempConditionMap.remove(flagTag);
				if (adapterMulty == null) {
					adapterMulty = new ChooseHotelMultiAdapter(getActivity(), HotelListFragment.brands);
				}
				adapterMulty.removeCondition(pos);
				brandposition = "";
			}
			if (pos == 0) {
				tempConditionMap.remove(flagTag);
				if (adapterMulty == null) {
					adapterMulty = new ChooseHotelMultiAdapter(getActivity(), HotelListFragment.brands);
				}
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

	/**
	 * 多选的选中回调，品牌
	 * 
	 * @Author: yangxy
	 * @Version: V1.0
	 * @Create Date: 2014-11-10
	 * @param position
	 * @param condition
	 * @param checkMap
	 * @param isCheck
	 */
	@Override
	public void selectMulti(int position, String condition, HashMap<Integer, Boolean> checkMap, boolean isCheck) {
		HotelListFragment.hotel_filter_List = HotelListFragment.hotel_brand_List;
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
					Brand brand = HotelListFragment.brands.get(key);
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

	/**
	 * 取消多选的选项
	 * 
	 * @Author: yangxy
	 * @Version: V1.0
	 * @Create Date: 2014-11-10
	 * @param window_filter_ll_condition
	 * @param condition
	 * @param count
	 */
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

	/**
	 * 清空筛选
	 * 
	 * @Author: yangxy
	 * @Version: V1.0
	 * @Create Date: 2014-11-10
	 * @param window_filter_ll_condition
	 * @param count
	 */
	private void removeAllCondition(LinearLayout window_filter_ll_condition, int count) {
		for (int i = 0; i < window_filter_ll_condition.getChildCount(); i++) {
			View v = (View) window_filter_ll_condition.getChildAt(i);
			TextView tv = (TextView) v.findViewById(R.id.item_hotel_filter_tv);
			if (tv != null) {
				String single_condition_tv = tv.getText().toString();
				for (int j = 0; j < HotelListFragment.hotel_filter_List.size(); j++) {
					if (single_condition_tv.equals(HotelListFragment.hotel_filter_List.get(j))) {
						window_filter_ll_condition.removeViewAt(i);
						i--;
						break;
					}
				}
			}
		}
	}

	@Override
	public void onResume() {
		tempConditionMap.clear();
		if (HotelListFragment.conditionMap.size() > 0) {
			showFilterBiao();
			for (Iterator it = HotelListFragment.conditionMap.entrySet().iterator(); it.hasNext();) {
				Map.Entry entry = (Entry) it.next();
				String key = entry.getKey().toString();
				String value = entry.getValue().toString();
				tempConditionMap.put(key, value);
			}
		} else {
			areaPosition = 1;
			starPosition = 0;
			zonePosition = 1;
			distancePosition = 0;
			dismissFilterBiao();
		}
		super.onResume();
	}

	private void showAddressList() {
		HotelListFragment.addressPosition = 0;
		if (tempConditionMap.get(KEY_AREA) != null || tempConditionMap.get(KEY_ZONE) != null) {
			HotelListFragment.addressPosition = 1;
		}
		HotelListFragment.hotel_filter_List = HotelListFragment.hotel_address_List;
		ChooseHotelAdressAdapter addressAdapter = new ChooseHotelAdressAdapter(getActivity(),
				HotelListFragment.hotel_filter_List, HotelListFragment.addressPosition);
		window_filter_address_lv.setAdapter(addressAdapter);
		addressAdapter.setOnAddressSelectListner(this);
	}

	// 筛选条件选择
	private void showFilter() {
		HotelListFragment.hotel_filter_List = HotelListFragment.hotel_address_List;
		popupView = getActivity().getLayoutInflater().inflate(R.layout.window_filter, null);
		// 要显示的第一个页面的view
		findViewOfPageOne();
		if (HotelListFragment.isSearchAddress() || HotelListFragment.isLocationCity()) {
			distance_rl.setVisibility(View.VISIBLE);
		}
		filterSelect(0);
		mPopupWindow = new PopupWindow(popupView, LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT, true);
		mPopupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {

			@Override
			public void onDismiss() {
				isPopDismissing = false;
			}
		});
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

		mPopupWindow.setBackgroundDrawable(getActivity().getResources().getDrawable(R.drawable.date_bg_normal));
		popupView.findViewById(R.id.window_filter_animPart).startAnimation(
				AnimationUtils.loadAnimation(getActivity(), R.anim.menu_bottombar_in));
		window_filter_ll_condition = (LinearLayout) popupView.findViewById(R.id.window_filter_ll_condition);
		if (HotelListFragment.conditionMap.size() > 0||tempConditionMap.size()>0) {
			echoFilterData(window_filter_ll_condition);
		}
		filterSelect(0);
		setWindowCloseListener(mPopupWindow);
	}

	private void setWindowCloseListener(PopupWindow mPopupWindow2) {
		mPopupWindow.setOnDismissListener(new OnDismissListener() {

			@Override
			public void onDismiss() {
				isPopDismissing = false;
			}
		});
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
		if (!TextUtils.isEmpty(zone) && !"0".equals(zone)) {
			String zoneCondition = HotelListFragment.zones.get(Integer.parseInt(zone)).getZoneName();
			add_condition_tv2condition_ll(KEY_ZONE, zoneCondition, window_filter_ll_condition);
		}else{
			//如果没有商业圈条件，要置zonePosition = 1,这样才能选中不限。
			zonePosition = 1;
		}
		if (!TextUtils.isEmpty(area) && !"0".equals(area)) {
			String areaCondition = HotelListFragment.areas.get(Integer.parseInt(area)).getLocationName();
			add_condition_tv2condition_ll(KEY_AREA, areaCondition, window_filter_ll_condition);
		}else{
			//如果没有行政区条件，要置areaPosition = 1,这样才能选中不限。
			areaPosition = 1;
		}
		if (!TextUtils.isEmpty(brand) && !"0".equals(brand)) {
			String[] brandStr = brand.split(",");
			for (int i = 0; i < brandStr.length; i++) {
				String brandCondition = HotelListFragment.brands.get(Integer.parseInt(brandStr[i])).getBrandName();
				add_condition_tv2condition_ll(KEY_BRAND, brandCondition, window_filter_ll_condition);
			}
		}
		if (!TextUtils.isEmpty(star) && !"0".equals(star)) {
			String starCondition = HotelListFragment.hotel_star_List.get(Integer.parseInt(star));
			add_condition_tv2condition_ll(KEY_STAR, starCondition, window_filter_ll_condition);
		}else{
			starPosition = 0;
		}
		if (!TextUtils.isEmpty(distance) && !"0".equals(distance)) {
			String distanceCondition = HotelListFragment.hotel_distance_List.get(Integer.parseInt(distance));
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
		address_rl = (RelativeLayout) popupView.findViewById(R.id.address_ll);
		brand_rl = (RelativeLayout) popupView.findViewById(R.id.brand_ll);
		star_rl = (RelativeLayout) popupView.findViewById(R.id.star_ll);
		distance_rl = (RelativeLayout) popupView.findViewById(R.id.distance_ll);
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
		address_rl.setOnClickListener(this);
		brand_rl.setOnClickListener(this);
		star_rl.setOnClickListener(this);
		distance_rl.setOnClickListener(this);
	}

	// 价格选择
	private void showSelectPrice() {
		popupView = getActivity().getLayoutInflater().inflate(R.layout.window_price, null);
		ListView window_price_lv = (ListView) popupView.findViewById(R.id.window_price_lv);
		ChooseConditionAdapter adapter = new ChooseConditionAdapter(getActivity(), HotelListFragment.hotel_price_List,
				null, "price", HotelListFragment.mPricePosition);
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

	/**
	 * 选择价格之后回调该方法
	 * 
	 * @Author: yangxy
	 * @Version: V1.0
	 * @Create Date: 2014-11-2
	 * @param position
	 *            点击的第几个价钱
	 */
	@Override
	public void selectPrice(int position) {
		HotelListFragment.min_price = null;
		HotelListFragment.max_price = null;
		switch (position) {
		case 0:
			HotelListFragment.min_price = null;
			HotelListFragment.max_price = null;
			break;
		case 1:
			HotelListFragment.min_price = "0";
			HotelListFragment.max_price = "150";
			break;
		case 2:
			HotelListFragment.min_price = "151";
			HotelListFragment.max_price = "300";
			break;
		case 3:
			HotelListFragment.min_price = "301";
			HotelListFragment.max_price = "450";
			break;
		case 4:
			HotelListFragment.min_price = "451";
			HotelListFragment.max_price = "600";
			break;
		case 5:
			HotelListFragment.min_price = "601";
			HotelListFragment.max_price = "1000";
			break;
		case 6:
			HotelListFragment.min_price = "1000";
			HotelListFragment.max_price = null;
			break;
		}
		HotelListFragment.mPricePosition = position;

		if (popupView != null && popupView.getVisibility() == View.VISIBLE) {
			popwindowDismiss();
		}
		if (HotelListFragment.hotelInfoRequest == null) {
			HotelListFragment.hotelInfoRequest = new HotelInfoRequest();
		}

		HotelListFragment.hotelInfoRequest.setMinPrice(HotelListFragment.min_price);
		HotelListFragment.hotelInfoRequest.setMaxPrice(HotelListFragment.max_price);
		// 请求网络
		page = 1;
		HotelListFragment.hotelInfoRequest.setPage(page);
		shouldClearList = true;
		loadHotel();
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

	/**
	 * 点击了地点之后回调该方法
	 * 
	 * @Author: yangxy
	 * @Version: V1.0
	 * @Create Date: 2014-11-2
	 * @param position
	 */
	@Override
	public void selectAddress(int position) {
		switch (position) {
		case 0:
			// 选择了不限，要把地址条件都清空
			clearAddressCondition();
			break;
		case 1:
			current_flag = KEY_ZONE;
			SpUtil sp_zone = new SpUtil(getActivity());
			String json_zone = sp_zone.getString(KEY_ZONE + cityName);
			if (TextUtils.isEmpty(json_zone) && !inFetchingZoneData) {
				inFetchingZoneData = true;
				showPb(window_filter_pb);
				window_filter_zone_lv.setVisibility(View.GONE);
				fetchzoneurl = HotelListFragment.FETCH_ZONE_URL + "?cityName=" + cityName;
				post.doGet(fetchzoneurl);
			} else {
				updateZoomPopWindow(json_zone);
			}
			break;
		case 2:
			current_flag = KEY_AREA;
			SpUtil sp_area = new SpUtil(getActivity());
			String json_area = sp_area.getString(KEY_AREA + cityName);
			if (TextUtils.isEmpty(json_area) && !inFetchingAreaData) {
				inFetchingAreaData = true;
				showPb(window_filter_pb);
				window_filter_area_lv.setVisibility(View.GONE);
				fetchareaurl = HotelListFragment.FETCH_AREA_URL + "?cityName=" + cityName;
				post.doGet(fetchareaurl);
			} else {
				updateAreaPopWindow(json_area);
			}
			break;
		}
	}

	private void clearAddressCondition() {
		areaPosition = 1;
		zonePosition = 1;
		int count = window_filter_ll_condition.getChildCount();
		for (int i = 0; i < count; i++) {
			View view = (View) window_filter_ll_condition.getChildAt(i);
			TextView tv = (TextView) view.findViewById(R.id.item_hotel_filter_tv);
			if (tv != null) {
				String condition_txt = tv.getText().toString();
				if (HotelListFragment.hotel_area_List != null && HotelListFragment.hotel_area_List.size() > 0) {
					for (String s : HotelListFragment.hotel_area_List) {
						if (condition_txt.equals(s)) {
							window_filter_ll_condition.removeViewAt(i);
							tempConditionMap.remove(KEY_AREA);
						}
					}
				}
			}
		}
		count = window_filter_ll_condition.getChildCount();
		for (int i = 0; i < count; i++) {
			View view = (View) window_filter_ll_condition.getChildAt(i);
			TextView tv = (TextView) view.findViewById(R.id.item_hotel_filter_tv);
			if (tv != null) {
				String condition_txt = tv.getText().toString();
				if (HotelListFragment.hotel_zone_List != null && HotelListFragment.hotel_zone_List.size() > 0) {
					for (String s : HotelListFragment.hotel_zone_List) {
						if (condition_txt.equals(s)) {
							window_filter_ll_condition.removeViewAt(i);
							tempConditionMap.remove(KEY_ZONE);
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

	/**
	 * 筛选-显示 行政区列表
	 * 
	 * @Author: yangxy
	 * @Version: V1.0
	 * @Create Date: 2014-11-2
	 * @param json
	 */
	private void updateAreaPopWindow(String json) {
		HotelListFragment.areas = JSONUtil.getInstance().fromJSON(new TypeToken<ArrayList<Area>>() {
		}.getType(), json);
		if (HotelListFragment.areas == null || HotelListFragment.areas.size() == 0) {
			return;
		}
		HotelListFragment.hotel_area_List.clear();
		Area a = new Area();
		a.setLocationName("不限");
		HotelListFragment.areas.add(0, a);
		Area a1 = new Area();
		a1.setLocationName("行政区");
		HotelListFragment.areas.add(0, a1);
		for (Area area : HotelListFragment.areas) {
			HotelListFragment.hotel_area_List.add(area.getLocationName());
		}
		showSingleList(KEY_AREA, false, new onFirstClickListner() {
			@Override
			public void onClick(View view) {
				filterSelect(currentSelectedIndex);
			}
		});
	}

	public void showFilterBiao() {
		fragment_hotel_list_iv_filterbiao.setVisibility(View.VISIBLE);
	}

	public void dismissFilterBiao() {
		fragment_hotel_list_iv_filterbiao.setVisibility(View.GONE);
	}

	public void setLoadMoreListner(onLoadMoreListner l) {
		this.loadMoreListner = l;
	}

	public interface onLoadMoreListner {
		public void loadMoreSuc(List<Hotel> hotelList, String key);

		public void showmProgress();

		public void dismissmProgress();
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		// 酒店关键字查询回调
		if (requestCode == FinalString.HOTEL_MAP_SEARCHKEY_REQCODE && resultCode == FinalString.HOTEL_SEARCHKEY_RESCODE) {
			shouldClearList = true;
			HotelListFragment.hotelInfoRequest = new HotelInfoRequest();
			bean = (HotelSearchKeyBean) data.getSerializableExtra("searchKeyBean");
			String latlng = data.getStringExtra("latlng");
			TextView tv_key = (TextView) getActivity().findViewById(R.id.activity_hotel_map_tv_searchkey);
			if (bean != null) {
				HotelListFragment.hotelsearchKeybean = bean;
				String key = bean.getName();
				if (!TextUtils.isEmpty(key)) {
					try {
						String key_utf8_decode = URLDecoder.decode(key, "utf-8");
						tv_key.setText(key_utf8_decode);
						String key_utf8_encode = URLEncoder.encode(key, "utf-8");
						HotelListFragment.hotelsearchKeybean.setName(key_utf8_encode);

					} catch (UnsupportedEncodingException e) {
						e.printStackTrace();
					}
				} else {
					tv_key.setText("");
				}
				if (TextUtils.isEmpty(key)) {
					HotelListFragment.hotelsearchKeybean = null;
				} else {
					String flag = HotelListFragment.hotelsearchKeybean.getType();
					if (TextUtils.isEmpty(flag)) {
						try {
							HotelListFragment.hotelInfoRequest.setKey(URLEncoder.encode(key, "utf-8"));
						} catch (UnsupportedEncodingException e) {
							e.printStackTrace();
						}
					} else {
						HotelListFragment.hotelInfoRequest.setKey(null);
					}
				}
				if (!TextUtils.isEmpty(latlng)) {
					HotelListFragment.isSearchAddress = true;
					String[] latlngs = latlng.split(",");
					String lat = latlngs[0];
					String lng = latlngs[1];
					HotelListFragment.hotelInfoRequest.setLat(lat);
					HotelListFragment.hotelInfoRequest.setLng(lng);
				} else {// 经纬度是空要置null，不然会报错
					HotelListFragment.isSearchAddress = false;
					HotelListFragment.hotelInfoRequest.setLat(null);
					HotelListFragment.hotelInfoRequest.setLng(null);
				}
				HotelListFragment.hotelInfoRequest.setDate(date);
			}
			try {
				String cityName_utf8 = URLEncoder.encode(cityName, "utf-8");
				HotelListFragment.hotelInfoRequest.setCityName(cityName_utf8);
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
			page = 1;
			HotelListFragment.hotelInfoRequest.setPage(page);
			HotelListFragment.hotelInfoRequest.setPageSize(HotelListFragment.PAGESIZE);
			shouldClearList = true;
			// 关键字查询之后，筛选的条件都要清空
			clearCondition();
		}
	}

	private void clearCondition() {
		// 清除价格筛选条件
		selectPrice(0);
		// 清除筛选条件
		HotelListFragment.conditionMap.clear();
		if (HotelListFragment.brands != null && HotelListFragment.brands.size() != 0) {
			if (adapterMulty == null) {
				adapterMulty = new ChooseHotelMultiAdapter(getActivity(), HotelListFragment.brands);
				adapterMulty.setOnMultiSelectListner(this);
			}
			adapterMulty.clickFirst();
			adapterMulty.removeCondition(0);
		}
	}

	private void popwindowDismiss() {
		if (!isPopDismissing) {
			orderbyprice.setClickable(true);
			screening.setClickable(true);
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
		} else {
			return;
		}
		isPopDismissing = true;
	}

	@Override
	public void selectBank(int position) {
		// TODO Auto-generated method stub

	}

	@Override
	public void selectDefalt(int position) {
		// TODO Auto-generated method stub

	}
}
