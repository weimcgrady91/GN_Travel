package com.gionee.gntravel.entity;

import java.io.Serializable;

/**
 * @Author:yangxy
 * @Create 2014-7-3
 */
public class HotelReservation implements Serializable{
	private String createDateTime;
	private String resStatus;
	private RoomStay roomStay;
	private ResGlobalInfo resGlobalInfo;
	private String cid;
	public String getCid() {
		return cid;
	}
	public void setCid(String cid) {
		this.cid = cid;
	}
	public String getCreateDateTime() {
		return createDateTime;
	}
	public void setCreateDateTime(String createDateTime) {
		this.createDateTime = createDateTime;
	}
	public String getResStatus() {
		return resStatus;
	}
	public void setResStatus(String resStatus) {
		this.resStatus = resStatus;
	}
	public ResGlobalInfo getResGlobalInfo() {
		return resGlobalInfo;
	}
	public void setResGlobalInfo(ResGlobalInfo resGlobalInfo) {
		this.resGlobalInfo = resGlobalInfo;
	}
}

