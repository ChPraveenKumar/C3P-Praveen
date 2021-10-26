package com.techm.orion.serviceImpl;

import java.io.File;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.techm.orion.entitybeans.ImageManagementEntity;
import com.techm.orion.repositories.ImageManagementRepository;
import com.techm.orion.rest.ImageManagementController;
import com.techm.orion.service.ImageManagementService;
import com.techm.orion.utility.C3PCoreAppLabels;

@Service
public class ImageManagementServiceImpl implements ImageManagementService {

	private static final Logger logger = LogManager.getLogger(ImageManagementServiceImpl.class);

	@Autowired
	private ImageManagementRepository imageManagementRepository;
	@Autowired
	private ImageManagementEntity firmwareUpgradeSingleDevice;

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

			List<ImageManagementEntity> checkImageNameExist = imageManagementRepository
					.fetchImageFileNamesByVendorAndFamily(vendor, family);
			List<ImageManagementEntity> checkDisplayNameExist = imageManagementRepository
					.fetchDisplayNamesByVendorAndFamily(vendor, family);
			String checkHigherDisplayNameExist = imageManagementRepository.fetchingDisplayNamesByVendorAndFamily(vendor, family);
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
				imageManagementRepository.save(firmwareUpgradeSingleDevice);
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
			imageManagementRepository.save(firmwareUpgradeSingleDevice);
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

			String checkHigherOsVersion = imageManagementRepository.fetchingDisplayNamesByVendorAndFamily(vendor, family);

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

	@SuppressWarnings("unchecked")
	public JSONObject editBinaryImage(ImageManagementEntity imageMgtDetails, String vendor, String family, String imageName,
			String displayName, boolean currentStatus, String userName) {
		JSONObject imageJson = new JSONObject();
		ImageManagementEntity savedRecord = null;
		String displayVersion = null;
		boolean imgStatus = imageMgtDetails.isImStatus();
		displayVersion = imageMgtDetails.getDisplayName();
		int version = compareVersion(displayVersion, displayName);
		if (version == 1) {
			imageJson.put("A higher version already exist", imageMgtDetails.getDisplayName());
		}
		else {
			if (imgStatus) {
				imageMgtDetails.setImStatus(currentStatus);
				imageMgtDetails.setDisplayName(displayName);
				imageMgtDetails.setUpdatedBy(userName);
				imageMgtDetails.setUpdatedDate(Timestamp.valueOf(LocalDateTime.now()));
				savedRecord = imageManagementRepository.save(imageMgtDetails);
			} else {
				File vendorDir = new File(C3PCoreAppLabels.IMAGE_FILE_PATH.getValue() + vendor);
				// Tests whether the vendor Exist or not in the directory.
				boolean vendorExists = vendorDir.isDirectory();
				if (vendorExists) {
					File familyDir = new File(vendorDir.getPath() + C3PCoreAppLabels.FOLDER_SEPARATOR.getValue()+family);
					// Tests whether the family Exist or not in the directory.
					boolean familyExists = familyDir.isDirectory();
					if (familyExists) {
						File imageDir = new File(familyDir.getPath() + C3PCoreAppLabels.FOLDER_SEPARATOR.getValue() + imageName);
						// Tests whether the image Exist or not in the directory.
						boolean isImageExist = imageDir.exists();
						if (isImageExist) {
							imageMgtDetails.setImStatus(currentStatus);
							imageMgtDetails.setDisplayName(displayName);
							imageMgtDetails.setUpdatedBy(userName);
							imageMgtDetails.setUpdatedDate(Timestamp.valueOf(LocalDateTime.now()));
							savedRecord = imageManagementRepository.save(imageMgtDetails);
						} else {
							imageJson.put("response", "The file or folder does not exist");
						}
					} else {
						imageJson.put("response", "The file or folder does not exist");
					}
				}
			}
		}
		if(savedRecord !=null)
			imageJson.put("response", "record updated successfully");
		return imageJson;
	}
	

	@SuppressWarnings("unchecked")
	@Override
	public JSONObject addBinaryImage(String request) {
		JSONObject imageJson = new JSONObject();
		try {
			ImageManagementEntity imageMangemnt =null;
			JSONParser imageParser = new JSONParser();
			String vendor = null,  family = null, imageFilename = null, displayName = null, userName = null;
			int compVal =0;
			imageJson = (JSONObject) imageParser.parse(request);
			if(imageJson.get("vendor") !=null)
				vendor = (String) imageJson.get("vendor");
			if(imageJson.get("family") !=null)
				family = (String) imageJson.get("family");
			if(imageJson.get("imageFilename") !=null)
				imageFilename = (String) imageJson.get("imageFilename");
			if(imageJson.get("displayName") !=null)
				displayName = (String) imageJson.get("displayName");
			if(imageJson.get("userName") !=null)
				userName = (String) imageJson.get("userName");

			ImageManagementEntity isImageNameExist = imageManagementRepository
					.findByVendorAndFamilyAndImageFilename(vendor, family, imageFilename);
			ImageManagementEntity isDisplayNameExist = imageManagementRepository.findByVendorAndFamilyAndDisplayName
					(vendor, family, displayName);
			String higherVersionCheck = imageManagementRepository.fetchingDisplayNamesByVendorAndFamily(vendor, family);
			if(higherVersionCheck !=null)
				compVal = compareVersion(higherVersionCheck, displayName);
			if(isImageNameExist !=null)
			{
				imageJson.put("status","Duplicate image filename");
			}
			else if(isDisplayNameExist !=null)
			{
				imageJson.put("status","Display Name already exists");
			}
			
			else if (compVal == 1) {
				imageJson.put("A higher version already exist", higherVersionCheck);
			}
			else
			{
				imageMangemnt = new ImageManagementEntity();
				imageMangemnt.setFamily(family);
				imageMangemnt.setDisplayName(displayName);
				imageMangemnt.setImageFilename(imageFilename);
				imageMangemnt.setVendor(vendor);
				imageMangemnt.setCreatedBy(userName);;
				imageMangemnt.setCreatedDate(Timestamp.valueOf(LocalDateTime.now()));
				imageMangemnt.setImStatus(true);
				imageManagementRepository.save(imageMangemnt);				
			}
		} catch (Exception e) {
			logger.error("Exception occrued in addBinaryImage" + e.getMessage());
		}
		return imageJson;
	}

	@SuppressWarnings("unchecked")
	@Override
	public JSONObject validateBinaryImage(String vendor, String family, String imageName, String displayName) {
		JSONObject imageJson = new JSONObject();
		try {
			File vendorDir = new File(C3PCoreAppLabels.IMAGE_FILE_PATH.getValue() + vendor);
			// Tests whether the vendor Exist or not in the directory.
			boolean vendorExists = vendorDir.isDirectory();
			if (vendorExists) {
				File familyDir = new File(vendorDir.getPath() + C3PCoreAppLabels.FOLDER_SEPARATOR.getValue() + family);
				// Tests whether the family Exist or not in the directory.
				boolean familyExists = familyDir.isDirectory();
				if (familyExists) {
					File osDir = new File(familyDir.getPath() + C3PCoreAppLabels.FOLDER_SEPARATOR.getValue() + displayName);
					// Tests whether the OS Exist or not in the directory.
					boolean osExists = osDir.isDirectory();
					if (osExists) {
						File imageExist = new File(osDir.getPath() + C3PCoreAppLabels.FOLDER_SEPARATOR.getValue() + imageName);
						// Tests whether the image Exist or not in the directory.
						boolean isImageExist = imageExist.exists();
						if (isImageExist) {
							imageJson.put("response", "File exists");
						} else
							imageJson.put("response", "The file does not exist");
					} else {
						imageJson.put("response", "The file or folder does not exist");
					}
				} else {
					imageJson.put("response", "The file or folder does not exist");
				}
			} else {
				imageJson.put("response", "The file or folder does not exist");
			}
		} catch (Exception e) {
			logger.error("Exception occured in validateBinaryImage" + e.getMessage());
			imageJson.put("response", e.getMessage());
		}
		return imageJson;
	}
	
	private int compareSubstrVersion(String substr_version1, String substr_version2) {
		int len_substr_version1 = substr_version1.length();
		int len_substr_version2 = substr_version1.length();

		if (len_substr_version1 > len_substr_version2)
			return 1;
		else if (len_substr_version2 < len_substr_version1)
			return -1;

		int result = substr_version1.compareTo(substr_version2);
		if (result > 0)
			return 1;
		else if (result < 0)
			return -1;
		return 0;
	}

	// Function to compare two versions.
	private int compareVersion(String version1, String version2) {
		String substr_version1[] = version1.split("[.]");
		String substr_version2[] = version2.split("[.]");

		int len_version1 = substr_version1.length;
		int len_version2 = substr_version2.length;
		int lengthVer1 = 0;
		int lengthVer2 = 0;
		// Loop until both strings are exhausted.
		// and extract the substrings from version1
		// and version2
		while (lengthVer1 < len_version1 || lengthVer2 < len_version2) {
			String versionSubstr1 = "";
			String versionSubstr2 = "";
			if (lengthVer1 < len_version1) {
				// Skip the leading zeros in
				// version1 string.
				if (substr_version1[lengthVer1].charAt(0) == '0') {
					int len = substr_version1[lengthVer1].length();
					int substrPos = 0;
					while (substrPos < len && substr_version1[lengthVer1].charAt(substrPos) == '0') {
						substrPos++;
					}
					versionSubstr1 += substr_version1[lengthVer1].substring(substrPos);
				} else
					versionSubstr1 += substr_version1[lengthVer1];
			}
			if (lengthVer2 < len_version2) {
				// Skip the leading zeros in version2 string.
				if (substr_version2[lengthVer1].charAt(0) == '0') {
					int len = substr_version2[lengthVer1].length();
					int substrPos = 0;

					while (substrPos < len && substr_version2[lengthVer1].charAt(substrPos) == '0') {
						substrPos++;
					}
					versionSubstr2 += substr_version2[lengthVer1].substring(substrPos);
				} else
					versionSubstr2 = substr_version2[lengthVer1];
			}
			// If res is either -1 or +1
			// then simply return.
			int res = compareSubstrVersion(versionSubstr1, versionSubstr2);
			if (res != 0)
				return res;

			lengthVer1++;
			lengthVer2++;
		}
		// Here both versions are exhausted
		// it implicitly means that both
		// strings are equal.
		return 0;
	}
}