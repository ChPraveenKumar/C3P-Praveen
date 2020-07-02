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
import java.io.OutputStreamWriter;
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
import com.techm.orion.dao.RequestInfoDao;
import com.techm.orion.dao.RequestInfoDetailsDao;
import com.techm.orion.pojo.CreateConfigRequest;
import com.techm.orion.pojo.HealthCheckComponent;
import com.techm.orion.pojo.RequestInfoPojo;
import com.techm.orion.pojo.UserPojo;
import com.techm.orion.utility.HealthCheckReport;
import com.techm.orion.utility.InvokeFtl;
import com.techm.orion.utility.PingTest;
import com.techm.orion.utility.ShowCPUUsage;
import com.techm.orion.utility.ShowInventoryTest;
import com.techm.orion.utility.ShowMemoryTest;
import com.techm.orion.utility.ShowPowerTest;
import com.techm.orion.utility.ShowVersionTest;
import com.techm.orion.utility.TextReport;

@Controller
@RequestMapping("/OsUpgrade")
public class PostUpgradeHealthCheck extends Thread {
	private static final Logger logger = LogManager.getLogger(PostUpgradeHealthCheck.class);
	public static String TSA_PROPERTIES_FILE = "TSA.properties";
	public static final Properties TSA_PROPERTIES = new Properties();

	@Autowired
	RequestInfoDao requestInfoDao;

	@Autowired
	RequestInfoDetailsDao requestDao;

	@POST
	@RequestMapping(value = "/HealthCheck", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
	@ResponseBody
	public JSONObject healthcheckCommandTest(@RequestBody String request, String type) {

		JSONObject obj = new JSONObject();
		String jsonArray = "";
		InvokeFtl invokeFtl = new InvokeFtl();
		CreateConfigRequest configRequest = new CreateConfigRequest();
		RequestInfoPojo requestinfo = new RequestInfoPojo();
		boolean value = true;
		String testToFail = null;
		JSONParser parser = new JSONParser();
		JSONObject json;
		int rechabilityTst = 0;

		try {
			json = (JSONObject) parser.parse(request);
			String RequestId = json.get("requestId").toString();
			String version = json.get("version").toString();

			configRequest = requestInfoDao.getRequestDetailFromDBForVersion(RequestId, version);
			requestinfo = requestDao.getRequestDetailTRequestInfoDBForVersion(RequestId, version);
			if (configRequest.getManagementIp() != null && !configRequest.getManagementIp().equals("")) {

				configRequest.setRequestId(RequestId);
				configRequest.setRequest_version(Double.parseDouble(json.get("version").toString()));
				String host = configRequest.getManagementIp();
				UserPojo userPojo = new UserPojo();
				userPojo = requestInfoDao.getRouterCredentials();

				String user = userPojo.getUsername();
				String password = userPojo.getPassword();
				String port = PostUpgradeHealthCheck.TSA_PROPERTIES.getProperty("portSSH");
				PingTest pingClass = new PingTest();

				if (type.equalsIgnoreCase("Pre")) {
					testToFail = "pre_health_checkup";
				} else {
					testToFail = "health_check";
				}
				boolean reachability = pingClass.cmdPingCall(host, configRequest.getHostname(),
						configRequest.getRegion());
				if (reachability) {
					rechabilityTst = 1;
				} else {
					rechabilityTst = 0;

				}

				if (rechabilityTst == 1) {
					String OsversionOnDevice = null;
					ShowVersionTest versionTest = new ShowVersionTest();
					OsversionOnDevice = versionTest.versionInfo(host, user, password, configRequest.getHostname(),
							configRequest.getRegion(), type);

					String memoryResult = null, powerResult = null, cpu_usage_result = null;

					if (OsversionOnDevice.equalsIgnoreCase("JSchException")) {
						value = false;
						requestInfoDao.editRequestforReportWebserviceInfo(configRequest.getRequestId(),
								Double.toString(configRequest.getRequest_version()), testToFail, "2", "Failure");
						requestInfoDao.editRequestForReportIOSWebserviceInfo(configRequest.getRequestId(),
								Double.toString(configRequest.getRequest_version()), "Device Reachability Failure",
								"Failure", "Could not connect to the router.");

						try {
							List<HealthCheckComponent> resultList = new ArrayList<HealthCheckComponent>();
							HealthCheckReport healthCheckReport = new HealthCheckReport();
							healthCheckReport.createFailureReport(resultList, configRequest.getHostname(),
									configRequest.getRegion(), type);
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					} else {
						ShowInventoryTest inventoryTest = new ShowInventoryTest();
						ShowMemoryTest memoryTest = new ShowMemoryTest();
						ShowPowerTest powerTest = new ShowPowerTest();
						ShowCPUUsage cpu_usage = new ShowCPUUsage();
						memoryResult = memoryTest.memoryInfo(host, user, password, configRequest.getHostname(),
								configRequest.getRegion(), type);
						powerResult = powerTest.powerInfo(host, user, password, configRequest.getHostname(),
								configRequest.getRegion(), type);
						cpu_usage_result = cpu_usage.cpuUsageInfo(host, user, password, configRequest.getHostname(),
								configRequest.getRegion(), type);
						// Create Health check report
						List<HealthCheckComponent> resultList = new ArrayList<HealthCheckComponent>();
						HealthCheckComponent memoryComp = new HealthCheckComponent();
						memoryComp.setTestname("Memory Check");
						memoryComp.setTestresult(memoryResult);

						resultList.add(memoryComp);
						resultList.add(memoryComp);

						HealthCheckComponent powerComp = new HealthCheckComponent();
						powerComp.setTestname("Power Check");
						powerComp.setTestresult(powerResult);

						resultList.add(powerComp);

						HealthCheckComponent cpu_usage_comp = new HealthCheckComponent();
						cpu_usage_comp.setTestname("CPU Check");
						cpu_usage_comp.setTestresult(cpu_usage_result);

						resultList.add(cpu_usage_comp);

						HealthCheckReport healthCheckReport = new HealthCheckReport();
						healthCheckReport.createReport(resultList, configRequest.getHostname(),
								configRequest.getRegion(), type);

						if (memoryResult.equalsIgnoreCase("pass") && powerResult.equalsIgnoreCase("pass")
								&& cpu_usage_result.equalsIgnoreCase("pass")) {
							value = true;
							jsonArray = new Gson().toJson(value);
							obj.put(new String("output"), jsonArray);
							if (type.equalsIgnoreCase("Post")) {
								requestInfoDao.editRequestforReportWebserviceInfo(configRequest.getRequestId(),
										Double.toString(configRequest.getRequest_version()), "health_check", "1",
										"In Progress");

							} else if (type.equalsIgnoreCase("Pre")) {
								requestInfoDao.editRequestforReportWebserviceInfo(configRequest.getRequestId(),
										Double.toString(configRequest.getRequest_version()), "pre_health_checkup", "1",
										"In Progress");

							}
							requestInfoDao.releaselockDeviceForRequest(configRequest.getManagementIp(),
									configRequest.getRequestId());

						} else {
							value = false;
							jsonArray = new Gson().toJson(value);
							// released device lock
							requestInfoDao.releaselockDeviceForRequest(configRequest.getManagementIp(),
									configRequest.getRequestId());
							obj.put(new String("output"), jsonArray);
							if (type.equalsIgnoreCase("Post")) {
								requestInfoDao.editRequestforReportWebserviceInfo(configRequest.getRequestId(),
										Double.toString(configRequest.getRequest_version()), "health_check", "2",
										"Failure");
								requestInfoDao.editRequestForReportIOSWebserviceInfo(configRequest.getRequestId(),
										Double.toString(configRequest.getRequest_version()),
										"Device Reachability Failure", "Failure", "Could not connect to the router.");
							} else if (type.equalsIgnoreCase("Pre")) {
								requestInfoDao.editRequestforReportWebserviceInfo(configRequest.getRequestId(),
										Double.toString(configRequest.getRequest_version()), "pre_health_checkup", "2",
										"Failure");

							} else {

							}
							HealthCheckReport healthCheckReportfailure = new HealthCheckReport();
							resultList = null;
							healthCheckReport.createReport(resultList, configRequest.getHostname(),
									configRequest.getRegion(), type);
						}

					}
				} else {
					value = false;
					// released device lock
					requestInfoDao.releaselockDeviceForRequest(configRequest.getManagementIp(),
							configRequest.getRequestId());
					jsonArray = new Gson().toJson(value);
					obj.put(new String("output"), jsonArray);
					if (type.equalsIgnoreCase("Post")) {
						requestInfoDao.editRequestforReportWebserviceInfo(configRequest.getRequestId(),
								Double.toString(configRequest.getRequest_version()), "health_check", "2", "Failure");
						requestInfoDao.editRequestForReportIOSWebserviceInfo(configRequest.getRequestId(),
								Double.toString(configRequest.getRequest_version()), "Device Reachability Failure",
								"Failure", "Could not connect to the router.");
					} else if (type.equalsIgnoreCase("Pre")) {
						requestInfoDao.editRequestforReportWebserviceInfo(configRequest.getRequestId(),
								Double.toString(configRequest.getRequest_version()), "pre_health_checkup", "2",
								"Failure");
					} else {

					}

					String response = "";
					String responseDownloadPath = "";
					try {
						response = invokeFtl.generateHealthCheckTestResultFailure(configRequest);
						requestInfoDao.updateHealthCheckTestStatus(configRequest.getRequestId(),
								Double.toString(configRequest.getRequest_version()), 0, 0, 0);
						requestInfoDao.updateRouterFailureHealthCheck(configRequest.getRequestId(),
								Double.toString(configRequest.getRequest_version()));
						responseDownloadPath = PostUpgradeHealthCheck.TSA_PROPERTIES
								.getProperty("responseDownloadPath");
						TextReport.writeFile(
								responseDownloadPath, configRequest.getRequestId() + "V"
										+ Double.toString(configRequest.getRequest_version()) + "_HealthCheck.txt",
								response);
						requestInfoDao.releaselockDeviceForRequest(configRequest.getManagementIp(),
								configRequest.getRequestId());
					} catch (Exception e) {
						// TODO Auto-generated catch block

					}
				}
			} else if (requestinfo.getManagementIp() != null && !requestinfo.getManagementIp().equals("")) {

				requestinfo.setAlphanumericReqId(RequestId);
				requestinfo.setRequestVersion(Double.parseDouble(json.get("version").toString()));

				String host = requestinfo.getManagementIp();
				UserPojo userPojo = new UserPojo();
				userPojo = requestInfoDao.getRouterCredentials();

				String user = userPojo.getUsername();
				String password = userPojo.getPassword();
				String port = PostUpgradeHealthCheck.TSA_PROPERTIES.getProperty("portSSH");
				PingTest pingClass = new PingTest();
				if (type.equalsIgnoreCase("Pre")) {
					testToFail = "pre_health_checkup";
				} else {
					testToFail = "health_check";
				}
				boolean reachability = pingClass.cmdPingCall(host, requestinfo.getHostname(), requestinfo.getRegion());
				if (reachability) {
					rechabilityTst = 1;
				} else {
					rechabilityTst = 0;

				}

				if (rechabilityTst == 1) {
					String OsversionOnDevice = null;
					ShowVersionTest versionTest = new ShowVersionTest();
					OsversionOnDevice = versionTest.versionInfo(host, user, password, requestinfo.getHostname(),
							requestinfo.getRegion(), type);

					String memoryResult = null, powerResult = null, cpu_usage_result = null;

					if (OsversionOnDevice.equalsIgnoreCase("JSchException")) {
						value = false;
						requestDao.editRequestforReportWebserviceInfo(requestinfo.getAlphanumericReqId(),
								Double.toString(requestinfo.getRequestVersion()), testToFail, "2", "Failure");
						requestInfoDao.editRequestForReportIOSWebserviceInfo(configRequest.getRequestId(),
								Double.toString(configRequest.getRequest_version()), "Device Reachability Failure",
								"Failure", "Could not connect to the router.");

						try {
							List<HealthCheckComponent> resultList = new ArrayList<HealthCheckComponent>();
							HealthCheckReport healthCheckReport = new HealthCheckReport();
							healthCheckReport.createFailureReport(resultList, requestinfo.getHostname(),
									requestinfo.getRegion(), type);
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					} else {

						ShowMemoryTest memoryTest = new ShowMemoryTest();
						ShowPowerTest powerTest = new ShowPowerTest();
						ShowCPUUsage cpu_usage = new ShowCPUUsage();
						memoryResult = memoryTest.memoryInfo(host, user, password, requestinfo.getHostname(),
								requestinfo.getRegion(), type);
						powerResult = powerTest.powerInfo(host, user, password, requestinfo.getHostname(),
								requestinfo.getRegion(), type);
						cpu_usage_result = cpu_usage.cpuUsageInfo(host, user, password, requestinfo.getHostname(),
								requestinfo.getRegion(), type);
						// Create Health check report
						List<HealthCheckComponent> resultList = new ArrayList<HealthCheckComponent>();
						HealthCheckComponent memoryComp = new HealthCheckComponent();
						memoryComp.setTestname("Memory Check");
						memoryComp.setTestresult(memoryResult);

						resultList.add(memoryComp);
						resultList.add(memoryComp);

						HealthCheckComponent powerComp = new HealthCheckComponent();
						powerComp.setTestname("Power Check");
						powerComp.setTestresult(powerResult);

						resultList.add(powerComp);

						HealthCheckComponent cpu_usage_comp = new HealthCheckComponent();
						cpu_usage_comp.setTestname("CPU Check");
						cpu_usage_comp.setTestresult(cpu_usage_result);

						resultList.add(cpu_usage_comp);

						HealthCheckReport healthCheckReport = new HealthCheckReport();
						healthCheckReport.createReport(resultList, requestinfo.getHostname(), requestinfo.getRegion(),
								type);

						if (memoryResult.equalsIgnoreCase("pass") && powerResult.equalsIgnoreCase("pass")
								&& cpu_usage_result.equalsIgnoreCase("pass")) {
							value = true;
							jsonArray = new Gson().toJson(value);
							obj.put(new String("output"), jsonArray);
							if (type.equalsIgnoreCase("Post")) {
								requestDao.editRequestforReportWebserviceInfo(requestinfo.getAlphanumericReqId(),
										Double.toString(requestinfo.getRequestVersion()), "health_check", "1",
										"In Progress");

							} else if (type.equalsIgnoreCase("Pre")) {
								requestDao.editRequestforReportWebserviceInfo(requestinfo.getAlphanumericReqId(),
										Double.toString(requestinfo.getRequestVersion()), "pre_health_checkup", "1",
										"In Progress");

							}
							requestInfoDao.releaselockDeviceForRequest(requestinfo.getManagementIp(),
									requestinfo.getAlphanumericReqId());

						} else {
							value = false;
							jsonArray = new Gson().toJson(value);
							// released device lock
							requestInfoDao.releaselockDeviceForRequest(configRequest.getManagementIp(),
									configRequest.getRequestId());
							obj.put(new String("output"), jsonArray);
							if (type.equalsIgnoreCase("Post")) {
								requestDao.editRequestforReportWebserviceInfo(requestinfo.getAlphanumericReqId(),
										Double.toString(requestinfo.getRequestVersion()), "health_check", "2",
										"Failure");
								requestInfoDao.editRequestForReportIOSWebserviceInfo(requestinfo.getAlphanumericReqId(),
										Double.toString(requestinfo.getRequestVersion()), "Device Reachability Failure",
										"Failure", "Could not connect to the router.");
							} else if (type.equalsIgnoreCase("Pre")) {
								requestDao.editRequestforReportWebserviceInfo(requestinfo.getAlphanumericReqId(),
										Double.toString(requestinfo.getRequestVersion()), "pre_health_checkup", "2",
										"Failure");

							} else {

							}
							HealthCheckReport healthCheckReportfailure = new HealthCheckReport();
							resultList = null;
							healthCheckReport.createReport(resultList, requestinfo.getHostname(),
									requestinfo.getRegion(), type);
						}

					}
				} else {
					value = false;
					// released device lock
					requestInfoDao.releaselockDeviceForRequest(requestinfo.getManagementIp(),
							requestinfo.getAlphanumericReqId());
					jsonArray = new Gson().toJson(value);
					obj.put(new String("output"), jsonArray);
					if (type.equalsIgnoreCase("Post")) {
						requestDao.editRequestforReportWebserviceInfo(requestinfo.getAlphanumericReqId(),
								Double.toString(requestinfo.getRequestVersion()), "health_check", "2", "Failure");
						requestInfoDao.editRequestForReportIOSWebserviceInfo(requestinfo.getAlphanumericReqId(),
								Double.toString(requestinfo.getRequestVersion()), "Device Reachability Failure",
								"Failure", "Could not connect to the router.");
					} else if (type.equalsIgnoreCase("Pre")) {
						requestDao.editRequestforReportWebserviceInfo(requestinfo.getAlphanumericReqId(),
								Double.toString(requestinfo.getRequestVersion()), "pre_health_checkup", "2", "Failure");
					} else {

					}

					String response = "";
					String responseDownloadPath = "";
					try {
						response = invokeFtl.generateHealthCheckTestResultFailure(requestinfo);
						requestInfoDao.updateHealthCheckTestStatus(requestinfo.getAlphanumericReqId(),
								Double.toString(requestinfo.getRequestVersion()), 0, 0, 0);
						requestInfoDao.updateRouterFailureHealthCheck(requestinfo.getAlphanumericReqId(),
								Double.toString(requestinfo.getRequestVersion()));
						responseDownloadPath = PostUpgradeHealthCheck.TSA_PROPERTIES
								.getProperty("responseDownloadPath");
						TextReport.writeFile(responseDownloadPath,
								requestinfo.getAlphanumericReqId() + "V"
										+ Double.toString(requestinfo.getRequestVersion()) + "_HealthCheck.txt",
								response);
						requestInfoDao.releaselockDeviceForRequest(requestinfo.getManagementIp(),
								requestinfo.getAlphanumericReqId());
					} catch (Exception e) {
						// TODO Auto-generated catch block

					}
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
		} catch (Exception e1) {
			if (configRequest.getManagementIp() != null && !configRequest.getManagementIp().equals("")) {
				e1.printStackTrace();
				if (e1.getMessage().contains("invalid server's version string")
						|| e1.getMessage().contains("Auth fail")) {
					value = false;
					requestInfoDao.editRequestforReportWebserviceInfo(configRequest.getRequestId(),
							Double.toString(configRequest.getRequest_version()), testToFail, "2", "Failure");
					requestInfoDao.editRequestForReportIOSWebserviceInfo(configRequest.getRequestId(),
							Double.toString(configRequest.getRequest_version()), "Device Reachability Failure",
							"Failure", "Could not connect to the router.");

					String response = "";
					String responseDownloadPath = "";

					try {
						response = invokeFtl.generateCustomerIOSHealthCheckFailedPost(configRequest);

						responseDownloadPath = PostUpgradeHealthCheck.TSA_PROPERTIES
								.getProperty("responseDownloadPathHealthCheckFolder");
						TextReport.writeFile(responseDownloadPath, type + "_" + configRequest.getHostname() + "_"
								+ configRequest.getRegion() + "_HealthCheckReport.txt", response);
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			} else if (requestinfo.getManagementIp() != null && !requestinfo.getManagementIp().equals("")) {

				e1.printStackTrace();
				if (e1.getMessage().contains("invalid server's version string")
						|| e1.getMessage().contains("Auth fail")) {
					value = false;
					requestDao.editRequestforReportWebserviceInfo(requestinfo.getAlphanumericReqId(),
							Double.toString(requestinfo.getRequestVersion()), testToFail, "2", "Failure");
					requestInfoDao.editRequestForReportIOSWebserviceInfo(requestinfo.getAlphanumericReqId(),
							Double.toString(requestinfo.getRequestVersion()), "Device Reachability Failure", "Failure",
							"Could not connect to the router.");

					String response = "";
					String responseDownloadPath = "";

					try {
						response = invokeFtl.generateCustomerIOSHealthCheckFailedPost(requestinfo);

						responseDownloadPath = PostUpgradeHealthCheck.TSA_PROPERTIES
								.getProperty("responseDownloadPathHealthCheckFolder");
						TextReport.writeFile(responseDownloadPath, type + "_" + requestinfo.getHostname() + "_"
								+ requestinfo.getRegion() + "_HealthCheckReport.txt", response);
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}

			}
		}
		jsonArray = new Gson().toJson(value);
		obj.put(new String("output"), jsonArray);
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
		String responseDownloadPath = PostUpgradeHealthCheck.TSA_PROPERTIES.getProperty("responseDownloadPath");
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
				int fileReadSize = Integer.parseInt(PostUpgradeHealthCheck.TSA_PROPERTIES.getProperty("fileChunkSize"));
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
		String responselogpath = PostUpgradeHealthCheck.TSA_PROPERTIES.getProperty("responselogpath");
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
			logger.info("exit-status: " + channel.getExitStatus());

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
		String responseDownloadPath = PostUpgradeHealthCheck.TSA_PROPERTIES.getProperty("responseDownloadPath");
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
			int fileReadSize = Integer.parseInt(PostUpgradeHealthCheck.TSA_PROPERTIES.getProperty("fileChunkSize"));
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
			String filepath = PostUpgradeHealthCheck.TSA_PROPERTIES.getProperty("analyserPath");
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

	private static void cmdPingCall(String requestId, String version) throws Exception {
		ProcessBuilder builder = new ProcessBuilder("cmd.exe");
		Process p = null;
		try {
			p = builder.start();
			BufferedWriter p_stdin = new BufferedWriter(new OutputStreamWriter(p.getOutputStream()));

			p_stdin.write("ping 30.0.0.2 -n 20");
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
			logger.info(s.nextLine());
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
			/* logger.info(new String(tmp, 0, i)); */
			String s = new String(tmp, 0, i);
			if (!(s.equals(""))) {
				logger.info(s);
				String filepath = PostUpgradeHealthCheck.TSA_PROPERTIES.getProperty("responseDownloadPath") + "//"
						+ requestID + "V" + version + "_HealthCheck.txt";
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
		 * if (channel.isClosed()) { logger.info("exit-status: " +
		 * channel.getExitStatus());
		 * 
		 * }
		 */

	}

	private static String readFile() throws IOException {
		String responseDownloadPath = PostUpgradeHealthCheck.TSA_PROPERTIES.getProperty("responseDownloadPath");

		BufferedReader br = new BufferedReader(
				new FileReader(responseDownloadPath + "//" + "HealthcheckTestCommand.txt"));

		// BufferedReader br = new BufferedReader(new FileReader("D:/C3P/New
		// folder/HealthcheckTestCommand.txt"));
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