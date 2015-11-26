package com.gionee.gntravel;

import java.util.ArrayList;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import com.gionee.gntravel.customview.CustomViewPager;
import com.gionee.gntravel.fragment.FlightListFragment;
import com.gionee.gntravel.fragment.HotelListFragment;
import com.gionee.gntravel.fragment.ListBaseFragment;
import com.youju.statistics.YouJuAgent;

public class ListOfMessageActivity extends FragmentActivity implements
		View.OnClickListener {
	private Button btn_flight;
	private Button hotel;
	private LinearLayout occlusionbg;
	private CustomViewPager viewPager;
	private ArrayList<ListBaseFragment> frgList;
	private ListBaseFragment lbf;
	private MyFragmentPagerAdapter frgAdapter;
	private SharedPreferences sp;
	
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {

		case R.id.btn_flight:
			viewPager.setCurrentItem(0);
			YouJuAgent.onEvent(this, getString(R.string.youju_flight));
			break;
		case R.id.hotel:
			viewPager.setCurrentItem(1);
			YouJuAgent.onEvent(this, getString(R.string.youju_hotel));
			setScanScroll(true);
			break;
		case R.id.tv_title:
			finish();
			break;
		case R.id.occlusionbg:
			if(viewPager.getCurrentItem()==0) {
				MyFragmentPagerAdapter adapter = ((MyFragmentPagerAdapter)viewPager.getAdapter());
				FlightListFragment fragment = (FlightListFragment)adapter.getFragment(0);
				fragment.hideSearch(false);
			}
			break;
		default:
			break;
		}
	}

	public void showOcclusionBg() {
		occlusionbg.setVisibility(View.VISIBLE);
	}

	public void hideOcclusionBg() {
		occlusionbg.setVisibility(View.GONE);
	}


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_list_of_message);
		initViews();
		initViewPager();
	}

	private void initViews() {
		findViewById(R.id.tv_title).setOnClickListener(this);
		findViewById(R.id.tv_title).setFocusable(true);
		btn_flight = (Button) findViewById(R.id.btn_flight);
		btn_flight.setOnClickListener(this);
		hotel = (Button) findViewById(R.id.hotel);
		hotel.setFocusable(true);
		btn_flight.setFocusable(true);
		hotel.setOnClickListener(this);
		occlusionbg = (LinearLayout)findViewById(R.id.occlusionbg);
		occlusionbg.setOnClickListener(this);
		viewPager = (CustomViewPager) findViewById(R.id.viewPager);
	}

	public void initViewPager() {
		frgList = new ArrayList<ListBaseFragment>();
		FlightListFragment flightFragment = FlightListFragment.newInstance();
		frgList.add(flightFragment);
		lbf = frgList.get(0);
		HotelListFragment hotelListFragment = HotelListFragment.newInstance();
		frgList.add(hotelListFragment);
		frgAdapter = new MyFragmentPagerAdapter(getSupportFragmentManager(), frgList);
		viewPager.setAdapter(frgAdapter);
		viewPager.setOnPageChangeListener(new MyOnPageChangeListener());
//		if(getIntent().getBooleanExtra("showHotelList", false)) {
//			viewPager.setCurrentItem(1);
//		} else {
//			viewPager.setCurrentItem(0);
//		}
		viewPager.setCurrentItem(0);
	}
	
	@Override
	public void onAttachFragment(Fragment fragment) {
		super.onAttachFragment(fragment);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && childOnKeyBack()) {
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}
	
	public class MyFragmentPagerAdapter extends FragmentPagerAdapter {
		public ArrayList<ListBaseFragment> list;

		public MyFragmentPagerAdapter(FragmentManager fm,
				ArrayList<ListBaseFragment> list) {
			super(fm);
			this.list = list;

		}

		@Override
		public int getCount() {
			return list.size();
		}

		@Override
		public ListBaseFragment getItem(int arg0) {
			return list.get(arg0);
		}

		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {
			super.destroyItem(container, position, object);
		}
		
		public ListBaseFragment getFragment(int key) {
			return list.get(key);
		}
	}

	public class MyOnPageChangeListener implements OnPageChangeListener {

		@Override
		public void onPageScrolled(int arg0, float arg1, int arg2) {

		}

		@Override
		public void onPageScrollStateChanged(int arg0) {

		}

		@Override
		public void onPageSelected(int position) {
			switch(position) {
			case 0:
				hotel.setBackground(getResources().getDrawable(
						R.drawable.btn_selector2));
				btn_flight.setBackground(getResources().getDrawable(
						R.drawable.btn_selector));
				if(lbf instanceof HotelListFragment) {
					((HotelListFragment) lbf).hideCalendar();
				}
				lbf = frgList.get(0);
				break;
			case 1:
				btn_flight.setBackground(getResources().getDrawable(
						R.drawable.btn_selector2));
				hotel.setBackground(getResources().getDrawable(
						R.drawable.btn_selector));
				if(lbf instanceof FlightListFragment) {
					((FlightListFragment) lbf).hideCalendar();
				}
				lbf = frgList.get(1);
				break;
			default:
				break;
			}

		}
	}
	

	private boolean childOnKeyBack() {
		int position = viewPager.getCurrentItem();
		switch(position) {
		case 0:
			return lbf.onKeyBackDown();
		case 1:
			return lbf.onKeyBackDown();
		case 2:
			return true;
		default:
			return false;
		}
	}
	
	public void moveToHotel() {
		viewPager.setCurrentItem(1);
	}
	
	public void setScanScroll(boolean flag) {
		viewPager.setScroll(flag);
	}

	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		viewPager.setCurrentItem(0);
		((FlightListFragment) lbf).onReload(intent);
		if(intent.getBooleanExtra("showHotelList", false)) {
			viewPager.setCurrentItem(1);
		} else {
			viewPager.setCurrentItem(0);
		}
	}
	
	
}
