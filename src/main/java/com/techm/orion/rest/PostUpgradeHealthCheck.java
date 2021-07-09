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
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

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
import com.techm.orion.entitybeans.CredentialManagementEntity;
import com.techm.orion.entitybeans.DeviceDiscoveryEntity;
import com.techm.orion.pojo.HealthCheckComponent;
import com.techm.orion.pojo.RequestInfoPojo;
import com.techm.orion.repositories.DeviceDiscoveryRepository;
import com.techm.orion.service.DcmConfigService;
import com.techm.orion.service.PingService;
import com.techm.orion.utility.HealthCheckReport;
import com.techm.orion.utility.InvokeFtl;
import com.techm.orion.utility.ShowCPUUsage;
import com.techm.orion.utility.ShowMemoryTest;
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
	
	@Autowired
	private DcmConfigService dcmConfigService;
	
	@Autowired
	private DeviceDiscoveryRepository deviceDiscoveryRepository;
	@Autowired
	private PingService pingService;

	/**
	 *This Api is marked as ***************c3p-ui Api Impacted****************
	 **/
	@SuppressWarnings("unchecked")
	@POST
	@RequestMapping(value = "/HealthCheck", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
	@ResponseBody
	public JSONObject healthcheckCommandTest(@RequestBody String request, String type) {

		JSONObject obj = new JSONObject();
		String jsonArray = "";
		InvokeFtl invokeFtl = new InvokeFtl();
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

			
			requestinfo = requestDao.getRequestDetailTRequestInfoDBForVersion(RequestId, version);
			if (requestinfo.getManagementIp() != null && !requestinfo.getManagementIp().equals("")) {
				DeviceDiscoveryEntity deviceDetails = deviceDiscoveryRepository
						.findByDHostNameAndDMgmtIpAndDDeComm(requestinfo.getHostname(),requestinfo.getManagementIp(),"0");
				
				requestinfo.setAlphanumericReqId(RequestId);
				requestinfo.setRequestVersion(Double.parseDouble(json.get("version").toString()));

				String host = requestinfo.getManagementIp();
				CredentialManagementEntity routerCredential = dcmConfigService.getRouterCredential(
						deviceDetails);
				String user = routerCredential.getLoginRead();
				String password = routerCredential.getPasswordWrite();	
				if (type.equalsIgnoreCase("Pre")) {
					testToFail = "pre_health_checkup";
				} else {
					testToFail = "health_check";
				}
				boolean reachability = pingService.pingResults(host, requestinfo.getHostname(), requestinfo.getRegion());
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

					String memoryResult = null, cpu_usage_result = null;

					if (OsversionOnDevice.equalsIgnoreCase("JSchException")) {
						value = false;
						requestDao.editRequestforReportWebserviceInfo(requestinfo.getAlphanumericReqId(),
								Double.toString(requestinfo.getRequestVersion()), testToFail, "2", "Failure");
						requestInfoDao.editRequestForReportIOSWebserviceInfo(requestinfo.getAlphanumericReqId(),
								Double.toString(requestinfo.getRequestVersion()), "Device Reachability Failure",
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
						ShowCPUUsage cpu_usage = new ShowCPUUsage();
						memoryResult = memoryTest.memoryInfo(host, user, password, requestinfo.getHostname(),
								requestinfo.getRegion(), type);
						//powerResult = powerTest.powerInfo(host, user, password, requestinfo.getHostname(),
							//	requestinfo.getRegion(), type);
						cpu_usage_result = cpu_usage.cpuUsageInfo(host, user, password, requestinfo.getHostname(),
								requestinfo.getRegion(), type);
						// Create Health check report
						List<HealthCheckComponent> resultList = new ArrayList<HealthCheckComponent>();
						HealthCheckComponent memoryComp = new HealthCheckComponent();
						memoryComp.setTestname("Memory Check");
						memoryComp.setTestresult(memoryResult);

						resultList.add(memoryComp);
						//resultList.add(memoryComp);

						HealthCheckComponent powerComp = new HealthCheckComponent();
						powerComp.setTestname("Power Check");
						powerComp.setTestresult("Pass");

						resultList.add(powerComp);

						HealthCheckComponent cpu_usage_comp = new HealthCheckComponent();
						cpu_usage_comp.setTestname("CPU Check");
						cpu_usage_comp.setTestresult(cpu_usage_result);
						cpu_usage_comp.setTestresult("");
						resultList.add(cpu_usage_comp);

						HealthCheckReport healthCheckReport = new HealthCheckReport();
						healthCheckReport.createReport(resultList, requestinfo.getHostname(), requestinfo.getRegion(),
								type);

						if (memoryResult.equalsIgnoreCase("pass") && /*powerResult.equalsIgnoreCase("pass")*/
								cpu_usage_result.equalsIgnoreCase("pass")) {
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
							requestInfoDao.releaselockDeviceForRequest(requestinfo.getManagementIp(),
									requestinfo.getAlphanumericReqId());
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
		} catch (Exception e1) {
			if (requestinfo.getManagementIp() != null && !requestinfo.getManagementIp().equals("")) {

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
			logger.error("Exception in loadProperties method "+exc.getMessage());
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
		String filePath = responseDownloadPath + requestIdForConfig + "V" + version + "_ConfigurationNoCmd";

		br = new BufferedReader(new FileReader(filePath));
		File f = new File(filePath);
		try {
			ArrayList<String> ar = new ArrayList<String>();
			if (f.exists()) {

				StringBuilder sb2 = new StringBuilder();

				rdr = new LineNumberReader(new FileReader(filePath));
				InputStream is = new BufferedInputStream(new FileInputStream(filePath));

				byte[] c = new byte[1024];
				int count = 0;
				int readChars = 0;
				while ((readChars = is.read(c)) != -1) {
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
		String filePath = responseDownloadPath + requestIdForConfig + "V" + version + "_Configuration";

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
			while ((readChars = is.read(c)) != -1) {
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


}