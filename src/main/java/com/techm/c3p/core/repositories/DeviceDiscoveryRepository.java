package com.techm.c3p.core.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.techm.c3p.core.entitybeans.DeviceDiscoveryEntity;
import com.techm.c3p.core.entitybeans.SiteInfoEntity;

@Repository
@Transactional
public interface DeviceDiscoveryRepository extends JpaRepository<DeviceDiscoveryEntity, Long> {

	List<DeviceDiscoveryEntity> findAllByDVendor(String vendor);

	List<DeviceDiscoveryEntity> findAllByDVendorAndDModel(String vendor, String model);

	List<DeviceDiscoveryEntity> findAllByDVendorAndDModelAndDHostName(String model, String vendor, String hostname);

	@Query(value = "select deviceinfo.d_hostname from c3p_deviceinfo deviceinfo left outer join c3p_cust_siteinfo sieinfo on deviceinfo.c_site_id=sieinfo.id where sieinfo.id=:id", nativeQuery = true)
	List<String> findDHostNameByCustSiteIdId(@Param("id") int id);

	int countDistinctCustSiteIdcCustNameBydId(int id);

	SiteInfoEntity findCustSiteIdBydId(int id);

	int countIdByUsersUserName(String user);

	List<DeviceDiscoveryEntity> findAllByUsersUserName(String user);

	List<DeviceDiscoveryEntity> findAllByDVendorAndUsersUserName(String vendor, String user);

	List<DeviceDiscoveryEntity> findAllByDVendorAndDModelAndUsersUserName(String vendor, String model, String user);

	List<DeviceDiscoveryEntity> findAllByDVendorAndDModelAndDHostNameAndUsersUserName(String vendor, String model,
			String hostName, String user);

	List<DeviceDiscoveryEntity> findAllByCustSiteIdCCustNameAndUsersUserName(String customerName, String userName);

	List<DeviceDiscoveryEntity> findAllByCustSiteIdCCustNameAndCustSiteIdCSiteRegionAndUsersUserName(
			String customerName, String region, String userName);

	List<DeviceDiscoveryEntity> findAllByCustSiteIdCCustNameAndCustSiteIdCSiteRegionAndCustSiteIdCSiteNameAndUsersUserName(
			String customerName, String region, String site, String userName);

	List<DeviceDiscoveryEntity> findAllByCustSiteIdCCustNameAndCustSiteIdCSiteRegionAndCustSiteIdCSiteNameAndDHostNameAndUsersUserName(
			String customerName, String region, String site, String hostName, String userName);

	List<DeviceDiscoveryEntity> findAllByCustSiteIdCCustName(String customerName);

	List<DeviceDiscoveryEntity> findAllByCustSiteIdCCustNameAndCustSiteIdCSiteRegion(String customerName,
			String region);

	List<DeviceDiscoveryEntity> findAllByCustSiteIdCCustNameAndCustSiteIdCSiteRegionAndCustSiteIdCSiteName(
			String customerName, String region, String site);

	List<DeviceDiscoveryEntity> findAllByCustSiteIdCCustNameAndCustSiteIdCSiteRegionAndCustSiteIdCSiteNameAndDHostName(
			String customerName, String region, String site, String hostName);

	List<DeviceDiscoveryEntity> findDModelByDVendor(String vendor);

	@Query(value = "SELECT d_hostname FROM c3p_deviceinfo where d_vendor=:vendor and d_model=:model", nativeQuery = true)
	List<String> findAllDHostNameByDModelAndDVendor(@Param("model") String model, @Param("vendor") String vendor);

	List<DeviceDiscoveryEntity> findBydHostName(String dHostname);

	List<DeviceDiscoveryEntity> findByCustSiteIdId(int siteId);

	DeviceDiscoveryEntity findBydMgmtIp(String dMgmtIp);

	DeviceDiscoveryEntity findByDHostName(String hostname);

	List<DeviceDiscoveryEntity> findBydHostNameContaining(String dHostname);

	List<DeviceDiscoveryEntity> findByDHostNameContainingAndCustSiteIdCCustName(String hostname, String customername);

	List<DeviceDiscoveryEntity> findByDHostNameContainingAndCustSiteIdCCustNameAndCustSiteIdCSiteRegion(String hostname,
			String customername, String region);

	List<DeviceDiscoveryEntity> findByDHostNameContainingAndCustSiteIdCCustNameAndCustSiteIdCSiteRegionAndCustSiteIdCSiteName(
			String hostname, String customername, String region, String sitename);

	List<DeviceDiscoveryEntity> findBydMgmtIpContaining(String dMgmtIp);

	List<DeviceDiscoveryEntity> findByDMgmtIpContainingAndCustSiteIdCCustName(String managementip, String customername);

	List<DeviceDiscoveryEntity> findByDMgmtIpContainingAndCustSiteIdCCustNameAndCustSiteIdCSiteRegion(
			String managementip, String customername, String region);

	List<DeviceDiscoveryEntity> findByDMgmtIpContainingAndCustSiteIdCCustNameAndCustSiteIdCSiteRegionAndCustSiteIdCSiteName(
			String managementip, String customername, String region, String sitename);

	List<DeviceDiscoveryEntity> findBydOsContaining(String os);

	List<DeviceDiscoveryEntity> findBydOsContainingAndCustSiteIdCCustName(String os, String customername);

	List<DeviceDiscoveryEntity> findBydOsContainingAndCustSiteIdCCustNameAndCustSiteIdCSiteRegion(String os,
			String customername, String region);

	List<DeviceDiscoveryEntity> findBydOsContainingAndCustSiteIdCCustNameAndCustSiteIdCSiteRegionAndCustSiteIdCSiteName(
			String os, String customername, String region, String sitename);

	List<DeviceDiscoveryEntity> findBydOsVersionContaining(String osversion);

	List<DeviceDiscoveryEntity> findBydOsVersionContainingAndCustSiteIdCCustName(String osversion, String customername);

	List<DeviceDiscoveryEntity> findBydOsVersionContainingAndCustSiteIdCCustNameAndCustSiteIdCSiteRegion(
			String osversion, String customername, String region);

	List<DeviceDiscoveryEntity> findBydOsVersionContainingAndCustSiteIdCCustNameAndCustSiteIdCSiteRegionAndCustSiteIdCSiteName(
			String osversion, String customername, String region, String sitename);

	List<DeviceDiscoveryEntity> findByDModelContaining(String model);

	List<DeviceDiscoveryEntity> findByDModelContainingAndCustSiteIdCCustName(String model, String customername);

	List<DeviceDiscoveryEntity> findByDModelContainingAndCustSiteIdCCustNameAndCustSiteIdCSiteRegion(String model,
			String customername, String region);

	List<DeviceDiscoveryEntity> findByDModelContainingAndCustSiteIdCCustNameAndCustSiteIdCSiteRegionAndCustSiteIdCSiteName(
			String model, String customername, String region, String sitename);

	List<DeviceDiscoveryEntity> findBydTypeContaining(String devicetype);

	List<DeviceDiscoveryEntity> findBydTypeContainingAndCustSiteIdCCustName(String devicetype, String customername);

	List<DeviceDiscoveryEntity> findBydTypeContainingAndCustSiteIdCCustNameAndCustSiteIdCSiteRegion(String devicetype,
			String customername, String region);

	List<DeviceDiscoveryEntity> findBydTypeContainingAndCustSiteIdCCustNameAndCustSiteIdCSiteRegionAndCustSiteIdCSiteName(
			String devicetype, String customername, String region, String sitename);

	List<DeviceDiscoveryEntity> findBydEndOfSaleDateContaining(String eol);

	List<DeviceDiscoveryEntity> findBydEndOfSaleDateContainingAndCustSiteIdCCustName(String eol, String customername);

	List<DeviceDiscoveryEntity> findBydEndOfSaleDateContainingAndCustSiteIdCCustNameAndCustSiteIdCSiteRegion(String eol,
			String customername, String region);

	List<DeviceDiscoveryEntity> findBydEndOfSaleDateContainingAndCustSiteIdCCustNameAndCustSiteIdCSiteRegionAndCustSiteIdCSiteName(
			String eol, String customername, String region, String sitename);

	List<DeviceDiscoveryEntity> findBydEndOfSupportDateContaining(String eos);

	List<DeviceDiscoveryEntity> findBydEndOfSupportDateContainingAndCustSiteIdCCustName(String eos,
			String customername);

	List<DeviceDiscoveryEntity> findBydEndOfSupportDateContainingAndCustSiteIdCCustNameAndCustSiteIdCSiteRegion(
			String eos, String customername, String region);

	List<DeviceDiscoveryEntity> findBydEndOfSupportDateContainingAndCustSiteIdCCustNameAndCustSiteIdCSiteRegionAndCustSiteIdCSiteName(
			String eos, String customername, String region, String sitename);

	List<DeviceDiscoveryEntity> findByCustSiteIdCCustName(String customername);

	List<DeviceDiscoveryEntity> findByCustSiteIdCCustNameAndCustSiteIdCSiteRegion(String customername, String region);

	List<DeviceDiscoveryEntity> findByCustSiteIdCCustNameAndCustSiteIdCSiteRegionAndCustSiteIdCSiteName(
			String customername, String region, String sitename);

	DeviceDiscoveryEntity findDVNFSupportByDHostName(String hostname);

	List<DeviceDiscoveryEntity> findByDHostNameAndDMgmtIp(String tempHostName, String tempManagementIp);

	List<DeviceDiscoveryEntity> findByCustSiteIdCCustNameAndCustSiteIdCSiteRegionAndDVendor(String customer,
			String region, String vendortosearch);

	List<DeviceDiscoveryEntity> findByCustSiteIdCCustNameAndCustSiteIdCSiteRegionAndDVendorAndDVNFSupport(
			String customer, String region, String vendortosearch, String networktosearch);

	List<DeviceDiscoveryEntity> findByCustSiteIdCCustNameAndCustSiteIdCSiteRegionAndDVendorAndDVNFSupportAndCustSiteIdCSiteName(
			String customer, String region, String vendortosearch, String networktosearch, String sitetosearch);
	
	List<DeviceDiscoveryEntity> findByCustSiteIdCCustNameAndCustSiteIdCSiteRegionAndDVendorAndDVNFSupportAndDDeviceFamily(
			String customer, String region, String vendortosearch, String networktosearch, String devicetosearch);	

	List<DeviceDiscoveryEntity> findByCustSiteIdCCustNameAndCustSiteIdCSiteRegionAndDVendorAndDVNFSupportAndCustSiteIdCSiteNameAndDDeviceFamily(
			String customer, String region, String vendortosearch, String networktosearch, String sitetosearch,
			String devicetosearch);

	List<DeviceDiscoveryEntity> findByCustSiteIdCCustNameAndCustSiteIdCSiteRegionAndDVendorAndDVNFSupportAndCustSiteIdCSiteNameAndDDeviceFamilyAndDModel(
			String customer, String region, String vendortosearch, String networktosearch, String sitetosearch,
			String devicetosearch, String modeltosearch);

	List<DeviceDiscoveryEntity> findByCustSiteIdCCustNameAndCustSiteIdCSiteRegionAndDVendorAndDVNFSupportAndCustSiteIdCSiteNameAndDModel(
			String customer, String region, String vendortosearch, String networktosearch, String sitetosearch,
			String modeltosearch);

	List<DeviceDiscoveryEntity> findByCustSiteIdCCustNameAndCustSiteIdCSiteRegionAndDVendorAndDVNFSupportAndDDeviceFamilyAndDModel(
			String customer, String region, String vendortosearch, String networktosearch, String devicetosearch,
			String modeltosearch);

	List<DeviceDiscoveryEntity> findAllByCustSiteId(int temp);

	List<DeviceDiscoveryEntity> findAllByDVendorAndDDeviceFamily(String vendor, String deviceFamily);

	/* Dhanshri Mane */
	@Query(value = "select * from c3p_deviceinfo where d_mgmtip= :mgmtip and d_hostname= :hostName ", nativeQuery = true)
	DeviceDiscoveryEntity findHostNameAndMgmtip(@Param("mgmtip") String mgmtip, @Param("hostName") String hostName);

	DeviceDiscoveryEntity findBydId(int id);

	@Modifying
	@Query(value = "update c3p_deviceinfo set d_dis_id = :resultDevice where d_Id = :deviceId", nativeQuery = true)
	int updateDescripancyId(@Param("resultDevice") int resultDevice, @Param("deviceId") int deviceId);

	@Query(value = "select count(d_auto_status) from c3p_deviceinfo where d_auto_status=:status", nativeQuery = true)
	int getDeviceStatusCount(@Param("status") String status);

	@Query(value = "select count(d_id) from c3p_deviceinfo", nativeQuery = true)
	int getDeviceCount();

	/**/

	@Query("SELECT dMgmtIp FROM DeviceDiscoveryEntity where dMgmtIp=:dMgmtIp")
	String findMgmtId(@Param("dMgmtIp") String dMgmtIp);

	@Query("SELECT dIPAddrSix FROM DeviceDiscoveryEntity where dIPAddrSix=:dIPAddrSix")
	String findIpV6(@Param("dIPAddrSix") String dIPAddrSix);

	@Query("SELECT dHostName FROM DeviceDiscoveryEntity")
	List<DeviceDiscoveryEntity> findHostName();

	@Query("SELECT data FROM DeviceDiscoveryEntity data where dMgmtIp=:dMgmtIp")
	List<DeviceDiscoveryEntity> existingDeviceInfoIpV4(@Param("dMgmtIp") String dMgmtIp);

	@Query("SELECT data FROM DeviceDiscoveryEntity data where dIPAddrSix=:dIPAddrSix")
	List<DeviceDiscoveryEntity> existingDeviceInfoIpV6(@Param("dIPAddrSix") String dIPAddrSix);

	String findDevices = "SELECT * FROM c3p_deviceinfo  where d_vendor = ?1 and d_device_family = ?2 and d_os_version < ?3";

	@Query(value = findDevices, nativeQuery = true)
	@Modifying
	@Transactional
	List<DeviceDiscoveryEntity> findAllDevices(String vendor, String deviceFamily, String osVersion);

	String findDevices1 = "select * FROM c3p_deviceinfo as u inner join c3p_cust_siteinfo as c on u.c_site_id= c.id where u.d_vendor = ?1 and u.d_sries = ?2 and u.d_os_version < ?3 and c.c_cust_name = ?4";

	@Query(value = findDevices1, nativeQuery = true)
	@Modifying
	@Transactional
	List<DeviceDiscoveryEntity> findByVendorFamilyVersionCustomer(String vendor, String deviceFamily, String osVersion,
			String customer);

	String findDevices2 = "select * FROM c3p_deviceinfo as u inner join c3p_cust_siteinfo as c on u.c_site_id= c.id where u.d_vendor = ?1 and u.d_sries = ?2 and u.d_os_version < ?3 and c.c_cust_name = ?4 and c.c_site_region = ?5";

	@Query(value = findDevices2, nativeQuery = true)
	@Modifying
	@Transactional
	List<DeviceDiscoveryEntity> findByVendorFamilyVersionCustomerRegion(String vendor, String deviceFamily,
			String osVersion, String customer, String region);

	String findDevices3 = "select * FROM c3p_deviceinfo as u inner join c3p_cust_siteinfo as c on u.c_site_id= c.id where u.d_vendor = ?1 and u.d_device_family = ?2 and u.d_os_version < ?3 and c.c_cust_name = ?4 and c.c_site_region = ?5 and c.c_site_name = ?6";

	@Query(value = findDevices3, nativeQuery = true)
	@Modifying
	@Transactional
	List<DeviceDiscoveryEntity> findByVendorFamilyVersionCustomerRegionSite(String vendor, String deviceFamily,
			String osVersion, String customer, String region, String site);

	@Query(value = "select * from c3p_deviceinfo where d_mgmtip=:dMgmtIp", nativeQuery = true)
	DeviceDiscoveryEntity findAllByMgmtId(@Param("dMgmtIp") String dMgmtIp);
	
	@Query(value = "SELECT d_id FROM c3p_deviceinfo where d_hostname=:d_hostname", nativeQuery = true)
	int findDid(@Param("d_hostname") String d_hostname);	
	

	@Query(value = "select distinct deviceinfo.* from c3p_deviceinfo deviceinfo left outer join c3p_device_credentials devicecred on deviceinfo.d_id=devicecred.device_id "
			+ "left outer join c3p_t_credential_management credinfo on credinfo.cr_info_id=devicecred.cr_info_id "
			+ "left outer join c3p_cust_siteinfo siteinfo on deviceinfo.c_site_id=siteinfo.id "
			+ "where siteinfo.c_cust_name=:customerName "
			+ "and (credinfo.cr_profile_name not in (:profileName) or credinfo.cr_profile_name is null)", nativeQuery = true)
	List<DeviceDiscoveryEntity> findAllByCustSiteIdCCustNameAndCredMgmtEntityNotInOrIsNull(
			@Param("customerName") String customerName, @Param("profileName") String profileName);
	
	@Query(value = "select distinct deviceinfo.* from c3p_deviceinfo deviceinfo left outer join c3p_device_credentials devicecred on deviceinfo.d_id=devicecred.device_id "
			+ "left outer join c3p_t_credential_management credinfo on credinfo.cr_info_id=devicecred.cr_info_id "
			+ "left outer join c3p_cust_siteinfo siteinfo on deviceinfo.c_site_id=siteinfo.id "
			+ "where siteinfo.c_cust_name=:customerName and siteinfo.c_site_region=:region "
			+ "and (credinfo.cr_profile_name not in (:profileName) or credinfo.cr_profile_name is null)", nativeQuery = true)
	List<DeviceDiscoveryEntity> findByCustSiteIdCCustNameAndCustSiteIdCSiteRegionAndCredMgmtEntityNotInOrIsNull(@Param("customerName") String customerName,
			@Param("region") String region, @Param("profileName") String profileName);
	
	@Query(value = "select distinct deviceinfo.* from c3p_deviceinfo deviceinfo left outer join c3p_device_credentials devicecred on deviceinfo.d_id=devicecred.device_id "
			+ "left outer join c3p_t_credential_management credinfo on credinfo.cr_info_id=devicecred.cr_info_id "
			+ "left outer join c3p_cust_siteinfo siteinfo on deviceinfo.c_site_id=siteinfo.id "
			+ "where siteinfo.c_cust_name=:customerName and siteinfo.c_site_name=:siteName and siteinfo.c_site_region=:region "
			+ "and (credinfo.cr_profile_name not in (:profileName) or credinfo.cr_profile_name is null)", nativeQuery = true)
	List<DeviceDiscoveryEntity> findByCustSiteIdCCustNameAndCustSiteIdCSiteRegionAndCustSiteIdCSiteNameAndCredMgmtEntityNotInOrIsNull(@Param("customerName") String customerName,
			@Param("region") String region, @Param("siteName") String siteName, @Param("profileName") String profileName);

	
	DeviceDiscoveryEntity findByDHostNameAndDMgmtIpAndDDeComm(String tempHostName, String tempManagementIp,String dcomm);
	
	List<DeviceDiscoveryEntity> findAllByOrderByDIdDesc();
	
	@Query(value = "select * from c3p_deviceinfo where d_hostname like :dHostName%", nativeQuery = true)
	List<DeviceDiscoveryEntity> findByHostName(@Param("dHostName")String dHostName);
	
	@Query(value = "select * from c3p_deviceinfo where d_mgmtip like :dMgmtIp%", nativeQuery = true)
	List<DeviceDiscoveryEntity> findByMgmtIp(@Param("dMgmtIp")String dMgmtIp);
	
	@Query(value = "select * from c3p_deviceinfo where d_os like :dOs%", nativeQuery = true)
	List<DeviceDiscoveryEntity> findByOS(@Param("dOs")String dOs);
	
	@Query(value = "select * from c3p_deviceinfo where d_os_version like :dOsVersion%", nativeQuery = true)
	List<DeviceDiscoveryEntity> findByOsVersion(@Param("dOsVersion")String dOsVersion);
	
	@Query(value = "select * from c3p_deviceinfo where d_model like :dModel%", nativeQuery = true)
	List<DeviceDiscoveryEntity> findByModel(@Param("dModel")String dModel);
	
	@Query(value = "select * from c3p_deviceinfo where d_device_family like :dDeviceFamily%", nativeQuery = true)
	List<DeviceDiscoveryEntity> findByDeviceFamily(@Param("dDeviceFamily")String dDeviceFamily);
	
	@Query(value = "select * from c3p_deviceinfo where d_endof_saledate like :dEndOfSaleDate%", nativeQuery = true)
	List<DeviceDiscoveryEntity> findBySaleDate(@Param("dEndOfSaleDate")String dEndOfSaleDate);
	
	@Query(value = "select * from c3p_deviceinfo where d_decomm !=1", nativeQuery = true)
	List<DeviceDiscoveryEntity> findDeviceDetails();	
	
	@Query(value = "select * from c3p_deviceinfo where d_id in(:did)", nativeQuery = true)
	List<DeviceDiscoveryEntity> findByDId(@Param("did") List<String> did);	
	
	DeviceDiscoveryEntity findAllByDId(int id);
	
	List<DeviceDiscoveryEntity> findByDClusterid(int clusterid);
	
	DeviceDiscoveryEntity findByDClusteridAndDHostName(int clusterid, String hostname);
	
	@Modifying
	@Query(value = "update c3p_deviceinfo set d_mgmtip = :d_mgmtip, d_decomm = 0 where d_id = :d_id", nativeQuery = true)
	int updateMgmtIpbyDeviceid(@Param("d_mgmtip") String d_mgmtip, @Param("d_id") int d_id);
	
	List<DeviceDiscoveryEntity> findByDVendorAndDOsAndDOsVersionAndDDeviceFamilyAndDVNFSupport(
			String vendor,String os,String osversion,String deviceFamily ,String networktype);
	

	List<DeviceDiscoveryEntity> findByDVendorAndDOsAndDDeviceFamilyAndDVNFSupport(
			String vendor,String os,String deviceFamily ,String networktype);
	
	
	@Query(value = "select * FROM c3p_deviceinfo as u inner join c3p_cust_siteinfo as c on u.c_site_id= c.id "
	+ "where (u.d_vendor like :vendor or u.d_vendor like '%All') and (u.d_os like :os or u.d_os like '%All') and (u.d_os_version like :osVersion or u.d_os_version like '%All') and (u.d_device_family like :devicefamily or u.d_device_family like '%All') and (u.d_vnf_support like :networkfunction or u.d_vnf_support like '%All') and (c.c_site_region like :region or c.c_site_region like '%All')",nativeQuery = true)
List<DeviceDiscoveryEntity> geAuditDeviceList(@Param("vendor") String vendor, @Param("os") String os,
	 @Param("osVersion") String osVersion, @Param("devicefamily") String devicefamily,
	@Param("networkfunction") String networkfunction,@Param("region") String region);
	
	
	List<DeviceDiscoveryEntity> findByCustSiteIdIdAndDVNFSupport(int siteId, String networkFuction);

	@Query(value = "SELECT * FROM c3p_deviceinfo where d_decomm= 0 and d_vnf_support='VNF'", nativeQuery = true)
	List<DeviceDiscoveryEntity> findLCMDeleteList();
}
