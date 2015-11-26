package com.gionee.gntravel.entity;

import java.io.Serializable;
import java.util.ArrayList;

public class Train implements Serializable {
	private String IsStartStation;
	private String IsEndStation;
	private String TrainID;
	private String TrainName;
	private String TrainTypeName;
	private String FirstStart;
	private String LastStop;
	private String FirstStartTime;
	private String LastArrivalTime;
	private String TotalDistance;
	private String TotalTakeDays;
	private String IsDirect;
	private String Bookable;
	private String Start;
	private String Stop;
	private String StartTime;
	private String ArrivalTime;
	private String Distance;
	private String TakeDays;
	private String TrainShortName;
	private ArrayList<Ticket> ticketList;
	private String StartStationID;
	private String EndStationID;
	private int totalTime;
	public int getTotalTime() {
		return totalTime;
	}
	
	public void setTotalTime(int totalTime) {
		this.totalTime = totalTime;
	}

	public String getIsStartStation() {
		return IsStartStation;
	}
	public void setIsStartStation(String isStartStation) {
		IsStartStation = isStartStation;
	}
	public String getIsEndStation() {
		return IsEndStation;
	}
	public void setIsEndStation(String isEndStation) {
		IsEndStation = isEndStation;
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
	public String getLastStop() {
		return LastStop;
	}
	public void setLastStop(String lastStop) {
		LastStop = lastStop;
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
	public String getStartTime() {
		return StartTime;
	}
	public void setStartTime(String startTime) {
		StartTime = startTime;
	}
	public String getArrivalTime() {
		return ArrivalTime;
	}
	public void setArrivalTime(String arrivalTime) {
		ArrivalTime = arrivalTime;
	}
	public String getDistance() {
		return Distance;
	}
	public void setDistance(String distance) {
		Distance = distance;
	}
	public String getTakeDays() {
		return TakeDays;
	}
	public void setTakeDays(String takeDays) {
		TakeDays = takeDays;
	}
	public String getTrainShortName() {
		return TrainShortName;
	}
	public void setTrainShortName(String trainShortName) {
		TrainShortName = trainShortName;
	}
	public ArrayList<Ticket> getTicketList() {
		return ticketList;
	}
	public void setTicketList(ArrayList<Ticket> ticketList) {
		this.ticketList = ticketList;
	}
	public String getStartStationID() {
		return StartStationID;
	}
	public void setStartStationID(String startStationID) {
		StartStationID = startStationID;
	}
	public String getEndStationID() {
		return EndStationID;
	}
	public void setEndStationID(String endStationID) {
		EndStationID = endStationID;
	}
	
	
}
