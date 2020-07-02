package com.techm.orion.rest;

import java.io.IOException;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;

import javax.ws.rs.POST;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.techm.orion.pojo.CreateConfigRequestDCM;
import com.techm.orion.pojo.RequestInfoSO;
import com.techm.orion.service.DcmConfigService;
import com.techm.orion.webService.GetAllDetailsService;

@Controller
@RequestMapping("/ModifyConfiguration")
public class ModifyConfigurationService implements Observer {

	@POST
	@RequestMapping(value = "/modify", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
	@ResponseBody
	public JSONObject createConfigurationDcm(@RequestBody String configRequest) {
		DcmConfigService dcmConfigService = new DcmConfigService();
		JSONObject obj = new JSONObject();
		String jsonMessage = "";
		String requestIdForConfig = "";
		String res = "false";
		String data = "Failure";

		try {

			 JSONParser parser = new JSONParser();
			    JSONObject json = (JSONObject) parser.parse(configRequest);
			    
			    CreateConfigRequestDCM configReqToSendToC3pCode= new CreateConfigRequestDCM();
			    JSONObject editdata =(JSONObject) json.get("editeData");
			    configReqToSendToC3pCode.setTemplateID(editdata.get("templateId").toString());
			    configReqToSendToC3pCode.setCustomer(editdata.get("customer").toString());
			    configReqToSendToC3pCode.setSiteid(editdata.get("siteid").toString().toUpperCase());
			    //configReqToSendToC3pCode.setDeviceName(json.get("deviceName").toString());
			    configReqToSendToC3pCode.setDeviceType(editdata.get("deviceType").toString());
			    configReqToSendToC3pCode.setModel(editdata.get("model").toString());
			    configReqToSendToC3pCode.setOs(editdata.get("os").toString());
			    configReqToSendToC3pCode.setOsVersion(editdata.get("osVersion").toString());
			    if(editdata.containsKey("vrfName"))
			    {
			    configReqToSendToC3pCode.setVrfName(editdata.get("vrfName").toString());
			    }
			    configReqToSendToC3pCode.setManagementIp(editdata.get("managementIp").toString());
			    if(editdata.containsKey("enablePassword"))
			    {
			    configReqToSendToC3pCode.setEnablePassword(editdata.get("enablePassword").toString());
			    }
			    else
			    {
				    configReqToSendToC3pCode.setEnablePassword(null);
			    }
			    if(editdata.containsKey("banner"))
			    {
			    configReqToSendToC3pCode.setBanner(editdata.get("banner").toString());
			    }
			    else
			    {
			    	 configReqToSendToC3pCode.setBanner(null);
			    }
			    configReqToSendToC3pCode.setRegion(editdata.get("region").toString().toUpperCase());
			    configReqToSendToC3pCode.setService(editdata.get("service").toString().toUpperCase());
			    configReqToSendToC3pCode.setHostname(editdata.get("hostname").toString().toUpperCase());
			    //configReqToSendToC3pCode.setVpn(json.get("vpn").toString());
			    configReqToSendToC3pCode.setVendor(editdata.get("vendor").toString().toUpperCase());
			   // configReqToSendToC3pCode.setRequestId(json.get("requestId").toString());
			    
			    JSONObject internetLvcrf =(JSONObject) editdata.get("internetLcVrf");
			    if(internetLvcrf.containsKey("networkIp"))
			    {
			    configReqToSendToC3pCode.setNetworkIp(internetLvcrf.get("networkIp").toString());
			    }
			    if(internetLvcrf.containsKey("neighbor1") && !internetLvcrf.get("routingProtocol").toString().isEmpty() && (internetLvcrf.get("routingProtocol")!=null))if(internetLvcrf.containsKey("neighbor1"))
			    {
			    configReqToSendToC3pCode.setNeighbor1(internetLvcrf.get("neighbor1").toString());
			    }
			    if(internetLvcrf.containsKey("neighbor2"))
			    {
			    configReqToSendToC3pCode.setNeighbor2(internetLvcrf.get("neighbor2").toString().toUpperCase());
			    }
			    else
			    {
			    	configReqToSendToC3pCode.setNeighbor2(null);
			    }
			    if(internetLvcrf.containsKey("neighbor1_remoteAS")&& !(internetLvcrf.get("neighbor1_remoteAS")==null))
			    {
			    configReqToSendToC3pCode.setNeighbor1_remoteAS(internetLvcrf.get("neighbor1_remoteAS").toString().toUpperCase());
			    }
			    if(internetLvcrf.containsKey("neighbor2_remoteAS")&& !(internetLvcrf.get("neighbor2_remoteAS")==null))
			    {
			    configReqToSendToC3pCode.setNeighbor2_remoteAS(internetLvcrf.get("neighbor2_remoteAS").toString().toUpperCase());
			    }
			    else
			    {
			    	configReqToSendToC3pCode.setNeighbor2_remoteAS(null);
			    }
			    if(internetLvcrf.containsKey("networkIp_subnetMask"))
			    {
			    configReqToSendToC3pCode.setNetworkIp_subnetMask(internetLvcrf.get("networkIp_subnetMask").toString());
			    }
			    if(internetLvcrf.containsKey("routingProtocol"))
			    {
			    configReqToSendToC3pCode.setRoutingProtocol(internetLvcrf.get("routingProtocol").toString().toUpperCase());
			    }
			    if(internetLvcrf.containsKey("bgpASNumber")&& !(internetLvcrf.get("bgpASNumber")==null))
			    {
			    configReqToSendToC3pCode.setBgpASNumber(internetLvcrf.get("bgpASNumber").toString().toUpperCase());
			    }
			    JSONObject c3p_interface =(JSONObject) editdata.get("deviceInterfaceSO");
			    if(c3p_interface.containsKey("name"))
			    {
			    configReqToSendToC3pCode.setName(c3p_interface.get("name").toString());
			    }
			    else
			    {
				    configReqToSendToC3pCode.setName(null);

			    }
			    if(c3p_interface.containsKey("description"))
			    {
			    configReqToSendToC3pCode.setDescription(c3p_interface.get("description").toString());
			    }
			    else
			    {
				    configReqToSendToC3pCode.setDescription(null);

			    }
			    if(c3p_interface.containsKey("ip"))
			    {
			    configReqToSendToC3pCode.setIp(c3p_interface.get("ip").toString());
			    }
			    else
			    {
			    	 configReqToSendToC3pCode.setIp(null);
			    }
			    if(c3p_interface.containsKey("mask"))
			    {
			    configReqToSendToC3pCode.setMask(c3p_interface.get("mask").toString());
			    }
			  
			    if(c3p_interface.containsKey("speed"))
			    {
			    configReqToSendToC3pCode.setSpeed(c3p_interface.get("speed").toString());
			    }
			    if(c3p_interface.containsKey("bandwidth"))
			    {
				configReqToSendToC3pCode.setBandwidth(c3p_interface.get("bandwidth").toString());
			    }
			    if(c3p_interface.containsKey("encapsulation"))
			    {
			    configReqToSendToC3pCode.setEncapsulation(c3p_interface.get("encapsulation").toString());
			    }
			    if(editdata.containsKey("misArPe"))
			    {
			    JSONObject mis =(JSONObject) editdata.get("misArPe");
			    {
			    configReqToSendToC3pCode.setRouterVrfVpnDGateway(mis.get("routerVrfVpnDIp").toString());
			    configReqToSendToC3pCode.setRouterVrfVpnDIp(mis.get("routerVrfVpnDGateway").toString());
			    configReqToSendToC3pCode.setFastEthernetIp(mis.get("fastEthernetIp").toString());
			    }
			    }
			    if(editdata.containsKey("isAutoProgress"))
			    {
			    configReqToSendToC3pCode.setIsAutoProgress((Boolean) editdata.get("isAutoProgress"));
			    }
			    else
			    {
				configReqToSendToC3pCode.setIsAutoProgress(true);
			    }

			
			
			configReqToSendToC3pCode.setDisplay_request_id(editdata.get(
					"display_request_id").toString());
			configReqToSendToC3pCode.setRequest_version(Double.parseDouble(editdata
					.get("request_version").toString()));
			configReqToSendToC3pCode
					.setRequest_parent_version(Double.parseDouble(editdata.get(
							"request_parent_version").toString()));

			
			DecimalFormat numberFormat = new DecimalFormat("#.0");
			configReqToSendToC3pCode.setRequest_version(Double
					.parseDouble(numberFormat.format(configReqToSendToC3pCode
							.getRequest_version() + 0.1)));
			configReqToSendToC3pCode
					.setRequest_parent_version(configReqToSendToC3pCode
							.getRequest_parent_version());
			ObjectMapper mapper = new ObjectMapper();
			/*
			 * CreateConfigRequestDCM mappedObj =
			 * mapper.readValue(configRequest, CreateConfigRequestDCM.class);
			 */

			// to store creator name
			String request_creator_name = dcmConfigService.getLogedInUserName();
			configReqToSendToC3pCode
					.setRequest_creator_name(request_creator_name);
			
			//to get the scheduled time for the requestID
			if(editdata.containsKey("scheduledTime"))
		    {
				configReqToSendToC3pCode.setScheduledTime(editdata.get("scheduledTime").toString());
		    }
			

			
			 if(editdata.containsKey("snmpHostAddress"))
			  {
			configReqToSendToC3pCode.setSnmpHostAddress(editdata.get("snmpHostAddress").toString());
			  }
			  else
			    {
			    	configReqToSendToC3pCode.setSnmpHostAddress(null);
			    }
			  if(editdata.containsKey("snmpString"))
			  {
			configReqToSendToC3pCode.setSnmpString(editdata.get("snmpString").toString());
			  }
			  else
			    {
			    	configReqToSendToC3pCode.setSnmpString(null);
			    }
			  if(editdata.containsKey("loopBackType"))
			  {
			configReqToSendToC3pCode.setLoopBackType(editdata.get("loopBackType").toString());
			  }
			  else
			    {
			    	configReqToSendToC3pCode.setLoopBackType(null);
			    }
			  if(editdata.containsKey("loopbackIPaddress"))
			  {
			configReqToSendToC3pCode.setLoopbackIPaddress(editdata.get("loopbackIPaddress").toString());
			  }
			  else
			    {
			    	configReqToSendToC3pCode.setLoopbackIPaddress(null);
			    }
			  if(editdata.containsKey("loopbackSubnetMask"))
			  {
			configReqToSendToC3pCode.setLoopbackSubnetMask(editdata.get("loopbackSubnetMask").toString());
			  }
			  else
			    {
			    	configReqToSendToC3pCode.setLoopbackSubnetMask(null);
			    }
			  
			  JSONObject certificationTestFlag =(JSONObject) json.get("certificationOptionListFlags");
				if(certificationTestFlag!=null)
				{
				//flag test selection
				if(certificationTestFlag.get("Interfaces status").toString().equals("1"))
				{
				configReqToSendToC3pCode.setInterfaceStatus(certificationTestFlag.get("Interfaces status").toString()); 
				}
				
				if(certificationTestFlag.get("WAN Interface").toString().equals("1"))
				{
				configReqToSendToC3pCode.setWanInterface(certificationTestFlag.get("WAN Interface").toString()); 
				}
				
				if(certificationTestFlag.get("Platform & IOS").toString().equals("1"))
				{
				configReqToSendToC3pCode.setPlatformIOS(certificationTestFlag.get("Platform & IOS").toString()); 
				}
				
				if(certificationTestFlag.get("BGP neighbor").toString().equals("1"))
				{
				configReqToSendToC3pCode.setBGPNeighbor(certificationTestFlag.get("BGP neighbor").toString()); 
				}
				if(certificationTestFlag.get("Throughput").toString().equals("1"))
				{
				configReqToSendToC3pCode.setThroughputTest(certificationTestFlag.get("Throughput").toString()); 
				}
				if(certificationTestFlag.get("FrameLoss").toString().equals("1"))
				{
				configReqToSendToC3pCode.setFrameLossTest(certificationTestFlag.get("FrameLoss").toString()); 
				}
				if(certificationTestFlag.get("Latency").toString().equals("1"))
				{
				configReqToSendToC3pCode.setLatencyTest(certificationTestFlag.get("Latency").toString()); 
				}
				
				String bit=certificationTestFlag.get("Interfaces status").toString()+certificationTestFlag.get("WAN Interface").toString()+certificationTestFlag.get("Platform & IOS").toString()+
						certificationTestFlag.get("BGP neighbor").toString()+certificationTestFlag.get("Throughput").toString()+certificationTestFlag.get("FrameLoss").toString()+certificationTestFlag.get("Latency").toString();
				
				
				
				configReqToSendToC3pCode.setCertificationSelectionBit(bit);
				}
//LAN interface
				
				if(editdata.containsKey("lanInterface"))
			    {
			    configReqToSendToC3pCode.setLanInterface(editdata.get("lanInterface").toString());
			    }
				if(editdata.containsKey("lanIp"))
			    {
			    configReqToSendToC3pCode.setLanIp(editdata.get("lanIp").toString());
			    }
				if(editdata.containsKey("lanMaskAddress"))
			    {
			    configReqToSendToC3pCode.setLanMaskAddress(editdata.get("lanMaskAddress").toString());
			    }
				if(editdata.containsKey("lanDescription"))
			    {
			    configReqToSendToC3pCode.setLanDescription(editdata.get("lanDescription").toString());
			    }
			  Date date = new Date();
			    SimpleDateFormat  sdf = new SimpleDateFormat("dd/MM/yyyy");

			    String strDate = sdf.format(date);
			    configReqToSendToC3pCode.setRequestCreatedOn(strDate);
			
			
			Map<String, String> result = dcmConfigService
					.updateAlldetailsOnModify(configReqToSendToC3pCode);

			for (Map.Entry<String, String> entry : result.entrySet()) {
				if (entry.getKey() == "requestID") {
					requestIdForConfig = entry.getValue();

				}
				if (entry.getKey() == "result") {
					res = entry.getValue();
					if (res.equalsIgnoreCase("true")) {
						data = "Success";
					}

				}

			}
			obj.put(new String("output"), new String(data));
			obj.put(new String("requestId"), new String(requestIdForConfig));
			obj.put(new String("version"),
					configReqToSendToC3pCode.getRequest_version());

		} catch (Exception e) {
			System.out.println(e);
		}

		return obj;

	}

	
	@Override
	public void update(Observable arg0, Object arg1) {
		// TODO Auto-generated method stub

	}
}
