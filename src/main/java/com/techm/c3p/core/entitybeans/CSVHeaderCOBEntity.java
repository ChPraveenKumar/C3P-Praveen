package com.techm.c3p.core.entitybeans;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "c3p_t_cob_m_header")
public class CSVHeaderCOBEntity {

	@GeneratedValue(strategy = GenerationType.AUTO)
	@Id
	private int id;

	@Column(name = "name", length = 25)
	private String csvHeaderName;

	@Column(name = "mandatory_flag")
	private String mandatoryFlag;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getCsvHeaderName() {
		return csvHeaderName;
	}

	public void setCsvHeaderName(String csvHeaderName) {
		this.csvHeaderName = csvHeaderName;
	}

	public String getMandatoryFlag() {
		return mandatoryFlag;
	}

	public void setMandatoryFlag(String mandatoryFlag) {
		this.mandatoryFlag = mandatoryFlag;
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
		CSVHeaderCOBEntity other = (CSVHeaderCOBEntity) obj;
		if (id != other.id)
			return false;
		return true;
	}

}
