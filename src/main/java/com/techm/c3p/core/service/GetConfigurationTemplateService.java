package com.techm.c3p.core.service;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.techm.c3p.core.dao.RequestInfoDao;
import com.techm.c3p.core.pojo.CreateConfigRequestDCM;
import com.techm.c3p.core.pojo.RequestInfoPojo;
import com.techm.c3p.core.utility.C3PCoreAppLabels;
import com.techm.c3p.core.utility.InvokeFtl;

@Component
public class GetConfigurationTemplateService {

	@Autowired
	private DcmConfigService dcmConfigService;
	
	@Autowired
	private RequestInfoDao requestInfoDao;
	
	public String generateTemplate(CreateConfigRequestDCM configRequest) {

		String response = null;
		InvokeFtl invokeFtl = new InvokeFtl();
		
		List<String> listOfTemplatesAvailable = new ArrayList<String>();
		boolean isTemplateAvailable = false;
		String fileToUse = null;
		// create the file
		try {

			if (null == configRequest.getTemplateID() || configRequest.getTemplateID().isEmpty())

			{
				String templateID = dcmConfigService.getTemplateName(configRequest.getRegion(),
						configRequest.getVendor(), configRequest.getModel(), configRequest.getOs(),
						configRequest.getOsVersion());
				configRequest.setTemplateID(templateID);
				// String responseHeader= invokeFtl.generateheader(configRequest);

				// open folder for template and read all available templatenames				
				final File folder = new File(getTemplateCreationPathForFolder());
				listOfTemplatesAvailable = dcmConfigService.listFilesForFolder(folder);
				if (listOfTemplatesAvailable.size() > 0) {
					String tempString = null;
					for (int i = 0; i < listOfTemplatesAvailable.size(); i++) {
						tempString = listOfTemplatesAvailable.get(i).substring(0,
								listOfTemplatesAvailable.get(i).indexOf("_V"));
						if (tempString.equalsIgnoreCase(templateID)) {
							isTemplateAvailable = true;
							break;
						}
					}
					float highestVersion = 0, tempVersion = 0;
					String tempToUseTemp = null;
					if (isTemplateAvailable) {
						for (int i = 0; i < listOfTemplatesAvailable.size(); i++) {
							tempString = listOfTemplatesAvailable.get(i).substring(0,
									listOfTemplatesAvailable.get(i).indexOf("_V"));
							if (tempString.equalsIgnoreCase(templateID)) {
								if (highestVersion == 0) {
									highestVersion = Float.parseFloat(listOfTemplatesAvailable.get(i).substring(
											listOfTemplatesAvailable.get(i).indexOf("_V") + 2,
											listOfTemplatesAvailable.get(i).length()));
								} else {
									tempVersion = Float.parseFloat(listOfTemplatesAvailable.get(i).substring(
											listOfTemplatesAvailable.get(i).indexOf("_V") + 2,
											listOfTemplatesAvailable.get(i).length()));
									if (tempVersion > highestVersion) {
										highestVersion = tempVersion;
									}
								}
								// break;
							}
						}
						fileToUse = templateID + "_V" + highestVersion;
						// isTemplateApproved=templateDao.getTemplateStatus(templateID,Float.toString(highestVersion));

					}
					// isTemplateApproved=templateDao.getTemplateStatus(tempString,Float.toString(highestVersion));

				}
			} else {
				fileToUse = configRequest.getTemplateID();

			}
			String responseHeader = invokeFtl.generateheader(configRequest);
			response = invokeFtl.generateConfigurationToPush(configRequest, fileToUse).replace("config", "");
			response = responseHeader.concat("\r\n").concat(response);
			if (response == null) {
				response = "Configuration not generated";
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return response;

	}

	public String getTemplateOnModify(CreateConfigRequestDCM configRequest) {
		CreateAndCompareModifyVersion createAndCompareModifyVersion = new CreateAndCompareModifyVersion();
		
		String requestIdForConfig = "";
		InvokeFtl invokeFtl = new InvokeFtl();
		String responseForTemplate = "";
		try {

			// validateMessage=requestInfoSO.getProcessID();

			Map<String, String> resultForFlag = new HashMap<String, String>();
			resultForFlag = requestInfoDao.getRequestFlag(configRequest.getRequestId(),
					configRequest.getRequest_version());
			String flagForPrevalidation = "";
			String flagFordelieverConfig = "";
			for (Map.Entry<String, String> entry : resultForFlag.entrySet()) {
				if (entry.getKey() == "flagForPrevalidation") {
					flagForPrevalidation = entry.getValue();

				}
				if (entry.getKey() == "flagFordelieverConfig") {
					flagFordelieverConfig = entry.getValue();
				}

			}
			configRequest.setFlagForPrevalidation(flagForPrevalidation);
			configRequest.setFlagFordelieverConfig(flagFordelieverConfig);
			// createAndCompareModifyVersion.CompareModifyVersion(requestIdForConfig);
			if (flagForPrevalidation.equalsIgnoreCase("1") && flagFordelieverConfig.equalsIgnoreCase("1")) {
				// compare the last two version to create file(no cmds+new cmds)
				String noCmdResponse = createAndCompareModifyVersion.CompareModifyVersionForTemplate(
						configRequest.getRequestId(), "templateGenerate", configRequest);
				String responseHeader = invokeFtl.generateheader(configRequest);
				if (noCmdResponse != "" && !noCmdResponse.isEmpty()) {

					responseForTemplate = responseHeader.concat("\r\n").concat(noCmdResponse);
				} else {
					responseForTemplate = responseHeader.concat("\r\n").concat("NoChanges");
				}

			}

			else {
				responseForTemplate = generateTemplate(configRequest);

			}

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return responseForTemplate;

	}

	/* Dhanshri Mane */
	// method overloading ,logic same added for UIRevamp
	public String generateTemplate(RequestInfoPojo configRequest) {
		String response = null;
		InvokeFtl invokeFtl = new InvokeFtl();
		
		List<String> listOfTemplatesAvailable = new ArrayList<String>();
		boolean isTemplateAvailable = false;
		String fileToUse = null;
		// create the file
		try {

			if (null == configRequest.getTemplateID() || configRequest.getTemplateID().isEmpty())

			{
				String templateID = dcmConfigService.getTemplateName(configRequest.getRegion(),
						configRequest.getVendor(), configRequest.getModel(), configRequest.getOs(),
						configRequest.getOsVersion());
				configRequest.setTemplateID(templateID);
				// String responseHeader= invokeFtl.generateheader(configRequest);
				final File folder = new File(getTemplateCreationPathForFolder());
				listOfTemplatesAvailable = dcmConfigService.listFilesForFolder(folder);
				if (listOfTemplatesAvailable.size() > 0) {
					String tempString = null;
					for (int i = 0; i < listOfTemplatesAvailable.size(); i++) {
						tempString = listOfTemplatesAvailable.get(i).substring(0,
								listOfTemplatesAvailable.get(i).indexOf("_V"));
						if (tempString.equalsIgnoreCase(templateID)) {
							isTemplateAvailable = true;
							break;
						}
					}
					float highestVersion = 0, tempVersion = 0;
					String tempToUseTemp = null;
					if (isTemplateAvailable) {
						for (int i = 0; i < listOfTemplatesAvailable.size(); i++) {
							tempString = listOfTemplatesAvailable.get(i).substring(0,
									listOfTemplatesAvailable.get(i).indexOf("_V"));
							if (tempString.equalsIgnoreCase(templateID)) {
								if (highestVersion == 0) {
									highestVersion = Float.parseFloat(listOfTemplatesAvailable.get(i).substring(
											listOfTemplatesAvailable.get(i).indexOf("_V") + 2,
											listOfTemplatesAvailable.get(i).length()));
								} else {
									tempVersion = Float.parseFloat(listOfTemplatesAvailable.get(i).substring(
											listOfTemplatesAvailable.get(i).indexOf("_V") + 2,
											listOfTemplatesAvailable.get(i).length()));
									if (tempVersion > highestVersion) {
										highestVersion = tempVersion;
									}
								}
								// break;
							}
						}
						fileToUse = templateID + "_V" + highestVersion;
						// isTemplateApproved=templateDao.getTemplateStatus(templateID,Float.toString(highestVersion));

					}
					// isTemplateApproved=templateDao.getTemplateStatus(tempString,Float.toString(highestVersion));

				}
			} else {
				fileToUse = configRequest.getTemplateID();

			}
			String responseHeader = invokeFtl.generateheader(configRequest);
			//Owner: Ruchita Salvi Comment: This if{} needs to be removed once master table is implemented for vendors
			/*if(configRequest.getVendor().equalsIgnoreCase("cisco"))
			{
				response = invokeFtl.generateConfigurationToPush(configRequest, fileToUse).replace("config t", "");
			}
			else
			{*/
				response = invokeFtl.generateConfigurationToPush(configRequest, fileToUse);
				

//			}
			response = responseHeader.concat("\r\n").concat(response);
			if (response == null) {
				response = "Configuration not generated";
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return response;

	}
	
	private String getTemplateCreationPathForFolder() {
		String path = C3PCoreAppLabels.NEW_TEMPLATE_CREATION_PATH.getValue(); 
		if (path.charAt(path.length()-1) == '\\' || path.charAt(path.length()-1) == '/'){
			path = path.substring(0, path.length()-1);
	    }
		 return path;
	}
}
