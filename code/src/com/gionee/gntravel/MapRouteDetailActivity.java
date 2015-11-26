package com.gionee.gntravel;

import java.util.ArrayList;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.gionee.gntravel.adapter.MapRouteDetailAdapter;

public class MapRouteDetailActivity extends BaseActivity implements OnClickListener {
	private RelativeLayout back_rl;
	private ListView activity_map_route_detail_lv;
	private ArrayList<String> routeList;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_map_route_detail);
		initData();
		findViews();
	}

	private void initData() {
		Bundle b = getIntent().getExtras();
		routeList = b.getStringArrayList("routeList");
		routeList.add(0, getResources().getString(R.string.my_location2));
	}

	private void findViews() {
		TextView tv = (TextView) findViewById(R.id.tv_title);
		tv.setText(getResources().getString(R.string.routedetail));
		tv.setOnClickListener(this);
		activity_map_route_detail_lv = (ListView) findViewById(R.id.activity_map_route_detail_lv);
		activity_map_route_detail_lv.setAdapter(new MapRouteDetailAdapter(this, routeList));
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.tv_title:
			finish();
			break;
		default:
			break;
		}
	}

}
