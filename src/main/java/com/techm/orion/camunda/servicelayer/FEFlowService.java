package com.techm.orion.camunda.servicelayer;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.util.Set;

import javax.ws.rs.POST;
import javax.ws.rs.core.Response;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONException;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.google.gson.Gson;
import com.techm.orion.dao.RequestInfoDao;
import com.techm.orion.dao.TemplateManagementDao;
import com.techm.orion.entitybeans.BasicConfiguration;
import com.techm.orion.entitybeans.RequestInfoEntity;
import com.techm.orion.entitybeans.Series;
import com.techm.orion.pojo.AttribCreateConfigPojo;
import com.techm.orion.pojo.CommandPojo;
import com.techm.orion.pojo.Global;
import com.techm.orion.pojo.RequestInfoPojo;
import com.techm.orion.repositories.BasicConfigurationRepository;
import com.techm.orion.repositories.RequestInfoDetailsRepositories;
import com.techm.orion.repositories.SeriesRepository;
import com.techm.orion.rest.CamundaServiceCreateReq;
import com.techm.orion.rest.CamundaServiceFEWorkflow;
import com.techm.orion.rest.DeviceReachabilityAndPreValidationTest;
import com.techm.orion.service.AttribCreateConfigService;
import com.techm.orion.service.GetConfigurationTemplateService;
import com.techm.orion.utility.InvokeFtl;

@Controller
@RequestMapping("/configuration")
@CrossOrigin(origins = "http://localhost:4200", maxAge = 3600)
public class FEFlowService implements Observer {
	
	@Autowired
	RequestInfoDetailsRepositories reository;

	@Autowired
	SeriesRepository seriesrepo;
	
	@Autowired
	BasicConfigurationRepository basicConfigRepo;
	

	@Autowired
	AttribCreateConfigService service;

	@POST
	@RequestMapping(value = "/startPreValidateTest", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
	@ResponseBody
	public Response prevalidationTest(@RequestBody String request) {
		DeviceReachabilityAndPreValidationTest DeviceRechabilityService = new DeviceReachabilityAndPreValidationTest();
		// CamundaServiceTemplateApproval camundaService = new
		// CamundaServiceTemplateApproval();

		Response response = null;
		try {
			DeviceRechabilityService.performPrevalidateTest(request);
		}
		// camundaService.initiateApprovalFlow(templateId, templateVersion,
		// "Admin");
		catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return response;
	}

	@POST
	@RequestMapping(value = "/responsefromfe", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
	@CrossOrigin(origins = "http://localhost:4200", maxAge = 3600)
	@ResponseBody
	public Response resumePrevalidationTest(@RequestBody String request) {
		RequestInfoDao daoService = new RequestInfoDao();
		JSONObject obj = new JSONObject();

		final CamundaServiceFEWorkflow workflowService = new CamundaServiceFEWorkflow();
		Response response = null;
		try {
			JSONParser parser = new JSONParser();
			JSONObject json = (JSONObject) parser.parse(request);
			final CamundaServiceCreateReq camundaServiceCreateReq = new CamundaServiceCreateReq();

			String RequestId = json.get("requestId").toString();
			String version = json.get("version").toString();
			final boolean status = (Boolean) json.get("status");
			Float v = Float.parseFloat(version);
			DecimalFormat df = new DecimalFormat("0.0");
			df.setMaximumFractionDigits(1);
			version = df.format(v);
			RequestInfoEntity req = reository.findByAlphanumericReqIdAndRequestVersion(RequestId, Double.valueOf(version));

			final String userTaskId = daoService.getUserTaskIdForRequest(RequestId, version);
			if(!req.getStatus().equalsIgnoreCase("Hold"))
			{
			Thread t = new Thread(new Runnable() {

				@Override
				public void run() {
					// TODO Auto-generated method stub
					workflowService.completeFEDeviceReachabilityFlow(userTaskId, (Boolean) json.get("status"));

				}
			});
			t.start();
			}
			boolean res;
			if (status) {
				// assign to SE again
				if(req.getStatus().equalsIgnoreCase("Hold"))
				{
					res = daoService.changeRequestOwner(RequestId, version, req.getRequestCreatorName());
					daoService.changeRequestStatus(RequestId, version, "In Progress");
					daoService.resetErrorStateOfRechabilityTest(RequestId, version);
					// call camunda service for given request id and version
					Thread t1 = new Thread(new Runnable() {

						@Override
						public void run() {
							// TODO Auto-generated method stub
							try {
								String v1=json.get("version").toString();
								Float v2 = Float.parseFloat(v1);
								DecimalFormat df = new DecimalFormat("0.0");
								df.setMaximumFractionDigits(1);
								String ver = df.format(v2);
								camundaServiceCreateReq.uploadToServerNew(RequestId, ver, "NEWREQUEST");
							} catch (JSONException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							} catch (IOException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}

						}
					});
					t1.start();
				}
				else
				{
					res = daoService.changeRequestOwner(RequestId, version, req.getRequestCreatorName());
					daoService.changeRequestStatus(RequestId, version, "In Progress");
					daoService.resetErrorStateOfRechabilityTest(RequestId, version);
				}

			} else {
				// change request status to hold
				daoService.changeRequestStatus(RequestId, version, "Hold");
				res = daoService.changeRequestOwner(RequestId, version, req.getRequestCreatorName());
				daoService.resetErrorStateOfRechabilityTest(RequestId, version);

			}
			obj.put("result", res);

		}
		// camundaService.initiateApprovalFlow(templateId, templateVersion,
		// "Admin");
		catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return Response.status(200).header("Access-Control-Allow-Origin", "*")
				.header("Access-Control-Allow-Headers", "origin, content-type, accept, authorization")
				.header("Access-Control-Allow-Credentials", "true")
				.header("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS, HEAD")
				.header("Access-Control-Max-Age", "1209600").entity(obj).build();
	}

	public void update(Observable arg0, Object arg1) {
		// TODO Auto-generated method stub

	}

	@POST
	@RequestMapping(value = "/getBasicConfiguration", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
	@CrossOrigin(origins = "http://localhost:4200", maxAge = 3600)
	@ResponseBody
	public Response getGeneratedConfiguration(@RequestBody String request) {
		Response response = null;
		InvokeFtl invokeFtl = new InvokeFtl();
		JSONObject obj = new JSONObject();
		RequestInfoDao dao = new RequestInfoDao();
		List<String> data = new ArrayList<String>();
		String data1=null;
		List<CommandPojo> cmdList = new ArrayList<CommandPojo>();
		try {

			JSONParser parser = new JSONParser();
			JSONObject json = (JSONObject) parser.parse(request);

			if (!json.isEmpty()) {
				String RequestId = json.get("requestId").toString();
				String version = json.get("version").toString();
				String readFlag = json.get("readFlag").toString();

				Float v = Float.parseFloat(version);
				DecimalFormat df = new DecimalFormat("0.0");
				df.setMaximumFractionDigits(1);
				version = df.format(v);
				// String readStatus=json.get("readFlag").toString();
				
				//data = invokeFtl.getGeneratedBasicConfigFile(RequestId, version);
				
				
				RequestInfoEntity req = reository.findByAlphanumericReqIdAndRequestVersion(RequestId, Double.valueOf(version));

				//get series
				String series=getSeries(req.getVendor(), req.getDeviceType(),req.getModel());
				
				Set<Series>setSeries=seriesrepo.findBySeries(series);
				 List<Series> listSeries = new ArrayList<>(setSeries);
				 Set<BasicConfiguration>setBasicConfig=basicConfigRepo.findBySeriesId(listSeries.get(0).getId());
				 List<BasicConfiguration> listConf = new ArrayList<>(setBasicConfig);

				//get master config from DB
				CommandPojo pojo;
				for (int i = 0; i < listConf.size(); i++) {
					pojo = new CommandPojo();
					pojo.setCommand_id(String.valueOf(listConf.get(i).getSequence_id()));
					pojo.setCommand_value(listConf.get(i).getConfiguration());
					pojo.setCommandValue(listConf.get(i).getConfiguration());
					cmdList.add(pojo);
				}
				
				TemplateManagementDao templatemanagementDao = new TemplateManagementDao();
				String seriesId = templatemanagementDao.getSeriesId(req.getTemplateUsed(), series);
				seriesId = StringUtils.substringAfter(seriesId, "Generic_");
				
				List<AttribCreateConfigPojo> masterAttribute = new ArrayList<>();
				List<AttribCreateConfigPojo> byAttribSeriesId = service.getByAttribSeriesId(seriesId);
				if (byAttribSeriesId != null && !byAttribSeriesId.isEmpty()) {
					masterAttribute.addAll(byAttribSeriesId);
				}
				
				List<CommandPojo> cammandsBySeriesId = null;
				cammandsBySeriesId = templatemanagementDao.getCammandsBySeriesId(seriesId, null);
				
				RequestInfoPojo createConfigRequest = new RequestInfoPojo();
				GetConfigurationTemplateService getConfigurationTemplateService = new GetConfigurationTemplateService();
				createConfigRequest.setOsVersion(req.getOsVersion());
				createConfigRequest.setHostname(req.getHostName());
				createConfigRequest.setOs(req.getOs());
				createConfigRequest.setModel(req.getModel());
				createConfigRequest.setManagementIp(req.getManagmentIP());
				createConfigRequest.setVendor(req.getVendor());
				createConfigRequest.setRegion(req.getRegion());
			    invokeFtl.createFinalTemplate(cmdList, null, null, null,
			    		req.getTemplateUsed());
			    data1 = getConfigurationTemplateService.generateTemplate(createConfigRequest);
				
				//Global.loggedInUser="feuser";
				if (Global.loggedInUser.equalsIgnoreCase("feuser")) {
					dao.setReadFlagFESE(RequestId, version, 1, "FE");


				} else if (Global.loggedInUser.equalsIgnoreCase("seuser")) {
					dao.setReadFlagFESE(RequestId, version, 1, "SE");

				}

			}
		}
		// camundaService.initiateApprovalFlow(templateId, templateVersion,
		// "Admin");
		catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String jsonList = new Gson().toJson(cmdList);
		obj.put(new String("output"), jsonList);

		return Response.status(200).header("Access-Control-Allow-Origin", "*")
				.header("Access-Control-Allow-Headers", "origin, content-type, accept, authorization")
				.header("Access-Control-Allow-Credentials", "true")
				.header("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS, HEAD")
				.header("Access-Control-Max-Age", "1209600").entity(obj).build();
	}

	@POST
	@RequestMapping(value = "/restartFEFlow", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
	@ResponseBody
	public Response restartFEFlow(@RequestBody String request) {
		final CamundaServiceCreateReq camundaServiceCreateReq = new CamundaServiceCreateReq();
		RequestInfoDao dao = new RequestInfoDao();
		Response response = null;
		try {

			JSONParser parser = new JSONParser();
			JSONObject json = (JSONObject) parser.parse(request);

			final String RequestId = json.get("requestId").toString();
			String version = json.get("version").toString();
			Float v = Float.parseFloat(version);
			DecimalFormat df = new DecimalFormat("0.0");
			df.setMaximumFractionDigits(1);
			final String version1 = df.format(v);
			// change request status to In Progress

			dao.changeRequestStatus(RequestId, version1, "In Progress");

			// call camunda service for given request id and version
			Thread t = new Thread(new Runnable() {

				@Override
				public void run() {
					// TODO Auto-generated method stub
					try {
						camundaServiceCreateReq.uploadToServerNew(RequestId, version1, "NEWREQUEST");
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

				}
			});
			t.start();
		}
		// camundaService.initiateApprovalFlow(templateId, templateVersion,
		// "Admin");
		catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return response;
	}

	private String getSeries(String vendor, String deviceType, String model)
	{
		String result=vendor+deviceType.toUpperCase()+model.substring(0, 2);
		return result;
	}
}
