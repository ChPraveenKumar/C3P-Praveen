package com.techm.c3p.core.entitybeans;

import java.io.Serializable;
import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
//@Table(name = "camundahistory")
@Table(name = "c3p_camunda_history")
public class CamundaHistoryEntity implements Serializable {
	
	 private static final long serialVersionUID = 1L;
	
	 @Id
	 @Column(name = "history_id", length = 11)
	 @GeneratedValue(strategy = GenerationType.AUTO)
	 private int historyId;
	
	 @Column(name = "history_process_id", length = 100)
	 private String historyProcessId;
	
	 @Column(name = "history_user_task_id", length = 100)
	 private String historyUserTaskId;
	
	 @Column(name = "history_request_id", length = 45)
	 private String historyRequestId;
	
	 @Column(name = "history_version_id", length = 45)
	 private String historyVersionId;
	
	 @Column(name = "history_user", length = 50)
	 private String historyUser;
	
	 @Column(name = "history_timestamp")
	 private Timestamp historyTimestamp;
	
	
	
	 public int getHistoryId() {
		return historyId;
	}

	public void setHistoryId(int historyId) {
		this.historyId = historyId;
	}

	public String getHistoryProcessId() {
		return historyProcessId;
	}

	public void setHistoryProcessId(String historyProcessId) {
		this.historyProcessId = historyProcessId;
	}

	public String getHistoryUserTaskId() {
		return historyUserTaskId;
	}

	public void setHistoryUserTaskId(String historyUserTaskId) {
		this.historyUserTaskId = historyUserTaskId;
	}

	public String getHistoryRequestId() {
		return historyRequestId;
	}

	public void setHistoryRequestId(String historyRequestId) {
		this.historyRequestId = historyRequestId;
	}

	public String getHistoryVersionId() {
		return historyVersionId;
	}

	public void setHistoryVersionId(String historyVersionId) {
		this.historyVersionId = historyVersionId;
	}

	public String getHistoryUser() {
		return historyUser;
	}

	public void setHistoryUser(String historyUser) {
		this.historyUser = historyUser;
	}

	public Timestamp getHistoryTimestamp() {
		return historyTimestamp;
	}

	public void setHistoryTimestamp(Timestamp historyTimestamp) {
		this.historyTimestamp = historyTimestamp;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	@Override
	 public int hashCode() {
	 final int prime = 31;
	 int result = 1;
	 result = prime * result + historyId;
	 result = prime * result + ((historyProcessId == null) ? 0 : historyProcessId.hashCode());
	 result = prime * result + ((historyRequestId == null) ? 0 : historyRequestId.hashCode());
	 result = prime * result + ((historyTimestamp == null) ? 0 : historyTimestamp.hashCode());
	 result = prime * result + ((historyUser == null) ? 0 : historyUser.hashCode());
	 result = prime * result + ((historyUserTaskId == null) ? 0 : historyUserTaskId.hashCode());
	 result = prime * result + ((historyVersionId == null) ? 0 : historyVersionId.hashCode());
	 return result;
	 }
	
	 @Override
	 public boolean equals(Object obj) {
	 if (this == obj)
	 return true;
	 if (obj == null)
	 return false;
	 if (getClass() != obj.getClass())
	 return false;
	 CamundaHistoryEntity other = (CamundaHistoryEntity) obj;
	 if (historyId != other.historyId)
	 return false;
	 if (historyProcessId == null) {
	 if (other.historyProcessId != null)
	 return false;
	 } else if (!historyProcessId.equals(other.historyProcessId))
	 return false;
	 if (historyRequestId == null) {
	 if (other.historyRequestId != null)
	 return false;
	 } else if (!historyRequestId.equals(other.historyRequestId))
	 return false;
	 if (historyTimestamp == null) {
	 if (other.historyTimestamp != null)
	 return false;
	 } else if (!historyTimestamp.equals(other.historyTimestamp))
	 return false;
	 if (historyUser == null) {
	 if (other.historyUser != null)
	 return false;
	 } else if (!historyUser.equals(other.historyUser))
	 return false;
	 if (historyUserTaskId == null) {
	 if (other.historyUserTaskId != null)
	 return false;
	 } else if (!historyUserTaskId.equals(other.historyUserTaskId))
	 return false;
	 if (historyVersionId == null) {
	 if (other.historyVersionId != null)
	 return false;
	 } else if (!historyVersionId.equals(other.historyVersionId))
	 return false;
	 return true;
	 }
}
