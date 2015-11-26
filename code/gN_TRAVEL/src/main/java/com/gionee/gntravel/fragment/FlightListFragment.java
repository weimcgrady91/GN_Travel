package com.gionee.gntravel.fragment;

import static java.util.Calendar.MILLISECOND;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.gionee.gntravel.FlightticketsDetailsActivity;
import com.gionee.gntravel.ListOfMessageActivity;
import com.gionee.gntravel.R;
import com.gionee.gntravel.TravelApplication;
import com.gionee.gntravel.entity.AirLineEntity;
import com.gionee.gntravel.entity.AirPortEntity;
import com.gionee.gntravel.entity.ClassTypeEntity;
import com.gionee.gntravel.entity.CraftTypeEntity;
import com.gionee.gntravel.entity.Flight;
import com.gionee.gntravel.entity.FlightInfoRequest;
import com.gionee.gntravel.entity.FlightSearchEntityBase;
import com.gionee.gntravel.entity.TakeOffEntity;
import com.gionee.gntravel.utils.BeanUtil;
import com.gionee.gntravel.utils.DateUtils;
import com.gionee.gntravel.utils.FinalString;
import com.gionee.gntravel.utils.HttpConnCallback;
import com.gionee.gntravel.utils.HttpConnUtil4Gionee;
import com.gionee.gntravel.utils.JSONUtil;
import com.gionee.gntravel.utils.MyComparator;
import com.gionee.gntravel.utils.NetWorkUtil;
import com.gionee.gntravel.utils.OnBack;
import com.weiqun.customcalendar.CustomCalendar;
import com.weiqun.customcalendar.CustomCalendar.OnDateSelectedListener;
import com.youju.statistics.YouJuAgent;

public class FlightListFragment extends ListBaseFragment implements
		HttpConnCallback, View.OnClickListener,
		AdapterView.OnItemClickListener, OnBack {
	private ResultHandler handler = new ResultHandler();
	private static final int SEARCH_FINISH = 0x1001;
	private MyAdapter myAdapter;
	private ListView listView;
	private RelativeLayout orderbytime;
	private RelativeLayout orderbyprice;
	private RelativeLayout screening;
	private boolean isByTimeAsc = true;
	private boolean isByPriceAsc = false;
	private HttpConnUtil4Gionee task;
	private TextView tv_today;
	private CustomCalendar myCalendar;
	private String flightUrl;
	private String mDepartCity;
	private String mArriveCity;
	private String takeOffTime;
	private HashMap<String, String> params;
	private FlightInfoRequest requestInfo;
	private String realDate;
	private ViewGroup searchContainer;
	private ViewGroup ll_preday;
	private ViewGroup ll_today;
	private ViewGroup ll_nextday;
	private ListView searchList;
	private ListOfMessageActivity parentActivity;
	private View btn_takeOff;
	private View btn_airlineCompany;
	private View btn_craftType;
	private View btn_classType;
	private View btn_airport;
	private FlightSearchEntityBase airLineEntity;
	private FlightSearchEntityBase craftTypeEntity;
	private TakeOffEntity takeOffEntity;
	private ImageView img_orderbytime;
	private ImageView img_orderbyprice;
	private ImageView img_screening;
	private ImageView img_takeoff;
	private ImageView img_price;
	private LinearLayout ll_airport;
	private FlightSearchEntityBase aAirPortEntity;
	private FlightSearchEntityBase dAirPortEntity;
	private FlightSearchEntityBase classTypeEntity;
	private ListView dAirPortList;
	private ListView aAirPortList;
	private LinearLayout ll_condition;
	private HorizontalScrollView scrollView;
	private RelativeLayout rl1;
	private Date targetDate;
	private ImageView img_occlusionbg;
	private TextView tvRefresh;
	private boolean mCancelLoad;
	private boolean mIsFinish;
	private boolean rl_failed_flag;
	private boolean isFirstLoad = true;

	private int DEFAULT_CLASSTYPE = 1; // 经济舱
	private int CLASSTYPE_Y = 1; // 经济舱
	private int CLASSTYPE_C = 2; // 商务舱
	private int CLASSTYPE_F = 3; // 头等舱

	private ArrayList<ArrayList<Flight>> flightlist = new ArrayList<ArrayList<Flight>>();
	private ArrayList<Flight> variableFlightList = new ArrayList<Flight>();
	
	private class ResultHandler extends Handler {

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			mIsFinish = true;
			switch (msg.what) {
			case FinalString.DATA_FINISH:
				loadDataFinish();
				break;
			case SEARCH_FINISH:
				searchFinish();
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
			isFirstLoad = false;
		}
	}

	private void searchFinish() {
		if (variableFlightList.size() == 0) {
			rl_failed.setBackground(getActivity().getResources().getDrawable(
					R.drawable.notfound_bg));
			rl_failed.setVisibility(View.VISIBLE);
			listView.setVisibility(View.GONE);
		} else {
			rl_failed.setVisibility(View.GONE);
			listView.setVisibility(View.VISIBLE);
		}
		myAdapter.notifyDataSetChanged();
	}

	public interface OnSearchClickListener {
		public void onSearchButtonClick(int visibilit);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.orderbytime:
			orderByTime();
			break;
		case R.id.orderbyprice:
			orderByPrice();
			break;
		case R.id.screening:
			showSearch();
			break;
		case R.id.ll_preday:
			loadPreOrNextDay(-1);
			break;
		case R.id.ll_nextday:
			loadPreOrNextDay(1);
			break;
		case R.id.ll_today:
			loadToady();
			break;
		case R.id.btn_takeOff:
			loadsearchFrg(1, btn_takeOff, takeOffEntity);
			break;
		case R.id.btn_airlineCompany:
			loadsearchFrg(1, btn_airlineCompany, airLineEntity);
			break;
		case R.id.btn_craftType:
			loadsearchFrg(1, btn_craftType, craftTypeEntity);
			break;
		case R.id.btn_classType:
			loadsearchFrg(2, btn_classType, classTypeEntity);
			break;
		case R.id.btn_airport:
			loadairportFrg();
			break;
		case R.id.btn_cancel:
			hideSearch(false);
			break;
		case R.id.btn_ok:
			hideSearch(true);
			break;
		case R.id.btn_clear:
			clearAllConditions();
			break;
		case R.id.img_occlusionbg:
			hideCalendar();
			break;
		case R.id.rl1:
			hideSearch(false);
			break;
		case R.id.rl_failed:
			if (rl_failed_flag) {
				loadingData();
				rl_failed_flag = false;
			}
			break;
		default:
			break;
		}
	}

	private void clearAllConditions() {
		clearConditions(airLineEntity);
		clearConditions(craftTypeEntity);
		clearConditions(classTypeEntity);
		clearConditions(takeOffEntity);
		clearConditions(airLineEntity);
		clearConditions(dAirPortEntity);
		clearConditions(aAirPortEntity);
		ll_condition.removeAllViews();
		scrollView.setVisibility(View.GONE);
		DEFAULT_CLASSTYPE = CLASSTYPE_Y;
	}

	private void doSearch() {
		new Thread(new DoSearch()).start();
	}

	private class DoSearch implements Runnable {

		@Override
		public void run() {
			if (variableFlightList == null) {
				return;
			}
			//判断舱位
			for (int j = 1; j < classTypeEntity.getValues().size(); j++) {
				if (classTypeEntity.getValues().get(j) == true) {
					String targetClassType = classTypeEntity.getKeys().get(j);
					if("经济舱".equals(targetClassType)){
						DEFAULT_CLASSTYPE = CLASSTYPE_Y;
					} else if("公务舱".equals(targetClassType)) {
						DEFAULT_CLASSTYPE = CLASSTYPE_C;
					} else if("头等舱".equals(targetClassType)) {
						DEFAULT_CLASSTYPE = CLASSTYPE_F;
					}
				}
			}
			restructureFlight();
			searchAirLineCompany();
			searchCraftType();
			searchTakeOff();
			searchAirPort();
			Message msg = Message.obtain();
			msg.what = SEARCH_FINISH;
			handler.sendMessage(msg);
		}

//		public void searchClassType() {
//			if (classTypeEntity.getValues().get(0) == true) {
//				return;
//			} else {
//				ArrayList<Flight> tmpList = new ArrayList<Flight>();
//				for (int i = 0; i < flightList.size(); i++) {
//					for (int j = 1; j < classTypeEntity.getValues().size(); j++) {
//						if (classTypeEntity.getValues().get(j) == true) {
//							if (flightList.get(i).getClassType()
//									.equals(classTypeEntity.getKeys().get(j))) {
//								tmpList.add(flightList.get(i));
//							}
//						}
//					}
//				}
//				flightList.clear();
//				flightList.addAll(tmpList);
//			}
//		}

		public void searchAirLineCompany() {
			if (airLineEntity.getValues().get(0) == true) {
				return;
			}
			ArrayList<Flight> tmpList = new ArrayList<Flight>();
			for (int i = 0; i < variableFlightList.size(); i++) {
				for (int j = 1; j < airLineEntity.getValues().size(); j++) {
					if (airLineEntity.getValues().get(j) == true) {
						if (airLineEntity.getKeys().get(j)
								.equals(variableFlightList.get(i).getAirlineName())) {
							tmpList.add(variableFlightList.get(i));
						}
					}
				}
			}
			variableFlightList.clear();
			variableFlightList.addAll(tmpList);
		}

		public void searchCraftType() {
			if (craftTypeEntity.getValues().get(0) == true) {
				return;
			}
			ArrayList<Flight> tmpList = new ArrayList<Flight>();
			for (int i = 0; i < variableFlightList.size(); i++) {
				for (int j = 1; j < craftTypeEntity.getValues().size(); j++) {
					if (craftTypeEntity.getValues().get(j) == true) {
						if (craftTypeEntity.getKeys().get(j)
								.equals(variableFlightList.get(i).getFlightType())) {
							tmpList.add(variableFlightList.get(i));
						}

					}
				}
			}
			variableFlightList.clear();
			variableFlightList.addAll(tmpList);
		}

		public void searchTakeOff() {
			if (takeOffEntity.getValues().get(0) == true) {
				return;
			}
			ArrayList<Flight> tmpList = new ArrayList<Flight>();
			for (int i = 0; i < variableFlightList.size(); i++) {
				for (int j = 1; j < takeOffEntity.getValues().size(); j++) {
					if (takeOffEntity.getValues().get(j) == true) {
						int[] tmp = takeOffEntity.limtTime.get(j);
						if (isInLimt(variableFlightList.get(i).getTakeOffTime()
								.replace("T", " "), tmp[0], tmp[1])) {
							tmpList.add(variableFlightList.get(i));
						}
					}
				}
			}
			variableFlightList.clear();
			variableFlightList.addAll(tmpList);

		}

		public void searchAirPort() {
			if (dAirPortEntity.getValues().get(0) == true
					&& aAirPortEntity.getValues().get(0) == true) {
				return;
			}
			String dPortName = null;
			for (int j = 1; j < dAirPortEntity.getValues().size(); j++) {
				if (dAirPortEntity.getValues().get(j) == true) {
					dPortName = dAirPortEntity.getKeys().get(j);
				}
			}
			String aPortName = null;
			for (int j = 1; j < aAirPortEntity.getValues().size(); j++) {
				if (aAirPortEntity.getValues().get(j) == true) {
					aPortName = aAirPortEntity.getKeys().get(j);
				}
			}
			ArrayList<Flight> tmpList = new ArrayList<Flight>();
			for (int i = 0; i < variableFlightList.size(); i++) {
				String sdPortName = variableFlightList.get(i).getdPortName();
				String saPortName = variableFlightList.get(i).getaPortName();
				if (TextUtils.isEmpty(dPortName)
						&& TextUtils.isEmpty(aPortName)) {
					return;
				}
				if (sdPortName.equals(dPortName)
						&& saPortName.equals(aPortName)) {
					tmpList.add(variableFlightList.get(i));
					continue;
				}
				if (TextUtils.isEmpty(dPortName)
						&& saPortName.equals(aPortName)) {
					tmpList.add(variableFlightList.get(i));
					continue;
				}
				if (TextUtils.isEmpty(aPortName)
						&& sdPortName.equals(dPortName)) {
					tmpList.add(variableFlightList.get(i));
					continue;
				}
			}
			variableFlightList.clear();
			variableFlightList.addAll(tmpList);
		}
	}

	private void loadairportFrg() {
		clearBg();
		btn_airport.setBackground(getActivity().getResources().getDrawable(
				R.drawable.selected_table));
		searchList.setVisibility(View.GONE);
		ll_airport.setVisibility(View.VISIBLE);
		dAirPortList.setAdapter(dAirPortEntity.getAdapter());
		aAirPortList.setAdapter(aAirPortEntity.getAdapter());
	}

	private void loadsearchFrg(int flag, View view,
			FlightSearchEntityBase entityBase) {
		clearBg();
		searchList.setVisibility(View.VISIBLE);
		ll_airport.setVisibility(View.GONE);
		view.setBackground(getActivity().getResources().getDrawable(
				R.drawable.selected_table));
		BaseAdapter adapter = null;
		switch (flag) {
		case 1:
			adapter = new SearchMultipleAdapter(entityBase);
			break;
		case 2:
			adapter = new SearchSingleAdapter(entityBase);
			break;
		default:
			break;
		}
		searchList.setAdapter(adapter);

	}

	private void clearBg() {
		btn_airport.setBackgroundResource(0);
		btn_craftType.setBackgroundResource(0);
		btn_classType.setBackgroundResource(0);
		btn_airlineCompany.setBackgroundResource(0);
		btn_takeOff.setBackgroundResource(0);
	}

	public boolean hideSearch(final boolean doSearch) {
		parentActivity.setScanScroll(true);
		if (rl1.getVisibility() == View.VISIBLE
				&& searchContainer.getVisibility() == View.VISIBLE) {
			Animation anim = AnimationUtils.loadAnimation(getActivity(),
					R.anim.anim_search_container_out);
			anim.setAnimationListener(new AnimationListener() {

				@Override
				public void onAnimationStart(Animation animation) {

				}

				@Override
				public void onAnimationRepeat(Animation animation) {

				}

				@Override
				public void onAnimationEnd(Animation animation) {
					rl1.setVisibility(View.GONE);
					parentActivity.hideOcclusionBg();
					if (ll_condition.getChildCount() > 0) {
						iv_search_dot.setVisibility(View.VISIBLE);
					} else {
						iv_search_dot.setVisibility(View.GONE);
					}
					if (doSearch == true) {
						doSearch();
					}
				}
			});
			searchContainer.setAnimation(anim);
			searchContainer.setVisibility(View.GONE);
			return true;
		}
		return false;
	}

	private void showSearch() {
		if (variableFlightList == null) {
			return;
		}
		tv_screeningStr.setTextAppearance(getActivity(), R.style.description_v3);
		tv_priceStr.setTextAppearance(getActivity(), R.style.description_v4);
		tv_takeOffStr.setTextAppearance(getActivity(), R.style.description_v4);
		
		parentActivity.setScanScroll(false);
		img_screening.setImageResource(R.drawable.screening_selected);
		img_price.setImageResource(R.drawable.price_normal);
		img_orderbyprice.setVisibility(View.GONE);
		img_takeoff.setImageResource(R.drawable.takeoff_normal);
		img_orderbytime.setVisibility(View.GONE);

		if (rl1.getVisibility() == View.GONE
				&& searchContainer.getVisibility() == View.GONE) {
			parentActivity.showOcclusionBg();
			rl1.setVisibility(View.VISIBLE);
			searchContainer.setAnimation(AnimationUtils.loadAnimation(
					getActivity(), R.anim.anim_search_container_in));
			searchContainer.setVisibility(View.VISIBLE);
			loadsearchFrg(1, btn_takeOff, takeOffEntity);
		}
	}

	private void loadToady() {
		showOrHideCalendar();
	}

	private void loadPreOrNextDay(int value) {
		Date date = DateUtils.StringToDate(realDate, "yyyy-MM-dd");
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		c.add(Calendar.DAY_OF_YEAR, value);

		Calendar maxCalendar = Calendar.getInstance();
		maxCalendar.add(Calendar.MONTH, 5);
		
		if(!DateUtils.isSameDay(c.getTime(), maxCalendar.getTime())) {
			Date date2 = c.getTime();
			String dateStr = DateUtils.dateToStr(date2);
			realDate = dateStr;
			initTodayStr(dateStr);
			requestInfo.setDepartDate(dateStr);
			myCalendar.withSelectedDates(date2);
			if (myCalendar.getVisibility() == View.VISIBLE) {
				hideCalendar();
			}
			
			app.replaceTripName(dateStr);
			
			loadingData();
			c.add(Calendar.DAY_OF_YEAR, 1);
			if(DateUtils.isSameDay(c.getTime(), maxCalendar.getTime())) {
				ll_nextday.setEnabled(false);
				tv_nextDay.setTextColor(Color.GRAY);
			} else {
				ll_nextday.setEnabled(true);
				tv_nextDay.setTextColor(getResources().getColor(R.color.text_color_v4));
			}
		} 


	}

	public boolean showCalendar() {
		parentActivity.setScanScroll(false);
		img_occlusionbg.setVisibility(View.VISIBLE);
		float x = myCalendar.getX();
		float y = myCalendar.getY();
		int height = myCalendar.getHeight();
		TranslateAnimation calendarInAnimation = new TranslateAnimation(x, x, y
				- height, y);
		calendarInAnimation.setDuration(300);
		myCalendar.setAnimation(calendarInAnimation);
		myCalendar.setVisibility(View.VISIBLE);
		return false;
	}

	public boolean hideCalendar() {
		if (myCalendar != null && myCalendar.getVisibility() == View.VISIBLE) {
			parentActivity.setScanScroll(true);
			float x = myCalendar.getX();
			float y = myCalendar.getY();
			int height = myCalendar.getHeight();
			TranslateAnimation calendarOutAnimation = new TranslateAnimation(x,
					x, y, y - height);
			calendarOutAnimation.setDuration(300);
			calendarOutAnimation.setAnimationListener(new AnimationListener() {

				@Override
				public void onAnimationStart(Animation animation) {

				}

				@Override
				public void onAnimationRepeat(Animation animation) {

				}

				@Override
				public void onAnimationEnd(Animation animation) {
					img_occlusionbg.setVisibility(View.GONE);
				}
			});
			myCalendar.setAnimation(calendarOutAnimation);
			myCalendar.setVisibility(View.INVISIBLE);
			return true;
		}
		return false;
	}

	public boolean showOrHideCalendar() {
		if (myCalendar.getVisibility() == View.INVISIBLE) {
			parentActivity.setScanScroll(false);
			img_occlusionbg.setVisibility(View.VISIBLE);
			float x = myCalendar.getX();
			float y = myCalendar.getY();
			int height = myCalendar.getHeight();
			TranslateAnimation calendarInAnimation = new TranslateAnimation(x,
					x, y - height, y);
			calendarInAnimation.setDuration(300);
			myCalendar.setAnimation(calendarInAnimation);
			myCalendar.setVisibility(View.VISIBLE);
			return false;
		} else {
			parentActivity.setScanScroll(true);
			float x = myCalendar.getX();
			float y = myCalendar.getY();
			int height = myCalendar.getHeight();
			TranslateAnimation calendarOutAnimation = new TranslateAnimation(x,
					x, y, y - height);
			calendarOutAnimation.setDuration(300);
			calendarOutAnimation.setAnimationListener(new AnimationListener() {

				@Override
				public void onAnimationStart(Animation animation) {

				}

				@Override
				public void onAnimationRepeat(Animation animation) {

				}

				@Override
				public void onAnimationEnd(Animation animation) {
					img_occlusionbg.setVisibility(View.GONE);
				}
			});
			myCalendar.setAnimation(calendarOutAnimation);
			myCalendar.setVisibility(View.INVISIBLE);
			return true;
		}
	}

	private void initCalendar() {
		final Calendar nextYear = Calendar.getInstance();
		nextYear.add(Calendar.MONTH, 5);

		final Calendar lastYear = Calendar.getInstance();
		lastYear.add(Calendar.MONTH, 0);

		myCalendar.initCalendar(lastYear.getTime(), nextYear.getTime())
				.withSelectedDates(targetDate);
		myCalendar.hitTitle(true);
		myCalendar.setOnDateSelectedListener(new OnDateSelectedListener() {

			@Override
			public void onDateUnselected(Date date) {

			}

			@Override
			public void onDateSelected(Date date) {
				hideCalendar();
				String dateStr = DateUtils.dateToStr(date);
				realDate = dateStr;
				initTodayStr(dateStr);
				requestInfo.setDepartDate(dateStr);
				loadingData();
				app.replaceTripName(dateStr);
				
				Calendar maxCalendar = Calendar.getInstance();
				maxCalendar.add(Calendar.MONTH, 5);
				
				Calendar next = Calendar.getInstance();
				next.setTime(date);
				next.add(Calendar.DAY_OF_YEAR, 1);
				if(DateUtils.isSameDay(next.getTime(), maxCalendar.getTime())) {
					ll_nextday.setEnabled(false);
					tv_nextDay.setTextColor(Color.GRAY);
				} else {
					ll_nextday.setEnabled(true);
					tv_nextDay.setTextColor(getResources().getColor(R.color.text_color_v4));
				}
			}
		});
	}

	public void orderByTime() {
		if (variableFlightList == null || variableFlightList.size() == 0) {
			return;
		}
		
		tv_takeOffStr.setTextAppearance(getActivity(), R.style.description_v3);
		tv_priceStr.setTextAppearance(getActivity(), R.style.description_v4);
		tv_screeningStr.setTextAppearance(getActivity(), R.style.description_v4);
		
		img_screening.setImageResource(R.drawable.screening_normal);
		img_price.setImageResource(R.drawable.price_normal);
		img_orderbyprice.setVisibility(View.GONE);
		img_takeoff.setImageResource(R.drawable.takeoff_selected);
		img_orderbytime.setVisibility(View.VISIBLE);
		if (isByTimeAsc) {
			img_orderbytime.setImageResource(R.drawable.desc_arrow);
			isByTimeAsc = false;
			Collections.sort(variableFlightList, MyComparator.getFlightTimeDesc());
			myAdapter.notifyDataSetChanged();

		} else {
			img_orderbytime.setImageResource(R.drawable.asc_arrow);
			isByTimeAsc = true;
			Collections.sort(variableFlightList, MyComparator.getFlightTimeAsc());
			myAdapter.notifyDataSetChanged();
		}
		listView.setSelection(0);
	}

	public void orderByPrice() {
		if (variableFlightList == null || variableFlightList.size() == 0) {
			return;
		}
		
		tv_priceStr.setTextAppearance(getActivity(), R.style.description_v3);
		tv_screeningStr.setTextAppearance(getActivity(), R.style.description_v4);
		tv_takeOffStr.setTextAppearance(getActivity(), R.style.description_v4);
		
		img_screening.setImageResource(R.drawable.screening_normal);
		img_price.setImageResource(R.drawable.price_selected);
		img_orderbytime.setVisibility(View.GONE);
		img_takeoff.setImageResource(R.drawable.takeoff_normal);
		img_orderbyprice.setVisibility(View.VISIBLE);
		if (isByPriceAsc) {
			img_orderbyprice.setImageResource(R.drawable.desc_arrow);
			isByPriceAsc = false;
			Collections.sort(variableFlightList, MyComparator.getFlightPriceDesc());
			myAdapter.notifyDataSetChanged();
		} else {
			img_orderbyprice.setImageResource(R.drawable.asc_arrow);
			isByPriceAsc = true;
			Collections.sort(variableFlightList, MyComparator.getFlightPriceAsc());
			myAdapter.notifyDataSetChanged();
		}
		listView.setSelection(0);
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		Flight flight = variableFlightList.get(position);
		Intent intent = new Intent();
		intent.setClass(getActivity(), FlightticketsDetailsActivity.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
		Bundle bundle = new Bundle();
		bundle.putSerializable("flight", flight);
		intent.putExtras(bundle);
		this.startActivity(intent);
		YouJuAgent.onEvent(getActivity(), getActivity().getString(R.string.youju_flightdetails));
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = findViews(inflater);
		Intent intent = getActivity().getIntent();
		initParam(intent);
		initTodayStr(takeOffTime);
		return view;
	}

	public static FlightListFragment newInstance() {
		FlightListFragment flightFrg = new FlightListFragment();
		Bundle args = new Bundle();
		flightFrg.setArguments(args);
		return flightFrg;
	}

	private void initSearch() {
		airLineEntity = new AirLineEntity();
		airLineEntity.init();
		craftTypeEntity = new CraftTypeEntity();
		craftTypeEntity.init();
		classTypeEntity = new ClassTypeEntity();
		classTypeEntity.init();
		takeOffEntity = new TakeOffEntity();
		takeOffEntity.init();
		dAirPortEntity = new AirPortEntity("dPort");
		dAirPortEntity.init();
		new SearchSingleAdapter(dAirPortEntity);
		aAirPortEntity = new AirPortEntity("aPort");
		aAirPortEntity.init();
		new SearchSingleAdapter(aAirPortEntity);
		ll_condition.removeAllViews();
		scrollView.setVisibility(View.GONE);
		iv_search_dot.setVisibility(View.GONE);
	}

	private void initAirLineCompany(String airlineName) {
		airLineEntity.add(airlineName);
	}

	private void initParam(Intent intent) {
		takeOffTime = intent.getStringExtra("TakeOffTime");
		realDate = intent.getStringExtra("TakeOffTime");
		mDepartCity = intent.getStringExtra("departCity");
		mArriveCity = intent.getStringExtra("arriveCity");
		String flightReturnDate = intent.getStringExtra("flightReturnDate");
		if (!TextUtils.isEmpty(flightReturnDate)) {
			takeOffTime = flightReturnDate;
		}
		flightUrl = getString(R.string.gionee_host)+ FinalString.FLIGHT_INFO_URL;
		requestInfo = BeanUtil.createFlightInfoRequest(takeOffTime,
				mDepartCity, mArriveCity, null, null, null,null,null);
		params = new HashMap<String, String>();
		params.put("flightInfo", JSONUtil.getInstance().toJSON(requestInfo));
		
		myAdapter = new MyAdapter();
		listView.setAdapter(myAdapter);
		app = (TravelApplication)getActivity().getApplication();
	}

	private View findViews(LayoutInflater inflater) {
		View view = inflater.inflate(R.layout.fragment_list_flight, null);
		ll_preday = (ViewGroup) view.findViewById(R.id.ll_preday);
		ll_preday.setFocusable(true);
		ll_preday.setOnClickListener(this);
		ll_today = (ViewGroup) view.findViewById(R.id.ll_today);
		ll_today.setOnClickListener(this);
		ll_today.setFocusable(true);
		ll_nextday = (ViewGroup) view.findViewById(R.id.ll_nextday);
		ll_nextday.setOnClickListener(this);
		ll_nextday.setFocusable(true);
		tv_today = (TextView) view.findViewById(R.id.tv_today);
		listView = (ListView) view.findViewById(R.id.list);
		listView.setOnItemClickListener(this);
		orderbytime = (RelativeLayout) view.findViewById(R.id.orderbytime);
		orderbytime.setOnClickListener(this);
		orderbytime.setFocusable(true);
		imgWifiOrFace=(ImageView) view.findViewById(R.id.img_face_wifi);
		orderbyprice = (RelativeLayout) view.findViewById(R.id.orderbyprice);
		orderbyprice.setOnClickListener(this);
		orderbyprice.setFocusable(true);
		screening = (RelativeLayout) view.findViewById(R.id.screening);
		screening.setOnClickListener(this);
		screening.setFocusable(true);
		img_takeoff = (ImageView) view.findViewById(R.id.img_takeoff);
		img_price = (ImageView) view.findViewById(R.id.img_price);
		img_orderbytime = (ImageView) view.findViewById(R.id.img_orderbytime);
		img_orderbyprice = (ImageView) view.findViewById(R.id.img_orderbyprice);
		img_screening = (ImageView) view.findViewById(R.id.img_screening);
		myCalendar = (CustomCalendar) view.findViewById(R.id.cv_list);
		searchContainer = (ViewGroup) view.findViewById(R.id.searchContainer);
		searchContainer.setOnClickListener(this);
		view.findViewById(R.id.btn_cancel).setOnClickListener(this);
		view.findViewById(R.id.btn_clear).setOnClickListener(this);
		view.findViewById(R.id.btn_ok).setOnClickListener(this);
		view.findViewById(R.id.container).setOnClickListener(this);
		btn_takeOff = (View) view.findViewById(R.id.btn_takeOff);
		btn_takeOff.setOnClickListener(this);
		btn_airlineCompany = (View) view.findViewById(R.id.btn_airlineCompany);
		btn_airlineCompany.setOnClickListener(this);
		btn_craftType = (View) view.findViewById(R.id.btn_craftType);
		btn_craftType.setOnClickListener(this);
		btn_classType = (View) view.findViewById(R.id.btn_classType);
		btn_classType.setOnClickListener(this);
		btn_airport = (View) view.findViewById(R.id.btn_airport);
		btn_airport.setOnClickListener(this);
		searchList = (ListView) view.findViewById(R.id.searchList);
		dAirPortList = (ListView) view.findViewById(R.id.dAirPortList);
		aAirPortList = (ListView) view.findViewById(R.id.aAirPortList);
		ll_airport = (LinearLayout) view.findViewById(R.id.ll_airport);
		ll_condition = (LinearLayout) view.findViewById(R.id.conditions);
		view.findViewById(R.id.btn_clear).setOnClickListener(this);
		scrollView = (HorizontalScrollView) view.findViewById(R.id.scrollView);
		rl1 = (RelativeLayout) view.findViewById(R.id.rl1);
		rl1.setOnClickListener(this);
		rl_failed = (RelativeLayout) view.findViewById(R.id.rl_failed);
		rl_failed.setOnClickListener(this);
		img_occlusionbg = (ImageView) view.findViewById(R.id.img_occlusionbg);
		img_occlusionbg.setOnClickListener(this);
		rl_loading = (RelativeLayout) view.findViewById(R.id.rl_loading);
		rl_loading.setOnClickListener(this);
		img_loading = (ImageView) view.findViewById(R.id.img_loading);
		iv_search_dot = (ImageView) view.findViewById(R.id.iv_search_dot);
		tvRefresh=(TextView) view.findViewById(R.id.btn_refresh);
		tv_priceStr = (TextView) view.findViewById(R.id.tv_priceStr);
		tv_takeOffStr = (TextView) view.findViewById(R.id.tv_takeOffStr);
		tv_screeningStr = (TextView) view.findViewById(R.id.tv_screeningStr);
		
		tv_preday = (TextView) view.findViewById(R.id.tv_preday);
		tv_nextDay = (TextView) view.findViewById(R.id.tv_nextDay);
		return view;
	}

	private void initTodayStr(String strDate) {
		Date date = DateUtils.StringToDate(strDate, "yyyy-MM-dd");
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		tv_today.setText(DateUtils.dateDelYear(date)
				+ DateUtils.getDayOfWeek(c));
		if (isToady(c)) {
			ll_preday.setEnabled(false);
			tv_preday.setTextColor(Color.GRAY);
		} else {
			ll_preday.setEnabled(true);
			tv_preday.setTextColor(getResources().getColor(R.color.text_color_v4));
		}
		targetDate = date;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		initCalendar();
		flash = true;
		super.onActivityCreated(savedInstanceState);
	}

	public void loadingData() {
		startProgressDialog();
		flightlist.clear();
		variableFlightList.clear();
		if (!NetWorkUtil.isNetworkConnected(getActivity())) {
			Message msg = Message.obtain();
			msg.what = FinalString.NET_ERROR;
			handler.sendMessage(msg);
			return;
		}
		params.clear();
		params.put("flightInfo", JSONUtil.getInstance().toJSON(requestInfo));
		task = new HttpConnUtil4Gionee(this);
		task.execute(flightUrl, params, HttpConnUtil4Gionee.HttpMethod.POST);
	}

	@Override
	public void execute(String result) {
		if (TextUtils.isEmpty(result)) {
			Message msg = Message.obtain();
			msg.what = FinalString.NET_ERROR;
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
				if (!FinalString.ERRORCODE.equals(responseJson
						.getString("errorCode"))) {
					if (!mCancelLoad) {
						Message msg = Message.obtain();
						msg.what = FinalString.NOT_FOUND;
						msg.obj = responseJson.getString("errorMsg");
						handler.sendMessage(msg);
					}
					return;
				}
				JSONArray arr = new JSONArray(responseJson.getString("content"));
				for (int i = 0; i < arr.length(); i++) {
					JSONObject jsonAll = (JSONObject) arr.get(i);
					JSONArray classTypes = new JSONArray(jsonAll.getString("classTypes"));
					ArrayList<Flight> flightGroup = new ArrayList<Flight>();
					for (int j = 0; j < classTypes.length(); j++) {
						JSONObject jsonClassTypes = (JSONObject) classTypes.get(j);

						Flight flightItem = new Flight();
						flightItem.setTakeOffTime(jsonAll.getString("takeOffTime"));
						flightItem.setDepartCity(jsonAll.getString("departCity"));
						flightItem.setArriveCity(jsonAll.getString("arriveCity"));
						flightItem.setAirlineCode(jsonAll.getString("airlineCode"));
						flightItem.setArriveTime(jsonAll.getString("arriveTime"));
						flightItem.setFlight(jsonAll.getString("flight"));
						flightItem.setCraftType(jsonAll.getString("craftType"));// 机型
//						flightItem.setdPortBuildingID(jsonAll.getString("dPortBuildingID"));
//						flightItem.setaPortBuildingID(jsonAll.getString("aPortBuildingID"));
						flightItem.setdPortCode(jsonAll.getString("dPortCode"));
						flightItem.setaPortCode(jsonAll.getString("aPortCode"));
						flightItem.setMealType(jsonAll.getString("mealType"));
						flightItem.setPunctualityRate(jsonAll.getString("punctualityRate"));
						flightItem.setAirlineName(jsonAll.getString("airlineName"));
						flightItem.setAirlineShortName(jsonAll.getString("airlineShortName"));
						flightItem.setdPortName(jsonAll.getString("dPortName"));
						flightItem.setaPortName(jsonAll.getString("aPortName"));
						flightItem.setFlightType(jsonAll.getString("flightType"));
						flightItem.setClassType(jsonClassTypes.getString("classType"));
						flightItem.setRate(jsonClassTypes.getString("rate"));
						flightItem.setPrice(jsonClassTypes.getString("price"));
						flightItem.setQuantity(jsonClassTypes.getString("quantity"));

						flightGroup.add(flightItem);
					}
					flightlist.add(flightGroup);

					initAirLineCompany(jsonAll.getString("airlineName"));
					initPortName(1, jsonAll.getString("dPortName"));
					initPortName(2, jsonAll.getString("aPortName"));
				}
				if (!mCancelLoad) {
					Message msg = Message.obtain();
					msg.what = FinalString.DATA_FINISH;
					handler.sendMessage(msg);
				}
			} catch (JSONException e) {
				e.printStackTrace();
				if (!mCancelLoad) {
					Message msg = Message.obtain();
					msg.what = FinalString.NOT_FOUND;
					handler.sendMessage(msg);
				}
			}
		}
	}

	@Override
	public void onStart() {
		mCancelLoad = false;
		if (!mIsFinish) {
			loadingData();
		}

		super.onStart();
	}

	@Override
	public void onResume() {
		super.onResume();
	}

	@Override
	public void onPause() {
		super.onPause();
	}

	@Override
	public void onStop() {
		stopProgressDialog();
		if (task != null)
			task.cancel(true);
		mCancelLoad = true;
		super.onStop();
	}

	@Override
	public void onDestroyView() {
		stopProgressDialog();
		if (task != null)
			task.cancel(true);

		super.onDestroyView();
	}

	private class MyAdapter extends BaseAdapter {
		@Override
		public int getCount() {
			return variableFlightList.size();
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
				convertView = getActivity().getLayoutInflater().inflate(
						R.layout.flight_item, null);
				viewHolder = new ViewHolder();
				viewHolder.takeOffTime = (TextView) convertView
						.findViewById(R.id.takeOffTime);
				viewHolder.departCity = (TextView) convertView
						.findViewById(R.id.departCity);
				viewHolder.flight = (TextView) convertView
						.findViewById(R.id.flight);
				viewHolder.dollar = (TextView) convertView
						.findViewById(R.id.tv_dollar);
				viewHolder.price = (TextView) convertView
						.findViewById(R.id.price);
				viewHolder.arriveTime = (TextView) convertView
						.findViewById(R.id.arriveTime);
				viewHolder.arriveCity = (TextView) convertView
						.findViewById(R.id.arriveCity);
				viewHolder.dJClass = (TextView) convertView
						.findViewById(R.id.dJClass);
				viewHolder.quantity = (TextView) convertView
						.findViewById(R.id.quantity);
				viewHolder.rate = (TextView) convertView
						.findViewById(R.id.rate);
				convertView.setTag(viewHolder);
			} else {
				viewHolder = (ViewHolder) convertView.getTag();
			}
			viewHolder.takeOffTime.setText(fomatDate(variableFlightList.get(position)
					.getTakeOffTime()));
			viewHolder.departCity.setText(variableFlightList.get(position)
					.getDepartCity());
			viewHolder.flight.setText(variableFlightList.get(position)
					.getAirlineShortName()
					+ variableFlightList.get(position).getFlight());
			viewHolder.arriveTime.setText(fomatDate(variableFlightList.get(position)
					.getArriveTime()));
			viewHolder.arriveCity.setText(variableFlightList.get(position)
					.getArriveCity());
			viewHolder.dJClass.setText(variableFlightList.get(position)
					.getClassType());
			viewHolder.price.setText(variableFlightList.get(position).getPrice());
			viewHolder.rate.setText(variableFlightList.get(position).getRate());
			String rate = variableFlightList.get(position).getRate();
			if (rate.indexOf("折") != -1) {
				if (Float.parseFloat(rate.substring(0, rate.length() - 1)) <= 5.0f) {
					viewHolder.price.setTextColor(getActivity().getResources()
							.getColor(R.color.text_color_v8));
					viewHolder.dollar.setTextColor(getActivity().getResources()
							.getColor(R.color.text_color_v8));
				} else {
					viewHolder.price.setTextColor(getActivity().getResources()
							.getColor(R.color.text_color_v7));
					viewHolder.dollar.setTextColor(getActivity().getResources()
							.getColor(R.color.text_color_v7));
				}
			} else {
				viewHolder.price.setTextColor(getActivity().getResources()
						.getColor(R.color.text_color_v7));
				viewHolder.dollar.setTextColor(getActivity().getResources()
						.getColor(R.color.text_color_v7));
			}

			if (Integer.parseInt(variableFlightList.get(position).getQuantity()) < 5) {
				viewHolder.quantity.setText(variableFlightList.get(position)
						.getQuantity()
						+ getActivity().getString(R.string.zhang));
			} else {
				viewHolder.quantity.setText("");
			}
			return convertView;
		}
	}

	static class ViewHolder {
		public TextView takeOffTime;
		public TextView departCity;
		public TextView price;
		public TextView arriveTime;
		public TextView arriveCity;
		public TextView dJClass;
		public TextView rate;
		public TextView flight;
		public TextView quantity;
		public TextView dollar;
	}

	public String fomatDate(String date) {
		Date d = DateUtils.StringToDate(date.replace("T", " "),
				"yyyy-MM-dd HH:mm");
		return DateUtils.dateToTime(d);
	}

	private class SearchMultipleAdapter extends BaseAdapter {
		private ArrayList<String> keys;
		private ArrayList<Boolean> values;
		private ArrayList<String> tagNames;
		private FlightSearchEntityBase entity;

		public SearchMultipleAdapter(FlightSearchEntityBase entity) {
			this.keys = entity.getKeys();
			this.values = entity.getValues();
			tagNames = new ArrayList<String>();
			this.entity = entity;
			entity.setAdapter(this);
		}

		@Override
		public int getCount() {
			return keys.size();
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
			SearchViewHolder viewHolder = null;
			if (convertView == null) {
				convertView = getActivity().getLayoutInflater().inflate(
						R.layout.flight_search_item, null);
				viewHolder = new SearchViewHolder();
				viewHolder.key = (TextView) convertView.findViewById(R.id.key);
				viewHolder.value = (ImageView) convertView
						.findViewById(R.id.value);
				convertView.setTag(viewHolder);
			} else {
				viewHolder = (SearchViewHolder) convertView.getTag();
			}
			viewHolder.key.setText(keys.get(position));
			if (values.get(position) == false) {
				viewHolder.value
						.setImageResource(R.drawable.multiple_no_choice);
			} else {
				viewHolder.value.setImageResource(R.drawable.multiple_choice);
			}
			convertView.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					if (position == 0) {
						initValues();
						deleteMultipleConditions(tagNames);
					} else {
						values.set(0, false);
						if (values.get(position) == false) {
							values.set(position, true);
							tagNames.add(keys.get(position));
							addMultipleConditions(position, entity);
						} else {
							values.set(position, false);
							deleteSingleConditions(keys.get(position));
							if (!values.contains(true)) {
								values.set(0, true);
							}
						}
					}
					notifyDataSetChanged();
				}
			});
			return convertView;
		}

		public void initValues() {
			values.set(0, true);
			for (int i = 1; i < values.size(); i++) {
				values.set(i, false);
			}
		}
	}

	private class SearchSingleAdapter extends BaseAdapter {
		private ArrayList<String> keys;
		private ArrayList<Boolean> values;
		private FlightSearchEntityBase entity;

		public SearchSingleAdapter(FlightSearchEntityBase entity) {
			this.entity = entity;
			this.keys = entity.getKeys();
			this.values = entity.getValues();
			entity.setAdapter(this);
		}

		@Override
		public int getCount() {
			return keys.size();
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
			SearchViewHolder viewHolder = null;
			if (convertView == null) {
				convertView = getActivity().getLayoutInflater().inflate(
						R.layout.flight_search_item, null);
				viewHolder = new SearchViewHolder();
				viewHolder.key = (TextView) convertView.findViewById(R.id.key);
				viewHolder.value = (ImageView) convertView
						.findViewById(R.id.value);
				convertView.setTag(viewHolder);
			} else {
				viewHolder = (SearchViewHolder) convertView.getTag();
			}
			viewHolder.key.setText(keys.get(position));
			if (values.get(position) == false) {
				viewHolder.value
						.setImageResource(R.drawable.search_radiobtn_normal);
			} else {
				viewHolder.value
						.setImageResource(R.drawable.search_radiobtn_press);
			}
			convertView.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					if (values.get(position) == false) {
						values.remove(true);
						values.add(false);
						values.set(position, true);
						if (position == 0) {
							deleteSingleConditions(entity.getTagName());
						} else {
							addSingleConditions(position, entity);
						}

					}
					notifyDataSetChanged();
				}
			});
			return convertView;
		}
	}

	static class SearchViewHolder {
		public TextView key;
		public ImageView value;
	}

	public boolean isToady(Calendar targetCalendar) {
		Calendar currCalendar = Calendar.getInstance();
		if ((currCalendar.get(Calendar.YEAR) == targetCalendar
				.get(Calendar.YEAR))
				&& (currCalendar.get(Calendar.MONTH) == targetCalendar
						.get(Calendar.MONTH))
				&& (currCalendar.get(Calendar.DAY_OF_MONTH) == targetCalendar
						.get(Calendar.DAY_OF_MONTH))) {
			return true;
		} else {
			return false;
		}
	}

	public static boolean isInLimt(String testTime, int start, int end) {
		SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss",
				Locale.CHINA);
		Calendar startCal = Calendar.getInstance();
		Calendar endCal = Calendar.getInstance();
		try {
			Date date1 = sdf1.parse(testTime);
			Calendar testCal = Calendar.getInstance();
			testCal.setTime(date1);
			startCal.setTime(date1);
			endCal.setTime(date1);
			startCal.set(Calendar.HOUR_OF_DAY, start);
			startCal.set(Calendar.MINUTE, 0);
			startCal.set(Calendar.SECOND, 0);
			endCal.set(Calendar.HOUR_OF_DAY, end);
			endCal.set(Calendar.MINUTE, 0);
			endCal.set(Calendar.SECOND, 0);
			endCal.set(MILLISECOND, 0);
			return testCal.after(startCal) && testCal.before(endCal);
		} catch (ParseException e) {
			e.printStackTrace();
			return false;
		}
	}

	public void initPortName(int index, String portName) {
		switch (index) {
		case 1:
			dAirPortEntity.add(portName);
			break;
		case 2:
			aAirPortEntity.add(portName);
			break;
		default:
			break;
		}
	}

	public void addSingleConditions(int position,
			final FlightSearchEntityBase entity) {
		View tagView = ll_condition.findViewWithTag(entity.getTagName());
		if (tagView != null) {
			Button conditionBtn = (Button) tagView;
			conditionBtn.setText(entity.keys.get(position));
		} else {
			final Button conditionBtn = new Button(getActivity());
			int height = (int) TypedValue.applyDimension(
					TypedValue.COMPLEX_UNIT_PX,
					getResources().getDimension(R.dimen.general_btn_heigh),
					getResources().getDisplayMetrics());
			conditionBtn.setHeight(height);
			conditionBtn.setTextAppearance(getActivity(),
					R.style.button_v1);
			conditionBtn.setText(entity.keys.get(position));
			conditionBtn.setTag(entity.getTagName());
			conditionBtn.setBackgroundResource(R.drawable.white_btn_selector);
			conditionBtn.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					ArrayList<String> keys = entity.getKeys();
					int position = -1;
					for (int index = 0; index < keys.size(); index++) {
						if (conditionBtn.getText().equals(keys.get(index))) {
							position = index;
							break;
						}
					}
					entity.getValues().set(0, true);
					entity.getValues().set(position, false);
					ViewGroup parent = (ViewGroup) conditionBtn.getParent();
					parent.removeView(conditionBtn);
					if (parent.getChildCount() == 0) {
						scrollView.setVisibility(View.GONE);
					}
					entity.getAdapter().notifyDataSetChanged();
				}
			});
			LayoutParams lp = new LayoutParams(LayoutParams.WRAP_CONTENT,
					LayoutParams.WRAP_CONTENT);
			int margin = (int) TypedValue.applyDimension(
					TypedValue.COMPLEX_UNIT_PX,
					getResources().getDimension(R.dimen.general_margin_half),
					getResources().getDisplayMetrics());
			lp.setMargins(margin, 0, margin, 0);
			conditionBtn.setLayoutParams(lp);
			ll_condition.addView(conditionBtn, 0);
			if (scrollView.getVisibility() == View.GONE) {
				scrollView.setVisibility(View.VISIBLE);
			}
		}
	}

	public void deleteSingleConditions(String tagName) {
		View tagView = ll_condition.findViewWithTag(tagName);
		if (tagView != null) {
			deleteMyself(tagView);
		}
		showOrHideConditions(scrollView.getVisibility());
	}

	public void addMultipleConditions(int position,
			final FlightSearchEntityBase entity) {
		final Button conditionBtn = new Button(getActivity());
		int height = (int) TypedValue.applyDimension(
				TypedValue.COMPLEX_UNIT_PX,
				getResources().getDimension(R.dimen.general_btn_heigh),
				getResources().getDisplayMetrics());
		conditionBtn.setHeight(height);
		conditionBtn.setTextAppearance(getActivity(), R.style.button_v1);
		conditionBtn.setText(entity.keys.get(position));
		conditionBtn.setTag(entity.keys.get(position));
		conditionBtn.setBackgroundResource(R.drawable.white_btn_selector);
		conditionBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				ArrayList<String> keys = entity.getKeys();
				int position = -1;
				for (int index = 0; index < keys.size(); index++) {
					if (conditionBtn.getText().equals(keys.get(index))) {
						position = index;
						break;
					}
				}
				entity.getValues().set(position, false);
				if (!entity.getValues().contains(true)) {
					entity.getValues().set(0, true);
				}
				ViewGroup parent = (ViewGroup) conditionBtn.getParent();
				parent.removeView(conditionBtn);
				if (parent.getChildCount() == 0) {
					scrollView.setVisibility(View.GONE);
				}
				entity.getAdapter().notifyDataSetChanged();
			}
		});
		LayoutParams lp = new LayoutParams(LayoutParams.WRAP_CONTENT,
				LayoutParams.WRAP_CONTENT);
		int margin = (int) TypedValue.applyDimension(
				TypedValue.COMPLEX_UNIT_PX,
				getResources().getDimension(R.dimen.general_margin_half),
				getResources().getDisplayMetrics());
		lp.setMargins(margin, 0, margin, 0);
		conditionBtn.setLayoutParams(lp);
		ll_condition.addView(conditionBtn, 0);
		if (scrollView.getVisibility() == View.GONE) {
			scrollView.setVisibility(View.VISIBLE);
		}

	}

	public void deleteMultipleConditions(ArrayList<String> tagNames) {
		for (int index = 0; index < tagNames.size(); index++) {
			View tagView = ll_condition.findViewWithTag(tagNames.get(index));
			if (tagView != null) {
				deleteMyself(tagView);
			}
		}
		showOrHideConditions(scrollView.getVisibility());
	}

	public void deleteMyself(View view) {
		ViewGroup parent = (ViewGroup) view.getParent();
		parent.removeView(view);
	}

	public void clearConditions(FlightSearchEntityBase entity) {
		ArrayList<Boolean> values = entity.getValues();
		for (int index = 0; index < values.size(); index++) {
			if (index == 0) {
				values.set(0, true);
			} else {
				values.set(index, false);
			}
		}
		if (entity.getAdapter() != null) {
			entity.getAdapter().notifyDataSetChanged();
		}
	}

	public void showOrHideConditions(int state) {
		switch (state) {
		case View.GONE:
			if (ll_condition.getChildCount() != 0) {
				scrollView.setVisibility(View.VISIBLE);
			}
			break;
		case View.VISIBLE:
			if (ll_condition.getChildCount() == 0) {
				scrollView.setVisibility(View.GONE);
			}
			break;
		default:
			scrollView.setVisibility(View.GONE);
			break;
		}
	}

	@Override
	public boolean onBack() {
		if (myCalendar.getVisibility() == View.VISIBLE) {
			hideCalendar();
			return true;
		}
		return hideSearch(false);
	}

	@Override
	public void onAttach(Activity activity) {
		try {
			parentActivity = (ListOfMessageActivity) activity;
		} catch (ClassCastException e) {
			  throw new ClassCastException(activity.toString()+"must implement OnArticleSelectedListener");
		}
		super.onAttach(activity);
	}

	private void startProgressDialog() {
		initSearch();
		isByTimeAsc = true;
		img_orderbytime.setImageResource(R.drawable.asc_arrow);
		img_orderbytime.setVisibility(View.VISIBLE);
		isByPriceAsc = false;
		img_orderbyprice.setImageResource(0);
		img_orderbyprice.setVisibility(View.GONE);
		rl_failed.setVisibility(View.GONE);
		listView.setVisibility(View.VISIBLE);
		myAdapter.notifyDataSetChanged();
		rl_loading.setVisibility(View.VISIBLE);
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

	private boolean flash = true;
	private RelativeLayout rl_failed;
	private ImageView imgWifiOrFace;
	private ImageView img_loading;
	private RelativeLayout rl_loading;
	private ImageView iv_search_dot;
	private TextView tv_priceStr;
	private TextView tv_takeOffStr;
	private TextView tv_screeningStr;
	private TravelApplication app;
	private TextView tv_preday;
	private TextView tv_nextDay;

	@Override
	public void loadDate() {
		if (!flash) {
			loadingData();
			flash = true;
		}
	}
	
	private void showNotFoundMsg() {
		stopProgressDialog();
		if(isFirstLoad) {
			parentActivity.moveToHotel();
		} 
//		rl_failed.setBackground(getActivity().getResources().getDrawable(
//				R.drawable.notfound_bg));
		rl_failed.setVisibility(View.VISIBLE);
		imgWifiOrFace.setBackgroundResource(R.drawable.face);
		tvRefresh.setText(getString(R.string.not_find));
		listView.setVisibility(View.GONE);
		myAdapter.notifyDataSetChanged();
	}

	public void showNetErrorMsg() {
		stopProgressDialog();
//		rl_failed.setBackground(getActivity().getResources().getDrawable(
//				R.drawable.net_error_bg));
		rl_failed.setVisibility(View.VISIBLE);
		imgWifiOrFace.setBackgroundResource(R.drawable.wifi);
		rl_failed_flag = true;
		listView.setVisibility(View.GONE);
		myAdapter.notifyDataSetChanged();
	}

	public void loadDataFinish() {
		stopProgressDialog();
		if (flightlist.size() == 0) {
			showNotFoundMsg();
			return;
		}
		clearAllConditions();
		restructureFlight();
		myAdapter.notifyDataSetChanged();
	}

	/**
	 * 按价格排序找到每条航班中最便宜的航班，经济舱最便宜 其次公务舱，头等舱 有经济舱取经济舱 没有取商务舱，其次头等舱
	 */
	private void restructureFlight() {
		switch(DEFAULT_CLASSTYPE){
		case 1:
			variableFlightList.clear();
			for (int i = 0; i < flightlist.size(); i++) {
				ArrayList<Flight> group = flightlist.get(i);
				if(group != null && group.size() != 0 ) {
					Collections.sort(group, MyComparator.getFlightPriceAsc());
					variableFlightList.add(group.get(0));
				}
			}
			break;
		case 2:
			variableFlightList.clear();
			for(int i=0;i<flightlist.size();i++) {
				ArrayList<Flight> group = flightlist.get(i);
				ArrayList<Flight> tmp = new ArrayList<Flight>();
				for(int j=0;j<group.size();j++) {
					if("公务舱".equals(group.get(j).getClassType())) {
						tmp.add(group.get(j));
					}
				}
				if(tmp != null && tmp.size() != 0 ) {
					Collections.sort(tmp, MyComparator.getFlightPriceAsc());
					variableFlightList.add(tmp.get(0));
				}
			}
			break;
		case 3:
			variableFlightList.clear();
			for(int i=0;i<flightlist.size();i++) {
				ArrayList<Flight> group = flightlist.get(i);
				ArrayList<Flight> tmp = new ArrayList<Flight>();
				for(int j=0;j<group.size();j++) {
					if("头等舱".equals(group.get(j).getClassType())) {
						tmp.add(group.get(j));
					}
				}
				if(tmp != null && tmp.size() != 0 ) {
					Collections.sort(tmp, MyComparator.getFlightPriceAsc());
					variableFlightList.add(tmp.get(0));
				}
			}
			break;
		}
		

	}
	
	
	public boolean getCalendarState() {
		if (myCalendar != null && myCalendar.getVisibility() == View.VISIBLE) {
			return true;
		} else {
			return false;
		}
	}

	public boolean getSearchContentState() {
		if (rl1.getVisibility() == View.VISIBLE) {
			return true;
		} else {
			return false;
		}
	}

	public boolean getCurrentContentState() {
		return getCalendarState() || getSearchContentState();
	}

	public void hideAllContent() {
		hideCalendar();
		hideSearch(false);
	}

	@Override
	public boolean onKeyBackDown() {
		if (getCurrentContentState()) {
			hideCalendar();
			hideSearch(false);
			return true;
		}
		return false;
	}
	
	public void onReload(Intent intent) {
		initParam(intent);
		initTodayStr(takeOffTime);
		mCancelLoad = false;
		mIsFinish = false;
		loadDate();
	}
}
