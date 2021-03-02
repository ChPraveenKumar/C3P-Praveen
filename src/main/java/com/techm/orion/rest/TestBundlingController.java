package com.techm.orion.rest;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.ws.rs.POST;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.techm.orion.dao.RequestInfoDao;
import com.techm.orion.entitybeans.DeviceFamily;
import com.techm.orion.entitybeans.OS;
import com.techm.orion.entitybeans.OSversion;
import com.techm.orion.entitybeans.TestBundling;
import com.techm.orion.entitybeans.TestDetail;
import com.techm.orion.entitybeans.TestStrategeyVersioningJsonModel;
import com.techm.orion.entitybeans.Vendors;
import com.techm.orion.models.TestBundleModel;
import com.techm.orion.pojo.TestBundlePojo;
import com.techm.orion.pojo.TestStrategyPojo;
import com.techm.orion.repositories.DeviceFamilyRepository;
import com.techm.orion.repositories.OSRepository;
import com.techm.orion.repositories.OSversionRepository;
import com.techm.orion.repositories.TestBundlingRepository;
import com.techm.orion.repositories.TestDetailsRepository;
import com.techm.orion.repositories.VendorRepository;
import com.techm.orion.service.TestBundleService;

@RestController
public class TestBundlingController {

	@Autowired
	private VendorRepository vendorRepository;

	@Autowired
	private DeviceFamilyRepository deviceFamilyRepository;

	@Autowired
	private OSRepository osRepository;

	@Autowired
	private OSversionRepository osversionRepository;

	@Autowired
	private TestDetailsRepository testDetailsRepository;

	@Autowired
	private TestBundlingRepository testBundlingRepository;

	@Autowired
	private TestBundleService testBundleServce;

	private static final Logger logger = LogManager.getLogger(TestBundlingController.class);

	RequestInfoDao dao = new RequestInfoDao();

	/**
	 *This Api is marked as ***************c3p-ui Api Impacted****************
	 **/
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@POST
	@RequestMapping(value = "/deviceFamilyBundling", method = RequestMethod.POST, produces = "application/json")
	public ResponseEntity getDeviceFamilyBundling(@RequestBody String request) {

		JSONObject object = null;
		JSONParser parser = new JSONParser();
		JSONObject obj = new JSONObject();
		List<DeviceFamily> deviceFamilyList = null;

		try {
			obj = (JSONObject) parser.parse(request);
			String vendor = obj.get("vendor").toString();

			Set<Vendors> exsistingvendor = vendorRepository.findByVendor(vendor);
			List<Vendors> list = new ArrayList<>(exsistingvendor);
			if (list.size() > 0) {
				deviceFamilyList = deviceFamilyRepository.findByVendor(list.get(0));

				object = new JSONObject();
				object.put(1, deviceFamilyList);

			}
		} catch (ParseException exe) {

			logger.error("Exception occurred while parsing the Json object" + exe.getMessage());
		}

		return new ResponseEntity(deviceFamilyList, HttpStatus.OK);

	}

	/**
	 *This Api is marked as ***************c3p-ui Api Impacted****************
	 **/
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@POST
	@RequestMapping(value = "/osBundling", method = RequestMethod.POST, produces = "application/json")
	public ResponseEntity getOsBundling(@RequestBody String request) {

		JSONParser parser = new JSONParser();
		JSONObject obj = new JSONObject();
		JSONObject object = null;
		
		JSONArray outputArray = new JSONArray();
		DeviceFamily familyList = null;
		List<OS> oslist = null;
		try {
			obj = (JSONObject) parser.parse(request);
			String family = obj.get("family").toString();

			if ("All".equals(family)) {

				oslist = osRepository.findAll();
				for (OS i : oslist) {
					object = new JSONObject();
					object.put("Id", i.getId());
					object.put("Os", i.getOs());
					outputArray.add(0, object);

				}

			} else {
				familyList = deviceFamilyRepository.findVendor(family);
				List<OS> osFamily = null;
			
					osFamily = osRepository.findByDeviceFamily(familyList);
					
					osFamily.forEach(action -> {
						JSONObject jsonObject = new JSONObject();
						jsonObject.put("Id", action.getId());
						jsonObject.put("Os", action.getOs());
						outputArray.add(jsonObject);
				});
			}

		} catch (Exception exe) {
			logger.error("Exception occurred while fetching the data object" + exe.getMessage());
		}

		return new ResponseEntity(outputArray, HttpStatus.OK);
	}

	/**
	 *This Api is marked as ***************c3p-ui Api Impacted****************
	 **/
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@POST
	@RequestMapping(value = "/osversionForBundling", method = RequestMethod.POST, produces = "application/json")
	public ResponseEntity getOsversionForBundling(@RequestBody String request) {

		Set<OS> setos = new HashSet<OS>();

		JSONParser parser = new JSONParser();
		JSONObject obj = new JSONObject();
		JSONObject object = null;
		JSONArray outputArray = new JSONArray();
		Set<OS> familyList = null;

		Set<OSversion> setosversion = new HashSet<OSversion>();

		List<OSversion> osversionlst = new ArrayList<OSversion>();

		try {

			obj = (JSONObject) parser.parse(request);
			String os = obj.get("os").toString();

			if ("All".equals(os)) {
				osversionlst = (List<OSversion>) osversionRepository.findAll();
				for (OSversion i : osversionlst) {
					object = new JSONObject();
					object.put("Id", i.getId());
					object.put("Os", i.getOsversion());
					outputArray.add(0, object);
				}
			} else {

				familyList = osRepository.findByOs(os);
				Set<OSversion> osVersionFamily = null;
				for (OS operatingSystem : familyList) {
				osVersionFamily = osversionRepository.findByOs(operatingSystem);
				List<OSversion> list = new ArrayList<>(osVersionFamily);
				list.forEach(action -> {
					JSONObject objectJson = new JSONObject();
					objectJson.put("Id", action.getId());
					objectJson.put("Os", action.getOsversion());
					outputArray.add(objectJson);
				});
				}
			}
		} catch (Exception exe) {
			logger.error("Exception occurred while fetching the data object" + exe.getMessage());
		}
		return new ResponseEntity(outputArray, HttpStatus.OK);

	}

	/**
	 *This Api is marked as ***************c3p-ui Api Impacted****************
	 **/
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@POST
	@RequestMapping(value = "/bundleNameValidation", method = RequestMethod.POST, produces = "application/json")
	public ResponseEntity validateBundleName(@RequestBody String request) {

		JSONParser parser = new JSONParser();
		JSONObject obj = new JSONObject();
		JSONObject object = new JSONObject();

		String bundleName = null, Str = null;

		List<String> listOfTestBundle = new ArrayList<String>();

		try {

			obj = (JSONObject) parser.parse(request);
			bundleName = obj.get("bundleName").toString();

			listOfTestBundle = testBundlingRepository.findBundleName();

			if (listOfTestBundle.contains(bundleName)) {
				Str = "Bundle name already exist";
			} else {
				Str = "Bundle name does not exist";
			}
			object.put("Validation", Str);
		} catch (Exception exe) {
			logger.error("Exception occurred while fetching the data object" + exe.getMessage());
		}
		return new ResponseEntity(object, HttpStatus.OK);

	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@POST
	@RequestMapping(value = "/searchTestBundle", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
	@ResponseBody
	public ResponseEntity searchTestBundle(@RequestBody String searchParameters) {

		JSONObject obj = null;
		JSONArray jsonArray = new JSONArray();
		List<TestBundleModel> versioningModel = new ArrayList<>();
		List<TestBundlePojo> versioningModelChildList = new ArrayList<TestBundlePojo>();
		TestBundlePojo objToAdd = null;
		TestBundleModel versioningModelObject = null;
		List<Integer> listOfTest = new ArrayList<>();
		Set tempTestCategory = new HashSet<>();
		int testId = 0;

		String tempTestCategoryName = null, testCategory = null;

		List<TestDetail> listOfTestId;

		String networkFunction = null, region = null, vendor = null, osVersion = null, os = null, deviceFamily = null;

		List<TestDetail> listOfTestDetails = new ArrayList<TestDetail>();
		try {
			JSONParser parser = new JSONParser();
			JSONObject json = (JSONObject) parser.parse(searchParameters);

			if (json.containsKey("networkFunction")) {
				networkFunction = json.get("networkFunction").toString();
			}
			if (json.containsKey("region")) {
				region = json.get("region").toString();
			}
			if (json.containsKey("vendor")) {
				vendor = json.get("vendor").toString();
			}
			if (json.containsKey("osVersion")) {
				osVersion = json.get("osVersion").toString();
			}
			if (json.containsKey("os")) {
				os = json.get("os").toString();
			}
			if (json.containsKey("deviceFamily")) {
				deviceFamily = json.get("deviceFamily").toString();
			}

			if ("All".equals(region)) {
				region = "%";
			} else {
				region = "%" + region +"%";
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
			

			listOfTestDetails = testDetailsRepository.getTesListData(deviceFamily, os, region, osVersion, vendor,
					networkFunction);

			for (TestDetail tempObj : listOfTestDetails) {

				testCategory = tempObj.getTestCategory();
				testId = tempObj.getId();
				listOfTest.add(testId);
				tempTestCategory.add(testCategory);
			}

			Iterator it = tempTestCategory.iterator();
			while (it.hasNext()) {
				versioningModelObject = new TestBundleModel();
				obj = new JSONObject();

				versioningModelChildList = new ArrayList<TestBundlePojo>();
				versioningModelObject.setName((String) it.next());

				tempTestCategoryName = versioningModelObject.getName();

				for (int i = 0; i < listOfTest.size(); i++) {

					listOfTestId = dao.getAllTestsForSearch(listOfTest.get(i).intValue(), tempTestCategoryName);

					if (listOfTestId.isEmpty()) {
						continue;
					} else {
						for (int j = 0; j < listOfTestId.size(); j++) {
							objToAdd = new TestBundlePojo();

							objToAdd.setTest_id(listOfTestId.get(j).getId());

							String s = listOfTestId.get(j).getTestName();
							String seriesId = StringUtils.substringAfterLast(s, "_")+"_"+listOfTestId.get(j).getVersion();

							objToAdd.setTestName(seriesId);
							versioningModelChildList.add(objToAdd);
							Collections.reverse(versioningModelChildList);

						}
					}
				}

				versioningModelObject.setTests(versioningModelChildList);
				versioningModel.add(versioningModelObject);
				jsonArray.add(versioningModel);
			}

			obj.put("categoryTest", versioningModel);
		} catch (Exception e) {
			logger.error(e);
			obj.put("data", "No Record Found");
		}

		return new ResponseEntity(obj, HttpStatus.OK);
	}

	/**
	 *This Api is marked as ***************c3p-ui Api Impacted****************
	 **/
	@POST
	@RequestMapping(value = "/saveBundle", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
	@ResponseBody
	public String saveBundle(@RequestBody String request) {
		String testList = null;
		try {
			JSONParser parser = new JSONParser();
			JSONObject json = (JSONObject) parser.parse(request);
			String bundleName = null, networkFunction = null, deviceFamily = null, vendor = null, os = null,
					osVersion = null, region = null, userName = null;
			
			if (json.get("bundleName") != null) {
				bundleName = json.get("bundleName").toString();
			}
			if (json.get("networkfunction") != null) {
				networkFunction = json.get("networkfunction").toString();
			}

			if (json.get("vendor") != null) {
				vendor = json.get("vendor").toString();
			}

			if (json.get("deviceFamily") != null) {
				deviceFamily = json.get("deviceFamily").toString();
			}
			if (json.get("os") != null) {
				os = json.get("os").toString();
			}
			if (json.get("osVersion") != null) {
				osVersion = json.get("osVersion").toString();
			}
			if (json.get("region") != null) {
				region = json.get("region").toString();
			}
			
			if(json.get("userName") !=null)
				userName = json.get("userName").toString();

			JSONArray testListJson = null;
			if (json.containsKey("tests")) {
				testListJson = (JSONArray) json.get("tests");
			}
			Set<TestDetail> testDetails = new HashSet<>();
			if (testListJson != null && !testListJson.isEmpty()) {
				for (int i = 0; i < testListJson.size(); i++) {
					TestDetail testEntity = new TestDetail();
					JSONObject object = (JSONObject) testListJson.get(i);
					String id = object.get("testId").toString();
					int idValue = Integer.parseInt(id);
					testEntity.setId(idValue);
					testDetails.add(testEntity);
				}
			}
			if (bundleName != null && networkFunction != null) {
				testList = testBundleServce.saveBundle(bundleName, networkFunction, vendor, deviceFamily, os, osVersion,
						region, testDetails, userName);
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
		}
		return testList;
	}

	/**
	 *This Api is marked as ***************c3p-ui Api Impacted****************
	 **/
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@POST
	@RequestMapping(value = "/searchTestBundleForSingleTest", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
	@ResponseBody
	public ResponseEntity searchTestBundleForSingleTest(@RequestBody String searchParameters) {

		JSONObject obj = null;

		int testId = 0;
		String testBundleName = null;
		JSONArray outputArray = null;

		String networkFunction = null, region = null, vendor = null, osVersion = null, os = null, deviceFamily = null;

		List<TestBundling> listOfTestDetails = new ArrayList<TestBundling>();
		try {
			JSONParser parser = new JSONParser();
			JSONObject json = (JSONObject) parser.parse(searchParameters);

			if (json.containsKey("networkFunction")) {
				networkFunction = json.get("networkFunction").toString();

			}
			if (json.containsKey("region")) {
				region = json.get("region").toString();
			}
			if (json.containsKey("vendor")) {
				vendor = json.get("vendor").toString();
			}
			if (json.containsKey("osVersion")) {
				osVersion = json.get("osVersion").toString();
			}
			if (json.containsKey("os")) {
				os = json.get("os").toString();
			}
			if (json.containsKey("deviceFamily")) {
				deviceFamily = json.get("deviceFamily").toString();
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

			listOfTestDetails = testBundlingRepository.getTestBundleData(deviceFamily, os, region, osVersion, vendor,
					networkFunction);

			outputArray = new JSONArray();

			for (int i = 0; i < listOfTestDetails.size(); i++) {

				obj = new JSONObject();
				testBundleName = listOfTestDetails.get(i).getTestBundle();
				testId = listOfTestDetails.get(i).getId();
				obj.put("id", testId);
				obj.put("bundleName", testBundleName);
				outputArray.add(obj);
			}

			// obj.put("BundleList", outputArray);
		} catch (Exception e) {
			logger.error(e);
			obj.put("data", "No Record Found");
		}

		return new ResponseEntity(outputArray, HttpStatus.OK);
	}

	/**
	 *This Api is marked as ***************c3p-ui Api Impacted****************
	 **/
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@POST
	@RequestMapping(value = "/getBundleList", method = RequestMethod.GET, produces = "application/json")
	@ResponseBody
	public ResponseEntity getTestList() {

		// Create first level

		List<TestStrategyPojo> mainList = new ArrayList<TestStrategyPojo>();

		TestStrategeyVersioningJsonModel model = new TestStrategeyVersioningJsonModel();
		List<TestStrategeyVersioningJsonModel> versioningModel = new ArrayList<TestStrategeyVersioningJsonModel>();
		List<TestBundling> bundleList = new ArrayList<TestBundling>();
		List<TestBundlePojo> testIdList = new ArrayList<TestBundlePojo>();

		int bundleId = 0, tempTestId = 0;

		List<TestStrategyPojo> modelList = new ArrayList<TestStrategyPojo>();
		bundleList = testBundlingRepository.findAll();

		for (TestBundling temp : bundleList) {
			model = new TestStrategeyVersioningJsonModel();
			bundleId = temp.getId();
			testIdList = dao.findTestIdList(bundleId);
			model.setBundleName(temp.getTestBundle());
			model.setVendor(temp.getVendor());
			model.setDeviceModel(temp.getDeviceFamily());
			model.setOs(temp.getOs() + "/" + temp.getOsVersion());
			model.setCreatedBy(temp.getUpdatedBy());
			model.setCreatedOn(temp.getUpdatedDate().toString());
			modelList = new ArrayList<TestStrategyPojo>();
			for (int i = 0; i < testIdList.size(); i++) {
				tempTestId = testIdList.get(i).getTest_id();
				mainList = dao.getTestsForTestStrategyOnId(tempTestId);
				modelList.add(mainList.get(0));
			}
			Collections.reverse(modelList);

			modelList.get(0).setEnabled(true);
			model.setTestStrategyPojoList(modelList);
			versioningModel.add(model);
		}

		return new ResponseEntity(versioningModel, HttpStatus.OK);

	}

	/**
	 *This Api is marked as ***************c3p-ui Api Impacted****************
	 **/
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@POST
	@RequestMapping(value = "/getSearchBundleList", method = RequestMethod.POST, produces = "application/json", consumes = "application/json")
	public ResponseEntity getSearchBundleList(@RequestBody String request) throws ParseException {

		String key = "", value = "";
		int bundleId = 0, tempTestId = 0;

		JSONParser parser = new JSONParser();

		List<TestBundlePojo> testIdList = new ArrayList<TestBundlePojo>();

		List<TestStrategyPojo> modelList = new ArrayList<TestStrategyPojo>();
		List<TestStrategyPojo> testDetail = new ArrayList<TestStrategyPojo>();

		List<TestBundling> mainList = new ArrayList<TestBundling>();

		TestStrategeyVersioningJsonModel model = new TestStrategeyVersioningJsonModel();
		List<TestStrategeyVersioningJsonModel> versioningModel = new ArrayList<TestStrategeyVersioningJsonModel>();

		JSONObject json = (JSONObject) parser.parse(request);

		key = (String) json.get("key");
		value = (String) json.get("value");

		if (value != null && !value.isEmpty()) {

			if (key.equalsIgnoreCase("Device Family")) {
				mainList = dao.findByTestNameForSearch(key, value);

			} else if (key.equalsIgnoreCase("Vendor")) {
				mainList = dao.findByTestNameForSearch(key, value);

			} else if (key.equalsIgnoreCase("OS")) {
				mainList = dao.findByTestNameForSearch(key, value);

			} else if (key.equalsIgnoreCase("OS Version")) {
				mainList = dao.findByTestNameForSearch(key, value);

			} else if (key.equalsIgnoreCase("Bundle Name")) {
				mainList = dao.findByTestNameForSearch(key, value);
			} else if (key.equalsIgnoreCase("Region")) {
				mainList = dao.findByTestNameForSearch(key, value);

			}

			for (TestBundling temp : mainList) {
				model = new TestStrategeyVersioningJsonModel();
				bundleId = temp.getId();
				testIdList = dao.findTestIdList(bundleId);
				model.setBundleName(temp.getTestBundle());
				model.setVendor(temp.getVendor());
				model.setDeviceFamily(temp.getDeviceFamily());
				model.setOs(temp.getOs() + "/" + temp.getOsVersion());
				model.setCreatedBy(temp.getUpdatedBy());
				model.setCreatedOn(temp.getUpdatedDate().toString());
				modelList = new ArrayList<TestStrategyPojo>();

				for (int i = 0; i < testIdList.size(); i++) {
					tempTestId = testIdList.get(i).getTest_id();
					testDetail = dao.getTestsForTestStrategyOnId(tempTestId);
					modelList.add(testDetail.get(0));
				}
				Collections.reverse(modelList);

				modelList.get(0).setEnabled(true);
				model.setTestStrategyPojoList(modelList);
				versioningModel.add(model);
			}

		}

		return new ResponseEntity(versioningModel, HttpStatus.OK);

	}
	
	/**
	 *This Api is marked as ***************c3p-ui Api Impacted****************
	 **/
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@POST
	@RequestMapping(value = "/getBundleView", method = RequestMethod.POST, produces = "application/json", consumes = "application/json")
	public ResponseEntity getBundleView(@RequestBody String request) throws ParseException {

		String key = "", value = "";
		int bundleId = 0, tempTestId = 0;

		JSONParser parser = new JSONParser();

		List<TestDetail> testIdList = new ArrayList<TestDetail>();

		List<TestDetail> modelList = new ArrayList<TestDetail>();
		List<TestDetail> testDetail = new ArrayList<TestDetail>();

		List<TestBundling> mainList = new ArrayList<TestBundling>();

		TestStrategeyVersioningJsonModel model = new TestStrategeyVersioningJsonModel();
		List<TestStrategeyVersioningJsonModel> versioningModel = new ArrayList<TestStrategeyVersioningJsonModel>();

		JSONObject json = (JSONObject) parser.parse(request);

		key = (String) json.get("key");
		value = (String) json.get("value");

		if (value != null && !value.isEmpty()) {

			if (key.equalsIgnoreCase("Bundle Name")) {
				mainList = dao.findByTestNameForSearch(key, value);

			}
			for (TestBundling temp : mainList) {
				model = new TestStrategeyVersioningJsonModel();
				bundleId = temp.getId();
				testIdList = dao.findTestId(bundleId);
				model.setBundleName(temp.getTestBundle());
				model.setVendor(temp.getVendor());
				model.setDeviceFamily(temp.getDeviceFamily());
				model.setOs(temp.getOs());
				model.setOsVersion(temp.getOsVersion());
				model.setCreatedBy(temp.getUpdatedBy());
				model.setCreatedOn(temp.getUpdatedDate().toString());
				model.setRegion(temp.getRegion());
				modelList = new ArrayList<TestDetail>();

				for (int i = 0; i < testIdList.size(); i++) {
					tempTestId = testIdList.get(i).getId();
					testDetail = dao.getBundleView(tempTestId);

					modelList.add(testDetail.get(0));
				}
				Collections.reverse(modelList);

				modelList.get(0).setEnabled(true);
				model.setTestStrategyPojoList1(modelList);
				versioningModel.add(model);
			}
		}

		return new ResponseEntity(versioningModel, HttpStatus.OK);

	}

}
