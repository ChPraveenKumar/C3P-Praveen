package com.techm.orion.utility;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.apache.commons.io.IOUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.XML;

public class XMLToJSONConverter {
	ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
	File FileToRead = null;

	public static String PROPERTIES_FILE = "TSA.properties";
	public static final Properties PROPERTIES = new Properties();

	String fileContent = "";

	@SuppressWarnings("deprecation")
	public JSONObject getDataForOrder(String orderID) {
		JSONObject xmlJSONObj = null;
		JSONObject resultJSON = null;
		try {
			FileToRead = new File(classLoader.getResource("test.xml").getFile());
			fileContent = IOUtils.toString(classLoader.getResourceAsStream("test.xml"));
			xmlJSONObj = XML.toJSONObject(fileContent);
			JSONObject objjj = xmlJSONObj.getJSONObject("RequestList");
			JSONArray array = objjj.getJSONArray("Request");
			for (int i = 0; i < array.length(); i++) {
				JSONObject arrayObj = array.getJSONObject(i);
				String orderid = arrayObj.getString("orderID");
				if (orderid.equalsIgnoreCase(orderID)) {
					resultJSON = arrayObj;
				}
			}
			/*
			 * JSONObject objjj=xmlJSONObj.getJSONObject("RequestList"); JSONArray
			 * array=objjj.getJSONArray("Request"); InternetLcVrfSO internetLvcrfSo=new
			 * InternetLcVrfSO(); DeviceInterfaceSO deviceInterfaceSo=new
			 * DeviceInterfaceSO(); for(int i=0; i<array.length();i++) { JSONObject
			 * arrayObj=array.getJSONObject(i); String
			 * orderid=arrayObj.getString("orderID"); if(orderid.equalsIgnoreCase(orderID))
			 * { JSONObject customerInfoObject=arrayObj.getJSONObject("customerInfo"); {
			 * result.setCustomer(customerInfoObject.getString("customer"));
			 * result.setService(customerInfoObject.getString("service"));
			 * result.setSite(customerInfoObject.getString("siteid"));
			 * result.setRegion(customerInfoObject.getString("region"));
			 * //result.setNetWorkType(customerInfoObject.getString("networkType"));
			 * hardcoded on UI } JSONObject
			 * deviceInfoObject=arrayObj.getJSONObject("deviceInfo"); {
			 * result.setDeviceType(deviceInfoObject.getString("deviceType"));
			 * result.setHostname(deviceInfoObject.getString("hostname"));
			 * result.setManagementIp(deviceInfoObject.getString("managementIp"));
			 * result.setOs(deviceInfoObject.getString("os"));
			 * result.setOsVersion(String.valueOf(deviceInfoObject.getInt("osVersion")));
			 * result.setVendor(deviceInfoObject.getString("vendor"));
			 * result.setModel(String.valueOf(deviceInfoObject.getInt("model")));
			 * 
			 * } JSONObject
			 * interfaceRoutingInfoObj=arrayObj.getJSONObject("interfaceRoutingInfo"); {
			 * result.setBanner(interfaceRoutingInfoObj.getString("banner"));
			 * result.setEnablePassword(interfaceRoutingInfoObj.getString("enablePassword"))
			 * ;
			 * 
			 * 
			 * JSONObject routingObj=interfaceRoutingInfoObj.getJSONObject("routing"); {
			 * internetLvcrfSo.setRoutingProtocol(routingObj.getString("routingProtocol"));
			 * internetLvcrfSo.setNetworkIp(routingObj.getString("networkIP"));
			 * internetLvcrfSo.setNetworkIp_subnetMask(routingObj.getString("networkMask"));
			 * internetLvcrfSo.setNeighbor1(routingObj.getString("neighbour1Ip"));
			 * internetLvcrfSo.setNeighbor2(routingObj.getString("neighbour2Ip"));
			 * internetLvcrfSo.setNeighbor1_remoteAS(routingObj.getString("neighbour1As"));
			 * internetLvcrfSo.setNeighbor2_remoteAS(routingObj.getString("neighbour2As"));
			 * internetLvcrfSo.setAS(routingObj.getString("asnumber"));
			 * result.setInternetLcVrf(internetLvcrfSo); }
			 * 
			 * JSONObject wanintobj=interfaceRoutingInfoObj.getJSONObject("wanInterface"); {
			 * deviceInterfaceSo.setBandwidth(wanintobj.getString("bandwidth"));
			 * deviceInterfaceSo.setEncapsulation(wanintobj.getString("encapsulation"));
			 * deviceInterfaceSo.setSpeed(wanintobj.getString("speed"));
			 * deviceInterfaceSo.setDescription(wanintobj.getString("description"));
			 * deviceInterfaceSo.setName(wanintobj.getString("wanInterfaceName"));
			 * deviceInterfaceSo.setIp(wanintobj.getString("wanInterfaceIP"));
			 * deviceInterfaceSo.setMask(wanintobj.getString("wanInterfaceIP")); }
			 * 
			 * }
			 * 
			 * 
			 * result.setLoopbackIPaddress(arrayObj.getString("loopbackIPaddress"));
			 * result.setLoopbackSubnetMask(arrayObj.getString("loopbackSubnetMask"));
			 * result.setLoopBackType(arrayObj.getString("loopBackType"));
			 * result.setSnmpHostAddress(arrayObj.getString("snmpHostAddress"));
			 * result.setSnmpString(arrayObj.getString("snmpString"));
			 * result.setVpn(arrayObj.getString("vpn"));
			 * result.setVrfName(arrayObj.getString("vrfName"));
			 * 
			 * 
			 * 
			 * 
			 * } }
			 */
		} catch (Exception e) {
			e.printStackTrace();
		}

		return resultJSON;
	}

	@SuppressWarnings("deprecation")
	public JSONObject getDataForOrder(String ip, String zip) {
		JSONObject xmlJSONObj = null;
		JSONObject resultJSON = new JSONObject();

		try {
			XMLToJSONConverter.loadProperties();
			String filePath = XMLToJSONConverter.PROPERTIES.getProperty("configurationFilesXML");

			FileToRead = new File(filePath);
			InputStream stream = new FileInputStream(FileToRead);
			fileContent = IOUtils.toString(stream);
			xmlJSONObj = XML.toJSONObject(fileContent);

			JSONArray csrarray = new JSONArray();
			csrarray = xmlJSONObj.getJSONArray("Entity");

			JSONArray responseFilteredArray = new JSONArray();

			if (ip == null) {
				ip = "";
			}
			if (zip == null) {
				zip = "";
			}

			if (!ip.isEmpty() && !zip.isEmpty()) {
				// This case will not come as user will input only either of parameters at a
				// time
			} else if (ip.isEmpty()) {
				// search on zip code-Direct match
				for (int i = 0; i < csrarray.length(); i++) {
					JSONObject csarrayobj = csrarray.getJSONObject(i);

					if (csarrayobj.get("zipcode").toString().equalsIgnoreCase(zip)) {
						responseFilteredArray.put(csarrayobj);
					}
				}
			} else if (zip.isEmpty()) {
				String site = null;
				// search on ip-find site of the ip and all other devices on that site
				for (int i = 0; i < csrarray.length(); i++) {
					JSONObject csarrayobj = csrarray.getJSONObject(i);

					if (csarrayobj.get("ip").toString().equalsIgnoreCase(ip)) {
						site = csarrayobj.get("siteid").toString();
					}
				}
				if (site != null) {
					for (int i = 0; i < csrarray.length(); i++) {
						JSONObject csarrayobj = csrarray.getJSONObject(i);

						if (csarrayobj.get("siteid").toString().equalsIgnoreCase(site)) {
							responseFilteredArray.put(csarrayobj);
						}
					}
				}

			}

			resultJSON.put("CSR", responseFilteredArray);

		} catch (Exception e) {
			e.printStackTrace();
		}

		return resultJSON;
	}

	public static boolean loadProperties() throws IOException {
		InputStream PropFile = Thread.currentThread().getContextClassLoader().getResourceAsStream(PROPERTIES_FILE);

		try {
			PROPERTIES.load(PropFile);
		} catch (IOException exc) {
			exc.printStackTrace();
			return false;
		}
		return false;
	}
}
