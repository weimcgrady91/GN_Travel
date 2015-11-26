package com.gionee.gntravel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Activity;
import android.app.Dialog;
import android.content.ContentResolver;
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
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.gionee.gntravel.customview.CustomProgressDialog;
import com.gionee.gntravel.db.dao.ContactsDao;
import com.gionee.gntravel.db.dao.MaiingAddressDao;
import com.gionee.gntravel.db.dao.PassengerDao;
import com.gionee.gntravel.db.dao.impl.ContactsDaoImpl;
import com.gionee.gntravel.db.dao.impl.MailAddressDaoImpl;
import com.gionee.gntravel.db.dao.impl.PassengerDaoImpl;
import com.gionee.gntravel.entity.AddressEntity;
import com.gionee.gntravel.entity.ContactInfo;
import com.gionee.gntravel.entity.ContentBean;
import com.gionee.gntravel.entity.DeliverInfo;
import com.gionee.gntravel.entity.Flight;
import com.gionee.gntravel.entity.FlightInfoRequest;
import com.gionee.gntravel.entity.Passenger;
import com.gionee.gntravel.entity.Person;
import com.gionee.gntravel.provider.GNTravelProviderMetaData;
import com.gionee.gntravel.utils.DBUtils;
import com.gionee.gntravel.utils.DateUtils;
import com.gionee.gntravel.utils.FinalString;
import com.gionee.gntravel.utils.HttpConnCallback;
import com.gionee.gntravel.utils.HttpConnUtil4Gionee;
import com.gionee.gntravel.utils.JSONUtil;
import com.gionee.gntravel.utils.NetWorkUtil;
import com.gionee.gntravel.utils.Utils;
import com.youju.statistics.YouJuAgent;

public class FlightOrderActivity extends Activity implements OnClickListener,
		HttpConnCallback {
	private HashMap<String, String> params;
	private static final int INTO_ADDRESS_ACTIVITY = 1;// 跳转到adressActivity
	private static final String TAG = "FlightOrderActivity";
	private final static int ADD_PASSENGER_BACK = 20;
	private final static int EDIT_PASSENGER_BACK = 30;
	private final static int ADD_MAIL_ADDRESS_BACK = 0;
	private Button btnSubmit;
	private LinearLayout llAddDengjiPerson;
	private LinearLayout ll_Container;
	private Button btnAddDengjiPerson;
	private Button btnListAddDengjiPersonSure;
	private Button btnListAddDengjiPersonCancel;
	private EditText etInputName;
	private EditText etInputMobile;
	private ViewGroup llAddView;
	private TextView tvCount;
	private ListView listView;
	private List<Person> list = new ArrayList<Person>();// 登机人
	private List<Person> listContacts = new ArrayList<Person>();// 联系人
	private ScrollView scrollView;
	private String code = "";
	private String name = "";
	private String goneId = "";// 修改跳过去的id
	private TextView tv_AirlineCode;
	private TextView tv_dPortName;
	private TextView tv_aPortName;
	private TextView tv_takeOffDate;
	private TextView tv_takeOffTime;
	private TextView tv_arriveTime;
	private TextView tv_airportConstructionFee;
	private TextView tv_TuigaiqianContent;
	private TextView tv_adultOilFee;
	private TextView tv_price;
	private TextView tv_classType;
	private TextView tv_AddressMessage;// 消息地址
	private TextView tv_AddressNameMobile;// 消息地址
	private TextView tv_AddressMailCode;// 邮编
	private TextView tv_add_dengji_person;// 弹出框内的新增
	private Boolean flag = false; // 开为true false为关；
	private ImageView img_img_reimbursementSwitch;// 是否需要邮寄地址的开关按钮
	private Button btnSelectXContacts;// 选择联系人按钮
	private ImageView img_Tuigaiqian;// 退改签上下按钮
	private LinearLayout ll_addressSwitch;// 在开关下面的整个地址的布局
	private RelativeLayout llArrow;// 点击这个按钮填写收件信息
	private MyAdaper myAdaper;
	private ContactsAdaper contactsAdaper;
	private int index;
	private ArrayList<Boolean> isSelectorValue;// 弹出的布局里面的图片是否被选中
	private ArrayList<Boolean> isSelectorValueContacts;// 弹出的布局里面的图片是否被选中(联系人)
	private ArrayList<ContentBean> passengerLocationList;// 上次选中的数据
	private ContentBean contactsLocation;// 上次选中的数据( 联系人)
	private boolean isAddPassenger = false;// 如果是点击添加新增登机人按钮 返回来的时候
											// 标记list中第一个为选中状态状态
	/*
	 * 以下4个是//传回lai的值
	 */
	String addressname = "";
	String addressmobile = "";
	String address = "";
	String addressmailCode = "";
	private Flight flight;
	private Dialog dialogDel;
	private FlightInfoRequest requestInfo;
	private ContactInfo contactInfo;
	
	private ArrayList<Passenger> passengerList;
	private HttpConnUtil4Gionee task;
	private CustomProgressDialog progressDialog;
	private LinearLayout llAddDengjiren;
	private RelativeLayout re_TuigaiqianLayout;
	private TextView tvCountPrice;// 登机一个人的价格
	private TextView tvCountPreson;// 登记人数
	private TextView tv_Title;
	private int countPrice;
	private ResultHandler handler = new ResultHandler();
	private static final int PASSENGER_DATA_FINISH = 0x5006;// 加载完常用旅客信息之后
	private static final int CONTACTS_DATA_FINISH = 0x5008;// 加载完常用联系人信息之后
	private static final int EXCEPTION_DATA_FINISH = 0x5007;// 异常
	private TravelApplication app;
	private String selectPassengerUrl;
	private Boolean passengerFlag = false;// 访问常用旅客
	private Boolean contactsFlag = false;// 访问常用联系人
	private DeliverInfo deliverInfo;

	private class ResultHandler extends Handler {

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case FinalString.DATA_FINISH:
				Intent intent = new Intent();
				intent.putExtra("tempOrderID",msg.getData().getString("tempOrderID"));
				intent.setClass(FlightOrderActivity.this, H5PayOrder.class);
				startActivity(intent);
				finish();
				break;
			case PASSENGER_DATA_FINISH:
				loadDataFinish();
				stopProgressDialog();
				break;
			case CONTACTS_DATA_FINISH:
				loadDataContactsFinish();
				stopProgressDialog();
				break;
			case FinalString.NET_ERROR:
				showNetErrorMsg();
				break;
			case FinalString.NOT_FOUND:
				selecttNotMsg(msg);

				break;
			case EXCEPTION_DATA_FINISH:
				stopProgressDialog();
				break;
			}
		}

	}

	private void showNetErrorMsg() {
		Toast.makeText(this, R.string.network_timeout, Toast.LENGTH_LONG).show();
		stopProgressDialog();
	}

	private void selecttNotMsg(Message msg) {
		stopProgressDialog();
		if ("常用旅客列表为空!".equals((String) msg.obj)) {
			stopProgressDialog();
			Intent intent = new Intent(this, AddPassengerActivity.class);
			intent.putExtra("titleName", getString(R.string.adddengjiren));
			intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
			startActivityForResult(intent, 200);
		} else if ("没有查询到相应的结果!".equals((String) msg.obj)) {
			stopProgressDialog();
			tv_add_dengji_person.setText(getString(R.string.add_contacts_from));
			contactsAdaper = new ContactsAdaper();
			listView.setAdapter(contactsAdaper);
			contactsAdaper.notifyDataSetChanged();
			showSearch();
		} else {
			Toast.makeText(this, (String) msg.obj, Toast.LENGTH_SHORT).show();
		}

	}

	private void loadDataFinish() {
		if (isAddPassenger) {
			if (isSelectorValue == null) {
				isSelectorValue = new ArrayList<Boolean>();
			}
			isSelectorValue.remove(0);
			isSelectorValue.add(0, true);
			isAddPassenger = false;
		}
		tv_add_dengji_person.setText(getString(R.string.xinzengdengjiren));
		showSearch();
		myAdaper = new MyAdaper();
		listView.setAdapter(myAdaper);
		myAdaper.notifyDataSetChanged();
	}

	/**
	 * 加载完常用联系人信息之后
	 */
	private void loadDataContactsFinish() {
		Log.d("lijinbao", "loadDataContactsFinish");
		tv_add_dengji_person.setText(getString(R.string.add_contacts_from));
		showSearch();
		contactsAdaper = new ContactsAdaper();
		listView.setAdapter(contactsAdaper);
		contactsAdaper.notifyDataSetChanged();
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_flightorder);
		findViews();
		initData();
		showLocationList();
		findLocationContact();
		getPersonAndPriceCount();
	}

	@Override
	protected void onResume() {
		Utils.closeSoftware(this);
		super.onResume();
	}

	private void showLocationList() {
		findLocationPassengerList();
		llAddView.removeAllViews();
		for (int i = 0; i < passengerLocationList.size(); i++) {

			final View view = (View) this.getLayoutInflater().inflate(R.layout.add_view_dengji_person, null);
			ImageView btndel = (ImageView) view.findViewById(R.id.btn_add_view_del);
			TextView tvAddViewName = (TextView) view.findViewById(R.id.tv_dontai_add_name);
			TextView tvAddViewCode = (TextView) view.findViewById(R.id.tv_dontai_add_code);
			TextView tvAddViewId = (TextView) view.findViewById(R.id.tv_dontai_add_id);
			tvAddViewName.setText(passengerLocationList.get(i).getName());
			String kongGeCode = passengerLocationList.get(i).getCode();
			tvAddViewCode.setText(kongGeCode);
			tvAddViewId.setText(passengerLocationList.get(i).getId());
			RelativeLayout re = (RelativeLayout) view.findViewById(R.id.re_edit);
			re.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					ViewGroup vg = (ViewGroup) view.getParent();
					for (int i = 0; i < vg.getChildCount(); i++) {
						if (vg.getChildAt(i) == view) {
							index = i;// 记住修改的是哪个
							code = ((TextView) llAddView.getChildAt(i).findViewById(R.id.tv_dontai_add_code)).getText().toString();
							name = ((TextView) llAddView.getChildAt(i).findViewById(R.id.tv_dontai_add_name)).getText().toString();
							goneId = ((TextView) llAddView.getChildAt(i).findViewById(R.id.tv_dontai_add_id)).getText().toString();
							Intent intent = new Intent(	FlightOrderActivity.this,EditMyPassengerActivity.class);
							intent.putExtra("CODE", code);
							intent.putExtra("NAME", name);
							intent.putExtra("INDEX", goneId);
							intent.putExtra("titleName",getString(R.string.editdengjiren));
							intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
							startActivityForResult(intent, 100);

						}
					}
				}
			});
			btndel.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					delLianxiPrensonDialog(view);
				}
			});

			llAddView.addView(view);

		}
	}

	/**
	 * 查询本地数据库(上次状态联系人)
	 */
	private void findLocationContact() {
		// 查询 数据库 如果数据中有此数据，则至为true
		DBUtils dbUitls = new DBUtils(FlightOrderActivity.this);
		SQLiteDatabase db = dbUitls.openReadOnlyDatabase();
		ContactsDao dao = new ContactsDaoImpl();
		contactsLocation = dao.findContactsByUserId(db, app.getU(),"flightcontact");
		if (contactsLocation != null) {
			etInputMobile.setText(contactsLocation.getCode());
			etInputName.setText(contactsLocation.getName());
		}
	}

	/**
	 * 查询本地数据库(上次状态登机人)
	 */
	private void findLocationPassengerList() {
		// 查询 数据库 如果数据中有此数据，则至为true
		DBUtils dbUitls = new DBUtils(FlightOrderActivity.this);
		SQLiteDatabase db = dbUitls.openReadOnlyDatabase();
		PassengerDao dao = new PassengerDaoImpl();
		passengerLocationList = dao.findPassengersByUserId(db, app.getU());
	}

	/**
	 * 修改本地数据库信息
	 */
	private void updatePassengerList(String name, String code) {
		// 查询 数据库 如果数据中有此数据，则至为true
		DBUtils dbUitls = new DBUtils(FlightOrderActivity.this);
		SQLiteDatabase db = dbUitls.openReadWriteDatabase();
		PassengerDao dao = new PassengerDaoImpl();
		dao.updatePassengersByUserIdandId(db, app.getU(), goneId, name,code);
	}

	/**
	 * 删除本地数据库信息(删除一条)
	 */
	private void delectPassengerList(String id) {
		// 查询 数据库 如果数据中有此数据，则至为true
		DBUtils dbUitls = new DBUtils(FlightOrderActivity.this);
		SQLiteDatabase db = dbUitls.openReadWriteDatabase();
		PassengerDao dao = new PassengerDaoImpl();
		dao.delectPassengersById(db, id);
	}

	/**
	 * 插入联系人信息到本地服务器
	 */
	private void insertContact(String id) {//可以不用根据id，插入本地服务器2015-4-2
		DBUtils dbUitls = new DBUtils(FlightOrderActivity.this);
		SQLiteDatabase db = dbUitls.openReadWriteDatabase();
		ContactsDao dao = new ContactsDaoImpl();
		dao.delectContactsByType(db, app.getU(), "flightcontact");
		dao.insertContactsByUserId(db, app.getU(), etInputName.getText()
				.toString(), etInputMobile.getText().toString(), null,"flightcontact");//可以不用根据id=null，插入本地服务器2015-4-2,改为
	}

	private void findViews() {
		tv_add_dengji_person = (TextView) findViewById(R.id.tv_add_dengji_person);
		btnSelectXContacts = (Button) findViewById(R.id.btn_select_contacts);
		btnSelectXContacts.setOnClickListener(this);
		tv_Title = (TextView) findViewById(R.id.tv_title);
		tv_Title.setOnClickListener(this);
		scrollView = (ScrollView) findViewById(R.id.sv);
		tv_AirlineCode = (TextView) findViewById(R.id.tv_air_line_code);
		tv_takeOffDate = (TextView) findViewById(R.id.tv_takeOffDate);
		tv_dPortName = (TextView) findViewById(R.id.tv_dPortName);
		tv_takeOffTime = (TextView) findViewById(R.id.tv_takeOffTime);
		tv_aPortName = (TextView) findViewById(R.id.tv_aPortName);
		tv_arriveTime = (TextView) findViewById(R.id.tv_arriveTime);
		tv_classType = (TextView) findViewById(R.id.tv_classType);
		tv_price = (TextView) findViewById(R.id.tv_price);
		tv_airportConstructionFee = (TextView) findViewById(R.id.tv_airportConstructionFee);
		tv_adultOilFee = (TextView) findViewById(R.id.tv_adultOilFee);
		tv_TuigaiqianContent = (TextView) findViewById(R.id.tv_tuigaiqian_content);
		re_TuigaiqianLayout = (RelativeLayout) findViewById(R.id.re_tuigaiqian);
		img_Tuigaiqian = (ImageView) findViewById(R.id.img_up_down_arrow);
		btnAddDengjiPerson = (Button) findViewById(R.id.btn_addDengjiPerson);
		ll_Container = (LinearLayout) findViewById(R.id.ll_container);
		tv_AddressMailCode = (TextView) findViewById(R.id.tv_mail_code);
		ll_addressSwitch = (LinearLayout) findViewById(R.id.ll_addressSwitch);
		img_img_reimbursementSwitch = (ImageView) findViewById(R.id.img_reimbursementSwitch);
		img_img_reimbursementSwitch.setOnClickListener(this);
		tv_AddressMessage = (TextView) findViewById(R.id.tv_xiangxi_address);
		tv_AddressNameMobile = (TextView) findViewById(R.id.tv_address_name_mobile);
		llArrow = (RelativeLayout) findViewById(R.id.ll_address);
		llArrow.setOnClickListener(this);
		tvCountPreson = (TextView) findViewById(R.id.tv_zhifu_dengjirenshu);
		tvCountPrice = (TextView) findViewById(R.id.tv_count_price);
		llAddDengjiren = (LinearLayout) findViewById(R.id.ll_add_dengji_person);
		btnSubmit = (Button) findViewById(R.id.btn_submit);
		tvCount = (TextView) findViewById(R.id.tv_dengjirenshu);
		llAddView = (ViewGroup) findViewById(R.id.ll_add_view);
		etInputName = (EditText) findViewById(R.id.et_lianxi_person_input_name);
		listView = (ListView) findViewById(R.id.lv_contact_person);
		etInputMobile = (EditText) findViewById(R.id.et_lianxi_person_input_mobile);
		btnListAddDengjiPersonSure = (Button) findViewById(R.id.btn_add_dengji_person_sure);
		btnListAddDengjiPersonCancel = (Button) findViewById(R.id.btn_add_dengji_person_cancel);
		llAddDengjiPerson = (LinearLayout) findViewById(R.id.ll_xinzeng_passenger);
		btnAddDengjiPerson.setOnClickListener(this);
		btnListAddDengjiPersonCancel.setOnClickListener(this);
		btnListAddDengjiPersonSure.setOnClickListener(this);
		llAddDengjiren.setOnClickListener(this);
		llAddDengjiPerson.setOnClickListener(this);
		re_TuigaiqianLayout.setOnClickListener(this);
		btnSubmit.setOnClickListener(this);
		findViewById(R.id.ll_dengji_prenson).setOnClickListener(this);
		Utils.mobileAddSpace(13, etInputMobile);
	}

	private void initData() {
		app = (TravelApplication) getApplication();
		tv_Title.setText(getString(R.string.suborder));
		Intent intent = getIntent();
		flight = (Flight) intent.getSerializableExtra("Flight");
		tv_AirlineCode.setText(flight.getAirlineName() + flight.getFlight());
		tv_takeOffDate.setText(DateUtils.fomatStrDate(flight.getTakeOffTime()));
		tv_dPortName.setText(flight.getdPortName());
		tv_takeOffTime.setText(DateUtils.dateToTime(flight.getTakeOffTime()));
		tv_aPortName.setText(flight.getaPortName());
		tv_arriveTime.setText(DateUtils.dateToTime(flight.getArriveTime()));
		tv_classType.setText(flight.getClassType());
		tv_price.setText(flight.getPrice());
		tv_airportConstructionFee.setText(flight.getAdultTax());
		tv_adultOilFee.setText(flight.getAdultOilFee());
		String rernote = "更改说明: " + flight.getRernote() + "\n";
		String endnote = "转签说明: " + flight.getEndnote() + "\n";
		String refnote = "退票说明: " + flight.getRefnote() + "\n";
		tv_TuigaiqianContent.setText(refnote + rernote + endnote);
		countPrice = Integer.parseInt(flight.getPrice())
				+ Integer.parseInt(flight.getAdultTax())
				+ Integer.parseInt(flight.getAdultOilFee());
		deliverInfo = new DeliverInfo();
	}

	private void processData() {
		requestInfo = new FlightInfoRequest();
		requestInfo.setArriveCity(flight.getArriveCityCode());
		requestInfo.setDepartCity(flight.getDepartCityCode());
		requestInfo.setDepartDate(DateUtils.fomatStrDate(flight
				.getTakeOffTime()));
		requestInfo.setFlightCode(flight.getFlight());
		if (flight.getClassType().equals("经济舱")) {
			requestInfo.setClassType("Y");
		} else if (flight.getClassType().equals("头等舱")) {
			requestInfo.setClassType("F");
		} else if (flight.getClassType().equals("公务舱")) {
			requestInfo.setClassType("C");
		}
		requestInfo.setTakeOffTime(flight.getTakeOffTime());
		requestInfo.setAirlineCode(flight.getAirlineCode());
		requestInfo.setPrice(flight.getPrice());
		contactInfo = new ContactInfo();
		contactInfo.setContactName(etInputName.getText().toString());
		String KongGeMobile = etInputMobile.getText().toString();
		KongGeMobile = KongGeMobile.trim().replace(" ", "");
		contactInfo.setMobilePhone(KongGeMobile);
		passengerList = new ArrayList<Passenger>();
		for (int index = 0; index < passengerLocationList.size(); index++) {
			Passenger passenger = new Passenger();
			passenger.setPassengerName(passengerLocationList.get(index).getName());
			passenger.setPassportTypeID("1");
			passenger.setPassportNo(passengerLocationList.get(index).getCode().replace(" ", ""));
			String birthday = Utils.getBirthdayStr(passengerLocationList.get(index).getCode().replace(" ", ""));
			passenger.setBirthDay(birthday);
			passengerList.add(passenger);
		}
	}

	public String loadAirLine(String airNum, String showString) {
		String result = null;
		Uri uri = GNTravelProviderMetaData.FlightCompany.CONTENT_URI;
		String[] projection = new String[] { showString };
		String selection = GNTravelProviderMetaData.FlightCompany.AIRLINECODE
				+ " = ? ";
		String[] selectionArgs = new String[] { airNum };
		Cursor c = getContentResolver().query(uri, projection, selection,
				selectionArgs, null);
		if (c.moveToNext() != false) {
			result = c.getString(0);
		} else {
			result = "";
		}
		if (c != null) {
			c.close();
			c = null;
		}
		return result;
	}

	public String loadAirPort(String airportCode) {
		String result = null;
		Uri uri = GNTravelProviderMetaData.FlightCity.CONTENT_URI;
		String[] projection = new String[] { GNTravelProviderMetaData.FlightCity.AIRPORTNAME };
		String selection = GNTravelProviderMetaData.FlightCity.AIRPORTCODE
				+ " = ? ";
		String[] selectionArgs = new String[] { airportCode };
		Cursor c = getContentResolver().query(uri, projection, selection,
				selectionArgs, null);
		if (c.moveToNext() != false) {
			result = c.getString(0);
		} else {
			result = "";
		}
		if (c != null) {
			c.close();
			c = null;
		}
		return result;
	}

	/**
	 * 点击邮寄开关
	 */
	private void mailReimbursement() {
		if (flag) {
			flag = !flag;
			img_img_reimbursementSwitch.setBackgroundResource(R.drawable.slt_common_switch_off);
			ll_addressSwitch.setVisibility(View.GONE);
		} else {

			flag = !flag;
			img_img_reimbursementSwitch.setBackgroundResource(R.drawable.slt_common_switch_on);
			ll_addressSwitch.setVisibility(View.VISIBLE);
			scrollView.post(new Runnable() {
				public void run() {
					scrollView.fullScroll(ScrollView.FOCUS_DOWN);
				}
			});
			findMailAddress();
		
		}
	}
	private void findMailAddress(){
		try {
			DBUtils dbUitls = new DBUtils(this);
			SQLiteDatabase db = dbUitls.openReadOnlyDatabase();
			MaiingAddressDao dao = new MailAddressDaoImpl();
			AddressEntity addressEntity=dao.findMailAddressByUserId(db, app.getU());
			if(addressEntity!=null){
				ll_addressSwitch.setVisibility(View.VISIBLE);
				tv_AddressMailCode.setVisibility(View.VISIBLE);
				tv_AddressMessage.setVisibility(View.VISIBLE);
				
				addressmailCode=addressEntity.getMailCode();
				addressname=addressEntity.getName();
				address=addressEntity.getAddress();
				addressmobile=addressEntity.getMobile();
				tv_AddressMailCode.setText(addressmailCode);
				tv_AddressMessage.setText(address);
				tv_AddressNameMobile.setText(addressname + " " +addressmobile );
				deliverInfo.setPostCode(addressmailCode);
				deliverInfo.setReceiver(addressname);
				deliverInfo.setAddress(address);
				deliverInfo.setReceiverPhone(addressmobile);
			}
			db.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	/**
	 * 跳到邮寄填写界面
	 */
	private void intoAddressActivity() {
		Intent intentAddress = new Intent(this, AddressActivity.class);
		intentAddress.putExtra("intoAddressName", addressname);
		intentAddress.putExtra("intoAddressMobile", addressmobile);
		intentAddress.putExtra("intoAddressMailCode", addressmailCode);
		intentAddress.putExtra("intoAddress", address);
		intentAddress.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
		startActivityForResult(intentAddress, INTO_ADDRESS_ACTIVITY);
	}

	private void tuigaiqianSwitch() {

		if (tv_TuigaiqianContent.getVisibility() == View.GONE) {
			tv_TuigaiqianContent.setVisibility(View.VISIBLE);
			startArrowAnim(img_Tuigaiqian);
			img_Tuigaiqian.setImageDrawable(this.getResources().getDrawable(R.drawable.slt_newtrip_arrow_up_default));

		} else {
			tv_TuigaiqianContent.setVisibility(View.GONE);
			startArrowAnim(img_Tuigaiqian);
			img_Tuigaiqian.setImageDrawable(this.getResources().getDrawable(R.drawable.slt_newtrip_arrow_down_default));
		}
	}
	private void startArrowAnim(ImageView imageView) {
		Animation animation = (Animation) AnimationUtils.loadAnimation(this, R.anim.anim_arrow_rotate);
		imageView.setAnimation(animation);
		animation.start();
	}
	/**
	 * 点击添加登机人按钮
	 */
	private void addPassenger() {
		try {
			passengerFlag = true;
			contactsFlag = false;
			list.clear();
			initParams();
			loadingData();
			findLocationPassengerList();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 点击添加联系人按钮
	 */
	private void addContacts() {

		try {
			passengerFlag = false;
			contactsFlag = true;
			listContacts.clear();
			initParamsContacts();
			loadingDataContacts();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 弹出框内的新增联系人 或者是新增登机人
	 */
	private void addPassengerOrContacts() {
		if (passengerFlag) {
			Intent intentAdd = new Intent(this, AddPassengerActivity.class);
			intentAdd.putExtra("titleName", getString(R.string.adddengjiren));
			intentAdd.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
			startActivityForResult(intentAdd, 200);
		}
		if (contactsFlag) {
			Intent intent = new Intent(Intent.ACTION_PICK,ContactsContract.Contacts.CONTENT_URI);
			intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
			startActivityForResult(intent, 0);
		}
	}

	/**
	 * 弹出框的确定按钮
	 */
	private void onclickSure() {
		try {
			btnListAddDengjiPersonSure.setEnabled(false);
			if (passengerFlag) {
				passengerFlag = false;
				contactsFlag = false;
				// 存入数据库
				DBUtils dbUitls = new DBUtils(FlightOrderActivity.this);
				SQLiteDatabase db = dbUitls.openReadWriteDatabase();
				PassengerDao dao = new PassengerDaoImpl();
				dao.delectPassengersByUserId(db, app.getU());
				llAddView.removeAllViews();
				for (int i = 0; i < list.size(); i++) {
					if (isSelectorValue.get(i)) {// 选中哪个 哪个就是真了
						// 存入数据库中的上次选中的数据
						// 选中多个出错 isSelectorValue标志位是对的 显示不对。
						String idLocation = list.get(i).getId();
						String nameLocation = list.get(i).getName();
						String codeLocation = list.get(i).getCode();
						dao.insertPassengersByUserId(db, app.getU(),idLocation, nameLocation, codeLocation);
						final View view = (View) this.getLayoutInflater().inflate(R.layout.add_view_dengji_person, null);
						ImageView btndel = (ImageView) view.findViewById(R.id.btn_add_view_del);
						TextView tvAddViewName = (TextView) view.findViewById(R.id.tv_dontai_add_name);
						TextView tvAddViewCode = (TextView) view.findViewById(R.id.tv_dontai_add_code);
						TextView tvAddViewId = (TextView) view.findViewById(R.id.tv_dontai_add_id);
						RelativeLayout re = (RelativeLayout) view.findViewById(R.id.re_edit);
						tvAddViewName.setText(nameLocation);
						tvAddViewCode.setText(codeLocation);
						tvAddViewId.setText(idLocation);
						re.setOnClickListener(new OnClickListener() {
							@Override
							public void onClick(View v) {
								ViewGroup vg = (ViewGroup) view.getParent();
								for (int i = 0; i < vg.getChildCount(); i++) {
									if (vg.getChildAt(i) == view) {
										index = i;// 记住修改的是哪个
										code = ((TextView) llAddView.getChildAt(i).findViewById(R.id.tv_dontai_add_code)).getText().toString();
										name = ((TextView) llAddView.getChildAt(i).findViewById(R.id.tv_dontai_add_name)).getText().toString();
										goneId = ((TextView) llAddView.getChildAt(i).findViewById(R.id.tv_dontai_add_id)).getText().toString();
										Intent intent = new Intent(FlightOrderActivity.this,EditMyPassengerActivity.class);
										intent.putExtra("CODE", code);
										intent.putExtra("NAME", name);
										intent.putExtra("INDEX", goneId);
										intent.putExtra("titleName",getString(R.string.editdengjiren));
										intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
										startActivityForResult(intent, 100);
									}
								}
							}
						});
						btndel.setOnClickListener(new OnClickListener() {

							@Override
							public void onClick(View v) {
								delLianxiPrensonDialog(view);
							}
						});
						llAddView.addView(view);
					} 
				}
				findLocationPassengerList();
				getPersonAndPriceCount();
			}

			else {
				contactsFlag = false;
				// 存入数据库
				DBUtils dbUitls = new DBUtils(FlightOrderActivity.this);
				SQLiteDatabase db = dbUitls.openReadWriteDatabase();
				ContactsDao dao = new ContactsDaoImpl();
				dao.delectContactsByType(db, app.getU(), "flightcontact");
				for (int i = 0; i < listContacts.size(); i++) {
					if (isSelectorValueContacts.get(i)) {// 选中哪个 哪个就是真了
						// 存入数据库中的上次选中的数据
						// 选中多个出错 isSelectorValue标志位是对的 显示不对。
						String idLocation = listContacts.get(i).getId();
						String nameLocation = listContacts.get(i).getName();
						String codeLocation = listContacts.get(i).getCode();
						dao.insertContactsByUserId(db, app.getU(),nameLocation, codeLocation, idLocation,"flightcontact");
						etInputName.setText(nameLocation);
						etInputMobile.setText(codeLocation);
					} 
				}
				findLocationContact();
			}
			hideSearch();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.ll_xinzeng_passenger:
			hideSearch();
			break;
		case R.id.img_reimbursementSwitch:
			mailReimbursement();
			break;
		case R.id.ll_address:
			intoAddressActivity();
			break;
		case R.id.re_tuigaiqian:
			tuigaiqianSwitch();
			break;
		case R.id.tv_title:
			this.finish();
			break;
		case R.id.btn_submit:
			validate();// 验证数据是否正确 ，正确才能请求服务器
			break;
		case R.id.ll_dengji_prenson:// 添加登机人按钮
			addPassenger();
			break;
		case R.id.btn_addDengjiPerson:// 添加登机人按钮
			addPassenger();
			break;
		case R.id.btn_select_contacts:// 添加联系人按钮
			addContacts();
			break;
		case R.id.ll_add_dengji_person:// 弹出框内的新增登机人或者是新增联系人
			addPassengerOrContacts();
			break;
		case R.id.btn_add_dengji_person_cancel:
			hideSearch();
			passengerFlag = false;
			contactsFlag = false;
			break;
		case R.id.btn_add_dengji_person_sure:// 弹出框内的确定按钮
			onclickSure();
			break;
		}
	}

	private void initParams() {
		params = new HashMap<String, String>();
//		String userId = app.getUserId();
//		params.put("tel", userId);
		params.put("u", app.getU());
		params.put("userId", app.getUserId());
		selectPassengerUrl = getString(R.string.gionee_host)+ "/GioneeTrip/tripAction_findUserAllBaorderInfo.action";
	}

	/**
	 * 
	 */
	private void initParamsContacts() {
		params = new HashMap<String, String>();
//		String userId = app.getUserId();
//		params.put("tel", userId);
		params.put("u", app.getU());
		params.put("userId", app.getUserId());
		
		selectPassengerUrl = getString(R.string.gionee_host)+ "/GioneeTrip/tripAction_findUserAllContacterInfo.action";
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
		task.execute(selectPassengerUrl, params,HttpConnUtil4Gionee.HttpMethod.POST);
	}

	private void loadingDataContacts() {
		startProgressDialog();

		if (!NetWorkUtil.isNetworkConnected(this)) {
			Message msg = Message.obtain();
			msg.what = FinalString.NET_ERROR;
			handler.sendMessageDelayed(msg, 1000);
			return;
		}
		task = new HttpConnUtil4Gionee(this);
		task.execute(selectPassengerUrl, params,HttpConnUtil4Gionee.HttpMethod.POST);
	}

	/*
	 * 验证 填入的手机号等是否合法
	 */
	private void validate() {
		findLocationPassengerList();
		String inputName = etInputName.getText().toString().trim();
		String inputMobile = etInputMobile.getText().toString().trim();
		inputMobile = inputMobile.replaceAll(" ", "");
		if (passengerLocationList == null || passengerLocationList.size() == 0) {
			Toast.makeText(this, R.string.please_add_passenger,Toast.LENGTH_SHORT).show();
		} else if (Utils.limitName(this, inputName)) {
			if (Utils.limitMobile(this, inputMobile)) {
				if(vaildDeliverInfo()) {
					submitOrder();
				} else {
					Toast.makeText(this, "收件信息不完整", 0).show();
				}
				
			} 
		}

	}

	private boolean vaildDeliverInfo() {
		if(flag) {
			if(deliverInfo != null && deliverInfo.contentIsValid()) {
				return true;
			} else {
				return false;
			}
		} 
		return true;
	}
	
	/*
	 * 通过登记人数 计算出实际价格
	 */
	private void getPersonAndPriceCount() {

		if (passengerLocationList.size() == 0) {
			tvCount.setText("");
			tvCountPreson.setText("");
			tvCountPrice.setText("");
		} else {
			tvCount.setText(passengerLocationList.size() + "人");
			tvCountPreson.setText(passengerLocationList.size() + "人");
			tvCountPrice.setText(String.valueOf(countPrice*passengerLocationList.size()));
		}

	}

	private void submitOrder() {
		Utils.closeSoftware(this);
		processData();
		String url = getString(R.string.gionee_host)+ FinalString.SUBMIT_FLIGHT_ORDER;
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("flightInfo", JSONUtil.getInstance().toJSON(requestInfo));
		params.put("pasengerInfo", JSONUtil.getInstance().toJSON(passengerList));
		params.put("contactInfo", JSONUtil.getInstance().toJSON(contactInfo));
		
		
//		params.put("uidKey", app.getUserId());
		params.put("u", app.getU());
		params.put("userId", app.getUserId());
		
		//行程单
		if(flag) {
			deliverInfo.setDeliveryType("PJS");
			params.put("deliverInfo", JSONUtil.getInstance().toJSON(deliverInfo));
			Log.e("test", JSONUtil.getInstance().toJSON(deliverInfo));
		} 
		
		startProgressDialog();
		task = new HttpConnUtil4Gionee(this);
		task.execute(url, params, HttpConnUtil4Gionee.HttpMethod.POST);
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

	private void delLianxiPrenson(View view) {
		ViewGroup vg = (ViewGroup) view.getParent();
		for (int i = 0; i < vg.getChildCount(); i++) {
			if (vg.getChildAt(i) == view) {
				String removeId = ((TextView) llAddView.getChildAt(i).findViewById(R.id.tv_dontai_add_id)).getText().toString().trim();
				vg.removeView(view);
				delectPassengerList(removeId);
				findLocationPassengerList();
				getPersonAndPriceCount();
			}
		}
	}

	private void delLianxiPrensonDialog(final View view) {
		dialogDel = new Dialog(this, R.style.CustomProgressDialog);
		View viewdia = LayoutInflater.from(this).inflate(
				R.layout.delete_dialog, null);
		Button btnDel = (Button) viewdia.findViewById(R.id.btn_dialog_sure);
		Button btnDelCancel = (Button) viewdia.findViewById(R.id.btn_dialog_cancel);
		RelativeLayout reDeLayout = (RelativeLayout) viewdia.findViewById(R.id.re_dialog);
		ImageView imgCancel = (ImageView) viewdia.findViewById(R.id.img_dialog_title_cha);
		TextView tvTitle = (TextView) viewdia.findViewById(R.id.tv_dialog_title_name);
		tvTitle.setText(getString(R.string.delect));
		btnDel.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				delLianxiPrenson(view);
				dialogDel.dismiss();
			}
		});
		btnDelCancel.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				dialogDel.dismiss();
			}
		});
		reDeLayout.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				dialogDel.dismiss();
			}
		});
		imgCancel.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				dialogDel.dismiss();
			}
		});
		dialogDel.setContentView(viewdia);
		dialogDel.getWindow().setLayout(LayoutParams.MATCH_PARENT,LayoutParams.MATCH_PARENT);
		dialogDel.show();

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode,
			Intent intent) {
		try {
			switch (resultCode) {
			case ADD_PASSENGER_BACK:
				isAddPassenger = true;
				list.clear();
				findLocationPassengerList();
				initParams();
				loadingData();
				break;
			case EDIT_PASSENGER_BACK:
				String editName = intent.getExtras().getString("EditName");
				String editCode = intent.getExtras().getString("EditCode");
				updatePassengerList(editName, editCode);// 不带空格
				View view = llAddView.getChildAt(index);
				TextView tvAddViewName = (TextView) view.findViewById(R.id.tv_dontai_add_name);
				TextView tvAddViewCode = (TextView) view.findViewById(R.id.tv_dontai_add_code);
				tvAddViewName.setText(editName);
				tvAddViewCode.setText(editCode);
				break;
			case ADD_MAIL_ADDRESS_BACK:
				addressname = intent.getStringExtra("addressName");
				addressmobile = intent.getStringExtra("addressMobile");
				addressmailCode = intent.getStringExtra("addressMailCode");
				address = intent.getStringExtra("address");
				ll_addressSwitch.setVisibility(View.VISIBLE);
				tv_AddressMailCode.setVisibility(View.VISIBLE);
				tv_AddressMessage.setVisibility(View.VISIBLE);
				tv_AddressMailCode.setText(addressmailCode);
				tv_AddressMessage.setText(address);
				tv_AddressNameMobile.setText(addressname + " " + addressmobile);
				
				deliverInfo.setPostCode(addressmailCode);
				deliverInfo.setReceiver(addressname);
				deliverInfo.setAddress(address);
				deliverInfo.setReceiverPhone(addressmobile);
				
				
				
				break;
			case Activity.RESULT_OK:
				getContactInformation(intent);
				break;
			}
			super.onActivityResult(requestCode, resultCode, intent);
		} catch (Exception e) {

			e.printStackTrace();
		}
	}

	/**
	 * 点击通讯录中的某个人获取到姓名和手机号
	 * 
	 * @param intent
	 */
	private void getContactInformation(Intent intent) {
		ContentResolver reContentResolverol = getContentResolver();
		Uri contactData = intent.getData();
		Cursor cursor = managedQuery(contactData, null, null, null, null);
		cursor.moveToFirst();
		String username = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
		String contactId = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
		// 获得DATA表中的电话号码，条件为联系人ID,因为手机号码可能会有多个
		Cursor phone = reContentResolverol.query(
				ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
				ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
				new String[] { contactId }, null, null);
		String usernumber = null;
		if (phone != null) {
			etInputName.setText(username);
			phone.moveToFirst();
			usernumber = phone.getString(phone.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
			etInputMobile.setText(usernumber);
			hideSearch();
		} else {
			Toast.makeText(this, "读取手机号失败，请确认是否允许权限", Toast.LENGTH_LONG).show();
		}
	}

	public class DoResult implements Runnable {
		private String result;
		
		public DoResult(String result) {
			this.result = result;
		}

		@Override
		public void run() {
			Log.d("lijinbao", "result = "+result);
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
				if (passengerFlag) {// 加载常用旅客信息
					// 查询所有
					Log.d("lijinbao", " 加载常用旅客信息"+result);
					isSelectorValue = new ArrayList<Boolean>();
					JSONArray jsonArray = new JSONArray(content);
					for (int i = 0; i < jsonArray.length(); i++) {
						boolean isSelect = false; // 是否被选中
						JSONObject Item = jsonArray.getJSONObject(i);
						String id = Item.getString("id");
						String code = Item.getString("cardId");
						String name = Item.getString("name");
						Person person = new Person();
						person.setId(id);
						person.setCode(code);
						person.setName(name);
						if (passengerLocationList == null|| passengerLocationList.size() == 0) {
							isSelectorValue.add(i, false);
						} else {
							for (int j = 0; j < passengerLocationList.size(); j++) {
								if (passengerLocationList.get(j).getId()
										.equals(id)) {
									isSelect = true;
								}
							}
							if (isSelect) {
								isSelectorValue.add(i, true);
							} else {
								isSelectorValue.add(i, false);
							}
						}
						list.add(person);
					}
					Message msg = Message.obtain();
					msg.what = PASSENGER_DATA_FINISH;
					handler.sendMessage(msg);

				} else if (contactsFlag) {// 加载常用旅客信息
					Log.d("lijinbao", " 加载常用联系人信息"+result);
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
						if (contactsLocation == null) {isSelectorValueContacts.add(i, false);
						} else {//因为改为本地没有存id 。所以这只能比对手机号和姓名2015-04-02
							if (contactsLocation.getCode().equals(code)&&contactsLocation.getName().equals(name)) {
								isSelect = true;
							}
							if (isSelect) {
								isSelectorValueContacts.add(i, true);
							} else {
								isSelectorValueContacts.add(i, false);
							}
						}
						listContacts.add(person);
					}
					Message msg = Message.obtain();
					msg.what = CONTACTS_DATA_FINISH;
					handler.sendMessage(msg);

				}

				else {// 下定单的请求
					Log.d("lijinbao", " 下定单"+result);
					JSONObject contentObj = new JSONObject(content);
					String tempOrderID = contentObj.getString("tempOrderID");
					if(TextUtils.isEmpty(tempOrderID) || tempOrderID.equals("null")) {
						Toast.makeText(FlightOrderActivity.this, "服务器繁忙,稍候再试", 0).show();
						Message msg = Message.obtain();
						msg.what = EXCEPTION_DATA_FINISH;
						handler.sendMessage(msg);
					}
					String contactId = contentObj.getString("cid");
					insertContact(contactId);
					Message msg = Message.obtain();
					msg.what = FinalString.DATA_FINISH;
					Bundle b = new Bundle();
					b.putString("tempOrderID", tempOrderID);
					msg.setData(b);
					handler.sendMessage(msg);
				}
			}

			catch (Exception e) {
				e.printStackTrace();
				Message msg = Message.obtain();
				msg.what = EXCEPTION_DATA_FINISH;
				handler.sendMessage(msg);
			}

		}
	}

	private void showSearch() {
		Log.d("lijinbao", "showSearch");
		btnListAddDengjiPersonSure.setEnabled(true);
		if (llAddDengjiPerson.getVisibility() == View.GONE&& ll_Container.getVisibility() == View.GONE) {
			llAddDengjiPerson.setVisibility(View.VISIBLE);
			ll_Container.setAnimation(AnimationUtils.loadAnimation(this,R.anim.anim_search_container_in));
			ll_Container.setVisibility(View.VISIBLE);

		}
	}

	private boolean hideSearch() {
		passengerFlag = false;
		contactsFlag = false;
		if (llAddDengjiPerson.getVisibility() == View.VISIBLE&& ll_Container.getVisibility() == View.VISIBLE) {
			Animation anim = AnimationUtils.loadAnimation(this,	R.anim.anim_search_container_out);
			anim.setAnimationListener(new AnimationListener() {
				@Override
				public void onAnimationStart(Animation animation) {
				}
				@Override
				public void onAnimationRepeat(Animation animation) {
				}
				@Override
				public void onAnimationEnd(Animation animation) {
					llAddDengjiPerson.setVisibility(View.GONE);
				}
			});
			ll_Container.setAnimation(anim);
			ll_Container.setVisibility(View.GONE);
			return true;
		}
		return false;
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if (llAddDengjiPerson.getVisibility() == View.VISIBLE
					&& ll_Container.getVisibility() == View.VISIBLE) {
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
						llAddDengjiPerson.setVisibility(View.GONE);

					}
				});
				ll_Container.setAnimation(anim);
				ll_Container.setVisibility(View.GONE);
				return true;
			}
		}

		return super.onKeyDown(keyCode, event);
	}

	private class MyAdaper extends BaseAdapter {

		@Override
		public int getCount() {
			return list.size();
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
			ViewHolder holder = null;

			if (convertView == null) {// convertView 存放item的
				holder = new ViewHolder();
				convertView = getLayoutInflater().inflate(R.layout.contacts_item, null);
				holder.name = (TextView) convertView.findViewById(R.id.tv_item_name);
				holder.shenfenzheng = (TextView) convertView.findViewById(R.id.tv_item_shenfenzhengjian);
				holder.code = (TextView) convertView.findViewById(R.id.tv_item_shenfenzhenghao);
				holder.value = (ImageView) convertView.findViewById(R.id.listview_checkbox);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			holder.name.setText(list.get(position).getName());
			holder.code.setText(list.get(position).getCode());
			if (isSelectorValue.get(position) == false) {
				holder.value.setImageResource(R.drawable.multiple_no_choice);
			} else {
				holder.value.setImageResource(R.drawable.multiple_choice);
			}

			convertView.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					if (isSelectorValue.get(position) == false) {
						isSelectorValue.remove(position);
						isSelectorValue.add(position, true);

					} else {
						isSelectorValue.remove(position);
						isSelectorValue.add(position, false);
					}
					notifyDataSetChanged();
				}
			});
			return convertView;
		}
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
		public View getView(final int position, View convertView,
				ViewGroup parent) {
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
							if (isSelectorValueContacts.get(j) == true&& j != position) {
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

	private void startProgressDialog() {
		if (progressDialog == null) {
			progressDialog = CustomProgressDialog.createDialog(this,R.layout.customprogressdialog);
			progressDialog.setCanceledOnTouchOutside(true);
			progressDialog.setCancelable(false);
		}
		progressDialog.show();
	}

	private void stopProgressDialog() {
		if (progressDialog != null) {
			progressDialog.dismiss();
			progressDialog = null;
		}
	}
}