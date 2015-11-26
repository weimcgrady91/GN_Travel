package com.gionee.gntravel;

import java.util.HashMap;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
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
import com.gionee.gntravel.utils.FinalString;
import com.gionee.gntravel.utils.HttpConnCallback;
import com.gionee.gntravel.utils.HttpConnUtil4Gionee;
import com.gionee.gntravel.utils.NetWorkUtil;
import com.gionee.gntravel.utils.Utils;

/**
 * 
 * @author lijinbao 增加常用旅客
 */
public class AddPassengerActivity extends Activity implements OnClickListener,
		HttpConnCallback {
	private HttpConnUtil4Gionee task;
	private Thread doResultThread;
	private ResultHandler handler = new ResultHandler();
	private Button btnSure;
	private EditText etName;
	private EditText etCode;
	private TextView tvTitle;
	private LinearLayout llWarning;
	private TravelApplication app;
	private String addPassengerlUrl;
	private HashMap<String, String> params;
	String name;
	String code;
	// private Uri uri;
	private Dialog dialog;
	private CustomProgressDialog progressDialog;

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
				showInsertErrorMsg(msg);
				break;
			default:
				break;
			}
		}

	}

	private void showInsertErrorMsg(Message msg) {
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
		Intent intent = new Intent();
		intent.putExtra("addName", name);
		intent.putExtra("addCode", code);
		setResult(20, intent);
		Utils.closeSoftware(this);
		finish();
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_add_dengji_person);
		setupView();
	}

	private void setupView() {
		app = (TravelApplication) getApplication();
		Intent intent = getIntent();
		llWarning = (LinearLayout) findViewById(R.id.ll_warning);
		llWarning.setVisibility(View.VISIBLE);
		tvTitle = (TextView) findViewById(R.id.tv_title);
		tvTitle.setText(intent.getStringExtra("titleName"));
		tvTitle.setOnClickListener(this);
		btnSure = (Button) findViewById(R.id.btn_function);
		btnSure.setText(getString(R.string.ok));
		etName = (EditText) findViewById(R.id.et_add_dengji_name);
		etCode = (EditText) findViewById(R.id.et_add_dengji_code);
		btnSure.setOnClickListener(this);
		bankCardNumAddSpace(etCode);

	}

	private void initParams() {
		params = new HashMap<String, String>();
//		String userId = app.getUserId();
		params.put("name", name);
		params.put("userId", app.getUserId());
		params.put("u", app.getU());
//		params.put("tel", userId);
		params.put("cardId", code);
		addPassengerlUrl = getString(R.string.gionee_host)
				+ "/GioneeTrip/tripAction_saveBoarderInfo.action";
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
		task.execute(addPassengerlUrl, params,
				HttpConnUtil4Gionee.HttpMethod.POST);
	}

	/*
	 * 添加一条数据到数据库中
	 */
	private void addPassenger() {
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

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_function:
			addPassenger();// 添加常用旅客

			break;
		case R.id.tv_title:
			Utils.closeSoftware(this);
			Utils.showDialog(this, new EditText[] { etCode, etName });
			break;
		default:
			break;
		}

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

				Message msg = Message.obtain();
				msg.what = FinalString.DATA_FINISH;
				handler.sendMessage(msg);
			} catch (JSONException e) {
				e.printStackTrace();
			}
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
