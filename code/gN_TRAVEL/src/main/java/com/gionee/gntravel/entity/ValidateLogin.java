package com.gionee.gntravel.entity;

import java.io.Serializable;

/**
 * 
 * @author lijinbao
 * 登录验证
 */
public class ValidateLogin  implements Serializable{
/**
 *  * tn：必须项，用户注册时的手机号码
	 * p:必选项，用户注册时的密码
	 * sdid: 可选项，对应手机加密后的imei号
	 * ver ：可选项，机型/ROM版本号/App名称/App版本号
	 * vid:可选项,通过refreshGVCImage刷新验证码获取的vid
	 * vtx:可选项,通过refreshGVCImage刷新验证码获取的vda解码，转成图片验证码
	 * vty:可选项,验证码类型,图片的是vtext.
	 * 
	 * return u,tn,na,ptr,ul,sty
	 * u:userid
 */
	private String tn;
	private String p;
	private String sdid;
	private String ver;
	private String vid;
	private String vts;
	private String vty;
	public String getTn() {
		return tn;
	}
	public void setTn(String tn) {
		this.tn = tn;
	}
	public String getP() {
		return p;
	}
	public void setP(String p) {
		this.p = p;
	}
	public String getSdid() {
		return sdid;
	}
	public void setSdid(String sdid) {
		this.sdid = sdid;
	}
	public String getVer() {
		return ver;
	}
	public void setVer(String ver) {
		this.ver = ver;
	}
	public String getVid() {
		return vid;
	}
	public void setVid(String vid) {
		this.vid = vid;
	}
	public String getVts() {
		return vts;
	}
	public void setVts(String vts) {
		this.vts = vts;
	}
	public String getVty() {
		return vty;
	}
	public void setVty(String vty) {
		this.vty = vty;
	}
	@Override
	public String toString() {
		return "ValidateLogin [tn=" + tn + ", p=" + p + ", sdid=" + sdid
				+ ", ver=" + ver + ", vid=" + vid + ", vts=" + vts + ", vty="
				+ vty + "]";
	}
	
}
