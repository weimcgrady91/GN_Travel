package com.gionee.gntravel.entity;

import java.io.Serializable;

/**
 * 
 * @author lijinbao
 * 重置密码 验证短信验证码
 */
public class ResetBySMSCode implements Serializable{
private String tn;
private String s;
private String sc;
public String getTn() {
	return tn;
}
public void setTn(String tn) {
	this.tn = tn;
}
public String getS() {
	return s;
}
public void setS(String s) {
	this.s = s;
}
public String getSc() {
	return sc;
}
public void setSc(String sc) {
	this.sc = sc;
}
@Override
public String toString() {
	return "ResetBySMSCode [tn=" + tn + ", s=" + s + ", sc=" + sc + "]";
}

}
