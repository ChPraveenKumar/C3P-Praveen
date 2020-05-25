package com.techm.orion.pojo;

import java.util.ArrayList;

public class StatusReportPojoColumnChart {
	
	private String status;
	private ArrayList<Integer>dayWiseDataArray=new ArrayList<Integer>();
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public ArrayList<Integer> getDayWiseDataArray() {
		return dayWiseDataArray;
	}
	public void setDayWiseDataArray(ArrayList<Integer> dayWiseDataArray) {
		this.dayWiseDataArray = dayWiseDataArray;
	}
	
	

}
