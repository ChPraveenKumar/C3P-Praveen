package com.techm.c3p.core.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.techm.c3p.core.entitybeans.TestStrategeyConfigResultsEntity;

@Repository
public interface TestStrategeyConfigResultsRepo extends JpaRepository<TestStrategeyConfigResultsEntity, Long> {

	TestStrategeyConfigResultsEntity findByRequestIdAndRequestVersion(String requestId, double requsetVersion);

	TestStrategeyConfigResultsEntity findByRequestId(String requestId);

	TestStrategeyConfigResultsEntity findByRequestIdAndTestCategory(String requestId, String testCategory);

	TestStrategeyConfigResultsEntity findByRequestIdAndTestNameAndRequestVersionAndTestCategoryAndTestSubCategory(
			String requestId, String testName, double requsetVersion, String category, String subCategory);

	TestStrategeyConfigResultsEntity findByRequestIdAndTestNameAndRequestVersion(String requestId, String testName,
			double requsetVersion);

	TestStrategeyConfigResultsEntity findByRequestIdAndTestCategoryAndRequestVersion(String requestId, String testtype,
			double requsetVersion);

	/*TestStrategeyConfigResultsEntity findByRequestIdAndRequestVersionAndTestCategory(String requestId,
			double requsetVersion, String testCategory);*/

}