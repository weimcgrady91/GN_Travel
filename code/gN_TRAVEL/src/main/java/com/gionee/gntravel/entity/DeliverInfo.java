package com.gionee.gntravel.entity;

import java.io.Serializable;

import android.text.TextUtils;

public class DeliverInfo implements Serializable {
	private String deliveryType;//配送方式：PJS 邮寄行程单,PJN 不需要
//	private String sendTicketCityID;//送票城市ID
//	private String orderRemark;//订单备注
	private String receiver;//收件人 
	private String receiverPhone;
//	private String province;//省 
//	private String city;//市 
//	private String canton;//区 
	private String address;//详细地址 
	private String postCode;//邮编 
	public String getDeliveryType() {
		return deliveryType;
	}
	public void setDeliveryType(String deliveryType) {
		this.deliveryType = deliveryType;
	}
	public String getReceiver() {
		return receiver;
	}
	public void setReceiver(String receiver) {
		this.receiver = receiver;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public String getPostCode() {
		return postCode;
	}
	public void setPostCode(String postCode) {
		this.postCode = postCode;
	}

	public boolean contentIsValid() {
		if(TextUtils.isEmpty(receiver) || TextUtils.isEmpty(address) || TextUtils.isEmpty(postCode)) {
			return false;
		} 
		return true;
		
	}
	@Override
	public String toString() {
		return "deliveryType="+deliveryType+",receiver=" + receiver +",address=" + address + ",postCode=" + postCode + ",receiverPhone="+ receiverPhone;
	}
	public String getReceiverPhone() {
		return receiverPhone;
	}
	public void setReceiverPhone(String receiverPhone) {
		this.receiverPhone = receiverPhone;
	}
	
	
	
}
