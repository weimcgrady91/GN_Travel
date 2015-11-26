package com.gionee.gntravel.adapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import android.app.Activity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckedTextView;
import android.widget.TextView;

import com.gionee.gntravel.R;
import com.gionee.gntravel.entity.Brand;

public class ChooseHotelMultiAdapter extends BaseAdapter {

	private Activity mContext;
	private List<Brand> brandList = new ArrayList<Brand>();
	private LayoutInflater mInflater;
	private onMultiSelectListner l;
	private static HashMap<Integer, Boolean> checkMap = new HashMap<Integer, Boolean>();
	private List<View> views = new ArrayList<View>();

	public ChooseHotelMultiAdapter(Activity context, List<Brand> brands) {
		mContext = context;
		brandList = brands;
		mInflater = LayoutInflater.from(mContext);

		if (checkMap.size() == 0) {
			checkMap.put(0, true);
		}
		for (int i = 0; i < getCount(); i++) {
			View view = getView(i);
			views.add(view);
		}
	}

	public void clear() {
		checkMap.clear();
	}

	@Override
	public int getCount() {
		return brandList.size();
	}

	@Override
	public Object getItem(int position) {
		return null;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	public View getView(int position) {

		View item = mInflater.inflate(R.layout.item_hotel_brand, null);

		TextView item_hotel_multi_tv = (TextView) item.findViewById(R.id.item_hotel_multi_tv);
		item_hotel_multi_tv.setText(brandList.get(position).getBrandName());

		final CheckedTextView item_hotel_multi_checktv = (CheckedTextView) item
				.findViewById(R.id.item_hotel_multi_checktv);
		Boolean checked = checkMap.get(position);
		if (checked == null) {
			checked = false;
		}
		item_hotel_multi_checktv.setChecked(checked);

		item.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				int position = (Integer) v.getTag();
				if (position == 0) {
					// 移除其他
					for (int i = 0; i < getCount(); i++) {
						ViewGroup innerItem = (ViewGroup) getView(i, null, null);
						CheckedTextView tvCheck = (CheckedTextView) innerItem
								.findViewById(R.id.item_hotel_multi_checktv);
						if (i == 0) {
							tvCheck.setChecked(true);
						} else {
							tvCheck.setChecked(false);
						}
						checkMap.put(i, tvCheck.isChecked());
					}
				} else {
					int checkNum = 0;

					ViewGroup currentItem = (ViewGroup) getView(position, null, null);
					CheckedTextView tvCheckCurrentItem = (CheckedTextView) currentItem
							.findViewById(R.id.item_hotel_multi_checktv);
					tvCheckCurrentItem.toggle();
					checkMap.put(position, tvCheckCurrentItem.isChecked());

					Iterator<Integer> keys = checkMap.keySet().iterator();
					while (keys.hasNext()) {
						int keyPosition = keys.next();
						if (keyPosition == 0) {
							continue;
						}
						Boolean isCheck = checkMap.get(keyPosition);
						if (isCheck == null) {
							isCheck = false;
						}
						if (isCheck) {
							checkNum++;
						}
					}

					ViewGroup item0 = (ViewGroup) getView(0, null, null);
					CheckedTextView tvCheck0 = (CheckedTextView) item0.findViewById(R.id.item_hotel_multi_checktv);
					tvCheck0.setChecked(checkNum == 0);
					checkMap.put(0, tvCheck0.isChecked());
				}
				if (l!=null) {
					l.selectMulti(position, brandList.get(position).getBrandName(), checkMap, checkMap.get(position));
				}
			}

		});

		item.setTag(position);
		return item;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		return views.get(position);
	}

	public void clickFirst() {
		ViewGroup item0 = (ViewGroup) getView(0, null, null);
		item0.performClick();
	}

	public void setOnMultiSelectListner(onMultiSelectListner l) {
		this.l = l;
	}

	public interface onMultiSelectListner {
		public void selectMulti(int position, String condition, HashMap<Integer, Boolean> checkMap, boolean isCheck);
	}

	public void removeCondition(int pos) {
		if (pos == 0) {
			checkMap.clear();
			checkMap.put(pos, true);
		} else {
			checkMap.put(pos, false);
		}
	}
}
