package com.gionee.gntravel.entity;

import java.util.ArrayList;

public class TrainNum {
	private String TrainID		;	//int		车次ID
	private String TrainName	;	//string	车次名称
	private String TrainTypeName	;//string	车次类型名称
	private String FirstStart	;	//	string	起始站名称
	private String 	FirstStartCityID;	//int		起始城市ID
	private String LastStop		;	//string	终点站名称
	private String LastStopCityID	;	//int		终点站城市ID
	private String FirstStartTime	;	//time		始发站出发时间（不含日期信息）
	private String LastArrivalTime	;	//	终点站到达时间（不含日期信息）
	private String TotalDistance	;	//int		从车次始发站到车次终点站的总公里数
	private String TotalTakeDays	;	//int		车次全程耗时，0-当天到达，1-隔天到达，以此类推
	private String IsDirect		;	//bool		是否直达车
	private String Bookable		;	//bool		是否可订（是否有票）
	private ArrayList<Station> ticketList;
	public ArrayList<Station> getTicketList() {
		return ticketList;
	}
	public void setTicketList(ArrayList<Station> ticketList) {
		this.ticketList = ticketList;
	}
	public String getTrainID() {
		return TrainID;
	}
	public void setTrainID(String trainID) {
		TrainID = trainID;
	}
	public String getTrainName() {
		return TrainName;
	}
	public void setTrainName(String trainName) {
		TrainName = trainName;
	}
	public String getTrainTypeName() {
		return TrainTypeName;
	}
	public void setTrainTypeName(String trainTypeName) {
		TrainTypeName = trainTypeName;
	}
	public String getFirstStart() {
		return FirstStart;
	}
	public void setFirstStart(String firstStart) {
		FirstStart = firstStart;
	}
	public String getFirstStartCityID() {
		return FirstStartCityID;
	}
	public void setFirstStartCityID(String firstStartCityID) {
		FirstStartCityID = firstStartCityID;
	}
	public String getLastStop() {
		return LastStop;
	}
	public void setLastStop(String lastStop) {
		LastStop = lastStop;
	}
	public String getLastStopCityID() {
		return LastStopCityID;
	}
	public void setLastStopCityID(String lastStopCityID) {
		LastStopCityID = lastStopCityID;
	}
	public String getFirstStartTime() {
		return FirstStartTime;
	}
	public void setFirstStartTime(String firstStartTime) {
		FirstStartTime = firstStartTime;
	}
	public String getLastArrivalTime() {
		return LastArrivalTime;
	}
	public void setLastArrivalTime(String lastArrivalTime) {
		LastArrivalTime = lastArrivalTime;
	}
	public String getTotalDistance() {
		return TotalDistance;
	}
	public void setTotalDistance(String totalDistance) {
		TotalDistance = totalDistance;
	}
	public String getTotalTakeDays() {
		return TotalTakeDays;
	}
	public void setTotalTakeDays(String totalTakeDays) {
		TotalTakeDays = totalTakeDays;
	}
	public String getIsDirect() {
		return IsDirect;
	}
	public void setIsDirect(String isDirect) {
		IsDirect = isDirect;
	}
	public String getBookable() {
		return Bookable;
	}
	public void setBookable(String bookable) {
		Bookable = bookable;
	}
	
	
	
	
	
}
