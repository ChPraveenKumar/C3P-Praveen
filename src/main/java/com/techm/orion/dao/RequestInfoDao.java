package com.techm.orion.dao;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import com.techm.orion.beans.RequestInfo;
import com.techm.orion.connection.ConnectionFactory;
import com.techm.orion.connection.DBUtil;
import com.techm.orion.entitybeans.BatchIdEntity;
import com.techm.orion.entitybeans.CertificationTestResultEntity;
import com.techm.orion.entitybeans.RequestInfoEntity;
import com.techm.orion.entitybeans.ServiceOrderEntity;
import com.techm.orion.entitybeans.TestDetail;
import com.techm.orion.entitybeans.TestRules;
import com.techm.orion.pojo.AlertInformationPojo;
import com.techm.orion.pojo.CertificationTestPojo;
import com.techm.orion.pojo.ColumnChartPojo;
import com.techm.orion.pojo.ConfigurationDataValuePojo;
import com.techm.orion.pojo.CreateConfigRequest;
import com.techm.orion.pojo.CreateConfigRequestDCM;
import com.techm.orion.pojo.DeviceInterfaceSO;
import com.techm.orion.pojo.EIPAMPojo;
import com.techm.orion.pojo.ElapsedTimeFormatPojo;
import com.techm.orion.pojo.ErrorValidationPojo;
import com.techm.orion.pojo.FirmwareUpgradeDetail;
import com.techm.orion.pojo.Global;
import com.techm.orion.pojo.Interface;
import com.techm.orion.pojo.InternetLCVRFType;
import com.techm.orion.pojo.InternetLcVrfSO;
import com.techm.orion.pojo.MISARPEType;
import com.techm.orion.pojo.MisArPeSO;
import com.techm.orion.pojo.ModifyConfigResultPojo;
import com.techm.orion.pojo.ReoprtFlags;
import com.techm.orion.pojo.RequestInfoPojo;
import com.techm.orion.pojo.RequestInfoSO;
import com.techm.orion.pojo.UserPojo;
import com.techm.orion.pojo.UserValidationResultDetailPojo;
import com.techm.orion.repositories.AlertInformationRepository;
import com.techm.orion.repositories.BatchInfoRepo;
import com.techm.orion.repositories.CreateConfigRepo;
import com.techm.orion.repositories.RequestDetailsBackUpAndRestoreRepo;
import com.techm.orion.repositories.RequestInfoDetailsRepositories;
import com.techm.orion.repositories.ServiceOrderRepo;
import com.techm.orion.rest.NetworkTestValidation;
import com.techm.orion.service.CertificationTestResultService;
import com.techm.orion.webService.GetAllDetailsService;

@Controller
public class RequestInfoDao {
	private static final Logger logger = LogManager.getLogger(RequestInfoDao.class);
	private Connection connection;
	Statement statement;
	List<ElapsedTimeFormatPojo> elapsedtimings;

	@Autowired
	public AlertInformationRepository alertInformationRepository;

	@Autowired
	CreateConfigRepo createConfigRepo;

	@Autowired
	RequestInfoDetailsRepositories reository;

	@Autowired
	public BatchInfoRepo batchInfoRepo;

	@Inject
	CertificationTestResultService certificationTestService;

	@Autowired
	ServiceOrderRepo serviceOrderRepo;

	public static String TSA_PROPERTIES_FILE = "TSA.properties";
	public static final Properties TSA_PROPERTIES = new Properties();

	public Map<String, String> insertRequestInDB(RequestInfoSO request) {
		Map<String, String> hmap = new HashMap<String, String>();
		String Os = null, banner = null, device_name = null, model = null, region = null, service = null,
				version = null, hostname = null, enablePassword = null, vrfName = null, alphaneumeric_req_id,
				customer = null, siteId = null, vendor = null, deviceType = null, vpn = null;
		String request_creator_name = null, snmpHostAddress = null, snmpString = null, loopBackType = null,
				loopbackIPaddress = null, loopbackSubnetMask = null, lanInterface = null, lanIp = null,
				lanMaskAddress = null, lanDescription = null, certificationSelectionBit = null;
		String managementIP = null, scheduledTime = null, templateId = null;
		String zipcode = null, managed = null, downtimerequired = null, lastupgradedon = null, networktype = null;
		double request_version = 0, request_parent_version = 0;
		boolean isAutoProgress;

		/* TimeZ Column added for Time Zone task */

		String sql = "INSERT INTO requestinfoso(Os,banner,device_name,model,region,service,os_version,hostname,enable_password,vrf_name,isAutoProgress,vendor,customer,siteid,managementIp,device_type,vpn,alphanumeric_req_id,request_status,request_version,request_parent_version,request_creator_name,snmpHostAddress,snmpString,loopBackType,loopbackIPaddress,loopbackSubnetMask,lanInterface,lanIp,lanMaskAddress,lanDescription,certificationSelectionBit,ScheduledTime,RequestType_Flag,TemplateIdUsed,RequestOwner,zipcode,managed,downtimeRequired,lastUpgradedOn,networktype)"
				+ "VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";

		connection = ConnectionFactory.getConnection();
		try {
			PreparedStatement ps = connection.prepareStatement(sql);
			if (request.getRequest_type().equalsIgnoreCase("IOSUPGRADE")
					&& request.getNetworkType().equalsIgnoreCase("Legacy")) {
				alphaneumeric_req_id = "SLGF-" + UUID.randomUUID().toString().toUpperCase();

			} else if (request.getRequest_type().equalsIgnoreCase("TS")
					&& request.getNetworkType().equalsIgnoreCase("Legacy")) {
				alphaneumeric_req_id = "SLGT-" + UUID.randomUUID().toString().toUpperCase();

			}

			else if (request.getRequest_type().equalsIgnoreCase("RESTCONF")
					&& request.getNetworkType().equalsIgnoreCase("VNF")) {
				alphaneumeric_req_id = "SNRC-" + UUID.randomUUID().toString().toUpperCase();

			} else if (request.getRequest_type().equalsIgnoreCase("NETCONF")
					&& request.getNetworkType().equalsIgnoreCase("VNF")) {
				alphaneumeric_req_id = "SNNC-" + UUID.randomUUID().toString().toUpperCase();

			} else if (request.getRequest_type().equalsIgnoreCase("SR")
					&& request.getNetworkType().equalsIgnoreCase("Legacy")) {
				alphaneumeric_req_id = "SLGC-" + UUID.randomUUID().toString().toUpperCase();

			} else if (request.getRequest_type().equalsIgnoreCase("SLGB")
					&& request.getNetworkType().equalsIgnoreCase("Legacy")) {
				alphaneumeric_req_id = "SLGB-" + UUID.randomUUID().toString().toUpperCase();
			} else {
				alphaneumeric_req_id = "SLGC-" + UUID.randomUUID().toString().toUpperCase();
			}
			// alphaneumeric_req_id = request.getProcessID();
			alphaneumeric_req_id = alphaneumeric_req_id.substring(0, 12);
			hmap.put("requestID", alphaneumeric_req_id);
			if (request.getOs() != null || request.getOs() != "") {
				Os = request.getOs();
			}

			if (request.getBanner() != null || request.getBanner() != "") {
				banner = request.getBanner();
			}

			if (request.getDeviceName() != null || request.getDeviceName() != "") {
				device_name = request.getDeviceName();
			}

			if (request.getModel() != null || request.getModel() != "") {
				model = request.getModel();
			}

			if (request.getRegion() != null || request.getRegion() != "") {
				region = request.getRegion();
			}

			if (request.getService() != null || request.getService() != "") {
				service = request.getService();
			}

			if (request.getOsVersion() != null || request.getOsVersion() != "") {
				version = request.getOsVersion();
			}
			if (request.getHostname() != null || request.getHostname() != "") {
				hostname = request.getHostname();
			}

			if (request.getEnablePassword() != null || request.getEnablePassword() != "") {
				enablePassword = request.getEnablePassword();
			}

			if (request.getVrfName() != null || request.getVrfName() != "") {
				vrfName = request.getVrfName();
			}

			if (request.getIsAutoProgress() != null) {
				isAutoProgress = request.getIsAutoProgress();
			} else {
				isAutoProgress = false;
			}
			if (request.getCustomer() != null || request.getCustomer() != "") {
				customer = request.getCustomer();
			}
			if (request.getSiteid() != null || request.getSiteid() != "") {
				siteId = request.getSiteid();
			}
			if (request.getVendor() != null || request.getVendor() != "") {
				vendor = request.getVendor();
			}
			if (request.getManagementIp() != null || request.getManagementIp() != "") {
				managementIP = request.getManagementIp();
			}
			if (request.getDeviceType() != null || request.getDeviceType() != "") {
				deviceType = request.getDeviceType();
			}
			if (request.getVpn() != null || request.getVpn() != "") {
				vpn = request.getVpn();
			}

			if (request.getRequest_version() != 0) {
				request_version = request.getRequest_version();
			}
			if (request.getRequest_parent_version() != 0) {
				request_parent_version = request.getRequest_parent_version();
			}
			if (request.getRequest_creator_name() != null) {
				request_creator_name = request.getRequest_creator_name();
			}

			if (request.getSnmpHostAddress() != null) {
				snmpHostAddress = request.getSnmpHostAddress();
			}
			if (request.getSnmpString() != null) {
				snmpString = request.getSnmpString();
			}
			if (request.getLoopBackType() != null) {
				loopBackType = request.getLoopBackType();
			}
			if (request.getLoopbackIPaddress() != null) {
				loopbackIPaddress = request.getLoopbackIPaddress();
			}
			if (request.getLoopbackSubnetMask() != null) {
				loopbackSubnetMask = request.getLoopbackSubnetMask();
			}
			if (request.getLanInterface() != null || request.getLanInterface() != "") {
				lanInterface = request.getLanInterface();
			}
			if (request.getLanIp() != null || request.getLanIp() != "") {
				lanIp = request.getLanIp();
			}
			if (request.getLanMaskAddress() != null || request.getLanMaskAddress() != "") {
				lanMaskAddress = request.getLanMaskAddress();
			}
			if (request.getLanDescription() != null || request.getLanDescription() != "") {
				lanDescription = request.getLanDescription();
			}
			if (request.getCertificationSelectionBit() != null || request.getCertificationSelectionBit() != "") {
				certificationSelectionBit = request.getCertificationSelectionBit();
			}

			if (request.getScheduledTime() != null || request.getScheduledTime() != "") {
				scheduledTime = request.getScheduledTime();
			}

			// template suggestion
			if (request.getTemplateId() != null || request.getTemplateId() != "") {
				templateId = request.getTemplateId();
			}
			if (request.getRequest_type().equalsIgnoreCase("IOSUPGRADE")) {
				zipcode = request.getZipcode();
				managed = request.getManaged();
				downtimerequired = request.getDownTimeRequired();
				lastupgradedon = request.getLastUpgradedOn();
			}
			if (request.getNetworkType() != null || request.getNetworkType() != "") {
				networktype = request.getNetworkType();
			} else {
				networktype = "Legacy";
			}
			if (request.getInternetLcVrf() != null) {
				String networkIp = null, AS = null, neighbor1 = null, neighbor2 = null, neighbor1_remoteAS = null,
						neighbor2_remoteAS = null, networkIp_subnetMask = null, routingProtocol = null;
				if (request.getInternetLcVrf().getNetworkIp() != null
						|| request.getInternetLcVrf().getNetworkIp() != "") {
					networkIp = request.getInternetLcVrf().getNetworkIp();
				}

				if (request.getInternetLcVrf().getBgpASNumber() != null
						|| request.getInternetLcVrf().getBgpASNumber() != "") {
					AS = request.getInternetLcVrf().getBgpASNumber();
				}

				if (request.getInternetLcVrf().getNeighbor1() != null
						|| request.getInternetLcVrf().getNeighbor1() != "") {
					neighbor1 = request.getInternetLcVrf().getNeighbor1();
				}

				if (request.getInternetLcVrf().getNeighbor2() != null
						|| request.getInternetLcVrf().getNeighbor2() != "") {
					neighbor2 = request.getInternetLcVrf().getNeighbor2();
				} else {
					neighbor2 = null;
				}
				if (request.getInternetLcVrf().getNeighbor1_remoteAS() != null
						|| request.getInternetLcVrf().getNeighbor1_remoteAS() != "") {
					neighbor1_remoteAS = request.getInternetLcVrf().getNeighbor1_remoteAS();
				}

				if (request.getInternetLcVrf().getNeighbor2_remoteAS() != null
						|| request.getInternetLcVrf().getNeighbor2_remoteAS() != "") {
					neighbor2_remoteAS = request.getInternetLcVrf().getNeighbor2_remoteAS();
				} else {
					neighbor2_remoteAS = null;
				}

				if (request.getInternetLcVrf().getNetworkIp_subnetMask() != null
						|| request.getInternetLcVrf().getNetworkIp_subnetMask() != "") {
					networkIp_subnetMask = request.getInternetLcVrf().getNetworkIp_subnetMask();
				}

				if (request.getInternetLcVrf().getRoutingProtocol() != null
						|| request.getInternetLcVrf().getRoutingProtocol() != "") {
					routingProtocol = request.getInternetLcVrf().getRoutingProtocol();
				}

				/* Code chanfe for Time Zone */

				/*
				 * if (request.getTimeZ()!= null || request.getTimeZ()!= "") {
				 * 
				 * ZonedDateTime now = ZonedDateTime.now();
				 * 
				 * DateTimeFormatter formatter = DateTimeFormatter.ofPattern("z");
				 * 
				 * TimeZ = now.format(formatter);
				 * 
				 * }
				 */

				String sql1 = "INSERT INTO internetlcvrfso(networkIp,asNumber,neighbor1,neighbor2,neighbor1_remoteAS,neighbor2_remoteAS,networkIp_subnetMask,routingProtocol) VALUES"
						+ "(?,?,?,?,?,?,?,?)";

				PreparedStatement ps1 = connection.prepareStatement(sql1);
				if (networkIp == "") {
					ps1.setString(1, networkIp);
				} else {
					ps1.setNull(1, java.sql.Types.VARCHAR);
				}
				if (AS != "") {
					ps1.setString(2, AS);
				} else {
					ps1.setNull(2, java.sql.Types.VARCHAR);
				}
				if (neighbor1 == "") {
					ps1.setString(3, neighbor1);
				} else {
					ps1.setNull(3, java.sql.Types.VARCHAR);
				}
				if (neighbor2 != "") {
					ps1.setString(4, neighbor2);
				} else {
					ps1.setNull(4, java.sql.Types.VARCHAR);
				}
				if (neighbor1_remoteAS != "") {
					ps1.setString(5, neighbor1_remoteAS);
				} else {
					ps1.setNull(5, java.sql.Types.VARCHAR);
				}

				if (neighbor2_remoteAS != "") {
					ps1.setString(6, neighbor2_remoteAS);
				} else {
					ps1.setNull(6, java.sql.Types.VARCHAR);
				}
				if (networkIp_subnetMask != "") {
					ps1.setString(7, networkIp_subnetMask);
				} else {
					ps1.setNull(7, java.sql.Types.VARCHAR);
				}
				if (routingProtocol != "") {
					ps1.setString(8, routingProtocol);
				} else {
					ps1.setNull(8, java.sql.Types.VARCHAR);
				}
				ps1.execute("SET FOREIGN_KEY_CHECKS=0");
				ps1.executeUpdate();
			} else {
				// String networkIp = null, remotePort = null, neighbor1 = null,
				// neighbor2 =null, neighbor3 =null, neighbor4 = null, neighbor5
				// = null, neighbor6 = null, routerBgp65k = null;

				String sql1 = "INSERT INTO internetlcvrfso(networkIp,asNumber,neighbor1,neighbor2,neighbor1_remoteAS,neighbor2_remoteAS,networkIp_subnetMask,routingProtocol) VALUES"
						+ "(?,?,?,?,?,?,?,?)";

				PreparedStatement ps1 = connection.prepareStatement(sql1);
				ps1.setNull(1, java.sql.Types.VARCHAR);
				ps1.setNull(2, java.sql.Types.VARCHAR);
				ps1.setNull(3, java.sql.Types.VARCHAR);
				ps1.setNull(4, java.sql.Types.VARCHAR);
				ps1.setNull(5, java.sql.Types.VARCHAR);
				ps1.setNull(6, java.sql.Types.VARCHAR);
				ps1.setNull(7, java.sql.Types.VARCHAR);
				ps1.setNull(8, java.sql.Types.VARCHAR);
				ps1.execute("SET FOREIGN_KEY_CHECKS=0");
				ps1.executeUpdate();
			}

			if (request.getMisArPeSO() != null) {

				String routerVrfVpnDIp = null, routerVrfVpnDGateway = null, fastEthernetIp = null;
				if (request.getMisArPeSO().getRouterVrfVpnDIp() != null
						|| request.getMisArPeSO().getRouterVrfVpnDIp() != "") {
					routerVrfVpnDIp = request.getMisArPeSO().getRouterVrfVpnDIp();
				}

				if (request.getMisArPeSO().getRouterVrfVpnDGateway() != null
						|| request.getMisArPeSO().getRouterVrfVpnDGateway() != "") {
					routerVrfVpnDGateway = request.getMisArPeSO().getRouterVrfVpnDGateway();
				}

				if (request.getMisArPeSO().getRouterVrfVpnDGateway() != null
						|| request.getMisArPeSO().getFastEthernetIp() != "") {
					fastEthernetIp = request.getMisArPeSO().getFastEthernetIp();
				}

				String sql1 = "INSERT INTO misarpeso(routerVrfVpnDIp, routerVrfVpnDGateway, fastEthernetIp) "
						+ "VALUES(?, ?, ?)";
				PreparedStatement ps1 = connection.prepareStatement(sql1);
				if (routerVrfVpnDIp != "") {
					ps1.setString(1, routerVrfVpnDIp);
				} else {
					ps1.setNull(1, java.sql.Types.VARCHAR);

				}
				if (routerVrfVpnDGateway != "") {
					ps1.setString(2, routerVrfVpnDGateway);
				} else {
					ps1.setNull(2, java.sql.Types.VARCHAR);

				}
				if (fastEthernetIp != "") {
					ps1.setString(3, fastEthernetIp);
				} else {
					ps.setNull(3, java.sql.Types.VARCHAR);

				}
				ps1.execute("SET FOREIGN_KEY_CHECKS=0");
				ps1.executeUpdate();

			} else {
				String routerVrfVpnDIp = "", routerVrfVpnDGateway = "", fastEthernetIp = "";
				String sql1 = "INSERT INTO misarpeso(routerVrfVpnDIp, routerVrfVpnDGateway, fastEthernetIp) "
						+ "VALUES(?, ?, ?)";
				PreparedStatement ps1 = connection.prepareStatement(sql1);
				if (routerVrfVpnDIp != "") {
					ps1.setString(1, routerVrfVpnDIp);
				} else {
					ps1.setNull(1, java.sql.Types.VARCHAR);

				}
				if (routerVrfVpnDGateway != "") {
					ps1.setString(2, routerVrfVpnDGateway);
				} else {
					ps1.setNull(2, java.sql.Types.VARCHAR);

				}
				if (fastEthernetIp != "") {
					ps1.setString(3, fastEthernetIp);
				} else {
					ps.setNull(3, java.sql.Types.VARCHAR);

				}
				ps1.execute("SET FOREIGN_KEY_CHECKS=0");
				ps1.executeUpdate();
			}

			if (request.getDeviceInterfaceSO() != null) {
				String name = null, description = null, ip = null, mask = null, speed = null, encapsulation = null,
						bandwidth = null;
				if (request.getDeviceInterfaceSO().getName() != null
						|| request.getDeviceInterfaceSO().getName() != "") {
					name = request.getDeviceInterfaceSO().getName();
				}

				if (request.getDeviceInterfaceSO().getDescription() != null
						|| request.getDeviceInterfaceSO().getDescription() != "") {
					description = request.getDeviceInterfaceSO().getDescription();
				}

				if (request.getDeviceInterfaceSO().getIp() != null || request.getDeviceInterfaceSO().getIp() != "") {
					ip = request.getDeviceInterfaceSO().getIp();
				}

				if (request.getDeviceInterfaceSO().getMask() != null
						|| request.getDeviceInterfaceSO().getMask() != "") {
					mask = request.getDeviceInterfaceSO().getMask();
				}

				if (request.getDeviceInterfaceSO().getSpeed() != null
						|| request.getDeviceInterfaceSO().getSpeed() != "") {
					speed = request.getDeviceInterfaceSO().getSpeed();
				}
				if (request.getDeviceInterfaceSO().getBandwidth() != null
						|| request.getDeviceInterfaceSO().getBandwidth() != "") {
					bandwidth = request.getDeviceInterfaceSO().getBandwidth();

				}

				if (request.getDeviceInterfaceSO().getEncapsulation() != null
						|| request.getDeviceInterfaceSO().getEncapsulation() != "") {
					encapsulation = request.getDeviceInterfaceSO().getEncapsulation();
				}

				String sql1 = "INSERT INTO deviceinterfaceso(name,description,ip,mask,speed,encapsulation,Bandwidth)"
						+ "VALUES(?, ?, ?, ?, ?, ?, ?)";

				PreparedStatement ps1 = connection.prepareStatement(sql1);
				if (name != "") {
					ps1.setString(1, name);
				} else {
					ps1.setNull(1, java.sql.Types.VARCHAR);
				}
				if (description != "") {
					ps1.setString(2, description);
				} else {
					ps1.setNull(2, java.sql.Types.VARCHAR);
				}
				if (ip != "") {
					ps1.setString(3, ip);
				} else {
					ps1.setNull(3, java.sql.Types.VARCHAR);
				}
				if (mask != "") {
					ps1.setString(4, mask);
				} else {
					ps1.setNull(4, java.sql.Types.VARCHAR);
				}
				if (speed == "") {
					ps1.setString(5, speed);
				} else {
					ps1.setNull(5, java.sql.Types.VARCHAR);
				}
				if (encapsulation != "") {
					ps1.setString(6, encapsulation);
				} else {
					ps1.setNull(6, java.sql.Types.VARCHAR);
				}
				if (bandwidth != "") {
					ps1.setString(7, bandwidth);
				} else {
					ps1.setNull(7, java.sql.Types.VARCHAR);
				}

				ps1.execute("SET FOREIGN_KEY_CHECKS=0");
				ps1.executeUpdate();
			} else {
				// String networkIp = null, remotePort = null, neighbor1 = null,
				// neighbor2 =null, neighbor3 =null, neighbor4 = null, neighbor5
				// = null, neighbor6 = null, routerBgp65k = null;

				String sql1 = "INSERT INTO deviceinterfaceso(name,description,ip,mask,speed,encapsulation)"
						+ "VALUES(?, ?, ?, ?, ?, ?)";
				PreparedStatement ps1 = connection.prepareStatement(sql1);
				ps1.setNull(1, java.sql.Types.VARCHAR);
				ps1.setNull(2, java.sql.Types.VARCHAR);
				ps1.setNull(3, java.sql.Types.VARCHAR);
				ps1.setNull(4, java.sql.Types.VARCHAR);
				ps1.setNull(5, java.sql.Types.VARCHAR);
				ps1.setNull(6, java.sql.Types.VARCHAR);
				ps1.setNull(7, java.sql.Types.VARCHAR);
				ps1.execute("SET FOREIGN_KEY_CHECKS=0");
				ps1.executeUpdate();
			}

			if (Os != "") {
				ps.setString(1, Os);
			} else {
				ps.setNull(1, java.sql.Types.VARCHAR);

			}

			// Logic to add banner text in new
			// table-----------------------------------------------------------------------------------------------------

			String sql1 = "INSERT INTO bannerdatatable(bannerdata)" + "VALUES(?)";

			PreparedStatement ps3 = connection.prepareStatement(sql1);
			if (banner != "") {
				ps3.setString(1, banner);
			} else {
				ps3.setNull(1, java.sql.Types.VARCHAR);
			}

			ps3.execute("SET FOREIGN_KEY_CHECKS=0");
			ps3.executeUpdate();
			// End of banner
			// logic------------------------------------------------------------------------------------------------------------------------

			if (banner != "") {
				ps.setString(2, banner);
			} else {
				ps.setNull(2, java.sql.Types.VARCHAR);

			}

			if (device_name != "") {
				ps.setString(3, device_name);
			} else {
				ps.setNull(3, java.sql.Types.VARCHAR);

			}
			if (model != "") {
				ps.setString(4, model);
			} else {
				ps.setNull(4, java.sql.Types.VARCHAR);

			}
			if (region != "") {
				ps.setString(5, region);
			} else {
				ps.setNull(5, java.sql.Types.VARCHAR);

			}
			if (service != "") {
				ps.setString(6, service);
			} else {
				ps.setNull(6, java.sql.Types.VARCHAR);

			}
			if (version != "") {
				ps.setString(7, version);
			} else {
				ps.setNull(7, java.sql.Types.VARCHAR);

			}
			if (hostname != "") {
				ps.setString(8, hostname);
			} else {
				ps.setNull(8, java.sql.Types.VARCHAR);

			}
			if (enablePassword != "") {
				ps.setString(9, enablePassword);
			} else {
				ps.setNull(9, java.sql.Types.VARCHAR);

			}
			if (vrfName != "") {
				ps.setString(10, vrfName);
			} else {
				ps.setNull(10, java.sql.Types.VARCHAR);

			}

			ps.setBoolean(11, isAutoProgress);

			if (vendor != "") {
				ps.setString(12, vendor);
			} else {
				ps.setNull(12, java.sql.Types.VARCHAR);

			}
			if (customer != "") {
				ps.setString(13, customer);
			} else {
				ps.setNull(13, java.sql.Types.VARCHAR);

			}

			if (siteId != "") {
				ps.setString(14, siteId);
			} else {
				ps.setNull(14, java.sql.Types.VARCHAR);

			}

			if (managementIP != "") {
				ps.setString(15, managementIP);
			} else {
				ps.setNull(15, java.sql.Types.VARCHAR);

			}
			if (deviceType != "") {
				ps.setString(16, deviceType);
			} else {
				ps.setNull(16, java.sql.Types.VARCHAR);

			}
			if (vpn != "") {
				ps.setString(17, vpn);
			} else {
				ps.setNull(17, java.sql.Types.VARCHAR);

			}
			if (alphaneumeric_req_id != "") {
				ps.setString(18, alphaneumeric_req_id);
			} else {
				ps.setNull(18, java.sql.Types.VARCHAR);

			}

			ps.setString(19, request.getStatus());
			if (request_version != 0) {
				ps.setDouble(20, request_version);
			} else {
				ps.setDouble(20, 0);

			}
			if (request_parent_version != 0) {
				ps.setDouble(21, request_parent_version);
			} else {
				ps.setDouble(21, 0);

			}
			if (request_creator_name != null) {
				ps.setString(22, request_creator_name);
			} else {
				ps.setNull(22, java.sql.Types.VARCHAR);

			}

			if (snmpHostAddress != null) {
				ps.setString(23, snmpHostAddress);
			} else {
				ps.setNull(23, java.sql.Types.VARCHAR);

			}
			if (snmpString != null) {
				ps.setString(24, snmpString);
			} else {
				ps.setNull(24, java.sql.Types.VARCHAR);

			}
			if (loopBackType != null) {
				ps.setString(25, loopBackType);
			} else {
				ps.setNull(25, java.sql.Types.VARCHAR);

			}
			if (loopbackIPaddress != null) {
				ps.setString(26, loopbackIPaddress);
			} else {
				ps.setNull(26, java.sql.Types.VARCHAR);

			}
			if (loopbackSubnetMask != null) {
				ps.setString(27, loopbackSubnetMask);
			} else {
				ps.setNull(27, java.sql.Types.VARCHAR);

			}
			if (lanInterface != null) {
				ps.setString(28, lanInterface);
			} else {
				ps.setNull(28, java.sql.Types.VARCHAR);

			}
			if (lanIp != null) {
				ps.setString(29, lanIp);
			} else {
				ps.setNull(29, java.sql.Types.VARCHAR);

			}
			if (lanMaskAddress != null) {
				ps.setString(30, lanMaskAddress);
			} else {
				ps.setNull(30, java.sql.Types.VARCHAR);

			}
			if (lanDescription != null) {
				ps.setString(31, lanDescription);
			} else {
				ps.setNull(31, java.sql.Types.VARCHAR);

			}
			if (certificationSelectionBit != null) {
				ps.setString(32, certificationSelectionBit);
			} else {
				ps.setNull(32, java.sql.Types.VARCHAR);

			}
			if (scheduledTime != null && scheduledTime != "") {

				SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");

				try {
					java.util.Date parsedDate = sdf.parse(scheduledTime);

					java.sql.Timestamp timestampTimeForScheduled = new java.sql.Timestamp(parsedDate.getTime());

					ps.setTimestamp(33, timestampTimeForScheduled);
					ps.setString(34, "S");
					/* ps.setString(37,TimeZ); *//* Added for TimeZ */

				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			} else {
				ps.setNull(33, java.sql.Types.TIMESTAMP);
				ps.setString(34, "M");
			}

			if (templateId != null) {
				ps.setString(35, templateId);
			} else {
				ps.setNull(35, java.sql.Types.VARCHAR);

			}
			if (request_creator_name != null) {
				ps.setString(36, request_creator_name);
			} else {
				ps.setNull(36, java.sql.Types.VARCHAR);

			}
			if (zipcode != null) {
				ps.setString(37, zipcode);

			} else {
				ps.setNull(37, java.sql.Types.VARCHAR);
			}
			if (managed != null) {
				ps.setString(38, managed);

			} else {
				ps.setNull(38, java.sql.Types.VARCHAR);

			}
			if (downtimerequired != null) {
				ps.setString(39, downtimerequired);

			} else {
				ps.setNull(39, java.sql.Types.VARCHAR);

			}
			if (lastupgradedon != null) {
				ps.setString(40, lastupgradedon);

			} else {
				ps.setNull(40, java.sql.Types.VARCHAR);

			}
			if (networktype != null) {
				ps.setString(41, networktype);

			} else {
				ps.setNull(41, java.sql.Types.VARCHAR);

			}
			int i = ps.executeUpdate();
			if (i == 1) {

				addRequestIDtoWebserviceInfo(alphaneumeric_req_id, Double.toString(request_version));
				addCertificationTestForRequest(alphaneumeric_req_id, Double.toString(request_version), "0");
				// add to OS_updgrade dilevary flag details table
				if (request.getRequest_type().equalsIgnoreCase("IOSUPGRADE")) {
					addRequestID_to_Os_Upgrade_dilevary_flags(alphaneumeric_req_id, Double.toString(request_version));
				}
				updateEIPAMTable(request.getDeviceInterfaceSO().getIp());
				hmap.put("result", "true");
				return hmap;

			}
		} catch (SQLException ex) {
			ex.printStackTrace();
		}

		hmap.put("result", "false");
		return hmap;
	}

	public int insertTestRecordInDB(String requestID, String testsSelected, String requestType) {
		int res = 0;
		connection = ConnectionFactory.getConnection();
		String sql = "INSERT INTO t_tststrategy_m_config_transaction(RequestId,TestsSelected,RequestType)"
				+ "VALUES(?,?,?)";
		try {
			PreparedStatement ps = connection.prepareStatement(sql);

			ps.setString(1, requestID);
			ps.setString(2, testsSelected);
			ps.setString(3, requestType);
			res = ps.executeUpdate();

		} catch (SQLException e) {

		}
		return res;
	}

	public final void addRequestID_to_Os_Upgrade_dilevary_flags(String requestId, String version) {
		connection = ConnectionFactory.getConnection();
		String sql = "INSERT INTO os_upgrade_dilevary_flags(request_id,request_version,login_flag,flash_size_flag,back_up_flag,os_download_flag,boot_system_flash_flag,reload_flag,post_login_flag)"
				+ "VALUES(?,?,?,?,?,?,?,?,?)";

		try {
			PreparedStatement ps = connection.prepareStatement(sql);

			ps.setString(1, requestId);
			ps.setString(2, version);
			ps.setInt(3, 0);
			ps.setInt(4, 0);
			ps.setInt(5, 0);
			ps.setInt(6, 0);
			ps.setInt(7, 0);
			ps.setInt(8, 0);
			ps.setInt(9, 0);
			int i = ps.executeUpdate();

		} catch (SQLException e) {

		}
	}

	public final List<RequestInfoSO> searchRequestsFromDB(String key, String value) throws IOException, ParseException {
		connection = ConnectionFactory.getConnection();
		String query = null;

		if (!Global.loggedInUser.equalsIgnoreCase("admin")) {
			if (key.equalsIgnoreCase("Request ID") || key.equalsIgnoreCase("Request")) {
				query = "SELECT * FROM requestinfo.requestinfoso WHERE concat(alphanumeric_req_id,concat('-v',request_version)) LIKE ? AND RequestOwner=?";
			} else if (key.equalsIgnoreCase("Region")) {
				query = "SELECT * FROM requestinfo.requestinfoso WHERE region LIKE ? AND RequestOwner=?";

			} else if (key.equalsIgnoreCase("Vendor")) {
				query = "SELECT * FROM requestinfo.requestinfoso WHERE vendor LIKE ? AND RequestOwner=?";

			} else if (key.equalsIgnoreCase("Model")) {
				query = "SELECT * FROM requestinfo.requestinfoso WHERE model LIKE ? AND RequestOwner=?";

			} else if (key.equalsIgnoreCase("Status")) {
				query = "SELECT * FROM requestinfo.requestinfoso WHERE request_status LIKE ? AND RequestOwner=?";

			}
		} else {
			if (key.equalsIgnoreCase("Request ID") || key.equalsIgnoreCase("Request")) {
				query = "SELECT * FROM requestinfo.requestinfoso WHERE concat(alphanumeric_req_id,concat('-v',request_version)) LIKE ?";
			} else if (key.equalsIgnoreCase("Region")) {
				query = "SELECT * FROM requestinfo.requestinfoso WHERE region LIKE ?";

			} else if (key.equalsIgnoreCase("Vendor")) {
				query = "SELECT * FROM requestinfo.requestinfoso WHERE vendor LIKE ?";

			} else if (key.equalsIgnoreCase("Model")) {
				query = "SELECT * FROM requestinfo.requestinfoso WHERE model LIKE ?";

			} else if (key.equalsIgnoreCase("Status")) {
				query = "SELECT * FROM requestinfo.requestinfoso WHERE request_status LIKE ?";

			}
		}
		ResultSet rs = null;
		RequestInfoSO request = null;
		List<RequestInfoSO> requestInfoList1 = null;
		PreparedStatement pst = null;
		try {

			pst = connection.prepareStatement(query);
			if (!Global.loggedInUser.equalsIgnoreCase("admin")) {
				pst.setString(1, value + "%");
				pst.setString(2, Global.loggedInUser);
			} else {
				pst.setString(1, value + "%");
			}
			rs = pst.executeQuery();
			requestInfoList1 = new ArrayList<RequestInfoSO>();

			int id;
			while (rs.next()) {
				request = new RequestInfoSO();
				id = (rs.getInt("request_info_id"));
				request.setOs(rs.getString("Os"));
				request.setBanner(rs.getString("banner"));
				request.setDeviceName(rs.getString("device_name"));
				request.setModel(rs.getString("model"));
				request.setRegion(rs.getString("region"));
				request.setService(rs.getString("service"));
				request.setHostname(rs.getString("hostname"));
				request.setOsVersion(rs.getString("os_version"));
				request.setEnablePassword(rs.getString("enable_password"));
				request.setVrfName(rs.getString("vrf_name"));
				request.setIsAutoProgress(rs.getBoolean("isAutoProgress"));

				Timestamp d = rs.getTimestamp("date_of_processing");

				request.setDateOfProcessing((covnertTStoString(d)));
				request.setVendor(rs.getString("vendor"));
				request.setCustomer(rs.getString("customer"));
				request.setSiteid(rs.getString("siteid"));
				request.setStatus(rs.getString("request_status"));
				request.setManagementIp(rs.getString("ManagementIP"));
				request.setDisplay_request_id(rs.getString("alphanumeric_req_id"));
				request.setDeviceType(rs.getString("device_type"));
				request.setVpn(rs.getString("vpn"));
				request.setRequest_id(rs.getInt("request_info_id"));
				request.setRequest_version(rs.getDouble("request_version"));
				request.setRequest_version_string(Double.toString(rs.getDouble("request_version")));
				request.setRequest_parent_version(rs.getDouble("request_parent_version"));
				request.setRequest_creator_name(rs.getString("request_creator_name"));
				request.setSnmpHostAddress(rs.getString("snmpHostAddress"));
				if (rs.getString("snmpHostAddress") == null) {
					request.setSnmpHostAddress("");
				}
				request.setSnmpString(rs.getString("snmpString"));
				if (rs.getString("snmpString") == null) {
					request.setSnmpString("");
				}
				request.setLoopBackType(rs.getString("loopBackType"));
				if (rs.getString("loopBackType") == null) {
					request.setLoopBackType("");
				}
				request.setLoopbackIPaddress(rs.getString("loopbackIPaddress"));
				if (rs.getString("loopbackIPaddress") == null) {
					request.setLoopbackIPaddress("");
				}
				request.setLoopbackSubnetMask(rs.getString("loopbackSubnetMask"));
				if (rs.getString("loopbackSubnetMask") == null) {
					request.setLoopbackSubnetMask("");
				}

				if (request.getStatus().equalsIgnoreCase("Success")
						|| request.getStatus().equalsIgnoreCase("success")) {
					request.setElapsed_time(rs.getString("request_elapsed_time"));
				} else if (request.getStatus().equalsIgnoreCase("Scheduled")) {
					SimpleDateFormat format = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
					Timestamp scheduledTime = rs.getTimestamp("ScheduledTime");

					/*
					 * Date d1_d = null; Date d2_d = null;
					 * 
					 * //d1_d = format.parse((covnertTStoString(d)));
					 * 
					 * d2_d = format.parse((covnertTStoString(d1)));
					 * 
					 * String scheduledTime = null; // in milliseconds //long diff = d2_d.getTime()
					 * - d1_d.getTime();
					 * 
					 * long diffSeconds = diff / 1000 % 60; long diffMinutes = diff / (60 * 1000) %
					 * 60; long diffHours = diff / (60 * 60 * 1000) % 24; long diffDays = diff / (24
					 * * 60 * 60 * 1000);
					 * 
					 * long dayTohours = diffDays * 24;
					 * 
					 * DecimalFormat formatter = new DecimalFormat("00"); String sec =
					 * formatter.format(diffSeconds); String min = formatter.format(diffMinutes);
					 * String hrs = formatter.format(diffHours + dayTohours);
					 * 
					 * scheduledTime = hrs + ":" + min + ":" + sec;
					 */
					if (scheduledTime != null) {
						request.setScheduledTime(covnertTStoString(scheduledTime));
					}

				} else {
					request.setElapsed_time(rs.getString("request_elapsed_time"));

				}

				String subQueryOne = "select*from misarpeso where request_info_id=" + id;
				Statement smt = connection.createStatement();
				ResultSet rsnew = smt.executeQuery(subQueryOne);

				if (rsnew != null) {
					MisArPeSO mis = new MisArPeSO();
					while (rsnew.next()) {
						mis.setFastEthernetIp(rsnew.getString("fastEthernetIp"));
						mis.setRouterVrfVpnDGateway(rsnew.getString("routerVrfVpnDGateway"));
						mis.setRouterVrfVpnDIp(rsnew.getString("routerVrfVpnDIp"));
					}
					request.setMisArPeSO(mis);
				}

				String subQueryTwo = "select*from internetlcvrfso where request_info_id=" + id;
				Statement smt1 = connection.createStatement();
				ResultSet rsnew1 = smt1.executeQuery(subQueryTwo);

				if (rsnew1 != null) {
					InternetLcVrfSO iis = new InternetLcVrfSO();
					while (rsnew1.next()) {
						iis.setNetworkIp(rsnew1.getString("networkIp"));
						iis.setBgpASNumber(rsnew1.getString("asNumber"));
						iis.setNeighbor1(rsnew1.getString("neighbor1"));
						iis.setNeighbor2(rsnew1.getString("neighbor2"));
						iis.setNeighbor1_remoteAS(rsnew1.getString("neighbor1_remoteAS"));
						iis.setNeighbor2_remoteAS(rsnew1.getString("neighbor2_remoteAS"));
						iis.setRoutingProtocol(rsnew1.getString("routingProtocol"));
						iis.setNetworkIp_subnetMask(rsnew1.getString("networkIp_subnetMask"));

					}
					request.setInternetLcVrf(iis);
				}

				String subQueryThree = "select*from deviceinterfaceso where request_info_id=" + id;
				Statement smt3 = connection.createStatement();
				ResultSet rsnew3 = smt3.executeQuery(subQueryThree);

				if (rsnew3 != null) {
					DeviceInterfaceSO iisd = new DeviceInterfaceSO();
					while (rsnew3.next()) {
						iisd.setDescription(rsnew3.getString("description"));
						iisd.setIp(rsnew3.getString("ip"));
						iisd.setEncapsulation(rsnew3.getString("encapsulation"));
						iisd.setMask(rsnew3.getString("mask"));
						iisd.setName(rsnew3.getString("name"));
						iisd.setSpeed(rsnew3.getString("speed"));
						iisd.setBandwidth(rsnew3.getString("Bandwidth"));
					}
					request.setDeviceInterfaceSO(iisd);

					if (rs.getString("RequestOwner") != null) {
						request.setRequest_assigned_to(rs.getString("RequestOwner"));

					} else {
						request.setRequest_assigned_to("");

					}

					requestInfoList1.add(request);
				}

			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			DBUtil.close(rs);
			DBUtil.close(statement);
			DBUtil.close(connection);
		}
		/*
		 * for (int i = 0; i < requestInfoList1.size(); i++) { //
		 * logger.info(""+requestInfoList1.get(i).getRequestId()); logger.info("" +
		 * requestInfoList1.get(i).getDeviceName()); logger.info("" +
		 * requestInfoList1.get(i).getMisArPeSO() .getFastEthernetIp()); System.out
		 * .println("" + requestInfoList1.get(i).getInternetLcVrf() .getNetworkIp()); }
		 */
		return requestInfoList1;

	}

	public final List<RequestInfo> getAllRequestInfoData() throws IOException {
		connection = ConnectionFactory.getConnection();
		String query = "SELECT * FROM requestinfoso";
		ResultSet rs = null;
		RequestInfoSO requestInfoObj = null;
		List<RequestInfoSO> requestInfoList1 = null;
		try {

			statement = connection.createStatement();
			rs = statement.executeQuery(query);
			requestInfoList1 = new ArrayList<RequestInfoSO>();

			int id;
			while (rs.next()) {
				requestInfoObj = new RequestInfoSO();
				id = (rs.getInt("request_info_id"));
				requestInfoObj.setOs(rs.getString("Os"));
				requestInfoObj.setBanner(rs.getString("banner"));
				requestInfoObj.setDeviceName(rs.getString("device_name"));
				requestInfoObj.setModel(rs.getString("model"));
				requestInfoObj.setRegion(rs.getString("region"));
				requestInfoObj.setService(rs.getString("service"));
				requestInfoObj.setHostname(rs.getString("hostname"));
				requestInfoObj.setOsVersion(rs.getString("os_version"));
				requestInfoObj.setEnablePassword(rs.getString("enable_password"));
				requestInfoObj.setVrfName(rs.getString("vrf_name"));
				requestInfoObj.setIsAutoProgress(rs.getBoolean("isAutoProgress"));
				Timestamp d = rs.getTimestamp("date_of_processing");
				requestInfoObj.setDateOfProcessing((covnertTStoString(d)));
				requestInfoObj.setVendor(rs.getString("vendor"));
				requestInfoObj.setCustomer(rs.getString("customer"));
				requestInfoObj.setSiteid(rs.getString("siteid"));
				requestInfoObj.setStatus(rs.getString("request_status"));
				requestInfoObj.setManagementIp(rs.getString("ManagementIP"));
				requestInfoObj.setDisplay_request_id(rs.getString("alphanumeric_req_id"));

				requestInfoObj.setRequest_id(rs.getInt("request_info_id"));

				String subQueryOne = "select*from misarpeso where request_info_id=" + id;
				Statement smt = connection.createStatement();
				ResultSet rsnew = smt.executeQuery(subQueryOne);

				if (rsnew != null) {
					MisArPeSO mis = new MisArPeSO();
					while (rsnew.next()) {
						mis.setFastEthernetIp(rsnew.getString("fastEthernetIp"));
						mis.setRouterVrfVpnDGateway(rsnew.getString("routerVrfVpnDGateway"));
						mis.setRouterVrfVpnDIp(rsnew.getString("routerVrfVpnDIp"));
					}
					requestInfoObj.setMisArPeSO(mis);
				}

				String subQueryTwo = "select*from internetlcvrfso where request_info_id=" + id;
				Statement smt1 = connection.createStatement();
				ResultSet rsnew1 = smt1.executeQuery(subQueryTwo);

				if (rsnew1 != null) {
					InternetLcVrfSO iis = new InternetLcVrfSO();
					while (rsnew1.next()) {
						iis.setNetworkIp(rsnew1.getString("networkIp"));
						iis.setBgpASNumber(rsnew1.getString("asNumber"));
						iis.setNeighbor1(rsnew1.getString("neighbor1"));
						iis.setNeighbor2(rsnew1.getString("neighbor2"));
						iis.setNeighbor1_remoteAS(rsnew1.getString("neighbor1_remoteAS"));
						iis.setNeighbor2_remoteAS(rsnew1.getString("neighbor2_remoteAS"));
						iis.setRoutingProtocol(rsnew1.getString("routingProtocol"));

					}
					requestInfoObj.setInternetLcVrf(iis);
				}

				requestInfoList1.add(requestInfoObj);
			}

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			DBUtil.close(rs);
			DBUtil.close(statement);
			DBUtil.close(connection);
		}
		/*
		 * for (int i = 0; i < requestInfoList1.size(); i++) { //
		 * logger.info(""+requestInfoList1.get(i).getRequestId()); logger.info("" +
		 * requestInfoList1.get(i).getDateOfProcessing()); logger.info("" +
		 * requestInfoList1.get(i).getRequest_id());
		 * 
		 * }
		 */

		List<RequestInfo> requestInfoList = new ArrayList<RequestInfo>();
		GetAllDetailsService gads = new GetAllDetailsService();
		String response = gads.jsonResponseString();

		JSONObject jsonObject = new JSONObject(response);
		JSONArray tsmresponse = (JSONArray) jsonObject.get("requestsDetail");

		for (int i = 0; i < tsmresponse.length(); i++) {
			RequestInfo requestInfo = new RequestInfo();
			requestInfo.setRequestId(tsmresponse.getJSONObject(i).getString("requestId"));
			requestInfo.setCustomerName(tsmresponse.getJSONObject(i).getString("customer"));
			requestInfo.setDeviceName(tsmresponse.getJSONObject(i).getString("deviceName"));
			requestInfo.setModel(tsmresponse.getJSONObject(i).getString("model"));
			requestInfo.setConfig_req_status(tsmresponse.getJSONObject(i).getString("status"));
			requestInfo.setDateProcessedString(tsmresponse.getJSONObject(i).getString("date"));
			requestInfoList.add(requestInfo);
		}
		logger.info("request>>>>>>>>>>>" + requestInfoList);
		return requestInfoList;
	}

	public boolean addRequestIDtoWebserviceInfo(String alphanumeric_req_id, String request_version) {
		connection = ConnectionFactory.getConnection();
		String sql = "INSERT INTO webserviceInfo(start_test,generate_config,deliever_config,health_checkup,network_test,application_test,customer_report,filename,latencyResultRes,alphanumeric_req_id,version,pre_health_checkup,others_test)"
				+ "VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?)";

		try {
			PreparedStatement ps = connection.prepareStatement(sql);

			ps.setInt(1, 1);
			ps.setInt(2, 1);
			ps.setInt(3, 0);
			ps.setInt(4, 0);
			ps.setInt(5, 0);
			ps.setInt(6, 0);
			ps.setInt(7, 0);
			ps.setInt(8, 0);
			ps.setInt(9, 0);
			ps.setString(10, alphanumeric_req_id);
			ps.setString(11, request_version);
			ps.setInt(12, 0);
			ps.setInt(13, 0);
			int i = ps.executeUpdate();
			if (i == 1) {

				return true;

			}
		} catch (SQLException e) {

		}

		return false;
	}

	public boolean checkDB(String requestId) {
		connection = ConnectionFactory.getConnection();
		String query = "SELECT * FROM webserviceInfo";
		// String query =
		// "INSERT INTO request_detail(request_id, request_date,
		// request_status,request_model,request_device_name,request_source) VALUES(2,
		// 00/00/0000,'Success','8888','CISCO','SOAP')";

		ResultSet rs = null;
		ReoprtFlags flags = null;
		List<ReoprtFlags> InfoList1 = null;

		try {

			statement = connection.createStatement();
			rs = statement.executeQuery(query);
			InfoList1 = new ArrayList<ReoprtFlags>();
			logger.info("" + rs.getFetchSize());

			if (rs != null) {
				while (rs.next()) {
					flags = new ReoprtFlags();
					flags.setRequestId(rs.getString("request_id"));
					flags.setStart_test(rs.getInt("start_test"));
					flags.setNetwork_test(rs.getInt("network_test"));
					flags.setHealth_checkup(rs.getInt("health_checkup"));
					flags.setGenerate_config(rs.getInt("generate_config"));
					flags.setDeliever_config(rs.getInt("deliever_config"));
					flags.setCustomer_report(rs.getInt("customer_report"));
					flags.setApplication_test(rs.getInt("application_test"));
					flags.setLatencyResultRes(rs.getInt("latencyResultRes"));
					flags.setFilename(rs.getInt("filename"));

					InfoList1.add(flags);
				}

				if (InfoList1.size() == 0) {
					return false;
				} else {
					for (int i = 0; i < InfoList1.size(); i++) {
						if (InfoList1.get(i).getRequestId().equalsIgnoreCase(requestId)) {
							return true;
						}
					}
				}
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {

		}
		return false;
	}

	public List<ReoprtFlags> getReportsInfoForAllRequestsDB() {
		connection = ConnectionFactory.getConnection();
		String query = "SELECT * FROM webserviceInfo";

		ResultSet rs = null;
		ReoprtFlags flags = null;
		List<ReoprtFlags> InfoList1 = null;

		try {

			statement = connection.createStatement();
			rs = statement.executeQuery(query);
			InfoList1 = new ArrayList<ReoprtFlags>();

			if (rs != null) {
				while (rs.next()) {
					flags = new ReoprtFlags();

					flags.setRequestId(rs.getString("request_id"));
					flags.setStart_test(rs.getInt("start_test"));
					flags.setNetwork_test(rs.getInt("network_test"));
					flags.setHealth_checkup(rs.getInt("health_checkup"));
					flags.setGenerate_config(rs.getInt("generate_config"));
					flags.setDeliever_config(rs.getInt("deliever_config"));
					flags.setCustomer_report(rs.getInt("customer_report"));
					flags.setApplication_test(rs.getInt("application_test"));
					flags.setLatencyResultRes(rs.getInt("latencyResultRes"));
					flags.setAlphanumeric_req_id(rs.getString("alphanumeric_req_id"));
					flags.setFilename(rs.getInt("filename"));
					flags.setPre_health_checkup(rs.getInt("pre_health_checkup"));
					flags.setOthers_test(rs.getInt("others_test"));
					flags.setNetwork_audit(rs.getInt("network_audit"));
					InfoList1.add(flags);
				}
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			DBUtil.close(rs);
			DBUtil.close(statement);
			DBUtil.close(connection);
		}
		return InfoList1;
	}

	public List<RequestInfoSO> getCertificationtestvalidation(String value) {
		connection = ConnectionFactory.getConnection();
		String query = "SELECT * FROM requestinfoso where alphanumeric_req_id =?";

		ResultSet rs = null;

		RequestInfoSO request = null;
		PreparedStatement pst = null;
		List<RequestInfoSO> requestInfoList = null;
		try {
			pst = connection.prepareStatement(query);
			pst.setString(1, value);

			rs = pst.executeQuery();
			requestInfoList = new ArrayList<RequestInfoSO>();

			if (rs != null) {
				while (rs.next()) {
					request = new RequestInfoSO();
					request.setRequest_id(rs.getInt("request_info_id"));
					// request.set(rs.getString("alphanumeric_req_id"));
					request.setCertificationSelectionBit(rs.getString("certificationSelectionBit"));

					request.setRequest_version(rs.getDouble("request_version"));

					requestInfoList.add(request);
				}
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			DBUtil.close(rs);
			DBUtil.close(statement);
			DBUtil.close(connection);
		}
		return requestInfoList;
	}

	public void editRequestForReportIOSWebserviceInfo(String requestId, String version, String textFound_dileverytest,
			String errorStatus_dilevarytest, String errorDescription_dilevarytest) {
		connection = ConnectionFactory.getConnection();
		String query = null;
		String query1 = null;
		query = "update webserviceInfo set TextFound_DeliveryTest = ?,ErrorStatus_DeliveryTest=?,ErrorDescription_DeliveryTest=? where alphanumeric_req_id = ? and version = ? ";
		PreparedStatement preparedStmt;
		try {
			preparedStmt = connection.prepareStatement(query);

			preparedStmt.setString(1, textFound_dileverytest);
			preparedStmt.setString(2, errorStatus_dilevarytest);
			preparedStmt.setString(3, errorDescription_dilevarytest);
			preparedStmt.setString(4, requestId);
			preparedStmt.setString(5, version);

			preparedStmt.executeUpdate();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void editRequestforReportWebserviceInfo(String requestId, String version, String field, String flag,
			String status) {
		connection = ConnectionFactory.getConnection();
		String query = null;
		String query1 = null;
		String query2 = null;

		if (field.equalsIgnoreCase("health_check")) {
			query = "update webserviceInfo set health_checkup = ? where alphanumeric_req_id = ? and version = ? ";
		} else if (field.equalsIgnoreCase("deliver_configuration")) {
			query = "update webserviceInfo set deliever_config = ? where alphanumeric_req_id = ? and version = ? ";

		} else if (field.equalsIgnoreCase("network_test")) {
			query = "update webserviceInfo set network_test = ? where alphanumeric_req_id = ? and version = ? ";
		} else if (field.equalsIgnoreCase("deliever_config")) {
			query = "update webserviceInfo set deliever_config = ? where alphanumeric_req_id = ? and version = ? ";
		}

		else if (field.equalsIgnoreCase("Application_test")) {
			query = "update webserviceInfo set application_test = ? where alphanumeric_req_id = ? and version = ? ";

		} else if (field.equalsIgnoreCase("customer_report")) {
			query = "update webserviceInfo set customer_report = ? where alphanumeric_req_id = ? and version = ? ";

		} else if (field.equalsIgnoreCase("generate_configuration")) {
			query = "update webserviceInfo set generate_config = ? where alphanumeric_req_id = ? and version = ? ";
		} else if (field.equalsIgnoreCase("pre_health_checkup")) {
			query = "update webserviceInfo set pre_health_checkup = ? where alphanumeric_req_id = ? and version = ? ";

		} else if (field.equalsIgnoreCase("others_test")) {
			query = "update webserviceInfo set others_test = ? where alphanumeric_req_id = ? and version = ? ";

		} else if (field.equalsIgnoreCase("network_audit")) {
			query = "update webserviceInfo set network_audit = ? where alphanumeric_req_id = ? and version = ? ";

		}

		PreparedStatement preparedStmt;
		try {
			preparedStmt = connection.prepareStatement(query);

			preparedStmt.setString(1, flag);
			preparedStmt.setString(2, requestId);
			preparedStmt.setString(3, version);
			preparedStmt.executeUpdate();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// query1 =
		// "update requestinfoso set request_status = ? where alphanumeric_req_id = ?";
		if (field.equalsIgnoreCase("customer_report") && status.contains("Success")) {
			String query0 = "select * from requestinfoso where alphanumeric_req_id = ? and request_version= ?";
			query1 = "update requestinfoso set request_status = ?,end_date_of_processing = ?,request_elapsed_time=? where alphanumeric_req_id = ? and request_version= ?";
			try {
				PreparedStatement preparedStmt1, preparedStmt0;
				ResultSet rs = null;
				java.util.Date date = new java.util.Date();
				java.sql.Timestamp timestamp = new java.sql.Timestamp(date.getTime());
				Timestamp d = null;
				preparedStmt0 = connection.prepareStatement(query0);
				preparedStmt0.setString(1, requestId);
				preparedStmt0.setString(2, version);
				rs = preparedStmt0.executeQuery();

				while (rs.next()) {
					if (rs.getString("temp_elapsed_time") == null) {
						if (rs.getString("RequestType_Flag").equalsIgnoreCase("M")) {
							d = rs.getTimestamp("date_of_processing");

						} else {
							d = rs.getTimestamp("ScheduledTime");
						}

						String diff = calcTimeDiffInMins(timestamp, d);

						preparedStmt1 = connection.prepareStatement(query1);
						preparedStmt1.setString(1, status);
						// preparedStmt1.setTimestamp(2, date);
						preparedStmt1.setTimestamp(2, timestamp);
						preparedStmt1.setString(3, diff);
						preparedStmt1.setString(4, requestId);
						preparedStmt1.setString(5, version);
						preparedStmt1.executeUpdate();
					} else {
						Timestamp d1 = null;
						date = new java.util.Date();
						timestamp = new java.sql.Timestamp(date.getTime());
						d1 = rs.getTimestamp("temp_processing_time");
						String diff1 = calcTimeDiffInMins(timestamp, d1);

						String diff2 = String.format("%.2f", Float.toString(
								(Float.parseFloat(diff1) + Float.parseFloat(rs.getString("temp_elapsed_time")))));
						// String query1 =
						// "update requestinfoso set temp_elapsed_time = ? where alphanumeric_req_id = ?
						// and request_version= ?";

						preparedStmt1 = connection.prepareStatement(query1);
						preparedStmt1.setString(1, status);
						preparedStmt1.setTimestamp(2, timestamp);
						preparedStmt1.setString(3, diff2);
						preparedStmt1.setString(4, requestId);
						preparedStmt1.setString(5, version);
						preparedStmt1.executeUpdate();
					}
				}

			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();

			}
			ServiceOrderEntity ent = serviceOrderRepo.findByRequestId(requestId);
			if (ent != null) {
				serviceOrderRepo.updateStatusAndRequestId(requestId, "Success", ent.getServiceOrder());
			}
		} else if (field.equalsIgnoreCase("customer_report") && status.equals("Failure")) {
			String query0 = "select * from requestinfoso where alphanumeric_req_id = ? and request_version= ?";
			query1 = "update requestinfoso set request_status = ?,end_date_of_processing = ?,request_elapsed_time=? where alphanumeric_req_id = ? and request_version= ?";
			try {
				PreparedStatement preparedStmt1, preparedStmt0;
				ResultSet rs = null;
				java.util.Date date = new java.util.Date();
				java.sql.Timestamp timestamp = new java.sql.Timestamp(date.getTime());
				Timestamp d = null;
				preparedStmt0 = connection.prepareStatement(query0);
				preparedStmt0.setString(1, requestId);
				preparedStmt0.setString(2, version);
				rs = preparedStmt0.executeQuery();
				if (rs != null) {
					while (rs.next()) {
						d = rs.getTimestamp("temp_processing_time");
					}
				}

				preparedStmt1 = connection.prepareStatement(query1);
				preparedStmt1.setString(1, status);
				// preparedStmt1.setTimestamp(2, date);
				preparedStmt1.setTimestamp(2, timestamp);
				preparedStmt1.setString(3, "0");
				preparedStmt1.setString(4, requestId);
				preparedStmt1.setString(5, version);
				preparedStmt1.executeUpdate();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();

			}
			ServiceOrderEntity ent = serviceOrderRepo.findByRequestId(requestId);
			if (ent != null) {
				serviceOrderRepo.updateStatusAndRequestId(requestId, "Failure", ent.getServiceOrder());
			}
		} else {

			query1 = "update requestinfoso set request_status = ?,end_date_of_processing = ?,request_elapsed_time=? where alphanumeric_req_id = ? and request_version= ?";
			try {
				java.util.Date date = new java.util.Date();
				java.sql.Timestamp timestamp = new java.sql.Timestamp(date.getTime());
				Timestamp d = null;
				ResultSet rs = null;
				PreparedStatement preparedStmt1, preparedStmt0;

				preparedStmt1 = connection.prepareStatement(query1);
				preparedStmt1.setString(1, status);
				// preparedStmt1.setTimestamp(2, date);
				preparedStmt1.setTimestamp(2, timestamp);
				preparedStmt1.setString(3, "0");
				preparedStmt1.setString(4, requestId);
				preparedStmt1.setString(5, version);
				preparedStmt1.executeUpdate();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();

			}
		}

	}

	public final List<RequestInfoSO> getAllResquestsFromDB() throws IOException {
		connection = ConnectionFactory.getConnection();
		String query = null;
		if (Global.loggedInUser.equalsIgnoreCase("feuser")) {
			query = "SELECT * FROM requestinfoso WHERE request_status NOT IN('Cancelled') and RequestOwner=? and alphanumeric_req_id rlike'SLGC|SLGF|SLGT|SNRC|SNNC|SNNA|SLGB'";
		} else if (Global.loggedInUser.equalsIgnoreCase("seuser")) {
			query = "SELECT * FROM requestinfoso WHERE request_status NOT IN('Cancelled') and request_creator_name=? and alphanumeric_req_id rlike'SLGC|SLGF|SLGT|SNRC|SNNC|SNNA|SLGB'";

		} else if (Global.loggedInUser.equalsIgnoreCase("admin")) {
			// query =
			// "SELECT * FROM requestinfoso WHERE request_status NOT IN('Cancelled') and
			// alphanumeric_req_id rlike'SR|OS'";
			query = "SELECT * FROM requestinfoso WHERE (request_status NOT IN('Cancelled') AND import_status IS NULL) OR import_status IN('Success')";
		}
		// String query =
		// "INSERT INTO request_detail(request_id, request_date,
		// request_status,request_model,request_device_name,request_source) VALUES(2,
		// 00/00/0000,'Success','8888','CISCO','SOAP')";

		ResultSet rs = null;
		RequestInfoSO request = null;
		List<RequestInfoSO> requestInfoList1 = null;
		PreparedStatement statement = null;
		try {
			if (!Global.loggedInUser.equalsIgnoreCase("admin")) {
				statement = connection.prepareStatement(query);

				statement.setString(1, Global.loggedInUser);
				rs = statement.executeQuery();
			} else {
				Statement smt = connection.createStatement();
				rs = smt.executeQuery(query);
			}

			requestInfoList1 = new ArrayList<RequestInfoSO>();
			int id;
			while (rs.next()) {

				request = new RequestInfoSO();
				id = (rs.getInt("request_info_id"));

				String type = rs.getString("alphanumeric_req_id").substring(0,
						Math.min(rs.getString("alphanumeric_req_id").length(), 4));

				if (!(type.equals("SLGB"))) {

					request.setOs(rs.getString("Os"));
					request.setBanner(rs.getString("banner"));
					request.setDeviceName(rs.getString("device_name"));
					request.setModel(rs.getString("model"));
					request.setRegion(rs.getString("region"));
					request.setService(rs.getString("service"));
					request.setHostname(rs.getString("hostname"));
					request.setOsVersion(rs.getString("os_version"));
					request.setEnablePassword(rs.getString("enable_password"));
					request.setVrfName(rs.getString("vrf_name"));
					request.setIsAutoProgress(rs.getBoolean("isAutoProgress"));
					Timestamp d = rs.getTimestamp("date_of_processing");
					request.setDateOfProcessing((covnertTStoString(d)));
					request.setVendor(rs.getString("vendor"));
					request.setCustomer(rs.getString("customer"));
					request.setSiteid(rs.getString("siteid"));
					request.setStatus(rs.getString("request_status"));
					request.setManagementIp(rs.getString("managementIp"));
					request.setDisplay_request_id(rs.getString("alphanumeric_req_id"));

					request.setImportsource(rs.getString("importsource"));

					request.setDeviceType(rs.getString("device_type"));
					request.setVpn(rs.getString("vpn"));
					request.setRequest_id(rs.getInt("request_info_id"));
					request.setRequest_version(rs.getDouble("request_version"));
					request.setRequest_parent_version(rs.getDouble("request_parent_version"));
					request.setRequest_creator_name(rs.getString("request_creator_name"));
					request.setElapsed_time(rs.getString("request_elapsed_time"));

					Timestamp d1 = rs.getTimestamp("end_date_of_processing");
					if (d1 != null) {
						request.setEndDateofProcessing(covnertTStoString(d1));
					}

					String subQueryOne = "select*from misarpeso where request_info_id=" + id;
					Statement smt = connection.createStatement();
					ResultSet rsnew = smt.executeQuery(subQueryOne);

					if (rsnew != null) {
						MisArPeSO mis = new MisArPeSO();
						while (rsnew.next()) {
							mis.setFastEthernetIp(rsnew.getString("fastEthernetIp"));
							mis.setRouterVrfVpnDGateway(rsnew.getString("routerVrfVpnDGateway"));
							mis.setRouterVrfVpnDIp(rsnew.getString("routerVrfVpnDIp"));
						}
						request.setMisArPeSO(mis);
					}

					String subQueryTwo = "select*from internetlcvrfso where request_info_id=" + id;
					Statement smt1 = connection.createStatement();
					ResultSet rsnew1 = smt1.executeQuery(subQueryTwo);

					if (rsnew1 != null) {
						InternetLcVrfSO iis = new InternetLcVrfSO();
						while (rsnew1.next()) {
							iis.setNetworkIp(rsnew1.getString("networkIp"));
							iis.setBgpASNumber(rsnew1.getString("asNumber"));
							iis.setNeighbor1(rsnew1.getString("neighbor1"));
							iis.setNeighbor2(rsnew1.getString("neighbor2"));
							iis.setNeighbor1_remoteAS(rsnew1.getString("neighbor1_remoteAS"));
							iis.setNeighbor2_remoteAS(rsnew1.getString("neighbor2_remoteAS"));
							iis.setRoutingProtocol(rsnew1.getString("routingProtocol"));

						}
						request.setInternetLcVrf(iis);
					}

					String subQueryThree = "select*from deviceinterfaceso where request_info_id=" + id;
					Statement smt3 = connection.createStatement();
					ResultSet rsnew3 = smt3.executeQuery(subQueryThree);

					if (rsnew3 != null) {
						DeviceInterfaceSO iisd = new DeviceInterfaceSO();
						while (rsnew3.next()) {
							iisd.setDescription(rsnew3.getString("description"));
							iisd.setIp(rsnew3.getString("ip"));
							iisd.setEncapsulation(rsnew3.getString("encapsulation"));
							iisd.setMask(rsnew3.getString("mask"));
							iisd.setName(rsnew3.getString("name"));
							iisd.setSpeed(rsnew3.getString("speed"));
							iisd.setBandwidth(rsnew3.getString("Bandwidth"));
						}
						request.setDeviceInterfaceSO(iisd);
						request.setRequest_assigned_to(rs.getString("RequestOwner"));
					}
					requestInfoList1.add(request);

				}
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			DBUtil.close(rs);
			DBUtil.close(statement);
			DBUtil.close(connection);
		}

		return requestInfoList1;

	}

	public final EIPAMPojo getIPAMIPfromDB(String site, String customer, String service, String region)
			throws IOException {
		connection = ConnectionFactory.getConnection();
		String ip = null;
		String query = "SELECT * FROM requestinfo.eipamdbtable WHERE eipam_site_id = ? AND eipam_customer_name=? AND eipam_service=? AND eipam_region=?";

		ResultSet rs = null;
		EIPAMPojo eipamobj = null;

		PreparedStatement pst = null;
		try {

			pst = connection.prepareStatement(query);
			pst.setString(1, site);
			pst.setString(2, customer);
			pst.setString(3, service);
			pst.setString(4, region);

			rs = pst.executeQuery();

			while (rs.next()) {
				eipamobj = new EIPAMPojo();
				eipamobj.setCustomer(customer);
				eipamobj.setSite(site);
				eipamobj.setIp(rs.getString("eipam_ip"));
				eipamobj.setMask(rs.getString("eipam_subnet_mask"));
				eipamobj.setStatus(rs.getInt("eipam_ip_status"));
				ip = rs.getString("eipam_ip");
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			DBUtil.close(rs);
			DBUtil.close(statement);
			DBUtil.close(connection);
		}

		return eipamobj;

	}

	public final List<EIPAMPojo> getALLIPAMDatafromDB() throws IOException {
		connection = ConnectionFactory.getConnection();

		String query = "SELECT * FROM requestinfo.eipamdbtable";

		EIPAMPojo pojo;
		ResultSet rs = null;
		List<EIPAMPojo> requestInfoList1 = null;
		try {
			statement = connection.createStatement();
			rs = statement.executeQuery(query);
			requestInfoList1 = new ArrayList<EIPAMPojo>();

			while (rs.next()) {
				pojo = new EIPAMPojo();
				pojo.setCustomer(rs.getString("eipam_customer_name"));
				pojo.setIp(rs.getString("eipam_ip"));
				pojo.setMask(rs.getString("eipam_subnet_mask"));
				pojo.setSite(rs.getString("eipam_site_id"));
				pojo.setService(rs.getString("eipam_service"));
				pojo.setRegion(rs.getString("eipam_region"));
				int used = rs.getInt("eipam_ip_status");
				if (used == 1) {
					pojo.setIpUsed(true);
				} else {
					pojo.setIpUsed(false);
				}
				requestInfoList1.add(pojo);
			}

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			DBUtil.close(rs);
			DBUtil.close(statement);
			DBUtil.close(connection);
		}

		return requestInfoList1;

	}

	public final UserValidationResultDetailPojo checkUsersDB(String username, String password) throws IOException {
		connection = ConnectionFactory.getConnection();
		String query = "SELECT * FROM users";
		ResultSet rs = null;
		UserPojo flags = null;
		List<UserPojo> InfoList1 = null;
		UserValidationResultDetailPojo resultSet = new UserValidationResultDetailPojo();
		try {

			statement = connection.createStatement();
			rs = statement.executeQuery(query);
			InfoList1 = new ArrayList<UserPojo>();
			logger.info("" + rs.getFetchSize());

			boolean flag = false;
			if (rs != null) {
				while (rs.next()) {
					flags = new UserPojo();
					flags.setUsername(rs.getString("user_name"));
					flags.setPassword(rs.getString("user_password"));
					flags.setPrivilegeLevel(rs.getInt("privilegeLevel"));

					InfoList1.add(flags);
				}

				if (InfoList1.size() == 0) {
					resultSet.setMessage("No data found");
					resultSet.setResult(false);
					resultSet.setPrivilegeLevel(0);
					return resultSet;
				} else {
					for (int i = 0; i < InfoList1.size(); i++) {
						if (InfoList1.get(i).getUsername().equals(username)
								&& InfoList1.get(i).getPassword().equals(password)) {
							boolean didLogin = setUserLoginFlag(username, password);
							if (didLogin) {
								resultSet.setPrivilegeLevel(InfoList1.get(i).getPrivilegeLevel());
								resultSet.setMessage("Success");
								resultSet.setResult(true);
								flag = true;
								break;
							} else {
								resultSet.setMessage("Failure");
								resultSet.setResult(false);
								resultSet.setPrivilegeLevel(0);
								flag = false;
								break;
							}
						}
					}
					if (flag == false) {
						resultSet.setMessage("Either Username or Password incorrect");
						resultSet.setResult(false);
						resultSet.setPrivilegeLevel(0);

					}

				}
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {

		}
		return resultSet;

	}

	private final boolean setUserLoginFlag(String username, String password) {
		boolean result = false;
		connection = ConnectionFactory.getConnection();
		String sql = "update users set user_status=1 where user_name=? AND user_password=?";
		try {
			PreparedStatement ps = connection.prepareStatement(sql);

			ps.setString(1, username);
			ps.setString(2, password);

			int i = ps.executeUpdate();
			if (i == 1) {
				result = true;

			} else {
				result = false;
			}
		} catch (SQLException e) {

			result = false;
		}
		return result;
	}

	public final UserPojo getRouterCredentials() throws IOException {

		connection = ConnectionFactory.getConnection();
		ResultSet rs = null;

		UserPojo userList = new UserPojo();
		try {
			String query = "SELECT * FROM routeruserdevicemanagementtable";
			statement = connection.createStatement();
			rs = statement.executeQuery(query);

			if (rs.next()) {

				userList.setUsername(rs.getString("router_username"));
				userList.setPassword(rs.getString("router_password"));
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {

		}

		return userList;

	}
	public final UserPojo getRouterCredentials(String mgmtip) throws IOException {

		connection = ConnectionFactory.getConnection();
		ResultSet rs = null;

		UserPojo userList = new UserPojo();
		try {
			String query = "SELECT * FROM routeruserdevicemanagementtable where mgmtip=?";
			//statement = connection.createStatement();
			//rs = statement.executeQuery(query);

			
			PreparedStatement pst = null;
			

				pst = connection.prepareStatement(query);
				pst.setString(1, mgmtip);
				
				rs = pst.executeQuery();
				if (rs.next()) {

					userList.setUsername(rs.getString("router_username"));
					userList.setPassword(rs.getString("router_password"));
				}
			
			
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {

		}

		return userList;

	}
	public boolean addNewUserToDB(String username, String pass) {
		connection = ConnectionFactory.getConnection();

		String sql = "INSERT INTO users(user_name,user_password,user_status)" + "VALUES(?,?,?)";

		try {
			PreparedStatement ps = connection.prepareStatement(sql);

			ps.setString(1, username);

			ps.setString(2, pass);

			ps.setInt(3, 0);

			int i = ps.executeUpdate();
			if (i == 1) {

				return true;
			}
		} catch (SQLException e) {

		}

		return false;
	}

	@SuppressWarnings("null")
	public UserPojo updateRouterDeviceManagementDetails(String username, String password) {
		connection = ConnectionFactory.getConnection();
		ResultSet rs = null;

		UserPojo userList = new UserPojo();
		try {
			String query = "SELECT * FROM routeruserdevicemanagementtable";
			statement = connection.createStatement();
			rs = statement.executeQuery(query);

			boolean flag = false;

			if (rs.next()) {
				String query1 = null;
				query1 = "update routeruserdevicemanagementtable set router_username = ?,router_password=? where id = ?";
				PreparedStatement preparedStmt;

				preparedStmt = connection.prepareStatement(query1);

				preparedStmt.setString(1, username);
				preparedStmt.setString(2, password);
				preparedStmt.setInt(3, 1);

				preparedStmt.executeUpdate();

			}

			else {
				String sql = "INSERT INTO routeruserdevicemanagementtable(router_username,router_password)"
						+ "VALUES(?,?)";

				PreparedStatement ps = connection.prepareStatement(sql);

				ps.setString(1, username);

				ps.setString(2, password);

				ps.executeUpdate();
			}
			query = "SELECT * FROM routeruserdevicemanagementtable";
			statement = connection.createStatement();
			rs = statement.executeQuery(query);
			while (rs.next()) {

				userList.setUsername(rs.getString("router_username"));
				userList.setPassword(rs.getString("router_password"));
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {

		}

		return userList;
	}

	public String covnertTStoString(Timestamp indate) {
		String dateString = null;
		Date date = new Date();
		date.setTime(indate.getTime());
		dateString = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss").format(date);
		;
		return dateString;
	}

	public final List<EIPAMPojo> getSearchedRecordsFromDB(String site, String customer, String service, String ip)
			throws IOException, SQLException {

		connection = ConnectionFactory.getConnection();
		// String query =
		// "SELECT * FROM requestinfo.eipamdbtable WHERE eipam_site_id LIKE '%"+site+"%'
		// OR eipam_service LIKE '%"+service+"%' OR eipam_ip LIKE '%"+ip+"%' OR
		// eipam_customer_name LIKE '%"+customer+"%'";
		String query1;
		PreparedStatement ps = null;
		int parameters_to_search = 0;

		if (!site.isEmpty()) {
			parameters_to_search++;
		}
		if (!customer.isEmpty()) {
			parameters_to_search++;
		}
		if (!service.isEmpty()) {
			parameters_to_search++;
		}
		if (!ip.isEmpty()) {
			parameters_to_search++;
		}
		if (parameters_to_search == 1) {
			if (!site.isEmpty()) {
				// query for site and customer
				query1 = "SELECT * FROM requestinfo.eipamdbtable WHERE eipam_site_id LIKE ?";
				ps = connection.prepareStatement(query1);

				ps.setString(1, site + "%");

			} else if (!customer.isEmpty()) {
				query1 = "SELECT * FROM requestinfo.eipamdbtable WHERE eipam_customer_name LIKE ?";
				ps = connection.prepareStatement(query1);

				ps.setString(1, customer + "%");
			} else if (!service.isEmpty()) {
				query1 = "SELECT * FROM requestinfo.eipamdbtable WHERE eipam_service LIKE ?";

				ps = connection.prepareStatement(query1);

				ps.setString(1, service + "%");
			} else if (!ip.isEmpty()) {
				query1 = "SELECT * FROM requestinfo.eipamdbtable WHERE eipam_ip LIKE ?";

				ps = connection.prepareStatement(query1);

				ps.setString(1, ip + "%");
			}
		} else if (parameters_to_search == 2) {
			if (!site.isEmpty() && !customer.isEmpty()) {
				// query for site and customer
				query1 = "SELECT * FROM requestinfo.eipamdbtable WHERE eipam_site_id LIKE ? AND eipam_customer_name LIKE ?";
				ps = connection.prepareStatement(query1);

				ps.setString(1, site + "%");
				ps.setString(2, customer + "%");

			} else if (!site.isEmpty() && !service.isEmpty()) {
				// site and service
				query1 = "SELECT * FROM requestinfo.eipamdbtable WHERE eipam_site_id LIKE ?  AND eipam_service LIKE ?";

				ps = connection.prepareStatement(query1);

				ps.setString(1, site + "%");
				ps.setString(2, service + "%");
			} else if (!site.isEmpty() && !ip.isEmpty()) {
				// site and ip
				query1 = "SELECT * FROM requestinfo.eipamdbtable WHERE eipam_site_id LIKE ? AND eipam_ip LIKE?";

				ps = connection.prepareStatement(query1);

				ps.setString(1, site + "%");
				ps.setString(2, ip + "%");
			} else if (!customer.isEmpty() && !service.isEmpty()) {
				// customer and service
				query1 = "SELECT * FROM requestinfo.eipamdbtable WHERE eipam_customer_name LIKE ? AND eipam_service LIKE ?";

				ps = connection.prepareStatement(query1);

				ps.setString(1, customer + "%");
				ps.setString(2, service + "%");
			} else if (!customer.isEmpty() && !ip.isEmpty()) {
				// customer and ip
				query1 = "SELECT * FROM requestinfo.eipamdbtable WHERE eipam_customer_name LIKE ? AND eipam_ip LIKE ?";
				ps = connection.prepareStatement(query1);

				ps.setString(1, customer + "%");
				ps.setString(2, ip + "%");
			} else if (!service.isEmpty() && !ip.isEmpty()) {
				// service and ip
				query1 = "SELECT * FROM requestinfo.eipamdbtable WHERE eipam_service LIKE ? AND eipam_ip LIKE ?";

				ps = connection.prepareStatement(query1);

				ps.setString(1, service + "%");
				ps.setString(2, ip + "%");
			}
		} else if (parameters_to_search == 3) {
			if (!site.isEmpty() && !customer.isEmpty() && !service.isEmpty()) {
				// site customer service
				query1 = "SELECT * FROM requestinfo.eipamdbtable WHERE eipam_site_id LIKE ? AND eipam_customer_name LIKE ? AND eipam_service LIKE ?";

				ps = connection.prepareStatement(query1);

				ps.setString(1, site + "%");
				ps.setString(2, customer + "%");
				ps.setString(3, service + "%");
			} else if (!site.isEmpty() && !service.isEmpty() && !ip.isEmpty()) {
				// site service ip
				query1 = "SELECT * FROM requestinfo.eipamdbtable WHERE eipam_site_id LIKE ? AND eipam_service LIKE ? AND eipam_ip LIKE ?";

				ps = connection.prepareStatement(query1);

				ps.setString(1, site + "%");
				ps.setString(2, service + "%");
				ps.setString(3, ip + "%");
			} else if (!customer.isEmpty() && !service.isEmpty() && !ip.isEmpty()) {
				// customer service ip
				query1 = "SELECT * FROM requestinfo.eipamdbtable WHERE eipam_customer_name LIKE ? AND eipam_service LIKE ? AND eipam_ip LIKE ?";

				ps = connection.prepareStatement(query1);

				ps.setString(1, customer + "%");
				ps.setString(2, service + "%");
				ps.setString(3, ip + "%");
			} else if (!site.isEmpty() && !customer.isEmpty() && !ip.isEmpty()) {
				// site customer ip
				query1 = "SELECT * FROM requestinfo.eipamdbtable WHERE eipam_site_id LIKE ? AND eipam_customer_name LIKE ? AND eipam_ip LIKE ?";

				ps = connection.prepareStatement(query1);

				ps.setString(1, site + "%");
				ps.setString(2, customer + "%");
				ps.setString(3, ip + "%");
			}
		} else {
			// all four paramerter serarch
			query1 = "SELECT * FROM requestinfo.eipamdbtable WHERE eipam_site_id = ? AND eipam_customer_name=? AND eipam_service=? AND eipam_ip=?";

			ps = connection.prepareStatement(query1);

			ps.setString(1, site + "%");
			ps.setString(2, customer + "%");
			ps.setString(3, service + "%");
			ps.setString(4, ip + "%");
		}

		// query1 =
		// "SELECT * FROM requestinfo.eipamdbtable WHERE eipam_site_id = ? OR
		// eipam_customer_name=? OR eipam_service=? OR eipam_ip=?";

		ResultSet rs = null;
		EIPAMPojo eipamobj = null;
		List<EIPAMPojo> requestInfoList1 = null;

		try {
			requestInfoList1 = new ArrayList<EIPAMPojo>();

			rs = ps.executeQuery();

			while (rs.next()) {
				eipamobj = new EIPAMPojo();
				eipamobj.setCustomer(rs.getString("eipam_customer_name"));
				eipamobj.setSite(rs.getString("eipam_site_id"));
				eipamobj.setIp(rs.getString("eipam_ip"));
				eipamobj.setMask(rs.getString("eipam_subnet_mask"));
				eipamobj.setRegion(rs.getString("eipam_region"));
				eipamobj.setService(rs.getString("eipam_service"));
				eipamobj.setStatus(rs.getInt("eipam_ip_status"));
				if (eipamobj.getStatus() == 1) {
					eipamobj.setIpUsed(true);
				} else {
					eipamobj.setIpUsed(false);
				}
				requestInfoList1.add(eipamobj);
			}

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			DBUtil.close(rs);
			DBUtil.close(statement);
			DBUtil.close(connection);
		}

		return requestInfoList1;
	}

	/*
	 * Code changes for JDBC to JPA migration --- Alert Page(To display All alerts)
	 */

	public final List<AlertInformationPojo> getALLAlertDataFromDB() throws IOException {

		connection = ConnectionFactory.getConnection();
		String query = "SELECT * FROM requestinfo.alertinformationtable";

		AlertInformationPojo pojo;
		ResultSet rs = null;
		List<AlertInformationPojo> requestInfoList1 = null;
		try {
			statement = connection.createStatement();
			rs = statement.executeQuery(query);

			requestInfoList1 = new ArrayList<AlertInformationPojo>();
			while (rs.next()) {
				pojo = new AlertInformationPojo();
				pojo.setAlert_code(rs.getString("alert_code"));
				pojo.setAlert_category(rs.getString("alert_category"));
				pojo.setAlert_description(rs.getString("alert_description"));
				requestInfoList1.add(pojo);
			}

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			DBUtil.close(rs);
			DBUtil.close(statement);
			DBUtil.close(connection);
		}

		return requestInfoList1;

	}

	public final UserValidationResultDetailPojo updateEIPAMDB(EIPAMPojo pojo) {
		connection = ConnectionFactory.getConnection();
		UserValidationResultDetailPojo validatedResult = new UserValidationResultDetailPojo();

		String sql = "INSERT INTO eipamdbtable(eipam_site_id,eipam_region,eipam_ip,eipam_subnet_mask,eipam_service,eipam_customer_name,eipam_ip_status)"
				+ "VALUES(?,?,?,?,?,?,?)";

		try {
			PreparedStatement ps = connection.prepareStatement(sql);

			ps.setString(1, pojo.getSite());
			ps.setString(2, pojo.getRegion());
			ps.setString(3, pojo.getIp());
			ps.setString(4, pojo.getMask());
			ps.setString(5, pojo.getService());
			ps.setString(6, pojo.getCustomer());
			ps.setInt(7, 0);

			int i = ps.executeUpdate();
			if (i == 1) {

				validatedResult.setMessage("Success");
				validatedResult.setResult(true);
				return validatedResult;
			} else {
				validatedResult.setMessage("Failure");
				validatedResult.setResult(true);
				return validatedResult;
			}
		} catch (SQLException e) {

		}
		return validatedResult;
	}

	public final List<AlertInformationPojo> getSearchedRecordsFromAlertsDB(String code, String description)
			throws IOException {

		connection = ConnectionFactory.getConnection();
		// String query =
		// "SELECT * FROM requestinfo.eipamdbtable WHERE eipam_site_id LIKE '%"+site+"%'
		// OR eipam_service LIKE '%"+service+"%' OR eipam_ip LIKE '%"+ip+"%' OR
		// eipam_customer_name LIKE '%"+customer+"%'";
		String query1 = "SELECT * FROM requestinfo.alertinformationtable WHERE alert_code LIKE ? OR alert_description LIKE ?";
		ResultSet rs = null;
		AlertInformationPojo eipamobj = null;
		List<AlertInformationPojo> requestInfoList1 = null;
		try {
			requestInfoList1 = new ArrayList<AlertInformationPojo>();

			PreparedStatement ps = connection.prepareStatement(query1);

			ps.setString(1, code + "%");
			ps.setString(2, description + "%");

			rs = ps.executeQuery();

			while (rs.next()) {
				eipamobj = new AlertInformationPojo();
				eipamobj.setAlert_code(rs.getString("alert_code"));
				eipamobj.setAlert_category(rs.getString("alert_category"));
				eipamobj.setAlert_description(rs.getString("alert_description"));
				requestInfoList1.add(eipamobj);
			}

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			DBUtil.close(rs);
			DBUtil.close(statement);
			DBUtil.close(connection);
		}

		return requestInfoList1;
	}

	public final List<AlertInformationPojo> getLastAlertId() throws IOException {
		connection = ConnectionFactory.getConnection();
		String query = "SELECT * FROM requestinfo.alertinformationtable";

		AlertInformationPojo pojo;
		ResultSet rs = null;
		List<AlertInformationPojo> resultobj = new ArrayList<AlertInformationPojo>();
		List<AlertInformationPojo> requestInfoList1 = null;
		try {
			statement = connection.createStatement();
			rs = statement.executeQuery(query);
			requestInfoList1 = new ArrayList<AlertInformationPojo>();
			while (rs.next()) {
				pojo = new AlertInformationPojo();
				pojo.setAlert_code(rs.getString("alert_code"));
				pojo.setAlert_category(rs.getString("alert_category"));
				pojo.setAlert_description(rs.getString("alert_description"));
				pojo.setAlert_type(rs.getString("alert_type"));
				requestInfoList1.add(pojo);
			}

			/*
			 * List<AlertInformationPojo> notificationTypeList = new
			 * ArrayList<AlertInformationPojo>(); List<AlertInformationPojo> AlertTypeList =
			 * new ArrayList<AlertInformationPojo>();
			 * 
			 * for (int i = 0; i < requestInfoList1.size(); i++) { if
			 * (requestInfoList1.get(i).getAlert_type() .equalsIgnoreCase("Notification")) {
			 * notificationTypeList.add(requestInfoList1.get(i)); } } for (int i = 0; i <
			 * requestInfoList1.size(); i++) { if (requestInfoList1.get(i).getAlert_type()
			 * .equalsIgnoreCase("Alert")) { AlertTypeList.add(requestInfoList1.get(i)); } }
			 * 
			 * if (notificationTypeList.size() > 0) { AlertInformationPojo notificationObj =
			 * new AlertInformationPojo(); notificationObj.setAlert_type("Notification");
			 * notificationObj.setAlert_code(separate(AlertTypeList.get(
			 * notificationTypeList.size() - 1).getAlert_code()));
			 * 
			 * resultobj.add(notificationObj); } else { AlertInformationPojo notifObj = new
			 * AlertInformationPojo(); notifObj.setAlert_type("Notification");
			 * notifObj.setAlert_code("99"); resultobj.add(notifObj); } if
			 * (AlertTypeList.size() > 0) { AlertInformationPojo alertObj = new
			 * AlertInformationPojo(); alertObj.setAlert_type("Alert");
			 * alertObj.setAlert_code(separate(AlertTypeList.get( AlertTypeList.size() -
			 * 1).getAlert_code())); resultobj.add(alertObj); } else { AlertInformationPojo
			 * alertObj = new AlertInformationPojo(); alertObj.setAlert_type("Alert");
			 * alertObj.setAlert_code("99"); resultobj.add(alertObj); }
			 */
			/*
			 * alert_id = requestInfoList1.get(requestInfoList1.size() - 1)
			 * .getAlert_code(); alert_id = separate(alert_id);
			 */
			AlertInformationPojo tempObj = new AlertInformationPojo();
			if (requestInfoList1.size() > 0) {
				tempObj = new AlertInformationPojo();
				tempObj.setAlert_code(separate(requestInfoList1.get(requestInfoList1.size() - 1).getAlert_code()));
			} else {
				tempObj = new AlertInformationPojo();
				tempObj.setAlert_code("999");
			}

			resultobj.add(tempObj);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			DBUtil.close(rs);
			DBUtil.close(statement);
			DBUtil.close(connection);
		}

		return resultobj;

	}

	public static String separate(String string) {
		StringBuilder alphabetsBuilder = new StringBuilder();
		StringBuilder numbersBuilder = new StringBuilder();
		StringBuilder symbolsBuilder = new StringBuilder();
		for (int i = 0; i < string.length(); i++) {
			char ch = string.charAt(i);
			if (Character.isAlphabetic(ch)) {
				alphabetsBuilder.append(ch);
			} else if (Character.isDigit(ch)) {
				numbersBuilder.append(ch);
			} else {
				symbolsBuilder.append(ch);
			}
		}
		return numbersBuilder.toString();
	}

	public final UserValidationResultDetailPojo addNewAlertNotification(AlertInformationPojo pojo) {
		connection = ConnectionFactory.getConnection();
		UserValidationResultDetailPojo validatedResult = new UserValidationResultDetailPojo();

		String sql = "INSERT INTO alertinformationtable(alert_code,alert_category,alert_description,alert_type)"
				+ "VALUES(?,?,?,?)";

		try {
			PreparedStatement ps = connection.prepareStatement(sql);

			ps.setString(1, pojo.getAlert_code());
			ps.setString(2, pojo.getAlert_category());
			ps.setString(3, pojo.getAlert_description());
			ps.setString(4, pojo.getAlert_type());

			int i = ps.executeUpdate();
			if (i == 1) {

				validatedResult.setMessage("Record added successfully.");
				validatedResult.setResult(true);
				return validatedResult;
			} else {

				validatedResult.setMessage("Error while adding the record.");
				validatedResult.setResult(true);
				return validatedResult;
			}
		} catch (SQLException e) {
			logger.info("Error:> " + e.getMessage());
		}
		return validatedResult;
	}

	/*
	 * private String getElapsedTime(Date d1, Date d2) { String elapsedtime = null;
	 * // in milliseconds long diff = d2.getTime() - d1.getTime();
	 * 
	 * long diffSeconds = diff / 1000 % 60; long diffMinutes = diff / (60 * 1000) %
	 * 60; long diffHours = diff / (60 * 60 * 1000) % 24; long diffDays = diff / (24
	 * * 60 * 60 * 1000);
	 * 
	 * long daymin = diffDays * 1440; long hourmin = diffHours * 60; long secmin =
	 * (long) (diffSeconds * 0.016); long totalMins = daymin + hourmin + diffMinutes
	 * + secmin;
	 * 
	 * long dayTohours = diffDays * 24;
	 * 
	 * DecimalFormat formatter = new DecimalFormat("00"); String sec =
	 * formatter.format(diffSeconds); String min = formatter.format(diffMinutes);
	 * String hrs = formatter.format(diffHours + dayTohours);
	 * 
	 * elapsedtime = hrs + ":" + min + ":" + sec;
	 * 
	 * ElapsedTimeFormatPojo time = new ElapsedTimeFormatPojo();
	 * time.setDisplayTime(elapsedtime); time.setElapsedTimeinMinutes((int)
	 * totalMins); elapsedtimings.add(time); return elapsedtime; }
	 */

	private boolean updateEIPAMTable(String ip) {
		boolean result = false;
		connection = ConnectionFactory.getConnection();
		String sql = "update eipamdbtable set eipam_ip_status=1 where eipam_ip=?";
		try {
			PreparedStatement ps = connection.prepareStatement(sql);

			ps.setString(1, ip);

			int i = ps.executeUpdate();
			if (i == 1) {
				result = true;

			} else {
				result = false;
			}
		} catch (SQLException e) {
			logger.info("Error:> " + e.getMessage());
			result = false;
		}
		return result;
	}

	public List<RequestInfoSO> getDatasForRequestfromDB(String id) {
		List<RequestInfoSO> list = null;
		connection = ConnectionFactory.getConnection();
		String query1 = "SELECT * FROM requestinfo.requestinfoso WHERE alphanumeric_req_id = ?";
		RequestInfoSO request;
		ResultSet rs = null;
		int id1;

		try {
			list = new ArrayList<RequestInfoSO>();

			PreparedStatement ps = connection.prepareStatement(query1);

			ps.setString(1, id);

			rs = ps.executeQuery();

			while (rs.next()) {
				request = new RequestInfoSO();
				id1 = (rs.getInt("request_info_id"));
				request.setOs(rs.getString("Os"));
				request.setBanner(rs.getString("banner"));
				request.setDeviceName(rs.getString("device_name"));
				request.setModel(rs.getString("model"));
				request.setRegion(rs.getString("region"));
				request.setService(rs.getString("service"));
				request.setHostname(rs.getString("hostname"));
				request.setOsVersion(rs.getString("os_version"));
				request.setEnablePassword(rs.getString("enable_password"));
				request.setVrfName(rs.getString("vrf_name"));
				request.setIsAutoProgress(rs.getBoolean("isAutoProgress"));

				Timestamp d = rs.getTimestamp("date_of_processing");

				request.setDateOfProcessing((covnertTStoString(d)));
				request.setVendor(rs.getString("vendor"));
				request.setCustomer(rs.getString("customer"));
				request.setSiteid(rs.getString("siteid"));
				request.setStatus(rs.getString("request_status"));
				request.setManagementIp(rs.getString("ManagementIP"));
				request.setDisplay_request_id(rs.getString("alphanumeric_req_id"));
				request.setDeviceType(rs.getString("device_type"));
				request.setVpn(rs.getString("vpn"));
				request.setRequest_id(rs.getInt("request_info_id"));

				request.setRequest_version(rs.getDouble("request_version"));
				request.setRequest_parent_version(rs.getDouble("request_parent_version"));
				if (request.getStatus().equalsIgnoreCase("Success")
						|| request.getStatus().equalsIgnoreCase("success")) {
					SimpleDateFormat format = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
					Timestamp d1 = rs.getTimestamp("end_date_of_processing");

					Date d1_d = null;
					Date d2_d = null;

					d1_d = format.parse((covnertTStoString(d)));

					d2_d = format.parse((covnertTStoString(d1)));

					String elapsedtime = null;
					// in milliseconds
					long diff = d2_d.getTime() - d1_d.getTime();

					long diffSeconds = diff / 1000 % 60;
					long diffMinutes = diff / (60 * 1000) % 60;
					long diffHours = diff / (60 * 60 * 1000) % 24;
					long diffDays = diff / (24 * 60 * 60 * 1000);
					long dayTohours = diffDays * 24;

					DecimalFormat formatter = new DecimalFormat("00");
					String sec = formatter.format(diffSeconds);
					String min = formatter.format(diffMinutes);
					String hrs = formatter.format(diffHours + dayTohours);

					elapsedtime = hrs + ":" + min + ":" + sec;

					request.setElapsedTime(elapsedtime);
				} else {
					request.setElapsedTime("00:00:00");

				}

				String subQueryOne = "select*from misarpeso where request_info_id = ?";

				ResultSet rsnew = null;
				PreparedStatement ps1 = connection.prepareStatement(subQueryOne);

				ps1.setString(1, Integer.toString(id1));
				rsnew = ps1.executeQuery();
				if (rsnew != null) {
					MisArPeSO mis = new MisArPeSO();
					while (rsnew.next()) {
						mis.setFastEthernetIp(rsnew.getString("fastEthernetIp"));
						mis.setRouterVrfVpnDGateway(rsnew.getString("routerVrfVpnDGateway"));
						mis.setRouterVrfVpnDIp(rsnew.getString("routerVrfVpnDIp"));
					}
					request.setMisArPeSO(mis);
				}

				String subQueryTwo = "select*from internetlcvrfso where request_info_id= ?";
				ResultSet rsnew1 = null;
				PreparedStatement ps2 = connection.prepareStatement(subQueryTwo);

				ps2.setString(1, Integer.toString(id1));
				rsnew1 = ps2.executeQuery();
				if (rsnew1 != null) {
					InternetLcVrfSO iis = new InternetLcVrfSO();
					while (rsnew1.next()) {
						iis.setNetworkIp(rsnew1.getString("networkIp"));
						iis.setBgpASNumber(rsnew1.getString("asNumber"));
						iis.setNeighbor1(rsnew1.getString("neighbor1"));
						iis.setNeighbor2(rsnew1.getString("neighbor2"));
						iis.setNeighbor1_remoteAS(rsnew1.getString("neighbor1_remoteAS"));
						iis.setNeighbor2_remoteAS(rsnew1.getString("neighbor2_remoteAS"));
						iis.setRoutingProtocol(rsnew1.getString("routingProtocol"));
						iis.setNetworkIp_subnetMask(rsnew1.getString("networkIp_subnetMask"));

					}
					request.setInternetLcVrf(iis);
				}

				String subQueryThree = "select*from deviceinterfaceso where request_info_id= ?";
				ResultSet rsnew3 = null;
				PreparedStatement ps3 = connection.prepareStatement(subQueryThree);

				ps3.setString(1, Integer.toString(id1));
				rsnew3 = ps3.executeQuery();

				if (rsnew3 != null) {
					DeviceInterfaceSO iisd = new DeviceInterfaceSO();
					while (rsnew3.next()) {
						iisd.setDescription(rsnew3.getString("description"));
						iisd.setIp(rsnew3.getString("ip"));
						iisd.setEncapsulation(rsnew3.getString("encapsulation"));
						iisd.setMask(rsnew3.getString("mask"));
						iisd.setName(rsnew3.getString("name"));
						iisd.setSpeed(rsnew3.getString("speed"));
						iisd.setBandwidth(rsnew3.getString("Bandwidth"));
					}
					request.setDeviceInterfaceSO(iisd);

					list.add(request);
				}
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			DBUtil.close(rs);
			DBUtil.close(statement);
			DBUtil.close(connection);
		}
		return list;
	}

	public Map<String, String> insertRequestInDBForNewVersion(RequestInfoSO request) {
		Map<String, String> hmap = new HashMap<String, String>();
		String Os = null, banner = null, device_name = null, model = null, region = null, service = null,
				version = null, hostname = null, enablePassword = null, vrfName = null, alphaneumeric_req_id,
				customer = null, siteId = null, vendor = null, deviceType = null, vpn = null;
		String managementIP = null, scheduledTime = null;
		String request_creator_name = null, request_status = null, snmpHostAddress = null, snmpString = null,
				loopBackType = null, loopbackIPaddress = null, loopbackSubnetMask = null, lanInterface = null,
				lanIp = null, lanMaskAddress = null, lanDescription = null, certificationSelectionBit = null,
				templateId = null;

		double request_version = 0, request_parent_version = 0;
		boolean isAutoProgress;
		String sql = "INSERT INTO requestinfoso(Os,banner,device_name,model,region,service,os_version,hostname,enable_password,vrf_name,isAutoProgress,vendor,customer,siteid,managementIp,device_type,vpn,alphanumeric_req_id,request_status,request_version,request_parent_version,request_creator_name,snmpHostAddress,snmpString,loopBackType,loopbackIPaddress,loopbackSubnetMask,lanInterface,lanIp,lanMaskAddress,lanDescription,certificationSelectionBit,ScheduledTime,RequestType_Flag,TemplateIdUsed,RequestOwner)"
				+ "VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";

		connection = ConnectionFactory.getConnection();
		try {
			PreparedStatement ps = connection.prepareStatement(sql);
			// alphaneumeric_req_id =
			// UUID.randomUUID().toString().toUpperCase();
			// alphaneumeric_req_id = request.getProcessID();
			alphaneumeric_req_id = request.getDisplay_request_id();
			hmap.put("requestID", alphaneumeric_req_id);
			if (request.getOs() != null || request.getOs() != "") {
				Os = request.getOs();
			}

			if (request.getBanner() != null || request.getBanner() != "") {
				banner = request.getBanner();
			}

			if (request.getDeviceName() != null || request.getDeviceName() != "") {
				device_name = request.getDeviceName();
			}

			if (request.getModel() != null || request.getModel() != "") {
				model = request.getModel();
			}

			if (request.getRegion() != null || request.getRegion() != "") {
				region = request.getRegion();
			}

			if (request.getService() != null || request.getService() != "") {
				service = request.getService();
			}

			if (request.getOsVersion() != null || request.getOsVersion() != "") {
				version = request.getOsVersion();
			}
			if (request.getHostname() != null || request.getHostname() != "") {
				hostname = request.getHostname();
			}

			if (request.getEnablePassword() != null || request.getEnablePassword() != "") {
				enablePassword = request.getEnablePassword();
			}

			if (request.getVrfName() != null || request.getVrfName() != "") {
				vrfName = request.getVrfName();
			}

			if (request.getIsAutoProgress() != null) {
				isAutoProgress = request.getIsAutoProgress();
			} else {
				isAutoProgress = false;
			}
			if (request.getCustomer() != null || request.getCustomer() != "") {
				customer = request.getCustomer();
			}
			if (request.getSiteid() != null || request.getSiteid() != "") {
				siteId = request.getSiteid();
			}
			if (request.getVendor() != null || request.getVendor() != "") {
				vendor = request.getVendor();
			}
			if (request.getManagementIp() != null || request.getManagementIp() != "") {
				managementIP = request.getManagementIp();
			}
			if (request.getDeviceType() != null || request.getDeviceType() != "") {
				deviceType = request.getDeviceType();
			}
			if (request.getVpn() != null || request.getVpn() != "") {
				vpn = request.getVpn();
			}
			if (request.getStatus() != null || request.getStatus() != "") {
				request_status = request.getStatus();
			}

			if (request.getRequest_version() != 0) {
				request_version = request.getRequest_version();
			}
			if (request.getRequest_parent_version() != 0) {
				request_parent_version = request.getRequest_parent_version();
			}
			if (request.getRequest_creator_name() != null) {
				request_creator_name = request.getRequest_creator_name();
			}

			if (request.getSnmpHostAddress() != null) {
				snmpHostAddress = request.getSnmpHostAddress();
			}
			if (request.getSnmpString() != null) {
				snmpString = request.getSnmpString();
			}
			if (request.getLoopBackType() != null) {
				loopBackType = request.getLoopBackType();
			}
			if (request.getLoopbackIPaddress() != null) {
				loopbackIPaddress = request.getLoopbackIPaddress();
			}
			if (request.getLoopbackSubnetMask() != null) {
				loopbackSubnetMask = request.getLoopbackSubnetMask();
			}
			if (request.getLanInterface() != null || request.getLanInterface() != "") {
				lanInterface = request.getLanInterface();
			}
			if (request.getLanIp() != null || request.getLanIp() != "") {
				lanIp = request.getLanIp();
			}
			if (request.getLanMaskAddress() != null || request.getLanMaskAddress() != "") {
				lanMaskAddress = request.getLanMaskAddress();
			}
			if (request.getLanDescription() != null || request.getLanDescription() != "") {
				lanDescription = request.getLanDescription();
			}
			if (request.getCertificationSelectionBit() != null || request.getCertificationSelectionBit() != "") {
				certificationSelectionBit = request.getCertificationSelectionBit();
			}

			if (request.getScheduledTime() != null || request.getScheduledTime() != "") {
				scheduledTime = request.getScheduledTime();
			}
			// template suggestion
			if (request.getTemplateId() != null || request.getTemplateId() != "") {
				templateId = request.getTemplateId();
			}
			if (request.getInternetLcVrf() != null) {
				String networkIp = null, AS = null, neighbor1 = null, neighbor2 = null, neighbor1_remoteAS = null,
						neighbor2_remoteAS = null, networkIp_subnetMask = null, routingProtocol = null;
				if (request.getInternetLcVrf().getNetworkIp() != null
						|| request.getInternetLcVrf().getNetworkIp() != "") {
					networkIp = request.getInternetLcVrf().getNetworkIp();
				}

				if (request.getInternetLcVrf().getBgpASNumber() != null
						|| request.getInternetLcVrf().getBgpASNumber() != "") {
					AS = request.getInternetLcVrf().getBgpASNumber();
				}

				if (request.getInternetLcVrf().getNeighbor1() != null
						|| request.getInternetLcVrf().getNeighbor1() != "") {
					neighbor1 = request.getInternetLcVrf().getNeighbor1();
				}

				if (request.getInternetLcVrf().getNeighbor2() != null
						|| request.getInternetLcVrf().getNeighbor2() != "") {
					neighbor2 = request.getInternetLcVrf().getNeighbor2();
				} else {
					neighbor2 = null;
				}
				if (request.getInternetLcVrf().getNeighbor1_remoteAS() != null
						|| request.getInternetLcVrf().getNeighbor1_remoteAS() != "") {
					neighbor1_remoteAS = request.getInternetLcVrf().getNeighbor1_remoteAS();
				}

				if (request.getInternetLcVrf().getNeighbor2_remoteAS() != null
						|| request.getInternetLcVrf().getNeighbor2_remoteAS() != "") {
					neighbor2_remoteAS = request.getInternetLcVrf().getNeighbor2_remoteAS();
				} else {
					neighbor2_remoteAS = null;
				}

				if (request.getInternetLcVrf().getNetworkIp_subnetMask() != null
						|| request.getInternetLcVrf().getNetworkIp_subnetMask() != "") {
					networkIp_subnetMask = request.getInternetLcVrf().getNetworkIp_subnetMask();
				}

				if (request.getInternetLcVrf().getRoutingProtocol() != null
						|| request.getInternetLcVrf().getRoutingProtocol() != "") {
					routingProtocol = request.getInternetLcVrf().getRoutingProtocol();
				}

				String sql1 = "INSERT INTO internetlcvrfso(networkIp,asNumber,neighbor1,neighbor2,neighbor1_remoteAS,neighbor2_remoteAS,networkIp_subnetMask,routingProtocol) VALUES"
						+ "(?,?,?,?,?,?,?,?)";

				PreparedStatement ps1 = connection.prepareStatement(sql1);
				if (networkIp != "") {
					ps1.setString(1, networkIp);
				} else {
					ps1.setNull(1, java.sql.Types.VARCHAR);
				}
				if (AS != "") {
					ps1.setString(2, AS);
				} else {
					ps1.setNull(2, java.sql.Types.VARCHAR);
				}
				if (neighbor1 != "") {
					ps1.setString(3, neighbor1);
				} else {
					ps1.setNull(3, java.sql.Types.VARCHAR);
				}
				if (neighbor2 != "") {
					ps1.setString(4, neighbor2);
				} else {
					ps1.setNull(4, java.sql.Types.VARCHAR);
				}
				if (neighbor1_remoteAS != "") {
					ps1.setString(5, neighbor1_remoteAS);
				} else {
					ps1.setNull(5, java.sql.Types.VARCHAR);
				}

				if (neighbor2_remoteAS != "") {
					ps1.setString(6, neighbor2_remoteAS);
				} else {
					ps1.setNull(6, java.sql.Types.VARCHAR);
				}
				if (networkIp_subnetMask != "") {
					ps1.setString(7, networkIp_subnetMask);
				} else {
					ps1.setNull(7, java.sql.Types.VARCHAR);
				}
				if (routingProtocol != "") {
					ps1.setString(8, routingProtocol);
				} else {
					ps1.setNull(8, java.sql.Types.VARCHAR);
				}
				ps1.execute("SET FOREIGN_KEY_CHECKS=0");
				ps1.executeUpdate();
			} else {
				// String networkIp = null, remotePort = null, neighbor1 = null,
				// neighbor2 =null, neighbor3 =null, neighbor4 = null, neighbor5
				// = null, neighbor6 = null, routerBgp65k = null;

				String sql1 = "INSERT INTO internetlcvrfso(networkIp,asNumber,neighbor1,neighbor2,neighbor1_remoteAS,neighbor2_remoteAS,networkIp_subnetMask,routingProtocol) VALUES"
						+ "(?,?,?,?,?,?,?,?)";

				PreparedStatement ps1 = connection.prepareStatement(sql1);
				ps1.setNull(1, java.sql.Types.VARCHAR);
				ps1.setNull(2, java.sql.Types.VARCHAR);
				ps1.setNull(3, java.sql.Types.VARCHAR);
				ps1.setNull(4, java.sql.Types.VARCHAR);
				ps1.setNull(5, java.sql.Types.VARCHAR);
				ps1.setNull(6, java.sql.Types.VARCHAR);
				ps1.setNull(7, java.sql.Types.VARCHAR);
				ps1.setNull(8, java.sql.Types.VARCHAR);
				ps1.execute("SET FOREIGN_KEY_CHECKS=0");
				ps1.executeUpdate();
			}

			if (request.getMisArPeSO() != null) {

				String routerVrfVpnDIp = null, routerVrfVpnDGateway = null, fastEthernetIp = null;
				if (request.getMisArPeSO().getRouterVrfVpnDIp() != null
						|| request.getMisArPeSO().getRouterVrfVpnDIp() != "") {
					routerVrfVpnDIp = request.getMisArPeSO().getRouterVrfVpnDIp();
				}

				if (request.getMisArPeSO().getRouterVrfVpnDGateway() != null
						|| request.getMisArPeSO().getRouterVrfVpnDGateway() != "") {
					routerVrfVpnDGateway = request.getMisArPeSO().getRouterVrfVpnDGateway();
				}

				if (request.getMisArPeSO().getRouterVrfVpnDGateway() != null
						|| request.getMisArPeSO().getFastEthernetIp() != "") {
					fastEthernetIp = request.getMisArPeSO().getFastEthernetIp();
				}

				String sql1 = "INSERT INTO misarpeso(routerVrfVpnDIp, routerVrfVpnDGateway, fastEthernetIp) "
						+ "VALUES(?, ?, ?)";
				PreparedStatement ps1 = connection.prepareStatement(sql1);
				if (routerVrfVpnDIp != "") {
					ps1.setString(1, routerVrfVpnDIp);
				} else {
					ps1.setNull(1, java.sql.Types.VARCHAR);

				}
				if (routerVrfVpnDGateway != "") {
					ps1.setString(2, routerVrfVpnDGateway);
				} else {
					ps1.setNull(2, java.sql.Types.VARCHAR);

				}
				if (fastEthernetIp != "") {
					ps1.setString(3, fastEthernetIp);
				} else {
					ps.setNull(3, java.sql.Types.VARCHAR);

				}
				ps1.execute("SET FOREIGN_KEY_CHECKS=0");
				ps1.executeUpdate();

			} else {
				String routerVrfVpnDIp = "", routerVrfVpnDGateway = "", fastEthernetIp = "";
				String sql1 = "INSERT INTO misarpeso(routerVrfVpnDIp, routerVrfVpnDGateway, fastEthernetIp) "
						+ "VALUES(?, ?, ?)";
				PreparedStatement ps1 = connection.prepareStatement(sql1);
				if (routerVrfVpnDIp != "") {
					ps1.setString(1, routerVrfVpnDIp);
				} else {
					ps1.setNull(1, java.sql.Types.VARCHAR);

				}
				if (routerVrfVpnDGateway != "") {
					ps1.setString(2, routerVrfVpnDGateway);
				} else {
					ps1.setNull(2, java.sql.Types.VARCHAR);

				}
				if (fastEthernetIp != "") {
					ps1.setString(3, fastEthernetIp);
				} else {
					ps.setNull(3, java.sql.Types.VARCHAR);

				}
				ps1.execute("SET FOREIGN_KEY_CHECKS=0");
				ps1.executeUpdate();
			}

			if (request.getDeviceInterfaceSO() != null) {
				String name = null, description = null, ip = null, mask = null, speed = null, encapsulation = null,
						bandwidth = null;
				if (request.getDeviceInterfaceSO().getName() != null
						|| request.getDeviceInterfaceSO().getName() != "") {
					name = request.getDeviceInterfaceSO().getName();
				}

				if (request.getDeviceInterfaceSO().getDescription() != null
						|| request.getDeviceInterfaceSO().getDescription() != "") {
					description = request.getDeviceInterfaceSO().getDescription();
				}

				if (request.getDeviceInterfaceSO().getIp() != null || request.getDeviceInterfaceSO().getIp() != "") {
					ip = request.getDeviceInterfaceSO().getIp();
				}

				if (request.getDeviceInterfaceSO().getMask() != null
						|| request.getDeviceInterfaceSO().getMask() != "") {
					mask = request.getDeviceInterfaceSO().getMask();
				}

				if (request.getDeviceInterfaceSO().getSpeed() != null
						&& !request.getDeviceInterfaceSO().getSpeed().isEmpty()) {
					speed = request.getDeviceInterfaceSO().getSpeed();
				} else {
					bandwidth = request.getDeviceInterfaceSO().getBandwidth();
				}

				if (request.getDeviceInterfaceSO().getEncapsulation() != null
						|| request.getDeviceInterfaceSO().getEncapsulation() != "") {
					encapsulation = request.getDeviceInterfaceSO().getEncapsulation();
				}

				String sql1 = "INSERT INTO deviceinterfaceso(name,description,ip,mask,speed,encapsulation,Bandwidth)"
						+ "VALUES(?, ?, ?, ?, ?, ?, ?)";

				PreparedStatement ps1 = connection.prepareStatement(sql1);
				if (name != "") {
					ps1.setString(1, name);
				} else {
					ps1.setNull(1, java.sql.Types.VARCHAR);
				}
				if (description != "") {
					ps1.setString(2, description);
				} else {
					ps1.setNull(2, java.sql.Types.VARCHAR);
				}
				if (ip != "") {
					ps1.setString(3, ip);
				} else {
					ps1.setNull(3, java.sql.Types.VARCHAR);
				}
				if (mask != "") {
					ps1.setString(4, mask);
				} else {
					ps1.setNull(4, java.sql.Types.VARCHAR);
				}
				if (speed != "") {
					ps1.setString(5, speed);
				} else {
					ps1.setNull(5, java.sql.Types.VARCHAR);
				}
				if (encapsulation != "") {
					ps1.setString(6, encapsulation);
				} else {
					ps1.setNull(6, java.sql.Types.VARCHAR);
				}
				if (bandwidth != "") {
					ps1.setString(7, bandwidth);
				} else {
					ps1.setNull(7, java.sql.Types.VARCHAR);
				}

				ps1.execute("SET FOREIGN_KEY_CHECKS=0");
				ps1.executeUpdate();
			} else {
				// String networkIp = null, remotePort = null, neighbor1 = null,
				// neighbor2 =null, neighbor3 =null, neighbor4 = null, neighbor5
				// = null, neighbor6 = null, routerBgp65k = null;

				String sql1 = "INSERT INTO deviceinterfaceso(name,description,ip,mask,speed,encapsulation)"
						+ "VALUES(?, ?, ?, ?, ?, ?)";
				PreparedStatement ps1 = connection.prepareStatement(sql1);
				ps1.setNull(1, java.sql.Types.VARCHAR);
				ps1.setNull(2, java.sql.Types.VARCHAR);
				ps1.setNull(3, java.sql.Types.VARCHAR);
				ps1.setNull(4, java.sql.Types.VARCHAR);
				ps1.setNull(5, java.sql.Types.VARCHAR);
				ps1.setNull(6, java.sql.Types.VARCHAR);
				ps1.setNull(7, java.sql.Types.VARCHAR);
				ps1.execute("SET FOREIGN_KEY_CHECKS=0");
				ps1.executeUpdate();
			}

			if (Os != "") {
				ps.setString(1, Os);
			} else {
				ps.setNull(1, java.sql.Types.VARCHAR);

			}

			// Logic to add banner text in new
			// table-----------------------------------------------------------------------------------------------------

			String sql1 = "INSERT INTO bannerdatatable(bannerdata)" + "VALUES(?)";

			PreparedStatement ps3 = connection.prepareStatement(sql1);
			if (banner != "") {
				ps3.setString(1, banner);
			} else {
				ps3.setNull(1, java.sql.Types.VARCHAR);
			}

			ps3.execute("SET FOREIGN_KEY_CHECKS=0");
			ps3.executeUpdate();
			// End of banner
			// logic------------------------------------------------------------------------------------------------------------------------

			if (banner != "") {
				ps.setString(2, banner);
			} else {
				ps.setNull(2, java.sql.Types.VARCHAR);

			}

			if (device_name != "") {
				ps.setString(3, device_name);
			} else {
				ps.setNull(3, java.sql.Types.VARCHAR);

			}
			if (model != "") {
				ps.setString(4, model);
			} else {
				ps.setNull(4, java.sql.Types.VARCHAR);

			}
			if (region != "") {
				ps.setString(5, region);
			} else {
				ps.setNull(5, java.sql.Types.VARCHAR);

			}
			if (service != "") {
				ps.setString(6, service);
			} else {
				ps.setNull(6, java.sql.Types.VARCHAR);

			}
			if (version != "") {
				ps.setString(7, version);
			} else {
				ps.setNull(7, java.sql.Types.VARCHAR);

			}
			if (hostname != "") {
				ps.setString(8, hostname);
			} else {
				ps.setNull(8, java.sql.Types.VARCHAR);

			}
			if (enablePassword != "") {
				ps.setString(9, enablePassword);
			} else {
				ps.setNull(9, java.sql.Types.VARCHAR);

			}
			if (vrfName != "") {
				ps.setString(10, vrfName);
			} else {
				ps.setNull(10, java.sql.Types.VARCHAR);

			}

			ps.setBoolean(11, isAutoProgress);

			if (vendor != "") {
				ps.setString(12, vendor);
			} else {
				ps.setNull(12, java.sql.Types.VARCHAR);

			}
			if (customer != "") {
				ps.setString(13, customer);
			} else {
				ps.setNull(13, java.sql.Types.VARCHAR);

			}

			if (siteId != "") {
				ps.setString(14, siteId);
			} else {
				ps.setNull(14, java.sql.Types.VARCHAR);

			}

			if (managementIP != "") {
				ps.setString(15, managementIP);
			} else {
				ps.setNull(15, java.sql.Types.VARCHAR);

			}
			if (deviceType != "") {
				ps.setString(16, deviceType);
			} else {
				ps.setNull(16, java.sql.Types.VARCHAR);

			}
			if (vpn != "") {
				ps.setString(17, vpn);
			} else {
				ps.setNull(17, java.sql.Types.VARCHAR);

			}
			if (alphaneumeric_req_id != "") {
				ps.setString(18, alphaneumeric_req_id);
			} else {
				ps.setNull(18, java.sql.Types.VARCHAR);

			}

			ps.setString(19, request_status);

			ps.setDouble(20, request_version);
			ps.setDouble(21, request_parent_version);
			if (request_creator_name != null) {
				ps.setString(22, request_creator_name);
			} else {
				ps.setNull(22, java.sql.Types.VARCHAR);

			}

			if (snmpHostAddress != null) {
				ps.setString(23, snmpHostAddress);
			} else {
				ps.setNull(23, java.sql.Types.VARCHAR);

			}
			if (snmpString != null) {
				ps.setString(24, snmpString);
			} else {
				ps.setNull(24, java.sql.Types.VARCHAR);

			}
			if (loopBackType != null) {
				ps.setString(25, loopBackType);
			} else {
				ps.setNull(25, java.sql.Types.VARCHAR);

			}
			if (loopbackIPaddress != null) {
				ps.setString(26, loopbackIPaddress);
			} else {
				ps.setNull(26, java.sql.Types.VARCHAR);

			}
			if (loopbackSubnetMask != null) {
				ps.setString(27, loopbackSubnetMask);
			} else {
				ps.setNull(27, java.sql.Types.VARCHAR);

			}
			if (lanInterface != null) {
				ps.setString(28, lanInterface);
			} else {
				ps.setNull(28, java.sql.Types.VARCHAR);

			}
			if (lanIp != null) {
				ps.setString(29, lanIp);
			} else {
				ps.setNull(29, java.sql.Types.VARCHAR);

			}
			if (lanMaskAddress != null) {
				ps.setString(30, lanMaskAddress);
			} else {
				ps.setNull(30, java.sql.Types.VARCHAR);

			}
			if (lanDescription != null) {
				ps.setString(31, lanDescription);
			} else {
				ps.setNull(31, java.sql.Types.VARCHAR);

			}
			if (certificationSelectionBit != null) {
				ps.setString(32, certificationSelectionBit);
			} else {
				ps.setNull(32, java.sql.Types.VARCHAR);

			}
			if (scheduledTime != null && scheduledTime != "") {

				SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");

				try {
					java.util.Date parsedDate = sdf.parse(scheduledTime);

					java.sql.Timestamp timestampTimeForScheduled = new java.sql.Timestamp(parsedDate.getTime());
					ps.setTimestamp(33, timestampTimeForScheduled);
					ps.setString(34, "S");
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			} else {
				ps.setNull(33, java.sql.Types.TIMESTAMP);
				ps.setString(34, "M");
			}

			if (templateId != null) {
				ps.setString(35, templateId);
			} else {
				ps.setNull(35, java.sql.Types.VARCHAR);

			}
			if (Global.loggedInUser != null || !Global.loggedInUser.isEmpty()) {
				ps.setString(36, Global.loggedInUser);
			} else {
				ps.setString(36, "seuser");

			}
			int i = ps.executeUpdate();
			if (i == 1) {

				addRequestIDtoWebserviceInfo(alphaneumeric_req_id, Double.toString(request_version));
				updateEIPAMTable(request.getDeviceInterfaceSO().getIp());
				hmap.put("result", "true");
				return hmap;

			}
		} catch (SQLException ex) {
			ex.printStackTrace();
		}

		hmap.put("result", "false");
		return hmap;
	}

	public final List<RequestInfoSO> searchRequestsFromDBWithVersion(String key, String value, String version)
			throws IOException, ParseException {
		connection = ConnectionFactory.getConnection();
		String query = null;
		// Global.loggedInUser="admin";

		if (!Global.loggedInUser.equalsIgnoreCase("admin")) {

			if (Global.loggedInUser.equalsIgnoreCase("seuser")) {
				if (key.equalsIgnoreCase("Request ID") || key.equalsIgnoreCase("Request")) {
					query = "SELECT * FROM requestinfo.requestinfoso WHERE alphanumeric_req_id = ? AND request_version = ? and request_creator_name=?";
				} else if (key.equalsIgnoreCase("Region")) {
					query = "SELECT * FROM requestinfo.requestinfoso WHERE region LIKE ?  and request_creator_name=?";

				} else if (key.equalsIgnoreCase("Vendor")) {
					query = "SELECT * FROM requestinfo.requestinfoso WHERE vendor LIKE ?  and request_creator_name=?";

				} else if (key.equalsIgnoreCase("Model")) {
					query = "SELECT * FROM requestinfo.requestinfoso WHERE model LIKE ?  and request_creator_name=?";

				} else if (key.equalsIgnoreCase("Status")) {
					query = "SELECT * FROM requestinfo.requestinfoso WHERE request_status LIKE ?  and request_creator_name=?";

				}
			} else if (Global.loggedInUser.equalsIgnoreCase("feuser")) {
				if (key.equalsIgnoreCase("Request ID") || key.equalsIgnoreCase("Request")) {
					query = "SELECT * FROM requestinfo.requestinfoso WHERE alphanumeric_req_id = ? AND request_version = ? and RequestOwner=?";
				} else if (key.equalsIgnoreCase("Region")) {
					query = "SELECT * FROM requestinfo.requestinfoso WHERE region LIKE ?  and RequestOwner=?";

				} else if (key.equalsIgnoreCase("Vendor")) {
					query = "SELECT * FROM requestinfo.requestinfoso WHERE vendor LIKE ?  and RequestOwner=?";

				} else if (key.equalsIgnoreCase("Model")) {
					query = "SELECT * FROM requestinfo.requestinfoso WHERE model LIKE ?  and RequestOwner=?";

				} else if (key.equalsIgnoreCase("Status")) {
					query = "SELECT * FROM requestinfo.requestinfoso WHERE request_status LIKE ?  and RequestOwner=?";

				}
			} else {
				if (key.equalsIgnoreCase("Request ID") || key.equalsIgnoreCase("Request")) {
					query = "SELECT * FROM requestinfo.requestinfoso WHERE alphanumeric_req_id = ? AND request_version = ? and RequestOwner=?";
				} else if (key.equalsIgnoreCase("Region")) {
					query = "SELECT * FROM requestinfo.requestinfoso WHERE region LIKE ?  and RequestOwner=?";

				} else if (key.equalsIgnoreCase("Vendor")) {
					query = "SELECT * FROM requestinfo.requestinfoso WHERE vendor LIKE ?  and RequestOwner=?";

				} else if (key.equalsIgnoreCase("Model")) {
					query = "SELECT * FROM requestinfo.requestinfoso WHERE model LIKE ?  and RequestOwner=?";

				} else if (key.equalsIgnoreCase("Status")) {
					query = "SELECT * FROM requestinfo.requestinfoso WHERE request_status LIKE ?  and RequestOwner=?";

				}
			}
		} else {
			if (key.equalsIgnoreCase("Request ID") || key.equalsIgnoreCase("Request")) {
				query = "SELECT * FROM requestinfo.requestinfoso WHERE alphanumeric_req_id = ? AND request_version = ?";
			} else if (key.equalsIgnoreCase("Region")) {
				query = "SELECT * FROM requestinfo.requestinfoso WHERE region LIKE ?";

			} else if (key.equalsIgnoreCase("Vendor")) {
				query = "SELECT * FROM requestinfo.requestinfoso WHERE vendor LIKE ?";

			} else if (key.equalsIgnoreCase("Model")) {
				query = "SELECT * FROM requestinfo.requestinfoso WHERE model LIKE ?";

			} else if (key.equalsIgnoreCase("Status")) {
				query = "SELECT * FROM requestinfo.requestinfoso WHERE request_status LIKE ?";

			}
		}
		ResultSet rs = null;
		RequestInfoSO request = null;
		List<RequestInfoSO> requestInfoList1 = null;
		PreparedStatement pst = null;
		try {

			pst = connection.prepareStatement(query);

			if (!Global.loggedInUser.equalsIgnoreCase("admin")) {
				pst.setString(1, value);
				if (key.equalsIgnoreCase("Request ID") || key.equalsIgnoreCase("Request")) {
					pst.setString(2, version);
					pst.setString(3, Global.loggedInUser);

				} else {
					pst.setString(2, Global.loggedInUser);
				}
			} else {
				pst.setString(1, value);
				if (key.equalsIgnoreCase("Request ID") || key.equalsIgnoreCase("Request")) {
					pst.setString(2, version);

				}

			}
			rs = pst.executeQuery();
			requestInfoList1 = new ArrayList<RequestInfoSO>();

			int id;
			while (rs.next()) {
				request = new RequestInfoSO();
				String importStatus = rs.getString("import_status");
				String importSource = rs.getString("importsource");
				if (importSource.equalsIgnoreCase("Manual")) {
					id = (rs.getInt("request_info_id"));
				}

				/*
				 * else if(importStatus ==null) //if(importStatus.equals("Not")) {
				 * 
				 * logger.info("Not taken");
				 * 
				 * }
				 */
				else if (importStatus.equals("Success")) {
					id = (rs.getInt("request_info_id"));
				}

				id = (rs.getInt("request_info_id"));
				request.setOs(rs.getString("Os"));
				request.setBanner(rs.getString("banner"));
				request.setDeviceName(rs.getString("device_name"));
				request.setModel(rs.getString("model"));
				request.setRegion(rs.getString("region"));
				request.setService(rs.getString("service"));
				request.setHostname(rs.getString("hostname"));
				request.setOsVersion(rs.getString("os_version"));
				request.setEnablePassword(rs.getString("enable_password"));
				request.setVrfName(rs.getString("vrf_name"));
				request.setIsAutoProgress(rs.getBoolean("isAutoProgress"));

				Timestamp d = rs.getTimestamp("date_of_processing");

				request.setDateOfProcessing((covnertTStoString(d)));
				request.setVendor(rs.getString("vendor"));
				request.setCustomer(rs.getString("customer"));
				request.setSiteid(rs.getString("siteid"));
				request.setStatus(rs.getString("request_status"));
				request.setManagementIp(rs.getString("ManagementIP"));
				request.setDisplay_request_id(rs.getString("alphanumeric_req_id"));
				request.setDeviceType(rs.getString("device_type"));
				request.setVpn(rs.getString("vpn"));
				request.setRequest_id(rs.getInt("request_info_id"));
				request.setRequest_version(rs.getDouble("request_version"));
				request.setRequest_parent_version(rs.getDouble("request_parent_version"));
				request.setRequest_creator_name(rs.getString("request_creator_name"));
				request.setSnmpHostAddress(rs.getString("snmpHostAddress"));
				if (rs.getString("snmpHostAddress") == null) {
					request.setSnmpHostAddress("");
				}
				request.setSnmpString(rs.getString("snmpString"));
				if (rs.getString("snmpString") == null) {
					request.setSnmpString("");
				}
				request.setLoopBackType(rs.getString("loopBackType"));
				if (rs.getString("loopBackType") == null) {
					request.setLoopBackType("");
				}
				request.setLoopbackIPaddress(rs.getString("loopbackIPaddress"));
				if (rs.getString("loopbackIPaddress") == null) {
					request.setLoopbackIPaddress("");
				}
				request.setLoopbackSubnetMask(rs.getString("loopbackSubnetMask"));
				if (rs.getString("loopbackSubnetMask") == null) {
					request.setLoopbackSubnetMask("");
				}
				request.setLanInterface(rs.getString("lanInterface"));
				if (rs.getString("lanInterface") == null) {
					request.setLanInterface("");
				}
				request.setLanIp(rs.getString("lanIp"));
				if (rs.getString("lanIp") == null) {
					request.setLanIp("");
				}
				request.setLanMaskAddress(rs.getString("lanMaskAddress"));
				if (rs.getString("lanMaskAddress") == null) {
					request.setLanMaskAddress("");
				}
				request.setLanDescription(rs.getString("lanDescription"));
				if (rs.getString("lanDescription") == null) {
					request.setLanDescription("");
				}
				request.setTemplateId(rs.getString("TemplateIdUsed"));
				if (rs.getString("TemplateIdUsed") == null) {
					request.setTemplateId("");
				}
				if (request.getStatus().equalsIgnoreCase("Success")
						|| request.getStatus().equalsIgnoreCase("success")) {
					SimpleDateFormat format = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
					Timestamp d1 = rs.getTimestamp("end_date_of_processing");

					if (rs.getString("RequestType_Flag").equalsIgnoreCase("M")) {
						d = rs.getTimestamp("date_of_processing");
					} else {
						d = rs.getTimestamp("ScheduledTime");
					}
					String calElapsedTime = rs.getString("request_elapsed_time");

					String[] arr = calElapsedTime.split("\\.");
					logger.info(arr[0]);
					if (arr[0].length() == 1) {
						arr[0] = "0" + arr[0];
					}
					String elapsedtime = "";
					Integer mins = Integer.parseInt(arr[0]);
					String sec = arr[1];
					if (mins / 60 == 0) {
						elapsedtime = "00:" + arr[0] + ":" + arr[1];

					} else {
						Integer hrs = mins / 60;
						Integer minsConvert = mins % 60;
						elapsedtime = hrs.toString() + ":" + minsConvert.toString() + ":" + sec;

					}

					request.setElapsedTime(elapsedtime);
				} else if (request.getStatus().equalsIgnoreCase("Scheduled")) {
					SimpleDateFormat format = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
					Timestamp scheduledTime = rs.getTimestamp("ScheduledTime");

					/*
					 * Date d1_d = null; Date d2_d = null;
					 * 
					 * //d1_d = format.parse((covnertTStoString(d)));
					 * 
					 * d2_d = format.parse((covnertTStoString(d1)));
					 * 
					 * String scheduledTime = null; // in milliseconds //long diff = d2_d.getTime()
					 * - d1_d.getTime();
					 * 
					 * long diffSeconds = diff / 1000 % 60; long diffMinutes = diff / (60 * 1000) %
					 * 60; long diffHours = diff / (60 * 60 * 1000) % 24; long diffDays = diff / (24
					 * * 60 * 60 * 1000);
					 * 
					 * long dayTohours = diffDays * 24;
					 * 
					 * DecimalFormat formatter = new DecimalFormat("00"); String sec =
					 * formatter.format(diffSeconds); String min = formatter.format(diffMinutes);
					 * String hrs = formatter.format(diffHours + dayTohours);
					 * 
					 * scheduledTime = hrs + ":" + min + ":" + sec;
					 */
					if (scheduledTime != null) {
						request.setScheduledTime(covnertTStoString(scheduledTime));
					}

				} else {
					request.setElapsedTime("00:00:00");

				}

				String subQueryOne = "select*from misarpeso where request_info_id=" + id;
				Statement smt = connection.createStatement();
				ResultSet rsnew = smt.executeQuery(subQueryOne);

				if (rsnew != null) {
					MisArPeSO mis = new MisArPeSO();
					while (rsnew.next()) {
						mis.setFastEthernetIp(rsnew.getString("fastEthernetIp"));
						mis.setRouterVrfVpnDGateway(rsnew.getString("routerVrfVpnDGateway"));
						mis.setRouterVrfVpnDIp(rsnew.getString("routerVrfVpnDIp"));
					}
					request.setMisArPeSO(mis);
				}

				String subQueryTwo = "select*from internetlcvrfso where request_info_id=" + id;
				Statement smt1 = connection.createStatement();
				ResultSet rsnew1 = smt1.executeQuery(subQueryTwo);

				if (rsnew1 != null) {
					InternetLcVrfSO iis = new InternetLcVrfSO();
					while (rsnew1.next()) {
						iis.setNetworkIp(rsnew1.getString("networkIp"));
						iis.setBgpASNumber(rsnew1.getString("asNumber"));
						iis.setNeighbor1(rsnew1.getString("neighbor1"));
						iis.setNeighbor2(rsnew1.getString("neighbor2"));
						iis.setNeighbor1_remoteAS(rsnew1.getString("neighbor1_remoteAS"));
						iis.setNeighbor2_remoteAS(rsnew1.getString("neighbor2_remoteAS"));
						iis.setRoutingProtocol(rsnew1.getString("routingProtocol"));
						iis.setNetworkIp_subnetMask(rsnew1.getString("networkIp_subnetMask"));

					}
					request.setInternetLcVrf(iis);
				}

				String subQueryThree = "select*from deviceinterfaceso where request_info_id=" + id;
				Statement smt3 = connection.createStatement();
				ResultSet rsnew3 = smt3.executeQuery(subQueryThree);

				if (rsnew3 != null) {
					DeviceInterfaceSO iisd = new DeviceInterfaceSO();
					while (rsnew3.next()) {
						iisd.setDescription(rsnew3.getString("description"));
						iisd.setIp(rsnew3.getString("ip"));
						iisd.setEncapsulation(rsnew3.getString("encapsulation"));
						iisd.setMask(rsnew3.getString("mask"));
						iisd.setName(rsnew3.getString("name"));
						iisd.setSpeed(rsnew3.getString("speed"));
						iisd.setBandwidth(rsnew3.getString("Bandwidth"));
					}
					request.setDeviceInterfaceSO(iisd);
					if (rs.getString("RequestOwner") != null) {
						request.setRequest_assigned_to(rs.getString("RequestOwner"));
					} else {
						request.setRequest_assigned_to("seuser");

					}

					request.setZipcode(rs.getString("zipcode"));
					request.setManaged(rs.getString("managed"));
					request.setDownTimeRequired(rs.getString("downtimeRequired"));
					request.setLastUpgradedOn(rs.getString("lastUpgradedOn"));

					requestInfoList1.add(request);
				}

			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			DBUtil.close(rs);
			DBUtil.close(statement);
			DBUtil.close(connection);
		}

		return requestInfoList1;

	}

	public List<RequestInfoSO> getDatasToCompareForRequestfromDB(String id) {
		List<RequestInfoSO> list = null;
		connection = ConnectionFactory.getConnection();
		String query1 = "SELECT * FROM requestinfo.requestinfoso WHERE alphanumeric_req_id = ? ORDER BY date_of_processing DESC LIMIT 0,2";

		RequestInfoSO request;
		ResultSet rs = null;
		int id1;
		try {
			list = new ArrayList<RequestInfoSO>();

			PreparedStatement ps = connection.prepareStatement(query1);

			ps.setString(1, id);

			rs = ps.executeQuery();
			while (rs.next()) {
				request = new RequestInfoSO();
				id1 = (rs.getInt("request_info_id"));
				request.setOs(rs.getString("Os"));
				request.setBanner(rs.getString("banner"));
				request.setDeviceName(rs.getString("device_name"));
				request.setModel(rs.getString("model"));
				request.setRegion(rs.getString("region"));
				request.setService(rs.getString("service"));
				request.setHostname(rs.getString("hostname"));
				request.setOsVersion(rs.getString("os_version"));
				request.setEnablePassword(rs.getString("enable_password"));
				request.setVrfName(rs.getString("vrf_name"));
				request.setIsAutoProgress(rs.getBoolean("isAutoProgress"));

				Timestamp d = rs.getTimestamp("date_of_processing");

				request.setDateOfProcessing((covnertTStoString(d)));
				request.setVendor(rs.getString("vendor"));
				request.setCustomer(rs.getString("customer"));
				request.setSiteid(rs.getString("siteid"));
				request.setStatus(rs.getString("request_status"));
				request.setManagementIp(rs.getString("ManagementIP"));
				request.setDisplay_request_id(rs.getString("alphanumeric_req_id"));
				request.setDeviceType(rs.getString("device_type"));
				request.setVpn(rs.getString("vpn"));
				request.setRequest_id(rs.getInt("request_info_id"));

				request.setRequest_version(rs.getDouble("request_version"));
				request.setRequest_parent_version(rs.getDouble("request_parent_version"));
				request.setSnmpHostAddress(rs.getString("snmpHostAddress"));
				request.setSnmpString(rs.getString("snmpString"));
				request.setLoopBackType(rs.getString("loopBackType"));
				request.setLoopbackIPaddress(rs.getString("loopbackIPaddress"));
				request.setLoopbackSubnetMask(rs.getString("loopbackSubnetMask"));
				if (request.getStatus().equalsIgnoreCase("Success")
						|| request.getStatus().equalsIgnoreCase("success")) {
					SimpleDateFormat format = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
					Timestamp d1 = rs.getTimestamp("end_date_of_processing");

					Date d1_d = null;
					Date d2_d = null;

					d1_d = format.parse((covnertTStoString(d)));

					d2_d = format.parse((covnertTStoString(d1)));

					String elapsedtime = null;
					// in milliseconds
					long diff = d2_d.getTime() - d1_d.getTime();

					long diffSeconds = diff / 1000 % 60;
					long diffMinutes = diff / (60 * 1000) % 60;
					long diffHours = diff / (60 * 60 * 1000) % 24;
					long diffDays = diff / (24 * 60 * 60 * 1000);

					/*
					 * long daymin = diffDays * 1440; long hourmin = diffHours * 60; long secmin =
					 * (long) (diffSeconds * 0.016); long totalMins = daymin + hourmin + diffMinutes
					 * + secmin;
					 */

					long dayTohours = diffDays * 24;

					DecimalFormat formatter = new DecimalFormat("00");
					String sec = formatter.format(diffSeconds);
					String min = formatter.format(diffMinutes);
					String hrs = formatter.format(diffHours + dayTohours);

					elapsedtime = hrs + ":" + min + ":" + sec;

					request.setElapsedTime(elapsedtime);
				} else {
					request.setElapsedTime("00:00:00");

				}

				String subQueryOne = "select*from misarpeso where request_info_id = ?";
				ResultSet rsnew = null;
				PreparedStatement ps1 = connection.prepareStatement(subQueryOne);

				ps1.setString(1, Integer.toString(id1));
				rsnew = ps1.executeQuery();
				if (rsnew != null) {
					MisArPeSO mis = new MisArPeSO();
					while (rsnew.next()) {
						mis.setFastEthernetIp(rsnew.getString("fastEthernetIp"));
						mis.setRouterVrfVpnDGateway(rsnew.getString("routerVrfVpnDGateway"));
						mis.setRouterVrfVpnDIp(rsnew.getString("routerVrfVpnDIp"));
					}
					request.setMisArPeSO(mis);
				}

				String subQueryTwo = "select*from internetlcvrfso where request_info_id= ?";
				ResultSet rsnew1 = null;
				PreparedStatement ps2 = connection.prepareStatement(subQueryTwo);

				ps2.setString(1, Integer.toString(id1));
				rsnew1 = ps2.executeQuery();
				if (rsnew1 != null) {
					InternetLcVrfSO iis = new InternetLcVrfSO();
					while (rsnew1.next()) {
						iis.setNetworkIp(rsnew1.getString("networkIp"));
						iis.setBgpASNumber(rsnew1.getString("asNumber"));
						iis.setNeighbor1(rsnew1.getString("neighbor1"));
						iis.setNeighbor2(rsnew1.getString("neighbor2"));
						iis.setNeighbor1_remoteAS(rsnew1.getString("neighbor1_remoteAS"));
						iis.setNeighbor2_remoteAS(rsnew1.getString("neighbor2_remoteAS"));
						iis.setRoutingProtocol(rsnew1.getString("routingProtocol"));
						iis.setNetworkIp_subnetMask(rsnew1.getString("networkIp_subnetMask"));

					}
					request.setInternetLcVrf(iis);
				}

				String subQueryThree = "select*from deviceinterfaceso where request_info_id= ?";
				ResultSet rsnew3 = null;
				PreparedStatement ps3 = connection.prepareStatement(subQueryThree);

				ps3.setString(1, Integer.toString(id1));
				rsnew3 = ps3.executeQuery();

				if (rsnew3 != null) {
					DeviceInterfaceSO iisd = new DeviceInterfaceSO();
					while (rsnew3.next()) {
						iisd.setDescription(rsnew3.getString("description"));
						iisd.setIp(rsnew3.getString("ip"));
						iisd.setEncapsulation(rsnew3.getString("encapsulation"));
						iisd.setMask(rsnew3.getString("mask"));
						iisd.setName(rsnew3.getString("name"));
						iisd.setSpeed(rsnew3.getString("speed"));
						iisd.setBandwidth(rsnew3.getString("Bandwidth"));
					}
					request.setDeviceInterfaceSO(iisd);

					list.add(request);
				}
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			DBUtil.close(rs);
			DBUtil.close(statement);
			DBUtil.close(connection);
		}
		return list;
	}

	public final List<ModifyConfigResultPojo> getConfigCmdRecordFordata(RequestInfoSO requestInfoSO, String key)
			throws IOException {

		connection = ConnectionFactory.getConnection();
		String query1 = "SELECT * FROM requestinfo.createsshconfig WHERE Vendor=? AND Device_Type=? AND Model=? AND OS=? AND OS_Version=? AND Assigned_Field_Name=?";
		ResultSet rs = null;
		ModifyConfigResultPojo configCmdPojo = null;
		List<ModifyConfigResultPojo> configCmdList = null;
		try {
			configCmdList = new ArrayList<ModifyConfigResultPojo>();

			PreparedStatement ps = connection.prepareStatement(query1);

			ps.setString(1, requestInfoSO.getVendor());
			ps.setString(2, requestInfoSO.getDeviceType());
			ps.setString(3, requestInfoSO.getModel());
			ps.setString(4, requestInfoSO.getOs());
			ps.setString(5, requestInfoSO.getOsVersion());
			ps.setString(6, key);

			rs = ps.executeQuery();

			while (rs.next()) {
				configCmdPojo = new ModifyConfigResultPojo();
				configCmdPojo.setNo_SSH_Command(rs.getString("No_SSH_Command"));
				configCmdPojo.setCreate_SSH_Command(rs.getString("Create_SSH_Command"));
				configCmdList.add(configCmdPojo);
			}

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			DBUtil.close(rs);
			DBUtil.close(statement);
			DBUtil.close(connection);
		}

		return configCmdList;
	}

	public final String getLogedInUserDetail() throws IOException {

		connection = ConnectionFactory.getConnection();
		String query1 = "SELECT * FROM requestinfo.users WHERE user_status=?";
		ResultSet rs = null;
		UserPojo user = null;
		try {
			PreparedStatement ps = connection.prepareStatement(query1);
			ps.setInt(1, 1);
			rs = ps.executeQuery();
			user = new UserPojo();
			while (rs.next()) {
				user.setUsername(rs.getString("user_name"));
				user.setPassword(rs.getString("user_password"));
			}

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			DBUtil.close(rs);
			DBUtil.close(statement);
			DBUtil.close(connection);
		}

		return user.getUsername();
	}

	public final boolean resetUsersDB(String username) {
		connection = ConnectionFactory.getConnection();
		boolean result = false;
		String query1 = "update requestinfo.users set user_status=0 WHERE user_name=?";
		try {
			PreparedStatement ps = connection.prepareStatement(query1);

			ps.setString(1, username);

			int i = ps.executeUpdate();
			if (i == 1) {
				result = true;

			} else {
				result = false;
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return result;
	}

	public int getTotalRequestsFromDB() {
		int num = 0;
		connection = ConnectionFactory.getConnection();
		ResultSet rs = null;
		String query1 = "SELECT COUNT(request_info_id) AS total FROM requestinfoso";
		try {
			Statement ps = connection.createStatement();

			rs = ps.executeQuery(query1);
			while (rs.next()) {
				num = rs.getInt("total");
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return num;
	}

	public int getSuccessRequestsFromDB() {
		int num = 0;
		connection = ConnectionFactory.getConnection();
		String query1 = null;
		ResultSet rs = null;
		if (Global.loggedInUser.equalsIgnoreCase("seuser")) {
			query1 = "SELECT COUNT(request_info_id) AS total FROM requestinfoso Where request_status = 'success' and request_creator_name=? and alphanumeric_req_id rlike'SR|OS'";
		} else if (Global.loggedInUser.equalsIgnoreCase("feuser")) {
			query1 = "SELECT COUNT(request_info_id) AS total FROM requestinfoso Where request_status = 'success' and RequestOwner=? and alphanumeric_req_id rlike'SR|OS'";

		} else if (Global.loggedInUser.equalsIgnoreCase("admin")) {
			query1 = "SELECT COUNT(request_info_id) AS total FROM requestinfoso Where request_status = 'success' and alphanumeric_req_id rlike'SR|OS'";

		}
		try {
			if (!Global.loggedInUser.equalsIgnoreCase("admin")) {
				PreparedStatement ps = connection.prepareStatement(query1);
				ps.setString(1, Global.loggedInUser);
				rs = ps.executeQuery();
			} else {
				Statement smt = connection.createStatement();
				rs = smt.executeQuery(query1);
			}

			while (rs.next()) {
				num = rs.getInt("total");
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return num;
	}

	public int getFailureRequestsFromDB() {
		int num = 0;
		connection = ConnectionFactory.getConnection();
		String query1 = null;
		ResultSet rs = null;
		if (Global.loggedInUser.equalsIgnoreCase("seuser")) {
			query1 = "SELECT COUNT(request_info_id) AS total FROM requestinfoso Where request_status = 'failure' and request_creator_name=? and alphanumeric_req_id rlike'SR|OS'";

		} else if (Global.loggedInUser.equalsIgnoreCase("feuser")) {
			query1 = "SELECT COUNT(request_info_id) AS total FROM requestinfoso Where request_status = 'failure' and RequestOwner=? and alphanumeric_req_id rlike'SR|OS'";

		} else if (Global.loggedInUser.equalsIgnoreCase("admin")) {
			query1 = "SELECT COUNT(request_info_id) AS total FROM requestinfoso Where request_status = 'failure' and alphanumeric_req_id rlike'SR|OS'";

		}
		try {
			if (!Global.loggedInUser.equalsIgnoreCase("admin")) {
				PreparedStatement ps = connection.prepareStatement(query1);
				ps.setString(1, Global.loggedInUser);
				rs = ps.executeQuery();
			} else {
				Statement smt = connection.createStatement();
				rs = smt.executeQuery(query1);
			}
			while (rs.next()) {
				num = rs.getInt("total");
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return num;
	}

	public int getInProgressRequestsFromDB() {
		int num = 0;
		connection = ConnectionFactory.getConnection();
		ResultSet rs = null;
		String query1 = null;
		if (Global.loggedInUser.equalsIgnoreCase("seuser")) {
			query1 = "SELECT COUNT(request_info_id) AS total FROM requestinfoso Where request_status = 'In Progress' and request_creator_name=? and alphanumeric_req_id rlike'SR|OS'";
		} else if (Global.loggedInUser.equalsIgnoreCase("feuser")) {
			query1 = "SELECT COUNT(request_info_id) AS total FROM requestinfoso Where request_status = 'In Progress' and RequestOwner=? and alphanumeric_req_id rlike'SR|OS'";

		} else if (Global.loggedInUser.equalsIgnoreCase("admin")) {
			query1 = "SELECT COUNT(request_info_id) AS total FROM requestinfoso Where request_status = 'In Progress' and alphanumeric_req_id rlike'SR|OS'";

		}

		try {
			if (!Global.loggedInUser.equalsIgnoreCase("admin")) {
				PreparedStatement ps = connection.prepareStatement(query1);
				ps.setString(1, Global.loggedInUser);
				rs = ps.executeQuery();
			} else {
				Statement smt = connection.createStatement();
				rs = smt.executeQuery(query1);
			}
			while (rs.next()) {
				num = rs.getInt("total");
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return num;
	}

	public final boolean updateEIPAMRecord(String customer, String site, String ip, String mask) {
		connection = ConnectionFactory.getConnection();
		boolean result = false;
		String query1 = "update requestinfo.eipamdbtable set eipam_ip=?,eipam_subnet_mask=?,eipam_ip_status=? WHERE eipam_customer_name=? AND eipam_site_id=?";
		try {
			PreparedStatement ps = connection.prepareStatement(query1);

			ps.setString(1, ip);
			ps.setString(2, mask);
			ps.setInt(3, 0);
			ps.setString(4, customer);
			ps.setString(5, site);

			int i = ps.executeUpdate();
			if (i == 1) {
				// updateEIPAMTable(ip);
				result = true;

			} else {
				result = false;
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return result;
	}

	public final boolean addEIPAMRecord(String customer, String site, String ip, String mask, String service,
			String region) {
		connection = ConnectionFactory.getConnection();
		boolean result = false;

		String query1 = "INSERT INTO eipamdbtable(eipam_ip,eipam_subnet_mask,eipam_ip_status,eipam_customer_name,eipam_site_id,eipam_region,eipam_service)"
				+ "VALUES(?,?,?,?,?,?,?)";
		try {
			PreparedStatement ps = connection.prepareStatement(query1);

			ps.setString(1, ip);
			ps.setString(2, mask);
			ps.setInt(3, 0);
			ps.setString(4, customer);
			ps.setString(5, site);
			ps.setString(6, region);
			ps.setString(7, service);

			int i = ps.executeUpdate();
			if (i == 1) {
				result = true;

			} else {
				result = false;
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return result;
	}

	public String calcTimeDiffInMins(Timestamp t1, Timestamp t2) {

		// create a calendar and assign it the same time
		Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(t1.getTime());

		// add a bunch of seconds to the calendar
		cal.add(Calendar.SECOND, 98765);

		// get time difference in seconds
		long milliseconds = t1.getTime() - t2.getTime();
		/*
		 * int seconds = (int) milliseconds / 1000; int minutes = (seconds % 3600) / 60;
		 * String str = minutes + "." + seconds;
		 */
		logger.info("we are here" + milliseconds);
		return String.format("%02d.%02d", TimeUnit.MILLISECONDS.toMinutes(milliseconds),
				TimeUnit.MILLISECONDS.toSeconds(milliseconds)
						- TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(milliseconds)));
	}

	public Map<String, String> getRequestFlag(String requestId, double version) {
		connection = ConnectionFactory.getConnection();
		String query = "";
		ResultSet rs = null;
		PreparedStatement preparedStmt;
		String flagForPrevalidation = "";
		String flagFordelieverConfig = "";
		Map<String, String> hmap = new HashMap<String, String>();
		DecimalFormat numberFormat = new DecimalFormat("#.0");
		String parentVersion = numberFormat.format(version - 0.1);
		query = "select application_test,deliever_config from  webserviceinfo where alphanumeric_req_id = ? and version = ? ";
		try {
			preparedStmt = connection.prepareStatement(query);
			preparedStmt.setString(1, requestId);
			preparedStmt.setString(2, parentVersion);
			rs = preparedStmt.executeQuery();
			if (rs != null) {
				while (rs.next()) {
					flagForPrevalidation = rs.getString("application_test");
					flagFordelieverConfig = rs.getString("deliever_config");
				}
			}
			hmap.put("flagForPrevalidation", flagForPrevalidation);
			hmap.put("flagFordelieverConfig", flagFordelieverConfig);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return hmap;
	}

	public boolean addCertificationTestForRequest(String alphanumeric_req_id, String request_version,
			String Device_Reachability_Test) {
		connection = ConnectionFactory.getConnection();
		String sql = "INSERT INTO CertificationTestValidation(Device_Reachability_Test,Vendor_Test,Device_Model_Test,IOSVersion_Test,PreValidation_Test,ShowIpIntBrief_Cmd,ShowInterface_Cmd,ShowVersion_Cmd,Network_Test,showIpBgpSummary_Cmd,Throughput_Test,FrameLoss_Test,Latency_Test,HealthCheck_Test,alphanumeric_req_id,version,suggestionForFailure)"
				+ "VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
		String suggestion = "NA";
		if (Device_Reachability_Test.equalsIgnoreCase("2")) {
			suggestion = "Please check the device connectivity";
		}
		if (Device_Reachability_Test.equalsIgnoreCase("2_Authentication")) {
			Device_Reachability_Test = "2";
			suggestion = "Please check the router credentials";
		}

		try {
			PreparedStatement ps = connection.prepareStatement(sql);

			ps.setInt(1, Integer.parseInt(Device_Reachability_Test));
			ps.setInt(2, 0);
			ps.setInt(3, 0);
			ps.setInt(4, 0);
			ps.setInt(5, 0);
			ps.setInt(6, 0);
			ps.setInt(7, 0);
			ps.setInt(8, 0);
			ps.setInt(9, 0);
			ps.setInt(10, 0);
			ps.setInt(11, 0);
			ps.setInt(12, 0);
			ps.setInt(13, 0);
			ps.setInt(14, 0);
			ps.setString(15, alphanumeric_req_id);
			ps.setString(16, request_version);
			ps.setString(17, suggestion);

			int i = ps.executeUpdate();
			if (i == 1) {
				return true;

			}
		} catch (SQLException e) {
			logger.info("Error:> " + e.getMessage());
		}
		return false;
	}

	public void updatePrevalidationStatus(String requestId, String version, int vendorflag, int versionflag,
			int modelflag) {
		connection = ConnectionFactory.getConnection();
		String query = null;
		String suggestion = "NA";
		query = "update CertificationTestValidation set Vendor_Test = ?,IOSVersion_Test = ?,Device_Model_Test  = ?,suggestionForFailure =? where alphanumeric_req_id = ? and version = ? ";
		if (vendorflag == 2) {
			suggestion = "Please select the correct Vendor from C3P GUI";
		}
		if (versionflag == 2) {
			suggestion = "Please select the correct IOS Version from C3P GUI";
		}
		if (modelflag == 2) {
			suggestion = "Please select the correct router model from C3P GUI";
		}
		PreparedStatement preparedStmt;
		try {
			preparedStmt = connection.prepareStatement(query);
			preparedStmt.setInt(1, vendorflag);
			preparedStmt.setInt(2, versionflag);
			preparedStmt.setInt(3, modelflag);
			preparedStmt.setString(4, suggestion);
			preparedStmt.setString(5, requestId);
			preparedStmt.setString(6, version);

			preparedStmt.executeUpdate();
		} catch (SQLException e) {

			logger.error(e.getMessage());
		}
	}

	public void updatePrevalidationValues(String requestId, String version, String vendorActual, String vendorGui,
			String osActual, String osGui, String modelActual, String modelGui) {
		connection = ConnectionFactory.getConnection();
		String query = null;
		String suggestion = "NA";
		query = "update CertificationTestValidation set actual_vendor = ?,gui_vendor = ?,actual_os_version  = ?,gui_os_version =?,actual_model=?,gui_model=? where alphanumeric_req_id = ? and version = ? ";

		PreparedStatement preparedStmt;
		try {
			preparedStmt = connection.prepareStatement(query);
			preparedStmt.setString(1, vendorActual);
			preparedStmt.setString(2, vendorGui);
			preparedStmt.setString(3, osActual);
			preparedStmt.setString(4, osGui);
			preparedStmt.setString(5, modelActual);
			preparedStmt.setString(6, modelGui);

			preparedStmt.setString(7, requestId);
			preparedStmt.setString(8, version);
			preparedStmt.executeUpdate();
		} catch (SQLException e) {

			logger.error(e.getMessage());
		}
	}

	public CertificationTestPojo getCertificationTestFlagData(String requestId, String version, String TestType) {
		connection = ConnectionFactory.getConnection();
		CertificationTestPojo certificationTestPojo = new CertificationTestPojo();
		String query = "";
		ResultSet rs = null;
		PreparedStatement preparedStmt;
		String flagForPrevalidation = "";
		String flagFordelieverConfig = "";
		Map<String, String> hmap = new HashMap<String, String>();

		query = "select * from  CertificationTestValidation where alphanumeric_req_id = ? and version = ? ";
		try {
			preparedStmt = connection.prepareStatement(query);
			preparedStmt.setString(1, requestId);
			preparedStmt.setString(2, version);
			rs = preparedStmt.executeQuery();

			if (TestType.equalsIgnoreCase("preValidate") && rs != null) {
				while (rs.next()) {
					certificationTestPojo.setDeviceReachabilityTest(rs.getString("Device_Reachability_Test"));
					certificationTestPojo.setVendorTest(rs.getString("Vendor_Test"));
					certificationTestPojo.setDeviceModelTest(rs.getString("Device_Model_Test"));
					certificationTestPojo.setIosVersionTest(rs.getString("IOSVersion_Test"));
				}
			}
			if (TestType.equalsIgnoreCase("networkTest") && rs != null) {
				while (rs.next()) {
					certificationTestPojo.setShowIpIntBriefCmd(rs.getString("ShowIpIntBrief_Cmd"));
					certificationTestPojo.setShowInterfaceCmd(rs.getString("ShowInterface_Cmd"));
					certificationTestPojo.setShowVersionCmd(rs.getString("ShowVersion_Cmd"));
					certificationTestPojo.setShowIpBgpSummaryCmd(rs.getString("showIpBgpSummary_Cmd"));
				}
			}
			if (TestType.equalsIgnoreCase("HealthTest") && rs != null) {
				while (rs.next()) {
					certificationTestPojo.setThroughputTest(rs.getString("Throughput_Test"));
					certificationTestPojo.setFrameLossTest(rs.getString("FrameLoss_Test"));
					certificationTestPojo.setLatencyTest(rs.getString("Latency_Test"));
					certificationTestPojo.setThroughput(rs.getString("throughput"));
					certificationTestPojo.setFrameLoss(rs.getString("frameLoss"));
					certificationTestPojo.setLatency(rs.getString("latency"));

				}

			}
			if (TestType.equalsIgnoreCase("FinalReport") && rs != null) {
				while (rs.next()) {

					certificationTestPojo.setSuggestion(rs.getString("suggestionForFailure"));

				}
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			DBUtil.close(rs);
			DBUtil.close(statement);
			// DBUtil.close(connection);
		}
		return certificationTestPojo;
	}

	public void updateHealthCheckTestStatus(String requestId, String version, int throughputflag, int framelossflag,
			int latencyflag) {
		connection = ConnectionFactory.getConnection();
		String query = null;

		query = "update CertificationTestValidation set Throughput_Test = ?,FrameLoss_Test = ?,Latency_Test  = ? where alphanumeric_req_id = ? and version = ? ";

		PreparedStatement preparedStmt;
		try {
			preparedStmt = connection.prepareStatement(query);
			preparedStmt.setInt(1, throughputflag);
			preparedStmt.setInt(2, framelossflag);
			preparedStmt.setInt(3, latencyflag);
			preparedStmt.setString(4, requestId);
			preparedStmt.setString(5, version);
			preparedStmt.executeUpdate();
		} catch (SQLException e) {

			logger.error(e.getMessage());
		}
	}

	public void updateNetworkAuditTestStatus(String requestId, String version, int throughputflag, int framelossflag,
			int latencyflag) {
		connection = ConnectionFactory.getConnection();
		String query = null;

		query = "update CertificationTestValidation set Throughput_Test = ?,FrameLoss_Test = ?,Latency_Test  = ? where alphanumeric_req_id = ? and version = ? ";

		PreparedStatement preparedStmt;
		try {
			preparedStmt = connection.prepareStatement(query);
			preparedStmt.setInt(1, throughputflag);
			preparedStmt.setInt(2, framelossflag);
			preparedStmt.setInt(3, latencyflag);
			preparedStmt.setString(4, requestId);
			preparedStmt.setString(5, version);
			preparedStmt.executeUpdate();
		} catch (SQLException e) {

			logger.error(e.getMessage());
		}
	}

	public void updateHealthCheckTestParameter(String requestId, String version, String value, String type) {
		connection = ConnectionFactory.getConnection();
		String query = null;

		if (type.equalsIgnoreCase("frameloss")) {
			query = "update CertificationTestValidation set frameloss = ? where alphanumeric_req_id = ? and version = ? ";
		} else if (type.equalsIgnoreCase("latency")) {
			query = "update CertificationTestValidation set latency = ? where alphanumeric_req_id = ? and version = ? ";
		} else {
			query = "update CertificationTestValidation set throughput = ? where alphanumeric_req_id = ? and version = ? ";
		}
		PreparedStatement preparedStmt;
		try {
			preparedStmt = connection.prepareStatement(query);
			preparedStmt.setString(1, value);
			preparedStmt.setString(2, requestId);
			preparedStmt.setString(3, version);
			preparedStmt.executeUpdate();
		} catch (SQLException e) {

			logger.error(e.getMessage());
		}
	}

	public void updateNetworkTestStatus(String requestId, String version, int ipInterfaceBriefflag,
			int interfacedetailflag, int versionDetailflag, int showIpBgpSummaryflag) {
		connection = ConnectionFactory.getConnection();
		String query = null;

		query = "update CertificationTestValidation set ShowIpIntBrief_Cmd = ?,ShowInterface_Cmd = ?,ShowVersion_Cmd  = ?,showIpBgpSummary_Cmd=? where alphanumeric_req_id = ? and version = ? ";

		PreparedStatement preparedStmt;
		try {
			preparedStmt = connection.prepareStatement(query);
			preparedStmt.setInt(1, ipInterfaceBriefflag);
			preparedStmt.setInt(2, interfacedetailflag);
			preparedStmt.setInt(3, versionDetailflag);
			preparedStmt.setInt(4, showIpBgpSummaryflag);
			preparedStmt.setString(5, requestId);
			preparedStmt.setString(6, version);
			preparedStmt.executeUpdate();
		} catch (SQLException e) {

			logger.error(e.getMessage());
		}
	}

	public JSONArray getColumnChartData() {
		ArrayList<ColumnChartPojo> list = new ArrayList<ColumnChartPojo>();
		connection = ConnectionFactory.getConnection();
		ResultSet rs = null;
		String query = "";
		JSONArray resultJSONArray = null;
		int[] successData = new int[] { 0, 0, 0, 0, 0, 0, 0 };
		int[] failureData = new int[] { 0, 0, 0, 0, 0, 0, 0 };
		int[] inProgressData = new int[] { 0, 0, 0, 0, 0, 0, 0 };

		int[] scheduledData = new int[] { 0, 0, 0, 0, 0, 0, 0 };

		int[] holdData = new int[] { 0, 0, 0, 0, 0, 0, 0 };

		Calendar now = Calendar.getInstance();
		SimpleDateFormat format = new SimpleDateFormat("MM/dd");
		String[] days = new String[7];
		int delta = -now.get(GregorianCalendar.DAY_OF_WEEK) + 2; // add 2 if
																	// your week
																	// start on
																	// monday
		List<String> daysArray = new ArrayList<String>();
		query = "select request_status,date_of_processing,request_creator_name,WEEKDAY(date_of_processing) AS DAYS from requestinfoso join (select alphanumeric_req_id, max(date_of_processing) as max_dt,WEEKDAY(date_of_processing) AS DAYS from requestinfoso WHERE YEARWEEK(date_of_processing)=YEARWEEK(NOW()) group by alphanumeric_req_id)t on requestinfoso.alphanumeric_req_id= t.alphanumeric_req_id and requestinfoso.date_of_processing = t.max_dt";
		// query="SELECT request_status,alphanumeric_req_id,WEEKDAY(date_of_processing)
		// AS DAYS FROM requestinfoso WHERE YEARWEEK(date_of_processing)=YEARWEEK(NOW())
		// GROUP BY alphanumeric_req_id ORDER BY date_of_processing DESC";
		// query =
		// "SELECT request_status,date_of_processing,WEEKDAY(date_of_processing) AS DAYS
		// FROM requestinfoso WHERE YEARWEEK(date_of_processing)=YEARWEEK(NOW())";
		// query =
		// "SELECT request_status,date_of_processing,WEEKDAY(date_of_processing) AS DAYS
		// FROM requestinfoso WHERE date_of_processing between date_sub(now(),INTERVAL 1
		// WEEK) and NOW()";

		try {
			Statement ps = connection.createStatement();
			int DayNumber, count = 0;
			rs = ps.executeQuery(query);
			while (rs.next()) {
				if (rs.getString("request_creator_name").equalsIgnoreCase(Global.loggedInUser)) {
					if (rs.getString("request_status").equalsIgnoreCase("success")) {
						if (rs.getInt("DAYS") == 0) {
							successData[0] = successData[0] + 1;
							count++;
						} else if (rs.getInt("DAYS") == 1) {
							successData[1] = successData[1] + 1;
							count++;
						} else if (rs.getInt("DAYS") == 2) {
							successData[2] = successData[2] + 1;
							count++;
						} else if (rs.getInt("DAYS") == 3) {
							successData[3] = successData[3] + 1;
							count++;
						} else if (rs.getInt("DAYS") == 4) {
							successData[4] = successData[4] + 1;
							count++;
						} else if (rs.getInt("DAYS") == 5) {
							successData[5] = successData[5] + 1;
							count++;
						} else if (rs.getInt("DAYS") == 6) {
							successData[6] = successData[6] + 1;
							count++;
						}
					} else if (rs.getString("request_status").equalsIgnoreCase("failure")) {
						if (rs.getInt("DAYS") == 0) {
							failureData[0] = failureData[0] + 1;
							count++;
						} else if (rs.getInt("DAYS") == 1) {
							failureData[1] = failureData[1] + 1;
							count++;
						} else if (rs.getInt("DAYS") == 2) {
							failureData[2] = failureData[2] + 1;
							count++;
						} else if (rs.getInt("DAYS") == 3) {
							failureData[3] = failureData[3] + 1;
							count++;
						} else if (rs.getInt("DAYS") == 4) {
							failureData[4] = failureData[4] + 1;
							count++;
						} else if (rs.getInt("DAYS") == 5) {
							failureData[5] = failureData[5] + 1;
							count++;
						} else if (rs.getInt("DAYS") == 6) {
							failureData[6] = failureData[6] + 1;
							count++;
						}
					} else if (rs.getString("request_status").equalsIgnoreCase("in progress")) {
						if (rs.getInt("DAYS") == 0) {
							inProgressData[0] = inProgressData[0] + 1;
							count++;
						} else if (rs.getInt("DAYS") == 1) {
							inProgressData[1] = inProgressData[1] + 1;
							count++;
						} else if (rs.getInt("DAYS") == 2) {
							inProgressData[2] = inProgressData[2] + 1;
							count++;
						} else if (rs.getInt("DAYS") == 3) {
							inProgressData[3] = inProgressData[3] + 1;
							count++;
						} else if (rs.getInt("DAYS") == 4) {
							inProgressData[4] = inProgressData[4] + 1;
							count++;
						} else if (rs.getInt("DAYS") == 5) {
							inProgressData[5] = inProgressData[5] + 1;
							count++;
						} else if (rs.getInt("DAYS") == 6) {
							inProgressData[6] = inProgressData[6] + 1;
							count++;
						}
					} else if (rs.getString("request_status").equalsIgnoreCase("scheduled")) {
						if (rs.getInt("DAYS") == 0) {
							scheduledData[0] = scheduledData[0] + 1;
							count++;
						} else if (rs.getInt("DAYS") == 1) {
							scheduledData[1] = scheduledData[1] + 1;
							count++;
						} else if (rs.getInt("DAYS") == 2) {
							scheduledData[2] = scheduledData[2] + 1;
							count++;
						} else if (rs.getInt("DAYS") == 3) {
							scheduledData[3] = scheduledData[3] + 1;
							count++;
						} else if (rs.getInt("DAYS") == 4) {
							scheduledData[4] = scheduledData[4] + 1;
							count++;
						} else if (rs.getInt("DAYS") == 5) {
							scheduledData[5] = scheduledData[5] + 1;
							count++;
						} else if (rs.getInt("DAYS") == 6) {
							scheduledData[6] = scheduledData[6] + 1;
							count++;
						}
					} else if (rs.getString("request_status").equalsIgnoreCase("hold")) {
						if (rs.getInt("DAYS") == 0) {
							holdData[0] = holdData[0] + 1;
							count++;
						} else if (rs.getInt("DAYS") == 1) {
							holdData[1] = holdData[1] + 1;
							count++;
						} else if (rs.getInt("DAYS") == 2) {
							holdData[2] = holdData[2] + 1;
							count++;
						} else if (rs.getInt("DAYS") == 3) {
							holdData[3] = holdData[3] + 1;
							count++;
						} else if (rs.getInt("DAYS") == 4) {
							holdData[4] = holdData[4] + 1;
							count++;
						} else if (rs.getInt("DAYS") == 5) {
							holdData[5] = holdData[5] + 1;
							count++;
						} else if (rs.getInt("DAYS") == 6) {
							holdData[6] = holdData[6] + 1;
							count++;
						}
					}
				}
			}
			now.add(Calendar.DAY_OF_MONTH, delta);
			for (int i = 0; i < 7; i++) {
				days[i] = format.format(now.getTime());
				now.add(Calendar.DAY_OF_MONTH, 1);
			}
			daysArray = new ArrayList<String>(Arrays.asList(days));
			logger.info("DayCheck");
			JSONObject successObj = new JSONObject();
			successObj.put("name", "Success");
			successObj.put("data", successData);

			JSONObject successMain = new JSONObject();
			successMain.put("Success", successObj);

			JSONObject failureObj = new JSONObject();
			failureObj.put("name", "Failure");
			failureObj.put("data", failureData);

			JSONObject failureMain = new JSONObject();
			failureMain.put("Failure", failureObj);

			JSONObject inProgressObj = new JSONObject();
			inProgressObj.put("name", "In Progress");
			inProgressObj.put("data", inProgressData);

			JSONObject inProgressMain = new JSONObject();
			inProgressMain.put("InProgress", inProgressObj);

			JSONObject scheduledObj = new JSONObject();
			scheduledObj.put("name", "Scheduled");
			scheduledObj.put("data", scheduledData);

			JSONObject scheduledMain = new JSONObject();
			scheduledMain.put("Scheduled", scheduledObj);

			JSONObject holdObj = new JSONObject();
			holdObj.put("name", "Hold");
			holdObj.put("data", holdData);

			JSONObject holdMain = new JSONObject();
			holdMain.put("Hold", holdObj);

			JSONObject datesArray = new JSONObject();
			datesArray.put("Dates", daysArray);
			resultJSONArray = new JSONArray();
			resultJSONArray.put(successMain);
			resultJSONArray.put(failureMain);
			resultJSONArray.put(inProgressMain);
			resultJSONArray.put(scheduledMain);
			resultJSONArray.put(holdMain);
			resultJSONArray.put(datesArray);
			JSONObject countobj = new JSONObject();
			countobj.put("totalCount", count);
			resultJSONArray.put(countobj);

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return resultJSONArray;
	}

	public final List<ConfigurationDataValuePojo> getALLVendorData() throws IOException {
		connection = ConnectionFactory.getConnection();

		String query = "SELECT * FROM requestinfo.c3p_data_configuration_table where component_name='c3p_vendor'";

		EIPAMPojo pojo;
		ResultSet rs = null;
		List<ConfigurationDataValuePojo> list = null;
		try {
			ConfigurationDataValuePojo object;
			statement = connection.createStatement();
			rs = statement.executeQuery(query);
			list = new ArrayList<ConfigurationDataValuePojo>();
			while (rs.next()) {
				object = new ConfigurationDataValuePojo();
				object.setComponent_value(rs.getString("component_value"));
				object.setComponent_make(rs.getString("component_make"));
				list.add(object);
			}

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			DBUtil.close(rs);
			DBUtil.close(statement);
			DBUtil.close(connection);
		}

		return list;

	}

	public final List<ConfigurationDataValuePojo> getALLDeviceTypeData() throws IOException {
		connection = ConnectionFactory.getConnection();

		String query = "SELECT * FROM requestinfo.c3p_data_configuration_table where component_name='c3p_device_type'";

		EIPAMPojo pojo;
		ResultSet rs = null;
		List<ConfigurationDataValuePojo> list = null;
		ConfigurationDataValuePojo object;
		try {
			statement = connection.createStatement();
			rs = statement.executeQuery(query);
			list = new ArrayList<ConfigurationDataValuePojo>();
			while (rs.next()) {
				object = new ConfigurationDataValuePojo();
				object.setComponent_make(rs.getString("component_make"));
				object.setComponent_value(rs.getString("component_value"));
				list.add(object);
			}

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			DBUtil.close(rs);
			DBUtil.close(statement);
			DBUtil.close(connection);
		}

		return list;

	}

	public final List<String> getALLModelData(String vendor, String deviceType) throws IOException {
		connection = ConnectionFactory.getConnection();
		boolean result = false;
		String query1 = "SELECT * FROM requestinfo.c3p_data_configuration_table where component_name=? AND component_make=?";
		List<String> list = new ArrayList<String>();
		ResultSet rs = null;
		try {
			PreparedStatement ps = connection.prepareStatement(query1);

			ps.setString(1, "c3p_" + deviceType.toLowerCase() + "_model");
			ps.setString(2, vendor);

			rs = ps.executeQuery();
			while (rs.next()) {
				list.add(rs.getString("component_value"));
			}

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			DBUtil.close(rs);
			DBUtil.close(statement);
			DBUtil.close(connection);
		}

		return list;

	}

	public final List<String> getALLOSData(String make, String deviceType) throws IOException {
		connection = ConnectionFactory.getConnection();
		boolean result = false;
		String query1 = "SELECT * FROM requestinfo.c3p_data_configuration_table where component_name='c3p_os_type'";
		List<String> list = new ArrayList<String>();
		ResultSet rs = null;
		try {
			statement = connection.createStatement();
			rs = statement.executeQuery(query1);
			list = new ArrayList<String>();
			while (rs.next()) {
				if (rs.getString("component_make").equalsIgnoreCase(make)) {
					list.add(rs.getString("component_value"));
				}
			}

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			DBUtil.close(rs);
			DBUtil.close(statement);
			DBUtil.close(connection);
		}

		return list;

	}

	public final List<String> getALLOSVersionData(String os, String model) throws IOException {
		connection = ConnectionFactory.getConnection();
		boolean result = false;
		String query1 = "SELECT * FROM requestinfo.c3p_data_configuration_table where component_name='c3p_os_version'";
		List<String> list = new ArrayList<String>();
		List<String> listtoSend = new ArrayList<String>();

		ResultSet rs = null;
		try {
			statement = connection.createStatement();
			rs = statement.executeQuery(query1);
			list = new ArrayList<String>();
			while (rs.next()) {
				if (rs.getString("component_make").equalsIgnoreCase(os)) {
					list.add(rs.getString("component_value"));
				}
			}
			for (int i = 0; i < list.size(); i++) {
				if (model.equalsIgnoreCase("7200")) {
					if (list.get(i).equalsIgnoreCase("12.4")) {
						listtoSend.add(list.get(i));
					}
				} else {
					listtoSend.add(list.get(i));

				}
			}

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			DBUtil.close(rs);
			DBUtil.close(statement);
			DBUtil.close(connection);
		}

		return listtoSend;

	}

	public JSONArray getColumnChartDataMonthly() {
		ArrayList<ColumnChartPojo> list = new ArrayList<ColumnChartPojo>();
		connection = ConnectionFactory.getConnection();
		ResultSet rs = null;
		String query = "";
		JSONArray resultJSONArray = null;
		int[] successData = new int[] { 0, 0, 0, 0, 0, 0, 0 };
		int[] failureData = new int[] { 0, 0, 0, 0, 0, 0, 0 };
		int[] inProgressData = new int[] { 0, 0, 0, 0, 0, 0, 0 };
		Calendar now = Calendar.getInstance();
		SimpleDateFormat format = new SimpleDateFormat("MM/dd");
		String[] days = new String[7];
		int delta = -now.get(GregorianCalendar.DAY_OF_WEEK) + 2; // add 2 if
																	// your week
																	// start on
																	// monday
		List<String> daysArray = new ArrayList<String>();
		query = "select request_status,date_of_processing,dayofmonth(date_of_processing) AS DAYS from requestinfoso where month(date_of_processing) = 12";
		// query="SELECT request_status,alphanumeric_req_id,WEEKDAY(date_of_processing)
		// AS DAYS FROM requestinfoso WHERE YEARWEEK(date_of_processing)=YEARWEEK(NOW())
		// GROUP BY alphanumeric_req_id ORDER BY date_of_processing DESC";
		// query =
		// "SELECT request_status,date_of_processing,WEEKDAY(date_of_processing) AS DAYS
		// FROM requestinfoso WHERE YEARWEEK(date_of_processing)=YEARWEEK(NOW())";
		// query =
		// "SELECT request_status,date_of_processing,WEEKDAY(date_of_processing) AS DAYS
		// FROM requestinfoso WHERE date_of_processing between date_sub(now(),INTERVAL 1
		// WEEK) and NOW()";

		try {
			Statement ps = connection.createStatement();
			int DayNumber, count = 0;
			rs = ps.executeQuery(query);
			while (rs.next()) {
				if (rs.getString("request_status").equalsIgnoreCase("success")) {
					if (rs.getInt("DAYS") == 0) {
						successData[0] = successData[0] + 1;
						count++;
					} else if (rs.getInt("DAYS") == 1) {
						successData[1] = successData[1] + 1;
						count++;
					} else if (rs.getInt("DAYS") == 2) {
						successData[2] = successData[2] + 1;
						count++;
					} else if (rs.getInt("DAYS") == 3) {
						successData[3] = successData[3] + 1;
						count++;
					} else if (rs.getInt("DAYS") == 4) {
						successData[4] = successData[4] + 1;
						count++;
					} else if (rs.getInt("DAYS") == 5) {
						successData[5] = successData[5] + 1;
						count++;
					} else if (rs.getInt("DAYS") == 6) {
						successData[6] = successData[6] + 1;
						count++;
					} else if (rs.getInt("DAYS") == 7) {
						successData[6] = successData[7] + 1;
						count++;
					} else if (rs.getInt("DAYS") == 8) {
						successData[6] = successData[8] + 1;
						count++;
					} else if (rs.getInt("DAYS") == 9) {
						successData[6] = successData[9] + 1;
						count++;
					} else if (rs.getInt("DAYS") == 10) {
						successData[6] = successData[10] + 1;
						count++;
					}

				} else if (rs.getString("request_status").equalsIgnoreCase("failure")) {
					if (rs.getInt("DAYS") == 0) {
						failureData[0] = failureData[0] + 1;
						count++;
					} else if (rs.getInt("DAYS") == 1) {
						failureData[1] = failureData[1] + 1;
						count++;
					} else if (rs.getInt("DAYS") == 2) {
						failureData[2] = failureData[2] + 1;
						count++;
					} else if (rs.getInt("DAYS") == 3) {
						failureData[3] = failureData[3] + 1;
						count++;
					} else if (rs.getInt("DAYS") == 4) {
						failureData[4] = failureData[4] + 1;
						count++;
					} else if (rs.getInt("DAYS") == 5) {
						failureData[5] = failureData[5] + 1;
						count++;
					} else if (rs.getInt("DAYS") == 6) {
						failureData[6] = failureData[6] + 1;
						count++;
					}
				} else if (rs.getString("request_status").equalsIgnoreCase("in progress")) {
					if (rs.getInt("DAYS") == 0) {
						inProgressData[0] = inProgressData[0] + 1;
						count++;
					} else if (rs.getInt("DAYS") == 1) {
						inProgressData[1] = inProgressData[1] + 1;
						count++;
					} else if (rs.getInt("DAYS") == 2) {
						inProgressData[2] = inProgressData[2] + 1;
						count++;
					} else if (rs.getInt("DAYS") == 3) {
						inProgressData[3] = inProgressData[3] + 1;
						count++;
					} else if (rs.getInt("DAYS") == 4) {
						inProgressData[4] = inProgressData[4] + 1;
						count++;
					} else if (rs.getInt("DAYS") == 5) {
						inProgressData[5] = inProgressData[5] + 1;
						count++;
					} else if (rs.getInt("DAYS") == 6) {
						inProgressData[6] = inProgressData[6] + 1;
						count++;
					}
				}

			}
			now.add(Calendar.DAY_OF_MONTH, delta);
			for (int i = 0; i < 7; i++) {
				days[i] = format.format(now.getTime());
				now.add(Calendar.DAY_OF_MONTH, 1);
			}
			daysArray = new ArrayList<String>(Arrays.asList(days));
			JSONObject successObj = new JSONObject();
			successObj.put("name", "Success");
			successObj.put("data", successData);

			JSONObject successMain = new JSONObject();
			successMain.put("Success", successObj);

			JSONObject failureObj = new JSONObject();
			failureObj.put("name", "Failure");
			failureObj.put("data", failureData);

			JSONObject failureMain = new JSONObject();
			failureMain.put("Failure", failureObj);

			JSONObject inProgressObj = new JSONObject();
			inProgressObj.put("name", "In Progress");
			inProgressObj.put("data", inProgressData);

			JSONObject inProgressMain = new JSONObject();
			inProgressMain.put("InProgress", inProgressObj);

			JSONObject datesArray = new JSONObject();
			datesArray.put("Dates", daysArray);
			resultJSONArray = new JSONArray();
			resultJSONArray.put(successMain);
			resultJSONArray.put(failureMain);
			resultJSONArray.put(inProgressMain);
			resultJSONArray.put(datesArray);

			JSONObject countobj = new JSONObject();
			countobj.put("totalCount", count);
			resultJSONArray.put(countobj);

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return resultJSONArray;
	}

	public final List<ConfigurationDataValuePojo> getALLRegionData() throws IOException {
		connection = ConnectionFactory.getConnection();

		String query = "SELECT * FROM requestinfo.c3p_data_configuration_table where component_name='c3p_region'";

		EIPAMPojo pojo;
		ResultSet rs = null;
		List<ConfigurationDataValuePojo> list = null;
		try {
			ConfigurationDataValuePojo object;
			statement = connection.createStatement();
			rs = statement.executeQuery(query);
			list = new ArrayList<ConfigurationDataValuePojo>();
			while (rs.next()) {
				object = new ConfigurationDataValuePojo();
				object.setComponent_value(rs.getString("component_value"));
				object.setComponent_make(rs.getString("component_make"));
				list.add(object);
			}

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			DBUtil.close(rs);
			DBUtil.close(statement);
			DBUtil.close(connection);
		}

		return list;

	}

	public final List<ErrorValidationPojo> getAllErrorCodeFromRouter() throws IOException {
		connection = ConnectionFactory.getConnection();

		String query = "select * from requestinfo.errorcodedata where router_error_message is not null";
		ResultSet rs = null;
		List<ErrorValidationPojo> list = null;
		try {
			ErrorValidationPojo object;
			statement = connection.createStatement();
			rs = statement.executeQuery(query);
			list = new ArrayList<ErrorValidationPojo>();
			while (rs.next()) {
				object = new ErrorValidationPojo();
				object.setError_type(rs.getString("ErrorType"));
				object.setError_description(rs.getString("ErrorDescription"));
				object.setRouter_error_message(rs.getString("router_error_message"));
				list.add(object);
			}

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			DBUtil.close(rs);
			DBUtil.close(statement);
			DBUtil.close(connection);
		}

		return list;

	}

	public void updateErrorDetailsDeliveryTestForRequestId(String RequestId, String version, String textFound,
			String errorType, String errorDescription) {
		connection = ConnectionFactory.getConnection();
		String query = null;
		PreparedStatement preparedStmt;
		ResultSet rs = null;
		String suggestionForErrorDesc = "";
		try {
			query = "update webserviceInfo set TextFound_DeliveryTest = ?,ErrorStatus_DeliveryTest=?,ErrorDescription_DeliveryTest=? where alphanumeric_req_id = ? and version = ?";

			preparedStmt = connection.prepareStatement(query);

			preparedStmt.setString(1, textFound);
			preparedStmt.setString(2, errorType);
			preparedStmt.setString(3, errorDescription);
			preparedStmt.setString(4, RequestId);
			preparedStmt.setString(5, version);
			preparedStmt.executeUpdate();
			String query1 = "select suggestion from errorcodedata where ErrorDescription = ?";

			PreparedStatement ps = connection.prepareStatement(query1);

			ps.setString(1, errorDescription);

			rs = ps.executeQuery();

			while (rs.next()) {

				suggestionForErrorDesc = (rs.getString("suggestion"));

			}

			query = "update certificationtestvalidation set suggestionForFailure = ? where alphanumeric_req_id = ? and version = ?";

			preparedStmt = connection.prepareStatement(query);

			preparedStmt.setString(1, suggestionForErrorDesc);
			preparedStmt.setString(2, RequestId);
			preparedStmt.setString(3, version);
			preparedStmt.executeUpdate();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			logger.error(e.getMessage());
		}
	}

	public final List<ModifyConfigResultPojo> getNoConfigCmdForPreviousConfig() throws IOException {

		connection = ConnectionFactory.getConnection();
		String query1 = "select No_SSH_Command from createsshconfig";
		ResultSet rs = null;
		ModifyConfigResultPojo configCmdPojo = null;
		List<ModifyConfigResultPojo> configCmdList = null;
		try {
			configCmdList = new ArrayList<ModifyConfigResultPojo>();

			PreparedStatement ps = connection.prepareStatement(query1);

			rs = ps.executeQuery();

			while (rs.next()) {
				configCmdPojo = new ModifyConfigResultPojo();
				configCmdPojo.setNo_SSH_Command(rs.getString("No_SSH_Command"));
				configCmdList.add(configCmdPojo);
			}

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			DBUtil.close(rs);
			DBUtil.close(statement);
			DBUtil.close(connection);
		}

		return configCmdList;
	}

	public ErrorValidationPojo getErrordetailsForRequestId(String requestId, String version) {
		List<String> list = new ArrayList<String>();
		ErrorValidationPojo errorValidationPojo = new ErrorValidationPojo();
		connection = ConnectionFactory.getConnection();
		String query = null;
		query = "Select * from webserviceInfo  where alphanumeric_req_id = ? and version = ?";
		ResultSet rs = null;
		PreparedStatement pst = null;

		try {

			pst = connection.prepareStatement(query);
			pst.setString(1, requestId);
			pst.setString(2, version);

			rs = pst.executeQuery();
			while (rs.next()) {
				errorValidationPojo.setRouter_error_message(rs.getString("TextFound_DeliveryTest"));
				errorValidationPojo.setError_type(rs.getString("ErrorStatus_DeliveryTest"));
				errorValidationPojo.setError_description(rs.getString("ErrorDescription_DeliveryTest"));
				errorValidationPojo.setDelivery_status(rs.getString("deliever_config"));
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			DBUtil.close(rs);
			DBUtil.close(statement);
			DBUtil.close(connection);
		}
		return errorValidationPojo;
	}

	public final CreateConfigRequest getRequestDetailFromDBForVersion(String value, String version)
			throws IOException, ParseException {
		connection = ConnectionFactory.getConnection();
		String query = null;

		query = "SELECT * FROM requestinfo.requestinfoso WHERE alphanumeric_req_id = ? AND request_version = ?";

		ResultSet rs = null;
		CreateConfigRequest request = new CreateConfigRequest();

		PreparedStatement pst = null;
		try {

			pst = connection.prepareStatement(query);
			pst.setString(1, value);
			pst.setString(2, version);
			rs = pst.executeQuery();

			int id;
			while (rs.next()) {

				id = (rs.getInt("request_info_id"));
				request.setOs(rs.getString("Os"));
				request.setBanner(rs.getString("banner"));
				// request.setde(rs.getString("device_name"));
				request.setModel(rs.getString("model"));
				request.setRegion(rs.getString("region"));
				request.setService(rs.getString("service"));
				request.setHostname(rs.getString("hostname"));
				request.setOsVersion(rs.getString("os_version"));
				request.setEnablePassword(rs.getString("enable_password"));
				request.setVrfName(rs.getString("vrf_name"));
				// request.setIsAutoProgress(rs.getBoolean("isAutoProgress"));

				if (rs.getTimestamp("end_date_of_processing") != null) {
					Timestamp d = rs.getTimestamp("end_date_of_processing");

					request.setEnd_date((covnertTStoString(d)));
				}

				request.setVendor(rs.getString("vendor"));
				request.setCustomer(rs.getString("customer"));
				request.setSiteid(rs.getString("siteid"));
				request.setRequestStatus(rs.getString("request_status"));
				request.setManagementIp(rs.getString("ManagementIP"));
				request.setRequestId(rs.getString("alphanumeric_req_id"));
				request.setDeviceType(rs.getString("device_type"));
				request.setDate_of_processing(rs.getString("date_of_processing"));
				request.setRequest_elapsed_time(rs.getString("request_elapsed_time"));
				request.setVpn(rs.getString("vpn"));
				// request.setRequest_id(rs.getInt("request_info_id"))
				request.setRequest_version(rs.getInt("request_version"));
				request.setRequest_parent_version(rs.getInt("request_parent_version"));
				/*
				 * request.setRequest_creator_name(rs .getString("request_creator_name"));
				 */
				request.setSnmpHostAddress(rs.getString("snmpHostAddress"));
				if (rs.getString("snmpHostAddress") == null) {
					request.setSnmpHostAddress("");
				}
				request.setSnmpString(rs.getString("snmpString"));
				if (rs.getString("snmpString") == null) {
					request.setSnmpString("");
				}
				request.setLoopBackType(rs.getString("loopBackType"));
				if (rs.getString("loopBackType") == null) {
					request.setLoopBackType("");
				}
				request.setLoopbackIPaddress(rs.getString("loopbackIPaddress"));
				if (rs.getString("loopbackIPaddress") == null) {
					request.setLoopbackIPaddress("");
				}
				request.setLoopbackSubnetMask(rs.getString("loopbackSubnetMask"));
				if (rs.getString("loopbackSubnetMask") == null) {
					request.setLoopbackSubnetMask("");
				}
				request.setLanInterface(rs.getString("lanInterface"));
				if (rs.getString("lanInterface") == null) {
					request.setLanInterface("");
				}
				request.setLanIp(rs.getString("lanIp"));
				request.setCertificationBit(rs.getString("certificationSelectionBit"));
				if (rs.getString("lanIp") == null) {
					request.setLanIp("");
				}
				request.setLanMaskAddress(rs.getString("lanMaskAddress"));
				if (rs.getString("lanMaskAddress") == null) {
					request.setLanMaskAddress("");
				}
				request.setLanDescription(rs.getString("lanDescription"));
				if (rs.getString("lanDescription") == null) {
					request.setLanDescription("");
				}
				request.setTemplateId(rs.getString("TemplateIdUsed"));
				if (rs.getString("TemplateIdUsed") == null) {
					request.setTemplateId("");
				}
				if (rs.getString("zipcode") != null) {
					request.setZipcode(rs.getString("zipcode"));
				} else {
					request.setZipcode("N\\A");
				}
				if (rs.getString("managed") != null) {
					request.setManaged(rs.getString("managed"));
				} else {
					request.setManaged("N\\A");
				}
				if (rs.getString("downtimeRequired") != null) {
					request.setDownTimeRequired(rs.getString("downtimeRequired"));
				} else {
					request.setDownTimeRequired("N\\A");
				}
				if (rs.getString("lastUpgradedOn") != null) {
					request.setLastUpgradedOn(rs.getString("lastUpgradedOn"));
				} else {
					request.setLastUpgradedOn("N\\A");
				}
				String subQueryOne = "select*from misarpeso where request_info_id=" + id;
				Statement smt = connection.createStatement();
				ResultSet rsnew = smt.executeQuery(subQueryOne);

				if (rsnew != null) {
					MISARPEType mis = new MISARPEType();
					while (rsnew.next()) {
						mis.setFastEthernetIp(rsnew.getString("fastEthernetIp"));
						mis.setRouterVrfVpnDGateway(rsnew.getString("routerVrfVpnDGateway"));
						mis.setRouterVrfVpnDIp(rsnew.getString("routerVrfVpnDIp"));
					}
					request.setMisArPe(mis);
				}

				String subQueryTwo = "select*from internetlcvrfso where request_info_id=" + id;
				Statement smt1 = connection.createStatement();
				ResultSet rsnew1 = smt1.executeQuery(subQueryTwo);

				if (rsnew1 != null) {
					InternetLCVRFType iis = new InternetLCVRFType();
					while (rsnew1.next()) {
						iis.setNetworkIp(rsnew1.getString("networkIp"));

						iis.setAS(rsnew1.getString("asNumber"));
						iis.setNeighbor1(rsnew1.getString("neighbor1"));
						iis.setNeighbor2(rsnew1.getString("neighbor2"));
						iis.setneighbor1_remoteAS(rsnew1.getString("neighbor1_remoteAS"));
						iis.setneighbor2_remoteAS(rsnew1.getString("neighbor2_remoteAS"));
						iis.setroutingProtocol(rsnew1.getString("routingProtocol"));
						iis.setnetworkIp_subnetMask(rsnew1.getString("networkIp_subnetMask"));

					}
					request.setInternetLcVrf(iis);
				}

				String subQueryThree = "select*from deviceinterfaceso where request_info_id=" + id;
				Statement smt3 = connection.createStatement();
				ResultSet rsnew3 = smt3.executeQuery(subQueryThree);

				if (rsnew3 != null) {
					Interface iisd = new Interface();
					while (rsnew3.next()) {
						iisd.setDescription(rsnew3.getString("description"));
						iisd.setIp(rsnew3.getString("ip"));
						iisd.setEncapsulation(rsnew3.getString("encapsulation"));
						iisd.setMask(rsnew3.getString("mask"));
						iisd.setName(rsnew3.getString("name"));
						iisd.setSpeed(rsnew3.getString("speed"));
						iisd.setBandwidth(rsnew3.getString("Bandwidth"));
					}
					request.setC3p_interface(iisd);
					request.setRequest_assigned_to(rs.getString("RequestOwner"));

				}

			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			DBUtil.close(rs);
			DBUtil.close(statement);
			DBUtil.close(connection);
		}

		return request;

	}

	public boolean checkForDeviceLockWithManagementIp(String requestId, String managementIp, String TestType) {
		connection = ConnectionFactory.getConnection();
		String query = null;

		ResultSet rs = null;
		PreparedStatement pst = null;
		boolean devicelocked = false;

		try {
			if (TestType.equalsIgnoreCase("DeviceTest")) {
				query = "Select * from DeviceLocked_ManagementIP  where management_ip = ?";
				pst = connection.prepareStatement(query);
				// pst.setString(1, requestId);
				pst.setString(1, managementIp);

				rs = pst.executeQuery();
				while (rs.next()) {
					devicelocked = true;
				}
			} else {
				query = "Select * from DeviceLocked_ManagementIP  where management_ip = ? and locked_by = ?";
				pst = connection.prepareStatement(query);
				pst.setString(1, managementIp);
				pst.setString(2, requestId);

				rs = pst.executeQuery();
				while (rs.next()) {
					devicelocked = true;
				}
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			DBUtil.close(rs);
			DBUtil.close(statement);
			DBUtil.close(connection);
		}
		return devicelocked;
	}

	public String lockDeviceForRequest(String managementIp, String RequestId) throws SQLException {
		String result = "";
		connection = ConnectionFactory.getConnection();
		ResultSet rs = null;
		PreparedStatement preparedStmt = null;
		String query = null;
		try {
			query = "INSERT INTO DeviceLocked_ManagementIP(management_ip,locked_by,flag)" + "VALUES(?,?,?)";

			preparedStmt = connection.prepareStatement(query);
			preparedStmt.setString(1, managementIp);
			preparedStmt.setString(2, RequestId);
			preparedStmt.setString(3, "Y");

			preparedStmt.executeUpdate();
			result = "Success";
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			result = "Failure";
			e.printStackTrace();
		} finally {
			DBUtil.close(rs);
			DBUtil.close(preparedStmt);
			DBUtil.close(connection);
		}

		return result;
	}

	public String releaselockDeviceForRequest(String managementIp, String RequestId) throws SQLException {
		String result = "";
		connection = ConnectionFactory.getConnection();
		ResultSet rs = null;
		PreparedStatement preparedStmt = null;
		String query = null;
		try {
			query = "delete from DeviceLocked_ManagementIP where management_ip = ? and locked_by = ? ";

			preparedStmt = connection.prepareStatement(query);
			preparedStmt.setString(1, managementIp);
			preparedStmt.setString(2, RequestId);

			preparedStmt.executeUpdate();
			result = "Success";
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			result = "Failure";
			e.printStackTrace();
		} finally {
			DBUtil.close(rs);
			DBUtil.close(preparedStmt);
			DBUtil.close(connection);
		}

		return result;
	}

	public Map<String, String> getRequestFlagForReport(String requestId, double versionId) {
		connection = ConnectionFactory.getConnection();
		String query = "";
		ResultSet rs = null;
		PreparedStatement preparedStmt;
		String flagForPrevalidation = "";
		String flagFordelieverConfig = "";
		Map<String, String> hmap = new HashMap<String, String>();
		DecimalFormat numberFormat = new DecimalFormat("#.0");
		String version = numberFormat.format(versionId);
		query = "select application_test,deliever_config from  webserviceinfo where alphanumeric_req_id = ? and version = ? ";
		try {
			preparedStmt = connection.prepareStatement(query);
			preparedStmt.setString(1, requestId);
			preparedStmt.setString(2, version);
			rs = preparedStmt.executeQuery();
			if (rs != null) {
				while (rs.next()) {
					flagForPrevalidation = rs.getString("application_test");
					flagFordelieverConfig = rs.getString("deliever_config");
				}
			}
			hmap.put("flagForPrevalidation", flagForPrevalidation);
			hmap.put("flagFordelieverConfig", flagFordelieverConfig);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return hmap;
	}

	public boolean changeRequestOwner(String requestid, String version, String owner) {
		boolean result = false;
		int res;
		String query1 = "update requestinfoso set RequestOwner = ? , readFE=?, readSE=? where alphanumeric_req_id = ? and request_version= ?";
		try {
			PreparedStatement preparedStmt1, preparedStmt0;
			ResultSet rs = null;
			java.util.Date date = new java.util.Date();
			java.sql.Timestamp timestamp = new java.sql.Timestamp(date.getTime());
			Timestamp d = null;
			preparedStmt0 = connection.prepareStatement(query1);
			preparedStmt0.setString(1, owner);

			if (owner.equalsIgnoreCase("seuser")) {
				preparedStmt0.setInt(2, 1);
				preparedStmt0.setInt(3, 0);
			} else if (owner.equalsIgnoreCase("feuser")) {
				preparedStmt0.setInt(2, 0);
				preparedStmt0.setInt(3, 1);
			}
			preparedStmt0.setString(4, requestid);
			preparedStmt0.setString(5, version);

			res = preparedStmt0.executeUpdate();

			if (res != 0) {
				result = true;
			} else {
				result = false;
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();

		}
		return result;
	}

	public String getUserTaskIdForRequest(String requestId, String version) {
		String usertaskid = null;
		connection = ConnectionFactory.getConnection();
		String query2 = "select * from  camundaHistory where history_requestId=? and history_versionId=?";
		try {
			PreparedStatement pst = connection.prepareStatement(query2);
			pst.setString(1, requestId);
			pst.setString(2, version);
			ResultSet res = pst.executeQuery();
			while (res.next()) {
				usertaskid = res.getString("history_userTaskId");
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return usertaskid;
	}

	public boolean changeRequestStatus(String requestid, String version, String status) {
		connection = ConnectionFactory.getConnection();

		boolean result = false;
		int res;
		String query1 = "update requestinfoso set request_status = ?, end_date_of_processing= now() where alphanumeric_req_id = ? and request_version= ?";
		try {
			PreparedStatement preparedStmt1, preparedStmt0;
			ResultSet rs = null;
			PreparedStatement pst = connection.prepareStatement(query1);

			preparedStmt1 = connection.prepareStatement(query1);
			preparedStmt1.setString(1, status);
			preparedStmt1.setString(2, requestid);
			preparedStmt1.setString(3, version);
			int val = preparedStmt1.executeUpdate();
			if (val < 0) {
				result = true;
			} else {
				result = false;
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();

		}
		return result;
	}

	public List<RequestInfoSO> getFEAssignedRequestList() {
		List<RequestInfoSO> list = new ArrayList<RequestInfoSO>();
		connection = ConnectionFactory.getConnection();
		String query2 = "select * from  requestinfoso where RequestOwner=?";
		try {
			RequestInfoSO request = null;
			PreparedStatement pst = connection.prepareStatement(query2);
			pst.setString(1, "feuser");
			ResultSet rs = pst.executeQuery();
			int id;
			while (rs.next()) {
				request = new RequestInfoSO();

				request = new RequestInfoSO();
				id = (rs.getInt("request_info_id"));
				request.setRead(rs.getInt("readFE"));
				request.setOs(rs.getString("Os"));
				request.setBanner(rs.getString("banner"));
				request.setDeviceName(rs.getString("device_name"));
				request.setModel(rs.getString("model"));
				request.setRegion(rs.getString("region"));
				request.setService(rs.getString("service"));
				request.setHostname(rs.getString("hostname"));
				request.setOsVersion(rs.getString("os_version"));
				request.setEnablePassword(rs.getString("enable_password"));
				request.setVrfName(rs.getString("vrf_name"));
				request.setIsAutoProgress(rs.getBoolean("isAutoProgress"));

				Timestamp d = rs.getTimestamp("date_of_processing");

				request.setDateOfProcessing((covnertTStoString(d)));
				request.setVendor(rs.getString("vendor"));
				request.setCustomer(rs.getString("customer"));
				request.setSiteid(rs.getString("siteid"));
				request.setStatus(rs.getString("request_status"));
				request.setManagementIp(rs.getString("ManagementIP"));
				request.setDisplay_request_id(rs.getString("alphanumeric_req_id"));
				request.setDeviceType(rs.getString("device_type"));
				request.setVpn(rs.getString("vpn"));
				request.setRequest_id(rs.getInt("request_info_id"));
				request.setRequest_version(rs.getDouble("request_version"));
				request.setRequest_parent_version(rs.getDouble("request_parent_version"));
				request.setRequest_creator_name(rs.getString("request_creator_name"));
				request.setSnmpHostAddress(rs.getString("snmpHostAddress"));
				if (rs.getString("snmpHostAddress") == null) {
					request.setSnmpHostAddress("");
				}
				request.setSnmpString(rs.getString("snmpString"));
				if (rs.getString("snmpString") == null) {
					request.setSnmpString("");
				}
				request.setLoopBackType(rs.getString("loopBackType"));
				if (rs.getString("loopBackType") == null) {
					request.setLoopBackType("");
				}
				request.setLoopbackIPaddress(rs.getString("loopbackIPaddress"));
				if (rs.getString("loopbackIPaddress") == null) {
					request.setLoopbackIPaddress("");
				}
				request.setLoopbackSubnetMask(rs.getString("loopbackSubnetMask"));
				if (rs.getString("loopbackSubnetMask") == null) {
					request.setLoopbackSubnetMask("");
				}

				if (request.getStatus().equalsIgnoreCase("Success")
						|| request.getStatus().equalsIgnoreCase("success")) {
					SimpleDateFormat format = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
					Timestamp d1 = rs.getTimestamp("end_date_of_processing");

					Date d1_d = null;
					Date d2_d = null;

					d1_d = format.parse((covnertTStoString(d)));

					d2_d = format.parse((covnertTStoString(d1)));

					String elapsedtime = null;
					// in milliseconds
					long diff = d2_d.getTime() - d1_d.getTime();

					long diffSeconds = diff / 1000 % 60;
					long diffMinutes = diff / (60 * 1000) % 60;
					long diffHours = diff / (60 * 60 * 1000) % 24;
					long diffDays = diff / (24 * 60 * 60 * 1000);

					long dayTohours = diffDays * 24;

					DecimalFormat formatter = new DecimalFormat("00");
					String sec = formatter.format(diffSeconds);
					String min = formatter.format(diffMinutes);
					String hrs = formatter.format(diffHours + dayTohours);

					elapsedtime = hrs + ":" + min + ":" + sec;

					request.setElapsedTime(elapsedtime);
				}

				else {
					request.setElapsedTime("00:00:00");

				}

				Timestamp d1 = rs.getTimestamp("end_date_of_processing");
				request.setEndDateofProcessing((covnertTStoString(d1)));

				String subQueryOne = "select*from misarpeso where request_info_id=" + id;
				Statement smt = connection.createStatement();
				ResultSet rsnew = smt.executeQuery(subQueryOne);

				if (rsnew != null) {
					MisArPeSO mis = new MisArPeSO();
					while (rsnew.next()) {
						mis.setFastEthernetIp(rsnew.getString("fastEthernetIp"));
						mis.setRouterVrfVpnDGateway(rsnew.getString("routerVrfVpnDGateway"));
						mis.setRouterVrfVpnDIp(rsnew.getString("routerVrfVpnDIp"));
					}
					request.setMisArPeSO(mis);
				}

				String subQueryTwo = "select*from internetlcvrfso where request_info_id=" + id;
				Statement smt1 = connection.createStatement();
				ResultSet rsnew1 = smt1.executeQuery(subQueryTwo);

				if (rsnew1 != null) {
					InternetLcVrfSO iis = new InternetLcVrfSO();
					while (rsnew1.next()) {
						iis.setNetworkIp(rsnew1.getString("networkIp"));
						iis.setBgpASNumber(rsnew1.getString("asNumber"));
						iis.setNeighbor1(rsnew1.getString("neighbor1"));
						iis.setNeighbor2(rsnew1.getString("neighbor2"));
						iis.setNeighbor1_remoteAS(rsnew1.getString("neighbor1_remoteAS"));
						iis.setNeighbor2_remoteAS(rsnew1.getString("neighbor2_remoteAS"));
						iis.setRoutingProtocol(rsnew1.getString("routingProtocol"));
						iis.setNetworkIp_subnetMask(rsnew1.getString("networkIp_subnetMask"));

					}
					request.setInternetLcVrf(iis);
				}

				String subQueryThree = "select*from deviceinterfaceso where request_info_id=" + id;
				Statement smt3 = connection.createStatement();
				ResultSet rsnew3 = smt3.executeQuery(subQueryThree);

				if (rsnew3 != null) {
					DeviceInterfaceSO iisd = new DeviceInterfaceSO();
					while (rsnew3.next()) {
						iisd.setDescription(rsnew3.getString("description"));
						iisd.setIp(rsnew3.getString("ip"));
						iisd.setEncapsulation(rsnew3.getString("encapsulation"));
						iisd.setMask(rsnew3.getString("mask"));
						iisd.setName(rsnew3.getString("name"));
						iisd.setSpeed(rsnew3.getString("speed"));
						iisd.setBandwidth(rsnew3.getString("Bandwidth"));
					}
					request.setDeviceInterfaceSO(iisd);

					list.add(request);
				}

			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return list;
	}

	public List<RequestInfoSO> getSEAssignedRequestList() {
		List<RequestInfoSO> list = new ArrayList<RequestInfoSO>();
		connection = ConnectionFactory.getConnection();
		// String query2 =
		// "select * from requestinfoso where RequestOwner=? and request_status=?";
		String query2 = "select * from  requestinfoso where RequestOwner=? and request_status  IN (?,?)";
		try {
			RequestInfoSO request = null;
			PreparedStatement pst = connection.prepareStatement(query2);
			pst.setString(1, "seuser");
			pst.setString(2, "Hold");
			pst.setString(3, "In Progress");

			ResultSet rs = pst.executeQuery();
			int id;
			while (rs.next()) {
				request = new RequestInfoSO();

				request = new RequestInfoSO();
				id = (rs.getInt("request_info_id"));
				request.setRead(rs.getInt("readSE"));
				request.setOs(rs.getString("Os"));
				request.setBanner(rs.getString("banner"));
				request.setDeviceName(rs.getString("device_name"));
				request.setModel(rs.getString("model"));
				request.setRegion(rs.getString("region"));
				request.setService(rs.getString("service"));
				request.setHostname(rs.getString("hostname"));
				request.setOsVersion(rs.getString("os_version"));
				request.setEnablePassword(rs.getString("enable_password"));
				request.setVrfName(rs.getString("vrf_name"));
				request.setIsAutoProgress(rs.getBoolean("isAutoProgress"));

				Timestamp d = rs.getTimestamp("date_of_processing");

				request.setDateOfProcessing((covnertTStoString(d)));
				request.setVendor(rs.getString("vendor"));
				request.setCustomer(rs.getString("customer"));
				request.setSiteid(rs.getString("siteid"));
				request.setStatus(rs.getString("request_status"));
				request.setManagementIp(rs.getString("ManagementIP"));
				request.setDisplay_request_id(rs.getString("alphanumeric_req_id"));
				request.setDeviceType(rs.getString("device_type"));
				request.setVpn(rs.getString("vpn"));
				request.setRequest_id(rs.getInt("request_info_id"));
				request.setRequest_version(rs.getDouble("request_version"));
				request.setRequest_parent_version(rs.getDouble("request_parent_version"));
				request.setRequest_creator_name(rs.getString("request_creator_name"));
				request.setSnmpHostAddress(rs.getString("snmpHostAddress"));
				if (rs.getString("snmpHostAddress") == null) {
					request.setSnmpHostAddress("");
				}
				request.setSnmpString(rs.getString("snmpString"));
				if (rs.getString("snmpString") == null) {
					request.setSnmpString("");
				}
				request.setLoopBackType(rs.getString("loopBackType"));
				if (rs.getString("loopBackType") == null) {
					request.setLoopBackType("");
				}
				request.setLoopbackIPaddress(rs.getString("loopbackIPaddress"));
				if (rs.getString("loopbackIPaddress") == null) {
					request.setLoopbackIPaddress("");
				}
				request.setLoopbackSubnetMask(rs.getString("loopbackSubnetMask"));
				if (rs.getString("loopbackSubnetMask") == null) {
					request.setLoopbackSubnetMask("");
				}

				if (request.getStatus().equalsIgnoreCase("Success")
						|| request.getStatus().equalsIgnoreCase("success")) {
					SimpleDateFormat format = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
					Timestamp d1 = rs.getTimestamp("end_date_of_processing");

					Date d1_d = null;
					Date d2_d = null;

					d1_d = format.parse((covnertTStoString(d)));

					d2_d = format.parse((covnertTStoString(d1)));

					String elapsedtime = null;
					// in milliseconds
					long diff = d2_d.getTime() - d1_d.getTime();

					long diffSeconds = diff / 1000 % 60;
					long diffMinutes = diff / (60 * 1000) % 60;
					long diffHours = diff / (60 * 60 * 1000) % 24;
					long diffDays = diff / (24 * 60 * 60 * 1000);

					long dayTohours = diffDays * 24;

					DecimalFormat formatter = new DecimalFormat("00");
					String sec = formatter.format(diffSeconds);
					String min = formatter.format(diffMinutes);
					String hrs = formatter.format(diffHours + dayTohours);

					elapsedtime = hrs + ":" + min + ":" + sec;

					request.setElapsedTime(elapsedtime);
				} else {
					request.setElapsedTime("00:00:00");

				}

				String subQueryOne = "select*from misarpeso where request_info_id=" + id;
				Statement smt = connection.createStatement();
				ResultSet rsnew = smt.executeQuery(subQueryOne);

				if (rsnew != null) {
					MisArPeSO mis = new MisArPeSO();
					while (rsnew.next()) {
						mis.setFastEthernetIp(rsnew.getString("fastEthernetIp"));
						mis.setRouterVrfVpnDGateway(rsnew.getString("routerVrfVpnDGateway"));
						mis.setRouterVrfVpnDIp(rsnew.getString("routerVrfVpnDIp"));
					}
					request.setMisArPeSO(mis);
				}

				String subQueryTwo = "select*from internetlcvrfso where request_info_id=" + id;
				Statement smt1 = connection.createStatement();
				ResultSet rsnew1 = smt1.executeQuery(subQueryTwo);

				if (rsnew1 != null) {
					InternetLcVrfSO iis = new InternetLcVrfSO();
					while (rsnew1.next()) {
						iis.setNetworkIp(rsnew1.getString("networkIp"));
						iis.setBgpASNumber(rsnew1.getString("asNumber"));
						iis.setNeighbor1(rsnew1.getString("neighbor1"));
						iis.setNeighbor2(rsnew1.getString("neighbor2"));
						iis.setNeighbor1_remoteAS(rsnew1.getString("neighbor1_remoteAS"));
						iis.setNeighbor2_remoteAS(rsnew1.getString("neighbor2_remoteAS"));
						iis.setRoutingProtocol(rsnew1.getString("routingProtocol"));
						iis.setNetworkIp_subnetMask(rsnew1.getString("networkIp_subnetMask"));

					}
					request.setInternetLcVrf(iis);
				}

				String subQueryThree = "select*from deviceinterfaceso where request_info_id=" + id;
				Statement smt3 = connection.createStatement();
				ResultSet rsnew3 = smt3.executeQuery(subQueryThree);

				if (rsnew3 != null) {
					DeviceInterfaceSO iisd = new DeviceInterfaceSO();
					while (rsnew3.next()) {
						iisd.setDescription(rsnew3.getString("description"));
						iisd.setIp(rsnew3.getString("ip"));
						iisd.setEncapsulation(rsnew3.getString("encapsulation"));
						iisd.setMask(rsnew3.getString("mask"));
						iisd.setName(rsnew3.getString("name"));
						iisd.setSpeed(rsnew3.getString("speed"));
						iisd.setBandwidth(rsnew3.getString("Bandwidth"));
					}
					request.setDeviceInterfaceSO(iisd);

					list.add(request);
				}

			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return list;
	}

	public List<RequestInfoSO> getAdminAssignedRequestList() {
		List<RequestInfoSO> list = new ArrayList<RequestInfoSO>();
		connection = ConnectionFactory.getConnection();
		// String query2 =
		// "select * from requestinfoso where RequestOwner=? and request_status=?";
		String query2 = "select * from  requestinfoso where RequestOwner=? and request_status  IN (?,?)";
		try {
			RequestInfoSO request = null;
			PreparedStatement pst = connection.prepareStatement(query2);
			pst.setString(1, "admin");
			pst.setString(2, "Hold");
			pst.setString(3, "In Progress");

			ResultSet rs = pst.executeQuery();
			int id;
			while (rs.next()) {
				request = new RequestInfoSO();

				request = new RequestInfoSO();
				id = (rs.getInt("request_info_id"));
				request.setRead(rs.getInt("readSE"));
				request.setOs(rs.getString("Os"));
				request.setBanner(rs.getString("banner"));
				request.setDeviceName(rs.getString("device_name"));
				request.setModel(rs.getString("model"));
				request.setRegion(rs.getString("region"));
				request.setService(rs.getString("service"));
				request.setHostname(rs.getString("hostname"));
				request.setOsVersion(rs.getString("os_version"));
				request.setEnablePassword(rs.getString("enable_password"));
				request.setVrfName(rs.getString("vrf_name"));
				request.setIsAutoProgress(rs.getBoolean("isAutoProgress"));

				Timestamp d = rs.getTimestamp("date_of_processing");

				request.setDateOfProcessing((covnertTStoString(d)));
				request.setVendor(rs.getString("vendor"));
				request.setCustomer(rs.getString("customer"));
				request.setSiteid(rs.getString("siteid"));
				request.setStatus(rs.getString("request_status"));
				request.setManagementIp(rs.getString("ManagementIP"));
				request.setDisplay_request_id(rs.getString("alphanumeric_req_id"));
				request.setDeviceType(rs.getString("device_type"));
				request.setVpn(rs.getString("vpn"));
				request.setRequest_id(rs.getInt("request_info_id"));
				request.setRequest_version(rs.getDouble("request_version"));
				request.setRequest_parent_version(rs.getDouble("request_parent_version"));
				request.setRequest_creator_name(rs.getString("request_creator_name"));
				request.setSnmpHostAddress(rs.getString("snmpHostAddress"));
				if (rs.getString("snmpHostAddress") == null) {
					request.setSnmpHostAddress("");
				}
				request.setSnmpString(rs.getString("snmpString"));
				if (rs.getString("snmpString") == null) {
					request.setSnmpString("");
				}
				request.setLoopBackType(rs.getString("loopBackType"));
				if (rs.getString("loopBackType") == null) {
					request.setLoopBackType("");
				}
				request.setLoopbackIPaddress(rs.getString("loopbackIPaddress"));
				if (rs.getString("loopbackIPaddress") == null) {
					request.setLoopbackIPaddress("");
				}
				request.setLoopbackSubnetMask(rs.getString("loopbackSubnetMask"));
				if (rs.getString("loopbackSubnetMask") == null) {
					request.setLoopbackSubnetMask("");
				}

				if (request.getStatus().equalsIgnoreCase("Success")
						|| request.getStatus().equalsIgnoreCase("success")) {
					SimpleDateFormat format = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
					Timestamp d1 = rs.getTimestamp("end_date_of_processing");

					Date d1_d = null;
					Date d2_d = null;

					d1_d = format.parse((covnertTStoString(d)));

					d2_d = format.parse((covnertTStoString(d1)));

					String elapsedtime = null;
					// in milliseconds
					long diff = d2_d.getTime() - d1_d.getTime();

					long diffSeconds = diff / 1000 % 60;
					long diffMinutes = diff / (60 * 1000) % 60;
					long diffHours = diff / (60 * 60 * 1000) % 24;
					long diffDays = diff / (24 * 60 * 60 * 1000);

					long dayTohours = diffDays * 24;

					DecimalFormat formatter = new DecimalFormat("00");
					String sec = formatter.format(diffSeconds);
					String min = formatter.format(diffMinutes);
					String hrs = formatter.format(diffHours + dayTohours);

					elapsedtime = hrs + ":" + min + ":" + sec;

					request.setElapsedTime(elapsedtime);
				} else {
					request.setElapsedTime("00:00:00");

				}

				String subQueryOne = "select*from misarpeso where request_info_id=" + id;
				Statement smt = connection.createStatement();
				ResultSet rsnew = smt.executeQuery(subQueryOne);

				if (rsnew != null) {
					MisArPeSO mis = new MisArPeSO();
					while (rsnew.next()) {
						mis.setFastEthernetIp(rsnew.getString("fastEthernetIp"));
						mis.setRouterVrfVpnDGateway(rsnew.getString("routerVrfVpnDGateway"));
						mis.setRouterVrfVpnDIp(rsnew.getString("routerVrfVpnDIp"));
					}
					request.setMisArPeSO(mis);
				}

				String subQueryTwo = "select*from internetlcvrfso where request_info_id=" + id;
				Statement smt1 = connection.createStatement();
				ResultSet rsnew1 = smt1.executeQuery(subQueryTwo);

				if (rsnew1 != null) {
					InternetLcVrfSO iis = new InternetLcVrfSO();
					while (rsnew1.next()) {
						iis.setNetworkIp(rsnew1.getString("networkIp"));
						iis.setBgpASNumber(rsnew1.getString("asNumber"));
						iis.setNeighbor1(rsnew1.getString("neighbor1"));
						iis.setNeighbor2(rsnew1.getString("neighbor2"));
						iis.setNeighbor1_remoteAS(rsnew1.getString("neighbor1_remoteAS"));
						iis.setNeighbor2_remoteAS(rsnew1.getString("neighbor2_remoteAS"));
						iis.setRoutingProtocol(rsnew1.getString("routingProtocol"));
						iis.setNetworkIp_subnetMask(rsnew1.getString("networkIp_subnetMask"));

					}
					request.setInternetLcVrf(iis);
				}

				String subQueryThree = "select*from deviceinterfaceso where request_info_id=" + id;
				Statement smt3 = connection.createStatement();
				ResultSet rsnew3 = smt3.executeQuery(subQueryThree);

				if (rsnew3 != null) {
					DeviceInterfaceSO iisd = new DeviceInterfaceSO();
					while (rsnew3.next()) {
						iisd.setDescription(rsnew3.getString("description"));
						iisd.setIp(rsnew3.getString("ip"));
						iisd.setEncapsulation(rsnew3.getString("encapsulation"));
						iisd.setMask(rsnew3.getString("mask"));
						iisd.setName(rsnew3.getString("name"));
						iisd.setSpeed(rsnew3.getString("speed"));
						iisd.setBandwidth(rsnew3.getString("Bandwidth"));
					}
					request.setDeviceInterfaceSO(iisd);

					list.add(request);
				}

			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return list;
	}

	public void setReadFlagFESE(String requestId, String version, int status, String key) {
		connection = ConnectionFactory.getConnection();
		String query = null;

		if (key.equalsIgnoreCase("FE")) {
			query = "update requestinfoso set readFE = ? where alphanumeric_req_id = ? and request_version = ? ";
		} else if (key.equalsIgnoreCase("SE")) {
			query = "update requestinfoso set readSE = ? where alphanumeric_req_id = ? and request_version = ? ";
		}

		PreparedStatement preparedStmt;
		try {
			preparedStmt = connection.prepareStatement(query);
			preparedStmt.setInt(1, status);
			preparedStmt.setString(2, requestId);
			preparedStmt.setString(3, version);
			preparedStmt.executeUpdate();
		} catch (SQLException e) {

			logger.error(e.getMessage());
		}
	}

	public int getScheduledRequestsFromDB() {
		int num = 0;
		connection = ConnectionFactory.getConnection();
		ResultSet rs = null;
		String query1 = null;
		if (Global.loggedInUser.equalsIgnoreCase("seuser")) {
			query1 = "SELECT COUNT(request_info_id) AS total FROM requestinfoso Where request_status = 'Scheduled' and request_creator_name=? and alphanumeric_req_id rlike'SR|OS'";
		} else if (Global.loggedInUser.equalsIgnoreCase("feuser")) {
			query1 = "SELECT COUNT(request_info_id) AS total FROM requestinfoso Where request_status = 'Scheduled' and RequestOwner=? and alphanumeric_req_id rlike'SR|OS'";

		} else if (Global.loggedInUser.equalsIgnoreCase("admin")) {
			query1 = "SELECT COUNT(request_info_id) AS total FROM requestinfoso Where request_status = 'Scheduled' and alphanumeric_req_id rlike'SR|OS'";

		}
		try {
			if (Global.loggedInUser.equalsIgnoreCase("admin")) {
				Statement ps = connection.createStatement();

				rs = ps.executeQuery(query1);
			} else {
				PreparedStatement ps = connection.prepareStatement(query1);
				ps.setString(1, Global.loggedInUser);
				rs = ps.executeQuery();
			}
			while (rs.next()) {
				num = rs.getInt("total");
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return num;
	}

	public int getHoldRequestsFromDB() {
		int num = 0;
		connection = ConnectionFactory.getConnection();
		ResultSet rs = null;
		String query1 = null;
		if (Global.loggedInUser.equalsIgnoreCase("seuser")) {
			query1 = "SELECT COUNT(request_info_id) AS total FROM requestinfoso Where request_status = 'Hold' and request_creator_name=? and alphanumeric_req_id rlike'SR|OS'";
		} else if (Global.loggedInUser.equalsIgnoreCase("feuser")) {
			query1 = "SELECT COUNT(request_info_id) AS total FROM requestinfoso Where request_status = 'Hold' and RequestOwner=? and alphanumeric_req_id rlike'SR|OS'";

		} else if (Global.loggedInUser.equalsIgnoreCase("admin")) {
			query1 = "SELECT COUNT(request_info_id) AS total FROM requestinfoso Where request_status = 'Hold' and alphanumeric_req_id rlike'SR|OS'";

		}
		try {
			if (Global.loggedInUser.equalsIgnoreCase("admin")) {
				Statement ps = connection.createStatement();

				rs = ps.executeQuery(query1);
			} else {
				PreparedStatement ps = connection.prepareStatement(query1);
				ps.setString(1, Global.loggedInUser);
				rs = ps.executeQuery();
			}
			while (rs.next()) {
				num = rs.getInt("total");
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return num;
	}

	public String getRequestOwner(String reqId, String version) {
		String owner = null;
		connection = ConnectionFactory.getConnection();
		ResultSet rs = null;
		PreparedStatement preparedStmt;
		String query1 = "SELECT request_creator_name FROM requestinfoso where alphanumeric_req_id = ? and request_version = ?";
		try {
			preparedStmt = connection.prepareStatement(query1);
			preparedStmt.setString(1, reqId);
			preparedStmt.setString(2, version);
			rs = preparedStmt.executeQuery();
			while (rs.next()) {
				owner = rs.getString("request_creator_name");
			}
		} catch (SQLException e) {

			logger.error(e.getMessage());
		}

		return owner;
	}

	public final List<ModifyConfigResultPojo> getConfigCmdRecordFordataForDelivery(CreateConfigRequest configRequest,
			String key) throws IOException {

		connection = ConnectionFactory.getConnection();
		String query1 = "SELECT * FROM requestinfo.createsshconfig WHERE Vendor=? AND Device_Type=? AND Model=? AND OS=? AND OS_Version=? AND Assigned_Field_Name=?";
		ResultSet rs = null;
		ModifyConfigResultPojo configCmdPojo = null;
		List<ModifyConfigResultPojo> configCmdList = null;
		try {
			configCmdList = new ArrayList<ModifyConfigResultPojo>();

			PreparedStatement ps = connection.prepareStatement(query1);

			ps.setString(1, configRequest.getVendor());
			ps.setString(2, configRequest.getDeviceType());
			ps.setString(3, configRequest.getModel());
			ps.setString(4, configRequest.getOs());
			ps.setString(5, configRequest.getOsVersion());
			ps.setString(6, key);

			rs = ps.executeQuery();

			while (rs.next()) {
				configCmdPojo = new ModifyConfigResultPojo();
				configCmdPojo.setNo_SSH_Command(rs.getString("No_SSH_Command"));
				/*
				 * configCmdPojo.setCreate_SSH_Command(rs .getString("Create_SSH_Command"));
				 */
				configCmdList.add(configCmdPojo);
			}

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			DBUtil.close(rs);
			DBUtil.close(statement);
			DBUtil.close(connection);
		}

		return configCmdList;
	}

	public void resetErrorStateOfRechabilityTest(String requestId, String version) {
		connection = ConnectionFactory.getConnection();
		PreparedStatement preparedStmt;
		int rs = 0;
		String query = "update webserviceInfo set application_test = ? where alphanumeric_req_id = ? and version = ? ";
		try {
			preparedStmt = connection.prepareStatement(query);
			preparedStmt.setString(1, "0");
			preparedStmt.setString(2, requestId);
			preparedStmt.setString(3, version);

			rs = preparedStmt.executeUpdate();

			query = "update CertificationTestValidation set Device_Reachability_Test = ? where alphanumeric_req_id = ? and version = ? ";
			preparedStmt = connection.prepareStatement(query);
			preparedStmt.setString(1, "0");
			preparedStmt.setString(2, requestId);
			preparedStmt.setString(3, version);

			rs = preparedStmt.executeUpdate();

		} catch (SQLException e) {

			logger.error(e.getMessage());
		} finally {

		}
	}

	public void updateRouterFailureHealthCheck(String requestId, String version) {
		connection = ConnectionFactory.getConnection();
		String query = null;
		String suggestion = "Please check the connectivity.Issue while performing Health check test";
		query = "update CertificationTestValidation set suggestionForFailure =? where alphanumeric_req_id = ? and version = ? ";

		PreparedStatement preparedStmt;
		try {
			preparedStmt = connection.prepareStatement(query);

			preparedStmt.setString(1, suggestion);
			preparedStmt.setString(2, requestId);
			preparedStmt.setString(3, version);

			preparedStmt.executeUpdate();
		} catch (SQLException e) {

			logger.error(e.getMessage());
		}
	}

	public String updateTimeForScheduledRequest(String requestId, String version) throws SQLException {
		connection = ConnectionFactory.getConnection();
		String query1 = "SELECT * FROM requestinfo.requestinfoso WHERE alphanumeric_req_id=? AND request_version=?";
		ResultSet rs = null;
		String result = "false";

		try {

			PreparedStatement ps = connection.prepareStatement(query1);

			ps.setString(1, requestId);
			ps.setString(2, version);

			rs = ps.executeQuery();

			while (rs.next()) {

				if (rs.getString("RequestType_Flag").equalsIgnoreCase("S")) {
					query1 = "update requestinfoso set date_of_processing = ? where alphanumeric_req_id = ? and version = ? ";
					ps = connection.prepareStatement(query1);
					ps.setTimestamp(1, rs.getTimestamp("ScheduledTime"));
					ps.setString(2, requestId);
					ps.setString(3, version);

					ps.executeUpdate();
				}

			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block

			e.printStackTrace();
		} finally {
			DBUtil.close(rs);

			DBUtil.close(connection);
		}

		return result;
	}

	public String updateTimeIntervalElapsedTime(String requestId, String version) throws SQLException {
		Connection connection;
		RequestInfoDao requestinfoDao = new RequestInfoDao();
		connection = ConnectionFactory.getConnection();
		String query0 = "select * from requestinfoso where alphanumeric_req_id = ? and request_version= ?";
		ResultSet rs = null;
		String result = "false";
		try {

			PreparedStatement ps = connection.prepareStatement(query0);

			ps.setString(1, requestId);
			ps.setString(2, version);

			rs = ps.executeQuery();
			while (rs.next()) {
				Timestamp d = null;
				java.util.Date date = new java.util.Date();
				java.sql.Timestamp timestamp = new java.sql.Timestamp(date.getTime());
				if (rs.getString("RequestType_Flag").equalsIgnoreCase("M")) {
					d = rs.getTimestamp("date_of_processing");

				} else {
					d = rs.getTimestamp("ScheduledTime");
				}

				if (rs.getString("temp_elapsed_time") == null) {
					String diff = requestinfoDao.calcTimeDiffInMins(timestamp, d);
					String query1 = "update requestinfoso set temp_elapsed_time = ?, temp_processing_time= now() where alphanumeric_req_id = ? and request_version= ?";

					PreparedStatement preparedStmt1;
					preparedStmt1 = connection.prepareStatement(query1);
					preparedStmt1.setString(1, diff);
					preparedStmt1.setString(2, requestId);
					preparedStmt1.setString(3, version);
					preparedStmt1.executeUpdate();
				} else {
					Timestamp d1 = null;
					date = new java.util.Date();
					timestamp = new java.sql.Timestamp(date.getTime());
					d1 = rs.getTimestamp("temp_processing_time");
					String diff1 = requestinfoDao.calcTimeDiffInMins(timestamp, d1);

					String diff2 = String.format("%.2f", Float
							.toString((Float.parseFloat(diff1) + Float.parseFloat(rs.getString("temp_elapsed_time")))));
					String query1 = "update requestinfoso set temp_elapsed_time = ? where alphanumeric_req_id = ? and request_version= ?";

					PreparedStatement preparedStmt1;
					preparedStmt1 = connection.prepareStatement(query1);
					preparedStmt1.setString(1, diff2);
					preparedStmt1.setString(2, requestId);
					preparedStmt1.setString(3, version);
					preparedStmt1.executeUpdate();
				}
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block

			e.printStackTrace();
		} finally {
			DBUtil.close(rs);

			DBUtil.close(connection);
		}
		return "ok";
	}

	public String updateTempProcessingTime(String requestId, String version) throws SQLException {
		Connection connection;
		connection = ConnectionFactory.getConnection();

		try {

			String query1 = "update requestinfoso set temp_processing_time= now() where alphanumeric_req_id = ? and request_version= ?";

			PreparedStatement preparedStmt1;
			preparedStmt1 = connection.prepareStatement(query1);
			preparedStmt1.setString(1, requestId);
			preparedStmt1.setString(2, version);
			preparedStmt1.executeUpdate();

		} catch (SQLException e) {
			// TODO Auto-generated catch block

			e.printStackTrace();
		} finally {

			DBUtil.close(connection);
		}
		return "ok";
	}

	public boolean updateEditedAlertData(String alertCode, String description) {
		connection = ConnectionFactory.getConnection();
		boolean result = false;
		String query1 = "update requestinfo.alertinformationtable set alert_description = ? WHERE alert_code=?";
		try {
			PreparedStatement ps = connection.prepareStatement(query1);
			ps.setString(1, description);
			ps.setString(2, alertCode);
			int i = ps.executeUpdate();
			if (i == 1) {

				result = true;

			} else {
				result = false;
			}
		}

		catch (SQLException e) {

			e.printStackTrace();
		}
		return result;
	}

	public String getFAQforPage(String page) throws IOException {
		String content = null;
		connection = ConnectionFactory.getConnection();
		ResultSet rs = null;
		PreparedStatement preparedStmt;
		String path = null;
		RequestInfoDao.loadProperties();
		String query1 = "SELECT data_path FROM t_faq_data where page = ?";
		String filePath = "";
		try {
			preparedStmt = connection.prepareStatement(query1);
			preparedStmt.setString(1, page);

			rs = preparedStmt.executeQuery();
			while (rs.next()) {
				path = rs.getString("data_path");
			}

			if (path != null) {
				String downloadPath = RequestInfoDao.TSA_PROPERTIES.getProperty("faqDocPath");
				filePath = downloadPath + path + "/" + page + ".txt";
				content = new String(Files.readAllBytes(Paths.get(filePath)));
				return content;
			} else {
				return content;
			}
		} catch (SQLException e) {

			logger.error(e.getMessage());
		}
		return content;
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

	public JSONArray getStatusReportData(String startDate, String endDate) {
		connection = ConnectionFactory.getConnection();
		JSONArray array = new JSONArray();

		DateTimeFormatter df = DateTimeFormatter.ofPattern("MM/dd/yyyy");

		LocalDate sdate = LocalDate.parse(startDate, df);
		LocalDate edate = LocalDate.parse(endDate, df);

		String sFdate = DateTimeFormatter.ofPattern("yyyy-MM-dd", Locale.ENGLISH).format(sdate);
		String eFdate = DateTimeFormatter.ofPattern("yyyy-MM-dd", Locale.ENGLISH).format(edate);

		ResultSet rs = null;
		PreparedStatement preparedStmt;
		String query = null;
		if (Global.loggedInUser.equalsIgnoreCase("seuser")) {
			query = "select request_status, count(*) as number from requestinfoso where DATE(date_of_processing) between ? and ? and request_creator_name=? and alphanumeric_req_id rlike'SR|OS' Group by request_status";
		} else if (Global.loggedInUser.equalsIgnoreCase("feuser")) {
			query = "select request_status, count(*) as number from requestinfoso where DATE(date_of_processing) between ? and ? and RequestOwner=? and alphanumeric_req_id rlike'SR|OS' Group by request_status";

		} else if (Global.loggedInUser.equalsIgnoreCase("admin")) {
			query = "select request_status, count(*) as number from requestinfoso where DATE(date_of_processing) between ? and ?  and alphanumeric_req_id rlike'SR|OS' Group by request_status";

		}
		try {
			preparedStmt = connection.prepareStatement(query);

			if (!Global.loggedInUser.equalsIgnoreCase("admin")) {
				preparedStmt.setString(1, sFdate);
				preparedStmt.setString(2, eFdate);
				preparedStmt.setString(3, Global.loggedInUser);
			} else {
				preparedStmt.setString(1, sFdate);
				preparedStmt.setString(2, eFdate);
			}
			rs = preparedStmt.executeQuery();

			JSONObject obj = new JSONObject();

			int count = 0;
			while (rs.next()) {
				JSONObject internalObj = new JSONObject();

				String status = rs.getString("request_status");
				if (status.equalsIgnoreCase("In Progress")) {
					status = "InProgress";
				}

				internalObj.put("name", status);
				internalObj.put("data", rs.getInt("number"));

				count = count + rs.getInt("number");

				obj.put(status, internalObj);

			}

			obj.put("TotalRequests", count);
			array.put(obj);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return array;
	}

	public boolean isHealthCheckSuccesfulForOSUpgrade(String requestID, double version) {
		boolean result = false;
		connection = ConnectionFactory.getConnection();
		String query = null;
		ResultSet rs = null;
		int flag = 0;
		PreparedStatement preparedStmt;
		DecimalFormat numberFormat = new DecimalFormat("#.0");
		String parentVersion = numberFormat.format(version);
		query = "select health_checkup from  webserviceinfo where alphanumeric_req_id = ? and version = ?";
		try {
			preparedStmt = connection.prepareStatement(query);
			preparedStmt.setString(1, requestID);
			preparedStmt.setString(2, parentVersion);
			rs = preparedStmt.executeQuery();
			if (rs != null) {
				while (rs.next()) {
					flag = rs.getInt("health_checkup");
				}
			}

			if (flag == 2) {
				result = false;
			} else {
				result = true;
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return result;
	}

	public boolean isDilevarySuccessforOSUpgrade(String requestID, double version) {
		boolean result = false;
		connection = ConnectionFactory.getConnection();
		String query = null;
		ResultSet rs = null;
		int flag = 0;
		PreparedStatement preparedStmt;
		DecimalFormat numberFormat = new DecimalFormat("#.0");
		String parentVersion = numberFormat.format(version);
		query = "select deliever_config from webserviceinfo where alphanumeric_req_id = ? and version = ?";
		try {
			preparedStmt = connection.prepareStatement(query);
			preparedStmt.setString(1, requestID);
			preparedStmt.setString(2, parentVersion);
			rs = preparedStmt.executeQuery();
			if (rs != null) {
				while (rs.next()) {
					flag = rs.getInt("deliever_config");
				}
			}

			if (flag == 2) {
				result = false;
			} else {
				result = true;
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return result;
	}

	public boolean isPreHealthCheckSuccesfulForOSUpgrade(String requestID, double version) {
		boolean result = false;
		connection = ConnectionFactory.getConnection();
		String query = null;
		ResultSet rs = null;
		int flag = 0;
		PreparedStatement preparedStmt;
		DecimalFormat numberFormat = new DecimalFormat("#.0");
		String parentVersion = numberFormat.format(version);
		query = "select pre_health_checkup from  webserviceinfo where alphanumeric_req_id = ? and version = ?";
		try {
			preparedStmt = connection.prepareStatement(query);
			preparedStmt.setString(1, requestID);
			preparedStmt.setString(2, parentVersion);
			rs = preparedStmt.executeQuery();
			if (rs != null) {
				while (rs.next()) {
					flag = rs.getInt("pre_health_checkup");
				}
			}

			if (flag == 2) {
				result = false;
			} else {
				result = true;
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return result;
	}

	public final void update_dilevary_step_flag_in_db(String key, int value, String requestId, String version) {
		connection = ConnectionFactory.getConnection();
		boolean result = false;
		String query1 = "update os_upgrade_dilevary_flags set " + key + "= ? WHERE request_id=? and request_version=?";
		try {
			PreparedStatement ps = connection.prepareStatement(query1);
			ps.setInt(1, value);
			ps.setString(2, requestId);
			ps.setString(3, version);

			int i = ps.executeUpdate();
			if (i == 1) {

				result = true;

			} else {
				result = false;
			}
		}

		catch (SQLException e) {

			e.printStackTrace();
		}
	}

	public final CreateConfigRequest getOSDilevarySteps(String requestId, String version) {
		CreateConfigRequest req = new CreateConfigRequest();
		connection = ConnectionFactory.getConnection();
		String query1 = "SELECT * FROM os_upgrade_dilevary_flags WHERE request_id=? AND request_version=?";
		ResultSet rs = null;
		String result = "false";

		try {

			PreparedStatement ps = connection.prepareStatement(query1);

			ps.setString(1, requestId);
			ps.setString(2, version);

			rs = ps.executeQuery();

			while (rs.next()) {
				if (rs.getInt("login_flag") == 1) {
					req.setOs_upgrade_dilevary_login_flag("Pass");
				} else if (rs.getInt("login_flag") == 2) {
					req.setOs_upgrade_dilevary_login_flag("Fail");

				} else {
					req.setOs_upgrade_dilevary_login_flag("Not conducted");

				}
				if (rs.getInt("flash_size_flag") == 1) {
					req.setOs_upgrade_dilevary_flash_size_flag("Pass");
				} else if (rs.getInt("flash_size_flag") == 2) {
					req.setOs_upgrade_dilevary_flash_size_flag("Fail");

				} else {
					req.setOs_upgrade_dilevary_flash_size_flag("Not Conducted");

				}
				if (rs.getInt("back_up_flag") == 1) {
					req.setOs_upgrade_dilevary_backup_flag("Pass");
				} else if (rs.getInt("back_up_flag") == 2) {
					req.setOs_upgrade_dilevary_backup_flag("Fail");

				} else {
					req.setOs_upgrade_dilevary_backup_flag("Not conducted");

				}
				if (rs.getInt("os_download_flag") == 1) {
					req.setOs_upgrade_dilevary_os_download_flag("Pass");
				} else if (rs.getInt("os_download_flag") == 2) {
					req.setOs_upgrade_dilevary_os_download_flag("Fail");

				} else {
					req.setOs_upgrade_dilevary_os_download_flag("Not conducted");

				}
				if (rs.getInt("boot_system_flash_flag") == 1) {
					req.setOs_upgrade_dilevary_boot_system_flash_flag("Pass");
				} else if (rs.getInt("boot_system_flash_flag") == 2) {
					req.setOs_upgrade_dilevary_boot_system_flash_flag("Fail");

				} else {
					req.setOs_upgrade_dilevary_boot_system_flash_flag("Not conducted");

				}
				if (rs.getInt("reload_flag") == 1) {
					req.setOs_upgrade_dilevary_reload_flag("Pass");
				} else if (rs.getInt("reload_flag") == 2) {
					req.setOs_upgrade_dilevary_reload_flag("Fail");

				} else {
					req.setOs_upgrade_dilevary_reload_flag("Not conducted");

				}
				if (rs.getInt("post_login_flag") == 1) {
					req.setOs_upgrade_dilevary_post_login_flag("Pass");
				} else if (rs.getInt("post_login_flag") == 2) {
					req.setOs_upgrade_dilevary_post_login_flag("Fail");

				} else {
					req.setOs_upgrade_dilevary_post_login_flag("Not conducted");

				}

			}

		} catch (SQLException e) {
			// TODO Auto-generated catch block

			e.printStackTrace();
		} finally {
			DBUtil.close(rs);

			DBUtil.close(connection);
		}
		return req;
	}

	public final org.json.simple.JSONObject get_dilevary_steps_status(String requestId, String version) {
		org.json.simple.JSONObject obj = new org.json.simple.JSONObject();
		connection = ConnectionFactory.getConnection();
		String query1 = "SELECT * FROM os_upgrade_dilevary_flags WHERE request_id=? AND request_version=?";
		ResultSet rs = null;
		String result = "false";

		try {

			PreparedStatement ps = connection.prepareStatement(query1);

			ps.setString(1, requestId);
			ps.setString(2, version);

			rs = ps.executeQuery();

			while (rs.next()) {
				if (rs.getInt("login_flag") == 1) {
					obj.put("login", rs.getInt("login_flag"));
				} else {
					obj.put("login", rs.getInt("login_flag"));

				}
				if (rs.getInt("flash_size_flag") == 1) {
					obj.put("flash_size", rs.getInt("flash_size_flag"));
				} else {
					obj.put("flash_size", rs.getInt("flash_size_flag"));

				}
				if (rs.getInt("back_up_flag") == 1) {
					obj.put("back_up", rs.getInt("back_up_flag"));
				} else {
					obj.put("back_up", rs.getInt("back_up_flag"));

				}
				if (rs.getInt("os_download_flag") == 1) {
					obj.put("os_download", rs.getInt("os_download_flag"));
				} else {
					obj.put("os_download", rs.getInt("os_download_flag"));

				}
				if (rs.getInt("boot_system_flash_flag") == 1) {
					obj.put("boot_system_flash", rs.getInt("boot_system_flash_flag"));
				} else {
					obj.put("boot_system_flash_flag", rs.getInt("boot_system_flash_flag"));

				}
				if (rs.getInt("reload_flag") == 1) {
					obj.put("reload", rs.getInt("reload_flag"));
				} else {
					obj.put("reload", rs.getInt("reload_flag"));

				}
				if (rs.getInt("post_login_flag") == 1) {
					obj.put("post_login", rs.getInt("post_login_flag"));
				} else {
					obj.put("post_login", rs.getInt("post_login_flag"));

				}

			}

		} catch (SQLException e) {
			// TODO Auto-generated catch block

			e.printStackTrace();
		} finally {
			DBUtil.close(rs);

			DBUtil.close(connection);
		}

		return obj;
	}

	public String getRequestFlagForReportPreHealthCheck(String requestId, String versionId) {
		connection = ConnectionFactory.getConnection();
		String query = "";
		ResultSet rs = null;
		PreparedStatement preparedStmt;
		String flagForPrevalidation = "";
		String flagFordelieverConfig = "";
		String res = null;
		Map<String, String> hmap = new HashMap<String, String>();
		logger.info("Version received" + versionId);

		query = "select pre_health_checkup from  webserviceinfo where alphanumeric_req_id = ? and version = ? ";
		try {
			preparedStmt = connection.prepareStatement(query);
			preparedStmt.setString(1, requestId);
			preparedStmt.setString(2, versionId);
			rs = preparedStmt.executeQuery();
			if (rs != null) {
				while (rs.next()) {
					res = rs.getString("pre_health_checkup");
				}
			}
			hmap.put("flagForPrevalidation", flagForPrevalidation);
			hmap.put("flagFordelieverConfig", flagFordelieverConfig);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return res;
	}

	/*
	 * Owner: Ruchita Salvi Module: Test Strategey
	 */
	public List<TestDetail> findTestFromTestStrategyDB(String devicemodel, String devicetype, String os,
			String osversion, String vendor, String region, String testCategory) {
		List<TestDetail> list = new ArrayList<TestDetail>();

		connection = ConnectionFactory.getConnection();
		String query = "", query1 = "", query2 = "", query3 = "";
		ResultSet rs = null, rs1 = null, rs2 = null, rs3 = null;
		String maxVersion = null;

		Set setOfTest = new HashSet();

		PreparedStatement preparedStmt, preparedSmt1, preparedSmt3;
		query = "select * from  t_tststrategy_m_tstdetails where device_model = ? and device_type = ? and os=? and os_version=? and vendor=? and region=? and test_category=?";
		query1 = "select * from t_tststrategy_m_tstrules where test_name=?";
		try {
			preparedStmt = connection.prepareStatement(query);
			preparedStmt.setString(1, devicemodel);
			preparedStmt.setString(2, devicetype);
			preparedStmt.setString(3, os);
			preparedStmt.setString(4, osversion);
			preparedStmt.setString(5, vendor);
			preparedStmt.setString(6, region);
			preparedStmt.setString(7, testCategory);

			rs = preparedStmt.executeQuery();
			if (rs != null) {
				while (rs.next()) {

					PreparedStatement preparedStmt2;
					String testName = rs.getString("test_name");

					if (!(setOfTest.contains(testName))) {

						query2 = "select * from  t_tststrategy_m_tstdetails where device_model = ? and device_type = ? and os=? and os_version=? and vendor=? and region=? and test_category=? and test_name=?";

						preparedStmt2 = connection.prepareStatement(query2);
						preparedStmt2.setString(1, devicemodel);
						preparedStmt2.setString(2, devicetype);
						preparedStmt2.setString(3, os);
						preparedStmt2.setString(4, osversion);
						preparedStmt2.setString(5, vendor);
						preparedStmt2.setString(6, region);
						preparedStmt2.setString(7, testCategory);
						preparedStmt2.setString(8, testName);

						rs2 = preparedStmt2.executeQuery();

						if (rs2 != null) {
							while (rs2.next()) {

								maxVersion = rs2.getString("version");
							}

							PreparedStatement preparedStmt3;
							query3 = "select * from  t_tststrategy_m_tstdetails where device_model = ? and device_type = ? and os=? and os_version=? and vendor=? and region=? and test_category=? and test_name=? and version=?";

							preparedStmt3 = connection.prepareStatement(query3);
							preparedStmt3.setString(1, devicemodel);
							preparedStmt3.setString(2, devicetype);
							preparedStmt3.setString(3, os);
							preparedStmt3.setString(4, osversion);
							preparedStmt3.setString(5, vendor);
							preparedStmt3.setString(6, region);
							preparedStmt3.setString(7, testCategory);
							preparedStmt3.setString(8, testName);
							preparedStmt3.setString(9, maxVersion);

							rs3 = preparedStmt3.executeQuery();
							if (rs3 != null) {
								while (rs3.next()) {

									TestDetail test = new TestDetail();
									test.setId(rs3.getInt("id"));
									test.setTestCommand(rs3.getString("test_command"));
									test.setTestConnectionProtocol(rs.getString("test_connection_protocol"));
									test.setTestName(rs3.getString("test_name").concat("_" + rs3.getString("version")));
									test.setTestCategory(rs3.getString("test_category"));
									test.setVersion(rs3.getString("version"));
									List<TestRules> rulelist = new ArrayList<TestRules>();
									preparedSmt1 = connection.prepareStatement(query1);
									preparedSmt1.setInt(1, test.getId());
									rs1 = preparedSmt1.executeQuery();

									while (rs1.next()) {
										TestRules rule = new TestRules();
										rule.setDataType(rs1.getString("data_type"));
										rule.setAfterText(rs1.getString("after_text"));
										rule.setBeforeText(rs1.getString("before_text"));
										rule.setFromColumn(rs1.getString("from_column"));
										rule.setNumberOfChars(rs1.getString("number_of_chars"));
										rule.setReferenceColumn(rs1.getString("reference_column"));
										rule.setReportedLabel(rs1.getString("reported_label"));
										rule.setSectionName(rs1.getString("section_name"));
										rule.setWhereKeyword(rs1.getString("where_keyword"));
										rule.setEvaluation(rs1.getString("evaluation"));
										rule.setOperator(rs1.getString("operator"));
										rule.setValue1(rs1.getString("value1"));
										rule.setValue2(rs1.getString("value2"));
										rule.setSnippet(rs1.getString("snippet"));
										rule.setKeyword(rs1.getString("keyword"));
										rulelist.add(rule);
									}

									test.setListRules(rulelist);

									list.add(test);

								}

							}

						}

						setOfTest.add(testName);

					}

				}

			}

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			DBUtil.close(connection);
			DBUtil.close(rs1);
			DBUtil.close(rs1);

		}
		return list;

	}

	/*
	 * Owner: Ruchita Salvi Module: Test Strategey
	 */
	public boolean updateTestStrategeyConfigResultsTable(String requestID, String testName, String testCategory,
			String testResult, String testText, String collectedValue, String evaluationCriteria, String notes,
			String data_type) {
		boolean res = false;
		connection = ConnectionFactory.getConnection();
		String query = "insert into t_tststrategy_m_config_results (TestResult,ResultText,RequestId,TestCategory,testName,CollectedValue,EvaluationCriteria,notes,data_type) values (?,?,?,?,?,?,?,?,?)";

		try {
			PreparedStatement ps = connection.prepareStatement(query);
			ps.setString(1, testResult);
			ps.setString(2, testText);
			ps.setString(3, requestID);
			ps.setString(4, testCategory);
			ps.setString(5, testName);
			ps.setString(6, collectedValue);
			ps.setString(7, evaluationCriteria);
			ps.setString(8, notes);
			ps.setString(9, data_type);
			int i = ps.executeUpdate();
			if (i == 1) {

				res = true;

			} else {
				res = false;
			}
		}

		catch (SQLException e) {

			e.printStackTrace();
		}
		return res;
	}

	/*
	 * Owner: Ruchita Salvi Module: Test Strategey
	 */
	/*
	 * public boolean insertIntoTestStrategeyConfigResultsTable(String requestId,
	 * String testCategory, String testResult, String testText, String testName) {
	 * boolean res = false; connection = ConnectionFactory.getConnection(); String
	 * query = "";
	 * 
	 * query =
	 * "INSERT INTO t_tststrategy_m_config_results(RequestId,TestCategory,TestResult,ResultText,testName) VALUES"
	 * + "(?,?,?,?,?)";
	 * 
	 * PreparedStatement preparedStmt = null; try { preparedStmt =
	 * connection.prepareStatement(query);
	 * 
	 * preparedStmt.setString(1, requestId); preparedStmt.setString(2,
	 * testCategory); preparedStmt.setString(3, testResult);
	 * preparedStmt.setString(4, testText); preparedStmt.setString(5, testName);
	 * 
	 * int i=preparedStmt.executeUpdate(); if(i>0) { res=true; }
	 * 
	 * } catch (SQLException e) { // TODO Auto-generated catch block
	 * e.printStackTrace(); } finally { DBUtil.close(connection);
	 * DBUtil.close(preparedStmt);
	 * 
	 * } return res; }
	 */

	/*
	 * Owner: Ruchita Salvi Module: Test Strategey
	 */
	@SuppressWarnings("unchecked")
	public org.json.simple.JSONArray getDynamicTestResult(String requestId, String version, String testtype) {
		org.json.simple.JSONObject res = new org.json.simple.JSONObject();
		org.json.simple.JSONArray array = new org.json.simple.JSONArray();

		if (!version.contains(".")) {
			version = version + ".0";
		}
		connection = ConnectionFactory.getConnection();
		String query = "";
		ResultSet rs = null;
		PreparedStatement preparedStmt;

		query = "select * from  t_tststrategy_m_config_results where RequestId = ? and TestCategory= ?";
		try {
			preparedStmt = connection.prepareStatement(query);
			preparedStmt.setString(1, requestId);
			preparedStmt.setString(2, testtype);
			rs = preparedStmt.executeQuery();
			if (rs != null) {
				while (rs.next()) {
					org.json.simple.JSONObject obj = new org.json.simple.JSONObject();
					obj.put("category", rs.getString("TestCategory"));
					if (rs.getString("TestResult").equalsIgnoreCase("Passed")) {
						obj.put("status", "1");

					} else if (rs.getString("TestResult").equalsIgnoreCase("Failed")) {
						obj.put("status", "2");

					} else {
						obj.put("status", "0");

					}
					obj.put("value", rs.getString("ResultText"));
					obj.put("testName", rs.getString("testName"));
					array.add(obj);
				}
			}

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		res.put("custom", array);
		return array;
	}

	/*
	 * Owner: Ruchita Salvi Module: Test Strategey
	 */
	@SuppressWarnings("unchecked")
	public org.json.simple.JSONArray getDynamicTestResultCustomerReport(String requestId, String version,
			String testtype) {
		org.json.simple.JSONObject res = new org.json.simple.JSONObject();
		org.json.simple.JSONArray array = new org.json.simple.JSONArray();

		if (!version.contains(".")) {
			version = version + ".0";
		}
		connection = ConnectionFactory.getConnection();
		String query = "";
		ResultSet rs = null;
		PreparedStatement preparedStmt;

		query = "select * from  t_tststrategy_m_config_results where RequestId = ? and TestCategory= ?";
		try {
			preparedStmt = connection.prepareStatement(query);
			preparedStmt.setString(1, requestId);
			preparedStmt.setString(2, testtype);
			rs = preparedStmt.executeQuery();
			if (rs != null) {
				while (rs.next()) {
					org.json.simple.JSONObject obj = new org.json.simple.JSONObject();

					obj.put("status", rs.getString("TestResult"));
					obj.put("CollectedValue", rs.getString("CollectedValue"));
					obj.put("EvaluationCriteria", rs.getString("EvaluationCriteria"));

					obj.put("testname", rs.getString("testName").substring(15).concat("_")
							.concat(rs.getString("data_type")).concat("_").concat(rs.getString("ResultText")));
					obj.put("notes", rs.getString("notes"));
					obj.put("dataType", rs.getString("data_type"));
					obj.put("keyword", rs.getString("CollectedValue"));

					obj.put("evaluationStatus", "N/A");
					array.add(obj);
				}
			}

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		res.put("custom", array);
		return array;
	}

	/*
	 * Owner: Ruchita Salvi Module: Test Strategey
	 */
	@SuppressWarnings("unchecked")
	public org.json.simple.JSONObject getStatusForCustomerReport(CreateConfigRequestDCM request) {
		org.json.simple.JSONObject obj = new org.json.simple.JSONObject();
		CertificationTestPojo certificationTestPojo1 = new CertificationTestPojo();
		CertificationTestPojo certificationTestPojo2 = new CertificationTestPojo();

		CertificationTestPojo certificationTestPojo3 = new CertificationTestPojo();

		CertificationTestResultEntity resultEnt = new CertificationTestResultEntity();
		org.json.simple.JSONArray prevalidationArray = new org.json.simple.JSONArray();
		org.json.simple.JSONObject reachabilityObj = new org.json.simple.JSONObject();
		org.json.simple.JSONObject iosVersion = new org.json.simple.JSONObject();
		org.json.simple.JSONObject deviceModel = new org.json.simple.JSONObject();
		org.json.simple.JSONObject vendorTest = new org.json.simple.JSONObject();
		NetworkTestValidation networkTestValidation = new NetworkTestValidation();
		org.json.simple.JSONArray othersArray = new org.json.simple.JSONArray();
		org.json.simple.JSONArray networkAuditArray = new org.json.simple.JSONArray();
		certificationTestPojo1 = getCertificationTestFlagData(request.getRequestId(), request.getVersion_report(),
				"preValidate");
		certificationTestService = new CertificationTestResultService();

		resultEnt = certificationTestService.getRecordByRequestId(request.getRequestId(), request.getVersion_report());

		if (certificationTestPojo1.getDeviceReachabilityTest().equalsIgnoreCase("2")) {
			reachabilityObj.put("testname", "Device Reachability test");
			reachabilityObj.put("status", "Failed");
			reachabilityObj.put("outcome", "");
			reachabilityObj.put("notes", "N/A");

		}
		if (certificationTestPojo1.getDeviceReachabilityTest().equalsIgnoreCase("1")) {
			reachabilityObj.put("testname", "Device Reachability test");
			reachabilityObj.put("status", "Passed");
			reachabilityObj.put("outcome", "");
			reachabilityObj.put("notes", "N/A");
		}
		if (certificationTestPojo1.getIosVersionTest().equalsIgnoreCase("2")) {
			iosVersion.put("testname", "OS");
			iosVersion.put("status", "Failed");
			iosVersion.put("outcome", "");
			iosVersion.put("notes", "N/A");
		}
		if (certificationTestPojo1.getIosVersionTest().equalsIgnoreCase("1")) {
			iosVersion.put("testname", "OS");
			iosVersion.put("status", "Passed");
			iosVersion.put("outcome", "");
			iosVersion.put("notes", "N/A");
		}
		if (certificationTestPojo1.getDeviceModelTest().equalsIgnoreCase("2")) {
			deviceModel.put("testname", "Device Model");
			deviceModel.put("status", "Failed");
			deviceModel.put("outcome", "");
			deviceModel.put("notes", "N/A");
		}
		if (certificationTestPojo1.getDeviceModelTest().equalsIgnoreCase("1")) {
			deviceModel.put("testname", "Device Model");
			deviceModel.put("status", "Passed");
			deviceModel.put("outcome", "");
			deviceModel.put("notes", "N/A");
		}
		if (certificationTestPojo1.getVendorTest().equalsIgnoreCase("2")) {
			vendorTest.put("testname", "Vendor Test");
			vendorTest.put("status", "Failed");
			vendorTest.put("outcome", "");
			vendorTest.put("notes", "N/A");
		}
		if (certificationTestPojo1.getVendorTest().equalsIgnoreCase("1")) {
			vendorTest.put("testname", "Vendor Test");
			vendorTest.put("status", "Passed");
			vendorTest.put("outcome", "");
			vendorTest.put("notes", "N/A");
		}
		prevalidationArray.add(vendorTest);
		prevalidationArray.add(deviceModel);
		prevalidationArray.add(iosVersion);
		prevalidationArray.add(reachabilityObj);

		org.json.simple.JSONArray networkArray = new org.json.simple.JSONArray();

		org.json.simple.JSONObject networkIfObj = new org.json.simple.JSONObject();
		org.json.simple.JSONObject networkPlatformIOS = new org.json.simple.JSONObject();
		org.json.simple.JSONObject waninterface = new org.json.simple.JSONObject();
		org.json.simple.JSONObject bgpneighbour = new org.json.simple.JSONObject();

		certificationTestPojo2 = getCertificationTestFlagData(request.getRequestId(), request.getVersion_report(),
				"networkTest");
		if (certificationTestPojo2.getShowIpIntBriefCmd().equalsIgnoreCase("1")) {
			networkIfObj.put("testname", "Network Interface Status");
			networkIfObj.put("status", "Passed");
			networkIfObj.put("outcome", "");
			networkIfObj.put("notes", "N/A");
		}
		if (certificationTestPojo2.getShowInterfaceCmd().equalsIgnoreCase("1")) {
			waninterface.put("testname", "Wan Interface");
			waninterface.put("status", "Passed");
			waninterface.put("outcome", "");
			waninterface.put("notes", "N/A");
		}
		if (certificationTestPojo2.getShowVersionCmd().equalsIgnoreCase("1")) {
			networkPlatformIOS.put("testname", "Network Platform IOS");
			networkPlatformIOS.put("status", "Passed");
			networkPlatformIOS.put("outcome", "");
			networkPlatformIOS.put("notes", "N/A");
		}
		if (certificationTestPojo2.getShowIpBgpSummaryCmd().equalsIgnoreCase("1")) {
			bgpneighbour.put("testname", "BGP Neighbour");
			bgpneighbour.put("status", "Passed");
			bgpneighbour.put("outcome", "");
			bgpneighbour.put("notes", "N/A");
		}
		networkArray.add(bgpneighbour);
		networkArray.add(waninterface);
		networkArray.add(networkPlatformIOS);
		networkArray.add(networkIfObj);

		org.json.simple.JSONArray healthArray = new org.json.simple.JSONArray();

		org.json.simple.JSONObject throughputObj = new org.json.simple.JSONObject();
		org.json.simple.JSONObject latencyObj = new org.json.simple.JSONObject();
		org.json.simple.JSONObject FrameLossObj = new org.json.simple.JSONObject();

		certificationTestPojo3 = getCertificationTestFlagData(request.getRequestId(), request.getVersion_report(),
				"HealthTest");
		if (null != certificationTestPojo3.getThroughput() && certificationTestPojo3.getThroughput() != "") {
			throughputObj.put("testname", "Throughput");
			throughputObj.put("status", "Passed");
			throughputObj.put("outcome", certificationTestPojo3.getThroughput());
			throughputObj.put("notes", "N/A");
		} else {
			throughputObj.put("testname", "Throughput");
			throughputObj.put("status", "Passed");
			throughputObj.put("outcome", "-1");
			throughputObj.put("notes", "N/A");
		}

		if (null != certificationTestPojo3.getLatency() && certificationTestPojo3.getLatency() != "") {
			latencyObj.put("testname", "Latency");
			latencyObj.put("status", "Passed");
			latencyObj.put("outcome", certificationTestPojo3.getLatency());
			latencyObj.put("notes", "N/A");
		} else {

			latencyObj.put("testname", "Latency");
			latencyObj.put("status", "Passed");
			latencyObj.put("outcome", "-1");
			latencyObj.put("notes", "N/A");
		}

		if (null != certificationTestPojo3.getFrameLoss() && certificationTestPojo3.getFrameLoss() != "") {
			FrameLossObj.put("testname", "Frameloss");
			FrameLossObj.put("status", "Passed");
			FrameLossObj.put("outcome", certificationTestPojo3.getFrameLoss());
			FrameLossObj.put("notes", "N/A");
		} else {
			FrameLossObj.put("testname", "Frameloss");
			FrameLossObj.put("status", "Passed");
			FrameLossObj.put("outcome", "-1");
			FrameLossObj.put("notes", "N/A");
		}
		healthArray.add(FrameLossObj);
		healthArray.add(latencyObj);
		healthArray.add(throughputObj);

		org.json.simple.JSONArray dynamicTestResultArray = new org.json.simple.JSONArray();
		dynamicTestResultArray = getDynamicTestResultCustomerReport(request.getRequestId(), request.getVersion_report(),
				"Network Test");

		if (dynamicTestResultArray.size() > 0) {
			for (int i = 0; i < dynamicTestResultArray.size(); i++) {
				networkArray.add(dynamicTestResultArray.get(i));
			}
		}

		org.json.simple.JSONArray dynamicTestResultArray1 = new org.json.simple.JSONArray();
		dynamicTestResultArray1 = getDynamicTestResultCustomerReport(request.getRequestId(),
				request.getVersion_report(), "Health Check");

		if (dynamicTestResultArray1.size() > 0) {
			for (int i = 0; i < dynamicTestResultArray1.size(); i++) {
				healthArray.add(dynamicTestResultArray1.get(i));
			}
		}

		org.json.simple.JSONArray dynamicTestResultArray2 = new org.json.simple.JSONArray();
		dynamicTestResultArray2 = getDynamicTestResultCustomerReport(request.getRequestId(),
				request.getVersion_report(), "Device Prevalidation");

		if (dynamicTestResultArray2.size() > 0) {
			for (int i = 0; i < dynamicTestResultArray2.size(); i++) {
				prevalidationArray.add(dynamicTestResultArray2.get(i));
			}
		}

		org.json.simple.JSONArray dynamicTestResultArray3 = new org.json.simple.JSONArray();
		dynamicTestResultArray3 = getDynamicTestResultCustomerReport(request.getRequestId(),
				request.getVersion_report(), "Others");

		if (dynamicTestResultArray3.size() > 0) {
			for (int i = 0; i < dynamicTestResultArray3.size(); i++) {
				othersArray.add(dynamicTestResultArray3.get(i));
			}
		}
		org.json.simple.JSONArray dynamicTestResultArray4 = new org.json.simple.JSONArray();
		dynamicTestResultArray4 = getDynamicTestResultCustomerReport(request.getRequestId(),
				request.getVersion_report(), "Network Audit");

		if (dynamicTestResultArray4.size() > 0) {
			for (int i = 0; i < dynamicTestResultArray4.size(); i++) {
				networkAuditArray.add(dynamicTestResultArray4.get(i));
			}
		}
		obj.put("Prevalidation", prevalidationArray);
		obj.put("Network", networkArray);
		obj.put("Health_Check", healthArray);
		obj.put("Others", othersArray);
		obj.put("NetworkAudit", networkAuditArray);
		return obj;
	}

	/*
	 * Owner: Ruchita Salvi Module: Test Strategey
	 */
	public List<TestDetail> findSelectedTests(String requestID, String testCategory) {
		List<TestDetail> resultList = new ArrayList<TestDetail>();
		connection = ConnectionFactory.getConnection();
		String query = "";
		ResultSet rs = null;
		PreparedStatement preparedStmt;
		String res = null;
		Map<String, String> hmap = new HashMap<String, String>();

		query = "select TestsSelected from  t_tststrategy_m_config_transaction where RequestId = ?";
		try {
			preparedStmt = connection.prepareStatement(query);
			preparedStmt.setString(1, requestID);
			rs = preparedStmt.executeQuery();
			if (rs != null) {
				while (rs.next()) {
					res = rs.getString("TestsSelected");
					if (rs.getString("TestsSelected") != null) {
						JSONArray jsonArray = new JSONArray(res);
						for (int i = 0; i < jsonArray.length(); i++) {
							JSONObject explrObject = jsonArray.getJSONObject(i);
							if (explrObject.get("testCategory").toString().equalsIgnoreCase(testCategory)) {
								TestDetail test = new TestDetail();
								test.setTestName(explrObject.getString("testName"));
								resultList.add(test);
							}
						}
					}
				}
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return resultList;
	}

	/* Commeting out session management code */

	/*
	 * public int checkedUserStatus(String username, String password) throws
	 * SQLException {
	 * 
	 * connection = ConnectionFactory.getConnection(); // String query = //
	 * "SELECT user_status FROM users where user_name LIKE ? AND user_password LIKE ?"
	 * ; ResultSet rs = null;
	 * 
	 * int userStatus = 0;
	 * 
	 * try { PreparedStatement pst = null;
	 * 
	 * pst = connection .prepareStatement(
	 * "SELECT user_status FROM users where user_name=? AND user_password=?");
	 * pst.setString(1, username); pst.setString(2, password); rs =
	 * pst.executeQuery(); while (rs.next()) { userStatus =
	 * rs.getInt("user_status"); } } catch (SQLException e) {
	 * 
	 * e.printStackTrace(); } finally { rs.close(); } return userStatus;
	 * 
	 * }
	 */

	public String getPreviousMileStoneStatus(String requestID, String version) {
		String status = null;
		// logic to get previous status from reqestinfoso
		String query2 = "select request_status from requestinfoso where alphanumeric_req_id = ? and request_version = ?";

		try {
			PreparedStatement preparedStmt2;
			ResultSet rs2 = null;

			preparedStmt2 = connection.prepareStatement(query2);
			preparedStmt2.setString(1, requestID);

			preparedStmt2.setString(2, version);
			rs2 = preparedStmt2.executeQuery();
			while (rs2.next()) {

				status = rs2.getString("request_status");
			}

		} catch (SQLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		return status;
	}

	@SuppressWarnings("unchecked")
	public org.json.simple.JSONArray getNetworkAuditReport(String requestId, String version, String testtype) {
		org.json.simple.JSONObject res = new org.json.simple.JSONObject();
		org.json.simple.JSONArray array = new org.json.simple.JSONArray();

		if (!version.contains(".")) {
			version = version + ".0";
		}
		connection = ConnectionFactory.getConnection();
		String query = "", query1 = "";
		ResultSet rs = null, rs1 = null;
		PreparedStatement preparedStmt, preparedStmt1;

		query = "select * from  t_tststrategy_m_config_results where RequestId = ? and TestCategory= ?";
		query1 = "select * from  t_tststrategy_m_tstrules where RequestId = ? and TestCategory= ?";
		try {
			preparedStmt = connection.prepareStatement(query);
			preparedStmt.setString(1, requestId);
			preparedStmt.setString(2, testtype);
			rs = preparedStmt.executeQuery();
			if (rs != null) {
				while (rs.next()) {
					org.json.simple.JSONObject obj = new org.json.simple.JSONObject();
					obj.put("category", rs.getString("TestCategory"));
					if (rs.getString("TestResult").equalsIgnoreCase("Passed")) {
						obj.put("status", "1");

					} else if (rs.getString("TestResult").equalsIgnoreCase("Fail")) {
						obj.put("status", "2");

					} else {
						obj.put("status", "0");

					}

					preparedStmt1 = connection.prepareStatement(query1);
					preparedStmt1.setString(1, requestId);
					preparedStmt1.setString(2, testtype);
					rs1 = preparedStmt1.executeQuery();
					if (rs1 != null) {
						while (rs1.next()) {

							String data = rs1.getString("data_type");
						}
					}

					obj.put("Execution Status", rs.getString("ResultText"));
					obj.put("TestName", rs.getString("testName"));
					array.add(obj);

				}
			}

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		res.put("custom", array);
		return array;
	}

	// To retrieve user stored snippet
	// Author: Ruchita Salvi Date : 13.01.2020
	public String getSnippet(String data_type, String label, String test_name) {
		int id = 0;
		String snippet = null;
		String query1 = "select id from t_tststrategy_m_tstdetails where test_name = ?";
		String query2 = "select snippet from t_tststrategy_m_tstrules where reported_label = ? and test_name=?";
		connection = ConnectionFactory.getConnection();

		try {
			PreparedStatement preparedStmt2;
			ResultSet rs2 = null;

			preparedStmt2 = connection.prepareStatement(query1);
			preparedStmt2.setString(1, test_name);

			rs2 = preparedStmt2.executeQuery();
			while (rs2.next()) {

				id = rs2.getInt("id");
			}

			preparedStmt2.close();
			rs2.close();
			preparedStmt2 = connection.prepareStatement(query2);
			preparedStmt2.setString(1, label);
			preparedStmt2.setInt(2, id);

			rs2 = preparedStmt2.executeQuery();
			while (rs2.next()) {

				snippet = rs2.getString("snippet");
			}

		} catch (SQLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		return snippet;
	}

	public List<TestDetail> getAllTests() {
		List<TestDetail> list = new ArrayList<TestDetail>();
		String query = "select * from t_tststrategy_m_tstdetails";
		ResultSet rs = null;
		PreparedStatement pst = null;
		connection = ConnectionFactory.getConnection();

		try {

			pst = connection.prepareStatement(query);

			rs = pst.executeQuery();
			TestDetail request;
			int id;
			while (rs.next()) {
				request = new TestDetail();
				request.setTestName(rs.getString("test_name"));
				request.setVersion(rs.getString("version"));
				request.setTestId(rs.getString("id"));
				request.setVendor(rs.getString("vendor"));
				request.setDeviceType(rs.getString("device_type"));
				request.setDeviceModel(rs.getString("device_model"));
				request.setOs(rs.getString("os"));
				request.setOsVersion(rs.getString("os_version"));
				request.setRegion(rs.getString("region"));
				request.setCreatedOn(rs.getString("created_on"));
				request.setCreatedBy(rs.getString("created_by"));
				request.setComment(rs.getString("comment"));
				request.setEnabled(rs.getBoolean("is_enabled"));

				list.add(request);
			}
		} catch (Exception e) {

		}
		return list;
	}

	public void updateVersion(String testName, boolean is_enabled) {
		connection = ConnectionFactory.getConnection();
		String query = null;
		String query1 = null;
		query = "update t_tststrategy_m_tstdetails set is_enabled=? where test_name = ? ";
		PreparedStatement preparedStmt;
		try {
			preparedStmt = connection.prepareStatement(query);

			preparedStmt.setBoolean(1, is_enabled);
			preparedStmt.setString(2, testName);

			preparedStmt.executeUpdate();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void updateRequestforReportWebserviceInfo(String requestId) {

		connection = ConnectionFactory.getConnection();
		String query = null;

		query = "Update webserviceinfo Set health_checkup='0', network_test='0',network_audit='0' where alphanumeric_req_id = ?";

		PreparedStatement preparedStmt = null;
		try {

			preparedStmt = connection.prepareStatement(query);
			preparedStmt.setString(1, requestId);
			preparedStmt.execute("SET FOREIGN_KEY_CHECKS=0");
			preparedStmt.executeUpdate();

			logger.info("I am here");

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	@Autowired
	public RequestDetailsBackUpAndRestoreRepo requestDetailsBackUpAndRestoreRepo;

	@SuppressWarnings("unchecked")
	public org.json.simple.JSONObject getStatusForBackUpRequestCustomerReport(CreateConfigRequestDCM request) {
		org.json.simple.JSONObject obj = new org.json.simple.JSONObject();
		CertificationTestPojo certificationTestPojo1 = new CertificationTestPojo();

		org.json.simple.JSONArray prevalidationArray = new org.json.simple.JSONArray();
		org.json.simple.JSONObject reachabilityObj = new org.json.simple.JSONObject();
		org.json.simple.JSONObject iosVersion = new org.json.simple.JSONObject();
		org.json.simple.JSONObject deviceModel = new org.json.simple.JSONObject();
		org.json.simple.JSONObject vendorTest = new org.json.simple.JSONObject();
		org.json.simple.JSONObject backUpStatus = new org.json.simple.JSONObject();
		String model = null, vendor = null, requestId = null, deliveryStatus = null;

		certificationTestPojo1 = getCertificationTestFlagData(request.getRequestId(), request.getVersion_report(),
				"preValidate");

		requestId = request.getRequestId();

		connection = ConnectionFactory.getConnection();
		String query = "SELECT * FROM requestinfoso where alphanumeric_req_id =?";
		String query1 = "SELECT * FROM webserviceinfo where alphanumeric_req_id =?";

		ResultSet rs = null, rs1 = null;

		PreparedStatement pst = null, pst1 = null;

		try {
			pst = connection.prepareStatement(query);
			pst.setString(1, requestId);

			rs = pst.executeQuery();

			if (rs != null) {
				while (rs.next()) {

					model = rs.getString("model");
					vendor = rs.getString("vendor");

				}
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		try {
			pst1 = connection.prepareStatement(query1);
			pst1.setString(1, requestId);

			rs1 = pst1.executeQuery();

			if (rs1 != null) {
				while (rs1.next()) {

					deliveryStatus = rs1.getString("deliever_config");

				}
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			DBUtil.close(rs);
			DBUtil.close(statement);
			DBUtil.close(connection);
		}

		if (certificationTestPojo1.getDeviceReachabilityTest().equalsIgnoreCase("2")) {
			reachabilityObj.put("testname", "Device Reachability test");
			reachabilityObj.put("status", "Failed");
			reachabilityObj.put("outcome", "");
			reachabilityObj.put("notes", "N/A");

		}
		if (certificationTestPojo1.getDeviceReachabilityTest().equalsIgnoreCase("1")) {
			reachabilityObj.put("testname", "Device Reachability test");
			reachabilityObj.put("status", "Passed");
			reachabilityObj.put("outcome", "");
			reachabilityObj.put("notes", "N/A");
		}

		if (certificationTestPojo1.getDeviceModelTest().equalsIgnoreCase("2")) {
			deviceModel.put("testname", "Device Model");
			deviceModel.put("status", "Failed");
			deviceModel.put("outcome", model);
			deviceModel.put("notes", "N/A");
		}
		if (certificationTestPojo1.getDeviceModelTest().equalsIgnoreCase("1")) {
			deviceModel.put("testname", "Device Model");
			deviceModel.put("status", "Passed");
			deviceModel.put("outcome", model);
			deviceModel.put("notes", "N/A");
		}
		if (certificationTestPojo1.getVendorTest().equalsIgnoreCase("2")) {
			vendorTest.put("testname", "Vendor Test");
			vendorTest.put("status", "Failed");
			vendorTest.put("outcome", vendor);
			vendorTest.put("notes", "N/A");
		}
		if (certificationTestPojo1.getVendorTest().equalsIgnoreCase("1")) {
			vendorTest.put("testname", "Vendor Test");
			vendorTest.put("status", "Passed");
			vendorTest.put("outcome", vendor);
			vendorTest.put("notes", "N/A");
		}

		if (deliveryStatus.equals("1")) {
			backUpStatus.put("backupstatus", "Success");
		} else {
			backUpStatus.put("backupstatus", "Failed");
		}
		prevalidationArray.add(vendorTest);
		prevalidationArray.add(deviceModel);

		prevalidationArray.add(reachabilityObj);
		prevalidationArray.add(backUpStatus);

		obj.put("Prevalidation", prevalidationArray);
		return obj;
	}

	public List<RequestInfoSO> getCertificationtestvalidationProcedure(String value) {
		connection = ConnectionFactory.getConnection();
		String query = "{ CALL `requestinfo`.`GetAllRequest`() }";

		ResultSet rs = null;

		RequestInfoSO request = null;
		PreparedStatement pst = null;
		List<RequestInfoSO> requestInfoList = null;
		try {
			pst = connection.prepareStatement(query);
			// pst.setString(1, value);

			rs = pst.executeQuery();
			requestInfoList = new ArrayList<RequestInfoSO>();

			if (rs != null) {
				while (rs.next()) {
					request = new RequestInfoSO();
					request.setRequest_id(rs.getInt("request_info_id"));
					// request.set(rs.getString("alphanumeric_req_id"));
					request.setCertificationSelectionBit(rs.getString("certificationSelectionBit"));

					request.setRequest_version(rs.getDouble("request_version"));

					requestInfoList.add(request);
				}
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			DBUtil.close(rs);
			DBUtil.close(statement);
			DBUtil.close(connection);
		}
		return requestInfoList;
	}

	public boolean get_dilevary_status(String string) {

		connection = ConnectionFactory.getConnection();
		String query = "SELECT * FROM webserviceinfo where alphanumeric_req_id =?";
		ResultSet rs = null;
		boolean deliver_status = false;

		PreparedStatement pst = null;

		try {
			pst = connection.prepareStatement(query);
			pst.setString(1, string);

			rs = pst.executeQuery();

			if (rs != null) {
				while (rs.next()) {

					int deleveryConfig = rs.getInt("deliever_config");
					if (deleveryConfig == 1) {
						deliver_status = true;
					} else if (deleveryConfig == 2) {
						deliver_status = false;
					}

				}
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return deliver_status;
	}

	/* Dhanshri Mane 6/2/2020 for getAll request for Request Type */
	public int getRequestTpyeData(String requestType) {
		int num = 0;
		connection = ConnectionFactory.getConnection();
		String query1 = null;
		ResultSet rs = null;
		if (Global.loggedInUser.equalsIgnoreCase("seuser")) {
			query1 = "SELECT COUNT(request_info_id) AS total FROM requestinfoso Where alphanumeric_req_id like ? and request_creator_name=?;";
		} else if (Global.loggedInUser.equalsIgnoreCase("feuser")) {
			query1 = "SELECT COUNT(request_info_id) AS total FROM requestinfoso Where alphanumeric_req_id like ? and RequestOwner=?;";

		} else if (Global.loggedInUser.equalsIgnoreCase("admin")) {
			query1 = "SELECT COUNT(request_info_id) AS total FROM requestinfoso Where alphanumeric_req_id like ?;";

		}
		try {
			PreparedStatement ps = connection.prepareStatement(query1);
			ps.setString(1, requestType);
			if (!Global.loggedInUser.equalsIgnoreCase("admin")) {
				ps.setString(2, Global.loggedInUser);
			}
			rs = ps.executeQuery();
			while (rs.next()) {
				num = rs.getInt("total");
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			DBUtil.close(rs);
			DBUtil.close(statement);
			DBUtil.close(connection);
		}
		return num;
	}

	/* Dhanshri Mane 6/2/2020 for getAll request for Request Type */
	public int getStatusForSpecificRequestType(String requestType, String requestStatus) {
		int num = 0;
		connection = ConnectionFactory.getConnection();
		String query1 = null;
		ResultSet rs = null;
		if (requestType != null) {
			if (Global.loggedInUser.equalsIgnoreCase("seuser")) {
				query1 = "SELECT COUNT(request_info_id) AS total FROM requestinfoso where alphanumeric_req_id like ? and request_status =? and request_creator_name=?;";
			} else if (Global.loggedInUser.equalsIgnoreCase("feuser")) {
				query1 = "SELECT COUNT(request_info_id) AS total FROM requestinfoso where alphanumeric_req_id like ? and request_status =? RequestOwner=?;";

			} else if (Global.loggedInUser.equalsIgnoreCase("admin")) {
				query1 = "SELECT COUNT(request_info_id) AS total FROM requestinfoso where alphanumeric_req_id like ? and request_status =?;";

			}
		} else {
			if (Global.loggedInUser.equalsIgnoreCase("seuser")) {
				query1 = "SELECT COUNT(request_info_id) AS total FROM requestinfoso where alphanumeric_req_id like ? and request_creator_name=?;";
			} else if (Global.loggedInUser.equalsIgnoreCase("feuser")) {
				query1 = "SELECT COUNT(request_info_id) AS total FROM requestinfoso where alphanumeric_req_id like ? RequestOwner=?;";

			} else if (Global.loggedInUser.equalsIgnoreCase("admin")) {
				query1 = "SELECT COUNT(request_info_id) AS total FROM requestinfoso where  request_status =?;";

			}
		}
		try {
			PreparedStatement ps = connection.prepareStatement(query1);
			if (requestType != null) {
				ps.setString(1, requestType);
				ps.setString(2, requestStatus);
				if (!Global.loggedInUser.equalsIgnoreCase("admin")) {
					ps.setString(3, Global.loggedInUser);
				}
			} else {
				ps.setString(1, requestStatus);
				if (!Global.loggedInUser.equalsIgnoreCase("admin")) {
					ps.setString(2, Global.loggedInUser);
				}
			}
			rs = ps.executeQuery();
			while (rs.next()) {
				num = rs.getInt("total");
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			DBUtil.close(rs);
			DBUtil.close(statement);
			DBUtil.close(connection);
		}
		return num;
	}

	/* Dhanshri Mane 6/2/2020 for getAll request for specific network type */
	public int getNetworkTypeRequest(String networkType, String requestType) {
		int num = 0;
		connection = ConnectionFactory.getConnection();
		String query1 = null;
		ResultSet rs = null;
		if (Global.loggedInUser.equalsIgnoreCase("seuser")) {
			query1 = "SELECT COUNT(request_info_id) AS total FROM requestinfoso Where networktype = ? and alphanumeric_req_id like ? and request_creator_name=?;";
		} else if (Global.loggedInUser.equalsIgnoreCase("feuser")) {
			query1 = "SELECT COUNT(request_info_id) AS total FROM requestinfoso Where networktype = ? and alphanumeric_req_id like ? and RequestOwner=?;";

		} else if (Global.loggedInUser.equalsIgnoreCase("admin")) {
			query1 = "SELECT COUNT(request_info_id) AS total FROM requestinfoso Where networktype = ? and alphanumeric_req_id like ? ;";

		}
		try {
			PreparedStatement ps = connection.prepareStatement(query1);
			ps.setString(1, networkType);
			ps.setString(2, requestType);
			if (!Global.loggedInUser.equalsIgnoreCase("admin")) {
				ps.setString(3, Global.loggedInUser);
			}
			rs = ps.executeQuery();
			while (rs.next()) {
				num = rs.getInt("total");
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			DBUtil.close(rs);
			DBUtil.close(statement);
			DBUtil.close(connection);
		}
		return num;
	}

	/* Method Overloading for UIRevamp */
	public Map<String, String> insertRequestInDB(RequestInfoPojo requestInfoSO) {
		Map<String, String> hmap = new HashMap<String, String>();
		String Os = null, model = null, region = null, service = null, version = null, hostname = null,
				alphaneumeric_req_id, customer = null, siteName = null, siteId = null, vendor = null, deviceType = null,
				vpn = null;
		String request_creator_name = null, certificationSelectionBit = null;
		String managementIP = null, scheduledTime = null, templateId = null;
		String zipcode = null, managed = null, downtimerequired = null, lastupgradedon = null, networktype = null;
		double request_version = 0, request_parent_version = 0;
		boolean isAutoProgress, startup = false;

		RequestInfoEntity requestEntity = new RequestInfoEntity();

		/* TimeZ Column added for Time Zone task */

		try {

			if (requestInfoSO.getRequestType().equalsIgnoreCase("IOSUPGRADE")
					&& requestInfoSO.getNetworkType().equalsIgnoreCase("PNF")) {
				alphaneumeric_req_id = "SLGF-" + UUID.randomUUID().toString().toUpperCase();

			} else if (requestInfoSO.getRequestType().equalsIgnoreCase("Test")
					&& requestInfoSO.getNetworkType().equalsIgnoreCase("PNF")) {
				alphaneumeric_req_id = "SLGT-" + UUID.randomUUID().toString().toUpperCase();

			} else if (requestInfoSO.getRequestType().equalsIgnoreCase("Audit")
					&& requestInfoSO.getNetworkType().equalsIgnoreCase("PNF")) {
				alphaneumeric_req_id = "SLGA-" + UUID.randomUUID().toString().toUpperCase();

			}

			else if (requestInfoSO.getRequestType().equalsIgnoreCase("RESTCONF")
					&& requestInfoSO.getNetworkType().equalsIgnoreCase("VNF")) {
				alphaneumeric_req_id = "SNRC-" + UUID.randomUUID().toString().toUpperCase();

			} else if (requestInfoSO.getRequestType().equalsIgnoreCase("NETCONF")
					&& requestInfoSO.getNetworkType().equalsIgnoreCase("VNF")) {
				alphaneumeric_req_id = "SNNC-" + UUID.randomUUID().toString().toUpperCase();

			} else if (requestInfoSO.getRequestType().equalsIgnoreCase("SR")
					&& requestInfoSO.getNetworkType().equalsIgnoreCase("PNF")) {
				alphaneumeric_req_id = "SLGC-" + UUID.randomUUID().toString().toUpperCase();

			} else if (requestInfoSO.getRequestType().equalsIgnoreCase("SLGB")
					&& requestInfoSO.getNetworkType().equalsIgnoreCase("PNF")) {
				alphaneumeric_req_id = "SLGB-" + UUID.randomUUID().toString().toUpperCase();
			} else if (requestInfoSO.getRequestType().equalsIgnoreCase("Test")
					&& requestInfoSO.getNetworkType().equalsIgnoreCase("VNF")) {
				alphaneumeric_req_id = "SLGT-" + UUID.randomUUID().toString().toUpperCase();

			}else {
				alphaneumeric_req_id = "SLGC-" + UUID.randomUUID().toString().toUpperCase();
			}
			// alphaneumeric_req_id = request.getProcessID();
			alphaneumeric_req_id = alphaneumeric_req_id.substring(0, 12);
			hmap.put("requestID", alphaneumeric_req_id);
			if (requestInfoSO.getOs() != null || requestInfoSO.getOs() != "") {
				Os = requestInfoSO.getOs();
			}

			if (requestInfoSO.getModel() != null || requestInfoSO.getModel() != "") {
				model = requestInfoSO.getModel();
			}

			if (requestInfoSO.getRegion() != null || requestInfoSO.getRegion() != "") {
				region = requestInfoSO.getRegion();
			}

			if (requestInfoSO.getService() != null || requestInfoSO.getService() != "") {
				service = requestInfoSO.getService();
			}

			if (requestInfoSO.getOsVersion() != null || requestInfoSO.getOsVersion() != "") {
				version = requestInfoSO.getOsVersion();
			}
			if (requestInfoSO.getHostname() != null || requestInfoSO.getHostname() != "") {
				hostname = requestInfoSO.getHostname();
			}

			/*
			 * if (requestInfoSO.getIsAutoProgress() != null) { isAutoProgress =
			 * requestInfoSO.getIsAutoProgress(); } else { isAutoProgress = false; }
			 */
			if (requestInfoSO.getCustomer() != null || requestInfoSO.getCustomer() != "") {
				customer = requestInfoSO.getCustomer();
			}
			if (requestInfoSO.getSiteName() != null || requestInfoSO.getSiteName() != "") {
				siteName = requestInfoSO.getSiteName();
			}
			if (requestInfoSO.getSiteid() != null || requestInfoSO.getSiteid() != "") {
				siteId = requestInfoSO.getSiteid();
			}
			if (requestInfoSO.getVendor() != null || requestInfoSO.getVendor() != "") {
				vendor = requestInfoSO.getVendor();
			}
			if (requestInfoSO.getManagementIp() != null || requestInfoSO.getManagementIp() != "") {
				managementIP = requestInfoSO.getManagementIp();
			}
			if (requestInfoSO.getDeviceType() != null || requestInfoSO.getDeviceType() != "") {
				deviceType = requestInfoSO.getDeviceType();
			}

			if (requestInfoSO.getRequestVersion() != 0) {
				request_version = requestInfoSO.getRequestVersion();
			}
			if (requestInfoSO.getRequestParentVersion() != 0) {
				request_parent_version = requestInfoSO.getRequestParentVersion();
			}
			if (requestInfoSO.getRequestCreatorName() != null) {
				request_creator_name = requestInfoSO.getRequestCreatorName();
			}
			if (requestInfoSO.getCertificationSelectionBit() != null
					|| requestInfoSO.getCertificationSelectionBit() != "") {
				certificationSelectionBit = requestInfoSO.getCertificationSelectionBit();
			}
			if (requestInfoSO.getStartUp() != null) {
				startup = requestInfoSO.getStartUp();
			}

			if (requestInfoSO.getSceheduledTime() != null && requestInfoSO.getSceheduledTime() != "") {
				scheduledTime = requestInfoSO.getSceheduledTime();
			}

			// template suggestion
			if (requestInfoSO.getTemplateID() != null && requestInfoSO.getTemplateID() != "") {
				templateId = requestInfoSO.getTemplateID();
			}
			if (requestInfoSO.getRequestType().equalsIgnoreCase("IOSUPGRADE")) {
				/*
				 * zipcode = requestInfoSO.getZipcode(); managed = requestInfoSO.getManaged();
				 * downtimerequired = requestInfoSO.getDownTimeRequired(); lastupgradedon =
				 * requestInfoSO.getLastUpgradedOn();
				 */}
			if (requestInfoSO.getNetworkType() != null || requestInfoSO.getNetworkType() != "") {
				networktype = requestInfoSO.getNetworkType();
			} else {
				networktype = "Legacy";
			}
			if (Os != "") {
				requestEntity.setOs(Os);
			}

			if (model != "") {
				requestEntity.setModel(model);
				requestEntity.setFamily(model);
			}
			if (region != "") {
				requestEntity.setRegion(region);
			}

			if (version != "") {
				requestEntity.setOsVersion(version);
			}

			if (hostname != "") {
				requestEntity.setHostName(hostname);
			}

			if (vendor != "") {
				requestEntity.setVendor(vendor);
			}

			if (customer != "") {
				requestEntity.setCustomer(customer);
			}
			if (siteName != "") {
				requestEntity.setSiteName(siteName);
			}
			if (siteId != "") {
				requestEntity.setSiteId(siteId);
			}
			if (managementIP != "") {
				requestEntity.setManagmentIP(managementIP);
			}
			if (deviceType != "") {
				requestEntity.setDeviceType(deviceType);
			}
			if (alphaneumeric_req_id != "") {
				requestEntity.setAlphanumericReqId(alphaneumeric_req_id);
			}

			requestEntity.setStatus(requestInfoSO.getStatus());

			if (request_version != 0) {
				requestEntity.setRequestVersion(request_version);
			} else {
				requestEntity.setRequestVersion(0.0);

			}
			if (request_parent_version != 0) {
				requestEntity.setRequestParentVersion(request_parent_version);
			} else {
				requestEntity.setRequestParentVersion(0.0);
			}
			requestEntity.setRequestType(requestInfoSO.getRequestType());
			if (request_creator_name != null) {
				requestEntity.setRequestCreatorName(request_creator_name);
				requestEntity.setRequestOwnerName(request_creator_name);
			}
			if (certificationSelectionBit != null) {
				requestEntity.setCertificationSelectionBit(certificationSelectionBit);
			}

			if (startup != false) {
				requestEntity.setStartUp(true);
			} else {
				requestEntity.setStartUp(false);
			}

			requestEntity.setRequestElapsedTime("00:00:00");

			if (scheduledTime != null && scheduledTime != "") {

				SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");

				try {
					java.util.Date parsedDate = sdf.parse(scheduledTime);

					java.sql.Timestamp timestampTimeForScheduled = new java.sql.Timestamp(parsedDate.getTime());

					// requestEntity.setSceheduledTime(timestampTimeForScheduled);
					requestEntity.setSceheduledTime(timestampTimeForScheduled);
					requestEntity.setRequestTypeFlag("S");
					/* ps.setString(37,TimeZ); *//* Added for TimeZ */

				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			} else {
				requestEntity.setRequestTypeFlag("M");

			}
			LocalDateTime nowDate = LocalDateTime.now();
			Timestamp timestamp = Timestamp.valueOf(nowDate);
			requestEntity.setDateofProcessing(timestamp);
			requestEntity.setNetworkType(networktype);
			if (templateId != null) {
				requestEntity.setTemplateUsed(templateId);
			} /*
				 * if (zipcode != null) { ps.setString(37, zipcode);
				 * 
				 * } else { ps.setNull(37, java.sql.Types.VARCHAR); } if (managed != null) {
				 * ps.setString(38, managed);
				 * 
				 * } else { ps.setNull(38, java.sql.Types.VARCHAR);
				 * 
				 * } if (downtimerequired != null) { ps.setString(39, downtimerequired);
				 * 
				 * } else { ps.setNull(39, java.sql.Types.VARCHAR);
				 * 
				 * } if (lastupgradedon != null) { ps.setString(40, lastupgradedon);
				 * 
				 * } else { ps.setNull(40, java.sql.Types.VARCHAR);
				 * 
				 * } if (networktype != null) { ps.setString(41, networktype);
				 * 
				 * } else { ps.setNull(41, java.sql.Types.VARCHAR);
				 * 
				 * } int i = ps.executeUpdate();
				 *
				 */
			RequestInfoEntity save = reository.save(requestEntity);
			if (save.getInfoId() > 0) {

				addRequestIDtoWebserviceInfo(alphaneumeric_req_id, Double.toString(request_version));
				addCertificationTestForRequest(alphaneumeric_req_id, Double.toString(request_version), "0");
				// add to OS_updgrade dilevary flag details table
				if (requestInfoSO.getRequestType().equalsIgnoreCase("IOSUPGRADE")) {
					addRequestID_to_Os_Upgrade_dilevary_flags(alphaneumeric_req_id, Double.toString(request_version));
				}
				// updateEIPAMTable(request.getDeviceInterfaceSO().getIp());
				hmap.put("result", "true");
				return hmap;

			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		hmap.put("result", "false");
		return hmap;
	}
	public List<TestDetail> findByTestName(String testNameUsed) {
		connection = ConnectionFactory.getConnection();
		String query = null, testName = null, version = null;

		List<TestDetail> requestInfoList = null;
		TestDetail request = null;
		ResultSet rs = null;
		PreparedStatement pst = null;
		boolean devicelocked = false;

		try {
			requestInfoList = new ArrayList<TestDetail>();
			query = "SELECT test_name,version FROM requestinfo.t_tststrategy_m_tstdetails where test_name LIKE ? order by test_name,version asc";
			pst = connection.prepareStatement(query);

			// pst.setString(1, requestId);
			pst.setString(1, testNameUsed + "%");

			rs = pst.executeQuery();
			while (rs.next()) {

				request = new TestDetail();
				request.setTestName(rs.getString("test_name"));
				request.setVersion(rs.getString("version"));
				requestInfoList.add(request);
			}

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			logger.error(e);
			e.printStackTrace();
		} finally {
			DBUtil.close(rs);
			DBUtil.close(statement);
			DBUtil.close(connection);
		}
		return requestInfoList;

	}

	public List<TestDetail> findByTestNameForSearch(String testNameUsed) {

		connection = ConnectionFactory.getConnection();
		String query = null, testName = null, version = null;

		List<TestDetail> requestInfoList = null;
		TestDetail request = null;
		ResultSet rs = null;
		PreparedStatement pst = null;
		boolean devicelocked = false;

		try {
			requestInfoList = new ArrayList<TestDetail>();
			query = "SELECT test_name,version,comment,device_type,vendor,device_model,os,created_on,created_by,is_enabled FROM requestinfo.t_tststrategy_m_tstdetails where test_name LIKE ? order by test_name,version asc";
			pst = connection.prepareStatement(query);

			// pst.setString(1, requestId);
			pst.setString(1, testNameUsed + "%");

			rs = pst.executeQuery();
			while (rs.next()) {

				request = new TestDetail();
				request.setTestName(rs.getString("test_name"));
				request.setVersion(rs.getString("version"));

				request.setComment(rs.getString("comment"));
				request.setDeviceType(rs.getString("device_type"));
				request.setVendor(rs.getString("vendor"));
				request.setDeviceModel(rs.getString("device_model"));
				request.setOs(rs.getString("os"));
				request.setCreatedOn(rs.getString("created_on"));
				request.setCreatedBy(rs.getString("created_by"));
				request.setEnabled(rs.getBoolean("is_enabled"));
				requestInfoList.add(request);
			}

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			logger.error(e);
			e.printStackTrace();
		} finally {
			DBUtil.close(rs);
			DBUtil.close(statement);
			DBUtil.close(connection);
		}
		return requestInfoList;

	}

	public List<FirmwareUpgradeDetail> findByVendorName(String vendor) {

		connection = ConnectionFactory.getConnection();
		String query = null, version = null;

		List<FirmwareUpgradeDetail> requestInfoList = null;
		FirmwareUpgradeDetail request = null;
		ResultSet rs = null;
		PreparedStatement pst = null;

		try {
			requestInfoList = new ArrayList<FirmwareUpgradeDetail>();
			query = "SELECT * FROM requestinfo.firmware_upgrade_single_device where vendor LIKE ?";
			pst = connection.prepareStatement(query);

			// pst.setString(1, requestId);
			pst.setString(1, vendor + "%");

			rs = pst.executeQuery();
			while (rs.next()) {

				request = new FirmwareUpgradeDetail();
				request.setFamily(rs.getString("family"));
				request.setOs_version(rs.getString("os_version"));
				request.setCreate_date(rs.getString("create_date"));
				request.setImage_filename(rs.getString("image_filename"));
				request.setVendor(rs.getString("vendor"));

				requestInfoList.add(request);
			}

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			logger.error(e);
			e.printStackTrace();
		} finally {
			DBUtil.close(rs);
			DBUtil.close(statement);
			DBUtil.close(connection);
		}
		return requestInfoList;

	}
	public List checkForDeviceLock(String requestId, String managementIp, String TestType) {
		connection = ConnectionFactory.getConnection();
		String query = null;

		ResultSet rs = null;
		PreparedStatement pst = null;
		List deviceLockList = new ArrayList<>();

		try {
			if (TestType.equalsIgnoreCase("DeviceTest")) {
				query = "Select * from DeviceLocked_ManagementIP where management_ip=?";
				pst = connection.prepareStatement(query);
				pst.setString(1,managementIp);
				rs = pst.executeQuery();
				while (rs.next()) {

					requestId = rs.getString("locked_by");
					deviceLockList.add(requestId);

				}
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			DBUtil.close(rs);
			DBUtil.close(statement);
			DBUtil.close(connection);
		}
		return deviceLockList;
	}

	public String deleteForDeviceLock(String locked_by) {

		connection = ConnectionFactory.getConnection();
		String query = null, result = null;

		ResultSet rs = null;
		PreparedStatement pst = null;
		boolean devicelocked = false;

		try {

			query = "delete from DeviceLocked_ManagementIP where locked_by = ?";
			pst = connection.prepareStatement(query);

			// pst.setString(1, requestId);
			pst.setString(1, locked_by);

			pst.executeUpdate();
			result = "Success";

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			DBUtil.close(rs);
			DBUtil.close(statement);
			DBUtil.close(connection);
		}
		return result;

	}

	public org.json.simple.JSONObject getStatusForBackUpRequestCustomerReport(RequestInfoPojo request) {

		org.json.simple.JSONObject obj = new org.json.simple.JSONObject();
		CertificationTestPojo certificationTestPojo1 = new CertificationTestPojo();
		CertificationTestResultEntity resultEnt = new CertificationTestResultEntity();
		org.json.simple.JSONArray prevalidationArray = new org.json.simple.JSONArray();
		org.json.simple.JSONObject reachabilityObj = new org.json.simple.JSONObject();
		org.json.simple.JSONObject iosVersion = new org.json.simple.JSONObject();
		org.json.simple.JSONObject deviceModel = new org.json.simple.JSONObject();
		org.json.simple.JSONObject vendorTest = new org.json.simple.JSONObject();
		org.json.simple.JSONObject backUpStatus = new org.json.simple.JSONObject();
		String model = null, vendor = null, requestId = null, deliveryStatus = null;
		certificationTestService = new CertificationTestResultService();
		resultEnt = certificationTestService.getRecordByRequestId(request.getAlphanumericReqId(),
				Double.toString(request.getRequestVersion()));

		certificationTestPojo1 = getCertificationTestFlagData(request.getAlphanumericReqId(),
				Double.toString(request.getRequestVersion()), "preValidate");

		requestId = request.getAlphanumericReqId();

		connection = ConnectionFactory.getConnection();
		// String query = "SELECT * FROM requestinfoso where alphanumeric_req_id =?";
		String query1 = "SELECT * FROM webserviceinfo where alphanumeric_req_id =?";

		ResultSet rs1 = null;

		PreparedStatement pst1 = null;

		try {
			RequestInfoEntity findAllByAlphanumericReqId = reository.findByAlphanumericReqId(requestId);
			if (findAllByAlphanumericReqId != null) {
				model = findAllByAlphanumericReqId.getModel();
				vendor = findAllByAlphanumericReqId.getVendor();
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		try {
			pst1 = connection.prepareStatement(query1);
			pst1.setString(1, requestId);

			rs1 = pst1.executeQuery();

			if (rs1 != null) {
				while (rs1.next()) {
					deliveryStatus = rs1.getString("deliever_config");
				}
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			DBUtil.close(statement);
			DBUtil.close(connection);
		}
		if (certificationTestPojo1.getDeviceReachabilityTest().equalsIgnoreCase("2")) {
			reachabilityObj.put("testname", "Device Reachability test");
			reachabilityObj.put("status", "Failed");
			reachabilityObj.put("outcome", "");
			reachabilityObj.put("notes", "N/A");

		}
		if (certificationTestPojo1.getDeviceReachabilityTest().equalsIgnoreCase("1")) {
			reachabilityObj.put("testname", "Device Reachability test");
			reachabilityObj.put("status", "Passed");
			reachabilityObj.put("outcome", "");
			reachabilityObj.put("notes", "N/A");
		}
		if (certificationTestPojo1.getIosVersionTest().equalsIgnoreCase("2")) {
			iosVersion.put("testname", "OS");
			iosVersion.put("status", "Failed");
			iosVersion.put("outcome", "");
			iosVersion.put("notes", "N/A");
			iosVersion.put("CollectedValue", resultEnt.getGuiOsVersion());
			iosVersion.put("EvaluationCriteria", resultEnt.getActualOsVersion());
		}
		if (certificationTestPojo1.getIosVersionTest().equalsIgnoreCase("1")) {
			iosVersion.put("testname", "OS");
			iosVersion.put("status", "Passed");
			iosVersion.put("outcome", "");
			iosVersion.put("notes", "N/A");
			iosVersion.put("CollectedValue", resultEnt.getGuiOsVersion());
			iosVersion.put("EvaluationCriteria", resultEnt.getActualOsVersion());
		}
		if (certificationTestPojo1.getDeviceModelTest().equalsIgnoreCase("2")) {
			deviceModel.put("testname", "Device Model");
			deviceModel.put("status", "Failed");
			deviceModel.put("outcome", "");
			deviceModel.put("notes", "N/A");
			deviceModel.put("CollectedValue", resultEnt.getGuiModel());
			deviceModel.put("EvaluationCriteria", resultEnt.getActualModel());
		}
		if (certificationTestPojo1.getDeviceModelTest().equalsIgnoreCase("1")) {
			deviceModel.put("testname", "Device Model");
			deviceModel.put("status", "Passed");
			deviceModel.put("outcome", "");
			deviceModel.put("notes", "N/A");
			deviceModel.put("CollectedValue", resultEnt.getGuiModel());
			deviceModel.put("EvaluationCriteria", resultEnt.getActualModel());
		}
		if (certificationTestPojo1.getVendorTest().equalsIgnoreCase("2")) {
			vendorTest.put("testname", "Vendor Test");
			vendorTest.put("status", "Failed");
			vendorTest.put("outcome", "");
			vendorTest.put("notes", "N/A");
			vendorTest.put("CollectedValue", resultEnt.getGuiVendor());
			vendorTest.put("EvaluationCriteria", resultEnt.getGuiVendor());
		}
		if (certificationTestPojo1.getVendorTest().equalsIgnoreCase("1")) {
			vendorTest.put("testname", "Vendor Test");
			vendorTest.put("status", "Passed");
			vendorTest.put("outcome", "");
			vendorTest.put("notes", "N/A");
			vendorTest.put("CollectedValue", resultEnt.getGuiVendor());
			vendorTest.put("EvaluationCriteria", resultEnt.getGuiVendor());
		}

		if (deliveryStatus.equals("1")) {
			backUpStatus.put("backupstatus", "Success");
		} else {
			backUpStatus.put("backupstatus", "Failed");
		}
		prevalidationArray.add(vendorTest);
		prevalidationArray.add(deviceModel);

		prevalidationArray.add(reachabilityObj);
		prevalidationArray.add(backUpStatus);

		obj.put("Prevalidation", prevalidationArray);
		return obj;

	}

	public org.json.simple.JSONObject getStatusForCustomerReport(RequestInfoPojo request) {

		org.json.simple.JSONObject obj = new org.json.simple.JSONObject();
		CertificationTestPojo certificationTestPojo1 = new CertificationTestPojo();
		CertificationTestPojo certificationTestPojo2 = new CertificationTestPojo();
		CertificationTestResultEntity resultEnt = new CertificationTestResultEntity();
		CertificationTestPojo certificationTestPojo3 = new CertificationTestPojo();
		org.json.simple.JSONArray prevalidationArray = new org.json.simple.JSONArray();
		org.json.simple.JSONObject reachabilityObj = new org.json.simple.JSONObject();
		org.json.simple.JSONObject iosVersion = new org.json.simple.JSONObject();
		org.json.simple.JSONObject deviceModel = new org.json.simple.JSONObject();
		org.json.simple.JSONObject vendorTest = new org.json.simple.JSONObject();
		NetworkTestValidation networkTestValidation = new NetworkTestValidation();
		org.json.simple.JSONArray othersArray = new org.json.simple.JSONArray();
		org.json.simple.JSONArray networkAuditArray = new org.json.simple.JSONArray();
		certificationTestPojo1 = getCertificationTestFlagData(request.getAlphanumericReqId(),
				Double.toString(request.getRequestVersion()), "preValidate");

		certificationTestService = new CertificationTestResultService();
		resultEnt = certificationTestService.getRecordByRequestId(request.getAlphanumericReqId(),
				Double.toString(request.getRequestVersion()));

		if (certificationTestPojo1.getDeviceReachabilityTest().equalsIgnoreCase("2")) {
			reachabilityObj.put("testname", "Device Reachability test");
			reachabilityObj.put("status", "Failed");
			reachabilityObj.put("outcome", "");
			reachabilityObj.put("notes", "N/A");

		}
		if (certificationTestPojo1.getDeviceReachabilityTest().equalsIgnoreCase("1")) {
			reachabilityObj.put("testname", "Device Reachability test");
			reachabilityObj.put("status", "Passed");
			reachabilityObj.put("outcome", "");
			reachabilityObj.put("notes", "N/A");
		}
		if (certificationTestPojo1.getIosVersionTest().equalsIgnoreCase("2")) {
			iosVersion.put("testname", "OS");
			iosVersion.put("status", "Failed");
			iosVersion.put("outcome", "");
			iosVersion.put("notes", "N/A");
			iosVersion.put("CollectedValue", resultEnt.getGuiOsVersion());
			iosVersion.put("EvaluationCriteria", resultEnt.getActualOsVersion());
		}
		if (certificationTestPojo1.getIosVersionTest().equalsIgnoreCase("1")) {
			iosVersion.put("testname", "OS");
			iosVersion.put("status", "Passed");
			iosVersion.put("outcome", "");
			iosVersion.put("notes", "N/A");
			iosVersion.put("CollectedValue", resultEnt.getGuiOsVersion());
			iosVersion.put("EvaluationCriteria", resultEnt.getActualOsVersion());
		}
		if (certificationTestPojo1.getDeviceModelTest().equalsIgnoreCase("2")) {
			deviceModel.put("testname", "Device Model");
			deviceModel.put("status", "Failed");
			deviceModel.put("outcome", "");
			deviceModel.put("notes", "N/A");
			deviceModel.put("CollectedValue", resultEnt.getGuiModel());
			deviceModel.put("EvaluationCriteria", resultEnt.getActualModel());
		}
		if (certificationTestPojo1.getDeviceModelTest().equalsIgnoreCase("1")) {
			deviceModel.put("testname", "Device Model");
			deviceModel.put("status", "Passed");
			deviceModel.put("outcome", "");
			deviceModel.put("notes", "N/A");
			deviceModel.put("CollectedValue", resultEnt.getGuiModel());
			deviceModel.put("EvaluationCriteria", resultEnt.getActualModel());
		}
		if (certificationTestPojo1.getVendorTest().equalsIgnoreCase("2")) {
			vendorTest.put("testname", "Vendor Test");
			vendorTest.put("status", "Failed");
			vendorTest.put("outcome", "");
			vendorTest.put("notes", "N/A");
			vendorTest.put("CollectedValue", resultEnt.getGuiVendor());
			vendorTest.put("EvaluationCriteria", resultEnt.getGuiVendor());
		}
		if (certificationTestPojo1.getVendorTest().equalsIgnoreCase("1")) {
			vendorTest.put("testname", "Vendor Test");
			vendorTest.put("status", "Passed");
			vendorTest.put("outcome", "");
			vendorTest.put("notes", "N/A");
			vendorTest.put("CollectedValue", resultEnt.getGuiVendor());
			vendorTest.put("EvaluationCriteria", resultEnt.getGuiVendor());
		}
		prevalidationArray.add(vendorTest);
		prevalidationArray.add(deviceModel);
		prevalidationArray.add(iosVersion);
		prevalidationArray.add(reachabilityObj);

		org.json.simple.JSONArray networkArray = new org.json.simple.JSONArray();

		org.json.simple.JSONObject networkIfObj = new org.json.simple.JSONObject();
		org.json.simple.JSONObject networkPlatformIOS = new org.json.simple.JSONObject();
		org.json.simple.JSONObject waninterface = new org.json.simple.JSONObject();
		org.json.simple.JSONObject bgpneighbour = new org.json.simple.JSONObject();

		certificationTestPojo2 = getCertificationTestFlagData(request.getAlphanumericReqId(),
				Double.toString(request.getRequestVersion()), "networkTest");
		if (certificationTestPojo2.getShowIpIntBriefCmd().equalsIgnoreCase("1")) {
			networkIfObj.put("testname", "Network Interface Status");
			networkIfObj.put("status", "Passed");
			networkIfObj.put("outcome", "");
			networkIfObj.put("notes", "N/A");
		}
		if (certificationTestPojo2.getShowInterfaceCmd().equalsIgnoreCase("1")) {
			waninterface.put("testname", "Wan Interface");
			waninterface.put("status", "Passed");
			waninterface.put("outcome", "");
			waninterface.put("notes", "N/A");
		}
		if (certificationTestPojo2.getShowVersionCmd().equalsIgnoreCase("1")) {
			networkPlatformIOS.put("testname", "Network Platform IOS");
			networkPlatformIOS.put("status", "Passed");
			networkPlatformIOS.put("outcome", "");
			networkPlatformIOS.put("notes", "N/A");
		}
		if (certificationTestPojo2.getShowIpBgpSummaryCmd().equalsIgnoreCase("1")) {
			bgpneighbour.put("testname", "BGP Neighbour");
			bgpneighbour.put("status", "Passed");
			bgpneighbour.put("outcome", "");
			bgpneighbour.put("notes", "N/A");
		}
		networkArray.add(bgpneighbour);
		networkArray.add(waninterface);
		networkArray.add(networkPlatformIOS);
		networkArray.add(networkIfObj);

		org.json.simple.JSONArray healthArray = new org.json.simple.JSONArray();

		org.json.simple.JSONObject throughputObj = new org.json.simple.JSONObject();
		org.json.simple.JSONObject latencyObj = new org.json.simple.JSONObject();
		org.json.simple.JSONObject FrameLossObj = new org.json.simple.JSONObject();

		certificationTestPojo3 = getCertificationTestFlagData(request.getAlphanumericReqId(),
				Double.toString(request.getRequestVersion()), "HealthTest");
		if (null != certificationTestPojo3.getThroughput() && certificationTestPojo3.getThroughput() != "") {
			throughputObj.put("testname", "Throughput");
			throughputObj.put("status", "Passed");
			throughputObj.put("outcome", certificationTestPojo3.getThroughput());
			throughputObj.put("notes", "N/A");
		} else {
			throughputObj.put("testname", "Throughput");
			throughputObj.put("status", "Passed");
			throughputObj.put("outcome", "-1");
			throughputObj.put("notes", "N/A");
		}

		if (null != certificationTestPojo3.getLatency() && certificationTestPojo3.getLatency() != "") {
			latencyObj.put("testname", "Latency");
			latencyObj.put("status", "Passed");
			latencyObj.put("outcome", certificationTestPojo3.getLatency());
			latencyObj.put("notes", "N/A");
		} else {

			latencyObj.put("testname", "Latency");
			latencyObj.put("status", "Passed");
			latencyObj.put("outcome", "-1");
			latencyObj.put("notes", "N/A");
		}

		if (null != certificationTestPojo3.getFrameLoss() && certificationTestPojo3.getFrameLoss() != "") {
			FrameLossObj.put("testname", "Frameloss");
			FrameLossObj.put("status", "Passed");
			FrameLossObj.put("outcome", certificationTestPojo3.getFrameLoss());
			FrameLossObj.put("notes", "N/A");
		} else {
			FrameLossObj.put("testname", "Frameloss");
			FrameLossObj.put("status", "Passed");
			FrameLossObj.put("outcome", "-1");
			FrameLossObj.put("notes", "N/A");
		}
		healthArray.add(FrameLossObj);
		healthArray.add(latencyObj);
		healthArray.add(throughputObj);

		org.json.simple.JSONArray dynamicTestResultArray = new org.json.simple.JSONArray();
		dynamicTestResultArray = getDynamicTestResultCustomerReport(request.getAlphanumericReqId(),
				Double.toString(request.getRequestVersion()), "Network Test");

		if (dynamicTestResultArray.size() > 0) {
			for (int i = 0; i < dynamicTestResultArray.size(); i++) {
				networkArray.add(dynamicTestResultArray.get(i));
			}
		}

		org.json.simple.JSONArray dynamicTestResultArray1 = new org.json.simple.JSONArray();
		dynamicTestResultArray1 = getDynamicTestResultCustomerReport(request.getAlphanumericReqId(),
				Double.toString(request.getRequestVersion()), "Health Check");

		if (dynamicTestResultArray1.size() > 0) {
			for (int i = 0; i < dynamicTestResultArray1.size(); i++) {
				healthArray.add(dynamicTestResultArray1.get(i));
			}
		}

		org.json.simple.JSONArray dynamicTestResultArray2 = new org.json.simple.JSONArray();
		dynamicTestResultArray2 = getDynamicTestResultCustomerReport(request.getAlphanumericReqId(),
				Double.toString(request.getRequestVersion()), "Device Prevalidation");

		if (dynamicTestResultArray2.size() > 0) {
			for (int i = 0; i < dynamicTestResultArray2.size(); i++) {
				prevalidationArray.add(dynamicTestResultArray2.get(i));
			}
		}

		org.json.simple.JSONArray dynamicTestResultArray3 = new org.json.simple.JSONArray();
		dynamicTestResultArray3 = getDynamicTestResultCustomerReport(request.getAlphanumericReqId(),
				Double.toString(request.getRequestVersion()), "Others");

		if (dynamicTestResultArray3.size() > 0) {
			for (int i = 0; i < dynamicTestResultArray3.size(); i++) {
				othersArray.add(dynamicTestResultArray3.get(i));
			}
		}
		org.json.simple.JSONArray dynamicTestResultArray4 = new org.json.simple.JSONArray();
		dynamicTestResultArray4 = getDynamicTestResultCustomerReport(request.getAlphanumericReqId(),
				Double.toString(request.getRequestVersion()), "Network Audit");

		if (dynamicTestResultArray4.size() > 0) {
			for (int i = 0; i < dynamicTestResultArray4.size(); i++) {
				networkAuditArray.add(dynamicTestResultArray4.get(i));
			}
		}
		obj.put("Prevalidation", prevalidationArray);
		obj.put("Network", networkArray);
		obj.put("Health_Check", healthArray);
		obj.put("Others", othersArray);
		obj.put("NetworkAudit", networkAuditArray);
		return obj;

	}

	public CertificationTestResultEntity findCertificationTestResultEntityByRequestID(String requestID,
			String version) {

		CertificationTestResultEntity ent = new CertificationTestResultEntity();
		connection = ConnectionFactory.getConnection();
		String query1 = "SELECT * FROM certificationtestvalidation WHERE alphanumeric_req_id=? AND version=?";
		ResultSet rs = null;
		String result = "false";

		try {

			PreparedStatement ps = connection.prepareStatement(query1);

			ps.setString(1, requestID);
			ps.setString(2, version);

			rs = ps.executeQuery();

			while (rs.next()) {

				ent.setActualModel(rs.getString("actual_model"));
				ent.setActualOsVersion(rs.getString("actual_os_version"));
				ent.setActualVendor(rs.getString("actual_vendor"));
				ent.setGuiModel(rs.getString("gui_model"));
				ent.setGuiOsVersion(rs.getString("gui_os_version"));
				ent.setGuiVendor(rs.getString("gui_vendor"));

			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block

			e.printStackTrace();
		} finally {
			DBUtil.close(rs);

			DBUtil.close(connection);
		}
		return ent;
	}

	public String findByRequestId(String requestId) {
		connection = ConnectionFactory.getConnection();
		String query1 = "SELECT * FROM t_tststrategy_m_config_results WHERE RequestId=?";
		ResultSet rs = null;
		String result = null;

		try {

			PreparedStatement ps = connection.prepareStatement(query1);

			ps.setString(1, requestId);

			rs = ps.executeQuery();

			while (rs.next()) {

				result = rs.getString("testName");

			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block

			e.printStackTrace();
		} finally {
			DBUtil.close(rs);

			DBUtil.close(connection);
		}
		return result;
	}

	/* Dhanshri Mane */
	public int getTestDetails(String requestId, String testName) {
		connection = ConnectionFactory.getConnection();
		String query = "";
		ResultSet rs = null;
		PreparedStatement preparedStmt;
		int status = 0;
		query = "select * from  t_tststrategy_m_config_results where RequestId = ? and testName= ?";
		try {
			preparedStmt = connection.prepareStatement(query);
			preparedStmt.setString(1, requestId);
			preparedStmt.setString(2, testName);
			rs = preparedStmt.executeQuery();

			int successCount = 0;
			int failuarCount = 0;
			if (rs != null) {
				while (rs.next()) {
					if (rs.getString("TestResult").equalsIgnoreCase("Passed")) {
						status = 1;
						successCount++;
					} else if (rs.getString("TestResult").equalsIgnoreCase("Failed")) {
						status = 2;
						failuarCount++;
					} else {
						status = 0;
					}
				}
			}
			if (failuarCount > 0) {
				return 2;
			}

			/* Check for partial success. */
			/*
			 * if ( successCount != 0 && failuarCount !=0 ) { if (successCount !=
			 * failuarCount) { status = 3; } }
			 */

		} catch (SQLException e) {
			e.printStackTrace();
		}

		return status;
	}

	public List<FirmwareUpgradeDetail> findByFamily(String isFamily, String isVendor) {

		connection = ConnectionFactory.getConnection();
		String query = null, version = null;

		List<FirmwareUpgradeDetail> requestInfoList = null;
		FirmwareUpgradeDetail request = null;
		ResultSet rs = null;
		PreparedStatement pst = null;

		try {
			requestInfoList = new ArrayList<FirmwareUpgradeDetail>();
			query = "SELECT * FROM requestinfo.firmware_upgrade_single_device where family LIKE ? AND vendor LIKE ?";
			pst = connection.prepareStatement(query);

			// pst.setString(1, requestId);
			pst.setString(1, isFamily + "%");
			pst.setString(2, isVendor + "%");

			rs = pst.executeQuery();
			while (rs.next()) {

				request = new FirmwareUpgradeDetail();
				request.setFamily(rs.getString("family"));
				request.setOs_version(rs.getString("os_version"));
				request.setCreate_date(rs.getString("create_date"));
				request.setImage_filename(rs.getString("image_filename"));
				request.setVendor(rs.getString("vendor"));

				requestInfoList.add(request);
			}

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			logger.error(e);
			e.printStackTrace();
		} finally {
			DBUtil.close(rs);
			DBUtil.close(statement);
			DBUtil.close(connection);
		}
		return requestInfoList;

	}

	public final boolean updateBatchStatus(String batchId) {
		boolean result = false;
		connection = ConnectionFactory.getConnection();
		String sql = "update c3p_t_request_batch_info set r_batch_status='Completed' where r_batch_id=?";
		try {
			PreparedStatement ps = connection.prepareStatement(sql);

			ps.setString(1, batchId);

			int i = ps.executeUpdate();
			if (i == 1) {
				result = true;

			} else {
				result = false;
			}
		} catch (SQLException e) {

			result = false;
		}
		return result;
	}

	public Map<String, String> insertBatchConfigRequestInDB(RequestInfoPojo requestInfoSO) {
		Map<String, String> hmap = new HashMap<String, String>();
		String Os = null, model = null, region = null, service = null, version = null, hostname = null,
				alphaneumeric_req_id = null, customer = null, siteName = null, siteId = null, vendor = null,
				deviceType = null, vpn = null;
		String request_creator_name = null, batchId = null, requestStatus = null,certificationSelectionBit=null;
		String managementIP = null, scheduledTime = null, templateId = null;
		String zipcode = null, managed = null, downtimerequired = null, lastupgradedon = null, networktype = null;
		double request_version = 0, request_parent_version = 0;
		boolean isAutoProgress, startup = false;

		RequestInfoEntity requestEntity = new RequestInfoEntity();

		BatchIdEntity batchIdEntity = new BatchIdEntity();

		try {

			if (requestInfoSO.getRequestType().equalsIgnoreCase("Config MACD")
					&& requestInfoSO.getNetworkType().equalsIgnoreCase("PNF")) {
				alphaneumeric_req_id = "SLGM-" + UUID.randomUUID().toString().toUpperCase();

			}
			else if(requestInfoSO.getRequestType().equalsIgnoreCase("Test")
					&& requestInfoSO.getNetworkType().equalsIgnoreCase("PNF"))
			{
				alphaneumeric_req_id = "SLGT-" + UUID.randomUUID().toString().toUpperCase();
			}
			 else if (requestInfoSO.getRequestType().equalsIgnoreCase("Audit")
						&& requestInfoSO.getNetworkType().equalsIgnoreCase("PNF")) {
					alphaneumeric_req_id = "SLGA-" + UUID.randomUUID().toString().toUpperCase();

				}

			

			alphaneumeric_req_id = alphaneumeric_req_id.substring(0, 12);
			hmap.put("requestID", alphaneumeric_req_id);
			if (requestInfoSO.getOs() != null || requestInfoSO.getOs() != "") {
				Os = requestInfoSO.getOs();
			}

			if (requestInfoSO.getModel() != null || requestInfoSO.getModel() != "") {
				model = requestInfoSO.getModel();
			}
			
			if (requestInfoSO.getCertificationSelectionBit() != null
					|| requestInfoSO.getCertificationSelectionBit() != "") {
				certificationSelectionBit = requestInfoSO.getCertificationSelectionBit();
			}

			if (requestInfoSO.getRegion() != null || requestInfoSO.getRegion() != "") {
				region = requestInfoSO.getRegion();
			}
			if (requestInfoSO.getStatus() != null || requestInfoSO.getStatus() != "") {
				requestStatus = requestInfoSO.getStatus();
			}
			if (requestInfoSO.getBatchId() != null || requestInfoSO.getBatchId() != "") {
				batchId = requestInfoSO.getBatchId();
			}

			if (requestInfoSO.getService() != null || requestInfoSO.getService() != "") {
				service = requestInfoSO.getService();
			}

			if (requestInfoSO.getOsVersion() != null || requestInfoSO.getOsVersion() != "") {
				version = requestInfoSO.getOsVersion();
			}
			if (requestInfoSO.getHostname() != null || requestInfoSO.getHostname() != "") {
				hostname = requestInfoSO.getHostname();
			}

			if (requestInfoSO.getCustomer() != null || requestInfoSO.getCustomer() != "") {
				customer = requestInfoSO.getCustomer();
			}
			if (requestInfoSO.getSiteName() != null || requestInfoSO.getSiteName() != "") {
				siteName = requestInfoSO.getSiteName();
			}
			if (requestInfoSO.getSiteid() != null || requestInfoSO.getSiteid() != "") {
				siteId = requestInfoSO.getSiteid();
			}
			if (requestInfoSO.getVendor() != null || requestInfoSO.getVendor() != "") {
				vendor = requestInfoSO.getVendor();
			}
			if (requestInfoSO.getManagementIp() != null || requestInfoSO.getManagementIp() != "") {
				managementIP = requestInfoSO.getManagementIp();
			}
			if (requestInfoSO.getDeviceType() != null || requestInfoSO.getDeviceType() != "") {
				deviceType = requestInfoSO.getDeviceType();
			}

			if (requestInfoSO.getRequestVersion() != 0) {
				request_version = requestInfoSO.getRequestVersion();
			}
			if (requestInfoSO.getRequestParentVersion() != 0) {
				request_parent_version = requestInfoSO.getRequestParentVersion();
			}
			if (requestInfoSO.getRequestCreatorName() != null) {
				request_creator_name = requestInfoSO.getRequestCreatorName();
			}

			if (requestInfoSO.getStartUp() != null) {
				startup = requestInfoSO.getStartUp();
			}

			if (requestInfoSO.getSceheduledTime() != null && requestInfoSO.getSceheduledTime() != "") {
				scheduledTime = requestInfoSO.getSceheduledTime();
			}

			if (requestInfoSO.getTemplateID() != null && requestInfoSO.getTemplateID() != "") {
				templateId = requestInfoSO.getTemplateID();
			}

			if (requestInfoSO.getNetworkType() != null || requestInfoSO.getNetworkType() != "") {
				networktype = requestInfoSO.getNetworkType();
			} else {
				networktype = "Legacy";
			}
			if (Os != "") {
				requestEntity.setOs(Os);
			}
			if (batchId != "") {
				requestEntity.setBatchId(batchId);
			}
			if (requestStatus != "") {
				requestEntity.setStatus(requestStatus);
			}

			if (model != "") {
				requestEntity.setModel(model);
				requestEntity.setFamily(model);
			}
			if (region != "") {
				requestEntity.setRegion(region);
			}

			if (version != "") {
				requestEntity.setOsVersion(version);
			}

			if (hostname != "") {
				requestEntity.setHostName(hostname);
			}

			if (vendor != "") {
				requestEntity.setVendor(vendor);
			}

			if (customer != "") {
				requestEntity.setCustomer(customer);
			}
			if (siteName != "") {
				requestEntity.setSiteName(siteName);
			}
			if (siteId != "") {
				requestEntity.setSiteId(siteId);
			}
			if (managementIP != "") {
				requestEntity.setManagmentIP(managementIP);
			}
			if (deviceType != "") {
				requestEntity.setDeviceType(deviceType);
			}
			if (alphaneumeric_req_id != "") {
				requestEntity.setAlphanumericReqId(alphaneumeric_req_id);
			}

			requestEntity.setStatus(requestInfoSO.getStatus());

			if (request_version != 0) {
				requestEntity.setRequestVersion(request_version);
			} else {
				requestEntity.setRequestVersion(0.0);

			}
			if (request_parent_version != 0) {
				requestEntity.setRequestParentVersion(request_parent_version);
			} else {
				requestEntity.setRequestParentVersion(0.0);
			}
			requestEntity.setRequestType(requestInfoSO.getRequestType());
			if (request_creator_name != null) {
				requestEntity.setRequestCreatorName(request_creator_name);
				requestEntity.setRequestOwnerName(request_creator_name);
			}

			if (startup != false) {
				requestEntity.setStartUp(true);
			} else {
				requestEntity.setStartUp(false);
			}
			
			if (requestInfoSO.getRequestType().equalsIgnoreCase("Config MACD")
					&& requestInfoSO.getNetworkType().equalsIgnoreCase("PNF")) {
			requestEntity.setCertificationSelectionBit("1010111");
			}
			else
			{
				requestEntity.setCertificationSelectionBit(certificationSelectionBit);
			}

			requestEntity.setRequestElapsedTime("00:00:00");

			if (scheduledTime != null && scheduledTime != "") {

				SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");

				try {
					java.util.Date parsedDate = sdf.parse(scheduledTime);

					java.sql.Timestamp timestampTimeForScheduled = new java.sql.Timestamp(parsedDate.getTime());

					requestEntity.setSceheduledTime(timestampTimeForScheduled);
					requestEntity.setRequestTypeFlag("S");

				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			} else {
				requestEntity.setRequestTypeFlag("M");

			}
			LocalDateTime nowDate = LocalDateTime.now();
			Timestamp timestamp = Timestamp.valueOf(nowDate);
			requestEntity.setDateofProcessing(timestamp);
			requestEntity.setNetworkType(networktype);
			if (templateId != null) {
				requestEntity.setTemplateUsed(templateId);
			}
			batchIdEntity.setBatchStatus("In Progress");

			batchIdEntity.setBatchId(batchId);

			batchIdEntity.setRequestInfoEntity(requestEntity);

			BatchIdEntity save = batchInfoRepo.save(batchIdEntity);

			if (save.getBatchId() != null) {

				addRequestIDtoWebserviceInfo(alphaneumeric_req_id, Double.toString(request_version));
				addCertificationTestForRequest(alphaneumeric_req_id, Double.toString(request_version), "0");

				hmap.put("result", "true");
				return hmap;

			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		hmap.put("result", "false");
		return hmap;
	}
	public final boolean updateBatchRequestStatus(String requestId) {
        boolean result = false;
        connection = ConnectionFactory.getConnection();
        String sql = "update c3p_t_request_info set r_status= 'Failure' where r_alphanumeric_req_id=?";
        try {
               PreparedStatement ps = connection.prepareStatement(sql);

               ps.setString(1, requestId);
               
               int i = ps.executeUpdate();
               if (i == 1) {
                     result = true;

               } else {
                     result = false;
               }
        } catch (SQLException e) {

               result = false;
        }
        return result;
 }
	
	public final boolean updateRequestExecutionStatus(String requestId) {
        boolean result = false;
        connection = ConnectionFactory.getConnection();
        String sql = "update c3p_t_request_info set r_execution_status= true where r_alphanumeric_req_id=?";
        try {
               PreparedStatement ps = connection.prepareStatement(sql);

               ps.setString(1, requestId);
               
               int i = ps.executeUpdate();
               if (i == 1) {
                     result = true;

               } else {
                     result = false;
               }
        } catch (SQLException e) {

               result = false;
        }
        return result;
 }

}
