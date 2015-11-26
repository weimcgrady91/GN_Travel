package com.gionee.gntravel.entity;

import java.io.Serializable;
/**
 * 
 * @author lijinbao
 * 忘记密码 最后一步需要的参数
 */
public class ResetNewPassword implements Serializable{
	
private String s;//会话
private String p;//密码
public String getS() {
	return s;
}
public void setS(String s) {
	this.s = s;
}
public String getP() {
	return p;
}
public void setP(String p) {
	this.p = p;
}

}
