package com.gionee.gntravel.entity;

import java.io.Serializable;

/**
 * @Author:yangxy
 * @Create 2014-7-3
 */
public class ResGlobalInfo implements Serializable{
	private String guaranteeCode;
	private String guaranteeAmount;
	private String cancelPenaltyStart;
	private String cancelPenaltyEnd;
	private String cancelPenaltyAmount;
	private String amountAfterTax;
	private String resID_Type;
	private String resID_Value;
	public String getGuaranteeCode() {
		return guaranteeCode;
	}
	public void setGuaranteeCode(String guaranteeCode) {
		this.guaranteeCode = guaranteeCode;
	}
	public String getGuaranteeAmount() {
		return guaranteeAmount;
	}
	public void setGuaranteeAmount(String guaranteeAmount) {
		this.guaranteeAmount = guaranteeAmount;
	}
	public String getCancelPenaltyStart() {
		return cancelPenaltyStart;
	}
	public void setCancelPenaltyStart(String cancelPenaltyStart) {
		this.cancelPenaltyStart = cancelPenaltyStart;
	}
	public String getCancelPenaltyEnd() {
		return cancelPenaltyEnd;
	}
	public void setCancelPenaltyEnd(String cancelPenaltyEnd) {
		this.cancelPenaltyEnd = cancelPenaltyEnd;
	}
	public String getCancelPenaltyAmount() {
		return cancelPenaltyAmount;
	}
	public void setCancelPenaltyAmount(String cancelPenaltyAmount) {
		this.cancelPenaltyAmount = cancelPenaltyAmount;
	}
	public String getAmountAfterTax() {
		return amountAfterTax;
	}
	public void setAmountAfterTax(String amountAfterTax) {
		this.amountAfterTax = amountAfterTax;
	}
	public String getResID_Type() {
		return resID_Type;
	}
	public void setResID_Type(String resID_Type) {
		this.resID_Type = resID_Type;
	}
	public String getResID_Value() {
		return resID_Value;
	}
	public void setResID_Value(String resID_Value) {
		this.resID_Value = resID_Value;
	}
	
}

