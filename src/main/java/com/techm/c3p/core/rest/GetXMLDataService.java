package com.techm.c3p.core.rest;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.core.Response;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.techm.c3p.core.dao.RequestInfoDao;
import com.techm.c3p.core.pojo.FirmwareUpgradeDetail;
import com.techm.c3p.core.utility.XMLToJSONConverter;

@Controller
@RequestMapping("/GetAllXMLData")
public class GetXMLDataService implements Observer {
	private static final Logger logger = LogManager.getLogger(GetXMLDataService.class);
	
	@Autowired
	private RequestInfoDao requestInfoDao;

	@POST
	@RequestMapping(value = "/get", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
	@ResponseBody
	public Response getAll(@RequestBody String requestParams) {

		JSONObject obj = new JSONObject();
		String jsonMessage = "";
		String jsonArray = "";
		// RequestInfoSO requestObj=null;
		String value = "";
		JSONObject flags = new JSONObject();

		org.json.JSONObject resultJSON = null;
		try {
			JSONParser parser = new JSONParser();
			JSONObject json = (JSONObject) parser.parse(requestParams);
			if (json.containsKey("orderID")) {
				XMLToJSONConverter xmlReader = new XMLToJSONConverter();
				value = json.get("orderID").toString();
				resultJSON = xmlReader.getDataForOrder(value);

			}

			if (resultJSON == null) {
				obj.put(new String("output"), "");
				obj.put(new String("errormessage"), "Invalid order id");
				obj.put(new String("status"), false);

			} else {
				org.json.JSONObject interfaceandroutingobject = resultJSON.getJSONObject("interfaceRoutingInfo");
				org.json.JSONObject routingobject = new org.json.JSONObject();
				org.json.JSONObject waninterfaceobject = new org.json.JSONObject();
				org.json.JSONObject vpnobject = new org.json.JSONObject();
				org.json.JSONObject loopbackobject = new org.json.JSONObject();
				org.json.JSONObject snmpobject = new org.json.JSONObject();
				org.json.JSONObject enablepasswordobject = new org.json.JSONObject();
				org.json.JSONObject bannerobject = new org.json.JSONObject();
				org.json.JSONObject informationObject = new org.json.JSONObject();
				org.json.JSONObject lanInterfaceObjectS = new org.json.JSONObject();
				if (interfaceandroutingobject.length() > 0) {
					routingobject = interfaceandroutingobject.getJSONObject("routing");
					if (routingobject.length() > 0) {
						flags.put("routingObj", true);
					} else {
						flags.put("routingObj", false);
					}
					waninterfaceobject = interfaceandroutingobject.getJSONObject("wanInterface");
					if (waninterfaceobject.length() > 0) {
						flags.put("wanInterfaceObj", true);

					} else {
						flags.put("wanInterfaceObj", false);

					}
					vpnobject = interfaceandroutingobject.getJSONObject("vpn");
					if (vpnobject.length() > 0) {
						flags.put("vpnObj", true);

					} else {
						flags.put("vpnObj", false);

					}
					loopbackobject = interfaceandroutingobject.getJSONObject("loopbackInterface");
					if (loopbackobject.length() > 0) {
						flags.put("loopbackInt", true);

					} else {
						flags.put("loopbackInt", false);

					}

					snmpobject = interfaceandroutingobject.getJSONObject("snmp");
					if (snmpobject.length() > 0) {
						flags.put("snmp", true);

					} else {
						flags.put("snmp", false);

					}
					int enablepasswordobjectS = 0;
					enablepasswordobjectS = interfaceandroutingobject.getInt("enablePassword");
					if (enablepasswordobjectS != 0) {
						flags.put("enablePassword", true);

					} else {
						flags.put("enablePassword", false);

					}
					String bannerobjectS = interfaceandroutingobject.getString("banner");
					if (!bannerobjectS.isEmpty()) {
						flags.put("banner", true);

					} else {
						flags.put("banner", false);

					}
					lanInterfaceObjectS = interfaceandroutingobject.getJSONObject("lanInterface");
					if (lanInterfaceObjectS.length() > 0) {
						flags.put("lanInterface", true);

					} else {
						flags.put("lanInterface", false);

					}

					interfaceandroutingobject.put("InformationStatus", flags);

				} else {
					flags.put("routingObj", false);
					flags.put("wanInterfaceObj", false);
					flags.put("vpnObj", false);
					flags.put("loopbackInt", false);
					flags.put("snmp", false);
					flags.put("enablePassword", false);
					flags.put("banner", false);
					flags.put("lanInterface", false);
					interfaceandroutingobject.put("InformationStatus", flags);

				}

				obj.put(new String("output"), resultJSON.toString());
				obj.put(new String("errormessage"), "");
				obj.put(new String("status"), true);

			}
			logger.info("JSON" + value);

		} catch (Exception e) {
			logger.error(e);
		}

		return Response.status(200).header("Access-Control-Allow-Origin", "*")
				.header("Access-Control-Allow-Headers", "origin, content-type, accept, authorization")
				.header("Access-Control-Allow-Credentials", "true")
				.header("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS, HEAD")
				.header("Access-Control-Max-Age", "1209600").entity(obj).build();
	}

	@GET
	@RequestMapping(value = "/get", method = RequestMethod.GET, produces = "application/json")
	public @ResponseBody JSONObject getAllOSUpgrade(@RequestParam String ipaddress, String zipcode) {

		JSONObject obj = new JSONObject();
		String jsonMessage = "";
		String jsonArray = "";
		// RequestInfoSO requestObj=null;
		String value = "";
		JSONObject flags = new JSONObject();

		org.json.JSONObject resultJSON = null;
		try {
			XMLToJSONConverter xmlReader = new XMLToJSONConverter();
			resultJSON = xmlReader.getDataForOrder(ipaddress, zipcode);

			JSONArray csrarray = new JSONArray();
			csrarray = resultJSON.getJSONArray("CSR");

			if (csrarray.length() > 0) {
				obj.put(new String("output"), csrarray.toString());
				obj.put(new String("errormessage"), "");
				obj.put(new String("status"), true);

			} else {
				obj.put(new String("output"), "");
				obj.put(new String("errormessage"), "Records not found.");
				obj.put(new String("status"), false);
			}

		} catch (Exception e) {
			logger.error(e);
		}

		return obj;

	}

	@SuppressWarnings({ "null", "unchecked" })
	@POST
	@RequestMapping(value = "/getDeviceDetail", method = RequestMethod.POST, produces = "application/json", consumes = "application/json")
	public Response getTestnamesAndVersionList(@RequestBody String request) throws ParseException {

		JSONObject obj = new JSONObject();

		String vendorName = "";
		String[] testNameFinal = null;

		JSONParser parser = new JSONParser();

		JSONObject json = (JSONObject) parser.parse(request);

		vendorName = (String) json.get("vendor");

		List<FirmwareUpgradeDetail> mainList = new ArrayList<FirmwareUpgradeDetail>();

		
		JSONArray array;

		mainList = requestInfoDao.findByVendorName(vendorName);

		String isCheck = null, secondCheck = null;
		int count = 0;
		JSONArray arrayElementOneArray = new JSONArray();

		for (int i = 0; i < mainList.size(); i++) {
			JSONObject arrayElementOneArrayElementTwo = new JSONObject();
			String[] testNameToSetArr = mainList.get(i).getVendor().split("_");

			obj.put("combination", testNameToSetArr[0]);

			if (testNameToSetArr.length >= 3) {
				isCheck = testNameToSetArr[1] + "_" + testNameToSetArr[2];
			} else {
				isCheck = testNameToSetArr[1];
			}
			if (isCheck.equals(secondCheck) || isCheck == null) {

				continue;

			}

			else if (count > 0) {

				JSONObject arrayElementOneArrayElementOne = new JSONObject();
				array = new JSONArray();
				arrayElementOneArrayElementOne.put("TestName", isCheck);

				mainList = requestInfoDao.findByVendorName(vendorName);
				for (int i1 = 0; i1 < mainList.size(); i1++) {
					// array.add(mainList.get(i1).getOs_version());
				}
				arrayElementOneArrayElementOne.put("versions", array);

				secondCheck = isCheck;
				// arrayElementOneArray.add(arrayElementOneArrayElementOne);

			} else {
				array = new JSONArray();
				arrayElementOneArrayElementTwo.put("TestName", isCheck);

				mainList = requestInfoDao.findByVendorName(vendorName);
				for (int i1 = 0; i1 < mainList.size(); i1++) {
					// array.add(mainList.get(i1).getOs_version());
				}

				arrayElementOneArrayElementTwo.put("versions", array);
				secondCheck = isCheck;
				count++;
				// arrayElementOneArray.add(arrayElementOneArrayElementTwo);
			}

		}
		obj.put("testNameList", arrayElementOneArray);

		return Response.status(200).header("Access-Control-Allow-Origin", "*")
				.header("Access-Control-Allow-Headers", "origin, content-type, accept, authorization")
				.header("Access-Control-Allow-Credentials", "true")
				.header("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS, HEAD")
				.header("Access-Control-Max-Age", "1209600").entity(obj).build();

	}

	@Override
	public void update(Observable o, Object arg) {
		// TODO Auto-generated method stub

	}

}
