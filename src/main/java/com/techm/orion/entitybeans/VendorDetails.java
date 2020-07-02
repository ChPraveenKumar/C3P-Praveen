package com.techm.orion.entitybeans;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

/*
 * Owner: Vivek Vidhate Module: Test Strategey Logic: To
 * Get, Save, edit, tree structure and show Network Audit tests for all rules(Text, Table, Section, Snippet, Keyword)
 * This class will work as entity class
 */
@Entity
@Table(name = "c3p_vendor_details")
public class VendorDetails implements Serializable

{

	/**
	 * 
	 */
	private static final long serialVersionUID = 3381690641270278930L;

	@GeneratedValue(strategy = GenerationType.AUTO)
	@Id
	private int id;

	@Column(name = "command")
	private String command;

	@Column(name = "priority")
	private String priority;

	@Column(name = "vendor")
	private String vendor;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getVendor() {
		return vendor;
	}

	public void setVendor(String vendor) {
		this.vendor = vendor;
	}

	public String getCommand() {
		return command;
	}

	public void setCommand(String command) {
		this.command = command;
	}

	public String getPriority() {
		return priority;
	}

	public void setPriority(String priority) {
		this.priority = priority;
	}

}