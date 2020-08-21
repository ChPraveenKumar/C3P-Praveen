package com.techm.orion.repositories;




import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.sql.Timestamp;

import com.techm.orion.entitybeans.ServiceOrderEntity;

@Repository
public interface ServiceOrderRepo extends JpaRepository<ServiceOrderEntity, Long> {
	@Modifying
	@Transactional
	@Query("update ServiceOrderEntity c set c.requestId = :requestId , c.requestStatus = :requestStatus , c.updatedBy = :updatedBy , c.updatedDate = :updatedDate where c.serviceOrder = :serviceOrder")
	int updateStatusAndRequestId(@Param("requestId") String requestid, @Param("requestStatus") String requestStatus,
			@Param("serviceOrder") String serviceOrder,
			@Param("updatedBy") String updatedBy,
			@Param("updatedDate") Timestamp updatedDate);

	@Modifying
	@Transactional
	@Query("update ServiceOrderEntity c set c.status = :status , c.updatedBy = :updatedBy , c.updatedDate = :updatedDate where c.serviceOrder = :serviceOrder")
	int updateSoStatus(@Param("status") String status, @Param("serviceOrder") String serviceOrder,
			@Param("updatedBy") String updatedBy,
			@Param("updatedDate") Timestamp updatedDate);

	ServiceOrderEntity findByRequestId(String requestId);
}
