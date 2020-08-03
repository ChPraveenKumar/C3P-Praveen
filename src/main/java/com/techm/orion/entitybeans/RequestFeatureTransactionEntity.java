package com.techm.orion.entitybeans;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

@Entity
@Table(name = "c3p_t_feature_config_transaction")
public class RequestFeatureTransactionEntity {
	
	@Id
	@Column(name = "id")
	@GeneratedValue(strategy = GenerationType.AUTO)
	private int id;
	
	@OneToOne(cascade = { CascadeType.ALL })
	@JoinColumn(name = "t_feature_id")
	private TemplateFeatureEntity tFeatureId;

	@Column(name = "t_request_id", length = 20)
	private String tRequestId;

	@Column(name = "t_hostname", length = 20)
	private String tHostName;

	@Column(name = "t_request_ver", length = 5)
	private Double tRequestVersion;

	public TemplateFeatureEntity gettFeatureId() {
		return tFeatureId;
	}

	public void settFeatureId(TemplateFeatureEntity tFeatureId) {
		this.tFeatureId = tFeatureId;
	}

	public String gettRequestId() {
		return tRequestId;
	}

	public void settRequestId(String tRequestId) {
		this.tRequestId = tRequestId;
	}

	public String gettHostName() {
		return tHostName;
	}

	public void settHostName(String tHostName) {
		this.tHostName = tHostName;
	}

	public Double gettRequestVersion() {
		return tRequestVersion;
	}

	public void settRequestVersion(Double tRequestVersion) {
		this.tRequestVersion = tRequestVersion;
	}

}
