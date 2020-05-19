package com.techm.orion.pojo;

public class ModifyConfigResultPojo {
	
	private String Assigned_Field_Name ;
	private String No_SSH_Command;
	private String Create_SSH_Command;
	public String getAssigned_Field_Name() {
		return Assigned_Field_Name;
	}
	public void setAssigned_Field_Name(String assigned_Field_Name) {
		Assigned_Field_Name = assigned_Field_Name;
	}
	public String getNo_SSH_Command() {
		return No_SSH_Command;
	}
	public void setNo_SSH_Command(String no_SSH_Command) {
		No_SSH_Command = no_SSH_Command;
	}
	public String getCreate_SSH_Command() {
		return Create_SSH_Command;
	}
	public void setCreate_SSH_Command(String create_SSH_Command) {
		Create_SSH_Command = create_SSH_Command;
	}
	@Override
	public String toString() {
		return "ModifyConfigResultPojo [Assigned_Field_Name="
				+ Assigned_Field_Name + ", No_SSH_Command=" + No_SSH_Command
				+ ", Create_SSH_Command=" + Create_SSH_Command + "]";
	}
	
}
