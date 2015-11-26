package com.gionee.gntravel.entity;

public class ContentBean {
private String id;
private String code;
private String name;
private String userid;
public String getId() {
	return id;
}
public void setId(String id) {
	this.id = id;
}
public String getCode() {
	return code;
}
public void setCode(String code) {
	this.code = code;
}
public String getName() {
	return name;
}
public void setName(String name) {
	this.name = name;
}

public ContentBean(String id, String code, String name) {
	super();
	this.id = id;
	this.code = code;
	this.name = name;
}
public String getUserid() {
	return userid;
}
public void setUserid(String userid) {
	this.userid = userid;
}
public ContentBean() {
	super();
}
@Override
public String toString() {
	return "ContentBean [id=" + id + ", code=" + code + ", name=" + name
			+ ", userid=" + userid + "]";
}


}
