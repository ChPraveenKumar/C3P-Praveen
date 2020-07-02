package com.techm.orion.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.techm.orion.entitybeans.SiteInfoEntity;

@Repository
public interface SiteInfoRepository extends JpaRepository<SiteInfoEntity, Long> {

	List<SiteInfoEntity> findCSiteRegionByCSiteName(String siteName);

	List<SiteInfoEntity> findCSiteRegionByCCustName(String customerName);

	List<SiteInfoEntity> findCSiteNameByCSiteRegion(String region);
	List<SiteInfoEntity> findByCCustNameAndCSiteRegion(String custName,String region);

	List<SiteInfoEntity> findCSiteNameByCCustName(String getcCustName);
	List<SiteInfoEntity> findCSiteIdByCCustNameAndCSiteName(String customer,String site);
	SiteInfoEntity findCSiteIdByCSiteName(String siteName);
	
	List<SiteInfoEntity> findCSiteIdByCCustNameAndCSiteRegionAndCSiteName(String customer, String region, String site);
	
	
	
}
