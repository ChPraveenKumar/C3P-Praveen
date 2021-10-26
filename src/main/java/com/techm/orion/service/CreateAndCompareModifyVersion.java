package com.techm.orion.service;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.techm.orion.dao.RequestInfoDao;
import com.techm.orion.pojo.ChildVersionPojo;
import com.techm.orion.pojo.CreateConfigRequestDCM;
import com.techm.orion.pojo.ModifyConfigResultPojo;
import com.techm.orion.pojo.ParentVersionPojo;
import com.techm.orion.pojo.RequestInfoSO;
import com.techm.orion.utility.InvokeFtl;
import com.techm.orion.utility.C3PCoreAppLabels;
import com.techm.orion.utility.TextReport;

@Component
public class CreateAndCompareModifyVersion {
	private static final Logger logger = LogManager.getLogger(CreateAndCompareModifyVersion.class);

	@Autowired
	private RequestInfoDao requestInfoDao;
	
	public String CompareModifyVersion(String requestIdForConfig, String type) throws Exception {

		String key = "";
		int counter = 1;
		int counterCreate = 1;
		List<RequestInfoSO> list = new ArrayList<RequestInfoSO>();
		
		ChildVersionPojo latestVersion = new ChildVersionPojo();
		ParentVersionPojo compareVersion = new ParentVersionPojo();
		InvokeFtl invokeFtl = new InvokeFtl();
		list = requestInfoDao.getDatasToCompareForRequestfromDB(requestIdForConfig);

		RequestInfoSO childVersion = list.get(0);
		RequestInfoSO parentVersion = list.get(1);
		// to map the new config
		latestVersion.setEnablePassword(childVersion.getEnablePassword());
		latestVersion.setVrfName(childVersion.getVrfName());
		latestVersion.setNeighbor1_remoteAS(childVersion.getInternetLcVrf().getNeighbor1_remoteAS());
		latestVersion.setRoutingProtocol(childVersion.getInternetLcVrf().getRoutingProtocol());
		latestVersion.setNetworkIp(childVersion.getInternetLcVrf().getNetworkIp());
		latestVersion.setNetworkIp_subnetMask(childVersion.getInternetLcVrf().getNetworkIp_subnetMask());
		latestVersion.setNeighbor1(childVersion.getInternetLcVrf().getNeighbor1());
		latestVersion.setName(childVersion.getDeviceInterfaceSO().getName());
		latestVersion.setDescription(childVersion.getDeviceInterfaceSO().getDescription());
		latestVersion.setBandwidth(childVersion.getDeviceInterfaceSO().getBandwidth());
		latestVersion.setEncapsulation(childVersion.getDeviceInterfaceSO().getEncapsulation());
		latestVersion.setBanner(childVersion.getBanner());
		latestVersion.setRequestId(childVersion.getDisplay_request_id());
		latestVersion.setRequest_version(childVersion.getRequest_version());
		latestVersion.setIp(childVersion.getDeviceInterfaceSO().getIp());
		latestVersion.setMask(childVersion.getDeviceInterfaceSO().getMask());
		latestVersion.setSnmpHostAddress(childVersion.getSnmpHostAddress());
		latestVersion.setSnmpString(childVersion.getSnmpString());
		latestVersion.setLoopBackType(childVersion.getLoopBackType());
		latestVersion.setLoopbackIPaddress(childVersion.getLoopbackIPaddress());
		latestVersion.setLoopbackSubnetMask(childVersion.getLoopbackSubnetMask());
		latestVersion.setBgpASNumber(childVersion.getInternetLcVrf().getBgpASNumber());

		// to map the old config
		compareVersion.setEnablePassword(parentVersion.getEnablePassword());
		compareVersion.setVrfName(parentVersion.getVrfName());
		compareVersion.setNeighbor1_remoteAS(parentVersion.getInternetLcVrf().getNeighbor1_remoteAS());
		compareVersion.setRoutingProtocol(parentVersion.getInternetLcVrf().getRoutingProtocol());
		compareVersion.setNetworkIp(parentVersion.getInternetLcVrf().getNetworkIp());
		compareVersion.setNetworkIp_subnetMask(parentVersion.getInternetLcVrf().getNetworkIp_subnetMask());
		compareVersion.setNeighbor1(parentVersion.getInternetLcVrf().getNeighbor1());
		compareVersion.setName(parentVersion.getDeviceInterfaceSO().getName());
		compareVersion.setDescription(parentVersion.getDeviceInterfaceSO().getDescription());
		compareVersion.setBandwidth(parentVersion.getDeviceInterfaceSO().getBandwidth());
		compareVersion.setEncapsulation(parentVersion.getDeviceInterfaceSO().getEncapsulation());
		compareVersion.setBanner(parentVersion.getBanner());
		compareVersion.setIp(parentVersion.getDeviceInterfaceSO().getIp());
		compareVersion.setMask(parentVersion.getDeviceInterfaceSO().getMask());
		compareVersion.setSnmpHostAddress(parentVersion.getSnmpHostAddress());
		compareVersion.setSnmpString(parentVersion.getSnmpString());
		compareVersion.setLoopBackType(parentVersion.getLoopBackType());
		compareVersion.setLoopbackIPaddress(parentVersion.getLoopbackIPaddress());
		compareVersion.setLoopbackSubnetMask(parentVersion.getLoopbackSubnetMask());
		compareVersion.setBgpASNumber(parentVersion.getInternetLcVrf().getBgpASNumber());

		if (childVersion.getVrfName() != null && parentVersion.getVrfName() != null
				&& childVersion.getInternetLcVrf().getNeighbor1_remoteAS() != null
				&& parentVersion.getInternetLcVrf().getNeighbor1_remoteAS() != null) {
			if (!(childVersion.getVrfName().equalsIgnoreCase(parentVersion.getVrfName())
					&& childVersion.getInternetLcVrf().getNeighbor1_remoteAS()
							.equalsIgnoreCase(parentVersion.getInternetLcVrf().getNeighbor1_remoteAS()))) {
				key = "vrf";
				// get the data
				getConfigDataforNoCmd(parentVersion, key, counter++);
			}
		}

		if (childVersion.getEnablePassword() != null && parentVersion.getEnablePassword() != null) {
			if (!(childVersion.getEnablePassword().equalsIgnoreCase(parentVersion.getEnablePassword()))) {
				key = "password";
				getConfigDataforNoCmd(parentVersion, key, counter++);

			}
		}

		if (childVersion.getInternetLcVrf().getRoutingProtocol() != null
				&& parentVersion.getInternetLcVrf().getRoutingProtocol() != null) {
			if (!(childVersion.getInternetLcVrf().getNeighbor1_remoteAS()
					.equalsIgnoreCase(parentVersion.getInternetLcVrf().getNeighbor1_remoteAS())
					&& childVersion.getVrfName().equalsIgnoreCase(parentVersion.getVrfName())
					&& childVersion.getInternetLcVrf().getBgpASNumber()
							.equalsIgnoreCase(parentVersion.getInternetLcVrf().getBgpASNumber())
					&& childVersion.getInternetLcVrf().getRoutingProtocol()
							.equalsIgnoreCase(parentVersion.getInternetLcVrf().getRoutingProtocol())
					&& childVersion.getInternetLcVrf().getNetworkIp()
							.equalsIgnoreCase(parentVersion.getInternetLcVrf().getNetworkIp())
					&& childVersion.getInternetLcVrf().getNetworkIp_subnetMask()
							.equalsIgnoreCase(parentVersion.getInternetLcVrf().getNetworkIp_subnetMask())
					&& childVersion.getInternetLcVrf().getNeighbor1()
							.equalsIgnoreCase(parentVersion.getInternetLcVrf().getNeighbor1())
			/*
			 * childVersion.getInternetLcVrf().getNeighbor2().equalsIgnoreCase(parentVersion
			 * .getInternetLcVrf().getNeighbor2())&&
			 * childVersion.getInternetLcVrf().getNeighbor2_remoteAS().equalsIgnoreCase(
			 * parentVersion.getInternetLcVrf().getNeighbor2_remoteAS())
			 */
			)) {
				key = "bgp";
				getConfigDataforNoCmd(parentVersion, key, counter++);
			}
		}
		if (childVersion.getDeviceInterfaceSO().getName() != null
				&& parentVersion.getDeviceInterfaceSO().getName() != null) {
			if (!(childVersion.getDeviceInterfaceSO().getName()
					.equalsIgnoreCase(parentVersion.getDeviceInterfaceSO().getName()) &&
			/*
			 * childVersion.getDeviceInterfaceSO().getDescription().equalsIgnoreCase(
			 * parentVersion.getDeviceInterfaceSO().getDescription())&
			 */
					childVersion.getDeviceInterfaceSO().getBandwidth()
							.equalsIgnoreCase(parentVersion.getDeviceInterfaceSO().getBandwidth())
					&& childVersion.getDeviceInterfaceSO().getEncapsulation()
							.equalsIgnoreCase(parentVersion.getDeviceInterfaceSO().getEncapsulation()))) {
				key = "wanInterface";
				getConfigDataforNoCmd(parentVersion, key, counter++);
			}
		}
		if (childVersion.getBanner() != null && parentVersion.getBanner() != null) {
			if (!(childVersion.getBanner().equalsIgnoreCase(parentVersion.getBanner()))) {
				key = "banner";
				// get the data
				getConfigDataforNoCmd(parentVersion, key, counter++);
			}
		}
		if (childVersion.getEnablePassword() != null && parentVersion.getEnablePassword() != null) {
			if (!(childVersion.getEnablePassword().equalsIgnoreCase(parentVersion.getEnablePassword()))) {
				key = "password";
				getConfigData(parentVersion, key, counterCreate++);

			}
		}
		if (childVersion.getVrfName() != null && parentVersion.getVrfName() != null
				&& childVersion.getInternetLcVrf().getNeighbor1_remoteAS() != null
				&& parentVersion.getInternetLcVrf().getNeighbor1_remoteAS() != null) {
			if (!(childVersion.getVrfName().equalsIgnoreCase(parentVersion.getVrfName()))) {
				key = "vrf";
				// get the data
				getConfigData(parentVersion, key, counterCreate++);
			}
		}
		if (childVersion.getInternetLcVrf().getRoutingProtocol() != null
				&& parentVersion.getInternetLcVrf().getRoutingProtocol() != null) {
			if (!(childVersion.getInternetLcVrf().getNeighbor1_remoteAS()
					.equalsIgnoreCase(parentVersion.getInternetLcVrf().getNeighbor1_remoteAS())
					&& childVersion.getInternetLcVrf().getBgpASNumber()
							.equalsIgnoreCase(parentVersion.getInternetLcVrf().getBgpASNumber())
					&& childVersion.getInternetLcVrf().getRoutingProtocol()
							.equalsIgnoreCase(parentVersion.getInternetLcVrf().getRoutingProtocol())
					&& childVersion.getInternetLcVrf().getNetworkIp()
							.equalsIgnoreCase(parentVersion.getInternetLcVrf().getNetworkIp())
					&& childVersion.getInternetLcVrf().getNetworkIp_subnetMask()
							.equalsIgnoreCase(parentVersion.getInternetLcVrf().getNetworkIp_subnetMask())
					&& childVersion.getInternetLcVrf().getNeighbor1()
							.equalsIgnoreCase(parentVersion.getInternetLcVrf().getNeighbor1())
			/*
			 * childVersion.getInternetLcVrf().getNeighbor2().equalsIgnoreCase(parentVersion
			 * .getInternetLcVrf().getNeighbor2())&&
			 * childVersion.getInternetLcVrf().getNeighbor2_remoteAS().equalsIgnoreCase(
			 * parentVersion.getInternetLcVrf().getNeighbor2_remoteAS())
			 */
			)) {

				key = "bgp";
				getConfigData(parentVersion, key, counterCreate++);
			}
		}
		if (childVersion.getDeviceInterfaceSO().getName() != null
				&& parentVersion.getDeviceInterfaceSO().getName() != null) {
			if (!(childVersion.getDeviceInterfaceSO().getName()
					.equalsIgnoreCase(parentVersion.getDeviceInterfaceSO().getName()) &&
			/*
			 * childVersion.getDeviceInterfaceSO().getDescription().equalsIgnoreCase(
			 * parentVersion.getDeviceInterfaceSO().getDescription())&
			 */
					childVersion.getDeviceInterfaceSO().getBandwidth()
							.equalsIgnoreCase(parentVersion.getDeviceInterfaceSO().getBandwidth())
					&& childVersion.getDeviceInterfaceSO().getEncapsulation()
							.equalsIgnoreCase(parentVersion.getDeviceInterfaceSO().getEncapsulation()))) {
				key = "wanInterface";
				getConfigData(parentVersion, key, counterCreate++);
			}
		}
		if (childVersion.getBanner() != null && parentVersion.getBanner() != null) {
			if (!(childVersion.getBanner().equalsIgnoreCase(parentVersion.getBanner()))) {
				key = "banner";
				// get the data
				getConfigData(parentVersion, key, counterCreate++);
			}
		}

		if (childVersion.getSnmpHostAddress() != null && parentVersion.getSnmpHostAddress() != null) {
			if (!(childVersion.getSnmpHostAddress().equalsIgnoreCase(parentVersion.getSnmpHostAddress())
					&& childVersion.getSnmpString().equalsIgnoreCase(parentVersion.getSnmpString()))) {
				key = "snmp";
				// get the data
				getConfigData(parentVersion, key, counterCreate++);
			}
		}
		if (childVersion.getSnmpHostAddress() != null && parentVersion.getSnmpHostAddress() != null) {
			if (!(childVersion.getSnmpHostAddress().equalsIgnoreCase(parentVersion.getSnmpHostAddress())
					&& childVersion.getSnmpString().equalsIgnoreCase(parentVersion.getSnmpString()))) {
				key = "snmp";
				// get the data
				getConfigDataforNoCmd(parentVersion, key, counter++);
			}
		}
		if (childVersion.getLoopbackIPaddress() != null && parentVersion.getLoopbackIPaddress() != null) {
			if (!(childVersion.getLoopBackType().equalsIgnoreCase(parentVersion.getLoopBackType())
					&& childVersion.getLoopbackIPaddress().equalsIgnoreCase(parentVersion.getLoopbackIPaddress())
					&& childVersion.getLoopbackSubnetMask().equalsIgnoreCase(parentVersion.getLoopbackSubnetMask()))) {
				key = "loopback";
				// get the data
				getConfigData(parentVersion, key, counterCreate++);
			}
		}
		if (childVersion.getLoopbackIPaddress() != null && parentVersion.getLoopbackIPaddress() != null) {
			if (!(childVersion.getLoopBackType().equalsIgnoreCase(parentVersion.getLoopBackType())
					&& childVersion.getLoopbackIPaddress().equalsIgnoreCase(parentVersion.getLoopbackIPaddress())
					&& childVersion.getLoopbackSubnetMask().equalsIgnoreCase(parentVersion.getLoopbackSubnetMask()))) {
				key = "loopback";
				// get the data
				getConfigDataforNoCmd(parentVersion, key, counter++);
			}
		}

		String response = invokeFtl.generateModifyConfigurationToPush(latestVersion, compareVersion);

		String responseforNoCmd = invokeFtl.generateModifyConfigurationToPushNoCmd(latestVersion, compareVersion);
		String responseToTemplate = responseforNoCmd.concat(response);
		// if the type is template create,we are going to generate a file for it
		if (type.equalsIgnoreCase("templatecreate")) {
			try {
				TextReport.writeFile(C3PCoreAppLabels.RESPONSE_DOWNLOAD_PATH.getValue(),
						latestVersion.getRequestId() + "V" + latestVersion.getRequest_version() + "_Configuration",
						response);
				TextReport.writeFile(C3PCoreAppLabels.RESPONSE_DOWNLOAD_PATH.getValue(),
						latestVersion.getRequestId() + "V" + latestVersion.getRequest_version() + "_ConfigurationNoCmd",
						responseforNoCmd);

			} catch (Exception exe) {
				exe.printStackTrace();
			}
		}
		return responseToTemplate;
	}

	public void getConfigData(RequestInfoSO latestVersion, String key, int counter) throws IOException {
		String config_cmd = "";
		
		List<ModifyConfigResultPojo> configCmdresultList = new ArrayList<ModifyConfigResultPojo>();
		configCmdresultList = requestInfoDao.getConfigCmdRecordFordata(latestVersion, key);
		for (Iterator<ModifyConfigResultPojo> iterator = configCmdresultList.iterator(); iterator.hasNext();) {
			ModifyConfigResultPojo modifyConfigResultPojo = (ModifyConfigResultPojo) iterator.next();

			config_cmd = config_cmd.concat(modifyConfigResultPojo.getCreate_SSH_Command());
			// String content="config t"+";"+config_cmd;
			String content = config_cmd;
			String ar[] = content.split(";");
			createconfigFile(ar, counter);
		}
	}

	public void getConfigDataforNoCmd(RequestInfoSO latestVersion, String key, int counter) throws IOException {

		String no_cmd = "";		
		List<ModifyConfigResultPojo> configCmdresultList = new ArrayList<ModifyConfigResultPojo>();
		configCmdresultList = requestInfoDao.getConfigCmdRecordFordata(latestVersion, key);
		for (Iterator<ModifyConfigResultPojo> iterator = configCmdresultList.iterator(); iterator.hasNext();) {
			ModifyConfigResultPojo modifyConfigResultPojo = (ModifyConfigResultPojo) iterator.next();
			no_cmd = no_cmd.concat(modifyConfigResultPojo.getNo_SSH_Command());

			// String content="config t"+";"+no_cmd+";";
			String content = no_cmd;
			String ar[] = content.split(";");
			createNoconfigFile(ar, counter);
		}

	}
	// counter to check when we create file for the first time

	public void createNoconfigFile(String[] cmd, int counter) {
		try {
			BufferedWriter bw = null;
			FileWriter fw = null;
			String filepath = C3PCoreAppLabels.RESPONSE_DOWNLOAD_PATH.getValue() + "/noconfig.txt";
			File file = new File(filepath);
			if (file.exists() && counter == 1) {
				PrintWriter writer = new PrintWriter(file);
				writer.print("");
				writer.close();
			}
			for (int i = 0; i < cmd.length; i++) {

				String s;
				s = cmd[i];

				// if file doesnt exists, then create it
				if (!file.exists()) {
					file.createNewFile();

					fw = new FileWriter(file, true);
					bw = new BufferedWriter(fw);
					bw.write(s);
					bw.newLine();
					bw.flush();
				} else {
					fw = new FileWriter(file.getAbsoluteFile(), true);
					bw = new BufferedWriter(fw);
					bw.write(s);
					bw.newLine();
					bw.flush();
				}

				// Always close files.
				bw.close();

			}
		} catch (IOException ex) {
			logger.error("Exception in createNoconfigFile method "+ex.getMessage());
		}
	}

	public void createconfigFile(String[] cmd, int counter) {
		try {
			BufferedWriter bw = null;
			FileWriter fw = null;
			String filepath = C3PCoreAppLabels.RESPONSE_DOWNLOAD_PATH.getValue() + "/createconfig.txt";
			File file = new File(filepath);
			if (file.exists() && counter == 1) {
				PrintWriter writer = new PrintWriter(file);
				writer.print("");
				writer.close();
			}
			for (int i = 0; i < cmd.length; i++) {

				String s;
				s = cmd[i];

				// if file doesnt exists, then create it
				if (!file.exists()) {
					file.createNewFile();

					fw = new FileWriter(file, true);
					bw = new BufferedWriter(fw);
					bw.write(s);
					bw.newLine();
					bw.flush();
				} else {
					fw = new FileWriter(file.getAbsoluteFile(), true);
					bw = new BufferedWriter(fw);
					bw.write(s);
					bw.newLine();
					bw.flush();
				}

				// Always close files.
				bw.close();

			}
		} catch (IOException ex) {
			logger.error("Exception in createconfigFile method "+ex.getMessage());
		}
	}

	public String CompareModifyVersionForTemplate(String requestIdForConfig, String type,
			CreateConfigRequestDCM configRequest) throws Exception {

		String key = "";
		int counter = 1;
		int counterCreate = 1;
		List<RequestInfoSO> list = new ArrayList<RequestInfoSO>();
		
		ChildVersionPojo latestVersion = new ChildVersionPojo();
		ParentVersionPojo compareVersion = new ParentVersionPojo();
		InvokeFtl invokeFtl = new InvokeFtl();
		list = requestInfoDao.getDatasToCompareForRequestfromDB(requestIdForConfig);

		// RequestInfoSO childVersion=list.get(0);
		RequestInfoSO parentVersion = list.get(0);
		// to map the new config
		latestVersion.setEnablePassword(configRequest.getEnablePassword());
		latestVersion.setVrfName(configRequest.getVrfName());
		latestVersion.setNeighbor1_remoteAS(configRequest.getNeighbor1_remoteAS());
		latestVersion.setRoutingProtocol(configRequest.getRoutingProtocol());
		latestVersion.setNetworkIp(configRequest.getNetworkIp());
		latestVersion.setNetworkIp_subnetMask(configRequest.getNetworkIp_subnetMask());
		latestVersion.setNeighbor1(configRequest.getNeighbor1());
		latestVersion.setName(configRequest.getName());
		latestVersion.setDescription(configRequest.getDescription());
		latestVersion.setBandwidth(configRequest.getBandwidth());
		latestVersion.setEncapsulation(configRequest.getEncapsulation());
		latestVersion.setBanner(configRequest.getBanner());
		latestVersion.setRequestId(configRequest.getDisplay_request_id());
		latestVersion.setRequest_version(configRequest.getRequest_version());
		latestVersion.setIp(configRequest.getIp());
		latestVersion.setMask(configRequest.getMask());
		latestVersion.setSnmpHostAddress(configRequest.getSnmpHostAddress());
		latestVersion.setSnmpString(configRequest.getSnmpString());
		latestVersion.setLoopBackType(configRequest.getLoopBackType());
		latestVersion.setLoopbackIPaddress(configRequest.getLoopbackIPaddress());
		latestVersion.setLoopbackSubnetMask(configRequest.getLoopbackSubnetMask());
		latestVersion.setBgpASNumber(configRequest.getBgpASNumber());

		// to map the old config
		compareVersion.setEnablePassword(parentVersion.getEnablePassword());
		compareVersion.setVrfName(parentVersion.getVrfName());
		compareVersion.setNeighbor1_remoteAS(parentVersion.getInternetLcVrf().getNeighbor1_remoteAS());
		compareVersion.setRoutingProtocol(parentVersion.getInternetLcVrf().getRoutingProtocol());
		compareVersion.setNetworkIp(parentVersion.getInternetLcVrf().getNetworkIp());
		compareVersion.setNetworkIp_subnetMask(parentVersion.getInternetLcVrf().getNetworkIp_subnetMask());
		compareVersion.setNeighbor1(parentVersion.getInternetLcVrf().getNeighbor1());
		compareVersion.setName(parentVersion.getDeviceInterfaceSO().getName());
		compareVersion.setDescription(parentVersion.getDeviceInterfaceSO().getDescription());
		compareVersion.setBandwidth(parentVersion.getDeviceInterfaceSO().getBandwidth());
		compareVersion.setEncapsulation(parentVersion.getDeviceInterfaceSO().getEncapsulation());
		compareVersion.setBanner(parentVersion.getBanner());
		compareVersion.setIp(parentVersion.getDeviceInterfaceSO().getIp());
		compareVersion.setMask(parentVersion.getDeviceInterfaceSO().getMask());
		compareVersion.setSnmpHostAddress(parentVersion.getSnmpHostAddress());
		compareVersion.setSnmpString(parentVersion.getSnmpString());
		compareVersion.setLoopBackType(parentVersion.getLoopBackType());
		compareVersion.setLoopbackIPaddress(parentVersion.getLoopbackIPaddress());
		compareVersion.setLoopbackSubnetMask(parentVersion.getLoopbackSubnetMask());
		compareVersion.setBgpASNumber(parentVersion.getInternetLcVrf().getBgpASNumber());

		if (configRequest.getVrfName() != null && parentVersion.getVrfName() != null
				&& configRequest.getNeighbor1_remoteAS() != null
				&& parentVersion.getInternetLcVrf().getNeighbor1_remoteAS() != null) {
			if (!(configRequest.getVrfName().equalsIgnoreCase(parentVersion.getVrfName())
					&& configRequest.getNeighbor1_remoteAS()
							.equalsIgnoreCase(parentVersion.getInternetLcVrf().getNeighbor1_remoteAS()))) {
				key = "vrf";
				// get the data
				getConfigDataforNoCmd(parentVersion, key, counter++);
			}
		}

		if (configRequest.getEnablePassword() != null && parentVersion.getEnablePassword() != null) {
			if (!(configRequest.getEnablePassword().equalsIgnoreCase(parentVersion.getEnablePassword()))) {
				key = "password";
				getConfigDataforNoCmd(parentVersion, key, counter++);

			}
		}

		if (configRequest.getRoutingProtocol() != null
				&& parentVersion.getInternetLcVrf().getRoutingProtocol() != null) {
			if (!(configRequest.getNeighbor1_remoteAS()
					.equalsIgnoreCase(parentVersion.getInternetLcVrf().getNeighbor1_remoteAS())
					&& configRequest.getVrfName().equalsIgnoreCase(parentVersion.getVrfName())
					&& configRequest.getBgpASNumber()
							.equalsIgnoreCase(parentVersion.getInternetLcVrf().getBgpASNumber())
					&& configRequest.getRoutingProtocol()
							.equalsIgnoreCase(parentVersion.getInternetLcVrf().getRoutingProtocol())
					&& configRequest.getNetworkIp().equalsIgnoreCase(parentVersion.getInternetLcVrf().getNetworkIp())
					&& configRequest.getNetworkIp_subnetMask()
							.equalsIgnoreCase(parentVersion.getInternetLcVrf().getNetworkIp_subnetMask())
					&& configRequest.getNeighbor1().equalsIgnoreCase(parentVersion.getInternetLcVrf().getNeighbor1())
			/*
			 * configRequest.getNeighbor2().equalsIgnoreCase(parentVersion.getInternetLcVrf(
			 * ).getNeighbor2())&&
			 * configRequest.getNeighbor2_remoteAS().equalsIgnoreCase(parentVersion.
			 * getInternetLcVrf().getNeighbor2_remoteAS())
			 */
			)) {

				key = "bgp";
				getConfigDataforNoCmd(parentVersion, key, counter++);
			}
		}
		if (configRequest.getName() != null && parentVersion.getDeviceInterfaceSO().getName() != null) {
			if (!(configRequest.getName().equalsIgnoreCase(parentVersion.getDeviceInterfaceSO().getName()) &&
			/*
			 * childVersion.getDeviceInterfaceSO().getDescription().equalsIgnoreCase(
			 * parentVersion.getDeviceInterfaceSO().getDescription())&
			 */
					configRequest.getBandwidth().equalsIgnoreCase(parentVersion.getDeviceInterfaceSO().getBandwidth())
					&& configRequest.getEncapsulation()
							.equalsIgnoreCase(parentVersion.getDeviceInterfaceSO().getEncapsulation()))) {
				key = "wanInterface";
				getConfigDataforNoCmd(parentVersion, key, counter++);
			}
		}
		if (configRequest.getBanner() != null && parentVersion.getBanner() != null) {
			if (!(configRequest.getBanner().equalsIgnoreCase(parentVersion.getBanner()))) {
				key = "banner";
				// get the data
				getConfigDataforNoCmd(parentVersion, key, counter++);
			}
		}
		if (configRequest.getEnablePassword() != null && parentVersion.getEnablePassword() != null) {
			if (!(configRequest.getEnablePassword().equalsIgnoreCase(parentVersion.getEnablePassword()))) {
				key = "password";
				getConfigData(parentVersion, key, counterCreate++);

			}
		}
		if (configRequest.getVrfName() != null && parentVersion.getVrfName() != null
				&& configRequest.getNeighbor1_remoteAS() != null
				&& parentVersion.getInternetLcVrf().getNeighbor1_remoteAS() != null) {
			if (!(configRequest.getVrfName().equalsIgnoreCase(parentVersion.getVrfName())
					&& configRequest.getNeighbor1_remoteAS()
							.equalsIgnoreCase(parentVersion.getInternetLcVrf().getNeighbor1_remoteAS()))) {
				key = "vrf";
				// get the data
				getConfigData(parentVersion, key, counterCreate++);
			}
		}
		if (configRequest.getRoutingProtocol() != null
				&& parentVersion.getInternetLcVrf().getRoutingProtocol() != null) {
			if (!(configRequest.getNeighbor1_remoteAS()
					.equalsIgnoreCase(parentVersion.getInternetLcVrf().getNeighbor1_remoteAS())
					&& configRequest.getVrfName().equalsIgnoreCase(parentVersion.getVrfName())
					&& configRequest.getBgpASNumber()
							.equalsIgnoreCase(parentVersion.getInternetLcVrf().getBgpASNumber())
					&& configRequest.getRoutingProtocol()
							.equalsIgnoreCase(parentVersion.getInternetLcVrf().getRoutingProtocol())
					&& configRequest.getNetworkIp().equalsIgnoreCase(parentVersion.getInternetLcVrf().getNetworkIp())
					&& configRequest.getNetworkIp_subnetMask()
							.equalsIgnoreCase(parentVersion.getInternetLcVrf().getNetworkIp_subnetMask())
					&& configRequest.getNeighbor1().equalsIgnoreCase(parentVersion.getInternetLcVrf().getNeighbor1())
			/*
			 * configRequest.getNeighbor2().equalsIgnoreCase(parentVersion.getInternetLcVrf(
			 * ).getNeighbor2())&&
			 * configRequest.getNeighbor2_remoteAS().equalsIgnoreCase(parentVersion.
			 * getInternetLcVrf().getNeighbor2_remoteAS())
			 */
			)) {

				key = "bgp";
				getConfigData(parentVersion, key, counterCreate++);
			}
		}
		if (configRequest.getName() != null && parentVersion.getDeviceInterfaceSO().getName() != null) {
			if (!(configRequest.getName().equalsIgnoreCase(parentVersion.getDeviceInterfaceSO().getName()) &&
			/*
			 * childVersion.getDeviceInterfaceSO().getDescription().equalsIgnoreCase(
			 * parentVersion.getDeviceInterfaceSO().getDescription())&
			 */
					configRequest.getBandwidth().equalsIgnoreCase(parentVersion.getDeviceInterfaceSO().getBandwidth())
					&& configRequest.getEncapsulation()
							.equalsIgnoreCase(parentVersion.getDeviceInterfaceSO().getEncapsulation()))) {
				key = "wanInterface";
				getConfigData(parentVersion, key, counterCreate++);
			}
		}
		if (configRequest.getBanner() != null && parentVersion.getBanner() != null) {
			if (!(configRequest.getBanner().equalsIgnoreCase(parentVersion.getBanner()))) {
				key = "banner";
				// get the data
				getConfigData(parentVersion, key, counterCreate++);
			}
		}

		if (configRequest.getSnmpHostAddress() != null && parentVersion.getSnmpHostAddress() != null) {
			if (!(configRequest.getSnmpHostAddress().equalsIgnoreCase(parentVersion.getSnmpHostAddress())
					&& configRequest.getSnmpString().equalsIgnoreCase(parentVersion.getSnmpString()))) {
				key = "snmp";
				// get the data
				getConfigData(parentVersion, key, counterCreate++);
			}
		}
		if (configRequest.getSnmpHostAddress() != null && parentVersion.getSnmpHostAddress() != null) {
			if (!(configRequest.getSnmpHostAddress().equalsIgnoreCase(parentVersion.getSnmpHostAddress())
					&& configRequest.getSnmpString().equalsIgnoreCase(parentVersion.getSnmpString()))) {
				key = "snmp";
				// get the data
				getConfigDataforNoCmd(parentVersion, key, counter++);
			}
		}
		if (configRequest.getLoopbackIPaddress() != null && parentVersion.getLoopbackIPaddress() != null) {
			if (!(configRequest.getLoopBackType().equalsIgnoreCase(parentVersion.getLoopBackType())
					&& configRequest.getLoopbackIPaddress().equalsIgnoreCase(parentVersion.getLoopbackIPaddress())
					&& configRequest.getLoopbackSubnetMask().equalsIgnoreCase(parentVersion.getLoopbackSubnetMask()))) {
				key = "loopback";
				// get the data
				getConfigData(parentVersion, key, counterCreate++);
			}
		}
		if (configRequest.getLoopbackIPaddress() != null && parentVersion.getLoopbackIPaddress() != null) {
			if (!(configRequest.getLoopBackType().equalsIgnoreCase(parentVersion.getLoopBackType())
					&& configRequest.getLoopbackIPaddress().equalsIgnoreCase(parentVersion.getLoopbackIPaddress())
					&& configRequest.getLoopbackSubnetMask().equalsIgnoreCase(parentVersion.getLoopbackSubnetMask()))) {
				key = "loopback";
				// get the data
				getConfigDataforNoCmd(parentVersion, key, counter++);
			}
		}

		String response = invokeFtl.generateModifyConfigurationToPush(latestVersion, compareVersion);

		String responseforNoCmd = invokeFtl.generateModifyConfigurationToPushNoCmd(latestVersion, compareVersion);
		String responseToTemplate = responseforNoCmd.concat(response);
		// if the type is template create,we are going to generate a file for it
		if (type.equalsIgnoreCase("templatecreate")) {
			try {
				TextReport.writeFile(C3PCoreAppLabels.RESPONSE_DOWNLOAD_PATH.getValue(),
						latestVersion.getRequestId() + "V" + latestVersion.getRequest_version() + "_Configuration",
						response);
				TextReport.writeFile(C3PCoreAppLabels.RESPONSE_DOWNLOAD_PATH.getValue(),
						latestVersion.getRequestId() + "V" + latestVersion.getRequest_version() + "_ConfigurationNoCmd",
						responseforNoCmd);

			} catch (Exception exe) {
				exe.printStackTrace();
			}
		}
		return responseToTemplate;
	}

}
