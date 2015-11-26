package com.gionee.gntravel.entity;

import java.io.Serializable;

public class UpdateEntity implements Serializable{
	private int versionCode;
	private String apkName;
	private String downloadPath;
	private String upDateMessage;

	public int getVersionCode() {
		return versionCode;
	}

	public void setVersionCode(int versionCode) {
		this.versionCode = versionCode;
	}

	public String getApkName() {
		return apkName;
	}

	public void setApkName(String apkName) {
		this.apkName = apkName;
	}

	public String getDownloadPath() {
		return downloadPath;
	}

	public void setDownloadPath(String downloadPath) {
		this.downloadPath = downloadPath;
	}

	public String getUpDateMessage() {
		return upDateMessage;
	}

	public void setUpDateMessage(String upDateMessage) {
		this.upDateMessage = upDateMessage;
	}

	
}
