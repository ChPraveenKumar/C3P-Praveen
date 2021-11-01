package com.techm.c3p.core.pojo;

import org.json.simple.JSONObject;

public class UserManagementResulltDetailPojo {

	boolean result;
	String Message;
	String role;
	String firstName;
	String lastName;
	String workGroup;
	String userName;
	String baseLocation;
	boolean isSuperUser;
	JSONObject moduleInfo;

	/**
	 * @return the errorMessage
	 */
	public String getMessage() {
		return Message;
	}

	/**
	 * @param errorMessage the errorMessage to set
	 */
	public void setMessage(String errorMessage) {
		this.Message = errorMessage;
	}

	/**
	 * @return the result
	 */
	public boolean isResult() {
		return result;
	}

	/**
	 * @param result the result to set
	 */
	public void setResult(boolean result) {
		this.result = result;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getRole() {
		return role;
	}

	public void setRole(String role) {
		this.role = role;
	}

	public String getWorkGroup() {
		return workGroup;
	}

	public void setWorkGroup(String workGroup) {
		this.workGroup = workGroup;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getBaseLocation() {
		return baseLocation;
	}

	public void setBaseLocation(String baseLocation) {
		this.baseLocation = baseLocation;
	}

	public JSONObject getModuleInfo() {
		return moduleInfo;
	}

	public void setModuleInfo(JSONObject moduleInfo) {
		this.moduleInfo = moduleInfo;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public boolean isSuperUser() {
		return isSuperUser;
	}

	public void setSuperUser(boolean isSuperUser) {
		this.isSuperUser = isSuperUser;
	}
}
