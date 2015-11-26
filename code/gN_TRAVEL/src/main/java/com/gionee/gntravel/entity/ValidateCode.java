package com.gionee.gntravel.entity;

import java.io.Serializable;

public class ValidateCode implements Serializable {
	// json={"tn":"18600060484","sc":"108287","s":"null","ver":"null","sdid":"null"}
	private String tn;
	private String sc;
	private String s;
	private String ver;
	private String sdid;

	public String getTn() {
		return tn;
	}

	public void setTn(String tn) {
		this.tn = tn;
	}

	public String getSc() {
		return sc;
	}

	public void setSc(String sc) {
		this.sc = sc;
	}

	public String getS() {
		return s;
	}

	public void setS(String s) {
		this.s = s;
	}

	public String getVer() {
		return ver;
	}

	public void setVer(String ver) {
		this.ver = ver;
	}

	public String getSdid() {
		return sdid;
	}

	public void setSdid(String sdid) {
		this.sdid = sdid;
	}

	@Override
	public String toString() {
		return "ValidateCode [tn=" + tn + ", sc=" + sc + ", s=" + s + ", ver="
				+ ver + ", sdid=" + sdid + "]";
	}

}
