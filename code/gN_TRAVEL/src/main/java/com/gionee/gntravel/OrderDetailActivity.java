package com.gionee.gntravel;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.gionee.gntravel.entity.Hotel;
import com.gionee.gntravel.entity.PassengerEntity;
import com.gionee.gntravel.entity.SimpleHotelBean;
import com.gionee.gntravel.utils.FinalString;
import com.gionee.gntravel.utils.HttpConnCallback;
import com.gionee.gntravel.utils.HttpConnUtil4Gionee;
import com.gionee.gntravel.utils.JSONUtil;
import com.gionee.gntravel.utils.NetWorkUtil;
import com.gionee.gntravel.utils.Utils;

/**
 * 
 * @author lijinbao 订单详情界面
 */
public class OrderDetailActivity extends BaseActivity implements
		HttpConnCallback, OnClickListener {
	private ResultHandler handler = new ResultHandler();
	private String orderId;
	private String orderType;
	private String userId;
	private String price;
	private ScrollView scrollView;
	private HttpConnUtil4Gionee task;
	private HashMap<String, String> params;
	private String orderDetailUrl;
	private Thread doResultThread;
	private TextView tvTitle;// 标题名称
	/**
	 * 公共部分
	 */
	private RelativeLayout rl_loading;
	private ImageView img_loading;
	private View rl_failed;
	private TextView tvOrderStatus;// 订单状态
	private TextView tvOrderPrice;// 订单价格
	private TextView tvOrderCode;// 订单编号
	private TextView tvContactName;// 联系人姓名
	private TextView tvContactMobile;// 联系人手机号

	/**
	 * 飞机部分
	 */
	private TextView tvName;// 航空公司名称
	private TextView tveDpartPortName;// 起飞机场
	private TextView tvArrivalPortName;// 到达机场
	private TextView tvTakeOffTime;// 起飞时间
	private TextView tvArrivalTime;// 到达时间
	private RelativeLayout reTuigaiqian;// 退改签规则布局
	private TextView tvTuigaiqian;
	private ImageView imgTuigaiqian;
	private LinearLayout llAddView;
	private ArrayList<PassengerEntity> listPassenger = null;
	/**
	 * 酒店部分
	 */

	private TextView tvHotalName;
	private RelativeLayout reHotalDetail;
	private TextView tvHotelAddress;
	private TextView tvOrderDate;// 预定日期
	private TextView tvRuzhuTime;// 从哪到哪天
	private TextView tvRoomType;//
	private TextView tvCheckInName;// 入住人
	private TextView tvWarnimg1;// 酒dian
	private TextView tvWarnimg2;// 酒dian
	private TextView tvPhoto;// 酒店电话
	private RelativeLayout reHotalMap;
	private LinearLayout llCallPhoto;
	private String flight;
	private String airLineName;

	private String takeOffTime;
	private String arrivalTime;
	private String rerNote;
	private String refNote;
	private String endNote;
	private String departPortName;
	private String arrivalPortName;
	private String orderPassengers;

	private String contactName;
	private String contactMobile;

	private String hotelName;
	private String start;
	private String end;
	private String hotelPhone;
	private String roomType;
	private String checkInName;
	private String lateArrivalTime;
	private String orderStatus;
	private String hotelAddress;
	private String hotelId;
	private TravelApplication app;
	private String hotelinfoUrl;
	private SimpleHotelBean simpleHotelBean;

	private class ResultHandler extends Handler {

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case FinalString.DATA_FINISH:
				loadDataFinish();
				stopProgressDialog();
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
		whichInitView();
		setupView();
		initParams();
		loadingData();
	}

	/**
	 * 跳入是酒店还是飞机
	 */
	private void whichInitView() {
		Intent intent = getIntent();
		orderType = intent.getStringExtra("orderType");
		orderId = intent.getStringExtra("orderId");
		price = intent.getStringExtra("price");
		orderStatus = intent.getStringExtra("orderStatus");
		if ("flight".equals(orderType)) {
			setContentView(R.layout.activity_order_flight_detail);
			setupViewFlight();
			Button btn_custom = (Button) findViewById(R.id.btn_function);
			btn_custom.setText(getString(R.string.gaiqian_retirement));
			btn_custom.setOnClickListener(this);
		} else {
			setContentView(R.layout.activity_order_hotal_detail);
			setupViewHotal();
			Button btn_custom = (Button) findViewById(R.id.btn_function);
			btn_custom.setText(getString(R.string.unsubscribe));
			btn_custom.setOnClickListener(this);
		}
	}

	private void setupViewHotal() {
		tvHotalName = (TextView) findViewById(R.id.tv_name);
		reHotalDetail = (RelativeLayout) findViewById(R.id.re_hotal_detail);
		reHotalDetail.setOnClickListener(this);
		reHotalMap = (RelativeLayout) findViewById(R.id.re_hotal_map);
		reHotalMap.setOnClickListener(this);
		tvHotelAddress = (TextView) findViewById(R.id.tv_address);
		tvOrderDate = (TextView) findViewById(R.id.tv_order_date);
		tvRuzhuTime = (TextView) findViewById(R.id.tv_checkin_date);
		tvRoomType = (TextView) findViewById(R.id.tv_room_type);
		tvCheckInName = (TextView) findViewById(R.id.tv_room_person);
		tvWarnimg1 = (TextView) findViewById(R.id.tv_order_warning1);
		tvWarnimg2 = (TextView) findViewById(R.id.tv_order_warning2);
		tvPhoto = (TextView) findViewById(R.id.tv_order_photo);
		llCallPhoto = (LinearLayout) findViewById(R.id.ll_call_photo);
		llCallPhoto.setOnClickListener(this);
	}

	private void setupViewFlight() {
		scrollView = (ScrollView) findViewById(R.id.scrollView);
		llAddView = (LinearLayout) findViewById(R.id.ll_add_view);
		tvArrivalPortName = (TextView) findViewById(R.id.tv_order_stop_airport);
		tveDpartPortName = (TextView) findViewById(R.id.tv_order_start_airport);
		tvTakeOffTime = (TextView) findViewById(R.id.tv_order_takeoff);
		tvArrivalTime = (TextView) findViewById(R.id.tv_order_arrive_time);
		reTuigaiqian = (RelativeLayout) findViewById(R.id.re_tuigaiqian);
		reTuigaiqian.setOnClickListener(this);
		tvTuigaiqian = (TextView) findViewById(R.id.tv_tuigaiqian_content);
		imgTuigaiqian = (ImageView) findViewById(R.id.img_up_down_arrow);
	}

	private void initParams() {
		params = new HashMap<String, String>();
//		userId = app.getUserId();
		params.put("type", orderType);
//		params.put("tel", userId);
		params.put("orderID", orderId);
		params.put("u", app.getU());
		params.put("userId", app.getUserId());
		
		orderDetailUrl = getString(R.string.gionee_host) + FinalString.ORDERDETAIL_URL;
	}

	private void setupView() {
		app = (TravelApplication) getApplication();
		tvTitle = (TextView) findViewById(R.id.tv_title);
		tvTitle.setText(R.string.order_detail);
		tvTitle.setOnClickListener(this);
		tvName = (TextView) findViewById(R.id.tv_name);
		tvOrderStatus = (TextView) findViewById(R.id.tv_order_tatus);
		tvOrderPrice = (TextView) findViewById(R.id.tv_price);
		tvOrderCode = (TextView) findViewById(R.id.tv_order_code);
		tvContactName = (TextView) findViewById(R.id.tv_contact_person);
		tvContactMobile = (TextView) findViewById(R.id.tv_contact_person_mobile);
		rl_failed = (View) findViewById(R.id.rl_failed);
		rl_failed.setOnClickListener(this);
		rl_loading = (RelativeLayout) findViewById(R.id.rl_loading);
		img_loading = (ImageView) findViewById(R.id.img_loading);
	}

	private void loadingData() {
		startProgressDialog();

		if (!NetWorkUtil.isNetworkConnected(this)) {
			Message msg = Message.obtain();
			msg.what = FinalString.NET_ERROR;
			handler.sendMessageDelayed(msg, 1000);
			return;
		}
		task = new HttpConnUtil4Gionee(this);
		task.execute(orderDetailUrl, params, HttpConnUtil4Gionee.HttpMethod.POST);
	}

	public void loadDataFinish() {
		tvOrderStatus.setText(orderStatus);
		tvOrderPrice.setText(price);
		tvOrderCode.setText(orderId);
		tvContactName.setText(contactName);
		contactMobile = Utils.getId(contactMobile);
		tvContactMobile.setText(contactMobile);
		if ("flight".equals(orderType)) {
			tvName.setText(airLineName + flight);
			tvArrivalPortName.setText(arrivalPortName);
			tveDpartPortName.setText(departPortName);
			tvTakeOffTime.setText(takeOffTime);
			tvArrivalTime.setText(arrivalTime);
			rerNote = "更改说明: " + rerNote + "\n";
			endNote = "转签说明: " + endNote + "\n";
			refNote = "退票说明: " + refNote;
			tvTuigaiqian.setText(rerNote + endNote + refNote);
			for (int i = 0; i < listPassenger.size(); i++) {
				View view = this.getLayoutInflater().inflate(
						R.layout.order_add_passenger, null);
				TextView tvPassengerName = (TextView) view
						.findViewById(R.id.tv_dengji_person_name);
				TextView tvCardTypeNumber = (TextView) view
						.findViewById(R.id.tv_dengji_person_mobile);
				String passengerName = listPassenger.get(i).getName();
				String cardTypeNumber = listPassenger.get(i).getCode();
				cardTypeNumber = Utils.getKonggeCode(cardTypeNumber);
				tvPassengerName.setText(passengerName);
				tvCardTypeNumber.setText(cardTypeNumber);
				llAddView.addView(view);
			}

		} else {
			tvName.setText(hotelName);
			tvHotelAddress.setText(hotelAddress);
			int index = start.indexOf("T");
			start = start.substring(0, index);
			end = end.substring(0, index);
			tvOrderDate.setText(start);
			tvRoomType.setText(roomType);
			tvCheckInName.setText(checkInName);
			start = Utils.getMonthAndDay(start, OrderDetailActivity.this);
			end = Utils.getMonthAndDay(end, OrderDetailActivity.this);
			tvRuzhuTime.setText(start + "—" + end);
			String lateArrivalTimeBefore = lateArrivalTime.substring(0, index);
			lateArrivalTimeBefore = Utils.getMonthAndDay(lateArrivalTimeBefore,
					OrderDetailActivity.this);
			String hour = lateArrivalTime.substring(index + 1, index + 3);
			tvWarnimg1.setText(getString(R.string.please_yu)
					+ lateArrivalTimeBefore + hour
					+ getString(R.string.hotal_warning1));
			tvWarnimg2.setText(getString(R.string.hotal_warning2));
			tvPhoto.setText(Html.fromHtml("<u>" + hotelPhone + "</u>"));
		}

	}

	/**
	 * 点击退改签
	 */
	private void onClickTuiGaiQian() {
		if (tvTuigaiqian.getVisibility() == View.GONE) {
			tvTuigaiqian.setVisibility(View.VISIBLE);
			startArrowAnim(imgTuigaiqian);
			imgTuigaiqian.setImageDrawable(this.getResources().getDrawable(
					R.drawable.slt_newtrip_arrow_up_default));
			scrollView.post(new Runnable() {
				public void run() {
					scrollView.fullScroll(ScrollView.FOCUS_DOWN);
				}
			});

		} else {
			tvTuigaiqian.setVisibility(View.GONE);
			startArrowAnim(imgTuigaiqian);
			imgTuigaiqian.setImageDrawable(this.getResources().getDrawable(
					R.drawable.slt_newtrip_arrow_down_default));
		}
	}

	private void startArrowAnim(ImageView imageView) {
		Animation animation = (Animation) AnimationUtils.loadAnimation(this,
				R.anim.anim_arrow_rotate);
		imageView.setAnimation(animation);
		animation.start();
	}

	private void showHotelMap() {
		try {
			Intent detailMapIntent = new Intent();
			detailMapIntent.setClass(this, HotelMapDetailActivity.class);
			Hotel hotel = new Hotel();
			hotel.setAddressLine(simpleHotelBean.getAddressLine());
			hotel.setLatitude(simpleHotelBean.getLatitude());
			hotel.setLongitude(simpleHotelBean.getLongitude());
			detailMapIntent.putExtra("hotelDetail", hotel);
			startActivity(detailMapIntent);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.tv_title:
			this.finish();
			break;
		case R.id.re_tuigaiqian:
			onClickTuiGaiQian();
			break;
		case R.id.re_hotal_detail:
			gotoHotelDetail(tvHotalName.getText().toString());
			break;
		case R.id.re_hotal_map:
			showHotelMap();
			break;
		case R.id.ll_call_photo:
			callHotalService();
			break;
		case R.id.rl_failed:
			loadingData();
			break;
		case R.id.btn_function:
			showCustomServiceDialog();
			break;
		default:
			break;
		}
	}

	private void callHotalService() {
		hotelPhone = hotelPhone.replaceAll("-", "");
		Intent phoneIntent = new Intent("android.intent.action.CALL",
				Uri.parse("tel:" + hotelPhone));
		phoneIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		startActivity(phoneIntent);
	}

	@Override
	public void onReqSuc(String requestUrl, String json) {
		super.onReqSuc(requestUrl, json);
		stopProgressDialog();
		simpleHotelBean = JSONUtil.getInstance().fromJSON(
				SimpleHotelBean.class, json);

	}

	@Override
	public void onReqFail(String requestUrl, Exception ex, int errorCode) {
		super.onReqFail(requestUrl, ex, errorCode);
		stopProgressDialog();
		 //Toast.makeText(this, "没有查到该酒店信息", Toast.LENGTH_LONG).show();
	}

	private void gotoHotelDetail(String hotelName) {
		try {
			Hotel hotel = new Hotel();
			hotel.setAddressLine(simpleHotelBean.getAddressLine());
			hotel.setHotelCode(simpleHotelBean.getHotelCode());
			hotel.setHotelName(simpleHotelBean.getHotelName());
			hotel.setCommRate(simpleHotelBean.getCtripCommRate());

			Intent intent = new Intent();
			intent.setClass(this, HotelDetailActivity.class);
			Bundle bundle = new Bundle();
			bundle.putSerializable("hotelInfo", hotel);
			bundle.putBoolean("bookable", false);
			intent.putExtras(bundle);
			this.startActivity(intent);
		} catch (Exception e) {
			Toast.makeText(this, "没有查到该酒店信息", Toast.LENGTH_LONG).show();
			e.printStackTrace();
		}
	}

	@Override
	public void execute(String result) {
		if (TextUtils.isEmpty(result)) {
			Message msg = Message.obtain();
			msg.what = FinalString.NET_ERROR;
			handler.sendMessage(msg);
			return;
		}
		doResultThread = new Thread(new DoResult(result));
		doResultThread.start();
	}

	private class DoResult implements Runnable {
		/**
		 * 
		 * 
		 * "content": { "orderDate": "2014-07-15T17:37:39.87", "orderStatus":
		 * "全部退票", "orderID": "1058600866", "flight": "CZ6412 ", "airLineName":
		 * "南方航空", "dCityName": "北京", "aCityName": "上海", "takeOffTime":
		 * "2014-07-31T06:35:00", "arrivalTime": "2014-07-31T08:45:00",
		 * "rerNote":
		 * "起飞前2小时（含）以外同等舱位变更按对应舱位公布运价收取10％变更费；2小时以内及起飞后同等舱位变更按对应舱位公布运价收取30％变更费。改期费与升舱费同时发生时，则需同时收取改期费和升舱差额。"
		 * , "refNote":
		 * "起飞前2小时（含）以外办理退票按对应舱位公布运价收取30％的退票费，2小时以内及起飞后退票按对应舱位公布运价收取50％的退票费。",
		 * "endNote": "不得签转。", "departPortName": "北京首都国际机场二号航站楼",
		 * "arrivalPortName": "上海虹桥国际机场二号航站楼", "orderPassengers": [ {
		 * "passengerName": "赵谦", "cardTypeNumber": "360121199007062934" } ],
		 * "contactName": "魏群", "contactMobile": "018501052451" },
		 */
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
					Message msg = Message.obtain();
					msg.what = FinalString.NOT_FOUND;
					msg.obj = responseJson.getString("errorMsg");
					handler.sendMessage(msg);
					return;
				}
				String content = responseJson.getString("content");
				JSONObject contentJson = new JSONObject(content);
				if (TextUtils.isEmpty(price)) {
					price = contentJson.getString("price");
				}
				if (TextUtils.isEmpty(orderId)) {
					orderId = contentJson.getString("orderID");
				}
				if (TextUtils.isEmpty(orderStatus)) {
					orderStatus = contentJson.getString("orderStatus");
				}
				if ("flight".equals(orderType)) {
					flight = contentJson.getString("flight");
					airLineName = contentJson.getString("airLineName");
					takeOffTime = contentJson.getString("takeOffTime");
					arrivalTime = contentJson.getString("arrivalTime");
					// 时间格式转换
					int flightindex = takeOffTime.indexOf("T");
					takeOffTime = takeOffTime.substring(flightindex + 1,
							takeOffTime.length() - 3);
					arrivalTime = arrivalTime.substring(flightindex + 1,
							arrivalTime.length() - 3);

					rerNote = contentJson.getString("rerNote");
					refNote = contentJson.getString("refNote");
					endNote = contentJson.getString("endNote");

					departPortName = contentJson.getString("departPortName");
					arrivalPortName = contentJson.getString("arrivalPortName");
					orderPassengers = contentJson.getString("orderPassengers");
					JSONArray orderPassengersArray = new JSONArray(
							orderPassengers);
					listPassenger = new ArrayList<PassengerEntity>();
					
					for (int i = 0; i < orderPassengersArray.length(); i++) {
						PassengerEntity passengerEntity = new PassengerEntity();
						JSONObject jsonAll = (JSONObject) orderPassengersArray
								.get(i);
						passengerEntity.setCode(jsonAll
								.getString("cardTypeNumber"));
						passengerEntity.setName(jsonAll
								.getString("passengerName"));
						listPassenger.add(passengerEntity);
					}
					contactName = contentJson.getString("contactName");
					contactMobile = contentJson.getString("contactMobile");

				} else {
					hotelId = contentJson.getString("hotelId");
					hotelName = contentJson.getString("hotelName");
					hotelAddress = contentJson.getString("hotelAddress");
					start = contentJson.getString("start");
					end = contentJson.getString("end");
					hotelPhone = contentJson.getString("hotelPhone");
					roomType = contentJson.getString("roomType");
					checkInName = contentJson.getString("checkInName");
					lateArrivalTime = contentJson.getString("lateArrivalTime");
					contactName = contentJson.getString("contactName");
					contactMobile = contentJson.getString("contactPhone");
					if (!TextUtils.isEmpty(hotelId)) {
//						String hotelId = URLEncoder.encode(hotelId,
//								"utf-8");
						hotelinfoUrl = getString(R.string.gionee_host)
								+ FinalString.HOTELDETAILURLBYID + "hotelId="
								+ hotelId;
						post.doGet(hotelinfoUrl);
					}
				}

				Message msg = Message.obtain();
				msg.what = FinalString.DATA_FINISH;
				handler.sendMessage(msg);
			} catch (Exception e) {
				e.printStackTrace();
				stopProgressDialog();
				Toast.makeText(OrderDetailActivity.this, "查询失败", Toast.LENGTH_SHORT).show();
			}
		}
	}

	@Override
	protected void onDestroy() {
		stopProgressDialog();
		if (task != null)
			task.cancel(true);
		super.onDestroy();
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
		Toast.makeText(this, R.string.select_failture, Toast.LENGTH_LONG)
				.show();
		stopProgressDialog();
	}

	private void showCustomServiceDialog() {
		final Dialog dialog = new Dialog(this, R.style.CustomProgressDialog);
		View viewDailog = LayoutInflater.from(this).inflate(
				R.layout.delete_dialog, null);
		RelativeLayout reDeLayout = (RelativeLayout) viewDailog
				.findViewById(R.id.re_dialog);
		TextView title = (TextView) viewDailog
				.findViewById(R.id.tv_dialog_title_name);
		title.setText(this.getString(R.string.custom_dialog_title));
		TextView tcContent = (TextView) viewDailog
				.findViewById(R.id.tv_dialog_title);
		tcContent.setText(this.getString(R.string.custom_dialog_content));
		Button btnCalcel = (Button) viewDailog
				.findViewById(R.id.btn_dialog_cancel);
		btnCalcel.setText(this.getString(R.string.cancel));
		Button btnOk = (Button) viewDailog.findViewById(R.id.btn_dialog_sure);
		btnOk.setText(this.getString(R.string.call));
		btnCalcel.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				dialog.dismiss();
			}
		});
		btnOk.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				callCustomService();
				dialog.dismiss();
			}
		});
		reDeLayout.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				dialog.dismiss();
			}
		});
		dialog.setContentView(viewDailog);
		dialog.getWindow().setLayout(LayoutParams.MATCH_PARENT,
				LayoutParams.MATCH_PARENT);
		dialog.show();
	}

	private void callCustomService() {
		Intent phoneIntent = new Intent("android.intent.action.CALL",
				Uri.parse("tel:"
						+ getString(R.string.custom_service_phoneNumber)));
		phoneIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		startActivity(phoneIntent);
	}
}
