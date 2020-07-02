package com.techm.orion.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.techm.orion.entitybeans.DeviceDiscoveryEntity;
import com.techm.orion.entitybeans.SiteInfoEntity;

@Repository
public interface DeviceDiscoveryRepository extends JpaRepository<DeviceDiscoveryEntity, Long> {

	
	
	List<DeviceDiscoveryEntity> findAllByDVendor(String vendor);
	List<DeviceDiscoveryEntity> findAllByDVendorAndDModel(String vendor,String model);
	List<DeviceDiscoveryEntity> findAllByDVendorAndDModelAndDHostName(String model,String vendor,String hostname);
		
	List<DeviceDiscoveryEntity> findDHostNameByCustSiteIdId(int id);
	int countDistinctCustSiteIdcCustNameBydId(int id);
	
	SiteInfoEntity findCustSiteIdBydId(int id);
	
	int countIdByUsersUserName(String user);
	
	List<DeviceDiscoveryEntity> findAllByUsersUserName(String user);
	List<DeviceDiscoveryEntity> findAllByDVendorAndUsersUserName(String vendor,String user);
	List<DeviceDiscoveryEntity> findAllByDVendorAndDModelAndUsersUserName(String vendor,String model,String user);
	List<DeviceDiscoveryEntity> findAllByDVendorAndDModelAndDHostNameAndUsersUserName(String vendor,String model,String hostName,String user);
	
	List<DeviceDiscoveryEntity> findAllByCustSiteIdCCustNameAndUsersUserName(String customerName,String userName);
	List<DeviceDiscoveryEntity> findAllByCustSiteIdCCustNameAndCustSiteIdCSiteRegionAndUsersUserName(String customerName,String region,String userName);
	List<DeviceDiscoveryEntity> findAllByCustSiteIdCCustNameAndCustSiteIdCSiteRegionAndCustSiteIdCSiteNameAndUsersUserName(String customerName,String region,String site,String userName);	
	List<DeviceDiscoveryEntity> findAllByCustSiteIdCCustNameAndCustSiteIdCSiteRegionAndCustSiteIdCSiteNameAndDHostNameAndUsersUserName(String customerName,String region,String site,String hostName,String userName);
	
	List<DeviceDiscoveryEntity> findAllByCustSiteIdCCustName(String customerName);
	List<DeviceDiscoveryEntity> findAllByCustSiteIdCCustNameAndCustSiteIdCSiteRegion(String customerName,String region);
	List<DeviceDiscoveryEntity> findAllByCustSiteIdCCustNameAndCustSiteIdCSiteRegionAndCustSiteIdCSiteName(String customerName,String region,String site);
	List<DeviceDiscoveryEntity> findAllByCustSiteIdCCustNameAndCustSiteIdCSiteRegionAndCustSiteIdCSiteNameAndDHostName(String customerName,String region,String site,String hostName);
	List<DeviceDiscoveryEntity> findDModelByDVendor(String vendor);
	List<DeviceDiscoveryEntity> findAllDHostNameByDModelAndDVendor(String model, String vendor);
	List<DeviceDiscoveryEntity> findBydHostName(String dHostname);
	
	List<DeviceDiscoveryEntity> findByCustSiteIdId(int siteId);
	List<DeviceDiscoveryEntity> findBydMgmtIp(String dMgmtIp);
	DeviceDiscoveryEntity findByDHostName(String hostname);
	DeviceDiscoveryEntity findDVNFSupportByDHostName(String hostname);
//	@Query(value = "select a from DeviceDiscoveryEntity a where a.dModel= ? and a.dVendor=? order by a.dDatePolled desc Limit 0, 5")	
//	List<DeviceDiscoveryEntity> findByTop(String model,String vendor);

	
}
