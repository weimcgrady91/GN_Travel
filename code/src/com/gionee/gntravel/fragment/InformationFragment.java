package com.gionee.gntravel.fragment;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.gionee.gntravel.ContactsActivity;
import com.gionee.gntravel.InfomationActivity;
import com.gionee.gntravel.LoginActivity;
import com.gionee.gntravel.MessageCenterActivity;
import com.gionee.gntravel.MyOrderActivity;
import com.gionee.gntravel.MyTravelActivity;
import com.gionee.gntravel.PassengerListActivity;
import com.gionee.gntravel.R;
import com.gionee.gntravel.RegisterActivity;
import com.gionee.gntravel.SettingActivity;
import com.gionee.gntravel.TravelApplication;
import com.gionee.gntravel.customview.CustomProgressDialog;
import com.gionee.gntravel.customview.RoundImageView;
import com.gionee.gntravel.entity.SmsEntity;
import com.gionee.gntravel.utils.FinalString;
import com.gionee.gntravel.utils.HttpConnCallback;
import com.gionee.gntravel.utils.HttpConnUtil4Gionee;
import com.gionee.gntravel.utils.SmsUtils;
import com.gionee.gntravel.utils.Utils;
import com.youju.statistics.YouJuAgent;

public class InformationFragment extends ListBaseFragment implements
		View.OnClickListener, HttpConnCallback {
	public static final String LOGIN_SUCCESS = "com.login.success";
	private RoundImageView btnPhoto;
	private ImageView imgNewFlag;
	private TextView tvId;
	private LinearLayout llLoginAndRegister;
	private Button btnLogin;// 登录按钮
	private Button btnRegister;// 注册按钮
	private RelativeLayout rePhoto;
	private String userName = "";// 帐号 是手机号 或者是 昵称
	private String userId = "";
	private TravelApplication app;
	private Dialog dialog;
	private String uploadJson;
	private ResultHandler handler = new ResultHandler();
	private static final int DATA_FINISH = 0x1000;
	private static final int NET_ERROR = 0x1002;
	private static final int UPLOAD_SUCCESS = 0x1003;
	private static final int DATA_ALREADY_EXITS = 0x1004;
	private static final int ERROR_MSG = 0x1005;
	private HttpConnUtil4Gionee task;

	private class ResultHandler extends Handler {

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case DATA_FINISH:
				uploadJson();
				break;
			case NET_ERROR:
				showFailedMsg();
				break;
			case UPLOAD_SUCCESS:
				stopProgressDialog();
				Toast.makeText(getActivity(), "导入成功", 0).show();
				Intent refreshIntent = new Intent(
						FinalString.REFRESH_TRIP_ACTION);
				getActivity().sendBroadcast(refreshIntent);
				break;
			case DATA_ALREADY_EXITS:
				stopProgressDialog();
				Toast.makeText(getActivity(), "没有需要导入的短信", 0).show();
				break;
			case ERROR_MSG:
				stopProgressDialog();
				Toast.makeText(getActivity(), "导入失败", 0).show();
				break;
			default:
				break;
			}
		}
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_information, null);
		app = (TravelApplication) getActivity().getApplication();
		init(view);
		customProgressDialog = CustomProgressDialog.createDialog(getActivity(),
				R.layout.customprogressdialog);
		return view;
	}

	private void init(View view) {
		view.findViewById(R.id.rl_information_myOrder).setOnClickListener(this);
		view.findViewById(R.id.rl_information_center).setOnClickListener(this);
		view.findViewById(R.id.rl_information_traveler)
				.setOnClickListener(this);
		view.findViewById(R.id.rl_information_setting).setOnClickListener(this);
		view.findViewById(R.id.rl_information_contacts)
				.setOnClickListener(this);
		view.findViewById(R.id.rl_information_importTravel).setOnClickListener(
				this);
		llLoginAndRegister = (LinearLayout) view
				.findViewById(R.id.ll_login_register);
		imgNewFlag = (ImageView) view.findViewById(R.id.img_new_flag);
		tvId = (TextView) view.findViewById(R.id.tv_info_id);
		btnLogin = (Button) view.findViewById(R.id.btn_info_login);
		btnLogin.setOnClickListener(this);
		btnRegister = (Button) view.findViewById(R.id.btn_info_register);
		btnRegister.setOnClickListener(this);
		btnPhoto = (RoundImageView) view.findViewById(R.id.img_photo);
		btnPhoto.setOnClickListener(this);
		rePhoto = (RelativeLayout) view.findViewById(R.id.re_photo);
		rePhoto.setOnClickListener(this);
	}

	public void onDestroy() {
		super.onDestroy();
	}

	@Override
	public void onClick(View v) {
		Intent intent;
		switch (v.getId()) {
		case R.id.rl_information_center:
			intent = new Intent(getActivity(), MessageCenterActivity.class);
			startActivity(intent);
			YouJuAgent.onEvent(getActivity(), getActivity().getString(R.string.youju_xiaoxizhongxin));
			break;
		case R.id.rl_information_setting:
			intent = new Intent(getActivity(), SettingActivity.class);
			startActivity(intent);
			YouJuAgent.onEvent(getActivity(), getActivity().getString(R.string.youju_settings));
			break;
		case R.id.img_photo:
			intent = new Intent(getActivity(), InfomationActivity.class);
			intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
			intent.putExtra("infoId", userName);
			startActivity(intent);
			break;
		case R.id.rl_information_traveler:
			intoPassengerListActivity();
			YouJuAgent.onEvent(getActivity(), getActivity().getString(R.string.youju_changyonglvke));
			break;
		case R.id.btn_info_login:// 跳转到登录
			intent = new Intent(getActivity(), LoginActivity.class);
			startActivity(intent);
			YouJuAgent.onEvent(getActivity(), getActivity().getString(R.string.youju_login));
			break;
		case R.id.btn_info_register:// 跳转到注册
			intent = new Intent(getActivity(), RegisterActivity.class);
			startActivity(intent);
			YouJuAgent.onEvent(getActivity(), getActivity().getString(R.string.youju_register));
			break;
		case R.id.rl_information_myOrder:
			startActivityForMyOrder();
			YouJuAgent.onEvent(getActivity(), getActivity().getString(R.string.youju_myorder));
			break;
		case R.id.rl_information_importTravel:
//			importTravelFromSmsDialog();
			break;
		case R.id.rl_information_contacts:// 常用联系人
			intoContactsListActivity();
			YouJuAgent.onEvent(getActivity(), getActivity().getString(R.string.youju_changyonglianxiren));
			break;
		default:
			break;
		}
	}

	/**
	 * 跳入常用登机人列表
	 */
	private void intoPassengerListActivity() {
		Intent intent = null;
		if (!TextUtils.isEmpty(app.getU())) {
			intent = new Intent(getActivity(), PassengerListActivity.class);
		} else {
			intent = new Intent(getActivity(), LoginActivity.class);
			intent.putExtra("target",
					"com.gionee.gntravel.PassengerListActivity");
		}
		startActivity(intent);
	}

	/**
	 * 跳入常用联系人列表
	 */
	private void intoContactsListActivity() {
		Intent intent = null;
		if (!TextUtils.isEmpty(app.getU())) {
			intent = new Intent(getActivity(), ContactsActivity.class);
		} else {
			intent = new Intent(getActivity(), LoginActivity.class);
			intent.putExtra("target", "com.gionee.gntravel.ContactsActivity");
		}
		startActivity(intent);
	}


	@Override
	public void onDestroyView() {
		// getActivity().unregisterReceiver(receiver);
		super.onDestroyView();
	}

	/**
	 * 设置头像
	 */
	private void setPhoto() {
		File file = new File(TravelApplication.PATH + "/" + userId + ".jpg");
		if (file.exists()) {

			Bitmap bitmap = Utils.loadImageFromSDCard(TravelApplication.PATH
					+ "/" + userId + ".jpg");
			if (bitmap != null) {
				btnPhoto.setImageBitmap(bitmap);

			} else {
				btnPhoto.setImageResource(R.drawable.photo_setting);
			}
		} else {

			btnPhoto.setImageResource(R.drawable.photo_setting);
		}
	}

	/**
	 * 判断是以何种方式登录的 (1.金立帐号登录 2.未登录)
	 */
	private void loginSwith() {
		userId = app.getUserInfo_sp().getString("userId");
		userName = app.getUserInfo_sp().getString("userName");
		if (app.getUserInfo_sp().getBoolean("userState")// 用金立自己帐号登录
				&& !TextUtils.isEmpty(userName)) {
			rePhoto.setVisibility(View.VISIBLE);
			llLoginAndRegister.setVisibility(View.GONE);
			// 根据手机号 存入昵称 （键为手机号，值为昵称）
			String userNickName = app.getUserInfo_sp().getString(userName);
			if (!TextUtils.isEmpty(userNickName)) {
				userName = userNickName;
			}
			tvId.setText(userName);
			setPhoto();

		} else {// 为做任何登录
			rePhoto.setVisibility(View.GONE);
			llLoginAndRegister.setVisibility(View.VISIBLE);
			btnPhoto.setImageResource(R.drawable.photo_setting);
		}
	}

	@Override
	public void onResume() {
		loginSwith();
		if (app.getUserInfo_sp().getBoolean("newFlag")) {
			imgNewFlag.setVisibility(View.VISIBLE);
		} else {
			imgNewFlag.setVisibility(View.GONE);
		}
		super.onResume();
	}

	@Override
	public void onStart() {
		super.onStart();
	}

	public void startActivityForMyOrder() {
		TravelApplication app = (TravelApplication) getActivity()
				.getApplication();
		if (app.isLoginState()) {
			Intent intent = new Intent();
			intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
			intent.setClass(getActivity(), MyOrderActivity.class);
			startActivity(intent);
		} else {
			Intent intent = new Intent();
			intent.setClass(getActivity(), LoginActivity.class);
			intent.putExtra("target", "com.gionee.gntravel.MyOrderActivity");
			startActivity(intent);
		}
	}

	public void startActivityForMyTravel() {
		TravelApplication app = (TravelApplication) getActivity()
				.getApplication();
		if (app.isLoginState()) {
			Intent intent = new Intent();
			intent.setClass(getActivity(), MyTravelActivity.class);
			startActivity(intent);
		} else {
			Intent intent = new Intent();
			intent.setClass(getActivity(), LoginActivity.class);
			startActivity(intent);
		}
	}

	private void importTravelFromSms() {
		startProgressDialog();
		Runnable doImportRunnable = new ImportTravelFromSms();
		new Handler().post(doImportRunnable);

	}

	private void importTravelFromSmsDialog() {
		// TravelApplication app = (TravelApplication) getActivity()
		// .getApplication();
		// if (app.isLoginState()) {
		dialog = new Dialog(getActivity(), R.style.CustomProgressDialog);
		View viewDailog = LayoutInflater.from(getActivity()).inflate(
				R.layout.delete_dialog, null);

		RelativeLayout reDeLayout = (RelativeLayout) viewDailog
				.findViewById(R.id.re_dialog);
		ImageView imgCancel = (ImageView) viewDailog
				.findViewById(R.id.img_dialog_title_cha);
		TextView title = (TextView) viewDailog
				.findViewById(R.id.tv_dialog_title_name);
		title.setText(R.string.import_sms);
		TextView titleContent = (TextView) viewDailog
				.findViewById(R.id.tv_dialog_title);
		titleContent.setText(getActivity().getString(R.string.import_title));
		Button btnCalcel = (Button) viewDailog
				.findViewById(R.id.btn_dialog_cancel);
		btnCalcel.setText(getActivity().getString(R.string.cancel));
		Button btnOk = (Button) viewDailog.findViewById(R.id.btn_dialog_sure);
		btnOk.setText(getActivity().getString(R.string.import_btn));
		reDeLayout.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				dialog.dismiss();
			}
		});
		imgCancel.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				dialog.dismiss();
			}
		});

		btnCalcel.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				dialog.dismiss();
			}
		});
		btnOk.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				importTravelFromSms();
				dialog.dismiss();
			}
		});
		dialog.setContentView(viewDailog);
		dialog.getWindow().setLayout(LayoutParams.MATCH_PARENT,
				LayoutParams.MATCH_PARENT);
		dialog.show();
		// } else {
		// Intent intent = new Intent();
		// intent.setClass(getActivity(), LoginActivity.class);
		// startActivity(intent);
		// }

	}

	private SmsUtils smsUtils;

	private class ImportTravelFromSms implements Runnable {

		private ArrayList<SmsEntity> smsList;
		private HashMap<String, ArrayList<SmsEntity>> map;

		@Override
		public void run() {
			smsUtils = new SmsUtils(getActivity());
			smsList = smsUtils.getSmsInPhone();
			if (smsList == null || smsList.size() == 0) {
				Message msg = Message.obtain();
				msg.what = DATA_ALREADY_EXITS;
				handler.sendMessage(msg);
			} else {
				map = new HashMap<String, ArrayList<SmsEntity>>();
				porcessDate(map, smsList);
			}
		}

		private void porcessDate(HashMap<String, ArrayList<SmsEntity>> map,
				ArrayList<SmsEntity> smsList) {
			uploadJson = smsUtils.processDate(map, smsList);
			if (uploadJson == null) {
				Message msg = Message.obtain();
				msg.what = DATA_ALREADY_EXITS;
				handler.sendMessage(msg);
			} else {
				Message msg = Message.obtain();
				msg.what = DATA_FINISH;
				handler.sendMessage(msg);
			}

		}

	}

	@Override
	public void execute(String result) {
		if (TextUtils.isEmpty(result)) {
			Message msg = Message.obtain();
			msg.what = NET_ERROR;
			handler.sendMessage(msg);
			return;
		}
		new Thread(new DoResult(result)).start();
	}

	private class DoResult implements Runnable {
		private String result;

		public DoResult(String result) {
			this.result = result;
		}

		@Override
		public void run() {
			// {"content":"success","errorCode":0,"errorMsg":null}
			try {
				JSONObject responseJson = new JSONObject(result);
				if (!FinalString.ERRORCODE.equals(responseJson
						.getString("errorCode"))) {
					Message msg = Message.obtain();
					msg.what = NET_ERROR;
					handler.sendMessage(msg);
					return;
				}
				if ("success".equals(responseJson.getString("content"))) {
					smsUtils.insertDB();
					Message msg = Message.obtain();
					msg.what = UPLOAD_SUCCESS;
					handler.sendMessage(msg);
				} else {
					Message msg = Message.obtain();
					msg.what = NET_ERROR;
					handler.sendMessage(msg);
				}
			} catch (JSONException e) {
				Log.getStackTraceString(e);
			}

		}

	}

	public void uploadJson() {
		uploadUrl = getActivity().getString(R.string.gionee_host) + "/"
				+ "GioneeTrip/tripAction_getTripOrderInfoFromSMS.action";
		String userName = app.getUserInfo_sp().getString("userName");
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("json", uploadJson);
		params.put("tel", userName);
		task = new HttpConnUtil4Gionee(this);
		task.execute(uploadUrl, params, HttpConnUtil4Gionee.HttpMethod.POST);
	}

	private CustomProgressDialog customProgressDialog;
	private String uploadUrl;

	private void startProgressDialog() {
		customProgressDialog.setMessage("导入短信旅程");
		customProgressDialog.show();
	}

	private void stopProgressDialog() {
		customProgressDialog.dismiss();
	}

	public void showFailedMsg() {
		stopProgressDialog();
		Toast.makeText(getActivity(), "导入失败!", 0).show();
	}

	@Override
	public void loadDate() {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean onKeyBackDown() {
		// TODO Auto-generated method stub
		return false;
	}

}