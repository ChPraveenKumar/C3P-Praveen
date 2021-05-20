package com.techm.orion.entitybeans;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Component
@Entity
@Table(name = "c3p_t_import_master_staging")
@JsonIgnoreProperties(ignoreUnknown = false)
public class ImportMasterStagingEntity implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -9062339565299224552L;

	@Id
	@Column(name = "id")
	@GeneratedValue(strategy = GenerationType.AUTO)
	private int id;

	@Column(name = "importid")
	private String importId;

	@Column(name = "status")
	private String status;

	@Column(name = "user_name")
	private String userName;

	@Column(name = "total_devices")
	private long totalDevices;

	@Column(name = "count_existing")
	private long countExisting;

	@Column(name = "count_new")
	private long countNew;

	@Column(name = "count_success")
	private long countSuccess;

	@Column(name = "count_exception")
	private long countException;

	@Column(name = "created_by")
	private String createdBy;

	@Transient
	private MultipartFile file;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "execution_processing_date")
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Calcutta")
	private Date executionProcessDate;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "execution_date")
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Calcutta")
	private Date executionDate;

	@PrePersist
	public void prePersist() {
		Date now = new Date();
		this.executionProcessDate = now;
		this.executionDate = now;
	}

	@PreUpdate
	public void preUpdate() {
		Date now = new Date();
		this.executionDate = now;
	}

	@Override
	public String toString() {
		return "ImportMasterStagingEntity [id=" + id + ", importId=" + importId + ", status=" + status + ", userName="
				+ userName + ", totalDevices=" + totalDevices + ", countExisting=" + countExisting + ", countNew="
				+ countNew + ", countSuccess=" + countSuccess + ", countException=" + countException + ", createdBy="
				+ createdBy + ", file=" + file + ", executionProcessDate=" + executionProcessDate + ", executionDate="
				+ executionDate + "]";
	}

	public ImportMasterStagingEntity() {
		super();
		// TODO Auto-generated constructor stub
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getImportId() {
		return importId;
	}

	public void setImportId(String importId) {
		this.importId = importId;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public long getTotalDevices() {
		return totalDevices;
	}

	public void setTotalDevices(long totalDevices) {
		this.totalDevices = totalDevices;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public long getCountExisting() {
		return countExisting;
	}

	public void setCountExisting(long countExisting) {
		this.countExisting = countExisting;
	}

	public long getCountNew() {
		return countNew;
	}

	public void setCountNew(long countNew) {
		this.countNew = countNew;
	}

	public long getCountSuccess() {
		return countSuccess;
	}

	public void setCountSuccess(long countSuccess) {
		this.countSuccess = countSuccess;
	}

	public long getCountException() {
		return countException;
	}

	public void setCountException(long countException) {
		this.countException = countException;
	}

	public String getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}

	public MultipartFile getFile() {
		return file;
	}

	public void setFile(MultipartFile file) {
		this.file = file;
	}

	public Date getExecutionProcessDate() {
		return executionProcessDate;
	}

	public void setExecutionProcessDate(Date executionProcessDate) {
		this.executionProcessDate = executionProcessDate;
	}

	public Date getExecutionDate() {
		return executionDate;
	}

	public void setExecutionDate(Date executionDate) {
		this.executionDate = executionDate;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + id;
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
		ImportMasterStagingEntity other = (ImportMasterStagingEntity) obj;
		if (id != other.id)
			return false;
		return true;
	}

}
