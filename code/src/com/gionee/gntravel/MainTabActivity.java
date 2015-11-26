package com.gionee.gntravel;

import java.util.ArrayList;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.gionee.gntravel.customview.CustomViewPager;
import com.gionee.gntravel.fragment.FoundFragment;
import com.gionee.gntravel.fragment.InformationFragment;
import com.gionee.gntravel.fragment.ListBaseFragment;
import com.gionee.gntravel.fragment.TravelFragment;
import com.youju.statistics.YouJuAgent;

public class MainTabActivity extends FragmentActivity implements
		View.OnClickListener {
	private boolean isExit = false;
	private ArrayList<ListBaseFragment> fragmentsList = new ArrayList<ListBaseFragment>();
	private CustomViewPager viewPager;
	private Button tv_found;
	private Button tv_information;
	private Button tv_travel;
//	private int currIndex;
//	private int bmpW;
//	private int offset;
	boolean flag;
	private TravelApplication app;
	private LinearLayout llPrompt;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_maintab);
//		initImageView();
		findViews();
		initViews();
	}

	@Override
	protected void onRestart() {
		super.onRestart();
	}
	
	@Override
	protected void onStart() {
		super.onStart();
	}

	private void findViews() {
		app = (TravelApplication) getApplication();
		tv_found = (Button) findViewById(R.id.tv_found);
		tv_information = (Button) findViewById(R.id.tv_infomation);
		tv_travel = (Button) findViewById(R.id.tv_travel);
		tv_travel.requestFocus();
		viewPager = (CustomViewPager) findViewById(R.id.viewpager);
		llPrompt=(LinearLayout) findViewById(R.id.ll_prompt);
		llPrompt.setOnClickListener(this);
		flag=app.getUserInfo_sp().getBoolean("prompt");
		if(!flag){
			llPrompt.setVisibility(View.VISIBLE);
		}
	}
	
	public void initViews() {
		FoundFragment foundFrg = new FoundFragment();
		TravelFragment travelFrg = new TravelFragment();
		InformationFragment informationFrg = new InformationFragment();
		fragmentsList.add(foundFrg);
		fragmentsList.add(travelFrg);
		fragmentsList.add(informationFrg);
		tv_found.setOnClickListener(this);
		tv_information.setOnClickListener(this);
		tv_travel.setOnClickListener(this);
		viewPager.setAdapter(new MyFragmentPagerAdapter(
				getSupportFragmentManager(), fragmentsList));
		viewPager.setOnPageChangeListener(new OnPageChangeListener() {
//			int one = offset * 2 + bmpW;

			@Override
			public void onPageSelected(int arg0) {
//				Animation animation = new TranslateAnimation(one * currIndex,
//						one * arg0, 0, 0);
//				currIndex = arg0;
//				animation.setFillAfter(true);
//				animation.setDuration(100);
//				imageView.startAnimation(animation);
				switch (arg0) {
				case 0:
					tv_found.setTextAppearance(MainTabActivity.this,
							R.style.title_v1);
					tv_travel.setTextAppearance(MainTabActivity.this,
							R.style.title_v2);
					tv_information.setTextAppearance(MainTabActivity.this,
							R.style.title_v2);
					break;
				case 1:
					tv_found.setTextAppearance(MainTabActivity.this,
							R.style.title_v2);
					tv_travel.setTextAppearance(MainTabActivity.this,
							R.style.title_v1);
					tv_information.setTextAppearance(MainTabActivity.this,
							R.style.title_v2);
					break;
				case 2:
					tv_found.setTextAppearance(MainTabActivity.this,
							R.style.title_v2);
					tv_travel.setTextAppearance(MainTabActivity.this,
							R.style.title_v2);
					tv_information.setTextAppearance(MainTabActivity.this,
							R.style.title_v1);
					break;
				}
			}

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {

			}

			@Override
			public void onPageScrollStateChanged(int arg0) {

			}
		});
		
		if( getIntent().getExtras() != null) {
			int position = getIntent().getExtras().getInt("navigation",0);
			if(position != 0) {
				viewPager.setCurrentItem(0);
			} else {
				viewPager.setCurrentItem(1);
			}
		} else {
			viewPager.setCurrentItem(1);
		}
		
		registerPushRid();
		
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if (!isExit) {
				isExit = true;
				Toast.makeText(getApplicationContext(), R.string.confirm_exit,
						Toast.LENGTH_SHORT).show();
				new Handler().postDelayed(new Runnable() {
					public void run() {
						isExit = false;
					}
				}, 2000);
				return false;
			}
		}
		return super.onKeyDown(keyCode, event);
	}

	public class MyFragmentPagerAdapter extends FragmentStatePagerAdapter {
		private ArrayList<ListBaseFragment> fragmentsList;

		public MyFragmentPagerAdapter(FragmentManager fm) {
			super(fm);
		}

		public MyFragmentPagerAdapter(FragmentManager fm,
				ArrayList<ListBaseFragment> fragments) {
			super(fm);
			this.fragmentsList = fragments;
		}

		@Override
		public int getCount() {
			return fragmentsList.size();
		}

		@Override
		public Fragment getItem(int arg0) {
			return fragmentsList.get(arg0);
		}

		@Override
		public int getItemPosition(Object object) {
			return PagerAdapter.POSITION_NONE;
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.tv_found:
			viewPager.setCurrentItem(0);
			break;
		case R.id.tv_travel:
			viewPager.setCurrentItem(1);
			break;
		case R.id.tv_infomation:
			viewPager.setCurrentItem(2);
			break;
		case R.id.ll_prompt:
			app.getUserInfo_sp().putBoolean("prompt", true);
			llPrompt.setVisibility(View.GONE);
		}
	}

//	private void initImageView() {
//		imageView = (ImageView) findViewById(R.id.cursor);
//		bmpW = BitmapFactory.decodeResource(getResources(),
//				R.drawable.bottom_line).getWidth();
//		DisplayMetrics dm = new DisplayMetrics();
//		getWindowManager().getDefaultDisplay().getMetrics(dm);
//		int screenW = dm.widthPixels;
//		offset = (screenW / 3 - bmpW) / 2;
//		Matrix matrix = new Matrix();
//		matrix.postTranslate(offset, 0);
//		imageView.setImageMatrix(matrix);
//	}
	
	public void setScanScroll(boolean flag) {
		viewPager.setScroll(flag);
	}
	@Override
	public boolean dispatchKeyEvent(KeyEvent event) {
		if(!flag){
			app.getUserInfo_sp().putBoolean("prompt", true);
			llPrompt.setVisibility(View.GONE);
		}
		return super.dispatchKeyEvent(event);
	}
	
	public void registerPushRid() {
		String rid = readRid(this);
		registerRid();
		// if ("-1".equals(rid)) {
		// // 2.1说明还没有注册或者没注销掉了,那么直接注册即可
		// registerRid();
		// } else {
		// // 2.2如果有RID,没有问题,可以根据自己应用的情况做些事情,例如检查是否已经注册自己应用的APS
		// // ReceiverNotifier.getInstance().notifyRidGot(rid);
		// // registerRid();
		// }
	}

	private void registerRid() {
		Intent intent = new Intent().setAction(
				"com.gionee.cloud.intent.REGISTER").putExtra("packagename",
				"com.gionee.gntravel");
		startService(intent);
	}

	private String readRid(Context mContext) {
		String rid = null;
		SharedPreferences mPreference = mContext.getSharedPreferences(
				"config_sp", Context.MODE_PRIVATE);
		rid = mPreference.getString("rid", "-1");
		return rid;
	}
}
