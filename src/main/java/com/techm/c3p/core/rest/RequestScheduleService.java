package com.techm.c3p.core.rest;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.ws.rs.POST;
import javax.ws.rs.core.Response;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.google.gson.Gson;
import com.techm.c3p.core.bpm.servicelayer.CamundaServiceCreateReq;
import com.techm.c3p.core.dao.RequestSchedulerDao;
import com.techm.c3p.core.pojo.CreateConfigRequestDCM;
import com.techm.c3p.core.pojo.SchedulerListPojo;
import com.techm.c3p.core.service.DcmConfigService;
import com.techm.c3p.core.service.RequestSchedulerForNewAndModify;

@Controller
@RequestMapping("/RequestScheduleService")
public class RequestScheduleService {
	private static final Logger logger = LogManager.getLogger(RequestScheduleService.class);

	@Autowired
	private DcmConfigService dcmConfigService;
	@Autowired
	private CamundaServiceCreateReq camundaServiceCreateReq;
	@Autowired
	private RequestSchedulerDao requestSchedulerDao;
	/**
	 *This Api is marked as ***************c3p-ui Api Impacted****************
	 **/
	@POST
	@RequestMapping(value = "/getScheduledHistory", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
	@ResponseBody
	public Response getScheduledHistory(@RequestBody String request) {

		JSONObject obj = new JSONObject();
		String jsonArray = "";

		try {
			JSONParser parser = new JSONParser();
			JSONObject json = (JSONObject) parser.parse(request);

			RequestSchedulerForNewAndModify requestSchedulerForNewAndModify = new RequestSchedulerForNewAndModify();

			String RequestId = json.get("requestId").toString();
			String version = json.get("version").toString();

			List<SchedulerListPojo> scheduledList = new ArrayList<SchedulerListPojo>();

			scheduledList = requestSchedulerForNewAndModify.getScheduledHistoryDB(RequestId, version);
			jsonArray = new Gson().toJson(scheduledList);

			obj.put(new String("output"), jsonArray);

		} catch (Exception e) {
			logger.error(e);
		}

		return Response.status(200).header("Access-Control-Allow-Origin", "*")
				.header("Access-Control-Allow-Headers", "origin, content-type, accept, authorization")
				.header("Access-Control-Allow-Credentials", "true")
				.header("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS, HEAD")
				.header("Access-Control-Max-Age", "1209600").entity(obj).build();

	}

	/**
	 *This Api is marked as ***************c3p-ui Api Impacted****************
	 **/
	@POST
	@RequestMapping(value = "/recheduleRequest", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
	@ResponseBody
	public Response recheduleRequest(@RequestBody String request) {

		JSONObject obj = new JSONObject();
		String processId = null;
		try {
			JSONParser parser = new JSONParser();
			JSONObject json = (JSONObject) parser.parse(request);

			RequestSchedulerForNewAndModify requestSchedulerForNewAndModify = new RequestSchedulerForNewAndModify();

			String RequestId = json.get("requestId").toString();
			String version = json.get("version").toString();
			String scheduledTime = json.get("scheduledTime").toString();

			CreateConfigRequestDCM createConfigRequestDCM = new CreateConfigRequestDCM();
			createConfigRequestDCM.setRequestId(RequestId);
			createConfigRequestDCM.setRequest_version(Double.parseDouble(version));
			createConfigRequestDCM.setScheduledTime(scheduledTime);

			processId = requestSchedulerDao.getProcessIdFromCamundaHistory(RequestId, version);
			if (processId != null) {
				camundaServiceCreateReq.deleteProcessID(processId);
				String result = requestSchedulerForNewAndModify.rescheduleRequestDB(RequestId, version, scheduledTime);

				String message = requestSchedulerForNewAndModify
						.createNewReScheduledRequestService(createConfigRequestDCM);

				obj.put(new String("output"), message);

			} else if (processId == null) {
				String result = requestSchedulerForNewAndModify.rescheduleRequestDB(RequestId, version, scheduledTime);

				String message = requestSchedulerForNewAndModify
						.createNewReScheduledRequestService(createConfigRequestDCM);

				obj.put(new String("output"), message);
			} else {
				obj.put(new String("output"), "Failed to reschedule");
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

	/**
	 *This Api is marked as ***************c3p-ui Api Impacted****************
	 **/
	@POST
	@RequestMapping(value = "/runScheduleRequest", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
	@ResponseBody
	public JSONObject runScheduleRequest(@RequestBody String configRequest) {
		
		JSONObject obj = new JSONObject();
		RequestSchedulerForNewAndModify requestSchedulerForNewAndModify = new RequestSchedulerForNewAndModify();
		String processId = null;

		try {

			JSONParser parser = new JSONParser();
			JSONObject jsonData = (JSONObject) parser.parse(configRequest);
			JSONObject json = (JSONObject) jsonData.get("data");
			CreateConfigRequestDCM createConfigRequestDCM = new CreateConfigRequestDCM();

			createConfigRequestDCM.setCustomer(json.get("customer").toString());
			createConfigRequestDCM.setSiteid(json.get("siteid").toString().toUpperCase());
			createConfigRequestDCM.setDeviceType(json.get("deviceType").toString());
			createConfigRequestDCM.setModel(json.get("model").toString());
			createConfigRequestDCM.setOs(json.get("os").toString());
			createConfigRequestDCM.setOsVersion(json.get("osVersion").toString());
			if (json.containsKey("vrfName")) {
				createConfigRequestDCM.setVrfName(json.get("vrfName").toString());
			}
			createConfigRequestDCM.setManagementIp(json.get("managementIp").toString());
			if (json.containsKey("enablePassword")) {
				createConfigRequestDCM.setEnablePassword(json.get("enablePassword").toString());
			} else {
				createConfigRequestDCM.setEnablePassword(null);
			}
			if (json.containsKey("banner")) {
				createConfigRequestDCM.setBanner(json.get("banner").toString());
			} else {
				createConfigRequestDCM.setBanner(null);
			}
			createConfigRequestDCM.setRegion(json.get("region").toString().toUpperCase());
			createConfigRequestDCM.setService(json.get("service").toString().toUpperCase());
			createConfigRequestDCM.setHostname(json.get("hostname").toString().toUpperCase());

			createConfigRequestDCM.setVendor(json.get("vendor").toString().toUpperCase());

			JSONObject internetLvcrf = (JSONObject) json.get("internetLcVrf");
			if (internetLvcrf.containsKey("networkIp") && internetLvcrf.get("networkIp").toString() != "") {
				createConfigRequestDCM.setNetworkIp(internetLvcrf.get("networkIp").toString());
			}
			if (internetLvcrf.containsKey("neighbor1")) {
				createConfigRequestDCM.setNeighbor1(internetLvcrf.get("neighbor1").toString());
			}
			if (internetLvcrf.containsKey("neighbor2")) {
				createConfigRequestDCM.setNeighbor2(internetLvcrf.get("neighbor2").toString().toUpperCase());
			} else {
				createConfigRequestDCM.setNeighbor2(null);
			}
			if (internetLvcrf.containsKey("neighbor1_remoteAS")) {
				createConfigRequestDCM
						.setNeighbor1_remoteAS(internetLvcrf.get("neighbor1_remoteAS").toString().toUpperCase());
			}
			if (internetLvcrf.containsKey("neighbor2_remoteAS")) {
				createConfigRequestDCM
						.setNeighbor2_remoteAS(internetLvcrf.get("neighbor2_remoteAS").toString().toUpperCase());
			} else {
				createConfigRequestDCM.setNeighbor2_remoteAS(null);
			}

			if (internetLvcrf.containsKey("networkIp_subnetMask")
					&& internetLvcrf.get("networkIp_subnetMask").toString() != "") {
				createConfigRequestDCM.setNetworkIp_subnetMask(internetLvcrf.get("networkIp_subnetMask").toString());
			}
			if (internetLvcrf.containsKey("routingProtocol")) {
				createConfigRequestDCM
						.setRoutingProtocol(internetLvcrf.get("routingProtocol").toString().toUpperCase());
			}
			if (internetLvcrf.containsKey("AS")) {
				createConfigRequestDCM.setBgpASNumber(internetLvcrf.get("AS").toString().toUpperCase());
			}
			if (jsonData.get("version").toString().equalsIgnoreCase("1.0")) {
				JSONObject c3p_interface = (JSONObject) json.get("c3p_interface");
				if (c3p_interface.containsKey("name") && c3p_interface.get("name").toString() != "") {
					createConfigRequestDCM.setName(c3p_interface.get("name").toString());
				}
				if (c3p_interface.containsKey("description") && c3p_interface.get("description").toString() != "") {
					createConfigRequestDCM.setDescription(c3p_interface.get("description").toString());
				} else {
					createConfigRequestDCM.setDescription(null);

				}

				if (c3p_interface.containsKey("ip") && c3p_interface.get("ip").toString() != "") {
					createConfigRequestDCM.setIp(c3p_interface.get("ip").toString());
				}
				if (c3p_interface.containsKey("mask") && c3p_interface.get("mask").toString() != "") {
					createConfigRequestDCM.setMask(c3p_interface.get("mask").toString());
				}

				if (c3p_interface.containsKey("speed") && c3p_interface.get("speed").toString() != "") {
					createConfigRequestDCM.setSpeed(c3p_interface.get("speed").toString());
				}
				if (c3p_interface.containsKey("bandwidth") && c3p_interface.get("bandwidth").toString() != "") {

					createConfigRequestDCM.setBandwidth(c3p_interface.get("bandwidth").toString());
				}

				if (c3p_interface.containsKey("encapsulation") && c3p_interface.get("encapsulation").toString() != "") {
					createConfigRequestDCM.setEncapsulation(c3p_interface.get("encapsulation").toString());
				}

			}

			if (!jsonData.get("version").toString().equalsIgnoreCase("1.0")) {
				JSONObject deviceInterfaceSO = (JSONObject) json.get("deviceInterfaceSO");

				if (deviceInterfaceSO.containsKey("name") && deviceInterfaceSO.get("name").toString() != "") {
					createConfigRequestDCM.setName(deviceInterfaceSO.get("name").toString());
				}
				if (deviceInterfaceSO.containsKey("description")
						&& deviceInterfaceSO.get("description").toString() != "") {
					createConfigRequestDCM.setDescription(deviceInterfaceSO.get("description").toString());
				} else {
					createConfigRequestDCM.setDescription(null);

				}

				if (deviceInterfaceSO.containsKey("ip") && deviceInterfaceSO.get("ip").toString() != "") {
					createConfigRequestDCM.setIp(deviceInterfaceSO.get("ip").toString());
				}
				if (deviceInterfaceSO.containsKey("mask") && deviceInterfaceSO.get("mask").toString() != "") {
					createConfigRequestDCM.setMask(deviceInterfaceSO.get("mask").toString());
				}

				if (deviceInterfaceSO.containsKey("speed") && deviceInterfaceSO.get("speed").toString() != "") {
					createConfigRequestDCM.setSpeed(deviceInterfaceSO.get("speed").toString());
				}
				if (deviceInterfaceSO.containsKey("bandwidth") && deviceInterfaceSO.get("bandwidth").toString() != "") {

					createConfigRequestDCM.setBandwidth(deviceInterfaceSO.get("bandwidth").toString());
				}

				if (deviceInterfaceSO.containsKey("encapsulation")
						&& deviceInterfaceSO.get("encapsulation").toString() != "") {
					createConfigRequestDCM.setEncapsulation(deviceInterfaceSO.get("encapsulation").toString());
				}
			}
			if (json.containsKey("misArPe")) {
				JSONObject mis = (JSONObject) json.get("misArPe");
				{
					createConfigRequestDCM.setRouterVrfVpnDGateway(mis.get("routerVrfVpnDIp").toString());
					createConfigRequestDCM.setRouterVrfVpnDIp(mis.get("routerVrfVpnDGateway").toString());
					createConfigRequestDCM.setFastEthernetIp(mis.get("fastEthernetIp").toString());
				}
			}
			if (json.containsKey("isAutoProgress")) {
				createConfigRequestDCM.setIsAutoProgress((Boolean) json.get("isAutoProgress"));
			} else {
				createConfigRequestDCM.setIsAutoProgress(true);
			}
			if (jsonData.containsKey("version")) {
				String version = jsonData.get("version").toString();
				createConfigRequestDCM.setRequest_version(Double.parseDouble(version));
			}
			if (jsonData.containsKey("requestId")) {
				createConfigRequestDCM.setRequestId(jsonData.get("requestId").toString());
			}

			// get request creator name
			String request_creator_name = dcmConfigService.getLogedInUserName();
			createConfigRequestDCM.setRequest_creator_name(request_creator_name);

			if (json.containsKey("snmpHostAddress")) {
				createConfigRequestDCM.setSnmpHostAddress(json.get("snmpHostAddress").toString());
			} else {
				createConfigRequestDCM.setSnmpHostAddress(null);
			}
			if (json.containsKey("snmpString")) {
				createConfigRequestDCM.setSnmpString(json.get("snmpString").toString());
			} else {
				createConfigRequestDCM.setSnmpString(null);
			}
			if (json.containsKey("loopBackType")) {
				createConfigRequestDCM.setLoopBackType(json.get("loopBackType").toString());
			} else {
				createConfigRequestDCM.setLoopBackType(null);
			}
			if (json.containsKey("loopbackIPaddress")) {
				createConfigRequestDCM.setLoopbackIPaddress(json.get("loopbackIPaddress").toString());
			} else {
				createConfigRequestDCM.setLoopbackIPaddress(null);
			}
			if (json.containsKey("loopbackSubnetMask")) {
				createConfigRequestDCM.setLoopbackSubnetMask(json.get("loopbackSubnetMask").toString());
			} else {
				createConfigRequestDCM.setLoopbackSubnetMask(null);
			}
			Date date = new Date();
			SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

			String strDate = sdf.format(date);
			createConfigRequestDCM.setRequestCreatedOn(strDate);

			JSONObject certificationTestFlag = (JSONObject) json.get("certificationOptionListFlags");

			// flag test selection
			if (certificationTestFlag.get("Interfaces status").toString().equals("1")) {
				createConfigRequestDCM.setInterfaceStatus(certificationTestFlag.get("Interfaces status").toString());
			}

			if (certificationTestFlag.get("WAN Interface").toString().equals("1")) {
				createConfigRequestDCM.setWanInterface(certificationTestFlag.get("WAN Interface").toString());
			}

			if (certificationTestFlag.get("Platform & IOS").toString().equals("1")) {
				createConfigRequestDCM.setPlatformIOS(certificationTestFlag.get("Platform & IOS").toString());
			}

			if (certificationTestFlag.get("BGP neighbor").toString().equals("1")) {
				createConfigRequestDCM.setBGPNeighbor(certificationTestFlag.get("BGP neighbor").toString());
			}
			if (certificationTestFlag.get("Throughput").toString().equals("1")) {
				createConfigRequestDCM.setThroughputTest(certificationTestFlag.get("Throughput").toString());
			}
			if (certificationTestFlag.get("FrameLoss").toString().equals("1")) {
				createConfigRequestDCM.setFrameLossTest(certificationTestFlag.get("FrameLoss").toString());
			}
			if (certificationTestFlag.get("Latency").toString().equals("1")) {
				createConfigRequestDCM.setLatencyTest(certificationTestFlag.get("Latency").toString());
			}

			String bit = certificationTestFlag.get("Interfaces status").toString()
					+ certificationTestFlag.get("WAN Interface").toString()
					+ certificationTestFlag.get("Platform & IOS").toString()
					+ certificationTestFlag.get("BGP neighbor").toString()
					+ certificationTestFlag.get("Throughput").toString()
					+ certificationTestFlag.get("FrameLoss").toString()
					+ certificationTestFlag.get("Latency").toString();

			createConfigRequestDCM.setCertificationSelectionBit(bit);
			// LAN interface
			if (json.containsKey("lanTnterface")) {
				createConfigRequestDCM.setLanInterface(json.get("lanTnterface").toString());
			}
			if (json.containsKey("lanIPaddress")) {
				createConfigRequestDCM.setLanIp(json.get("lanIPaddress").toString());
			}
			if (json.containsKey("lanSubnetMask")) {
				createConfigRequestDCM.setLanMaskAddress(json.get("lanSubnetMask").toString());
			}
			if (json.containsKey("lanDescription")) {
				createConfigRequestDCM.setLanDescription(json.get("lanDescription").toString());
			}
			// to get the scheduled time for the requestID
			if (json.containsKey("scheduledTime")) {
				createConfigRequestDCM.setScheduledTime(json.get("scheduledTime").toString());
			}

			processId = requestSchedulerDao.getProcessIdFromCamundaHistory(jsonData.get("requestId").toString(),
					jsonData.get("version").toString());
			if (processId != null) {
				camundaServiceCreateReq.deleteProcessID(processId);
				String message = requestSchedulerForNewAndModify.runScheduledRequestService(createConfigRequestDCM);
				obj.put(new String("output"), new String(message));

			} else {
				obj.put(new String("output"), "Failed to reschedule");
			}

		} catch (Exception e) {
			logger.error(e);
		}

		return obj;

	}

	/**
	 *This Api is marked as ***************c3p-ui Api Impacted****************
	 **/
	@POST
	@RequestMapping(value = "/cancelRequest", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
	@ResponseBody
	public Response cancelRequest(@RequestBody String request) {

		JSONObject obj = new JSONObject();
		String processId = null;

		try {
			JSONParser parser = new JSONParser();
			JSONObject json = (JSONObject) parser.parse(request);

			RequestSchedulerForNewAndModify requestSchedulerForNewAndModify = new RequestSchedulerForNewAndModify();

			String RequestId = json.get("requestId").toString();
			String version = json.get("version").toString();

			processId = requestSchedulerDao.getProcessIdFromCamundaHistory(RequestId, version);
			if (processId != null) {
				camundaServiceCreateReq.deleteProcessID(processId);
				requestSchedulerDao.deleteProcessIdFromCamundaHistory(processId);
				String result = requestSchedulerForNewAndModify.cancelRequestDB(RequestId, version);
				obj.put(new String("output"), result);

			} else {
				obj.put(new String("output"), "Failed to reschedule");
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

	/**
	 *This Api is marked as ***************c3p-ui Api Impacted****************
	 **/
	@POST
	@RequestMapping(value = "/abortRequest", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
	@ResponseBody
	public Response abortRequest(@RequestBody String request) {

		JSONObject obj = new JSONObject();

		try {
			JSONParser parser = new JSONParser();
			JSONObject json = (JSONObject) parser.parse(request);

			RequestSchedulerForNewAndModify requestSchedulerForNewAndModify = new RequestSchedulerForNewAndModify();

			String RequestId = json.get("requestId").toString();
			String version = json.get("version").toString();

			String result = requestSchedulerForNewAndModify.abortScheduledRequestDB(RequestId, version);

			obj.put(new String("output"), result);


		} catch (Exception e) {
			logger.error(e);
		}

		return Response.status(200).header("Access-Control-Allow-Origin", "*")
				.header("Access-Control-Allow-Headers", "origin, content-type, accept, authorization")
				.header("Access-Control-Allow-Credentials", "true")
				.header("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS, HEAD")
				.header("Access-Control-Max-Age", "1209600").entity(obj).build();

	}

}
