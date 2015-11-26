package com.gionee.gntravel.entity;

public class ForgetPassword {
/**
 *  * tn:必选项，用户注册时电话号码
	 * vid:必选项,通过refreshGVCImage刷新验证码获取的vid
	 * vtx:必选项,通过refreshGVCImage刷新验证码获取的vda解码，转成图片验证码
	 * vty:必选项,验证码类型,图片的是vtext.
 */
	private String tn;
	private String vid;
	private String vtx;
	private String vty;
	public String getTn() {
		return tn;
	}
	public void setTn(String tn) {
		this.tn = tn;
	}
	public String getVid() {
		return vid;
	}
	public void setVid(String vid) {
		this.vid = vid;
	}
	public String getVtx() {
		return vtx;
	}
	public void setVtx(String vtx) {
		this.vtx = vtx;
	}
	public String getVty() {
		return vty;
	}
	public void setVty(String vty) {
		this.vty = vty;
	}
	@Override
	public String toString() {
		return "ForgetPassword [tn=" + tn + ", vid=" + vid + ", vtx=" + vtx
				+ ", vty=" + vty + "]";
	}
	
}
