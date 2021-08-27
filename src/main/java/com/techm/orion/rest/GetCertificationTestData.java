package com.techm.orion.rest;


import java.util.List;

import javax.ws.rs.POST;
import javax.ws.rs.core.Response;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.google.gson.Gson;
import com.techm.orion.dao.RequestInfoDao;
import com.techm.orion.entitybeans.RfoDecomposedEntity;
import com.techm.orion.pojo.CertificationTestPojo;
import com.techm.orion.pojo.CreateConfigRequestDCM;
import com.techm.orion.repositories.RfoDecomposedRepository;
import com.techm.orion.service.CertificationTestFlagDetailsService;

@Controller
@RequestMapping("/GetCertificationTestData")
public class GetCertificationTestData {
	private static final Logger logger = LogManager.getLogger(GetCertificationTestData.class);
	@Autowired
	private RfoDecomposedRepository rfoDecomposedRepo;
	
	@Autowired
	private RequestInfoDao requestInfoDao;
	
	@Autowired
	private CertificationTestFlagDetailsService certificationTestFlagDetailsService;
	
	/**
	 *This Api is marked as ***************c3p-ui Api Impacted****************
	 **/
	@SuppressWarnings("unchecked")
	@POST
	@RequestMapping(value = "/getPrevalidationTestData", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
	@ResponseBody
	public Response getPrevalidationTestData(@RequestBody String configRequest) {
		
		JSONObject obj = new JSONObject();		
		String jsonArray = "";
		Response response = null;
		try {
			JSONArray dynamicTestArray = new JSONArray();	
			
			CreateConfigRequestDCM createConfigRequestDCM = fetchConfigRequestInput(configRequest);
			if(createConfigRequestDCM !=null && createConfigRequestDCM.getRequestId() !=null 
					&& createConfigRequestDCM.getVersion_report() !=null && createConfigRequestDCM.getTestType() !=null) {

				CertificationTestPojo certificationTestResultObject = certificationTestFlagDetailsService.getPrevalidationTestFlag(createConfigRequestDCM);
				jsonArray = new Gson().toJson(certificationTestResultObject);
				JSONObject resultObj = new JSONObject();
				// Logic to add wrapper to jsonArray to set it to a specific form
				JSONObject subObj = null;
				JSONParser parser = new JSONParser();
				JSONObject json2 = (JSONObject) parser.parse(jsonArray);
				JSONArray subObjArray = new JSONArray();
				if (json2.containsKey("deviceReachabilityTest")) {
					subObj = new JSONObject();
					subObj.put("testName", "Device Reachability");
					subObj.put("status", json2.get("deviceReachabilityTest"));
					subObj.put("value", "");
					subObjArray.add(subObj);
				}
				/*if (json2.containsKey("vendorTest")) {
					subObj = new JSONObject();
					subObj.put("testName", "Vendor Test");
					subObj.put("status", json2.get("vendorTest"));
					subObj.put("value", "");
					subObjArray.add(subObj);
	
				}
				if (json2.containsKey("frameLossTest")) {
					subObj = new JSONObject();
					subObj.put("testName", "Frameloss Test");
					subObj.put("status", json2.get("frameLossTest"));
					subObj.put("value", "");
					subObjArray.add(subObj);
	
				}
				if (json2.containsKey("deviceModelTest")) {
					subObj = new JSONObject();
					subObj.put("testName", "Device Model Test");
					subObj.put("status", json2.get("deviceModelTest"));
					subObj.put("value", "");
					subObjArray.add(subObj);
	
				}
				if (json2.containsKey("iosVersionTest")) {
					subObj = new JSONObject();
					subObj.put("testName", "OS Version Test");
					subObj.put("status", json2.get("iosVersionTest"));
					subObj.put("value", "");
					subObjArray.add(subObj);
	
				}
				if (json2.containsKey("prevalidationTest")) {
					subObj = new JSONObject();
					subObj.put("testName", "Prevalidation Test");
					subObj.put("status", json2.get("prevalidationTest"));
					subObj.put("value", "");
					subObjArray.add(subObj);
	
				}
				if (json2.containsKey("networkTest")) {
					subObj = new JSONObject();
					subObj.put("testName", "Network Test");
					subObj.put("status", json2.get("networkTest"));
					subObj.put("value", "");
					subObjArray.add(subObj);
	
				}
				if (json2.containsKey("showIpIntBriefCmd")) {
					subObj = new JSONObject();
					subObj.put("testName", "Interface Status");
					subObj.put("status", json2.get("showIpIntBriefCmd"));
					subObj.put("value", "");
					subObjArray.add(subObj);
	
				}
				if (json2.containsKey("showInterfaceCmd")) {
					subObj = new JSONObject();
					subObj.put("testName", "WAN Interface");
					subObj.put("status", json2.get("showInterfaceCmd"));
					subObj.put("value", "");
					subObjArray.add(subObj);
	
				}
				if (json2.containsKey("showVersionCmd")) {
					subObj = new JSONObject();
					subObj.put("testName", "Platform&OS");
					subObj.put("status", json2.get("showVersionCmd"));
					subObj.put("value", "");
					subObjArray.add(subObj);
	
				}
				if (json2.containsKey("showIpBgpSummaryCmd")) {
					subObj = new JSONObject();
					subObj.put("testName", "BGP neighbour");
					subObj.put("status", json2.get("showIpBgpSummaryCmd"));
					subObj.put("value", "");
					subObjArray.add(subObj);
	
				}*/
				if (json2.containsKey("throughputTest")) {
					subObj = new JSONObject();
					subObj.put("testName", "Throughput Test");
					subObj.put("status", json2.get("throughputTest"));
					subObj.put("value", "");
					subObjArray.add(subObj);
	
				}
				if (json2.containsKey("LatencyTest")) {
					subObj = new JSONObject();
					subObj.put("testName", "Latency Test");
					subObj.put("status", json2.get("LatencyTest"));
					subObj.put("value", "");
					subObjArray.add(subObj);
	
				}
	
				if (json2.containsKey("healthCheckTest")) {
					subObj = new JSONObject();
					subObj.put("testName", "healthCheck Test");
					subObj.put("status", json2.get("healthCheckTest"));
					subObj.put("value", "");
					subObjArray.add(subObj);
	
				}
				if (json2.containsKey("throughput")) {
					subObj = new JSONObject();
					subObj.put("testName", "throughput");
					subObj.put("status", "");
					subObj.put("value", json2.get("throughput"));
					subObjArray.add(subObj);
	
				}
				if (json2.containsKey("frameLoss")) {
					subObj = new JSONObject();
					subObj.put("testName", "frameLoss");
					subObj.put("status", "");
					subObj.put("value", json2.get("frameLoss"));
					subObjArray.add(subObj);
	
				}
				if (json2.containsKey("Latency")) {
					subObj = new JSONObject();
					subObj.put("testName", "Latency");
					subObj.put("status", "");
					subObj.put("value", json2.get("Latency"));
					subObjArray.add(subObj);
	
				}
				resultObj.put("Default", subObjArray);
				// to fetch dynamic test results
				if ("HealthTest".equalsIgnoreCase(createConfigRequestDCM.getTestType())) {
					if(StringUtils.startsWith(createConfigRequestDCM.getRequestId(),"SLGF")) {
						dynamicTestArray = requestInfoDao.getDynamicTestResult(createConfigRequestDCM.getRequestId(),
								createConfigRequestDCM.getVersion_report(), "Software Upgrade",createConfigRequestDCM.getTestType());
					}else {
					dynamicTestArray = requestInfoDao.getDynamicTestResult(createConfigRequestDCM.getRequestId(),
							createConfigRequestDCM.getVersion_report(), "Health Check",createConfigRequestDCM.getTestType());
					}
				} else if ("networkTest".equalsIgnoreCase(createConfigRequestDCM.getTestType())) {
					dynamicTestArray = requestInfoDao.getDynamicTestResult(createConfigRequestDCM.getRequestId(),
							createConfigRequestDCM.getVersion_report(), "Network Test",createConfigRequestDCM.getTestType());
	
				} else if ("othersTest".equalsIgnoreCase(createConfigRequestDCM.getTestType())) {
					dynamicTestArray = requestInfoDao.getDynamicTestResult(createConfigRequestDCM.getRequestId(),
							createConfigRequestDCM.getVersion_report(), "Others",createConfigRequestDCM.getTestType());
	
				} else if ("preValidate".equalsIgnoreCase(createConfigRequestDCM.getTestType())) {
					dynamicTestArray = requestInfoDao.getDynamicTestResult(createConfigRequestDCM.getRequestId(),
							createConfigRequestDCM.getVersion_report(), "Device Prevalidation",createConfigRequestDCM.getTestType());
	
				} else if ("networkAuditTest".equalsIgnoreCase(createConfigRequestDCM.getTestType())) {
					dynamicTestArray = requestInfoDao.getDynamicTestResult(createConfigRequestDCM.getRequestId(),
							createConfigRequestDCM.getVersion_report(), "Network Audit",createConfigRequestDCM.getTestType());
	
				}else if ("iospreValidate".equalsIgnoreCase(createConfigRequestDCM.getTestType())) {
					dynamicTestArray = requestInfoDao.getDynamicTestResult(createConfigRequestDCM.getRequestId(),
							createConfigRequestDCM.getVersion_report(), "Software Upgrade",createConfigRequestDCM.getTestType());	
				}				
				resultObj.put("Custom", dynamicTestArray);
	
				obj.put(new String("output"), resultObj);
				response = Response.status(200).header("Access-Control-Allow-Origin", "*")
						.header("Access-Control-Allow-Headers", "origin, content-type, accept, authorization")
						.header("Access-Control-Allow-Credentials", "true")
						.header("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS, HEAD")
						.header("Access-Control-Max-Age", "1209600").entity(obj).build();
			}else {
				obj.put(new String("Error"), "Missing mandatory inputs in the request");
				response = Response.status(400).header("Access-Control-Allow-Origin", "*")
						.header("Access-Control-Allow-Headers", "origin, content-type, accept, authorization")
						.header("Access-Control-Allow-Credentials", "true")
						.header("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS, HEAD")
						.header("Access-Control-Max-Age", "1209600").entity(obj).build();
			}
			
		} catch (Exception exe) {
			logger.error("Error in getPrevalidationTestData -"+exe.getMessage());
			obj.put(new String("Error"), exe.getMessage());
			response = Response.status(400).header("Access-Control-Allow-Origin", "*")
					.header("Access-Control-Allow-Headers", "origin, content-type, accept, authorization")
					.header("Access-Control-Allow-Credentials", "true")
					.header("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS, HEAD")
					.header("Access-Control-Max-Age", "1209600").entity(obj).build();
		}

		return response;

	}
	/**
	 * This method will prepare the CreateConfigRequestDCM object with available inputs which passes through the api request
	 * @param configRequest
	 * @return createConfigRequestDCM
	 */
	private CreateConfigRequestDCM fetchConfigRequestInput(String configRequest) {
		CreateConfigRequestDCM createConfigRequestDCM = null;
		try {
			JSONParser parser = new JSONParser();
			JSONObject inputJson = (JSONObject) parser.parse(configRequest);
			createConfigRequestDCM = new CreateConfigRequestDCM();
			if(inputJson.containsKey("requestID") && inputJson.get("requestID") !=null) {
				createConfigRequestDCM.setRequestId(inputJson.get("requestID").toString());
				logger.info("fetchConfigRequestInput RequestId-"+createConfigRequestDCM.getRequestId());
			}else if (inputJson.containsKey("soID") && inputJson.get("soID") !=null) {
				logger.info("fetchConfigRequestInput soID-"+inputJson.get("soID"));
				/** Call c3p_rfo_decomposed to find the Request ID based on SO ID for external system */
				List<RfoDecomposedEntity> rfoDecomposedEntity = rfoDecomposedRepo.findRequestId(inputJson.get("soID").toString());
				if (rfoDecomposedEntity != null && rfoDecomposedEntity.size() >0) {
					createConfigRequestDCM.setRequestId(rfoDecomposedEntity.get(0).getOdRequestId());
					logger.info("fetchConfigRequestInput RequestId-"+createConfigRequestDCM.getRequestId());
				}
			}
			if(inputJson.containsKey("version") && inputJson.get("version") !=null) {
				createConfigRequestDCM.setVersion_report(inputJson.get("version").toString());
			}else {
				createConfigRequestDCM.setVersion_report("1.0");
			}
			if(inputJson.containsKey("testType") && inputJson.get("testType") !=null) {
				createConfigRequestDCM.setTestType(inputJson.get("testType").toString());
			}
		}catch(ParseException exe) {
			logger.error("Error in fetchConfigRequestInput method-"+exe.getMessage());
		}
		return createConfigRequestDCM;
	}

}
