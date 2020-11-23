package com.techm.orion.entitybeans;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "c3p_template_transaction_command_list")
public class TemplateCommands implements Serializable {

	private static final long serialVersionUID = 1L;

	@GeneratedValue(strategy = GenerationType.AUTO)
	@Id
	@Column(name = "id", length = 10)
	private int id;

	@Column(name = "command_id", length = 100)
	String commandId;

	@Column(name = "command_sequence_id", length = 30)
	int commandSequenceId;

	@Column(name = "command_template_id", length = 50)
	String commandTemplateId;

	@Column(name = "command_position", length = 6)
	int commandPosition;

	@Column(name = "is_save", length = 11)
	int isSave;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getCommandId() {
		return commandId;
	}

	public void setCommandId(String commandId) {
		this.commandId = commandId;
	}

	public int getCommandSequenceId() {
		return commandSequenceId;
	}

	public void setCommandSequenceId(int commandSequenceId) {
		this.commandSequenceId = commandSequenceId;
	}

	public String getCommandTemplateId() {
		return commandTemplateId;
	}

	public void setCommandTemplateId(String commandTemplateId) {
		this.commandTemplateId = commandTemplateId;
	}

	public int getCommandPosition() {
		return commandPosition;
	}

	public void setCommandPosition(int commandPosition) {
		this.commandPosition = commandPosition;
	}

	public int getIsSave() {
		return isSave;
	}

	public void setIsSave(int isSave) {
		this.isSave = isSave;
	}
}
