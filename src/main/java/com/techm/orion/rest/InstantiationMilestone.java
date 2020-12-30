package com.techm.orion.rest;

import java.io.IOException;

import javax.ws.rs.POST;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONException;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.google.gson.Gson;
import com.techm.orion.dao.RequestInfoDao;
import com.techm.orion.dao.RequestInfoDetailsDao;
import com.techm.orion.pojo.RequestInfoPojo;
import com.techm.orion.repositories.DeviceDiscoveryRepository;
import com.techm.orion.service.VnfInstantiationMilestoneService;
import com.techm.orion.utility.TestStrategeyAnalyser;

@Controller
@RequestMapping("/Instantiation")
public class InstantiationMilestone extends Thread {
	private static final Logger logger = LogManager.getLogger(InstantiationMilestone.class);

	@Autowired
	RequestInfoDao requestInfoDao;

	@Autowired
	RequestInfoDetailsDao requestDao;

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
}