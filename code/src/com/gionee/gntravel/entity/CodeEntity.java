package com.gionee.gntravel.entity;

import java.io.Serializable;

/**
 * 
 * @author lijinbao注册时候 请求短信验证码的实体类
 */
public class CodeEntity implements Serializable{
	private String tn;// 手机号
	private String s;// 可选项 ,当服务器发现帐号有危险时,注册必须通过regRegisterByGVC

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

	@Override
	public String toString() {
		return "codeEntity [tn=" + tn + ", s=" + s + "]";
	}

}
