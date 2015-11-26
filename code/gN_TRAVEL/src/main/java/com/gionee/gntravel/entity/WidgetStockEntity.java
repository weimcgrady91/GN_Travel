package com.gionee.gntravel.entity;

import java.io.Serializable;

import android.os.Parcel;

@SuppressWarnings("serial")
public class WidgetStockEntity implements Serializable {
	public String name;
	public String recent;
	public String changeratio;
	public String change;
	public String status;
	public String url;

	
	
	public WidgetStockEntity() {
		super();
	}

	public WidgetStockEntity(Parcel in ) {
		name = in.readString();
		recent = in.readString();
		changeratio = in.readString();
		change = in.readString();
		status = in.readString();
		url = in.readString();
	}
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getRecent() {
		return recent;
	}

	public void setRecent(String recent) {
		this.recent = recent;
	}

	public String getChangeratio() {
		return changeratio;
	}

	public void setChangeratio(String changeratio) {
		this.changeratio = changeratio;
	}

	public String getChange() {
		return change;
	}

	public void setChange(String change) {
		this.change = change;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}


}
