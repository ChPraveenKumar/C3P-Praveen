package com.techm.orion.repositories;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.techm.orion.entitybeans.TestDetail;
import com.techm.orion.entitybeans.TestFeatureList;

@Repository
public interface TestFeatureListRepository extends CrudRepository<TestFeatureList, Integer> {

	List<TestFeatureList> findByTestDetail(TestDetail testDetail);

	List<TestFeatureList> findByTestFeature(String featureId);

}
