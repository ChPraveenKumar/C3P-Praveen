package com.techm.orion.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
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
	private static final Logger logger = LogManager.getLogger(InventoryManagmentService.class);

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
		List<DeviceDiscoveryEntity> deviceDetails = deviceInforepo.findAll();
		deviceDetails.stream().filter(distinctByKey(p -> p.getdVendor())).forEach(vendorInfo -> {
			JSONObject vendor = new JSONObject();
			vendor.put("vendor", vendorInfo.getdVendor());
			JSONArray modelArray = new JSONArray();
			deviceDetails.stream().filter(distinctByKey(p -> p.getdModel())).forEach(modelInfo -> {
				JSONObject model = new JSONObject();
				JSONArray hostArray = new JSONArray();
				(deviceInforepo.findAllDHostNameByDModelAndDVendor(modelInfo.getdModel(), vendorInfo.getdVendor()))
						.forEach(hostNameObj -> {
							JSONObject hostName = new JSONObject();
							hostName.put("hostName", hostNameObj);
							hostArray.add(hostName);
						});
				model.put("hostNames", hostArray);
				model.put("model", modelInfo.getdModel());
				modelArray.add(model);
			});
			vendor.put("models", modelArray);
			vendorArray.add(vendor);
		});
		return vendorArray;
	}

	public JSONArray getAllDeviceDescoverdForCustomer() {
		long startMethod = System.currentTimeMillis();

		List<SiteInfoEntity> allSiteInfo = siteRepo.findAll();
		JSONArray customerArray = new JSONArray();
		allSiteInfo.stream().filter(distinctByKey(p -> p.getcCustName())).forEach(customerInfo -> {
			JSONObject customerObject = new JSONObject();
			customerObject.put("customer", customerInfo.getcCustName());
			JSONArray regionsArray = new JSONArray();
			allSiteInfo.stream().filter(distinctByKey(p -> p.getcSiteRegion())).forEach(regionInfo -> {
				JSONObject regionObject = new JSONObject();
				regionObject.put("region", regionInfo.getcSiteRegion());
				JSONArray siteArray = new JSONArray();
				allSiteInfo.stream().filter(distinctByKey(p -> p.getcSiteName())).forEach(siteInfo -> {
					JSONObject siteObject = new JSONObject();
					JSONArray hostArray = new JSONArray();
					deviceInforepo.findDHostNameByCustSiteIdId(siteInfo.getId()).forEach(hostNameInfo -> {
						JSONObject hostNameObject = new JSONObject();
						hostNameObject.put("hostName", hostNameInfo);
						hostArray.add(hostNameObject);
					});
					siteObject.put("hostNames", hostArray);
					siteObject.put("site", siteInfo.getcSiteName());
					siteArray.add(siteObject);
				});

				regionObject.put("sites", siteArray);
				regionsArray.add(regionObject);
			});
			customerObject.put("regions", regionsArray);
			customerArray.add(customerObject);
		});
		logger.info("Total time taken to execute the method in milli secs - "
				+ ((System.currentTimeMillis() - startMethod)));
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
			deviceinfoPojo.setDeviceFamily(deviceinfo.getdDeviceFamily());
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
		long startMethod = System.currentTimeMillis();
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

				allSiteInfo.stream().filter(distinctByKey(p -> p.getcCustName())).forEach(customerInfo -> {
					JSONObject customerObject = new JSONObject();
					customerObject.put("customer", customerInfo.getcCustName());
					JSONArray regionsArray = new JSONArray();
					allSiteInfo.stream().filter(distinctByKey(p -> p.getcSiteRegion())).forEach(regionInfo -> {
						JSONObject regionObject = new JSONObject();
						regionObject.put("region", regionInfo.getcSiteRegion());
						JSONArray siteArray = new JSONArray();

						allSiteInfo.stream().filter(distinctByKey(p -> p.getcSiteName())).forEach(siteInfo -> {
							JSONObject siteObject = new JSONObject();
							JSONArray hostArray = new JSONArray();
							deviceInforepo.findDHostNameByCustSiteIdId(siteInfo.getId()).forEach(hostNameinfo -> {
								JSONObject hostNameObject = new JSONObject();
								hostNameObject.put("hostName", hostNameinfo);
								hostArray.add(hostNameObject);
							});
							siteObject.put("hostNames", hostArray);
							siteObject.put("site", siteInfo.getcSiteName());
							siteArray.add(siteObject);
						});
						regionObject.put("sites", siteArray);
						regionsArray.add(regionObject);
					});
					customerObject.put("regions", regionsArray);
					customerArray.add(customerObject);

				});
			});

		}
		logger.info("Total time taken to execute the method in milli secs - "
				+ ((System.currentTimeMillis() - startMethod)));
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
															hostName.put("hostName", hostNameObj);
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