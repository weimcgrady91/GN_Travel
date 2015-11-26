package com.gionee.gntravel.adapter;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.ctrip.openapi.java.utils.ImageUtil;
import com.gionee.gntravel.R;
import com.gionee.gntravel.TravelApplication;
import com.gionee.gntravel.entity.Hotel;
import com.gionee.gntravel.entity.HotelSearchKeyBean;
import com.gionee.gntravel.fragment.HotelListFragment;

public class HotelListAdapter extends BaseAdapter {

	private List<Hotel> hotelList = new ArrayList<Hotel>();
	private LayoutInflater mInflater;
	private Context mContext;

	public HotelListAdapter(List<Hotel> hotelList, Context context) {
		this.hotelList = hotelList;
		this.mContext = context;
		mInflater = LayoutInflater.from(mContext);
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return hotelList.size()==0?0:hotelList.size() + 1;
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (position == 0) {
			View item_tujia = mInflater.inflate(R.layout.item_tujia, null);
			return item_tujia;
		}
		ViewHolder holder = null;
		if (convertView == null || position == 1 || convertView.getTag() == null) {
			holder = new ViewHolder();
			convertView = mInflater.inflate(R.layout.item_hotel, null);
			holder.hotel_iv_pic = (ImageView) convertView.findViewById(R.id.hotel_iv_pic);
			holder.hotel_tv_name = (TextView) convertView.findViewById(R.id.hotel_tv_name);
			holder.hotel_tv_price = (TextView) convertView.findViewById(R.id.hotel_tv_price);
			holder.hotel_tv_star = (TextView) convertView.findViewById(R.id.hotel_tv_star);
			holder.hotel_tv_zone = (TextView) convertView.findViewById(R.id.hotel_tv_zone);
			holder.hotel_tv_commrate = (TextView) convertView.findViewById(R.id.hotel_tv_commrate);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		Hotel hotel = hotelList.get(position - 1);
		holder.hotel_tv_name.setText(hotel.getHotelName());
		if (TextUtils.isEmpty(hotel.getPrice())) {
			holder.hotel_tv_price.setText("未知");
		} else {
			holder.hotel_tv_price.setText((int) (Float.parseFloat(hotel.getPrice())) + "");
		}
		holder.hotel_tv_commrate.setText(hotel.getCommRate() + "分");
		int hotelStarRate = 0;
		if (!TextUtils.isEmpty(hotel.getHotelStarRate())) {
			hotelStarRate = Integer.parseInt(hotel.getHotelStarRate());
		}
		String hotelStar = "";
		if (hotelStarRate < 3) {
			hotelStar = "经济型酒店";
		}
		if (hotelStarRate == 3) {
			hotelStar = "三星级酒店";
		}
		if (hotelStarRate == 4) {
			hotelStar = "四星级酒店";
		}
		if (hotelStarRate == 5) {
			hotelStar = "五星级酒店";
		}
		holder.hotel_tv_star.setText(hotelStar);
		String distance = hotel.getDistance();
		HotelSearchKeyBean bean = HotelListFragment.getHotel_searchKey();
		String hotelsearchkey = "";
		if (bean!=null) {
			hotelsearchkey =bean.getName();
		}
		if (!TextUtils.isEmpty(distance)) {
			if (!TextUtils.isEmpty(hotelsearchkey)&&HotelListFragment.isSearchAddress()) {
				String hotelsearchkey_utf8;
				try {
					hotelsearchkey_utf8 = URLDecoder.decode(hotelsearchkey, "utf-8");
					holder.hotel_tv_zone.setText("距"+hotelsearchkey_utf8+distance);
				} catch (UnsupportedEncodingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}else if (HotelListFragment.isLocationCity()) {
				holder.hotel_tv_zone.setText("距您"+distance);
			}else{
				holder.hotel_tv_zone.setText(hotel.getZoneName());
			}
		}else{
			holder.hotel_tv_zone.setText(hotel.getZoneName());
		}

		if (((TravelApplication)mContext.getApplicationContext()).isShowImgWithWifiState()) {
			ConnectivityManager connManager = (ConnectivityManager) mContext
					.getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo networkInfo = connManager.getActiveNetworkInfo();
			if (networkInfo.isConnected()) {
				if (networkInfo.getType() == ConnectivityManager.TYPE_WIFI) {
					ImageUtil.getInstance(mContext).display(hotel.getImageUrl(), holder.hotel_iv_pic, null);
				} 
			}
		} else {
			ImageUtil.getInstance(mContext).display(hotel.getImageUrl(), holder.hotel_iv_pic, null);
		}
		return convertView;
	}

	private class ViewHolder {
		private TextView hotel_tv_name;
		private TextView hotel_tv_star;
		private TextView hotel_tv_price;
		private TextView hotel_tv_commrate;
		private TextView hotel_tv_zone;
		private ImageView hotel_iv_pic;
	}
}
