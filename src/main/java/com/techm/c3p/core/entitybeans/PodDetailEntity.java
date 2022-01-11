package com.techm.c3p.core.entitybeans;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Entity
@Table(name = "c3p_m_pod_detail")
@JsonIgnoreProperties(ignoreUnknown = false)
public class PodDetailEntity {

	@GeneratedValue(strategy = GenerationType.AUTO)
	@Id
	private int id;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	@Column(name = "pd_cluster_id")
	private int pdClusterId;

	

	@Column(name = "pd_pod_creation_request_id")
	private String pdPodCreationRequestId;
	
	

	@Lob
    @Column(name="pd_tf_state_json")
    private byte[] pdTfStateJson;

	public int getPdClusterId() {
		return pdClusterId;
	}

	public void setPdClusterId(int pdClusterId) {
		this.pdClusterId = pdClusterId;
	}

	public byte[] getPdTfStateJson() {
		return pdTfStateJson;
	}

	public void setPdTfStateJson(byte[] pdTfStateJson) {
		this.pdTfStateJson = pdTfStateJson;
	}
	public String getPdPodCreationRequestId() {
		return pdPodCreationRequestId;
	}

	public void setPdPodCreationRequestId(String pdPodCreationRequestId) {
		this.pdPodCreationRequestId = pdPodCreationRequestId;
	}
}
