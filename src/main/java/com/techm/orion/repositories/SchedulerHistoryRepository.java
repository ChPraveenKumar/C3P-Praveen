package com.techm.orion.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.techm.orion.entitybeans.SchedulerHistoryEntity;
@Repository
public interface SchedulerHistoryRepository extends JpaRepository<SchedulerHistoryEntity, Long> {

}
