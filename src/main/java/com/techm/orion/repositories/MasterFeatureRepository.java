package com.techm.orion.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.techm.orion.entitybeans.MasterFeatureEntity;

@Repository
@Transactional
public interface MasterFeatureRepository extends
		JpaRepository<MasterFeatureEntity, Long> {

}
