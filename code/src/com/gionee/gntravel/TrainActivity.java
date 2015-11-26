package com.gionee.gntravel;

import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

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
import android.view.Gravity;
import android.view.KeyEvent;
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
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.gionee.gntravel.entity.Ticket;
import com.gionee.gntravel.entity.Train;
import com.gionee.gntravel.entity.TrainRequestInfo;
import com.gionee.gntravel.utils.DateUtils;
import com.gionee.gntravel.utils.FinalString;
import com.gionee.gntravel.utils.HttpConnCallback;
import com.gionee.gntravel.utils.HttpConnUtil4Gionee;
import com.gionee.gntravel.utils.JSONUtil;
import com.gionee.gntravel.utils.NetWorkUtil;
import com.gionee.gntravel.utils.TrainComparator;
import com.gionee.gntravel.utils.UnitUtil;
import com.gionee.gntravel.utils.Utils;
import com.weiqun.customcalendar.CustomCalendar;
import com.weiqun.customcalendar.CustomCalendar.OnDateSelectedListener;

public class TrainActivity extends Activity implements HttpConnCallback,
		View.OnClickListener, AdapterView.OnItemClickListener {

	private ArrayList<Train> trainList=new ArrayList<Train>();
	private ArrayList<Train> orderTrainList;
	private HashMap<String, String> params;
	private ImageView img_occlusionbg;
	private ResultHandler handler = new ResultHandler();
	private MyAdapter myAdapter;
	private ListView listView;
	private RelativeLayout orderbytime;
	private RelativeLayout orderbyprice;
	private RelativeLayout orderbyTotalprice;
	private RelativeLayout screening;
	private RelativeLayout searchContainer;
	private boolean isByTimeAsc = true;
	private boolean isByPriceAsc = false;
	private HttpConnUtil4Gionee task;
	private TextView tv_today;
	private ViewGroup ll_preday;
	private ViewGroup ll_nextday;
	private ViewGroup ll_today;
	private CustomCalendar myCalendar;
	private static final String TAG = "FlightListFragment";
	private String departCity;
	private String arriveCity;
	private String takeOffTime;
	private String hour;
	private String min;
	private TrainRequestInfo requestInfo;
	private ImageView imgStartTime;
	private TextView tvStartime;
	private ImageView imgStartTimeArrow;
	private ImageView imgCountTime;
	private TextView tvCounttime;
	private ImageView imgCountArrow;
	private ImageView imgPrvce;
	private TextView tvPrice;
	private ImageView imgPriceArrow;
	private ImageView imgscreening;
	private TextView tvScreening;
	private ImageView img_loading;
	private RelativeLayout rl_loading;
	private HorizontalScrollView horizontalScrollView;
	private TextView tvTitle;// 标题名称
	private String time;// 今天上显示的时间

	/*
	 * 以下为筛选,到下划线
	 */
	private Map<String, String> map1 = new HashMap<String, String>();
	private Map<String, String> map2 = new HashMap<String, String>();
	private Map<String, String> map3 = new HashMap<String, String>();
	private Map<String, String> map4 = new HashMap<String, String>();
	private String[] arr1 = null;
	// 对应的类型 高速 特快 快速
	private String[] arr2 = null;
	private String[] arr3 = null;
	private int[] btnIds = { R.id.btn_tarins_shaixuan_1,R.id.btn_tarins_shaixuan_2, R.id.btn_tarins_shaixuan_3,R.id.btn_tarins_shaixuan_4 };
	private int[] reIds = { R.id.re_tarins_shaixuan_1,R.id.re_tarins_shaixuan_2, R.id.re_tarins_shaixuan_3,R.id.re_tarins_shaixuan_4 };
	private TextView[] btns = new TextView[4]; // 4个筛选按钮
	private ListView listViewshuaixuan = null;
	private RelativeLayout[] res = new RelativeLayout[4];
	private RelativeLayout reShaixuan;// 筛选整个布局
	private Button btnShaixuanCancel;// 筛选取消
	private Button btnShaixuanSure;// 筛选确定
	private Button btnShaixuanNull;// 筛选清空
	private String trainNum;// 单选车次类型
	private String trainSeatTypeName;// 座席类型
	private String trainStartTime;// 选择的出发时间
	private String trainArriveTime;// 选择的到达时间
	private LinearLayout layoutShaiXuanAddView;
	private int shaiXuanAddViewId1 = 0;
	private int shaiXuanAddViewId2 = 0;
	private int shaiXuanAddViewId3 = 0;
	private int shaiXuanAddViewId4 = 0;

	private TextView textView1;
	private TextView textView2;
	private TextView textView3;
	private TextView textView4;
	private ArrayList<String> classTypesKey1;
	private ArrayList<String> classTypesKey2;
	private ArrayList<String> classTypesKey3;
	private ArrayList<String> classTypesKey4;
	private ArrayList<Boolean> classTypesValue1;
	private ArrayList<Boolean> classTypesValue2;
	private ArrayList<Boolean> classTypesValue3;
	private ArrayList<Boolean> classTypesValue4;
	private ClassTypeAdapter1 classTypeAdapter1;
	private ClassTypeAdapter2 classTypeAdapter2;
	private ClassTypeAdapter3 classTypeAdapter3;
	private ClassTypeAdapter4 classTypeAdapter4;
	private RelativeLayout rl_failed;

	private class ResultHandler extends Handler {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case FinalString.DATA_FINISH:
				stopProgressDialog();
				if (orderTrainList.size() == 0) {
					showNotFoundMsg();
				}
				myAdapter.changeData(orderTrainList);
				
				break;
			case FinalString.NET_ERROR:
				showNetErrorMsg();
				break;
			}
		}

	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.fragment_list_train);
		findViews();
		initParam();
		initCalendar();
		loadingData();
	}

	private void initParam() {
		requestInfo = new TrainRequestInfo();
		requestInfo.setStart(departCity);
		requestInfo.setStop(arriveCity);
		requestInfo.setStartTime(takeOffTime);
		requestInfo.setTrainType("");
		requestInfo.setDepartureTime("");
		requestInfo.setArrivalTime("");
		requestInfo.setMustStartStation("false");
		requestInfo.setMustEndStation("false");
		requestInfo.setHaveTickets("0");
		params = new HashMap<String, String>();
	}

	public void showFailedMsg() {
		stopProgressDialog();
		rl_failed.setVisibility(View.VISIBLE);
		listView.setVisibility(View.GONE);
	}

	private void findViews() {
		rl_loading = (RelativeLayout) findViewById(R.id.rl_loading);
		img_loading = (ImageView) findViewById(R.id.img_loading);
		arr1 = new String[] { getString(R.string.not_limite),
				getString(R.string.gdc), getString(R.string.tz),
				getString(R.string.k) };
		arr2 = new String[] { getString(R.string.not_limite),
				getString(R.string.yingzuo), getString(R.string.yingwo),
				getString(R.string.ruanwo), getString(R.string.erdeng),
				getString(R.string.yideng), getString(R.string.tedeng),
				getString(R.string.shangwu_seat) };
		arr3 = new String[] { getString(R.string.not_limite),
				getString(R.string.lingchen), getString(R.string.shangwu),
				getString(R.string.xiawu), getString(R.string.wanshang) };
		rl_failed = (RelativeLayout) findViewById(R.id.rl_failed);
		rl_failed.setOnClickListener(this);
		img_occlusionbg = (ImageView) findViewById(R.id.img_occlusionbg);
		img_occlusionbg.setOnClickListener(this);
		departCity = getIntent().getStringExtra("departcity");
		arriveCity = getIntent().getStringExtra("arrivecity");
		takeOffTime = getIntent().getStringExtra("TakeOffTime");
		tvTitle = (TextView) findViewById(R.id.tv_title);
		tvTitle.setText(R.string.trainticketsDetails);
		tvTitle.setOnClickListener(this);
		imgStartTime = (ImageView) findViewById(R.id.img_orderbytime_train);
		tvStartime = (TextView) findViewById(R.id.tv_orderbytime_train);
		imgStartTimeArrow = (ImageView) findViewById(R.id.img_orderbytime_train_arrow);
		imgCountTime = (ImageView) findViewById(R.id.img_orderby_count_time_train);
		tvCounttime = (TextView) findViewById(R.id.tv_orderby_count_time_train);
		imgCountArrow = (ImageView) findViewById(R.id.img_orderby_count_time_train_arrow);
		imgPrvce = (ImageView) findViewById(R.id.img_orderbyprice_train);
		tvPrice = (TextView) findViewById(R.id.tv_orderbyprice_train);
		imgPriceArrow = (ImageView) findViewById(R.id.img_orderbyprice_train_arrow);
		imgscreening = (ImageView) findViewById(R.id.img_screening_train);
		tvScreening = (TextView) findViewById(R.id.tv_screening_train);
		horizontalScrollView = (HorizontalScrollView) findViewById(R.id.hs_shaixuan_addview);
		btnShaixuanCancel = (Button) findViewById(R.id.btn_shaixuan_cancel);
		btnShaixuanCancel.setOnClickListener(this);
		btnShaixuanSure = (Button) findViewById(R.id.btn_shaixuan_sure);
		btnShaixuanSure.setOnClickListener(this);
		btnShaixuanNull = (Button) findViewById(R.id.btn_shaixuan_null);
		btnShaixuanNull.setOnClickListener(this);
		listViewshuaixuan = (ListView) findViewById(R.id.lv_trains_shuaixuan_1);
		for (int i = 0; i < 4; i++) {
			btns[i] = (TextView) findViewById(btnIds[i]);
			btns[i].setOnClickListener(this);
			res[i] = (RelativeLayout) findViewById(reIds[i]);
		}

		map1.put(getString(R.string.traintype), getString(R.string.not_limite));
		initSearch1();
		classTypeAdapter1 = new ClassTypeAdapter1();
		listViewshuaixuan.setAdapter(classTypeAdapter1);
		map2.put(getString(R.string.seattype), getString(R.string.not_limite));
		initSearch2();
		classTypeAdapter2 = new ClassTypeAdapter2();
		map3.put(getString(R.string.starttime), getString(R.string.not_limite));
		initSearch3();
		classTypeAdapter3 = new ClassTypeAdapter3();
		map4.put(getString(R.string.arrivetime), getString(R.string.not_limite));
		initSearch4();
		classTypeAdapter4 = new ClassTypeAdapter4();
		searchContainer = (RelativeLayout) findViewById(R.id.re_searchContainer);
		reShaixuan = (RelativeLayout) findViewById(R.id.re_shaixuan);
		reShaixuan.setOnClickListener(this);
		listView = (ListView) findViewById(R.id.list_train);
		listView.setOnItemClickListener(this);
		orderbytime = (RelativeLayout) findViewById(R.id.orderbytime_train);
		orderbytime.setOnClickListener(this);
		orderbyTotalprice = (RelativeLayout) findViewById(R.id.orderby_count_time_train);
		orderbyTotalprice.setOnClickListener(this);
		orderbyprice = (RelativeLayout) findViewById(R.id.orderbyprice_train);
		orderbyprice.setOnClickListener(this);
		screening = (RelativeLayout) findViewById(R.id.screening_train);
		screening.setOnClickListener(this);
		layoutShaiXuanAddView = (LinearLayout) findViewById(R.id.ll_shaixuan_addview);
		layoutShaiXuanAddView.setGravity(Gravity.CENTER_VERTICAL);
		ll_preday = (ViewGroup) findViewById(R.id.ll_preday_train);
		ll_preday.setOnClickListener(this);
		ll_today = (ViewGroup) findViewById(R.id.ll_today_train);
		ll_today.setOnClickListener(this);
		ll_nextday = (ViewGroup) findViewById(R.id.ll_nextday_train);
		ll_nextday.setOnClickListener(this);
		myCalendar = (CustomCalendar) findViewById(R.id.cv_list_train);
		tv_today = (TextView) findViewById(R.id.tv_today_train);
		initTodayStr(takeOffTime);
		myAdapter=new MyAdapter(trainList);
		listView.setAdapter(myAdapter);
	}

	private void initTodayStr(String dateStr) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		try {
			Date date = sdf.parse(dateStr);
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(date);
			time = DateUtils.dateDelYear(date)
					+ DateUtils.getDayOfWeek(calendar);
			tv_today.setText(time);

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public String dateFormat(Date date) {
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		String dateStr = dateFormat.format(date);
		return dateStr;
	}

	public void loadingData() {
		if (trainList != null) {
			trainList.clear();
		} 
		clearscreen();
		startProgressDialog();
		if (!NetWorkUtil.isNetworkConnected(this)) {
			Message msg = Message.obtain();
			msg.what = FinalString.NET_ERROR;
			handler.sendMessageDelayed(msg, 1000);
			return;
		}
		String trainUrl = getString(R.string.gionee_host)+ FinalString.TRAIN_STATION_URL;
		params.put("json",URLEncoder.encode(JSONUtil.getInstance().toJSON(requestInfo)));
		task = new HttpConnUtil4Gionee(this);
		task.execute(trainUrl, params, HttpConnUtil4Gionee.HttpMethod.GET);
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int position,
			long arg3) {
		Train train = orderTrainList.get(position);
		Intent intent = new Intent();
		intent.setClass(this, TrainticketsDetailsActivity.class);
		Bundle bundle = new Bundle();
		intent.putExtra("departCity", departCity);
		intent.putExtra("arriveCity", arriveCity);
		bundle.putSerializable("train", train);
		bundle.putString("time", time);
		intent.putExtras(bundle);
		this.startActivity(intent);
	}

	public void orderByTime() {
		if (orderTrainList != null && orderTrainList.size() > 0) {

			if (isByTimeAsc) {
				isByTimeAsc = false;
				imgStartTimeArrow.setImageResource(R.drawable.desc_arrow);
				Collections.sort(orderTrainList,
						TrainComparator.getTrainTimeDesc());
				myAdapter.changeData(orderTrainList);
			} else {
				isByTimeAsc = true;
				Collections.sort(orderTrainList,
						TrainComparator.getTrainTimeAsc());
				imgStartTimeArrow.setImageResource(R.drawable.asc_arrow);
				myAdapter.changeData(orderTrainList);
			}
		}
	}

	public void orderByPrice() {
		if (orderTrainList != null && orderTrainList.size() > 0) {
			if (isByPriceAsc) {
				isByPriceAsc = false;
				imgPriceArrow.setImageResource(R.drawable.desc_arrow);
				Collections.sort(orderTrainList,
						TrainComparator.getTrainPriceDesc());
				myAdapter.changeData(orderTrainList);
			} else {
				isByPriceAsc = true;
				imgPriceArrow.setImageResource(R.drawable.asc_arrow);
				Collections.sort(orderTrainList,
						TrainComparator.getTrainPriceAsc());
				myAdapter.changeData(orderTrainList);
			}
		}
	}

	private void orderByTotalTime() {
		if (orderTrainList != null && orderTrainList.size() > 0) {
			if (isByPriceAsc) {
				isByPriceAsc = false;
				imgCountArrow.setImageResource(R.drawable.desc_arrow);
				Collections.sort(orderTrainList,
						TrainComparator.getTrainTotalTimeDesc());
				myAdapter.changeData(orderTrainList);
			} else {
				isByPriceAsc = true;
				imgCountArrow.setImageResource(R.drawable.asc_arrow);
				Collections.sort(orderTrainList,
						TrainComparator.getTrainTotalTimeAsc());
				myAdapter.changeData(orderTrainList);
			}
		}
	}

	private ArrayList<Train> getShaiXuanTrainList() {
		ArrayList<Train> listShaiXuan = new ArrayList<Train>();
		for (int i = 0; trainList != null && i < trainList.size(); i++) {
			if (getShaiXuanTypeName(i, trainNum)
					&& getShaiXuanStartTime(i, trainStartTime)
					&& getShaiXuanArriveTime(i, trainArriveTime)
					&& getShaiXuanSeatTypename(i, trainSeatTypeName)) {
				listShaiXuan.add(trainList.get(i));
			}
		}
		System.out.println("listShaiXuan de 大小===" + listShaiXuan.size());
		return listShaiXuan;
	}

	/*
	 * 第一个筛选条件，根据车次类型筛选
	 */
	private Boolean getShaiXuanTypeName(int i, String name) {
		if (getString(R.string.not_limite).equals(name)) {// 不限
			return true;
			//
		} else if (getString(R.string.gdc_gn).equals(name)
				&& (trainList.get(i).getTrainTypeName()
						.equals(getString(R.string.gaosu)) || trainList.get(i)
						.getTrainTypeName().equals(getString(R.string.dongche))// 高铁动车

				)) {
			return true;
		} else if (getString(R.string.tekuai).equals(name)
				&& (getString(R.string.tekuai).equals(
						trainList.get(i).getTrainTypeName()) || getString(
						R.string.zhidatekuai).equals(
						trainList.get(i).getTrainTypeName()))) {// 特快 T Z
			return true;
		} else if (getString(R.string.k_gn).equals(name)
				&& (getString(R.string.kuaisu).equals(
						trainList.get(i).getTrainTypeName()) || getString(
						R.string.puke).equals(
						trainList.get(i).getTrainTypeName()))) {// k 1267
			return true;
		}

		return false;

	}

	/*
	 * 点击的车次类型
	 */
	private void setShaiXuanTypeName() {
		trainNum = map1.get(getString(R.string.traintype));
		if (trainNum.equals(arr1[1])) {// 高铁动车
			trainNum = getString(R.string.gdc_gn);
		} else if (trainNum.equals(arr1[2])) {// T Z
			trainNum = getString(R.string.tekuai);
		} else if (trainNum.equals(arr1[3])) {// K 1245
			trainNum = getString(R.string.k_gn);
		}
	}

	/*
	 * 选择的出发时间
	 */
	private void setShaiXuanStartTime() {

		trainStartTime = map3.get(getString(R.string.starttime));
		if (trainStartTime.equals(arr3[1])) {
			trainStartTime = "00:00-06:00";
		} else if (trainStartTime.equals(arr3[2])) {
			trainStartTime = "06:00-12:00";
		} else if (trainStartTime.equals(arr3[3])) {
			trainStartTime = "12:00-18:00";
		} else if (trainStartTime.equals(arr3[4])) {
			trainStartTime = "18:00-24:00";
		}
	}

	/*
	 * 选择的到达时间
	 */
	private void setShaiXuanArriveTime() {

		trainArriveTime = map4.get(getString(R.string.arrivetime));
		if (trainArriveTime.equals(arr3[1])) {
			trainArriveTime = "00:00-06:00";
		} else if (trainArriveTime.equals(arr3[2])) {
			trainArriveTime = "06:00-12:00";
		} else if (trainArriveTime.equals(arr3[3])) {
			trainArriveTime = "12:00-18:00";
		} else if (trainArriveTime.equals(arr3[4])) {
			trainArriveTime = "18:00-24:00";
		}
	}

	/*
	 * 第二个筛选 多选 复杂
	 */
	private Boolean getShaiXuanSeatTypename(int i, String name) {

		if (getString(R.string.not_limite).equals(name)) {
			return true;
		} else {
			for (int m = 0; m < trainList.get(i).getTicketList().size(); m++) {
				if (trainList.get(i).getTicketList().get(m).getSeatTypeName()
						.startsWith(name)) {
					return true;
				}
			}
		}
		return false;

	}

	/*
	 * 第三个筛选条件，根据出发类型筛选
	 */
	private Boolean getShaiXuanStartTime(int i, String startTime) {

		if (getString(R.string.not_limite).equals(startTime)) {
			return true;
		} else {

			String str2 = trainList.get(i).getStartTime();
			int start = Integer.valueOf(startTime.substring(0, 2));// 获取时间段开始时间
			int end = new Integer(startTime.substring(6, 8));// 获取时间段开始时间
			int timePointHour = new Integer(str2.substring(0, 2));// 获取时间点 小时段
			int timePointMinute = new Integer(str2.substring(3, 5));// 获取时间点 小时段
			if (timePointHour >= start && timePointHour < end) {
				return true;
			} else if (timePointHour == end && timePointMinute == 0) {
				return true;
			}
			return false;
		}
	}

	/*
	 * 第四个筛选条件，根据到达时间类型筛选
	 */
	private Boolean getShaiXuanArriveTime(int i, String ArriveTime) {

		if (getString(R.string.not_limite).equals(ArriveTime)) {
			return true;
		} else {

			String str2 = trainList.get(i).getArrivalTime();
			int start = Integer.valueOf(ArriveTime.substring(0, 2));// 获取时间段开始时间
			int end = new Integer(ArriveTime.substring(6, 8));// 获取时间段开始时间
			System.out.println("start:" + start + "    end:" + end);
			int timePointHour = new Integer(str2.substring(0, 2));// 获取时间点 小时段
			int timePointMinute = new Integer(str2.substring(3, 5));// 获取时间点 小时段
			System.out.println("timePointHour:" + timePointHour
					+ " timePointMinute:" + timePointMinute);
			if (timePointHour >= start && timePointHour < end) {
				return true;
			} else if (timePointHour == end && timePointMinute == 0) {
				return true;
			}
			return false;
		}
	}

	private void clearBg() {
		for (int i = 0; i < res.length; i++) {
			res[i].setBackgroundResource(0);
		}

	}

	private void showSearch() {
		if (reShaixuan.getVisibility() == View.GONE
				&& searchContainer.getVisibility() == View.GONE) {
			searchContainer.setAnimation(AnimationUtils.loadAnimation(this,
					R.anim.anim_search_container_in));
			reShaixuan.setVisibility(View.VISIBLE);
			searchContainer.setVisibility(View.VISIBLE);
		}
	}

	private void clearscreen() {
		initSearch1();
		initSearch2();
		initSearch3();
		initSearch4();
		map1.put(getString(R.string.traintype), getString(R.string.not_limite));
		map2.put(getString(R.string.seattype), getString(R.string.not_limite));
		map3.put(getString(R.string.starttime), getString(R.string.not_limite));
		map4.put(getString(R.string.arrivetime), getString(R.string.not_limite));
		classTypeAdapter1.notifyDataSetChanged();
		classTypeAdapter2.notifyDataSetChanged();
		classTypeAdapter3.notifyDataSetChanged();
		classTypeAdapter4.notifyDataSetChanged();
		shaiXuanAddViewId1 = 0;
		shaiXuanAddViewId2 = 0;
		shaiXuanAddViewId3 = 0;
		shaiXuanAddViewId4 = 0;
		layoutShaiXuanAddView.removeAllViews();
		horizontalScrollView.setVisibility(View.GONE);
		btnShaixuanNull.setBackgroundResource(R.drawable.white_btn_pressed);
	}
	private void setStyle(int a) {
		imgStartTime.setImageResource(R.drawable.img_start_time_no_select);
		imgStartTimeArrow.setVisibility(View.INVISIBLE);
		tvStartime.setTextColor(getResources().getColor(
				R.color.text_color_v9));
		imgCountTime.setImageResource(R.drawable.img_waste_time_no_select);
		imgCountArrow.setVisibility(View.INVISIBLE);
		tvCounttime.setTextColor(getResources().getColor(
				R.color.text_color_v9));
		imgPrvce.setImageResource(R.drawable.price_normal);
		imgPriceArrow.setVisibility(View.INVISIBLE);
		tvPrice.setTextColor(getResources().getColor(R.color.text_color_v9));
		imgscreening.setImageResource(R.drawable.screening_normal);
		tvScreening.setTextColor(getResources().getColor(
				R.color.text_color_v9));
		switch (a) {
		case 1:
			imgStartTime.setImageResource(R.drawable.img_start_time_select);
			imgStartTimeArrow.setVisibility(View.VISIBLE);
			tvStartime.setTextColor(getResources().getColor(
					R.color.text_color_v1));
			break;
		case 2:
			imgCountTime.setImageResource(R.drawable.img_waste_time_select);
			imgCountArrow.setVisibility(View.VISIBLE);
			tvCounttime.setTextColor(getResources().getColor(
					R.color.text_color_v1));
			break;

		case 3:

			imgPrvce.setImageResource(R.drawable.price_selected);
			imgPriceArrow.setVisibility(View.VISIBLE);
			tvPrice.setTextColor(getResources().getColor(
					R.color.text_color_v1));
			break;
		case 4:
			imgscreening.setImageResource(R.drawable.screening_selected);
			tvScreening.setTextColor(getResources().getColor(
					R.color.text_color_v1));
			break;

		default:
			break;
		}
	}

	private void loadToady() {
		showOrHideCalendar();
	}

	private void preday() {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		try {
			Date date = sdf.parse(takeOffTime);
			Calendar c = Calendar.getInstance();
			c.setTime(date);
			c.add(Calendar.DAY_OF_YEAR, -1);
			Date date2 = c.getTime();
			String dateStr = sdf.format(date2);
			takeOffTime = dateStr;
			initTodayStr(dateStr);
			requestInfo.setStartTime(dateStr);
			loadingData();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void nextDay() {
		SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd");
		try {
			Date date = sdf1.parse(takeOffTime);
			Calendar c = Calendar.getInstance();
			c.setTime(date);
			c.add(Calendar.DAY_OF_YEAR, 1);
			Date date2 = c.getTime();
			String dateStr = sdf1.format(date2);
			takeOffTime = dateStr;
			initTodayStr(dateStr);
			requestInfo.setStartTime(dateStr);
			loadingData();
		} catch (Exception e) {
			e.printStackTrace();
		}
}
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.re_shaixuan:
			hideSearch();
			break;
		case R.id.rl_failed:
			loadingData();
			break;
		case R.id.tv_title:
			this.finish();
			break;
		case R.id.orderbytime_train:
			setStyle(1);
			orderByTime();
			break;
		case R.id.img_occlusionbg:
			showOrHideCalendar();
			break;
		case R.id.orderbyprice_train:
			setStyle(3);
			orderByPrice();
			break;
		case R.id.orderby_count_time_train:
			setStyle(2);
			orderByTotalTime();
			break;
		case R.id.screening_train:
			setStyle(4);
			showSearch();
			clearBg();
			res[0].setBackground(getResources().getDrawable(R.drawable.selected_table));
			break;
		case R.id.ll_preday_train:
			preday();
			break;

		case R.id.ll_nextday_train:
			nextDay();
			break;

		case R.id.ll_today_train:
			loadToady();
			break;
		case R.id.btn_shaixuan_cancel:
			hideSearch();
			break;
		case R.id.btn_shaixuan_sure:
			hideSearch();
			setShaiXuanTypeName();
			trainSeatTypeName = map2.get(getString(R.string.seattype));
			setShaiXuanStartTime();
			setShaiXuanArriveTime();
			orderTrainList = getShaiXuanTrainList();
			// 刷新界面，通知

			myAdapter.changeData(orderTrainList);
			if (orderTrainList.size() == 0) {
				Toast.makeText(this, R.string.not_exist_train,
						Toast.LENGTH_SHORT).show();

			}
			break;
		case R.id.btn_shaixuan_null:
			clearscreen();
			break;

		case R.id.btn_tarins_shaixuan_1:
			clearBg();
			res[0].setBackground(this.getResources().getDrawable(
					R.drawable.selected_table));
			classTypeAdapter1 = new ClassTypeAdapter1();
			listViewshuaixuan.setAdapter(classTypeAdapter1);

			break;
		case R.id.btn_tarins_shaixuan_2:
			clearBg();
			res[1].setBackground(this.getResources().getDrawable(
					R.drawable.selected_table));
			listViewshuaixuan.setAdapter(classTypeAdapter2);

			break;

		case R.id.btn_tarins_shaixuan_3:
			clearBg();
			res[2].setBackground(this.getResources().getDrawable(
					R.drawable.selected_table));
			listViewshuaixuan.setAdapter(classTypeAdapter3);

			break;
		case R.id.btn_tarins_shaixuan_4:
			clearBg();
			res[3].setBackground(this.getResources().getDrawable(
					R.drawable.selected_table));
			listViewshuaixuan.setAdapter(classTypeAdapter4);

			break;

		default:
			break;
		}

	}

	private void initTypeValues(ArrayList<String> tempListKey,ArrayList<Boolean> tempListVlue) {
		for (int i = 0; i < tempListKey.size(); i++) {
			if (i == 0) {
				tempListVlue.add(true);
			} else {
				tempListVlue.add(false);
			}
		}
	}
	private void initSearch1() {
		classTypesKey1 = new ArrayList<String>();
		classTypesKey1.add(getString(R.string.not_limite));
		classTypesKey1.add(getString(R.string.gdc));
		classTypesKey1.add(getString(R.string.tz));
		classTypesKey1.add(getString(R.string.k));
		classTypesValue1 = new ArrayList<Boolean>();
		initTypeValues(classTypesKey1,classTypesValue1);
	}

	private void initSearch2() {

		classTypesKey2 = new ArrayList<String>();
		classTypesKey2.add(getString(R.string.not_limite));
		classTypesKey2.add(getString(R.string.yingzuo));
		classTypesKey2.add(getString(R.string.yingwo));
		classTypesKey2.add(getString(R.string.ruanwo));
		classTypesKey2.add(getString(R.string.erdeng));
		classTypesKey2.add(getString(R.string.yideng));
		classTypesKey2.add(getString(R.string.tedeng));
		classTypesKey2.add(getString(R.string.shangwu_seat));
		classTypesValue2 = new ArrayList<Boolean>();
		initTypeValues(classTypesKey2,classTypesValue2);
	}

	private void initSearch3() {

		classTypesKey3 = new ArrayList<String>();
		classTypesKey3.add(getString(R.string.not_limite));
		classTypesKey3.add(getString(R.string.lingchen));
		classTypesKey3.add(getString(R.string.shangwu));
		classTypesKey3.add(getString(R.string.xiawu));
		classTypesKey3.add(getString(R.string.wanshang));
		classTypesValue3 = new ArrayList<Boolean>();
		initTypeValues(classTypesKey3,classTypesValue3);
	}

	private void initSearch4() {

		classTypesKey4 = new ArrayList<String>();
		classTypesKey4.add(getString(R.string.not_limite));
		classTypesKey4.add(getString(R.string.lingchen));
		classTypesKey4.add(getString(R.string.shangwu));
		classTypesKey4.add(getString(R.string.xiawu));
		classTypesKey4.add(getString(R.string.wanshang));
		classTypesValue4 = new ArrayList<Boolean>();
		initTypeValues(classTypesKey4,classTypesValue4);
	}

	public void execute(String result) {
		if (TextUtils.isEmpty(result)) {
			Message msg = Message.obtain();
			msg.what = FinalString.NET_ERROR;
			handler.sendMessage(msg);
			return;
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
					msg.what = FinalString.DATA_FINISH;
					msg.obj = all.getString("errorMsg");
					handler.sendMessage(msg);
					return;
				}

				String responseBody = all.getString("ResponseBody");
				JSONObject obj1 = new JSONObject(responseBody);
				JSONArray trainItems = obj1.getJSONArray("TrainItems");
				for (int index = 0; index < trainItems.length(); index++) {
					JSONObject trainItem = trainItems.getJSONObject(index);
					Train train = new Train();
					train.setTrainID(trainItem.getString("TrainID"));
					train.setTrainShortName(trainItem.getString("TrainShortName"));// 1火车名字
					// 高速 G, 动车D, 特快T, 快速, k 普客 1524
					train.setTrainTypeName(trainItem.getString("TrainTypeName"));
					//train.setFirstStart(trainItem.getString("FirstStart"));
					//train.setLastStop(trainItem.getString("LastStop"));
					//train.setFirstStartTime(trainItem.getString("FirstStartTime"));
					//train.setLastArrivalTime(trainItem.getString("LastArrivalTime"));
					train.setTakeDays(trainItem.getString("TakeDays"));// 3间隔天数
					//train.setBookable(trainItem.getString("Bookable"));// 是否可预定
					train.setStart(trainItem.getString("Start"));// 4发车
					train.setStop(trainItem.getString("Stop"));// 5到达
					train.setStartTime(trainItem.getString("StartTime"));// 6发车时间
					train.setArrivalTime(trainItem.getString("ArrivalTime"));// 7到达时间
					String totalTime[] = Utils.getTimes(trainItem.getString("StartTime"), trainItem.getString("ArrivalTime"),
							new Integer(trainItem.getString("TakeDays")));
					hour = totalTime[0];
					min = totalTime[1];
					int time = (new Integer(hour)) * 60 + new Integer(min);
					train.setTotalTime(time);// 耗时 多长时间
					train.setDistance(trainItem.getString("Distance"));// 8距离
					JSONArray ticketItems = new JSONArray(trainItem.getString("TicketItems"));
					ArrayList<Ticket> ticketList = new ArrayList<Ticket>();
					for (int j = 0; j < ticketItems.length(); j++) {
						JSONObject ticketItem = ticketItems.getJSONObject(j);
						Ticket ticket = new Ticket();
						//ticket.setBookable(ticketItem.getString("Bookable"));// 是否可预定
						ticket.setPrice(ticketItem.getString("Price"));// 9价格
						ticket.setSeatTypeName(ticketItem.getString("SeatTypeName"));// 10座位类型
						ticketList.add(ticket);
					}
					train.setTicketList(ticketList);
					trainList.add(train);

				}
				orderTrainList = trainList;
				System.out
						.println("orderTrainList 大小 " + orderTrainList.size());
				Message msg = Message.obtain();
				msg.what = FinalString.DATA_FINISH;
				handler.sendMessage(msg);
			} catch (JSONException e) {
				e.printStackTrace();
				Message msg = Message.obtain();
				msg.what = FinalString.NET_ERROR;
				handler.sendMessage(msg);
			}

		}
	}

	private class MyAdapter extends BaseAdapter {
		private ArrayList<Train> list;

		public MyAdapter(ArrayList<Train> list) {
			this.list = list;

		}

		public void setDate(ArrayList<Train> list) {
			if (list != null)
				this.list = list;
			else
				this.list = new ArrayList<Train>();
		}

		private void changeData(ArrayList<Train> list) {
			this.setDate(list);
			this.notifyDataSetChanged();
		}

		@Override
		public int getCount() {
			return list.size();
		}

		@Override
		public Object getItem(int position) {
			return list.get(position);
		}

		@Override
		public long getItemId(int position) {
			return Long.parseLong(list.get(position).getTrainID());
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder viewHolder = null;
			if (convertView == null) {
				convertView = getLayoutInflater().inflate(R.layout.train_item,
						null);
				viewHolder = new ViewHolder();
				viewHolder.startTime = (TextView) convertView
						.findViewById(R.id.tv_train_start_time);
				viewHolder.departCity = (TextView) convertView
						.findViewById(R.id.tv_train_start);
				viewHolder.price = (TextView) convertView
						.findViewById(R.id.tv_train_price);
				viewHolder.arriveTime = (TextView) convertView
						.findViewById(R.id.tv_train_end_time);
				viewHolder.arriveCity = (TextView) convertView
						.findViewById(R.id.tv_train_stop);
				viewHolder.totalCount = (TextView) convertView
						.findViewById(R.id.tv_train_total_count);
				viewHolder.totalDistance = (TextView) convertView
						.findViewById(R.id.tv_train_total_distance);
				viewHolder.trainName = (TextView) convertView
						.findViewById(R.id.tv_train_name);
				viewHolder.trainTpypeName = (TextView) convertView
						.findViewById(R.id.tv_train_type_name);
				convertView.setTag(viewHolder);
			} else {
				viewHolder = (ViewHolder) convertView.getTag();
			}
			viewHolder.startTime.setText(list.get(position).getStartTime());
			viewHolder.departCity.setText(list.get(position).getStart());
			viewHolder.trainTpypeName.setText(list.get(position)
					.getTicketList().get(0).getSeatTypeName());
			viewHolder.price.setText(list.get(position).getTicketList().get(0)
					.getPrice());
			viewHolder.arriveTime.setText(list.get(position).getArrivalTime());

			String totalTimes[] = Utils.getTimes(list.get(position)
					.getStartTime(), list.get(position).getArrivalTime(),
					new Integer(list.get(position).getTakeDays()));
			hour = totalTimes[0];
			min = totalTimes[1];

			int time = (new Integer(hour)) * 60 + new Integer(min);

			viewHolder.arriveCity.setText(list.get(position).getStop());
			viewHolder.trainName
					.setText(list.get(position).getTrainShortName());
			String totalTime = hour + getString(R.string.hour) + min
					+ getString(R.string.min2);
			viewHolder.totalDistance.setText(totalTime);
			return convertView;
		}
	}

	static class ViewHolder {
		public TextView startTime;
		public TextView departCity;
		public TextView price;
		public TextView arriveTime;
		public TextView arriveCity;
		public TextView trainName;
		public TextView totalDistance;
		public TextView totalCount;
		public TextView trainTpypeName;
	}

	public String fomatDate(String date) {
		return date.substring(date.indexOf("T") + 1, date.length() - 3);
	}

	private void initCalendar() {
		final Calendar nextYear = Calendar.getInstance();
		nextYear.add(Calendar.MONTH, 5);

		final Calendar lastYear = Calendar.getInstance();
		lastYear.add(Calendar.MONTH, 0);

		myCalendar.initCalendar(lastYear.getTime(), nextYear.getTime())
				.withSelectedDates(new Date());
		myCalendar.hitTitle(true);
		myCalendar.setOnDateSelectedListener(new OnDateSelectedListener() {

			@Override
			public void onDateUnselected(Date date) {

			}

			@Override
			public void onDateSelected(Date date) {
				showOrHideCalendar();
				String dateStr = dateFormat(date);
				takeOffTime = dateStr;
				initTodayStr(dateStr);
				requestInfo.setStartTime(dateStr);
				loadingData();

			}
		});
	}

	private void setTextViewStyle(TextView textView) {
		textView.setBackgroundResource(R.drawable.white_btn_selector);
		textView.setTextColor(getResources().getColor(R.color.text_color_v4));
		textView.setTextSize(10);
		textView.setGravity(Gravity.CENTER);
	}

	private LinearLayout.LayoutParams setTextDistance() {
		LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
				UnitUtil.dip2px(this, 96), UnitUtil.dip2px(this, 29));
		lp.gravity = Gravity.CENTER_VERTICAL;
		lp.rightMargin = 6;
		return lp;
	}

	/**
	 * 设置清空按钮的颜色背景
	 */
	private void setShaiXuanNullBackground() {
		if (layoutShaiXuanAddView.getChildCount() == 0) {
			btnShaixuanNull.setBackgroundResource(R.drawable.white_btn_pressed);
			btnShaixuanNull.setEnabled(false);
			horizontalScrollView.setVisibility(View.GONE);
		} else {
			btnShaixuanNull.setBackgroundResource(R.drawable.white_btn_normal);
			btnShaixuanNull.setEnabled(true);
			horizontalScrollView.setVisibility(View.VISIBLE);
		}
	}

	private class ClassTypeAdapter1 extends BaseAdapter {

		@Override
		public int getCount() {
			return classTypesKey1.size();
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
			ClassTypesViewHolder viewHolder = null;
			if (convertView == null) {
				convertView = getLayoutInflater().inflate(
						R.layout.flight_search_item, null);
				viewHolder = new ClassTypesViewHolder();
				viewHolder.key = (TextView) convertView.findViewById(R.id.key);
				viewHolder.value = (ImageView) convertView
						.findViewById(R.id.value);
				convertView.setTag(viewHolder);
			} else {
				viewHolder = (ClassTypesViewHolder) convertView.getTag();
			}
			viewHolder.key.setText(classTypesKey1.get(position));
			if (classTypesValue1.get(position) == false) {
				viewHolder.value
						.setImageResource(R.drawable.search_radiobtn_normal);
			} else {
				viewHolder.value
						.setImageResource(R.drawable.search_radiobtn_press);
			}
			convertView.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					if (classTypesValue1.get(position) == false) {
						classTypesValue1.remove(true);
						classTypesValue1.add(false);
						classTypesValue1.remove(position);
						classTypesValue1.add(position, true);

					}
					classTypeAdapter1.notifyDataSetChanged();

					map1.put(getString(R.string.traintype), arr1[position]);

					String trainType = map1.get(getString(R.string.traintype));
					if (trainType.equals(arr1[1])) {
						trainType = getString(R.string.gdc_gn);

					} else if (trainType.equals(arr1[2])) {
						trainType = getString(R.string.tz_gn);

					} else if (trainType.equals(arr1[3])) {
						trainType = getString(R.string.k_gn);
					}

					if (getString(R.string.not_limite).equals(
							map1.get(getString(R.string.traintype)))) {
						if (shaiXuanAddViewId1 != 0) {
							layoutShaiXuanAddView.removeView(textView1);
							shaiXuanAddViewId1 = 0;
							setShaiXuanNullBackground();
						}
						return;
					} else {

						if (shaiXuanAddViewId1 == 0) {

							horizontalScrollView.setVisibility(View.VISIBLE);
							textView1 = new TextView(TrainActivity.this);
							// 设置样式
							setTextViewStyle(textView1);
							textView1.setText(trainType);
							layoutShaiXuanAddView.addView(textView1, 0,
									setTextDistance());
							shaiXuanAddViewId1 = textView1.getId();
						} else {

							// 修改
							textView1.setText(trainType);
						}

					}
					setShaiXuanNullBackground();
					textView1.setOnClickListener(new OnClickListener() {

						@Override
						public void onClick(View v) {
							// 自己移除自己
							layoutShaiXuanAddView.removeView(textView1);
							shaiXuanAddViewId1 = 0;
							// 将列表框设置为不限
							remove(classTypesValue1, position);
							classTypeAdapter1.notifyDataSetChanged();
							map1.put(getString(R.string.traintype), arr1[0]);
							setShaiXuanNullBackground();
						}
					});

				}
			});
			return convertView;
		}
	}

	private class ClassTypeAdapter2 extends BaseAdapter {

		@Override
		public int getCount() {
			return classTypesKey2.size();
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

			ClassTypesViewHolder viewHolder = null;
			if (convertView == null) {
				convertView = getLayoutInflater().inflate(
						R.layout.flight_search_item, null);
				viewHolder = new ClassTypesViewHolder();
				viewHolder.key = (TextView) convertView.findViewById(R.id.key);
				viewHolder.value = (ImageView) convertView
						.findViewById(R.id.value);
				convertView.setTag(viewHolder);
			} else {
				viewHolder = (ClassTypesViewHolder) convertView.getTag();
			}
			viewHolder.key.setText(classTypesKey2.get(position));
			if (classTypesValue2.get(position) == false) {
				viewHolder.value
						.setImageResource(R.drawable.search_radiobtn_normal);
			} else {
				viewHolder.value
						.setImageResource(R.drawable.search_radiobtn_press);
			}
			convertView.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					if (classTypesValue2.get(position) == false) {
						classTypesValue2.remove(true);
						classTypesValue2.add(false);
						classTypesValue2.remove(position);
						classTypesValue2.add(position, true);

					}
					classTypeAdapter2.notifyDataSetChanged();

					map2.put(getString(R.string.seattype), arr2[position]);

					trainSeatTypeName = map2.get(getString(R.string.seattype));

					if (getString(R.string.not_limite).equals(
							map2.get(getString(R.string.seattype)))) {
						if (shaiXuanAddViewId2 != 0) {
							layoutShaiXuanAddView.removeView(textView2);
							shaiXuanAddViewId2 = 0;
							setShaiXuanNullBackground();
						}
						return;
					} else {

						if (shaiXuanAddViewId2 == 0) {

							horizontalScrollView.setVisibility(View.VISIBLE);
							textView2 = new TextView(TrainActivity.this);
							// 设置样式
							setTextViewStyle(textView2);
							textView2.setText(trainSeatTypeName);
							layoutShaiXuanAddView.addView(textView2, 0,
									setTextDistance());
							shaiXuanAddViewId2 = textView2.getId();
						} else {

							// 修改
							textView2.setText(trainSeatTypeName);
						}

					}
					setShaiXuanNullBackground();
					textView2.setOnClickListener(new OnClickListener() {

						@Override
						public void onClick(View v) {
							// 自己移除自己
							layoutShaiXuanAddView.removeView(textView2);
							shaiXuanAddViewId2 = 0;
							// 将列表框设置为不限

							remove(classTypesValue2, position);
							classTypeAdapter2.notifyDataSetChanged();
							map2.put(getString(R.string.seattype), arr2[0]);
							setShaiXuanNullBackground();
						}
					});

				}
			});
			return convertView;
		}
	}

	/**
	 * 
	 * @param classTypesValue
	 * @param position
	 *            移除自己，同时变成不限
	 */
	private void remove(ArrayList<Boolean> classTypesValue, int position) {
		classTypesValue.remove(position);
		classTypesValue.add(position, false);
		classTypesValue.remove(0);
		classTypesValue.add(0, true);

	}

	private class ClassTypeAdapter3 extends BaseAdapter {

		@Override
		public int getCount() {
			return classTypesKey3.size();
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
			ClassTypesViewHolder viewHolder = null;
			if (convertView == null) {
				convertView = getLayoutInflater().inflate(
						R.layout.flight_search_item, null);
				viewHolder = new ClassTypesViewHolder();
				viewHolder.key = (TextView) convertView.findViewById(R.id.key);
				viewHolder.value = (ImageView) convertView
						.findViewById(R.id.value);
				convertView.setTag(viewHolder);
			} else {
				viewHolder = (ClassTypesViewHolder) convertView.getTag();
			}
			viewHolder.key.setText(classTypesKey3.get(position));
			if (classTypesValue3.get(position) == false) {
				viewHolder.value
						.setImageResource(R.drawable.search_radiobtn_normal);
			} else {
				viewHolder.value
						.setImageResource(R.drawable.search_radiobtn_press);
			}
			convertView.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					if (classTypesValue3.get(position) == false) {
						classTypesValue3.remove(true);
						classTypesValue3.add(false);
						classTypesValue3.remove(position);
						classTypesValue3.add(position, true);

					}
					classTypeAdapter3.notifyDataSetChanged();
					map3.put(getString(R.string.starttime), arr3[position]);
					String startTimeAddview = map3
							.get(getString(R.string.starttime));
					if (startTimeAddview.equals(arr3[1])) {
						startTimeAddview = getString(R.string.lingchen_start);

					} else if (startTimeAddview.equals(arr3[2])) {
						startTimeAddview = getString(R.string.shangwu_start);

					} else if (startTimeAddview.equals(arr3[3])) {
						startTimeAddview = getString(R.string.xiawu_start);

					} else if (startTimeAddview.equals(arr3[4])) {
						startTimeAddview = getString(R.string.wanshang_arrive);

					}

					if (getString(R.string.not_limite).equals(
							map3.get(getString(R.string.starttime)))) {
						if (shaiXuanAddViewId3 != 0) {
							layoutShaiXuanAddView.removeView(textView3);
							shaiXuanAddViewId3 = 0;
							setShaiXuanNullBackground();
						}

						return;
					} else {

						if (shaiXuanAddViewId3 == 0) {
							horizontalScrollView.setVisibility(View.VISIBLE);
							textView3 = new TextView(TrainActivity.this);
							setTextViewStyle(textView3);
							textView3.setText(startTimeAddview);
							layoutShaiXuanAddView.addView(textView3, 0,
									setTextDistance());
							shaiXuanAddViewId3 = textView3.getId();
						} else {

							// 修改
							textView3.setText(startTimeAddview);
						}

					}
					setShaiXuanNullBackground();
					textView3.setOnClickListener(new OnClickListener() {

						@Override
						public void onClick(View v) {
							// 自己移除自己
							layoutShaiXuanAddView.removeView(textView3);
							shaiXuanAddViewId3 = 0;
							remove(classTypesValue3, position);
							classTypeAdapter3.notifyDataSetChanged();
							map3.put(getString(R.string.starttime), arr3[0]);
							setShaiXuanNullBackground();
						}
					});

				}
			});
			return convertView;
		}
	}

	private class ClassTypeAdapter4 extends BaseAdapter {

		@Override
		public int getCount() {
			return classTypesKey4.size();
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
			ClassTypesViewHolder viewHolder = null;
			if (convertView == null) {
				convertView = getLayoutInflater().inflate(
						R.layout.flight_search_item, null);
				viewHolder = new ClassTypesViewHolder();
				viewHolder.key = (TextView) convertView.findViewById(R.id.key);
				viewHolder.value = (ImageView) convertView
						.findViewById(R.id.value);
				convertView.setTag(viewHolder);
			} else {
				viewHolder = (ClassTypesViewHolder) convertView.getTag();
			}
			viewHolder.key.setText(classTypesKey4.get(position));
			if (classTypesValue4.get(position) == false) {
				viewHolder.value
						.setImageResource(R.drawable.search_radiobtn_normal);
			} else {
				viewHolder.value
						.setImageResource(R.drawable.search_radiobtn_press);
			}
			convertView.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					if (classTypesValue4.get(position) == false) {
						classTypesValue4.remove(true);
						classTypesValue4.add(false);
						classTypesValue4.remove(position);
						classTypesValue4.add(position, true);
					}
					classTypeAdapter4.notifyDataSetChanged();

					map4.put(getString(R.string.arrivetime), arr3[position]);
					String arriveTimeAddview = map4
							.get(getString(R.string.arrivetime));
					if (arriveTimeAddview.equals(arr3[1])) {
						arriveTimeAddview = getString(R.string.lingchen_arrive);

					} else if (arriveTimeAddview.equals(arr3[2])) {
						arriveTimeAddview = getString(R.string.shangwu_arrive);

					} else if (arriveTimeAddview.equals(arr3[3])) {
						arriveTimeAddview = getString(R.string.xiawu_arrive);

					} else if (arriveTimeAddview.equals(arr3[4])) {
						arriveTimeAddview = getString(R.string.wanshang_arrive);

					}

					if (getString(R.string.not_limite).equals(
							map4.get(getString(R.string.arrivetime)))) {
						if (shaiXuanAddViewId4 != 0) {
							layoutShaiXuanAddView.removeView(textView4);
							shaiXuanAddViewId4 = 0;
							setShaiXuanNullBackground();
						}

						return;
					} else {

						if (shaiXuanAddViewId4 == 0) {

							horizontalScrollView.setVisibility(View.VISIBLE);
							textView4 = new TextView(TrainActivity.this);
							setTextViewStyle(textView4);
							textView4.setText(arriveTimeAddview);
							layoutShaiXuanAddView.addView(textView4, 0,
									setTextDistance());
							shaiXuanAddViewId4 = textView4.getId();
						} else {

							// 修改
							textView4.setText(arriveTimeAddview);
						}

					}
					setShaiXuanNullBackground();
					textView4.setOnClickListener(new OnClickListener() {

						@Override
						public void onClick(View v) {
							// 自己移除自己
							layoutShaiXuanAddView.removeView(textView4);
							shaiXuanAddViewId4 = 0;
							// 将列表框设置为不限
							remove(classTypesValue4, position);
							classTypeAdapter4.notifyDataSetChanged();
							map4.put(getString(R.string.arrivetime), arr3[0]);
							setShaiXuanNullBackground();
						}
					});

				}
			});
			return convertView;
		}
	}

	static class ClassTypesViewHolder {
		public TextView key;
		public ImageView value;
	}

	@Override
	public void onDestroy() {
		stopProgressDialog();
		if (task != null)
			task.cancel(true);
		super.onDestroy();
	}

	public void showOrHideCalendar() {
		if (myCalendar.getVisibility() == View.INVISIBLE) {
			img_occlusionbg.setVisibility(View.VISIBLE);
			float x = myCalendar.getX();
			float y = myCalendar.getY();
			int height = myCalendar.getHeight();
			TranslateAnimation calendarInAnimation = new TranslateAnimation(x,
					x, y - height, y);
			calendarInAnimation.setDuration(300);
			myCalendar.setAnimation(calendarInAnimation);
			myCalendar.setVisibility(View.VISIBLE);
		} else {
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

		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == event.KEYCODE_BACK) {
			if (myCalendar.getVisibility() == View.VISIBLE) {
				showOrHideCalendar();
				return true;
			}
			if (reShaixuan.getVisibility() == View.VISIBLE
					&& searchContainer.getVisibility() == View.VISIBLE) {
				Animation anim = AnimationUtils.loadAnimation(this,
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
						reShaixuan.setVisibility(View.GONE);

					}
				});
				searchContainer.setAnimation(anim);
				searchContainer.setVisibility(View.GONE);
				return true;
			}
		}
		return super.onKeyDown(keyCode, event);

	}

	private boolean hideSearch() {
		if (reShaixuan.getVisibility() == View.VISIBLE
				&& searchContainer.getVisibility() == View.VISIBLE) {
			Animation anim = AnimationUtils.loadAnimation(this,
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
					reShaixuan.setVisibility(View.GONE);

				}
			});
			searchContainer.setAnimation(anim);
			searchContainer.setVisibility(View.GONE);
			return true;
		}
		return false;
	}

	@Override
	public void onStart() {
		super.onStart();
	}

	@Override
	public void onResume() {
		super.onResume();
	}

	private void startProgressDialog() {
		rl_failed.setVisibility(View.GONE);
		listView.setVisibility(View.VISIBLE);
		ll_preday.setEnabled(false);
		ll_today.setEnabled(false);
		ll_nextday.setEnabled(false);
		orderbytime.setEnabled(false);
		orderbyprice.setEnabled(false);
		screening.setEnabled(false);
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
		ll_preday.setEnabled(true);
		ll_today.setEnabled(true);
		ll_nextday.setEnabled(true);
		orderbytime.setEnabled(true);
		orderbyprice.setEnabled(true);
		screening.setEnabled(true);
	}

	private void showNotFoundMsg() {
		
		rl_failed.setBackground(this.getResources().getDrawable(
				R.drawable.notfound_bg));
		rl_failed.setVisibility(View.VISIBLE);
		listView.setVisibility(View.GONE);
	}

	public void showNetErrorMsg() {
		stopProgressDialog();
		rl_failed.setBackground(this.getResources().getDrawable(
				R.drawable.net_error_bg));
		rl_failed.setVisibility(View.VISIBLE);
		listView.setVisibility(View.GONE);
	}

}
