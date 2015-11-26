package com.gionee.gntravel.entity;

import java.io.Serializable;

public class Station implements Serializable{
	private String StationID	;		//int		站点ID
	private String StationName		;//string	站点名称
	private String DepartureTime;	//time		离站时间（不含日期信息）
	private String ArrivalTime	;	//time		到站时间
	private String DistanceFromStart;//	int		出发站到当前站点的距离（行程公里数）
	private String DaysFromStart	;//	int		出发后到达当前站点的耗时（行程天数）
	private String DuringFromStart;	//int		距离始发站的分钟数
	private String TrainShortName	;//string	该车次在当前站的车次名（同一线路的火车在不同站点可能会有不同的车次名）
	private String TimetableSeqID	;//int		时刻表顺序（当前站是该车次的第几站）
	private String StopType			;//string	停靠类型，说明所在节点对应的站点是该车次的始发站、途经站还是终点站。StartStation-始发站，WayStation-途经站，EndStation-终点站。
	public String getStationID() {
		return StationID;
	}
	public void setStationID(String stationID) {
		StationID = stationID;
	}
	public String getStationName() {
		return StationName;
	}
	public void setStationName(String stationName) {
		StationName = stationName;
	}
	public String getDepartureTime() {
		return DepartureTime;
	}
	public void setDepartureTime(String departureTime) {
		DepartureTime = departureTime;
	}
	public String getArrivalTime() {
		return ArrivalTime;
	}
	public void setArrivalTime(String arrivalTime) {
		ArrivalTime = arrivalTime;
	}
	public String getDistanceFromStart() {
		return DistanceFromStart;
	}
	public void setDistanceFromStart(String distanceFromStart) {
		DistanceFromStart = distanceFromStart;
	}
	public String getDaysFromStart() {
		return DaysFromStart;
	}
	public void setDaysFromStart(String daysFromStart) {
		DaysFromStart = daysFromStart;
	}
	public String getDuringFromStart() {
		return DuringFromStart;
	}
	public void setDuringFromStart(String duringFromStart) {
		DuringFromStart = duringFromStart;
	}
	public String getTrainShortName() {
		return TrainShortName;
	}
	public void setTrainShortName(String trainShortName) {
		TrainShortName = trainShortName;
	}
	public String getTimetableSeqID() {
		return TimetableSeqID;
	}
	public void setTimetableSeqID(String timetableSeqID) {
		TimetableSeqID = timetableSeqID;
	}
	public String getStopType() {
		return StopType;
	}
	public void setStopType(String stopType) {
		StopType = stopType;
	}
	
	
	
	
}
