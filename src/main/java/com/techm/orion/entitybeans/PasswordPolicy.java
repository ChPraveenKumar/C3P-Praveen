package com.techm.orion.entitybeans;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import javax.persistence.Table;

import org.springframework.stereotype.Component;

@Component
@Entity
@Table(name="password_policy")
public class PasswordPolicy implements Serializable {
	
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;
	
	@Column(name="min_length")
	private int minLength;
	
	@Column(name="enforce_history")
	private int enforceHistory;

	@Column(name="complexity")
	private String Complexity;
	
	@Column(name="match_password")
	private String match;
	
	@Column(name="lockout")
	private String lockout;
	
	@Column(name="max_attempts")
	private int maxAttempts;
	
	@Column(name="lockout_period")
	private int lockoutPeriod;

	public int getMinLength() {
		return minLength;
	}

	public void setMinLength(int minLength) {
		this.minLength = minLength;
	}

	public int getEnforceHistory() {
		return enforceHistory;
	}

	public void setEnforceHistory(int enforceHistory) {
		this.enforceHistory = enforceHistory;
	}

	public String getComplexity() {
		return Complexity;
	}

	public void setComplexity(String complexity) {
		Complexity = complexity;
	}

	public String getMatch() {
		return match;
	}

	public void setMatch(String match) {
		this.match = match;
	}

	public String getLockout() {
		return lockout;
	}

	public void setLockout(String lockout) {
		this.lockout = lockout;
	}

	public int getMaxAttempts() {
		return maxAttempts;
	}

	public void setMaxAttempts(int maxAttempts) {
		this.maxAttempts = maxAttempts;
	}
	
	public static long getSerialversionuid() {
		return serialVersionUID;
	}


	public int getLockoutPeriod() {
		return lockoutPeriod;
	}

	public void setLockoutPeriod(int lockoutPeriod) {
		this.lockoutPeriod = lockoutPeriod;
	}
	
}