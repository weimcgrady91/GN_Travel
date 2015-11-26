package com.gionee.gntravel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.gionee.gntravel.customview.CustomProgressDialog;
import com.gionee.gntravel.db.dao.PassengerDao;
import com.gionee.gntravel.db.dao.impl.PassengerDaoImpl;
import com.gionee.gntravel.entity.ContentBean;
import com.gionee.gntravel.utils.DBUtils;
import com.gionee.gntravel.utils.FinalString;
import com.gionee.gntravel.utils.HttpConnCallback;
import com.gionee.gntravel.utils.HttpConnUtil4Gionee;
import com.gionee.gntravel.utils.NetWorkUtil;

/**
 * 
 * @author lijinbao 旅客列表
 */
public class PassengerListActivity extends Activity implements OnClickListener,
		HttpConnCallback {
	private HttpConnUtil4Gionee task;
	private Thread doResultThread;
	private ResultHandler handler = new ResultHandler();
	private CustomProgressDialog progressDialog;
	private HashMap<String, String> params;
	private LinearLayout llAddPassenger;
	private ListView listView;
	private List<ContentBean> list = new ArrayList<ContentBean>();
	private TextView tvTitle;
	private ImageView imgLine;
	private Dialog dialogDel;
	private TravelApplication app;
	private PassengerListAdaper passengerListAdaper;
	private String selectPassengerUrl;
	private int index;// 删除的是哪个
	private static final int EDLECTSUC = 0x5005;
	private String id;// 修改的是哪个id
	private class ResultHandler extends Handler {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case FinalString.DATA_FINISH:
				loadDataFinish();
				stopProgressDialog();
				break;
			case FinalString.NET_ERROR:
				showNetErrorMsg();
				break;
			case FinalString.NOT_FOUND:
				selecttErrorMsg(msg);
				break;
			case EDLECTSUC:

				delectPassengerList(list.get(index).getId());
				delLoadDataFinish();
				stopProgressDialog();
				break;
			default:
				break;
			}
		}
	}

	/**
	 * 
	 * @param msg
	 */
	private void selecttErrorMsg(Message msg) {
		stopProgressDialog();
		String errorMsg = (String) msg.obj;
		if ("常用旅客列表为空!".equals(errorMsg)) {
			imgLine.setVisibility(View.GONE);
			return;
		}
		Toast.makeText(this, errorMsg, Toast.LENGTH_LONG).show();
	}

	private void showNetErrorMsg() {
		Toast.makeText(this, R.string.network_timeout, Toast.LENGTH_LONG)
				.show();
		stopProgressDialog();
	}

	private void loadDataFinish() {
		imgLine.setVisibility(View.VISIBLE);
		passengerListAdaper.notifyDataSetChanged();
	}

	/**
	 * 删除后重新刷新界面
	 */
	private void delLoadDataFinish() {
		list.remove(index);
		if (list.size() == 0) {
			imgLine.setVisibility(View.GONE);
		}
		passengerListAdaper.notifyDataSetChanged();
	}

	/**
	 * 删除本地数据库信息(删除一条)
	 */
	private void delectPassengerList(String id) {
		// 查询 数据库 如果数据中有此数据，则至为true
		DBUtils dbUitls = new DBUtils(PassengerListActivity.this);
		SQLiteDatabase db = dbUitls.openReadWriteDatabase();
		PassengerDao dao = new PassengerDaoImpl();
		dao.delectPassengersById(db, id);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_passenger_list);
		setupView();
		passengerListAdaper = new PassengerListAdaper();
		listView.setAdapter(passengerListAdaper);
		initParams();
		loadingData();
		addListener();
	}

	private void setupView() {
		app = (TravelApplication) getApplication();
		tvTitle = (TextView) findViewById(R.id.tv_title);
		tvTitle.setText(getString(R.string.lvke));
		tvTitle.setOnClickListener(this);
		imgLine = (ImageView) findViewById(R.id.img_line);
		llAddPassenger = (LinearLayout) findViewById(R.id.ll_add_passenger);
		listView = (ListView) findViewById(R.id.lv_passenger_list);
		llAddPassenger.setOnClickListener(this);

	}

	private void initParams() {
		params = new HashMap<String, String>();
//		String userId = app.getUserId();
//		params.put("tel", userId);
		params.put("u", app.getU());
		params.put("userId", app.getUserId());
		selectPassengerUrl = getString(R.string.gionee_host)
				+ "/GioneeTrip/tripAction_findUserAllBaorderInfo.action";
	}

	/**
	 * 长按删除，点击修改
	 */
	private void addListener() {
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long arg) {
				index = position;
				id = list.get(position).getId();
				String name = list.get(position).getName();
				String code = list.get(position).getCode();
				Intent intent = new Intent(PassengerListActivity.this,
						EditMyPassengerActivity.class);
				intent.putExtra("titleName", getString(R.string.editlvkje));
				intent.putExtra("CODE", code);
				intent.putExtra("NAME", name);
				intent.putExtra("INDEX", id);
				intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
				startActivityForResult(intent, 100);
				return;
			}
		});
		listView.setOnItemLongClickListener(new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view,
					int position, long id) {
				index = position;
				delLianxiPrensonDialog();
				return true;
			}
		});
	}

	private void loadingData() {
		startProgressDialog();

		if (!NetWorkUtil.isNetworkConnected(this)) {
			Message msg = Message.obtain();
			msg.what = FinalString.NET_ERROR;
			handler.sendMessageDelayed(msg, 1000);
			return;
		}
		task = new HttpConnUtil4Gionee(this);
		task.execute(selectPassengerUrl, params,
				HttpConnUtil4Gionee.HttpMethod.POST);
	}

	@Override
	public void execute(String result) {
		if (TextUtils.isEmpty(result)) {
			Message msg = Message.obtain();
			msg.what = FinalString.NET_ERROR;
			handler.sendMessage(msg);
			return;
		}
		doResultThread = new Thread(new DoResult(result));
		doResultThread.start();
	}

	private class DoResult implements Runnable {
		private String result;

		public DoResult(String result) {
			this.result = result;
		}

		@Override
		public void run() {
			try {
				JSONObject responseJson = new JSONObject(result);
				if (!FinalString.ERRORCODE.equals(responseJson
						.getString("errorCode"))) {
					Message msg = Message.obtain();
					msg.what = FinalString.NOT_FOUND;
					msg.obj = responseJson.getString("errorMsg");
					handler.sendMessage(msg);
					return;
				}
				String cotent = responseJson.getString("content");
				if ("删除成功".equals(cotent)) {
					Message msg = Message.obtain();
					msg.what = EDLECTSUC;
					handler.sendMessage(msg);
				} else {// 查询所有

					JSONArray jsonArray = new JSONArray(cotent);
					for (int i = 0; i < jsonArray.length(); i++) {
						JSONObject Item = jsonArray.getJSONObject(i);
						String id = Item.getString("id");
						String code = Item.getString("cardId");
						String name = Item.getString("name");
						list.add(new ContentBean(id, code, name));
					}
					Message msg = Message.obtain();
					msg.what = FinalString.DATA_FINISH;
					handler.sendMessage(msg);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.ll_add_passenger:
			Intent intent = new Intent(this, AddPassengerActivity.class);
			intent.putExtra("titleName", getString(R.string.xinzenglvke));
			intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
			startActivityForResult(intent, 200);
			break;

		case R.id.tv_title:
			this.finish();
			break;

		default:
			break;
		}
	}

	private class PassengerListAdaper extends BaseAdapter {
		@Override
		public int getCount() {
			return list.size();
		}

		@Override
		public Object getItem(int position) {
			return position;
		}

		@Override
		public long getItemId(int position) {
			return position;
		}
		@Override
		public View getView(final int position, View convertView,
				ViewGroup parent) {
			ViewHolder holder = null;
			if (convertView == null) {// convertView 存放item的
				holder = new ViewHolder();
				convertView = getLayoutInflater().inflate(R.layout.my_add_view_passenger, null);
				holder.name = (TextView) convertView.findViewById(R.id.tv_my_passenger_name);
				holder.code = (TextView) convertView.findViewById(R.id.tv_my_passenger_code);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			holder.name.setText(list.get(position).getName());
			holder.code.setText(list.get(position).getCode());
			return convertView;
		}
	}

	public final class ViewHolder {
		public TextView name;
		public TextView code;
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode,
			Intent intent) {
		if (resultCode == 20) {// 新增常用旅客回来
			list.clear();
			initParams();
			loadingData();

		} else if (resultCode == 30) {
			String editName = intent.getExtras().getString("EditName");
			String editCode = intent.getExtras().getString("EditCode");
			list.remove(index);
			list.add(index, new ContentBean(id, editCode, editName));
			passengerListAdaper.notifyDataSetChanged();
		}

		super.onActivityResult(requestCode, resultCode, intent);
	}

	/**
	 * 删除某条数据
	 */
	private void delLoadingData() {
		String id = list.get(index).getId();
		params = new HashMap<String, String>();
		params.put("bid", id);
		selectPassengerUrl = getString(R.string.gionee_host)+ "/GioneeTrip/tripAction_deleteBoarderInfo.action";
		startProgressDialog();
		if (!NetWorkUtil.isNetworkConnected(this)) {
			Message msg = Message.obtain();
			msg.what = FinalString.NET_ERROR;
			handler.sendMessageDelayed(msg, 1000);
			return;
		}
		Log.e("test", params.toString());
		task = new HttpConnUtil4Gionee(this);
		task.execute(selectPassengerUrl, params,
				HttpConnUtil4Gionee.HttpMethod.POST);
	}

	private void delLianxiPrensonDialog() {
		dialogDel = new Dialog(this, R.style.CustomProgressDialog);
		View viewdia = LayoutInflater.from(this).inflate(R.layout.delete_dialog, null);
		Button btnDel = (Button) viewdia.findViewById(R.id.btn_dialog_sure);
		Button btnDelCancel = (Button) viewdia.findViewById(R.id.btn_dialog_cancel);
		RelativeLayout reDeLayout = (RelativeLayout) viewdia.findViewById(R.id.re_dialog);
		ImageView imgCancel = (ImageView) viewdia.findViewById(R.id.img_dialog_title_cha);
		TextView tvTitle = (TextView) viewdia.findViewById(R.id.tv_dialog_title_name);
		tvTitle.setText(getString(R.string.delect));
		btnDel.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				delLoadingData();
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

	private void startProgressDialog() {
		if (progressDialog == null) {
			progressDialog = CustomProgressDialog.createDialog(this,
					R.layout.customprogressdialog);
			progressDialog.setCanceledOnTouchOutside(false);
			progressDialog.setCancelable(false);
		}
		progressDialog.show();
	}

	private void stopProgressDialog() {
		if (progressDialog != null) {
			progressDialog.dismiss();
			progressDialog = null;
		}
	}

}