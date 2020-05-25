package com.techm.orion.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.techm.orion.entitybeans.TestDetail;
import com.techm.orion.entitybeans.TestStrategeBasicConfigurationEntity;

public interface TestStrategeBasicConfigurationRepository extends JpaRepository<TestStrategeBasicConfigurationEntity, Integer> {

}
