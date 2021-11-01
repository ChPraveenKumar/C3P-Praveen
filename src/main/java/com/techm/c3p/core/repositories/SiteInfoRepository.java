package com.techm.c3p.core.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.techm.c3p.core.entitybeans.SiteInfoEntity;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

@Repository
public interface SiteInfoRepository extends JpaRepository<SiteInfoEntity, Long> {

	List<SiteInfoEntity> findCSiteRegionByCSiteName(String siteName);

	List<SiteInfoEntity> findCSiteRegionByCCustName(String customerName);

	List<SiteInfoEntity> findCSiteNameByCSiteRegion(String region);
	List<SiteInfoEntity> findByCCustNameAndCSiteRegion(String custName,String region);

	List<SiteInfoEntity> findCSiteNameByCCustName(String getcCustName);
	List<SiteInfoEntity> findCSiteIdByCCustNameAndCSiteName(String customer,String site);
	List<SiteInfoEntity> findCSiteIdByCSiteName(String siteName);
	
	List<SiteInfoEntity> findCSiteIdByCCustNameAndCSiteRegionAndCSiteName(String customer, String region, String site);

	@Query("SELECT distinct cCustName FROM SiteInfoEntity ")
	List<SiteInfoEntity> findCustomerDetails();
	
	@Query("SELECT distinct concat(substring(cSiteRegion, 1, 3), '-',cSiteName) as cSiteName FROM SiteInfoEntity where "
			+ "cCustName IN (:cCustName) AND  cSiteRegion IN (:cSiteRegion)")
	List<SiteInfoEntity> findSitesDetails(@Param("cCustName") List cCustName , @Param("cSiteRegion") List cSiteRegion);
	
	@Query("SELECT distinct concat(substring(cSiteRegion, 1, 3), '-',cSiteName) as cSiteName, cSiteName, cSiteRegion"
			+ " FROM SiteInfoEntity where "
			+ "cCustName IN (:cCustName) AND  cSiteRegion IN (:cSiteRegion)")
	List<SiteInfoEntity> findSitesDetailsInfo(@Param("cCustName") List cCustName , @Param("cSiteRegion") List cSiteRegion);
	
	@Query("SELECT distinct cSiteRegion FROM SiteInfoEntity where cCustName IN (:cCustName)")
	List<SiteInfoEntity> findRegionDetails(@Param("cCustName") List cCustName);		
	
}
