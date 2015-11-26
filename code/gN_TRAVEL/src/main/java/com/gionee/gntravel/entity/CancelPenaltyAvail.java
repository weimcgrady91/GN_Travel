package com.gionee.gntravel.entity;

import java.io.Serializable;

//<!--取消制度 -->
public class CancelPenaltyAvail  implements Serializable{
	private String start;
	private String end;
	private double amount;
	private String currencyCode;
	public String getStart() {
		return start;
	}
	public void setStart(String start) {
		this.start = start;
	}
	public String getEnd() {
		return end;
	}
	public void setEnd(String end) {
		this.end = end;
	}
	public double getAmount() {
		return amount;
	}
	public void setAmount(double amount) {
		this.amount = amount;
	}
	public String getCurrencyCode() {
		return currencyCode;
	}
	public void setCurrencyCode(String currencyCode) {
		this.currencyCode = currencyCode;
	}
	

}
