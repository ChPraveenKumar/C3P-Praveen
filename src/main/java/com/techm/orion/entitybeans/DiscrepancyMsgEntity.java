package com.techm.orion.entitybeans;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "c3p_discripancy_msg")
public class DiscrepancyMsgEntity {

	@GeneratedValue(strategy = GenerationType.AUTO)
	@Id
	private int id;

	@Column(name = "discripancy_type", length = 25)
	private String discripancyType;

	@Column(name = "discripancy_msg")
	private String discripancyMsg;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getDiscripancyType() {
		return discripancyType;
	}

	public void setDiscripancyType(String discripancyType) {
		this.discripancyType = discripancyType;
	}

	public String getDiscripancyMsg() {
		return discripancyMsg;
	}

	public void setDiscripancyMsg(String discripancyMsg) {
		this.discripancyMsg = discripancyMsg;
	}

}
