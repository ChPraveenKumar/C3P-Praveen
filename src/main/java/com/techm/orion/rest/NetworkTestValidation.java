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

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
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
import com.techm.orion.entitybeans.CredentialManagementEntity;
import com.techm.orion.entitybeans.DeviceDiscoveryEntity;
import com.techm.orion.entitybeans.TestDetail;
import com.techm.orion.pojo.CreateConfigRequest;
import com.techm.orion.pojo.RequestInfoPojo;
import com.techm.orion.pojo.UserPojo;
import com.techm.orion.repositories.DeviceDiscoveryRepository;
import com.techm.orion.service.DcmConfigService;
import com.techm.orion.utility.InvokeFtl;
import com.techm.orion.utility.ODLClient;
import com.techm.orion.utility.TestStrategeyAnalyser;
import com.techm.orion.utility.TextReport;
import com.techm.orion.utility.VNFHelper;

@Controller
@RequestMapping("/NetworkTestValidation")
public class NetworkTestValidation extends Thread {
	private static final Logger logger = LogManager.getLogger(NetworkTestValidation.class);
	public static String TSA_PROPERTIES_FILE = "TSA.properties";
	public static final Properties TSA_PROPERTIES = new Properties();

	@Autowired
	private RequestInfoDao requestInfoDao;

	@Autowired
	private RequestInfoDetailsDao requestInfoDetailsDao;

	@Autowired
	private TestStrategeyAnalyser testStrategeyAnalyser;

	@Autowired
	private DeviceDiscoveryRepository deviceDiscoveryRepository;
	
	@Autowired
	private DcmConfigService dcmConfigService;
	
	/**
	 *This Api is marked as ***************c3p-ui Api Impacted****************
	 **/
	@POST
	@RequestMapping(value = "/networkCommandTest", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
	@ResponseBody
	public JSONObject networkCommandTest(@RequestBody String request) throws ParseException {

		JSONObject obj = new JSONObject();
		String jsonArray = "";
		InvokeFtl invokeFtl = new InvokeFtl();
		RequestInfoPojo requestinfo = new RequestInfoPojo();
		Boolean value = false;

		JSONParser parser = new JSONParser();
		JSONObject json = (JSONObject) parser.parse(request);

		String RequestId = json.get("requestId").toString();
		String version = json.get("version").toString();

		String type = RequestId.substring(0, Math.min(RequestId.length(), 4));
		JSch jsch = new JSch();
		Channel channel = null;
		Session session = null;
	    if (!((type.equals("SLGB") || (type.equals("SNAI") )))){

			try {				
				logger.info("Request ID in network test validation" + RequestId);
				requestinfo = requestInfoDetailsDao.getRequestDetailTRequestInfoDBForVersion(RequestId, version);
				if (requestinfo.getManagementIp() != null && !requestinfo.getManagementIp().equals("")) {
					DeviceDiscoveryEntity deviceDetails = deviceDiscoveryRepository
							.findByDHostNameAndDMgmtIpAndDDeComm(requestinfo.getHostname(),requestinfo.getManagementIp(),"0");
					String statusVAlue = requestInfoDetailsDao.getPreviousMileStoneStatus(requestinfo.getAlphanumericReqId(),
							requestinfo.getRequestVersion());
					requestInfoDao.editRequestforReportWebserviceInfo(requestinfo.getAlphanumericReqId(),
							Double.toString(requestinfo.getRequestVersion()), "network_test", "4", statusVAlue);

					requestinfo.setAlphanumericReqId(RequestId);
					requestinfo.setRequestVersion(Double.parseDouble(json.get("version").toString()));

					if (type.equalsIgnoreCase("SLGC") || type.equalsIgnoreCase("SLGT") || type.equalsIgnoreCase("SNRC")
							|| type.equalsIgnoreCase("SNNC") || type.equalsIgnoreCase("SLGA")
							|| type.equalsIgnoreCase("SLGM") || type.equalsIgnoreCase("SNRM")
							|| type.equalsIgnoreCase("SNNM")) {
						NetworkTestValidation.loadProperties();
						String host = requestinfo.getManagementIp();
						CredentialManagementEntity routerCredential = dcmConfigService.getRouterCredential(
								deviceDetails);
						String user = routerCredential.getLoginRead();
						String password = routerCredential.getPasswordWrite();	
						
						/*if (type.equalsIgnoreCase("SNRC") || type.equalsIgnoreCase("SNNC")) {
							user = "c3pteam";
							password = "csr1000v";
						} else {
							user = userPojo.getUsername();
							password = userPojo.getPassword();
						}*/
						String port = NetworkTestValidation.TSA_PROPERTIES.getProperty("portSSH");
						session = jsch.getSession(user, host, Integer.parseInt(port));
						Properties config = new Properties();
						config.put("StrictHostKeyChecking", "no");
						session.setConfig(config);
						session.setPassword(password);
						logger.info("Before session.connet in network test validation Username" + user
								+ " Password " + password + " host" + host);
						session.connect();
						try {
							Thread.sleep(5000);
						} catch (Exception ee) {
						}
						try {
							
							channel = session.openChannel("shell");
							OutputStream ops = channel.getOutputStream();

							PrintStream ps = new PrintStream(ops, true);
							logger.info("Channel Connected to machine " + host + " server");
							channel.connect();
							InputStream input = channel.getInputStream();
							ps.println("terminal length 0");
							if (requestinfo.getCertificationSelectionBit().substring(0, 1).equalsIgnoreCase("1")) {
								ps.println("show ip interface brief");
								printResult(input, channel, requestinfo.getAlphanumericReqId(),
										Double.toString(requestinfo.getRequestVersion()));
								requestinfo.setNetwork_test_interfaceStatus("Passed");
							}
							if (requestinfo.getCertificationSelectionBit().substring(1, 2).equalsIgnoreCase("1")) {
								// ps.println("show interface "
								// + configRequest.getC3p_interface().getName());
								printResult(input, channel, requestinfo.getAlphanumericReqId(),
										Double.toString(requestinfo.getRequestVersion()));
								requestinfo.setNetwork_test_wanInterface("Passed");
							}
							if (requestinfo.getCertificationSelectionBit().substring(2, 3).equalsIgnoreCase("1")) {
								ps.println("show version");
								printResult(input, channel, requestinfo.getAlphanumericReqId(),
										Double.toString(requestinfo.getRequestVersion()));
								requestinfo.setNetwork_test_platformIOS("Passed");
							}
							if (requestinfo.getCertificationSelectionBit().substring(3, 4).equalsIgnoreCase("1")) {
								ps.println("sh ip bgp summary");
								printResult(input, channel, requestinfo.getAlphanumericReqId(),
										Double.toString(requestinfo.getRequestVersion()));
								requestinfo.setNetwork_test_BGPNeighbor("Passed");
							}

							try {
								Thread.sleep(6000);
							} catch (Exception ee) {
							}

							printResult(input, channel, requestinfo.getAlphanumericReqId(),
									Double.toString(requestinfo.getRequestVersion()));

							if (channel.isClosed()) {
								channel.connect();

							}

							/*
							 * Owner: Ruchita Salvi Module: Test Strategey Logic: To find and run and
							 * analyse custom tests
							 */

							// input = channel.getInputStream();
							// fetch extra network test added
							List<Boolean> results = null;
							RequestInfoDao dao = new RequestInfoDao();
							List<TestDetail> listOfTests = new ArrayList<TestDetail>();
							List<TestDetail> finallistOfTests = new ArrayList<TestDetail>();
							TestDetail test = new TestDetail();
							listOfTests = dao.findTestFromTestStrategyDB(
									requestinfo.getFamily(), requestinfo.getOs(), requestinfo.getOsVersion(),
									requestinfo.getVendor(), requestinfo.getRegion(), "Network Test");
							List<TestDetail> selectedTests = dao.findSelectedTests(requestinfo.getAlphanumericReqId(),
									"Network Test",version);
							if (selectedTests.size() > 0) {
								for (int i = 0; i < listOfTests.size(); i++) {
									for (int j = 0; j < selectedTests.size(); j++) {
										if (selectedTests.get(j).getTestName()
												.equalsIgnoreCase(listOfTests.get(i).getTestName())) {
											finallistOfTests.add(listOfTests.get(i));

										}

									}
								}

								if (finallistOfTests.size() > 0) {
									results = new ArrayList<Boolean>();
									for (int i = 0; i < finallistOfTests.size(); i++) {
									


										// conduct and analyse the tests
										
										if(deviceDetails.getdConnect().equalsIgnoreCase("NETCONF"))
										{
											VNFHelper helper=new VNFHelper();
											helper.performTest(finallistOfTests.get(i),requestinfo, user, password);
										}
										else if(deviceDetails.getdConnect().equalsIgnoreCase("RESTCONF"))
										{
											ODLClient client=new ODLClient();
											client.performTest(finallistOfTests.get(i),requestinfo, user, password);
										}
										else
										{
											ps.println(finallistOfTests.get(i).getTestCommand());
											try {
												Thread.sleep(6000);
											} catch (Exception ee) {
											}

											// printResult(input,
											// channel,configRequest.getRequestId(),Double.toString(configRequest.getRequest_version()));
											Boolean res = testStrategeyAnalyser.printAndAnalyse(input, channel,
													requestinfo.getAlphanumericReqId(),
													Double.toString(requestinfo.getRequestVersion()),
													finallistOfTests.get(i), "Network Test");
											results.add(res);
										}
									
									
									}
								}

							} else {
								// No new network test added
							}

							/*
							 * END
							 */

							logger.info("Certification bits in network test "
									+ requestinfo.getCertificationSelectionBit().substring(0, 1).equalsIgnoreCase("1")
									+ requestinfo.getCertificationSelectionBit().substring(1, 2)
									+ requestinfo.getCertificationSelectionBit().substring(2, 3)
									+ requestinfo.getCertificationSelectionBit().substring(3, 4));
							// working on simulator so condition has been set to true
						
								boolean result = true;
								if (listOfTests.size() > 0) {
									if (results != null && !results.isEmpty()) {
										for (int i = 0; i < results.size(); i++) {
											if (results.get(i) == false) {
												result = false;
											}
										}
									}
								
								}
								if (requestinfo.getCertificationSelectionBit().substring(0, 1).equalsIgnoreCase("1")
										|| requestinfo.getCertificationSelectionBit().substring(1, 2).equalsIgnoreCase("1")
										|| requestinfo.getCertificationSelectionBit().substring(2, 3).equalsIgnoreCase("1")
										|| requestinfo.getCertificationSelectionBit().substring(3, 4)
												.equalsIgnoreCase("1"))
								{
								requestInfoDao.updateNetworkTestStatus(requestinfo.getAlphanumericReqId(),
										Double.toString(requestinfo.getRequestVersion()),
										Integer.parseInt(requestinfo.getCertificationSelectionBit().substring(0, 1)),
										Integer.parseInt(requestinfo.getCertificationSelectionBit().substring(1, 2)),
										Integer.parseInt(requestinfo.getCertificationSelectionBit().substring(2, 3)),
										Integer.parseInt(requestinfo.getCertificationSelectionBit().substring(3, 4)));
								}
								String status = requestInfoDetailsDao.getPreviousMileStoneStatus(
										requestinfo.getAlphanumericReqId(), requestinfo.getRequestVersion());
								int statusData = requestInfoDetailsDao.getStatusForMilestone(requestinfo.getAlphanumericReqId(),
										Double.toString(requestinfo.getRequestVersion()), "network_test");
								if (statusData != 3) {
									requestInfoDao.editRequestforReportWebserviceInfo(
											requestinfo.getAlphanumericReqId(),
											Double.toString(requestinfo.getRequestVersion()), "network_test", "1",
											status);
								}

							
						
							
							//value = true;// hardcoded for default tests

							// this is to evaluate according to newly added tests else it is true by
							// default.
							if (results != null) {
								for (int i = 0; i < results.size(); i++) {
									if (!results.get(i)) {
										value = false;
										break;
									}

								}
							}
							else
							{
								value = true;
							}
							channel.disconnect();
							session.disconnect();
							logger.info("DONE");
							jsonArray = new Gson().toJson(value);
							try {
								Thread.sleep(15000);
							} catch (Exception ee) {
							}
							obj.put(new String("output"), jsonArray);
						} catch (IOException ex) {
							jsonArray = new Gson().toJson(value);
							obj.put(new String("output"), jsonArray);
							requestInfoDetailsDao.editRequestforReportWebserviceInfo(requestinfo.getAlphanumericReqId(),
									Double.toString(requestinfo.getRequestVersion()), "network_test", "2", "Failure");

							String response = "";
							String responseDownloadPath = "";
							try {
								response = invokeFtl.generateNetworkTestResultFileFailure(requestinfo);
								responseDownloadPath = NetworkTestValidation.TSA_PROPERTIES
										.getProperty("responseDownloadPath");
								TextReport.writeFile(responseDownloadPath,
										requestinfo.getAlphanumericReqId() + "V"
												+ Double.toString(requestinfo.getRequestVersion()) + "_networkTest.txt",
										response);
							} catch (Exception e) {
								// TODO Auto-generated catch block

							}

						}
						channel.disconnect();
						session.disconnect();
					}

					else {
						value = true;
						logger.info("DONE Network Test");
						jsonArray = new Gson().toJson(value);
						obj.put(new String("output"), jsonArray);
					}
				}
			}
			// when reachability fails
			catch (Exception ex) {
				if (requestinfo.getManagementIp() != null && !requestinfo.getManagementIp().equals("")) {

					logger.info("Exception in network tst" + ex.getMessage());
					jsonArray = new Gson().toJson(value);
					obj.put(new String("output"), jsonArray);
					requestInfoDetailsDao.editRequestforReportWebserviceInfo(requestinfo.getAlphanumericReqId(),
							Double.toString(requestinfo.getRequestVersion()), "network_test", "2", "Failure");
					String response = "";
					String responseDownloadPath = "";
					try {
						response = invokeFtl.generateNetworkTestResultFileFailure(requestinfo);
						responseDownloadPath = NetworkTestValidation.TSA_PROPERTIES.getProperty("responseDownloadPath");
						TextReport.writeFile(responseDownloadPath,
								requestinfo.getAlphanumericReqId() + "V"
										+ Double.toString(requestinfo.getRequestVersion()) + "_networkTest.txt",
								response);
					} catch (Exception e) {
						// TODO Auto-generated catch block

					}

				}
			}
			finally {

				if (channel != null) {
					try {
					session = channel.getSession();
					
					if (channel.getExitStatus() == -1) {
						
							Thread.sleep(5000);
						
					}
					} catch (Exception e) {
						System.out.println(e);
					}
					channel.disconnect();
					session.disconnect();
				
				}
			}
			
		} else {
			value = true;

			jsonArray = new Gson().toJson(value);
			obj.put(new String("output"), jsonArray);

		}

		return obj;

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

	private static void printResult(InputStream input, Channel channel, String requestID, String version)
			throws Exception {
		BufferedWriter bw = null;
		FileWriter fw = null;
		int SIZE = 1024;
		byte[] tmp = new byte[SIZE];

		while (input.available() > 0) {
			int i = input.read(tmp, 0, SIZE);
			if (i < 0)
				break;
			/* logger.info(new String(tmp, 0, i)); */

			String s = new String(tmp, 0, i);
			if (!(s.equals(""))) {
				logger.info(s);

				String filepath = NetworkTestValidation.TSA_PROPERTIES.getProperty("responseDownloadPath")
						+ requestID + "V" + version + "_networkTest.txt";
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
			logger.info("exit-status: " + channel.getExitStatus());

		}
		try {
			Thread.sleep(1000);
		} catch (Exception ee) {
		}

	}

	@SuppressWarnings("resource")
	public String validateNetworkTest(CreateConfigRequest configRequest) throws Exception {
		NetworkTestValidation.loadProperties();
		logger.info("In side validate network test line no 356");
		String content = "";
		String path = NetworkTestValidation.TSA_PROPERTIES.getProperty("responseDownloadPath")
				+ configRequest.getRequestId() + "V" + configRequest.getRequest_version() + "_networkTest.txt";

		File file = new File(path);
		Scanner in = null;
		try {
			if (file.exists()) {
				in = new Scanner(file);
				while (in.hasNext()) {
					String line = in.nextLine();

					String interfacename = configRequest.getC3p_interface().getName();
					if (interfacename == null) {
						configRequest.getC3p_interface().setName("");

					}
					if (line.contains(configRequest.getC3p_interface().getName())) {
						logger.info(line);
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
		logger.info("path" + path);

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

	/* method overloading for UIRevamp */
	public String validateNetworkTest(RequestInfoPojo requestinfo) throws IOException {

		NetworkTestValidation.loadProperties();
		logger.info("In side validate network test line no 356");
		String content = "";
		String path = NetworkTestValidation.TSA_PROPERTIES.getProperty("responseDownloadPath")
				+ requestinfo.getAlphanumericReqId() + "V" + requestinfo.getRequestVersion() + "_networkTest.txt";

		File file = new File(path);
		Scanner in = null;
		try {
			if (file.exists()) {
				in = new Scanner(file);
				while (in.hasNext()) {
					String line = in.nextLine();

					// String interfacename = requestinfo.getC3p_interface()
					// .getName();
					// if (interfacename == null) {
					// requestinfo.getC3p_interface().setName("");
					//
					// }
					// if (line.contains(requestinfo.getC3p_interface()
					// .getName())) {
					// logger.info(line);
					// content = line;
					// break;
					// }

				}
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return content;
	}
}