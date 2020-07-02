package com.techm.orion.service;

import java.util.List;
import com.techm.orion.entitybeans.WorkGroup;

public interface WorkGroupInterface {

	List<WorkGroup> getAllWorkGroup();
	List<WorkGroup> getAllWorkGroupName();
	WorkGroup getWorkGroupById(int moduleId);
	WorkGroup getWorkGroupByName(String moduleName);
	void deleteById(int moduleId);
	WorkGroup updateWorkGroup(WorkGroup module);
}
