package com.techm.orion.repositories;

import java.sql.Timestamp;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.techm.orion.entitybeans.MasterFeatureEntity;

@Repository
@Transactional
public interface MasterFeatureRepository extends JpaRepository<MasterFeatureEntity, Long> {

	@Query(value = "select count(f_id) from c3p_m_features where f_vendor like :vendor and f_name like :featureName"
			+ " and f_os like :os and f_osversion like :osVersion and f_region like :region and f_networkfun like :networkType and f_family like :family", nativeQuery = true)
	int getCountByFVendorAndFNameAndFOsAndFOsversionAndFRegionAndFNetworkfunAndFFamily(@Param("vendor") String vendor,
			@Param("featureName") String featureName, @Param("os") String os, @Param("osVersion") String osVersion,
			@Param("region") String region, @Param("networkType") String networkType, @Param("family") String family);

	@Query(value = "select count(f_id) from c3p_m_features where f_status like :status and f_owner like :owner", nativeQuery = true)
	public int getCountByFStatusAndUser(@Param("status") String status, @Param("owner") String owner);

	@Query(value = "select * from c3p_m_features where f_owner like :owner and f_status like :status", nativeQuery = true)
	public List<MasterFeatureEntity> getListByOwner(@Param("status") String status, @Param("owner") String owner);

	MasterFeatureEntity findByFId(String fId);

	@Transactional
	@Modifying
	@Query(value = "UPDATE c3p_m_features SET f_status = :status, f_comments = :comment, f_owner = :owner , f_updated_by = :updater, f_updated_date = :date WHERE f_id = :infoId and f_version = :version", nativeQuery = true)
	int updateMasterFeatureStatus(@Param("status") String status, @Param("infoId") String infoId,
			@Param("version") String version, @Param("comment") String comment, @Param("owner") String owner, @Param("updater") String updater, @Param("date") Timestamp date);

	List<MasterFeatureEntity> findAllByFVendorAndFFamilyAndFOsAndFOsversionAndFRegionAndFNetworkfun(String vendor,
			String family, String os, String osVersion, String region, String networkType);
	
	@Query(value = "select * from c3p_m_features where f_vendor = :vendor and (f_family = 'All' or f_family =:deviceFamily)"
			+ " and (f_os = 'All' or f_os =:os) and (f_osversion = 'All' or f_osversion =:osVersion) "
			+ "and (f_region = 'All' or f_region =:region) and (f_networkfun = 'All' or f_networkfun =:networkType) and f_status ='Approved';", nativeQuery = true)
	List<MasterFeatureEntity> findNearestMatchEntities(@Param("vendor") String vendor,
			@Param("deviceFamily") String deviceFamily, @Param("os") String os, @Param("osVersion") String osVersion,
			@Param("region") String region, @Param("networkType") String networkType);

	
	@Query(value = "SELECT distinct f_vendor FROM c3p_m_features", nativeQuery = true)
	public List<String> findVendor();
		
	public List<MasterFeatureEntity> findAllByFVendor(String vendor);
	
	@Query(value = "select * from c3p_m_features where f_vendor = :vendor and  f_family =:deviceFamily"
			+ " and f_os =:os and  f_osversion =:osVersion "
			+ "and f_region =:region and  f_networkfun =:networkType and f_status ='Approved';", nativeQuery = true)
	List<MasterFeatureEntity> findApprovedFeatureEntity(@Param("vendor") String vendor,
			@Param("deviceFamily") String deviceFamily, @Param("os") String os, @Param("osVersion") String osVersion,
			@Param("region") String region, @Param("networkType") String networkType);

}
