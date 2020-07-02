package com.techm.orion.rest;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.ws.rs.POST;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.techm.orion.pojo.CreateConfigRequestDCM;
import com.techm.orion.service.GetConfigurationTemplateService;

@Controller
@RequestMapping("/GetConfigurationTemplateForModify")
public class GetConfigurationTemplateForModify {
	private static final Logger logger = LogManager.getLogger(GetConfigurationTemplateForModify.class);

	@SuppressWarnings("unchecked")
	@POST
	@RequestMapping(value = "/createConfigurationTemplateModify", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
	@ResponseBody
	public JSONObject createConfigurationTemplateModify(@RequestBody String configRequest) {
		JSONObject obj = new JSONObject();
		String data = "";

		CreateConfigRequestDCM createConfigRequest = new CreateConfigRequestDCM();
		GetConfigurationTemplateService getConfigurationTemplateService = new GetConfigurationTemplateService();
		try {

			JSONParser parser = new JSONParser();
			JSONObject json = (JSONObject) parser.parse(configRequest);
			createConfigRequest.setTemplateID(json.get("templateId").toString());
			createConfigRequest.setCustomer(json.get("customer").toString());
			createConfigRequest.setSiteid(json.get("siteid").toString().toUpperCase());

			createConfigRequest.setDeviceType(json.get("deviceType").toString());
			createConfigRequest.setModel(json.get("model").toString());
			createConfigRequest.setOs(json.get("os").toString());
			createConfigRequest.setOsVersion(json.get("osVersion").toString());
			if (json.containsKey("vrfName")) {
				createConfigRequest.setVrfName(json.get("vrfName").toString());
			}
			createConfigRequest.setManagementIp(json.get("managementIp").toString());
			if (json.containsKey("enablePassword")) {
				createConfigRequest.setEnablePassword(json.get("enablePassword").toString());
			} else {
				createConfigRequest.setEnablePassword(null);
			}
			if (json.containsKey("banner")) {
				createConfigRequest.setBanner(json.get("banner").toString());
			} else {
				createConfigRequest.setBanner(null);
			}
			createConfigRequest.setRegion(json.get("region").toString().toUpperCase());
			createConfigRequest.setService(json.get("service").toString().toUpperCase());
			createConfigRequest.setHostname(json.get("hostname").toString().toUpperCase());
			// createConfigRequest.setVpn(json.get("VPN").toString());
			createConfigRequest.setVendor(json.get("vendor").toString().toUpperCase());

			JSONObject internetLvcrf = (JSONObject) json.get("internetLcVrf");
			if (internetLvcrf.containsKey("networkIp") && internetLvcrf.get("networkIp").toString() != "") {
				createConfigRequest.setNetworkIp(internetLvcrf.get("networkIp").toString());
			}
			if (internetLvcrf.containsKey("neighbor1")) {
				createConfigRequest.setNeighbor1(internetLvcrf.get("neighbor1").toString());
			}
			if (internetLvcrf.containsKey("neighbor2")) {
				createConfigRequest.setNeighbor2(internetLvcrf.get("neighbor2").toString().toUpperCase());
			} else {
				createConfigRequest.setNeighbor2(null);
			}
			if (internetLvcrf.containsKey("neighbor1_remoteAS")
					&& internetLvcrf.get("neighbor1_remoteAS").toString() != "") {
				createConfigRequest
						.setNeighbor1_remoteAS(internetLvcrf.get("neighbor1_remoteAS").toString().toUpperCase());
			}
			if (internetLvcrf.containsKey("neighbor2_remoteAS")
					&& internetLvcrf.get("neighbor2_remoteAS").toString() != "") {
				createConfigRequest
						.setNeighbor2_remoteAS(internetLvcrf.get("neighbor2_remoteAS").toString().toUpperCase());
			} else {
				createConfigRequest.setNeighbor2_remoteAS(null);
			}

			if (internetLvcrf.containsKey("networkIp_subnetMask")
					&& internetLvcrf.get("networkIp_subnetMask").toString() != "") {
				createConfigRequest.setNetworkIp_subnetMask(internetLvcrf.get("networkIp_subnetMask").toString());
			}
			if (internetLvcrf.containsKey("routingProtocol") && internetLvcrf.get("routingProtocol").toString() != "") {
				createConfigRequest.setRoutingProtocol(internetLvcrf.get("routingProtocol").toString().toUpperCase());
			}
			if (internetLvcrf.containsKey("bgpASNumber") && internetLvcrf.get("bgpASNumber").toString() != "") {
				createConfigRequest.setBgpASNumber(internetLvcrf.get("bgpASNumber").toString().toUpperCase());
			} else {
				createConfigRequest.setBgpASNumber("65000");
			}

			JSONObject c3p_interface = (JSONObject) json.get("deviceInterfaceSO");
			if (c3p_interface.containsKey("name")) {
				createConfigRequest.setName(c3p_interface.get("name").toString());
			}

			if (c3p_interface.containsKey("description")) {
				createConfigRequest.setDescription(c3p_interface.get("description").toString());
			} else {
				createConfigRequest.setDescription(null);

			}
			if (c3p_interface.containsKey("ip") && c3p_interface.get("ip").toString() != "") {
				createConfigRequest.setIp(c3p_interface.get("ip").toString());
			}
			if (c3p_interface.containsKey("ip") && c3p_interface.get("mask").toString() != "") {
				createConfigRequest.setMask(c3p_interface.get("mask").toString());
			}
			if (c3p_interface.containsKey("speed")) {
				createConfigRequest.setSpeed(c3p_interface.get("speed").toString());
			}
			if (c3p_interface.containsKey("bandwidth")) {

				createConfigRequest.setBandwidth(c3p_interface.get("bandwidth").toString());
			}
			if (c3p_interface.containsKey("encapsulation")) {
				createConfigRequest.setEncapsulation(c3p_interface.get("encapsulation").toString());
			}
			if (json.containsKey("misArPe")) {
				JSONObject mis = (JSONObject) json.get("misArPe");
				{
					createConfigRequest.setRouterVrfVpnDGateway(mis.get("routerVrfVpnDIp").toString());
					createConfigRequest.setRouterVrfVpnDIp(mis.get("routerVrfVpnDGateway").toString());
					createConfigRequest.setFastEthernetIp(mis.get("fastEthernetIp").toString());
				}
			}

			if (json.containsKey("snmpHostAddress")) {
				createConfigRequest.setSnmpHostAddress(json.get("snmpHostAddress").toString());
			} else {
				createConfigRequest.setSnmpHostAddress(null);
			}

			if (json.containsKey("snmpString")) {
				createConfigRequest.setSnmpString(json.get("snmpString").toString());
			} else {
				createConfigRequest.setSnmpString(null);
			}
			if (json.containsKey("loopBackType")) {
				createConfigRequest.setLoopBackType(json.get("loopBackType").toString());
			} else {
				createConfigRequest.setLoopBackType(null);
			}
			if (json.containsKey("loopbackIPaddress")) {
				createConfigRequest.setLoopbackIPaddress(json.get("loopbackIPaddress").toString());
			} else {
				createConfigRequest.setLoopbackIPaddress(null);
			}
			if (json.containsKey("loopbackSubnetMask")) {
				createConfigRequest.setLoopbackSubnetMask(json.get("loopbackSubnetMask").toString());
			} else {
				createConfigRequest.setLoopbackSubnetMask(null);
			}
			if (json.containsKey("lanInterface")) {
				createConfigRequest.setLanInterface(json.get("lanInterface").toString());
			}
			if (json.containsKey("lanIp")) {
				createConfigRequest.setLanIp(json.get("lanIp").toString());
			}
			if (json.containsKey("lanMaskAddress")) {
				createConfigRequest.setLanMaskAddress(json.get("lanMaskAddress").toString());
			}
			if (json.containsKey("lanDescription")) {
				createConfigRequest.setLanDescription(json.get("lanDescription").toString());
			}

			createConfigRequest.setDisplay_request_id(json.get("display_request_id").toString());
			createConfigRequest.setRequest_version(Double.parseDouble(json.get("request_version").toString()));
			createConfigRequest
					.setRequest_parent_version(Double.parseDouble(json.get("request_parent_version").toString()));

			createConfigRequest.setRequestId(createConfigRequest.getDisplay_request_id());
			DecimalFormat numberFormat = new DecimalFormat("#.0");
			createConfigRequest.setRequest_version(
					Double.parseDouble(numberFormat.format(createConfigRequest.getRequest_version() + 0.1)));

			Date date = new Date();
			SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

			String strDate = sdf.format(date);
			createConfigRequest.setRequestCreatedOn(strDate);

			data = getConfigurationTemplateService.getTemplateOnModify(createConfigRequest);

			if (data.contains("NoChanges")) {
				data = data.replace("NoChanges", "");
				obj.put(new String("output"), new String(data));
				obj.put(new String("changes"), new String("false"));
			} else {
				obj.put(new String("output"), new String(data));
			}
		} catch (Exception e) {
			logger.error(e);
		}

		return obj;

	}
}
