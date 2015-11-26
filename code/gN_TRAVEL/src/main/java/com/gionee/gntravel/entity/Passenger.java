package com.gionee.gntravel.entity;

import java.io.Serializable;

public class Passenger implements Serializable {
	private String passengerName;
	private String birthDay;
	private String passportTypeID;
	private String passportNo;
//	private String contactTelephone;
//	private String gender;
//	private String nationalityCode;

//	public String getContactTelephone() {
//		return contactTelephone;
//	}
//
//	public void setContactTelephone(String contactTelephone) {
//		this.contactTelephone = contactTelephone;
//	}

	public String getPassengerName() {
		return passengerName;
	}

	public void setPassengerName(String passengerName) {
		this.passengerName = passengerName;
	}

	public String getBirthDay() {
		return birthDay;
	}

	public void setBirthDay(String birthDay) {
		this.birthDay = birthDay;
	}

	public String getPassportTypeID() {
		return passportTypeID;
	}

	public void setPassportTypeID(String passportTypeID) {
		this.passportTypeID = passportTypeID;
	}

	public String getPassportNo() {
		return passportNo;
	}

	public void setPassportNo(String passportNo) {
		this.passportNo = passportNo;
	}

//	public String getGender() {
//		return gender;
//	}
//
//	public void setGender(String gender) {
//		this.gender = gender;
//	}
//
//	public String getNationalityCode() {
//		return nationalityCode;
//	}
//
//	public void setNationalityCode(String nationalityCode) {
//		this.nationalityCode = nationalityCode;
//	}

}
