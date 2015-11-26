package com.gionee.gntravel;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.gionee.gntravel.adapter.ChooseConditionAdapter;
import com.gionee.gntravel.adapter.ChooseConditionAdapter.onSelectListner;
import com.gionee.gntravel.entity.Bank;
import com.gionee.gntravel.entity.CancelPenaltyAvail;
import com.gionee.gntravel.entity.DepositBean;
import com.gionee.gntravel.entity.HotelOrderBean;
import com.gionee.gntravel.entity.HotelReservation;
import com.gionee.gntravel.utils.FinalString;
import com.gionee.gntravel.utils.IdCardUtil;
import com.gionee.gntravel.utils.JSONUtil;
import com.gionee.gntravel.utils.SpUtil;
import com.gionee.gntravel.utils.ValidateUtil;
import com.google.gson.reflect.TypeToken;
import com.youju.statistics.YouJuAgent;

public class HotelDepositActivity extends BaseActivity implements OnClickListener, onSelectListner {
	private TextView tv_title;
	private RelativeLayout deposit_ll_chosebanck;
	private View popupView;
	private int mPosition = 0;
	private PopupWindow mPopupWindow;
	private View view;
	private Animation out_anim;
	private EditText deposit_et_chosebanck;
	private List<Bank> bankList = new ArrayList<Bank>();
	private TextView order_tv_depositpice;
	private Button deposit_bt_submitorder;
	private EditText deposit_et_cardnum;
	private EditText deposit_et_name;
	private EditText deposit_et_expiredate;
	private EditText deposit_et_seriescode;
	private EditText deposit_et_idcard;
	private boolean increatingorder = false;
	private TextView deposit_hotelname;
	private EditText deposit_et_phonenum;
	private static final String KEY_BANK_DATA = "key_bank_data";
	private String CREATEHOTELORDERURL;
	private String FETCH_BANK_URL;
	private ImageView img_loading;
	private ScrollView sv_hotel_deposit;
	private RelativeLayout rl_loading;
	private LinearLayout bottom;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		view = getLayoutInflater().inflate(R.layout.activity_hotel_deposit, null);
		setContentView(view);
		initData();
		findViews();
	}

	private void initData() {
		CREATEHOTELORDERURL =getString(R.string.gionee_host)+FinalString.HOTEL_ACTION + "createHotelOrder.action";
		FETCH_BANK_URL =getString(R.string.gionee_host)+FinalString.HOTEL_ACTION + "bankList.action";
		bundle = getIntent().getExtras();
		orderBean = (HotelOrderBean) bundle.getSerializable("orderBean");
		avail = (CancelPenaltyAvail) bundle.getSerializable("cancelPanelty");
		String bankLocal = new SpUtil(this).getString(KEY_BANK_DATA);
		if (TextUtils.isEmpty(bankLocal)) {
			fetchBankData();
		}
	}

	private void findViews() {
		img_loading = (ImageView) findViewById(R.id.img_loading);
		sv_hotel_deposit = (ScrollView) findViewById(R.id.sv_hotel_deposit);
		rl_loading = (RelativeLayout)findViewById(R.id.rl_loading);
		bottom = (LinearLayout)findViewById(R.id.bottom);
		img_loading = (ImageView) findViewById(R.id.img_loading);
		deposit_ll_chosebanck = (RelativeLayout) findViewById(R.id.activity_hotel_deposit_ll_chosebanck);
		order_tv_depositpice = (TextView) findViewById(R.id.order_tv_depositpice);
		deposit_bt_submitorder = (Button) findViewById(R.id.activity_hotel_deposit_bt_submitorder);
		deposit_hotelname = (TextView) findViewById(R.id.activity_hotel_deposit_hotelname);
		deposit_hotelname.setText(bundle.getString("hotelName"));
		deposit_et_chosebanck = (EditText) findViewById(R.id.activity_hotel_deposit_tv_chosebanck);
		deposit_et_cardnum = (EditText) findViewById(R.id.activity_hotel_deposit_et_cardnum);
		deposit_et_name = (EditText) findViewById(R.id.activity_hotel_deposit_et_name);
		deposit_et_idcard = (EditText) findViewById(R.id.activity_hotel_deposit_et_idcard);
		deposit_et_phonenum = (EditText) findViewById(R.id.activity_hotel_deposit_et_phonenum);
		deposit_et_expiredate = (EditText) findViewById(R.id.activity_hotel_deposit_et_expiredate);
		deposit_et_seriescode = (EditText) findViewById(R.id.activity_hotel_deposit_et_seriescode);
		order_tv_depositpice.setText(getIntent().getStringExtra("price"));
		tv_title = (TextView) findViewById(R.id.tv_title);
		tv_title.setText("提交订单");
		tv_title.setOnClickListener(this);
		deposit_ll_chosebanck.setOnClickListener(this);
		deposit_et_chosebanck.setOnClickListener(this);
		deposit_bt_submitorder.setOnClickListener(this);
		popupView = this.getLayoutInflater().inflate(R.layout.window_price, null);
	}

	@Override
	public void onReqSuc(String requestUrl, String json) {
		stopProgressDialog();
		if (requestUrl.equals(FETCH_BANK_URL)) {
			inFetchingBankData = false;
			// 持久化json
			SpUtil sp = new SpUtil(this);
			sp.putString(KEY_BANK_DATA, json);
		}
		if (requestUrl.equals(CREATEHOTELORDERURL)) {
			increatingorder = false;
			HotelReservation hotelReservation = JSONUtil.getInstance().fromJSON(HotelReservation.class, json);
			if ("S".equals(hotelReservation.getResStatus())) {
				printLog("order", hotelReservation.getResGlobalInfo().getResID_Value());
				Intent i = new Intent();
				bundle.putSerializable("hotelReservation", hotelReservation);
				bundle.putSerializable("orderBean", orderBean);
				i.putExtras(bundle);
				i.setClass(this, HotelOrderSucActivity.class);
				startActivity(i);
				YouJuAgent.onEvent(this, getString(R.string.youju_hotel_pay_succ));
			}
		}
	}

	@Override
	public void onReqFail(String requestUrl, Exception ex, int errorCode) {
		stopProgressDialog();
		if (!TextUtils.isEmpty(ex.getMessage())) {
			Toast.makeText(this, ex.getMessage(), Toast.LENGTH_LONG).show();
			increatingorder = false;
		}else{
			Toast.makeText(this, "链接超时，请重试", Toast.LENGTH_LONG).show();
			increatingorder = false;
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.activity_hotel_deposit_ll_chosebanck:
			showBankList();
			break;
		case R.id.tv_title:
			finish();
			break;
		case R.id.activity_hotel_deposit_bt_submitorder:
			doDeposit();
			break;
		}
	}
	private void doDeposit() {
		if (isLocalInfoValidate()) {
			gotoDeposit();
		}
	}

	private void gotoDeposit() {
		depositBean = new DepositBean();
		depositBean.setAmount(orderBean.getAmountBeforeTax()+"");
//		depositBean.setCardHolderIDCard(cardnum);
		depositBean.setCardNumber(cardnum);
		try {
			depositBean.setCardHolderName(URLEncoder.encode(name, "UTF-8"));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
//		depositBean.setCardNumber(idcard);
		depositBean.setCardHolderIDCard(idcard);

		for (Bank bank : bankList) {
			if (banckName.equals(bank.getBankName())) {
				banckName = bank.getBankName();
			}
		}
		try {
			depositBean.setBankName(URLEncoder.encode(banckName, "UTF-8"));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		Calendar c = Calendar.getInstance();
		int month = c.get(Calendar.MONTH) + 1;
		int year = c.get(Calendar.YEAR);
		String yearStr = year + "";
		yearStr = yearStr.substring(2, 4);
		String monthStr = month + "";
		if (month < 10) {
			monthStr = 0 + monthStr;
		}
		depositBean.setEffectiveDate(monthStr + yearStr);
		depositBean.setExpireDate(expiredate);
		depositBean.setSeriesCode(seriescode);
		depositBean.setPhoneNum(phonenum);

		penaltyAvail = new CancelPenaltyAvail();
		penaltyAvail.setAmount(orderBean.getAmountBeforeTax());
		penaltyAvail.setStart(avail.getStart());
		penaltyAvail.setEnd(avail.getEnd());
		String orderBeanJson = JSONUtil.getInstance().toJSON(orderBean);
		String depositBeanJson = JSONUtil.getInstance().toJSON(depositBean);
		String penaltyBeanJson = JSONUtil.getInstance().toJSON(penaltyAvail);
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("orderBean", orderBeanJson);
		params.put("depositBean", depositBeanJson);
		params.put("penaltyAvail", penaltyBeanJson);
		TravelApplication app = (TravelApplication) getApplication();
		String tripName = app.getTripName();
		try {
			tripName = URLEncoder.encode(tripName, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		params.put("tripName", tripName);
		if (!increatingorder) {
			startProgressDialog();
			post.doPost(CREATEHOTELORDERURL, params);
			increatingorder = true;
		}
	}
	private void startProgressDialog() {
		sv_hotel_deposit.setEnabled(false);
		sv_hotel_deposit.setVisibility(View.VISIBLE);
		rl_loading.setVisibility(View.VISIBLE);
		bottom.setClickable(false);
		AnimationDrawable animationDrawable = (AnimationDrawable) img_loading.getBackground();
		animationDrawable.start();
	}

	private void stopProgressDialog() {
		bottom.setClickable(true);
		if (sv_hotel_deposit != null) {
			sv_hotel_deposit.setEnabled(true);
		}
		if (img_loading != null) {
			AnimationDrawable animationDrawable = (AnimationDrawable) img_loading.getBackground();
			animationDrawable.stop();
		}
		if (rl_loading != null) {
			rl_loading.setVisibility(View.GONE);
		}
	}

	// 判断填写格式是否有误
	private boolean isLocalInfoValidate() {
		banckName = deposit_et_chosebanck.getText().toString();
		cardnum = deposit_et_cardnum.getText().toString();
		name = deposit_et_name.getText().toString();
		idcard = deposit_et_idcard.getText().toString();
		phonenum = deposit_et_phonenum.getText().toString();
		expiredate = deposit_et_expiredate.getText().toString();
		seriescode = deposit_et_seriescode.getText().toString();
		if (TextUtils.isEmpty(banckName) || banckName.equals("请选择发卡银行")) {
			Toast.makeText(this, "请选择发卡银行", Toast.LENGTH_LONG).show();
			return false;
		}
		if (TextUtils.isEmpty(cardnum) || cardnum.equals("信用卡号") || !ValidateUtil.isValidCard(cardnum)) {
			Toast.makeText(this, "请输入正确的信用卡号", Toast.LENGTH_LONG).show();
			return false;
		}
		if (TextUtils.isEmpty(name) || name.equals("姓名")) {
			Toast.makeText(this, "请输入正确的姓名", Toast.LENGTH_LONG).show();
			return false;
		}
		if (TextUtils.isEmpty(idcard) || idcard.equals("身份证号码") || !IdCardUtil.IDCardValidate(idcard)) {
			Toast.makeText(this, "请输入正确的身份证号码", Toast.LENGTH_LONG).show();
			return false;
		}
		if (TextUtils.isEmpty(phonenum) || phonenum.equals("手机号码") || !ValidateUtil.isValidPhone(phonenum)) {
			Toast.makeText(this, "请输入正确的手机号码", Toast.LENGTH_LONG).show();
			return false;
		}
		if (TextUtils.isEmpty(expiredate) || expiredate.equals("月份年份 比如：0814")) {
			Toast.makeText(this, "请输入正确的失效日期", Toast.LENGTH_LONG).show();
			return false;
		}
		if (TextUtils.isEmpty(seriescode) || seriescode.equals("信用卡验证码")) {
			Toast.makeText(this, "请输入正确的信用卡验证码", Toast.LENGTH_LONG).show();
			return false;
		}
		return true;
	}

	private void showBankList() {
		String bankLocal = new SpUtil(this).getString(KEY_BANK_DATA);
		if (TextUtils.isEmpty(bankLocal)) {
			fetchBankData();
		} else {
			updateBanckPopWindow(bankLocal);
		}
	}

	private void updateBanckPopWindow(String bankLocal) {
		bankList = JSONUtil.getInstance().fromJSON(new TypeToken<ArrayList<Bank>>() {
		}.getType(), bankLocal);
		Bank b = new Bank();
		b.setBankName("请选择发卡银行");
		bankList.add(0, b);
		
		ListView window_price_lv = (ListView) popupView.findViewById(R.id.window_price_lv);
		ChooseConditionAdapter adapter = new ChooseConditionAdapter(this, null, bankList, "bank", mPosition);
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
				AnimationUtils.loadAnimation(this, R.anim.menu_bottombar_in));
	}

	private boolean inFetchingBankData = false;
	private Bundle bundle;
	private HotelOrderBean orderBean;
	private CancelPenaltyAvail avail;
	private CancelPenaltyAvail penaltyAvail;//取消制度
	private DepositBean depositBean;//担保制度
	private String banckName;
	private String cardnum;
	private String name;
	private String idcard;
	private String phonenum;
	private String expiredate;
	private String seriescode;

	private void fetchBankData() {
		if (inFetchingBankData) {
			return;
		}
		inFetchingBankData = true;
		post.doGet(FETCH_BANK_URL);
	}

	private void popwindowDismiss() {
		out_anim = AnimationUtils.loadAnimation(this, R.anim.menu_bottombar_out);
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
	}

	@Override
	public void selectPrice(int position) {

	}

	@Override
	public void selectDefalt(int position) {

	}

	@Override
	public void selectBank(int position) {
		mPosition = position;
		deposit_et_chosebanck.setText(bankList.get(position).getBankName());
		popwindowDismiss();
	}
}
