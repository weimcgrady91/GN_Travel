package com.gionee.gntravel.entity;

import java.io.Serializable;

/**
 * 
 * @author lijinbao 修改密码 entity
 */
public class EditPassWord implements Serializable {
	/**
	 * tn:必选项，用户注册时电话号码 
	 * p:必选项，原密码，
	 *  np:必须项，新密码，
	 * vid:可选项,通过refreshGVCImage刷新验证码获取的vid
	 * vtx:可选项,通过refreshGVCImage刷新验证码获取的vda解码，转成图片验证码 
	 * vty:可选项,验证码类型,图片的是vtext.
	 * 
	 */
	private String tn;
	private String p;
	private String np;
	private String vid;
	private String vtx;
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

	public String getNp() {
		return np;
	}

	public void setNp(String np) {
		this.np = np;
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
		return "EditPassWord [tn=" + tn + ", p=" + p + ", np=" + np + ", vid="
				+ vid + ", vtx=" + vtx + ", vty=" + vty + "]";
	}

}
