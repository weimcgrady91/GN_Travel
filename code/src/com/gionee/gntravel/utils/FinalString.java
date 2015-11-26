package com.gionee.gntravel.utils;

public interface FinalString {
	public static final String REFRESH_TRIP_ACTION = "com.gionee.gntravel.REFRESH_TRIP";
	public static final String UPDATE_WIDGET = "com.gionee.gntravel.UPDATEWIDGET";
	public static final String UPDATE_WIDGET_NEWS = "com.gionee.gntravel.UPDATEWIDGET_NEWS";
	public static final String NEWS_URL = "http://api.sina.cn/sinago/list.json";
	public static final String ERRORCODE = "0";
	public static final String USER_LOGIN_ACTION = "com.gionee.gntravel.USER_LOGIN";
	public static final String USER_CHECKOUT_ACTION = "com.gionee.gntravel.USER_CHECKOUT";
	public static final String WIDGET_SHOW_TRAVEL = "com.gionee.gntravel.WIDGET_SHOW_TRAVEL";
	public static final String WIDGET_SHOW_NEWS = "com.gionee.gntravel.WIDGET_SHOW_NEWS";
	public static final String WIDGET_SHOW_STOCK = "com.gionee.gntravel.WIDGET_SHOW_STOCK";
	public static final String WIDGET_SHOW_WEATHER = "com.gionee.gntravel.WIDGET_SHOW_WEATHER";
	public static final String WIDGET_SHOW_NEWS_RELOAD = "com.gionee.gntravel.WIDGET_SHOW_NEWS_RELOAD";
	public static final String FLIGHT_INFO_URL = "/GioneeTrip/tripAction_flightInfo.action";

	public static final String DELETE_TRIP_URL = "/GioneeTrip/tripAction_deleteTripDetail.action";
	public static final String RENAME_TRIP_URL = "/GioneeTrip/tripAction_updateTripName.action";
	
	public static final String SUBMIT_FLIGHT_ORDER = "/GioneeTrip/tripAction_flightOrderSave.action";
	public static final String SEND_RID_TO_APS = "/GioneeTrip/accountAction_saveTripPushId.action";
	public static final String FLIGHT_SHOW_URL = "/GioneeTrip/tripAction_payResult.action";
	
	public static final String SIMPLE_TRIP_URL = "/GioneeTrip/tripAction_findAllSimpleTripInfo.action";
	public static final String TRIP_FORM = "/GioneeTrip/tripAction_findAllTripDetailInfo.action";
	
	public static final String ORDERDETAIL_URL = "/GioneeTrip/tripAction_findUserOrderDetaiInfo.action";
	public static final String MYORDER_URL = "/GioneeTrip/tripAction_findUserOrderList.action";
	public static final String LOGIN_URL = "/GioneeTrip/accountAction_accountLogin.action";// 登录接口
	public static final String REGISTER_GET_SMS_URL = "/GioneeTrip/accountAction_getSMSForRegister.action"; // 注册得到短信验证码
	public static final String REGISTER_VALIDATE_SMS_URL = "/GioneeTrip/accountAction_registerBySMSCode.action";// 注册验证短信验证码
	public static final String REGISTER_INPUT_PASS_URL = "/GioneeTrip/accountAction_registerByPass.action"; // 注册输入密码后调用的接口

	public static final String RESET_REFRESH_IMAGE_URL = "/GioneeTrip/accountAction_refreshGVCImage.action";// 重置密码
																											// 刷新图形验证码接口
	public static final String RESET_GET_SMS_URL = "/GioneeTrip/accountAction_passResetReady.action"; // 重置密码
																										// 根据图形验证码获取短信验证码
	public static final String RESET_VALIDATE_SMS_URL = "/GioneeTrip/accountAction_resetBySMSCode.action"; // 重置密码
																											// 根据短信验证码进行下一步请求
	public static final String RESET_REFRUSH_SMS_URL = "/GioneeTrip/accountAction_resetRefCode.action"; // 重置密码
																										// 重新发送短信验证码
	public static final String RESET_SET_NEWPASS_URL = "/GioneeTrip/accountAction_resetNewPassword.action"; // 重置密码
																											// 设置新密码(最后一步)

	public static final String EDIT_REFRUSH_SMS_URL = "/GioneeTrip/accountAction_accountPassModifyByBase64.action";// 修改密码
																				// 接口
	public static final String GET_SMS_BY_GVC_URL = "/GioneeTrip/accountAction_regRegisterByGVC.action";// 注册时根据图形验证码获取短信验证码
	public static final String PASS_RESET_READY = "/GioneeTrip/accountAction_regRegisterByGVC.action";// 忘记密码时 根据图形验证码获取短信验证码
	public static final String TRAIN_STATION_URL = "/GioneeTrip/ticketAction_listTicketsInfo.action";// 火车列表
	public static final String TRAIN_STATION_TABLE_URL = "/GioneeTrip/ticketAction_findTicketDetailByTrainName.action";// 列车时刻表
	public static final String TRAIN_STATION_WEATHER_URL = "/GioneeTrip/ticketAction_getWeatherInfo.action";// 火车天气接口
	public static final int WEB_ERROR = 0x3000;
	public static final int PARSE_ERROR = 0x2000;
	public static final int DATA_FINISH = 0x1000;
	public static final int NET_ERROR = 0x1002;
	public static final int NOT_FOUND = 0x1003;
	public static final int WEBSERVICE_ERROR = 0x1004;
	public static final int MODIFY_SUCC = 0x1005;
	public static final int CLEAR_CACHE_FINISH = 0x1006;
	public  static final int UPDATE = 0x1007;
	public static final int SMS_CODE_FINISH = 0x4000;// 短信验证码
	public static final String TRIP_ALARM_ACTION = "com.goinee.gntravel.TripAlarm";
	public static final String TRAVEL_CACHE_FILE = "unLoginCacheFile";

	// hotel
	public static final int HOTEL_SEARCHKEY_RESCODE = 0x2001;// 酒店关键字查询响应
	public static final int HOTEL_SEARCHKEY_REQCODE = 0x2002;// 酒店关键字查询请求
	public static final int HOTEL_MAP_SEARCHKEY_REQCODE = 0x2003;// 酒店关键字查询请求
	public static final String HOTELDETAILURL = "/GioneeTrip/hotelAction_findHotelByName.action?";// 根据酒店名称查询酒店信息
	public static final String HOTELDETAILURLBYID = "/GioneeTrip/hotelAction_findHotelById.action?";// 根据酒店名称查询酒店信息
	public static final String HOTEL_ACTION = "/GioneeTrip/hotelAction_";// 根据酒店名称查询酒店信息

	public static final String UPDATE_ACTION = "/GioneeTrip/versionAction_check.action";
	
}
