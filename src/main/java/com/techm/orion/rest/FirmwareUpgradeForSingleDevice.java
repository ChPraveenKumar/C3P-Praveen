package com.techm.orion.rest;

import java.util.Map;

import javax.ws.rs.POST;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.techm.orion.service.FirmwareUpgradeSingleDeviceInterface;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping({ "/firmwareUpgradeForSingleDevice" })
public class FirmwareUpgradeForSingleDevice {
	private static final Logger logger = LogManager.getLogger(FinalReportForTTUTest.class);

	@Autowired
	private FirmwareUpgradeSingleDeviceInterface firmwareInterface;

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
			Map<String, String> success = firmwareInterface.addFirmWare(searchParameters);
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

	@POST
	@RequestMapping(value = "/addFirmwareUpgradeForSingleDeviceLowerImage", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
	@ResponseBody
	public ResponseEntity addFirmwareUpgradeForSingleDeviceHigherVersion(@RequestBody String searchParameters) {

		JSONObject obj = new JSONObject();
		JSONParser parser = new JSONParser();
		JSONObject json = new JSONObject();
		try {
			json = (JSONObject) parser.parse(searchParameters);
			Map<String, String> success = firmwareInterface.addFirmWareLowerImage(searchParameters);
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
	@POST
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
			Map<String, String> success = firmwareInterface.findHighestOsVersion(searchParameters);

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

}
