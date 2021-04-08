package com.techm.orion.entitybeans;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONException;
import org.json.simple.JSONObject;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.techm.orion.rest.TemplateManagementService;

@Entity
@Table(name = "c3p_vnf_template_details")
@JsonIgnoreProperties(ignoreUnknown = false)
public class VNFTemplateEntity {

	private static final Logger logger = LogManager.getLogger(TemplateManagementService.class);

	@GeneratedValue(strategy = GenerationType.AUTO)
	@Id
	private int vtRowId;


	@Column(name = "vt_component_name")
	private String vtComponentName;


	@Column(name = "vt_component_parent_id")
	private int vtComponentParentId;


	public int getVtRowId() {
		return vtRowId;
	}


	public void setVtRowId(int vtRowId) {
		this.vtRowId = vtRowId;
	}


	public String getVtComponentName() {
		return vtComponentName;
	}


	public void setVtComponentName(String vtComponentName) {
		this.vtComponentName = vtComponentName;
	}


	public int getVtComponentParentId() {
		return vtComponentParentId;
	}


	public void setVtComponentParentId(int vtComponentParentId) {
		this.vtComponentParentId = vtComponentParentId;
	}

	 public VNFTemplateEntity(int invtRowId, String invtComponentName, int invtComponentParentId) {
		 vtRowId = invtRowId;
		 vtComponentName = invtComponentName;
		 vtComponentParentId = invtComponentParentId;
	    }

	 public JSONObject getJSONObject() {
	        JSONObject obj = new JSONObject();
	        try {
	            obj.put("ID", vtRowId);
	            obj.put("templateName", vtComponentName);
	            obj.put("PARENT_ID", vtComponentParentId);
	        } catch (JSONException e) {
				logger.error("Cannot convert to json object"+e.getMessage());
	        }
	        return obj;
	    }
	 
	 public VNFTemplateEntity(){}
}
