package com.techm.orion.rest;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.ws.rs.POST;

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
	public VendorRepository vendorRepository;

	@Autowired
	public DeviceFamilyRepository deviceFamilyRepository;

	@Autowired
	public OSRepository osRepository;

	@Autowired
	public OSversionRepository osversionRepository;

	@Autowired
	private TestDetailsRepository testDetailsRepository;

	@Autowired
	private TestBundlingRepository testBundlingRepository;

	@Autowired
	private TestBundleService testBundleServce;

	private static final Logger logger = LogManager.getLogger(TestBundlingController.class);

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
		} catch (ParseException e) {

			logger.error(e);
		}

		return new ResponseEntity(deviceFamilyList, HttpStatus.OK);

	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@POST
	@RequestMapping(value = "/osBundling", method = RequestMethod.POST, produces = "application/json")
	public ResponseEntity getOsBundling(@RequestBody String request) {

		JSONParser parser = new JSONParser();
		JSONObject obj = new JSONObject();
		JSONObject object = null;
		JSONArray outputArray = new JSONArray();
		Set<DeviceFamily> familyList = null;
		List<OS> oslist = null;
		try {
			obj = (JSONObject) parser.parse(request);
			String family = obj.get("family").toString();

			if (family.equals("All")) {

				oslist = osRepository.findAll();
				for (OS i : oslist) {
					object = new JSONObject();
					object.put("Id", i.getId());
					object.put("Os", i.getOs());
					outputArray.add(0, object);

				}

			} else {

				familyList = deviceFamilyRepository.findByDeviceFamily(family);

				if (null != familyList && !familyList.isEmpty()) {
					object = new JSONObject();
					List<DeviceFamily> list = new ArrayList<>(familyList);
					oslist = osRepository.findByDeviceFamily(list.get(0));
					object.put("Id", oslist.get(0).getId());
					object.put("Os", oslist.get(0).getOs());
					outputArray.add(0, object);

				}
			}

		} catch (Exception e) {
			logger.error(e);
		}

		return new ResponseEntity(outputArray, HttpStatus.OK);
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@POST
	@RequestMapping(value = "/osversionForBundling", method = RequestMethod.POST, produces = "application/json")
	public ResponseEntity getOsversionForBundling(@RequestBody String request) {

		Set<OS> setos = new HashSet<OS>();

		JSONParser parser = new JSONParser();
		JSONObject obj = new JSONObject();
		JSONObject object = null;
		JSONArray outputArray = new JSONArray();

		Set<OSversion> setosversion = new HashSet<OSversion>();

		List<OSversion> osversionlst = new ArrayList<OSversion>();

		try {

			obj = (JSONObject) parser.parse(request);
			String os = obj.get("os").toString();

			if (os.equals("All")) {
				osversionlst = (List<OSversion>) osversionRepository.findAll();
				for (OSversion i : osversionlst) {
					object = new JSONObject();
					object.put("Id", i.getId());
					object.put("Os", i.getOsversion());
					outputArray.add(0, object);

				}
			} else {

				setos = osRepository.findByOs(os);

				for (OS os1 : setos) {
					setosversion = osversionRepository.findByOs(os1);

					osversionlst.addAll(setosversion);
					for (OSversion i : osversionlst) {
						object = new JSONObject();
						object.put("Id", i.getId());
						object.put("Os", i.getOsversion());
						outputArray.add(0, object);

					}

				}
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
		return new ResponseEntity(outputArray, HttpStatus.OK);

	}

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
		} catch (Exception e) {
			// TODO: handle exception
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
		List<TestBundleModel> versioningModel = new ArrayList<TestBundleModel>();
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

			// Implementation of search logic based on fields received from UI
			String nonMandatoryfiltersbits = "000";

			if (deviceFamily.equals("All")) {
				nonMandatoryfiltersbits = "100";
			}
			if (os.equals("All")) {
				nonMandatoryfiltersbits = "110";
			}
			if (osVersion.equals("All")) {
				nonMandatoryfiltersbits = "111";
			}

			if (region.equals("All")) {
				nonMandatoryfiltersbits = "211";
			}
			if (deviceFamily.equals("All") && os.equals("All")) {
				nonMandatoryfiltersbits = "221";
			}
			if (deviceFamily.equals("All") && osVersion.equals("All")) {
				nonMandatoryfiltersbits = "222";
			}
			if (deviceFamily.equals("All") && region.equals("All")) {
				nonMandatoryfiltersbits = "322";
			}
			if (os.equals("All") && osVersion.equals("All")) {
				nonMandatoryfiltersbits = "332";
			}
			if (os.equals("All") && region.equals("All")) {
				nonMandatoryfiltersbits = "333";
			}
			if (region.equals("All") && osVersion.equals("All")) {
				nonMandatoryfiltersbits = "433";
			}
			if ((deviceFamily.equals("All") && os.equals("All"))
					&& (deviceFamily.equals("All") && osVersion.equals("All"))) {
				nonMandatoryfiltersbits = "443";
			}
			if ((osVersion.equals("All") && os.equals("All")) && (region.equals("All") && osVersion.equals("All"))) {
				nonMandatoryfiltersbits = "444";
			}
			if ((deviceFamily.equals("All") && os.equals("All"))
					&& (region.equals("All") && deviceFamily.equals("All"))) {
				nonMandatoryfiltersbits = "544";
			}
			if ((deviceFamily.equals("All") && osVersion.equals("All"))
					&& (region.equals("All") && deviceFamily.equals("All"))) {
				nonMandatoryfiltersbits = "554";
			}
			if ((deviceFamily.equals("All") && os.equals("All")) && (region.equals("All") && osVersion.equals("All"))) {
				nonMandatoryfiltersbits = "555";
			}
			/* getting Test Details list based on search */
			if (nonMandatoryfiltersbits.equalsIgnoreCase("100")) {

				listOfTestDetails = testDetailsRepository.findByOsAndOsVersionAndVendorAndRegionAndNetworkType(os,
						osVersion, vendor, region, networkFunction);
			}
			if (nonMandatoryfiltersbits.equalsIgnoreCase("110")) {

				listOfTestDetails = testDetailsRepository
						.findByDeviceFamilyAndOsVersionAndVendorAndRegionAndNetworkType(deviceFamily, osVersion, vendor,
								region, networkFunction);
			}
			if (nonMandatoryfiltersbits.equalsIgnoreCase("111")) {

				listOfTestDetails = testDetailsRepository.findByDeviceFamilyAndOsAndVendorAndRegionAndNetworkType(
						deviceFamily, os, vendor, region, networkFunction);
			}
			if (nonMandatoryfiltersbits.equalsIgnoreCase("211")) {

				listOfTestDetails = testDetailsRepository.findByDeviceFamilyAndOsAndOsVersionAndVendorAndNetworkType(
						deviceFamily, os, osVersion, vendor, networkFunction);
			}
			if (nonMandatoryfiltersbits.equalsIgnoreCase("221")) {

				listOfTestDetails = testDetailsRepository.findByOsVersionAndVendorAndNetworkTypeAndRegion(osVersion,
						vendor, networkFunction, region);
			}
			if (nonMandatoryfiltersbits.equalsIgnoreCase("222")) {

				listOfTestDetails = testDetailsRepository.findByOsAndVendorAndNetworkTypeAndRegion(os, vendor,
						networkFunction, region);
			}
			if (nonMandatoryfiltersbits.equalsIgnoreCase("322")) {

				listOfTestDetails = testDetailsRepository.findByOsAndOsVersionAndVendorAndNetworkType(os, osVersion,
						vendor, networkFunction);
			}
			if (nonMandatoryfiltersbits.equalsIgnoreCase("332")) {

				listOfTestDetails = testDetailsRepository.findByDeviceFamilyAndVendorAndNetworkTypeAndRegion(
						deviceFamily, vendor, networkFunction, region);
			}
			if (nonMandatoryfiltersbits.equalsIgnoreCase("333")) {

				listOfTestDetails = testDetailsRepository.findByDeviceFamilyAndOsVersionAndVendorAndNetworkType(
						deviceFamily, osVersion, vendor, networkFunction);
			}
			if (nonMandatoryfiltersbits.equalsIgnoreCase("433")) {

				listOfTestDetails = testDetailsRepository.findByDeviceFamilyAndOsAndVendorAndNetworkType(deviceFamily,
						os, vendor, networkFunction);
			}
			if (nonMandatoryfiltersbits.equalsIgnoreCase("443")) {
				listOfTestDetails = testDetailsRepository.findByVendorAndRegionAndNetworkType(vendor, region,
						networkFunction);

			}
			if (nonMandatoryfiltersbits.equalsIgnoreCase("444")) {

				listOfTestDetails = testDetailsRepository.findByDeviceFamilyAndVendorAndNetworkType(deviceFamily,
						vendor, networkFunction);
			}
			if (nonMandatoryfiltersbits.equalsIgnoreCase("544")) {

				listOfTestDetails = testDetailsRepository.findByOsVersionAndVendorAndNetworkType(osVersion, vendor,
						networkFunction);
			}
			if (nonMandatoryfiltersbits.equalsIgnoreCase("554")) {

				listOfTestDetails = testDetailsRepository.findByOsAndVendorAndNetworkType(os, vendor, networkFunction);
			}
			if (nonMandatoryfiltersbits.equalsIgnoreCase("555")) {

				listOfTestDetails = testDetailsRepository.findByVendorAndNetworkType(vendor, networkFunction);
			}

			// listOfTestBundle = testBundleJoinRepo.findAll();
			for (int i = 0; i < listOfTestDetails.size(); i++) {

				testCategory = listOfTestDetails.get(i).getTestCategory();
				testId = listOfTestDetails.get(i).getId();
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

					listOfTestId = testDetailsRepository.findByTestCategory(listOfTest.get(i).intValue(),
							tempTestCategoryName);

					if (listOfTestId.isEmpty()) {
						continue;
					} else {
						for (int j = 0; j < listOfTestId.size(); j++) {
							objToAdd = new TestBundlePojo();

							objToAdd.setTestId(listOfTestId.get(j).getId());
							objToAdd.setTestName(listOfTestId.get(j).getTestId().substring(15));
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

	@POST
	@RequestMapping(value = "/saveBundle", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
	@ResponseBody
	public String saveBundle(@RequestBody String request) {
		String testList = null;
		try {
			JSONParser parser = new JSONParser();
			JSONObject json = (JSONObject) parser.parse(request);
			String bundleName = null, networkFunction = null, deviceFamily = null, vendor = null, os = null,
					osVersion = null, region = null, model = null;

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
						region, testDetails);
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
		}
		return testList;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@POST
	@RequestMapping(value = "/searchTestBundleForSingleTest", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
	@ResponseBody
	public ResponseEntity searchTestBundleForSingleTest(@RequestBody String searchParameters) {

		JSONObject obj = null;

		int testId = 0;
		String testBundleName = null;
		JSONArray outputArray = null;
		HashMap<Integer, String> hmap = new HashMap<Integer, String>();

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

			// Implementation of search logic based on fields received from UI
			String nonMandatoryfiltersbits = "000";

			if (deviceFamily.equals("All")) {
				nonMandatoryfiltersbits = "100";
			}
			if (os.equals("All")) {
				nonMandatoryfiltersbits = "110";
			}
			if (osVersion.equals("All")) {
				nonMandatoryfiltersbits = "111";
			}

			if (region.equals("All")) {
				nonMandatoryfiltersbits = "211";
			}
			if (deviceFamily.equals("All") && os.equals("All")) {
				nonMandatoryfiltersbits = "221";
			}
			if (deviceFamily.equals("All") && osVersion.equals("All")) {
				nonMandatoryfiltersbits = "222";
			}
			if (deviceFamily.equals("All") && region.equals("All")) {
				nonMandatoryfiltersbits = "322";
			}
			if (os.equals("All") && osVersion.equals("All")) {
				nonMandatoryfiltersbits = "332";
			}
			if (os.equals("All") && region.equals("All")) {
				nonMandatoryfiltersbits = "333";
			}
			if (region.equals("All") && osVersion.equals("All")) {
				nonMandatoryfiltersbits = "433";
			}
			if ((deviceFamily.equals("All") && os.equals("All"))
					&& (deviceFamily.equals("All") && osVersion.equals("All"))) {
				nonMandatoryfiltersbits = "443";
			}
			if ((osVersion.equals("All") && os.equals("All")) && (region.equals("All") && osVersion.equals("All"))) {
				nonMandatoryfiltersbits = "444";
			}
			if ((deviceFamily.equals("All") && os.equals("All"))
					&& (region.equals("All") && deviceFamily.equals("All"))) {
				nonMandatoryfiltersbits = "544";
			}
			if ((deviceFamily.equals("All") && osVersion.equals("All"))
					&& (region.equals("All") && deviceFamily.equals("All"))) {
				nonMandatoryfiltersbits = "554";
			}
			if ((deviceFamily.equals("All") && os.equals("All")) && (region.equals("All") && osVersion.equals("All"))) {
				nonMandatoryfiltersbits = "555";
			}
			/* getting Test Details list based on search */
			if (nonMandatoryfiltersbits.equalsIgnoreCase("100")) {

				listOfTestDetails = testBundlingRepository.findByOsAndOsVersionAndVendorAndRegionAndNetworkFunction(os,
						osVersion, vendor, region, networkFunction);
			}
			if (nonMandatoryfiltersbits.equalsIgnoreCase("110")) {

				listOfTestDetails = testBundlingRepository
						.findByDeviceFamilyAndOsVersionAndVendorAndRegionAndNetworkFunction(deviceFamily, osVersion,
								vendor, region, networkFunction);
			}
			if (nonMandatoryfiltersbits.equalsIgnoreCase("111")) {

				listOfTestDetails = testBundlingRepository.findByDeviceFamilyAndOsAndVendorAndRegionAndNetworkFunction(
						deviceFamily, os, vendor, region, networkFunction);
			}
			if (nonMandatoryfiltersbits.equalsIgnoreCase("211")) {

				listOfTestDetails = testBundlingRepository
						.findByDeviceFamilyAndOsAndOsVersionAndVendorAndNetworkFunction(deviceFamily, os, osVersion,
								vendor, networkFunction);
			}
			if (nonMandatoryfiltersbits.equalsIgnoreCase("221")) {

				listOfTestDetails = testBundlingRepository.findByOsVersionAndVendorAndNetworkFunctionAndRegion(
						osVersion, vendor, networkFunction, region);
			}
			if (nonMandatoryfiltersbits.equalsIgnoreCase("222")) {

				listOfTestDetails = testBundlingRepository.findByOsAndVendorAndNetworkFunctionAndRegion(os, vendor,
						networkFunction, region);
			}
			if (nonMandatoryfiltersbits.equalsIgnoreCase("322")) {

				listOfTestDetails = testBundlingRepository.findByOsAndOsVersionAndVendorAndNetworkFunction(os,
						osVersion, vendor, networkFunction);
			}
			if (nonMandatoryfiltersbits.equalsIgnoreCase("332")) {

				listOfTestDetails = testBundlingRepository.findByDeviceFamilyAndVendorAndNetworkFunctionAndRegion(
						deviceFamily, vendor, networkFunction, region);
			}
			if (nonMandatoryfiltersbits.equalsIgnoreCase("333")) {

				listOfTestDetails = testBundlingRepository.findByDeviceFamilyAndOsVersionAndVendorAndNetworkFunction(
						deviceFamily, osVersion, vendor, networkFunction);
			}
			if (nonMandatoryfiltersbits.equalsIgnoreCase("433")) {

				listOfTestDetails = testBundlingRepository
						.findByDeviceFamilyAndOsAndVendorAndNetworkFunction(deviceFamily, os, vendor, networkFunction);
			}
			if (nonMandatoryfiltersbits.equalsIgnoreCase("443")) {
				listOfTestDetails = testBundlingRepository.findByVendorAndRegionAndNetworkFunction(vendor, region,
						networkFunction);

			}
			if (nonMandatoryfiltersbits.equalsIgnoreCase("444")) {

				listOfTestDetails = testBundlingRepository.findByDeviceFamilyAndVendorAndNetworkFunction(deviceFamily,
						vendor, networkFunction);
			}
			if (nonMandatoryfiltersbits.equalsIgnoreCase("544")) {

				listOfTestDetails = testBundlingRepository.findByOsVersionAndVendorAndNetworkFunction(osVersion, vendor,
						networkFunction);
			}
			if (nonMandatoryfiltersbits.equalsIgnoreCase("554")) {

				listOfTestDetails = testBundlingRepository.findByOsAndVendorAndNetworkFunction(os, vendor,
						networkFunction);
			}
			if (nonMandatoryfiltersbits.equalsIgnoreCase("555")) {

				listOfTestDetails = testBundlingRepository.findByVendorAndNetworkFunction(vendor, networkFunction);
			}
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

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@POST
	@RequestMapping(value = "/getBundleList", method = RequestMethod.GET, produces = "application/json")
	@ResponseBody
	public ResponseEntity getTestList() {

		// Create first level

		RequestInfoDao dao = new RequestInfoDao();
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
			model.setCreatedBy(temp.getCreatedBy());
			model.setCreatedOn(temp.getCreatedDate().toString());
			modelList = new ArrayList<TestStrategyPojo>();
			for (int i = 0; i < testIdList.size(); i++) {
				tempTestId = testIdList.get(i).getTest_id();
				mainList = dao.getTestsForTestStrategyOnId(tempTestId);
				modelList.add(mainList.get(0));
			}
			Collections.reverse(modelList);

			modelList.get(0).setEnabled(true);
			model.setChildList(modelList);
			versioningModel.add(model);
		}

		return new ResponseEntity(versioningModel, HttpStatus.OK);

	}

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

		RequestInfoDao dao = new RequestInfoDao();
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

				List temp = new ArrayList<>();
				mainList = dao.findByTestNameForSearch(key, value);

			}

			for (TestBundling temp : mainList) {
				model = new TestStrategeyVersioningJsonModel();
				bundleId = temp.getId();
				testIdList = dao.findTestIdList(bundleId);
				model.setBundleName(temp.getTestBundle());
				model.setVendor(temp.getVendor());
				model.setDeviceModel(temp.getDeviceFamily());
				model.setOs(temp.getOs() + "/" + temp.getOsVersion());
				model.setCreatedBy(temp.getCreatedBy());
				model.setCreatedOn(temp.getCreatedDate().toString());
				modelList = new ArrayList<TestStrategyPojo>();

				for (int i = 0; i < testIdList.size(); i++) {
					tempTestId = testIdList.get(i).getTest_id();
					testDetail = dao.getTestsForTestStrategyOnId(tempTestId);
					modelList.add(testDetail.get(0));
				}
				Collections.reverse(modelList);

				modelList.get(0).setEnabled(true);
				model.setChildList(modelList);
				versioningModel.add(model);
			}

		}

		return new ResponseEntity(versioningModel, HttpStatus.OK);

	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@POST
	@RequestMapping(value = "/getBundleView", method = RequestMethod.POST, produces = "application/json", consumes = "application/json")
	public ResponseEntity getBundleView(@RequestBody String request) throws ParseException {

		String key = "", value = "";
		int bundleId = 0, tempTestId = 0;

		JSONParser parser = new JSONParser();

		List<TestBundlePojo> testIdList = new ArrayList<TestBundlePojo>();

		List<TestStrategyPojo> modelList = new ArrayList<TestStrategyPojo>();
		List<TestStrategyPojo> testDetail = new ArrayList<TestStrategyPojo>();

		RequestInfoDao dao = new RequestInfoDao();
		List<TestBundling> mainList = new ArrayList<TestBundling>();

		TestStrategeyVersioningJsonModel model = new TestStrategeyVersioningJsonModel();
		List<TestStrategeyVersioningJsonModel> versioningModel = new ArrayList<TestStrategeyVersioningJsonModel>();

		JSONObject json = (JSONObject) parser.parse(request);

		key = (String) json.get("key");
		value = (String) json.get("value");

		if (value != null && !value.isEmpty()) {

			if (key.equalsIgnoreCase("Bundle Name")) {

				List temp = new ArrayList<>();
				mainList = dao.findByTestNameForSearch(key, value);

			}

			for (TestBundling temp : mainList) {
				model = new TestStrategeyVersioningJsonModel();
				bundleId = temp.getId();
				testIdList = dao.findTestIdList(bundleId);
				model.setBundleName(temp.getTestBundle());
				model.setVendor(temp.getVendor());
				model.setDeviceModel(temp.getDeviceFamily());
				model.setOs(temp.getOs() + "/" + temp.getOsVersion());
				model.setCreatedBy(temp.getCreatedBy());
				model.setCreatedOn(temp.getCreatedDate().toString());
				modelList = new ArrayList<TestStrategyPojo>();

				for (int i = 0; i < testIdList.size(); i++) {
					tempTestId = testIdList.get(i).getTest_id();
					testDetail = dao.getTestsForTestStrategyOnId(tempTestId);
					modelList.add(testDetail.get(0));
				}
				Collections.reverse(modelList);

				modelList.get(0).setEnabled(true);
				model.setChildList(modelList);
				versioningModel.add(model);
			}

		}

		return new ResponseEntity(versioningModel, HttpStatus.OK);

	}

}
