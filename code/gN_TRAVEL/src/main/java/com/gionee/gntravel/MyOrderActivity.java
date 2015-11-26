package com.gionee.gntravel;

import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.gionee.gntravel.adapter.MyOrderAdapter;
import com.gionee.gntravel.entity.OrderType;
import com.gionee.gntravel.entity.RequestOrderEntity;
import com.gionee.gntravel.entity.UserOrderEntity;
import com.gionee.gntravel.task.GeneralGetInfoTask;
import com.gionee.gntravel.utils.FinalString;
import com.gionee.gntravel.utils.HttpConnUtil4Gionee;
import com.gionee.gntravel.utils.JSONUtil;
import com.gionee.gntravel.utils.NetWorkUtil;
import com.gionee.gntravel.utils.TaskCallBack;

public class MyOrderActivity extends Activity implements TaskCallBack,
		GeneralGetInfoTask.ParseTool, OnClickListener {
	private ResultHandler handler = new ResultHandler();
	private ListView listView;

	private GeneralGetInfoTask task;
	private HashMap<String, String> params;
	private RequestOrderEntity orderEntity;
	private String searchOrderUrl;
	private ArrayList<UserOrderEntity> orderList = new ArrayList<UserOrderEntity>();
	private ArrayList<UserOrderEntity> showOrderList = new ArrayList<UserOrderEntity>();
	private TravelApplication app;
	private TextView tv_orderType;
	private PopupWindow mPop;
	private View rl_title;
	private RelativeLayout rl_loading;
	private ImageView img_loading;
	private View rl_failed;
	private boolean mIsFinish;
	private MyOrderAdapter adapter;
	private TextView tvRefresh;

	private class ResultHandler extends Handler {

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			mIsFinish = true;
			switch (msg.what) {
			case FinalString.DATA_FINISH:
				loadDataFinish();
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
		}
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_myorder);
		app = (TravelApplication) getApplication();
		initViews();
		initPopup();
		initParams();
	}

	private void initViews() {
		tvRefresh=(TextView) findViewById(R.id.btn_refresh);
		tv_orderType = (TextView) findViewById(R.id.tv_orderType);
		tv_orderType.setOnClickListener(this);
		listView = (ListView) findViewById(R.id.listView);
		TextView tv_title = (TextView) findViewById(R.id.tv_title);
		tv_title.setText(getString(R.string.order));
		tv_title.setOnClickListener(this);
		rl_title = (View) findViewById(R.id.rl_title);
		rl_loading = (RelativeLayout) findViewById(R.id.rl_loading);
		img_loading = (ImageView) findViewById(R.id.img_loading);
		rl_failed = (View) findViewById(R.id.rl_failed);
		rl_failed.setOnClickListener(this);
		adapter = new MyOrderAdapter(this, orderList);

	}

	private void initParams() {
//		orderEntity.setUidKey(app.getUserId());
		
		
		orderEntity = new RequestOrderEntity();
		orderEntity.setU(app.getU());
		orderEntity.setUserId(app.getUserId());
		orderEntity.setOrderId(null);
		searchOrderUrl = getString(R.string.gionee_host)+ FinalString.MYORDER_URL;
		params = new HashMap<String, String>();
		params.put("json", JSONUtil.getInstance().toJSON(orderEntity));
	}

	@Override
	protected void onStart() {
		super.onStart();
		if (!mIsFinish) {
			loadingData();
		}

	}

	@Override
	protected void onStop() {
		super.onStop();
		if (task != null && !task.isCancelled()) {
			task.cancel(true);
		}
		if (!mIsFinish) {
			stopProgressDialog();
		}
	}

	private void loadingData() {
		mIsFinish = false;
		tv_orderType.setText("全部订单");
		startProgressDialog();
		if (!NetWorkUtil.isNetworkConnected(this)) {
			Message msg = Message.obtain();
			msg.what = FinalString.NET_ERROR;
			handler.sendMessage(msg);
			return;
		}
		task = new GeneralGetInfoTask(this, this, this);
		task.execute(searchOrderUrl, params,
				HttpConnUtil4Gionee.HttpMethod.POST);
	}

	@Override
	public void execute(Object result) {
		Message msg = Message.obtain();
		orderList = (ArrayList<UserOrderEntity>) result;
		if (orderList == null) {
			msg.what = FinalString.NET_ERROR;
		} else if (orderList.isEmpty()) {
			msg.what = FinalString.NOT_FOUND;
		} else {
			msg.what = FinalString.DATA_FINISH;
		}
		handler.sendMessage(msg);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.tv_title:
			finish();
			break;
		case R.id.tv_orderType:
			if (mIsFinish) {
				showPopup();
			}
			break;
		case R.id.tv_allOrderType:
			showAllOrderList();
			break;
		case R.id.tv_flightOrderType:
			showFlightOrderList();
			break;
		case R.id.tv_hotelOrderType:
			showHotelOrderList();
			break;
		case R.id.btn_refresh:
			loadingData();
			break;
		case R.id.rl_failed:
			loadingData();
			break;
		default:
			break;
		}
	}

	private void showAllOrderList() {
		showPopup();
		tv_orderType.setText("全部订单");
		adapter.setList(orderList);
		adapter.notifyDataSetChanged();
	}

	private void showHotelOrderList() {
		showPopup();
		showOrderList.clear();
		for (int index = 0; index < orderList.size(); index++) {
			if (orderList.get(index).getOrderType() == OrderType.HOTEL) {
				showOrderList.add(orderList.get(index));
			}
		}
		adapter.setList(showOrderList);
		adapter.notifyDataSetChanged();
		tv_orderType.setText("酒店订单");
	}

	private void showFlightOrderList() {
		showPopup();
		showOrderList.clear();
		for (int index = 0; index < orderList.size(); index++) {
			if (orderList.get(index).getOrderType() == OrderType.FLIGHT) {
				showOrderList.add(orderList.get(index));
			}
		}
		adapter.setList(showOrderList);
		adapter.notifyDataSetChanged();
		tv_orderType.setText("机票订单");
	}

	private void startProgressDialog() {
		rl_failed.setVisibility(View.GONE);
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

	public void showNetErrorMsg() {
		rl_failed.setBackground(getResources().getDrawable(
				R.drawable.net_error_bg));
		rl_failed.setVisibility(View.VISIBLE);
		stopProgressDialog();
	}

	public void showNotFoundMsg() {
		stopProgressDialog();
		rl_failed.setBackground(getResources().getDrawable(
				R.drawable.notfound_bg));
		rl_failed.setVisibility(View.VISIBLE);
		tvRefresh.setText(getString(R.string.not_order));
	}

	public void loadDataFinish() {
		stopProgressDialog();
		adapter = new MyOrderAdapter(this, orderList);
		listView.setAdapter(adapter);
	}

	public void initPopup() {
		ViewGroup menuView = (ViewGroup) getLayoutInflater().inflate(
				R.layout.popup_order_type, null, true);
		TextView tv_allOrderType = (TextView) menuView
				.findViewById(R.id.tv_allOrderType);
		TextView tv_flightOrderType = (TextView) menuView
				.findViewById(R.id.tv_flightOrderType);
		TextView tv_hotelOrderType = (TextView) menuView
				.findViewById(R.id.tv_hotelOrderType);
		tv_allOrderType.setOnClickListener(this);
		tv_flightOrderType.setOnClickListener(this);
		tv_hotelOrderType.setOnClickListener(this);
		mPop = new PopupWindow(menuView, LayoutParams.WRAP_CONTENT,
				LayoutParams.WRAP_CONTENT, true);
		mPop.setOutsideTouchable(true);
		mPop.setFocusable(true);
		mPop.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
	}

	public void showPopup() {
		if (mPop.isShowing()) {
			mPop.dismiss();
		} else {
			int heigh = (int) TypedValue.applyDimension(
					TypedValue.COMPLEX_UNIT_PX,
					getResources().getDimension(R.dimen.actionbar_height),
					getResources().getDisplayMetrics());
			int heigh2 = (int) TypedValue.applyDimension(
					TypedValue.COMPLEX_UNIT_PX,
					getResources().getDimension(R.dimen.general_margin),
					getResources().getDisplayMetrics());
			int[] location = new int[2];
			rl_title.getLocationOnScreen(location);

			mPop.showAtLocation(rl_title, Gravity.CENTER_HORIZONTAL
					| Gravity.TOP, location[0], heigh + location[1] - heigh2);
		}
	}

	/**
	 * 网络不好返回 null 服务器数据错误返回 长度为0的 list
	 * 
	 * @param result
	 * @return
	 */
	public ArrayList<UserOrderEntity> parseResult(String result) {
		ArrayList<UserOrderEntity> list = new ArrayList<UserOrderEntity>();
		try {
			JSONObject json = new JSONObject(result);
			if (!FinalString.ERRORCODE.equals(json.getString("errorCode"))) {
				return list;
			}

			JSONArray jsonArray = new JSONArray(json.getString("content"));
			for (int i = 0; i < jsonArray.length(); i++) {
				UserOrderEntity orderEntity = new UserOrderEntity();
				JSONObject jsonAll = (JSONObject) jsonArray.get(i);
				orderEntity.setFlight(jsonAll.getString("flight"));
				orderEntity.setdAirPortName(jsonAll.getString("dAirPortName"));
				orderEntity.setaAirPortName(jsonAll.getString("aAirPortName"));
				orderEntity.setOrderId(jsonAll.getString("orderId"));
				orderEntity.setStartDate(jsonAll.getString("startDate"));
				orderEntity.setPrice(jsonAll.getString("price"));
				orderEntity.setStatus(jsonAll.getString("status"));
				orderEntity.setType(jsonAll.getString("type"));
				orderEntity.setHotelName(jsonAll.getString("hotelName"));
				list.add(orderEntity);
			}
		} catch (JSONException e) {
			e.printStackTrace();
			return null;
		}
		return list;
	}

}
