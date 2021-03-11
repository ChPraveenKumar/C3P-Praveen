package com.techm.orion.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.techm.orion.entitybeans.ResourceCharacteristicsEntity;

@Repository
public interface ResourceCharacteristicsRepository extends
JpaRepository<ResourceCharacteristicsEntity, Long> {
	
	ResourceCharacteristicsEntity findByDeviceIdAndRcFeatureIdAndRcCharacteristicId(int deviceId, 
			String rcFeatureId, String rcCharacteristicId);
	List<ResourceCharacteristicsEntity> findByRcDeviceHostname(String hostname);
	
	@Query(value = "select distinct(rc_feature_id) from c3p_resourcecharacteristics  where rc_device_hostname = :hostName order by rc_created_date desc", nativeQuery = true)
	List<String>findDistinctFeaturesForHostname(@Param("hostName") String hostName);

	List<ResourceCharacteristicsEntity>findByRcFeatureIdAndRcDeviceHostname(String featureId, String hostname);
	
	List<ResourceCharacteristicsEntity>findByRcFeatureIdAndRcDeviceHostnameOrderByRcCreatedDateDesc(String featureId, String hostname);
}