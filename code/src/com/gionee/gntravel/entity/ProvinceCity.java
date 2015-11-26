package com.gionee.gntravel.entity;

import java.io.Serializable;


public class ProvinceCity implements Serializable{

	private String name;
	private String suoxiePinyin;
	private String pinyin;
	private String province;

	public String getProvince() {
		return province;
	}

	public void setProvince(String province) {
		this.province = province;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getSuoxiePinyin() {
		return suoxiePinyin;
	}

	public void setSuoxiePinyin(String suoxiePinyin) {
		this.suoxiePinyin = suoxiePinyin;
	}

	public String getPinyin() {
		return pinyin;
	}

	public void setPinyin(String pinyin) {
		this.pinyin = pinyin;
	}


	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((pinyin == null) ? 0 : pinyin.hashCode());
		result = prime * result + ((suoxiePinyin == null) ? 0 : suoxiePinyin.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ProvinceCity other = (ProvinceCity) obj;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (pinyin == null) {
			if (other.pinyin != null)
				return false;
		} else if (!pinyin.equals(other.pinyin))
			return false;
		if (suoxiePinyin == null) {
			if (other.suoxiePinyin != null)
				return false;
		} else if (!suoxiePinyin.equals(other.suoxiePinyin))
			return false;
		return true;
	}

}
