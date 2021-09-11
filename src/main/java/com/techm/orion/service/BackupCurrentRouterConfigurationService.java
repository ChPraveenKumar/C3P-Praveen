package com.techm.orion.service;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.Arrays;
import java.util.Properties;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;
import com.techm.orion.dao.RequestInfoDao;
import com.techm.orion.dao.RequestInfoDetailsDao;
import com.techm.orion.entitybeans.CredentialManagementEntity;
import com.techm.orion.entitybeans.DeviceDiscoveryEntity;
import com.techm.orion.pojo.CreateConfigRequest;
import com.techm.orion.pojo.RequestInfoPojo;
import com.techm.orion.repositories.DeviceDiscoveryRepository;
import com.techm.orion.utility.InvokeFtl;
import com.techm.orion.utility.TSALabels;
import com.techm.orion.utility.TextReport;
import com.techm.orion.utility.UtilityMethods;

@Service
public class BackupCurrentRouterConfigurationService extends Thread {
	private static final Logger logger = LogManager.getLogger(BackupCurrentRouterConfigurationService.class);

	@Autowired
	private DeviceDiscoveryRepository deviceDiscoveryRepository;
	
	@Autowired
	private DcmConfigService dcmConfigService;
	
	@Autowired
	private RequestInfoDao requestInfoDao ;
	
	@Autowired
	private RequestInfoDetailsDao requestInfoDetailsDao;
	private static final String JSCH_CONFIG_INPUT_BUFFER= "max_input_buffer_size";
		
	public boolean getRouterConfig(CreateConfigRequest configRequest, String routerVersionType) throws IOException {
		
		InvokeFtl invokeFtl = new InvokeFtl();
		CreateConfigRequest createConfigRequest = new CreateConfigRequest();
		boolean backupdone = false;
		JSch jsch = new JSch();
		Channel channel = null;
		Session session = null;
		try {
			String host = configRequest.getManagementIp();
			DeviceDiscoveryEntity deviceDetails = deviceDiscoveryRepository
					.findByDHostNameAndDMgmtIpAndDDeComm(configRequest.getHostname(),configRequest.getManagementIp(),"0");			
			CredentialManagementEntity routerCredential = dcmConfigService.getRouterCredential(
					deviceDetails);
			String user = routerCredential.getLoginRead();
			String password = routerCredential.getPasswordWrite();

			session = jsch.getSession(user, host, Integer.parseInt(TSALabels.PORT_SSH.getValue()));
			Properties config = new Properties();
			config.put("StrictHostKeyChecking", "no");
			config.put(JSCH_CONFIG_INPUT_BUFFER, TSALabels.JSCH_CHANNEL_INPUT_BUFFER_SIZE.getValue());
			session.setConfig(config);
			session.setPassword(password);
			session.connect();
			UtilityMethods.sleepThread(10000);

			try {
				channel = session.openChannel("shell");
				OutputStream ops = channel.getOutputStream();

				PrintStream ps = new PrintStream(ops, true);
				logger.info("Channel Connected to machine " + host + " server");
				channel.connect();
				InputStream input = channel.getInputStream();
				ps.println("terminal length 0");
				ps.println("show run");
				UtilityMethods.sleepThread(20000);
				logger.info("getRouterConfig - Total size of the Channel InputStream -->"+input.available());
				if (routerVersionType.equalsIgnoreCase("previous")) {
					backupdone = true;
					printPreviousVersionInfo(input, channel, configRequest.getRequestId(),
							Double.toString(configRequest.getRequest_version()));
				} else {
					backupdone = true;
					printCurrentVersionInfo(input, channel, configRequest.getRequestId(),
							Double.toString(configRequest.getRequest_version()));
				}
				input.close();
				channel.disconnect();
				session.disconnect();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				backupdone = false;
				e.printStackTrace();
			}
		}

		catch (Exception ex) {
			backupdone = false;
			channel.disconnect();
			session.disconnect();
			String response = "";
			try {
				requestInfoDao.editRequestforReportWebserviceInfo(createConfigRequest.getRequestId(),
						Double.toString(createConfigRequest.getRequest_version()), "deliever_config", "2", "Failure");
				response = invokeFtl.generateDeliveryConfigFileFailure(createConfigRequest);
				TextReport.writeFile(
						TSALabels.RESPONSE_DOWNLOAD_PATH.getValue(), configRequest.getRequestId() + "V"
								+ Double.toString(configRequest.getRequest_version()) + "_deliveredConfig.txt",
						response);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
		finally {

			if (channel != null) {
				try {
				session = channel.getSession();
				
				if (channel.getExitStatus() == -1) {
					
					UtilityMethods.sleepThread(5000);
					
				}
				} catch (Exception e) {
					logger.error(e);
				}
				channel.disconnect();
				session.disconnect();
			
			}
		}
		return backupdone;

	}

	public boolean getRouterConfigStartUp(CreateConfigRequest configRequest, String routerVersionType)
			throws IOException {
		
		InvokeFtl invokeFtl = new InvokeFtl();
		CreateConfigRequest createConfigRequest = new CreateConfigRequest();
		boolean backupdone = false;
		try {
			String host = configRequest.getManagementIp();
			DeviceDiscoveryEntity deviceDetails = deviceDiscoveryRepository
					.findByDHostNameAndDMgmtIpAndDDeComm(configRequest.getHostname(),configRequest.getManagementIp(),"0");			
			CredentialManagementEntity routerCredential = dcmConfigService.getRouterCredential(
					deviceDetails);
			String user = routerCredential.getLoginRead();
			String password = routerCredential.getPasswordWrite();

			JSch jsch = new JSch();
			Channel channel = null;
			Session session = jsch.getSession(user, host, Integer.parseInt(TSALabels.PORT_SSH.getValue()));
			Properties config = new Properties();
			config.put("StrictHostKeyChecking", "no");
			config.put(JSCH_CONFIG_INPUT_BUFFER, TSALabels.JSCH_CHANNEL_INPUT_BUFFER_SIZE.getValue());
			session.setConfig(config);
			session.setPassword(password);
			session.connect();
			UtilityMethods.sleepThread(10000);

			try {
				channel = session.openChannel("shell");
				OutputStream ops = channel.getOutputStream();

				PrintStream ps = new PrintStream(ops, true);
				logger.info("Channel Connected to machine " + host + " server");
				channel.connect();
				InputStream input = channel.getInputStream();
				ps.println("terminal length 0");
				ps.println("show start");
				UtilityMethods.sleepThread(3000);
				if (routerVersionType.equalsIgnoreCase("startup")) {
					backupdone = true;
					printstartupVersionInfo(input, channel, configRequest.getRequestId(),
							Double.toString(configRequest.getRequest_version()));
				} else {
					backupdone = true;
					printCurrentVersionInfo(input, channel, configRequest.getRequestId(),
							Double.toString(configRequest.getRequest_version()));
				}
				channel.disconnect();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		catch (Exception ex) {
			String response = "";
			try {
				requestInfoDao.editRequestforReportWebserviceInfo(createConfigRequest.getRequestId(),
						Double.toString(createConfigRequest.getRequest_version()), "deliever_config", "2", "Failure");
				response = invokeFtl.generateDeliveryConfigFileFailure(createConfigRequest);
				TextReport.writeFile(
						TSALabels.RESPONSE_DOWNLOAD_PATH.getValue(), configRequest.getRequestId() + "V"
								+ Double.toString(configRequest.getRequest_version()) + "_deliveredConfig.txt",
						response);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
		return backupdone;

	}

	public void printPreviousVersionInfo(InputStream input, Channel channel, String requestID, String version)
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
				// logger.info(str);
				String filepath = TSALabels.RESPONSE_DOWNLOAD_PATH.getValue() + requestID + "V" + version + "_PreviousConfig.txt";
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
		UtilityMethods.sleepThread(1000);

	}

	public void printstartupVersionInfo(InputStream input, Channel channel, String requestID, String version)
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
				// logger.info(str);
				String filepath = TSALabels.RESPONSE_DOWNLOAD_PATH.getValue() + requestID + "V" + version + "_StartupConfig.txt";
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
		UtilityMethods.sleepThread(1000);

	}

	public void printCurrentVersionInfo(InputStream input, Channel channel, String requestID, String version)
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
				// logger.info(str);
				String filepath = TSALabels.RESPONSE_DOWNLOAD_PATH.getValue() + requestID + "V" + version + "_CurrentVersionConfig.txt";
				logger.info("File path for current "+filepath);
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
		UtilityMethods.sleepThread(1000);

	}

	public boolean getRouterConfig(RequestInfoPojo configRequest, String routerVersionType,Boolean isStartUp) {
		
		InvokeFtl invokeFtl = new InvokeFtl();
		CreateConfigRequest createConfigRequest = new CreateConfigRequest();
		boolean backupdone = false;
		try {
			if ("VNF".equalsIgnoreCase(configRequest.getNetworkType()) && configRequest.getAlphanumericReqId() !=null 
					&& configRequest.getRequestVersion() !=null && configRequest.getManagementIp() !=null
					&& configRequest.getHostname() !=null && routerVersionType !=null) {
				backupdone = vnfBackup(configRequest.getAlphanumericReqId(), configRequest.getRequestVersion(),
						configRequest.getManagementIp(), configRequest.getHostname(), routerVersionType, "running");
			}
			else {
			String host = configRequest.getManagementIp();
			DeviceDiscoveryEntity deviceDetails = deviceDiscoveryRepository
					.findByDHostNameAndDMgmtIpAndDDeComm(configRequest.getHostname(),configRequest.getManagementIp(),"0");			
			CredentialManagementEntity routerCredential = dcmConfigService.getRouterCredential(
					deviceDetails);
			String user = routerCredential.getLoginRead();
			String password = routerCredential.getPasswordWrite();

			JSch jsch = new JSch();
			Channel channel = null;
			Session session = jsch.getSession(user, host, Integer.parseInt(TSALabels.PORT_SSH.getValue()));
			Properties config = new Properties();
			config.put("StrictHostKeyChecking", "no");
			config.put(JSCH_CONFIG_INPUT_BUFFER, TSALabels.JSCH_CHANNEL_INPUT_BUFFER_SIZE.getValue());
			session.setConfig(config);
			session.setPassword(password);
			session.connect();
			UtilityMethods.sleepThread(10000);

			try {
				channel = session.openChannel("shell");
				OutputStream ops = channel.getOutputStream();

				PrintStream ps = new PrintStream(ops, true);
				logger.info("Channel Connected to machine " + host + " server");
				channel.connect();
				InputStream input = channel.getInputStream();
				ps = requestInfoDetailsDao.setCommandStream(ps,configRequest,"backup",isStartUp);
//				ps.println("terminal length 0");
//				ps.println("show run");
				UtilityMethods.sleepThread(20000);
				logger.info("getRouterConfig - Total size of the Channel InputStream -->"+input.available());
				if (routerVersionType.equalsIgnoreCase("previous")) {
					backupdone = true;
					printPreviousVersionInfo(input, channel, configRequest.getAlphanumericReqId(),
							Double.toString(configRequest.getRequestVersion()));
				} else {
					backupdone = true;
					printCurrentVersionInfo(input, channel, configRequest.getAlphanumericReqId(),
							Double.toString(configRequest.getRequestVersion()));
				}
				channel.disconnect();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
		catch (Exception ex) {
			String response = "";
			try {
				requestInfoDao.editRequestforReportWebserviceInfo(createConfigRequest.getRequestId(),
						Double.toString(createConfigRequest.getRequest_version()), "deliever_config", "2", "Failure");
				response = invokeFtl.generateDeliveryConfigFileFailure(createConfigRequest);			
				TextReport.writeFile(
						TSALabels.RESPONSE_DOWNLOAD_PATH.getValue(), configRequest.getAlphanumericReqId() + "V"
								+ Double.toString(configRequest.getRequestVersion()) + "_deliveredConfig.txt",
						response);
			} catch (Exception e) {
				e.printStackTrace();
			}

		}
		return backupdone;

	}

	
	public boolean getRouterConfigStartUp(RequestInfoPojo configRequest, String routerVersionType,Boolean isStartUp) {
		
		InvokeFtl invokeFtl = new InvokeFtl();
		CreateConfigRequest createConfigRequest = new CreateConfigRequest();
		boolean backupdone = false;
		try {
			if ("VNF".equalsIgnoreCase(configRequest.getNetworkType()) && configRequest.getAlphanumericReqId() !=null 
					&& configRequest.getRequestVersion() !=null && configRequest.getManagementIp() !=null
					&& configRequest.getHostname() !=null && routerVersionType !=null) {
				backupdone = vnfBackup(configRequest.getAlphanumericReqId(), configRequest.getRequestVersion(),
						configRequest.getManagementIp(), configRequest.getHostname(), routerVersionType, "startup");
			}
			else {
				String host = configRequest.getManagementIp();
				DeviceDiscoveryEntity deviceDetails = deviceDiscoveryRepository.findByDHostNameAndDMgmtIpAndDDeComm(
						configRequest.getHostname(), configRequest.getManagementIp(), "0");
				CredentialManagementEntity routerCredential = dcmConfigService.getRouterCredential(deviceDetails);
				String user = routerCredential.getLoginRead();
				String password = routerCredential.getPasswordWrite();

				JSch jsch = new JSch();
				Channel channel = null;
				Session session = jsch.getSession(user, host, Integer.parseInt(TSALabels.PORT_SSH.getValue()));
				Properties config = new Properties();
				config.put("StrictHostKeyChecking", "no");
				config.put(JSCH_CONFIG_INPUT_BUFFER, TSALabels.JSCH_CHANNEL_INPUT_BUFFER_SIZE.getValue());
				session.setConfig(config);
				session.setPassword(password);
				session.connect();
				UtilityMethods.sleepThread(10000);

				try {
					channel = session.openChannel("shell");
					OutputStream ops = channel.getOutputStream();

					PrintStream ps = new PrintStream(ops, true);
					logger.info("Channel Connected to machine " + host + " server");
					channel.connect();
					InputStream input = channel.getInputStream();
					ps = requestInfoDetailsDao.setCommandStream(ps,configRequest,"backup",isStartUp);
//					ps.println("terminal length 0");
//					ps.println("show start");
					UtilityMethods.sleepThread(5000);
					if (routerVersionType.equalsIgnoreCase("startup")) {
						backupdone = true;
						printstartupVersionInfo(input, channel, configRequest.getAlphanumericReqId(),
								Double.toString(configRequest.getRequestVersion()));
					} else {
						backupdone = true;
						printCurrentVersionInfo(input, channel, configRequest.getAlphanumericReqId(),
								Double.toString(configRequest.getRequestVersion()));
					}
					channel.disconnect();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		} catch (Exception ex) {
			String response = "";
			try {
				requestInfoDao.editRequestforReportWebserviceInfo(createConfigRequest.getRequestId(),
						Double.toString(createConfigRequest.getRequest_version()), "deliever_config", "2", "Failure");
				response = invokeFtl.generateDeliveryConfigFileFailure(createConfigRequest);
				TextReport.writeFile(
						TSALabels.RESPONSE_DOWNLOAD_PATH.getValue(), configRequest.getAlphanumericReqId() + "V"
								+ Double.toString(configRequest.getRequestVersion()) + "_deliveredConfig.txt",
						response);
			} catch (Exception e) {
				e.printStackTrace();
			}

		}
		return backupdone;

	}
	
	public String formatXml(String xmlStringToBeFormatted) {
		String formattedXmlString = null;
		try {
			DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
			documentBuilderFactory.setValidating(false);
			DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
			InputSource inputSource = new InputSource(new StringReader(xmlStringToBeFormatted));
			Document document = documentBuilder.parse(inputSource);

			Transformer transformer = TransformerFactory.newInstance().newTransformer();
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");

			StreamResult streamResult = new StreamResult(new StringWriter());
			DOMSource dOMSource = new DOMSource(document);
			transformer.transform(dOMSource, streamResult);
			if(streamResult.getWriter() !=null)
				formattedXmlString = streamResult.getWriter().toString().trim();
		} catch (Exception exe) {
			logger.error("Exception - formatXml->" + exe.getMessage());
		}
		return formattedXmlString;
	}
	
	@SuppressWarnings({ "unchecked", "unused" })
	private boolean vnfBackup(String requestId, double version, String managementIp, String hostName, String stage, String source) {
		boolean backupdone = false;
		try {
			RestTemplate restTemplate = new RestTemplate();
			JSONObject request = new JSONObject();
			String fileName = null;
			request.put(new String("ip"), managementIp);
			request.put(new String("port"), TSALabels.BACKUP_PORT.getValue());
			request.put(new String("source"), source);
			request.put(new String("requestId"), requestId);
			request.put(new String("stage"), stage);
			request.put(new String("version"), version);
			request.put(new String("hostname"), hostName);
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
			HttpEntity<JSONObject> entity = new HttpEntity<JSONObject>(request, headers);
			String url = TSALabels.PYTHON_SERVICES.getValue() + TSALabels.PYTHON_BACKUP.getValue();
			String response = restTemplate.exchange(url, HttpMethod.POST, entity, String.class).getBody();
			logger.info("response of getConfig is " + response);

			if ("previous".equals(stage)) {
				fileName = "PreviousConfig.txt";
			} else if ("startup".equals(stage)) {
				fileName = "StartupConfig.txt";
			} else if ("current".equals(stage)) {
				fileName = "CurrentVersionConfig.txt";
			} else {
				fileName = "PreviousConfig.txt";
			}

			if (response.contains("Error")) {
				TextReport.writeFile(TSALabels.RESPONSE_DOWNLOAD_PATH.getValue(),
						requestId + "V" + Double.toString(version) + "_" + fileName, response);
			} else {
				TextReport.writeFile(TSALabels.RESPONSE_DOWNLOAD_PATH.getValue(),
						requestId + "V" + Double.toString(version) + "_" + fileName, formatXml(response));
			}
			if (response != null) {
				backupdone = true;
			} else {
				backupdone = false;
			}

		} catch (HttpClientErrorException serviceErr) {
			logger.error("HttpClientErrorException - vnfBackup -> " + serviceErr.getMessage());
		} catch (Exception exe) {
			logger.error("Exception - vnfBackup->" + exe.getMessage());
			exe.printStackTrace();
		}
		logger.info("End - vnfBackup ->" + backupdone);

		return backupdone;
	}
}