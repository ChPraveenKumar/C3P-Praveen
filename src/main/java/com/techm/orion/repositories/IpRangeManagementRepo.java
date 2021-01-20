package com.techm.orion.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.techm.orion.entitybeans.IpRangeManagementEntity;

@Repository
public interface IpRangeManagementRepo extends JpaRepository<IpRangeManagementEntity, Long> {
	
	IpRangeManagementEntity findByRangeIpRange(String rangeIpRange);
	
}