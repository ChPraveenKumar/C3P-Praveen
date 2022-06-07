package com.techm.c3p.core.pojo;

import java.util.List;

public class ReservationReportPojo {

	private String project;

	private String startDate;

	private String endDate;
	private List<String> portSelected;
	private String comment;
	public String getProject() {
		return project;
	}
	public void setProject(String project) {
		this.project = project;
	}
	public String getStartDate() {
		return startDate;
	}
	public void setStartDate(String startDate) {
		this.startDate = startDate;
	}
	public String getEndDate() {
		return endDate;
	}
	public void setEndDate(String endDate) {
		this.endDate = endDate;
	}
	
	public List<String> getPortSelected() {
		return portSelected;
	}
	public void setPortSelected(List<String> portSelected) {
		this.portSelected = portSelected;
	}
	public String getComment() {
		return comment;
	}
	public void setComment(String comment) {
		this.comment = comment;
	}
	

}
