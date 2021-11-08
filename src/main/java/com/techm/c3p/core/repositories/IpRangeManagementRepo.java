package com.techm.c3p.core.repositories;

import java.util.List;
import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.techm.c3p.core.entitybeans.IpRangeManagementEntity;

@Repository
public interface IpRangeManagementRepo extends JpaRepository<IpRangeManagementEntity, Long> {

	IpRangeManagementEntity findByRangeIpRangeAndRangeMask(String rangeIpRange, String rangeMask);

	IpRangeManagementEntity findByRangeStartIpAndRangeEndIp(String startIp, String endIp);

	List<IpRangeManagementEntity> findByRangeStatus(String rangeStatus);

	IpRangeManagementEntity findByRangeIpRange(String rangeStatus);

}