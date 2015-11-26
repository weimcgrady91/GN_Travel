package com.gionee.gntravel;

import java.net.URLEncoder;
import java.util.HashMap;

import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.gionee.gntravel.customview.CustomProgressDialog;
import com.gionee.gntravel.entity.ValidateLogin;
import com.gionee.gntravel.utils.FinalString;
import com.gionee.gntravel.utils.HttpConnCallback;
import com.gionee.gntravel.utils.HttpConnUtil4Gionee;
import com.gionee.gntravel.utils.JSONUtil;
import com.gionee.gntravel.utils.NetWorkUtil;
import com.gionee.gntravel.utils.Utils;

/**
 * 
 * @author lijinbao 登陆界面
 */
public class LoginActivity extends Activity implements OnClickListener,
		HttpConnCallback {

	private TextView tvTitle;// 标题名称
	private EditText etId;// 帐号
	private EditText etPassword;// mima
	private CustomProgressDialog progressDialog;
	private Button btnLogin;// 登录
	private Button btnForgetPw;// 忘记密码
	private Button btnNOHuiyuanLogin;// 非会员直接登陆 或者是 订单查询
	private String id;// 手机号（开始是带有空格的）
	private String passWord;// 输入的密码
	private Button btnFuntion;
	private RelativeLayout rl_loading;
	private ImageView img_loading;
	private HttpConnUtil4Gionee task;
	private String target;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		app = (TravelApplication) getApplication();
		setupView();
	}

	private void setupView() {
		Intent intent = getIntent();
		target = intent.getStringExtra("target");
		btnNOHuiyuanLogin = (Button) findViewById(R.id.btn_guest_login);
		btnNOHuiyuanLogin.setOnClickListener(this);
		btnLogin = (Button) findViewById(R.id.btn_login);
		btnNoVipLoginStyle();
		id = app.getUserInfo_sp().getString("userName");
		id = Utils.getId(id);
		tvTitle = (TextView) findViewById(R.id.tv_title);
		tvTitle.setText(R.string.login);
		tvTitle.setOnClickListener(this);
		btnFuntion = (Button) findViewById(R.id.btn_login_reg);
		btnFuntion.setOnClickListener(this);
		etId = (EditText) findViewById(R.id.et_id);
		etId.setText(id);
		etPassword = (EditText) findViewById(R.id.et_password);
		btnLogin.setOnClickListener(this);
		btnForgetPw = (Button) findViewById(R.id.btn_login_forget_pw);
		btnForgetPw.setOnClickListener(this);
		Utils.mobileAddSpace(13, etId);
		etId.setSelection(etId.getText().length());

	}
	private void btnNoVipLoginStyle(){
		if ("com.gionee.gntravel.FlightOrderActivity".equals(target)
				|| "com.gionee.gntravel.HotelOrderActivity".equals(target)) {
			if (TextUtils.isEmpty(app.getU())) {// 如果不获取手机号，是不能显示非会员登录的
				btnNOHuiyuanLogin.setVisibility(View.GONE);
			} else {
				btnNOHuiyuanLoginStyle();
			}
		} else if ("com.gionee.gntravel.MyOrderActivity".equals(target)) {
			if (TextUtils.isEmpty(app.getU())) {
				btnNOHuiyuanLogin.setVisibility(View.GONE);
			} else {
				btnNOHuiyuanLoginStyle();
				btnNOHuiyuanLogin.setText(getString(R.string.no_vip));
			}
		} else {
			btnNOHuiyuanLogin.setVisibility(View.GONE);

		}
	}
	/*
	 * 此按钮的样式 （背景 字颜色。。。）
	 */
	private void btnNOHuiyuanLoginStyle() {
		btnNOHuiyuanLogin.setVisibility(View.VISIBLE);
		btnLogin.setBackgroundResource(R.drawable.white_btn_selector);
		btnLogin.setTextColor(getResources().getColor(R.color.text_color_v4));
		btnNOHuiyuanLogin.setBackgroundResource(R.drawable.red_btn_selector);
		btnNOHuiyuanLogin.setTextColor(getResources().getColor(R.color.text_color_v1));
	}

	private void validateLogin() {
		startProgressDialog();
		if (!NetWorkUtil.isNetworkConnected(this)) {
			// 无网络连接 提示
			Toast.makeText(this, R.string.no_network, Toast.LENGTH_LONG).show();
			stopProgressDialog();
			return;
		}
		ValidateLogin validateLoginRequest = new ValidateLogin();
		validateLoginRequest.setP(passWord);
		validateLoginRequest.setTn(id);
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("json",URLEncoder.encode(JSONUtil.getInstance().toJSON(validateLoginRequest)));
		task = new HttpConnUtil4Gionee(this);
		String host = getString(R.string.gionee_host) + FinalString.LOGIN_URL;
		task.execute(host, params, HttpConnUtil4Gionee.HttpMethod.GET);

	}

	@Override
	public void onClick(View v) {
		Intent intent = null;
		switch (v.getId()) {
		case R.id.tv_title:
			this.finish();
			break;
		case R.id.btn_login:
			id = etId.getText().toString().trim();
			id = id.replaceAll(" ", "");// 去掉空格
			passWord = etPassword.getText().toString().trim();
			if (TextUtils.isEmpty(id)) {
				Toast.makeText(this, R.string.id_not_null, Toast.LENGTH_SHORT).show();
			} else if ((!Utils.isMobile(id))) {
				Toast.makeText(this, R.string.id_not_guize, Toast.LENGTH_SHORT).show();
			} else if (TextUtils.isEmpty(passWord)) {
				Toast.makeText(this, R.string.pw_not_null, Toast.LENGTH_SHORT).show();
			} 
			 else if (passWord.length() < 4 || passWord.length() > 16) {
			 Toast.makeText(this, R.string.passwordguize, Toast.LENGTH_SHORT)
			 .show();
			 }
			else {// 服务器进行验证
				validateLogin();
			}
			break;
		case R.id.btn_login_forget_pw:
			intent = new Intent(this, ForgetPwActivity.class);
			intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
			startActivityForResult(intent, 0);
			break;
		case R.id.btn_login_reg:
			intent = new Intent(this, RegisterActivity.class);
			intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
			startActivityForResult(intent, 0);
			break;
		case R.id.btn_guest_login:
			// 1 预定酒店和飞机票
			if ("com.gionee.gntravel.FlightOrderActivity".equals(target)
					|| "com.gionee.gntravel.HotelOrderActivity".equals(target)) {
				guestLogin();
				// 2 查询订单详情
			} else if ("com.gionee.gntravel.MyOrderActivity".equals(target)) {
				selectOrderForm();
			}
			break;
		default:
			break;
		}

	}

	TextWatcher mTextWatcher = new TextWatcher() {

		@Override
		public void onTextChanged(CharSequence s, int start, int before,
				int count) {
		}

		@Override
		public void beforeTextChanged(CharSequence s, int start, int count,
				int after) {
		}

		@Override
		public void afterTextChanged(Editable s) {
			if (s.length() == 13) {
				if ((etPassword.getText().toString().trim().length() > 3)
						&& (etPassword.getText().toString().trim().length() < 17)) {

					btnLogin.setEnabled(true);
					btnLogin.setBackgroundResource(R.drawable.red_btn_selector);
				}
			} else {
				btnLogin.setEnabled(false);
				btnLogin.setBackgroundResource(R.drawable.register_up);
			}
		}
	};
	/**
	 * 只有输入的位数合适 才能把登录按钮变成高亮。密码按钮监测
	 */
	TextWatcher mTextWatcher2 = new TextWatcher() {

		@Override
		public void onTextChanged(CharSequence s, int start, int before,
				int count) {
		}

		@Override
		public void beforeTextChanged(CharSequence s, int start, int count,
				int after) {
		}

		@Override
		public void afterTextChanged(Editable s) {
			if (s.length() > 3 && s.length() < 17) {
				if ((etId.getText().toString().trim().length() == 13)) {

					btnLogin.setEnabled(true);
					btnLogin.setBackgroundResource(R.drawable.red_btn_selector);
				}
			} else {
				btnLogin.setEnabled(false);
				btnLogin.setBackgroundResource(R.drawable.register_up);
			}
		}
	};
	private TravelApplication app;

	@Override
	public void execute(String result) {
		stopProgressDialog();
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
			if ("00004".equals(resultStr)) {// 登录成功
				
				String u = all.getString("u");
				String userId = all.getString("userId");
				
				app.getUserInfo_sp().putString("u", u);
				app.getUserInfo_sp().putString("userId", userId);
				
				app.getUserInfo_sp().putBoolean("userState", true);
				app.getUserInfo_sp().putString("userName", id);
				app.setLoginState(true);
				
				Intent intent = getIntent();
				Bundle bundle = intent.getExtras();
				if (bundle != null) {
					String target = bundle.getString("target");
					intent.setClassName(getApplicationContext(), target);
					startActivity(intent);
				}
				sendLoginBroadcast();
				this.finish();

			} else if ("1110".equals(resultStr)) {
				Toast.makeText(this, R.string.pw_error, Toast.LENGTH_LONG)
						.show();
			} else {// 该帐号没有注册，可以给用户发送验证码
				Utils.MyToast(this, resultStr);
			}
		} catch (Exception e) {
			Toast.makeText(this, R.string.service_error, Toast.LENGTH_LONG)
					.show();
		}

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == TravelApplication.FORGET_PW_ONE) {

			this.finish();
		}
		super.onActivityResult(requestCode, resultCode, data);
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

	public void guestLogin() {
		Intent intent = getIntent();
		Bundle bundle = intent.getExtras();
		if (bundle != null) {
			String target = bundle.getString("target");
			intent.setClassName(getApplicationContext(), target);
			startActivity(intent);
			finish();
		} else {
			finish();
		}
	}

	/**
	 * 非会员订单查询跳转
	 */
	public void selectOrderForm() {
		Intent intent = getIntent();
		Bundle bundle = intent.getExtras();
		if (bundle != null) {
			String target = bundle.getString("target");
			intent.setClassName(getApplicationContext(), target);
			startActivity(intent);
			finish();
		} else {
			finish();
		}
	}

	public void sendLoginBroadcast() {
		Intent intent = new Intent(FinalString.USER_LOGIN_ACTION);
		sendBroadcast(intent);
	}
}