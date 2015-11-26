package com.gionee.gntravel.adapter;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.gionee.gntravel.R;
import com.gionee.gntravel.entity.Bank;

public class ChooseConditionAdapter extends BaseAdapter {

	private Activity mContext;
	private List<String> mList = new ArrayList<String>();
	private List<Bank> mbanks = new ArrayList<Bank>();
	private LayoutInflater mInflater;
	private int selectId;
	private onSelectListner l;
	private String flag;

	public ChooseConditionAdapter(Activity context, List<String> list, List<Bank> banks, String flag, int position) {
		mContext = context;
		mList = list;
		mInflater = LayoutInflater.from(mContext);
		this.selectId = position;
		this.flag = flag;
		this.mbanks = banks;
	}

	@Override
	public int getCount() {
		if (flag.equals("bank")) {
			return mbanks.size();
		}
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
		ViewHolder holder = null;
		holder = new ViewHolder();
		convertView = mInflater.inflate(R.layout.item_price, null);
		holder.window_price_tv = (TextView) convertView.findViewById(R.id.window_price_tv);
		holder.window_price_rb = (RadioButton) convertView.findViewById(R.id.window_price_rb);
		if (position == 0) {
			holder.window_price_tv.setTextColor(mContext.getResources().getColor(R.color.text_color_v3));
		}
		if (flag.equals("bank")) {
			holder.window_price_tv.setText(mbanks.get(position).getBankName());
		} else {
			holder.window_price_tv.setText(mList.get(position));
		}
		if (position == selectId) {
			holder.window_price_rb.setChecked(true);
		} else {
			holder.window_price_rb.setChecked(false);
		}
		final View temp_view = convertView;
		OnClickListener clickListener = new OnClickListener() {

			@Override
			public void onClick(View v) {
				ListView parent_listview = (ListView) temp_view.getParent();
				for (int i = 0; mList != null && mList.size() > 0 && i < mList.size(); i++) {
					if (i != position) {
						RelativeLayout rl = (RelativeLayout) parent_listview.getChildAt(i);
						RadioButton rb = (RadioButton) rl.getChildAt(1);
						rb.setChecked(false);
					}
				}
				if (l != null) {
					if (flag.equals("price")) {
						l.selectPrice(position);
					}
					if (flag.equals("bank")) {
						if (position == 0) {
							temp_view.findViewById(R.id.window_price_rb).setVisibility(View.GONE);
							temp_view.setClickable(false);
						}
						l.selectBank(position);
					}
					if (flag.equals("defalt")) {
						l.selectDefalt(position);
					}
				}
			}
		};
		convertView.setOnClickListener(clickListener);
		holder.window_price_rb.setOnClickListener(clickListener);
		convertView.setClickable(true);
		convertView.setFocusable(true);
		return convertView;
	}

	private class ViewHolder {
		private TextView window_price_tv;
		private RadioButton window_price_rb;
	}

	public void setOnSelectListner(onSelectListner l) {
		this.l = l;
	}

	public interface onSelectListner {
		public void selectPrice(int position);

		public void selectBank(int position);

		public void selectDefalt(int position);
	}
}
