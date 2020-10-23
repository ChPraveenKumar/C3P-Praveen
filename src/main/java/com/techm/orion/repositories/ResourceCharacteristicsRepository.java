package com.techm.orion.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.techm.orion.entitybeans.ResourceCharacteristicsEntity;

@Repository
public interface ResourceCharacteristicsRepository extends
JpaRepository<ResourceCharacteristicsEntity, Long> {
}