package com.gionee.gntravel.entity;

import java.io.Serializable;

public class WeatherRepParam implements Serializable{
	private String w;//天气
	private String skt;//实况温度
	private String p;//PM2.5
	private String xq;//星期几
	private String fc;//天气预报
	private String wd;//风
	private String rh;//相对湿度
	private String z;//紫外线
	private String c;//穿衣
	public String getW() {
		return w;
	}
	public void setW(String w) {
		this.w = w;
	}
	public String getSkt() {
		return skt;
	}
	public void setSkt(String skt) {
		this.skt = skt;
	}
	public String getP() {
		return p;
	}
	public void setP(String p) {
		this.p = p;
	}
	public String getXq() {
		return xq;
	}
	public void setXq(String xq) {
		this.xq = xq;
	}
	public String getFc() {
		return fc;
	}
	public void setFc(String fc) {
		this.fc = fc;
	}
	public String getWd() {
		return wd;
	}
	public void setWd(String wd) {
		this.wd = wd;
	}
	public String getRh() {
		return rh;
	}
	public void setRh(String rh) {
		this.rh = rh;
	}
	public String getZ() {
		return z;
	}
	public void setZ(String z) {
		this.z = z;
	}
	public String getC() {
		return c;
	}
	public void setC(String c) {
		this.c = c;
	}
}
