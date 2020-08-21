package com.techm.orion.camunda.servicelayer;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import javax.ws.rs.POST;
import javax.ws.rs.core.Response;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONException;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.google.gson.Gson;
import com.techm.orion.dao.TemplateManagementDao;
import com.techm.orion.pojo.SearchParamPojo;
import com.techm.orion.pojo.TemplateBasicConfigurationPojo;
import com.techm.orion.rest.CamundaServiceTemplateApproval;
import com.techm.orion.rest.GetTemplateConfigurationData;

@Controller
@RequestMapping("/createTemplate")
public class TemplateApprovalWorkflowService implements Observer {
	private static final Logger logger = LogManager.getLogger(TemplateApprovalWorkflowService.class);

	@POST
	@RequestMapping(value = "/saveTemplate", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
	@ResponseBody
	public Response saveTemplate(@RequestBody String string) {
		GetTemplateConfigurationData templateSaveFlowService = new GetTemplateConfigurationData();
		CamundaServiceTemplateApproval camundaService = new CamundaServiceTemplateApproval();
		JSONParser parser = new JSONParser();
		String templateId = null, templateVersion = null;
		Response response = null;
		DecimalFormat numberFormat = new DecimalFormat("#.0");

		try {
			JSONObject json = (JSONObject) parser.parse(string);
			templateId = json.get("templateid").toString();
			if (json.get("templateVersion") != null) {

				templateVersion = numberFormat.format(Double.parseDouble(json.get("templateVersion").toString()));

			} else {
				templateVersion = templateId.substring(templateId.indexOf("V") + 1, templateId.length());
				templateId = templateId.substring(0, templateId.indexOf("V") - 1);
			}
			response = templateSaveFlowService.saveConfigurationTemplate(string, templateId, templateVersion);
			camundaService.initiateApprovalFlow(templateId, templateVersion, "Admin");
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return response;
	}

	@POST
	@RequestMapping(value = "/updateTemplateStatus", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
	@ResponseBody
	public Response updateTemplateStatus(@RequestBody String string) {
		JSONObject obj = new JSONObject();

		TemplateManagementDao templateSaveFlowService = new TemplateManagementDao();
		CamundaServiceTemplateApproval camundaService = new CamundaServiceTemplateApproval();
		JSONParser parser = new JSONParser();
		String templateId = null, templateVersion = null, status = null, approverComment = null;
		String userTaskId = null;
		int response = 0;
		try {
			JSONObject json = (JSONObject) parser.parse(string);
			templateId = json.get("templateid").toString().replace("-", "_");
			if (json.get("templateVersion") != null) {
				templateVersion = (json.get("templateVersion").toString());

			} else {
				templateVersion = templateId.substring(templateId.indexOf("V") + 1, templateId.length());
				templateId = templateId.substring(0, templateId.indexOf("V") - 1);

			}
			status = json.get("status").toString();
			approverComment = json.get("comment").toString();
			response = templateSaveFlowService.updateTemplateStatus(templateId, templateVersion, status,
					approverComment);
			userTaskId = templateSaveFlowService.getUserTaskIdForTemplate(templateId, templateVersion);
			camundaService.completeApprovalFlow(userTaskId, status, approverComment);
			// camundaService.initiateApprovalFlow(templateId, templateVersion, "Admin");
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (response >= 1) {
			obj.put(new String("Status"), "success");
		} else {
			obj.put(new String("Status"), "failure");

		}
		return Response.status(200).header("Access-Control-Allow-Origin", "*")
				.header("Access-Control-Allow-Headers", "origin, content-type, accept, authorization")
				.header("Access-Control-Allow-Credentials", "true")
				.header("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS, HEAD")
				.header("Access-Control-Max-Age", "1209600").entity(obj).build();
	}

	@POST
	@RequestMapping(value = "/search", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
	@ResponseBody
	public Response search(@RequestBody String searchParameters) {

		JSONObject obj = new JSONObject();
		String jsonMessage = "";
		String jsonArray = "";
		String jsonArrayReports = "";
		String key = null, value = null;
		TemplateManagementDao templateSaveFlowService = new TemplateManagementDao();

		try {
			Gson gson = new Gson();
			SearchParamPojo dto = gson.fromJson(searchParameters, SearchParamPojo.class);
			key = dto.getKey();
			value = dto.getValue();
			List<TemplateBasicConfigurationPojo> detailsList = new ArrayList<TemplateBasicConfigurationPojo>();
			if (value != null && !value.isEmpty()) {
				try {
					// quick fix for json not getting serialized

					detailsList = templateSaveFlowService.searchResults(key, value);

					jsonArray = new Gson().toJson(detailsList);
					obj.put(new String("output"), jsonArray);
					obj.put(new String("Result"), "success");

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
	public void update(Observable arg0, Object arg1) {
		// TODO Auto-generated method stub

	}

}
