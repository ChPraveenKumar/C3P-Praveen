package com.techm.c3p.core.entitybeans;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "c3p_t_tststrategy_m_config_transaction")
public class TestsSelectedEntity implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private int id;

	@Column(name = "request_id", length = 40)
	private String requestId;

	@Column(name = "tests_selected", length = 300)
	private String testsSelected;

	@Column(name = "request_type", length = 40)
	private String requestType;
	
	@Column(name = "request_version", length = 10)
	private double requestVersion;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getRequestId() {
		return requestId;
	}

	public void setRequestId(String requestId) {
		this.requestId = requestId;
	}

	public String getTestsSelected() {
		return testsSelected;
	}

	public void setTestsSelected(String testsSelected) {
		this.testsSelected = testsSelected;
	}

	public String getRequestType() {
		return requestType;
	}

	public void setRequestType(String requestType) {
		this.requestType = requestType;
	}

	public double getRequestVersion() {
		return requestVersion;
	}

	public void setRequestVersion(double requestVersion) {
		this.requestVersion = requestVersion;
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
		TestsSelectedEntity other = (TestsSelectedEntity) obj;
		if (id != other.id)
			return false;
		return true;
	}
}