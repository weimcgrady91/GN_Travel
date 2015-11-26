package com.gionee.gntravel;

import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.Selection;
import android.text.TextUtils;
import android.text.TextWatcher;
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
import com.gionee.gntravel.db.dao.PassengerDao;
import com.gionee.gntravel.db.dao.impl.PassengerDaoImpl;
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
public class EditMyPassengerActivity extends Activity implements
		OnClickListener, HttpConnCallback {
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
	TravelApplication app;
	private CustomProgressDialog progressDialog;
	private ArrayList<ContentBean> passengerLocationList = new ArrayList<ContentBean>();
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
		findLocationPassengerList();
		for (int i = 0; i < passengerLocationList.size(); i++) {
			if (passengerLocationList.get(i).getId().equals(editIndex)) {
				// 修改
				// 去掉空格
				updatePassengerList(etName.getText().toString(), etCode
						.getText().toString());
			}
		}
		setResult(30, intent);
		Utils.closeSoftware(this);
		this.finish();

	}

	/**
	 * 查询本地数库
	 */
	private void findLocationPassengerList() {
		// 查询 数据库 如果数据中有此数据，则至为true
		DBUtils dbUitls = new DBUtils(EditMyPassengerActivity.this);
		SQLiteDatabase db = dbUitls.openReadOnlyDatabase();
		PassengerDao dao = new PassengerDaoImpl();

		passengerLocationList = dao.findPassengersByUserId(db,
				app.getU());
	}

	/**
	 * 修改本地数据库信息
	 */
	private void updatePassengerList(String name, String code) {
		// 查询 数据库 如果数据中有此数据，则至为true
		DBUtils dbUitls = new DBUtils(EditMyPassengerActivity.this);
		SQLiteDatabase db = dbUitls.openReadWriteDatabase();
		PassengerDao dao = new PassengerDaoImpl();
		dao.updatePassengersByUserIdandId(db, app.getU(), editIndex,
				name, code);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_add_dengji_person);
		app = (TravelApplication) getApplication();
		setupView();
		bankCardNumAddSpace(etCode);
	}

	private void setupView() {
		Intent intent = getIntent();
		titleName = intent.getStringExtra("titleName");
		codeStr = intent.getStringExtra("CODE");// 18位没有空格
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
			if (Utils.limitCardId(this, codeYanzheng)) {
				initParams();
				loadingData();
			}
		}

	}

	private void initParams() {
		params = new HashMap<String, String>();
		params.put("bid", editIndex);
		params.put("name", name);
		params.put("cardId", code);
		editPassengerUrl = getString(R.string.gionee_host)
				+ "/GioneeTrip/tripAction_updateBoarderInfo.action";
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

	/**
	 * 身份证加空格
	 * 
	 * @param mEditText
	 */
	protected void bankCardNumAddSpace(final EditText mEditText) {
		mEditText.addTextChangedListener(new TextWatcher() {
			int beforeTextLength = 0;
			int onTextLength = 0;
			boolean isChanged = false;

			int location = 0;// 记录光标的位置
			private char[] tempChar;
			private StringBuffer buffer = new StringBuffer();
			int konggeNumberB = 0;

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				beforeTextLength = s.length();
				if (buffer.length() > 0) {
					buffer.delete(0, buffer.length());
				}
				konggeNumberB = 0;
				for (int i = 0; i < s.length(); i++) {
					if (s.charAt(i) == ' ') {
						konggeNumberB++;
					}
				}
			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				onTextLength = s.length();
				buffer.append(s.toString());
				if (onTextLength == beforeTextLength || onTextLength <= 3
						|| isChanged) {
					isChanged = false;
					return;
				}
				isChanged = true;
			}

			@Override
			public void afterTextChanged(Editable s) {
				if (isChanged) {
					location = mEditText.getSelectionEnd();
					int index = 0;
					while (index < buffer.length()) {
						if (buffer.charAt(index) == ' ') {
							buffer.deleteCharAt(index);
						} else {
							index++;
						}
					}

					index = 0;
					int konggeNumberC = 0;
					while (index < buffer.length()) {
						if ((index == 6 || index == 15)) {
							buffer.insert(index, ' ');
							konggeNumberC++;
						}
						index++;
					}

					if (konggeNumberC > konggeNumberB) {
						location += (konggeNumberC - konggeNumberB);
					}

					tempChar = new char[buffer.length()];
					buffer.getChars(0, buffer.length(), tempChar, 0);
					String str = buffer.toString();
					if (location > str.length()) {
						location = str.length();
					} else if (location < 0) {
						location = 0;
					}

					mEditText.setText(str);
					Editable etable = mEditText.getText();
					Selection.setSelection(etable, location);
					isChanged = false;
				}
			}

		});
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