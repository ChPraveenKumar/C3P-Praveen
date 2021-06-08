package com.techm.orion.rest;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import javax.ws.rs.POST;
import javax.ws.rs.core.Response;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.simple.JSONObject;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.google.gson.Gson;
import com.techm.orion.models.TemplateVersioningJSONModel;
import com.techm.orion.pojo.SearchParamPojo;
import com.techm.orion.pojo.TemplateBasicConfigurationPojo;
import com.techm.orion.service.TemplateManagementDetailsService;

@Controller
@RequestMapping("/SearchTemplateList")
@CrossOrigin(origins = "http://localhost:4200", maxAge = 3600)
public class SearchTemplateListService implements Observer {
	private static final Logger logger = LogManager.getLogger(SearchTemplateListService.class);

	TemplateManagementDetailsService service = new TemplateManagementDetailsService();

	/**
	 *This Api is marked as ***************c3p-ui Api Impacted****************
	 **/
	@POST
	@RequestMapping(value = "/search", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
	@ResponseBody
	public Response search(@RequestBody String searchParameters) {

		JSONObject obj = new JSONObject();
		String jsonMessage = "";
		String jsonArray = "";
		String jsonArrayReports = "";
		String key = null, value = null;

		try {
			Gson gson = new Gson();
			SearchParamPojo dto = gson.fromJson(searchParameters, SearchParamPojo.class);
			key = dto.getKey();
			value = dto.getValue().replace("-", "_");
			List<TemplateBasicConfigurationPojo> detailsList = new ArrayList<TemplateBasicConfigurationPojo>();
			if (value != null && !value.isEmpty()) {
				try {
					// quick fix for json not getting serialized

					detailsList = service.searchTemplates(key, value);
					if (detailsList.size() > 0) {
						List<TemplateVersioningJSONModel> versioningModel = new ArrayList<TemplateVersioningJSONModel>();
						List<TemplateBasicConfigurationPojo> versioningModelChildList = new ArrayList<TemplateBasicConfigurationPojo>();
						TemplateBasicConfigurationPojo objToAdd;
						TemplateVersioningJSONModel versioningModelObject = null;

						// create treeview json
						for (int i = 0; i < detailsList.size(); i++) {
							boolean objectPrsent = false;
							if (versioningModel.size() > 0) {
								for (int j = 0; j < versioningModel.size(); j++) {
									if (versioningModel.get(j).getTemplateId()
											.equalsIgnoreCase(detailsList.get(i).getTemplateId())) {
										objectPrsent = true;
										break;
									}
								}
							}
							if (objectPrsent == false) {
								versioningModelObject = new TemplateVersioningJSONModel();
								objToAdd = new TemplateBasicConfigurationPojo();
								objToAdd = detailsList.get(i);
								versioningModelObject.setTemplateId(objToAdd.getTemplateId());
								versioningModelObject.setVendor(objToAdd.getVendor());
								versioningModelObject.setRegion(objToAdd.getRegion());
								versioningModelObject.setModel(objToAdd.getModel());
								versioningModelObject.setDeviceFamily(objToAdd.getDeviceFamily());
								versioningModelObject.setDeviceOsVersion(objToAdd.getOsVersion());
								versioningModelObject.setDeviceOs(objToAdd.getDeviceOs());
								if (objToAdd.getComment() == null) {
									versioningModelObject.setComment("");
								} else {
									versioningModelObject.setComment(objToAdd.getComment());
								}
								versioningModelObject.setApprover(objToAdd.getApprover());
								versioningModelObject.setStatus(objToAdd.getStatus());
								versioningModelObject.setCreatedBy(objToAdd.getCreatedBy());
								versioningModelChildList = new ArrayList<TemplateBasicConfigurationPojo>();
								for (int k = 0; k < detailsList.size(); k++) {
									if (detailsList.get(k).getTemplateId()
											.equalsIgnoreCase(versioningModelObject.getTemplateId())) {
										versioningModelChildList.add(detailsList.get(k));
									}
								}
								Collections.reverse(versioningModelChildList);
								versioningModelChildList.get(0).setEnabled(objToAdd.isEditable());
								versioningModelObject.setChildList(versioningModelChildList);
								versioningModel.add(versioningModelObject);

							}

						}
						jsonArray = new Gson().toJson(versioningModel);
						obj.put(new String("output"), jsonArray);
						obj.put(new String("templateCount"), versioningModel.size());
						obj.put(new String("Result"), "success");

					} else {
						obj.put(new String("output"), jsonArray);
						obj.put(new String("templateCount"), detailsList.size());
						obj.put(new String("Result"), "failure");

					}

				} catch (Exception e) {
					logger.error(e);
				}
			}
		} catch (Exception e) {
			logger.error(e);
		}

		return Response.status(200).header("Access-Control-Allow-Origin", "*")
				.header("Access-Control-Allow-Headers", "origin, content-type, accept, authorization")
				.header("Access-Control-Allow-Credentials", "true")
				.header("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS, HEAD")
				.header("Access-Control-Max-Age", "1209600").entity(obj).build();
	}

	@Override
	public void update(Observable o, Object arg) {
		// TODO Auto-generated method stub

	}

}
