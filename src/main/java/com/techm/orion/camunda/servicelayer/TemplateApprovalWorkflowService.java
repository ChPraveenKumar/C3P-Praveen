package com.techm.orion.camunda.servicelayer;

import java.io.IOException;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.time.LocalDateTime;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.google.gson.Gson;
import com.techm.orion.dao.TemplateManagementDao;
import com.techm.orion.entitybeans.MasterFeatureEntity;
import com.techm.orion.entitybeans.TemplateFeatureEntity;
import com.techm.orion.pojo.SearchParamPojo;
import com.techm.orion.pojo.TemplateBasicConfigurationPojo;
import com.techm.orion.repositories.MasterFeatureRepository;
import com.techm.orion.repositories.TemplateFeatureRepo;
import com.techm.orion.rest.CamundaServiceTemplateApproval;
import com.techm.orion.rest.GetTemplateConfigurationData;

@Controller
@RequestMapping("/createTemplate")
public class TemplateApprovalWorkflowService implements Observer {
	private static final Logger logger = LogManager.getLogger(TemplateApprovalWorkflowService.class);

	@Autowired
	private MasterFeatureRepository masterFeatureRepository;
	
	@Autowired
	private TemplateFeatureRepo templateFeatureRepo;

	
	@POST
	@RequestMapping(value = "/saveTemplate", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
	@ResponseBody
	public ResponseEntity<JSONObject> saveTemplate(@RequestBody String string) {
		GetTemplateConfigurationData templateSaveFlowService = new GetTemplateConfigurationData();
		CamundaServiceTemplateApproval camundaService = new CamundaServiceTemplateApproval();
		JSONParser parser = new JSONParser();
		String templateId = null, templateVersion = null;
		ResponseEntity<JSONObject> response = null;
		DecimalFormat numberFormat = new DecimalFormat("#.0");

		try {
			JSONObject json = (JSONObject) parser.parse(string);
			templateId = json.get("templateid").toString();
			templateId = templateId.replace("-", "_");
			if (json.get("templateVersion") != null) {
				templateVersion = numberFormat.format(Double.parseDouble(json.get("templateVersion").toString()));
			} else {
				templateVersion = templateId.substring(templateId.indexOf("_V")+2, templateId.length());
				templateId = templateId.substring(0, templateId.indexOf("_V"));
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

	@SuppressWarnings("unchecked")
	@POST
	@RequestMapping(value = "/updateTemplateStatus", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
	@ResponseBody
	public Response updateTemplateStatus(@RequestBody String string) {
		JSONObject obj = new JSONObject();

		TemplateManagementDao templateSaveFlowService = new TemplateManagementDao();
		CamundaServiceTemplateApproval camundaService = new CamundaServiceTemplateApproval();
		JSONParser parser = new JSONParser();
		String templateId = null, templateVersion = null, status = null, approverComment = null,featureID=null, featureVersion=null;
		String userTaskId = null;
		DecimalFormat numberFormat = new DecimalFormat("#.0");

		int response = 0;
		try {
			JSONObject json = (JSONObject) parser.parse(string);
			if(json.containsKey("status") && !json.get("status").toString().isEmpty())
			{
				status = json.get("status").toString();
			}
			else
			{
				status="";
			}
			if(json.containsKey("comment") && !json.get("comment").toString().isEmpty())
			{
				approverComment = json.get("comment").toString();
			}
			else
			{
				approverComment="";
			}
			if (Boolean.parseBoolean(json.get("isTemplate").toString())) {
				templateId = json.get("templateid").toString().replace("-", "_");
				String templateidForFeatureExtraction=templateId;
				if (json.get("templateVersion") != null) {
					templateVersion = (json.get("templateVersion").toString());
				} else {
					templateVersion = templateId.substring(templateId.indexOf("_V")+2, templateId.length());
					templateId = templateId.substring(0, templateId.indexOf("_V"));

				}
				
				//get feature id based of command type
				List<TemplateFeatureEntity>listFeatures=templateFeatureRepo.findMasterFIdByCommand(templateidForFeatureExtraction);
				
				listFeatures.forEach(feature -> {
					masterFeatureRepository.updateMasterFeatureStatus(json.get("status").toString(), json.get("comment").toString() , "Admin", "Suser",Timestamp.valueOf(LocalDateTime.now()), feature.getMasterFId(), "1");

				});
				response = templateSaveFlowService.updateTemplateStatus(templateId, templateVersion, status,
						approverComment);
				userTaskId = templateSaveFlowService.getUserTaskIdForTemplate(json.get("templateid").toString().replace("-", "_"), templateVersion);
				camundaService.completeApprovalFlow(userTaskId, status, approverComment);
			}
			else
			{
				/*In case of feature*/
				featureID=json.get("featureid").toString();
				
				MasterFeatureEntity entity=masterFeatureRepository.findByFId(featureID);
				String comment=null;
				String str=entity.getfComments();
				if(str!=null)
				{
					comment=entity.getfComments().concat(approverComment);

				}
				else
				{
					comment=approverComment;
				}
				featureVersion = numberFormat.format(Double.parseDouble(json.get("featureversion").toString()));
				
				response=masterFeatureRepository.updateMasterFeatureStatus(status, comment , "Admin", "Suser",Timestamp.valueOf(LocalDateTime.now()), featureID, featureVersion);
				
				userTaskId = templateSaveFlowService.getUserTaskIdForTemplate(featureID, featureVersion);
				
				
				camundaService.completeApprovalFlow(userTaskId, status, approverComment);
				
			}
			
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

	@SuppressWarnings("unchecked")
	@POST
	@RequestMapping(value = "/search", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
	@ResponseBody
	public Response search(@RequestBody String searchParameters) {

		JSONObject obj = new JSONObject();
		String jsonArray = "";
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
