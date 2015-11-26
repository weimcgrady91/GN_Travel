package com.gionee.gntravel;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ctrip.openapi.java.utils.ImageUtil;
import com.gionee.gntravel.entity.HotelImage;
import com.gionee.gntravel.utils.JSONUtil;
import com.google.gson.reflect.TypeToken;

public class HotelBigImageActivity extends BaseActivity implements OnClickListener{
	private int currentIndex;
	private List<HotelImage> images;
	private ViewPager vp;
	private TextView tv;
	private RelativeLayout iv_prepage;
	private RelativeLayout iv_nextpage;
	private TextView tv_img_desc;
	private TextView tv_title;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_hotelbigimage);
		getData();
		findViews();
	}

	private void findViews() {
		iv_prepage = (RelativeLayout) findViewById(R.id.activity_hotelbigimg_iv_pre);
		iv_nextpage = (RelativeLayout) findViewById(R.id.activity_hotelbigimg_iv_next);
		tv_img_desc = (TextView) findViewById(R.id.activity_hotel_bigimg_tv_desc);
		if (images != null && images.size() != 0) {
			vp = (ViewPager) findViewById(R.id.activity_hotelbigImage_vp);
			tv = (TextView) findViewById(R.id.activity_hotelbigImage_tv_index);
			vp.setAdapter(new BigImageAdapter());
			vp.setOnPageChangeListener(new BigImagePageChangeListener());
			int tempPosition = currentIndex;
			vp.setCurrentItem(currentIndex+1);
			vp.setCurrentItem(tempPosition);
		}
		tv_title = (TextView) findViewById(R.id.tv_title);
		tv_title.setText(R.string.hotel_desc_bigimg);
		tv_title.setOnClickListener(this);
		iv_prepage.setOnClickListener(this);
		iv_nextpage.setOnClickListener(this);
	}

	private void getData() {
		currentIndex = getIntent().getIntExtra("index", 0);
		String json = getIntent().getStringExtra("json");
		if (json == null) {
			Toast.makeText(this, "无法展示001", Toast.LENGTH_LONG).show();
			finish();
			return;
		}
		images = JSONUtil.getInstance().fromJSON(new TypeToken<ArrayList<HotelImage>>() {
		}.getType(), json);
		if (images == null || images.size() == 0) {
			Toast.makeText(this, "无法展示002", Toast.LENGTH_LONG).show();
			finish();
			return;
		}
	}

	private class BigImagePageChangeListener implements OnPageChangeListener {

		@Override
		public void onPageScrollStateChanged(int arg0) {

		}

		@Override
		public void onPageScrolled(int arg0, float arg1, int arg2) {

		}

		@Override
		public void onPageSelected(int position) {
			if (images!=null&& images.size()>0) {
				tv.setText((position+1)+"/"+images.size());
				String desc = images.get(position).getDescription();
				if (desc.indexOf(".jpg")>0) {
					desc = desc.substring(0, desc.indexOf(".jpg"));
				}
				tv_img_desc.setText(desc);
			}
			currentIndex = position;
		}
	}

	private class BigImageAdapter extends PagerAdapter {

		@Override
		public int getCount() {
			return images.size();
		}

		@Override
		public boolean isViewFromObject(View arg0, Object arg1) {
			return arg0 == arg1;
		}

		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {
			View child = (View) object;
			container.removeView(child);
		}

		@Override
		public Object instantiateItem(ViewGroup container, int position) {
			ImageView iv = new ImageView(HotelBigImageActivity.this);

			ViewGroup.LayoutParams lp = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
					ViewGroup.LayoutParams.MATCH_PARENT);
			iv.setScaleType(ScaleType.FIT_XY);
			iv.setLayoutParams(lp);
			iv.setImageDrawable(getResources().getDrawable(R.drawable.defaltimg307144));
			if (((TravelApplication)getApplication()).isShowImgWithWifiState()) {
				ConnectivityManager connManager = (ConnectivityManager) getApplication().getSystemService(Context.CONNECTIVITY_SERVICE);
				NetworkInfo networkInfo = connManager.getActiveNetworkInfo();
				if (networkInfo.isConnected()) {
					if (networkInfo.getType() == ConnectivityManager.TYPE_WIFI) {
						ImageUtil.getInstance(HotelBigImageActivity.this).display(images.get(position).getUrl(), iv, null);
					} 
				}
			} else {
				ImageUtil.getInstance(HotelBigImageActivity.this).display(images.get(position).getUrl(), iv, null);
			}


			container.addView(iv);
			return iv;
		}

	}

	@Override
	public void onClick(View v) {

		switch (v.getId()) {
		case R.id.tv_title:
			finish();
			break;
		case R.id.activity_hotelbigimg_iv_pre:
			currentIndex--;
			vp.setCurrentItem(currentIndex);
			break;
		case R.id.activity_hotelbigimg_iv_next:
			currentIndex++;
			vp.setCurrentItem(currentIndex);
			break;

		default:
			break;
		}
	}
}
