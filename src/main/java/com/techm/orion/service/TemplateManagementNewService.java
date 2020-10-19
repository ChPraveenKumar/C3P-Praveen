package com.techm.orion.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.techm.orion.dao.TemplateManagementDB;
import com.techm.orion.pojo.GetTemplateMngmntActiveDataPojo;
import com.techm.orion.repositories.ErrorValidationRepository;
import com.techm.orion.repositories.TemplateConfigBasicDetailsRepository;

@Service
public class TemplateManagementNewService {
	@Autowired
	private TemplateConfigBasicDetailsRepository templateConfigBasicDetailsRepository;
	@Autowired
	private ErrorValidationRepository errorValidationRepository;

	public List<GetTemplateMngmntActiveDataPojo> getDataForRightPanelOnEditTemplate(String templateId,
			boolean selectAll) throws Exception {

		TemplateManagementDB templateManagementDB = new TemplateManagementDB();

		List<GetTemplateMngmntActiveDataPojo> templateactiveList = new ArrayList<GetTemplateMngmntActiveDataPojo>();
		templateactiveList = templateManagementDB.getRightPanelOnEditTemplate(templateId, selectAll);

		return templateactiveList;
	}

	/*
	 * Create new addTemplate method for Template name to include 3 more characters
	 */
	public Map<String, String> addTemplate(String vendor, String family, String os, String osVersion, String region) {
		Map<String, String> result = new HashMap<String, String>();
		String tempNumber = null, finalTempId = null, tempId = null;
		try {
			if (vendor != null && family != null && os != null && osVersion != null && region != null) {
				tempId = templateConfigBasicDetailsRepository.createTemplateBasicConfig(os, vendor, family, osVersion,
						region);
				if (tempId != null && !tempId.isEmpty()) {
					tempNumber = tempId.substring(tempId.length() - 2);
					tempNumber = String.format("%02d", Integer.parseInt(tempNumber) + 1);
					if (Integer.parseInt(tempNumber) > 99) {
						tempNumber = "T" + tempNumber;
						finalTempId = tempId.replace(tempId.substring(tempId.length() - 3), tempNumber);
						throw new Exception(errorValidationRepository.findByErrorId("C3P_TM_001"));
					} else {
						tempNumber = "T" + tempNumber;
						finalTempId = tempId.replace(tempId.substring(tempId.length() - 3), tempNumber);
						result.put("status", "success");
						result.put("errorCode", null);
						result.put("errorType", null);
						result.put("errorDescription", null);
						result.put("version", "1.0");
						result.put("tempid", finalTempId);
					}
				} else {
					tempNumber = "T01";
					finalTempId = getTemplateID(vendor, family, os, osVersion, region, tempNumber);
					result.put("status", "success");
					result.put("errorCode", null);
					result.put("errorType", null);
					result.put("errorDescription", null);
					result.put("version", "1.0");
					result.put("tempid", finalTempId);
				}
			}
		} catch (Exception e) {
			result.put("tempid", finalTempId);
			result.put("status", "failure");
			result.put("errorCode", "");
			result.put("errorType", "");
			result.put("errorDescription", e.getMessage());
			result.put("version", "1.0");
		}
		return result;
	}

	public String getTemplateID(String vendor, String family, String os, String osVersion, String region,
			String tempNumber) {
		String temp = null;
		// will be modified once edit flow is enabled have to check version and
		// accordingliy append the version
		if (vendor != null && family != null && os != null && osVersion != null && region != null && tempNumber != null)
			temp = region.toUpperCase().substring(0, 2) + vendor.substring(0, 2).toUpperCase() + family.toUpperCase()
					+ os.substring(0, 2).toUpperCase() + osVersion + tempNumber;
		return temp;
	}
}
