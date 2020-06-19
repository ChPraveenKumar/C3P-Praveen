package com.techm.orion.entitybeans;

import java.io.Serializable;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;


	
	@Entity
	@Table(name = "c3p_t_request_batch_info")
	public class BatchIdEntity implements Serializable

	{

		@GeneratedValue(strategy = GenerationType.AUTO)
		@Id
		private int infoId;

		
		@JsonIgnore
		@ManyToOne(fetch = FetchType.LAZY, cascade = { CascadeType.MERGE,
				CascadeType.PERSIST })
		@JoinColumn(name = "r_alphanumeric_req_id")
		private RequestInfoEntity requestInfoEntity;
		
	

		@Column(name = "r_batch_id")
		private String batchId;
		


		@Column(name = "r_batch_status")
	    private String batchStatus;

		public String getBatchStatus() {
			return batchStatus;
		}

		public void setBatchStatus(String batchStatus) {
			this.batchStatus = batchStatus;
		}

		public String getBatchId() {
			return batchId;
		}

		public void setBatchId(String batchId) {
			this.batchId = batchId;
		}

		public RequestInfoEntity getRequestInfoEntity() {
			return requestInfoEntity;
		}

		public void setRequestInfoEntity(RequestInfoEntity requestInfoEntity) {
			this.requestInfoEntity = requestInfoEntity;
		}
}
