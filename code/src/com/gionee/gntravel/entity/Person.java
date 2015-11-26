package com.gionee.gntravel.entity;

public class Person {
private String id;
private String code;
private String name;
private Boolean flag;
public Boolean getFlag() {
	return flag;
}
public void setFlag(Boolean flag) {
	this.flag = flag;
}
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

public Person() {
	super();
}
public Person(String id, String code, String name) {
	super();
	this.id = id;
	this.code = code;
	this.name = name;
}


public Person(String code, String name, Boolean flag) {
	super();
	this.code = code;
	this.name = name;
	this.flag = flag;
}
@Override
public String toString() {
	return "ContentBean [id=" + id + ", code=" + code + ", name=" + name
			+ ", flag=" + flag + "]";
}


}
