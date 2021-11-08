package com.techm.c3p.core.service;

import java.util.List;

import com.techm.c3p.core.entitybeans.WorkGroup;

public interface WorkGroupInterface {

	List<WorkGroup> getAllWorkGroup();
	List<WorkGroup> getAllWorkGroupName();
	WorkGroup getWorkGroupById(int moduleId);
	WorkGroup getWorkGroupByName(String moduleName);
	void deleteById(int moduleId);
	WorkGroup updateWorkGroup(WorkGroup module);
}
