package com.techm.c3p.core.repositories;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.techm.c3p.core.entitybeans.WorkGroup;;

@Repository
public interface WorkGroupRepository extends JpaRepository<WorkGroup, Long> {

	WorkGroup findByWorkGroupName(String workGroupName);
	WorkGroup findById(int id);	
	List<WorkGroup> findAll();
	@Query("SELECT workGroupName FROM WorkGroup")
	List<WorkGroup> findName();
	
	WorkGroup findByDefaultRole(String role);
	
	List<WorkGroup> findByWorkGroupNameOrDefaultRoleOrDescriptionOrCreatedBy(String workGroupName, String role, String description, String createdBy);
	
	List<WorkGroup> findAllByOrderByCreatedDateDesc();
	
	WorkGroup findAllByWorkGroupIdAndWorkGroupType(String projectId,String type);
	
	List<WorkGroup> findByWorkGroupNameContains(String workGroupName);
	
	WorkGroup findNameById(int id);
}