package com.techm.c3p.core.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.techm.c3p.core.entitybeans.ResourceCharacteristicsEntity;

@Repository
public interface ResourceCharacteristicsRepository extends
JpaRepository<ResourceCharacteristicsEntity, Long> {
	
	ResourceCharacteristicsEntity findByDeviceIdAndRcFeatureIdAndRcCharacteristicId(int deviceId, 
			String rcFeatureId, String rcCharacteristicId);
	List<ResourceCharacteristicsEntity> findByRcDeviceHostname(String hostname);
	
	@Query(value = "select distinct(rc_feature_id) from c3p_resourcecharacteristics  where rc_device_hostname = :hostName", nativeQuery = true)
	List<String>findDistinctFeaturesForHostname(@Param("hostName") String hostName);

	List<ResourceCharacteristicsEntity> findByRcFeatureIdAndRcDeviceHostnameOrderByRcCreatedDateDesc(String featureId, String hostname);
	
	ResourceCharacteristicsEntity findByDeviceIdAndRcFeatureIdAndRcCharacteristicNameAndRcKeyValue(int deviceId,
			String rcFeatureId, String rcCharacteristicName, String rcKeyValue);
	
	@Query(value = "select * from c3p_resourcecharacteristics where device_id=:device_id and rc_feature_id=:rc_feature_id  "
			+ " and rc_characteristic_value=rc_key_value ", nativeQuery = true)
    ResourceCharacteristicsEntity findByDeviceIdAndRcFeatureIdAndRcKeyValueIsNotNull(@Param("device_id") int deviceId,
		    @Param("rc_feature_id") String rcFeatureId);
	
	ResourceCharacteristicsEntity findByDeviceIdAndRcFeatureIdAndRcCharacteristicIdAndRcKeyValue(int deviceId,
			String rcFeatureId, String rcCharacteristicId, String rcKeyValue);
	
	ResourceCharacteristicsEntity findByDeviceIdAndRcFeatureId(int deviceId,
			String rcFeatureId);
}