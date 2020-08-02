package com.techm.orion.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.techm.orion.entitybeans.CreateConfigEntity;

@Repository
public interface CreateConfigRepo extends JpaRepository<CreateConfigEntity, Long> {

	public CreateConfigEntity findByRequestId(String requestId);

	@Query(value = "SELECT master_label_value FROM t_create_config_m_attrib_info where master_label_id = :attribId and request_id =:requestId and request_version=:requestVersion ", nativeQuery = true)
	String findAttribValuByRequestId(@Param("attribId") int attribId,
			@Param("requestId") String requestId,@Param("requestVersion") double version);

}
