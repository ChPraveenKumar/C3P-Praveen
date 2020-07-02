package com.techm.orion.service;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.Properties;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;
import com.techm.orion.dao.RequestInfoDao;
import com.techm.orion.pojo.CreateConfigRequest;
import com.techm.orion.pojo.CreateConfigRequestDCM;
import com.techm.orion.pojo.RequestInfoPojo;
import com.techm.orion.pojo.UserPojo;
import com.techm.orion.utility.InvokeFtl;
import com.techm.orion.utility.TextReport;

public class BackupCurrentRouterConfigurationService extends Thread {
	private static final Logger logger = LogManager.getLogger(BackupCurrentRouterConfigurationService.class);

	public static String TSA_PROPERTIES_FILE = "TSA.properties";
	public static final Properties TSA_PROPERTIES = new Properties();
	CreateConfigRequestDCM configRequest = new CreateConfigRequestDCM();

	public boolean getRouterConfig(CreateConfigRequest configRequest, String routerVersionType) throws IOException {
		RequestInfoDao requestInfoDao = new RequestInfoDao();
		InvokeFtl invokeFtl = new InvokeFtl();
		CreateConfigRequest createConfigRequest = new CreateConfigRequest();
		boolean backupdone = false;
		JSch jsch = new JSch();
		Channel channel = null;
		Session session = null;
		try {
			BackupCurrentRouterConfigurationService.loadProperties();
			String host = configRequest.getManagementIp();
			UserPojo userPojo = new UserPojo();
			userPojo = requestInfoDao.getRouterCredentials();

			String user = userPojo.getUsername();
			String password = userPojo.getPassword();
			String port = BackupCurrentRouterConfigurationService.TSA_PROPERTIES.getProperty("portSSH");

			session = jsch.getSession(user, host, Integer.parseInt(port));
			Properties config = new Properties();
			config.put("StrictHostKeyChecking", "no");
			session.setConfig(config);
			session.setPassword(password);
			session.connect();
			try {
				Thread.sleep(10000);
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
				ps.println("show run");
				try {
					Thread.sleep(3000);
				} catch (Exception ee) {
				}
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
			String responseDownloadPath = "";
			try {
				requestInfoDao.editRequestforReportWebserviceInfo(createConfigRequest.getRequestId(),
						Double.toString(createConfigRequest.getRequest_version()), "deliever_config", "2", "Failure");
				response = invokeFtl.generateDeliveryConfigFileFailure(createConfigRequest);
				responseDownloadPath = BackupCurrentRouterConfigurationService.TSA_PROPERTIES
						.getProperty("responseDownloadPath");
				TextReport.writeFile(
						responseDownloadPath, configRequest.getRequestId() + "V"
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
					
						Thread.sleep(5000);
					
				}
				} catch (Exception e) {
					System.out.println(e);
				}
				channel.disconnect();
				session.disconnect();
			
			}
		}
		return backupdone;

	}

	public boolean getRouterConfigStartUp(CreateConfigRequest configRequest, String routerVersionType)
			throws IOException {
		RequestInfoDao requestInfoDao = new RequestInfoDao();
		InvokeFtl invokeFtl = new InvokeFtl();
		CreateConfigRequest createConfigRequest = new CreateConfigRequest();
		boolean backupdone = false;
		try {
			BackupCurrentRouterConfigurationService.loadProperties();
			String host = configRequest.getManagementIp();
			UserPojo userPojo = new UserPojo();
			userPojo = requestInfoDao.getRouterCredentials();

			String user = userPojo.getUsername();
			String password = userPojo.getPassword();
			String port = BackupCurrentRouterConfigurationService.TSA_PROPERTIES.getProperty("portSSH");

			JSch jsch = new JSch();
			Channel channel = null;
			Session session = jsch.getSession(user, host, Integer.parseInt(port));
			Properties config = new Properties();
			config.put("StrictHostKeyChecking", "no");
			session.setConfig(config);
			session.setPassword(password);
			session.connect();
			try {
				Thread.sleep(10000);
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
				ps.println("show start");
				try {
					Thread.sleep(3000);
				} catch (Exception ee) {
				}
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
			String responseDownloadPath = "";
			try {
				requestInfoDao.editRequestforReportWebserviceInfo(createConfigRequest.getRequestId(),
						Double.toString(createConfigRequest.getRequest_version()), "deliever_config", "2", "Failure");
				response = invokeFtl.generateDeliveryConfigFileFailure(createConfigRequest);
				responseDownloadPath = BackupCurrentRouterConfigurationService.TSA_PROPERTIES
						.getProperty("responseDownloadPath");
				TextReport.writeFile(
						responseDownloadPath, configRequest.getRequestId() + "V"
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
				String filepath = BackupCurrentRouterConfigurationService.TSA_PROPERTIES
						.getProperty("responseDownloadPath") + "//" + requestID + "V" + version + "_PreviousConfig.txt";
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
				String filepath = BackupCurrentRouterConfigurationService.TSA_PROPERTIES
						.getProperty("responseDownloadPath") + "//" + requestID + "V" + version + "_StartupConfig.txt";
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
				String filepath = BackupCurrentRouterConfigurationService.TSA_PROPERTIES.getProperty(
						"responseDownloadPath") + "//" + requestID + "V" + version + "_CurrentVersionConfig.txt";
				System.out.println("File path for current "+filepath);
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

	public boolean getRouterConfig(RequestInfoPojo configRequest, String routerVersionType) {
		RequestInfoDao requestInfoDao = new RequestInfoDao();
		InvokeFtl invokeFtl = new InvokeFtl();
		CreateConfigRequest createConfigRequest = new CreateConfigRequest();
		boolean backupdone = false;
		try {
			BackupCurrentRouterConfigurationService.loadProperties();
			String host = configRequest.getManagementIp();
			UserPojo userPojo = new UserPojo();
			userPojo = requestInfoDao.getRouterCredentials();

			String user = userPojo.getUsername();
			String password = userPojo.getPassword();
			String port = BackupCurrentRouterConfigurationService.TSA_PROPERTIES.getProperty("portSSH");

			JSch jsch = new JSch();
			Channel channel = null;
			Session session = jsch.getSession(user, host, Integer.parseInt(port));
			Properties config = new Properties();
			config.put("StrictHostKeyChecking", "no");
			session.setConfig(config);
			session.setPassword(password);
			session.connect();
			try {
				Thread.sleep(10000);
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
				ps.println("show run");
				try {
					Thread.sleep(3000);
				} catch (Exception ee) {
				}
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

		catch (Exception ex) {
			String response = "";
			String responseDownloadPath = "";
			try {
				requestInfoDao.editRequestforReportWebserviceInfo(createConfigRequest.getRequestId(),
						Double.toString(createConfigRequest.getRequest_version()), "deliever_config", "2", "Failure");
				response = invokeFtl.generateDeliveryConfigFileFailure(createConfigRequest);
				responseDownloadPath = BackupCurrentRouterConfigurationService.TSA_PROPERTIES
						.getProperty("responseDownloadPath");
				TextReport.writeFile(
						responseDownloadPath, configRequest.getAlphanumericReqId() + "V"
								+ Double.toString(configRequest.getRequestVersion()) + "_deliveredConfig.txt",
						response);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
		return backupdone;

	}

	public boolean getRouterConfigStartUp(RequestInfoPojo configRequest, String routerVersionType) {
		RequestInfoDao requestInfoDao = new RequestInfoDao();
		InvokeFtl invokeFtl = new InvokeFtl();
		CreateConfigRequest createConfigRequest = new CreateConfigRequest();
		boolean backupdone = false;
		try {
			BackupCurrentRouterConfigurationService.loadProperties();
			String host = configRequest.getManagementIp();
			UserPojo userPojo = new UserPojo();
			userPojo = requestInfoDao.getRouterCredentials();

			String user = userPojo.getUsername();
			String password = userPojo.getPassword();
			String port = BackupCurrentRouterConfigurationService.TSA_PROPERTIES.getProperty("portSSH");

			JSch jsch = new JSch();
			Channel channel = null;
			Session session = jsch.getSession(user, host, Integer.parseInt(port));
			Properties config = new Properties();
			config.put("StrictHostKeyChecking", "no");
			session.setConfig(config);
			session.setPassword(password);
			session.connect();
			try {
				Thread.sleep(10000);
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
				ps.println("show start");
				try {
					Thread.sleep(3000);
				} catch (Exception ee) {
				}
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
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		catch (Exception ex) {
			String response = "";
			String responseDownloadPath = "";
			try {
				requestInfoDao.editRequestforReportWebserviceInfo(createConfigRequest.getRequestId(),
						Double.toString(createConfigRequest.getRequest_version()), "deliever_config", "2", "Failure");
				response = invokeFtl.generateDeliveryConfigFileFailure(createConfigRequest);
				responseDownloadPath = BackupCurrentRouterConfigurationService.TSA_PROPERTIES
						.getProperty("responseDownloadPath");
				TextReport.writeFile(
						responseDownloadPath, configRequest.getAlphanumericReqId() + "V"
								+ Double.toString(configRequest.getRequestVersion()) + "_deliveredConfig.txt",
						response);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
		return backupdone;

	}

}