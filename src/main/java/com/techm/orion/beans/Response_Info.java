package com.techm.orion.beans;

//import java.io.Serializable;
//import java.util.List;
//
//public class Response_Info implements Serializable {
//	
//	private static final long serialVersionUID = 5515363483766260237L;
//
//	//Member list.
//	private List<Delay_Info> delayInfoList;
//
//	public List<Delay_Info> getDelayInfoList() {
//		return delayInfoList;
//	}
//
//	public void setDelayInfoList(List<Delay_Info> delayInfoList) {
//		this.delayInfoList = delayInfoList;
//	}
//}




import java.sql.Timestamp;
import java.text.SimpleDateFormat;

public class Response_Info {
	
	public String getTaskValue() {
		return taskValue;
	}


	public void setTaskValue(String taskValue) {
		this.taskValue = taskValue;
	}



		public String getStatusValue() {
			return statusValue;
		}


		public void setStatusValue(String statusValue) {
			this.statusValue = statusValue;
		}
	
	
	
	private String taskValue;
	private String statusValue;
	
	
	}
	