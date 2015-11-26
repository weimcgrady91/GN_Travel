package com.gionee.gntravel.adapter;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckedTextView;
import android.widget.ImageView;
import android.widget.TextView;

import com.gionee.gntravel.R;

public class ChooseHotelAdressAdapter extends BaseAdapter {

	private Activity mContext;
	private List<String> mList = new ArrayList<String>();
	private LayoutInflater mInflater;
	private onAddressSelectListner l;
	private int mPosition;

	public ChooseHotelAdressAdapter(Activity context, List<String> list, int addressPosition) {
		mContext = context;
		mList = list;
		mInflater = LayoutInflater.from(mContext);
		mPosition = addressPosition;
	}

	@Override
	public int getCount() {
		return mList.size();
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
	public View getView(final int position, View convertView, ViewGroup parent) {
		final ViewHolder holder = new ViewHolder();
		View layout = mInflater.inflate(R.layout.item_hotel_address, null);
		holder.item_hotel_address_tv = (TextView) layout.findViewById(R.id.item_hotel_address_tv);
		holder.item_hotel_address_iv = (ImageView) layout.findViewById(R.id.item_hotel_address_iv);
		holder.item_hotel_single_checktv = (CheckedTextView) layout.findViewById(R.id.item_hotel_single_checktv);
		layout.setTag(holder);
		if (mPosition == 0) {
			holder.item_hotel_single_checktv.setChecked(true);
		}
		if (position == 0) {
			holder.item_hotel_address_iv.setVisibility(View.GONE);
			holder.item_hotel_single_checktv.setVisibility(View.VISIBLE);

		}
		layout.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (position == 0) {
					holder.item_hotel_single_checktv.setChecked(true);
					l.selectAddress(position);
				} else {
					if (l != null) {
						l.selectAddress(position);
					}
				}
			}
		});
		holder.item_hotel_address_tv.setText(mList.get(position));
		return layout;
	}

	private class ViewHolder {
		private TextView item_hotel_address_tv;
		private ImageView item_hotel_address_iv;
		private CheckedTextView item_hotel_single_checktv;
	}

	public void setOnAddressSelectListner(onAddressSelectListner l) {
		this.l = l;
	}

	public interface onAddressSelectListner {
		public void selectAddress(int position);
	}
}
