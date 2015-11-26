package com.gionee.gntravel;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.ContactsContract;
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
import android.view.inputmethod.InputMethodManager;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.PopupWindow.OnDismissListener;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.gionee.gntravel.adapter.ChooseRoomNumAdapter;
import com.gionee.gntravel.adapter.ChooseRoomNumAdapter.onRoomSelectListner;
import com.gionee.gntravel.customview.CustomProgressDialog;
import com.gionee.gntravel.db.dao.ContactsDao;
import com.gionee.gntravel.db.dao.impl.ContactsDaoImpl;
import com.gionee.gntravel.entity.CancelPenaltyAvail;
import com.gionee.gntravel.entity.ContentBean;
import com.gionee.gntravel.entity.GuaranteePayment;
import com.gionee.gntravel.entity.GuestRoom;
import com.gionee.gntravel.entity.HotelOrderBean;
import com.gionee.gntravel.entity.HotelReservation;
import com.gionee.gntravel.entity.Person;
import com.gionee.gntravel.entity.RoomStay;
import com.gionee.gntravel.utils.DBUtils;
import com.gionee.gntravel.utils.FinalString;
import com.gionee.gntravel.utils.HttpConnCallback;
import com.gionee.gntravel.utils.HttpConnUtil4Gionee;
import com.gionee.gntravel.utils.JSONUtil;
import com.gionee.gntravel.utils.NetWorkUtil;
import com.gionee.gntravel.utils.Utils;
import com.gionee.gntravel.utils.ValidateUtil;

public class HotelOrderActivity extends BaseActivity implements OnClickListener, onRoomSelectListner, HttpConnCallback {
	private static final int CONTACTS_DATA_FINISH = 0x5008;// 加载完常用联系人信息之后
	private static final int EXCEPTION_DATA_FINISH = 0x5007;// 异常
	private List<String> list = new ArrayList<String>();
	private ArrayList<Boolean> isSelectorValueContacts;// 弹出的布局里面的图片是否被选中(联系人)
	private HashMap<String, String> params;
	private String selectContactUrl;
	private HttpConnUtil4Gionee task;
	private ResultHandler handler = new ResultHandler();
	private CustomProgressDialog progressDialog;
	private ContentBean contactsLocation;// 上次选中的数据( 联系人)
	private LinearLayout llAddContacts;//
	private LinearLayout ll_Container;
	private ContactsAdaper contactsAdaper;
	private ListView listView;
	private TextView tvContactFrom;// 从通讯录
	private Button btnSure;// 弹出框中的确定
	private Button btnCancel;// 弹出框中的取消
	private EditText etRoomName;
	private GuestRoom guestRoom;
	private String indate;
	private String leavedate;
	private int mPostion = 0;
	private PopupWindow mPopupWindow;
	private List<Person> listContacts = new ArrayList<Person>();// 联系人
	private View view;
	private TextView activity_hotel_order_tv_roomnum;
	private Button activity_hotel_order_submitorder;
	private LinearLayout hotel_order_ll_cus;
	private TextView order_tv_roomcount;
	private TextView order_tv_orderpice;
	private String unit_price;
	private TextView order_tv_leavedate;
	private TextView order_tv_indate;
	private int numberOfUnits = 1;
	private EditText hotelorder_et_contact;
	private EditText hotelorder_et_phone;
	private String CHECK_HOTEL_AVAIL_URL;
	private String SUBMIT_HOTEL_ORDER_URL;
	private String REQ_HOTELPOLICY_URL;
	private HotelOrderBean orderBean;
	private String roomName;
	private String hotelName;
	private TextView hotelorder_tv_hotelname;
	private TextView hotel_order_tv_roomtype;
	private TextView hotel_order_tv_desc;
	private TextView hotel_order_tv_desctitle;
	private boolean isfirst = true;
	private int tempPosition;
	private View popupView;
	// private String leaveDatestr;
	private RelativeLayout rl_loading;
	private ImageView img_loading;
	private TextView activity_hotel_order_tv_inuser;
	private Button btnAddContact;
	private boolean isFirst = true;
	private View activity_hote_order_ll;
	// private String SAVEORDERURL;
	private HotelReservation hotelReservation;
	private TravelApplication app;
	private String hotelCode;
	private String reqHotelPolicyUrl;
	private boolean isPopIsDismissing;
	private RelativeLayout rl_failed;
	private ImageView iv_failed;
	private TextView btn_refresh;
	private ScrollView activity_sv_order;
	private LinearLayout activity_ll_bottom;

	private class ResultHandler extends Handler {

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case CONTACTS_DATA_FINISH:
				loadDataContactsFinish();
				dismissProgress();
				break;
			case FinalString.NET_ERROR:
				showNetErrorMsg();
				break;
			case FinalString.NOT_FOUND:
				selecttNotMsg(msg);

				break;
			case EXCEPTION_DATA_FINISH:
				dismissProgress();
				break;
			}
		}

	}

	/**
	 * 加载完常用联系人信息之后
	 */
	private void loadDataContactsFinish() {
		showSearch();
		contactsAdaper = new ContactsAdaper();
		listView.setAdapter(contactsAdaper);
		contactsAdaper.notifyDataSetChanged();
	}

	private void selecttNotMsg(Message msg) {
		if ("没有查询到相应的结果!".equals((String) msg.obj)) {
			dismissProgress();
			showSearch();
		}
	}

	private void showSearch() {
		activity_hote_order_ll.setEnabled(false);
		btnSure.setEnabled(true);
		btnAddContact.setEnabled(false);
		if (llAddContacts.getVisibility() == View.GONE && ll_Container.getVisibility() == View.GONE) {

			llAddContacts.setVisibility(View.VISIBLE);
			ll_Container.setAnimation(AnimationUtils.loadAnimation(this, R.anim.anim_search_container_in));
			ll_Container.setVisibility(View.VISIBLE);

		}
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_hotel_order);
		app = (TravelApplication) getApplication();
		initData();
		findViews();
		findLocationContact();
		findLocationRoompreson();
		setListner();
		checkAvail();
	}

	private String findLocationRoompreson() {
		DBUtils dbUitls = new DBUtils(HotelOrderActivity.this);
		SQLiteDatabase db = dbUitls.openReadOnlyDatabase();
		ContactsDao dao = new ContactsDaoImpl();
		ContentBean roomPerson = dao.findContactsByUserId(db, app.getU(), "hotelroom");
		if (roomPerson != null) {
			return roomPerson.getName();
		}
		return null;
	}

	private void insertRoomperson(String name) {
		// 查询 数据库 如果数据中有此数据，则至为true
		DBUtils dbUitls = new DBUtils(HotelOrderActivity.this);
		SQLiteDatabase db = dbUitls.openReadWriteDatabase();
		ContactsDao dao = new ContactsDaoImpl();
		dao.delectContactsByType(db, app.getU(), "hotelroom");
		dao.insertContactsByUserId(db, app.getU(), name, "", "0000", "hotelroom");

	}

	private void initData() {
		String url_head = getString(R.string.gionee_host) + FinalString.HOTEL_ACTION;
		CHECK_HOTEL_AVAIL_URL = url_head + "checkHotelAvail.action";
		SUBMIT_HOTEL_ORDER_URL = url_head + "createHotelOrder.action";
		REQ_HOTELPOLICY_URL = url_head + "findHotelPolicyByHotelCode.action";
		// SAVEORDERURL = url_head + "saveHotelOrderInfo.action";
		list.add("一间");
		list.add("二间");
		list.add("三间");
		list.add("四间");
		list.add("五间");
		list.add("六间");
		list.add("七间");
		list.add("八间");
		Bundle b = this.getIntent().getExtras();
		guestRoom = (GuestRoom) b.get("room");
		hotelCode = guestRoom.getHotelCode();
		roomName = guestRoom.getRoomTypeName();

		indate = (String) b.get("indate");
		leavedate = (String) b.get("leavedate");
		hotelName = (String) b.get("hotelName");
		unit_price = (guestRoom.getAmountBeforeTax() + "").replace("￥", "");

		orderBean = new HotelOrderBean();
	}

	private void setListner() {
		activity_hote_order_ll.setOnClickListener(this);
		findViewById(R.id.tv_title).setOnClickListener(this);
		activity_hotel_order_submitorder.setOnClickListener(this);
	}

	// 选择房间数量
	private void showSelectRoom() {
		popupView = this.getLayoutInflater().inflate(R.layout.window_room_num, null);
		ListView window_room_lv = (ListView) popupView.findViewById(R.id.window_room_lv);
		ChooseRoomNumAdapter adapter = new ChooseRoomNumAdapter(this, list, mPostion);
		adapter.setOnRoomSelectListner(this);
		window_room_lv.setAdapter(adapter);
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
				AnimationUtils.loadAnimation(this, R.anim.menu_bottombar_in));
		setWindowCloseListener();
	}

	private void setWindowCloseListener() {
		mPopupWindow.setOnDismissListener(new OnDismissListener() {

			@Override
			public void onDismiss() {
				isPopIsDismissing = false;
			}
		});
	}

	private void findViews() {
		view = getLayoutInflater().inflate(R.layout.fragment_list_hotel, null);
		rl_failed = (RelativeLayout) findViewById(R.id.rl_failed);
		activity_ll_bottom = (LinearLayout) findViewById(R.id.activity_ll_bottom);
		iv_failed = (ImageView) findViewById(R.id.iv_failed);
		rl_failed.setOnClickListener(this);
		btn_refresh = (TextView) findViewById(R.id.btn_refresh);
		activity_sv_order = (ScrollView) findViewById(R.id.activity_sv_order);
		btnAddContact = (Button) findViewById(R.id.btn_select_contacts);
		btnAddContact.setOnClickListener(this);
		llAddContacts = (LinearLayout) findViewById(R.id.ll_add_contact);
		llAddContacts.setOnClickListener(this);
		ll_Container = (LinearLayout) findViewById(R.id.ll_container);
		listView = (ListView) findViewById(R.id.lv_contact);
		tvContactFrom = (TextView) findViewById(R.id.tv_add_contact);
		tvContactFrom.setOnClickListener(this);
		btnSure = (Button) findViewById(R.id.btn_add_contact_sure);
		btnSure.setOnClickListener(this);
		btnCancel = (Button) findViewById(R.id.btn_add_contact_cancel);
		btnCancel.setOnClickListener(this);

		activity_hotel_order_tv_roomnum = (TextView) findViewById(R.id.activity_hotel_order_tv_roomnum);
		activity_hote_order_ll = findViewById(R.id.activity_hote_order_ll);
		activity_hotel_order_tv_inuser = (TextView) findViewById(R.id.activity_hotel_order_tv_inuser);
		order_tv_roomcount = (TextView) findViewById(R.id.order_tv_roomcount);
		order_tv_orderpice = (TextView) findViewById(R.id.order_tv_orderpice);
		order_tv_leavedate = (TextView) findViewById(R.id.activity_hotel_order_tv_leavedate);
		order_tv_indate = (TextView) findViewById(R.id.activity_hotel_order_tv_indate);
		hotelorder_et_phone = (EditText) findViewById(R.id.activity_hotelorder_et_phone);
		hotelorder_et_contact = (EditText) findViewById(R.id.activity_hotelorder_et_contact);
		hotelorder_tv_hotelname = (TextView) findViewById(R.id.activity_hotel_order_tv_hotelname);
		hotel_order_tv_roomtype = (TextView) findViewById(R.id.activity_hotel_order_tv_roomtype);
		hotel_order_tv_desctitle = (TextView) findViewById(R.id.activity_hotel_order_tv_attentiontitle);
		hotel_order_tv_desc = (TextView) findViewById(R.id.activity_hotel_order_tv_attention);
		rl_loading = (RelativeLayout) view.findViewById(R.id.rl_loading);
		img_loading = (ImageView) view.findViewById(R.id.img_loading);
		hotel_order_tv_roomtype.setText(roomName);
		hotelorder_tv_hotelname.setText(hotelName);
		try {
			Date leavedateD = getStrDateFormater().parse(leavedate);
			String leavedate_str = getFormater().format(leavedateD);
			Date indateD = getStrDateFormater().parse(indate);
			String indate_str = getFormater().format(indateD);
			order_tv_leavedate.setText(leavedate_str);
			order_tv_indate.setText(indate_str);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		TextView tv_title = (TextView) findViewById(R.id.tv_title);
		tv_title.setText(getString(R.string.hotel_order_title));
		activity_hotel_order_submitorder = (Button) findViewById(R.id.activity_hotel_order_submitorder);
		hotel_order_ll_cus = (LinearLayout) findViewById(R.id.activity_hotel_order_ll_cus);

		for (int i = 0; i < 8; i++) {
			View layout = getLayoutInflater().inflate(R.layout.item_cus_name, null);
			if (i == 0) {
				etRoomName = (EditText) layout.findViewById(R.id.item_cus_et_name);
				if (findLocationRoompreson() != null) {
					etRoomName.setText(findLocationRoompreson());

				}
			}
			hotel_order_ll_cus.addView(layout);
		}

		selectRoom(0);
		Utils.mobileAddSpace(13, hotelorder_et_phone);

	}

	private boolean hideSearch() {
		activity_hote_order_ll.setEnabled(true);
		btnAddContact.setEnabled(true);

		if (llAddContacts.getVisibility() == View.VISIBLE && ll_Container.getVisibility() == View.VISIBLE) {
			Animation anim = AnimationUtils.loadAnimation(this, R.anim.anim_search_container_out);
			anim.setAnimationListener(new AnimationListener() {

				@Override
				public void onAnimationStart(Animation animation) {

				}

				@Override
				public void onAnimationRepeat(Animation animation) {

				}

				@Override
				public void onAnimationEnd(Animation animation) {
					llAddContacts.setVisibility(View.GONE);

				}
			});
			ll_Container.setAnimation(anim);
			ll_Container.setVisibility(View.GONE);

			return true;
		}
		return false;
	}

	private void initParamsContacts() {
		params = new HashMap<String, String>();
		params.put("userId", app.getUserId());
		params.put("u", app.getU());
		selectContactUrl = getString(R.string.gionee_host) + "/GioneeTrip/tripAction_findUserAllContacterInfo.action";
	}

	private void loadingDataContacts() {
		showProgress();
		if (!NetWorkUtil.isNetworkConnected(this)) {
			Message msg = Message.obtain();
			msg.what = FinalString.NET_ERROR;
			handler.sendMessageDelayed(msg, 1000);
			return;
		}
		task = new HttpConnUtil4Gionee(this);
		task.execute(selectContactUrl, params, HttpConnUtil4Gionee.HttpMethod.POST);
	}

	@Override
	public void execute(String response) {
		if (TextUtils.isEmpty(response)) {
			Message msg = Message.obtain();
			msg.what = FinalString.NET_ERROR;
			handler.sendMessage(msg);
			return;
		}
		new Thread(new DoResult(response)).start();
	}

	public class DoResult implements Runnable {
		private String result;

		public DoResult(String result) {
			this.result = result;
		}

		@Override
		public void run() {
			try {
				JSONObject responseJson = new JSONObject(result);
				if (!FinalString.ERRORCODE.equals(responseJson.getString("errorCode"))) {
					Message msg = Message.obtain();
					msg.what = FinalString.NOT_FOUND;
					msg.obj = responseJson.getString("errorMsg");
					handler.sendMessage(msg);
					return;
				}
				String content = responseJson.getString("content");
				// 加载常用旅客信息
				// 查询所有

				isSelectorValueContacts = new ArrayList<Boolean>();
				JSONArray jsonArray = new JSONArray(content);
				for (int i = 0; i < jsonArray.length(); i++) {
					boolean isSelect = false; // 是否被选中
					JSONObject Item = jsonArray.getJSONObject(i);
					String id = Item.getString("id");
					String code = Item.getString("telphone");
					String name = Item.getString("name");
					Person person = new Person();
					person.setId(id);
					person.setCode(code);
					person.setName(name);
					if (contactsLocation == null) {
						isSelectorValueContacts.add(i, false);
						person.setFlag(false);
					} else {//因为改为本地没有存id 。所以这只能比对手机号和姓名2015-04-02
						if (contactsLocation.getCode().equals(code)&&contactsLocation.getName().equals(name)) {
							isSelect = true;
						}

						if (isSelect) {
							isSelectorValueContacts.add(i, true);
							person.setFlag(true);
						} else {
							isSelectorValueContacts.add(i, false);
							person.setFlag(false);
						}

					}
					listContacts.add(person);
				}
				Message msg = Message.obtain();
				msg.what = CONTACTS_DATA_FINISH;
				handler.sendMessage(msg);

			}

			catch (Exception e) {
				e.printStackTrace();
				Message msg = Message.obtain();
				msg.what = EXCEPTION_DATA_FINISH;
				handler.sendMessage(msg);
			}

		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.ll_add_contact:
			hideSearch();
			break;
		case R.id.rl_failed:
			checkAvail();
			break;
		case R.id.activity_hote_order_ll:
			showSelectRoom();
			break;
		case R.id.tv_title:
			finish();
			break;
		case R.id.btn_select_contacts:
			listContacts.clear();
			initParamsContacts();
			loadingDataContacts();
			break;
		case R.id.activity_hotel_order_submitorder:
			submitOrder();
		case R.id.tv_add_contact:// 弹出框内的新增登机人或者是新增联系人
			Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
			intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
			startActivityForResult(intent, 0);

			break;
		case R.id.btn_add_contact_cancel:
			hideSearch();

			break;
		case R.id.btn_add_contact_sure:// 弹出框内的确定按钮
			try {
				btnSure.setEnabled(false);
				// 存入数据库
				DBUtils dbUitls = new DBUtils(HotelOrderActivity.this);
				SQLiteDatabase db = dbUitls.openReadWriteDatabase();
				ContactsDao dao = new ContactsDaoImpl();
				dao.delectContactsByType(db, app.getU(), "hotelcontact");
				for (int i = 0; i < listContacts.size(); i++) {
					if (isSelectorValueContacts.get(i)) {// 选中哪个 哪个就是真了
						listContacts.get(i).setFlag(true);
						// 存入数据库中的上次选中的数据
						// 选中多个出错 isSelectorValue标志位是对的 显示不对。
						String idLocation = listContacts.get(i).getId();
						String nameLocation = listContacts.get(i).getName();
						String codeLocation = listContacts.get(i).getCode();
						dao.insertContactsByUserId(db, app.getU(), nameLocation, codeLocation, idLocation,
								"hotelcontact");
						hotelorder_et_contact.setText(nameLocation);
						hotelorder_et_phone.setText(codeLocation);

					} else {
						listContacts.get(i).setFlag(false);

					}

				}
				findLocationContact();

				hideSearch();
			} catch (Exception e) {
				e.printStackTrace();
			}

			break;
		default:
			break;
		}
	}

	/**
	 * 查询本地数据库(上次状态联系人)
	 */
	private void findLocationContact() {
		// 查询 数据库 如果数据中有此数据，则至为true
		DBUtils dbUitls = new DBUtils(HotelOrderActivity.this);
		SQLiteDatabase db = dbUitls.openReadOnlyDatabase();
		ContactsDao dao = new ContactsDaoImpl();

		contactsLocation = dao.findContactsByUserId(db, app.getU(), "hotelcontact");
		if (contactsLocation != null) {
			hotelorder_et_phone.setText(contactsLocation.getCode());
			hotelorder_et_contact.setText(contactsLocation.getName());
		}
	}

	/**
	 * 提交订单，首先检查是否可定
	 * 
	 * @Author: yangxy
	 * @Version: V1.0
	 * @Create Date: 2014-7-3
	 */
	private void submitOrder() {
		insertRoomperson(etRoomName.getText().toString());
		if (checkLocalInfo()) {
			showProgress();
			String orderBeanJson = JSONUtil.getInstance().toJSON(orderBean);
			HashMap<String, String> params = new HashMap<String, String>();
			params.put("orderBean", orderBeanJson);
			TravelApplication app = (TravelApplication) getApplication();
			String tripName = app.getTripName();
			try {
				tripName = URLEncoder.encode(tripName, "UTF-8");
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
			params.put("tripName", tripName);
			post.doPost(SUBMIT_HOTEL_ORDER_URL, params);
		}
	}

	private boolean checkLocalInfo() {
		String contactName = hotelorder_et_contact.getText().toString();
		String cusName = "";
		for (int j = 0; j < numberOfUnits; j++) {
			String cusNameSingle = ((EditText) hotel_order_ll_cus.getChildAt(j).findViewById(R.id.item_cus_et_name))
					.getText().toString();
			if (TextUtils.isEmpty(cusNameSingle)) {
				Toast.makeText(this, "请输入入住人姓名!", Toast.LENGTH_LONG).show();
				return false;
			}
			cusName = cusName + cusNameSingle + ",";
		}
		cusName = cusName.substring(0, cusName.length() - 1);
		if (TextUtils.isEmpty(contactName)) {
			Toast.makeText(this, "请输入联系人姓名!", Toast.LENGTH_LONG).show();
			return false;
		}
		try {
			cusName = URLEncoder.encode(cusName, "utf-8");
			contactName = URLEncoder.encode(contactName, "utf-8");
		} catch (UnsupportedEncodingException e1) {
			e1.printStackTrace();
		}
		double amount = 0;
		String phone = hotelorder_et_phone.getText().toString();
		phone = phone.replaceAll(" ", "");
		if (TextUtils.isEmpty(phone)) {
			Toast.makeText(this, "请输入联系人手机号!", Toast.LENGTH_LONG).show();
			return false;
		} else {
			if (!ValidateUtil.isValidPhone(phone)) {
				Toast.makeText(this, "联系人手机号格式不正确!", Toast.LENGTH_LONG).show();
				return false;
			}
		}
		String orderpice = order_tv_orderpice.getText().toString();
		if (!"未知".equals(orderpice)) {
			amount = Double.parseDouble(orderpice);
		}
		orderBean.setCustomName(cusName);
		orderBean.setAmountBeforeTax(amount);
		orderBean.setContactName(contactName);
		orderBean.setPhoneNumber(phone);
		String userId = "";
		userId = app.getUserId();
		orderBean.setUserId(userId);
		orderBean.setU(app.getU());
		return true;
	}

	private void checkAvail() {
		orderBean.setEnd(leavedate + "T12:00:00");
		orderBean.setStart(indate + "T12:00:00");
		orderBean.setLateArrivalTime(indate + "T18:00:00");
		orderBean.setCount(numberOfUnits);
		orderBean.setNumberOfUnits(numberOfUnits);
		orderBean.setRatePlanCode(guestRoom.getRoomTypeCode());
		orderBean.setHotelCode(hotelCode);
		orderBean.setRatePlanCategory(guestRoom.getRatePlanCategory());
		// 可定检查
		showProgress();
		rl_failed.setVisibility(View.GONE);
		activity_ll_bottom.setVisibility(View.VISIBLE);
		activity_sv_order.setVisibility(View.VISIBLE);
		post.doPost(CHECK_HOTEL_AVAIL_URL, orderBean);
	}

	@Override
	public void selectRoom(int position) {
		tempPosition = position;
		numberOfUnits = position + 1;
		if (isfirst) {
			mPostion = position;
			isfirst = false;
			refreshView(tempPosition);
		} else {
			isfirst = false;
			checkAvail();
		}
	}

	@Override
	public void onReqSuc(String requestUrl, String json) {
		super.onReqSuc(requestUrl, json);
		if (CHECK_HOTEL_AVAIL_URL.equals(requestUrl)) {
			refreshView(tempPosition);
			RoomStay roomStay = JSONUtil.getInstance().fromJSON(RoomStay.class, json);
			String availabilityStatus = roomStay.getAvailabilityStatus();
			if ("NoAvailability".equals(availabilityStatus)) {
				Toast.makeText(this, "当前房间不可预订！", Toast.LENGTH_LONG).show();
				this.finish();
				return;
			}
			
			final GuaranteePayment guaranteePayment = roomStay.getGuaranteePayment();
			String price = "";
			if (guaranteePayment != null) {//说明是担保的酒店，类型：手机担保，一律担保
				final CancelPenaltyAvail avail = roomStay.getCancelPenalty();
				String guarateeCode = guaranteePayment.getGuaranteeCode();
				price = guaranteePayment.getAmount();
				String desc = guaranteePayment.getDescription();
				if (roomStay.getPrepaidIndicator()!=null&&"true".equals(roomStay.getPrepaidIndicator())) {
					activity_hotel_order_submitorder.setText("去支付");
					activity_hotel_order_submitorder.setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View v) {
							submitOrder();
						}
					});
				}else{
					if (!TextUtils.isEmpty(price)) {//普通担保
						if (!TextUtils.isEmpty(desc)) {
							hotel_order_tv_desctitle.setText("担保规则");
							desc = desc.replaceAll("\\{Money\\}", roomStay.getAmountBeforeTax());
							hotel_order_tv_desc.setText(desc);
						}
						activity_hotel_order_submitorder.setText("担保");
						activity_hotel_order_submitorder.setOnClickListener(new OnClickListener() {

							@Override
							public void onClick(View v) {
								if (!checkLocalInfo()) {
									return;
								}
								String p = guaranteePayment.getAmount();
								String prices[] = p.split("\\.");
								if (prices.length > 0) {
									p = prices[0];
								}
								Intent i = new Intent(HotelOrderActivity.this, HotelDepositActivity.class);
								Bundle b = new Bundle();
								b.putString("indate", indate);
								b.putString("leavedate", leavedate);
								b.putString("roomName", roomName);
								b.putString("hotelName", hotelName);
								b.putSerializable("orderBean", orderBean);
								b.putString("price", p);
								b.putSerializable("cancelPanelty", avail);
								i.putExtras(b);
								startActivity(i);
							}
						});
					} else {//手机担保酒店。
						orderBean.setGuaranteecode(guarateeCode);
						hotel_order_tv_desc.setText(desc);
						activity_hotel_order_submitorder.setText("提交订单");
						price = roomStay.getAmountBeforeTax();
						activity_hotel_order_submitorder.setOnClickListener(new OnClickListener() {

							@Override
							public void onClick(View v) {
								submitOrder();
							}
						});
					}
				}
				
				dismissProgress();
			} else {//非担保酒店
				activity_hotel_order_submitorder.setText("提交订单");
				price = roomStay.getAmountBeforeTax();
				activity_hotel_order_submitorder.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						submitOrder();
					}
				});
				reqHotelPolicyUrl = REQ_HOTELPOLICY_URL + "?hotelCode=" + hotelCode;
				post.doGet(reqHotelPolicyUrl);
			}
			String prices[] = price.split("\\.");
			if (prices.length > 0) {
				price = prices[0];
			}
			// 生成订单
			order_tv_orderpice.setText(price);
		}
		if (!TextUtils.isEmpty(reqHotelPolicyUrl) && reqHotelPolicyUrl.equals(requestUrl)) {
			try {
				JSONObject obj = new JSONObject(json);
				String policy = obj.getString("hotelPolicy");
				policy = this.getResources().getString(R.string.hotel_attention_ruzhutime) + policy
						+ this.getResources().getString(R.string.hotel_attention_fapiao);
				hotel_order_tv_desc.setText(policy);
				dismissProgress();
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		if (SUBMIT_HOTEL_ORDER_URL.equals(requestUrl)) {
			hotelReservation = JSONUtil.getInstance().fromJSON(HotelReservation.class, json);
			if ("S".equals(hotelReservation.getResStatus())) {
				String contactId = hotelReservation.getCid();
				insertContact(contactId);
				Intent i = new Intent();
				Bundle b = new Bundle();
				b.putSerializable("hotelReservation", hotelReservation);
				b.putString("indate", indate);
				b.putString("leavedate", leavedate);
				b.putString("roomName", roomName);
				b.putString("hotelName", hotelName);
				b.putSerializable("orderBean", orderBean);
				i.putExtras(b);
				i.setClass(this, HotelOrderSucActivity.class);
				finish();
				startActivity(i);
				Intent refreshIntent = new Intent(FinalString.REFRESH_TRIP_ACTION);
				this.sendBroadcast(refreshIntent);
			}else{
				Toast.makeText(this.getApplication(),"预定失败！" ,0).show();
			}
			dismissProgress();
		}

	}

	/**
	 * 插入联系人信息到本地数据库
	 */
	private void insertContact(String id) {
		DBUtils dbUitls = new DBUtils(HotelOrderActivity.this);
		SQLiteDatabase db = dbUitls.openReadWriteDatabase();
		ContactsDao dao = new ContactsDaoImpl();
		dao.delectContactsByType(db, app.getU(), "hotelcontact");
		dao.insertContactsByUserId(db, app.getU(), hotelorder_et_contact.getText().toString(), hotelorder_et_phone
				.getText().toString(), null, "hotelcontact");//可以不用根据id=null，插入本地服务器2015-4-2,改为
	}

	private void refreshView(int position) {
		mPostion = position;
		numberOfUnits = position + 1;
		activity_hotel_order_tv_roomnum.setText(list.get(mPostion));
		order_tv_roomcount.setText(list.get(mPostion));
		if (!TextUtils.isEmpty(order_tv_orderpice.getText().toString())) {

			if ("未知".equals(unit_price)) {
				order_tv_orderpice.setText(unit_price);
			} else {
				order_tv_orderpice.setText((int) Float.parseFloat(unit_price) * (mPostion + 1) + "");
			}
		}
		for (int i = 0; i < 8; i++) {
			if (i <= mPostion) {
				hotel_order_ll_cus.getChildAt(i).setVisibility(View.VISIBLE);
			} else {
				hotel_order_ll_cus.getChildAt(i).setVisibility(View.GONE);
			}
			if (mPostion == i) {
				hotel_order_ll_cus.getChildAt(i).findViewById(R.id.item_cus_name_tv_line).setVisibility(View.GONE);
			} else {
				hotel_order_ll_cus.getChildAt(i).findViewById(R.id.item_cus_name_tv_line).setVisibility(View.VISIBLE);
			}
		}
		if (mPopupWindow != null) {
			popwindowDismiss();
		}
	}

	@Override
	public void onReqFail(String requestUrl, Exception ex, int errorCode) {
		super.onReqFail(requestUrl, ex, errorCode);
		if (ex instanceof IOException) {
			showNetErrorMsg();
			return;
		}
		Toast.makeText(this, ex.getMessage(), Toast.LENGTH_LONG).show();
		if (CHECK_HOTEL_AVAIL_URL != null && CHECK_HOTEL_AVAIL_URL.equals(requestUrl)) {
			if (mPopupWindow != null) {
				popwindowDismiss();
			}
			InputMethodManager im = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
			if (im.isAcceptingText())
				im.toggleSoftInput(InputMethodManager.SHOW_IMPLICIT, InputMethodManager.HIDE_NOT_ALWAYS);
			finish();
			return;
		}
		if (reqHotelPolicyUrl != null && reqHotelPolicyUrl.endsWith(requestUrl)) {
			Toast.makeText(this, "获取酒店注意事项失败！", Toast.LENGTH_LONG).show();
		}
		finish();
		dismissProgress();
	}

	public void showNetErrorMsg() {
		dismissProgress();
		activity_sv_order.setVisibility(View.GONE);
		rl_loading.setVisibility(View.GONE);
		activity_ll_bottom.setVisibility(View.GONE);
		iv_failed.setImageResource(R.drawable.wifi);
		btn_refresh.setText(getResources().getString(R.string.net_refresh));
		rl_failed.setVisibility(View.VISIBLE);
	}

	private void popwindowDismiss() {
		if (!isPopIsDismissing) {
			Animation out_anim = AnimationUtils.loadAnimation(this, R.anim.menu_bottombar_out);
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
		isPopIsDismissing = true;
	}

	public final class ViewHolder {
		public TextView name;
		public TextView shenfenzheng;
		public TextView code;
		public ImageView value;
	}

	private class ContactsAdaper extends BaseAdapter {

		@Override
		public int getCount() {
			return listContacts.size();
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
			ViewHolder holder = null;

			if (convertView == null) {// convertView 存放item的
				holder = new ViewHolder();
				convertView = getLayoutInflater().inflate(R.layout.contacts_item, null);
				holder.name = (TextView) convertView.findViewById(R.id.tv_item_name);
				holder.shenfenzheng = (TextView) convertView.findViewById(R.id.tv_item_shenfenzhengjian);
				holder.shenfenzheng.setText(getString(R.string.mobile));
				holder.code = (TextView) convertView.findViewById(R.id.tv_item_shenfenzhenghao);
				holder.value = (ImageView) convertView.findViewById(R.id.listview_checkbox);
				convertView.setTag(holder);

			} else {

				holder = (ViewHolder) convertView.getTag();
			}

			holder.name.setText(listContacts.get(position).getName());
			holder.code.setText(listContacts.get(position).getCode());
			if (isSelectorValueContacts.get(position) == false) {
				holder.value.setImageResource(R.drawable.search_radiobtn_normal);
			} else {
				holder.value.setImageResource(R.drawable.search_radiobtn_press);
			}

			convertView.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					if (isSelectorValueContacts.get(position) == false) {

						isSelectorValueContacts.remove(position);
						isSelectorValueContacts.add(position, true);
						for (int j = 0; j < isSelectorValueContacts.size(); j++) {

							if (isSelectorValueContacts.get(j) == true && j != position) {
								isSelectorValueContacts.remove(j);
								isSelectorValueContacts.add(j, false);
							}
						}

					} else {
						isSelectorValueContacts.remove(position);
						isSelectorValueContacts.add(position, false);
					}

					notifyDataSetChanged();
				}
			});
			return convertView;
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
		try {
			if (resultCode == Activity.RESULT_OK) {
				// ContentProvider展示数据类似一个单个数据库表
				// ContentResolver实例带的方法可实现找到指定的ContentProvider并获取到ContentProvider的数据
				ContentResolver reContentResolverol = getContentResolver();
				// URI,每个ContentProvider定义一个唯一的公开的URI,用于指定到它的数据集
				Uri contactData = intent.getData();
				// 查询就是输入URI等参数,其中URI是必须的,其他是可选的,如果系统能找到URI对应的ContentProvider将返回一个Cursor对象.
				Cursor cursor = managedQuery(contactData, null, null, null, null);
				cursor.moveToFirst();
				// 获得DATA表中的名字
				String username = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
				// 条件为联系人ID
				String contactId = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
				// 获得DATA表中的电话号码，条件为联系人ID,因为手机号码可能会有多个
				Cursor phone = reContentResolverol.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
						ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?", new String[] { contactId }, null,
						null);
				String usernumber = null;
				if (phone != null) {
					hideSearch();
					hotelorder_et_contact.setText(username);
					while (phone.moveToNext()) {
						usernumber = phone.getString(phone
								.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));

						hotelorder_et_phone.setText(usernumber);
					}
				} else {
					Toast.makeText(this, "读取手机号失败，请确认是否允许权限", Toast.LENGTH_LONG).show();
				}
			}

			super.onActivityResult(requestCode, resultCode, intent);
		} catch (Exception e) {

			e.printStackTrace();
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {

			if (llAddContacts.getVisibility() == View.VISIBLE && ll_Container.getVisibility() == View.VISIBLE) {
				Animation anim = AnimationUtils.loadAnimation(this, R.anim.anim_search_container_out);
				anim.setAnimationListener(new AnimationListener() {

					@Override
					public void onAnimationStart(Animation animation) {

					}

					@Override
					public void onAnimationRepeat(Animation animation) {

					}

					@Override
					public void onAnimationEnd(Animation animation) {
						llAddContacts.setVisibility(View.GONE);

					}
				});
				ll_Container.setAnimation(anim);
				ll_Container.setVisibility(View.GONE);
				activity_hote_order_ll.setEnabled(true);
				btnAddContact.setEnabled(true);
				return true;
			}
		}

		return super.onKeyDown(keyCode, event);
	}
}
