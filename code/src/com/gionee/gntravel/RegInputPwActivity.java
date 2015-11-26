package com.gionee.gntravel;

import java.net.URLEncoder;
import java.util.HashMap;

import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.gionee.gntravel.customview.CustomProgressDialog;
import com.gionee.gntravel.entity.ValidateLogin;
import com.gionee.gntravel.entity.ValidatePassword;
import com.gionee.gntravel.utils.FinalString;
import com.gionee.gntravel.utils.HttpConnCallback;
import com.gionee.gntravel.utils.HttpConnUtil4Gionee;
import com.gionee.gntravel.utils.JSONUtil;
import com.gionee.gntravel.utils.NetWorkUtil;
import com.gionee.gntravel.utils.Utils;

public class RegInputPwActivity extends Activity implements OnClickListener,
		HttpConnCallback {
	private CustomProgressDialog progressDialog;
	private TextView tvTitle;// 标题名称
	private TextView tvId;// 帐号
	private EditText etPassword;// mima
	private Button btnRegister;
	private String id;// 手机号（开始是带有空格的）
	private String passWord;// 输入的密码
	private ImageView imgMingorAn;// 控制输入是否为明文或者暗文
	private int statusImg = 1;// 1 为暗文 2为明文
	private String sesson;
	private HttpConnUtil4Gionee task;
	private TravelApplication app;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_register_input_pw);
		setupView();
		RegisterActivity.smsRegFlag = false;
		app = (TravelApplication) getApplication();
	}

	private void setupView() {
		Intent intent = getIntent();
		sesson = intent.getStringExtra("S");// 验证密码时候用的
		tvTitle = (TextView) findViewById(R.id.tv_title);
		tvTitle.setText(R.string.inputpw);
		tvTitle.setOnClickListener(this);
		tvId = (TextView) findViewById(R.id.tv_reg_input_pw_id);
		id = intent.getStringExtra("Id");
		tvId.setText(id);
		etPassword = (EditText) findViewById(R.id.et_reg_password);
		imgMingorAn = (ImageView) findViewById(R.id.img_reg_ming_or_an);
		imgMingorAn.setOnClickListener(this);
		btnRegister = (Button) findViewById(R.id.btn_register_input);
		btnRegister.setOnClickListener(this);
	}

	private void validatePassword() {
		startProgressDialog();
		if (!NetWorkUtil.isNetworkConnected(this)) {
			// 无网络连接 提示
			Toast.makeText(this, R.string.no_network, Toast.LENGTH_LONG).show();
			stopProgressDialog();
			return;
		}
		ValidatePassword validatePasswordRequest = new ValidatePassword();
		validatePasswordRequest.setP(passWord);
		validatePasswordRequest.setS(sesson);
		String host = getString(R.string.gionee_host)
				+ FinalString.REGISTER_INPUT_PASS_URL;
		HashMap<String, String> params = new HashMap<String, String>();
		params.put(
				"json",
				URLEncoder.encode(JSONUtil.getInstance().toJSON(
						validatePasswordRequest)));
		task = new HttpConnUtil4Gionee(this);
		task.execute(host, params, HttpConnUtil4Gionee.HttpMethod.GET);
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

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.tv_title:
			this.finish();
			break;
		case R.id.btn_register_input:
			passWord = etPassword.getText().toString().trim();
			if (TextUtils.isEmpty(passWord)) {
				Toast.makeText(this, R.string.pw_not_null, Toast.LENGTH_LONG)
						.show();
			} else if (passWord.length() < 4 || passWord.length() > 16) {
				Toast.makeText(this, R.string.passwordguize, Toast.LENGTH_SHORT).show();
			}else if (!Utils.isPassword(passWord)) {
				Toast.makeText(this, R.string.passwordguize, Toast.LENGTH_LONG)
						.show();
			} else {
				// 服务器验证
				validatePassword();
			}
			break;
		case R.id.img_reg_ming_or_an:
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
		if (TextUtils.isEmpty(result)) {
			Toast.makeText(this, R.string.network_timeout, Toast.LENGTH_SHORT)
					.show();
			return;
		}
		try {
			JSONObject all = new JSONObject(result);
			if (!FinalString.ERRORCODE.equals(all.getString("errorCode"))) {
				Toast.makeText(this, R.string.network_timeout,
						Toast.LENGTH_SHORT).show();
				return;
			}
			String resultStr = all.getString("r");
			if ("00003".equals(resultStr)) {
				Toast.makeText(this, R.string.reg_suc, Toast.LENGTH_SHORT)
						.show();
				app.getUserInfo_sp().putString("userName", id);
				autoLogin();
			} else if ("00004".equals(resultStr)) {
				String userId = all.getString("userId");
				String u = all.getString("u");
				
//				String userId = all.getString("u");
				app.getUserInfo_sp().putBoolean("userState", true);// 登录成功
				app.getUserInfo_sp().putString("userId", userId);
				app.getUserInfo_sp().putString("u", u);
				
				app.setLoginState(true);// 变成true
				Toast.makeText(this, R.string.login_suc, Toast.LENGTH_SHORT)
						.show();
				setResult(20);
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
