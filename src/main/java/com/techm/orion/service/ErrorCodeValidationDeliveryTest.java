package com.techm.orion.service;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.LineNumberReader;
import java.io.OutputStream;
import java.io.PrintStream;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;
import com.techm.orion.dao.RequestInfoDao;
import com.techm.orion.pojo.ChildVersionPojo;
import com.techm.orion.pojo.CreateConfigRequest;
import com.techm.orion.pojo.ErrorValidationPojo;
import com.techm.orion.pojo.ModifyConfigResultPojo;
import com.techm.orion.pojo.ParentVersionPojo;
import com.techm.orion.pojo.UserPojo;
import com.techm.orion.rest.DeliverConfigurationAndBackupTest;
import com.techm.orion.utility.InvokeFtl;
import com.techm.orion.utility.TSALabels;
import com.techm.orion.utility.TextReport;

public class ErrorCodeValidationDeliveryTest extends Thread {
	private static final Logger logger = LogManager.getLogger(ErrorCodeValidationDeliveryTest.class);

	public String checkErrorCode(String requestId, double version) throws IOException {
		RequestInfoDao requestInfoDao = new RequestInfoDao();
		List<ErrorValidationPojo> list = new ArrayList<ErrorValidationPojo>();
		String textFound = "";
		String errorType = null;
		String errorDescription = null;
		try {
			list = requestInfoDao.getAllErrorCodeFromRouter();
			for (Iterator iterator = list.iterator(); iterator.hasNext();) {

				ErrorValidationPojo errorValidationPojo = (ErrorValidationPojo) iterator.next();
				String errorMsg = errorValidationPojo.getRouter_error_message();
				errorType = errorValidationPojo.getError_type();
				errorDescription = errorValidationPojo.getError_description();
				String commandError = parseFile(
						TSALabels.RESPONSE_LOG_PATH.getValue() + "/" + requestId + "_" + Double.toString(version) + "theSSHfile.txt",
						errorMsg);
				if (commandError != "") {
					textFound = commandError;
					break;
				}
			}
			if (textFound != "") {
				// we need to save for that particular request error details
				requestInfoDao.updateErrorDetailsDeliveryTestForRequestId(requestId, Double.toString(version),
						textFound, errorType, errorDescription);

			} else {
				errorType = "No Error";
				requestInfoDao.updateErrorDetailsDeliveryTestForRequestId(requestId, Double.toString(version),
						"No Error", "No Error", "NA");
			}

			logger.info(textFound);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return errorType;
	}

	public String parseFile(String fileName, String searchStr) throws FileNotFoundException {
		BufferedReader buf = new BufferedReader(new FileReader(fileName));
		String line = "";
		int lineNumber = 0;
		int newLineNumber = 0;
		String line1 = "";
		int count = 0;
		String finalString = "";
		try {
			while ((line = buf.readLine()) != null) {
				lineNumber++;
				if (line.contains(searchStr)) {
					if (line.contains("^")) {
						newLineNumber = lineNumber - 2;
					} else {
						newLineNumber = lineNumber - 1;
					}
					BufferedReader br = new BufferedReader(new FileReader(fileName));
					try {
						while ((line1 = br.readLine()) != null) {
							count++;
							if (count == newLineNumber) {
								break;
							}
						}

					} catch (IOException e) {
						e.printStackTrace();
					}

					if (line1 != "" && line != "") {
						finalString = line1.concat("<br>").concat(line);
					}

					break;
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return finalString;
	}

	/*
	 * to push no command and get the previous configuration of the version
	 */
	public void pushPreviousVersionConfiguration(CreateConfigRequest configRequest) throws IOException {
		DeliverConfigurationAndBackupTest deliverConfigurationAndBackupTest = new DeliverConfigurationAndBackupTest();
		Double previousVersion = 0d;
		try {

			RequestInfoDao requestInfoDao = new RequestInfoDao();
			String host = configRequest.getManagementIp();
			UserPojo userPojo = new UserPojo();
			userPojo = requestInfoDao.getRouterCredentials();

			String user = userPojo.getUsername();
			String password = userPojo.getPassword();
			ArrayList<String> commandToPush = new ArrayList<String>();

			JSch jsch = new JSch();
			Channel channel = null;
			Session session = jsch.getSession(user, host, Integer.parseInt(TSALabels.PORT_SSH.getValue()));
			Properties config = new Properties();
			config.put("StrictHostKeyChecking", "no");
			session.setConfig(config);
			session.setPassword(password);
			session.connect();
			try {

				channel = session.openChannel("shell");
				OutputStream ops = channel.getOutputStream();

				PrintStream ps = new PrintStream(ops, true);
				logger.info("Channel Connected to machine " + host + " server");
				channel.connect();
				InputStream input = channel.getInputStream();
				DecimalFormat numberFormat = new DecimalFormat("#.0");
				if (configRequest.getRequest_version() != 1.0) {
					previousVersion = Double.parseDouble(numberFormat.format(configRequest.getRequest_version() - 0.1));

					commandToPush = deliverConfigurationAndBackupTest.readFile(configRequest.getRequestId(),
							Double.toString(previousVersion));
					ps.println("config t");
					for (String arr : commandToPush) {

						ps.println(arr);

						printResult(input, channel);

					}
					ps.println("exit");
				} else {
					// get the previous router configuration

					commandToPush = readFile(configRequest.getRequestId(),
							Double.toString(configRequest.getRequest_version()));

					ps.println("config t");
					for (String arr : commandToPush) {

						ps.println(arr);

						printResult(input, channel);

					}
					ps.println("exit");
				}
				// printResult(input, channel);

			} catch (Exception ex) {

			}
		} catch (Exception ex) {

		}
	}

	public String pushNoCommandConfiguration(CreateConfigRequest configRequest) throws IOException {

		String result = "Success";
		try {

			String key = "";
			int counter = 1;
			InvokeFtl invokeFtl = new InvokeFtl();
			RequestInfoDao requestInfoDao = new RequestInfoDao();
			ParentVersionPojo compareVersion = new ParentVersionPojo();
			ChildVersionPojo latestVersion = new ChildVersionPojo();

			compareVersion.setEnablePassword(configRequest.getEnablePassword());
			compareVersion.setVrfName(configRequest.getVrfName());
			compareVersion.setNeighbor1_remoteAS(configRequest.getInternetLcVrf().getneighbor1_remoteAS());
			compareVersion.setRoutingProtocol(configRequest.getInternetLcVrf().getroutingProtocol());
			compareVersion.setNetworkIp(configRequest.getInternetLcVrf().getNetworkIp());
			compareVersion.setNetworkIp_subnetMask(configRequest.getInternetLcVrf().getnetworkIp_subnetMask());
			compareVersion.setNeighbor1(configRequest.getInternetLcVrf().getNeighbor1());
			compareVersion.setName(configRequest.getC3p_interface().getName());
			compareVersion.setDescription(configRequest.getC3p_interface().getDescription());
			compareVersion.setBandwidth(configRequest.getC3p_interface().getBandwidth());
			compareVersion.setEncapsulation(configRequest.getC3p_interface().getEncapsulation());
			compareVersion.setBanner(configRequest.getBanner());
			compareVersion.setIp(configRequest.getC3p_interface().getIp());
			compareVersion.setMask(configRequest.getC3p_interface().getMask());
			compareVersion.setSnmpHostAddress(configRequest.getSnmpHostAddress());
			compareVersion.setSnmpString(configRequest.getSnmpString());
			compareVersion.setLoopBackType(configRequest.getLoopBackType());
			compareVersion.setLoopbackIPaddress(configRequest.getLoopbackIPaddress());
			compareVersion.setLoopbackSubnetMask(configRequest.getLoopbackSubnetMask());
			compareVersion.setBgpASNumber(configRequest.getInternetLcVrf().getAS());
			compareVersion.setLanInterface(configRequest.getLanInterface());
			compareVersion.setLanDescription(configRequest.getLanDescription());
			compareVersion.setLanIp(configRequest.getLanIp());
			compareVersion.setLanMaskAddress(configRequest.getLanMaskAddress());

			if (compareVersion.getVrfName() != null && compareVersion.getNeighbor1_remoteAS() != null) {

				key = "vrf";
				// get the data
				getConfigDataforNoCmd(configRequest, key, counter++);

			}

			if (compareVersion.getEnablePassword() != null) {

				key = "password";
				getConfigDataforNoCmd(configRequest, key, counter++);

			}

			if (compareVersion.getRoutingProtocol() != null && !compareVersion.getRoutingProtocol().isEmpty()) {

				key = "bgp";
				getConfigDataforNoCmd(configRequest, key, counter++);

			}
			if (compareVersion.getName() != null && !compareVersion.getName().isEmpty()) {

				key = "wanInterface";
				getConfigDataforNoCmd(configRequest, key, counter++);

			}
			if (compareVersion.getBanner() != null && !compareVersion.getBanner().isEmpty()) {

				key = "banner";
				// get the data
				getConfigDataforNoCmd(configRequest, key, counter++);

			}
			if (compareVersion.getLoopbackIPaddress() != null && !compareVersion.getLoopbackIPaddress().isEmpty()) {

				key = "loopback";
				// get the data
				getConfigDataforNoCmd(configRequest, key, counter++);

			}
			if (compareVersion.getSnmpHostAddress() != null && !compareVersion.getSnmpHostAddress().isEmpty()) {

				key = "snmp";
				// get the data
				getConfigDataforNoCmd(configRequest, key, counter++);

			}
			if (compareVersion.getSnmpHostAddress() != null && !compareVersion.getSnmpHostAddress().isEmpty()) {

				key = "accesslist";
				// get the data
				getConfigDataforNoCmd(configRequest, key, counter++);

			}
			if (compareVersion.getLanIp() != null && !compareVersion.getLanIp().isEmpty()) {

				key = "lanInterface";
				getConfigDataforNoCmd(configRequest, key, counter++);

			}
			if (compareVersion.getRoutingProtocol() != null && !compareVersion.getRoutingProtocol().isEmpty()) {

				key = "routeMap";
				getConfigDataforNoCmd(configRequest, key, counter++);

			}
			if (compareVersion.getSnmpHostAddress() != null && !compareVersion.getSnmpHostAddress().isEmpty()) {

				key = "accesslist";
				// get the data
				getConfigDataforNoCmd(configRequest, key, counter++);

			}
			String responseforNoCmd = invokeFtl.generateModifyConfigurationToPushNoCmd(latestVersion, compareVersion);

			TextReport.writeFile(TSALabels.RESPONSE_DOWNLOAD_PATH.getValue(), configRequest.getRequestId() + "V"
					+ configRequest.getRequest_version() + "_ConfigurationNoCmdForError", responseforNoCmd);

			String host = configRequest.getManagementIp();
			UserPojo userPojo = new UserPojo();
			userPojo = requestInfoDao.getRouterCredentials();

			String user = userPojo.getUsername();
			String password = userPojo.getPassword();
			ArrayList<String> commandToPush = new ArrayList<String>();

			JSch jsch = new JSch();
			Channel channel = null;
			Session session = jsch.getSession(user, host, Integer.parseInt(TSALabels.PORT_SSH.getValue()));
			Properties config = new Properties();
			config.put("StrictHostKeyChecking", "no");
			session.setConfig(config);
			session.setPassword(password);
			session.connect();
			try {

				channel = session.openChannel("shell");
				OutputStream ops = channel.getOutputStream();

				PrintStream ps = new PrintStream(ops, true);
				logger.info("Channel Connected to machine " + host + " server");
				channel.connect();
				logger.info("will read the file now");
				InputStream input = channel.getInputStream();
				commandToPush = readFileForNoCommand(configRequest.getRequestId(),
						Double.toString(configRequest.getRequest_version()));
				ps.println("config t");
				for (String arr : commandToPush) {

					ps.println(arr);

					printResult(input, channel);

				}
				printResult(input, channel);

			} catch (Exception ex) {
				result = "Failure";
			}
		} catch (Exception ex) {
			result = "Failure";
		}

		return result;

	}


	@SuppressWarnings("resource")
	public ArrayList<String> readFile(String requestIdForConfig, String version) throws IOException {
		BufferedReader br = null;
		LineNumberReader rdr = null;
		String filePath = TSALabels.RESPONSE_DOWNLOAD_PATH.getValue() + requestIdForConfig + "V" + version + "_PreviousConfig.txt";

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
			int fileReadSize = Integer.parseInt(TSALabels.FILE_CHUNK_SIZE.getValue());
			int chunks = (count / fileReadSize) + 1;
			String line;

			for (int loop = 1; loop <= chunks; loop++) {
				if (loop == 1) {
					rdr = new LineNumberReader(new FileReader(filePath));
					line = rdr.readLine();
					sb2.append(line).append("\n");
					for (line = null; (line = rdr.readLine()) != null;) {

						if (rdr.getLineNumber() <= fileReadSize) {
							if (!line.contains("#") && !line.contains("configuration")) {
								sb2.append(line).append("\n");
							}
						}

					}
					ar.add(sb2.toString());
				} else {
					LineNumberReader rdr1 = new LineNumberReader(new FileReader(filePath));
					sb2 = new StringBuilder();
					for (line = null; (line = rdr1.readLine()) != null;) {

						if (rdr1.getLineNumber() > (fileReadSize * (loop - 1))
								&& rdr1.getLineNumber() <= (fileReadSize * loop)) {
							if (!line.contains("#") && !line.contains("configuration")) {
								sb2.append(line).append("\n");
							}
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

	public ArrayList<String> readFileForNoCommand(String requestIdForConfig, String version) throws IOException {
		BufferedReader br = null;
		LineNumberReader rdr = null;
		String filePath = TSALabels.RESPONSE_DOWNLOAD_PATH.getValue() + requestIdForConfig + "V" + version
				+ "_ConfigurationNoCmdForError";

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
			int fileReadSize = Integer.parseInt(TSALabels.FILE_CHUNK_SIZE.getValue());
			int chunks = (count / fileReadSize) + 1;
			String line;

			for (int loop = 1; loop <= chunks; loop++) {
				if (loop == 1) {
					rdr = new LineNumberReader(new FileReader(filePath));
					line = rdr.readLine();
					sb2.append(line).append("\n");
					for (line = null; (line = rdr.readLine()) != null;) {

						if (rdr.getLineNumber() <= fileReadSize) {
							if (!line.contains("#") && !line.contains("configuration")) {
								sb2.append(line).append("\n");
							}
						}

					}
					ar.add(sb2.toString());
				} else {
					LineNumberReader rdr1 = new LineNumberReader(new FileReader(filePath));
					sb2 = new StringBuilder();
					for (line = null; (line = rdr1.readLine()) != null;) {

						if (rdr1.getLineNumber() > (fileReadSize * (loop - 1))
								&& rdr1.getLineNumber() <= (fileReadSize * loop)) {
							if (!line.contains("#") && !line.contains("configuration")) {
								sb2.append(line).append("\n");
							}
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

	public void printResult(InputStream input, Channel channel) throws Exception {
		BufferedWriter bw = null;
		FileWriter fw = null;
		int SIZE = 1024;
		byte[] tmp = new byte[SIZE];

		File file = new File(TSALabels.RESPONSE_LOG_PATH.getValue() + "/" + "theSSHfile.txt");
		/*
		 * if (file.exists()) { file.delete(); }
		 */
		while (input.available() > 0) {
			int i = input.read(tmp, 0, SIZE);
			if (i < 0)
				break;

			String s = new String(tmp, 0, i);
			if (!(s.equals(""))) {

				file = new File(TSALabels.RESPONSE_LOG_PATH.getValue() + "/" + "theSSHfile.txt");

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
		try {
			Thread.sleep(1000);
		} catch (Exception ee) {
		}

	}

	public void getConfigDataforNoCmd(CreateConfigRequest configRequest, String key, int counter) throws IOException {

		String no_cmd = "";

		CreateAndCompareModifyVersion createAndCompareModifyVersion = new CreateAndCompareModifyVersion();
		RequestInfoDao requestInfoDao = new RequestInfoDao();
		List<ModifyConfigResultPojo> configCmdresultList = new ArrayList<ModifyConfigResultPojo>();
		configCmdresultList = requestInfoDao.getConfigCmdRecordFordataForDelivery(configRequest, key);
		for (Iterator<ModifyConfigResultPojo> iterator = configCmdresultList.iterator(); iterator.hasNext();) {
			ModifyConfigResultPojo modifyConfigResultPojo = (ModifyConfigResultPojo) iterator.next();
			no_cmd = no_cmd.concat(modifyConfigResultPojo.getNo_SSH_Command());

			String content = ";" + no_cmd + ";";
			// String content=no_cmd;
			String ar[] = content.split(";");
			createAndCompareModifyVersion.createNoconfigFile(ar, counter);

		}

	}

}