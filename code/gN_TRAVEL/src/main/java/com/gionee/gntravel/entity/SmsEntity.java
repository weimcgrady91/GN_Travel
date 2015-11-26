package com.gionee.gntravel.entity;

import java.util.Date;

public class SmsEntity {
	private String phoneNumber;
	private String smsbody;
	private Date date;
	private int _id;

	public String getPhoneNumber() {
		return phoneNumber;
	}

	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	public String getSmsbody() {
		return smsbody;
	}

	public void setSmsbody(String smsbody) {
		this.smsbody = smsbody;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public int get_id() {
		return _id;
	}

	public void set_id(int _id) {
		this._id = _id;
	}

	
}
