package com.techm.orion.repositories;

import java.util.List;
import java.util.Set;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import com.techm.orion.entitybeans.OS;
import com.techm.orion.entitybeans.OSversion;
import com.techm.orion.entitybeans.TestDetail;
import com.techm.orion.entitybeans.TestRules;

public interface TestRulesRepository extends CrudRepository<TestRules, Integer> {

	List<TestRules> findByTestDetail(TestDetail testDetail);

	List<TestRules> findById(int ruleid);
	
	


	
	
	
	

}
