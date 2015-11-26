package com.gionee.gntravel.adapter;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RadioButton;
import android.widget.TextView;

import com.gionee.gntravel.R;

public class ChooseRoomNumAdapter extends BaseAdapter {

	private Activity mContext;
	private List<String> mList = new ArrayList<String>();
	private LayoutInflater mInflater;
	private int mPosition;
	private onRoomSelectListner l;

	public ChooseRoomNumAdapter(Activity context, List<String> list, int position) {
		mContext = context;
		mList = list;
		mInflater = LayoutInflater.from(mContext);
		this.mPosition = position;
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

		ViewHolder holder = new ViewHolder();

		convertView = mInflater.inflate(R.layout.item_choose_room, null);
		holder.hotelorder_room_tv_num = (TextView) convertView.findViewById(R.id.activity_hotelorder_tv_roomnum);
		holder.hotelorder_room_rb_num = (RadioButton) convertView.findViewById(R.id.activity_hotelorder_rv_roomnum);

		holder.hotelorder_room_tv_num.setText(mList.get(position));
		convertView.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				mPosition = position;
				if (l != null) {
					l.selectRoom(mPosition);
				}
			}
		});
		holder.hotelorder_room_rb_num.setOnClickListener(new OnClickListener() {
			   
			   @Override
			   public void onClick(View v) {
			    ((View) v.getParent()).performClick();
			   }
			  });
		if (position == mPosition) {
			holder.hotelorder_room_rb_num.setChecked(true);
		} else {
			holder.hotelorder_room_rb_num.setChecked(false);
		}
		return convertView;
	}

	private class ViewHolder {
		private TextView hotelorder_room_tv_num;
		private RadioButton hotelorder_room_rb_num;
	}

	public void setOnRoomSelectListner(onRoomSelectListner l) {
		this.l = l;
	}

	public interface onRoomSelectListner {
		public void selectRoom(int position);
	}

}
