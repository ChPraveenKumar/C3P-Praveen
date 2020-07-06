package com.techm.orion.serviceImpl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.techm.orion.entitybeans.WorkGroup;
import com.techm.orion.service.WorkGroupInterface;
import com.techm.orion.repositories.WorkGroupRepository;

@Service
public class WorkGroupImpl implements WorkGroupInterface{

	@Autowired
	WorkGroupRepository workGroupRepository;
	
	
	public List<WorkGroup> getAllWorkGroup() {
		List<WorkGroup> listWorkGroup = workGroupRepository.findAll();
		return listWorkGroup;
	}

	public List<WorkGroup> getAllWorkGroupName() {
		List<WorkGroup> listWorkGroup = workGroupRepository.findName();
		return listWorkGroup;
	}
	
	public WorkGroup getWorkGroupById(int workGroupId) {
		WorkGroup workGroup = workGroupRepository.findById(workGroupId);
		return workGroup;
	}

	public void deleteById(int workGroupId) {
		workGroupRepository.delete((long) workGroupId);
	}

	public WorkGroup updateWorkGroup(WorkGroup workGroup1) {
		WorkGroup workGroup2 = workGroupRepository.findById(workGroup1.getId());
		if(workGroup2!=null){
			workGroup1.setId(workGroup2.getId());
			workGroupRepository.save(workGroup1);
			return workGroup1;
		}else{
			return null;
		}
	}

	public WorkGroup createWorkGroup(WorkGroup workGroup) {
		WorkGroup workGroup2 = workGroupRepository.save(workGroup);
		return workGroup2;
	}

	public WorkGroup getWorkGroupByName(String name) {
		WorkGroup workGroup = workGroupRepository.findByWorkGroupName(name);
		return workGroup;
	}
}