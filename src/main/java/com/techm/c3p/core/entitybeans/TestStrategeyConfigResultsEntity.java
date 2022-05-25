package com.techm.c3p.core.entitybeans;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "c3p_t_tststrategy_m_config_results")
public class TestStrategeyConfigResultsEntity implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5997438945156499374L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private int id;

	@Column(name = "request_id", length = 40)
	private String requestId;

	@Column(name = "test_category", length = 300)
	private String testCategory;

	@Column(name = "test_result", length = 40)
	private String testResult;
	
	@Column(name = "result_text", length = 40)
	private String resultText;
	
	@Column(name = "test_name", length = 250)
	private String testName;
	
	@Column(name = "collected_value", length = 40)
	private String collectedValue;
	
	@Column(name = "evaluation_criteria", length = 40)
	private String evaluationCriteria;
	
	@Column(name = "notes", length = 40)
	private String notes;
	
	@Column(name = "data_type", length = 40)
	private String dataType;
	
	@Column(name = "test_sub_category", length = 40)
	private String testSubCategory;
	
	@Column(name = "request_version", length = 10)
	private double requestVersion;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getRequestId() {
		return requestId;
	}

	public void setRequestId(String requestId) {
		this.requestId = requestId;
	}

	public String getTestCategory() {
		return testCategory;
	}

	public void setTestCategory(String testCategory) {
		this.testCategory = testCategory;
	}

	public String getTestResult() {
		return testResult;
	}

	public void setTestResult(String testResult) {
		this.testResult = testResult;
	}

	public String getResultText() {
		return resultText;
	}

	public void setResultText(String resultText) {
		this.resultText = resultText;
	}

	public String getTestName() {
		return testName;
	}

	public void setTestName(String testName) {
		this.testName = testName;
	}

	public String getCollectedValue() {
		return collectedValue;
	}

	public void setCollectedValue(String collectedValue) {
		this.collectedValue = collectedValue;
	}

	public String getEvaluationCriteria() {
		return evaluationCriteria;
	}

	public void setEvaluationCriteria(String evaluationCriteria) {
		this.evaluationCriteria = evaluationCriteria;
	}

	public String getNotes() {
		return notes;
	}

	public void setNotes(String notes) {
		this.notes = notes;
	}

	public String getDataType() {
		return dataType;
	}

	public void setDataType(String dataType) {
		this.dataType = dataType;
	}

	public String getTestSubCategory() {
		return testSubCategory;
	}

	public void setTestSubCategory(String testSubCategory) {
		this.testSubCategory = testSubCategory;
	}

	public double getRequestVersion() {
		return requestVersion;
	}

	public void setRequestVersion(double requestVersion) {
		this.requestVersion = requestVersion;
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
		TestStrategeyConfigResultsEntity other = (TestStrategeyConfigResultsEntity) obj;
		if (id != other.id)
			return false;
		return true;
	}
}