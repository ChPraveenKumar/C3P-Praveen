package com.techm.c3p.core.entitybeans;


import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="c3p_m_heat_templates")
public class HeatTemplate{
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ht_row_id")
	private int rowId;
	
	@Column(name = "ht_vendor", length = 45)
	private String vendor;
	
	@Column(name = "ht_network_function", length = 10)
	private String networkFunction;
	
	@Column(name = "ht_vm_type", length = 50)
	private String vmType;
	
	@Column(name = "ht_flavour", length = 50)
	private String flavour;
	
	@Column(name = "ht_heat_template_id", length = 255)
	private String heatTemplateId;
	
	@Column(name = "ht_variable_template_id", length = 255)
	private String variableTemplateId;
	
	@Column(name = "ht_feature_list", length = 255)
	private String featureList;
	
	
		
	public int getRowId() {
		return rowId;
	}

	public void setRowId(int rowId) {
		this.rowId = rowId;
	}

	public String getVendor() {
		return vendor;
	}

	public void setVendor(String vendor) {
		this.vendor = vendor;
	}

	public String getNetworkFunction() {
		return networkFunction;
	}

	public void setNetworkFunction(String networkFunction) {
		this.networkFunction = networkFunction;
	}

	public String getVmType() {
		return vmType;
	}

	public void setVmType(String vmType) {
		this.vmType = vmType;
	}

	public String getFlavour() {
		return flavour;
	}

	public void setFlavour(String flavour) {
		this.flavour = flavour;
	}

	public String getHeatTemplateId() {
		return heatTemplateId;
	}

	public void setHeatTemplateId(String heatTemplateId) {
		this.heatTemplateId = heatTemplateId;
	}

	public String getVariableTemplateId() {
		return variableTemplateId;
	}

	public void setVariableTemplateId(String variableTemplateId) {
		this.variableTemplateId = variableTemplateId;
	}

	public String getFeatureList() {
		return featureList;
	}

	public void setFeatureList(String featureList) {
		this.featureList = featureList;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + rowId;
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
		HeatTemplate other = (HeatTemplate) obj;
		if (rowId != other.rowId)
			return false;
		return true;
	}
	

}