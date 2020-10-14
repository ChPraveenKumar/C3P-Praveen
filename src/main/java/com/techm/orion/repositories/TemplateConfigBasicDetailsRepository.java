package com.techm.orion.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import com.techm.orion.entitybeans.TemplateConfigBasicDetailsEntity;

@Repository
@Transactional
public interface TemplateConfigBasicDetailsRepository extends
		JpaRepository<TemplateConfigBasicDetailsEntity, Long> {

	@Query(value = "SELECT max(temp_id) FROM templateconfig_basic_details where temp_device_os=:temp_device_os and temp_vendor=:temp_vendor and "
			+ "temp_device_family=:temp_device_family and temp_os_version=:temp_os_version and temp_region=:temp_region"
			, nativeQuery = true)
	String createTemplateBasicConfig(@Param("temp_device_os") String temp_device_os,
			@Param("temp_vendor") String temp_vendor,
			@Param("temp_device_family") String temp_device_family,
			@Param("temp_os_version") String temp_os_version,
			@Param("temp_region") String temp_region);
}
