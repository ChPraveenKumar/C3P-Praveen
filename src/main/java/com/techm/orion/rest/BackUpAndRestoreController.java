package com.techm.orion.rest;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.LineNumberReader;
import java.io.OutputStream;
import java.io.PrintStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.core.Response;

import org.apache.commons.lang3.StringUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.jcraft.jsch.Channel;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.techm.orion.dao.RequestInfoDao;
import com.techm.orion.dao.TemplateManagementDao;
import com.techm.orion.entitybeans.DeviceInterfaceEntity;
import com.techm.orion.entitybeans.InternetInfoEntity;
import com.techm.orion.entitybeans.RequestDetailsBackUpAndRestoreEntity;
import com.techm.orion.entitybeans.RequestDetailsEntity;
import com.techm.orion.entitybeans.RequestInfoEntity;
import com.techm.orion.entitybeans.VendorDetails;
import com.techm.orion.models.BackUpRequestVersioningJSONModel;
import com.techm.orion.models.TemplateVersioningJSONModel;
import com.techm.orion.pojo.AttribCreateConfigPojo;
import com.techm.orion.pojo.CreateConfigPojo;
import com.techm.orion.pojo.CreateConfigRequest;
import com.techm.orion.pojo.CreateConfigRequestDCM;
import com.techm.orion.pojo.SearchParamPojo;
import com.techm.orion.pojo.TemplateBasicConfigurationPojo;
import com.techm.orion.pojo.UserPojo;
import com.techm.orion.repositories.DeviceInterfaceRepo;
import com.techm.orion.repositories.InternetInfoRepo;
import com.techm.orion.repositories.RequestDetailsBackUpAndRestoreRepo;
import com.techm.orion.repositories.RequestDetailsImportRepo;
import com.techm.orion.repositories.RequestInfoDetailsRepositories;
import com.techm.orion.repositories.RouterVfRepo;
import com.techm.orion.repositories.VendorDetailsRepository;
import com.techm.orion.repositories.WebServiceRepo;
import com.techm.orion.service.BackupCurrentRouterConfigurationService;
import com.techm.orion.service.DcmConfigService;
import com.techm.orion.service.ErrorCodeValidationDeliveryTest;
import com.techm.orion.service.TemplateManagementDetailsService;
import com.techm.orion.utility.InvokeFtl;
import com.techm.orion.utility.TextReport;

@Controller
@RequestMapping("/BackUpConfigurationAndTest")
public class BackUpAndRestoreController {

	public static String TSA_PROPERTIES_FILE = "TSA.properties";
	public static final Properties TSA_PROPERTIES = new Properties();

	List<String> files = new ArrayList<String>();

	@Autowired
	public RequestDetailsImportRepo requestDetailsImportRepo;

	@Autowired
	public DeviceInterfaceRepo deviceInterfaceRepo;

	@Autowired
	public InternetInfoRepo internetInfoRepo;

	@Autowired
	public RouterVfRepo routerVfRepo;

	@Autowired
	public WebServiceRepo webServiceRepo;

	@Autowired
	public VendorDetailsRepository vendorDetailsRepo;

	@Autowired
	public RequestDetailsBackUpAndRestoreRepo requestDetailsBackUpAndRestoreRepo;

	@Autowired
	DcmConfigService dcmConfigService;

	@Autowired
	ConfigMngmntService myObj;

	@Autowired
	public RequestInfoDetailsRepositories requestInfoDetailsRepositories;
	
	@SuppressWarnings("unchecked")
	@POST
	@RequestMapping(value = "/getVendorCheck", method = RequestMethod.POST, produces = "application/json")
	@ResponseBody
	public Response getVendorStatus(@RequestBody String request) {

		JSONObject obj = new JSONObject();
		JSONParser parser = new JSONParser();
		String vendorCheck = null, str = null;
		List<VendorDetails> vendorList = new ArrayList<VendorDetails>();

		try {
			obj = (JSONObject) parser.parse(request);

			vendorCheck = obj.get("Vendor").toString();

			vendorList = vendorDetailsRepo.findByVendor(vendorCheck);

			for (int i = 0; i < vendorList.size(); i++) {

				if (vendorList.get(i).getVendor().contains(vendorCheck)) {
					str = "true";
				}

			}

			if (vendorList.isEmpty()) {
				str = "false";
			}

		} catch (Exception e) {
			System.out.println(e);
		}

		return Response.status(200).entity(str).build();
	}

	@SuppressWarnings("unchecked")
	@POST
	@RequestMapping(value = "/getManagementIP", method = RequestMethod.POST, produces = "application/json")
	@ResponseBody
	public Response getManagementIP(@RequestBody String request) {

		JSONObject obj = new JSONObject();
		JSONParser parser = new JSONParser();
		String hostName = null, str = null;

		List<RequestDetailsEntity> hostNameList = new ArrayList<RequestDetailsEntity>();

		try {
			obj = (JSONObject) parser.parse(request);

			hostName = obj.get("Hostname").toString();

			hostNameList = requestDetailsImportRepo.findByHostname(hostName);

			for (int i = 0; i < hostNameList.size(); i++) {

				if (hostNameList.get(i).getHostname().contains(hostName)) {
					str = hostName;
				}

			}

			if (hostNameList.isEmpty()) {
				str = "This hostName is not supported. Please contact system admin";
			}

		} catch (Exception e) {
			System.out.println(e);
		}

		return Response.status(200).entity(str).build();
	}

	@SuppressWarnings("unchecked")
	@POST
	@RequestMapping(value = "/getBackUpSRList", method = RequestMethod.GET, produces = "application/json")
	@ResponseBody
	public Response getBackUpSRList() {

		JSONObject obj = new JSONObject();
		String jsonArray = "";
		RequestDetailsBackUpAndRestoreEntity service = new RequestDetailsBackUpAndRestoreEntity();
		List<RequestDetailsBackUpAndRestoreEntity> list = new ArrayList<RequestDetailsBackUpAndRestoreEntity>();

		try {
			JSONParser parser = new JSONParser();
			List<BackUpRequestVersioningJSONModel> versioningModel = new ArrayList<BackUpRequestVersioningJSONModel>();
			List<RequestDetailsBackUpAndRestoreEntity> versioningModelChildList = new ArrayList<RequestDetailsBackUpAndRestoreEntity>();
			RequestDetailsBackUpAndRestoreEntity objToAdd = null;
			BackUpRequestVersioningJSONModel versioningModelObject = null;
			String requestType = null;
			list = requestDetailsBackUpAndRestoreRepo.findAll();
			// create treeview json
			for (int i = 0; i < list.size(); i++) {
				boolean objectPrsent = false;
				if (versioningModel.size() > 0) {
					for (int j = 0; j < versioningModel.size(); j++) {
						if (versioningModel.get(j).getHostname()
								.equalsIgnoreCase(list.get(i).getHostname())) {
							objectPrsent = true;
							break;
						}
					}
				}

				objToAdd = list.get(i);
				String backUpRequestCheck = objToAdd.getAlphanumericReqId()
						.substring(0, 4);

				if (objectPrsent == false
						&& backUpRequestCheck.contains("SLGB")) {
					versioningModelObject = new BackUpRequestVersioningJSONModel();
					objToAdd = new RequestDetailsBackUpAndRestoreEntity();
					objToAdd = list.get(i);
					versioningModelObject.setHostname(objToAdd.getHostname());

					versioningModelObject.setVendor(objToAdd.getVendor());
					versioningModelObject.setDevice_type(objToAdd
							.getDevice_type());

					versioningModelObject.setModel(objToAdd.getModel());

					versioningModelObject.setManagementIp(objToAdd
							.getManagementIp());
					versioningModelObject.setModel(objToAdd.getModel());

					versioningModelObject.setRequest_creator_name(objToAdd
							.getRequest_creator_name());

					versioningModelChildList = new ArrayList<RequestDetailsBackUpAndRestoreEntity>();
					for (int k = 0; k < list.size(); k++) {

						requestType = list
								.get(k)
								.getAlphanumericReqId()
								.substring(
										0,
										Math.min(list.get(k)
												.getAlphanumericReqId()
												.length(), 4));

						if (list.get(k)
								.getHostname()
								.equalsIgnoreCase(
										versioningModelObject.getHostname())
								&& requestType.equals("SLGB")) {
							versioningModelChildList.add(list.get(k));
						}
					}
					Collections.reverse(versioningModelChildList);

					versioningModelObject
							.setChildList(versioningModelChildList);
					versioningModel.add(versioningModelObject);

				}

			}

			jsonArray = new Gson().toJson(versioningModel);
			obj.put(new String("output"), jsonArray);

		} catch (Exception e) {
			System.out.println(e);
		}

		return Response
				.status(200)
				.header("Access-Control-Allow-Origin", "*")
				.header("Access-Control-Allow-Headers",
						"origin, content-type, accept, authorization")
				.header("Access-Control-Allow-Credentials", "true")
				.header("Access-Control-Allow-Methods",
						"GET, POST, PUT, DELETE, OPTIONS, HEAD")
				.header("Access-Control-Max-Age", "1209600").entity(obj)
				.build();

	}

	@SuppressWarnings("unchecked")
	@POST
	@RequestMapping(value = "/getBaselineVersion", method = RequestMethod.POST, produces = "application/json")
	@ResponseBody
	public Response getBaselineData(@RequestBody String request) {

		JSONObject obj = new JSONObject();
		JSONParser parser = new JSONParser();
		String hostName = null, requestId = null, requestIdToCheck = null, str = null;

		List<RequestDetailsBackUpAndRestoreEntity> baseLineVersionList = new ArrayList<RequestDetailsBackUpAndRestoreEntity>();

		try {
			obj = (JSONObject) parser.parse(request);

			hostName = obj.get("hostname").toString();
			requestId = obj.get("alphanumericReqId").toString();

			baseLineVersionList = requestDetailsBackUpAndRestoreRepo
					.findByHostname(hostName);
			for (int i = 0; i < baseLineVersionList.size(); i++) {

				requestIdToCheck = baseLineVersionList.get(i)
						.getAlphanumericReqId();

				if (requestIdToCheck.equals(requestId)) {
					baseLineVersionList.get(i).setBaselinedFlag(true);
				} else

				{

					baseLineVersionList.get(i).setBaselinedFlag(false);
				}
			}

			requestDetailsBackUpAndRestoreRepo.save(baseLineVersionList);
			str = "Baseline version reset successfully";

		} catch (Exception e) {
			System.out.println(e);
		}

		return Response.status(200).entity(str).build();
	}

	@POST
	@RequestMapping(value = "/createConfigurationDcmBackUpAndRestore", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
	@ResponseBody
	public JSONObject createConfigurationDcmBackUpAndRestore(
			@RequestBody String configRequest) {

		JSONObject obj = new JSONObject();
		String hostName = "", managementIp = "",scheduledTime="";
		Boolean startup;
		
	
		int requestInfoId = 0;
		List<RequestInfoEntity> requestDetail = null;
		List<RequestInfoEntity> requestDetail1 = null;
		List<RequestInfoEntity> requestDetail2 = null;

		InternetInfoEntity internetRequestDetail = null;

		DeviceInterfaceEntity deviceInterfaceDetail = null;

		ObjectMapper mapper = new ObjectMapper();

		try {

			JSONParser parser = new JSONParser();

			JSONObject json = (JSONObject) parser.parse(configRequest);

			CreateConfigRequestDCM configReqToSendToC3pCode = new CreateConfigRequestDCM();

			hostName = json.get("hostname").toString();
			managementIp = json.get("managementIp").toString();
			
			scheduledTime= json.get("scheduleDate").toString();
			
			

			requestDetail1 = requestInfoDetailsRepositories
					.findByHostNameAndManagmentIP(hostName, managementIp);

			int requestinfoid = 0;

			for (int i = 0; i < requestDetail1.size(); i++) {

				requestinfoid = requestDetail1.get(i).getInfoId();

			}

			requestDetail = requestInfoDetailsRepositories
					.findByInfoId(requestinfoid);
			

			for (int i = 0; i < requestDetail.size(); i++) {

				requestInfoId = requestDetail.get(i).getInfoId();

				configReqToSendToC3pCode.setRequestType("SLGB");
				configReqToSendToC3pCode.setNetworkType(requestDetail.get(i).getNetworkType());

				configReqToSendToC3pCode.setTemplateID(requestDetail.get(i)
						.getTemplateUsed());

				configReqToSendToC3pCode.setCustomer(requestDetail.get(i)
						.getCustomer());
				configReqToSendToC3pCode.setSiteid(requestDetail.get(i)
						.getSiteId());
				Boolean isStartUp = (Boolean) json.get("startup");
				configReqToSendToC3pCode.setIsStartUp(isStartUp);
				
				configReqToSendToC3pCode.setRequestType_Flag(requestDetail.get(i).getRequestTypeFlag());
				
				configReqToSendToC3pCode.setDeviceType(requestDetail.get(i)
						.getDeviceType());
				configReqToSendToC3pCode.setModel(requestDetail.get(i)
						.getModel());
				configReqToSendToC3pCode.setOs(requestDetail.get(i).getOs());

				configReqToSendToC3pCode.setOsVersion(requestDetail.get(i)
						.getOsVersion());
				configReqToSendToC3pCode.setService(requestDetail.get(i).getService());

				configReqToSendToC3pCode.setSiteName(requestDetail.get(i).getSiteName());
				

				configReqToSendToC3pCode.setManagementIp(requestDetail.get(i)
						.getManagmentIP());

			

				configReqToSendToC3pCode.setRequest_creator_name(requestDetail
						.get(i).getRequestOwnerName());


				configReqToSendToC3pCode.setRegion(requestDetail.get(i)
						.getRegion());
				configReqToSendToC3pCode.setService(requestDetail.get(i)
						.getService());
				configReqToSendToC3pCode.setHostname(requestDetail.get(i)
						.getHostName());
			
				configReqToSendToC3pCode.setVendor(requestDetail.get(i)
						.getVendor());
			
				configReqToSendToC3pCode.setRequest_version(1.0);
				
				configReqToSendToC3pCode.setRequest_creator_name(requestDetail.get(i).getRequestCreatorName());
				
				configReqToSendToC3pCode.setRequest_parent_version(1.0);
				configReqToSendToC3pCode.setStatus(requestDetail.get(i).getStatus());
				configReqToSendToC3pCode.setFamily(requestDetail.get(i).getFamily());
				configReqToSendToC3pCode.setDateofProcessing(requestDetail.get(i).getDateofProcessing());
				
				if(!(scheduledTime.isEmpty()))
				{
				configReqToSendToC3pCode.setBackUpScheduleTime(scheduledTime);
				}
				String jsonString = mapper
						.writeValueAsString(configReqToSendToC3pCode);

				obj = myObj.createConfigurationDcm(jsonString);

				requestDetail2 = requestInfoDetailsRepositories
						.findByHostNameAndManagmentIP(hostName, managementIp);

				for (int i1 = 0; i1 < requestDetail2.size(); i1++) {

					String type = requestDetail2
							.get(i1)
							.getAlphanumericReqId()
							.substring(
									0,
									Math.min(requestDetail2.get(i1)
											.getAlphanumericReqId().length(), 4));

					if (type.equals("SLGB")
							&& (!(requestDetail2.get(i1).getIsBaselineFlag()))) {
						requestDetail2.get(i1).setIsBaselineFlag(true);
						requestInfoDetailsRepositories.save(requestDetail2);

						break;

					} else if (type.equals("SLGB")
							&& ((requestDetail2.get(i1).getIsBaselineFlag()))) {
						break;
					}

				}

				obj.put("output", "Backup Request created successfully");

			}

		}

		catch (Exception e) {
		}
		return obj;

	}

	/* Web service call to search request based on user input */
	@SuppressWarnings("unchecked")
	@POST
	@RequestMapping(value = "/search", method = RequestMethod.POST, produces = "application/json")
	@ResponseBody
	public Response getAllBackUpRequest(@RequestBody String searchParameters) {

		JSONObject obj = new JSONObject();
		Gson gson = new Gson();
		String jsonArray = "";
		String key = null, value = null;
		Boolean flag = false;

		List<RequestDetailsBackUpAndRestoreEntity> detailsList = new ArrayList<RequestDetailsBackUpAndRestoreEntity>();
		List<BackUpRequestVersioningJSONModel> detailsListFinal = new ArrayList<BackUpRequestVersioningJSONModel>();
		List<RequestDetailsBackUpAndRestoreEntity> emptyList = new ArrayList<RequestDetailsBackUpAndRestoreEntity>();
		detailsList = requestDetailsBackUpAndRestoreRepo.findAll();

		try {

			SearchParamPojo dto = gson.fromJson(searchParameters,
					SearchParamPojo.class);

			key = dto.getKey();
			value = dto.getValue();

			if (value != null && !value.isEmpty()) {
				/*
				 * Search request based on Region, Vendor, Status, Model, Host
				 * Name and Management IP
				 */
				if (key.equalsIgnoreCase("Request ID")) {
					detailsList = requestDetailsBackUpAndRestoreRepo
							.findByAlphanumericReqId(value);

					detailsListFinal = searchImportDashboard(detailsList);
					flag = true;

				} else if (key.equalsIgnoreCase("Vendor")) {
					detailsList = requestDetailsBackUpAndRestoreRepo
							.findByVendor(value);

					detailsListFinal = searchImportDashboard(detailsList);
					flag = true;

				} else if (key.equalsIgnoreCase("Device Type")) {
					detailsList = requestDetailsBackUpAndRestoreRepo
							.findByDeviceType(value);

					detailsListFinal = searchImportDashboard(detailsList);
					flag = true;

				} else if (key.equalsIgnoreCase("Model")) {
					detailsList = requestDetailsBackUpAndRestoreRepo
							.findByModel(value);

					detailsListFinal = searchImportDashboard(detailsList);
					flag = true;

				} else if (key.equalsIgnoreCase("Hostname")) {
					detailsList = requestDetailsBackUpAndRestoreRepo
							.findByHostname(value);

					detailsListFinal = searchImportDashboard(detailsList);
					flag = true;

				} else if (key.equalsIgnoreCase("IP Address")) {
					detailsList = requestDetailsBackUpAndRestoreRepo
							.findByManagementIp(value);

					detailsListFinal = searchImportDashboard(detailsList);
					flag = true;

				}

			}

			if (flag == true) {
				jsonArray = gson.toJson(detailsListFinal);
				obj.put(new String("output"), jsonArray);
				flag = false;
			} else

			{

				jsonArray = new Gson().toJson(emptyList);
				obj.put(new String("output"), jsonArray);
			}

		}

		catch (Exception e) {
			System.out.println(e);

		}

		return Response
				.status(200)
				.header("Access-Control-Allow-Origin", "*")
				.header("Access-Control-Allow-Headers",
						"origin, content-type, accept, authorization")
				.header("Access-Control-Allow-Credentials", "true")
				.header("Access-Control-Allow-Methods",
						"GET, POST, PUT, DELETE, OPTIONS, HEAD")
				.header("Access-Control-Max-Age", "1209600").entity(obj)
				.build();
	}

	/* Method to iterate over all possible search result */
	private List<BackUpRequestVersioningJSONModel> searchImportDashboard(
			List<RequestDetailsBackUpAndRestoreEntity> list) {

		JSONObject obj = new JSONObject();
		String jsonArray = "";
		RequestDetailsBackUpAndRestoreEntity service = new RequestDetailsBackUpAndRestoreEntity();

		List<BackUpRequestVersioningJSONModel> versioningModel = new ArrayList<BackUpRequestVersioningJSONModel>();

		try {
			JSONParser parser = new JSONParser();

			List<RequestDetailsBackUpAndRestoreEntity> versioningModelChildList = new ArrayList<RequestDetailsBackUpAndRestoreEntity>();
			RequestDetailsBackUpAndRestoreEntity objToAdd = null;
			BackUpRequestVersioningJSONModel versioningModelObject = null;
			String requestType = null;

			// create treeview json
			for (int i = 0; i < list.size(); i++) {
				boolean objectPrsent = false;
				if (versioningModel.size() > 0) {
					for (int j = 0; j < versioningModel.size(); j++) {
						if (versioningModel.get(j).getHostname()
								.equalsIgnoreCase(list.get(i).getHostname())) {
							objectPrsent = true;
							break;
						}
					}
				}

				objToAdd = list.get(i);
				String backUpRequestCheck = objToAdd.getAlphanumericReqId()
						.substring(0, 4);

				if (objectPrsent == false
						&& backUpRequestCheck.contains("SLGB")) {
					versioningModelObject = new BackUpRequestVersioningJSONModel();
					objToAdd = new RequestDetailsBackUpAndRestoreEntity();
					objToAdd = list.get(i);
					versioningModelObject.setHostname(objToAdd.getHostname());

					versioningModelObject.setVendor(objToAdd.getVendor());
					versioningModelObject.setDevice_type(objToAdd
							.getDevice_type());

					versioningModelObject.setModel(objToAdd.getModel());

					versioningModelObject.setManagementIp(objToAdd
							.getManagementIp());
					versioningModelObject.setModel(objToAdd.getModel());

					versioningModelObject.setRequest_creator_name(objToAdd
							.getRequest_creator_name());

					versioningModelChildList = new ArrayList<RequestDetailsBackUpAndRestoreEntity>();
					for (int k = 0; k < list.size(); k++) {

						requestType = list
								.get(k)
								.getAlphanumericReqId()
								.substring(
										0,
										Math.min(list.get(k)
												.getAlphanumericReqId()
												.length(), 4));

						if (list.get(k)
								.getHostname()
								.equalsIgnoreCase(
										versioningModelObject.getHostname())
								&& requestType.equals("SLGB")) {
							versioningModelChildList.add(list.get(k));
						}
					}
					Collections.reverse(versioningModelChildList);

					versioningModelObject
							.setChildList(versioningModelChildList);
					versioningModel.add(versioningModelObject);

				}

			}

		} catch (Exception e) {
			System.out.println(e);
		}

		return versioningModel;

	}
}
