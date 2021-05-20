package com.techm.orion.entitybeans;

import java.io.Serializable;
import java.util.Date;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "c3p_scheduler_history")
public class SchedulerHistoryEntity implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7757402454056442824L;

	@Id
	@Column(name = "sh_rowid", length = 20)
	@GeneratedValue(strategy = GenerationType.AUTO)
	private int shRowid;

	@Column(name = "sh_schedule_id", length = 25)
	private String shScheduleId;

	@Column(name = "sh_sch_type", length = 3)
	private String shSchType;

	@Column(name = "sh_status", length = 25)
	private String shStatus;

	@Column(name = "sh_execute_datetime")
	private Date shExecuteDatetime;

	@Column(name = "sh_next_execute_datetime")
	private Date shNextExecuteDatetime;

	@Column(name = "sh_create_datetime")
	private Date shCreateDatetime;

	@Column(name = "sh_update_datetime")
	private Date shUpdateDatetime;

	@Column(name = "sh_end_datetime")
	private Date shEndDatetime;

	@Column(name = "sh_created_by", length = 15)
	private String shCreatedBy;

	@Column(name = "sh_update_by", length = 15)
	private String shUpdateBy;

	@Column(name = "sh_create_json")
	private String shCreateJson;

	@Column(name = "sh_create_url")
	private String shCreateUrl;

	@Column(name = "sh_request_id")
	private String shRequestId;

	public String getShRequestId() {
		return shRequestId;
	}

	public void setShRequestId(String shRequestId) {
		this.shRequestId = shRequestId;
	}

	public int getShRowid() {
		return shRowid;
	}

	public void setShRowid(int shRowid) {
		this.shRowid = shRowid;
	}

	public String getShScheduleId() {
		return shScheduleId;
	}

	public void setShScheduleId(String shScheduleId) {
		this.shScheduleId = shScheduleId;
	}

	public String getShSchType() {
		return shSchType;
	}

	public void setShSchType(String shSchType) {
		this.shSchType = shSchType;
	}

	public String getShStatus() {
		return shStatus;
	}

	public void setShStatus(String shStatus) {
		this.shStatus = shStatus;
	}

	public Date getShExecuteDatetime() {
		return shExecuteDatetime;
	}

	public void setShExecuteDatetime(Date shExecuteDatetime) {
		this.shExecuteDatetime = shExecuteDatetime;
	}

	public Date getShNextExecuteDatetime() {
		return shNextExecuteDatetime;
	}

	public void setShNextExecuteDatetime(Date shNextExecuteDatetime) {
		this.shNextExecuteDatetime = shNextExecuteDatetime;
	}

	public Date getShCreateDatetime() {
		return shCreateDatetime;
	}

	public void setShCreateDatetime(Date shCreateDatetime) {
		this.shCreateDatetime = shCreateDatetime;
	}

	public Date getShUpdateDatetime() {
		return shUpdateDatetime;
	}

	public void setShUpdateDatetime(Date shUpdateDatetime) {
		this.shUpdateDatetime = shUpdateDatetime;
	}

	public Date getShEndDatetime() {
		return shEndDatetime;
	}

	public void setShEndDatetime(Date shEndDatetime) {
		this.shEndDatetime = shEndDatetime;
	}

	public String getShCreatedBy() {
		return shCreatedBy;
	}

	public void setShCreatedBy(String shCreatedBy) {
		this.shCreatedBy = shCreatedBy;
	}

	public String getShUpdateBy() {
		return shUpdateBy;
	}

	public void setShUpdateBy(String shUpdateBy) {
		this.shUpdateBy = shUpdateBy;
	}

	public String getShCreateJson() {
		return shCreateJson;
	}

	public void setShCreateJson(String shCreateJson) {
		this.shCreateJson = shCreateJson;
	}

	public String getShCreateUrl() {
		return shCreateUrl;
	}

	public void setShCreateUrl(String shCreateUrl) {
		this.shCreateUrl = shCreateUrl;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + shRowid;
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
		SchedulerHistoryEntity other = (SchedulerHistoryEntity) obj;
		if (shRowid != other.shRowid)
			return false;
		return true;
	}

}
