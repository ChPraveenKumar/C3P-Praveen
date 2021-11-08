package com.techm.c3p.core.pojo;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

public class RecentActivityPojo implements Comparable<RecentActivityPojo> {

	private String createdDate;
	
	@JsonInclude(Include.NON_NULL)
	private String updatedDate;

	private String message;

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(String createdDate) {
		this.createdDate = createdDate;
	}

	public String getUpdatedDate() {
		return updatedDate;
	}

	public void setUpdatedDate(String updatedDate) {
		this.updatedDate = updatedDate;
	}

	@Override
	public int compareTo(RecentActivityPojo o) {
		if(createdDate.equals(o.getCreatedDate()))  
			return 0;  
		else if(createdDate.compareTo(o.getCreatedDate())<0)  
			return 1;  
		else  
			return -1;  
	}
}