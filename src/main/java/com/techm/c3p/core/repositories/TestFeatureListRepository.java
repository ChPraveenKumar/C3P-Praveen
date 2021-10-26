package com.techm.c3p.core.repositories;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.techm.c3p.core.entitybeans.TestDetail;
import com.techm.c3p.core.entitybeans.TestFeatureList;

@Repository
public interface TestFeatureListRepository extends CrudRepository<TestFeatureList, Integer> {

	List<TestFeatureList> findByTestDetail(TestDetail testDetail);

	List<TestFeatureList> findByTestFeature(String featureId);

}
