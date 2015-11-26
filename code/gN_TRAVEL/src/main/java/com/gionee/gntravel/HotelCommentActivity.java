package com.gionee.gntravel;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.gionee.gntravel.adapter.HotelCommentAdapter;
import com.gionee.gntravel.entity.DomesticHotelComment;
import com.gionee.gntravel.utils.FinalString;
import com.gionee.gntravel.utils.JSONUtil;
import com.google.gson.reflect.TypeToken;

public class HotelCommentActivity extends BaseActivity implements OnClickListener {

	private String FETCHHOTELCOMMURL;
	private String hotelId;
	private String fetchhotelcommurl;
	private ListView activity_hotelcomment_lv;
	private TextView tv_title;
	private TextView no_data;
	private RelativeLayout rl_failed;
	private ImageView iv_failed;
	private TextView btn_refresh;
	private boolean inFetchingHotelComment = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		FETCHHOTELCOMMURL =getString(R.string.gionee_host)+FinalString.HOTEL_ACTION+"hotelCommList.action";
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_hotel_comment);
		findViews();
		fetchData();
	}

	/**
	 * 请求评论数据
	 * @Author: yangxy
	 * @Version: V1.0
	 * @Create Date: 2014-11-10
	 */
	private void fetchData() {
		if (inFetchingHotelComment) {
			return;
		}
		inFetchingHotelComment = true;
		showProgress();
		rl_failed.setVisibility(View.GONE);
		new Handler().post(new Runnable() {
			
			@Override
			public void run() {
				fetchhotelcommurl = FETCHHOTELCOMMURL + "?hotelId=" + hotelId;
				post.doGet(fetchhotelcommurl);
			}
		});
	}
	private void findViews() {
		hotelId = getIntent().getStringExtra("hotelId");
		activity_hotelcomment_lv = (ListView) findViewById(R.id.activity_hotelcomment_lv);
		no_data = (TextView) findViewById(R.id.no_data);
		tv_title = (TextView) findViewById(R.id.tv_title);
		tv_title.setText(R.string.hotel_comment_title);
		tv_title.setOnClickListener(this);
		rl_failed = (RelativeLayout) findViewById(R.id.rl_failed);
		iv_failed = (ImageView) findViewById(R.id.iv_failed);
		rl_failed.setOnClickListener(this);
		btn_refresh = (TextView) findViewById(R.id.btn_refresh);
	}

	@Override
	public void onReqSuc(String requestUrl, String json) {
		inFetchingHotelComment = false;
		activity_hotelcomment_lv.setVisibility(View.VISIBLE);
		no_data.setVisibility(View.GONE);
		rl_failed.setVisibility(View.GONE);
		if (!TextUtils.isEmpty(json)) {
			List<DomesticHotelComment> commentList = JSONUtil.getInstance().fromJSON(
					new TypeToken<ArrayList<DomesticHotelComment>>() {
					}.getType(), json);
			activity_hotelcomment_lv.setAdapter(new HotelCommentAdapter(this, commentList));
		}
		dismissProgress();
	}

	@Override
	public void onReqFail(String requestUrl, Exception ex, int errorCode) {
		inFetchingHotelComment = false;
		activity_hotelcomment_lv.setVisibility(View.GONE);
		if (ex instanceof IOException) {
			showNetErrorMsg();
		}else{
			no_data.setVisibility(View.VISIBLE);
			rl_failed.setVisibility(View.GONE);
		}
		dismissProgress();
	}
	public void showNetErrorMsg() {
		activity_hotelcomment_lv.setVisibility(View.GONE);
		no_data.setVisibility(View.GONE);
		iv_failed.setImageResource(R.drawable.wifi);
		btn_refresh.setText(getResources().getString(R.string.net_refresh));
		rl_failed.setVisibility(View.VISIBLE);
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

}
