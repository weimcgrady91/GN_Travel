package com.gionee.gntravel.adapter;

import java.util.ArrayList;

import android.app.Activity;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.gionee.gntravel.R;

public class MapRouteDetailAdapter extends BaseAdapter {
	private ArrayList<String> mRouteList = new ArrayList<String>();
	private Activity mcontex;
	private LayoutInflater minInflater;
	private TextView iv_yuan;

	public MapRouteDetailAdapter(Activity contex, ArrayList<String> routeList) {
		mRouteList = routeList;
		mcontex = contex;
		minInflater = LayoutInflater.from(mcontex);
	}

	@Override
	public int getCount() {
		return mRouteList.size();
	}

	@Override
	public Object getItem(int posotion) {
		return mRouteList.get(posotion);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		View v = minInflater.inflate(R.layout.item_route, null);
		TextView tv = (TextView) v.findViewById(R.id.item_route_tv);
		iv_yuan = (TextView) v.findViewById(R.id.item_route_yuan);
		iv_yuan.setText(position+"");
		tv.setText(mRouteList.get(position));
		TextView tv_line = (TextView) v.findViewById(R.id.item_route_line);
		if (position==0) {
			v.setBackgroundColor(mcontex.getResources().getColor(R.color.text_color_v1));
			ImageView iv_location = (ImageView) v.findViewById(R.id.item_route_location);
			iv_location.setVisibility(View.VISIBLE);
			tv_line.setVisibility(View.GONE);
			iv_yuan.setVisibility(View.GONE);
		}
		if (position==1) {
			FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.MATCH_PARENT);  
			//此处相当于布局文件中的Android:layout_gravity属性  
			lp.gravity = Gravity.BOTTOM|Gravity.CENTER_HORIZONTAL;  
			tv_line.setLayoutParams(lp);
			tv_line.getLayoutParams().height=27;
		}
		if (position==mRouteList.size()-1) {
			tv_line.getLayoutParams().height=27;
			iv_yuan.setBackground(mcontex.getResources().getDrawable(R.drawable.slt_redcirclefinish));
			iv_yuan.setText("");
		}
		return v;
	}

}
