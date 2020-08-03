package com.techm.orion.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import com.techm.orion.entitybeans.FirmwareUpgradeSingleDeviceEntity;

@Repository
public interface FirmUpgradeSingleDeviceRepository extends JpaRepository<FirmwareUpgradeSingleDeviceEntity, Long> {

	List<FirmwareUpgradeSingleDeviceEntity> findAll();
	
	@Query("SELECT imageFilename FROM FirmwareUpgradeSingleDeviceEntity WHERE vendor=:vendor AND family=:family ") 
	List<FirmwareUpgradeSingleDeviceEntity> checkDuplicateImage(@Param("vendor") String vendor, @Param("family") String family) ;
	
	@Query("SELECT displayName FROM FirmwareUpgradeSingleDeviceEntity WHERE vendor=:vendor AND family=:family ") 
	List<FirmwareUpgradeSingleDeviceEntity> checkDuplicateDisplayName(@Param("vendor") String vendor, @Param("family") String family) ;
	
	@Query("SELECT max(displayName) FROM FirmwareUpgradeSingleDeviceEntity WHERE vendor=:vendor AND family=:family ") 
	String checkHigherDisplayName(@Param("vendor") String vendor, @Param("family") String family) ;
	
	@Query("SELECT max(displayName) FROM FirmwareUpgradeSingleDeviceEntity WHERE vendor=:vendor AND family=:family ") 
	String checkHigherOsVersion(@Param("vendor") String vendor, @Param("family") String family) ;

	List<FirmwareUpgradeSingleDeviceEntity> findByVendorAndFamily(String vendor, String deviceFamily);
}
