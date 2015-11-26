package com.gionee.gntravel;

import java.util.HashMap;

import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.gionee.gntravel.customview.CustomProgressDialog;
import com.gionee.gntravel.db.dao.ContactsDao;
import com.gionee.gntravel.db.dao.impl.ContactsDaoImpl;
import com.gionee.gntravel.entity.ContentBean;
import com.gionee.gntravel.utils.DBUtils;
import com.gionee.gntravel.utils.FinalString;
import com.gionee.gntravel.utils.HttpConnCallback;
import com.gionee.gntravel.utils.HttpConnUtil4Gionee;
import com.gionee.gntravel.utils.NetWorkUtil;
import com.gionee.gntravel.utils.Utils;

/**
 * 
 * @author lijinbao 修改常用旅客
 */
public class EditContactsActivity extends Activity implements OnClickListener,
		HttpConnCallback {
	private HttpConnUtil4Gionee task;
	private Thread doResultThread;
	private ResultHandler handler = new ResultHandler();
	private Button btnSure;
	private EditText etName;
	private EditText etCode;
	private TextView tvTitle;
	private LinearLayout llWarning;
	private String codeStr = "";
	private String nameStr = "";
	private String editIndex;
	private HashMap<String, String> params;
	private String editPassengerUrl;
	private String titleName;
	private TextView tvTitleCradidorMobile;
	TravelApplication app;
	private CustomProgressDialog progressDialog;
	private ContentBean contactsLocation = new ContentBean();
	String name;
	String code;

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
			default:
				break;
			}
		}
	}

	private void selecttErrorMsg(Message msg) {
		String errorMsg = (String) msg.obj;
		Toast.makeText(this, errorMsg, Toast.LENGTH_LONG).show();
		stopProgressDialog();
	}

	private void showNetErrorMsg() {
		Toast.makeText(this, R.string.network_timeout, Toast.LENGTH_LONG)
				.show();
		stopProgressDialog();
	}

	private void loadDataFinish() {
		stopProgressDialog();
		Intent intent = new Intent();
		intent.putExtra("EditName", etName.getText().toString());
		intent.putExtra("EditCode", etCode.getText().toString());
		updateContact(etName.getText().toString(), etCode.getText().toString());
		setResult(ContactsActivity.EDIT_CONTACTS, intent);
		Utils.closeSoftware(this);
		this.finish();

	}

	/**
	 * 修改本地数据库信息
	 */
	private void updateContact(String name, String code) {
		// 查询 数据库 如果数据中有此数据，则至为true
		DBUtils dbUitls = new DBUtils(EditContactsActivity.this);
		SQLiteDatabase db = dbUitls.openReadWriteDatabase();
		ContactsDao dao = new ContactsDaoImpl();
		dao.updateContactsByUserIdandId(db, app.getU(), editIndex, name,
				code);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_add_dengji_person);
		app = (TravelApplication) getApplication();
		setupView();
		Utils.mobileAddSpace(13, etCode);
	}

	private void setupView() {
		Intent intent = getIntent();
		titleName = intent.getStringExtra("titleName");
		codeStr = intent.getStringExtra("CODE");// 18位没有空
		nameStr = intent.getStringExtra("NAME");
		editIndex = intent.getStringExtra("INDEX");
		tvTitle = (TextView) findViewById(R.id.tv_title);
		tvTitle.setText(titleName);
		tvTitle.setOnClickListener(this);
		btnSure = (Button) findViewById(R.id.btn_function);
		btnSure.setText(getString(R.string.ok));
		etName = (EditText) findViewById(R.id.et_add_dengji_name);
		etCode = (EditText) findViewById(R.id.et_add_dengji_code);
		llWarning = (LinearLayout) findViewById(R.id.ll_warning);
		tvTitleCradidorMobile = (TextView) findViewById(R.id.tv_title_cradid_or_mobile);
		tvTitleCradidorMobile.setText(getString(R.string.mobile));
		etCode.setHint(getString(R.string.pleasemobile));
		llWarning.setVisibility(View.GONE);
		btnSure.setOnClickListener(this);
		etName.setText(nameStr);
		etCode.setText(codeStr);

	}

	/**
	 * 修改数据
	 */
	private void editDate() {
		name = etName.getText().toString().trim();
		code = etCode.getText().toString().trim();
		String codeYanzheng = code.replaceAll(" ", "");
		if (Utils.limitName(this, name)) {
			if (Utils.limitMobile(this, codeYanzheng)) {
				initParams();
				loadingData();
			}
		}

	}

	private void initParams() {
		params = new HashMap<String, String>();
		params.put("cid", editIndex);
		params.put("name", name);
		params.put("telphone", code);
		editPassengerUrl = getString(R.string.gionee_host)
				+ "/GioneeTrip/tripAction_updateContacterInfo.action";
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
		task.execute(editPassengerUrl, params,
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
				if ("修改成功".equals(cotent)) {
					Message msg = Message.obtain();
					msg.what = FinalString.DATA_FINISH;
					msg.obj = (String) cotent;
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
		case R.id.btn_function:
			editDate();

			break;
		case R.id.tv_title:
			Utils.closeSoftware(this);
			Utils.showDialog(this, new EditText[] { etCode, etName });
			break;

		default:
			break;
		}

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

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (4 == event.getKeyCode()) {
			Utils.showDialog(this, new EditText[] { etCode, etName });
		}
		return super.onKeyDown(keyCode, event);
	}

}