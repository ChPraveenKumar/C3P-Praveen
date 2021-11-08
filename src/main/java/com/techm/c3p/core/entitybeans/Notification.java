package com.techm.c3p.core.entitybeans;

import java.sql.Timestamp;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "c3p_notification")
public class Notification {

	@GeneratedValue(strategy = GenerationType.AUTO)
	@Id
	@Column(name = "notif_id", length = 20)
	private int id;

	@Column(name = "notif_from_user", length = 45)
	private String notifFromUser;

	@Column(name = "notif_to_user", length = 255)
	private String notifToUser;

	@Column(name = "notif_type", length = 45)
	private String notifType;

	@Column(name = "notif_created_date")
	private Timestamp notifCreatedDate;

	@Column(name = "notif_reference", length = 255)
	private String notifReference;

	@Column(name = "notif_message", length = 255)
	private String notifMessage;

	@Column(name = "notif_priority ", length = 5)
	private String notifPriority =null;

	@Column(name = "notif_readby", length = 255)
	private String notifReadby;

	@Column(name = "notif_to_workgroup", length = 45)
	private String notifToWorkgroup;

	@Column(name = "notif_status", length = 45)
	private String notifStatus;

	@Column(name = "notif_completedby", length = 45)
	private String notifCompletedby;

	@Column(name = "notif_expiry_date")
	private Timestamp notifExpiryDate;

	@Column(name = "notif_label", length = 255)
	private String notifLabel;
	
	
	public String getNotifLabel() {
		return notifLabel;
	}

	public void setNotifLabel(String notifLabel) {
		this.notifLabel = notifLabel;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getNotifFromUser() {
		return notifFromUser;
	}

	public void setNotifFromUser(String notifFromUser) {
		this.notifFromUser = notifFromUser;
	}

	public String getNotifToUser() {
		return notifToUser;
	}

	public void setNotifToUser(String notifToUser) {
		this.notifToUser = notifToUser;
	}

	public String getNotifType() {
		return notifType;
	}

	public void setNotifType(String notifType) {
		this.notifType = notifType;
	}

	public Timestamp getNotifCreatedDate() {
		return notifCreatedDate;
	}

	public void setNotifCreatedDate(Timestamp notifCreatedDate) {
		this.notifCreatedDate = notifCreatedDate;
	}

	public String getNotifReference() {
		return notifReference;
	}

	public void setNotifReference(String notifReference) {
		this.notifReference = notifReference;
	}

	public String getNotifMessage() {
		return notifMessage;
	}

	public void setNotifMessage(String notifMessage) {
		this.notifMessage = notifMessage;
	}

	public String getNotifPriority() {
		return notifPriority;
	}

	public void setNotifPriority(String notifPriority) {
		this.notifPriority = notifPriority;
	}

	public String getNotifReadby() {
		return notifReadby;
	}

	public void setNotifReadby(String notifReadby) {
		this.notifReadby = notifReadby;
	}

	public String getNotifToWorkgroup() {
		return notifToWorkgroup;
	}

	public void setNotifToWorkgroup(String notifToWorkgroup) {
		this.notifToWorkgroup = notifToWorkgroup;
	}

	public String getNotifStatus() {
		return notifStatus;
	}

	public void setNotifStatus(String notifStatus) {
		this.notifStatus = notifStatus;
	}

	public String getNotifCompletedby() {
		return notifCompletedby;
	}

	public void setNotifCompletedby(String notifCompletedby) {
		this.notifCompletedby = notifCompletedby;
	}

	public Timestamp getNotifExpiryDate() {
		return notifExpiryDate;
	}

	public void setNotifExpiryDate(Timestamp notifExpiryDate) {
		this.notifExpiryDate = notifExpiryDate;
	}

	public Notification() {
		super();
	}

	public Notification(int id, String notifType, String notifReference, String notifMessage, String notifReadby,
			String notifLabel) {
		super();
		this.id = id;
		this.notifType = notifType;
		this.notifReference = notifReference;
		this.notifMessage = notifMessage;
		this.notifReadby = notifReadby;
		this.notifLabel = notifLabel;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + id;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Notification other = (Notification) obj;
		if (id != other.id)
			return false;
		return true;
	}
	
}
