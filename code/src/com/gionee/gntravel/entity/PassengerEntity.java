package com.gionee.gntravel.entity;
/**
 * 
 * @author lijinbao
 * 飞机订单详情 登机人 姓名 和 身份证号 
 */
public class PassengerEntity {
private String name;
private String code;
public String getName() {
	return name;
}
public void setName(String name) {
	this.name = name;
}
public String getCode() {
	return code;
}
public void setCode(String code) {
	this.code = code;
}
@Override
public String toString() {
	return "PassengerEntity [name=" + name + ", code=" + code + "]";
}

}
