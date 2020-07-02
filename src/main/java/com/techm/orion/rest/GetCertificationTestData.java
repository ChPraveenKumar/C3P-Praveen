package com.techm.orion.rest;

import java.util.Observable;
import java.util.Observer;

import javax.ws.rs.POST;
import javax.ws.rs.core.Response;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.google.gson.Gson;
import com.techm.orion.dao.RequestInfoDao;
import com.techm.orion.pojo.CertificationTestPojo;
import com.techm.orion.pojo.CreateConfigRequestDCM;
import com.techm.orion.service.CertificationTestFlagDetailsService;

@Controller
@RequestMapping("/GetCertificationTestData")
public class GetCertificationTestData implements Observer {
	private static final Logger logger = LogManager.getLogger(GetCertificationTestData.class);

	@SuppressWarnings("unchecked")
	@POST
	@RequestMapping(value = "/getPrevalidationTestData", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
	@ResponseBody
	public Response getPrevalidationTestData(@RequestBody String configRequest) {

		CertificationTestFlagDetailsService FlagDetailsService = new CertificationTestFlagDetailsService();
		JSONObject obj = new JSONObject();
		RequestInfoDao dao = new RequestInfoDao();
		String jsonArray = "";

		try {
			JSONArray dynamicTestArray = new JSONArray();
			JSONObject dynamicTestObj = new JSONObject();
			JSONParser parser = new JSONParser();
			JSONObject json = (JSONObject) parser.parse(configRequest);
			CertificationTestPojo certificationTestResultObject = new CertificationTestPojo();
			CreateConfigRequestDCM createConfigRequestDCM = new CreateConfigRequestDCM();

			createConfigRequestDCM.setRequestId(json.get("requestID").toString());
			createConfigRequestDCM.setVersion_report(json.get("version").toString());
			createConfigRequestDCM.setTestType(json.get("testType").toString());

			certificationTestResultObject = FlagDetailsService.getPrevalidationTestFlag(createConfigRequestDCM);
			jsonArray = new Gson().toJson(certificationTestResultObject);
			JSONObject resultObj = new JSONObject();
			// Logic to add wrapper to jsonArray to set it to a specific form
			JSONObject subObj = null;
			JSONObject json2 = (JSONObject) parser.parse(jsonArray);
			JSONArray subObjArray = new JSONArray();
			if (json2.containsKey("deviceReachabilityTest")) {
				subObj = new JSONObject();
				subObj.put("testName", "Device Reachability");
				subObj.put("status", json2.get("deviceReachabilityTest"));
				subObj.put("value", "");
				subObjArray.add(subObj);
			}
			if (json2.containsKey("vendorTest")) {
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

			}
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
			if (createConfigRequestDCM.getTestType().equalsIgnoreCase("HealthTest")) {
				dynamicTestArray = dao.getDynamicTestResult(createConfigRequestDCM.getRequestId(),
						createConfigRequestDCM.getVersion_report(), "Health Check");
			} else if (createConfigRequestDCM.getTestType().equalsIgnoreCase("networkTest")) {
				dynamicTestArray = dao.getDynamicTestResult(createConfigRequestDCM.getRequestId(),
						createConfigRequestDCM.getVersion_report(), "Network Test");

			} else if (createConfigRequestDCM.getTestType().equalsIgnoreCase("othersTest")) {
				dynamicTestArray = dao.getDynamicTestResult(createConfigRequestDCM.getRequestId(),
						createConfigRequestDCM.getVersion_report(), "Others");

			} else if (createConfigRequestDCM.getTestType().equalsIgnoreCase("preValidate")) {
				dynamicTestArray = dao.getDynamicTestResult(createConfigRequestDCM.getRequestId(),
						createConfigRequestDCM.getVersion_report(), "Device Prevalidation");

			} else if (createConfigRequestDCM.getTestType().equalsIgnoreCase("networkAuditTest")) {
				dynamicTestArray = dao.getDynamicTestResult(createConfigRequestDCM.getRequestId(),
						createConfigRequestDCM.getVersion_report(), "Network Audit");

			}
			// JSONParser parser1 = new JSONParser();
			// JSONObject json1 = (JSONObject) parser1.parse(jsonArray);
			// json1.put("Custom", dynamicTestArray);
			resultObj.put("Custom", dynamicTestArray);

			obj.put(new String("output"), resultObj);

			// obj.put(new String("testType"), new
			// String(createConfigRequestDCM.getTestType()));
		} catch (Exception e) {
			logger.error(e);
		}

		return Response.status(200).header("Access-Control-Allow-Origin", "*")
				.header("Access-Control-Allow-Headers", "origin, content-type, accept, authorization")
				.header("Access-Control-Allow-Credentials", "true")
				.header("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS, HEAD")
				.header("Access-Control-Max-Age", "1209600").entity(obj).build();

	}

	@Override
	public void update(Observable o, Object arg) {
		// TODO Auto-generated method stub

	}

}
