package com.gionee.gntravel;

import java.net.URLEncoder;
import java.util.HashMap;

import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.gionee.gntravel.customview.CustomProgressDialog;
import com.gionee.gntravel.entity.ResetNewPassword;
import com.gionee.gntravel.entity.ValidateLogin;
import com.gionee.gntravel.utils.FinalString;
import com.gionee.gntravel.utils.HttpConnCallback;
import com.gionee.gntravel.utils.HttpConnUtil4Gionee;
import com.gionee.gntravel.utils.JSONUtil;
import com.gionee.gntravel.utils.NetWorkUtil;
import com.gionee.gntravel.utils.Utils;

public class ForgetPwActivityFinish extends Activity implements
		OnClickListener, HttpConnCallback {
	private TextView tvTitle;// 标题名称
	private CustomProgressDialog progressDialog;
	private EditText etPassword;// mima
	private Button btnForgetFinish;
	private String id;// 手机号（开始是带有空格的）
	private String passWord;// 输入的密码
	private String s;
	private ImageView imgMingorAn;// 控制输入是否为明文或者暗文
	private int statusImg = 1;// 1 为暗文 2为明文
	private HttpConnUtil4Gionee task;
	private TravelApplication app;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_forget_finish);
		setupView();
		app = (TravelApplication) getApplication();
		ForgetPwActivitySecond.smsFlag = false;
	}

	private void setupView() {
		Intent intent = getIntent();
		id = intent.getStringExtra("IdForget");
		s = intent.getStringExtra("S");
		tvTitle = (TextView) findViewById(R.id.tv_title);
		tvTitle.setText(R.string.inputpw);
		tvTitle.setOnClickListener(this);
		etPassword = (EditText) findViewById(R.id.et_forget_password);
		imgMingorAn = (ImageView) findViewById(R.id.img_forget_ming_or_an);
		imgMingorAn.setOnClickListener(this);
		btnForgetFinish = (Button) findViewById(R.id.btn_forget_finish);
		btnForgetFinish.setOnClickListener(this);
	}

	private void SetPw() {// 设置密码,最后一步
		ResetNewPassword resetNewPwRequest = new ResetNewPassword();
		resetNewPwRequest.setP(passWord);
		resetNewPwRequest.setS(s);
		task = new HttpConnUtil4Gionee(this);
		String host = getString(R.string.gionee_host)
				+ FinalString.RESET_SET_NEWPASS_URL;
		HashMap<String, String> params = new HashMap<String, String>();
		params.put(
				"json",
				URLEncoder.encode(JSONUtil.getInstance().toJSON(
						resetNewPwRequest)));
		task.execute(host, params, HttpConnUtil4Gionee.HttpMethod.GET);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.tv_title:
			this.finish();
			break;
		case R.id.btn_forget_finish:
			passWord = etPassword.getText().toString().trim();
			if (passWord.length() < 4 || passWord.length() > 16) {
				Toast.makeText(this, R.string.passwordguize, Toast.LENGTH_SHORT)
						.show();
			}else if (!Utils.isPassword(passWord)) {
				Toast.makeText(this, R.string.passwordguize, Toast.LENGTH_LONG).show();
			} else {
				startProgressDialog();
				if (!NetWorkUtil.isNetworkConnected(this)) {
					// 无网络连接 提示
					Toast.makeText(this, R.string.no_network, Toast.LENGTH_LONG)
							.show();
					stopProgressDialog();
					return;
				}
				SetPw();
			}
			break;
		case R.id.img_forget_ming_or_an:
			int length;
			switch (statusImg) {
			case 1:
				// 输入密码 是暗文
				imgMingorAn.setBackgroundResource(R.drawable.ming_code);
				statusImg = 2;
				etPassword.setInputType(InputType.TYPE_CLASS_TEXT);
				length = etPassword.getText().toString().length();
				etPassword.setSelection(length);
				break;

			case 2:
				imgMingorAn.setBackgroundResource(R.drawable.an_code);
				statusImg = 1;
				etPassword.setInputType(InputType.TYPE_CLASS_TEXT
						| InputType.TYPE_TEXT_VARIATION_PASSWORD);
				length = etPassword.getText().toString().length();
				etPassword.setSelection(length);
				break;
			}

			break;
		}
	}

	@Override
	public void execute(String result) {
		stopProgressDialog();
		getResetNewPwResult(result);
	}

	private void autoLogin() {

		// 注册成功之后,自动登录
		ValidateLogin validateLoginRequest = new ValidateLogin();
		validateLoginRequest.setP(passWord);
		validateLoginRequest.setTn(id);
		HashMap<String, String> params = new HashMap<String, String>();
		params.put(
				"json",
				URLEncoder.encode(JSONUtil.getInstance().toJSON(
						validateLoginRequest)));
		task = new HttpConnUtil4Gionee(this);
		String host = getString(R.string.gionee_host) + FinalString.LOGIN_URL;
		task.execute(host, params, HttpConnUtil4Gionee.HttpMethod.GET);
	}

	private void getResetNewPwResult(String result) {
		if (TextUtils.isEmpty(result)) {
			Toast.makeText(this, R.string.network_timeout, 1).show();
			return;
		}
		try {
			JSONObject all = new JSONObject(result);
			if (!FinalString.ERRORCODE.equals(all.getString("errorCode"))) {
				Toast.makeText(this, R.string.service_error, 1).show();
				return;
			}
			String resultStr = all.getString("r");
			if ("00007".equals(resultStr)) {
				Toast.makeText(this, R.string.reset_pw_suc, Toast.LENGTH_LONG)
						.show();
				app.getUserInfo_sp().putString("userName", id);
				autoLogin();
			} else if ("00004".equals(resultStr)) {
//				String userId = all.getString("u");
				String userId = all.getString("userId");
				String u = all.getString("u");
				app.getUserInfo_sp().putString("u", u);
				app.getUserInfo_sp().putString("userId", userId);
				app.getUserInfo_sp().putBoolean("userState", true);// 登录成功
				app.setLoginState(true);
				
				// 变成true
				setResult(TravelApplication.FORGET_PW_THIRD);
				this.finish();
			} else if ("1110".equals(resultStr)) {
				Toast.makeText(this, R.string.auto_login_error,
						Toast.LENGTH_LONG).show();
				setResult(TravelApplication.FORGET_PW_THIRD);
				this.finish();
			} else {
				Utils.MyToast(this, resultStr);
			}

		} catch (Exception e) {
			e.printStackTrace();
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
}
