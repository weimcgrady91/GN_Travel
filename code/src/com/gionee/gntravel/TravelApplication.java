package com.gionee.gntravel;

import java.io.File;

import android.app.Application;
import android.content.Context;
import android.os.Environment;
import android.telephony.TelephonyManager;
import android.text.TextUtils;

import com.baidu.location.BDLocation;
import com.baidu.mapapi.SDKInitializer;
import com.gionee.gntravel.utils.SharedPreferenceUtils;
import com.youju.statistics.YouJuAgent;

public class TravelApplication extends Application {

	public static final String PATH = Environment.getExternalStorageDirectory()
			.toString() + "/GN_TRAVEL";
	public static final int FORGET_PW_THIRD = 1003;
	public static final int FORGET_PW_SECOND = 1002;
	public static final int FORGET_PW_ONE = 1001;

	private boolean loginState;
	private boolean showImgWithWifiState;
	private boolean visitLocationState;
	private boolean travelWarnState;
	private String takeOffDate;
	private SharedPreferenceUtils default_sp;
	private SharedPreferenceUtils userInfo_sp;
	private SharedPreferenceUtils config_sp;
	private BDLocation location;
	private String departcity;
	private String arrivecity;
	private String departCityCode;
	private String arriveCityCode;
	private String locationCity;

	public String getLocationCity() {
		return locationCity;
	}

	public void setLocationCity(String locationCity) {
		this.locationCity = locationCity;
	}

	public String getDepartcity() {
		return departcity;
	}

	public void setDepartcity(String departcity) {
		this.departcity = departcity;
	}

	public String getArrivecity() {
		return arrivecity;
	}

	public void setArrivecity(String arrivecity) {
		this.arrivecity = arrivecity;
	}

	public BDLocation getLocation() {
		return location;
	}

	public void setLocation(BDLocation location) {
		this.location = location;
	}

	public boolean isTravelWarnState() {
		return travelWarnState;
	}

	public void setTravelWarnState(boolean travelWarnState) {
		this.travelWarnState = travelWarnState;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		initYouju();
		createFileDir();
		initSharedPreference();
		initUserState();
		initVisitLocationState();
		initShowImgWithWifiState();
		initShowImgWithRravelState();
	}

	public void initYouju(){
		SDKInitializer.initialize(this);
		YouJuAgent.init(this);
	}
	
	/*
	 * 创建文件夹用于存放设置的头像图片
	 */
	private void createFileDir() {
		File file = new File(PATH);
		if (!file.exists()) {
			file.mkdirs();
		}
	}

	public void initSharedPreference() {
		default_sp = new SharedPreferenceUtils(this, "default_sp");
		userInfo_sp = new SharedPreferenceUtils(this, "userInfo_sp");
		config_sp = new SharedPreferenceUtils(this, "config_sp");
	}

	public void initUserState() {
		if (userInfo_sp.getBoolean("userState")) {
			setLoginState(true);
		}
	}

	public void initShowImgWithWifiState() {
		if (config_sp.getBoolean("showImgWithWifiState")) {

			setShowImgWithWifiState(true);
		}
	}

	public void initVisitLocationState() {
		if (!config_sp.getBoolean("visitLocationState")) {
			setVisitLocationState(true);
		}
	}

	public void initShowImgWithRravelState() {
		if (config_sp.getBoolean("showImgWithTravelState")) {
			setShowImgWithWifiState(true);
		}
	}
	
	public boolean isLoginState() {
		return loginState;
	}

	public void setLoginState(boolean loginState) {
		this.loginState = loginState;
	}

	public boolean isShowImgWithWifiState() {
		return showImgWithWifiState;
	}

	public void setShowImgWithWifiState(boolean showImgWithWifiState) {
		this.showImgWithWifiState = showImgWithWifiState;
	}

	public boolean isVisitLocationState() {
		return visitLocationState;
	}

	public void setVisitLocationState(boolean visitLocationState) {
		this.visitLocationState = visitLocationState;
	}

	public String getTripName() {
		String lastTripName = config_sp.getString("lastTripName");
		if (TextUtils.isEmpty(lastTripName)) {
			return null;
		}
		return lastTripName;
	}

	public void setTripName(String tripName) {
		config_sp.putString("lastTripName", tripName);
	}

	public SharedPreferenceUtils getDefault_sp() {
		return default_sp;
	}

	public void setDefault_sp(SharedPreferenceUtils default_sp) {
		this.default_sp = default_sp;
	}

	public SharedPreferenceUtils getUserInfo_sp() {
		return userInfo_sp;
	}

	public void setUserInfo_sp(SharedPreferenceUtils userInfo_sp) {
		this.userInfo_sp = userInfo_sp;
	}

	public SharedPreferenceUtils getConfig_sp() {
		return config_sp;
	}

	public void setConfig_sp(SharedPreferenceUtils config_sp) {
		this.config_sp = config_sp;
	}

	/**
	 * 判断当前状态得到相对应的用户名 登录状态取真正的用户名,非登录状态取imei号当作用户名
	 * 
	 * @return 用户名
	 */
	public String getUserId() {
		if (isLoginState()) {
			return getUserInfo_sp().getString("userId");
		} else {
			return "";
		}
	}

	public String getU() {
		if (isLoginState()) {
			return getUserInfo_sp().getString("u");
		} else {
			TelephonyManager tm = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
			return tm.getSimSerialNumber();
		}
	}
	
	public String getDepartCityCode() {
		return departCityCode;
	}

	public void setDepartCityCode(String departCityCode) {
		this.departCityCode = departCityCode;
	}

	public String getArriveCityCode() {
		return arriveCityCode;
	}

	public void setArriveCityCode(String arriveCityCode) {
		this.arriveCityCode = arriveCityCode;
	}

	public String getTakeOffDate() {
		return takeOffDate;
	}

	public void setTakeOffDate(String takeOffDate) {
		this.takeOffDate = takeOffDate;
	}

	public boolean getTripState() {
		if (config_sp.getBoolean("tripState")) {
			return true;
		} else {
			return false;
		}
	}

	public void setTripState(boolean state) {
		config_sp.putBoolean("tripState", state);
	}

	public void replaceTripName(String newDateStr) {
		if (TextUtils.isEmpty(getTripName())) {
			return;
		} else {
			if (getTripState()) {
				String oldTripName = getTripName();
				String oldPath = oldTripName
						.substring(oldTripName.indexOf("T"));
				String newTripName = newDateStr + oldPath;
				setTripName(newTripName);
			}
		}
	}

}
