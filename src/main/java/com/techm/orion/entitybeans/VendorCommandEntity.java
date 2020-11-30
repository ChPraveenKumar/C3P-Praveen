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

	@Column(name = "vc_parent_id")
	private Integer vcParentId;

	@Column(name = "vc_start", length = 100)
	private String vcStart;
	
	@Column(name = "vc_end", length = 100)
	private String vcEnd;
	
	@Column(name = "vc_append", length = 100)
	private String vcAppend;
	
	@Column(name = "vc_vendor_name", length = 100)
	private String vcVendorName;
	
	@Column(name = "vc_repetition", length = 100)
	private String vcRepetition;

	public int getVcRowid() {
		return vcRowid;
	}

	public void setVcRowid(int vcRowid) {
		this.vcRowid = vcRowid;
	}

	public Integer getVcParentId() {
		return vcParentId;
	}

	public void setVcParentId(Integer vcParentId) {
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
	
}
