package com.techm.orion.models;

import java.util.ArrayList;
import java.util.List;


import com.techm.orion.pojo.TestBundlePojo;

public class TestBundleModel {
	
	private List<TestBundlePojo>tests=new ArrayList<TestBundlePojo>();

	public List<TestBundlePojo> getTests() {
		return tests;
	}
	public void setTests(List<TestBundlePojo> tests) {
		this.tests = tests;
	}
	private String name;
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}

	public boolean addTest(TestBundlePojo test) {
		return tests.add(test);
	}
	public void removeTest(TestBundlePojo test) {
		tests.remove(test);
	}


}
