package com.techm.orion.pojo;

public class CommandPojo implements Comparable {

	int command_sequence_id;
	String command_value;
	String commandValue;
	int commandSequenceId;
	String no_command_value;
	String id;
	int position;
	boolean isNew = false;
	int is_save;
	String tempId;

	public int getIs_save() {
		return is_save;
	}

	public void setIs_save(int is_save) {
		this.is_save = is_save;
	}

	public String getCommandValue() {
		return commandValue;
	}

	public void setCommandValue(String commandValue) {
		this.commandValue = commandValue;
	}

	public int getCommandSequenceId() {
		return commandSequenceId;
	}

	public void setCommandSequenceId(int commandSequenceId) {
		this.commandSequenceId = commandSequenceId;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public int getPosition() {
		return position;
	}

	public void setPosition(int position) {
		this.position = position;
	}

	public String getNo_command_value() {
		return no_command_value;
	}

	public void setNo_command_value(String no_command_value) {
		this.no_command_value = no_command_value;
	}

	public boolean isNew() {
		return isNew;
	}

	public void setNew(boolean isNew) {
		this.isNew = isNew;
	}

	boolean checked = false;

	public boolean isChecked() {
		return checked;
	}

	public void setChecked(boolean checked) {
		this.checked = checked;
	}

	public int getCommand_sequence_id() {
		return command_sequence_id;
	}

	public void setCommand_sequence_id(int command_sequence_id) {
		this.command_sequence_id = command_sequence_id;
	}

	public String getCommand_value() {
		return command_value;
	}

	public void setCommand_value(String command_value) {
		this.command_value = command_value;
	}

	public String getCommand_id() {
		return id;
	}

	public void setCommand_id(String command_id) {
		this.id = command_id;
	}

	public String getTempId() {
		return tempId;
	}

	public void setTempId(String tempId) {
		this.tempId = tempId;
	}


	@Override
	public int compareTo(Object o) {
		// TODO Auto-generated method stub
		int compareid = Integer.parseInt(((CommandPojo) o).getCommand_id());
		/* For Ascending order */
		int id=Integer.parseInt(this.id);
		return id - compareid;
	}

	@Override
    public String toString() {
        return command_value+"\n";
    }
}
