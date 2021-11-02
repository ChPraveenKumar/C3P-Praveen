package com.techm.c3p.core.entitybeans;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "c3p_m_ds_import_headers")
public class CSVHeaderCOBEntity {

	@GeneratedValue(strategy = GenerationType.AUTO)
	@Id
	private int ds_header_row_id;

	@Column(name = "ds_header_name", length = 25)
	private String dsHeaderName;

	@Column(name = "ds_header_mandatory_flag", columnDefinition = "TINYINT(1) default 0", nullable = false )
	private boolean dsHeaderMandatoryFlag;

	@Column(name = "ds_header_category", length = 25)
	private String dsHeaderCategory;

	public int getDs_header_row_id() {
		return ds_header_row_id;
	}

	public void setDs_header_row_id(int ds_header_row_id) {
		this.ds_header_row_id = ds_header_row_id;
	}

	public String getDsHeaderName() {
		return dsHeaderName;
	}

	public void setDsHeaderName(String dsHeaderName) {
		this.dsHeaderName = dsHeaderName;
	}

	public boolean isDsHeaderMandatoryFlag() {
		return dsHeaderMandatoryFlag;
	}

	public void setDsHeaderMandatoryFlag(boolean dsHeaderMandatoryFlag) {
		this.dsHeaderMandatoryFlag = dsHeaderMandatoryFlag;
	}

	public String getDsHeaderCategory() {
		return dsHeaderCategory;
	}

	public void setDsHeaderCategory(String dsHeaderCategory) {
		this.dsHeaderCategory = dsHeaderCategory;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((dsHeaderCategory == null) ? 0 : dsHeaderCategory.hashCode());
		result = prime * result + (dsHeaderMandatoryFlag ? 1231 : 1237);
		result = prime * result + ((dsHeaderName == null) ? 0 : dsHeaderName.hashCode());
		result = prime * result + ds_header_row_id;
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
		if (dsHeaderCategory == null) {
			if (other.dsHeaderCategory != null)
				return false;
		} else if (!dsHeaderCategory.equals(other.dsHeaderCategory))
			return false;
		if (dsHeaderMandatoryFlag != other.dsHeaderMandatoryFlag)
			return false;
		if (dsHeaderName == null) {
			if (other.dsHeaderName != null)
				return false;
		} else if (!dsHeaderName.equals(other.dsHeaderName))
			return false;
		if (ds_header_row_id != other.ds_header_row_id)
			return false;
		return true;
	}
}