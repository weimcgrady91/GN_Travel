package com.gionee.gntravel.entity;

import java.util.ArrayList;

/**
 * 
 * @author lijinbao 订单详情实体类
 */
public class OrderFormDetailEntity {
	private String type;
	private String orderStatus;//订单状态
	private String orderPrice;//订单价格
	private String orderCode;//订单编号
	private String airtime;// 年月日
	private String takeOffTime;
	private String arriveTime;
	
	private String airstartTime;// 起飞时间
	private String airstopTime;// 到达时间
	private String airstartAirPortName;// 起飞机场
	private String airstopAirPortName;// 到达机场
	private String airLineName;// 航空公司名称
	private String djName;//登记人姓名  或者是入住人
	private String djId;//登记人身份证号
	private String lxMobile;//联系人手机号
	private String LxName;//联系人姓名
	private String mailName;//邮寄人姓名
	private String mailAddress;//邮寄人地址 
	private String mailCode;//邮寄人邮编
	private String tuiGaiQian;//退改签规则
	private String orderNumber;

	private String hotalName;
	private String hotaltelephone;
	private String hotalLasttime;// 最晚到店时间
	private String hotalroomTypeName;// 房间类型
	private String hotalfromTime;// 从哪天到哪天
	private String hotalToTime;// 从哪天到哪天
	private String hotalOrderDate;// 预定日期
	private String hotalAddress;// 酒店地址
	private String location;// 我的位置
	private String latitude;
	private String longitude;
	
	
	
	


	public String getLatitude() {
		return latitude;
	}

	public void setLatitude(String latitude) {
		this.latitude = latitude;
	}

	public String getLongitude() {
		return longitude;
	}

	public void setLongitude(String longitude) {
		this.longitude = longitude;
	}

	private ArrayList<String> listCitys;
	
	public ArrayList<String> getListCitys() {
		return listCitys;
	}

	public void setListCitys(ArrayList<String> listCitys) {
		this.listCitys = listCitys;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getAirtime() {
		return airtime;
	}

	public void setAirtime(String airtime) {
		this.airtime = airtime;
	}

	public String getAirstartTime() {
		return airstartTime;
	}

	public void setAirstartTime(String airstartTime) {
		this.airstartTime = airstartTime;
	}

	public String getAirstopTime() {
		return airstopTime;
	}

	public void setAirstopTime(String airstopTime) {
		this.airstopTime = airstopTime;
	}

	public String getAirstartAirPortName() {
		return airstartAirPortName;
	}

	public void setAirstartAirPortName(String airstartAirPortName) {
		this.airstartAirPortName = airstartAirPortName;
	}

	public String getAirstopAirPortName() {
		return airstopAirPortName;
	}

	public void setAirstopAirPortName(String airstopAirPortName) {
		this.airstopAirPortName = airstopAirPortName;
	}

	public String getAirLineName() {
		return airLineName;
	}

	public void setAirLineName(String airLineName) {
		this.airLineName = airLineName;
	}

	public String getOrderNumber() {
		return orderNumber;
	}

	public void setOrderNumber(String orderNumber) {
		this.orderNumber = orderNumber;
	}

	public String getHotalName() {
		return hotalName;
	}

	public void setHotalName(String hotalName) {
		this.hotalName = hotalName;
	}

	public String getHotaltelephone() {
		return hotaltelephone;
	}

	public void setHotaltelephone(String hotaltelephone) {
		this.hotaltelephone = hotaltelephone;
	}

	public String getHotalLasttime() {
		return hotalLasttime;
	}

	public void setHotalLasttime(String hotalLasttime) {
		this.hotalLasttime = hotalLasttime;
	}

	public String getHotalroomTypeName() {
		return hotalroomTypeName;
	}

	public void setHotalroomTypeName(String hotalroomTypeName) {
		this.hotalroomTypeName = hotalroomTypeName;
	}

	public String getHotalfromTime() {
		return hotalfromTime;
	}

	public void setHotalfromTime(String hotalfromTime) {
		this.hotalfromTime = hotalfromTime;
	}

	public String getHotalToTime() {
		return hotalToTime;
	}

	public void setHotalToTime(String hotalToTime) {
		this.hotalToTime = hotalToTime;
	}

	
	public String getDjName() {
		return djName;
	}

	public void setDjName(String djName) {
		this.djName = djName;
	}

	public String getDjId() {
		return djId;
	}

	public void setDjId(String djId) {
		this.djId = djId;
	}

	public String getLxMobile() {
		return lxMobile;
	}

	public void setLxMobile(String lxMobile) {
		this.lxMobile = lxMobile;
	}

	public String getLxName() {
		return LxName;
	}

	public void setLxName(String lxName) {
		LxName = lxName;
	}

	public String getMailName() {
		return mailName;
	}

	public void setMailName(String mailName) {
		this.mailName = mailName;
	}

	public String getMailAddress() {
		return mailAddress;
	}

	public void setMailAddress(String mailAddress) {
		this.mailAddress = mailAddress;
	}

	public String getMailCode() {
		return mailCode;
	}

	public void setMailCode(String mailCode) {
		this.mailCode = mailCode;
	}

	public String getTuiGaiQian() {
		return tuiGaiQian;
	}

	public void setTuiGaiQian(String tuiGaiQian) {
		this.tuiGaiQian = tuiGaiQian;
	}

	
	public String getOrderStatus() {
		return orderStatus;
	}

	public void setOrderStatus(String orderStatus) {
		this.orderStatus = orderStatus;
	}

	public String getOrderPrice() {
		return orderPrice;
	}

	public void setOrderPrice(String orderPrice) {
		this.orderPrice = orderPrice;
	}

	public String getOrderCode() {
		return orderCode;
	}

	public void setOrderCode(String orderCode) {
		this.orderCode = orderCode;
	}

	public String getHotalOrderDate() {
		return hotalOrderDate;
	}

	public void setHotalOrderDate(String hotalOrderDate) {
		this.hotalOrderDate = hotalOrderDate;
	}

	public String getHotalAddress() {
		return hotalAddress;
	}

	public void setHotalAddress(String hotalAddress) {
		this.hotalAddress = hotalAddress;
	}

	@Override
	public String toString() {
		return "OrderFormDetailEntity [type=" + type + ", orderStatus="
				+ orderStatus + ", orderPrice=" + orderPrice + ", orderCode="
				+ orderCode + ", airtime=" + airtime + ", airstartTime="
				+ airstartTime + ", airstopTime=" + airstopTime
				+ ", airstartAirPortName=" + airstartAirPortName
				+ ", airstopAirPortName=" + airstopAirPortName
				+ ", airLineName=" + airLineName + ", djName=" + djName
				+ ", djId=" + djId + ", lxMobile=" + lxMobile + ", LxName="
				+ LxName + ", mailName=" + mailName + ", mailAddress="
				+ mailAddress + ", mailCode=" + mailCode + ", tuiGaiQian="
				+ tuiGaiQian + ", orderNumber=" + orderNumber + ", hotalName="
				+ hotalName + ", hotaltelephone=" + hotaltelephone
				+ ", hotalLasttime=" + hotalLasttime + ", hotalroomTypeName="
				+ hotalroomTypeName + ", hotalfromTime=" + hotalfromTime
				+ ", hotalToTime=" + hotalToTime + ", hotalOrderDate="
				+ hotalOrderDate + ", hotalAddress=" + hotalAddress + "]";
	}

	public String getTakeOffTime() {
		return takeOffTime;
	}

	public void setTakeOffTime(String takeOffTime) {
		this.takeOffTime = takeOffTime;
	}

	public String getArriveTime() {
		return arriveTime;
	}

	public void setArriveTime(String arriveTime) {
		this.arriveTime = arriveTime;
	}

	
	
	

}
