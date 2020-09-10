package com.techm.orion.models;

import java.util.ArrayList;
import java.util.List;


import com.techm.orion.pojo.TestBundlePojo;

public class TestBundleModel {
	
	List<TestBundlePojo>tests=new ArrayList<TestBundlePojo>();

	private String name;
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}

	public List<TestBundlePojo> getTests() {
		return tests;
	}
	public void setTests(List<TestBundlePojo> tests) {
		this.tests = tests;
	}

}
