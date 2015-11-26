package com.gionee.gntravel.adapter;

import java.util.List;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.gionee.gntravel.R;
import com.gionee.gntravel.entity.HotelSearchKeyBean;

public class SearchAdapter extends BaseAdapter {

	private List<HotelSearchKeyBean> items;
	private Context context;
	private OnKeyItemClickListner listener;

	public SearchAdapter(Context context, List<HotelSearchKeyBean> items) {
		this.items = items;
		this.context = context;
//		this.listener = listener;
	}

	public void clear() {
		items.clear();
	}

	@Override
	public int getCount() {
		return items == null ? 0 : items.size();
	}

	@Override
	public Object getItem(int position) {
		return null;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		convertView = LayoutInflater.from(context).inflate(R.layout.item_hotel_keysearch, null);
		TextView tv_name = (TextView) convertView.findViewById(R.id.item_hotelkeysearch_tv_name);
		TextView tv_flag = (TextView) convertView.findViewById(R.id.item_hotelkeysearch_tv_flag);
		final HotelSearchKeyBean bean = items.get(position);
		tv_name.setText(bean.getName());
		String flag = bean.getType();
		if (!TextUtils.isEmpty(flag)) {
			if (flag.equals("zone")) {
				flag = context.getString(R.string.zone_str);
			}else if (flag.equals("area")) {
				flag = context.getString(R.string.area_str);
			}else if (flag.equals("brand")) {
				flag = context.getString(R.string.brand_str);
			}else if (flag.equals("hotel")) {
				flag = context.getString(R.string.hotel_str);
			}
		}
		tv_flag.setText(flag);
		convertView.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				listener.selectKeyItem(bean);
			}
		});
		return convertView;
	}
	public void setOnKeyItemClickListner(OnKeyItemClickListner l) {
		this.listener = l;
	}

	public interface OnKeyItemClickListner {
		public void selectKeyItem(HotelSearchKeyBean bean);
	}

}
