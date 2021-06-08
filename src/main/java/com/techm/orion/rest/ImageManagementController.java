package com.techm.orion.rest;

import java.util.List;
import java.util.Map;

import javax.ws.rs.GET;
import javax.ws.rs.POST;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.InvalidDataAccessResourceUsageException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.techm.orion.entitybeans.ImageManagementEntity;
import com.techm.orion.repositories.ErrorValidationRepository;
import com.techm.orion.repositories.ImageManagementRepository;
import com.techm.orion.service.ImageManagementService;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping({ "/imageManagement" })
public class ImageManagementController {
	private static final Logger logger = LogManager.getLogger(ImageManagementController.class);

	@Autowired
	private ImageManagementService imageManagementService;
	
	@Autowired
	private ImageManagementRepository imageMangemntRepository;
	
	@Autowired
	private ErrorValidationRepository errorValidationRepository;


	/*
	 * Add record for single firmupgrade Device with Below validations will be
	 * applicable i->Duplicate Image_filename: For same Vendor and Family, if new
	 * image filename exactly matches with any of existing Image_filename, then
	 * return with error message : Image already exists ii->Duplicate Display Name :
	 * For same Vendor and Family, if new Display Name exactly matches with any of
	 * existing Display Name, then return with error message : Display Name already
	 * exists iii->Lower Image : For same Vendor and Family, if new Display Name is
	 * less than the highest Display Name available, then return with error message
	 * : A higher version already exists. Are you sure to add you want to add this
	 * version ?
	 */
	
	/**
	 *This Api is marked as ***************Both Api Impacted****************
	 **/
	@POST
	@RequestMapping(value = "/addFirmwareUpgradeForSingleDevice", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
	@ResponseBody
	public ResponseEntity addFirmwareUpgradeForSingleDevice(@RequestBody String searchParameters) {

		JSONObject obj = new JSONObject();
		JSONParser parser = new JSONParser();
		JSONObject json = new JSONObject();
		String displayName = "";

		try {
			json = (JSONObject) parser.parse(searchParameters);
			displayName = (String) json.get("displayName");
			Map<String, String> success = imageManagementService.addFirmWare(searchParameters);
			if (success.toString().contains("Image already exists")) {
				obj.put("errorMsg", "Image already exists");
				obj.put("errorType", "Duplicate Image filename");
				obj.put("currentOS", "");
				obj.put("upgradableOS", "");
			} else if (success.toString().contains("Display Name already exists")) {
				obj.put("errorMsg", "Display Name already exists");
				obj.put("errorType", "Duplicate Display Name");
				obj.put("currentOS", "");
				obj.put("upgradableOS", "");
			} else if (success.containsKey("HigherVersionExist")) {
				obj.put("errorMsg",
						"A higher version already exists. Are you sure to add you want to add this version ?");
				obj.put("errorType", "Lower Image");
				obj.put("currentOS", displayName);
				obj.put("upgradableOS", success.get("HigherVersionExist"));
			} else if (success.containsKey("error")) {
				obj.put("errorMsg", success.get("error").toString());
				obj.put("errorType", "Exception");
				obj.put("currentOS", "");
				obj.put("upgradableOS", "");
			} else {
				obj.put("errorMsg", "");
				obj.put("errorType", "");
				obj.put("currentOS", "");
				obj.put("upgradableOS", "");
				obj.put("status", "Added record successfully");
			}
		} catch (Exception e) {
			obj.put("errorMsg", e.getMessage());
			obj.put("errorType", "Exception");
			obj.put("currentOS", "");
			obj.put("upgradableOS", "");
		}
		return new ResponseEntity<JSONObject>(obj, HttpStatus.OK);
	}

	/**
	 *This Api is marked as ***************Both Api Impacted****************
	 **/
	@POST
	@RequestMapping(value = "/addFirmwareUpgradeForSingleDeviceLowerImage", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
	@ResponseBody
	public ResponseEntity addFirmwareUpgradeForSingleDeviceHigherVersion(@RequestBody String searchParameters) {

		JSONObject obj = new JSONObject();
		JSONParser parser = new JSONParser();
		JSONObject json = new JSONObject();
		try {
			json = (JSONObject) parser.parse(searchParameters);
			Map<String, String> success = imageManagementService.addFirmWareLowerImage(searchParameters);
			// String message = (String) res.get("responseResult");
			if (success.containsKey("error")) {
				obj.put("errorMsg", success.get("error").toString());
				obj.put("errorType", "Exception");
				obj.put("currentOS", "");
				obj.put("upgradableOS", "");
			} else {
				obj.put("errorMsg", "");
				obj.put("errorType", "");
				obj.put("currentOS", "");
				obj.put("upgradableOS", "");
				obj.put("status", "Added Lower Image successfully");
			}
		} catch (Exception e) {
			obj.put("errorMsg", e.getMessage());
			obj.put("errorType", "Exception");
			obj.put("currentOS", "");
			obj.put("upgradableOS", "");
		}
		return new ResponseEntity<JSONObject>(obj, HttpStatus.OK);
	}

	/*
	 * When user upgrades a Device from UI, this API will provide value for field
	 * “Update OS” by searching for highest available OS version (from column
	 * Display Name) against the selected device’s vendor and device model.
	 */
	
	/**
	 *This Api is marked as ***************Both Api Impacted****************
	 **/
	@POST
	@PreAuthorize("#oauth2.hasScope('read')")
	@RequestMapping(value = "/highestAvailableOsVersionForSingleDevice", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
	@ResponseBody
	public ResponseEntity highestAvailableOsVersionForSingleDevice(@RequestBody String searchParameters) {

		JSONObject obj = new JSONObject();
		JSONParser parser = new JSONParser();
		JSONObject json = new JSONObject();
		String displayName = "";

		try {
			json = (JSONObject) parser.parse(searchParameters);
			displayName = (String) json.get("osversion");
			Map<String, String> success = imageManagementService.findHighestOsVersion(searchParameters);

			if (success.containsKey("error")) {
				obj.put("statusMsg", success.get("error").toString());
				obj.put("statusType", "Exception");
				obj.put("currentOS", "");
				obj.put("upgradableOS", "");

			} else if (success.containsKey("statusMsg")) {
				obj.put("statusMsg", "No record found");
				obj.put("statusType", "Not found");
				obj.put("currentOS", "");
				obj.put("upgradableOS", "");
			} else if (success.containsKey("alreadyHigherVersionExist")) {
				obj.put("statusMsg", "Already Higher Version Exist");
				obj.put("statusType", "Higher Version");
				obj.put("currentOS", "");
				obj.put("upgradableOS", "");
			} else {
				obj.put("statusMsg",
						"A higher version already exists. Are you sure to add you want to add this version ?");
				obj.put("statusType", "Lower Image");
				obj.put("currentOS", displayName);
				obj.put("upgradableOS", success.get("highestOsVersion"));
			}
		} catch (Exception e) {
			obj.put("statusMsg", e.getMessage());
			obj.put("statusType", "Exception");
			obj.put("currentOS", "");
			obj.put("upgradableOS", "");
		}
		return new ResponseEntity<JSONObject>(obj, HttpStatus.OK);
	}
	
	/**
	 * This Api is marked as ***************c3p-ui Api Impacted****************
	 **/
	@SuppressWarnings("unchecked")
	@GET
	@RequestMapping(value = "/getImageManagmentDashboard", method = RequestMethod.GET, produces = "application/json")
	public ResponseEntity<JSONObject> getImageManagmentDashboard() {

		List<ImageManagementEntity> imageManagementEntities = null;
		JSONObject imageMgtJson = null;
		JSONArray imgRes = new JSONArray();
		ResponseEntity<JSONObject> responseEntity = null;
		try {
			logger.info("Inside getImageManagmentDashboardData Service");
			imageManagementEntities = imageMangemntRepository.findAll();
			if(imageManagementEntities !=null)
			{		
				imageMgtJson = new JSONObject();
				for (ImageManagementEntity entity : imageManagementEntities) {
					JSONObject imgJson = new JSONObject();
					imgJson.put("Id", entity.getId());
					imgJson.put("Vendor", entity.getVendor());
					imgJson.put("Family", entity.getFamily());
					imgJson.put("Image Name", entity.getImageFilename());
					imgJson.put("Display Name", entity.getDisplayName());
					if (entity.getUpdatedDate() != null)
						imgJson.put("Updated On", entity.getUpdatedDate().toString());
					else
						imgJson.put("Updated On", "");
					if (entity.getCreatedDate() != null)
						imgJson.put("Creation Date", entity.getCreatedDate().toString());
					else
						imgJson.put("Creation Date", "");
					imgJson.put("Current Status", entity.isImStatus());
					imgJson.put("createdBy", entity.getCreatedBy());
					imgRes.add(imgJson);
				}
				imageMgtJson.put("total", imgRes.size());
				imageMgtJson.put("result", imgRes);
			}
			
		} catch (InvalidDataAccessResourceUsageException e) {
			logger.error("exception in getImageManagmentDashboardData" + e.getMessage());
		} catch (Exception e) {
			logger.error("exception in getImageManagmentDashboardData" + e.getClass().getName());
		}
		if (imageMgtJson != null) {
			responseEntity = new ResponseEntity<JSONObject>(imageMgtJson, HttpStatus.OK);
		} else {
			responseEntity = new ResponseEntity<JSONObject>(imageMgtJson, HttpStatus.BAD_REQUEST);
		}
		return responseEntity;
	}
	
	@SuppressWarnings("unchecked")
	@POST
	@RequestMapping(value = "/viewBinaryImage", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
	@ResponseBody
	public ResponseEntity<JSONObject> viewBinaryImage(@RequestBody String request) {
		JSONObject imageJson = null;
		JSONParser imageParser = new JSONParser();
		ResponseEntity<JSONObject> responseEntity = null;
		int imageId = 0;
		try {
			JSONObject imageMgtInfo = (JSONObject) imageParser.parse(request);
			if (imageMgtInfo.get("infoId") != null && imageMgtInfo.containsKey("infoId")) {
				imageId = Integer.parseInt(imageMgtInfo.get("infoId").toString());
			}
			ImageManagementEntity imageMgtDetails = imageMangemntRepository.findById(imageId);
			imageJson = new JSONObject();
			if (imageMgtDetails != null) {
				JSONObject imgJson = new JSONObject();
				imgJson.put("vendor", imageMgtDetails.getVendor());
				imgJson.put("family", imageMgtDetails.getFamily());
				imgJson.put("imageName", imageMgtDetails.getImageFilename());
				imgJson.put("displayName", imageMgtDetails.getDisplayName());
				imgJson.put("status", imageMgtDetails.isImStatus());
				imgJson.put("createdBy", imageMgtDetails.getCreatedBy());
				imageJson.put("result", imgJson);
			}
			else
				imageJson.put("result", "No record found");
		} catch (ParseException e) {
			logger.error("Exception occrued in viewBinaryImage" + e.getMessage());
		} catch (Exception e) {
			logger.error("Exception occrued in viewBinaryImage" + e.getClass().getName());
		}
		if (imageJson != null) {
			responseEntity = new ResponseEntity<JSONObject>(imageJson, HttpStatus.OK);
		} else {
			responseEntity = new ResponseEntity<JSONObject>(imageJson, HttpStatus.BAD_REQUEST);
		}
		return responseEntity;
	}
	
	@SuppressWarnings("unchecked")
	@POST
	@RequestMapping(value = "/validateBinaryImage", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
	@ResponseBody
	public ResponseEntity<JSONObject>  validateBinaryImage(@RequestBody String request) {
		JSONObject imageJson = null;
		JSONParser imageParser = new JSONParser();
		String vendor = null, family = null, imageName = null;
		ResponseEntity<JSONObject> responseEntity = null;
		try {
			JSONObject imageMgtInfo = (JSONObject) imageParser.parse(request);
			imageJson = new JSONObject();
			if (imageMgtInfo.get("vendor") != null && imageMgtInfo.containsKey("vendor")) {
				vendor = imageMgtInfo.get("vendor").toString();
			}
			if (imageMgtInfo.get("family") != null && imageMgtInfo.containsKey("family")) {
				family = imageMgtInfo.get("family").toString();
			}
			if (imageMgtInfo.get("imageName") != null && imageMgtInfo.containsKey("imageName")) {
				imageName = imageMgtInfo.get("imageName").toString();
			}	
			imageJson = imageManagementService.validateBinaryImage(vendor, family, imageName);
		} catch (ParseException e) {
			logger.error("Exception occrued in validateBinaryImage" + e.getMessage());
		} catch (Exception e) {
			logger.error("Exception occrued in validateBinaryImage" + e.getMessage());
		}
		if (imageJson != null) {
			responseEntity = new ResponseEntity<JSONObject>(imageJson, HttpStatus.OK);
		} else {
			responseEntity = new ResponseEntity<JSONObject>(imageJson, HttpStatus.BAD_REQUEST);
		}
		return responseEntity;
	}
	
	
	@SuppressWarnings("unchecked")
	@POST
	@RequestMapping(value = "/editBinaryImage", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
	@ResponseBody
	public ResponseEntity<JSONObject> editBinaryImage(@RequestBody String request) {
		JSONObject imageJson = null;
		JSONParser imageParser = new JSONParser();
		ResponseEntity<JSONObject> responseEntity = null;
		String vendor = null, family = null, imageName = null, displayName = null, userName = null;
		boolean status = false;
		try {
			JSONObject imageMgtInfo = (JSONObject) imageParser.parse(request);
			imageJson = new JSONObject();
			if (imageMgtInfo.get("vendor") != null && imageMgtInfo.containsKey("vendor")) {
				vendor = imageMgtInfo.get("vendor").toString();
			}
			if (imageMgtInfo.get("family") != null && imageMgtInfo.containsKey("family")) {
				family = imageMgtInfo.get("family").toString();
			}
			if (imageMgtInfo.get("imageName") != null && imageMgtInfo.containsKey("imageName")) {
				imageName = imageMgtInfo.get("imageName").toString();
			}
			if (imageMgtInfo.get("displayName") != null && imageMgtInfo.containsKey("displayName")) {
				displayName = imageMgtInfo.get("displayName").toString();
			}
			if (imageMgtInfo.get("status") != null && imageMgtInfo.containsKey("status")) {
				status = Boolean.parseBoolean(imageMgtInfo.get("status").toString());
			}
			if (imageMgtInfo.get("userName") != null && imageMgtInfo.containsKey("userName")) {
				userName = imageMgtInfo.get("userName").toString();
			}

			ImageManagementEntity imageMgtDetails = imageMangemntRepository
					.findByVendorAndFamilyAndImageFilename(vendor, family, imageName);
			if(imageMgtDetails !=null)
				imageJson  = imageManagementService.editBinaryImage(imageMgtDetails, vendor, family, imageName, displayName, status, userName);
			else
				imageJson.put("response", "No record found for an update");
			
		} catch (ParseException e) {
			logger.error("Exception occrued in validateImage" + e.getMessage());
		} catch (Exception e) {
			logger.error("Exception occrued in validateImage" + e.getClass().getName());
		}
		if (imageJson != null) {
			responseEntity = new ResponseEntity<JSONObject>(imageJson, HttpStatus.OK);
		} else {
			responseEntity = new ResponseEntity<JSONObject>(imageJson, HttpStatus.BAD_REQUEST);
		}
		return responseEntity;
	}
	
	/**
	 *This Api is marked as ***************Both Api Impacted****************
	 **/
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@POST
	@RequestMapping(value = "/addBinaryImage", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
	@ResponseBody
	public ResponseEntity addBinaryImage(@RequestBody String request) {

		JSONObject imgJson = new JSONObject();
		JSONObject imgJsonRes = new JSONObject();
		JSONParser imgParser = new JSONParser();
		String displayName = null;
		try {
			imgJson = (JSONObject) imgParser.parse(request);
			displayName = (String) imgJson.get("displayName");
			JSONObject success = imageManagementService.addBinaryImage(request);
			if (success.toString().contains("Display Name already exists")) {
				imgJsonRes.put("errorMsg", errorValidationRepository.findByErrorId("C3P_IM_002"));
				imgJsonRes.put("errorType", "Duplicate display name");
				imgJsonRes.put("currentOS", "");
				imgJsonRes.put("upgradableOS", "");
			} else if (success.toString().contains("Duplicate image filename")) {
				imgJsonRes.put("errorMsg", errorValidationRepository.findByErrorId("C3P_IM_001"));
				imgJsonRes.put("errorType", "Duplicate Image filename");
				imgJsonRes.put("currentOS", "");
				imgJsonRes.put("upgradableOS", "");
			} else if (success.toString().contains("A higher version already exist")) {
				imgJsonRes.put("errorMsg", errorValidationRepository.findByErrorId("C3P_IM_003"));
				imgJsonRes.put("errorType", "higher version already exist");
				imgJsonRes.put("currentOS", "");
				imgJsonRes.put("upgradableOS", "");
			} else {
				imgJsonRes.put("errorMsg", "");
				imgJsonRes.put("errorType", "");
				imgJsonRes.put("currentOS", displayName);
				imgJsonRes.put("upgradableOS", "");
				imgJsonRes.put("status", "Added record successfully");
			}
		} catch (ParseException e) {
			logger.error("Exception occrued in addImage" + e.getMessage());
		}
		catch (Exception e) {
			logger.error("Exception occrued in addImage" + e.getMessage());
		}
		return new ResponseEntity<JSONObject>(imgJsonRes, HttpStatus.OK);
	}
}