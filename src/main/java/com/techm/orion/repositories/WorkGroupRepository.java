package com.techm.orion.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.techm.orion.entitybeans.Module;
import com.techm.orion.entitybeans.WorkGroup;;

@Repository
public interface WorkGroupRepository extends JpaRepository<WorkGroup, Long> {

	WorkGroup findByWorkGroupName(String workGroupName);
	WorkGroup findById(int moduleId);	
	List<WorkGroup> findAll();
	@Query("SELECT workGroupName FROM WorkGroup")
	List<WorkGroup> findName();
}
