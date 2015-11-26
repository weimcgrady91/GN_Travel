package com.gionee.gntravel.entity;

public class AddressEntity {
private String name;
private String mobile;
private String mailCode;
private String address;
public String getName() {
	return name;
}
public void setName(String name) {
	this.name = name;
}
public String getMobile() {
	return mobile;
}
public void setMobile(String mobile) {
	this.mobile = mobile;
}
public String getMailCode() {
	return mailCode;
}
public void setMailCode(String mailCode) {
	this.mailCode = mailCode;
}
public String getAddress() {
	return address;
}
public void setAddress(String address) {
	this.address = address;
}
@Override
public String toString() {
	return "AddressEntity [name=" + name + ", mobile=" + mobile + ", mailCode="
			+ mailCode + ", address=" + address + "]";
}

}
