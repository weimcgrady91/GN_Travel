package com.gionee.gntravel;

import java.util.ArrayList;
import java.util.List;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.gionee.gntravel.entity.WeatherBean;
import com.gionee.gntravel.entity.WeatherLocal;

public class WeatherCityManageActivity extends BaseActivity implements OnClickListener {
	private TextView tv_title;
	private Button activity_weather_btn_addcity;
	private ArrayList<WeatherLocal> weatherLocals = new ArrayList<WeatherLocal>();
	private ArrayList<WeatherLocal> weatherLocals_delete = new ArrayList<WeatherLocal>();
	private ListView activity_weather_lv_city;
	// private Button activity_weather_btn_editcity;
	private List<Boolean> deleteStateList = new ArrayList<Boolean>();
	private WeatherCityAdapter adapter;
	private Dialog dialogDel;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_weather_citymanage);

		Bundle bundle = getIntent().getExtras();
		ArrayList list = bundle.getParcelableArrayList("list");
		if (list != null && list.size() != 0) {
			weatherLocals = (ArrayList<WeatherLocal>) list.get(0);
		}

		for (int i = 0; weatherLocals != null && i < weatherLocals.size(); i++) {
			deleteStateList.add(false);
		}

		adapter = new WeatherCityAdapter();

		activity_weather_btn_addcity = (Button) findViewById(R.id.activity_weather_btn_addcity);
		tv_title = (TextView) findViewById(R.id.tv_title);
		tv_title.setText(R.string.weather_city_manage);
		tv_title.setOnClickListener(this);
		activity_weather_btn_addcity.setOnClickListener(this);
		activity_weather_lv_city = (ListView) findViewById(R.id.activity_weather_lv_city);
		// activity_weather_btn_editcity = (Button)
		// findViewById(R.id.activity_weather_btn_editcity);
		// activity_weather_btn_editcity.setOnClickListener(this);
		activity_weather_lv_city.setAdapter(adapter);
	}

	@Override
	protected void onResume() {
		super.onResume();
		// updateEditBtnState();
	}

	// private void updateEditBtnState() {
	// if (weatherLocals != null && weatherLocals.size() != 0) {
	// if (weatherLocals.size() == 1 &&
	// WeatherActivity.isCurrentCity(weatherLocals.get(0).getWeatherBean())) {
	// activity_weather_btn_editcity.setVisibility(View.INVISIBLE);
	// } else {
	// activity_weather_btn_editcity.setVisibility(View.VISIBLE);
	// }
	// } else {
	// activity_weather_btn_editcity.setVisibility(View.INVISIBLE);
	// }
	// }

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.tv_title:
			finish();
			break;
		case R.id.activity_weather_btn_addcity:
			addCity();
			break;
		// case R.id.activity_weather_btn_editcity:
		// String edit = getResources().getString(R.string.weather_edit);
		// String ok = getResources().getString(R.string.weather_ok);
		//
		// if (edit.equals(activity_weather_btn_editcity.getText().toString()))
		// {
		// activity_weather_btn_editcity.setText(ok);
		// setState(true);
		// } else {
		// //点击确定，保存删除，并将按钮置为编辑状态，如果删除到只有一个定位城市，该按钮要消失不见
		// activity_weather_btn_editcity.setText(edit);
		// setState(false);
		// //真正删除本地数据
		// for (WeatherLocal local:weatherLocals_delete) {
		// WeatherActivity.indexOfCurrentCity = 0;
		// WeatherActivity.updateOrAddLocalsByWeatherBean(WeatherCityManageActivity.this,
		// local.getWeatherBean(), -2, false);
		// }
		// }
		// break;
		default:
			break;
		}
	}

	private void setState(boolean deleteState) {
		for (int i = 0; i < deleteStateList.size(); i++) {
			deleteStateList.set(i, deleteState);
		}
		adapter.notifyDataSetChanged();
	}

	private void addCity() {
		Intent intent = new Intent(this, WeatherAddCityActivity.class);
		startActivity(intent);

	}

	public class WeatherCityAdapter extends BaseAdapter {

		LayoutInflater inflater = LayoutInflater.from(WeatherCityManageActivity.this);

		@Override
		public int getCount() {
			return weatherLocals == null ? 0 : weatherLocals.size();
		}

		@Override
		public Object getItem(int arg0) {
			return null;
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {

			final WeatherLocal local = weatherLocals.get(position);

			View view = inflater.inflate(R.layout.item_weather_city, null);
			ImageView item_weather_city_iv_local = (ImageView) view.findViewById(R.id.item_weather_city_iv_local);
			TextView item_weather_city_tv_cityname = (TextView) view.findViewById(R.id.item_weather_city_tv_cityname);
			// Button item_weather_city_bt_delete = (Button)
			// view.findViewById(R.id.item_weather_city_bt_delete);

			// 删除，只是表象，不真正删除，点击上方确定才真正删除
			// item_weather_city_bt_delete.setOnClickListener(new
			// OnClickListener() {
			//
			// @Override
			// public void onClick(View v) {
			// if (WeatherActivity.isCurrentCity(local.getWeatherBean())) {
			// return;
			// }
			// //定位到要删除的条目位置
			// int removeIndex = weatherLocals.indexOf(local);
			// //将要删除的条目保存起来
			// weatherLocals_delete.add(local);
			// //维护的删除按钮的状态要改变
			// if (weatherLocals.remove(local)) {
			// //删除内存中的数据
			// deleteStateList.remove(removeIndex);
			// }
			// WeatherCityAdapter.this.notifyDataSetChanged();
			//
			// }
			// });
			view.setOnLongClickListener(new View.OnLongClickListener() {

				@Override
				public boolean onLongClick(View v) {
					if (WeatherActivity.isCurrentCity(local.getWeatherBean())) {
						return false;
					}
					delCityDialog(local);
					return true;
				}

			});
			item_weather_city_tv_cityname.setText(local.getCity());

			// Boolean showStateOfDelete = deleteStateList.get(position);
			// if (showStateOfDelete == null || showStateOfDelete == false) {
			// item_weather_city_bt_delete.setVisibility(View.INVISIBLE);
			// } else {
			// item_weather_city_bt_delete.setVisibility(View.VISIBLE);
			// }
			//
			if (WeatherActivity.isCurrentCity(local.getWeatherBean())) {
				item_weather_city_iv_local.setVisibility(View.VISIBLE);
				// item_weather_city_bt_delete.setVisibility(View.INVISIBLE);
			}
			view.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					getWeather(local.getCity());
				}
			});
			return view;
		}

	}
	private void delCityDialog(final WeatherLocal local) {
		dialogDel = new Dialog(this, R.style.CustomProgressDialog);
		View viewdia = LayoutInflater.from(this).inflate(
				R.layout.delete_dialog, null);
		Button btnDel = (Button) viewdia.findViewById(R.id.btn_dialog_sure);
		Button btnDelCancel = (Button) viewdia
				.findViewById(R.id.btn_dialog_cancel);
		RelativeLayout reDeLayout = (RelativeLayout) viewdia
				.findViewById(R.id.re_dialog);
		ImageView imgCancel = (ImageView) viewdia
				.findViewById(R.id.img_dialog_title_cha);
		
		TextView tvTitleContent = (TextView) viewdia
		.findViewById(R.id.tv_dialog_title);
		tvTitleContent.setText("确定要删除该城市？");
		TextView tvTitle = (TextView) viewdia
				.findViewById(R.id.tv_dialog_title_name);
		tvTitle.setText(getString(R.string.delect));
		btnDel.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// 定位到要删除的条目位置
				int removeIndex = weatherLocals.indexOf(local);
				// 将要删除的条目保存起来
				weatherLocals_delete.add(local);
				// 维护的删除按钮的状态要改变
				if (weatherLocals.remove(local)) {
					// 删除内存中的数据
					deleteStateList.remove(removeIndex);
				}
				adapter.notifyDataSetChanged();
				 for (WeatherLocal local:weatherLocals_delete) {
				 WeatherActivity.indexOfCurrentCity = 0;
				 WeatherActivity.updateOrAddLocalsByWeatherBean(WeatherCityManageActivity.this,
				 local.getWeatherBean(), -2, false);
				 }
				dialogDel.dismiss();
			}
		});
		btnDelCancel.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				dialogDel.dismiss();
			}
		});
		reDeLayout.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				dialogDel.dismiss();
			}
		});
		imgCancel.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				dialogDel.dismiss();
			}
		});
		dialogDel.setContentView(viewdia);
		dialogDel.getWindow().setLayout(LayoutParams.MATCH_PARENT,
				LayoutParams.MATCH_PARENT);
		dialogDel.show();

	}
//	private void deletCity(final WeatherLocal local) {
//
//		AlertDialog.Builder builder = new Builder(WeatherCityManageActivity.this);
//		builder.setMessage("确定要删除？").setTitle("提示").setPositiveButton("确定", new DialogInterface.OnClickListener() {
//
//			@Override
//			public void onClick(DialogInterface dialog, int which) {
//				// 定位到要删除的条目位置
//				int removeIndex = weatherLocals.indexOf(local);
//				// 将要删除的条目保存起来
//				weatherLocals_delete.add(local);
//				// 维护的删除按钮的状态要改变
//				if (weatherLocals.remove(local)) {
//					// 删除内存中的数据
//					deleteStateList.remove(removeIndex);
//				}
//				adapter.notifyDataSetChanged();
//				 for (WeatherLocal local:weatherLocals_delete) {
//				 WeatherActivity.indexOfCurrentCity = 0;
//				 WeatherActivity.updateOrAddLocalsByWeatherBean(WeatherCityManageActivity.this,
//				 local.getWeatherBean(), -2, false);
//				 }
//				dialog.dismiss();
//			}
//		}).setNegativeButton("取消", new DialogInterface.OnClickListener() {
//
//			@Override
//			public void onClick(DialogInterface dialog, int which) {
//				dialog.dismiss();
//			}
//		});
//		builder.create().show();
//	}

	public void getWeather(final String text) {
		WeatherBean bean = new WeatherBean();
		bean.setCity(text);
		int indexOfOld = WeatherActivity.updateOrAddLocalsByWeatherBean(WeatherCityManageActivity.this, bean, 0, true);
		Intent i = new Intent(WeatherCityManageActivity.this, WeatherActivity.class);
		i.putExtra("needLocation", false);
		i.putExtra("city", text);
		WeatherActivity.indexOfCurrentCity = indexOfOld;
		i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		startActivity(i);
	}

}
