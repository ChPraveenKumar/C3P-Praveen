package com.techm.orion.pojo;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

@Entity
@Table(name = "c3p_template_master_command_list")
public class CommandPojo implements Comparable<CommandPojo> {

	@GeneratedValue(strategy = GenerationType.AUTO)
	@Id
	@Column(name = "id")
	private int rid;

	@Column(name = "command_value")
	String command_value;

	@Transient
	String commandValue;

	@Column(name = "command_id")
	int command_id;

	@Column(name = "command_type")
	String command_type;

	@Column(name = "command_sequence_id")
	int command_sequence_id;

	@Transient
	int commandSequenceId;

	@Column(name = "no_command_value")
	String no_command_value;

	@Column(name = "command_replication_ind")
	String command_replication_ind;

	@Column(name = "master_f_id")
	String masterFId;

	@Transient
	String id;

	@Transient
	int position;

	@Transient
	boolean isNew = false;

	@Transient
	int is_save;

	@Transient
	String tempId;

	public String getCommand_type() {
		return command_type;
	}

	public void setCommand_type(String command_type) {
		this.command_type = command_type;
	}

	public String getMasterFId() {
		return masterFId;
	}

	public void setMasterFId(String masterFId) {
		this.masterFId = masterFId;
	}

	public String getCommand_replication_ind() {
		return command_replication_ind;
	}

	public void setCommand_replication_ind(String command_replication_ind) {
		this.command_replication_ind = command_replication_ind;
	}

	public String getMaster_f_id() {
		return masterFId;
	}

	public void setMaster_f_id(String master_f_id) {
		this.masterFId = master_f_id;
	}

	public void setCommand_id(int command_id) {
		this.command_id = command_id;
	}

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
	public int compareTo(CommandPojo o) {
		int compareid = Integer.parseInt(o.getCommand_id());
		/* For Ascending order */
		int id = Integer.parseInt(this.id);
		return id - compareid;
	}

	@Override
	public String toString() {
		return command_value + "\n";
	}
}
