package com.techm.c3p.core.repositories;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import com.techm.c3p.core.entitybeans.TestDetail;
import com.techm.c3p.core.entitybeans.TestRules;

public interface TestRulesRepository extends CrudRepository<TestRules, Integer> {

	List<TestRules> findByTestDetail(TestDetail testDetail);

	List<TestRules> findById(int ruleid);

}
