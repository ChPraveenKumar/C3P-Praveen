package com.techm.orion.rest;

import java.io.IOException;
import java.util.Arrays;

import javax.ws.rs.POST;

import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONException;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.client.RestTemplate;

import com.google.gson.Gson;
import com.techm.orion.dao.RequestInfoDetailsDao;
import com.techm.orion.entitybeans.RfoDecomposedEntity;
import com.techm.orion.pojo.RequestInfoPojo;
import com.techm.orion.repositories.RfoDecomposedRepository;
import com.techm.orion.service.VnfInstantiationMilestoneService;
import com.techm.orion.utility.TSALabels;

@Controller
@RequestMapping("/Instantiation")
public class InstantiationMilestone extends Thread {
	private static final Logger logger = LogManager.getLogger(InstantiationMilestone.class);

	@Autowired
	private RequestInfoDetailsDao requestDao;	
	@Autowired
	private RestTemplate restTemplate;
	@Autowired
	private RfoDecomposedRepository rfoDecomposedRepo;

	/**
	 *This Api is marked as ***************Both Api Impacted****************
	 **/
	@POST
	@RequestMapping(value = "/performInstantiation", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
	@ResponseBody
	public JSONObject performInstantiation(@RequestBody String request) throws ParseException {

		JSONObject obj = new JSONObject();
		String jsonArray = "";
		Boolean value = false;

		JSONParser parser = new JSONParser();
		JSONObject json = (JSONObject) parser.parse(request);

		String requestId = json.get("requestId").toString();
		String version = json.get("version").toString();

		String type = requestId.substring(0, Math.min(requestId.length(), 4));
		
		if (("SNAI").equalsIgnoreCase(type)) {
			RequestInfoPojo requestinfo = new RequestInfoPojo();

			requestinfo = requestDao.getRequestDetailTRequestInfoDBForVersion(
					requestId, version);
			//Call python API for Instantiation
			requestDao.editRequestforReportWebserviceInfo(
					requestinfo.getAlphanumericReqId(),
					Double.toString(requestinfo
							.getRequestVersion()),
					"instantiation", "4", "In Progress");
			VnfInstantiationMilestoneService service=new VnfInstantiationMilestoneService();
			try {
				JSONObject output=service.callPython(requestId, version);
				String out=output.get("workflow_status").toString();
				if(out.equalsIgnoreCase("true"))
				{
					value=true;
				}
				else
				{
					value=false;
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			//Call the python API
			
			requestDao.editRequestforReportWebserviceInfo(
					requestinfo.getAlphanumericReqId(),
					Double.toString(requestinfo
							.getRequestVersion()),
					"instantiation", "1", "In Progress");
			jsonArray = new Gson().toJson(value);
			obj.put(new String("output"), jsonArray);
			
		} else {
			value = true;
			jsonArray = new Gson().toJson(value);
			obj.put(new String("output"), jsonArray);

		}

		return obj;
	}

	/**
	 * This service is used to push the milestone status info to external system.
	 * 
	 * @param request
	 * @return outputObj
	 * @throws ParseException
	 */
	@SuppressWarnings("unchecked")
	@POST
	@RequestMapping(value = "/pushMilestoneInfo", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
	@ResponseBody
	public JSONObject pushMilestoneInfo(@RequestBody String request) throws ParseException {
		logger.info("Start - pushMilestoneInfo");
		JSONObject outputObj = new JSONObject();
		boolean isUpdate = false;
		try {
			JSONParser parser = new JSONParser();
			JSONObject inputJson = (JSONObject) parser.parse(request);
			if (inputJson.get("requestId") != null && inputJson.get("mileStoneName") != null
					&& inputJson.get("mileStoneStatus") != null) {
				String requestId = inputJson.get("requestId").toString();
				String mileStoneName = inputJson.get("mileStoneName").toString();
				String mileStoneStatus = inputJson.get("mileStoneStatus").toString();
				logger.info("requestId ->" + requestId);
				logger.info("mileStoneName ->" + mileStoneName);
				logger.info("mileStoneStatus ->" + mileStoneStatus);
				/** Call c3p_rfo_decomposed to find the SO ID for external system */
				RfoDecomposedEntity rfoDecomposedEntity = rfoDecomposedRepo.findByOdRequestId(requestId);
				if (rfoDecomposedEntity != null && rfoDecomposedEntity.getOdRfoId() != null) {
					JSONObject requestJson = new JSONObject();
					HttpHeaders headers = new HttpHeaders();
					headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
					requestJson.put("u_milestone_name", mileStoneName);
					requestJson.put("u_status", mileStoneStatus);
					requestJson.put("u_so_id", rfoDecomposedEntity.getOdRfoId());
					logger.info("mileStoneName ->" + mileStoneName);
					logger.info("mileStoneStatus ->" + mileStoneStatus);
					logger.info("SO ID ->" + rfoDecomposedEntity.getOdRfoId());
					HttpEntity<JSONObject> entity = new HttpEntity<JSONObject>(requestJson, headers);
					String apiEndPoint = TSALabels.EXTERNAL_MILESTONE_API.getValue() + "now/table/u_imp_stage_status";
					restTemplate.setRequestFactory(getClientHttpRequestFactory());
					String response = restTemplate.exchange(apiEndPoint, HttpMethod.POST, entity, String.class)
							.getBody();
					JSONObject responseJson = (JSONObject) parser.parse(response);
					logger.info("responseJson ->" + responseJson);
					if (responseJson != null) {
						isUpdate = true;
					}
				}
			}

		} catch (Exception exe) {
			logger.error("Exception occured in pushMilestoneInfo ->" + exe.getMessage());
			exe.printStackTrace();
		}
		outputObj.put(new String("output"), isUpdate);
		return outputObj;
	}
	/**
	 * Setups the Client Http Request Factory for authentication mechanism.
	 * @return clientHttpRequestFactory
	 */
	private HttpComponentsClientHttpRequestFactory getClientHttpRequestFactory() {
		HttpComponentsClientHttpRequestFactory clientHttpRequestFactory = new HttpComponentsClientHttpRequestFactory();
		clientHttpRequestFactory.setHttpClient(httpClient());
		return clientHttpRequestFactory;
	}
	/**
	 * Setups the Basic Authentication logic here.
	 * @return client
	 */
	private HttpClient httpClient() {
		CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
		credentialsProvider.setCredentials(AuthScope.ANY,
				new UsernamePasswordCredentials(TSALabels.EXTERNAL_MILESTONE_API_USERNAME.getValue(),
						TSALabels.EXTERNAL_MILESTONE_API_PASSWORD.getValue()));
		HttpClient client = HttpClientBuilder.create().setDefaultCredentialsProvider(credentialsProvider).build();
		return client;
	}
}