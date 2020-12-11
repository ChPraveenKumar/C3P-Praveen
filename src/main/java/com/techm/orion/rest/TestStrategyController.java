package com.techm.orion.rest;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
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
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.techm.orion.dao.RequestInfoDao;
import com.techm.orion.entitybeans.DeviceDiscoveryEntity;
import com.techm.orion.entitybeans.RequestInfoEntity;
import com.techm.orion.entitybeans.SiteInfoEntity;
import com.techm.orion.entitybeans.TestBundling;
import com.techm.orion.entitybeans.TestDetail;
import com.techm.orion.entitybeans.TestFeatureList;
import com.techm.orion.entitybeans.TestRules;
import com.techm.orion.entitybeans.TestStrategeyVersioningJsonModel;
import com.techm.orion.pojo.FirmwareUpgradeDetail;
import com.techm.orion.pojo.TestStrategyPojo;
import com.techm.orion.repositories.DeviceDiscoveryRepository;
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
	private RequestInfoDetailsRepositories requestInfoDetailsRepositories;

	@Autowired
	private DeviceDiscoveryRepository deviceDiscoveryRepository;

	@Autowired
	private TestBundlingRepository testBundleRepo;

	RequestInfoDao dao = new RequestInfoDao();

	@GET
	@RequestMapping(value = "/testfeatureList", method = RequestMethod.GET, produces = "application/json")
	public Response getOsversions() {
		return Response.status(200).entity(testFeatureListRepository.findAll()).build();
	}

	@SuppressWarnings("unused")
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
					.findByDeviceFamilyIgnoreCaseContainingAndOsIgnoreCaseContainingAndOsVersionIgnoreCaseContainingAndVendorIgnoreCaseContainingAndRegionIgnoreCaseContainingAndNetworkType(
							deviceType, os, osVersion, vendor, region, networkType);

			for (int i = 0; i < testDetailsList.size(); i++) {

				testNameList.add(testDetailsList.get(i).getTestName());

			}

			Iterator<String> itrator = testNameList.iterator();
			while (itrator.hasNext()) {
				testName = itrator.next();
				testDetailsListAllVersion = testDetailsRepository
						.findByDeviceFamilyIgnoreCaseContainingAndOsIgnoreCaseContainingAndOsVersionIgnoreCaseContainingAndVendorIgnoreCaseContainingAndRegionIgnoreCaseContainingAndTestNameIgnoreCaseContaining(
								deviceType, os, osVersion, vendor, region, testName);

				for (int i = 0; i < testDetailsListAllVersion.size(); i++) {

					if (testName.equals(testDetailsListAllVersion.get(i).getTestName())) {
						version = testDetailsListAllVersion.get(i).getVersion();
					}
				}

				testDetailsListLatestVersion = testDetailsRepository
						.findByDeviceFamilyAndOsAndOsVersionAndVendorAndRegionAndVersionAndTestName(deviceType, os,
								osVersion, vendor, region, version, testName);

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

	@SuppressWarnings({ "unused", "unchecked", "rawtypes" })
	@GET
	@RequestMapping(value = "/getTestDetails", method = RequestMethod.GET, produces = "application/json")
	public ResponseEntity getSeriess(@RequestParam String testid, String version) {

		int testDetailsId = 0;
		List<Integer> findBundleIds = new ArrayList<>();
		List<String> bundleNameList = new ArrayList<>();
		String testBundling = null;

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

			testDetailsId = testdetaillist.get(0).getId();

			findBundleIds = dao.findBundleId(testDetailsId);

			for (Integer tempObj : findBundleIds) {
				testBundling = testBundleRepo.findByBundleName(tempObj.intValue());
				bundleNameList.add(testBundling);
			}
			detail.setBundleName(bundleNameList);
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
			return new ResponseEntity(detail, HttpStatus.OK);
		} else {

			return new ResponseEntity("Unable to fetch data for the test.", HttpStatus.OK);

		}

	}

	@GET
	@RequestMapping(value = "/getalltests", method = RequestMethod.GET, produces = "application/json")
	public Response getOs() {
		return Response.status(200).entity(testDetailsRepository.findAll()).build();
	}

	@SuppressWarnings("unused")
	@POST
	@RequestMapping(value = "/savetestdetails", method = RequestMethod.POST, produces = "application/json", consumes = "application/json")
	public Response saveBasicConfiguration(@RequestBody String teststrategesaveRqst) {

		String str = "";

		int bundleId = 0;

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
			List<TestBundling> bundleList = new ArrayList<TestBundling>();

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

			if (json.containsKey("bundle_attributes")) {

				JSONArray attribarray = (JSONArray) json.get("bundle_attributes");
				for (int i = 0; i < attribarray.size(); i++) {
					TestBundling bundleDetail = new TestBundling();

					JSONObject attribobj = (JSONObject) attribarray.get(i);

					if (attribobj.containsKey("id")) {

						bundleId = Integer.parseInt(attribobj.get("id").toString());
						bundleDetail.setId(bundleId);
					}

					bundleList.add(bundleDetail);
				}

			}

			Set<TestRules> setrules = new HashSet<TestRules>(rulelst);
			Set<TestBundling> hash_Set = new HashSet<TestBundling>(bundleList);
			List<TestDetail> savetest1 = new ArrayList<TestDetail>();
			List<String> testNameCheck = new ArrayList<>();
			testDetail.setTestrules(setrules);
			testDetail.setTestbundling(hash_Set);
			try {
				savetest1 = testDetailsRepository.findAll();

				if (!(savetest1.isEmpty())) {
					for (int i = 0; i < savetest1.size(); i++) {
						String testName = savetest1.get(i).getTestName();
						testNameCheck.add(testName);
					}
				}
				if (!(testNameCheck.contains(testDetail.getTestName()))) {
					TestDetail save = testDetailsRepository.save(testDetail);

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

	@SuppressWarnings("unused")
	@POST
	@RequestMapping(value = "/edittestdetails", method = RequestMethod.POST, produces = "application/json", consumes = "application/json")
	public Response editBasicConfiguration(@RequestBody String teststrategeeditRqst) {

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

			for (TestDetail tempObj : testNameForVersion) {
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

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@POST
	@RequestMapping(value = "/getTestList", method = RequestMethod.GET, produces = "application/json")
	@ResponseBody
	public ResponseEntity getTestList() {

		// Create first level

		List<TestStrategyPojo> mainList = new ArrayList<TestStrategyPojo>();
		mainList = dao.getAllTestsForTestStrategy();

		TestStrategeyVersioningJsonModel model = new TestStrategeyVersioningJsonModel();
		List<TestStrategeyVersioningJsonModel> versioningModel = new ArrayList<TestStrategeyVersioningJsonModel>();
		TestStrategyPojo objToAdd = null;
		List<String> tempStringList = new ArrayList<>();
		String tempString = null, tempString1 = null, tempString2 = null, tempString3 = null, tempString4 = null,
				tempString5 = null, tempString6 = null, tempString7 = null;
		int testCount = 0;
		List<TestStrategyPojo> modelList = new ArrayList<TestStrategyPojo>();
		try {

			for (int i = 0; i < mainList.size(); i++) {
				tempString = mainList.get(i).getTestName();
				tempString1 = StringUtils.substringBefore(tempString, "_");

				boolean objectPrsent = false;
				if (modelList.size() > 0) {
					for (int j = 0; j < modelList.size(); j++) {
						tempString2 = modelList.get(j).getTestName();
						tempString3 = StringUtils.substringBefore(tempString2, "_");
						tempString4 = mainList.get(i).getTestName();
						tempString5 = StringUtils.substringBefore(tempString4, "_");

						if (tempString3.equalsIgnoreCase(tempString5)) {
							objectPrsent = true;
							break;
						}
					}
				}
				if (!objectPrsent && !tempStringList.contains(tempString1)) {
					model = new TestStrategeyVersioningJsonModel();
					objToAdd = new TestStrategyPojo();
					objToAdd = mainList.get(i);
					testCount = mainList.size();
					model.setTestId(objToAdd.getTestId());
					model.setTestCount(testCount);
					tempString6 = objToAdd.getTestName();
					tempString7 = StringUtils.substringBefore(tempString6, "_");
					tempStringList.add(tempString7);
					model.setTestName(tempString7);
					model.setName(tempString7);
					model.setFullTestName(StringUtils.substringAfter(objToAdd.getTestName().substring(15), "_"));
					model.setVendor(objToAdd.getVendor());
					model.setRegion(objToAdd.getRegion());
					model.setDeviceModel(objToAdd.getDeviceModel());
					model.setTest_category(objToAdd.getTest_category());
					model.setDeviceFamily(objToAdd.getDeviceFamily());
					model.setOs(objToAdd.getOs());
					model.setOsVersion(objToAdd.getOsVersion());
					model.setCreatedBy(objToAdd.getCreatedBy());
					model.setCreatedOn(objToAdd.getCreatedDate());

					model.setCreatedBy(objToAdd.getCreatedBy());

					modelList = new ArrayList<TestStrategyPojo>();
					for (TestStrategyPojo tempObj : mainList) {

						String s11 = tempObj.getTestName();
						String seriesId1 = StringUtils.substringBefore(s11, "_");
						if (seriesId1.equalsIgnoreCase(model.getTestName())) {
							modelList.add(tempObj);
						}
					}
					Collections.reverse(modelList);

					modelList.get(0).setEnabled(true);
					model.setTestStrategyPojoList(modelList);
					versioningModel.add(model);

				}

			}
		} catch (Exception exe) {
			logger.error("Exception occurred while fetching the data object" + exe.getMessage());
		}
		return new ResponseEntity(versioningModel, HttpStatus.OK);

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

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@POST
	@RequestMapping(value = "/getSearchTestList", method = RequestMethod.POST, produces = "application/json", consumes = "application/json")
	public ResponseEntity getSearchTestList(@RequestBody String request) throws ParseException {

		String key = "", value = "";

		JSONParser parser = new JSONParser();

		List<TestStrategyPojo> mainList = new ArrayList<TestStrategyPojo>();

		TestStrategeyVersioningJsonModel model = new TestStrategeyVersioningJsonModel();
		List<TestStrategeyVersioningJsonModel> versioningModel = new ArrayList<TestStrategeyVersioningJsonModel>();
		TestStrategyPojo objToAdd = null;

		List<TestStrategyPojo> modelList = new ArrayList<TestStrategyPojo>();
		List<String> tempStringList = new ArrayList<>();
		String tempString = null, tempString1 = null, tempString2 = null, tempString3 = null, tempString4 = null,
				tempString5 = null, tempString6 = null, tempString7 = null;

		JSONObject json = (JSONObject) parser.parse(request);

		key = (String) json.get("key");
		value = (String) json.get("value");

		if (value != null && !value.isEmpty()) {

			if (key.equalsIgnoreCase("Device Family")) {
				mainList = dao.findByForSearch(key, value);

			} else if (key.equalsIgnoreCase("Vendor")) {
				mainList = dao.findByForSearch(key, value);

			} else if (key.equalsIgnoreCase("OS")) {
				mainList = dao.findByForSearch(key, value);

			} else if (key.equalsIgnoreCase("Test Name")) {
				mainList = dao.findByForSearch(key, value);

			} else if (key.equalsIgnoreCase("OS Version")) {
				mainList = dao.findByForSearch(key, value);

			} else if (key.equalsIgnoreCase("Region")) {
				mainList = dao.findByForSearch(key, value);

			}

		}

		for (int i = 0; i < mainList.size(); i++) {
			tempString = mainList.get(i).getTestName();
			tempString1 = StringUtils.substringBefore(tempString, "_");

			boolean objectPrsent = false;
			if (modelList.size() > 0) {
				for (int j = 0; j < modelList.size(); j++) {
					tempString2 = modelList.get(j).getTestName();
					tempString3 = StringUtils.substringBefore(tempString2, "_");
					tempString4 = mainList.get(i).getTestName();
					tempString5 = StringUtils.substringBefore(tempString4, "_");

					if (tempString3.equalsIgnoreCase(tempString5)) {
						objectPrsent = true;
						break;
					}
				}
			}
			if (!objectPrsent && !tempStringList.contains(tempString1)) {
				model = new TestStrategeyVersioningJsonModel();
				objToAdd = new TestStrategyPojo();
				objToAdd = mainList.get(i);

				model.setTestId(objToAdd.getTestId());
				tempString6 = objToAdd.getTestName();
				tempString7 = StringUtils.substringBefore(tempString6, "_");
				tempStringList.add(tempString7);
				model.setTestName(tempString7);
				model.setName(tempString7);
				model.setFullTestName(StringUtils.substringAfter(objToAdd.getTestName().substring(15), "_"));
				model.setVendor(objToAdd.getVendor());
				model.setRegion(objToAdd.getRegion());
				model.setDeviceModel(objToAdd.getDeviceModel());
				model.setTest_category(objToAdd.getTest_category());
				model.setDeviceFamily(objToAdd.getDeviceFamily());
				model.setOs(objToAdd.getOs());
				model.setOsVersion(objToAdd.getOsVersion());
				model.setCreatedBy(objToAdd.getCreatedBy());
				model.setCreatedOn(objToAdd.getCreatedDate());

				model.setCreatedBy(objToAdd.getCreatedBy());

				modelList = new ArrayList<TestStrategyPojo>();
				for (TestStrategyPojo tempObj : mainList) {

					String s11 = tempObj.getTestName();
					String seriesId1 = StringUtils.substringBefore(s11, "_");
					if (seriesId1.equalsIgnoreCase(model.getTestName())) {
						modelList.add(tempObj);
					}
				}
				Collections.reverse(modelList);

				modelList.get(0).setEnabled(true);
				model.setTestStrategyPojoList(modelList);
				versioningModel.add(model);
			}

		}

		return new ResponseEntity(versioningModel, HttpStatus.OK);

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

	@SuppressWarnings({ "unchecked" })
	@POST
	@RequestMapping(value = "/newCertificationtestsfordevice", method = RequestMethod.POST, produces = "application/json", consumes = "application/json")
	public JSONArray getTestsForDeviceNewUi(@RequestBody String request) {
		JSONArray testDetailsValue = new JSONArray();
		List<TestDetail> testDetailsList = new ArrayList<TestDetail>();
		String deviceFamily = null, vendor = null, os = null, osVersion = null, region = null, networkType = null,
				requestType = null;
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
			if ("All".equals(region)) {
				region = "%";
			} else {
				region = "%" + region+"%";
			}
			if ("All".equals(osVersion)) {
				osVersion = "%";
			} else {
				osVersion = "%" + osVersion+"%";
			}
			if ("All".equals(os)) {
				os = "%";
			} else {
				os = "%" + os+"%";
			}
			if ("All".equals(deviceFamily)) {
				deviceFamily = "%";
			} else {
				deviceFamily = "%" + deviceFamily+"%";
			}
			testDetailsList = testDetailsRepository.getTesListData(deviceFamily, os, region, osVersion, vendor,
					networkType);
			testDetailsValue = getTestData(deviceFamily, os, region, osVersion, vendor, networkType,requestType,testDetailsValue,testDetailsList);
			List<TestBundling> testBundleData = testBundleRepo.getTestBundleData(deviceFamily, os, region, osVersion, vendor, networkType);
			testDetailsValue = getBundleData(deviceFamily, os, region, osVersion, vendor, networkType,requestType,testDetailsValue,testBundleData);
			logger.info(testDetailsValue);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			JSONObject testDetails = new JSONObject();
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

	@SuppressWarnings("unchecked")
	@POST
	@RequestMapping(value = "/testListForBatch", method = RequestMethod.POST, produces = "application/json", consumes = "application/json")
	public JSONArray getTestsForBatch(@RequestBody String request) {
		JSONArray testDetailsValue = new JSONArray();

		String deviceFamily = null, vendor = null, os = null, osVersion = null, region = null, networkType = null,
				requestType = null;
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
			List<TestDetail> testDetailsList = testDetailsRepository.findByDeviceFamilyAndOsAndOsVersionAndVendorAndRegionAndNetworkType(deviceFamily, os,osVersion,vendor,region,
					networkType);
			testDetailsValue = getTestData(deviceFamily, os, region, osVersion, vendor, networkType,requestType,testDetailsValue,testDetailsList);
			List<TestBundling> testBundleData = testBundleRepo.getTestBundleData(deviceFamily, os, region, osVersion, vendor, networkType);
			testDetailsValue = getBundleData(deviceFamily, os, region, osVersion, vendor, networkType,requestType,testDetailsValue,testBundleData);

			logger.info(testDetailsValue);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			JSONObject testDetails = new JSONObject();
			testDetails.put("msg", "Unable read GUI input");
			testDetailsValue.add(testDetails);
			return testDetailsValue;
		}
		return testDetailsValue;
	}

	@SuppressWarnings("unchecked")
	private JSONArray getTestData(String deviceFamily, String os, String region, String osVersion, String vendor, String networkType, String requestType, JSONArray testDetailsValue, List<TestDetail> testDetailsList) {
		List<TestDetail> testDetailsFinal = new ArrayList<TestDetail>();
		HashSet<String> testNameList = new HashSet<>();
		Set<String> testCategoryList = new HashSet<>();
		
		String testCategory = null;
		for (int i = 0; i < testDetailsList.size(); i++) {
			switch (requestType) {
			case "config":
				testNameList.add(testDetailsList.get(i).getTestName());
				testCategoryList.add(testDetailsList.get(i).getTestCategory());
				testDetailsFinal.add(testDetailsList.get(i));
				break;
			case "test":
				testCategory = testDetailsList.get(i).getTestCategory();
				if (!testCategory.equals("Network Audit")) {
					testNameList.add(testDetailsList.get(i).getTestName());
					testCategoryList.add(testDetailsList.get(i).getTestCategory());
					testDetailsFinal.add(testDetailsList.get(i));
				}
				break;
			case "audit":
				testCategory = testDetailsList.get(i).getTestCategory();
				if (testCategory.equals("Network Audit")) {
					testNameList.add(testDetailsList.get(i).getTestName());
					testCategoryList.add(testDetailsList.get(i).getTestCategory());
					testDetailsFinal.add(testDetailsList.get(i));
				}
				break;
			default:
				break;
			}
		}
		testCategoryList.forEach(category -> {
			JSONArray testDetailsArray = new JSONArray();
			testDetailsFinal.stream().filter(it -> category.equals(it.getTestCategory())).forEach(testInfo -> {
				JSONObject testObject = new JSONObject();
				testObject.put("testName", testInfo.getTestName() + "_" + testInfo.getVersion());
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
		return testDetailsValue;
		
	}

	@SuppressWarnings("unchecked")
	private JSONArray getBundleData(String deviceFamily, String os, String region, String osVersion, String vendor, String networkType, String requestType, JSONArray testDetailsValue, List<TestBundling> testBundleData) {
		
		testBundleData.forEach(bundle -> {
			JSONArray testDetailsArray = new JSONArray();
			boolean auditFlag = false;
			boolean testOnly = false;

			Set<TestDetail> testDetail = bundle.getTestDetails();
			if ("test".equals(requestType)) {
				testOnly = testDetail.stream()
						.anyMatch(testInfo -> !testInfo.getTestCategory().equals("Network Audit"));
			} else if ("audit".equals(requestType)) {
				auditFlag = testDetail.stream()
						.anyMatch(testInfo -> testInfo.getTestCategory().equals("Network Audit"));
			}
			if ((auditFlag && "audit".equals(requestType))
					|| (testOnly && "test".equals(requestType))
					|| (!auditFlag && !testOnly && ("config".equals(requestType)))) {
				for (TestDetail testInfo : testDetail) {
					JSONObject testObject = new JSONObject();
					testObject.put("testCategory", testInfo.getTestCategory());
					testObject.put("testName", testInfo.getTestName() + "_" + testInfo.getVersion());
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
		return testDetailsValue;
	}

}