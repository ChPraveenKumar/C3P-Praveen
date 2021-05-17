package com.techm.orion.camunda.servicelayer;

import java.io.IOException;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import javax.ws.rs.POST;
import javax.ws.rs.core.Response;

import org.json.JSONException;
import org.json.simple.JSONArray;
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

import com.techm.orion.dao.RequestInfoDao;
import com.techm.orion.entitybeans.Notification;
import com.techm.orion.entitybeans.RequestInfoEntity;
import com.techm.orion.entitybeans.TemplateFeatureEntity;
import com.techm.orion.pojo.CommandPojo;
import com.techm.orion.repositories.MasterCommandsRepository;
import com.techm.orion.repositories.NotificationRepo;
import com.techm.orion.repositories.RequestInfoDetailsRepositories;
import com.techm.orion.repositories.TemplateFeatureRepo;
import com.techm.orion.rest.CamundaServiceCreateReq;
import com.techm.orion.rest.CamundaServiceFEWorkflow;
import com.techm.orion.rest.DeviceReachabilityAndPreValidationTest;

@Controller
@RequestMapping("/configuration")
@CrossOrigin(origins = "http://localhost:4200", maxAge = 3600)
public class FEFlowService implements Observer {
	
	@Autowired
	private RequestInfoDetailsRepositories reository;

	@Autowired
	private TemplateFeatureRepo templateFeatureRepo;
	
	@Autowired
	private MasterCommandsRepository masterCommandsRepository;
	@Autowired
	private NotificationRepo notificationRepo;
	
	@Autowired
	private RequestInfoDao requestInfoDao;
	
	@Autowired
	private DeviceReachabilityAndPreValidationTest deviceReachabilityAndPreValidationTest;


	@POST
	@RequestMapping(value = "/startPreValidateTest", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
	@ResponseBody
	public Response prevalidationTest(@RequestBody String request) {
		
		// CamundaServiceTemplateApproval camundaService = new
		// CamundaServiceTemplateApproval();

		Response response = null;
		try {
			deviceReachabilityAndPreValidationTest.performPrevalidateTest(request);
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
		JSONObject obj = new JSONObject();
		Date date = new Date();
		Timestamp timestamp = new Timestamp(date.getTime());
		Calendar cal = Calendar.getInstance();
		int notifId = 0;
		final CamundaServiceFEWorkflow workflowService = new CamundaServiceFEWorkflow();
		try {
			JSONParser parser = new JSONParser();
			JSONObject json = (JSONObject) parser.parse(request);
			final CamundaServiceCreateReq camundaServiceCreateReq = new CamundaServiceCreateReq();

			String RequestId = json.get("requestId").toString();
			String version = json.get("version").toString();
			String userName = json.get("userName").toString();
			final boolean status = (Boolean) json.get("status");
			Float v = Float.parseFloat(version);
			DecimalFormat df = new DecimalFormat("0.0");
			df.setMaximumFractionDigits(1);
			version = df.format(v);
			RequestInfoEntity req = reository.findByAlphanumericReqIdAndRequestVersion(RequestId, Double.valueOf(version));
			
			final String userTaskId = requestInfoDao.getUserTaskIdForRequest(RequestId, version);
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
					res = requestInfoDao.changeRequestOwner(RequestId, version, req.getRequestCreatorName());
					requestInfoDao.changeRequestStatus(RequestId, version, "In Progress");
					requestInfoDao.resetErrorStateOfRechabilityTest(RequestId, version);
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
								camundaServiceCreateReq.uploadToServerNew(RequestId, ver, "NEWREQUEST", userName);
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
					res = requestInfoDao.changeRequestOwner(RequestId, version, req.getRequestCreatorName());
					requestInfoDao.changeRequestStatus(RequestId, version, "In Progress");
					requestInfoDao.resetErrorStateOfRechabilityTest(RequestId, version);
				}

			} else {
				// change request status to hold
				requestInfoDao.changeRequestStatus(RequestId, version, "Hold");
				res = requestInfoDao.changeRequestOwner(RequestId, version, req.getRequestCreatorName());
				requestInfoDao.resetErrorStateOfRechabilityTest(RequestId, version);

			}
			if(json.get("notif_id") != null)
				notifId = Integer.parseInt(json.get("notif_id").toString());
			Notification notificationData = notificationRepo.findById(notifId);
			notificationData.setNotifStatus("Completed");
			notificationData.setNotifCompletedby(userName);
			notificationRepo.save(notificationData);
			Notification newNotification = new Notification();
			newNotification.setNotifFromUser(notificationData.getNotifCompletedby());
			newNotification.setNotifToUser(notificationData.getNotifFromUser());
			newNotification.setNotifType(notificationData.getNotifType());
			newNotification.setNotifCreatedDate(timestamp);
			newNotification.setNotifReference(notificationData.getNotifReference());
			
			if(status)
			{
				newNotification.setNotifMessage("Request proceeded");
				newNotification.setNotifLabel(notificationData.getNotifReference()+" : "+"Request proceeded");
			}
			else
			{	
				newNotification.setNotifMessage("Request put on hold");
				newNotification.setNotifLabel(notificationData.getNotifReference()+" : "+"Request put on hold");
			}
			
			newNotification.setNotifPriority("1");
			newNotification.setNotifStatus("Pending");
			cal.setTimeInMillis(timestamp.getTime());
		    cal.add(Calendar.DAY_OF_MONTH, 30);
		    timestamp = new Timestamp(cal.getTime().getTime());
			newNotification.setNotifExpiryDate(timestamp);
			notificationRepo.save(newNotification);
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
		JSONObject obj = new JSONObject();		
		JSONArray jsonArray = new JSONArray();
		JSONObject jsonObject = new JSONObject();
		int notifId =0;
		String userName = null;
		try {
			JSONParser parser = new JSONParser();
			JSONObject json = (JSONObject) parser.parse(request);
			if (!json.isEmpty()) {
				if(json.get("notif_id") != null)
					notifId = Integer.parseInt(json.get("notif_id").toString());	
				Notification notificationData = notificationRepo.findById(notifId);
				if (json.get("userName") != null) 
					userName = json.get("userName").toString();
				String RequestId = json.get("requestId").toString();
				String version = json.get("version").toString();
				String readFlag = json.get("readFlag").toString();
				Float v = Float.parseFloat(version);
				DecimalFormat df = new DecimalFormat("0.0");
				df.setMaximumFractionDigits(1);
				version = df.format(v);
				String userRole = json.get("userRole").toString();
				// String readStatus=json.get("readFlag").toString();
				
				//data = invokeFtl.getGeneratedBasicConfigFile(RequestId, version);
				
				RequestInfoEntity req = reository.findByAlphanumericReqIdAndRequestVersion(RequestId, Double.valueOf(version));
				List<TemplateFeatureEntity> templateFeatureEntity = templateFeatureRepo
						.findByCommandType(req.getTemplateUsed());
				List<CommandPojo> commandValue = new ArrayList<>();
				templateFeatureEntity.forEach(templateFeature -> {
					if (templateFeature.getComandDisplayFeature().contains("Basic Configuration")) {
						commandValue.addAll(masterCommandsRepository.findByCommandId(templateFeature.getId()));
					}
				});
				commandValue.forEach(commands -> {
					CommandPojo commandpojo = new CommandPojo();
					commandpojo.setCommand_value(commands.getCommand_value());
					commandpojo.setCommand_sequence_id(commands.getCommand_sequence_id());
					jsonArray.add(commandpojo);
				});
				jsonObject.put("output", jsonArray);
				
				//Global.loggedInUser="feuser";
				if (userRole.equalsIgnoreCase("feuser")) {
					requestInfoDao.setReadFlagFESE(RequestId, version, 1, "FE");
				} else if (userRole.equalsIgnoreCase("seuser")) {
					requestInfoDao.setReadFlagFESE(RequestId, version, 1, "SE");
				}
				notificationData.setNotifReadby(userName);
				notificationRepo.save(notificationData);
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
		return Response.status(200).header("Access-Control-Allow-Origin", "*")
				.header("Access-Control-Allow-Headers", "origin, content-type, accept, authorization")
				.header("Access-Control-Allow-Credentials", "true")
				.header("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS, HEAD")
				.header("Access-Control-Max-Age", "1209600").entity(jsonObject).build();
	}

	@POST
	@RequestMapping(value = "/restartFEFlow", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
	@ResponseBody
	public Response restartFEFlow(@RequestBody String request) {
		final CamundaServiceCreateReq camundaServiceCreateReq = new CamundaServiceCreateReq();
		
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

			requestInfoDao.changeRequestStatus(RequestId, version1, "In Progress");

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
