package com.techm.c3p.core.repositories;




import java.sql.Timestamp;
import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.techm.c3p.core.entitybeans.ServiceOrderEntity;

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
	
	@Query(value = "select decom.od_rfo_id as serviceOrder, decom.od_request_id as requestId, dInfo.d_hostname as hostName, sInfo.c_cust_name as customer, "
			+ "sInfo.c_site_name as site, dInfo.d_vendor as vendor, dInfo.d_device_family as family, dInfo.d_model as model, orders.rfo_status as status,"
			+ " decom.od_requeststatus as requestStatus, decom.od_updated_date as date, decom.od_rf_taskname as taskName "
			+ "from c3p_deviceinfo dInfo JOIN  c3p_rfo_decomposed decom JOIN c3p_cust_siteinfo sInfo "
			+ "JOIN c3p_rf_orders orders on dInfo.d_id=decom.od_req_resource_id and dInfo.c_site_id=sInfo.id and "
			+ "orders.rfo_id= decom.od_rfo_id ", nativeQuery = true)
	List<String> findAllByOrderByCreatedDateDesc();
}
