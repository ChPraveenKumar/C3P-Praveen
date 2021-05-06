package com.techm.orion.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import com.techm.orion.entitybeans.ImageManagementEntity;

@Repository
public interface ImageManagementRepository extends JpaRepository<ImageManagementEntity, Long> {
	
	@Query("SELECT imageFilename FROM ImageManagementEntity WHERE vendor=:vendor AND family=:family ") 
	List<ImageManagementEntity> fetchImageFileNamesByVendorAndFamily(@Param("vendor") String vendor, @Param("family") String family) ;
	
	@Query("SELECT displayName FROM ImageManagementEntity WHERE vendor=:vendor AND family=:family ") 
	List<ImageManagementEntity> fetchDisplayNamesByVendorAndFamily(@Param("vendor") String vendor, @Param("family") String family) ;
	
	@Query("SELECT max(displayName) FROM ImageManagementEntity WHERE vendor=:vendor AND family=:family ") 
	String fetchingDisplayNamesByVendorAndFamily(@Param("vendor") String vendor, @Param("family") String family) ;

	List<ImageManagementEntity> findByVendorAndFamily(String vendor, String deviceFamily);
		
	ImageManagementEntity findByVendorAndFamilyAndImageFilename(String vendor, String deviceFamily, String imageName);
	
	ImageManagementEntity findByVendorAndFamilyAndDisplayName(String vendor, String deviceFamily, String displayName);
	
	ImageManagementEntity findById(int infoId);
	
	List<ImageManagementEntity> findByVendor(String vendor);
	
}
