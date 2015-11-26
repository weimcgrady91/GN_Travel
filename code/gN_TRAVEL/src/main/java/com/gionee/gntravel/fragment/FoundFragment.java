package com.gionee.gntravel.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.gionee.gntravel.H5Activity;
import com.gionee.gntravel.R;
import com.gionee.gntravel.WeatherActivity;
import com.gionee.gntravel.utils.HttpConnCallback;
import com.youju.statistics.YouJuAgent;

public class FoundFragment extends ListBaseFragment implements HttpConnCallback,
		View.OnClickListener {

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_found, null);
		view.findViewById(R.id.ll_news).setOnClickListener(this);
		view.findViewById(R.id.ll_stock).setOnClickListener(this);
		view.findViewById(R.id.ll_weather).setOnClickListener(this);
		view.findViewById(R.id.re_found_train).setOnClickListener(this);
		view.findViewById(R.id.rl_found_lifeService).setOnClickListener(this);
		return view;
	}

	@Override
	public void onClick(View v) {
		switch(v.getId()) {
		case R.id.ll_news:
			loadH5Activity(getString(R.string.news_url),getString(R.string.widget_news));
			YouJuAgent.onEvent(getActivity(), getString(R.string.youju_zixun));
			break;
		case R.id.ll_stock:
			loadH5Activity(getString(R.string.stock_url), getString(R.string.economics));
			YouJuAgent.onEvent(getActivity(), getString(R.string.youju_caijing));
			break;
		case R.id.ll_weather:
			showWeather();
			YouJuAgent.onEvent(getActivity(), getString(R.string.youju_tianqi));
			break;
		case R.id.re_found_train:
			loadH5Activity("http://m.tieyou.com/?utm_source=jlsl&ad=close&top=close&buy=close&free=close&qcp=1", "火车票查询");
			YouJuAgent.onEvent(getActivity(), getString(R.string.youju_huochepiao));
			break;
		case R.id.rl_found_lifeService:
			loadH5Activity(getString(R.string.dazhongdianping)
					+"?uid=gionee&category=全部分类&hasheader=1"
					, getString(R.string.life_service));
			YouJuAgent.onEvent(getActivity(), getString(R.string.youju_shenghuofuwu));
			break;
		default:
			
			break;
		}
	}

	private void showWeather() {
		Intent i = new Intent(this.getActivity(), WeatherActivity.class);
		i.putExtra("indexOfCurrentCity", 0);
		startActivity(i);
	}

	@Override
	public void execute(String result) {

	}
	
//	public void loadActivity(Class activityClass) {
//		Intent intent = new Intent();
//		intent.putExtra("url", getString(R.string.dazhongdianping));
//		intent.setClass(getActivity(), activityClass);
//		startActivity(intent);
//	}
	
	public void loadH5Activity(String url, String title) {
		Intent intent = new Intent();
		intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
		intent.putExtra("url", url);
		intent.putExtra("title", title);
		intent.putExtra("mIsNeedRetunParent", false);
		intent.setClass(getActivity(), H5Activity.class);
		startActivity(intent);
	}

	@Override
	public void loadDate() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean onKeyBackDown() {
		// TODO Auto-generated method stub
		return false;
	}
	
	
}
