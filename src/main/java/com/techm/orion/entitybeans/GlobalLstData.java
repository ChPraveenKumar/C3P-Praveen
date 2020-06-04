package com.techm.orion.entitybeans;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "T_TPMGMT_GLBLIST_M_Globallistmetadata")
public class GlobalLstData implements Serializable {

	@Id
	private int id;

	private String globallist;
	
	private String url;

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getGloballist() {
		return globallist;
	}

	public void setGloballist(String globallist) {
		this.globallist = globallist;
	}


}
