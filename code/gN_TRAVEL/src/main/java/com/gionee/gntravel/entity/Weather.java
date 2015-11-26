package com.gionee.gntravel.entity;

import java.io.Serializable;

/**
 * 
 * @author lijinbao
 * 温度请求参数
 */
public class Weather implements Serializable{
private String Start;
private String Stop;
public String getStart() {
	return Start;
}
public void setStart(String start) {
	Start = start;
}
public String getStop() {
	return Stop;
}
public void setStop(String stop) {
	Stop = stop;
}

}
