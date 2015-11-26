package com.gionee.gntravel;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.gionee.gntravel.entity.HotelDesc;
import com.gionee.gntravel.utils.FinalString;
import com.gionee.gntravel.utils.GNConnUtil;
import com.gionee.gntravel.utils.GNConnUtil.GNConnListener;
import com.gionee.gntravel.utils.JSONUtil;

public class HotelDescActivity extends BaseActivity implements GNConnListener, OnClickListener {
	private TextView activity_hoteldesc_tv_address;
	private TextView activity_hoteldesc_tv_phone;
	private TextView activity_hoteldesc_tv_desctext;
	private GNConnUtil post;
	private String FETCHHOTELDESCURL;
	private boolean inFetchingHotelDesc = false;
	private TextView tv_title;
	private String hotelCode;
	private String fetchhoteldesc;
	private ScrollView sv_hotel_desc;
	private RelativeLayout rl_failed;
	private ImageView iv_failed;
	private TextView btn_refresh;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_hotel_desc);
		FETCHHOTELDESCURL = getString(R.string.gionee_host) + FinalString.HOTEL_ACTION + "hotelDesc.action";
		post = new GNConnUtil();
		post.setGNConnListener(this);
		findViews();
		fetchData();
	}

	private void findViews() {
		hotelCode = getIntent().getStringExtra("hotelCode");
		activity_hoteldesc_tv_address = (TextView) findViewById(R.id.activity_hoteldesc_tv_address);
		sv_hotel_desc = (ScrollView) findViewById(R.id.sv_hotel_desc);
		rl_failed = (RelativeLayout) findViewById(R.id.rl_failed);
		iv_failed = (ImageView) findViewById(R.id.iv_failed);
		rl_failed.setOnClickListener(this);
		btn_refresh = (TextView) findViewById(R.id.btn_refresh);
		activity_hoteldesc_tv_phone = (TextView) findViewById(R.id.activity_hoteldesc_tv_phone);
		activity_hoteldesc_tv_desctext = (TextView) findViewById(R.id.activity_hoteldesc_tv_desctext);
		tv_title = (TextView) findViewById(R.id.tv_title);
		tv_title.setText(R.string.hotel_desc_title);
		tv_title.setOnClickListener(this);
	}

	private void fetchData() {
		// 如果正在请求就返回
		if (inFetchingHotelDesc) {
			return;
		}
		rl_failed.setVisibility(View.GONE);
		inFetchingHotelDesc = true;
		showProgress();
		new Handler().post(new Runnable() {
			@Override
			public void run() {
				fetchhoteldesc = FETCHHOTELDESCURL + "?hotelCode=" + hotelCode;
				post.doGet(fetchhoteldesc);
			}
		});
	}

	@Override
	public void onReqSuc(String requestUrl, String json) {
		if (requestUrl.equals(fetchhoteldesc)) {
			inFetchingHotelDesc = false;
			HotelDesc desc = JSONUtil.getInstance().fromJSON(HotelDesc.class, json);
			if (desc != null) {
				String address = desc.getAddress();
				String phone = desc.getPhone();
				String facility = desc.getService();
				String descrition = desc.getHotelDesc();
				if (!TextUtils.isEmpty(address)) {
					activity_hoteldesc_tv_address.setText(address);
				}
				if (!TextUtils.isEmpty(phone)) {
					activity_hoteldesc_tv_phone.setText(phone);
				}
				if (!TextUtils.isEmpty(facility)) {
					String facilitys[] = facility.split(",");
					List<List<String>> batchList = new ArrayList<List<String>>();
					List<String> tempList = new ArrayList<String>();
					int index = 0;
					for (int i = 0; i < facilitys.length; i++) {
						if (index % 2 == 0) {
							tempList = new ArrayList<String>();
							batchList.add(tempList);
						}
						tempList.add(facilitys[i]);
						index++;
					}
					LinearLayout ll = (LinearLayout) findViewById(R.id.activity_hotel_desc_ll_facility);
					for (List<String> list : batchList) {
						String one_facility = list.get(0);
						LinearLayout view = (LinearLayout) LayoutInflater.from(this).inflate(R.layout.activity_hotel_desc_item, null);
						TextView tv_one = (TextView) view.findViewById(R.id.activity_hoteldesc_tv_one);
						TextView tv_two = (TextView) view.findViewById(R.id.activity_hoteldesc_tv_two);
						tv_one.setText(one_facility);
						if (list.size() == 2) {
							String two_facility = list.get(1);
							tv_two.setText(two_facility);
						} else {
							tv_two.setVisibility(View.GONE);
						}
						ll.addView(view);
					}
				}
				if (!TextUtils.isEmpty(descrition)) {
					activity_hoteldesc_tv_desctext.setText(descrition);
				}
			}
		}
		dismissProgress();
	}

	@Override
	public void onReqFail(String requestUrl, Exception ex, int errorCode) {
		if (ex instanceof IOException) {
			showNetErrorMsg();
		}
		dismissProgress();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.tv_title:
			finish();
			break;
		case R.id.rl_failed:
			fetchData();
			break;
		default:
			break;
		}
	}

	public void showNetErrorMsg() {
		sv_hotel_desc.setVisibility(View.GONE);
		iv_failed.setImageResource(R.drawable.wifi);
		btn_refresh.setText(getResources().getString(R.string.net_refresh));
		rl_failed.setVisibility(View.VISIBLE);
		inFetchingHotelDesc = false;
	}
}
