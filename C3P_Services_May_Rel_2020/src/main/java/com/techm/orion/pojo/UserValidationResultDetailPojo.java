package com.techm.orion.pojo;

public class UserValidationResultDetailPojo {

    String Message;
    boolean result;
    int privilegeLevel;
    public int getPrivilegeLevel() {
		return privilegeLevel;
	}
	public void setPrivilegeLevel(int privilegeLevel) {
		this.privilegeLevel = privilegeLevel;
	}
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
    
}
