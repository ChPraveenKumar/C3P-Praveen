package com.techm.orion.rest;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.nio.file.Files;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;
import java.util.Properties;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.POST;
import javax.ws.rs.core.Response;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.json.XML;
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
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Maps;
import com.google.common.io.Resources;
import com.google.gson.Gson;
import com.hubspot.jinjava.Jinjava;
import com.jcraft.jsch.Channel;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.techm.orion.dao.RequestInfoDao;
import com.techm.orion.dao.RequestInfoDetailsDao;
import com.techm.orion.dao.TemplateManagementDao;
import com.techm.orion.entitybeans.TempVNFAttribEntity;
import com.techm.orion.entitybeans.TempVNFEntity;
import com.techm.orion.entitybeans.TestDetail;
import com.techm.orion.pojo.AttribCreateConfigPojo;
import com.techm.orion.pojo.CreateConfigRequest;
import com.techm.orion.pojo.CreateConfigRequestDCM;
import com.techm.orion.pojo.Global;
import com.techm.orion.pojo.RequestInfoPojo;
import com.techm.orion.pojo.RequestInfoSO;
import com.techm.orion.pojo.TemplateBasicConfigurationPojo;
import com.techm.orion.pojo.UserPojo;
import com.techm.orion.pojo.UserValidationResultDetailPojo;
import com.techm.orion.service.AttribCreateConfigService;
import com.techm.orion.service.DcmConfigService;
import com.techm.orion.service.PrevalidationTestServiceImpl;
import com.techm.orion.utility.InvokeFtl;
import com.techm.orion.utility.ODLClient;
import com.techm.orion.utility.TestStrategeyAnalyser;
import com.techm.orion.utility.TextReport;
import com.techm.orion.utility.VNFHelper;

@Controller
@RequestMapping("/vnfservices")
@CrossOrigin(origins = "http://localhost:4200", maxAge = 3600)
public class VnfConfigService implements Observer {
	public static String TSA_PROPERTIES_FILE = "TSA.properties";
	public static final Properties TSA_PROPERTIES = new Properties();
	@Autowired
	AttribCreateConfigService service;

	@Autowired
	RequestInfoDetailsDao requestDao;

	@Autowired
	RequestInfoDao requestInfoDao;

	@SuppressWarnings({ "unchecked", "null" })
	@POST
	@RequestMapping(value = "/generateConfiguration", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
	@ResponseBody
	public Response generateConfiguration(@RequestBody String params) {

		CreateConfigRequestDCM configReqToSendToC3pCode = new CreateConfigRequestDCM();
		JSONObject obj = new JSONObject();

		/*
		 * String fileName = "combinedFeatures.xml"; ClassLoader classLoader = new
		 * VnfConfigService().getClass().getClassLoader();
		 * 
		 * File file = new File(classLoader.getResource(fileName).getFile()); String
		 * content; try { content = new String(Files.readAllBytes(file.toPath()));
		 * DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		 * DocumentBuilder dBuilder = dbFactory.newDocumentBuilder(); Document doc =
		 * dBuilder.parse(file); doc.getDocumentElement().normalize();
		 * 
		 * System.out.println("Root element :" +
		 * doc.getDocumentElement().getNodeName()); NodeList nList =
		 * doc.getElementsByTagName("interface"); System.out.println("");
		 * 
		 * 
		 * } catch (IOException e) { // TODO Auto-generated catch block
		 * e.printStackTrace(); } catch (ParserConfigurationException e) { // TODO
		 * Auto-generated catch block e.printStackTrace(); } catch (SAXException e) { //
		 * TODO Auto-generated catch block e.printStackTrace(); }
		 */
		// 1. Check if request if for NF or SR type
		// Need to create a wrapper service for this
		// 2. If request type is SR Call sr flow call create config dcm service
		// 3. Else call service for VNF configuration
		JSONParser parser = new JSONParser();
		try {
			VnfConfigService.loadProperties();
			JSONObject json = (JSONObject) parser.parse(params);
			List<TempVNFEntity> list = new ArrayList<TempVNFEntity>();

			System.out.println("");
			if (json.containsKey("dynamicAttribs")) {
				JSONArray array = (JSONArray) json.get("dynamicAttribs");
				for (int i = 0; i < array.size(); i++) {
					JSONObject arrayObj = (JSONObject) array.get(i);
					TempVNFEntity vnfObj = new TempVNFEntity();
					vnfObj.setFeature_name(arrayObj.get("featureName").toString());
					List<TempVNFAttribEntity> attribList = new ArrayList<TempVNFAttribEntity>();

					if (arrayObj.containsKey("featureAttributes")) {
						JSONArray attribArray = (JSONArray) arrayObj.get("featureAttributes");
						for (int j = 0; j < attribArray.size(); j++) {
							JSONObject attribJsonObj = (JSONObject) attribArray.get(j);
							TempVNFAttribEntity attribEntity = new TempVNFAttribEntity();
							attribEntity.setAttrib_name(attribJsonObj.get("label").toString());
							attribEntity.setAttrib_value(attribJsonObj.get("value").toString());
							attribList.add(attribEntity);

						}
					}
					if (attribList.size() > 0) {
						vnfObj.setAttrib_list(attribList);
					}
					list.add(vnfObj);
				}

				String xmlFileDownloadPath = VnfConfigService.TSA_PROPERTIES.getProperty("staticXMLVnf");

				// once we have list of features that we need to configure we
				// must go ahead and put the values in hard coded xml.
				String fileName = "combinedFeatures.xml";
				ClassLoader classLoader = new VnfConfigService().getClass().getClassLoader();

				File file = new File(xmlFileDownloadPath);
				String contents = new String(Files.readAllBytes(file.toPath()));

				Jinjava jinjava = new Jinjava();
				Map<String, Object> context = Maps.newHashMap();

				// context.put("LOOPBACK_INDEX", "4");

				org.json.JSONObject xmlobj = XML.toJSONObject(contents);
				org.json.JSONObject configobj = xmlobj.getJSONObject("config");
				org.json.JSONObject nativeobj = configobj.getJSONObject("native");

				for (int i = 0; i < list.size(); i++) {
					TempVNFEntity entityObj = list.get(i);
					List<TempVNFAttribEntity> attribList = entityObj.getAttrib_list();

					if (entityObj.getFeature_name().equalsIgnoreCase("Loopback")) {
						org.json.JSONObject interfaceObj = nativeobj.getJSONObject("interface");
						org.json.JSONObject loopbackObj = interfaceObj.getJSONObject("Loopback");
						for (int j = 0; j < attribList.size(); j++) {
							if (attribList.get(j).getAttrib_name().equalsIgnoreCase("name")) {

								context.put(
										loopbackObj.get("name").toString().replace("}}", "").replace("{{", "").trim(),
										attribList.get(j).getAttrib_value());
								// loopbackObj.put("name",attribList.get(j).getAttrib_value());
							} else if (attribList.get(j).getAttrib_name().equalsIgnoreCase("description")) {
								context.put(loopbackObj.get("description").toString().replace("}}", "")
										.replace("{{", "").trim(), attribList.get(j).getAttrib_value());

								// loopbackObj.put("description",attribList.get(j).getAttrib_value());
							} else if (attribList.get(j).getAttrib_name().equalsIgnoreCase("ip")) {
								org.json.JSONObject ipObj = loopbackObj.getJSONObject("ip");
								org.json.JSONObject addressesObj = ipObj.getJSONObject("address");
								org.json.JSONObject primaryObj = addressesObj.getJSONObject("primary");

								context.put(
										primaryObj.get("address").toString().replace("}}", "").replace("{{", "").trim(),
										attribList.get(j).getAttrib_value());

								// primaryObj.put("address",attribList.get(j).getAttrib_value());
							} else if (attribList.get(j).getAttrib_name().equalsIgnoreCase("SubnetMask")) {
								org.json.JSONObject ipObj = loopbackObj.getJSONObject("ip");
								org.json.JSONObject addressesObj = ipObj.getJSONObject("address");
								org.json.JSONObject primaryObj = addressesObj.getJSONObject("primary");
								context.put(
										primaryObj.get("mask").toString().replace("}}", "").replace("{{", "").trim(),
										attribList.get(j).getAttrib_value());

								// primaryObj.put("mask",attribList.get(j).getAttrib_value());
							}
						}

					} else if (entityObj.getFeature_name().equalsIgnoreCase("Multilink")) {
						org.json.JSONObject interfaceObj = nativeobj.getJSONObject("interface");
						org.json.JSONObject loopbackObj = interfaceObj.getJSONObject("Multilink");
						for (int j = 0; j < attribList.size(); j++) {
							if (attribList.get(j).getAttrib_name().equalsIgnoreCase("name")) {
								context.put(
										loopbackObj.get("name").toString().replace("}}", "").replace("{{", "").trim(),
										attribList.get(j).getAttrib_value());

								// loopbackObj.put("name",attribList.get(j).getAttrib_value());
							} else if (attribList.get(j).getAttrib_name().equalsIgnoreCase("description")) {
								context.put(loopbackObj.get("description").toString().replace("}}", "")
										.replace("{{", "").trim(), attribList.get(j).getAttrib_value());

								// loopbackObj.put("description",attribList.get(j).getAttrib_value());
							} else if (attribList.get(j).getAttrib_name().equalsIgnoreCase("ip")) {
								org.json.JSONObject ipObj = loopbackObj.getJSONObject("ip");
								org.json.JSONObject addressesObj = ipObj.getJSONObject("address");
								org.json.JSONObject primaryObj = addressesObj.getJSONObject("primary");
								context.put(
										primaryObj.get("address").toString().replace("}}", "").replace("{{", "").trim(),
										attribList.get(j).getAttrib_value());

								// primaryObj.put("address",attribList.get(j).getAttrib_value());
							} else if (attribList.get(j).getAttrib_name().equalsIgnoreCase("SubnetMask")) {
								org.json.JSONObject ipObj = loopbackObj.getJSONObject("ip");
								org.json.JSONObject addressesObj = ipObj.getJSONObject("address");
								org.json.JSONObject primaryObj = addressesObj.getJSONObject("primary");
								context.put(
										primaryObj.get("mask").toString().replace("}}", "").replace("{{", "").trim(),
										attribList.get(j).getAttrib_value());

								// primaryObj.put("mask",attribList.get(j).getAttrib_value());
							}
						}
					} else if (entityObj.getFeature_name().equalsIgnoreCase("Virtual-Template")) {
						org.json.JSONObject interfaceObj = nativeobj.getJSONObject("interface");
						org.json.JSONObject loopbackObj = interfaceObj.getJSONObject("Virtual-Template");
						for (int j = 0; j < attribList.size(); j++) {
							if (attribList.get(j).getAttrib_name().equalsIgnoreCase("name")) {
								context.put(
										loopbackObj.get("name").toString().replace("}}", "").replace("{{", "").trim(),
										attribList.get(j).getAttrib_value());

								// loopbackObj.put("name",attribList.get(j).getAttrib_value());
							} else if (attribList.get(j).getAttrib_name().equalsIgnoreCase("description")) {
								context.put(loopbackObj.get("description").toString().replace("}}", "")
										.replace("{{", "").trim(), attribList.get(j).getAttrib_value());

								// loopbackObj.put("description",attribList.get(j).getAttrib_value());
							} else if (attribList.get(j).getAttrib_name().equalsIgnoreCase("ip")) {
								org.json.JSONObject ipObj = loopbackObj.getJSONObject("ip");
								org.json.JSONObject addressesObj = ipObj.getJSONObject("address");
								org.json.JSONObject primaryObj = addressesObj.getJSONObject("primary");
								context.put(
										primaryObj.get("address").toString().replace("}}", "").replace("{{", "").trim(),
										attribList.get(j).getAttrib_value());

								// primaryObj.put("address",attribList.get(j).getAttrib_value());
							} else if (attribList.get(j).getAttrib_name().equalsIgnoreCase("SubnetMask")) {
								org.json.JSONObject ipObj = loopbackObj.getJSONObject("ip");
								org.json.JSONObject addressesObj = ipObj.getJSONObject("address");
								org.json.JSONObject primaryObj = addressesObj.getJSONObject("primary");
								context.put(
										primaryObj.get("mask").toString().replace("}}", "").replace("{{", "").trim(),
										attribList.get(j).getAttrib_value());

								// primaryObj.put("mask",attribList.get(j).getAttrib_value());
							}
						}
					} else if (entityObj.getFeature_name().equalsIgnoreCase("BGP")) {
						org.json.JSONObject routerObj = nativeobj.getJSONObject("router");
						org.json.JSONObject bgpObj = routerObj.getJSONObject("bgp");
						for (int j = 0; j < attribList.size(); j++) {
							if (attribList.get(j).getAttrib_name().equalsIgnoreCase("id")) {
								context.put(bgpObj.get("id").toString().replace("}}", "").replace("{{", "").trim(),
										attribList.get(j).getAttrib_value());

								// bgpObj.put("id",attribList.get(j).getAttrib_value());
							} else if (attribList.get(j).getAttrib_name().equalsIgnoreCase("bgpNeighbourIp")) {
								org.json.JSONObject neighbourObj = bgpObj.getJSONObject("neighbor");
								context.put(
										neighbourObj.get("id").toString().replace("}}", "").replace("{{", "").trim(),
										attribList.get(j).getAttrib_value());

								// neighbourObj.put("id",attribList.get(j).getAttrib_value());
							} else if (attribList.get(j).getAttrib_name().equalsIgnoreCase("bgpAsNumber")) {
								org.json.JSONObject neighbourObj = bgpObj.getJSONObject("neighbor");
								context.put(neighbourObj.get("remote-as").toString().replace("}}", "").replace("{{", "")
										.trim(), attribList.get(j).getAttrib_value());

								// neighbourObj.put("remote-as",attribList.get(j).getAttrib_value());
							} else if (attribList.get(j).getAttrib_name().equalsIgnoreCase("bgpNeighbourNetworkIp")) {
								org.json.JSONObject networkObj = bgpObj.getJSONObject("network");
								context.put(
										networkObj.get("number").toString().replace("}}", "").replace("{{", "").trim(),
										attribList.get(j).getAttrib_value());

								// networkObj.put("number",attribList.get(j).getAttrib_value());
							} else if (attribList.get(j).getAttrib_name().equalsIgnoreCase("bgpNeighbourNetworkMask")) {
								org.json.JSONObject networkObj = bgpObj.getJSONObject("network");
								context.put(
										networkObj.get("mask").toString().replace("}}", "").replace("{{", "").trim(),
										attribList.get(j).getAttrib_value());

								// networkObj.put("mask",attribList.get(j).getAttrib_value());
							}
						}
					}
				}
				String renderedTemplate = jinjava.render(contents, context);

				String new_xml_data = XML.toString(xmlobj);
				String formattedXML = prettyPrintXml(renderedTemplate);
				obj.put("data", formattedXML);

				System.out.println(obj.toString());
				/*
				 * DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
				 * DocumentBuilder docBuilder = docFactory.newDocumentBuilder(); Document doc =
				 * docBuilder.parse(file);
				 * 
				 * for(int j=0;j<list.size();j++) { TempVNFEntity ent=list.get(j);
				 * if(ent.getFeature_name().equalsIgnoreCase("Loopback")) {
				 * List<TempVNFAttribEntity>attribList=new ArrayList<TempVNFAttribEntity>();
				 * attribList=ent.getAttrib_list();
				 * 
				 * Node interfaceElement = doc.getElementsByTagName("interface").item(0);
				 * NodeList interfaceList = interfaceElement.getChildNodes(); for (int i = 0; i
				 * < interfaceList.getLength(); i++) {
				 * 
				 * Node node = interfaceList.item(i);
				 * if(node.getNodeName().equalsIgnoreCase("Loopback")) {
				 * 
				 * NodeList loopbackChildern=node.getChildNodes(); for(int loopbackIterator=0;
				 * loopbackIterator<loopbackChildern.getLength ();loopbackIterator++) { Node
				 * loopbackChildNode=loopbackChildern.item(loopbackIterator); System
				 * .out.println("Loopback child node"+loopbackChildNode.getNodeName ());
				 * if(loopbackChildNode.getNodeName().equalsIgnoreCase("name")) { for(int
				 * attribListIte=0;attribListIte<attribList.size();attribListIte ++) {
				 * if(attribList.get(attribListIte).getAttrib_name(). equalsIgnoreCase("name"))
				 * { loopbackChildNode.setNodeValue(attribList
				 * .get(attribListIte).getAttrib_value()); } } } else if(loopbackChildNode
				 * .getNodeName().equalsIgnoreCase("description")) { if(loopbackChildNode
				 * .getNodeName().equalsIgnoreCase("description")) { for(int attribListIte
				 * =0;attribListIte<attribList.size();attribListIte++) { if(attribList
				 * .get(attribListIte).getAttrib_name().equalsIgnoreCase ("description")) {
				 * loopbackChildNode.setNodeValue(attribList.get
				 * (attribListIte).getAttrib_value()); } } } } else if(loopbackChildNode
				 * .getNodeName().equalsIgnoreCase("description")) { if(loopbackChildNode
				 * .getNodeName().equalsIgnoreCase("description")) { for(int attribListIte
				 * =0;attribListIte<attribList.size();attribListIte++) { if(attribList
				 * .get(attribListIte).getAttrib_name().equalsIgnoreCase ("description")) {
				 * loopbackChildNode.setNodeValue(attribList.get
				 * (attribListIte).getAttrib_value()); } } } }
				 * 
				 * } } } }
				 */

			}

			System.out.println("");

		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return Response.status(200).header("Access-Control-Allow-Origin", "*")
				.header("Access-Control-Allow-Headers", "origin, content-type, accept, authorization")
				.header("Access-Control-Allow-Credentials", "true")
				.header("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS, HEAD")
				.header("Access-Control-Max-Age", "1209600").entity(obj).build();
	}

	@SuppressWarnings({ "unchecked", "null" })
	@POST
	@RequestMapping(value = "/prevalidateODL", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
	@ResponseBody
	public JSONObject deviceReachabilityVNFOdl(@RequestBody String params) {
		RequestInfoPojo requestinfo = new RequestInfoPojo();

		JSONObject obj = new JSONObject();
		DcmConfigService dcmConfigService = new DcmConfigService();
		String requestIdForConfig = "";
		CreateConfigRequest createConfigRequest = new CreateConfigRequest();
		RequestInfoDao requestInfoDao = new RequestInfoDao();
		PrevalidationTestServiceImpl prevalidationTestServiceImpl = new PrevalidationTestServiceImpl();
		Boolean value = false;
		JSONParser parser = new JSONParser();
		JSONObject json;
		ODLClient odlClient = new ODLClient();
		String jsonArray = "";
		InvokeFtl invokeFtl = new InvokeFtl();
		try {

			VnfConfigService.loadProperties();

			json = (JSONObject) parser.parse(params);

			// Require requestId and version from camunda
			String RequestId = json.get("requestId").toString();
			String version = json.get("version").toString();
			createConfigRequest = requestInfoDao.getRequestDetailFromDBForVersion(RequestId, version);
			requestinfo = requestDao.getRequestDetailTRequestInfoDBForVersion(RequestId, version);

			if (createConfigRequest.getManagementIp() != null && !createConfigRequest.getManagementIp().equals("")) {
				// Load payload from resources (payload is hardcoded)
				VNFHelper helper = new VNFHelper();
				String payload = helper.loadXMLPayload("ODL_new_device.xml");
				String mountStatus = null;
				// Check if device is already mounted and mount it same url is used for both
				String output = odlClient.doPostNetworkTopology(
						"http://10.62.0.119:8181/restconf/config/network-topology:network-topology/topology/topology-netconf/node/controller-config/yang-ext:mount/config:modules ",
						payload, "application/xml");

				if (output.equalsIgnoreCase("data-exists"))// if device already exisits or device mounted successfully
				{
					// device already exists or we have mounted the device check if it has been
					// successfully mounted
					String result = odlClient.doGetNodeinTopology(
							"http://10.62.0.119:8181/restconf/operational/network-topology:network-topology/");
					if (result.equalsIgnoreCase("Success")) {
						// device mounted successfully
						mountStatus = "Pass";
						// Check for Vendor, Device model, os, os version
						String host = createConfigRequest.getManagementIp();
						UserPojo userPojo = new UserPojo();
						userPojo = requestInfoDao.getRouterCredentials();

						String user = userPojo.getUsername();
						String password = userPojo.getPassword();
						String port = VnfConfigService.TSA_PROPERTIES.getProperty("portSSH");

						System.out.println("port " + port + "host " + host);
						JSch jsch = new JSch();
						Channel channel = null;
						Session session = jsch.getSession("c3pteam", "10.62.0.27", Integer.parseInt(port));
						Properties config = new Properties();
						config.put("StrictHostKeyChecking", "no");
						session.setConfig(config);
						session.setPassword("csr1000v");
						session.connect();
						try {
							Thread.sleep(10000);
						} catch (Exception ee) {
						}
						channel = session.openChannel("shell");
						OutputStream ops = channel.getOutputStream();

						PrintStream ps = new PrintStream(ops, true);
						System.out.println("Channel Connected to machine " + host + " server");
						channel.connect();
						InputStream input = channel.getInputStream();
						ps.println("show version");
						try {
							Thread.sleep(5000);
						} catch (Exception ee) {
						}

						requestInfoDao.addCertificationTestForRequest(createConfigRequest.getRequestId(),
								Double.toString(createConfigRequest.getRequest_version()), "1");
						printVersionversionInfo(input, channel, createConfigRequest.getRequestId(),
								Double.toString(createConfigRequest.getRequest_version()));

						value = prevalidationTestServiceImpl.PreValidation(createConfigRequest,
								Double.toString(createConfigRequest.getRequest_version()), mountStatus);
						if (value) {

							// changes for testing strategy
							List<Boolean> results = null;
							RequestInfoDao dao = new RequestInfoDao();
							List<TestDetail> listOfTests = new ArrayList<TestDetail>();
							List<TestDetail> finallistOfTests = new ArrayList<TestDetail>();
							TestDetail test = new TestDetail();
							listOfTests = dao.findTestFromTestStrategyDB(createConfigRequest.getModel(),
									createConfigRequest.getDeviceType(), createConfigRequest.getOs(),
									createConfigRequest.getOsVersion(), createConfigRequest.getVendor(),
									createConfigRequest.getRegion(), "Device Prevalidation");
							List<TestDetail> selectedTests = dao.findSelectedTests(createConfigRequest.getRequestId(),
									"Device Prevalidation");
							if (selectedTests.size() > 0) {
								for (int i = 0; i < listOfTests.size(); i++) {
									for (int j = 0; j < selectedTests.size(); j++) {
										if (selectedTests.get(j).getTestName()
												.equalsIgnoreCase(listOfTests.get(i).getTestName())) {
											finallistOfTests.add(listOfTests.get(j));
										}
									}
								}
							}
							if (finallistOfTests.size() > 0) {
								results = new ArrayList<Boolean>();
								for (int i = 0; i < finallistOfTests.size(); i++) {

									// conduct and analyse the tests
									ps.println("terminal length 0");
									ps.println(finallistOfTests.get(i).getTestCommand());
									try {
										Thread.sleep(6000);
									} catch (Exception ee) {
									}

									// printResult(input,
									// channel,configRequest.getRequestId(),Double.toString(configRequest.getRequest_version()));
									Boolean res = TestStrategeyAnalyser.printAndAnalyse(input, channel,
											createConfigRequest.getRequestId(),
											Double.toString(createConfigRequest.getRequest_version()),
											finallistOfTests.get(i), "Device Prevalidation");
									results.add(res);
								}
								if (results != null) {
									for (int i = 0; i < results.size(); i++) {
										if (!results.get(i)) {
											value = false;
											break;
										}
									}
								}
							} else {
								// No new device prevalidation test added
							}

						}
						channel.disconnect();
						session.disconnect();
						jsonArray = new Gson().toJson(value);
						obj.put(new String("output"), jsonArray);
					} else {
						// device not mounted successfully
						jsonArray = new Gson().toJson(value);
						obj.put(new String("output"), jsonArray);
						requestInfoDao.editRequestforReportWebserviceInfo(createConfigRequest.getRequestId(),
								Double.toString(createConfigRequest.getRequest_version()), "Application_test", "2",
								"Failure");
						requestInfoDao.addCertificationTestForRequest(createConfigRequest.getRequestId(),
								Double.toString(createConfigRequest.getRequest_version()), "2");
						String response = "";
						String responseDownloadPath = "";

						try {
							response = invokeFtl.generatePrevalidationResultFileFailureODLMount(createConfigRequest);

							responseDownloadPath = VnfConfigService.TSA_PROPERTIES.getProperty("responseDownloadPath");
							TextReport.writeFile(responseDownloadPath,
									createConfigRequest.getRequestId() + "V"
											+ Double.toString(createConfigRequest.getRequest_version())
											+ "_prevalidationTest.txt",
									response);
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}

				} else {
				}
			} else if (requestinfo.getManagementIp() != null && !requestinfo.getManagementIp().equals("")) {
				VNFHelper helper = new VNFHelper();
				String payload = helper.loadXMLPayload("ODL_new_device.xml");
				String mountStatus = null;
				// Check if device is already mounted and mount it same url is used for both
				String output = odlClient.doPostNetworkTopology(
						"http://10.62.0.119:8181/restconf/config/network-topology:network-topology/topology/topology-netconf/node/controller-config/yang-ext:mount/config:modules ",
						payload, "application/xml");

				if (output.equalsIgnoreCase("data-exists"))// if device already exisits or device mounted successfully
				{
					// device already exists or we have mounted the device check if it has been
					// successfully mounted
					String result = odlClient.doGetNodeinTopology(
							"http://10.62.0.119:8181/restconf/operational/network-topology:network-topology/");
					if (result.equalsIgnoreCase("Success")) {
						// device mounted successfully
						mountStatus = "Pass";
						// Check for Vendor, Device model, os, os version
						String host = requestinfo.getManagementIp();
						UserPojo userPojo = new UserPojo();
						userPojo = requestInfoDao.getRouterCredentials();

						String user = userPojo.getUsername();
						String password = userPojo.getPassword();
						String port = VnfConfigService.TSA_PROPERTIES.getProperty("portSSH");

						System.out.println("port " + port + "host " + host);
						JSch jsch = new JSch();
						Channel channel = null;
						Session session = jsch.getSession("c3pteam", "10.62.0.27", Integer.parseInt(port));
						Properties config = new Properties();
						config.put("StrictHostKeyChecking", "no");
						session.setConfig(config);
						session.setPassword("csr1000v");
						session.connect();
						try {
							Thread.sleep(10000);
						} catch (Exception ee) {
						}
						channel = session.openChannel("shell");
						OutputStream ops = channel.getOutputStream();

						PrintStream ps = new PrintStream(ops, true);
						System.out.println("Channel Connected to machine " + host + " server");
						channel.connect();
						InputStream input = channel.getInputStream();
						ps.println("show version");
						try {
							Thread.sleep(5000);
						} catch (Exception ee) {
						}

						requestInfoDao.addCertificationTestForRequest(requestinfo.getAlphanumericReqId(),
								Double.toString(requestinfo.getRequestVersion()), "1");
						printVersionversionInfo(input, channel, requestinfo.getAlphanumericReqId(),
								Double.toString(requestinfo.getRequestVersion()));

						value = prevalidationTestServiceImpl.PreValidation(requestinfo,
								Double.toString(requestinfo.getRequestVersion()), mountStatus);
						if (value) {

							// changes for testing strategy
							List<Boolean> results = null;
							RequestInfoDao dao = new RequestInfoDao();
							List<TestDetail> listOfTests = new ArrayList<TestDetail>();
							List<TestDetail> finallistOfTests = new ArrayList<TestDetail>();
							TestDetail test = new TestDetail();
							listOfTests = dao.findTestFromTestStrategyDB(requestinfo.getModel(),
									requestinfo.getDeviceType(), requestinfo.getOs(), requestinfo.getOsVersion(),
									requestinfo.getVendor(), requestinfo.getRegion(), "Device Prevalidation");
							List<TestDetail> selectedTests = dao.findSelectedTests(requestinfo.getAlphanumericReqId(),
									"Device Prevalidation");
							if (selectedTests.size() > 0) {
								for (int i = 0; i < listOfTests.size(); i++) {
									for (int j = 0; j < selectedTests.size(); j++) {
										if (selectedTests.get(j).getTestName()
												.equalsIgnoreCase(listOfTests.get(i).getTestName())) {
											finallistOfTests.add(listOfTests.get(j));
										}
									}
								}
							}
							if (finallistOfTests.size() > 0) {
								results = new ArrayList<Boolean>();
								for (int i = 0; i < finallistOfTests.size(); i++) {

									// conduct and analyse the tests
									ps.println("terminal length 0");
									ps.println(finallistOfTests.get(i).getTestCommand());
									try {
										Thread.sleep(6000);
									} catch (Exception ee) {
									}

									// printResult(input,
									// channel,configRequest.getRequestId(),Double.toString(configRequest.getRequest_version()));
									Boolean res = TestStrategeyAnalyser.printAndAnalyse(input, channel,
											requestinfo.getAlphanumericReqId(),
											Double.toString(requestinfo.getRequestVersion()), finallistOfTests.get(i),
											"Device Prevalidation");
									results.add(res);
								}
								if (results != null) {
									for (int i = 0; i < results.size(); i++) {
										if (!results.get(i)) {
											value = false;
											break;
										}
									}
								}
							} else {
								// No new device prevalidation test added
							}

						}
						channel.disconnect();
						session.disconnect();
						jsonArray = new Gson().toJson(value);
						obj.put(new String("output"), jsonArray);
					} else {
						// device not mounted successfully
						jsonArray = new Gson().toJson(value);
						obj.put(new String("output"), jsonArray);
						requestDao.editRequestforReportWebserviceInfo(requestinfo.getAlphanumericReqId(),
								Double.toString(requestinfo.getRequestVersion()), "Application_test", "2", "Failure");
						requestInfoDao.addCertificationTestForRequest(requestinfo.getAlphanumericReqId(),
								Double.toString(requestinfo.getRequestVersion()), "2");
						String response = "";
						String responseDownloadPath = "";

						try {
							response = invokeFtl.generatePrevalidationResultFileFailureODLMount(requestinfo);

							responseDownloadPath = VnfConfigService.TSA_PROPERTIES.getProperty("responseDownloadPath");
							TextReport.writeFile(responseDownloadPath, requestinfo.getAlphanumericReqId() + "V"
									+ Double.toString(requestinfo.getRequestVersion()) + "_prevalidationTest.txt",
									response);
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}

				} else {
				}

			}
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (java.text.ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NumberFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JSchException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e1) {
			if (createConfigRequest.getManagementIp() != null && !createConfigRequest.getManagementIp().equals("")) {

				// TODO Auto-generated catch block

				System.out.print(e1.getMessage());

				if (e1.getMessage().contains("invalid server's version string")
						|| e1.getMessage().contains("Auth fail")) {
					jsonArray = new Gson().toJson(value);
					obj.put(new String("output"), jsonArray);
					requestInfoDao.editRequestforReportWebserviceInfo(createConfigRequest.getRequestId(),
							Double.toString(createConfigRequest.getRequest_version()), "Application_test", "2",
							"Failure");
					requestInfoDao.addCertificationTestForRequest(createConfigRequest.getRequestId(),
							Double.toString(createConfigRequest.getRequest_version()), "2_Authentication");
					String response = "";
					String responseDownloadPath = "";

					try {
						response = invokeFtl.generateAuthenticationFailure(createConfigRequest);

						responseDownloadPath = VnfConfigService.TSA_PROPERTIES.getProperty("responseDownloadPath");
						TextReport.writeFile(responseDownloadPath, createConfigRequest.getRequestId() + "V"
								+ Double.toString(createConfigRequest.getRequest_version()) + "_prevalidationTest.txt",
								response);
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

				} else {
					jsonArray = new Gson().toJson(value);
					obj.put(new String("output"), jsonArray);
					requestInfoDao.editRequestforReportWebserviceInfo(createConfigRequest.getRequestId(),
							Double.toString(createConfigRequest.getRequest_version()), "Application_test", "2",
							"Failure");
					requestInfoDao.addCertificationTestForRequest(createConfigRequest.getRequestId(),
							Double.toString(createConfigRequest.getRequest_version()), "2");
					String response = "";
					String responseDownloadPath = "";

					try {
						response = invokeFtl.generatePrevalidationResultFileFailure(createConfigRequest);

						responseDownloadPath = VnfConfigService.TSA_PROPERTIES.getProperty("responseDownloadPath");
						TextReport.writeFile(responseDownloadPath, createConfigRequest.getRequestId() + "V"
								+ Double.toString(createConfigRequest.getRequest_version()) + "_prevalidationTest.txt",
								response);
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}

			} else if (requestinfo.getManagementIp() != null && !requestinfo.getManagementIp().equals("")) {
				// TODO Auto-generated catch block

				System.out.print(e1.getMessage());

				if (e1.getMessage().contains("invalid server's version string")
						|| e1.getMessage().contains("Auth fail")) {
					jsonArray = new Gson().toJson(value);
					obj.put(new String("output"), jsonArray);
					requestDao.editRequestforReportWebserviceInfo(requestinfo.getAlphanumericReqId(),
							Double.toString(requestinfo.getRequestVersion()), "Application_test", "2",
							"Failure");
					requestInfoDao.addCertificationTestForRequest(requestinfo.getAlphanumericReqId(),
							Double.toString(requestinfo.getRequestVersion()), "2_Authentication");
					String response = "";
					String responseDownloadPath = "";

					try {
						response = invokeFtl.generateAuthenticationFailure(requestinfo);

						responseDownloadPath = VnfConfigService.TSA_PROPERTIES.getProperty("responseDownloadPath");
						TextReport.writeFile(responseDownloadPath, requestinfo.getAlphanumericReqId() + "V"
								+ Double.toString(requestinfo.getRequestVersion()) + "_prevalidationTest.txt",
								response);
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

				} else {
					jsonArray = new Gson().toJson(value);
					obj.put(new String("output"), jsonArray);
					requestDao.editRequestforReportWebserviceInfo(requestinfo.getAlphanumericReqId(),
							Double.toString(requestinfo.getRequestVersion()), "Application_test", "2",
							"Failure");
					requestInfoDao.addCertificationTestForRequest(requestinfo.getAlphanumericReqId(),
							Double.toString(requestinfo.getRequestVersion()), "2");
					String response = "";
					String responseDownloadPath = "";

					try {
						response = invokeFtl.generatePrevalidationResultFileFailure(requestinfo);

						responseDownloadPath = VnfConfigService.TSA_PROPERTIES.getProperty("responseDownloadPath");
						TextReport.writeFile(responseDownloadPath,requestinfo.getAlphanumericReqId() + "V"
								+ Double.toString(requestinfo.getRequestVersion()) + "_prevalidationTest.txt",
								response);
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}

			}
		}

		// value=true;
		// jsonArray = new Gson().toJson(value);
		// obj.put(new String("output"), jsonArray);
		return obj;
	}

	@SuppressWarnings({ "unchecked", "null" })
	@POST
	@RequestMapping(value = "/pushConfiguration", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
	@ResponseBody
	public Response pushConfiguration(@RequestBody String params) {

		JSONObject obj = new JSONObject();
		DcmConfigService dcmConfigService = new DcmConfigService();
		String requestIdForConfig = "";
		String res = "false";
		String data = "Failure";
		try {
			VnfConfigService.loadProperties();
			JSONParser parser = new JSONParser();
			JSONObject json = (JSONObject) parser.parse(params);
			CreateConfigRequestDCM configReqToSendToC3pCode = new CreateConfigRequestDCM();
			if (json.containsKey("requestType")) {
				configReqToSendToC3pCode.setRequestType(json.get("requestType").toString());
			} else {
				configReqToSendToC3pCode.setRequestType("NF");

			}
			configReqToSendToC3pCode.setCustomer(json.get("customer").toString());
			configReqToSendToC3pCode.setSiteid(json.get("siteid").toString().toUpperCase());
			configReqToSendToC3pCode.setDeviceType(json.get("deviceType").toString());
			configReqToSendToC3pCode.setModel(json.get("model").toString());
			configReqToSendToC3pCode.setOs(json.get("os").toString());
			configReqToSendToC3pCode.setManagementIp(json.get("managementIp").toString());
			configReqToSendToC3pCode.setVendor(json.get("vendor").toString());
			if (json.containsKey("osVersion")) {
				configReqToSendToC3pCode.setOsVersion(json.get("osVersion").toString());
			}
			configReqToSendToC3pCode.setRegion(json.get("region").toString().toUpperCase());
			configReqToSendToC3pCode.setService(json.get("service").toString().toUpperCase());
			configReqToSendToC3pCode.setHostname(json.get("hostname").toString().toUpperCase());
			configReqToSendToC3pCode.setRequest_version(1.0);

			Date date = new Date();
			SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

			String strDate = sdf.format(date);
			configReqToSendToC3pCode.setRequestCreatedOn(strDate);
			JSONObject certificationTestFlag = (JSONObject) json.get("certificationOptionListFlags");

			if (certificationTestFlag.containsKey("defaults")) {
				// flag test selection
				JSONObject defaultObj = (JSONObject) certificationTestFlag.get("defaults");
				if (defaultObj.get("Interfaces status").toString().equals("1")) {
					configReqToSendToC3pCode.setInterfaceStatus(defaultObj.get("Interfaces status").toString());
				}

				if (defaultObj.get("WAN Interface").toString().equals("1")) {
					configReqToSendToC3pCode.setWanInterface(defaultObj.get("WAN Interface").toString());
				}

				if (defaultObj.get("Platform & IOS").toString().equals("1")) {
					configReqToSendToC3pCode.setPlatformIOS(defaultObj.get("Platform & IOS").toString());
				}

				if (defaultObj.get("BGP neighbor").toString().equals("1")) {
					configReqToSendToC3pCode.setBGPNeighbor(defaultObj.get("BGP neighbor").toString());
				}
				if (defaultObj.get("Throughput").toString().equals("1")) {
					configReqToSendToC3pCode.setThroughputTest(defaultObj.get("Throughput").toString());
				}
				if (defaultObj.get("FrameLoss").toString().equals("1")) {
					configReqToSendToC3pCode.setFrameLossTest(defaultObj.get("FrameLoss").toString());
				}
				if (defaultObj.get("Latency").toString().equals("1")) {
					configReqToSendToC3pCode.setLatencyTest(defaultObj.get("Latency").toString());
				}

				String bit = defaultObj.get("Interfaces status").toString() + defaultObj.get("WAN Interface").toString()
						+ defaultObj.get("Platform & IOS").toString() + defaultObj.get("BGP neighbor").toString()
						+ defaultObj.get("Throughput").toString() + defaultObj.get("FrameLoss").toString()
						+ defaultObj.get("Latency").toString();
				System.out.println(bit);
				configReqToSendToC3pCode.setCertificationSelectionBit(bit);

			}
			if (certificationTestFlag.containsKey("dynamic")) {
				JSONArray dynamicArray = (JSONArray) certificationTestFlag.get("dynamic");
				JSONArray toSaveArray = new JSONArray();

				for (int i = 0; i < dynamicArray.size(); i++) {
					JSONObject arrayObj = (JSONObject) dynamicArray.get(i);
					long isSelected = (long) arrayObj.get("selected");
					if (isSelected == 1) {
						toSaveArray.add(arrayObj);
					}
				}

				String testsSelected = toSaveArray.toString();
				configReqToSendToC3pCode.setTestsSelected(testsSelected);

			}
			if (certificationTestFlag.containsKey("vnf")) {
				JSONObject vnfObj = (JSONObject) certificationTestFlag.get("vnf");

				// RAM-DISK-CPU Hardcoded for now

			}
			if (json.containsKey("scheduledTime")) {
				configReqToSendToC3pCode.setScheduledTime(json.get("scheduledTime").toString());
			}
			String dataXML = json.get("data").toString();
			/*
			 * Map<String, String> result = dcmConfigService.updateAlldetails(
			 * configReqToSendToC3pCode, null); for (Map.Entry<String, String> entry :
			 * result.entrySet()) { if (entry.getKey() == "requestID") { requestIdForConfig
			 * = entry.getValue();
			 * 
			 * } if (entry.getKey() == "result") { res = entry.getValue(); if
			 * (res.equalsIgnoreCase("true")) { data = "Submitted"; }
			 * 
			 * }
			 * 
			 * }
			 */

			// save xml data as new xml file
			VNFHelper helper = new VNFHelper();
			if (dataXML != null) {
				String filepath = helper.saveXML(dataXML, requestIdForConfig, configReqToSendToC3pCode);
				/*
				 * if (filepath != null) { // Push the configuration to server boolean
				 * resultPush = helper.pushOnVnfDevice(filepath);
				 * 
				 * if (resultPush) { obj.put(new String("output"), "Success"); obj.put(new
				 * String("requestId"), new String( requestIdForConfig)); obj.put(new
				 * String("version"), "1.0");
				 * 
				 * } else { obj.put(new String("output"),
				 * "Failure while pushing configuration on device"); obj.put(new
				 * String("requestId"), new String( requestIdForConfig)); obj.put(new
				 * String("version"), "1.0");
				 * 
				 * } } else { obj.put(new String("output"),
				 * "Failure while creating config file"); obj.put(new String("requestId"), new
				 * String( requestIdForConfig)); obj.put(new String("version"), "1.0");
				 * 
				 * }
				 */
			} else {
				obj.put(new String("output"), "Failure- No data received from UI");
				obj.put(new String("requestId"), new String(requestIdForConfig));
				obj.put(new String("version"), "1.0");

			}

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// Save data to tempvnfentity
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

	public static boolean loadProperties() throws IOException {
		InputStream tsaPropFile = Thread.currentThread().getContextClassLoader()
				.getResourceAsStream(TSA_PROPERTIES_FILE);

		try {
			TSA_PROPERTIES.load(tsaPropFile);
		} catch (IOException exc) {
			exc.printStackTrace();
			return false;
		}
		return false;
	}

	public String prettyPrintXml(String xmlStringToBeFormatted) {
		String formattedXmlString = null;
		try {
			DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
			documentBuilderFactory.setValidating(true);
			DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
			InputSource inputSource = new InputSource(new StringReader(xmlStringToBeFormatted));
			Document document = documentBuilder.parse(inputSource);

			Transformer transformer = TransformerFactory.newInstance().newTransformer();
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");

			StreamResult streamResult = new StreamResult(new StringWriter());
			DOMSource dOMSource = new DOMSource(document);
			transformer.transform(dOMSource, streamResult);
			formattedXmlString = streamResult.getWriter().toString().trim();
		} catch (Exception ex) {
			ex.printStackTrace(System.err);
		}
		return formattedXmlString;
	}

	public void printVersionversionInfo(InputStream input, Channel channel, String requestID, String version)
			throws Exception {
		BufferedWriter bw = null;
		FileWriter fw = null;
		int SIZE = 1024;
		byte[] tmp = new byte[SIZE];

		while (input.available() > 0) {
			int i = input.read(tmp, 0, SIZE);
			if (i < 0)
				break;
			/* System.out.print(new String(tmp, 0, i)); */
			String s = new String(tmp, 0, i);
			if (!(s.equals(""))) {
				// System.out.print(str);
				String filepath = VnfConfigService.TSA_PROPERTIES.getProperty("responseDownloadPath") + "//" + requestID
						+ "V" + version + "_VersionInfo.txt";
				File file = new File(filepath);

				// if file doesnt exists, then create it
				if (!file.exists()) {
					file.createNewFile();

					fw = new FileWriter(file, true);
					bw = new BufferedWriter(fw);
					bw.append(s);
					bw.close();
				} else {
					fw = new FileWriter(file.getAbsoluteFile(), true);
					bw = new BufferedWriter(fw);
					bw.append(s);
					bw.close();
				}
			}

		}
		if (channel.isClosed()) {
			System.out.println("exit-status: " + channel.getExitStatus());

		}
		try {
			Thread.sleep(1000);
		} catch (Exception ee) {
		}

	}

}