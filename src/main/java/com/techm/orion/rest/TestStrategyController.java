package com.techm.orion.rest;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.core.Response;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.google.gson.Gson;
import com.techm.orion.dao.RequestInfoDao;
import com.techm.orion.entitybeans.DeviceDiscoveryEntity;
import com.techm.orion.entitybeans.PredefineTestDetailEntity;
import com.techm.orion.entitybeans.RequestInfoEntity;
import com.techm.orion.entitybeans.SiteInfoEntity;
import com.techm.orion.entitybeans.TestDetail;
import com.techm.orion.entitybeans.TestFeatureList;
import com.techm.orion.entitybeans.TestRules;
import com.techm.orion.entitybeans.TestStrategeyVersioningJsonModel;
import com.techm.orion.pojo.BatchPojo;
import com.techm.orion.pojo.FirmwareUpgradeDetail;
import com.techm.orion.repositories.DeviceDiscoveryRepository;
import com.techm.orion.repositories.PredefineTestDetailsRepository;
import com.techm.orion.repositories.RequestInfoDetailsRepositories;
import com.techm.orion.repositories.TestBundlingRepository;
import com.techm.orion.repositories.TestDetailsRepository;
import com.techm.orion.repositories.TestFeatureListRepository;
import com.techm.orion.repositories.TestRulesRepository;

/*
 * Owner: Vivek Vidhate Module: Test Strategey Logic: To
 * Get, Save, edit, tree structure and show Network Audit tests for all rules(Text, Table, Section, Snippet, Keyword)
 */
@RestController
public class TestStrategyController {
	private static final Logger logger = LogManager.getLogger(TestStrategyController.class);
	@Autowired
	private TestDetailsRepository testDetailsRepository;

	@Autowired
	private TestFeatureListRepository testFeatureListRepository;

	@Autowired
	private TestRulesRepository testRulesRepository;

	@Autowired
	private PredefineTestDetailsRepository predefineTestDetailsRepository;

	@Autowired
	private RequestInfoDetailsRepositories requestInfoDetailsRepositories;

	@Autowired
	private DeviceDiscoveryRepository deviceDiscoveryRepository;

	@Autowired
	private TestBundlingRepository testBundleRepo;

	@GET
	@RequestMapping(value = "/testfeatureList", method = RequestMethod.GET, produces = "application/json")
	public Response getOsversions() {
		return Response.status(200).entity(testFeatureListRepository.findAll()).build();
	}

	@POST
	@RequestMapping(value = "/certificationtestsfordevice", method = RequestMethod.POST, produces = "application/json", consumes = "application/json")
	public Response getTestsForDevice(@RequestBody String request) {

		List<TestDetail> testDetailsList = new ArrayList<TestDetail>();
		List<TestDetail> testDetailsListLatestVersion = new ArrayList<TestDetail>();

		List<TestDetail> testDetailsFinal = new ArrayList<TestDetail>();
		List<TestDetail> testDetailsListAllVersion = new ArrayList<TestDetail>();

		HashSet<String> testNameList = new HashSet<>();
		String response = null;

		String deviceType = null, deviceModel = null, vendor = null, os = null, osVersion = null, region = null,
				networkType = null;
		JSONParser parser = new JSONParser();
		JSONObject json;
		List<String> featuresFromUI = new ArrayList<String>();
		try {
			json = (JSONObject) parser.parse(request);

			if (json.containsKey("featureList")) {
				JSONArray fArray = (JSONArray) json.get("featureList");
				for (int i = 0; i < fArray.size(); i++) {
					JSONObject fArrayObj = (JSONObject) fArray.get(i);
					if ((boolean) fArrayObj.get("selected")) {
						featuresFromUI.add(fArrayObj.get("value").toString());
					}
				}
			}
			if (json.containsKey("deviceType")) {
				deviceType = json.get("deviceType").toString();
			}
			if (json.containsKey("vendor")) {
				vendor = json.get("vendor").toString();
			}
			if (json.containsKey("deviceModel")) {
				deviceModel = json.get("deviceModel").toString();
			}
			if (json.containsKey("os")) {
				os = json.get("os").toString();
			}
			if (json.containsKey("osVersion")) {
				osVersion = json.get("osVersion").toString();
			}
			if (json.containsKey("region")) {
				region = json.get("region").toString();
			}

			if (json.containsKey("networkType")) {
				networkType = json.get("networkType").toString();
			}
			String version = null;
			String testName = null;
			testDetailsList = testDetailsRepository
					.findByDeviceFamilyIgnoreCaseContainingAndDeviceModelIgnoreCaseContainingAndOsIgnoreCaseContainingAndOsVersionIgnoreCaseContainingAndVendorIgnoreCaseContainingAndRegionIgnoreCaseContainingAndNetworkType(
							deviceType, deviceModel, os, osVersion, vendor, region, networkType);

			for (int i = 0; i < testDetailsList.size(); i++) {

				testNameList.add(testDetailsList.get(i).getTestName());

			}

			Iterator<String> itrator = testNameList.iterator();
			while (itrator.hasNext()) {
				testName = itrator.next();
				testDetailsListAllVersion = testDetailsRepository
						.findByDeviceFamilyIgnoreCaseContainingAndDeviceModelIgnoreCaseContainingAndOsIgnoreCaseContainingAndOsVersionIgnoreCaseContainingAndVendorIgnoreCaseContainingAndRegionIgnoreCaseContainingAndTestNameIgnoreCaseContaining(
								deviceType, deviceModel, os, osVersion, vendor, region, testName);

				for (int i = 0; i < testDetailsListAllVersion.size(); i++) {

					if (testName.equals(testDetailsListAllVersion.get(i).getTestName())) {
						version = testDetailsListAllVersion.get(i).getVersion();
					}
				}

				testDetailsListLatestVersion = testDetailsRepository
						.findByDeviceFamilyAndDeviceModelAndOsAndOsVersionAndVendorAndRegionAndVersionAndTestName(
								deviceType, deviceModel, os, osVersion, vendor, region, version, testName);

				if (null != testDetailsListLatestVersion && testDetailsListLatestVersion.size() > 0) {

					int n = testDetailsListLatestVersion.size();

					List<TestDetail> aList = new ArrayList<TestDetail>(n);
					for (TestDetail x : testDetailsListLatestVersion) {

						aList.add(x);
					}
					// Logic to set disabled and selected bit in the test detail
					// array to be sent to ui based on features selected

					if (featuresFromUI != null && featuresFromUI.size() > 0) {
						for (int i = 0; i < aList.size(); i++) {
							List<TestFeatureList> dbFeatures = testFeatureListRepository.findByTestDetail(aList.get(i));

							for (int j = 0; j < dbFeatures.size(); j++) {
								if (featuresFromUI.contains(dbFeatures.get(j).getTestFeature())) {
									aList.get(i).setSelected(true);
									aList.get(i).setDisabled(false);
								}
							}
							logger.info("");
						}
					} else {
						for (int i = 0; i < aList.size(); i++) {

							aList.get(i).setSelected(false);
							aList.get(i).setDisabled(false);

							logger.info("");
						}
					}

					testDetailsFinal.addAll(aList);
				}

				else {
					response = "Records not found";
					return Response.status(200).entity(response).build();
				}
			}
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			response = "Unable read GUI input";
			return Response.status(200).entity(response).build();
		}

		return Response.status(200).entity(testDetailsFinal).build();
	}

	@SuppressWarnings("unused")
	@GET
	@RequestMapping(value = "/getTestDetails", method = RequestMethod.GET, produces = "application/json")
	public Response getSeriess(@RequestParam String testid, String version) {

		Set<TestDetail> settestDetails = new HashSet<TestDetail>();

		List<TestFeatureList> testfeaturelist = new ArrayList<TestFeatureList>();
		List<TestRules> testruleslist = null;
		List<TestDetail> testdetaillist = new ArrayList<TestDetail>();
		List<TestRules> testruleslisttext = new ArrayList<TestRules>();
		List<TestRules> testruleslisttable = new ArrayList<TestRules>();
		List<TestRules> testruleslistsection = new ArrayList<TestRules>();
		List<TestRules> testruleslistsnippet = new ArrayList<TestRules>();
		List<TestRules> testruleslistkeyword = new ArrayList<TestRules>();
		List<TestRules> testruleslisttextfinal = new ArrayList<TestRules>();
		List<TestRules> testruleslisttablefinal = new ArrayList<TestRules>();
		List<TestRules> testruleslistsectionfinal = new ArrayList<TestRules>();
		List<TestRules> testruleslistsnippetfinal = new ArrayList<TestRules>();
		List<TestRules> testruleslistkeywordfinal = new ArrayList<TestRules>();

		TestDetail detail = null;
		boolean ischeck = false;
		settestDetails = testDetailsRepository.findByTestIdAndVersion(testid, version);
		testdetaillist.addAll(settestDetails);
		if (null != settestDetails && !settestDetails.isEmpty()) {

			detail = testdetaillist.get(0);

			testruleslist = testRulesRepository.findByTestDetail(testdetaillist.get(0));

			for (int i = 0; i < testruleslist.size(); i++) {

				if (testruleslist.get(i).getDataType().contains("Text")) {

					int ruleid = testruleslist.get(i).getId();

					testruleslisttext = testRulesRepository.findById(ruleid);

					testruleslisttextfinal.addAll(testruleslisttext);

				}
				if (testruleslist.get(i).getDataType().contains("Table")) {

					int ruleid = testruleslist.get(i).getId();

					testruleslisttable = testRulesRepository.findById(ruleid);

					testruleslisttablefinal.addAll(testruleslisttable);

				}
				if (testruleslist.get(i).getDataType().contains("Section")) {
					String data_type = "Section";

					int ruleid = testruleslist.get(i).getId();

					testruleslistsection = testRulesRepository.findById(ruleid);

					testruleslistsectionfinal.addAll(testruleslistsection);

				}
				if (testruleslist.get(i).getDataType().contains("Snippet")) {
					String data_type = "Snippet";

					int ruleid = testruleslist.get(i).getId();

					testruleslistsnippet = testRulesRepository.findById(ruleid);

					testruleslistsnippetfinal.addAll(testruleslistsnippet);

				}
				if (testruleslist.get(i).getDataType().contains("Keyword")) {
					String data_type = "Keyword";

					int ruleid = testruleslist.get(i).getId();

					testruleslistkeyword = testRulesRepository.findById(ruleid);

					testruleslistkeywordfinal.addAll(testruleslistkeyword);

				}
			}

			testfeaturelist = testFeatureListRepository.findByTestDetail(testdetaillist.get(0));
			detail.setListFeatures(testfeaturelist);
			detail.setText_attributes(testruleslisttextfinal);
			detail.setTable_attributes(testruleslisttablefinal);
			detail.setSection_attributes(testruleslistsectionfinal);
			detail.setSnippet_attributes(testruleslistsnippetfinal);
			detail.setKeyword_attributes(testruleslistkeywordfinal);

			ischeck = true;

		}
		if (ischeck) {
			return Response.status(200).entity(detail).build();
		} else {
			return Response.status(200).entity("Unable to fetch data for the test.").build();

		}

	}

	@GET
	@RequestMapping(value = "/getalltests", method = RequestMethod.GET, produces = "application/json")
	public Response getOs() {
		return Response.status(200).entity(testDetailsRepository.findAll()).build();
	}

	@POST
	@RequestMapping(value = "/savetestdetails", method = RequestMethod.POST, produces = "application/json", consumes = "application/json")
	public Response saveBasicConfiguration(@RequestBody String teststrategesaveRqst) {

		String str = "";
		JSONParser parser = new JSONParser();
		try {
			JSONObject json = (JSONObject) parser.parse(teststrategesaveRqst);
			TestDetail testDetail = new TestDetail();

			if (json.containsKey("testName")) {
				testDetail.setTestName(json.get("testName").toString());
				testDetail.setTestId(json.get("testName").toString());
			}
			if (json.containsKey("testCategory")) {
				testDetail.setTestCategory(json.get("testCategory").toString());
			}
			if (json.containsKey("version")) {
				testDetail.setVersion(json.get("version").toString());
			}

			if (json.containsKey("testType")) {
				testDetail.setTestType(json.get("testType").toString());
			}
			if (json.containsKey("connectionProtocol")) {
				testDetail.setTestConnectionProtocol(json.get("connectionProtocol").toString());
			}
			if (json.containsKey("command")) {
				testDetail.setTestCommand(json.get("command").toString());
			}

			if (json.containsKey("deviceFamily")) {
				testDetail.setDeviceFamily(json.get("deviceFamily").toString());
			}
			if (json.containsKey("vendor")) {
				testDetail.setVendor(json.get("vendor").toString());
			}
			if (json.containsKey("model")) {
				testDetail.setDeviceModel(json.get("model").toString());
			}
			if (json.containsKey("os")) {
				testDetail.setOs(json.get("os").toString());
			}
			if (json.containsKey("osVersion")) {
				testDetail.setOsVersion(json.get("osVersion").toString());
			}
			if (json.containsKey("region")) {
				testDetail.setRegion(json.get("region").toString());
			}
			if (json.containsKey("networkType")) {
				testDetail.setNetworkType(json.get("networkType").toString());
			}
			String timeStamp = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(new Date());
			testDetail.setCreatedOn(timeStamp);
			testDetail.setCreatedBy("admin");
			if (json.containsKey("Comment")) {
				testDetail.setComment(json.get("Comment").toString());
			}

			testDetail.setEnabled(true);

			List<TestFeatureList> list = new ArrayList<TestFeatureList>();
			if (json.containsKey("featureList")) {

				JSONArray jsonArray = (JSONArray) json.get("featureList");
				for (int i = 0; i < jsonArray.size(); i++) {
					TestFeatureList listObj = new TestFeatureList();
					listObj.setTestDetail(testDetail);
					listObj.setTestFeature(jsonArray.get(i).toString());
					list.add(listObj);
				}
			}

			Set<TestFeatureList> setFeatureList = new HashSet<TestFeatureList>(list);
			testDetail.setTestfeaturelist(setFeatureList);

			List<TestRules> rulelst = new ArrayList<TestRules>();

			if (json.containsKey("text_attributes")) {

				JSONArray attribarray = (JSONArray) json.get("text_attributes");
				for (int i = 0; i < attribarray.size(); i++) {
					TestRules rule = new TestRules();
					JSONObject attribobj = (JSONObject) attribarray.get(i);

					rule.setDataType("Text");

					if (attribobj.containsKey("reportedLabel")) {
						rule.setReportedLabel(attribobj.get("reportedLabel").toString());
					}
					if (attribobj.containsKey("beforeText")) {
						rule.setBeforeText(attribobj.get("beforeText").toString());

					}
					if (attribobj.containsKey("afterText")) {
						rule.setAfterText(attribobj.get("afterText").toString());
					}
					if (attribobj.containsKey("noOfChars")) {
						rule.setNumberOfChars(attribobj.get("noOfChars").toString());
					}
					if (attribobj.containsKey("fromColumn")) {
						rule.setFromColumn(attribobj.get("fromColumn").toString());
					}
					if (attribobj.containsKey("referenceColumn")) {
						rule.setReferenceColumn(attribobj.get("referenceColumn").toString());
					}
					if (attribobj.containsKey("whereKeyword")) {
						rule.setWhereKeyword(attribobj.get("whereKeyword").toString());
					}
					if (attribobj.containsKey("sectionName")) {
						rule.setSectionName(attribobj.get("sectionName").toString());
					}
					if (attribobj.containsKey("evaluation")) {
						rule.setEvaluation(attribobj.get("evaluation").toString());
					}
					if (attribobj.containsKey("operator")) {
						rule.setOperator(attribobj.get("operator").toString());
					}
					if (attribobj.containsKey("value1")) {
						rule.setValue1(attribobj.get("value1").toString());
					}
					if (attribobj.containsKey("value2")) {
						rule.setValue2(attribobj.get("value2").toString());
					}
					rule.setTestDetail(testDetail);
					rulelst.add(rule);
				}

			}

			if (json.containsKey("table_attributes")) {
				JSONArray attribarray = (JSONArray) json.get("table_attributes");
				for (int i = 0; i < attribarray.size(); i++) {
					TestRules rule = new TestRules();
					JSONObject attribobj = (JSONObject) attribarray.get(i);

					rule.setDataType("Table");

					if (attribobj.containsKey("reportedLabel")) {
						rule.setReportedLabel(attribobj.get("reportedLabel").toString());
					}
					if (attribobj.containsKey("beforeText")) {
						rule.setBeforeText(attribobj.get("beforeText").toString());

					}
					if (attribobj.containsKey("afterText")) {
						rule.setAfterText(attribobj.get("afterText").toString());
					}
					if (attribobj.containsKey("noOfChars")) {
						rule.setNumberOfChars(attribobj.get("noOfChars").toString());
					}
					if (attribobj.containsKey("fromColumn")) {
						rule.setFromColumn(attribobj.get("fromColumn").toString());
					}
					if (attribobj.containsKey("referenceColumn")) {
						rule.setReferenceColumn(attribobj.get("referenceColumn").toString());
					}
					if (attribobj.containsKey("whereKeyword")) {
						rule.setWhereKeyword(attribobj.get("whereKeyword").toString());
					}
					if (attribobj.containsKey("sectionName")) {
						rule.setSectionName(attribobj.get("sectionName").toString());
					}
					if (attribobj.containsKey("evaluation")) {
						rule.setEvaluation(attribobj.get("evaluation").toString());
					}
					if (attribobj.containsKey("operator")) {
						rule.setOperator(attribobj.get("operator").toString());
					}
					if (attribobj.containsKey("value1")) {
						rule.setValue1(attribobj.get("value1").toString());
					}
					if (attribobj.containsKey("value2")) {
						rule.setValue2(attribobj.get("value2").toString());
					}
					rule.setTestDetail(testDetail);
					rulelst.add(rule);
				}

			}

			if (json.containsKey("section_attributes")) {
				JSONArray attribarray = (JSONArray) json.get("section_attributes");
				for (int i = 0; i < attribarray.size(); i++) {
					TestRules rule = new TestRules();
					JSONObject attribobj = (JSONObject) attribarray.get(i);

					rule.setDataType("Section");

					if (attribobj.containsKey("reportedLabel")) {
						rule.setReportedLabel(attribobj.get("reportedLabel").toString());
					}
					if (attribobj.containsKey("beforeText")) {
						rule.setBeforeText(attribobj.get("beforeText").toString());

					}
					if (attribobj.containsKey("afterText")) {
						rule.setAfterText(attribobj.get("afterText").toString());
					}
					if (attribobj.containsKey("noOfChars")) {
						rule.setNumberOfChars(attribobj.get("noOfChars").toString());
					}
					if (attribobj.containsKey("fromColumn")) {
						rule.setFromColumn(attribobj.get("fromColumn").toString());
					}
					if (attribobj.containsKey("referenceColumn")) {
						rule.setReferenceColumn(attribobj.get("referenceColumn").toString());
					}
					if (attribobj.containsKey("whereKeyword")) {
						rule.setWhereKeyword(attribobj.get("whereKeyword").toString());
					}
					if (attribobj.containsKey("sectionName")) {
						rule.setSectionName(attribobj.get("sectionName").toString());
					}
					if (attribobj.containsKey("evaluation")) {
						rule.setEvaluation(attribobj.get("evaluation").toString());
					}
					if (attribobj.containsKey("operator")) {
						rule.setOperator(attribobj.get("operator").toString());
					}
					if (attribobj.containsKey("value1")) {
						rule.setValue1(attribobj.get("value1").toString());
					}
					if (attribobj.containsKey("value2")) {
						rule.setValue2(attribobj.get("value2").toString());
					}
					rule.setTestDetail(testDetail);
					rulelst.add(rule);
				}
			}

			if (json.containsKey("snippet_attributes")) {

				JSONArray attribarray = (JSONArray) json.get("snippet_attributes");
				for (int i = 0; i < attribarray.size(); i++) {
					TestRules rule = new TestRules();
					JSONObject attribobj = (JSONObject) attribarray.get(i);

					rule.setDataType("Snippet");

					if (attribobj.containsKey("reportedLabel")) {
						rule.setReportedLabel(attribobj.get("reportedLabel").toString());
					}

					if (attribobj.containsKey("evaluation")) {
						rule.setEvaluation(attribobj.get("evaluation").toString());
					}

					if (attribobj.containsKey("snippet")) {
						rule.setSnippet(attribobj.get("snippet").toString());
					}

					rule.setTestDetail(testDetail);
					rulelst.add(rule);
				}

			}

			if (json.containsKey("keyword_attributes")) {

				JSONArray attribarray = (JSONArray) json.get("keyword_attributes");
				for (int i = 0; i < attribarray.size(); i++) {
					TestRules rule = new TestRules();
					JSONObject attribobj = (JSONObject) attribarray.get(i);

					rule.setDataType("Keyword");

					if (attribobj.containsKey("reportedLabel")) {
						rule.setReportedLabel(attribobj.get("reportedLabel").toString());
					}

					if (attribobj.containsKey("evaluation")) {
						rule.setEvaluation(attribobj.get("evaluation").toString());
					}

					if (attribobj.containsKey("keyword")) {
						rule.setKeyword(attribobj.get("keyword").toString());
					}
					rule.setTestDetail(testDetail);
					rulelst.add(rule);

				}

			}

			Set<TestRules> setrules = new HashSet<TestRules>(rulelst);
			List<TestDetail> savetest1 = new ArrayList<TestDetail>();
			List<String> testNameCheck = new ArrayList<>();
			testDetail.setTestrules(setrules);
			try {
				savetest1 = testDetailsRepository.findAll();

				if (!(savetest1.isEmpty())) {
					for (int i = 0; i < savetest1.size(); i++) {
						String testName = savetest1.get(i).getTestName();
						testNameCheck.add(testName);
					}
				}
				if (!(testNameCheck.contains(testDetail.getTestName()))) {
					testDetailsRepository.save(testDetail);
					str = "Test saved successfully";
				} else {
					str = "Test is Duplicate";
				}

			} catch (DataIntegrityViolationException e) {
				// TODO Auto-generated catch block
				return Response.status(409).entity("Test is Duplicate").build();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				return Response.status(422).entity("Could not save service").build();
			}
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			logger.error(e);
		}
		return Response.status(200).entity(str).build();
	}

	@GET
	@RequestMapping(value = "/testStrategySearch", method = RequestMethod.GET, produces = "application/json")
	public Response get(@RequestParam String value, @RequestParam String key) {
		Set<TestDetail> result = null;
		if (key.equalsIgnoreCase("Test Name")) {
			result = testDetailsRepository.findByTestNameContaining(value);

		} else if (key.equalsIgnoreCase("Device Type")) {
			result = testDetailsRepository.findByDeviceFamilyContaining(value);
		} else if (key.equalsIgnoreCase("Vendor")) {
			result = testDetailsRepository.findByVendorContaining(value);
		} else if (key.equalsIgnoreCase("Device Model")) {
			result = testDetailsRepository.findByDeviceModelContaining(value);
		} else if (key.equalsIgnoreCase("OS")) {
			result = testDetailsRepository.findByOsContaining(value);
		} else if (key.equalsIgnoreCase("OS Version")) {
			result = testDetailsRepository.findByOsVersionContaining(value);
		} else if (key.equalsIgnoreCase("createdon")) {
			result = testDetailsRepository.findByCreatedOnContaining(value);

		} else if (key.equalsIgnoreCase("createdby")) {
			result = testDetailsRepository.findByCreatedByContaining(value);
		}
		return Response.status(200).entity(result).build();

	}

	@POST
	@RequestMapping(value = "/edittestdetails", method = RequestMethod.POST, produces = "application/json", consumes = "application/json")
	public Response editBasicConfiguration(@RequestBody String teststrategeeditRqst) {
		RequestInfoDao dao = new RequestInfoDao();
		String str = "";
		JSONParser parser = new JSONParser();
		try {
			JSONObject json = (JSONObject) parser.parse(teststrategeeditRqst);

			TestDetail testDetail = new TestDetail();

			if (json.containsKey("testName")) {
				testDetail.setTestName(json.get("testName").toString());
				testDetail.setTestId(json.get("testName").toString());
			}
			if (json.containsKey("testCategory")) {
				testDetail.setTestCategory(json.get("testCategory").toString());
			}
			if (json.containsKey("version")) {
				testDetail.setVersion(json.get("version").toString());
			}
			if (json.containsKey("testType")) {
				testDetail.setTestType(json.get("testType").toString());
			}
			if (json.containsKey("connectionProtocol")) {
				testDetail.setTestConnectionProtocol(json.get("connectionProtocol").toString());
			}
			if (json.containsKey("command")) {
				testDetail.setTestCommand(json.get("command").toString());
			}

			if (json.containsKey("deviceFamily")) {
				testDetail.setDeviceFamily(json.get("deviceFamily").toString());
			}
			if (json.containsKey("vendor")) {
				testDetail.setVendor(json.get("vendor").toString());
			}
			if (json.containsKey("model")) {
				testDetail.setDeviceModel(json.get("model").toString());
			}
			if (json.containsKey("networkType")) {
				testDetail.setNetworkType(json.get("networkType").toString());
			}
			if (json.containsKey("os")) {
				testDetail.setOs(json.get("os").toString());
			}
			if (json.containsKey("osVersion")) {
				testDetail.setOsVersion(json.get("osVersion").toString());
			}
			if (json.containsKey("region")) {
				testDetail.setRegion(json.get("region").toString());
			}
			String timeStamp = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(new Date());
			testDetail.setCreatedOn(timeStamp);
			testDetail.setCreatedBy("admin");

			if (json.containsKey("Comment")) {
				testDetail.setComment(json.get("Comment").toString());
			}
			List<TestFeatureList> list = new ArrayList<TestFeatureList>();
			if (json.containsKey("featureList")) {

				JSONArray jsonArray = (JSONArray) json.get("featureList");
				for (int i = 0; i < jsonArray.size(); i++) {
					TestFeatureList listObj = new TestFeatureList();
					listObj.setTestDetail(testDetail);
					listObj.setTestFeature(jsonArray.get(i).toString());
					list.add(listObj);
				}
			}

			Set<TestFeatureList> setFeatureList = new HashSet<TestFeatureList>(list);
			testDetail.setTestfeaturelist(setFeatureList);

			List<TestRules> rulelst = new ArrayList<TestRules>();

			if (json.containsKey("text_attributes")) {

				JSONArray attribarray = (JSONArray) json.get("text_attributes");
				for (int i = 0; i < attribarray.size(); i++) {
					TestRules rule = new TestRules();
					JSONObject attribobj = (JSONObject) attribarray.get(i);

					rule.setDataType("Text");

					if (attribobj.containsKey("reportedLabel")) {
						rule.setReportedLabel(attribobj.get("reportedLabel").toString());
					}
					if (attribobj.containsKey("beforeText")) {
						rule.setBeforeText(attribobj.get("beforeText").toString());

					}
					if (attribobj.containsKey("afterText")) {
						rule.setAfterText(attribobj.get("afterText").toString());
					}
					if (attribobj.containsKey("noOfChars")) {
						rule.setNumberOfChars(attribobj.get("noOfChars").toString());
					}
					if (attribobj.containsKey("fromColumn")) {
						rule.setFromColumn(attribobj.get("fromColumn").toString());
					}
					if (attribobj.containsKey("referenceColumn")) {
						rule.setReferenceColumn(attribobj.get("referenceColumn").toString());
					}
					if (attribobj.containsKey("whereKeyword")) {
						rule.setWhereKeyword(attribobj.get("whereKeyword").toString());
					}
					if (attribobj.containsKey("sectionName")) {
						rule.setSectionName(attribobj.get("sectionName").toString());
					}
					if (attribobj.containsKey("evaluation")) {
						rule.setEvaluation(attribobj.get("evaluation").toString());
					}
					if (attribobj.containsKey("operator")) {
						rule.setOperator(attribobj.get("operator").toString());
					}
					if (attribobj.containsKey("value1")) {
						rule.setValue1(attribobj.get("value1").toString());
					}
					if (attribobj.containsKey("value2")) {
						rule.setValue2(attribobj.get("value2").toString());
					}
					rule.setTestDetail(testDetail);

					rulelst.add(rule);

				}

			}

			if (json.containsKey("table_attributes")) {
				JSONArray attribarray = (JSONArray) json.get("table_attributes");
				for (int i = 0; i < attribarray.size(); i++) {
					TestRules rule = new TestRules();
					JSONObject attribobj = (JSONObject) attribarray.get(i);

					rule.setDataType("Table");

					if (attribobj.containsKey("reportedLabel")) {
						rule.setReportedLabel(attribobj.get("reportedLabel").toString());
					}
					if (attribobj.containsKey("beforeText")) {
						rule.setBeforeText(attribobj.get("beforeText").toString());

					}
					if (attribobj.containsKey("afterText")) {
						rule.setAfterText(attribobj.get("afterText").toString());
					}
					if (attribobj.containsKey("noOfChars")) {
						rule.setNumberOfChars(attribobj.get("noOfChars").toString());
					}
					if (attribobj.containsKey("fromColumn")) {
						rule.setFromColumn(attribobj.get("fromColumn").toString());
					}
					if (attribobj.containsKey("referenceColumn")) {
						rule.setReferenceColumn(attribobj.get("referenceColumn").toString());
					}
					if (attribobj.containsKey("whereKeyword")) {
						rule.setWhereKeyword(attribobj.get("whereKeyword").toString());
					}
					if (attribobj.containsKey("sectionName")) {
						rule.setSectionName(attribobj.get("sectionName").toString());
					}
					if (attribobj.containsKey("evaluation")) {
						rule.setEvaluation(attribobj.get("evaluation").toString());
					}
					if (attribobj.containsKey("operator")) {
						rule.setOperator(attribobj.get("operator").toString());
					}
					if (attribobj.containsKey("value1")) {
						rule.setValue1(attribobj.get("value1").toString());
					}
					if (attribobj.containsKey("value2")) {
						rule.setValue2(attribobj.get("value2").toString());
					}
					rule.setTestDetail(testDetail);
					rulelst.add(rule);

				}

			}

			if (json.containsKey("section_attributes")) {
				JSONArray attribarray = (JSONArray) json.get("section_attributes");
				for (int i = 0; i < attribarray.size(); i++) {
					TestRules rule = new TestRules();
					JSONObject attribobj = (JSONObject) attribarray.get(i);

					rule.setDataType("Section");

					if (attribobj.containsKey("reportedLabel")) {
						rule.setReportedLabel(attribobj.get("reportedLabel").toString());
					}
					if (attribobj.containsKey("beforeText")) {
						rule.setBeforeText(attribobj.get("beforeText").toString());

					}
					if (attribobj.containsKey("afterText")) {
						rule.setAfterText(attribobj.get("afterText").toString());
					}
					if (attribobj.containsKey("noOfChars")) {
						rule.setNumberOfChars(attribobj.get("noOfChars").toString());
					}
					if (attribobj.containsKey("fromColumn")) {
						rule.setFromColumn(attribobj.get("fromColumn").toString());
					}
					if (attribobj.containsKey("referenceColumn")) {
						rule.setReferenceColumn(attribobj.get("referenceColumn").toString());
					}
					if (attribobj.containsKey("whereKeyword")) {
						rule.setWhereKeyword(attribobj.get("whereKeyword").toString());
					}
					if (attribobj.containsKey("sectionName")) {
						rule.setSectionName(attribobj.get("sectionName").toString());
					}
					if (attribobj.containsKey("evaluation")) {
						rule.setEvaluation(attribobj.get("evaluation").toString());
					}
					if (attribobj.containsKey("operator")) {
						rule.setOperator(attribobj.get("operator").toString());
					}
					if (attribobj.containsKey("value1")) {
						rule.setValue1(attribobj.get("value1").toString());
					}
					if (attribobj.containsKey("value2")) {
						rule.setValue2(attribobj.get("value2").toString());
					}
					rule.setTestDetail(testDetail);
					rulelst.add(rule);

				}
			}
			if (json.containsKey("snippet_attributes")) {

				JSONArray attribarray = (JSONArray) json.get("snippet_attributes");
				for (int i = 0; i < attribarray.size(); i++) {
					TestRules rule = new TestRules();
					JSONObject attribobj = (JSONObject) attribarray.get(i);

					rule.setDataType("Snippet");

					if (attribobj.containsKey("reportedLabel")) {
						rule.setReportedLabel(attribobj.get("reportedLabel").toString());
					}

					if (attribobj.containsKey("evaluation")) {
						rule.setEvaluation(attribobj.get("evaluation").toString());
					}

					if (attribobj.containsKey("snippet")) {
						rule.setSnippet(attribobj.get("snippet").toString());
					}

					rule.setTestDetail(testDetail);
					rulelst.add(rule);
				}

			}

			if (json.containsKey("keyword_attributes")) {

				JSONArray attribarray = (JSONArray) json.get("keyword_attributes");
				for (int i = 0; i < attribarray.size(); i++) {
					TestRules rule = new TestRules();
					JSONObject attribobj = (JSONObject) attribarray.get(i);

					rule.setDataType("Keyword");

					if (attribobj.containsKey("reportedLabel")) {
						rule.setReportedLabel(attribobj.get("reportedLabel").toString());
					}

					if (attribobj.containsKey("evaluation")) {
						rule.setEvaluation(attribobj.get("evaluation").toString());
					}

					if (attribobj.containsKey("keyword")) {
						rule.setKeyword(attribobj.get("keyword").toString());
					}
					rule.setTestDetail(testDetail);
					rulelst.add(rule);

				}

			}

			String testName = json.get("testName").toString();
			List<TestDetail> testNameForVersion = testDetailsRepository.findByTestName(testName);

			for (int l = 0; l < testNameForVersion.size(); l++) {
				boolean is_enabled = false;
				dao.updateVersion(testName, is_enabled);
			}

			Set<TestRules> setrules = new LinkedHashSet<TestRules>(rulelst);
			testDetail.setEnabled(true);
			TestDetail savetest = new TestDetail();
			testDetail.setTestrules(setrules);
			try {
				savetest = testDetailsRepository.save(testDetail);

				if (savetest != null) {
					str = "Test edited successfully";
				} else {
					str = "Error occurred while editing test details";
				}
			} catch (DataIntegrityViolationException e) {
				// TODO Auto-generated catch block
				return Response.status(409).entity("Test is Duplicate").build();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				return Response.status(422).entity("Could not edit test").build();
			}
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			logger.error(e);
		}
		return Response.status(200).entity(str).build();
	}

	@POST
	@RequestMapping(value = "/networkAuditReport", method = RequestMethod.POST, produces = "application/json", consumes = "application/json")
	public Response networkAuditReport(@RequestBody String teststrategeeditRqst) {

		JSONParser parser = new JSONParser();
		try {
			JSONObject json = (JSONObject) parser.parse(teststrategeeditRqst);
			TestDetail testDetail = new TestDetail();

			if (json.containsKey("testName")) {
				testDetail.setTestName(json.get("testName").toString());
				testDetail.setTestId(json.get("testName").toString());
			}
		} catch (Exception e) {

		}
		return null;
	}

	@POST
	@RequestMapping(value = "/getTestList", method = RequestMethod.GET, produces = "application/json")
	@ResponseBody
	public Response getTestList() {

		// Create first level
		RequestInfoDao dao = new RequestInfoDao();
		List<TestDetail> mainList = new ArrayList<TestDetail>();
		mainList = dao.getAllTests();
		int testNameSize = 0, testNameSize1 = 0;
		TestStrategeyVersioningJsonModel model = new TestStrategeyVersioningJsonModel();

		List<TestStrategeyVersioningJsonModel> modelList = new ArrayList<TestStrategeyVersioningJsonModel>();

		for (int i = 0; i < mainList.size(); i++) {
			if ((modelList.size() == 0)) {
				model = new TestStrategeyVersioningJsonModel();
				String[] testNameToSetArr = mainList.get(i).getTestName().split("_");

				model.setTestName(testNameToSetArr[0]);
				model.setVersion(mainList.get(i).getVersion());
				model.setFullTestName(mainList.get(i).getTestName());
				model.setTestId(mainList.get(i).getTestId());
				model.setVendor((mainList.get(i).getVendor()));
				model.setDeviceFamily(mainList.get(i).getDeviceFamily());
				model.setDeviceModel(mainList.get(i).getDeviceModel());
				model.setOs(mainList.get(i).getOs());
				model.setOsVersion(mainList.get(i).getOsVersion());
				model.setRegion(mainList.get(i).getRegion());
				model.setCreatedOn(mainList.get(i).getCreatedOn());
				model.setCreatedBy(mainList.get(i).getCreatedBy());
				model.setComment(mainList.get(i).getComment());
				model.setNetworkType(mainList.get(i).getNetworkType());

				TestStrategeyVersioningJsonModel child = new TestStrategeyVersioningJsonModel();
				List<TestStrategeyVersioningJsonModel> childList = new ArrayList<TestStrategeyVersioningJsonModel>();

				testNameSize = testNameToSetArr.length;

				if (testNameSize == 2) {
					child.setTestName(testNameToSetArr[1]);
				} else {
					child.setTestName(testNameToSetArr[2]);
				}
				child.setFullTestName(mainList.get(i).getTestName());
				child.setVersion(mainList.get(i).getVersion());
				child.setTestId(mainList.get(i).getTestId());
				child.setVendor((mainList.get(i).getVendor()));
				child.setDeviceFamily(mainList.get(i).getDeviceFamily());
				child.setDeviceModel(mainList.get(i).getDeviceModel());
				child.setOs(mainList.get(i).getOs());
				child.setOsVersion(mainList.get(i).getOsVersion());
				child.setRegion(mainList.get(i).getRegion());
				child.setCreatedOn(mainList.get(i).getCreatedOn());
				child.setCreatedBy(mainList.get(i).getCreatedBy());
				child.setComment(mainList.get(i).getComment());
				child.setNetworkType(mainList.get(i).getNetworkType());

				childList.add(child);
				model.setChildList(childList);
				modelList.add(model);
			} else {

				boolean flag = false;
				for (int j = 0; j < modelList.size(); j++) {
					String[] arrOfStrMain = mainList.get(i).getTestName().split("_");
					// String[] arrOfStrModel =
					// modelList.get(j).getTest_name().split("_");

					if (arrOfStrMain[0].equalsIgnoreCase(modelList.get(j).getTestName())) {
						flag = true;
						List<TestStrategeyVersioningJsonModel> childList = new ArrayList<TestStrategeyVersioningJsonModel>();
						TestStrategeyVersioningJsonModel child = new TestStrategeyVersioningJsonModel();

						testNameSize1 = arrOfStrMain.length;

						if (testNameSize1 == 2) {
							child.setTestName(arrOfStrMain[1]);
						} else {
							child.setTestName(arrOfStrMain[2]);
						}
						child.setVersion(mainList.get(i).getVersion());
						child.setFullTestName(mainList.get(i).getTestName());
						child.setVersion(mainList.get(i).getVersion());
						child.setTestId(mainList.get(i).getTestId());
						child.setVendor((mainList.get(i).getVendor()));
						child.setDeviceFamily(mainList.get(i).getDeviceFamily());
						child.setDeviceModel(mainList.get(i).getDeviceModel());
						child.setOs(mainList.get(i).getOs());
						child.setOsVersion(mainList.get(i).getOsVersion());
						child.setRegion(mainList.get(i).getRegion());
						child.setCreatedOn(mainList.get(i).getCreatedOn());
						child.setCreatedBy(mainList.get(i).getCreatedBy());
						child.setComment(mainList.get(i).getComment());
						child.setNetworkType(mainList.get(i).getNetworkType());

						childList = modelList.get(j).getChildList();
						if (childList.size() == 0) {
							childList.add(child);
						} else {
							boolean found = false;

							for (int k = 0; k < childList.size(); k++) {
								if (childList.get(k).getTestName().equalsIgnoreCase(arrOfStrMain[testNameSize1 - 1])) {
									found = true;
								}
							}

							if (!found) {
								childList.add(child);
							}
						}

						modelList.get(j).setChildList(childList);

					}
				}
				if (flag == false) {
					model = new TestStrategeyVersioningJsonModel();
					String[] testNameToSetArr = mainList.get(i).getTestName().split("_");

					model.setTestName(testNameToSetArr[0]);
					model.setVersion(mainList.get(i).getVersion());
					model.setFullTestName(mainList.get(i).getTestName());
					model.setTestId(mainList.get(i).getTestId());
					model.setVendor((mainList.get(i).getVendor()));
					model.setDeviceFamily(mainList.get(i).getDeviceFamily());
					model.setDeviceModel(mainList.get(i).getDeviceModel());
					model.setOs(mainList.get(i).getOs());
					model.setOsVersion(mainList.get(i).getOsVersion());
					model.setRegion(mainList.get(i).getRegion());
					model.setCreatedOn(mainList.get(i).getCreatedOn());
					model.setCreatedBy(mainList.get(i).getCreatedBy());
					model.setComment(mainList.get(i).getComment());
					model.setNetworkType(mainList.get(i).getNetworkType());
					TestStrategeyVersioningJsonModel child = new TestStrategeyVersioningJsonModel();
					List<TestStrategeyVersioningJsonModel> childList = new ArrayList<TestStrategeyVersioningJsonModel>();

					testNameSize = testNameToSetArr.length;

					if (testNameSize == 2) {
						child.setTestName(testNameToSetArr[1]);
					} else {
						child.setTestName(testNameToSetArr[2]);
					}
					child.setFullTestName(mainList.get(i).getTestName());
					child.setVersion(mainList.get(i).getVersion());
					child.setTestId(mainList.get(i).getTestId());
					child.setVendor((mainList.get(i).getVendor()));
					child.setDeviceFamily(mainList.get(i).getDeviceFamily());
					child.setDeviceModel(mainList.get(i).getDeviceModel());
					child.setOs(mainList.get(i).getOs());
					child.setOsVersion(mainList.get(i).getOsVersion());
					child.setRegion(mainList.get(i).getRegion());
					child.setCreatedOn(mainList.get(i).getCreatedOn());
					child.setCreatedBy(mainList.get(i).getCreatedBy());
					child.setComment(mainList.get(i).getComment());
					child.setNetworkType(mainList.get(i).getNetworkType());

					childList.add(child);
					model.setChildList(childList);
					modelList.add(model);
				}

			}

		}

		// /////////Loop end for two level

		// //This loop will iterate over all the children
		for (int i = 0; i < mainList.size(); i++) {

			String[] testNameToSetArr = mainList.get(i).getTestName().split("_");
			String testNameMain = testNameToSetArr[0] + testNameToSetArr[2];
			String testNameToSet = testNameToSetArr[1] + "_" + testNameToSetArr[2];

			// mainList1 =dao.findByTestName(testNameUsed);

			for (int j = 0; j < modelList.size(); j++) {
				List<TestStrategeyVersioningJsonModel> childListToIterate = new ArrayList<TestStrategeyVersioningJsonModel>();
				childListToIterate = modelList.get(j).getChildList();
				String[] testNameToSetArr1 = modelList.get(j).getFullTestName().split("_");
				String testNameChild = testNameToSetArr1[0] + testNameToSetArr1[2];

				for (int k = 0; k < childListToIterate.size(); k++) {
					if (mainList.get(i).getTestName().equalsIgnoreCase(childListToIterate.get(k).getFullTestName())) {
						TestStrategeyVersioningJsonModel child = new TestStrategeyVersioningJsonModel();
						child.setTestName(mainList.get(i).getVersion());
						child.setTestName(testNameToSet);
						child.setFullTestName(mainList.get(i).getTestName());
						child.setVersion(mainList.get(i).getVersion());
						child.setTestId(mainList.get(i).getTestId());
						child.setVendor((mainList.get(i).getVendor()));
						child.setDeviceFamily(mainList.get(i).getDeviceFamily());
						child.setDeviceModel(mainList.get(i).getDeviceModel());
						child.setOs(mainList.get(i).getOs());
						child.setOsVersion(mainList.get(i).getOsVersion());
						child.setRegion(mainList.get(i).getRegion());
						child.setCreatedOn(mainList.get(i).getCreatedOn());
						child.setCreatedBy(mainList.get(i).getCreatedBy());
						child.setComment(mainList.get(i).getComment());
						child.setNetworkType(mainList.get(i).getNetworkType());

						Boolean flag = mainList.get(i).isEnabled();
						child.setEnabled(flag);

						List<TestStrategeyVersioningJsonModel> childList = childListToIterate.get(k).getChildList();
						childList.add(child);
						break;

					} else if (testNameMain.equals(testNameChild)) {

						TestStrategeyVersioningJsonModel child = new TestStrategeyVersioningJsonModel();
						child.setTestName(mainList.get(i).getVersion());
						child.setTestName(testNameToSet);
						child.setFullTestName(mainList.get(i).getTestName());
						child.setVersion(mainList.get(i).getVersion());
						child.setTestId(mainList.get(i).getTestId());
						child.setVendor((mainList.get(i).getVendor()));
						child.setDeviceFamily(mainList.get(i).getDeviceFamily());
						child.setDeviceModel(mainList.get(i).getDeviceModel());
						child.setOs(mainList.get(i).getOs());
						child.setOsVersion(mainList.get(i).getOsVersion());
						child.setRegion(mainList.get(i).getRegion());
						child.setCreatedOn(mainList.get(i).getCreatedOn());
						child.setCreatedBy(mainList.get(i).getCreatedBy());
						child.setComment(mainList.get(i).getComment());
						child.setNetworkType(mainList.get(i).getNetworkType());

						Boolean flag = mainList.get(i).isEnabled();
						child.setEnabled(flag);

						List<TestStrategeyVersioningJsonModel> childList = childListToIterate.get(k).getChildList();
						childList.add(child);

					}

				}
			}

		}
		return Response.status(200).header("Access-Control-Allow-Origin", "*")
				.header("Access-Control-Allow-Headers", "origin, content-type, accept, authorization")
				.header("Access-Control-Allow-Credentials", "true")
				.header("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS, HEAD")
				.header("Access-Control-Max-Age", "1209600").entity(modelList).build();

	}

	@SuppressWarnings({ "unchecked" })
	@POST
	@RequestMapping(value = "/getTestnamesAndVersionList", method = RequestMethod.POST, produces = "application/json", consumes = "application/json")
	public Response getTestnamesAndVersionList(@RequestBody String request) throws ParseException {

		JSONObject obj = new JSONObject();

		String testName = "", testNameUsed;
		String[] testNameFinal = null;

		JSONParser parser = new JSONParser();

		JSONObject json = (JSONObject) parser.parse(request);

		testName = (String) json.get("testid");
		testNameFinal = testName.split("_");
		testNameUsed = testNameFinal[0];

		List<TestDetail> mainList = new ArrayList<TestDetail>();
		List<TestDetail> mainList1 = new ArrayList<TestDetail>();
		List<TestDetail> mainList2 = new ArrayList<TestDetail>();

		RequestInfoDao dao = new RequestInfoDao();
		JSONArray array;

		mainList = dao.findByTestName(testNameUsed);

		String isCheck = null, secondCheck = null;
		int count = 0;
		JSONArray arrayElementOneArray = new JSONArray();

		for (int i = 0; i < mainList.size(); i++) {
			JSONObject arrayElementOneArrayElementTwo = new JSONObject();
			String[] testNameToSetArr = mainList.get(i).getTestName().split("_");

			obj.put("combination", testNameToSetArr[0]);

			if (testNameToSetArr.length >= 3) {
				isCheck = testNameToSetArr[1] + "_" + testNameToSetArr[2];
			} else {
				isCheck = testNameToSetArr[1];
			}
			if (isCheck.equals(secondCheck) || isCheck == null) {

				continue;

			}

			else if (count > 0) {

				JSONObject arrayElementOneArrayElementOne = new JSONObject();
				array = new JSONArray();
				arrayElementOneArrayElementOne.put("TestName", isCheck);

				mainList2 = dao.findByTestName(testNameUsed.concat("_" + isCheck));
				for (int i1 = 0; i1 < mainList2.size(); i1++) {
					array.add(mainList2.get(i1).getVersion());
				}
				arrayElementOneArrayElementOne.put("versions", array);

				secondCheck = isCheck;
				arrayElementOneArray.add(arrayElementOneArrayElementOne);
			} else {
				array = new JSONArray();
				arrayElementOneArrayElementTwo.put("TestName", isCheck);

				mainList1 = dao.findByTestName(testNameUsed.concat("_") + isCheck);
				for (int i1 = 0; i1 < mainList1.size(); i1++) {
					array.add(mainList1.get(i1).getVersion());
				}

				arrayElementOneArrayElementTwo.put("versions", array);
				secondCheck = isCheck;
				count++;
				arrayElementOneArray.add(arrayElementOneArrayElementTwo);
			}

		}
		obj.put("testNameList", arrayElementOneArray);

		return Response.status(200).header("Access-Control-Allow-Origin", "*")
				.header("Access-Control-Allow-Headers", "origin, content-type, accept, authorization")
				.header("Access-Control-Allow-Credentials", "true")
				.header("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS, HEAD")
				.header("Access-Control-Max-Age", "1209600").entity(obj).build();

	}

	@POST
	@RequestMapping(value = "/getSearchTestList", method = RequestMethod.POST, produces = "application/json", consumes = "application/json")
	public Response getSearchTestList(@RequestBody String request) throws ParseException {

		String key = "", value = "";

		JSONParser parser = new JSONParser();

		JSONObject json = (JSONObject) parser.parse(request);

		key = (String) json.get("key");
		value = (String) json.get("value");

		RequestInfoDao dao = new RequestInfoDao();

		List<TestDetail> mainList = new ArrayList<TestDetail>();

		if (value != null && !value.isEmpty()) {
			/*
			 * Search request based on Region, Vendor, Status, Model, Import Id and
			 * Management IP
			 */
			if (key.equalsIgnoreCase("Device Family")) {
				mainList = testDetailsRepository.findByDeviceFamily(value);

			} else if (key.equalsIgnoreCase("Vendor")) {
				mainList = testDetailsRepository.findByVendor(value);

			} else if (key.equalsIgnoreCase("Model")) {
				mainList = testDetailsRepository.findByDeviceModel(value);

			} else if (key.equalsIgnoreCase("OS")) {
				mainList = testDetailsRepository.findByOs(value);

			} else if (key.equalsIgnoreCase("Test Name")) {
				mainList = dao.findByTestNameForSearch(value);

			} else if (key.equalsIgnoreCase("OS Version")) {
				mainList = testDetailsRepository.findByOsVersion(value);

			}

		}

		int testNameSize = 0, testNameSize1 = 0;
		TestStrategeyVersioningJsonModel model = new TestStrategeyVersioningJsonModel();

		List<TestStrategeyVersioningJsonModel> modelList = new ArrayList<TestStrategeyVersioningJsonModel>();

		for (int i = 0; i < mainList.size(); i++) {
			if ((modelList.size() == 0)) {
				model = new TestStrategeyVersioningJsonModel();
				String[] testNameToSetArr = mainList.get(i).getTestName().split("_");

				model.setTestName(testNameToSetArr[0]);
				model.setVersion(mainList.get(i).getVersion());
				model.setFullTestName(mainList.get(i).getTestName());
				model.setTestId(mainList.get(i).getTestId());
				model.setVendor((mainList.get(i).getVendor()));
				model.setDeviceFamily(mainList.get(i).getDeviceFamily());
				model.setDeviceModel(mainList.get(i).getDeviceModel());
				model.setOs(mainList.get(i).getOs());
				model.setOsVersion(mainList.get(i).getOsVersion());
				model.setRegion(mainList.get(i).getRegion());
				model.setCreatedOn(mainList.get(i).getCreatedOn());
				model.setCreatedBy(mainList.get(i).getCreatedBy());
				model.setComment(mainList.get(i).getComment());
				model.setNetworkType(mainList.get(i).getNetworkType());

				TestStrategeyVersioningJsonModel child = new TestStrategeyVersioningJsonModel();
				List<TestStrategeyVersioningJsonModel> childList = new ArrayList<TestStrategeyVersioningJsonModel>();

				testNameSize = testNameToSetArr.length;

				if (testNameSize == 2) {
					child.setTestName(testNameToSetArr[1]);
				} else {
					child.setTestName(testNameToSetArr[2]);
				}
				child.setFullTestName(mainList.get(i).getTestName());
				child.setVersion(mainList.get(i).getVersion());
				child.setTestId(mainList.get(i).getTestId());
				child.setVendor((mainList.get(i).getVendor()));
				child.setDeviceFamily(mainList.get(i).getDeviceFamily());
				child.setDeviceModel(mainList.get(i).getDeviceModel());
				child.setOs(mainList.get(i).getOs());
				child.setOsVersion(mainList.get(i).getOsVersion());
				child.setRegion(mainList.get(i).getRegion());
				child.setCreatedOn(mainList.get(i).getCreatedOn());
				child.setCreatedBy(mainList.get(i).getCreatedBy());
				child.setComment(mainList.get(i).getComment());
				child.setNetworkType(mainList.get(i).getNetworkType());

				childList.add(child);
				model.setChildList(childList);
				modelList.add(model);
			} else {

				boolean flag = false;
				for (int j = 0; j < modelList.size(); j++) {
					String[] arrOfStrMain = mainList.get(i).getTestName().split("_");
					// String[] arrOfStrModel =
					// modelList.get(j).getTest_name().split("_");

					if (arrOfStrMain[0].equalsIgnoreCase(modelList.get(j).getTestName())) {
						flag = true;
						List<TestStrategeyVersioningJsonModel> childList = new ArrayList<TestStrategeyVersioningJsonModel>();
						TestStrategeyVersioningJsonModel child = new TestStrategeyVersioningJsonModel();

						testNameSize1 = arrOfStrMain.length;

						if (testNameSize1 == 2) {
							child.setTestName(arrOfStrMain[1]);
						} else {
							child.setTestName(arrOfStrMain[2]);
						}
						child.setVersion(mainList.get(i).getVersion());
						child.setFullTestName(mainList.get(i).getTestName());
						child.setVersion(mainList.get(i).getVersion());
						child.setTestId(mainList.get(i).getTestId());
						child.setVendor((mainList.get(i).getVendor()));
						child.setDeviceFamily(mainList.get(i).getDeviceFamily());
						child.setDeviceModel(mainList.get(i).getDeviceModel());
						child.setOs(mainList.get(i).getOs());
						child.setOsVersion(mainList.get(i).getOsVersion());
						child.setRegion(mainList.get(i).getRegion());
						child.setCreatedOn(mainList.get(i).getCreatedOn());
						child.setCreatedBy(mainList.get(i).getCreatedBy());
						child.setComment(mainList.get(i).getComment());
						child.setNetworkType(mainList.get(i).getNetworkType());

						childList = modelList.get(j).getChildList();
						if (childList.size() == 0) {
							childList.add(child);
						} else {
							boolean found = false;

							for (int k = 0; k < childList.size(); k++) {
								if (childList.get(k).getTestName().equalsIgnoreCase(arrOfStrMain[testNameSize1 - 1])) {
									found = true;
								}
							}

							if (!found) {
								childList.add(child);
							}
						}

						modelList.get(j).setChildList(childList);

					}
				}
				if (flag == false) {
					model = new TestStrategeyVersioningJsonModel();
					String[] testNameToSetArr = mainList.get(i).getTestName().split("_");

					model.setTestName(testNameToSetArr[0]);
					model.setVersion(mainList.get(i).getVersion());
					model.setFullTestName(mainList.get(i).getTestName());
					model.setTestId(mainList.get(i).getTestId());
					model.setVendor((mainList.get(i).getVendor()));
					model.setDeviceFamily(mainList.get(i).getDeviceFamily());
					model.setDeviceModel(mainList.get(i).getDeviceModel());
					model.setOs(mainList.get(i).getOs());
					model.setOsVersion(mainList.get(i).getOsVersion());
					model.setRegion(mainList.get(i).getRegion());
					model.setCreatedOn(mainList.get(i).getCreatedOn());
					model.setCreatedBy(mainList.get(i).getCreatedBy());
					model.setComment(mainList.get(i).getComment());
					model.setNetworkType(mainList.get(i).getNetworkType());

					TestStrategeyVersioningJsonModel child = new TestStrategeyVersioningJsonModel();
					List<TestStrategeyVersioningJsonModel> childList = new ArrayList<TestStrategeyVersioningJsonModel>();

					testNameSize = testNameToSetArr.length;

					if (testNameSize == 2) {
						child.setTestName(testNameToSetArr[1]);
					} else {
						child.setTestName(testNameToSetArr[2]);
					}
					child.setFullTestName(mainList.get(i).getTestName());
					child.setVersion(mainList.get(i).getVersion());
					child.setTestId(mainList.get(i).getTestId());
					child.setVendor((mainList.get(i).getVendor()));
					child.setDeviceFamily(mainList.get(i).getDeviceFamily());
					child.setDeviceModel(mainList.get(i).getDeviceModel());
					child.setOs(mainList.get(i).getOs());
					child.setOsVersion(mainList.get(i).getOsVersion());
					child.setRegion(mainList.get(i).getRegion());
					child.setCreatedOn(mainList.get(i).getCreatedOn());
					child.setCreatedBy(mainList.get(i).getCreatedBy());
					child.setComment(mainList.get(i).getComment());
					child.setNetworkType(mainList.get(i).getNetworkType());

					childList.add(child);
					model.setChildList(childList);
					modelList.add(model);
				}

			}

		}

		// /////////Loop end for two level

		// //This loop will iterate over all the children
		for (int i = 0; i < mainList.size(); i++) {

			String[] testNameToSetArr = mainList.get(i).getTestName().split("_");
			String testNameMain = testNameToSetArr[0] + testNameToSetArr[2];
			String testNameToSet = testNameToSetArr[1] + "_" + testNameToSetArr[2];

			// mainList1 =dao.findByTestName(testNameUsed);

			for (int j = 0; j < modelList.size(); j++) {
				List<TestStrategeyVersioningJsonModel> childListToIterate = new ArrayList<TestStrategeyVersioningJsonModel>();
				childListToIterate = modelList.get(j).getChildList();
				String[] testNameToSetArr1 = modelList.get(j).getFullTestName().split("_");
				String testNameChild = testNameToSetArr1[0] + testNameToSetArr1[2];

				for (int k = 0; k < childListToIterate.size(); k++) {
					if (mainList.get(i).getTestName().equalsIgnoreCase(childListToIterate.get(k).getFullTestName())) {
						TestStrategeyVersioningJsonModel child = new TestStrategeyVersioningJsonModel();
						child.setTestName(mainList.get(i).getVersion());
						child.setTestName(testNameToSet);
						child.setFullTestName(mainList.get(i).getTestName());
						child.setVersion(mainList.get(i).getVersion());
						child.setTestId(mainList.get(i).getTestId());
						child.setVendor((mainList.get(i).getVendor()));
						child.setDeviceFamily(mainList.get(i).getDeviceFamily());
						child.setDeviceModel(mainList.get(i).getDeviceModel());
						child.setOs(mainList.get(i).getOs());
						child.setOsVersion(mainList.get(i).getOsVersion());
						child.setRegion(mainList.get(i).getRegion());
						child.setCreatedOn(mainList.get(i).getCreatedOn());
						child.setCreatedBy(mainList.get(i).getCreatedBy());
						child.setComment(mainList.get(i).getComment());
						child.setNetworkType(mainList.get(i).getNetworkType());

						Boolean flag = mainList.get(i).isEnabled();
						child.setEnabled(flag);

						List<TestStrategeyVersioningJsonModel> childList = childListToIterate.get(k).getChildList();
						childList.add(child);
						break;

					} else if (testNameMain.equals(testNameChild)) {

						TestStrategeyVersioningJsonModel child = new TestStrategeyVersioningJsonModel();
						child.setTestName(mainList.get(i).getVersion());
						child.setTestName(testNameToSet);
						child.setFullTestName(mainList.get(i).getTestName());
						child.setVersion(mainList.get(i).getVersion());
						child.setTestId(mainList.get(i).getTestId());
						child.setVendor((mainList.get(i).getVendor()));
						child.setDeviceFamily(mainList.get(i).getDeviceFamily());
						child.setDeviceModel(mainList.get(i).getDeviceModel());
						child.setOs(mainList.get(i).getOs());
						child.setOsVersion(mainList.get(i).getOsVersion());
						child.setRegion(mainList.get(i).getRegion());
						child.setCreatedOn(mainList.get(i).getCreatedOn());
						child.setCreatedBy(mainList.get(i).getCreatedBy());
						child.setComment(mainList.get(i).getComment());
						child.setNetworkType(mainList.get(i).getNetworkType());

						Boolean flag = mainList.get(i).isEnabled();
						child.setEnabled(flag);

						List<TestStrategeyVersioningJsonModel> childList = childListToIterate.get(k).getChildList();
						childList.add(child);

					}

				}
			}

		}
		return Response.status(200).header("Access-Control-Allow-Origin", "*")
				.header("Access-Control-Allow-Headers", "origin, content-type, accept, authorization")
				.header("Access-Control-Allow-Credentials", "true")
				.header("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS, HEAD")
				.header("Access-Control-Max-Age", "1209600").entity(modelList).build();

	}

	@POST
	@RequestMapping(value = "/getDeviceInfoByHostName", method = RequestMethod.POST, produces = "application/json", consumes = "application/json")
	public Response getDeviceInfo(@RequestBody String request) throws ParseException {

		String key = "", value = "";

		JSONParser parser = new JSONParser();

		JSONObject json = (JSONObject) parser.parse(request);

		key = (String) json.get("key");
		value = (String) json.get("value");

		List<RequestInfoEntity> mainList = new ArrayList<RequestInfoEntity>();

		if (value != null && !value.isEmpty()) {
			/*
			 * Search request based on Region, Vendor, Status, Model, Import Id and
			 * Management IP
			 */
			if (key.equalsIgnoreCase("Hostname")) {
				mainList = requestInfoDetailsRepositories.findAllByHostName(value);
			}
		}
		return Response.status(200).header("Access-Control-Allow-Origin", "*")
				.header("Access-Control-Allow-Headers", "origin, content-type, accept, authorization")
				.header("Access-Control-Allow-Credentials", "true")
				.header("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS, HEAD")
				.header("Access-Control-Max-Age", "1209600").entity(mainList).build();
	}

	/* Dhanshri Mane */
	/*
	 * For new UI same logic and method only changes in JSON Formate
	 * (getTestsForDevice)
	 */

	@SuppressWarnings({ "null", "unchecked" })
	@POST
	@RequestMapping(value = "/newCertificationtestsfordevice", method = RequestMethod.POST, produces = "application/json", consumes = "application/json")
	public JSONArray getTestsForDeviceNewUi(@RequestBody String request) {
		JSONArray testDetailsValue = new JSONArray();
		List<TestDetail> testDetailsList = new ArrayList<TestDetail>();
		List<TestDetail> testDetailsListLatestVersion = new ArrayList<TestDetail>();
		List<TestDetail> testDetailsFinal = new ArrayList<TestDetail>();
		List<TestDetail> testDetailsListAllVersion = new ArrayList<TestDetail>();
		List<PredefineTestDetailEntity> masterTestDetails = new ArrayList<>();
		HashSet<String> testNameList = new HashSet<>();
		Set<String> testCategoryList = new HashSet<>();
		String response = null;

		String deviceFamily = null, deviceModel = null, vendor = null, os = null, osVersion = null, region = null,
				networkType = null, requestType = null;
		JSONParser parser = new JSONParser();
		JSONObject json;
		List<String> featuresFromUI = new ArrayList<String>();
		try {
			json = (JSONObject) parser.parse(request);

			if (json.containsKey("featureList")) {
				JSONArray fArray = (JSONArray) json.get("featureList");
				for (int i = 0; i < fArray.size(); i++) {
					JSONObject fArrayObj = (JSONObject) fArray.get(i);
					if ((boolean) fArrayObj.get("selected")) {
						featuresFromUI.add(fArrayObj.get("value").toString());
					}
				}
			}
			if (json.containsKey("deviceFamily")) {
				deviceFamily = json.get("deviceFamily").toString();
			}
			if (json.containsKey("vendor")) {
				vendor = json.get("vendor").toString();
			}
			if (json.containsKey("deviceModel")) {
				deviceModel = json.get("deviceModel").toString();
			}
			if (json.containsKey("os")) {
				os = json.get("os").toString();
			}
			if (json.containsKey("osVersion")) {
				osVersion = json.get("osVersion").toString();
			}
			if (json.containsKey("region")) {
				region = json.get("region").toString();
			}

			if (json.containsKey("networkType")) {
				networkType = json.get("networkType").toString();
			}
			if (json.containsKey("requestType")) {
				requestType = json.get("requestType").toString().toLowerCase();
			}
			String version = null;
			String testName = null;
					
			testDetailsList = testDetailsRepository
					.findByDeviceFamilyIgnoreCaseContainingAndDeviceModelIgnoreCaseContainingAndOsIgnoreCaseContainingAndOsVersionIgnoreCaseContainingAndVendorIgnoreCaseContainingAndRegionIgnoreCaseContainingAndNetworkType(
							deviceFamily, deviceModel, os, osVersion, vendor, region, networkType);
			String testCategory = null;
			for (int i = 0; i < testDetailsList.size(); i++) {
				switch (requestType) {
				case "config":
					testNameList.add(testDetailsList.get(i).getTestName());
					testCategoryList.add(testDetailsList.get(i).getTestCategory());
					break;
				case "test":
					testCategory = testDetailsList.get(i).getTestCategory();
					if (!testCategory.equals("Network Audit")) {
						testNameList.add(testDetailsList.get(i).getTestName());
						testCategoryList.add(testDetailsList.get(i).getTestCategory());
					}
					break;
				case "audit":
					testCategory = testDetailsList.get(i).getTestCategory();
					if (testCategory.equals("Network Audit")) {
						testNameList.add(testDetailsList.get(i).getTestName());
						testCategoryList.add(testDetailsList.get(i).getTestCategory());
					}
					break;

				default:
					break;
				}

			}

			Iterator<String> itrator = testNameList.iterator();
			while (itrator.hasNext()) {
				testName = itrator.next();
				testDetailsListAllVersion = testDetailsRepository
						.findByDeviceFamilyIgnoreCaseContainingAndDeviceModelIgnoreCaseContainingAndOsIgnoreCaseContainingAndOsVersionIgnoreCaseContainingAndVendorIgnoreCaseContainingAndRegionIgnoreCaseContainingAndTestNameIgnoreCaseContaining(
								deviceFamily, deviceModel, os, osVersion, vendor, region, testName);

				for (int i = 0; i < testDetailsListAllVersion.size(); i++) {

					if (testName.equals(testDetailsListAllVersion.get(i).getTestName())) {
						version = testDetailsListAllVersion.get(i).getVersion();
					}
				}

				testDetailsListLatestVersion = testDetailsRepository
						.findByDeviceFamilyAndDeviceModelAndOsAndOsVersionAndVendorAndRegionAndVersionAndTestName(
								deviceFamily, deviceModel, os, osVersion, vendor, region, version, testName);

				if (null != testDetailsListLatestVersion || !testDetailsListLatestVersion.isEmpty()) {

					int n = testDetailsListLatestVersion.size();

					List<TestDetail> aList = new ArrayList<TestDetail>(n);
					for (TestDetail x : testDetailsListLatestVersion) {

						aList.add(x);
					}
					// Logic to set disabled and selected bit in the test detail
					// array to be sent to ui based on features selected

					if (featuresFromUI != null && featuresFromUI.size() > 0) {
						for (int i = 0; i < aList.size(); i++) {
							List<TestFeatureList> dbFeatures = testFeatureListRepository.findByTestDetail(aList.get(i));

							for (int j = 0; j < dbFeatures.size(); j++) {
								if (featuresFromUI.contains(dbFeatures.get(j).getTestFeature())) {
									aList.get(i).setSelected(true);
									aList.get(i).setDisabled(false);
								}
							}
							logger.info("");
						}
					} else {
						for (int i = 0; i < aList.size(); i++) {

							aList.get(i).setSelected(false);
							aList.get(i).setDisabled(false);

							logger.info("");
						}
					}	
					testDetailsFinal.addAll(aList);
				}

				else {
					JSONObject testDetails= new JSONObject();
					testDetails.put("msg", "Record not found");
					testDetailsValue.add(testDetails);
					return testDetailsValue;
				}
				/*predefineTestDetailsRepository.findAll().forEach(predefineTest->{
					TestDetail testObject = new TestDetail();
					testObject.setTestName(predefineTest.getTestName());
					testObject.setTestCategory(predefineTest.getTestCategory());
					testObject.setVersion(String.valueOf(predefineTest.getVersion()));
					testObject.setId(predefineTest.getId());
					testDetailsFinal.add(testObject);
					testCategoryList.add(predefineTest.getTestCategory());					
				});*/
			}
			testCategoryList.forEach(category -> {
				JSONArray testDetailsArray = new JSONArray();
				testDetailsFinal.stream().filter(it -> category.equals(it.getTestCategory())).forEach(testInfo -> {
					JSONObject testObject = new JSONObject();
					testObject.put("testName", testInfo.getTestName()+"_"+testInfo.getVersion());
					testObject.put("testCategory", testInfo.getTestCategory());
					testObject.put("testId", testInfo.getTestId());
					testObject.put("isBundle", false);
					testDetailsArray.add(testObject);
				});
				JSONObject testObject2 = new JSONObject();
				testObject2.put("name", category);
				testObject2.put("tests", testDetailsArray);
				testDetailsValue.add(testObject2);
			});
			String requestTypevalue = requestType;

			testBundleRepo.findByVendorAndDeviceFamilyAndDeviceModelAndOsAndOsVersionAndNetworkFunctionAndRegion(
					vendor, deviceFamily, deviceModel, os, osVersion, networkType, region).forEach(bundle -> {
						JSONArray testDetailsArray = new JSONArray();
						boolean auditFlag = false;
						boolean testOnly = false;

						Set<TestDetail> testDetail = bundle.getTestDetails();
						if ("test".equals(requestTypevalue)) {
							testOnly = testDetail.stream()
									.anyMatch(testInfo -> !testInfo.getTestCategory().equals("Network Audit"));
						} else if ("audit".equals(requestTypevalue)) {
							auditFlag = testDetail.stream()
									.anyMatch(testInfo -> testInfo.getTestCategory().equals("Network Audit"));
						}
						if((auditFlag && "audit".equals(requestTypevalue)) || (testOnly && "test".equals(requestTypevalue)) || (!auditFlag && !testOnly && ("config".equals(requestTypevalue)))){
						for (TestDetail testInfo : testDetail) {								
							JSONObject testObject = new JSONObject();
							testObject.put("testCategory", testInfo.getTestCategory());			
							testObject.put("testName", testInfo.getTestName()+"_"+testInfo.getVersion());
							testObject.put("testId", testInfo.getTestId());
							testObject.put("isBundle", true);
							testDetailsArray.add(testObject);
						}
						JSONObject testObject2 = new JSONObject();
						testObject2.put("name", bundle.getTestBundle());
						testObject2.put("tests", testDetailsArray);
						testDetailsValue.add(testObject2);
						}
					});					

			logger.info(testDetailsValue);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			JSONObject testDetails= new JSONObject();
			testDetails.put("msg", "Unable read GUI input");
			testDetailsValue.add(testDetails);
			return testDetailsValue;
		}		
		return testDetailsValue;
	}

	@SuppressWarnings("unchecked")
	@POST
	@RequestMapping(value = "/getAllVersion", method = { RequestMethod.POST,
			RequestMethod.PUT }, produces = "application/json", consumes = "application/json")
	public Response getAllVersion(@RequestBody String request) throws ParseException {
		JSONObject obj = new JSONObject();
		JSONArray arrayElementOneArray = new JSONArray();
		HashSet<String> set = new HashSet<>();
		List<FirmwareUpgradeDetail> mainList = new ArrayList<FirmwareUpgradeDetail>();
		List<FirmwareUpgradeDetail> mainList1 = new ArrayList<FirmwareUpgradeDetail>();
		String vendorName = "", isVendor = null, secondCheck = null, isFamily = null, isVersion = null;
		RequestInfoDao dao = new RequestInfoDao();

		JSONParser parser = new JSONParser();

		JSONObject json = (JSONObject) parser.parse(request);

		vendorName = (String) json.get("vendor");

		mainList = dao.findByVendorName(vendorName);

		for (int i = 0; i < mainList.size(); i++) {

			isVendor = mainList.get(i).getVendor();
			isFamily = mainList.get(i).getFamily();
			if (secondCheck != null) {
				set.add(secondCheck);
			}
			obj.put("Vendor", isVendor);

			if (isFamily.equals(secondCheck)) {

				continue;

			}
			if (set.contains(isFamily)) {
				continue;
			}

			else {

				JSONObject arrayElementOneArrayElementOne = new JSONObject();

				arrayElementOneArrayElementOne.put("Family", isFamily);

				mainList1 = dao.findByFamily(isFamily, isVendor);
				for (int i2 = 0; i2 < mainList1.size(); i2++) {
					isVersion = mainList1.get(i2).getOs_version();
					arrayElementOneArrayElementOne.put(i2, isVersion);
				}
				secondCheck = isFamily;

				arrayElementOneArray.add(arrayElementOneArrayElementOne);

			}
		}

		obj.put("VendorList", arrayElementOneArray);
		return Response.status(200).header("Access-Control-Allow-Origin", "*")
				.header("Access-Control-Allow-Headers", "origin, content-type, accept, authorization")
				.header("Access-Control-Allow-Credentials", "true")
				.header("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS, HEAD")
				.header("Access-Control-Max-Age", "1209600").entity(obj).build();

	}

	@SuppressWarnings("unchecked")
	@POST
	@RequestMapping(value = "/searchAllRequest", method = { RequestMethod.POST,
			RequestMethod.PUT }, produces = "application/json", consumes = "application/json")
	public Response searchAllRequest(@RequestBody String request) throws ParseException {
		JSONObject obj = new JSONObject();
		DeviceDiscoveryEntity entity = new DeviceDiscoveryEntity();
		SiteInfoEntity entity1 = new SiteInfoEntity();
		List<DeviceDiscoveryEntity> mainList = new ArrayList<DeviceDiscoveryEntity>();
		List<DeviceDiscoveryEntity> mainList2 = new ArrayList<DeviceDiscoveryEntity>();
		List<SiteInfoEntity> mainList1 = new ArrayList<SiteInfoEntity>();
		String vendorName = null;

		JSONParser parser = new JSONParser();

		JSONObject json = (JSONObject) parser.parse(request);

		vendorName = (String) json.get("vendor");

		mainList = deviceDiscoveryRepository.findAllByDVendor(vendorName);

		for (int i = 0; i < mainList.size(); i++) {
			entity.setdHostName(mainList.get(i).getdHostName());
			entity.setdMgmtIp(mainList.get(i).getdMgmtIp());
			entity.setdOs(mainList.get(i).getdOs());
			entity.setdContact(mainList.get(i).getdContact());
			entity1.setcCustName(mainList1.get(i).getcCustName());
			entity.setdModel(mainList.get(i).getdModel());
			entity.setdType(mainList.get(i).getdType());
			entity.setCustSiteId(entity1);
			mainList2.add(entity);
		}

		obj.put("Firmware Upgrade", mainList2);
		return Response.status(200).header("Access-Control-Allow-Origin", "*")
				.header("Access-Control-Allow-Headers", "origin, content-type, accept, authorization")
				.header("Access-Control-Allow-Credentials", "true")
				.header("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS, HEAD")
				.header("Access-Control-Max-Age", "1209600").entity(obj).build();

	}

	@SuppressWarnings({ "null", "unchecked", "unused" })
	@POST
	@RequestMapping(value = "/testListForBatch", method = RequestMethod.POST, produces = "application/json", consumes = "application/json")
	public Response getTestsForBatch(@RequestBody String request) {

		List<TestDetail> testDetailsList = new ArrayList<TestDetail>();
		List<TestDetail> testDetailsListLatestVersion = new ArrayList<TestDetail>();
		List<TestDetail> testDetailsFinal = new ArrayList<TestDetail>();
		List<TestDetail> testDetailsListAllVersion = new ArrayList<TestDetail>();
		List<PredefineTestDetailEntity> masterTestDetails = new ArrayList<>();

		HashSet<String> testNameList = new HashSet<>();
		String response = null;

		String deviceModel = null, vendor = null, deviceFamily = null, version = null, region = null, testName = null,
				networkType = null, requestType = null;

		List<String> featuresFromUI = new ArrayList<String>();
		HashMap<String, String> uiInput = new HashMap<>();
		Gson gson = new Gson();

		BatchPojo[] userArray = gson.fromJson(request, BatchPojo[].class);

		for (int j = 0; j < userArray.length; j++) {

			uiInput.put(userArray[j].getKey(), userArray[j].getValue());

		}

		if (uiInput.containsKey("vendor")) {
			vendor = uiInput.get("vendor").toString();
		}
		if (uiInput.containsKey("model")) {
			deviceModel = uiInput.get("model").toString();
		}
		if (uiInput.containsKey("deviceFamily")) {
			deviceFamily = uiInput.get("deviceFamily").toString();
		}

		if (uiInput.containsKey("region")) {
			region = uiInput.get("region").toString();
		}

		if (uiInput.containsKey("networkFunction")) {
			networkType = uiInput.get("networkFunction").toString();
		}
		if (uiInput.containsKey("requestType")) {
			requestType = uiInput.get("requestType").toString();
		}

		masterTestDetails = predefineTestDetailsRepository.findAll();

		if (deviceModel.equals("")) {

			testDetailsList = testDetailsRepository.findBySelectionWithoutModel(region, vendor, networkType);
		} else {
			testDetailsList = testDetailsRepository.findBySelection(region, vendor, networkType, deviceModel);
		}

		String testCategory = null;
		for (int i = 0; i < testDetailsList.size(); i++) {
			switch (requestType) {
			case "config":
				testNameList.add(testDetailsList.get(i).getTestName());
				break;
			case "Test":
				testCategory = testDetailsList.get(i).getTestCategory();
				if (!testCategory.equals("Network Audit")) {
					testNameList.add(testDetailsList.get(i).getTestName());
				}
				break;
			case "Network Audit":
				testCategory = testDetailsList.get(i).getTestCategory();
				if (testCategory.equals("Network Audit")) {
					testNameList.add(testDetailsList.get(i).getTestName());
				}
				break;

			default:
				break;
			}

		}

		Iterator<String> itrator = testNameList.iterator();
		while (itrator.hasNext()) {
			testName = itrator.next();
			testDetailsListAllVersion = testDetailsRepository
					.findByRegionIgnoreCaseContainingAndVendorIgnoreCaseContainingAndNetworkTypeAndTestNameIgnoreCaseContaining(
							region, vendor, networkType, testName);

			for (int i = 0; i < testDetailsListAllVersion.size(); i++) {

				if (testName.equals(testDetailsListAllVersion.get(i).getTestName())) {
					version = testDetailsListAllVersion.get(i).getVersion();
				}
			}

			testDetailsListLatestVersion = testDetailsRepository
					.findByRegionIgnoreCaseContainingAndVendorIgnoreCaseContainingAndNetworkTypeAndTestNameIgnoreCaseContainingAndVersion(
							region, vendor, networkType, testName, version);

			if (null != testDetailsListLatestVersion || !testDetailsListLatestVersion.isEmpty()) {

				int n = testDetailsListLatestVersion.size();

				List<TestDetail> aList = new ArrayList<TestDetail>(n);
				for (TestDetail x : testDetailsListLatestVersion) {

					aList.add(x);
				}
				// Logic to set disabled and selected bit in the test detail
				// array to be sent to ui based on features selected

				if (featuresFromUI != null && featuresFromUI.size() > 0) {
					for (int i = 0; i < aList.size(); i++) {
						List<TestFeatureList> dbFeatures = testFeatureListRepository.findByTestDetail(aList.get(i));

						for (int j = 0; j < dbFeatures.size(); j++) {
							if (featuresFromUI.contains(dbFeatures.get(j).getTestFeature())) {
								aList.get(i).setSelected(true);
								aList.get(i).setDisabled(false);
							}
						}

					}
				} else {
					for (int i = 0; i < aList.size(); i++) {

						aList.get(i).setSelected(false);
						aList.get(i).setDisabled(false);

					}
				}

				testDetailsFinal.addAll(aList);
			}

			else {
				response = "Records not found";
				return Response.status(200).entity(response).build();
			}
		}
		JSONObject testDetails = new JSONObject();
		testDetails.put("default", masterTestDetails);
		testDetails.put("dynamic", testDetailsFinal);

		return Response.status(200).entity(testDetails).build();
	}

}