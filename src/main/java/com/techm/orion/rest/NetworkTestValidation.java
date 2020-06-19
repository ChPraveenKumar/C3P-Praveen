package com.techm.orion.rest;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Scanner;

import javax.ws.rs.POST;

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
import com.jcraft.jsch.Channel;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;
import com.techm.orion.dao.RequestInfoDao;
import com.techm.orion.dao.RequestInfoDetailsDao;
import com.techm.orion.entitybeans.TestDetail;
import com.techm.orion.pojo.CreateConfigRequest;
import com.techm.orion.pojo.RequestInfoPojo;
import com.techm.orion.pojo.UserPojo;
import com.techm.orion.utility.InvokeFtl;
import com.techm.orion.utility.TestStrategeyAnalyser;
import com.techm.orion.utility.TextReport;

@Controller
@RequestMapping("/NetworkTestValidation")
public class NetworkTestValidation extends Thread {

	public static String TSA_PROPERTIES_FILE = "TSA.properties";
	public static final Properties TSA_PROPERTIES = new Properties();

	@Autowired
	RequestInfoDao requestInfoDao;

	@Autowired
	RequestInfoDetailsDao requestDao;
	
	@Autowired 
	TestStrategeyAnalyser analyser;
	
	@POST
	@RequestMapping(value = "/networkCommandTest", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
	@ResponseBody
	public JSONObject networkCommandTest(@RequestBody String request) throws ParseException {

		JSONObject obj = new JSONObject();
		String jsonArray = "";
		InvokeFtl invokeFtl = new InvokeFtl();
		CreateConfigRequest configRequest = new CreateConfigRequest();
		RequestInfoPojo requestinfo = new RequestInfoPojo();
		Boolean value = false;
		
		JSONParser parser = new JSONParser();
		JSONObject json = (JSONObject) parser.parse(request);
		
		String RequestId = json.get("requestId").toString();
		String version = json.get("version").toString();		
		
		String type = RequestId.substring(0,
				Math.min(RequestId.length(), 4));
		
		if (!((type.equals("SLGB") || (type.equals("SLGM")))))
		{

		try {

			System.out.println("Request ID in network test validation"
					+ RequestId);

			configRequest = requestInfoDao.getRequestDetailFromDBForVersion(
					RequestId, version);
			
		 requestinfo = requestDao.getRequestDetailTRequestInfoDBForVersion(RequestId, version);
		if (configRequest.getManagementIp() != null && !configRequest.getManagementIp().equals("")) {
			configRequest.setRequestId(RequestId);
			configRequest.setRequest_version(Double.parseDouble(json.get(
					"version").toString()));

			
			if (type.equalsIgnoreCase("SLGC") || type.equalsIgnoreCase("SLGT")||type.equalsIgnoreCase("SNRC") || type.equalsIgnoreCase("SNNC")|| type.equalsIgnoreCase("SLGA")||type.equalsIgnoreCase("SLGM")||type.equalsIgnoreCase("SNRM")||type.equalsIgnoreCase("SNNM")) {
				NetworkTestValidation.loadProperties();
				String host = configRequest.getManagementIp();
				UserPojo userPojo = new UserPojo();
				userPojo = requestInfoDao.getRouterCredentials();
				String user=null,password=null;
				
				 user = userPojo.getUsername();
				 password = userPojo.getPassword();
				if(type.equalsIgnoreCase("SNRC") || type.equalsIgnoreCase("SNNC"))
				{
					 user = "c3pteam";
					 password = "csr1000v";
				}
				else
				{
					 user = userPojo.getUsername();
					 password = userPojo.getPassword();
				}
				String port = NetworkTestValidation.TSA_PROPERTIES
						.getProperty("portSSH");
				ArrayList<String> commandToPush = new ArrayList<String>();

				JSch jsch = new JSch();
				Channel channel = null;
				Session session = jsch.getSession(user, host,
						Integer.parseInt(port));
				Properties config = new Properties();
				config.put("StrictHostKeyChecking", "no");
				session.setConfig(config);
				session.setPassword(password);
				System.out
						.println("Before session.connet in network test validation Username"
								+ user
								+ " Password "
								+ password
								+ " host"
								+ host);
				session.connect();
				try {
					Thread.sleep(10000);
				} catch (Exception ee) {
				}
				try {
					channel = session.openChannel("shell");
					OutputStream ops = channel.getOutputStream();

					PrintStream ps = new PrintStream(ops, true);
					System.out.println("Channel Connected to machine " + host
							+ " server");
					channel.connect();
					InputStream input = channel.getInputStream();

					session = jsch.getSession(user, host,
							Integer.parseInt(port));
					config = new Properties();

					config.put("StrictHostKeyChecking", "no");
					session.setConfig(config);
					session.setPassword(password);
					session.connect();

					channel = session.openChannel("shell");
					ops = channel.getOutputStream();

					ps = new PrintStream(ops, true);

					channel.connect();

					input = channel.getInputStream();
					ps.println("terminal length 0");
					if (configRequest.getCertificationBit().substring(0, 1)
							.equalsIgnoreCase("1")) {
						ps.println("show ip interface brief");
						printResult(input, channel,
								configRequest.getRequestId(),
								Double.toString(configRequest
										.getRequest_version()));
						configRequest.setNetwork_test_interfaceStatus("Passed");
					}
					if (configRequest.getCertificationBit().substring(1, 2)
							.equalsIgnoreCase("1")) {
						ps.println("show interface "
								+ configRequest.getC3p_interface().getName());
						printResult(input, channel,
								configRequest.getRequestId(),
								Double.toString(configRequest
										.getRequest_version()));
						configRequest.setNetwork_test_wanInterface("Passed");
					}
					if (configRequest.getCertificationBit().substring(2, 3)
							.equalsIgnoreCase("1")) {
						ps.println("show version");
						printResult(input, channel,
								configRequest.getRequestId(),
								Double.toString(configRequest
										.getRequest_version()));
						configRequest.setNetwork_test_platformIOS("Passed");
					}
					if (configRequest.getCertificationBit().substring(3, 4)
							.equalsIgnoreCase("1")) {
						ps.println("sh ip bgp summary");
						printResult(input, channel,
								configRequest.getRequestId(),
								Double.toString(configRequest
										.getRequest_version()));
						configRequest.setNetwork_test_BGPNeighbor("Passed");
					}

					try {
						Thread.sleep(6000);
					} catch (Exception ee) {
					}

					printResult(input, channel, configRequest.getRequestId(),
							Double.toString(configRequest.getRequest_version()));

					if (channel.isClosed()) {
						channel.connect();

					}

					/*
					 * Owner: Ruchita Salvi Module: Test Strategey Logic: To
					 * find and run and analyse custom tests
					 */

					// input = channel.getInputStream();
					// fetch extra network test added
					List<Boolean> results = null;
					RequestInfoDao dao = new RequestInfoDao();
					List<TestDetail> listOfTests = new ArrayList<TestDetail>();
					List<TestDetail> finallistOfTests = new ArrayList<TestDetail>();
					TestDetail test = new TestDetail();
					listOfTests = dao.findTestFromTestStrategyDB(
							configRequest.getModel(),
							configRequest.getDeviceType(),
							configRequest.getOs(),
							configRequest.getOsVersion(),
							configRequest.getVendor(),
							configRequest.getRegion(), "Network Test");
					List<TestDetail> selectedTests = dao.findSelectedTests(
							configRequest.getRequestId(), "Network Test");
					if (selectedTests.size() > 0) {
						for (int i = 0; i < listOfTests.size(); i++) {
							for (int j = 0; j < selectedTests.size(); j++) {
								if (selectedTests
										.get(j)
										.getTestName()
										.equalsIgnoreCase(
												listOfTests.get(i)
														.getTestName())) {
									finallistOfTests.add(listOfTests.get(i));
									
								}
								
								}
						}

						if (finallistOfTests.size() > 0) {
							results = new ArrayList<Boolean>();
							for (int i = 0; i < finallistOfTests.size(); i++) {

								// conduct and analyse the tests
								ps.println(finallistOfTests.get(i)
										.getTestCommand());
								try {
									Thread.sleep(6000);
								} catch (Exception ee) {
								}

								// printResult(input,
								// channel,configRequest.getRequestId(),Double.toString(configRequest.getRequest_version()));
								Boolean res = analyser.printAndAnalyse(input, channel,
										configRequest.getRequestId(),
										Double.toString(configRequest
												.getRequest_version()),
										finallistOfTests.get(i),"Network Test");
								results.add(res);
							}
						}
						

					} else {
						// No new network test added
					}

					/*
					 * END
					 */

					System.out.println("Certification bits in network test "
							+ configRequest.getCertificationBit()
									.substring(0, 1).equalsIgnoreCase("1")
							+ configRequest.getCertificationBit().substring(1,
									2)
							+ configRequest.getCertificationBit().substring(2,
									3)
							+ configRequest.getCertificationBit().substring(3,
									4));
					// working on simulator so condition has been set to true
					if (configRequest.getCertificationBit().substring(0, 1)
							.equalsIgnoreCase("1")
							|| configRequest.getCertificationBit()
									.substring(1, 2).equalsIgnoreCase("1")
							|| configRequest.getCertificationBit()
									.substring(2, 3).equalsIgnoreCase("1")
							|| configRequest.getCertificationBit()
									.substring(3, 4).equalsIgnoreCase("1")) {
						boolean result = true;
						if (listOfTests.size() > 0) {
							if (results != null && !results.isEmpty()) {
								for (int i = 0; i < results.size(); i++) {
									if (results.get(i) == false) {
										result = false;
									}
								}
							}
							/*
							 * if(result==false) {
							 * 
							 * value=false;
							 * requestInfoDao.updateNetworkTestStatus
							 * (configRequest
							 * .getRequestId(),Double.toString(configRequest
							 * .getRequest_version
							 * ()),Integer.parseInt(configRequest
							 * .getCertificationBit
							 * ().substring(0,1)),Integer.parseInt
							 * (configRequest.
							 * getCertificationBit().substring(1,2
							 * )),Integer.parseInt
							 * (configRequest.getCertificationBit
							 * ().substring(2,3
							 * )),Integer.parseInt(configRequest.
							 * getCertificationBit().substring(3,4)));
							 * requestInfoDao
							 * .editRequestforReportWebserviceInfo(
							 * configRequest.
							 * getRequestId(),Double.toString(configRequest
							 * .getRequest_version
							 * ()),"network_test","2","Failure"); } else {
							 * value=true;
							 * 
							 * requestInfoDao.updateNetworkTestStatus(configRequest
							 * .getRequestId(),Double.toString(configRequest.
							 * getRequest_version
							 * ()),Integer.parseInt(configRequest
							 * .getCertificationBit
							 * ().substring(0,1)),Integer.parseInt
							 * (configRequest.
							 * getCertificationBit().substring(1,2
							 * )),Integer.parseInt
							 * (configRequest.getCertificationBit
							 * ().substring(2,3
							 * )),Integer.parseInt(configRequest.
							 * getCertificationBit().substring(3,4)));
							 * requestInfoDao
							 * .editRequestforReportWebserviceInfo(
							 * configRequest.
							 * getRequestId(),Double.toString(configRequest
							 * .getRequest_version
							 * ()),"network_test","1","In Progress"); }
							 */

						}
						requestInfoDao.updateNetworkTestStatus(configRequest
								.getRequestId(), Double.toString(configRequest
								.getRequest_version()), Integer
								.parseInt(configRequest.getCertificationBit()
										.substring(0, 1)), Integer
								.parseInt(configRequest.getCertificationBit()
										.substring(1, 2)), Integer
								.parseInt(configRequest.getCertificationBit()
										.substring(2, 3)), Integer
								.parseInt(configRequest.getCertificationBit()
										.substring(3, 4)));
						
						String status=requestInfoDao.getPreviousMileStoneStatus(configRequest.getRequestId(), Double
								.toString(configRequest
										.getRequest_version()));
			
						
						requestInfoDao.editRequestforReportWebserviceInfo(
								configRequest.getRequestId(), Double
										.toString(configRequest
												.getRequest_version()),
								"network_test", "1", status);
					}
					/*
					 * else if(configRequest.getCertificationBit().substring(0).
					 * equalsIgnoreCase("0")&&
					 * configRequest.getCertificationBit(
					 * ).substring(0).equalsIgnoreCase
					 * ("0")&&configRequest.getCertificationBit
					 * ().substring(2).equalsIgnoreCase("0")&&
					 * configRequest.getCertificationBit
					 * ().substring(3).equalsIgnoreCase("0")) {
					 * HealthCheckTestSSH healthCheckTestSSH=new
					 * HealthCheckTestSSH();
					 * healthCheckTestSSH.HealthCheckTest(configRequest);
					 * 
					 * }
					 */
					else {
						// db call to set flag false
						requestInfoDao.updateNetworkTestStatus(configRequest
								.getRequestId(), Double.toString(configRequest
								.getRequest_version()), Integer
								.parseInt(configRequest.getCertificationBit()
										.substring(0, 1)), Integer
								.parseInt(configRequest.getCertificationBit()
										.substring(1, 2)), Integer
								.parseInt(configRequest.getCertificationBit()
										.substring(2, 3)), Integer
								.parseInt(configRequest.getCertificationBit()
										.substring(3, 4)));
						requestInfoDao.editRequestforReportWebserviceInfo(
								configRequest.getRequestId(), Double
										.toString(configRequest
												.getRequest_version()),
								"network_test", "2", "Failure");
					}
					value=true;//hardcoded for default tests
					
					//this is to evaluate according to newly added tests else it is true by default.
					if(results!=null)
					{
					for(int i=0;i<results.size();i++)
					{
						if(!results.get(i))
						{
							value=false;
							break;
						}
						
					}
					}
					channel.disconnect();
					session.disconnect();
					System.out.println("DONE");
					jsonArray = new Gson().toJson(value);
					try {
						Thread.sleep(15000);
					} catch (Exception ee) {
					}
					obj.put(new String("output"), jsonArray);
				} catch (IOException ex) {
					jsonArray = new Gson().toJson(value);
					obj.put(new String("output"), jsonArray);
					requestInfoDao
							.editRequestforReportWebserviceInfo(configRequest
									.getRequestId(), Double
									.toString(configRequest
											.getRequest_version()),
									"network_test", "2", "Failure");

					String response = "";
					String responseDownloadPath = "";
					try {
						response = invokeFtl
								.generateNetworkTestResultFileFailure(configRequest);
						responseDownloadPath = NetworkTestValidation.TSA_PROPERTIES
								.getProperty("responseDownloadPath");
						TextReport.writeFile(
								responseDownloadPath,
								configRequest.getRequestId()
										+ "V"
										+ Double.toString(configRequest
												.getRequest_version())
										+ "_networkTest.txt", response);
					} catch (Exception e) {
						// TODO Auto-generated catch block

					}

				}
				channel.disconnect();
				session.disconnect();
			}
			
			else {
				value = true;
				System.out.println("DONE Network Test");
				jsonArray = new Gson().toJson(value);
				obj.put(new String("output"), jsonArray);
			}
		
		} else if (requestinfo.getManagementIp() != null && !requestinfo.getManagementIp().equals("")) {
			String statusVAlue = requestDao.getPreviousMileStoneStatus(
					requestinfo.getAlphanumericReqId(),
					requestinfo.getRequestVersion());
			requestInfoDao.editRequestforReportWebserviceInfo(requestinfo.getAlphanumericReqId(),
					Double.toString(requestinfo.getRequestVersion()), "network_test", "4",statusVAlue);
			
			requestinfo.setAlphanumericReqId(RequestId);
			requestinfo.setRequestVersion(Double.parseDouble(json.get("version").toString()));
					
			if (type.equalsIgnoreCase("SLGC") || type.equalsIgnoreCase("SLGT")||type.equalsIgnoreCase("SNRC") || type.equalsIgnoreCase("SNNC") || type.equalsIgnoreCase("SLGA")||type.equalsIgnoreCase("SLGM")||type.equalsIgnoreCase("SNRM")||type.equalsIgnoreCase("SNNM")) {
				NetworkTestValidation.loadProperties();
				String host = requestinfo.getManagementIp();
				UserPojo userPojo = new UserPojo();
				userPojo = requestInfoDao.getRouterCredentials();
				String user=null,password=null;				
				 user = userPojo.getUsername();
				 password = userPojo.getPassword();
				if(type.equalsIgnoreCase("SNRC") || type.equalsIgnoreCase("SNNC"))
				{
					 user = "c3pteam";
					 password = "csr1000v";
				}
				else
				{
					 user = userPojo.getUsername();
					 password = userPojo.getPassword();
				}
				String port = NetworkTestValidation.TSA_PROPERTIES
						.getProperty("portSSH");

				JSch jsch = new JSch();
				Channel channel = null;
				Session session = jsch.getSession(user, host,
						Integer.parseInt(port));
				Properties config = new Properties();
				config.put("StrictHostKeyChecking", "no");
				session.setConfig(config);
				session.setPassword(password);
				System.out
						.println("Before session.connet in network test validation Username"
								+ user
								+ " Password "
								+ password
								+ " host"
								+ host);
				session.connect();
				try {
					Thread.sleep(10000);
				} catch (Exception ee) {
				}
				try {
					channel = session.openChannel("shell");
					OutputStream ops = channel.getOutputStream();

					PrintStream ps = new PrintStream(ops, true);
					System.out.println("Channel Connected to machine " + host
							+ " server");
					channel.connect();
					InputStream input = channel.getInputStream();

					session = jsch.getSession(user, host,
							Integer.parseInt(port));
					config = new Properties();

					config.put("StrictHostKeyChecking", "no");
					session.setConfig(config);
					session.setPassword(password);
					session.connect();

					channel = session.openChannel("shell");
					ops = channel.getOutputStream();

					ps = new PrintStream(ops, true);

					channel.connect();

					input = channel.getInputStream();
					ps.println("terminal length 0");
					if (requestinfo.getCertificationSelectionBit().substring(0, 1)
							.equalsIgnoreCase("1")) {
						ps.println("show ip interface brief");
						printResult(input, channel,
								requestinfo.getAlphanumericReqId(),
								Double.toString(requestinfo.getRequestVersion()));
						requestinfo.setNetwork_test_interfaceStatus("Passed");
					}
					if (requestinfo.getCertificationSelectionBit().substring(1, 2)
							.equalsIgnoreCase("1")) {
//						ps.println("show interface "
//								+ configRequest.getC3p_interface().getName());
						printResult(input, channel,
								requestinfo.getAlphanumericReqId(),
								Double.toString(requestinfo.getRequestVersion()));
						requestinfo.setNetwork_test_wanInterface("Passed");
					}
					if (requestinfo.getCertificationSelectionBit().substring(2, 3)
							.equalsIgnoreCase("1")) {
						ps.println("show version");
						printResult(input, channel,
								requestinfo.getAlphanumericReqId(),
								Double.toString(requestinfo.getRequestVersion()));
						requestinfo.setNetwork_test_platformIOS("Passed");
					}
					if (requestinfo.getCertificationSelectionBit().substring(3, 4)
							.equalsIgnoreCase("1")) {
						ps.println("sh ip bgp summary");
						printResult(input, channel,
								requestinfo.getAlphanumericReqId(),
								Double.toString(requestinfo.getRequestVersion()));
						requestinfo.setNetwork_test_BGPNeighbor("Passed");
					}

					try {
						Thread.sleep(6000);
					} catch (Exception ee) {
					}

					printResult(input, channel,requestinfo.getAlphanumericReqId(),
							Double.toString(requestinfo.getRequestVersion()));

					if (channel.isClosed()) {
						channel.connect();

					}

					/*
					 * Owner: Ruchita Salvi Module: Test Strategey Logic: To
					 * find and run and analyse custom tests
					 */

					// input = channel.getInputStream();
					// fetch extra network test added
					List<Boolean> results = null;
					RequestInfoDao dao = new RequestInfoDao();
					List<TestDetail> listOfTests = new ArrayList<TestDetail>();
					List<TestDetail> finallistOfTests = new ArrayList<TestDetail>();
					TestDetail test = new TestDetail();
					listOfTests = dao.findTestFromTestStrategyDB(
							requestinfo.getModel(),
							requestinfo.getDeviceType(),
							requestinfo.getOs(),
							requestinfo.getOsVersion(),
							requestinfo.getVendor(),
							requestinfo.getRegion(), "Network Test");
					List<TestDetail> selectedTests = dao.findSelectedTests(
							requestinfo.getAlphanumericReqId(), "Network Test");
					if (selectedTests.size() > 0) {
						for (int i = 0; i < listOfTests.size(); i++) {
							for (int j = 0; j < selectedTests.size(); j++) {
								if (selectedTests
										.get(j)
										.getTestName()
										.equalsIgnoreCase(
												listOfTests.get(i)
														.getTestName())) {
									finallistOfTests.add(listOfTests.get(i));
									
								}
								
								}
						}

						if (finallistOfTests.size() > 0) {
							results = new ArrayList<Boolean>();
							for (int i = 0; i < finallistOfTests.size(); i++) {

								// conduct and analyse the tests
								ps.println(finallistOfTests.get(i)
										.getTestCommand());
								try {
									Thread.sleep(6000);
								} catch (Exception ee) {
								}

								// printResult(input,
								// channel,configRequest.getRequestId(),Double.toString(configRequest.getRequest_version()));
								Boolean res = analyser.printAndAnalyse(input, channel,
										requestinfo.getAlphanumericReqId(),
										Double.toString(requestinfo.getRequestVersion()),
										finallistOfTests.get(i),"Network Test");
								results.add(res);
							}
						}
						

					} else {
						// No new network test added
					}

					/*
					 * END
					 */

					System.out.println("Certification bits in network test "
							+ requestinfo.getCertificationSelectionBit()
									.substring(0, 1).equalsIgnoreCase("1")
									+ requestinfo.getCertificationSelectionBit().substring(1,
									2)
									+ requestinfo.getCertificationSelectionBit().substring(2,
									3)
									+ requestinfo.getCertificationSelectionBit().substring(3,
									4));
					// working on simulator so condition has been set to true
					if (requestinfo.getCertificationSelectionBit().substring(0, 1)
							.equalsIgnoreCase("1")
							|| requestinfo.getCertificationSelectionBit()
									.substring(1, 2).equalsIgnoreCase("1")
							|| requestinfo.getCertificationSelectionBit()
									.substring(2, 3).equalsIgnoreCase("1")
							|| requestinfo.getCertificationSelectionBit()
									.substring(3, 4).equalsIgnoreCase("1")) {
						boolean result = true;
						if (listOfTests.size() > 0) {
							if (results != null && !results.isEmpty()) {
								for (int i = 0; i < results.size(); i++) {
									if (results.get(i) == false) {
										result = false;
									}
								}
							}
							/*
							 * if(result==false) {
							 * 
							 * value=false;
							 * requestInfoDao.updateNetworkTestStatus
							 * (configRequest
							 * .getRequestId(),Double.toString(configRequest
							 * .getRequest_version
							 * ()),Integer.parseInt(configRequest
							 * .getCertificationBit
							 * ().substring(0,1)),Integer.parseInt
							 * (configRequest.
							 * getCertificationBit().substring(1,2
							 * )),Integer.parseInt
							 * (configRequest.getCertificationBit
							 * ().substring(2,3
							 * )),Integer.parseInt(configRequest.
							 * getCertificationBit().substring(3,4)));
							 * requestInfoDao
							 * .editRequestforReportWebserviceInfo(
							 * configRequest.
							 * getRequestId(),Double.toString(configRequest
							 * .getRequest_version
							 * ()),"network_test","2","Failure"); } else {
							 * value=true;
							 * 
							 * requestInfoDao.updateNetworkTestStatus(configRequest
							 * .getRequestId(),Double.toString(configRequest.
							 * getRequest_version
							 * ()),Integer.parseInt(configRequest
							 * .getCertificationBit
							 * ().substring(0,1)),Integer.parseInt
							 * (configRequest.
							 * getCertificationBit().substring(1,2
							 * )),Integer.parseInt
							 * (configRequest.getCertificationBit
							 * ().substring(2,3
							 * )),Integer.parseInt(configRequest.
							 * getCertificationBit().substring(3,4)));
							 * requestInfoDao
							 * .editRequestforReportWebserviceInfo(
							 * configRequest.
							 * getRequestId(),Double.toString(configRequest
							 * .getRequest_version
							 * ()),"network_test","1","In Progress"); }
							 */

						}
						requestInfoDao.updateNetworkTestStatus(configRequest
								.getRequestId(), Double.toString(configRequest
								.getRequest_version()), Integer
								.parseInt(requestinfo.getCertificationSelectionBit()
										.substring(0, 1)), Integer
								.parseInt(requestinfo.getCertificationSelectionBit()
										.substring(1, 2)), Integer
								.parseInt(requestinfo.getCertificationSelectionBit()
										.substring(2, 3)), Integer
								.parseInt(requestinfo.getCertificationSelectionBit()
										.substring(3, 4)));
						
						String status=requestDao.getPreviousMileStoneStatus(requestinfo.getAlphanumericReqId(),requestinfo.getRequestVersion());
						int statusData=requestDao.getStatusForMilestone(requestinfo.getAlphanumericReqId(),
								Double.toString(requestinfo.getRequestVersion()), "network_test");
						if(statusData!=3) {
							requestInfoDao.editRequestforReportWebserviceInfo(
									requestinfo.getAlphanumericReqId(),
									Double.toString(requestinfo.getRequestVersion()), "network_test", "1",
									status);
						}

					}
					/*
					 * else if(configRequest.getCertificationBit().substring(0).
					 * equalsIgnoreCase("0")&&
					 * configRequest.getCertificationBit(
					 * ).substring(0).equalsIgnoreCase
					 * ("0")&&configRequest.getCertificationBit
					 * ().substring(2).equalsIgnoreCase("0")&&
					 * configRequest.getCertificationBit
					 * ().substring(3).equalsIgnoreCase("0")) {
					 * HealthCheckTestSSH healthCheckTestSSH=new
					 * HealthCheckTestSSH();
					 * healthCheckTestSSH.HealthCheckTest(configRequest);
					 * 
					 * }
					 */
					else {
						// db call to set flag false
						requestInfoDao.updateNetworkTestStatus(requestinfo.getAlphanumericReqId(), Double.toString(requestinfo.getRequestVersion()
								), Integer
								.parseInt(requestinfo.getCertificationSelectionBit()
										.substring(0, 1)), Integer
								.parseInt(requestinfo.getCertificationSelectionBit()
										.substring(1, 2)), Integer
								.parseInt(requestinfo.getCertificationSelectionBit()
										.substring(2, 3)), Integer
								.parseInt(requestinfo.getCertificationSelectionBit()
										.substring(3, 4)));
						requestDao.editRequestforReportWebserviceInfo(requestinfo.getAlphanumericReqId(), Double.toString(requestinfo.getRequestVersion()),
								"network_test", "2", "Failure");
					}
					value=true;//hardcoded for default tests
					
					//this is to evaluate according to newly added tests else it is true by default.
					if(results!=null)
					{
					for(int i=0;i<results.size();i++)
					{
						if(!results.get(i))
						{
							value=false;
							break;
						}
						
					}
					}
					channel.disconnect();
					session.disconnect();
					System.out.println("DONE");
					jsonArray = new Gson().toJson(value);
					try {
						Thread.sleep(15000);
					} catch (Exception ee) {
					}
					obj.put(new String("output"), jsonArray);
				} catch (IOException ex) {
					jsonArray = new Gson().toJson(value);
					obj.put(new String("output"), jsonArray);
					requestDao
							.editRequestforReportWebserviceInfo(requestinfo.getAlphanumericReqId(), Double.toString(requestinfo.getRequestVersion()),
									"network_test", "2", "Failure");

					String response = "";
					String responseDownloadPath = "";
					try {
						response = invokeFtl
								.generateNetworkTestResultFileFailure(requestinfo);
						responseDownloadPath = NetworkTestValidation.TSA_PROPERTIES
								.getProperty("responseDownloadPath");
						TextReport.writeFile(
								responseDownloadPath,
								requestinfo.getAlphanumericReqId()
										+ "V"
										+ Double.toString(requestinfo.getRequestVersion())
										+ "_networkTest.txt", response);
					} catch (Exception e) {
						// TODO Auto-generated catch block

					}

				}
				channel.disconnect();
				session.disconnect();
			}
			
			else {
				value = true;
				System.out.println("DONE Network Test");
				jsonArray = new Gson().toJson(value);
				obj.put(new String("output"), jsonArray);
			}			
		}}
		// when reachability fails
		catch (Exception ex) {
			if (configRequest.getManagementIp() != null && !configRequest.getManagementIp().equals("")) {

				System.out.println("Exception in network tst" + ex.getMessage());
				jsonArray = new Gson().toJson(value);
				obj.put(new String("output"), jsonArray);
				requestInfoDao.editRequestforReportWebserviceInfo(
						configRequest.getRequestId(),
						Double.toString(configRequest.getRequest_version()),
						"network_test", "2", "Failure");
				String response = "";
				String responseDownloadPath = "";
				try {
					response = invokeFtl
							.generateNetworkTestResultFileFailure(configRequest);
					responseDownloadPath = NetworkTestValidation.TSA_PROPERTIES
							.getProperty("responseDownloadPath");
					TextReport.writeFile(
							responseDownloadPath,
							configRequest.getRequestId()
									+ "V"
									+ Double.toString(configRequest
											.getRequest_version())
									+ "_networkTest.txt", response);
				} catch (Exception e) {
					// TODO Auto-generated catch block

				}

			} else if (requestinfo.getManagementIp() != null && !requestinfo.getManagementIp().equals("")) {

				System.out.println("Exception in network tst" + ex.getMessage());
				jsonArray = new Gson().toJson(value);
				obj.put(new String("output"), jsonArray);
				requestDao.editRequestforReportWebserviceInfo(
						requestinfo.getAlphanumericReqId(),
						Double.toString(requestinfo.getRequestVersion()),
						"network_test", "2", "Failure");
				String response = "";
				String responseDownloadPath = "";
				try {
					response = invokeFtl
							.generateNetworkTestResultFileFailure(requestinfo);
					responseDownloadPath = NetworkTestValidation.TSA_PROPERTIES
							.getProperty("responseDownloadPath");
					TextReport.writeFile(
							responseDownloadPath,
							requestinfo.getAlphanumericReqId()
									+ "V"
									+ Double.toString(requestinfo.getRequestVersion())
									+ "_networkTest.txt", response);
				} catch (Exception e) {
					// TODO Auto-generated catch block

				}
				
			}
		}
		}
		else
		{
			value=true;
			
			jsonArray = new Gson().toJson(value);
			obj.put(new String("output"), jsonArray);
			
			
		}

		/*
		 * return Response .status(200) .header("Access-Control-Allow-Origin",
		 * "*") .header("Access-Control-Allow-Headers",
		 * "origin, content-type, accept, authorization")
		 * .header("Access-Control-Allow-Credentials", "true")
		 * .header("Access-Control-Allow-Methods",
		 * "GET, POST, PUT, DELETE, OPTIONS, HEAD")
		 * .header("Access-Control-Max-Age", "1209600").entity(obj) .build();
		 */
		return obj;

	}

	public static boolean loadProperties() throws IOException {
		InputStream tsaPropFile = Thread.currentThread()
				.getContextClassLoader()
				.getResourceAsStream(TSA_PROPERTIES_FILE);

		try {
			TSA_PROPERTIES.load(tsaPropFile);
		} catch (IOException exc) {
			exc.printStackTrace();
			return false;
		}
		return false;
	}


	private static void printResult(InputStream input, Channel channel,
			String requestID, String version) throws Exception {
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
				System.out.print(s);

				String filepath = NetworkTestValidation.TSA_PROPERTIES
						.getProperty("responseDownloadPath")
						+ "//"
						+ requestID
						+ "V" + version + "_networkTest.txt";
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

	@SuppressWarnings("resource")
	public String validateNetworkTest(CreateConfigRequest configRequest)
			throws Exception {
		NetworkTestValidation.loadProperties();
		System.out.println("In side validate network test line no 356");
		String content = "";
		String path = NetworkTestValidation.TSA_PROPERTIES
				.getProperty("responseDownloadPath")
				+ "//"
				+ configRequest.getRequestId()
				+ "V"
				+ configRequest.getRequest_version() + "_networkTest.txt";

		File file = new File(path);
		Scanner in = null;
		try {
			if (file.exists()) {
				in = new Scanner(file);
				while (in.hasNext()) {
					String line = in.nextLine();

					String interfacename = configRequest.getC3p_interface()
							.getName();
					if (interfacename == null) {
						configRequest.getC3p_interface().setName("");

					}
					if (line.contains(configRequest.getC3p_interface()
							.getName())) {
						System.out.println(line);
						content = line;
						break;
					}

				}
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return content;
	}

	private static String readFile(String path) throws IOException {
		System.out.println("path" + path);

		BufferedReader br = new BufferedReader(new FileReader(path));
		try {
			StringBuilder sb = new StringBuilder();
			String line = br.readLine();

			while (line != null) {
				sb.append(line);
				sb.append("\n");
				line = br.readLine();
			}
			return sb.toString();
		} finally {
			br.close();
		}
	}
/*method overloading for UIRevamp*/
	public String validateNetworkTest(RequestInfoPojo requestinfo) throws IOException {

		NetworkTestValidation.loadProperties();
		System.out.println("In side validate network test line no 356");
		String content = "";
		String path = NetworkTestValidation.TSA_PROPERTIES
				.getProperty("responseDownloadPath")
				+ "//"
				+ requestinfo.getAlphanumericReqId()
				+ "V"
				+ requestinfo.getRequestVersion() + "_networkTest.txt";

		File file = new File(path);
		Scanner in = null;
		try {
			if (file.exists()) {
				in = new Scanner(file);
				while (in.hasNext()) {
					String line = in.nextLine();

//					String interfacename = requestinfo.getC3p_interface()
//							.getName();
//					if (interfacename == null) {
//						requestinfo.getC3p_interface().setName("");
//
//					}
//					if (line.contains(requestinfo.getC3p_interface()
//							.getName())) {
//						System.out.println(line);
//						content = line;
//						break;
//					}

				}
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return content;
	}
}