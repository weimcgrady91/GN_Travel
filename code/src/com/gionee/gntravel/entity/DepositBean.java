package com.gionee.gntravel.entity;
/**
 * 
 * @Author: yangxy
 * @Create Date: 2014-7-2
 */
public class DepositBean {
	private String bankName; //银行名称
	private String cardNumber; // CardNumber属性：信用卡号；string类型；可空
	private String seriesCode; // SeriesCode属性：串号信用卡背后的3位数字，如没有可以不录入；string类型；可空
	private String effectiveDate; // EffectiveDate属性：生效日期；datetime类型；可空
	private String expireDate; // ExpireDate属性：失效日期；datetime类型；可空
	private String cardHolderName; //CardHolderName：持卡人姓名，string类型；可空
	private String cardHolderIDCard; //持卡人身份证号：，string类型；可空 
	private String amount; //接收到的担保金额
	private String phoneNum; //电话号码
	
	
	public String getPhoneNum() {
		return phoneNum;
	}
	public void setPhoneNum(String phoneNum) {
		this.phoneNum = phoneNum;
	}
	public String getBankName() {
		return bankName;
	}
	public void setBankName(String bankName) {
		this.bankName = bankName;
	}
	public String getCardNumber() {
		return cardNumber;
	}
	public void setCardNumber(String cardNumber) {
		this.cardNumber = cardNumber;
	}
	public String getSeriesCode() {
		return seriesCode;
	}
	public void setSeriesCode(String seriesCode) {
		this.seriesCode = seriesCode;
	}
	public String getEffectiveDate() {
		return effectiveDate;
	}
	public void setEffectiveDate(String effectiveDate) {
		this.effectiveDate = effectiveDate;
	}
	public String getExpireDate() {
		return expireDate;
	}
	public void setExpireDate(String expireDate) {
		this.expireDate = expireDate;
	}
	public String getCardHolderName() {
		return cardHolderName;
	}
	public void setCardHolderName(String cardHolderName) {
		this.cardHolderName = cardHolderName;
	}
	public String getCardHolderIDCard() {
		return cardHolderIDCard;
	}
	public void setCardHolderIDCard(String cardHolderIDCard) {
		this.cardHolderIDCard = cardHolderIDCard;
	}
	public String getAmount() {
		return amount;
	}
	public void setAmount(String amount) {
		this.amount = amount;
	}
	
}
