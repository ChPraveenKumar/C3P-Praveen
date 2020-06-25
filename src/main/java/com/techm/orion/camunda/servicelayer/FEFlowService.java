package com.techm.orion.camunda.servicelayer;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import javax.ws.rs.POST;
import javax.ws.rs.core.Response;

import org.json.JSONException;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.google.gson.Gson;
import com.techm.orion.dao.RequestInfoDao;
import com.techm.orion.pojo.CommandPojo;
import com.techm.orion.pojo.Global;
import com.techm.orion.rest.CamundaServiceCreateReq;
import com.techm.orion.rest.CamundaServiceFEWorkflow;
import com.techm.orion.rest.DeviceReachabilityAndPreValidationTest;
import com.techm.orion.utility.InvokeFtl;

@Controller
@RequestMapping("/configuration")
@CrossOrigin(origins = "http://localhost:4200", maxAge = 3600)
public class FEFlowService implements Observer {
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

			String RequestId = json.get("requestId").toString();
			String version = json.get("version").toString();
			final boolean status = (Boolean) json.get("status");
			Float v = Float.parseFloat(version);
			DecimalFormat df = new DecimalFormat("0.0");
			df.setMaximumFractionDigits(1);
			version = df.format(v);
			final String userTaskId = daoService.getUserTaskIdForRequest(RequestId, version);
			Thread t = new Thread(new Runnable() {

				@Override
				public void run() {
					// TODO Auto-generated method stub
					workflowService.completeFEDeviceReachabilityFlow(userTaskId, status);

				}
			});
			t.start();
			boolean res;
			if (status) {
				// assign to SE again
				res = daoService.changeRequestOwner(RequestId, version, "seuser");
				daoService.changeRequestStatus(RequestId, version, "In Progress");
				daoService.resetErrorStateOfRechabilityTest(RequestId, version);

			} else {
				// change request status to hold
				daoService.changeRequestStatus(RequestId, version, "Hold");
				res = daoService.changeRequestOwner(RequestId, version, "seuser");
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
				data = invokeFtl.getGeneratedBasicConfigFile(RequestId, version);
				if (readFlag.equalsIgnoreCase("1")) {
					dao.setReadFlagFESE(RequestId, version, 1, "FE");
				} else {
					dao.setReadFlagFESE(RequestId, version, 0, "FE");

				}
				CommandPojo pojo;
				for (int i = 0; i < data.size(); i++) {
					pojo = new CommandPojo();
					pojo.setCommand_id("" + i);
					pojo.setCommand_value(data.get(i));
					cmdList.add(pojo);
				}
				if (Global.loggedInUser.equalsIgnoreCase("feuser")) {

				} else if (Global.loggedInUser.equalsIgnoreCase("seuser")) {

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

}
