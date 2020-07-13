package com.techm.orion.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.techm.orion.entitybeans.DiscrepancyMsgEntity;

@Repository
public interface DiscrepancyMsgRepository extends JpaRepository<DiscrepancyMsgEntity, Long> {

	@Query(value = "select discripancy_msg from c3p_discripancy_msg r where r.discripancy_type= :type ", nativeQuery = true)
	String findDiscrepancyMsg(@Param("type") String type);

}
