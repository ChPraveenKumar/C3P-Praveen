package com.techm.orion.repositories;

import java.util.List;
import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.techm.orion.entitybeans.IpRangeManagementEntity;

@Repository
public interface IpRangeManagementRepo extends JpaRepository<IpRangeManagementEntity, Long> {
	
	IpRangeManagementEntity findByRangeIpRangeAndRangeMask(String rangeIpRange, String rangeMask);

	IpRangeManagementEntity findByRangeStartIpAndRangeEndIp(String startIp, String endIp);

	List<IpRangeManagementEntity> findByRangeStatus(String rangeStatus);

	IpRangeManagementEntity findByRangeIpRange(String rangeStatus);
	
	@Query(value = "select count(r_ip_pool_id) from c3p_ip_range_pool_mgmt where r_status =:status or r_status =:rangeStatus", nativeQuery = true)
	int getStatusCountNumber(@Param ("status") String status, @Param ("rangeStatus") String rangeStatus);
	
	@Query(value = "select count(r_ip_pool_id) from c3p_ip_range_pool_mgmt where r_status =:status", nativeQuery = true)
	int getCountStatus(@Param ("status") String status);
	
	@Query(value = "select count(r_ip_pool_id) from c3p_ip_range_pool_mgmt where r_status =:status or r_status =:rangeStatus or r_status =:rangeIpStatus", nativeQuery = true)
	int getCountNumber(@Param ("status") String status, @Param ("rangeStatus") String rangeStatus, @Param ("rangeIpStatus") String rangeIpStatus);
	
	@Query(value = "SELECT DATE_FORMAT(r_released_on,'%Y-%m-%d') FROM `c3p_ip_range_pool_mgmt` where r_released_on IS NOT NULL and `r_released_on`>=(CURDATE()-interval 6 day)", nativeQuery = true)
	Set<String> getIpRangeReleasedDate();

	@Query(value = "SELECT count(r_ip_pool_id) FROM c3p_ip_range_pool_mgmt where DATE(r_released_on) =:rangeReleasedOn", nativeQuery = true)
	int datesCount(@Param("rangeReleasedOn") String rangeReleasedOn);
	
}