package com.techm.c3p.core.entitybeans;

import java.io.Serializable;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;

/*
 * Owner: Vivek Vidhate Module: Test Strategey Logic: To
 * Get, Save, edit, tree structure and show Network Audit tests for all rules(Text, Table, Section, Snippet, Keyword)
 * This class will work as entity class
 */
@Entity
@Table(name = "T_TSTSTRATEGY_M_TSTRULES")
public class TestRules implements Serializable

{

	/**
	 * 
	 */
	private static final long serialVersionUID = -645443588346704742L;

	@GeneratedValue(strategy = GenerationType.AUTO)
	@Id
	private int id;

	@JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY, cascade = { CascadeType.MERGE, CascadeType.PERSIST })
	@JoinColumn(name = "testName")
	private TestDetail testDetail;

	@Column(name = "reportedLabel")
	private String reportedLabel;

	@Column(name = "dataType")
	private String dataType;

	@Column(name = "beforeText")
	private String beforeText;

	@Column(name = "afterText")
	private String afterText;

	@Column(name = "numberOfChars")
	private String numberOfChars;

	@Column(name = "fromColumn")
	private String fromColumn;

	@Column(name = "referenceColumn")
	private String referenceColumn;

	@Column(name = "whereKeyword")
	private String whereKeyword;

	@Column(name = "sectionName")
	private String sectionName;

	@Column(name = "evaluation")
	private String evaluation;

	@Column(name = "operator")
	private String operator;

	@Column(name = "value1")
	private String value1;

	@Column(name = "value2")
	private String value2;

	@Column(name = "snippet")
	private String snippet;

	@Column(name = "keyword")
	private String keyword;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public TestDetail getTestDetail() {
		return testDetail;
	}

	public void setTestDetail(TestDetail testDetail) {
		this.testDetail = testDetail;
	}

	public String getReportedLabel() {
		return reportedLabel;
	}

	public void setReportedLabel(String reportedLabel) {
		this.reportedLabel = reportedLabel;
	}

	public String getDataType() {
		return dataType;
	}

	public void setDataType(String dataType) {
		this.dataType = dataType;
	}

	public String getBeforeText() {
		return beforeText;
	}

	public void setBeforeText(String beforeText) {
		this.beforeText = beforeText;
	}

	public String getAfterText() {
		return afterText;
	}

	public void setAfterText(String afterText) {
		this.afterText = afterText;
	}

	public String getNumberOfChars() {
		return numberOfChars;
	}

	public void setNumberOfChars(String numberOfChars) {
		this.numberOfChars = numberOfChars;
	}

	public String getFromColumn() {
		return fromColumn;
	}

	public void setFromColumn(String fromColumn) {
		this.fromColumn = fromColumn;
	}

	public String getReferenceColumn() {
		return referenceColumn;
	}

	public void setReferenceColumn(String referenceColumn) {
		this.referenceColumn = referenceColumn;
	}

	public String getWhereKeyword() {
		return whereKeyword;
	}

	public void setWhereKeyword(String whereKeyword) {
		this.whereKeyword = whereKeyword;
	}

	public String getSectionName() {
		return sectionName;
	}

	public void setSectionName(String sectionName) {
		this.sectionName = sectionName;
	}

	public String getEvaluation() {
		return evaluation;
	}

	public void setEvaluation(String evaluation) {
		this.evaluation = evaluation;
	}

	public String getOperator() {
		return operator;
	}

	public void setOperator(String operator) {
		this.operator = operator;
	}

	public String getValue1() {
		return value1;
	}

	public void setValue1(String value1) {
		this.value1 = value1;
	}

	public String getValue2() {
		return value2;
	}

	public void setValue2(String value2) {
		this.value2 = value2;
	}

	public String getSnippet() {
		return snippet;
	}

	public void setSnippet(String snippet) {
		this.snippet = snippet;
	}

	public String getKeyword() {
		return keyword;
	}

	public void setKeyword(String keyword) {
		this.keyword = keyword;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		TestRules other = (TestRules) obj;
		if (id != other.id)
			return false;
		return true;
	}
}
