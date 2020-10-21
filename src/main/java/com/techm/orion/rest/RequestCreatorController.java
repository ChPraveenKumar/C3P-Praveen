package com.techm.orion.rest;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.core.Response;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.techm.orion.dao.TemplateManagementDao;
import com.techm.orion.entitybeans.DeviceDiscoveryEntity;
import com.techm.orion.entitybeans.SiteInfoEntity;
import com.techm.orion.entitybeans.TemplateFeatureEntity;
import com.techm.orion.pojo.AttribCreateConfigPojo;
import com.techm.orion.pojo.CommandPojo;
import com.techm.orion.pojo.RequestInfoPojo;
import com.techm.orion.repositories.DeviceDiscoveryRepository;
import com.techm.orion.repositories.SiteInfoRepository;
import com.techm.orion.repositories.TemplateFeatureRepo;
import com.techm.orion.service.AttribCreateConfigService;
import com.techm.orion.service.DcmConfigService;
import com.techm.orion.service.GetConfigurationTemplateService;
import com.techm.orion.utility.InvokeFtl;

@RestController
public class RequestCreatorController {
	private static final Logger logger = LogManager.getLogger(RequestCreatorController.class);
	@Autowired
	SiteInfoRepository siteRepo;

	@Autowired
	DeviceDiscoveryRepository deviceRepo;

	@Autowired
	AttribCreateConfigService service;

	@Autowired
	DcmConfigService dcmConfigService;

	@Autowired
	TemplateFeatureRepo templatefeatureRepo;

	@GET
	@RequestMapping(value = "/getCustomerList", method = RequestMethod.GET, produces = "application/json")
	public Response getCustomerList() {
		Set<String> customerList = new HashSet<>();
		List<SiteInfoEntity> allSiteInfo = siteRepo.findAll();
		allSiteInfo.forEach(site -> {
			customerList.add(site.getcCustName());
		});
		return Response.status(200).entity(customerList).build();
	}

	@POST
	@RequestMapping(value = "/getRegions", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
	@ResponseBody
	public Response getRegions(@RequestBody String request) {
		Set<String> region = new HashSet<>();
		try {
			JSONParser parser = new JSONParser();
			JSONObject json = (JSONObject) parser.parse(request);
			String customer = json.get("customerName").toString();

			List<SiteInfoEntity> findCSiteNameByCCustName = siteRepo.findCSiteRegionByCCustName(customer);
			findCSiteNameByCCustName.forEach(site -> {
				region.add(site.getcSiteRegion());
			});
		} catch (Exception e) {
			logger.error(e);
		}
		return Response.status(200).entity(region).build();
	}

	@POST
	@RequestMapping(value = "/getSites", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
	@ResponseBody
	public Response getSites(@RequestBody String request) {
		Set<String> siteNames = new HashSet<>();
		try {
			JSONParser parser = new JSONParser();
			JSONObject json = (JSONObject) parser.parse(request);
			String customer = json.get("customerName").toString();
			String region = json.get("region").toString();
			List<SiteInfoEntity> findCSiteNameByCCustName = siteRepo.findByCCustNameAndCSiteRegion(customer, region);
			findCSiteNameByCCustName.forEach(site -> {
				siteNames.add(site.getcSiteName());
			});
		} catch (Exception e) {
			logger.error(e);
		}
		return Response.status(200).entity(siteNames).build();
	}

	/*
	 * @POST
	 * 
	 * @RequestMapping(value = "/getSitesId", method = RequestMethod.POST, consumes
	 * = "application/json", produces = "application/json")
	 * 
	 * @ResponseBody public Response getSitesId(@RequestBody String request) {
	 * Set<String> siteId = new HashSet<>(); try { JSONParser parser = new
	 * JSONParser(); JSONObject json = (JSONObject) parser.parse(request); String
	 * siteName = json.get("siteName").toString(); String customer =
	 * json.get("customerName").toString(); List<SiteInfoEntity>
	 * findCSiteNameByCCustName =
	 * siteRepo.findByCCustNameAndCSiteName(customer,siteName);
	 * findCSiteNameByCCustName.forEach(site -> { siteId.add(site.getcSiteId()); });
	 * } catch (Exception e) { logger.error(e); } return
	 * Response.status(200).entity(siteId).build(); }
	 */

	@POST
	@RequestMapping(value = "/getZones", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
	@ResponseBody
	public Response getZones(@RequestBody String request) {

		try {
			JSONParser parser = new JSONParser();
			JSONObject json = (JSONObject) parser.parse(request);
			String customer = json.get("customerName").toString();
			String region = json.get("region").toString();
			String site = json.get("site").toString();

		} catch (Exception e) {
			logger.error(e);
		}
		return Response.status(200).entity("Abc").build();
	}

	/*
	 * @GET
	 * 
	 * @RequestMapping(value = "/getVendorList", method = RequestMethod.GET,
	 * produces = "application/json") public Response getVendorList() { Set<String>
	 * vendorList = new HashSet<>(); List<DeviceDiscoveryEntity> deviceInfo =
	 * deviceRepo.findAll(); deviceInfo.forEach(device -> {
	 * vendorList.add(device.getdVendor()); }); return
	 * Response.status(200).entity(vendorList).build(); }
	 */

	/*
	 * @POST
	 * 
	 * @RequestMapping(value = "/getVendorList", method = RequestMethod.POST,
	 * consumes = "application/json", produces = "application/json")
	 * 
	 * @ResponseBody public Response getVendorList(@RequestBody String request) {
	 * Set<String> vendorList = new HashSet<>(); try { JSONParser parser = new
	 * JSONParser(); JSONObject json = (JSONObject) parser.parse(request); String
	 * customer = json.get("customerName").toString(); String siteName =
	 * json.get("siteName").toString(); String siteId =
	 * json.get("siteId").toString();
	 * 
	 * List<SiteInfoEntity> siteList =
	 * siteRepo.findCSiteIdByCCustNameAndCSiteIdAndCSiteName(customer, siteId,
	 * siteName); siteList.forEach(site -> { DeviceDiscoveryEntity device =
	 * deviceRepo.findByCustSiteIdId(site.getId());
	 * vendorList.add(device.getdVendor()); }); } catch (Exception e) {
	 * logger.error(e); } return
	 * Response.status(200).entity(vendorList).build(); }
	 */

	@POST
	@RequestMapping(value = "/getHostName", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
	@ResponseBody
	public Response getHostName(@RequestBody String request) {
		Set<String> hostNameList = new HashSet<>();
		try {
			JSONParser parser = new JSONParser();
			JSONObject json = (JSONObject) parser.parse(request);
			String customer = json.get("customerName").toString();
			String siteName = json.get("siteName").toString();
			String region = json.get("region").toString();
			// String vendor = json.get("vendor").toString();
			List<SiteInfoEntity> siteList = siteRepo.findCSiteIdByCCustNameAndCSiteRegionAndCSiteName(customer, region,
					siteName);
			siteList.forEach(site -> {
				List<DeviceDiscoveryEntity> device = deviceRepo.findByCustSiteIdId(site.getId());
				device.forEach(item -> {
					hostNameList.add(item.getdHostName());
				});

			});
		} catch (Exception e) {
			logger.error(e);
		}
		return Response.status(200).entity(hostNameList).build();
	}

	@POST
	@RequestMapping(value = "/getDevieDetails", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
	@ResponseBody
	public Response getDevieDetails(@RequestBody String request) {
		JSONObject deviceDetails = new JSONObject();
		try {
			JSONParser parser = new JSONParser();
			JSONObject json = (JSONObject) parser.parse(request);
			String customer = json.get("customerName").toString();
			String siteName = json.get("siteName").toString();
			String hostName = json.get("hostName").toString();

			List<SiteInfoEntity> siteList = siteRepo.findCSiteIdByCCustNameAndCSiteName(customer, siteName);
			siteList.forEach(site -> {
				List<DeviceDiscoveryEntity> deviceList = deviceRepo.findByCustSiteIdId(site.getId());
				deviceList.forEach(device -> {
					if (hostName.equals(device.getdHostName())) {
						deviceDetails.put("vendor", device.getdVendor());
						deviceDetails.put("managmentIP", device.getdMgmtIp());
						deviceDetails.put("deviceType", device.getdType());
						deviceDetails.put("deviceFamily", device.getdDeviceFamily());
						deviceDetails.put("model", device.getdModel());
						deviceDetails.put("os_osVerion", device.getdOs() + "/" + device.getdOsVersion());
					}
				});

			});
		} catch (Exception e) {
			logger.error(e);
		}
		return Response.status(200).entity(deviceDetails).build();
	}

	@POST
	@RequestMapping(value = "/verifyConfiguration", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
	@ResponseBody
	public JSONObject generateCreateRequestDetails(@RequestBody String configRequest) {
		logger.info("generateCreateRequestDetails - configRequest-  "+configRequest);
		JSONObject obj = new JSONObject();
		String data = "";
		InvokeFtl invokeFtl = new InvokeFtl();
		// CreateConfigRequestDCM createConfigRequest = new CreateConfigRequestDCM();
		RequestInfoPojo createConfigRequest = new RequestInfoPojo();
		GetConfigurationTemplateService getConfigurationTemplateService = new GetConfigurationTemplateService();
		TemplateManagementDao dao = new TemplateManagementDao();
		try {

			JSONParser parser = new JSONParser();
			JSONObject json = (JSONObject) parser.parse(configRequest);

			// template suggestion
			if (json.containsKey("templateId") && json.get("templateId") !=null && !json.get("templateId").toString().isEmpty()) {
				createConfigRequest.setTemplateID(json.get("templateId").toString());
			}
			createConfigRequest.setCustomer(json.get("customer").toString());
			createConfigRequest.setSiteName(json.get("siteName").toString().toUpperCase());
			SiteInfoEntity siteId = siteRepo.findCSiteIdByCSiteName(createConfigRequest.getSiteName());
			createConfigRequest.setSiteid(siteId.getcSiteId());
			if(json.get("deviceType") !=null) {
				createConfigRequest.setDeviceType(json.get("deviceType").toString());
			}
			createConfigRequest.setFamily(json.get("deviceFamily").toString());
			createConfigRequest.setModel(json.get("model").toString());
			createConfigRequest.setOs(json.get("os").toString());
			createConfigRequest.setOsVersion(json.get("osVersion").toString());

			createConfigRequest.setManagementIp(json.get("managementIp").toString());
			createConfigRequest.setRegion(json.get("region").toString().toUpperCase());
			createConfigRequest.setHostname(json.get("hostname").toString().toUpperCase());

			createConfigRequest.setVendor(json.get("vendor").toString().toUpperCase());
			LocalDateTime nowDate = LocalDateTime.now();
			Timestamp timestamp = Timestamp.valueOf(nowDate);
			createConfigRequest.setRequestCreatedOn(timestamp.toString());
			if (!json.get("networkType").toString().equals("") && json.get("networkType").toString()!=null) {
			createConfigRequest.setNetworkType(json.get("networkType").toString());
			}else {
				DeviceDiscoveryEntity networkfunctio =deviceRepo.findDVNFSupportByDHostName(createConfigRequest.getHostname());
				createConfigRequest.setNetworkType(networkfunctio.getdVNFSupport());
			}
			/* Get Cammands and Template attribute selected Features */
			org.json.simple.JSONArray featureListJson = null;
			if (json.containsKey("selectedFeatures")) {

				featureListJson = (org.json.simple.JSONArray) json.get("selectedFeatures");
			}

			List<String> featureList = new ArrayList<String>();
			if (featureListJson != null && !featureListJson.isEmpty()) {
				for (int i = 0; i < featureListJson.size(); i++) {
					if (featureListJson.get(i).toString().contains("Basic Con")) {

					} else {
						featureList.add((String) featureListJson.get(i));
					}

				}
			}
			// Extract dynamicAttribs Json Value and map it to MasteAtrribute
			// List
			org.json.simple.JSONArray attribJson = null;
			if (json.containsKey("dynamicAttribs")) {
				attribJson = (org.json.simple.JSONArray) json.get("dynamicAttribs");
			}
			boolean flag = false;
			if (json.get("networkType").toString().equals("VNF")) {
				JSONObject vnfFinalObject = new JSONObject();
				JSONArray fianlJson = new JSONArray();				
				
				for (String feature : featureList) {
					JSONArray vnfattribJson = new JSONArray();
					String templateId = createConfigRequest.getTemplateID();
					List<AttribCreateConfigPojo> byAttribTemplateAndFeatureName = service
							.getByAttribTemplateAndFeatureName(templateId, feature);
					JSONObject vnfObject = new JSONObject();
						
					for (AttribCreateConfigPojo attr : byAttribTemplateAndFeatureName) {
						 AA:
						for (int i = 0; i < attribJson.size(); i++) {
							JSONObject object = (JSONObject) attribJson.get(i);

							String attribLabel = object.get("label").toString();
							String attribName = object.get("name").toString();
							if ( attribName.equals(attr.getAttribName()) && attribLabel.equals(attr.getAttribLabel())) {
								vnfattribJson.add(object);
								break AA;
					
							}

						}
						
					}

					vnfObject.put("featureName", feature);
					vnfObject.put("featureAttributes", vnfattribJson);
					fianlJson.add(vnfObject);
					logger.info(fianlJson.toString());
					
				}
				vnfFinalObject.put("dynamicAttribs", fianlJson);


				VnfConfigService vnfService = new VnfConfigService();
				Response generateConfiguration = vnfService.generateConfiguration(vnfFinalObject.toString());
				JSONObject entity = (JSONObject) generateConfiguration.getEntity();
				Object object = entity.get("data");
				obj.put(new String("output"), new String(object.toString()));
			} else {

				/*
				 * create SeriesId for getting master configuration Commands and master
				 * Atrribute
				 */
				String seriesId = dcmConfigService.getSeriesId(createConfigRequest.getVendor(),
						createConfigRequest.getFamily(), createConfigRequest.getModel());
				logger.info("seriesId ->"+seriesId);
				/* Get Series according to template id */
				TemplateManagementDao templatemanagementDao = new TemplateManagementDao();
				/*Code not required*/
				/*seriesId = templatemanagementDao.getSeriesId(createConfigRequest.getTemplateID(), seriesId);
				seriesId = StringUtils.substringAfter(seriesId, "Generic_");
				logger.info("seriesId ->"+seriesId);
				List<AttribCreateConfigPojo> masterAttribute = new ArrayList<>();
				List<AttribCreateConfigPojo> byAttribSeriesId = service.getByAttribSeriesId(seriesId);
				if (byAttribSeriesId != null && !byAttribSeriesId.isEmpty()) {
					masterAttribute.addAll(byAttribSeriesId);
				}
				List<CommandPojo> cammandsBySeriesId = null;
				// Getting Commands Using Series Id
				if (createConfigRequest.getTemplateID() == null | createConfigRequest.getTemplateID().equals("")) {
					cammandsBySeriesId = dao.getCammandsBySeriesId(seriesId, null);
				} else {
					cammandsBySeriesId = dao.getCammandsBySeriesId(seriesId, createConfigRequest.getTemplateID());
				}
				*/
				List<AttribCreateConfigPojo> templateAttribute = new ArrayList<>();
				List<CommandPojo> cammandByTemplate = new ArrayList<>();
				for (String feature : featureList) {
					String templateId = createConfigRequest.getTemplateID();
					TemplateFeatureEntity findIdByfeatureAndCammand = templatefeatureRepo
							.findIdByComandDisplayFeatureAndCommandContains(feature, templateId);
					List<AttribCreateConfigPojo> byAttribTemplateAndFeatureName = service
							.getByAttribTemplateAndFeatureName(templateId, feature);
					if (byAttribTemplateAndFeatureName != null && !byAttribTemplateAndFeatureName.isEmpty()) {
						templateAttribute.addAll(byAttribTemplateAndFeatureName);
					}
					cammandByTemplate.addAll(dao.getCammandByTemplateAndfeatureId(findIdByfeatureAndCammand.getId(),
							createConfigRequest.getTemplateID()));
				}

				// Extract Json and map to CreateConfigPojo fields
				if (attribJson != null) {
					for (int i = 0; i < attribJson.size(); i++) {
						JSONObject object = (JSONObject) attribJson.get(i);
						String attribLabel = object.get("label").toString();
						String attriValue = object.get("value").toString();
						String attribType = object.get("type").toString();
						String attribName = object.get("name").toString();
						/*
						 * Map data using attribType if type is masterAttribute then map data into
						 * master configuration which is extracted using series ID
						 */
						/*for (AttribCreateConfigPojo attrib : masterAttribute) {

							if (attribLabel.contains(attrib.getAttribLabel())) {
								String attribValue = attrib.getAttribName();
								if (attribValue.contains(attribName)) {
									if (attrib.getAttribType().equals("Master")) {

										if (attribType.equals("Master")) {
											if (attribName.equals("OsVer")) {
												createConfigRequest.setOsVer(attriValue);
												break;
											}
											if (attribName.equals("HostNameConfig")) {
												createConfigRequest.setHostNameConfig(attriValue);
												break;
											}
											if (attribName.equals("LoggingBuffer")) {
												createConfigRequest.setLoggingBuffer(attriValue);
												break;
											}
											if (attribName.equals("MemorySize")) {
												createConfigRequest.setMemorySize(attriValue);
												break;
											}
											if (attribName.equals("LoggingSourceInterface")) {
												createConfigRequest.setLoggingSourceInterface(attriValue);
												break;
											}
											if (attribName.equals("IPTFTPSourceInterface")) {
												createConfigRequest.setiPTFTPSourceInterface(attriValue);
												break;
											}
											if (attribName.equals("IPFTPSourceInterface")) {
												createConfigRequest.setiPFTPSourceInterface(attriValue);
												break;
											}
											if (attribName.equals("LineConPassword")) {
												createConfigRequest.setLineConPassword(attriValue);
												break;
											}
											if (attribName.equals("LineAuxPassword")) {
												createConfigRequest.setLineAuxPassword(attriValue);
												break;
											}
											if (attribName.equals("LineVTYPassword")) {
												createConfigRequest.setLineVTYPassword(attriValue);
												break;
											}
											if (attribName.equals("M_Attrib1")) {
												createConfigRequest.setM_Attrib1(attriValue);
												break;
											}
											if (attribName.equals("M_Attrib2")) {
												createConfigRequest.setM_Attrib2(attriValue);
												break;
											}
											if (attribName.equals("M_Attrib3")) {
												createConfigRequest.setM_Attrib3(attriValue);
												break;
											}
											if (attribName.equals("M_Attrib4")) {
												createConfigRequest.setM_Attrib4(attriValue);
												break;
											}
											if (attribName.equals("M_Attrib5")) {
												createConfigRequest.setM_Attrib5(attriValue);
												break;
											}
											if (attribName.equals("M_Attrib6")) {
												createConfigRequest.setM_Attrib6(attriValue);
												break;
											}
											if (attribName.equals("M_Attrib7")) {
												createConfigRequest.setM_Attrib7(attriValue);
												break;
											}
											if (attribName.equals("M_Attrib8")) {
												createConfigRequest.setM_Attrib8(attriValue);
												break;
											}
											if (attribName.equals("M_Attrib9")) {
												createConfigRequest.setM_Attrib9(attriValue);
												break;
											}
											if (attribName.equals("M_Attrib10")) {
												createConfigRequest.setM_Attrib10(attriValue);
												break;
											}
											if (attribName.equals("M_Attrib11")) {
												createConfigRequest.setM_Attrib11(attriValue);
												break;
											}
											if (attribName.equals("M_Attrib12")) {
												createConfigRequest.setM_Attrib12(attriValue);
												break;
											}
											if (attribName.equals("M_Attrib13")) {
												createConfigRequest.setM_Attrib13(attriValue);
												break;
											}
											if (attribName.equals("M_Attrib14")) {
												createConfigRequest.setM_Attrib14(attriValue);
												break;
											}
											if (attribName.equals("M_Attrib15")) {
												createConfigRequest.setM_Attrib15(attriValue);
												break;
											}

										}
									}
								}
							}
						}*/
						for (AttribCreateConfigPojo templateAttrib : templateAttribute) {

							if (attribLabel.contains(templateAttrib.getAttribLabel())) {
								String attribValue = templateAttrib.getAttribName();
								if (attribValue.contains(attribName)) {
									if (templateAttrib.getAttribType().equals("Template")) {
										if (attribType.equals("Template")) {

											
											if (attribName.equals("LANInterfaceIP1")) {
												createConfigRequest.setlANInterfaceIP1(attriValue);
												break;
											}
											if (attribName.equals("LANInterfaceMask1")) {
												createConfigRequest.setlANInterfaceMask1(attriValue);
												break;
											}
											if (attribName.equals("LANInterfaceIP2")) {
												createConfigRequest.setlANInterfaceIP2(attriValue);
												break;
											}
											if (attribName.equals("LANInterfaceMask2")) {
												createConfigRequest.setlANInterfaceMask2(attriValue);
												break;
											}
											if (attribName.equals("WANInterfaceIP1")) {
												createConfigRequest.setwANInterfaceIP1(attriValue);
												break;
											}

											if (attribName.equals("WANInterfaceMask1")) {
												createConfigRequest.setwANInterfaceMask1(attriValue);
												break;
											}
											if (attribName.equals("WANInterfaceIP2")) {
												createConfigRequest.setwANInterfaceIP2(attriValue);
												break;
											}
											if (attribName.equals("WANInterfaceMask2")) {
												createConfigRequest.setwANInterfaceMask2(attriValue);
												break;
											}
											if (attribName.equals("ResInterfaceIP")) {
												createConfigRequest.setResInterfaceIP(attriValue);
												break;
											}

											if (attribName.equals("ResInterfaceMask")) {
												createConfigRequest.setResInterfaceMask(attriValue);
												break;
											}

											if (attribName.equals("VRFName")) {
												createConfigRequest.setvRFName(attriValue);
												break;
											}

											if (attribName.equals("BGPASNumber")) {
												createConfigRequest.setbGPASNumber(attriValue);
												break;
											}

											if (attribName.equals("BGPRouterID")) {
												createConfigRequest.setbGPRouterID(attriValue);
												break;
											}

											if (attribName.equals("BGPNeighborIP1")) {
												createConfigRequest.setResInterfaceIP(attriValue);
												break;
											}

											if (attribName.equals("BGPRemoteAS1")) {
												createConfigRequest.setbGPRemoteAS1(attriValue);
												break;
											}

											if (attribName.equals("BGPNeighborIP2")) {
												createConfigRequest.setbGPNeighborIP1(attriValue);
												break;
											}

											if (attribName.equals("BGPRemoteAS2")) {
												createConfigRequest.setbGPRemoteAS2(attriValue);
												break;
											}

											if (attribName.equals("BGPNetworkIP1")) {
												createConfigRequest.setbGPNetworkIP1(attriValue);
												break;
											}

											if (attribName.equals("BGPNetworkWildcard1")) {
												createConfigRequest.setbGPNetworkWildcard1(attriValue);
												break;
											}

											if (attribName.equals("BGPNetworkIP2")) {
												createConfigRequest.setbGPNetworkIP2(attriValue);
												break;
											}

											if (attribName.equals("BGPNetworkWildcard2")) {
												createConfigRequest.setbGPNetworkWildcard2(attriValue);
												break;
											}

											if (attribName.equals("Attrib1")) {
												createConfigRequest.setAttrib1(attriValue);
												break;
											}
											if (attribName.equals("Attrib2")) {
												createConfigRequest.setAttrib2(attriValue);
												break;
											}
											if (attribName.equals("Attrib3")) {
												createConfigRequest.setAttrib3(attriValue);
												break;
											}
											if (attribName.equals("Attrib4")) {
												createConfigRequest.setAttrib4(attriValue);
												break;
											}
											if (attribName.equals("Attrib5")) {
												createConfigRequest.setAttrib5(attriValue);
												break;
											}
											if (attribName.equals("Attrib6")) {
												createConfigRequest.setAttrib6(attriValue);
												break;
											}
											if (attribName.equals("Attrib7")) {
												createConfigRequest.setAttrib7(attriValue);
												break;
											}
											if (attribName.equals("Attrib8")) {
												createConfigRequest.setAttrib8(attriValue);
												break;
											}
											if (attribName.equals("Attrib9")) {
												createConfigRequest.setAttrib9(attriValue);
												break;
											}
											if (attribName.equals("Attrib10")) {
												createConfigRequest.setAttrib10(attriValue);
												break;
											}
											if (attribName.equals("Attrib11")) {
												createConfigRequest.setAttrib11(attriValue);
												break;
											}
											if (attribName.equals("Attrib12")) {
												createConfigRequest.setAttrib12(attriValue);
												break;
											}
											if (attribName.equals("Attrib13")) {
												createConfigRequest.setAttrib13(attriValue);
												break;
											}
											if (attribName.equals("Attrib14")) {
												createConfigRequest.setAttrib14(attriValue);
												break;
											}
											if (attribName.equals("Attrib15")) {
												createConfigRequest.setAttrib15(attriValue);
												break;
											}
											if (attribName.equals("Attrib16")) {
												createConfigRequest.setAttrib16(attriValue);
												break;
											}
											if (attribName.equals("Attrib17")) {
												createConfigRequest.setAttrib17(attriValue);
												break;
											}
											if (attribName.equals("Attrib18")) {
												createConfigRequest.setAttrib18(attriValue);
												break;
											}
											if (attribName.equals("Attrib19")) {
												createConfigRequest.setAttrib19(attriValue);
												break;
											}
											if (attribName.equals("Attrib20")) {
												createConfigRequest.setAttrib20(attriValue);
												break;
											}
											if (attribName.equals("Attrib21")) {
												createConfigRequest.setAttrib21(attriValue);
												break;
											}
											if (attribName.equals("Attrib22")) {
												createConfigRequest.setAttrib22(attriValue);
												break;
											}
											if (attribName.equals("Attrib23")) {
												createConfigRequest.setAttrib23(attriValue);
												break;
											}
											if (attribName.equals("Attrib24")) {
												createConfigRequest.setAttrib24(attriValue);
												break;
											}
											if (attribName.equals("Attrib25")) {
												createConfigRequest.setAttrib25(attriValue);
												break;
											}
											if (attribName.equals("Attrib26")) {
												createConfigRequest.setAttrib26(attriValue);
												break;
											}
											if (attribName.equals("Attrib27")) {
												createConfigRequest.setAttrib27(attriValue);
												break;
											}
											if (attribName.equals("Attrib28")) {
												createConfigRequest.setAttrib28(attriValue);
												break;
											}
											if (attribName.equals("Attrib29")) {
												createConfigRequest.setAttrib29(attriValue);
												break;
											}
											if (attribName.equals("Attrib30")) {
												createConfigRequest.setAttrib30(attriValue);
												break;
											}
											if (attribName.equals("Attrib31")) {
												createConfigRequest.setAttrib31(attriValue);
												break;
											}
											if (attribName.equals("Attrib32")) {
												createConfigRequest.setAttrib32(attriValue);
												break;
											}
											if (attribName.equals("Attrib33")) {
												createConfigRequest.setAttrib33(attriValue);
												break;
											}
											if (attribName.equals("Attrib34")) {
												createConfigRequest.setAttrib34(attriValue);
												break;
											}
											if (attribName.equals("Attrib35")) {
												createConfigRequest.setAttrib35(attriValue);
												break;
											}
											if (attribName.equals("Attrib36")) {
												createConfigRequest.setAttrib36(attriValue);
												break;
											}
											if (attribName.equals("Attrib37")) {
												createConfigRequest.setAttrib37(attriValue);
												break;
											}
											if (attribName.equals("Attrib38")) {
												createConfigRequest.setAttrib38(attriValue);
												break;
											}
											if (attribName.equals("Attrib39")) {
												createConfigRequest.setAttrib39(attriValue);
												break;
											}
											if (attribName.equals("Attrib40")) {
												createConfigRequest.setAttrib40(attriValue);
												break;
											}
											if (attribName.equals("Attrib41")) {
												createConfigRequest.setAttrib41(attriValue);
												break;
											}
											if (attribName.equals("Attrib42")) {
												createConfigRequest.setAttrib42(attriValue);
												break;
											}
											if (attribName.equals("Attrib43")) {
												createConfigRequest.setAttrib43(attriValue);
												break;
											}
											if (attribName.equals("Attrib44")) {
												createConfigRequest.setAttrib44(attriValue);
												break;
											}
											if (attribName.equals("Attrib45")) {
												createConfigRequest.setAttrib45(attriValue);
												break;
											}
											if (attribName.equals("Attrib46")) {
												createConfigRequest.setAttrib46(attriValue);
												break;
											}
											if (attribName.equals("Attrib47")) {
												createConfigRequest.setAttrib47(attriValue);
												break;
											}
											if (attribName.equals("Attrib48")) {
												createConfigRequest.setAttrib48(attriValue);
												break;
											}
											if (attribName.equals("Attrib49")) {
												createConfigRequest.setAttrib49(attriValue);
												break;
											}
											if (attribName.equals("Attrib50")) {
												createConfigRequest.setAttrib50(attriValue);
												break;
											}
											
											if (attribName.equals("OsVer")) {
												createConfigRequest.setOsVer(attriValue);
												break;
											}
											if (attribName.equals("HostNameConfig")) {
												createConfigRequest.setHostNameConfig(attriValue);
												break;
											}
											if (attribName.equals("LoggingBuffer")) {
												createConfigRequest.setLoggingBuffer(attriValue);
												break;
											}
											if (attribName.equals("MemorySize")) {
												createConfigRequest.setMemorySize(attriValue);
												break;
											}
											if (attribName.equals("LoggingSourceInterface")) {
												createConfigRequest.setLoggingSourceInterface(attriValue);
												break;
											}
											if (attribName.equals("IPTFTPSourceInterface")) {
												createConfigRequest.setiPTFTPSourceInterface(attriValue);
												break;
											}
											if (attribName.equals("IPFTPSourceInterface")) {
												createConfigRequest.setiPFTPSourceInterface(attriValue);
												break;
											}
											if (attribName.equals("LineConPassword")) {
												createConfigRequest.setLineConPassword(attriValue);
												break;
											}
											if (attribName.equals("LineAuxPassword")) {
												createConfigRequest.setLineAuxPassword(attriValue);
												break;
											}
											if (attribName.equals("LineVTYPassword")) {
												createConfigRequest.setLineVTYPassword(attriValue);
												break;
											}
											if (attribName.equals("M_Attrib1")) {
												createConfigRequest.setM_Attrib1(attriValue);
												break;
											}
											if (attribName.equals("M_Attrib2")) {
												createConfigRequest.setM_Attrib2(attriValue);
												break;
											}
											if (attribName.equals("M_Attrib3")) {
												createConfigRequest.setM_Attrib3(attriValue);
												break;
											}
											if (attribName.equals("M_Attrib4")) {
												createConfigRequest.setM_Attrib4(attriValue);
												break;
											}
											if (attribName.equals("M_Attrib5")) {
												createConfigRequest.setM_Attrib5(attriValue);
												break;
											}
											if (attribName.equals("M_Attrib6")) {
												createConfigRequest.setM_Attrib6(attriValue);
												break;
											}
											if (attribName.equals("M_Attrib7")) {
												createConfigRequest.setM_Attrib7(attriValue);
												break;
											}
											if (attribName.equals("M_Attrib8")) {
												createConfigRequest.setM_Attrib8(attriValue);
												break;
											}
											if (attribName.equals("M_Attrib9")) {
												createConfigRequest.setM_Attrib9(attriValue);
												break;
											}
											if (attribName.equals("M_Attrib10")) {
												createConfigRequest.setM_Attrib10(attriValue);
												break;
											}
											if (attribName.equals("M_Attrib11")) {
												createConfigRequest.setM_Attrib11(attriValue);
												break;
											}
											if (attribName.equals("M_Attrib12")) {
												createConfigRequest.setM_Attrib12(attriValue);
												break;
											}
											if (attribName.equals("M_Attrib13")) {
												createConfigRequest.setM_Attrib13(attriValue);
												break;
											}
											if (attribName.equals("M_Attrib14")) {
												createConfigRequest.setM_Attrib14(attriValue);
												break;
											}
											if (attribName.equals("M_Attrib15")) {
												createConfigRequest.setM_Attrib15(attriValue);
												break;
											}

										}
									}
								}
							}

						}
					}

				}

				/*
				 * Create TemplateId for creating master configuration when template id is null
				 * or empty
				 */
				if (createConfigRequest.getTemplateID().equals("") || createConfigRequest.getTemplateID() == null) {
					String templateName = "";
					templateName = dcmConfigService.getTemplateName(createConfigRequest.getRegion(),
							createConfigRequest.getVendor(), createConfigRequest.getModel(),
							createConfigRequest.getOs(), createConfigRequest.getOsVersion());
					templateName = templateName + "_V1.0";
					createConfigRequest.setTemplateID(templateName);
				}
				logger.info("generateCreateRequestDetails - getTemplateID-  "+createConfigRequest.getTemplateID());
				// Create new Template
				invokeFtl.createFinalTemplate(null, cammandByTemplate, null, templateAttribute,
						createConfigRequest.getTemplateID());
				data = getConfigurationTemplateService.generateTemplate(createConfigRequest);

				obj.put(new String("output"), new String(data));
			}

		} catch (Exception exe) {
			logger.error("Exception occurred in generateCreateRequestDetails method - "+exe.getMessage());
			exe.printStackTrace();
		}
		return obj;

	}

}
