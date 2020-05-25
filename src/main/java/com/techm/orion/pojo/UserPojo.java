package com.techm.orion.pojo;

public class UserPojo {
    private String username = null;
    private String password = null;
    private int privilegeLevel=0;
    public int getPrivilegeLevel() {
		return privilegeLevel;
	}
	public void setPrivilegeLevel(int privilegeLevel) {
		this.privilegeLevel = privilegeLevel;
	}
	/**
     * @return the username
     */
    public String getUsername() {
        return username;
    }
    /**
     * @param username the username to set
     */
    public void setUsername(String username) {
        this.username = username;
    }
    /**
     * @return the password
     */
    public String getPassword() {
        return password;
    }
    /**
     * @param password the password to set
     */
    public void setPassword(String password) {
        this.password = password;
    }
    
}
