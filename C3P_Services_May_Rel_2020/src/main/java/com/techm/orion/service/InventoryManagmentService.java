package com.techm.orion.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.techm.orion.entitybeans.DeviceDiscoveryEntity;
import com.techm.orion.entitybeans.RequestDetailsEntity;
import com.techm.orion.entitybeans.SiteInfoEntity;
import com.techm.orion.entitybeans.UserEntity;
import com.techm.orion.mapper.RequestDetailsResponseMapper;
import com.techm.orion.pojo.DeviceDiscoverPojo;
import com.techm.orion.pojo.ServiceRequestPojo;
import com.techm.orion.pojo.SiteInfoPojo;
import com.techm.orion.repositories.DeviceDiscoveryRepository;
import com.techm.orion.repositories.RequestDetailsRepository;
import com.techm.orion.repositories.SiteInfoRepository;
import com.techm.orion.repositories.UserRepository;

@Service
public class InventoryManagmentService {

	@Autowired
	DeviceDiscoveryRepository deviceInforepo;

	@Autowired
	DcmConfigService dcmConfigService;

	@Autowired
	SiteInfoRepository siteRepo;

	@Autowired
	UserRepository userRepo;

	@Autowired
	RequestDetailsRepository requestDetailsRepo;

	public JSONArray getAllDeviceDescoverdForVendor() {

		JSONArray vendorArray = new JSONArray();

		(deviceInforepo.findAll().stream().filter(distinctByKey(p -> p.getdVendor())).collect(Collectors.toList()))
				.forEach(vendorObj -> {
					JSONObject vendor = new JSONObject();
					vendor.put("vendor", vendorObj.getdVendor());
					JSONArray modelArray = new JSONArray();
					(deviceInforepo.findDModelByDVendor(vendorObj.getdVendor()).stream()
							.filter(distinctByKey(p -> p.getdModel())).collect(Collectors.toList()))
									.forEach(modelObj -> {
										JSONObject model = new JSONObject();
										JSONArray hostArray = new JSONArray();
										(deviceInforepo.findAllDHostNameByDModelAndDVendor(modelObj.getdModel(),
												vendorObj.getdVendor())).forEach(hostNameObj -> {
													JSONObject hostName = new JSONObject();
													hostName.put("hostName", hostNameObj.getdHostName());
													hostArray.add(hostName);
												});
										model.put("hostNames", hostArray);
										model.put("model", modelObj.getdModel());
										modelArray.add(model);
									});
					vendor.put("models", modelArray);
					vendorArray.add(vendor);
				});

		/*
		 * for (DeviceDiscoveryEntity entity : distinctElementsForVendor) { JSONObject
		 * vendor = new JSONObject(); vendor.put("vendor", entity.getdVendor());
		 * List<DeviceDiscoveryEntity> models =
		 * deviceInforepo.findDModelByDVendor(entity.getdVendor());
		 * 
		 * // Get distinct objects by key List<DeviceDiscoveryEntity> distinctModel =
		 * models.stream().filter(distinctByKey(p -> p.getdModel()))
		 * .collect(Collectors.toList());
		 * 
		 * JSONArray modelArray = new JSONArray(); for (DeviceDiscoveryEntity modelName
		 * : distinctModel) { JSONObject model = new JSONObject();
		 * List<DeviceDiscoveryEntity> findAllDHostNameByDModelAndDVendor =
		 * deviceInforepo
		 * .findAllDHostNameByDModelAndDVendor(modelName.getdModel(),entity.getdVendor()
		 * );
		 * 
		 * JSONArray hostArray = new JSONArray(); for (DeviceDiscoveryEntity host :
		 * findAllDHostNameByDModelAndDVendor) { JSONObject hostName = new JSONObject();
		 * hostName.put("hostName", host.getdHostName()); hostArray.add(hostName); }
		 * model.put("hostNames", hostArray); model.put("model", modelName.getdModel());
		 * modelArray.add(model);
		 * 
		 * } vendor.put("models", modelArray); vendorArray.add(vendor);
		 * 
		 * }
		 */
		// vendorObject.put("vendors", vendorArray);

		return vendorArray;
	}

	public JSONArray getAllDeviceDescoverdForCustomer() {

		List<SiteInfoEntity> allSiteInfo = siteRepo.findAll();
		List<SiteInfoEntity> customerNames = allSiteInfo.stream().filter(distinctByKey(p -> p.getcCustName()))
				.collect(Collectors.toList());
		JSONArray customerArray = new JSONArray();
		for (SiteInfoEntity entity : customerNames) {
			JSONObject customerObject = new JSONObject();
			customerObject.put("customer", entity.getcCustName());

			List<SiteInfoEntity> regionsNames = siteRepo.findCSiteRegionByCCustName(entity.getcCustName());

			List<SiteInfoEntity> regionsNameEntity = regionsNames.stream()
					.filter(distinctByKey(p -> p.getcSiteRegion())).collect(Collectors.toList());

			JSONArray regionsArray = new JSONArray();
			for (SiteInfoEntity region : regionsNameEntity) {
				JSONObject regionObject = new JSONObject();
				int regionId = region.getId();
				regionObject.put("region", region.getcSiteRegion());
				List<SiteInfoEntity> sites = siteRepo.findCSiteNameByCSiteRegion(region.getcSiteRegion());
				List<SiteInfoEntity> sitesValue = sites.stream().filter(distinctByKey(p -> p.getcSiteName()))
						.collect(Collectors.toList());
				JSONArray siteArray = new JSONArray();
				for (SiteInfoEntity site : sitesValue) {
					JSONObject siteObject = new JSONObject();
					int siteId = site.getId();
					JSONArray hostArray = new JSONArray();
					// if (regionId == siteId) {
					List<DeviceDiscoveryEntity> findDHostNameByCustSiteIdId = deviceInforepo
							.findDHostNameByCustSiteIdId(siteId);
					siteObject.put("site", site.getcSiteName());
					for (DeviceDiscoveryEntity host : findDHostNameByCustSiteIdId) {
						JSONObject hostNameObject = new JSONObject();
						hostNameObject.put("hostName", host.getdHostName());
						hostArray.add(hostNameObject);
					}
					// }
					siteObject.put("hostNames", hostArray);
					siteArray.add(siteObject);
				}
				regionObject.put("sites", siteArray);
				regionsArray.add(regionObject);
			}
			customerObject.put("regions", regionsArray);
			customerArray.add(customerObject);
		}

		return customerArray;
	}

	public static <T> Predicate<T> distinctByKey(Function<? super T, Object> keyExtractor) {
		Map<Object, Boolean> map = new ConcurrentHashMap<>();
		return t -> map.putIfAbsent(keyExtractor.apply(t), Boolean.TRUE) == null;
	}

	public List<ServiceRequestPojo> getRequestDeatils(String hostName) {
		List<RequestDetailsEntity> allRequestDetails = requestDetailsRepo.findAllByHostname(hostName);
		RequestDetailsResponseMapper mapper = new RequestDetailsResponseMapper();
		List<ServiceRequestPojo> allRequestMapper = mapper.getAllRequestMapper(allRequestDetails);
		return allRequestMapper;
	}

	public List<DeviceDiscoverPojo> getAllDevice() {
		List<DeviceDiscoveryEntity> getAllDevice = deviceInforepo.findAll();
		List<DeviceDiscoverPojo> DeviceList = new ArrayList<>();
		getAllDevice.forEach(deviceinfo -> {
			DeviceDiscoverPojo deviceinfoPojo = new DeviceDiscoverPojo();
			deviceinfoPojo.setHostName(deviceinfo.getdHostName());
			deviceinfoPojo.setManagmentId(deviceinfo.getdMgmtIp());
			deviceinfoPojo.setOs(deviceinfo.getdOs());
			deviceinfoPojo.setOsVersion(deviceinfo.getdOsVersion());
			deviceinfoPojo.setVendor(deviceinfo.getdVendor());
			deviceinfoPojo.setSeries(deviceinfo.getdSeries());
			deviceinfoPojo.setType(deviceinfo.getdType());
			deviceinfoPojo.setModel(deviceinfo.getdModel());
			deviceinfoPojo.setVnfSupport(deviceinfo.getdVNFSupport());
			if (deviceinfo.getCustSiteId() != null) {
				SiteInfoEntity custSiteId = deviceinfo.getCustSiteId();
				SiteInfoPojo siteInfoPojo = new SiteInfoPojo();
				siteInfoPojo.setCustName(custSiteId.getcCustName());
				siteInfoPojo.setSiteRegion(custSiteId.getcSiteRegion());
				siteInfoPojo.setSiteName(custSiteId.getcSiteName());
				siteInfoPojo.setSiteActive(custSiteId.getcSiteAddressLine1());
				deviceinfoPojo.setSiteInfo(siteInfoPojo);
			}
			DeviceList.add(deviceinfoPojo);
		});
		return DeviceList;
	}

	public JSONArray getMyCustomersDevice() {
		String logedInUserName = dcmConfigService.getLogedInUserName();
		JSONArray customerArray = new JSONArray();
		if (logedInUserName != null) {

			List<UserEntity> findDevicesByUserName = userRepo.findDevicesByUserName(logedInUserName);
			findDevicesByUserName.forEach(user -> {
				List<DeviceDiscoveryEntity> devices = user.getDevices();
				List<SiteInfoEntity> allSiteInfo = new ArrayList<>();
				devices.forEach(item -> {
					allSiteInfo.add(item.getCustSiteId());
				});
				// List<SiteInfoEntity> allSiteInfo = siteRepo.findAll();
				List<SiteInfoEntity> customerNames = allSiteInfo.stream().filter(distinctByKey(p -> p.getcCustName()))
						.collect(Collectors.toList());

				for (SiteInfoEntity entity : customerNames) {
					JSONObject customerObject = new JSONObject();
					customerObject.put("customer", entity.getcCustName());

					List<SiteInfoEntity> regionsNames = siteRepo.findCSiteRegionByCCustName(entity.getcCustName());

					List<SiteInfoEntity> regionsNameEntity = regionsNames.stream()
							.filter(distinctByKey(p -> p.getcSiteRegion())).collect(Collectors.toList());

					JSONArray regionsArray = new JSONArray();
					for (SiteInfoEntity region : regionsNameEntity) {
						JSONObject regionObject = new JSONObject();
						int regionId = region.getId();
						regionObject.put("region", region.getcSiteRegion());
						List<SiteInfoEntity> sites = siteRepo.findCSiteNameByCSiteRegion(region.getcSiteRegion());
						List<SiteInfoEntity> sitesValue = sites.stream().filter(distinctByKey(p -> p.getcSiteName()))
								.collect(Collectors.toList());
						JSONArray siteArray = new JSONArray();
						for (SiteInfoEntity site : sitesValue) {
							JSONObject siteObject = new JSONObject();
							int siteId = site.getId();
							JSONArray hostArray = new JSONArray();
							// if (regionId == siteId) {
							List<DeviceDiscoveryEntity> findDHostNameByCustSiteIdId = deviceInforepo
									.findDHostNameByCustSiteIdId(siteId);
							siteObject.put("site", site.getcSiteName());
							for (DeviceDiscoveryEntity host : findDHostNameByCustSiteIdId) {
								JSONObject hostNameObject = new JSONObject();
								hostNameObject.put("hostName", host.getdHostName());
								hostArray.add(hostNameObject);
							}
							// }
							siteObject.put("hostNames", hostArray);
							siteArray.add(siteObject);
						}
						regionObject.put("sites", siteArray);
						regionsArray.add(regionObject);
					}
					customerObject.put("regions", regionsArray);
					customerArray.add(customerObject);
				}

			});
		}

		return customerArray;
	}

	public JSONArray getMyVendorsDevice() {

		JSONArray vendorArray = new JSONArray();
		String logedInUserName = dcmConfigService.getLogedInUserName();		
		if (logedInUserName != null) {
			List<UserEntity> findDevicesByUserName = userRepo.findDevicesByUserName(logedInUserName);
			findDevicesByUserName.forEach(user -> {
				List<DeviceDiscoveryEntity> devices = user.getDevices();
				List<SiteInfoEntity> allSiteInfo = new ArrayList<>();
				devices.forEach(item -> {
					allSiteInfo.add(item.getCustSiteId());
				});
				(devices.stream().filter(distinctByKey(p -> p.getdVendor())).collect(Collectors.toList()))
						.forEach(vendorObj -> {
							JSONObject vendor = new JSONObject();
							vendor.put("vendor", vendorObj.getdVendor());
							JSONArray modelArray = new JSONArray();
							(deviceInforepo.findDModelByDVendor(vendorObj.getdVendor()).stream()
									.filter(distinctByKey(p -> p.getdModel())).collect(Collectors.toList()))
											.forEach(modelObj -> {
												JSONObject model = new JSONObject();
												JSONArray hostArray = new JSONArray();
												(deviceInforepo.findAllDHostNameByDModelAndDVendor(modelObj.getdModel(),
														vendorObj.getdVendor())).forEach(hostNameObj -> {
															JSONObject hostName = new JSONObject();
															hostName.put("hostName", hostNameObj.getdHostName());
															hostArray.add(hostName);
														});
												model.put("hostNames", hostArray);
												model.put("model", modelObj.getdModel());
												modelArray.add(model);
											});
							vendor.put("models", modelArray);
							vendorArray.add(vendor);
						});
			});
		}
		return vendorArray;
	}
}