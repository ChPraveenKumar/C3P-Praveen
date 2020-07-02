package com.techm.orion.serviceImpl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.techm.orion.entitybeans.FirmwareUpgradeSingleDeviceEntity;
import com.techm.orion.repositories.FirmUpgradeSingleDeviceRepository;
import com.techm.orion.service.FirmwareUpgradeSingleDeviceInterface;

@Service
public class FirmwareUpgradeSingleDeviceImpl implements FirmwareUpgradeSingleDeviceInterface {

	@Autowired
	FirmUpgradeSingleDeviceRepository firmupgradeRepository;
	@Autowired
	private FirmwareUpgradeSingleDeviceEntity firmwareUpgradeSingleDevice;

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
	@Override
	public Map<String, String> addFirmWare(String firmwareData) throws Exception {
		Map<String, String> map = new HashMap<String, String>();
		try {

			JSONParser parser = new JSONParser();
			String vendor = "", imageFilename = "", displayName = "", family = "";

			JSONObject json = (JSONObject) parser.parse(firmwareData);
			vendor = (String) json.get("vendor");
			family = (String) json.get("family");
			imageFilename = (String) json.get("imageFilename");
			displayName = (String) json.get("displayName");

			List<FirmwareUpgradeSingleDeviceEntity> checkImageNameExist = firmupgradeRepository
					.checkDuplicateImage(vendor, family);
			List<FirmwareUpgradeSingleDeviceEntity> checkDisplayNameExist = firmupgradeRepository
					.checkDuplicateDisplayName(vendor, family);
			String checkHigherDisplayNameExist = firmupgradeRepository.checkHigherDisplayName(vendor, family);
			for (Object imageNameExist : checkImageNameExist) {
				if (imageNameExist.toString().equalsIgnoreCase(imageFilename))
					throw new Exception("Image already exists");
			}

			for (Object displayNameExist : checkDisplayNameExist) {
				if (displayNameExist.toString().equalsIgnoreCase(displayName))
					throw new Exception("Display Name already exists");
			}

			if (checkHigherDisplayNameExist != null
					&& checkHigherDisplayNameExist.toString().compareToIgnoreCase(displayName.toString()) > 1)
				map.put("HigherVersionExist", checkHigherDisplayNameExist);
			else {
				firmwareUpgradeSingleDevice.setId(0);
				firmwareUpgradeSingleDevice.setFamily(family);
				firmwareUpgradeSingleDevice.setDisplayName(displayName);
				firmwareUpgradeSingleDevice.setImageFilename(imageFilename);
				firmwareUpgradeSingleDevice.setVendor(vendor);
				firmupgradeRepository.save(firmwareUpgradeSingleDevice);
				map.put("responseResult", "Added record successfully");
			}
		} catch (Exception e) {
			map.put("error", e.toString());
		}
		return map;
	}

	/*
	 * Add record for single firmupgrade Device with Below validations will be
	 * applicable If user confirms (from UI) , that he wants to add the lower image,
	 * then it should get added in the database table.
	 */
	@Override
	public Map<String, String> addFirmWareLowerImage(String firmwareData) throws Exception {

		Map<String, String> map = new HashMap<String, String>();
		try {
			JSONParser parser = new JSONParser();
			String vendor = "", imageFilename = "", displayName = "", family = "";

			JSONObject json = (JSONObject) parser.parse(firmwareData);
			vendor = (String) json.get("vendor");
			family = (String) json.get("family");
			imageFilename = (String) json.get("imageFilename");
			displayName = (String) json.get("displayName");

			firmwareUpgradeSingleDevice.setId(0);
			firmwareUpgradeSingleDevice.setFamily(family);
			firmwareUpgradeSingleDevice.setDisplayName(displayName);
			firmwareUpgradeSingleDevice.setImageFilename(imageFilename);
			firmwareUpgradeSingleDevice.setVendor(vendor);
			firmupgradeRepository.save(firmwareUpgradeSingleDevice);
			map.put("responseResult", "Lower Image Added Successfully");
		} catch (Exception e) {
			map.put("error", e.toString());
		}
		return map;
	}

	/*
	 * When user upgrades a Device from UI, this API will provide value for field
	 * “Update OS” by searching for highest available OS version (from column
	 * Display Name) against the selected device’s vendor and device model.
	 */
	@Override
	public Map<String, String> findHighestOsVersion(String firmwareData) throws Exception {

		Map<String, String> map = new HashMap<String, String>();
		try {
			JSONParser parser = new JSONParser();
			String vendor = "", osversion = "", family = "";

			JSONObject json = (JSONObject) parser.parse(firmwareData);
			vendor = (String) json.get("vendor");
			family = (String) json.get("family");
			osversion = (String) json.get("osversion");

			String checkHigherOsVersion = firmupgradeRepository.checkHigherOsVersion(vendor, family);

			if (checkHigherOsVersion != null
					&& checkHigherOsVersion.toString().compareToIgnoreCase(osversion.toString()) >= 1)
				map.put("highestOsVersion", checkHigherOsVersion);
			else if (checkHigherOsVersion != null
					&& checkHigherOsVersion.toString().compareToIgnoreCase(osversion.toString()) < 1)
				map.put("alreadyHigherVersionExist", "Already higher version");
			else
				map.put("statusMsg", "No record found");
		} catch (Exception e) {
			map.put("error", e.toString());
		}
		return map;
	}
}
