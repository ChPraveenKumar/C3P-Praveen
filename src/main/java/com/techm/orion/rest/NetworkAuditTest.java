package com.techm.orion.rest;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.LineNumberReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
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

/*
 * Owner: Vivek Vidhate Module: Test Strategey Logic: To
 * find and run and analyse Network Audit tests
 */
@Controller
@RequestMapping("/NetworkAuditTest")
public class NetworkAuditTest extends Thread {

	public static String TSA_PROPERTIES_FILE = "TSA.properties";
	public static final Properties TSA_PROPERTIES = new Properties();
	
	@Autowired
	RequestInfoDao requestInfoDao;

	@Autowired
	RequestInfoDetailsDao requestDao;
	
	@POST
	@RequestMapping(value = "/networkAuditCommandTest", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
	@ResponseBody
	public JSONObject NetworkAuditCommandTest(@RequestBody String request) throws ParseException {

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

		String type = RequestId.substring(0, Math.min(RequestId.length(), 4));

		if (!(type.equals("SLGB"))) {

			try {
				configRequest = requestInfoDao.getRequestDetailFromDBForVersion(RequestId, version);

				requestinfo = requestDao.getRequestDetailTRequestInfoDBForVersion(RequestId, version);

				if (configRequest.getManagementIp() != null && !configRequest.getManagementIp().equals("")) {
					configRequest = requestInfoDao.getRequestDetailFromDBForVersion(RequestId, version);

					configRequest.setRequestId(RequestId);
					configRequest.setRequest_version(Double.parseDouble(json.get("version").toString()));

					NetworkAuditTest.loadProperties();
					String sshPrivateKeyFilePath = NetworkAuditTest.TSA_PROPERTIES.getProperty("sshPrivateKeyPath");
					String host = configRequest.getManagementIp();
					UserPojo userPojo = new UserPojo();
					userPojo = requestInfoDao.getRouterCredentials();
					System.out.println("Request ID in Network audit test validation" + RequestId);
					String user = userPojo.getUsername();
					String password = userPojo.getPassword();
					String port = NetworkAuditTest.TSA_PROPERTIES.getProperty("portSSH");
					ArrayList<String> commandToPush = new ArrayList<String>();
					/* Logic to connect router */
					String privateKeyPath = NetworkAuditTest.TSA_PROPERTIES.getProperty("sshPrivateKeyPath");

					if (type.equalsIgnoreCase("SLGC") || type.equalsIgnoreCase("SLGT")) {
						JSch jsch = new JSch();
						Channel channel = null;
						Session session = jsch.getSession(user, host, Integer.parseInt(port));
						Properties config = new Properties();
						config.put("StrictHostKeyChecking", "no");
						System.out.println("Password for network audit test " + password + "user " + user + "host "
								+ host + "Port " + port);
						session.setConfig(config);
						session.setPassword(password);
						session.connect();
						System.out.println("After session.connect Network audit milestone");
						try {
							Thread.sleep(10000);
						} catch (Exception ee) {
						}
						try {

							channel = session.openChannel("shell");
							OutputStream ops = channel.getOutputStream();

							PrintStream ps = new PrintStream(ops, true);
							System.out.println("Channel Connected to machine " + host + " server");
							channel.connect();
							InputStream input = channel.getInputStream();
							/* Logic to collect number of select test out of all */
							List<TestDetail> finallistOfTests = new ArrayList<TestDetail>();
							RequestInfoDao dao = new RequestInfoDao();
							List<TestDetail> listOfTests = new ArrayList<TestDetail>();
							TestDetail test = new TestDetail();
							listOfTests = dao.findTestFromTestStrategyDB(configRequest.getModel(),
									configRequest.getDeviceType(), configRequest.getOs(), configRequest.getOsVersion(),
									configRequest.getVendor(), configRequest.getRegion(), "Network Audit");
							List<TestDetail> selectedTests = dao.findSelectedTests(configRequest.getRequestId(),
									"Network Audit");
							List<Boolean> results = null;
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
										ps.println("terminal length 0");
										ps.println(finallistOfTests.get(i).getTestCommand());
										try {
											Thread.sleep(8000);
										} catch (Exception ee) {
										}
										/*
										 * Collect Network Audit test result for snippet and keyword from router
										 */
										Boolean res = TestStrategeyAnalyser.printAndAnalyse(input, channel,
												configRequest.getRequestId(),
												Double.toString(configRequest.getRequest_version()),
												finallistOfTests.get(i), "Network Audit");
										results.add(res);

										String status = requestInfoDao.getPreviousMileStoneStatus(
												configRequest.getRequestId(),
												Double.toString(configRequest.getRequest_version()));
										String switchh = "1";

										requestInfoDao.editRequestforReportWebserviceInfo(configRequest.getRequestId(),
												Double.toString(configRequest.getRequest_version()), "network_audit",
												"1", status);

										channel.disconnect();
										session.disconnect();
										value = true;
									}
								}

							} else {
								String status = requestInfoDao.getPreviousMileStoneStatus(configRequest.getRequestId(),
										Double.toString(configRequest.getRequest_version()));
								String switchh = "1";

								requestInfoDao.editRequestforReportWebserviceInfo(configRequest.getRequestId(),
										Double.toString(configRequest.getRequest_version()), "network_audit", "0",
										status);

								channel.disconnect();
								session.disconnect();
								value = true;

							}

							/* Updating web service info table for true or false case */

							if (results != null) {
								for (int i = 0; i < results.size(); i++) {
									if (!results.get(i)) {
										value = false;
										break;
									}

								}
							}
							if (!channel.isClosed()) {
								channel.disconnect();
							}
							session.disconnect();
							try {
								Thread.sleep(15000);
							} catch (Exception ee) {
							}

							jsonArray = new Gson().toJson(value);
							obj.put(new String("output"), jsonArray);
						} catch (IOException ex) {
							System.out.println("Error in Network Audit check first catch " + ex.getMessage());
							System.out.println("Error trace " + ex.getStackTrace());
							System.out.println("" + ex.getCause());
							jsonArray = new Gson().toJson(value);
							obj.put(new String("output"), jsonArray);
							requestInfoDao.editRequestforReportWebserviceInfo(configRequest.getRequestId(),
									Double.toString(configRequest.getRequest_version()), "network_audit", "2",
									"Failure");

							String response = "";
							String responseDownloadPath = "";
							try {
								response = invokeFtl.generateNetworkAuditTestResultFailure(configRequest);
								requestInfoDao.updateNetworkAuditTestStatus(configRequest.getRequestId(),
										Double.toString(configRequest.getRequest_version()), 0, 0, 0);
								requestInfoDao.updateRouterFailureHealthCheck(configRequest.getRequestId(),
										Double.toString(configRequest.getRequest_version()));
								responseDownloadPath = NetworkAuditTest.TSA_PROPERTIES
										.getProperty("responseDownloadPath");
								TextReport.writeFile(responseDownloadPath,
										configRequest.getRequestId() + "V"
												+ Double.toString(configRequest.getRequest_version()) + "_.txt",
										response);
							} catch (Exception e) {
								// TODO Auto-generated catch block

							}
						}

						session.disconnect();
					} else if (type.equalsIgnoreCase("SLGF")) {
						PostUpgradeHealthCheck osHealthChk = new PostUpgradeHealthCheck();
						obj = osHealthChk.healthcheckCommandTest(request, "POST");
					} else if (type.equalsIgnoreCase("SNRC") || type.equalsIgnoreCase("SNNC")) {
						// TO be done
						value = true;
						System.out.println("DONE Network Test");
						jsonArray = new Gson().toJson(value);
						obj.put(new String("output"), jsonArray);
					}

				} else if (requestinfo.getManagementIp() != null && !requestinfo.getManagementIp().equals("")) {

					NetworkAuditTest.loadProperties();
					String sshPrivateKeyFilePath = NetworkAuditTest.TSA_PROPERTIES.getProperty("sshPrivateKeyPath");
					String host = requestinfo.getManagementIp();
					UserPojo userPojo = new UserPojo();
					userPojo = requestInfoDao.getRouterCredentials();
					System.out.println("Request ID in Network audit test validation" + RequestId);
					String user = userPojo.getUsername();
					String password = userPojo.getPassword();
					String port = NetworkAuditTest.TSA_PROPERTIES.getProperty("portSSH");
					ArrayList<String> commandToPush = new ArrayList<String>();
					/* Logic to connect router */
					String privateKeyPath = NetworkAuditTest.TSA_PROPERTIES.getProperty("sshPrivateKeyPath");

					if (type.equalsIgnoreCase("SLGC") || type.equalsIgnoreCase("SLGT")) {
						JSch jsch = new JSch();
						Channel channel = null;
						Session session = jsch.getSession(user, host, Integer.parseInt(port));
						Properties config = new Properties();
						config.put("StrictHostKeyChecking", "no");
						System.out.println("Password for network audit test " + password + "user " + user + "host "
								+ host + "Port " + port);
						session.setConfig(config);
						session.setPassword(password);
						session.connect();
						System.out.println("After session.connect Network audit milestone");
						try {
							Thread.sleep(10000);
						} catch (Exception ee) {
						}
						try {

							channel = session.openChannel("shell");
							OutputStream ops = channel.getOutputStream();

							PrintStream ps = new PrintStream(ops, true);
							System.out.println("Channel Connected to machine " + host + " server");
							channel.connect();
							InputStream input = channel.getInputStream();
							/* Logic to collect number of select test out of all */
							List<TestDetail> finallistOfTests = new ArrayList<TestDetail>();
							RequestInfoDao dao = new RequestInfoDao();
							List<TestDetail> listOfTests = new ArrayList<TestDetail>();
							TestDetail test = new TestDetail();
							listOfTests = dao.findTestFromTestStrategyDB(requestinfo.getModel(),
									requestinfo.getDeviceType(), requestinfo.getOs(), requestinfo.getOsVersion(),
									requestinfo.getVendor(), requestinfo.getRegion(), "Network Audit");
							List<TestDetail> selectedTests = dao.findSelectedTests(requestinfo.getAlphanumericReqId(),
									"Network Audit");
							List<Boolean> results = null;
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
										ps.println("terminal length 0");
										ps.println(finallistOfTests.get(i).getTestCommand());
										try {
											Thread.sleep(8000);
										} catch (Exception ee) {
										}
										/*
										 * Collect Network Audit test result for snippet and keyword from router
										 */
										Boolean res = TestStrategeyAnalyser.printAndAnalyse(input, channel,
												requestinfo.getAlphanumericReqId(),
												Double.toString(requestinfo.getRequestVersion()),
												finallistOfTests.get(i), "Network Audit");
										results.add(res);

										String status = requestDao.getPreviousMileStoneStatus(
												requestinfo.getAlphanumericReqId(),
												requestinfo.getRequestVersion());
										String switchh = "1";

										requestDao.editRequestforReportWebserviceInfo(requestinfo.getAlphanumericReqId(),
												Double.toString(requestinfo.getRequestVersion()), "network_audit",
												"1", status);

										channel.disconnect();
										session.disconnect();
										value = true;
									}
								}

							} else {
								String status = requestDao.getPreviousMileStoneStatus(requestinfo.getAlphanumericReqId(),
										requestinfo.getRequestVersion());
								String switchh = "1";

								requestDao.editRequestforReportWebserviceInfo(requestinfo.getAlphanumericReqId(),
										Double.toString(requestinfo.getRequestVersion()), "network_audit", "0",
										status);

								channel.disconnect();
								session.disconnect();
								value = true;

							}

							/* Updating web service info table for true or false case */

							if (results != null) {
								for (int i = 0; i < results.size(); i++) {
									if (!results.get(i)) {
										value = false;
										break;
									}

								}
							}
							if (!channel.isClosed()) {
								channel.disconnect();
							}
							session.disconnect();
							try {
								Thread.sleep(15000);
							} catch (Exception ee) {
							}

							jsonArray = new Gson().toJson(value);
							obj.put(new String("output"), jsonArray);
						} catch (IOException ex) {
							System.out.println("Error in Network Audit check first catch " + ex.getMessage());
							System.out.println("Error trace " + ex.getStackTrace());
							System.out.println("" + ex.getCause());
							jsonArray = new Gson().toJson(value);
							obj.put(new String("output"), jsonArray);
							requestDao.editRequestforReportWebserviceInfo(requestinfo.getAlphanumericReqId(),
									Double.toString(requestinfo.getRequestVersion()), "network_audit", "2",
									"Failure");

							String response = "";
							String responseDownloadPath = "";
							try {
								response = invokeFtl.generateNetworkAuditTestResultFailure(configRequest);
								requestInfoDao.updateNetworkAuditTestStatus(configRequest.getRequestId(),
										Double.toString(configRequest.getRequest_version()), 0, 0, 0);
								requestInfoDao.updateRouterFailureHealthCheck(configRequest.getRequestId(),
										Double.toString(configRequest.getRequest_version()));
								responseDownloadPath = NetworkAuditTest.TSA_PROPERTIES
										.getProperty("responseDownloadPath");
								TextReport.writeFile(responseDownloadPath,
										configRequest.getRequestId() + "V"
												+ Double.toString(configRequest.getRequest_version()) + "_.txt",
										response);
							} catch (Exception e) {
								// TODO Auto-generated catch block

							}
						}

						session.disconnect();
					} else if (type.equalsIgnoreCase("SLGF")) {
						PostUpgradeHealthCheck osHealthChk = new PostUpgradeHealthCheck();
						obj = osHealthChk.healthcheckCommandTest(request, "POST");
					} else if (type.equalsIgnoreCase("SNRC") || type.equalsIgnoreCase("SNNC")) {
						// TO be done
						value = true;
						System.out.println("DONE Network Test");
						jsonArray = new Gson().toJson(value);
						obj.put(new String("output"), jsonArray);
					}

				}
			}
			// when reachability fails
			catch (Exception ex) {
				if (configRequest.getManagementIp() != null && !configRequest.getManagementIp().equals("")) {
					System.out.println("Error in Network audit send catch " + ex.getMessage());
					System.out.println("Error trace " + ex.getStackTrace());
					ex.printStackTrace();
					jsonArray = new Gson().toJson(value);
					obj.put(new String("output"), jsonArray);
					requestInfoDao.editRequestforReportWebserviceInfo(configRequest.getRequestId(),
							Double.toString(configRequest.getRequest_version()), "network_audit", "2", "Failure");

					String response = "";
					String responseDownloadPath = "";
					try {
						response = invokeFtl.generateNetworkAuditTestResultFailure(configRequest);
						requestInfoDao.updateNetworkAuditTestStatus(configRequest.getRequestId(),
								Double.toString(configRequest.getRequest_version()), 0, 0, 0);
						requestInfoDao.updateRouterFailureHealthCheck(configRequest.getRequestId(),
								Double.toString(configRequest.getRequest_version()));
						responseDownloadPath = NetworkAuditTest.TSA_PROPERTIES.getProperty("responseDownloadPath");
						TextReport.writeFile(
								responseDownloadPath, configRequest.getRequestId() + "V"
										+ Double.toString(configRequest.getRequest_version()) + "_CustomTests.txt",
								response);
						requestInfoDao.releaselockDeviceForRequest(configRequest.getManagementIp(),
								configRequest.getRequestId());
					} catch (Exception e) {
						// TODO Auto-generated catch block

					}				
				} else if (requestinfo.getManagementIp() != null && !requestinfo.getManagementIp().equals("")) {
					System.out.println("Error in Network audit send catch " + ex.getMessage());
					System.out.println("Error trace " + ex.getStackTrace());
					ex.printStackTrace();
					jsonArray = new Gson().toJson(value);
					obj.put(new String("output"), jsonArray);
					requestInfoDao.editRequestforReportWebserviceInfo(requestinfo.getAlphanumericReqId(),
							Double.toString(requestinfo.getRequestVersion()), "network_audit", "2", "Failure");

					String response = "";
					String responseDownloadPath = "";
					try {
						response = invokeFtl.generateNetworkAuditTestResultFailure(requestinfo);
						requestInfoDao.updateNetworkAuditTestStatus(requestinfo.getAlphanumericReqId(),
								Double.toString(requestinfo.getRequestVersion()), 0, 0, 0);
						requestInfoDao.updateRouterFailureHealthCheck(requestinfo.getAlphanumericReqId(),
								Double.toString(requestinfo.getRequestVersion()));
						responseDownloadPath = NetworkAuditTest.TSA_PROPERTIES.getProperty("responseDownloadPath");
						TextReport.writeFile(
								responseDownloadPath, requestinfo.getAlphanumericReqId() + "V"
										+ Double.toString(requestinfo.getRequestVersion()) + "_CustomTests.txt",
								response);
						requestInfoDao.releaselockDeviceForRequest(requestinfo.getManagementIp(),
								requestinfo.getAlphanumericReqId());
					} catch (Exception e) {
						// TODO Auto-generated catch block

					}
				
				}
			}
		} else {
			value = true;

			jsonArray = new Gson().toJson(value);
			obj.put(new String("output"), jsonArray);

		}

		/*
		 * return Response .status(200) .header("Access-Control-Allow-Origin", "*")
		 * .header("Access-Control-Allow-Headers",
		 * "origin, content-type, accept, authorization")
		 * .header("Access-Control-Allow-Credentials", "true")
		 * .header("Access-Control-Allow-Methods",
		 * "GET, POST, PUT, DELETE, OPTIONS, HEAD") .header("Access-Control-Max-Age",
		 * "1209600").entity(obj) .build();
		 */

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

	@SuppressWarnings("resource")
	public ArrayList<String> readFileNoCmd(String requestIdForConfig, String version) throws IOException {
		BufferedReader br = null;
		LineNumberReader rdr = null;
		/* StringBuilder sb2=null; */
		String responseDownloadPath = NetworkAuditTest.TSA_PROPERTIES.getProperty("responseDownloadPath");
		String filePath = responseDownloadPath + "//" + requestIdForConfig + "V" + version + "_ConfigurationNoCmd";

		br = new BufferedReader(new FileReader(filePath));
		File f = new File(filePath);
		try {
			ArrayList<String> ar = new ArrayList<String>();
			if (f.exists()) {

				StringBuffer send = null;
				StringBuilder sb2 = new StringBuilder();

				rdr = new LineNumberReader(new FileReader(filePath));
				InputStream is = new BufferedInputStream(new FileInputStream(filePath));

				byte[] c = new byte[1024];
				int count = 0;
				int readChars = 0;
				boolean empty = true;
				while ((readChars = is.read(c)) != -1) {
					empty = false;
					for (int i = 0; i < readChars; ++i) {
						if (c[i] == '\n') {
							++count;
						}
					}
				}
				int fileReadSize = Integer.parseInt(NetworkAuditTest.TSA_PROPERTIES.getProperty("fileChunkSize"));
				int chunks = (count / fileReadSize) + 1;
				String line;

				for (int loop = 1; loop <= chunks; loop++) {
					if (loop == 1) {
						rdr = new LineNumberReader(new FileReader(filePath));
						line = rdr.readLine();
						sb2.append(line).append("\n");
						for (line = null; (line = rdr.readLine()) != null;) {

							if (rdr.getLineNumber() <= fileReadSize) {
								sb2.append(line).append("\n");
							}

						}
						ar.add(sb2.toString());
					} else {
						LineNumberReader rdr1 = new LineNumberReader(new FileReader(filePath));
						sb2 = new StringBuilder();
						for (line = null; (line = rdr1.readLine()) != null;) {

							if (rdr1.getLineNumber() > (fileReadSize * (loop - 1))
									&& rdr1.getLineNumber() <= (fileReadSize * loop)) {
								sb2.append(line).append("\n");
							}

						}
						ar.add(sb2.toString());
					}

				}

			}
			return ar;
		} finally {
			br.close();
		}
	}

	public void printResult(InputStream input, Channel channel, String requestId, String version) throws Exception {
		BufferedWriter bw = null;
		FileWriter fw = null;
		int SIZE = 1024;
		byte[] tmp = new byte[SIZE];
		String responselogpath = NetworkAuditTest.TSA_PROPERTIES.getProperty("responselogpath");
		File file = new File(responselogpath + "/" + requestId + "_" + version + "theSSHfile.txt");
		/*
		 * if (file.exists()) { file.delete(); }
		 */
		while (input.available() > 0) {
			int i = input.read(tmp, 0, SIZE);
			if (i < 0)
				break;

			String s = new String(tmp, 0, i);
			if (!(s.equals(""))) {

				file = new File(responselogpath + "/" + requestId + "_" + version + "theSSHfile.txt");

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
	public ArrayList<String> readFile(String requestIdForConfig, String version) throws IOException {
		BufferedReader br = null;
		LineNumberReader rdr = null;
		/* StringBuilder sb2=null; */
		String responseDownloadPath = NetworkAuditTest.TSA_PROPERTIES.getProperty("responseDownloadPath");
		String filePath = responseDownloadPath + "//" + requestIdForConfig + "V" + version + "_Configuration";

		br = new BufferedReader(new FileReader(filePath));
		try {
			ArrayList<String> ar = new ArrayList<String>();
			// StringBuffer send = null;
			StringBuilder sb2 = new StringBuilder();

			rdr = new LineNumberReader(new FileReader(filePath));
			InputStream is = new BufferedInputStream(new FileInputStream(filePath));

			byte[] c = new byte[1024];
			int count = 0;
			int readChars = 0;
			boolean empty = true;
			while ((readChars = is.read(c)) != -1) {
				empty = false;
				for (int i = 0; i < readChars; ++i) {
					if (c[i] == '\n') {
						++count;
					}
				}
			}
			int fileReadSize = Integer.parseInt(NetworkAuditTest.TSA_PROPERTIES.getProperty("fileChunkSize"));
			int chunks = (count / fileReadSize) + 1;
			String line;

			for (int loop = 1; loop <= chunks; loop++) {
				if (loop == 1) {
					rdr = new LineNumberReader(new FileReader(filePath));
					line = rdr.readLine();
					sb2.append(line).append("\n");
					for (line = null; (line = rdr.readLine()) != null;) {

						if (rdr.getLineNumber() <= fileReadSize) {
							sb2.append(line).append("\n");
						}

					}
					ar.add(sb2.toString());
				} else {
					LineNumberReader rdr1 = new LineNumberReader(new FileReader(filePath));
					sb2 = new StringBuilder();
					for (line = null; (line = rdr1.readLine()) != null;) {

						if (rdr1.getLineNumber() > (fileReadSize * (loop - 1))
								&& rdr1.getLineNumber() <= (fileReadSize * loop)) {
							sb2.append(line).append("\n");
						}

					}
					ar.add(sb2.toString());
				}

			}
			return ar;
		} finally {
			br.close();
		}
	}

	private static void cmdCall(String requestId, String version, String managementIp) throws Exception {
		ProcessBuilder builder = new ProcessBuilder("cmd.exe");
		Process p = null;
		try {
			p = builder.start();
			BufferedWriter p_stdin = new BufferedWriter(new OutputStreamWriter(p.getOutputStream()));
			String filepath = NetworkAuditTest.TSA_PROPERTIES.getProperty("analyserPath");
			/*
			 * for (int i=0; i<2; i++) { p_stdin.write("cd..");
			 * 
			 * p_stdin.newLine(); p_stdin.flush(); }
			 */
			p_stdin.write("cd " + filepath);
			p_stdin.newLine();
			p_stdin.flush();
			p_stdin.write("ttcp -t nbufs 1 verbose host " + managementIp);
			p_stdin.newLine();
			p_stdin.flush();

			try {
				Thread.sleep(150000);
			} catch (Exception ee) {
			}
			InputStream input = p.getInputStream();
			printResult(input, requestId, version);
			p_stdin.write("exit");
			p_stdin.newLine();
			p_stdin.flush();
		}

		catch (IOException e) {
			e.printStackTrace();
		}

	}

	private static void cmdPingCall(String requestId, String version, String managementIp) throws Exception {
		ProcessBuilder builder = new ProcessBuilder("cmd.exe");
		Process p = null;
		try {
			p = builder.start();
			BufferedWriter p_stdin = new BufferedWriter(new OutputStreamWriter(p.getOutputStream()));

			String commandToPing = "ping " + managementIp + " -n 20";
			p_stdin.write(commandToPing);
			System.out.println("command To Ping : " + commandToPing);
			System.out.println("Management IP : " + managementIp);

			p_stdin.newLine();
			p_stdin.flush();
			try {
				Thread.sleep(21000);
			} catch (Exception ee) {
			}
			p_stdin.write("exit");
			p_stdin.newLine();
			p_stdin.flush();
		}

		catch (IOException e) {
			e.printStackTrace();
		}

		Scanner s = new Scanner(p.getInputStream());

		InputStream input = p.getInputStream();
		printResult(input, requestId, version);

		while (s.hasNext()) {
			System.out.println(s.nextLine());
		}
		s.close();
	}

	private static void printResult(InputStream input, String requestID, String version) throws Exception {
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
				String filepath = NetworkAuditTest.TSA_PROPERTIES.getProperty("responseDownloadPath") + "//" + requestID
						+ "V" + version + "_CustomTests.txt";
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
		/*
		 * if (channel.isClosed()) { System.out.println("exit-status: " +
		 * channel.getExitStatus());
		 * 
		 * }
		 */

	}

	private static String readFile() throws IOException {
		String responseDownloadPath = NetworkAuditTest.TSA_PROPERTIES.getProperty("responseDownloadPath");

		BufferedReader br = new BufferedReader(new FileReader(responseDownloadPath + "//" + "CustomTests.txt"));

		// BufferedReader br = new BufferedReader(new
		// FileReader("D:/C3P/New folder/HealthcheckTestCommand.txt"));
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

	private static String readFile(String path) throws IOException {

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

}