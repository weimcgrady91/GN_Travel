package com.gionee.gntravel.adapter;

import java.util.HashMap;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.gionee.gntravel.R;
import com.gionee.gntravel.entity.DomesticHotelComment;

public class HotelCommentAdapter extends BaseAdapter {
	private LayoutInflater mInflater;
	private List<DomesticHotelComment> mList;
	private Context context;
	private static HashMap<Integer, Boolean> isSelected;
	private int page;

//	@Override
//	public boolean isEnabled(int position) {
//		// TODO Auto-generated method stub
//		return false;
//	}
//
	@Override
	public boolean areAllItemsEnabled() {
		// TODO Auto-generated method stub
		return false;
	}

	public HotelCommentAdapter(Context context, List<DomesticHotelComment> list) {
		this.mInflater = LayoutInflater.from(context);
		this.mList = list;
		this.context = context;
		isSelected = new HashMap<Integer, Boolean>();
	}

	public int getPage() {
		return page;
	}

	public void setPage(int page) {
		this.page = page;
	}

	public List<DomesticHotelComment> getmList() {
		return mList;
	}

	@Override
	public int getCount() {

		return mList.size();
	}

	@Override
	public Object getItem(int position) {
		return mList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	public static HashMap<Integer, Boolean> getIsSelected() {
		return isSelected;
	}

	public static void setIsSelected(HashMap<Integer, Boolean> isSelected) {
		HotelCommentAdapter.isSelected = isSelected;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		if (convertView == null) {// convertView 存放item的
			holder = new ViewHolder();
			convertView = mInflater.inflate(R.layout.item_hotel_comment, null);
			holder.score = (TextView) convertView.findViewById(R.id.item_hotel_comm_tv_score);
			holder.commText = (TextView) convertView.findViewById(R.id.item_hotel_comm_tv_text);
			holder.author = (TextView) convertView.findViewById(R.id.item_hotel_comm_tv_author);
			holder.commDate = (TextView) convertView.findViewById(R.id.item_hotel_comm_tv_date);
			// holder.item_hotel_comm_line =
			// (TextView)convertView.findViewById(R.id.item_hotel_comm_line);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		String date = mList.get(position).getWritingDate();
		int index = date.indexOf("T");
		date = date.substring(0, index);
		holder.score.setText(mList.get(position).getRating() + "分");
		holder.commText.setText(mList.get(position).getContent());
		holder.author.setText(mList.get(position).getUserId());
		holder.commDate.setText(date);
		convertView.setEnabled(false);
		return convertView;
	}

	// 存放listview中的数据项
	public final class ViewHolder {
		public TextView score;
		public TextView commText;
		public TextView author;
		public TextView commDate;
		public TextView item_hotel_comm_line;
	}
}
