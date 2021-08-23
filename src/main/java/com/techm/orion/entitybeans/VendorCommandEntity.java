package com.techm.orion.entitybeans;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "c3p_m_vendor_specific_command")
public class VendorCommandEntity {

	@Id
	@Column(name = "vc_row_id")
	@GeneratedValue(strategy = GenerationType.AUTO)
	private int vcRowid;

	@Column(name = "vc_record_id", length = 30)
	private String vcRecordId;

	@Column(name = "vc_append", length = 100)
	private String vcAppend;

	@Column(name = "vc_end", length = 100)
	private String vcEnd;

	@Column(name = "vc_parent_id", length = 30)
	private String vcParentId;

	@Column(name = "vc_repetition", length = 20)
	private String vcRepetition;

	@Column(name = "vc_start", length = 100)
	private String vcStart;

	@Column(name = "vc_vendor_name", length = 100)
	private String vcVendorName;

	@Column(name = "vc_extetced_response", length = 100)
	private String vcExtetcedResponse;

	@Column(name = "vc_network_type", length = 40)
	private String vcNetworkType;

	@Column(name = "vc_os", length = 100)
	private String vcOs;
	
	@Column(name = "vc_is_applicable")
	private Boolean vCisApplicable;

	public int getVcRowid() {
		return vcRowid;
	}

	public void setVcRowid(int vcRowid) {
		this.vcRowid = vcRowid;
	}

	public String getVcParentId() {
		return vcParentId;
	}

	public void setVcParentId(String vcParentId) {
		this.vcParentId = vcParentId;
	}

	public String getVcStart() {
		return vcStart;
	}

	public void setVcStart(String vcStart) {
		this.vcStart = vcStart;
	}

	public String getVcEnd() {
		return vcEnd;
	}

	public void setVcEnd(String vcEnd) {
		this.vcEnd = vcEnd;
	}

	public String getVcAppend() {
		return vcAppend;
	}

	public void setVcAppend(String vcAppend) {
		this.vcAppend = vcAppend;
	}

	public String getVcVendorName() {
		return vcVendorName;
	}

	public void setVcVendorName(String vcVendorName) {
		this.vcVendorName = vcVendorName;
	}

	public String getVcRepetition() {
		return vcRepetition;
	}

	public void setVcRepetition(String vcRepetition) {
		this.vcRepetition = vcRepetition;
	}

	public String getVcRecordId() {
		return vcRecordId;
	}

	public void setVcRecordId(String vcRecordId) {
		this.vcRecordId = vcRecordId;
	}

	public String getVcExtetcedResponse() {
		return vcExtetcedResponse;
	}

	public void setVcExtetcedResponse(String vcExtetcedResponse) {
		this.vcExtetcedResponse = vcExtetcedResponse;
	}

	public String getVcNetworkType() {
		return vcNetworkType;
	}

	public void setVcNetworkType(String vcNetworkType) {
		this.vcNetworkType = vcNetworkType;
	}

	public String getVcOs() {
		return vcOs;
	}

	public void setVcOs(String vcOs) {
		this.vcOs = vcOs;
	}
	
	public Boolean isvCisApplicable() {
		return vCisApplicable;
	}

	public void setvCisApplicable(Boolean vCisApplicable) {
		this.vCisApplicable = vCisApplicable;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + vcRowid;
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
		VendorCommandEntity other = (VendorCommandEntity) obj;
		if (vcRowid != other.vcRowid)
			return false;
		return true;
	}

}
