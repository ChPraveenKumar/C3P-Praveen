package com.techm.c3p.core.entitybeans;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Entity
@Table(name = "errorcodedata")
@JsonIgnoreProperties(ignoreUnknown = false)
public class ErrorValidationEntity implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8335538017837461950L;

	@GeneratedValue(strategy = GenerationType.AUTO)
	@Id
	private int id;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	@Column(name = "ErrorId")
	private String ErrorId;

	@Column(name = "ErrorType")
	private String ErrorType;

	@Column(name = "ErrorDescription")
	private String ErrorDescription;

	@Column(name = "router_error_message")
	private String router_error_message;

	@Column(name = "suggestion")
	private String suggestion;

	@Column(name = "category")
	private String category;

	public String getErrorId() {
		return ErrorId;
	}

	public void setErrorId(String errorId) {
		ErrorId = errorId;
	}

	public String getErrorType() {
		return ErrorType;
	}

	public void setErrorType(String errorType) {
		ErrorType = errorType;
	}

	public String getErrorDescription() {
		return ErrorDescription;
	}

	public void setErrorDescription(String errorDescription) {
		ErrorDescription = errorDescription;
	}

	public String getRouter_error_message() {
		return router_error_message;
	}

	public void setRouter_error_message(String router_error_message) {
		this.router_error_message = router_error_message;
	}

	public String getSuggestion() {
		return suggestion;
	}

	public void setSuggestion(String suggestion) {
		this.suggestion = suggestion;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
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
		ErrorValidationEntity other = (ErrorValidationEntity) obj;
		if (id != other.id)
			return false;
		return true;
	}

}
