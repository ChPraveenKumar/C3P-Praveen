package com.techm.orion.dao;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
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
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.util.StringUtil;
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
import com.techm.orion.entitybeans.TestBundling;
import com.techm.orion.entitybeans.TestDetail;
import com.techm.orion.entitybeans.TestRules;
import com.techm.orion.pojo.AlertInformationPojo;
import com.techm.orion.pojo.CertificationTestPojo;
import com.techm.orion.pojo.ConfigurationDataValuePojo;
import com.techm.orion.pojo.CreateConfigRequest;
import com.techm.orion.pojo.CreateConfigRequestDCM;
import com.techm.orion.pojo.DeviceInterfaceSO;
import com.techm.orion.pojo.EIPAMPojo;
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
import com.techm.orion.pojo.TestBundlePojo;
import com.techm.orion.pojo.TestStrategyPojo;
import com.techm.orion.pojo.UserPojo;
import com.techm.orion.pojo.UserValidationResultDetailPojo;
import com.techm.orion.repositories.BatchInfoRepo;
import com.techm.orion.repositories.RequestInfoDetailsRepositories;
import com.techm.orion.repositories.ServiceOrderRepo;
import com.techm.orion.service.CertificationTestResultService;
import com.techm.orion.utility.TSALabels;
import com.techm.orion.utility.UtilityMethods;
import com.techm.orion.webService.GetAllDetailsService;

@Controller
public class RequestInfoDao {
	private static final Logger logger = LogManager.getLogger(RequestInfoDao.class);
	@Autowired
	private RequestInfoDetailsRepositories reository;
	@Autowired
	private BatchInfoRepo batchInfoRepo;
	@Inject
	private CertificationTestResultService certificationTestService;
	@Autowired
	private ServiceOrderRepo serviceOrderRepo;
	/* SQL information */
	private static final String INSERT_REQUEST_INFOSO = "INSERT INTO requestinfoso(Os,banner,device_name,model,region,service,os_version,hostname,enable_password,vrf_name,isAutoProgress,vendor,customer,siteid,managementIp,device_type,vpn,alphanumeric_req_id,request_status,request_version,request_parent_version,request_creator_name,snmpHostAddress,snmpString,loopBackType,loopbackIPaddress,loopbackSubnetMask,lanInterface,lanIp,lanMaskAddress,lanDescription,certificationSelectionBit,ScheduledTime,RequestType_Flag,TemplateIdUsed,RequestOwner,zipcode,managed,downtimeRequired,lastUpgradedOn,networktype)"
			+ "VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
	private static final String INSERT_INTERNET_LCVRF_SO = "INSERT INTO internetlcvrfso(networkIp,asNumber,neighbor1,neighbor2,neighbor1_remoteAS,neighbor2_remoteAS,networkIp_subnetMask,routingProtocol) "
			+ "VALUES (?,?,?,?,?,?,?,?)";
	private static final String INSERT_MIS_AR_PE_SO = "INSERT INTO misarpeso(routerVrfVpnDIp, routerVrfVpnDGateway, fastEthernetIp) "
			+ "VALUES(?, ?, ?)";
	private static final String INSERT_DEVICE_INTERFACE_SO = "INSERT INTO deviceinterfaceso(name,description,ip,mask,speed,encapsulation,Bandwidth)"
			+ "VALUES(?, ?, ?, ?, ?, ?, ?)";
	private static final String INSERT_BANNER_DATA_TABLE = "INSERT INTO bannerdatatable(bannerdata) VALUES(?)";
	private static final String INSERT_T_TSTSTRATEGY_M_CONFIG_TRANSACTION = "INSERT INTO t_tststrategy_m_config_transaction(RequestId,TestsSelected,RequestType,request_version)"
			+ "VALUES(?,?,?,?)";
	private static final String INSERT_OS_UPGRADE_DELIVERY_FLAGS = "INSERT INTO os_upgrade_dilevary_flags(request_id,request_version,login_flag,flash_size_flag,back_up_flag,os_download_flag,boot_system_flash_flag,reload_flag,post_login_flag)"
			+ "VALUES(?,?,?,?,?,?,?,?,?)";
	private static final String INSERT_WEB_SERVICE_INFO = "INSERT INTO webserviceinfo(start_test,generate_config,deliever_config,health_checkup,network_test,application_test,customer_report,filename,latencyResultRes,alphanumeric_req_id,version,pre_health_checkup,others_test)"
			+ "VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?)";
	private static final String GET_MIS_AR_PE_SO = "select * from misarpeso where request_info_id= ?";
	private static final String GET_INTERNET_LCVRF_SO = "select * from internetlcvrfso where request_info_id= ?";
	private static final String GET_DEVICE_INTERFACE_SO = "select * from deviceinterfaceso where request_info_id= ?";
	private static final String GET_REQUEST_INFO_SO_BY_ALPREQID_VERSION = "select * from requestinfoso where alphanumeric_req_id = ? and request_version= ?";
	private static final String UPDATE_REQUEST_INFO_SO_BY_ALPREQID_VERSION = "update requestinfoso set request_status = ?, end_date_of_processing = ?, request_elapsed_time=? where alphanumeric_req_id = ? and request_version= ?";

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

		try (Connection connection = ConnectionFactory.getConnection();
				PreparedStatement prepStmt = connection.prepareStatement(INSERT_REQUEST_INFOSO);) {
			if (request.getRequest_type().equalsIgnoreCase("IOSUPGRADE")
					&& request.getNetworkType().equalsIgnoreCase("Legacy")) {
				alphaneumeric_req_id = "SLGF-" + UUID.randomUUID().toString().toUpperCase();
			} else if (request.getRequest_type().equalsIgnoreCase("TS")
					&& request.getNetworkType().equalsIgnoreCase("Legacy")) {
				alphaneumeric_req_id = "SLGT-" + UUID.randomUUID().toString().toUpperCase();
			} else if (request.getRequest_type().equalsIgnoreCase("RESTCONF")
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
			// Call insertInternetCvrfso method to insert the data in
			// internetlcvrfso table
			insertInternetCvrfso(request);
			// Call insertMisArPeSO method to insert the data in misarpeso table
			insertMisArPeSo(request);
			// Call insertMisArPeSO method to insert the data in
			// deviceinterfaceso table
			insertDeviceInterfaceSo(request);
			// Logic to add banner text in new
			// table-----------------------------------------------------------------------------------------------------
			insertBannerDataTable(banner);
			// End of banner
			// logic------------------------------------------------------------------------------------------------------------------------

			if (Os != "") {
				prepStmt.setString(1, Os);
			} else {
				prepStmt.setNull(1, Types.VARCHAR);
			}

			if (banner != "") {
				prepStmt.setString(2, banner);
			} else {
				prepStmt.setNull(2, Types.VARCHAR);
			}

			if (device_name != "") {
				prepStmt.setString(3, device_name);
			} else {
				prepStmt.setNull(3, Types.VARCHAR);
			}

			if (model != "") {
				prepStmt.setString(4, model);
			} else {
				prepStmt.setNull(4, Types.VARCHAR);
			}

			if (region != "") {
				prepStmt.setString(5, region);
			} else {
				prepStmt.setNull(5, Types.VARCHAR);
			}

			if (service != "") {
				prepStmt.setString(6, service);
			} else {
				prepStmt.setNull(6, Types.VARCHAR);
			}

			if (version != "") {
				prepStmt.setString(7, version);
			} else {
				prepStmt.setNull(7, Types.VARCHAR);
			}

			if (hostname != "") {
				prepStmt.setString(8, hostname);
			} else {
				prepStmt.setNull(8, Types.VARCHAR);
			}

			if (enablePassword != "") {
				prepStmt.setString(9, enablePassword);
			} else {
				prepStmt.setNull(9, Types.VARCHAR);
			}

			if (vrfName != "") {
				prepStmt.setString(10, vrfName);
			} else {
				prepStmt.setNull(10, Types.VARCHAR);
			}

			prepStmt.setBoolean(11, isAutoProgress);

			if (vendor != "") {
				prepStmt.setString(12, vendor);
			} else {
				prepStmt.setNull(12, Types.VARCHAR);
			}

			if (customer != "") {
				prepStmt.setString(13, customer);
			} else {
				prepStmt.setNull(13, Types.VARCHAR);
			}

			if (siteId != "") {
				prepStmt.setString(14, siteId);
			} else {
				prepStmt.setNull(14, Types.VARCHAR);
			}

			if (managementIP != "") {
				prepStmt.setString(15, managementIP);
			} else {
				prepStmt.setNull(15, Types.VARCHAR);
			}

			if (deviceType != "") {
				prepStmt.setString(16, deviceType);
			} else {
				prepStmt.setNull(16, Types.VARCHAR);
			}

			if (vpn != "") {
				prepStmt.setString(17, vpn);
			} else {
				prepStmt.setNull(17, Types.VARCHAR);
			}

			if (alphaneumeric_req_id != "") {
				prepStmt.setString(18, alphaneumeric_req_id);
			} else {
				prepStmt.setNull(18, Types.VARCHAR);
			}

			prepStmt.setString(19, request.getStatus());
			if (request_version != 0) {
				prepStmt.setDouble(20, request_version);
			} else {
				prepStmt.setDouble(20, 0);
			}

			if (request_parent_version != 0) {
				prepStmt.setDouble(21, request_parent_version);
			} else {
				prepStmt.setDouble(21, 0);
			}

			if (request_creator_name != null) {
				prepStmt.setString(22, request_creator_name);
			} else {
				prepStmt.setNull(22, Types.VARCHAR);
			}

			if (snmpHostAddress != null) {
				prepStmt.setString(23, snmpHostAddress);
			} else {
				prepStmt.setNull(23, Types.VARCHAR);
			}

			if (snmpString != null) {
				prepStmt.setString(24, snmpString);
			} else {
				prepStmt.setNull(24, Types.VARCHAR);
			}

			if (loopBackType != null) {
				prepStmt.setString(25, loopBackType);
			} else {
				prepStmt.setNull(25, Types.VARCHAR);
			}

			if (loopbackIPaddress != null) {
				prepStmt.setString(26, loopbackIPaddress);
			} else {
				prepStmt.setNull(26, Types.VARCHAR);
			}

			if (loopbackSubnetMask != null) {
				prepStmt.setString(27, loopbackSubnetMask);
			} else {
				prepStmt.setNull(27, Types.VARCHAR);
			}

			if (lanInterface != null) {
				prepStmt.setString(28, lanInterface);
			} else {
				prepStmt.setNull(28, Types.VARCHAR);
			}

			if (lanIp != null) {
				prepStmt.setString(29, lanIp);
			} else {
				prepStmt.setNull(29, Types.VARCHAR);
			}

			if (lanMaskAddress != null) {
				prepStmt.setString(30, lanMaskAddress);
			} else {
				prepStmt.setNull(30, Types.VARCHAR);
			}

			if (lanDescription != null) {
				prepStmt.setString(31, lanDescription);
			} else {
				prepStmt.setNull(31, Types.VARCHAR);
			}
			if (certificationSelectionBit != null) {
				prepStmt.setString(32, certificationSelectionBit);
			} else {
				prepStmt.setNull(32, Types.VARCHAR);
			}

			if (scheduledTime != null && scheduledTime != "") {
				SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
				try {
					Date parsedDate = sdf.parse(scheduledTime);
					Timestamp timestampTimeForScheduled = new Timestamp(parsedDate.getTime());

					prepStmt.setTimestamp(33, timestampTimeForScheduled);
					prepStmt.setString(34, "S");

				} catch (ParseException exe) {
					exe.printStackTrace();
				}
			} else {
				prepStmt.setNull(33, java.sql.Types.TIMESTAMP);
				prepStmt.setString(34, "M");
			}

			if (templateId != null) {
				prepStmt.setString(35, templateId);
			} else {
				prepStmt.setNull(35, Types.VARCHAR);
			}

			if (request_creator_name != null) {
				prepStmt.setString(36, request_creator_name);
			} else {
				prepStmt.setNull(36, Types.VARCHAR);
			}

			if (zipcode != null) {
				prepStmt.setString(37, zipcode);
			} else {
				prepStmt.setNull(37, Types.VARCHAR);
			}

			if (managed != null) {
				prepStmt.setString(38, managed);
			} else {
				prepStmt.setNull(38, Types.VARCHAR);
			}

			if (downtimerequired != null) {
				prepStmt.setString(39, downtimerequired);
			} else {
				prepStmt.setNull(39, Types.VARCHAR);
			}

			if (lastupgradedon != null) {
				prepStmt.setString(40, lastupgradedon);
			} else {
				prepStmt.setNull(40, Types.VARCHAR);
			}

			if (networktype != null) {
				prepStmt.setString(41, networktype);
			} else {
				prepStmt.setNull(41, Types.VARCHAR);
			}

			int result = prepStmt.executeUpdate();
			if (result == 1) {
				addRequestIDtoWebserviceInfo(alphaneumeric_req_id, Double.toString(request_version));
				addCertificationTestForRequest(alphaneumeric_req_id, Double.toString(request_version), "0");
				// add to OS_updgrade delivery flag details table
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

	public int insertTestRecordInDB(String requestID, String testsSelected, String requestType, double requestVersion) {
		int result = 0;
		try (Connection connection = ConnectionFactory.getConnection();
				PreparedStatement prepStmt = connection.prepareStatement(INSERT_T_TSTSTRATEGY_M_CONFIG_TRANSACTION);) {
			prepStmt.setString(1, requestID);
			prepStmt.setString(2, testsSelected);
			prepStmt.setString(3, requestType);
			prepStmt.setDouble(4, requestVersion);
			result = prepStmt.executeUpdate();
		} catch (SQLException exe) {
			logger.error("SQL Exception in insertTestRecordInDB method " + exe.getMessage());
		}
		return result;
	}

	public void addRequestID_to_Os_Upgrade_dilevary_flags(String requestId, String version) {
		try (Connection connection = ConnectionFactory.getConnection();
				PreparedStatement prepStmt = connection.prepareStatement(INSERT_OS_UPGRADE_DELIVERY_FLAGS);) {
			prepStmt.setString(1, requestId);
			prepStmt.setString(2, version);
			prepStmt.setInt(3, 0);
			prepStmt.setInt(4, 0);
			prepStmt.setInt(5, 0);
			prepStmt.setInt(6, 0);
			prepStmt.setInt(7, 0);
			prepStmt.setInt(8, 0);
			prepStmt.setInt(9, 0);
			prepStmt.executeUpdate();
		} catch (SQLException exe) {
			logger.error("SQL Exception in addRequestID_to_Os_Upgrade_dilevary_flags method " + exe.getMessage());
		}
	}

	public final List<RequestInfoSO> searchRequestsFromDB(String key, String value) throws ParseException {
		String query = null;
		if (!Global.loggedInUser.equalsIgnoreCase("admin")) {
			if (key.equalsIgnoreCase("Request ID") || key.equalsIgnoreCase("Request")) {
				query = "SELECT * FROM requestinfoso WHERE concat(alphanumeric_req_id,concat('-v',request_version)) LIKE ? AND RequestOwner=?";
			} else if (key.equalsIgnoreCase("Region")) {
				query = "SELECT * FROM requestinfoso WHERE region LIKE ? AND RequestOwner=?";

			} else if (key.equalsIgnoreCase("Vendor")) {
				query = "SELECT * FROM requestinfoso WHERE vendor LIKE ? AND RequestOwner=?";

			} else if (key.equalsIgnoreCase("Model")) {
				query = "SELECT * FROM requestinfoso WHERE model LIKE ? AND RequestOwner=?";

			} else if (key.equalsIgnoreCase("Status")) {
				query = "SELECT * FROM requestinfoso WHERE request_status LIKE ? AND RequestOwner=?";

			}
		} else {
			if (key.equalsIgnoreCase("Request ID") || key.equalsIgnoreCase("Request")) {
				query = "SELECT * FROM requestinfoso WHERE concat(alphanumeric_req_id,concat('-v',request_version)) LIKE ?";
			} else if (key.equalsIgnoreCase("Region")) {
				query = "SELECT * FROM requestinfoso WHERE region LIKE ?";

			} else if (key.equalsIgnoreCase("Vendor")) {
				query = "SELECT * FROM requestinfoso WHERE vendor LIKE ?";

			} else if (key.equalsIgnoreCase("Model")) {
				query = "SELECT * FROM requestinfoso WHERE model LIKE ?";

			} else if (key.equalsIgnoreCase("Status")) {
				query = "SELECT * FROM requestinfoso WHERE request_status LIKE ?";

			}
		}
		ResultSet rs = null;
		RequestInfoSO request = null;
		List<RequestInfoSO> requestInfoList = null;
		try (Connection connection = ConnectionFactory.getConnection();
				PreparedStatement prepStmt = connection.prepareStatement(query);) {
			if (!Global.loggedInUser.equalsIgnoreCase("admin")) {
				prepStmt.setString(1, value + "%");
				prepStmt.setString(2, Global.loggedInUser);
			} else {
				prepStmt.setString(1, value + "%");
			}
			rs = prepStmt.executeQuery();
			requestInfoList = new ArrayList<RequestInfoSO>();

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

				Timestamp date = rs.getTimestamp("date_of_processing");

				request.setDateOfProcessing((covnertTStoString(date)));
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
					Timestamp scheduledTime = rs.getTimestamp("ScheduledTime");
					if (scheduledTime != null) {
						request.setScheduledTime(covnertTStoString(scheduledTime));
					}

				} else {
					request.setElapsed_time(rs.getString("request_elapsed_time"));
				}
				if (rs.getString("RequestOwner") != null) {
					request.setRequest_assigned_to(rs.getString("RequestOwner"));

				} else {
					request.setRequest_assigned_to("");
				}

				request.setMisArPeSO(getMisArPeSO(id));
				request.setInternetLcVrf(getInternetLcVrf(id));
				request.setDeviceInterfaceSO(getDeviceInterfaceSO(id));
				requestInfoList.add(request);
			}
		} catch (SQLException exe) {
			logger.error("SQL Exception in method searchRequestsFromDB " + exe.getMessage());
		} finally {
			DBUtil.close(rs);
		}

		return requestInfoList;

	}

	/** TO-DO Redundant code */
	public final List<RequestInfo> getAllRequestInfoData() {
		String query = "SELECT * FROM requestinfoso";
		ResultSet resultSet = null;
		RequestInfoSO requestInfoObj = null;
		List<RequestInfoSO> requestInfoList1 = null;
		try (Connection connection = ConnectionFactory.getConnection();
				PreparedStatement prepStmt = connection.prepareStatement(query);) {
			resultSet = prepStmt.executeQuery();
			requestInfoList1 = new ArrayList<RequestInfoSO>();
			while (resultSet.next()) {
				requestInfoObj = new RequestInfoSO();
				requestInfoObj.setOs(resultSet.getString("Os"));
				requestInfoObj.setBanner(resultSet.getString("banner"));
				requestInfoObj.setDeviceName(resultSet.getString("device_name"));
				requestInfoObj.setModel(resultSet.getString("model"));
				requestInfoObj.setRegion(resultSet.getString("region"));
				requestInfoObj.setService(resultSet.getString("service"));
				requestInfoObj.setHostname(resultSet.getString("hostname"));
				requestInfoObj.setOsVersion(resultSet.getString("os_version"));
				requestInfoObj.setEnablePassword(resultSet.getString("enable_password"));
				requestInfoObj.setVrfName(resultSet.getString("vrf_name"));
				requestInfoObj.setIsAutoProgress(resultSet.getBoolean("isAutoProgress"));
				Timestamp date = resultSet.getTimestamp("date_of_processing");
				requestInfoObj.setDateOfProcessing((covnertTStoString(date)));
				requestInfoObj.setVendor(resultSet.getString("vendor"));
				requestInfoObj.setCustomer(resultSet.getString("customer"));
				requestInfoObj.setSiteid(resultSet.getString("siteid"));
				requestInfoObj.setStatus(resultSet.getString("request_status"));
				requestInfoObj.setManagementIp(resultSet.getString("ManagementIP"));
				requestInfoObj.setDisplay_request_id(resultSet.getString("alphanumeric_req_id"));

				requestInfoObj.setRequest_id(resultSet.getInt("request_info_id"));
				requestInfoObj.setMisArPeSO(getMisArPeSO(requestInfoObj.getRequest_id()));
				requestInfoObj.setInternetLcVrf(getInternetLcVrf(requestInfoObj.getRequest_id()));
				requestInfoList1.add(requestInfoObj);
			}

		} catch (SQLException exe) {
			logger.error("SQL Exception in getAllRequestInfoData method " + exe.getMessage());
		} finally {
			DBUtil.close(resultSet);
		}

		List<RequestInfo> requestInfoList = new ArrayList<RequestInfo>();
		GetAllDetailsService gads = new GetAllDetailsService();
		String response;
		try {
			response = gads.jsonResponseString();
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
		} catch (IOException exe) {
			logger.error("IO Exception in getAllRequestInfoData method " + exe.getMessage());
		}
		logger.info("request>>>>>>>>>>>" + requestInfoList);
		return requestInfoList;
	}

	public boolean addRequestIDtoWebserviceInfo(String alphanumeric_req_id, String request_version) {
		boolean queryStatus = false;
		try (Connection connection = ConnectionFactory.getConnection();
				PreparedStatement prepStmt = connection.prepareStatement(INSERT_WEB_SERVICE_INFO);) {
			prepStmt.setInt(1, 1);
			prepStmt.setInt(2, 1);
			prepStmt.setInt(3, 0);
			prepStmt.setInt(4, 0);
			prepStmt.setInt(5, 0);
			prepStmt.setInt(6, 0);
			prepStmt.setInt(7, 0);
			prepStmt.setInt(8, 0);
			prepStmt.setInt(9, 0);
			prepStmt.setString(10, alphanumeric_req_id);
			prepStmt.setString(11, request_version);
			prepStmt.setInt(12, 0);
			prepStmt.setInt(13, 0);
			int result = prepStmt.executeUpdate();
			if (result > 0) {
				queryStatus = true;
			}
		} catch (SQLException exe) {
			logger.error("Exception in addRequestIDtoWebserviceInfo - " + exe.getMessage());
		}
		return queryStatus;
	}

	public boolean checkDB(String requestId) {
		String query = "SELECT * FROM webserviceinfo where request_id = ?";
		ResultSet resultSet = null;
		boolean checkReportFlags = false;
		try (Connection connection = ConnectionFactory.getConnection();
				PreparedStatement prepStmt = connection.prepareStatement(query);) {
			prepStmt.setInt(1, Integer.parseInt(requestId));
			resultSet = prepStmt.executeQuery();
			if (resultSet != null) {
				while (resultSet.next()) {
					checkReportFlags = true;
				}
			}
		} catch (SQLException exe) {
			logger.error("SQL Exception in checkDB method " + exe.getMessage());
		} finally {
			DBUtil.close(resultSet);
		}
		return checkReportFlags;
	}

	public List<ReoprtFlags> getReportsInfoForAllRequestsDB() {
		String query = "SELECT * FROM webserviceinfo";
		ResultSet resultSet = null;
		ReoprtFlags flags = null;
		List<ReoprtFlags> reportFlags = null;

		try (Connection connection = ConnectionFactory.getConnection();
				PreparedStatement prepStmt = connection.prepareStatement(query);) {
			resultSet = prepStmt.executeQuery();
			reportFlags = new ArrayList<ReoprtFlags>();
			if (resultSet != null) {
				while (resultSet.next()) {
					flags = new ReoprtFlags();
					flags.setRequestId(resultSet.getString("request_id"));
					flags.setStart_test(resultSet.getInt("start_test"));
					flags.setNetwork_test(resultSet.getInt("network_test"));
					flags.setHealth_checkup(resultSet.getInt("health_checkup"));
					flags.setGenerate_config(resultSet.getInt("generate_config"));
					flags.setDeliever_config(resultSet.getInt("deliever_config"));
					flags.setCustomer_report(resultSet.getInt("customer_report"));
					flags.setApplication_test(resultSet.getInt("application_test"));
					flags.setLatencyResultRes(resultSet.getInt("latencyResultRes"));
					flags.setAlphanumeric_req_id(resultSet.getString("alphanumeric_req_id"));
					flags.setFilename(resultSet.getInt("filename"));
					flags.setPre_health_checkup(resultSet.getInt("pre_health_checkup"));
					flags.setOthers_test(resultSet.getInt("others_test"));
					flags.setNetwork_audit(resultSet.getInt("network_audit"));
					flags.setRequestVersion(resultSet.getDouble("version"));
					reportFlags.add(flags);
				}
			}
		} catch (SQLException exe) {
			logger.error("SQL Exception in getReportsInfoForAllRequestsDB method " + exe.getMessage());
		} finally {
			DBUtil.close(resultSet);
		}
		return reportFlags;
	}

	public List<RequestInfoSO> getCertificationtestvalidation(String value) {
		String query = "SELECT * FROM requestinfoso where alphanumeric_req_id = ?";
		ResultSet resultSet = null;
		RequestInfoSO request = null;
		List<RequestInfoSO> requestInfoList = null;
		try (Connection connection = ConnectionFactory.getConnection();
				PreparedStatement prepStmt = connection.prepareStatement(query);) {
			prepStmt.setString(1, value);
			resultSet = prepStmt.executeQuery();
			requestInfoList = new ArrayList<RequestInfoSO>();
			if (resultSet != null) {
				while (resultSet.next()) {
					request = new RequestInfoSO();
					request.setRequest_id(resultSet.getInt("request_info_id"));
					request.setCertificationSelectionBit(resultSet.getString("certificationSelectionBit"));
					request.setRequest_version(resultSet.getDouble("request_version"));
					requestInfoList.add(request);
				}
			}
		} catch (SQLException exe) {
			logger.error("SQL Exception in getCertificationtestvalidation method " + exe.getMessage());
		} finally {
			DBUtil.close(resultSet);
		}
		return requestInfoList;
	}

	public void editRequestForReportIOSWebserviceInfo(String requestId, String version, String textFound_dileverytest,
			String errorStatus_dilevarytest, String errorDescription_dilevarytest) {
		String query = "update webserviceinfo set TextFound_DeliveryTest = ?, ErrorStatus_DeliveryTest=?, ErrorDescription_DeliveryTest=? where alphanumeric_req_id = ? and version = ? ";
		try (Connection connection = ConnectionFactory.getConnection();
				PreparedStatement prepStmt = connection.prepareStatement(query);) {
			prepStmt.setString(1, textFound_dileverytest);
			prepStmt.setString(2, errorStatus_dilevarytest);
			prepStmt.setString(3, errorDescription_dilevarytest);
			prepStmt.setString(4, requestId);
			prepStmt.setString(5, version);

			prepStmt.executeUpdate();
		} catch (SQLException exe) {
			logger.error("SQL Exception in editRequestForReportIOSWebserviceInfo method " + exe.getMessage());
		}
	}

	public void editRequestforReportWebserviceInfo(String requestId, String version, String field, String flag,
			String status) {
		String query = null;

		if (field.equalsIgnoreCase("health_check")) {
			query = "update webserviceinfo set health_checkup = ? where alphanumeric_req_id = ? and version = ? ";
		} else if (field.equalsIgnoreCase("deliver_configuration")) {
			query = "update webserviceinfo set deliever_config = ? where alphanumeric_req_id = ? and version = ? ";
		} else if (field.equalsIgnoreCase("network_test")) {
			query = "update webserviceinfo set network_test = ? where alphanumeric_req_id = ? and version = ? ";
		} else if (field.equalsIgnoreCase("deliever_config")) {
			query = "update webserviceinfo set deliever_config = ? where alphanumeric_req_id = ? and version = ? ";
		} else if (field.equalsIgnoreCase("Application_test")) {
			query = "update webserviceinfo set application_test = ? where alphanumeric_req_id = ? and version = ? ";
		} else if (field.equalsIgnoreCase("customer_report")) {
			query = "update webserviceinfo set customer_report = ? where alphanumeric_req_id = ? and version = ? ";
		} else if (field.equalsIgnoreCase("generate_configuration")) {
			query = "update webserviceinfo set generate_config = ? where alphanumeric_req_id = ? and version = ? ";
		} else if (field.equalsIgnoreCase("pre_health_checkup")) {
			query = "update webserviceinfo set pre_health_checkup = ? where alphanumeric_req_id = ? and version = ? ";
		} else if (field.equalsIgnoreCase("others_test")) {
			query = "update webserviceinfo set others_test = ? where alphanumeric_req_id = ? and version = ? ";
		} else if (field.equalsIgnoreCase("network_audit")) {
			query = "update webserviceinfo set network_audit = ? where alphanumeric_req_id = ? and version = ? ";
		}

		try (Connection connection = ConnectionFactory.getConnection();
				PreparedStatement preparedStmt = connection.prepareStatement(query);) {
			preparedStmt.setString(1, flag);
			preparedStmt.setString(2, requestId);
			preparedStmt.setString(3, version);
			preparedStmt.executeUpdate();
		} catch (SQLException exe) {
			logger.error("SQL Exception in editRequestforReportWebserviceInfo method " + exe.getMessage());
		}
		Date date = new Date();
		Timestamp timestamp = new Timestamp(date.getTime());
		Timestamp d = null;
		if (field.equalsIgnoreCase("customer_report") && status.contains("Success")) {
			ResultSet rs = null;
			try (Connection connection = ConnectionFactory.getConnection();
					PreparedStatement preparedStmt = connection
							.prepareStatement(GET_REQUEST_INFO_SO_BY_ALPREQID_VERSION);) {
				preparedStmt.setString(1, requestId);
				preparedStmt.setString(2, version);
				rs = preparedStmt.executeQuery();

				while (rs.next()) {
					if (rs.getString("temp_elapsed_time") == null) {
						if (rs.getString("RequestType_Flag").equalsIgnoreCase("M")) {
							d = rs.getTimestamp("date_of_processing");
						} else {
							d = rs.getTimestamp("ScheduledTime");
						}
						String diff = calcTimeDiffInMins(timestamp, d);
						updateRequestInfoSoByAlpReqVersion(requestId, version, status, timestamp, diff);

					} else {
						d = rs.getTimestamp("temp_processing_time");
						String diff1 = calcTimeDiffInMins(timestamp, d);
						String diff2 = String.format("%.2f", Float.toString(
								(Float.parseFloat(diff1) + Float.parseFloat(rs.getString("temp_elapsed_time")))));
						updateRequestInfoSoByAlpReqVersion(requestId, version, status, timestamp, diff2);
					}
				}
			} catch (SQLException exe) {
				logger.error("SQL Exception in editRequestforReportWebserviceInfo method " + exe.getMessage());
			} finally {
				DBUtil.close(rs);
			}
			ServiceOrderEntity ent = serviceOrderRepo.findByRequestId(requestId);

			if (ent != null) {
				serviceOrderRepo.updateStatusAndRequestId(requestId, "Success", ent.getServiceOrder(), "admin",
						Timestamp.valueOf(LocalDateTime.now()));
			}
		} else if (field.equalsIgnoreCase("customer_report") && status.equals("Failure")) {
			ResultSet rs = null;
			try (Connection connection = ConnectionFactory.getConnection();
					PreparedStatement preparedStmt = connection
							.prepareStatement(GET_REQUEST_INFO_SO_BY_ALPREQID_VERSION);) {

				preparedStmt.setString(1, requestId);
				preparedStmt.setString(2, version);
				rs = preparedStmt.executeQuery();
				if (rs != null) {
					while (rs.next()) {
						d = rs.getTimestamp("temp_processing_time");
					}
				}
				updateRequestInfoSoByAlpReqVersion(requestId, version, status, timestamp, "0");
			} catch (SQLException exe) {
				logger.error("SQL Exception in editRequestforReportWebserviceInfo method " + exe.getMessage());
			} finally {
				DBUtil.close(rs);
			}
			ServiceOrderEntity ent = serviceOrderRepo.findByRequestId(requestId);

			if (ent != null)
				serviceOrderRepo.updateStatusAndRequestId(requestId, "Failure", ent.getServiceOrder(), "admin",
						Timestamp.valueOf(LocalDateTime.now()));
		} else {
			updateRequestInfoSoByAlpReqVersion(requestId, version, status, timestamp, "0");
		}

	}

	public List<RequestInfoSO> getAllResquestsFromDB() {
		String query = null;
		if (Global.loggedInUser.equalsIgnoreCase("feuser")) {
			query = "SELECT * FROM requestinfoso WHERE request_status NOT IN('Cancelled') and RequestOwner=? and alphanumeric_req_id rlike'SLGC|SLGF|SLGT|SNRC|SNNC|SNNA|SLGB'";
		} else if (Global.loggedInUser.equalsIgnoreCase("seuser")) {
			query = "SELECT * FROM requestinfoso WHERE request_status NOT IN('Cancelled') and request_creator_name=? and alphanumeric_req_id rlike'SLGC|SLGF|SLGT|SNRC|SNNC|SNNA|SLGB'";

		} else if (Global.loggedInUser.equalsIgnoreCase("admin")) {
			// query =
			// "SELECT * FROM requestinfoso WHERE request_status NOT
			// IN('Cancelled') and
			// alphanumeric_req_id rlike'SR|OS'";
			query = "SELECT * FROM requestinfoso WHERE (request_status NOT IN('Cancelled') AND import_status IS NULL) OR import_status IN('Success')";
		}

		ResultSet rs = null;
		RequestInfoSO request = null;
		List<RequestInfoSO> requestInfoList = null;
		try (Connection connection = ConnectionFactory.getConnection();
				PreparedStatement preparedStmt = connection.prepareStatement(query);) {
			rs = preparedStmt.executeQuery();
			requestInfoList = new ArrayList<RequestInfoSO>();
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
					request.setRequest_assigned_to(rs.getString("RequestOwner"));

					request.setMisArPeSO(getMisArPeSO(id));
					request.setInternetLcVrf(getInternetLcVrf(id));
					request.setDeviceInterfaceSO(getDeviceInterfaceSO(id));

					requestInfoList.add(request);
				}
			}
		} catch (SQLException exe) {
			logger.error("SQL Exception in getAllResquestsFromDB method " + exe.getMessage());
		} finally {
			DBUtil.close(rs);
		}

		return requestInfoList;

	}

	public EIPAMPojo getIPAMIPfromDB(String site, String customer, String service, String region) {
		String query = "SELECT * FROM eipamdbtable WHERE eipam_site_id = ? AND eipam_customer_name=? AND eipam_service=? AND eipam_region=?";
		ResultSet rs = null;
		EIPAMPojo eipamobj = null;
		try (Connection connection = ConnectionFactory.getConnection();
				PreparedStatement pst = connection.prepareStatement(query);) {
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
			}
		} catch (SQLException exe) {
			logger.error("SQL Exception in getIPAMIPfromDB method " + exe.getMessage());
		} finally {
			DBUtil.close(rs);
		}
		return eipamobj;
	}

	public List<EIPAMPojo> getALLIPAMDatafromDB() {
		String query = "SELECT * FROM eipamdbtable";
		EIPAMPojo pojo;
		ResultSet rs = null;
		List<EIPAMPojo> requestInfoList = null;
		try (Connection connection = ConnectionFactory.getConnection();
				PreparedStatement pst = connection.prepareStatement(query);) {
			rs = pst.executeQuery();
			requestInfoList = new ArrayList<EIPAMPojo>();
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
				requestInfoList.add(pojo);
			}

		} catch (SQLException exe) {
			logger.error("SQL Exception in getALLIPAMDatafromDB method " + exe.getMessage());
		} finally {
			DBUtil.close(rs);
		}
		return requestInfoList;
	}

	public UserValidationResultDetailPojo checkUsersDB(String username, String password) {
		String query = "SELECT * FROM users";
		ResultSet rs = null;
		UserPojo flags = null;
		List<UserPojo> userList = null;
		UserValidationResultDetailPojo resultSet = new UserValidationResultDetailPojo();
		try (Connection connection = ConnectionFactory.getConnection();
				PreparedStatement pst = connection.prepareStatement(query);) {
			rs = pst.executeQuery();
			userList = new ArrayList<UserPojo>();
			boolean flag = false;
			if (rs != null) {
				while (rs.next()) {
					flags = new UserPojo();
					flags.setUsername(rs.getString("user_name"));
					flags.setPassword(rs.getString("user_password"));
					flags.setPrivilegeLevel(rs.getInt("privilegeLevel"));
					userList.add(flags);
				}

				if (userList.size() == 0) {
					resultSet.setMessage("No data found");
					resultSet.setResult(false);
					resultSet.setPrivilegeLevel(0);
					return resultSet;
				} else {
					for (int i = 0; i < userList.size(); i++) {
						if (userList.get(i).getUsername().equals(username)
								&& userList.get(i).getPassword().equals(password)) {
							boolean didLogin = setUserLoginFlag(username, password);
							if (didLogin) {
								resultSet.setPrivilegeLevel(userList.get(i).getPrivilegeLevel());
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
		} catch (SQLException exe) {
			logger.error("SQL Exception in checkUsersDB method " + exe.getMessage());
		} finally {
			DBUtil.close(rs);
		}
		return resultSet;

	}

	private boolean setUserLoginFlag(String username, String password) {
		boolean result = false;
		String query = "update users set user_status=1 where user_name=? AND user_password=?";
		try (Connection connection = ConnectionFactory.getConnection();
				PreparedStatement pst = connection.prepareStatement(query);) {
			pst.setString(1, username);
			pst.setString(2, password);

			int i = pst.executeUpdate();
			if (i > 0) {
				result = true;
			}
		} catch (SQLException exe) {
			logger.error("SQL Exception in checkUsersDB method " + exe.getMessage());
		}
		return result;
	}

	public UserPojo getRouterCredentials() {
		String query = "SELECT * FROM routeruserdevicemanagementtable";
		ResultSet rs = null;
		UserPojo userList = new UserPojo();
		try (Connection connection = ConnectionFactory.getConnection();
				PreparedStatement pst = connection.prepareStatement(query);) {
			rs = pst.executeQuery();
			if (rs.next()) {
				userList.setUsername(rs.getString("router_username"));
				userList.setPassword(rs.getString("router_password"));
			}
		} catch (SQLException exe) {
			logger.error("SQL Exception in getRouterCredentials method " + exe.getMessage());
		}
		return userList;
	}

	public UserPojo getRouterCredentials(String mgmtip) {
		String query = "SELECT * FROM routeruserdevicemanagementtable where mgmtip=?";
		ResultSet rs = null;
		UserPojo userList = new UserPojo();
		try (Connection connection = ConnectionFactory.getConnection();
				PreparedStatement pst = connection.prepareStatement(query);) {
			pst.setString(1, mgmtip);

			rs = pst.executeQuery();
			if (rs.next()) {
				userList.setUsername(rs.getString("router_username"));
				userList.setPassword(rs.getString("router_password"));
			}
		} catch (SQLException exe) {
			logger.error("SQL Exception in getRouterCredentials with mgmtip method " + exe.getMessage());
		}
		return userList;
	}

	public boolean addNewUserToDB(String username, String pass) {
		String query = "INSERT INTO users(user_name,user_password,user_status)" + "VALUES(?,?,?)";
		try (Connection connection = ConnectionFactory.getConnection();
				PreparedStatement pst = connection.prepareStatement(query);) {
			pst.setString(1, username);
			pst.setString(2, pass);
			pst.setInt(3, 0);

			int i = pst.executeUpdate();
			if (i > 0) {
				return true;
			}
		} catch (SQLException exe) {
			logger.error("SQL Exception in addNewUserToDB method " + exe.getMessage());
		}
		return false;
	}

	public UserPojo updateRouterDeviceManagementDetails(String username, String password) {
		String query = "SELECT * FROM routeruserdevicemanagementtable";
		String updateQuery = "update routeruserdevicemanagementtable set router_username = ?,router_password=? where id = ?";
		String insertQuery = "INSERT INTO routeruserdevicemanagementtable(router_username,router_password) VALUES(?,?)";
		ResultSet rs = null;
		ResultSet getRs = null;
		UserPojo userList = new UserPojo();
		try (Connection connection = ConnectionFactory.getConnection();
				PreparedStatement pst = connection.prepareStatement(query);) {
			rs = pst.executeQuery();
			if (rs.next()) {
				try (PreparedStatement uptPst = connection.prepareStatement(updateQuery);) {
					uptPst.setString(1, username);
					uptPst.setString(2, password);
					uptPst.setInt(3, 1);
					uptPst.executeUpdate();
				} catch (SQLException exe) {
					logger.error(
							"SQL Exception in updateRouterDeviceManagementDetails update method " + exe.getMessage());
				}
			} else {
				try (PreparedStatement instPst = connection.prepareStatement(insertQuery);) {
					instPst.setString(1, username);
					instPst.setString(2, password);
					instPst.executeUpdate();
				} catch (SQLException exe) {
					logger.error(
							"SQL Exception in updateRouterDeviceManagementDetails insert method " + exe.getMessage());
				}
			}

			try (PreparedStatement getPst = connection.prepareStatement(query);) {
				getRs = getPst.executeQuery();
				while (getRs.next()) {
					userList.setUsername(getRs.getString("router_username"));
					userList.setPassword(getRs.getString("router_password"));
				}
			} catch (SQLException exe) {
				logger.error("SQL Exception in updateRouterDeviceManagementDetails get method " + exe.getMessage());
			}
		} catch (SQLException exe) {
			logger.error("SQL Exception in updateRouterDeviceManagementDetails method " + exe.getMessage());
		} finally {
			DBUtil.close(rs);
			DBUtil.close(getRs);
		}
		return userList;
	}

	public List<EIPAMPojo> getSearchedRecordsFromDB(String site, String customer, String service, String ip)
			throws SQLException {
		Connection connection = ConnectionFactory.getConnection();
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
				query1 = "SELECT * FROM eipamdbtable WHERE eipam_site_id LIKE ?";
				ps = connection.prepareStatement(query1);

				ps.setString(1, site + "%");

			} else if (!customer.isEmpty()) {
				query1 = "SELECT * FROM eipamdbtable WHERE eipam_customer_name LIKE ?";
				ps = connection.prepareStatement(query1);

				ps.setString(1, customer + "%");
			} else if (!service.isEmpty()) {
				query1 = "SELECT * FROM eipamdbtable WHERE eipam_service LIKE ?";

				ps = connection.prepareStatement(query1);

				ps.setString(1, service + "%");
			} else if (!ip.isEmpty()) {
				query1 = "SELECT * FROM eipamdbtable WHERE eipam_ip LIKE ?";

				ps = connection.prepareStatement(query1);

				ps.setString(1, ip + "%");
			}
		} else if (parameters_to_search == 2) {
			if (!site.isEmpty() && !customer.isEmpty()) {
				// query for site and customer
				query1 = "SELECT * FROM eipamdbtable WHERE eipam_site_id LIKE ? AND eipam_customer_name LIKE ?";
				ps = connection.prepareStatement(query1);

				ps.setString(1, site + "%");
				ps.setString(2, customer + "%");

			} else if (!site.isEmpty() && !service.isEmpty()) {
				// site and service
				query1 = "SELECT * FROM eipamdbtable WHERE eipam_site_id LIKE ?  AND eipam_service LIKE ?";

				ps = connection.prepareStatement(query1);

				ps.setString(1, site + "%");
				ps.setString(2, service + "%");
			} else if (!site.isEmpty() && !ip.isEmpty()) {
				// site and ip
				query1 = "SELECT * FROM eipamdbtable WHERE eipam_site_id LIKE ? AND eipam_ip LIKE?";

				ps = connection.prepareStatement(query1);

				ps.setString(1, site + "%");
				ps.setString(2, ip + "%");
			} else if (!customer.isEmpty() && !service.isEmpty()) {
				// customer and service
				query1 = "SELECT * FROM eipamdbtable WHERE eipam_customer_name LIKE ? AND eipam_service LIKE ?";

				ps = connection.prepareStatement(query1);

				ps.setString(1, customer + "%");
				ps.setString(2, service + "%");
			} else if (!customer.isEmpty() && !ip.isEmpty()) {
				// customer and ip
				query1 = "SELECT * FROM eipamdbtable WHERE eipam_customer_name LIKE ? AND eipam_ip LIKE ?";
				ps = connection.prepareStatement(query1);

				ps.setString(1, customer + "%");
				ps.setString(2, ip + "%");
			} else if (!service.isEmpty() && !ip.isEmpty()) {
				// service and ip
				query1 = "SELECT * FROM eipamdbtable WHERE eipam_service LIKE ? AND eipam_ip LIKE ?";

				ps = connection.prepareStatement(query1);

				ps.setString(1, service + "%");
				ps.setString(2, ip + "%");
			}
		} else if (parameters_to_search == 3) {
			if (!site.isEmpty() && !customer.isEmpty() && !service.isEmpty()) {
				// site customer service
				query1 = "SELECT * FROM eipamdbtable WHERE eipam_site_id LIKE ? AND eipam_customer_name LIKE ? AND eipam_service LIKE ?";

				ps = connection.prepareStatement(query1);

				ps.setString(1, site + "%");
				ps.setString(2, customer + "%");
				ps.setString(3, service + "%");
			} else if (!site.isEmpty() && !service.isEmpty() && !ip.isEmpty()) {
				// site service ip
				query1 = "SELECT * FROM eipamdbtable WHERE eipam_site_id LIKE ? AND eipam_service LIKE ? AND eipam_ip LIKE ?";

				ps = connection.prepareStatement(query1);

				ps.setString(1, site + "%");
				ps.setString(2, service + "%");
				ps.setString(3, ip + "%");
			} else if (!customer.isEmpty() && !service.isEmpty() && !ip.isEmpty()) {
				// customer service ip
				query1 = "SELECT * FROM eipamdbtable WHERE eipam_customer_name LIKE ? AND eipam_service LIKE ? AND eipam_ip LIKE ?";

				ps = connection.prepareStatement(query1);

				ps.setString(1, customer + "%");
				ps.setString(2, service + "%");
				ps.setString(3, ip + "%");
			} else if (!site.isEmpty() && !customer.isEmpty() && !ip.isEmpty()) {
				// site customer ip
				query1 = "SELECT * FROM eipamdbtable WHERE eipam_site_id LIKE ? AND eipam_customer_name LIKE ? AND eipam_ip LIKE ?";

				ps = connection.prepareStatement(query1);

				ps.setString(1, site + "%");
				ps.setString(2, customer + "%");
				ps.setString(3, ip + "%");
			}
		} else {
			// all four paramerter serarch
			query1 = "SELECT * FROM eipamdbtable WHERE eipam_site_id = ? AND eipam_customer_name=? AND eipam_service=? AND eipam_ip=?";

			ps = connection.prepareStatement(query1);

			ps.setString(1, site + "%");
			ps.setString(2, customer + "%");
			ps.setString(3, service + "%");
			ps.setString(4, ip + "%");
		}

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
			e.printStackTrace();
		} finally {
			DBUtil.close(rs);
			DBUtil.close(ps);
			DBUtil.close(connection);
		}

		return requestInfoList1;
	}

	/*
	 * Code changes for JDBC to JPA migration --- Alert Page(To display All
	 * alerts)
	 */

	public List<AlertInformationPojo> getALLAlertDataFromDB() {
		String query = "SELECT * FROM alertinformationtable";
		AlertInformationPojo pojo;
		ResultSet rs = null;
		List<AlertInformationPojo> requestInfoList = null;
		try (Connection connection = ConnectionFactory.getConnection();
				PreparedStatement pst = connection.prepareStatement(query);) {
			rs = pst.executeQuery();
			requestInfoList = new ArrayList<AlertInformationPojo>();
			while (rs.next()) {
				pojo = new AlertInformationPojo();
				pojo.setAlert_code(rs.getString("alert_code"));
				pojo.setAlert_category(rs.getString("alert_category"));
				pojo.setAlert_description(rs.getString("alert_description"));
				requestInfoList.add(pojo);
			}
		} catch (SQLException exe) {
			logger.error("SQL Exception in getALLAlertDataFromDB method " + exe.getMessage());
		} finally {
			DBUtil.close(rs);
		}
		return requestInfoList;
	}

	public UserValidationResultDetailPojo updateEIPAMDB(EIPAMPojo pojo) {
		UserValidationResultDetailPojo validatedResult = new UserValidationResultDetailPojo();
		String query = "INSERT INTO eipamdbtable(eipam_site_id,eipam_region,eipam_ip,eipam_subnet_mask,eipam_service,eipam_customer_name,eipam_ip_status)"
				+ "VALUES(?,?,?,?,?,?,?)";

		try (Connection connection = ConnectionFactory.getConnection();
				PreparedStatement ps = connection.prepareStatement(query);) {
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
		} catch (SQLException exe) {
			logger.error("SQL Exception in updateEIPAMDB method " + exe.getMessage());
		}
		return validatedResult;
	}

	public final List<AlertInformationPojo> getSearchedRecordsFromAlertsDB(String code, String description) {
		String query = "SELECT * FROM alertinformationtable WHERE alert_code LIKE ? OR alert_description LIKE ?";
		ResultSet rs = null;
		AlertInformationPojo eipamobj = null;
		List<AlertInformationPojo> requestInfoList1 = null;
		try (Connection connection = ConnectionFactory.getConnection();
				PreparedStatement ps = connection.prepareStatement(query);) {
			requestInfoList1 = new ArrayList<AlertInformationPojo>();
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

		} catch (SQLException exe) {
			logger.error("SQL Exception in getSearchedRecordsFromAlertsDB method " + exe.getMessage());
		} finally {
			DBUtil.close(rs);
		}
		return requestInfoList1;
	}

	public final List<AlertInformationPojo> getLastAlertId() {
		String query = "SELECT * FROM alertinformationtable";
		AlertInformationPojo pojo;
		ResultSet rs = null;
		List<AlertInformationPojo> resultobj = new ArrayList<AlertInformationPojo>();
		List<AlertInformationPojo> requestInfoList1 = null;
		try (Connection connection = ConnectionFactory.getConnection();
				PreparedStatement ps = connection.prepareStatement(query);) {
			rs = ps.executeQuery();
			requestInfoList1 = new ArrayList<AlertInformationPojo>();
			while (rs.next()) {
				pojo = new AlertInformationPojo();
				pojo.setAlert_code(rs.getString("alert_code"));
				pojo.setAlert_category(rs.getString("alert_category"));
				pojo.setAlert_description(rs.getString("alert_description"));
				pojo.setAlert_type(rs.getString("alert_type"));
				requestInfoList1.add(pojo);
			}
			AlertInformationPojo tempObj = new AlertInformationPojo();
			if (requestInfoList1.size() > 0) {
				tempObj = new AlertInformationPojo();
				tempObj.setAlert_code(separate(requestInfoList1.get(requestInfoList1.size() - 1).getAlert_code()));
			} else {
				tempObj = new AlertInformationPojo();
				tempObj.setAlert_code("999");
			}

			resultobj.add(tempObj);
		} catch (SQLException exe) {
			logger.error("SQL Exception in getLastAlertId method " + exe.getMessage());
		} finally {
			DBUtil.close(rs);
		}
		return resultobj;
	}

	public UserValidationResultDetailPojo addNewAlertNotification(AlertInformationPojo pojo) {
		UserValidationResultDetailPojo validatedResult = new UserValidationResultDetailPojo();
		String query = "INSERT INTO alertinformationtable(alert_code,alert_category,alert_description,alert_type)"
				+ "VALUES(?,?,?,?)";
		try (Connection connection = ConnectionFactory.getConnection();
				PreparedStatement ps = connection.prepareStatement(query);) {
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
			logger.error("Error:> " + e.getMessage());
		}
		return validatedResult;
	}

	private boolean updateEIPAMTable(String ip) {
		boolean result = false;
		String query = "update eipamdbtable set eipam_ip_status=1 where eipam_ip=?";
		try (Connection connection = ConnectionFactory.getConnection();
				PreparedStatement ps = connection.prepareStatement(query);) {
			ps.setString(1, ip);
			int i = ps.executeUpdate();
			if (i > 0) {
				result = true;
			}
		} catch (SQLException e) {
			logger.error("Error:> " + e.getMessage());
		}
		return result;
	}

	public List<RequestInfoSO> getDatasForRequestfromDB(String id) {
		List<RequestInfoSO> list = null;
		String query = "SELECT * FROM requestinfoso WHERE alphanumeric_req_id = ?";
		RequestInfoSO request;
		ResultSet rs = null;
		int id1;

		try (Connection connection = ConnectionFactory.getConnection();
				PreparedStatement ps = connection.prepareStatement(query);) {
			list = new ArrayList<RequestInfoSO>();
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
				request.setMisArPeSO(getMisArPeSO(id1));
				request.setInternetLcVrf(getInternetLcVrf(id1));
				request.setDeviceInterfaceSO(getDeviceInterfaceSO(id1));
				list.add(request);
			}
		} catch (SQLException exe) {
			logger.error("SQL Exception in getDatasForRequestfromDB method " + exe.getMessage());
		} catch (ParseException exe) {
			logger.error("ParseException in getDatasForRequestfromDB method " + exe.getMessage());
		} finally {
			DBUtil.close(rs);
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
		String query = "INSERT INTO requestinfoso(Os,banner,device_name,model,region,service,os_version,hostname,enable_password,vrf_name,isAutoProgress,vendor,customer,siteid,managementIp,device_type,vpn,alphanumeric_req_id,request_status,request_version,request_parent_version,request_creator_name,snmpHostAddress,snmpString,loopBackType,loopbackIPaddress,loopbackSubnetMask,lanInterface,lanIp,lanMaskAddress,lanDescription,certificationSelectionBit,ScheduledTime,RequestType_Flag,TemplateIdUsed,RequestOwner)"
				+ "VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";

		try (Connection connection = ConnectionFactory.getConnection();
				PreparedStatement ps = connection.prepareStatement(query);) {

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
			insertInternetCvrfso(request);
			insertMisArPeSo(request);
			insertDeviceInterfaceSo(request);
			insertBannerDataTable(banner);

			if (Os != "") {
				ps.setString(1, Os);
			} else {
				ps.setNull(1, Types.VARCHAR);

			}
			if (banner != "") {
				ps.setString(2, banner);
			} else {
				ps.setNull(2, Types.VARCHAR);

			}

			if (device_name != "") {
				ps.setString(3, device_name);
			} else {
				ps.setNull(3, Types.VARCHAR);

			}
			if (model != "") {
				ps.setString(4, model);
			} else {
				ps.setNull(4, Types.VARCHAR);

			}
			if (region != "") {
				ps.setString(5, region);
			} else {
				ps.setNull(5, Types.VARCHAR);

			}
			if (service != "") {
				ps.setString(6, service);
			} else {
				ps.setNull(6, Types.VARCHAR);

			}
			if (version != "") {
				ps.setString(7, version);
			} else {
				ps.setNull(7, Types.VARCHAR);

			}
			if (hostname != "") {
				ps.setString(8, hostname);
			} else {
				ps.setNull(8, Types.VARCHAR);

			}
			if (enablePassword != "") {
				ps.setString(9, enablePassword);
			} else {
				ps.setNull(9, Types.VARCHAR);

			}
			if (vrfName != "") {
				ps.setString(10, vrfName);
			} else {
				ps.setNull(10, Types.VARCHAR);

			}

			ps.setBoolean(11, isAutoProgress);

			if (vendor != "") {
				ps.setString(12, vendor);
			} else {
				ps.setNull(12, Types.VARCHAR);

			}
			if (customer != "") {
				ps.setString(13, customer);
			} else {
				ps.setNull(13, Types.VARCHAR);

			}

			if (siteId != "") {
				ps.setString(14, siteId);
			} else {
				ps.setNull(14, Types.VARCHAR);

			}

			if (managementIP != "") {
				ps.setString(15, managementIP);
			} else {
				ps.setNull(15, Types.VARCHAR);

			}
			if (deviceType != "") {
				ps.setString(16, deviceType);
			} else {
				ps.setNull(16, Types.VARCHAR);

			}
			if (vpn != "") {
				ps.setString(17, vpn);
			} else {
				ps.setNull(17, Types.VARCHAR);

			}
			if (alphaneumeric_req_id != "") {
				ps.setString(18, alphaneumeric_req_id);
			} else {
				ps.setNull(18, Types.VARCHAR);

			}

			ps.setString(19, request_status);

			ps.setDouble(20, request_version);
			ps.setDouble(21, request_parent_version);
			if (request_creator_name != null) {
				ps.setString(22, request_creator_name);
			} else {
				ps.setNull(22, Types.VARCHAR);

			}

			if (snmpHostAddress != null) {
				ps.setString(23, snmpHostAddress);
			} else {
				ps.setNull(23, Types.VARCHAR);

			}
			if (snmpString != null) {
				ps.setString(24, snmpString);
			} else {
				ps.setNull(24, Types.VARCHAR);

			}
			if (loopBackType != null) {
				ps.setString(25, loopBackType);
			} else {
				ps.setNull(25, Types.VARCHAR);

			}
			if (loopbackIPaddress != null) {
				ps.setString(26, loopbackIPaddress);
			} else {
				ps.setNull(26, Types.VARCHAR);

			}
			if (loopbackSubnetMask != null) {
				ps.setString(27, loopbackSubnetMask);
			} else {
				ps.setNull(27, Types.VARCHAR);

			}
			if (lanInterface != null) {
				ps.setString(28, lanInterface);
			} else {
				ps.setNull(28, Types.VARCHAR);

			}
			if (lanIp != null) {
				ps.setString(29, lanIp);
			} else {
				ps.setNull(29, Types.VARCHAR);

			}
			if (lanMaskAddress != null) {
				ps.setString(30, lanMaskAddress);
			} else {
				ps.setNull(30, Types.VARCHAR);

			}
			if (lanDescription != null) {
				ps.setString(31, lanDescription);
			} else {
				ps.setNull(31, Types.VARCHAR);

			}
			if (certificationSelectionBit != null) {
				ps.setString(32, certificationSelectionBit);
			} else {
				ps.setNull(32, Types.VARCHAR);

			}
			if (scheduledTime != null && scheduledTime != "") {
				SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
				try {
					Date parsedDate = sdf.parse(scheduledTime);
					Timestamp timestampTimeForScheduled = new Timestamp(parsedDate.getTime());
					ps.setTimestamp(33, timestampTimeForScheduled);
					ps.setString(34, "S");
				} catch (ParseException e) {
					e.printStackTrace();
				}
			} else {
				ps.setNull(33, java.sql.Types.TIMESTAMP);
				ps.setString(34, "M");
			}

			if (templateId != null) {
				ps.setString(35, templateId);
			} else {
				ps.setNull(35, Types.VARCHAR);

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
		} catch (SQLException exe) {
			logger.error("SQL Exception in insertRequestInDBForNewVersion method " + exe.getMessage());
		}

		hmap.put("result", "false");
		return hmap;
	}

	public List<RequestInfoSO> searchRequestsFromDBWithVersion(String key, String value, String version) {
		String query = null;
		if (!Global.loggedInUser.equalsIgnoreCase("admin")) {

			if (Global.loggedInUser.equalsIgnoreCase("seuser")) {
				if (key.equalsIgnoreCase("Request ID") || key.equalsIgnoreCase("Request")) {
					query = "SELECT * FROM requestinfoso WHERE alphanumeric_req_id = ? AND request_version = ? and request_creator_name=?";
				} else if (key.equalsIgnoreCase("Region")) {
					query = "SELECT * FROM requestinfoso WHERE region LIKE ?  and request_creator_name=?";

				} else if (key.equalsIgnoreCase("Vendor")) {
					query = "SELECT * FROM requestinfoso WHERE vendor LIKE ?  and request_creator_name=?";

				} else if (key.equalsIgnoreCase("Model")) {
					query = "SELECT * FROM requestinfoso WHERE model LIKE ?  and request_creator_name=?";

				} else if (key.equalsIgnoreCase("Status")) {
					query = "SELECT * FROM requestinfoso WHERE request_status LIKE ?  and request_creator_name=?";

				}
			} else if (Global.loggedInUser.equalsIgnoreCase("feuser")) {
				if (key.equalsIgnoreCase("Request ID") || key.equalsIgnoreCase("Request")) {
					query = "SELECT * FROM requestinfoso WHERE alphanumeric_req_id = ? AND request_version = ? and RequestOwner=?";
				} else if (key.equalsIgnoreCase("Region")) {
					query = "SELECT * FROM requestinfoso WHERE region LIKE ?  and RequestOwner=?";

				} else if (key.equalsIgnoreCase("Vendor")) {
					query = "SELECT * FROM requestinfoso WHERE vendor LIKE ?  and RequestOwner=?";

				} else if (key.equalsIgnoreCase("Model")) {
					query = "SELECT * FROM requestinfoso WHERE model LIKE ?  and RequestOwner=?";

				} else if (key.equalsIgnoreCase("Status")) {
					query = "SELECT * FROM requestinfoso WHERE request_status LIKE ?  and RequestOwner=?";

				}
			} else {
				if (key.equalsIgnoreCase("Request ID") || key.equalsIgnoreCase("Request")) {
					query = "SELECT * FROM requestinfoso WHERE alphanumeric_req_id = ? AND request_version = ? and RequestOwner=?";
				} else if (key.equalsIgnoreCase("Region")) {
					query = "SELECT * FROM requestinfoso WHERE region LIKE ?  and RequestOwner=?";

				} else if (key.equalsIgnoreCase("Vendor")) {
					query = "SELECT * FROM requestinfoso WHERE vendor LIKE ?  and RequestOwner=?";

				} else if (key.equalsIgnoreCase("Model")) {
					query = "SELECT * FROM requestinfoso WHERE model LIKE ?  and RequestOwner=?";

				} else if (key.equalsIgnoreCase("Status")) {
					query = "SELECT * FROM requestinfoso WHERE request_status LIKE ?  and RequestOwner=?";

				}
			}
		} else {
			if (key.equalsIgnoreCase("Request ID") || key.equalsIgnoreCase("Request")) {
				query = "SELECT * FROM requestinfoso WHERE alphanumeric_req_id = ? AND request_version = ?";
			} else if (key.equalsIgnoreCase("Region")) {
				query = "SELECT * FROM requestinfoso WHERE region LIKE ?";

			} else if (key.equalsIgnoreCase("Vendor")) {
				query = "SELECT * FROM requestinfoso WHERE vendor LIKE ?";

			} else if (key.equalsIgnoreCase("Model")) {
				query = "SELECT * FROM requestinfoso WHERE model LIKE ?";

			} else if (key.equalsIgnoreCase("Status")) {
				query = "SELECT * FROM requestinfoso WHERE request_status LIKE ?";

			}
		}
		ResultSet rs = null;
		RequestInfoSO request = null;
		List<RequestInfoSO> requestInfoList1 = null;
		try (Connection connection = ConnectionFactory.getConnection();
				PreparedStatement pst = connection.prepareStatement(query);) {

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
				} else if (importStatus.equals("Success")) {
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
					Timestamp scheduledTime = rs.getTimestamp("ScheduledTime");
					if (scheduledTime != null) {
						request.setScheduledTime(covnertTStoString(scheduledTime));
					}
				} else {
					request.setElapsedTime("00:00:00");
				}

				if (rs.getString("RequestOwner") != null) {
					request.setRequest_assigned_to(rs.getString("RequestOwner"));
				} else {
					request.setRequest_assigned_to("seuser");
				}

				request.setMisArPeSO(getMisArPeSO(id));
				request.setInternetLcVrf(getInternetLcVrf(id));
				request.setDeviceInterfaceSO(getDeviceInterfaceSO(id));

				request.setZipcode(rs.getString("zipcode"));
				request.setManaged(rs.getString("managed"));
				request.setDownTimeRequired(rs.getString("downtimeRequired"));
				request.setLastUpgradedOn(rs.getString("lastUpgradedOn"));

				requestInfoList1.add(request);
			}
		} catch (SQLException exe) {
			logger.error("SQL Exception in searchRequestsFromDBWithVersion method " + exe.getMessage());
		} finally {
			DBUtil.close(rs);
		}

		return requestInfoList1;

	}

	public List<RequestInfoSO> getDatasToCompareForRequestfromDB(String id) {
		List<RequestInfoSO> list = null;
		String query = "SELECT * FROM requestinfoso WHERE alphanumeric_req_id = ? ORDER BY date_of_processing DESC LIMIT 0,2";
		RequestInfoSO request;
		ResultSet rs = null;
		int id1;
		try (Connection connection = ConnectionFactory.getConnection();
				PreparedStatement ps = connection.prepareStatement(query);) {
			list = new ArrayList<RequestInfoSO>();
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

				request.setMisArPeSO(getMisArPeSO(id1));
				request.setInternetLcVrf(getInternetLcVrf(id1));
				request.setDeviceInterfaceSO(getDeviceInterfaceSO(id1));
				list.add(request);
			}
		} catch (SQLException exe) {
			logger.error("SQL Exception in getDatasToCompareForRequestfromDB method " + exe.getMessage());
		} catch (ParseException exe) {
			logger.error("ParseException in getDatasToCompareForRequestfromDB method " + exe.getMessage());
		} finally {
			DBUtil.close(rs);
		}
		return list;
	}

	public List<ModifyConfigResultPojo> getConfigCmdRecordFordata(RequestInfoSO requestInfoSO, String key) {
		String query = "SELECT * FROM createsshconfig WHERE Vendor=? AND Device_Type=? AND Model=? AND OS=? AND OS_Version=? AND Assigned_Field_Name=?";
		ResultSet rs = null;
		ModifyConfigResultPojo configCmdPojo = null;
		List<ModifyConfigResultPojo> configCmdList = null;
		try (Connection connection = ConnectionFactory.getConnection();
				PreparedStatement ps = connection.prepareStatement(query);) {
			configCmdList = new ArrayList<ModifyConfigResultPojo>();

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
		} catch (SQLException exe) {
			logger.error("SQL Exception in getConfigCmdRecordFordata method " + exe.getMessage());
		} finally {
			DBUtil.close(rs);
		}
		return configCmdList;
	}

	public String getLogedInUserDetail() {
		String query = "SELECT * FROM users WHERE user_status=?";
		ResultSet rs = null;
		UserPojo user = null;
		try (Connection connection = ConnectionFactory.getConnection();
				PreparedStatement ps = connection.prepareStatement(query);) {
			ps.setInt(1, 1);
			rs = ps.executeQuery();
			user = new UserPojo();
			while (rs.next()) {
				user.setUsername(rs.getString("user_name"));
				user.setPassword(rs.getString("user_password"));
			}
		} catch (SQLException exe) {
			logger.error("SQL Exception in getLogedInUserDetail method " + exe.getMessage());
		} finally {
			DBUtil.close(rs);
		}

		return user.getUsername();
	}

	public boolean resetUsersDB(String username) {
		boolean result = false;
		String query = "update users set user_status=0 WHERE user_name=?";
		try (Connection connection = ConnectionFactory.getConnection();
				PreparedStatement ps = connection.prepareStatement(query);) {
			ps.setString(1, username);
			int i = ps.executeUpdate();
			if (i > 0) {
				result = true;
			}
		} catch (SQLException exe) {
			logger.error("SQL Exception in resetUsersDB method " + exe.getMessage());
		}
		return result;
	}

	public int getTotalRequestsFromDB() {
		int num = 0;
		ResultSet rs = null;
		String query = "SELECT COUNT(request_info_id) AS total FROM requestinfoso";
		try (Connection connection = ConnectionFactory.getConnection();
				PreparedStatement ps = connection.prepareStatement(query);) {
			rs = ps.executeQuery();
			while (rs.next()) {
				num = rs.getInt("total");
			}
		} catch (SQLException exe) {
			logger.error("SQL Exception in getTotalRequestsFromDB method " + exe.getMessage());
		} finally {
			DBUtil.close(rs);
		}
		return num;
	}

	public int getSuccessRequestsFromDB() {
		int num = 0;
		String query = null;
		ResultSet rs = null;
		if (Global.loggedInUser.equalsIgnoreCase("seuser")) {
			query = "SELECT COUNT(request_info_id) AS total FROM requestinfoso Where request_status = 'success' and request_creator_name=? and alphanumeric_req_id rlike'SR|OS'";
		} else if (Global.loggedInUser.equalsIgnoreCase("feuser")) {
			query = "SELECT COUNT(request_info_id) AS total FROM requestinfoso Where request_status = 'success' and RequestOwner=? and alphanumeric_req_id rlike'SR|OS'";

		} else if (Global.loggedInUser.equalsIgnoreCase("admin")) {
			query = "SELECT COUNT(request_info_id) AS total FROM requestinfoso Where request_status = 'success' and alphanumeric_req_id rlike'SR|OS'";

		}
		try (Connection connection = ConnectionFactory.getConnection();
				PreparedStatement ps = connection.prepareStatement(query);) {
			if (!Global.loggedInUser.equalsIgnoreCase("admin")) {
				ps.setString(1, Global.loggedInUser);
			}
			rs = ps.executeQuery();
			while (rs.next()) {
				num = rs.getInt("total");
			}
		} catch (SQLException exe) {
			logger.error("SQL Exception in getSuccessRequestsFromDB method " + exe.getMessage());
		} finally {
			DBUtil.close(rs);
		}
		return num;
	}

	public int getFailureRequestsFromDB() {
		int num = 0;
		String query = null;
		ResultSet rs = null;
		if (Global.loggedInUser.equalsIgnoreCase("seuser")) {
			query = "SELECT COUNT(request_info_id) AS total FROM requestinfoso Where request_status = 'failure' and request_creator_name=? and alphanumeric_req_id rlike'SR|OS'";

		} else if (Global.loggedInUser.equalsIgnoreCase("feuser")) {
			query = "SELECT COUNT(request_info_id) AS total FROM requestinfoso Where request_status = 'failure' and RequestOwner=? and alphanumeric_req_id rlike'SR|OS'";

		} else if (Global.loggedInUser.equalsIgnoreCase("admin")) {
			query = "SELECT COUNT(request_info_id) AS total FROM requestinfoso Where request_status = 'failure' and alphanumeric_req_id rlike'SR|OS'";

		}
		try (Connection connection = ConnectionFactory.getConnection();
				PreparedStatement ps = connection.prepareStatement(query);) {
			if (!Global.loggedInUser.equalsIgnoreCase("admin")) {
				ps.setString(1, Global.loggedInUser);
			}
			rs = ps.executeQuery();
			while (rs.next()) {
				num = rs.getInt("total");
			}
		} catch (SQLException exe) {
			logger.error("SQL Exception in getFailureRequestsFromDB method " + exe.getMessage());
		} finally {
			DBUtil.close(rs);
		}
		return num;
	}

	public int getInProgressRequestsFromDB() {
		int num = 0;
		ResultSet rs = null;
		String query = null;
		if (Global.loggedInUser.equalsIgnoreCase("seuser")) {
			query = "SELECT COUNT(request_info_id) AS total FROM requestinfoso Where request_status = 'In Progress' and request_creator_name=? and alphanumeric_req_id rlike'SR|OS'";
		} else if (Global.loggedInUser.equalsIgnoreCase("feuser")) {
			query = "SELECT COUNT(request_info_id) AS total FROM requestinfoso Where request_status = 'In Progress' and RequestOwner=? and alphanumeric_req_id rlike'SR|OS'";

		} else if (Global.loggedInUser.equalsIgnoreCase("admin")) {
			query = "SELECT COUNT(request_info_id) AS total FROM requestinfoso Where request_status = 'In Progress' and alphanumeric_req_id rlike'SR|OS'";

		}

		try (Connection connection = ConnectionFactory.getConnection();
				PreparedStatement ps = connection.prepareStatement(query);) {
			if (!Global.loggedInUser.equalsIgnoreCase("admin")) {
				ps.setString(1, Global.loggedInUser);
			}
			rs = ps.executeQuery();
			while (rs.next()) {
				num = rs.getInt("total");
			}
		} catch (SQLException exe) {
			logger.error("SQL Exception in getInProgressRequestsFromDB method " + exe.getMessage());
		} finally {
			DBUtil.close(rs);
		}
		return num;
	}

	public final boolean updateEIPAMRecord(String customer, String site, String ip, String mask) {
		boolean result = false;
		String query = "update eipamdbtable set eipam_ip=?,eipam_subnet_mask=?,eipam_ip_status=? WHERE eipam_customer_name=? AND eipam_site_id=?";
		try (Connection connection = ConnectionFactory.getConnection();
				PreparedStatement ps = connection.prepareStatement(query);) {
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
		} catch (SQLException exe) {
			logger.error("SQL Exception in updateEIPAMRecord method " + exe.getMessage());
		}
		return result;
	}

	public final boolean addEIPAMRecord(String customer, String site, String ip, String mask, String service,
			String region) {
		boolean result = false;
		String query = "INSERT INTO eipamdbtable(eipam_ip,eipam_subnet_mask,eipam_ip_status,eipam_customer_name,eipam_site_id,eipam_region,eipam_service)"
				+ "VALUES(?,?,?,?,?,?,?)";
		try (Connection connection = ConnectionFactory.getConnection();
				PreparedStatement ps = connection.prepareStatement(query);) {

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
		} catch (SQLException exe) {
			logger.error("SQL Exception in addEIPAMRecord method " + exe.getMessage());
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

		logger.info("we are here" + milliseconds);
		return String.format("%02d.%02d", TimeUnit.MILLISECONDS.toMinutes(milliseconds),
				TimeUnit.MILLISECONDS.toSeconds(milliseconds)
						- TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(milliseconds)));
	}

	public Map<String, String> getRequestFlag(String requestId, double version) {
		String query = "select application_test,deliever_config from  webserviceinfo where alphanumeric_req_id = ? and version = ? ";
		ResultSet rs = null;
		String flagForPrevalidation = "";
		String flagFordelieverConfig = "";
		Map<String, String> hmap = new HashMap<String, String>();
		DecimalFormat numberFormat = new DecimalFormat("#.0");
		String parentVersion = numberFormat.format(version - 0.1);
		try (Connection connection = ConnectionFactory.getConnection();
				PreparedStatement preparedStmt = connection.prepareStatement(query);) {
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
		} catch (SQLException exe) {
			logger.error("SQL Exception in addEIPAMRecord method " + exe.getMessage());
		} finally {
			DBUtil.close(rs);
		}
		return hmap;
	}

	public boolean addCertificationTestForRequest(String alphanumeric_req_id, String request_version,
			String Device_Reachability_Test) {
		String query = "INSERT INTO certificationtestvalidation(Device_Reachability_Test,Vendor_Test,Device_Model_Test,IOSVersion_Test,PreValidation_Test,ShowIpIntBrief_Cmd,ShowInterface_Cmd,ShowVersion_Cmd,Network_Test,showIpBgpSummary_Cmd,Throughput_Test,FrameLoss_Test,Latency_Test,HealthCheck_Test,alphanumeric_req_id,version,suggestionForFailure)"
				+ "VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
		String suggestion = "NA";
		if (Device_Reachability_Test.equalsIgnoreCase("2")) {
			suggestion = "Please check the device connectivity";
		}
		if (Device_Reachability_Test.equalsIgnoreCase("2_Authentication")) {
			Device_Reachability_Test = "2";
			suggestion = "Please check the router credentials";
		}

		try (Connection connection = ConnectionFactory.getConnection();
				PreparedStatement ps = connection.prepareStatement(query);) {
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
		String query = null;
		String suggestion = "NA";
		query = "update certificationtestvalidation set Vendor_Test = ?,IOSVersion_Test = ?,Device_Model_Test  = ?,suggestionForFailure =? where alphanumeric_req_id = ? and version = ? ";
		if (vendorflag == 2) {
			suggestion = "Please select the correct Vendor from C3P GUI";
		}
		if (versionflag == 2) {
			suggestion = "Please select the correct IOS Version from C3P GUI";
		}
		if (modelflag == 2) {
			suggestion = "Please select the correct router model from C3P GUI";
		}
		try (Connection connection = ConnectionFactory.getConnection();
				PreparedStatement preparedStmt = connection.prepareStatement(query);) {
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
		String query = "update certificationtestvalidation set actual_vendor = ?,gui_vendor = ?,actual_os_version  = ?,gui_os_version =?,actual_model=?,gui_model=? where alphanumeric_req_id = ? and version = ? ";

		try (Connection connection = ConnectionFactory.getConnection();
				PreparedStatement preparedStmt = connection.prepareStatement(query);) {
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
		CertificationTestPojo certificationTestPojo = new CertificationTestPojo();
		String query = "select * from  certificationtestvalidation where alphanumeric_req_id = ? and version = ? ";
		ResultSet rs = null;
		try (Connection connection = ConnectionFactory.getConnection();
				PreparedStatement preparedStmt = connection.prepareStatement(query);) {
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
		} catch (SQLException exe) {
			logger.error("SQL Exception in getCertificationTestFlagData method " + exe.getMessage());
		} finally {
			DBUtil.close(rs);
		}
		return certificationTestPojo;
	}

	public void updateHealthCheckTestStatus(String requestId, String version, int throughputflag, int framelossflag,
			int latencyflag) {
		String query = "update certificationtestvalidation set Throughput_Test = ?,FrameLoss_Test = ?,Latency_Test  = ? where alphanumeric_req_id = ? and version = ? ";

		try (Connection connection = ConnectionFactory.getConnection();
				PreparedStatement preparedStmt = connection.prepareStatement(query);) {
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
		String query = "update certificationtestvalidation set Throughput_Test = ?,FrameLoss_Test = ?,Latency_Test  = ? where alphanumeric_req_id = ? and version = ? ";
		try (Connection connection = ConnectionFactory.getConnection();
				PreparedStatement preparedStmt = connection.prepareStatement(query);) {
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
		String query = null;
		if (type.equalsIgnoreCase("frameloss")) {
			query = "update certificationtestvalidation set frameloss = ? where alphanumeric_req_id = ? and version = ? ";
		} else if (type.equalsIgnoreCase("latency")) {
			query = "update certificationtestvalidation set latency = ? where alphanumeric_req_id = ? and version = ? ";
		} else {
			query = "update certificationtestvalidation set throughput = ? where alphanumeric_req_id = ? and version = ? ";
		}
		try (Connection connection = ConnectionFactory.getConnection();
				PreparedStatement preparedStmt = connection.prepareStatement(query);) {
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
		String query = "update certificationtestvalidation set ShowIpIntBrief_Cmd = ?,ShowInterface_Cmd = ?,ShowVersion_Cmd  = ?,showIpBgpSummary_Cmd=? where alphanumeric_req_id = ? and version = ? ";

		try (Connection connection = ConnectionFactory.getConnection();
				PreparedStatement preparedStmt = connection.prepareStatement(query);) {
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
		ResultSet rs = null;
		String query = "select request_status,date_of_processing,request_creator_name,WEEKDAY(date_of_processing) AS DAYS from requestinfoso join (select alphanumeric_req_id, max(date_of_processing) as max_dt,WEEKDAY(date_of_processing) AS DAYS from requestinfoso WHERE YEARWEEK(date_of_processing)=YEARWEEK(NOW()) group by alphanumeric_req_id)t on requestinfoso.alphanumeric_req_id= t.alphanumeric_req_id and requestinfoso.date_of_processing = t.max_dt";
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

		try (Connection connection = ConnectionFactory.getConnection();
				PreparedStatement ps = connection.prepareStatement(query);) {
			int count = 0;
			rs = ps.executeQuery();
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

		} catch (SQLException exe) {
			logger.error("SQL Exception in getColumnChartData method " + exe.getMessage());
		} finally {
			DBUtil.close(rs);
		}
		return resultJSONArray;
	}

	public List<ConfigurationDataValuePojo> getALLVendorData() {
		String query = "SELECT * FROM c3p_data_configuration_table where component_name='c3p_vendor'";
		ConfigurationDataValuePojo object = null;
		ResultSet rs = null;
		List<ConfigurationDataValuePojo> list = null;
		try (Connection connection = ConnectionFactory.getConnection();
				PreparedStatement ps = connection.prepareStatement(query);) {
			rs = ps.executeQuery();
			list = new ArrayList<ConfigurationDataValuePojo>();
			while (rs.next()) {
				object = new ConfigurationDataValuePojo();
				object.setComponent_value(rs.getString("component_value"));
				object.setComponent_make(rs.getString("component_make"));
				list.add(object);
			}
		} catch (SQLException exe) {
			logger.error("SQL Exception in getALLVendorData method " + exe.getMessage());
		} finally {
			DBUtil.close(rs);
		}
		return list;
	}

	public List<ConfigurationDataValuePojo> getALLDeviceTypeData() {
		String query = "SELECT * FROM c3p_data_configuration_table where component_name='c3p_device_type'";
		ResultSet rs = null;
		List<ConfigurationDataValuePojo> list = null;
		ConfigurationDataValuePojo object;
		try (Connection connection = ConnectionFactory.getConnection();
				PreparedStatement ps = connection.prepareStatement(query);) {
			rs = ps.executeQuery();
			list = new ArrayList<ConfigurationDataValuePojo>();
			while (rs.next()) {
				object = new ConfigurationDataValuePojo();
				object.setComponent_make(rs.getString("component_make"));
				object.setComponent_value(rs.getString("component_value"));
				list.add(object);
			}
		} catch (SQLException exe) {
			logger.error("SQL Exception in getALLDeviceTypeData method " + exe.getMessage());
		} finally {
			DBUtil.close(rs);
		}
		return list;
	}

	public List<String> getALLModelData(String vendor, String deviceType) throws IOException {
		String query = "SELECT * FROM c3p_data_configuration_table where component_name=? AND component_make=?";
		List<String> list = new ArrayList<String>();
		ResultSet rs = null;
		try (Connection connection = ConnectionFactory.getConnection();
				PreparedStatement ps = connection.prepareStatement(query);) {
			ps.setString(1, "c3p_" + deviceType.toLowerCase() + "_model");
			ps.setString(2, vendor);

			rs = ps.executeQuery();
			while (rs.next()) {
				list.add(rs.getString("component_value"));
			}
		} catch (SQLException exe) {
			logger.error("SQL Exception in getALLModelData method " + exe.getMessage());
		} finally {
			DBUtil.close(rs);
		}
		return list;
	}

	public List<String> getALLOSData(String make, String deviceType) throws IOException {
		String query = "SELECT * FROM c3p_data_configuration_table where component_name='c3p_os_type'";
		List<String> list = new ArrayList<String>();
		ResultSet rs = null;
		try (Connection connection = ConnectionFactory.getConnection();
				PreparedStatement ps = connection.prepareStatement(query);) {
			rs = ps.executeQuery();
			list = new ArrayList<String>();
			while (rs.next()) {
				if (rs.getString("component_make").equalsIgnoreCase(make)) {
					list.add(rs.getString("component_value"));
				}
			}
		} catch (SQLException exe) {
			logger.error("SQL Exception in getALLOSData method " + exe.getMessage());
		} finally {
			DBUtil.close(rs);
		}
		return list;

	}

	public final List<String> getALLOSVersionData(String os, String model) throws IOException {
		String query = "SELECT * FROM c3p_data_configuration_table where component_name='c3p_os_version'";
		List<String> list = new ArrayList<String>();
		List<String> listtoSend = new ArrayList<String>();
		ResultSet rs = null;
		try (Connection connection = ConnectionFactory.getConnection();
				PreparedStatement ps = connection.prepareStatement(query);) {
			rs = ps.executeQuery();
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
		} catch (SQLException exe) {
			logger.error("SQL Exception in getALLOSVersionData method " + exe.getMessage());
		} finally {
			DBUtil.close(rs);
		}
		return listtoSend;

	}

	public JSONArray getColumnChartDataMonthly() {
		ResultSet rs = null;
		String query = "select request_status,date_of_processing,dayofmonth(date_of_processing) AS DAYS from requestinfoso where month(date_of_processing) = 12";
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

		try (Connection connection = ConnectionFactory.getConnection();
				PreparedStatement ps = connection.prepareStatement(query);) {
			int count = 0;
			rs = ps.executeQuery();
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

		} catch (SQLException exe) {
			logger.error("SQL Exception in getColumnChartDataMonthly method " + exe.getMessage());
		} finally {
			DBUtil.close(rs);
		}
		return resultJSONArray;
	}

	public final List<ConfigurationDataValuePojo> getALLRegionData() throws IOException {
		String query = "SELECT * FROM c3p_data_configuration_table where component_name='c3p_region'";
		ResultSet rs = null;
		List<ConfigurationDataValuePojo> list = null;
		try (Connection connection = ConnectionFactory.getConnection();
				PreparedStatement ps = connection.prepareStatement(query);) {
			ConfigurationDataValuePojo object;
			rs = ps.executeQuery();
			list = new ArrayList<ConfigurationDataValuePojo>();
			while (rs.next()) {
				object = new ConfigurationDataValuePojo();
				object.setComponent_value(rs.getString("component_value"));
				object.setComponent_make(rs.getString("component_make"));
				list.add(object);
			}
		} catch (SQLException exe) {
			logger.error("SQL Exception in getALLRegionData method " + exe.getMessage());
		} finally {
			DBUtil.close(rs);
		}
		return list;

	}

	public final List<ErrorValidationPojo> getAllErrorCodeFromRouter() {
		String query = "select * from errorcodedata where router_error_message is not null";
		ResultSet rs = null;
		List<ErrorValidationPojo> list = null;
		try (Connection connection = ConnectionFactory.getConnection();
				PreparedStatement ps = connection.prepareStatement(query);) {
			ErrorValidationPojo object;
			rs = ps.executeQuery();
			list = new ArrayList<ErrorValidationPojo>();
			while (rs.next()) {
				object = new ErrorValidationPojo();
				object.setError_type(rs.getString("ErrorType"));
				object.setError_description(rs.getString("ErrorDescription"));
				object.setRouter_error_message(rs.getString("router_error_message"));
				list.add(object);
			}
		} catch (SQLException exe) {
			logger.error("SQL Exception in getAllErrorCodeFromRouter method " + exe.getMessage());
		} finally {
			DBUtil.close(rs);
		}
		return list;
	}

	public void updateErrorDetailsDeliveryTestForRequestId(String RequestId, String version, String textFound,
			String errorType, String errorDescription) {
		String query = "update webserviceinfo set TextFound_DeliveryTest = ?,ErrorStatus_DeliveryTest=?,ErrorDescription_DeliveryTest=? where alphanumeric_req_id = ? and version = ?";
		String errorQuery = "select suggestion from errorcodedata where ErrorDescription = ?";
		String updateQuery = "update certificationtestvalidation set suggestionForFailure = ? where alphanumeric_req_id = ? and version = ?";
		ResultSet rs = null;
		String suggestionForErrorDesc = "";
		try (Connection connection = ConnectionFactory.getConnection();
				PreparedStatement preparedStmt = connection.prepareStatement(query);) {
			preparedStmt.setString(1, textFound);
			preparedStmt.setString(2, errorType);
			preparedStmt.setString(3, errorDescription);
			preparedStmt.setString(4, RequestId);
			preparedStmt.setString(5, version);
			preparedStmt.executeUpdate();
			try (PreparedStatement errorPs = connection.prepareStatement(errorQuery);) {
				errorPs.setString(1, errorDescription);
				rs = errorPs.executeQuery();
				while (rs.next()) {
					suggestionForErrorDesc = (rs.getString("suggestion"));
				}
			} catch (SQLException e) {
				logger.error(e.getMessage());
			}

			try (PreparedStatement updatePs = connection.prepareStatement(updateQuery);) {
				updatePs.setString(1, suggestionForErrorDesc);
				updatePs.setString(2, RequestId);
				updatePs.setString(3, version);
				updatePs.executeUpdate();
			} catch (SQLException e) {
				logger.error(e.getMessage());
			}
		} catch (SQLException exe) {
			logger.error("SQL Exception in updateErrorDetailsDeliveryTestForRequestId method " + exe.getMessage());
		} finally {
			DBUtil.close(rs);
		}
	}

	public final List<ModifyConfigResultPojo> getNoConfigCmdForPreviousConfig() {
		String query = "select No_SSH_Command from createsshconfig";
		ResultSet rs = null;
		ModifyConfigResultPojo configCmdPojo = null;
		List<ModifyConfigResultPojo> configCmdList = null;
		try (Connection connection = ConnectionFactory.getConnection();
				PreparedStatement ps = connection.prepareStatement(query);) {
			configCmdList = new ArrayList<ModifyConfigResultPojo>();
			rs = ps.executeQuery();
			while (rs.next()) {
				configCmdPojo = new ModifyConfigResultPojo();
				configCmdPojo.setNo_SSH_Command(rs.getString("No_SSH_Command"));
				configCmdList.add(configCmdPojo);
			}
		} catch (SQLException exe) {
			logger.error("SQL Exception in updateErrorDetailsDeliveryTestForRequestId method " + exe.getMessage());
		} finally {
			DBUtil.close(rs);
		}

		return configCmdList;
	}

	public ErrorValidationPojo getErrordetailsForRequestId(String requestId, String version) {
		ErrorValidationPojo errorValidationPojo = new ErrorValidationPojo();
		String query = "Select * from webserviceinfo  where alphanumeric_req_id = ? and version = ?";
		ResultSet rs = null;
		try (Connection connection = ConnectionFactory.getConnection();
				PreparedStatement pst = connection.prepareStatement(query);) {
			pst.setString(1, requestId);
			pst.setString(2, version);

			rs = pst.executeQuery();
			while (rs.next()) {
				errorValidationPojo.setRouter_error_message(rs.getString("TextFound_DeliveryTest"));
				errorValidationPojo.setError_type(rs.getString("ErrorStatus_DeliveryTest"));
				errorValidationPojo.setError_description(rs.getString("ErrorDescription_DeliveryTest"));
				errorValidationPojo.setDelivery_status(rs.getString("deliever_config"));
			}
		} catch (SQLException exe) {
			logger.error("SQL Exception in updateErrorDetailsDeliveryTestForRequestId method " + exe.getMessage());
		} finally {
			DBUtil.close(rs);
		}
		return errorValidationPojo;
	}

	public CreateConfigRequest getRequestDetailFromDBForVersion(String value, String version) {
		String query = "SELECT * FROM requestinfoso WHERE alphanumeric_req_id = ? AND request_version = ?";

		ResultSet rs = null;
		CreateConfigRequest request = new CreateConfigRequest();
		try (Connection connection = ConnectionFactory.getConnection();
				PreparedStatement pst = connection.prepareStatement(query);) {
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
				request.setRequest_version(rs.getInt("request_version"));
				request.setRequest_parent_version(rs.getInt("request_parent_version"));
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
				MisArPeSO misArPeSo = getMisArPeSO(id);
				InternetLcVrfSO internetLcVrfSO = getInternetLcVrf(id);
				DeviceInterfaceSO deviceInterfaceSO = getDeviceInterfaceSO(id);
				MISARPEType misaRPEType = new MISARPEType();
				misaRPEType.setFastEthernetIp(misArPeSo.getFastEthernetIp());
				misaRPEType.setRouterVrfVpnDGateway(misArPeSo.getRouterVrfVpnDGateway());
				misaRPEType.setRouterVrfVpnDIp(misArPeSo.getRouterVrfVpnDIp());
				request.setMisArPe(misaRPEType);

				InternetLCVRFType iis = new InternetLCVRFType();
				iis.setNetworkIp(internetLcVrfSO.getNetworkIp());
				iis.setAS(internetLcVrfSO.getBgpASNumber());
				iis.setNeighbor1(internetLcVrfSO.getNeighbor1());
				iis.setNeighbor2(internetLcVrfSO.getNeighbor2());
				iis.setneighbor1_remoteAS(internetLcVrfSO.getNeighbor1_remoteAS());
				iis.setneighbor2_remoteAS(internetLcVrfSO.getNeighbor2_remoteAS());
				iis.setroutingProtocol(internetLcVrfSO.getRoutingProtocol());
				iis.setnetworkIp_subnetMask(internetLcVrfSO.getNetworkIp_subnetMask());
				request.setInternetLcVrf(iis);

				Interface iisd = new Interface();
				iisd.setDescription(deviceInterfaceSO.getDescription());
				iisd.setIp(deviceInterfaceSO.getIp());
				iisd.setEncapsulation(deviceInterfaceSO.getEncapsulation());
				iisd.setMask(deviceInterfaceSO.getMask());
				iisd.setName(deviceInterfaceSO.getName());
				iisd.setSpeed(deviceInterfaceSO.getSpeed());
				iisd.setBandwidth(deviceInterfaceSO.getBandwidth());
				request.setC3p_interface(iisd);
				request.setRequest_assigned_to(rs.getString("RequestOwner"));

			}
		} catch (SQLException exe) {
			logger.error("SQL Exception in getRequestDetailFromDBForVersion method " + exe.getMessage());
		} finally {
			DBUtil.close(rs);
		}
		return request;
	}

	public boolean checkForDeviceLockWithManagementIp(String requestId, String managementIp, String TestType) {
		String query = null;
		if (TestType.equalsIgnoreCase("DeviceTest")) {
			query = "Select * from devicelocked_managementip  where management_ip = ?";
		} else {
			query = "Select * from devicelocked_managementip  where management_ip = ? and locked_by = ?";
		}
		ResultSet rs = null;
		boolean devicelocked = false;
		try (Connection connection = ConnectionFactory.getConnection();
				PreparedStatement pst = connection.prepareStatement(query);) {
			if (TestType.equalsIgnoreCase("DeviceTest")) {
				pst.setString(1, managementIp);
			} else {
				pst.setString(1, managementIp);
				pst.setString(2, requestId);
			}
			rs = pst.executeQuery();
			while (rs.next()) {
				devicelocked = true;
			}

		} catch (SQLException exe) {
			logger.error("SQL Exception in getRequestDetailFromDBForVersion method " + exe.getMessage());
		} finally {
			DBUtil.close(rs);
		}
		return devicelocked;
	}

	public String lockDeviceForRequest(String managementIp, String RequestId) {
		String result = "";
		String query = "INSERT INTO devicelocked_managementip(management_ip,locked_by,flag)" + "VALUES(?,?,?)";
		try (Connection connection = ConnectionFactory.getConnection();
				PreparedStatement preparedStmt = connection.prepareStatement(query);) {
			preparedStmt.setString(1, managementIp);
			preparedStmt.setString(2, RequestId);
			preparedStmt.setString(3, "Y");

			preparedStmt.executeUpdate();
			result = "Success";
		} catch (SQLException exe) {
			logger.error("SQL Exception in lockDeviceForRequest method " + exe.getMessage());
			result = "Failure";
		}
		return result;
	}

	public String releaselockDeviceForRequest(String managementIp, String RequestId) {
		String result = "";
		String query = "delete from devicelocked_managementip where management_ip = ? and locked_by = ? ";
		try (Connection connection = ConnectionFactory.getConnection();
				PreparedStatement preparedStmt = connection.prepareStatement(query);) {
			preparedStmt.setString(1, managementIp);
			preparedStmt.setString(2, RequestId);

			preparedStmt.executeUpdate();
			result = "Success";
		} catch (SQLException exe) {
			logger.error("SQL Exception in lockDeviceForRequest method " + exe.getMessage());
			result = "Failure";
		}
		return result;
	}

	public Map<String, String> getRequestFlagForReport(String requestId, double versionId) {
		String query = "select application_test,deliever_config from  webserviceinfo where alphanumeric_req_id = ? and version = ? ";
		ResultSet rs = null;
		String flagForPrevalidation = "";
		String flagFordelieverConfig = "";
		Map<String, String> hmap = new HashMap<String, String>();
		DecimalFormat numberFormat = new DecimalFormat("#.0");
		String version = numberFormat.format(versionId);
		try (Connection connection = ConnectionFactory.getConnection();
				PreparedStatement preparedStmt = connection.prepareStatement(query);) {
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
		} catch (SQLException exe) {
			logger.error("SQL Exception in lockDeviceForRequest method " + exe.getMessage());
		} finally {
			DBUtil.close(rs);
		}
		return hmap;
	}

	public boolean changeRequestOwner(String requestid, String version, String owner) {
		boolean result = false;
		String query = "update c3p_t_request_info set r_request_owner = ? , r_read_fe=?, r_read_se=? where r_alphanumeric_req_id = ? and r_request_version= ?";
		try (Connection connection = ConnectionFactory.getConnection();
				PreparedStatement preparedStmt = connection.prepareStatement(query);) {
			preparedStmt.setString(1, owner);
			if (owner.equalsIgnoreCase("seuser")) {
				preparedStmt.setInt(2, 1);
				preparedStmt.setInt(3, 0);
			} else if (owner.equalsIgnoreCase("feuser")) {
				preparedStmt.setInt(2, 0);
				preparedStmt.setInt(3, 1);
			} else if (owner.equalsIgnoreCase("admin")) {
				preparedStmt.setInt(2, 1);
				preparedStmt.setInt(3, 0);
			}
			preparedStmt.setString(4, requestid);
			preparedStmt.setString(5, version);

			int res = preparedStmt.executeUpdate();

			if (res != 0) {
				result = true;
			} else {
				result = false;
			}
		} catch (SQLException exe) {
			logger.error("SQL Exception in changeRequestOwner method " + exe.getMessage());
		}
		return result;
	}

	public String getUserTaskIdForRequest(String requestId, String version) {
		String usertaskid = null;
		String query = "select * from  camundahistory where history_requestId=? and history_versionId=?";
		try (Connection connection = ConnectionFactory.getConnection();
				PreparedStatement pst = connection.prepareStatement(query);) {
			pst.setString(1, requestId);
			pst.setString(2, version);
			ResultSet res = pst.executeQuery();
			while (res.next()) {
				usertaskid = res.getString("history_userTaskId");
			}
		} catch (SQLException exe) {
			logger.error("SQL Exception in changeRequestOwner method " + exe.getMessage());
		}
		return usertaskid;
	}

	public boolean changeRequestStatus(String requestid, String version, String status) {
		boolean result = false;
		String query = "update c3p_t_request_info set r_status = ?, r_end_date_of_processing= now() where r_alphanumeric_req_id = ? and r_request_version= ?";
		try (Connection connection = ConnectionFactory.getConnection();
				PreparedStatement pst = connection.prepareStatement(query);) {
			pst.setString(1, status);
			pst.setString(2, requestid);
			pst.setString(3, version);
			int val = pst.executeUpdate();
			if (val > 0) {
				result = true;
			} else {
				result = false;
			}
		} catch (SQLException exe) {
			logger.error("SQL Exception in changeRequestStatus method " + exe.getMessage());
		}
		return result;
	}

	public List<RequestInfoSO> getFEAssignedRequestList() {
		List<RequestInfoSO> list = new ArrayList<RequestInfoSO>();
		String query = "select * from  requestinfoso where RequestOwner=?";
		try (Connection connection = ConnectionFactory.getConnection();
				PreparedStatement pst = connection.prepareStatement(query);) {
			RequestInfoSO request = null;
			pst.setString(1, "feuser");
			ResultSet rs = pst.executeQuery();
			int id;
			while (rs.next()) {
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
				} else {
					request.setElapsedTime("00:00:00");
				}

				Timestamp d1 = rs.getTimestamp("end_date_of_processing");
				request.setEndDateofProcessing((covnertTStoString(d1)));

				request.setMisArPeSO(getMisArPeSO(id));
				request.setInternetLcVrf(getInternetLcVrf(id));
				request.setDeviceInterfaceSO(getDeviceInterfaceSO(id));
				list.add(request);
			}
		} catch (SQLException exe) {
			logger.error("SQL Exception in changeRequestStatus method " + exe.getMessage());
		} catch (ParseException exe) {
			logger.error("Parse Exception in changeRequestStatus method " + exe.getMessage());
		}
		return list;
	}

	public List<RequestInfoSO> getSEAssignedRequestList() {
		List<RequestInfoSO> list = new ArrayList<RequestInfoSO>();
		ResultSet rs = null;
		String query = "select * from  requestinfoso where RequestOwner=? and request_status  IN (?,?)";
		try (Connection connection = ConnectionFactory.getConnection();
				PreparedStatement pst = connection.prepareStatement(query);) {
			RequestInfoSO request = null;
			pst.setString(1, "seuser");
			pst.setString(2, "Hold");
			pst.setString(3, "In Progress");

			rs = pst.executeQuery();
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

				request.setMisArPeSO(getMisArPeSO(id));
				request.setInternetLcVrf(getInternetLcVrf(id));
				request.setDeviceInterfaceSO(getDeviceInterfaceSO(id));
				list.add(request);
			}
		} catch (SQLException exe) {
			logger.error("SQL Exception in changeRequestStatus method " + exe.getMessage());
		} catch (ParseException exe) {
			logger.error("Parse Exception in changeRequestStatus method " + exe.getMessage());
		}
		return list;
	}

	public List<RequestInfoSO> getAdminAssignedRequestList() {
		List<RequestInfoSO> list = new ArrayList<RequestInfoSO>();
		String query = "select * from  requestinfoso where RequestOwner=? and request_status  IN (?,?)";
		ResultSet rs = null;
		try (Connection connection = ConnectionFactory.getConnection();
				PreparedStatement pst = connection.prepareStatement(query);) {
			RequestInfoSO request = null;
			pst.setString(1, "admin");
			pst.setString(2, "Hold");
			pst.setString(3, "In Progress");

			rs = pst.executeQuery();
			int id;
			while (rs.next()) {
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

				request.setMisArPeSO(getMisArPeSO(id));
				request.setInternetLcVrf(getInternetLcVrf(id));
				request.setDeviceInterfaceSO(getDeviceInterfaceSO(id));
				list.add(request);
			}
		} catch (SQLException exe) {
			logger.error("SQL Exception in getAdminAssignedRequestList method " + exe.getMessage());
		} catch (ParseException exe) {
			logger.error("Parse Exception in getAdminAssignedRequestList method " + exe.getMessage());
		}

		return list;
	}

	public void setReadFlagFESE(String requestId, String version, int status, String key) {
		String query = null;

		if (key.equalsIgnoreCase("FE")) {
			query = "update c3p_t_request_info set r_read_fe = ? where r_alphanumeric_req_id = ? and r_request_version = ? ";
		} else if (key.equalsIgnoreCase("SE")) {
			query = "update c3p_t_request_info set r_read_se = ? where r_alphanumeric_req_id = ? and r_request_version = ? ";
		}

		try (Connection connection = ConnectionFactory.getConnection();
				PreparedStatement preparedStmt = connection.prepareStatement(query);) {
			preparedStmt.setInt(1, status);
			preparedStmt.setString(2, requestId);
			preparedStmt.setString(3, version);
			preparedStmt.executeUpdate();
		} catch (SQLException exe) {
			logger.error("SQL Exception in setReadFlagFESE method " + exe.getMessage());
		}
	}

	public int getScheduledRequestsFromDB() {
		int num = 0;
		ResultSet rs = null;
		String query = null;
		if (Global.loggedInUser.equalsIgnoreCase("seuser")) {
			query = "SELECT COUNT(request_info_id) AS total FROM requestinfoso Where request_status = 'Scheduled' and request_creator_name=? and alphanumeric_req_id rlike'SR|OS'";
		} else if (Global.loggedInUser.equalsIgnoreCase("feuser")) {
			query = "SELECT COUNT(request_info_id) AS total FROM requestinfoso Where request_status = 'Scheduled' and RequestOwner=? and alphanumeric_req_id rlike'SR|OS'";

		} else if (Global.loggedInUser.equalsIgnoreCase("admin")) {
			query = "SELECT COUNT(request_info_id) AS total FROM requestinfoso Where request_status = 'Scheduled' and alphanumeric_req_id rlike'SR|OS'";

		}
		try (Connection connection = ConnectionFactory.getConnection();
				PreparedStatement ps = connection.prepareStatement(query);) {
			if (!Global.loggedInUser.equalsIgnoreCase("admin")) {
				ps.setString(1, Global.loggedInUser);
			}
			rs = ps.executeQuery();
			while (rs.next()) {
				num = rs.getInt("total");
			}
		} catch (SQLException exe) {
			logger.error("SQL Exception in setReadFlagFESE method " + exe.getMessage());
		} finally {
			DBUtil.close(rs);
		}
		return num;
	}

	public int getHoldRequestsFromDB() {
		int num = 0;
		ResultSet rs = null;
		String query = null;
		if (Global.loggedInUser.equalsIgnoreCase("seuser")) {
			query = "SELECT COUNT(request_info_id) AS total FROM requestinfoso Where request_status = 'Hold' and request_creator_name=? and alphanumeric_req_id rlike'SR|OS'";
		} else if (Global.loggedInUser.equalsIgnoreCase("feuser")) {
			query = "SELECT COUNT(request_info_id) AS total FROM requestinfoso Where request_status = 'Hold' and RequestOwner=? and alphanumeric_req_id rlike'SR|OS'";
		} else if (Global.loggedInUser.equalsIgnoreCase("admin")) {
			query = "SELECT COUNT(request_info_id) AS total FROM requestinfoso Where request_status = 'Hold' and alphanumeric_req_id rlike'SR|OS'";
		}
		try (Connection connection = ConnectionFactory.getConnection();
				PreparedStatement ps = connection.prepareStatement(query);) {
			if (!Global.loggedInUser.equalsIgnoreCase("admin")) {
				ps.setString(1, Global.loggedInUser);
			}
			rs = ps.executeQuery();
			while (rs.next()) {
				num = rs.getInt("total");
			}
		} catch (SQLException exe) {
			logger.error("SQL Exception in setReadFlagFESE method " + exe.getMessage());
		} finally {
			DBUtil.close(rs);
		}
		return num;
	}

	public String getRequestOwner(String reqId, String version) {
		String owner = null;
		ResultSet rs = null;
		String query = "SELECT request_creator_name FROM requestinfoso where alphanumeric_req_id = ? and request_version = ?";
		try (Connection connection = ConnectionFactory.getConnection();
				PreparedStatement preparedStmt = connection.prepareStatement(query);) {
			preparedStmt.setString(1, reqId);
			preparedStmt.setString(2, version);
			rs = preparedStmt.executeQuery();
			while (rs.next()) {
				owner = rs.getString("request_creator_name");
			}
		} catch (SQLException exe) {
			logger.error("SQL Exception in getRequestOwner method " + exe.getMessage());
		} finally {
			DBUtil.close(rs);
		}

		return owner;
	}

	public final List<ModifyConfigResultPojo> getConfigCmdRecordFordataForDelivery(CreateConfigRequest configRequest,
			String key) {
		String query = "SELECT * FROM createsshconfig WHERE Vendor=? AND Device_Type=? AND Model=? AND OS=? AND OS_Version=? AND Assigned_Field_Name=?";
		ResultSet rs = null;
		ModifyConfigResultPojo configCmdPojo = null;
		List<ModifyConfigResultPojo> configCmdList = null;
		try (Connection connection = ConnectionFactory.getConnection();
				PreparedStatement ps = connection.prepareStatement(query);) {
			configCmdList = new ArrayList<ModifyConfigResultPojo>();
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
				configCmdList.add(configCmdPojo);
			}

		} catch (SQLException exe) {
			logger.error("SQL Exception in getConfigCmdRecordFordataForDelivery method " + exe.getMessage());
		} finally {
			DBUtil.close(rs);
		}
		return configCmdList;
	}

	public void resetErrorStateOfRechabilityTest(String requestId, String version) {
		String query = "update webserviceinfo set application_test = ? where alphanumeric_req_id = ? and version = ? ";
		String updatedQuery = "update certificationtestvalidation set Device_Reachability_Test = ? where alphanumeric_req_id = ? and version = ? ";
		try (Connection connection = ConnectionFactory.getConnection();
				PreparedStatement preparedStmt = connection.prepareStatement(query);) {
			preparedStmt.setString(1, "0");
			preparedStmt.setString(2, requestId);
			preparedStmt.setString(3, version);
			preparedStmt.executeUpdate();

			try (PreparedStatement updatedStmt = connection.prepareStatement(updatedQuery);) {
				updatedStmt.setString(1, "0");
				updatedStmt.setString(2, requestId);
				updatedStmt.setString(3, version);
				preparedStmt.executeUpdate();
			} catch (SQLException exe) {
				logger.error(
						"SQL Exception in getConfigCmdRecordFordataForDelivery update certificationtestvalidation method "
								+ exe.getMessage());
			}

		} catch (SQLException exe) {
			logger.error("SQL Exception in getConfigCmdRecordFordataForDelivery update webserviceinfo method "
					+ exe.getMessage());
		}
	}

	public void updateRouterFailureHealthCheck(String requestId, String version) {
		String query = "update certificationtestvalidation set suggestionForFailure =? where alphanumeric_req_id = ? and version = ? ";
		String suggestion = "Please check the connectivity.Issue while performing Health check test";
		try (Connection connection = ConnectionFactory.getConnection();
				PreparedStatement preparedStmt = connection.prepareStatement(query);) {
			preparedStmt.setString(1, suggestion);
			preparedStmt.setString(2, requestId);
			preparedStmt.setString(3, version);

			preparedStmt.executeUpdate();
		} catch (SQLException exe) {
			logger.error("SQL Exception in updateRouterFailureHealthCheck method " + exe.getMessage());
		}
	}

	public String updateTimeForScheduledRequest(String requestId, String version) throws SQLException {
		String query = "SELECT * FROM requestinfoso WHERE alphanumeric_req_id=? AND request_version=?";
		String updateQuery = "update requestinfoso set date_of_processing = ? where alphanumeric_req_id = ? and version = ? ";
		ResultSet rs = null;
		String result = "false";
		try (Connection connection = ConnectionFactory.getConnection();
				PreparedStatement ps = connection.prepareStatement(query);) {
			ps.setString(1, requestId);
			ps.setString(2, version);

			rs = ps.executeQuery();

			while (rs.next()) {
				if (rs.getString("RequestType_Flag").equalsIgnoreCase("S")) {
					try (PreparedStatement updatePs = connection.prepareStatement(updateQuery);) {
						updatePs.setTimestamp(1, rs.getTimestamp("ScheduledTime"));
						updatePs.setString(2, requestId);
						updatePs.setString(3, version);

						updatePs.executeUpdate();
					} catch (SQLException exe) {
						logger.error(
								"SQL Exception in updateTimeForScheduledRequest update method " + exe.getMessage());
					}
				}
			}
		} catch (SQLException exe) {
			logger.error("SQL Exception in updateTimeForScheduledRequest method " + exe.getMessage());
		} finally {
			DBUtil.close(rs);
		}

		return result;
	}

	public String updateTimeIntervalElapsedTime(String requestId, String version) throws SQLException {
		RequestInfoDao requestinfoDao = new RequestInfoDao();
		String query = "select * from requestinfoso where alphanumeric_req_id = ? and request_version= ?";
		ResultSet rs = null;
		String updateQuery = null;
		String diff = null;
		try (Connection connection = ConnectionFactory.getConnection();
				PreparedStatement ps = connection.prepareStatement(query);) {

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
					diff = requestinfoDao.calcTimeDiffInMins(timestamp, d);
					updateQuery = "update requestinfoso set temp_elapsed_time = ?, temp_processing_time= now() where alphanumeric_req_id = ? and request_version= ?";
				} else {
					Timestamp d1 = null;
					date = new java.util.Date();
					timestamp = new java.sql.Timestamp(date.getTime());
					d1 = rs.getTimestamp("temp_processing_time");
					String diff1 = requestinfoDao.calcTimeDiffInMins(timestamp, d1);
					diff = String.format("%.2f", Float
							.toString((Float.parseFloat(diff1) + Float.parseFloat(rs.getString("temp_elapsed_time")))));
					updateQuery = "update requestinfoso set temp_elapsed_time = ? where alphanumeric_req_id = ? and request_version= ?";
				}
				try (PreparedStatement updatePs = connection.prepareStatement(updateQuery);) {
					updatePs.setString(1, diff);
					updatePs.setString(2, requestId);
					updatePs.setString(3, version);
					updatePs.executeUpdate();
				} catch (SQLException exe) {
					logger.error("SQL Exception in updateTimeIntervalElapsedTime update method " + exe.getMessage());
				}
			}
		} catch (SQLException exe) {
			logger.error("SQL Exception in updateTimeIntervalElapsedTime method " + exe.getMessage());
		} finally {
			DBUtil.close(rs);
		}
		return "ok";
	}

	public String updateTempProcessingTime(String requestId, String version) throws SQLException {
		String query = "update requestinfoso set temp_processing_time= now() where alphanumeric_req_id = ? and request_version= ?";
		try (Connection connection = ConnectionFactory.getConnection();
				PreparedStatement ps = connection.prepareStatement(query);) {
			ps.setString(1, requestId);
			ps.setString(2, version);
			ps.executeUpdate();
		} catch (SQLException exe) {
			logger.error("SQL Exception in updateTempProcessingTime method " + exe.getMessage());
		}
		return "ok";
	}

	public boolean updateEditedAlertData(String alertCode, String description) {
		boolean result = false;
		String query = "update alertinformationtable set alert_description = ? WHERE alert_code=?";
		try (Connection connection = ConnectionFactory.getConnection();
				PreparedStatement ps = connection.prepareStatement(query);) {
			ps.setString(1, description);
			ps.setString(2, alertCode);
			int i = ps.executeUpdate();
			if (i == 1) {
				result = true;
			} else {
				result = false;
			}
		} catch (SQLException exe) {
			logger.error("SQL Exception in updateEditedAlertData method " + exe.getMessage());
		}
		return result;
	}

	public String getFAQforPage(String page) {
		String content = null;
		ResultSet rs = null;
		String path = null;
		String query = "SELECT data_path FROM t_faq_data where page = ?";
		String filePath = "";
		try (Connection connection = ConnectionFactory.getConnection();
				PreparedStatement preparedStmt = connection.prepareStatement(query);) {
			preparedStmt.setString(1, page);

			rs = preparedStmt.executeQuery();
			while (rs.next()) {
				path = rs.getString("data_path");
			}
			if (path != null) {
				filePath = TSALabels.FAQ_DOC_PATH.getValue() + path + "/" + page + ".txt";
				content = new String(Files.readAllBytes(Paths.get(filePath)));
			}
		} catch (SQLException e) {
			logger.error(e.getMessage());
		} catch (IOException e) {
			logger.error(e.getMessage());
		} finally {
			DBUtil.close(rs);
		}
		return content;
	}

	public JSONArray getStatusReportData(String startDate, String endDate) {
		JSONArray array = new JSONArray();
		DateTimeFormatter df = DateTimeFormatter.ofPattern("MM/dd/yyyy");

		LocalDate sdate = LocalDate.parse(startDate, df);
		LocalDate edate = LocalDate.parse(endDate, df);

		String sFdate = DateTimeFormatter.ofPattern("yyyy-MM-dd", Locale.ENGLISH).format(sdate);
		String eFdate = DateTimeFormatter.ofPattern("yyyy-MM-dd", Locale.ENGLISH).format(edate);

		ResultSet rs = null;
		String query = null;
		if (Global.loggedInUser.equalsIgnoreCase("seuser")) {
			query = "select request_status, count(*) as number from requestinfoso where DATE(date_of_processing) between ? and ? and request_creator_name=? and alphanumeric_req_id rlike'SR|OS' Group by request_status";
		} else if (Global.loggedInUser.equalsIgnoreCase("feuser")) {
			query = "select request_status, count(*) as number from requestinfoso where DATE(date_of_processing) between ? and ? and RequestOwner=? and alphanumeric_req_id rlike'SR|OS' Group by request_status";

		} else if (Global.loggedInUser.equalsIgnoreCase("admin")) {
			query = "select request_status, count(*) as number from requestinfoso where DATE(date_of_processing) between ? and ?  and alphanumeric_req_id rlike'SR|OS' Group by request_status";

		}
		try (Connection connection = ConnectionFactory.getConnection();
				PreparedStatement preparedStmt = connection.prepareStatement(query);) {

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
		} catch (SQLException exe) {
			logger.error("SQL Exception in getStatusReportData method " + exe.getMessage());
		} finally {
			DBUtil.close(rs);
		}

		return array;
	}

	public boolean isHealthCheckSuccesfulForOSUpgrade(String requestID, double version) {
		boolean result = false;
		String query = "select health_checkup from  webserviceinfo where alphanumeric_req_id = ? and version = ?";
		ResultSet rs = null;
		int flag = 0;
		DecimalFormat numberFormat = new DecimalFormat("#.0");
		String parentVersion = numberFormat.format(version);
		try (Connection connection = ConnectionFactory.getConnection();
				PreparedStatement preparedStmt = connection.prepareStatement(query);) {
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
		} catch (SQLException exe) {
			logger.error("SQL Exception in isHealthCheckSuccesfulForOSUpgrade method " + exe.getMessage());
		} finally {
			DBUtil.close(rs);
		}
		return result;
	}

	public boolean isDilevarySuccessforOSUpgrade(String requestID, double version) {
		boolean result = false;
		String query = "select deliever_config from webserviceinfo where alphanumeric_req_id = ? and version = ?";
		ResultSet rs = null;
		int flag = 0;
		DecimalFormat numberFormat = new DecimalFormat("#.0");
		String parentVersion = numberFormat.format(version);
		try (Connection connection = ConnectionFactory.getConnection();
				PreparedStatement preparedStmt = connection.prepareStatement(query);) {
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
		} catch (SQLException exe) {
			logger.error("SQL Exception in isDilevarySuccessforOSUpgrade method " + exe.getMessage());
		} finally {
			DBUtil.close(rs);
		}
		return result;
	}

	public boolean isPreHealthCheckSuccesfulForOSUpgrade(String requestID, double version) {
		boolean result = false;
		String query = null;
		ResultSet rs = null;
		int flag = 0;
		DecimalFormat numberFormat = new DecimalFormat("#.0");
		String parentVersion = numberFormat.format(version);
		query = "select pre_health_checkup from  webserviceinfo where alphanumeric_req_id = ? and version = ?";
		try (Connection connection = ConnectionFactory.getConnection();
				PreparedStatement preparedStmt = connection.prepareStatement(query);) {
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
		} catch (SQLException exe) {
			logger.error("SQL Exception in isPreHealthCheckSuccesfulForOSUpgrade method " + exe.getMessage());
		} finally {
			DBUtil.close(rs);
		}
		return result;
	}

	public void update_dilevary_step_flag_in_db(String key, int value, String requestId, String version) {
		String query = "update os_upgrade_dilevary_flags set " + key + "= ? WHERE request_id=? and request_version=?";
		try (Connection connection = ConnectionFactory.getConnection();
				PreparedStatement ps = connection.prepareStatement(query);) {
			ps.setInt(1, value);
			ps.setString(2, requestId);
			ps.setString(3, version);
			ps.executeUpdate();
		} catch (SQLException exe) {
			logger.error("SQL Exception in update_dilevary_step_flag_in_db method " + exe.getMessage());
		}
	}

	public CreateConfigRequest getOSDilevarySteps(String requestId, String version) {
		CreateConfigRequest req = new CreateConfigRequest();
		String query = "SELECT * FROM os_upgrade_dilevary_flags WHERE request_id=? AND request_version=?";
		ResultSet rs = null;

		try (Connection connection = ConnectionFactory.getConnection();
				PreparedStatement ps = connection.prepareStatement(query);) {

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

		} catch (SQLException exe) {
			logger.error("SQL Exception in getOSDilevarySteps method " + exe.getMessage());
		} finally {
			DBUtil.close(rs);
		}
		return req;
	}

	@SuppressWarnings("unchecked")
	public org.json.simple.JSONObject get_dilevary_steps_status(String requestId, String version) {
		org.json.simple.JSONObject obj = new org.json.simple.JSONObject();
		String query = "SELECT * FROM os_upgrade_dilevary_flags WHERE request_id=? AND request_version=?";
		ResultSet rs = null;
		try (Connection connection = ConnectionFactory.getConnection();
				PreparedStatement ps = connection.prepareStatement(query);) {

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

		} catch (SQLException exe) {
			logger.error("SQL Exception in get_dilevary_steps_status method " + exe.getMessage());
		} finally {
			DBUtil.close(rs);
		}

		return obj;
	}

	public String getRequestFlagForReportPreHealthCheck(String requestId, String versionId) {
		String query = "select pre_health_checkup from  webserviceinfo where alphanumeric_req_id = ? and version = ? ";
		ResultSet rs = null;
		String flagForPrevalidation = "";
		String flagFordelieverConfig = "";
		String res = null;
		Map<String, String> hmap = new HashMap<String, String>();
		logger.info("Version received" + versionId);
		try (Connection connection = ConnectionFactory.getConnection();
				PreparedStatement preparedStmt = connection.prepareStatement(query);) {
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
		} catch (SQLException exe) {
			logger.error("SQL Exception in getRequestFlagForReportPreHealthCheck method " + exe.getMessage());
		} finally {
			DBUtil.close(rs);
		}
		return res;
	}

	/*
	 * Owner: Ruchita Salvi Module: Test Strategy
	 */
	public List<TestDetail> findTestFromTestStrategyDB(String deviceFamily, String os, String osversion, String vendor,
			String region, String testCategory) {
		List<TestDetail> list = new ArrayList<TestDetail>();

		if ("All".equals(region)) {
			region = "%";
		} else {
			region = "%" + region +"%";
		}
		if ("All".equals(osversion)) {
			osversion = "%";
		} else {
			osversion = "%" + osversion +"%";
		}
		if ("All".equals(os)) {
			os = "%";
		} else {
			os = "%" + os+"%";
		}
		if ("All".equals(deviceFamily)) {
			deviceFamily = "%";
		} else {
			deviceFamily = "%" + deviceFamily+"%";
		}
		
		String queryTstDetails = "select * from  t_tststrategy_m_tstdetails where (device_family like ? or device_family like '%All')  and (os like ? or os like '%All') and (os_version like ? or os_version like '%All') and vendor =? and (region like ? or region like '%All') and test_category=?";
		String queryTstRules = "select * from t_tststrategy_m_tstrules where test_name=?";
		String queryTstDetailsTestName = "select * from  t_tststrategy_m_tstdetails where  (device_family like ? or device_family like '%All') and (os like ? or os like '%All')  and (os_version like ? or os_version like '%All') and vendor=? and (region like ? or region like '%All') and test_category=? and test_name=?";
		String queryTstDetailsTestNameV = "select * from  t_tststrategy_m_tstdetails where (device_family like ? or device_family like '%All') and (os like ? or os like '%All') and (os_version like ? or os_version like '%All') and vendor =? and (region like ? or region like '%All') and test_category=? and test_name=? and version=?";
		
		ResultSet rs = null, rs1 = null, rs2 = null, rs3 = null;
		String maxVersion = null;
		Set<String> setOfTest = new HashSet<>();
		try (Connection connection = ConnectionFactory.getConnection();
				PreparedStatement preparedStmt = connection.prepareStatement(queryTstDetails);) {

			preparedStmt.setString(1, deviceFamily);
			preparedStmt.setString(2, os);
			preparedStmt.setString(3, osversion);
			preparedStmt.setString(4, vendor);
			preparedStmt.setString(5, region);
			preparedStmt.setString(6, testCategory);

			rs = preparedStmt.executeQuery();
			if (rs != null) {
				while (rs.next()) {

					String testName = rs.getString("test_name");
					if (!(setOfTest.contains(testName))) {

						try (PreparedStatement tstDetailsTestNamePs = connection
								.prepareStatement(queryTstDetailsTestName);) {

							tstDetailsTestNamePs.setString(1, deviceFamily);
							tstDetailsTestNamePs.setString(2, os);
							tstDetailsTestNamePs.setString(3, osversion);
							tstDetailsTestNamePs.setString(4, vendor);
							tstDetailsTestNamePs.setString(5, region);
							tstDetailsTestNamePs.setString(6, testCategory);
							tstDetailsTestNamePs.setString(7, testName);

							rs2 = tstDetailsTestNamePs.executeQuery();

							if (rs2 != null) {
								while (rs2.next()) {
									maxVersion = rs2.getString("version");
								}

								try (PreparedStatement tstDetailsTestNameVPs = connection
										.prepareStatement(queryTstDetailsTestNameV);) {

									tstDetailsTestNameVPs.setString(1, deviceFamily);
									tstDetailsTestNameVPs.setString(2, os);
									tstDetailsTestNameVPs.setString(3, osversion);
									tstDetailsTestNameVPs.setString(4, vendor);
									tstDetailsTestNameVPs.setString(5, region);
									tstDetailsTestNameVPs.setString(6, testCategory);
									tstDetailsTestNameVPs.setString(7, testName);
									tstDetailsTestNameVPs.setString(8, maxVersion);

									rs3 = tstDetailsTestNameVPs.executeQuery();
									if (rs3 != null) {
										while (rs3.next()) {

											TestDetail test = new TestDetail();
											test.setId(rs3.getInt("id"));
											test.setTestCommand(rs3.getString("test_command"));
											test.setTestConnectionProtocol(rs.getString("test_connection_protocol"));
											test.setTestName(
													rs3.getString("test_name").concat("_" + rs3.getString("version")));
											test.setTestCategory(rs3.getString("test_category"));
											test.setVersion(rs3.getString("version"));
											List<TestRules> rulelist = new ArrayList<TestRules>();

											try (PreparedStatement tstRulesPs = connection
													.prepareStatement(queryTstRules);) {
												tstRulesPs.setInt(1, test.getId());
												rs1 = tstRulesPs.executeQuery();
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
											} catch (SQLException exe) {
												logger.error("SQL Exception in findTestFromTestStrategyDB 2 method "
														+ exe.getMessage());
											}

											test.setListRules(rulelist);
											list.add(test);
										}
									}
								} catch (SQLException exe) {
									logger.error(
											"SQL Exception in findTestFromTestStrategyDB 3 method " + exe.getMessage());
								}

							}
						} catch (SQLException exe) {
							logger.error("SQL Exception in findTestFromTestStrategyDB method " + exe.getMessage());
						}
						setOfTest.add(testName);
					}
				}
			}
		} catch (SQLException exe) {
			logger.error("SQL Exception in findTestFromTestStrategyDB main method " + exe.getMessage());
		} finally {
			DBUtil.close(rs);
			DBUtil.close(rs1);
			DBUtil.close(rs2);
			DBUtil.close(rs3);
		}
		list = list.stream().filter(UtilityMethods.distinctByKey(p -> p.getTestName())).collect(Collectors.toList());
		return list;
	}

	/*
	 * Owner: Ruchita Salvi Module: Test Strategy
	 */
	public boolean updateTestStrategeyConfigResultsTable(String requestID, String testName, String testCategory,
			String testResult, String testText, String collectedValue, String evaluationCriteria, String notes,
			String data_type, double requestVersion) {
		boolean res = false;
		String query = "insert into t_tststrategy_m_config_results (TestResult,ResultText,RequestId,TestCategory,testName,CollectedValue,EvaluationCriteria,notes,data_type,request_version) values (?,?,?,?,?,?,?,?,?,?)";
		try (Connection connection = ConnectionFactory.getConnection();
				PreparedStatement ps = connection.prepareStatement(query);) {
			ps.setString(1, testResult);
			ps.setString(2, testText);
			ps.setString(3, requestID);
			ps.setString(4, testCategory);
			ps.setString(5, testName);
			ps.setString(6, collectedValue);
			ps.setString(7, evaluationCriteria);
			ps.setString(8, notes);
			ps.setString(9, data_type);
			ps.setDouble(10, requestVersion);
			int i = ps.executeUpdate();
			if (i == 1) {
				res = true;
			} else {
				res = false;
			}
		} catch (SQLException exe) {
			logger.error("SQL Exception in findTestFromTestStrategyDB method " + exe.getMessage());
		}
		return res;
	}

	/*
	 * Owner: Ruchita Salvi Module: Test Strategy
	 */
	@SuppressWarnings("unchecked")
	public org.json.simple.JSONArray getDynamicTestResult(String requestId, String version, String testtype) {
		org.json.simple.JSONObject res = new org.json.simple.JSONObject();
		org.json.simple.JSONArray array = new org.json.simple.JSONArray();

		if (!version.contains(".")) {
			version = version + ".0";
		}
		String query = "select * from  t_tststrategy_m_config_results where RequestId = ? and TestCategory= ?";
		ResultSet rs = null;

		try (Connection connection = ConnectionFactory.getConnection();
				PreparedStatement preparedStmt = connection.prepareStatement(query);) {
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
					String testNameAndVersion = rs.getString("testName");
			        testNameAndVersion = StringUtils.substringAfter(testNameAndVersion, "_");
					obj.put("testName", testNameAndVersion);
					array.add(obj);
				}
			}

		} catch (SQLException exe) {
			logger.error("SQL Exception in getDynamicTestResult method " + exe.getMessage());
		} finally {
			DBUtil.close(rs);
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
		String query = "select * from  t_tststrategy_m_config_results where RequestId = ? and TestCategory= ? and request_version =?";
		ResultSet rs = null;
		String testName = null;

		try (Connection connection = ConnectionFactory.getConnection();
				PreparedStatement preparedStmt = connection.prepareStatement(query);) {
			preparedStmt.setString(1, requestId);
			preparedStmt.setString(2, testtype);
			preparedStmt.setString(3, version);
			rs = preparedStmt.executeQuery();
			if (rs != null) {
				while (rs.next()) {
					org.json.simple.JSONObject obj = new org.json.simple.JSONObject();

					obj.put("status", rs.getString("TestResult"));
					obj.put("CollectedValue", rs.getString("CollectedValue").replace(",", "$"));
					obj.put("EvaluationCriteria", rs.getString("EvaluationCriteria"));
					testName = rs.getString("testName");
					testName = StringUtils.substringAfter(testName, "_");
					testName = StringUtils.substringBeforeLast(testName, "_");
					obj.put("testname", testName);
					obj.put("reportLabel", rs.getString("ResultText"));

					obj.put("notes", rs.getString("notes"));
					obj.put("dataType", rs.getString("data_type"));
					obj.put("keyword", rs.getString("CollectedValue"));

					obj.put("evaluationStatus", "N/A");
					array.add(obj);
				}
			}

		} catch (SQLException exe) {
			logger.error("SQL Exception in getDynamicTestResultCustomerReport method " + exe.getMessage());
		} finally {
			DBUtil.close(rs);
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

		org.json.simple.JSONArray prevalidationArray = new org.json.simple.JSONArray();
		org.json.simple.JSONObject reachabilityObj = new org.json.simple.JSONObject();
		org.json.simple.JSONObject iosVersion = new org.json.simple.JSONObject();
		org.json.simple.JSONObject deviceModel = new org.json.simple.JSONObject();
		org.json.simple.JSONObject vendorTest = new org.json.simple.JSONObject();
		org.json.simple.JSONArray othersArray = new org.json.simple.JSONArray();
		org.json.simple.JSONArray networkAuditArray = new org.json.simple.JSONArray();
		certificationTestPojo1 = getCertificationTestFlagData(request.getRequestId(), request.getVersion_report(),
				"preValidate");
		// certificationTestService = new CertificationTestResultService();
		// resultEnt =
		// certificationTestService.getRecordByRequestId(request.getRequestId(),
		// request.getVersion_report());

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
	 * Owner: Ruchita Salvi Module: Test Strategy
	 */
	public List<TestDetail> findSelectedTests(String requestID, String testCategory, String version) {
		List<TestDetail> resultList = new ArrayList<TestDetail>();
		String query = "select TestsSelected from  t_tststrategy_m_config_transaction where RequestId = ?";
		ResultSet rs = null;
		String res = null;

		try (Connection connection = ConnectionFactory.getConnection();
				PreparedStatement preparedStmt = connection.prepareStatement(query);) {
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
		} catch (SQLException exe) {
			logger.error("SQL Exception in findSelectedTests method " + exe.getMessage());
		} finally {
			DBUtil.close(rs);
		}
		resultList = resultList.stream().filter(UtilityMethods.distinctByKey(p -> p.getTestName()))
				.collect(Collectors.toList());
		return resultList;
	}

	public String getPreviousMileStoneStatus(String requestID, String version) {
		String status = null;
		// logic to get previous status from reqestinfoso
		String query = "select request_status from requestinfoso where alphanumeric_req_id = ? and request_version = ?";
		ResultSet rs = null;
		try (Connection connection = ConnectionFactory.getConnection();
				PreparedStatement preparedStmt = connection.prepareStatement(query);) {
			preparedStmt.setString(1, requestID);

			preparedStmt.setString(2, version);
			rs = preparedStmt.executeQuery();
			while (rs.next()) {
				status = rs.getString("request_status");
			}
		} catch (SQLException exe) {
			logger.error("SQL Exception in getPreviousMileStoneStatus method " + exe.getMessage());
		} finally {
			DBUtil.close(rs);
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
		String query = "select * from  t_tststrategy_m_config_results where RequestId = ? and TestCategory= ?";
		ResultSet rs = null;
		try (Connection connection = ConnectionFactory.getConnection();
				PreparedStatement preparedStmt = connection.prepareStatement(query);) {
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
					obj.put("Execution Status", rs.getString("ResultText"));
					obj.put("TestName", rs.getString("testName"));
					array.add(obj);
				}
			}

		} catch (SQLException exe) {
			logger.error("SQL Exception in getNetworkAuditReport main method " + exe.getMessage());
		} finally {
			DBUtil.close(rs);
		}
		res.put("custom", array);
		return array;
	}

	// To retrieve user stored snippet
	// Author: Ruchita Salvi Date : 13.01.2020
	public String getSnippet(String data_type, String label, String test_name) {
		int id = 0;
		String snippet = null;
		String query = "select id from t_tststrategy_m_tstdetails where test_name = ?";
		String queryTstRules = "select snippet from t_tststrategy_m_tstrules where reported_label = ? and test_name=?";
		ResultSet rs = null;
		ResultSet rs1 = null;
		try (Connection connection = ConnectionFactory.getConnection();
				PreparedStatement preparedStmt = connection.prepareStatement(query);) {
			preparedStmt.setString(1, test_name);

			rs = preparedStmt.executeQuery();
			while (rs.next()) {
				id = rs.getInt("id");
			}
			try (PreparedStatement tstRulesPs = connection.prepareStatement(queryTstRules);) {
				tstRulesPs.setString(1, label);
				tstRulesPs.setInt(2, id);
				rs1 = tstRulesPs.executeQuery();
				while (rs1.next()) {
					snippet = rs1.getString("snippet");
				}
			} catch (SQLException exe) {
				logger.error("SQL Exception in getSnippet method " + exe.getMessage());
			}
		} catch (SQLException exe) {
			logger.error("SQL Exception in getSnippet main method " + exe.getMessage());
		} finally {
			DBUtil.close(rs);
			DBUtil.close(rs1);
		}

		return snippet;
	}

	/* Adding changes for device family */
	public List<TestDetail> getAllTests() {
		List<TestDetail> list = new ArrayList<TestDetail>();
		String query = "select * from t_tststrategy_m_tstdetails";
		ResultSet rs = null;
		try (Connection connection = ConnectionFactory.getConnection();
				PreparedStatement pst = connection.prepareStatement(query);) {

			rs = pst.executeQuery();
			TestDetail request;
			while (rs.next()) {
				request = new TestDetail();
				request.setTestName(rs.getString("test_name"));
				request.setVersion(rs.getString("version"));
				request.setTestId(rs.getString("id"));
				request.setVendor(rs.getString("vendor"));
				request.setDeviceFamily(rs.getString("device_family"));
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
		} catch (SQLException exe) {
			logger.error("SQL Exception in getAllTests method " + exe.getMessage());
		} finally {
			DBUtil.close(rs);
		}
		return list;
	}

	public void updateVersion(String testName, boolean is_enabled) {
		String query = "update t_tststrategy_m_tstdetails set is_enabled=? where test_name = ? ";
		try (Connection connection = ConnectionFactory.getConnection();
				PreparedStatement preparedStmt = connection.prepareStatement(query);) {

			preparedStmt.setBoolean(1, is_enabled);
			preparedStmt.setString(2, testName);

			preparedStmt.executeUpdate();
		} catch (SQLException exe) {
			logger.error("SQL Exception in updateVersion method " + exe.getMessage());
		}
	}

	public void updateRequestforReportWebserviceInfo(String requestId) {
		String query = "Update webserviceinfo Set health_checkup='0', network_test='0',network_audit='0' where alphanumeric_req_id = ?";
		try (Connection connection = ConnectionFactory.getConnection();
				PreparedStatement preparedStmt = connection.prepareStatement(query);) {
			preparedStmt.setString(1, requestId);
			preparedStmt.execute("SET FOREIGN_KEY_CHECKS=0");
			preparedStmt.executeUpdate();
		} catch (SQLException exe) {
			logger.error("SQL Exception in updateRequestforReportWebserviceInfo method " + exe.getMessage());
		}
	}

	@SuppressWarnings("unchecked")
	public org.json.simple.JSONObject getStatusForBackUpRequestCustomerReport(CreateConfigRequestDCM request) {
		org.json.simple.JSONObject obj = new org.json.simple.JSONObject();
		CertificationTestPojo certificationTestPojo1 = new CertificationTestPojo();

		org.json.simple.JSONArray prevalidationArray = new org.json.simple.JSONArray();
		org.json.simple.JSONObject reachabilityObj = new org.json.simple.JSONObject();
		org.json.simple.JSONObject deviceModel = new org.json.simple.JSONObject();
		org.json.simple.JSONObject vendorTest = new org.json.simple.JSONObject();
		org.json.simple.JSONObject backUpStatus = new org.json.simple.JSONObject();
		String model = null, vendor = null, requestId = null, deliveryStatus = null;

		certificationTestPojo1 = getCertificationTestFlagData(request.getRequestId(), request.getVersion_report(),
				"preValidate");

		requestId = request.getRequestId();

		String query = "SELECT * FROM requestinfoso where alphanumeric_req_id =?";
		String query1 = "SELECT * FROM webserviceinfo where alphanumeric_req_id =?";

		ResultSet rs = null, rs1 = null;
		try (Connection connection = ConnectionFactory.getConnection();
				PreparedStatement pst = connection.prepareStatement(query);) {
			pst.setString(1, requestId);

			rs = pst.executeQuery();
			if (rs != null) {
				while (rs.next()) {
					model = rs.getString("model");
					vendor = rs.getString("vendor");
				}
			}
		} catch (SQLException exe) {
			logger.error("SQL Exception in getStatusForBackUpRequestCustomerReport method " + exe.getMessage());
		} finally {
			DBUtil.close(rs);
		}

		try (Connection connection = ConnectionFactory.getConnection();
				PreparedStatement pst1 = connection.prepareStatement(query1);) {
			pst1.setString(1, requestId);
			rs1 = pst1.executeQuery();
			if (rs1 != null) {
				while (rs1.next()) {
					deliveryStatus = rs1.getString("deliever_config");
				}
			}
		} catch (SQLException exe) {
			logger.error("SQL Exception in getStatusForBackUpRequestCustomerReport method " + exe.getMessage());
		} finally {
			DBUtil.close(rs1);
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
		String query = "{ CALL `c3pdbschema`.`GetAllRequest`() }";
		ResultSet rs = null;
		RequestInfoSO request = null;
		List<RequestInfoSO> requestInfoList = null;
		try (Connection connection = ConnectionFactory.getConnection();
				PreparedStatement pst = connection.prepareStatement(query);) {

			rs = pst.executeQuery();
			requestInfoList = new ArrayList<RequestInfoSO>();
			if (rs != null) {
				while (rs.next()) {
					request = new RequestInfoSO();
					request.setRequest_id(rs.getInt("request_info_id"));
					request.setCertificationSelectionBit(rs.getString("certificationSelectionBit"));
					request.setRequest_version(rs.getDouble("request_version"));
					requestInfoList.add(request);
				}
			}
		} catch (SQLException exe) {
			logger.error("SQL Exception in getCertificationtestvalidationProcedure method " + exe.getMessage());
		} finally {
			DBUtil.close(rs);
		}
		return requestInfoList;
	}

	public boolean get_dilevary_status(String string) {
		String query = "SELECT * FROM webserviceinfo where alphanumeric_req_id =?";
		ResultSet rs = null;
		boolean deliver_status = false;
		try (Connection connection = ConnectionFactory.getConnection();
				PreparedStatement pst = connection.prepareStatement(query);) {
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
		} catch (SQLException exe) {
			logger.error("SQL Exception in get_dilevary_status method " + exe.getMessage());
		} finally {
			DBUtil.close(rs);
		}
		return deliver_status;
	}

	/* Dhanshri Mane 6/2/2020 for getAll request for Request Type */
	public int getRequestTpyeData(String requestType) {
		int num = 0;
		String query = null;
		ResultSet rs = null;
		if (Global.loggedInUser.equalsIgnoreCase("seuser")) {
			query = "SELECT COUNT(request_info_id) AS total FROM requestinfoso Where alphanumeric_req_id like ? and request_creator_name=?;";
		} else if (Global.loggedInUser.equalsIgnoreCase("feuser")) {
			query = "SELECT COUNT(request_info_id) AS total FROM requestinfoso Where alphanumeric_req_id like ? and RequestOwner=?;";

		} else if (Global.loggedInUser.equalsIgnoreCase("admin")) {
			query = "SELECT COUNT(request_info_id) AS total FROM requestinfoso Where alphanumeric_req_id like ?;";

		}
		try (Connection connection = ConnectionFactory.getConnection();
				PreparedStatement ps = connection.prepareStatement(query);) {
			ps.setString(1, requestType);
			if (!Global.loggedInUser.equalsIgnoreCase("admin")) {
				ps.setString(2, Global.loggedInUser);
			}
			rs = ps.executeQuery();
			while (rs.next()) {
				num = rs.getInt("total");
			}
		} catch (SQLException exe) {
			logger.error("SQL Exception in getRequestTpyeData method " + exe.getMessage());
		} finally {
			DBUtil.close(rs);
		}
		return num;
	}

	/* Dhanshri Mane 6/2/2020 for getAll request for Request Type */
	public int getStatusForSpecificRequestType(String requestType, String requestStatus) {
		int num = 0;
		String query = null;
		ResultSet rs = null;
		if (requestType != null) {
			if (Global.loggedInUser.equalsIgnoreCase("seuser")) {
				query = "SELECT COUNT(request_info_id) AS total FROM requestinfoso where alphanumeric_req_id like ? and request_status =? and request_creator_name=?;";
			} else if (Global.loggedInUser.equalsIgnoreCase("feuser")) {
				query = "SELECT COUNT(request_info_id) AS total FROM requestinfoso where alphanumeric_req_id like ? and request_status =? RequestOwner=?;";

			} else if (Global.loggedInUser.equalsIgnoreCase("admin")) {
				query = "SELECT COUNT(request_info_id) AS total FROM requestinfoso where alphanumeric_req_id like ? and request_status =?;";

			}
		} else {
			if (Global.loggedInUser.equalsIgnoreCase("seuser")) {
				query = "SELECT COUNT(request_info_id) AS total FROM requestinfoso where alphanumeric_req_id like ? and request_creator_name=?;";
			} else if (Global.loggedInUser.equalsIgnoreCase("feuser")) {
				query = "SELECT COUNT(request_info_id) AS total FROM requestinfoso where alphanumeric_req_id like ? RequestOwner=?;";

			} else if (Global.loggedInUser.equalsIgnoreCase("admin")) {
				query = "SELECT COUNT(request_info_id) AS total FROM requestinfoso where  request_status =?;";

			}
		}
		try (Connection connection = ConnectionFactory.getConnection();
				PreparedStatement ps = connection.prepareStatement(query);) {
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
		} catch (SQLException exe) {
			logger.error("SQL Exception in getStatusForSpecificRequestType method " + exe.getMessage());
		} finally {
			DBUtil.close(rs);
		}
		return num;
	}

	/* Dhanshri Mane 6/2/2020 for getAll request for specific network type */
	public int getNetworkTypeRequest(String networkType, String requestType) {
		int num = 0;
		String query = null;
		ResultSet rs = null;
		if (Global.loggedInUser.equalsIgnoreCase("seuser")) {
			query = "SELECT COUNT(request_info_id) AS total FROM requestinfoso Where networktype = ? and alphanumeric_req_id like ? and request_creator_name=?;";
		} else if (Global.loggedInUser.equalsIgnoreCase("feuser")) {
			query = "SELECT COUNT(request_info_id) AS total FROM requestinfoso Where networktype = ? and alphanumeric_req_id like ? and RequestOwner=?;";

		} else if (Global.loggedInUser.equalsIgnoreCase("admin")) {
			query = "SELECT COUNT(request_info_id) AS total FROM requestinfoso Where networktype = ? and alphanumeric_req_id like ? ;";

		}
		try (Connection connection = ConnectionFactory.getConnection();
				PreparedStatement ps = connection.prepareStatement(query);) {
			ps.setString(1, networkType);
			ps.setString(2, requestType);
			if (!Global.loggedInUser.equalsIgnoreCase("admin")) {
				ps.setString(3, Global.loggedInUser);
			}
			rs = ps.executeQuery();
			while (rs.next()) {
				num = rs.getInt("total");
			}
		} catch (SQLException exe) {
			logger.error("SQL Exception in getNetworkTypeRequest method " + exe.getMessage());
		} finally {
			DBUtil.close(rs);
		}
		return num;
	}

	/* Method Overloading for UIRevamp */
	public Map<String, String> insertRequestInDB(RequestInfoPojo requestInfoSO) {
		Map<String, String> hmap = new HashMap<String, String>();
		String Os = null, model = null, region = null, version = null, hostname = null, alphaneumeric_req_id,
				customer = null, siteName = null, family = null, siteId = null, vendor = null, deviceType = null;
		String request_creator_name = null, certificationSelectionBit = null;
		String managementIP = null, scheduledTime = null, templateId = null;
		String networktype = null;
		double request_version = 0, request_parent_version = 0;
		boolean startup = false;

		RequestInfoEntity requestEntity = new RequestInfoEntity();

		/* TimeZ Column added for Time Zone task */

		try {
			if (requestInfoSO.getAlphanumericReqId() != null && !requestInfoSO.getAlphanumericReqId().equals("")) {
				alphaneumeric_req_id = requestInfoSO.getAlphanumericReqId();
				if (alphaneumeric_req_id.contains("SLGM")) {
					requestInfoSO.setRequestType("Config MACD");
				}
			} else {
				if (requestInfoSO.getRequestType().equalsIgnoreCase("IOSUPGRADE")
						&& requestInfoSO.getNetworkType().equalsIgnoreCase("PNF")) {
					alphaneumeric_req_id = "SLGF-" + UUID.randomUUID().toString().toUpperCase();

				} else if (requestInfoSO.getRequestType().equalsIgnoreCase("Test")
						&& requestInfoSO.getNetworkType().equalsIgnoreCase("PNF")) {
					alphaneumeric_req_id = "SLGT-" + UUID.randomUUID().toString().toUpperCase();

				} else if (requestInfoSO.getRequestType().equalsIgnoreCase("Audit")
						&& requestInfoSO.getNetworkType().equalsIgnoreCase("PNF")) {
					alphaneumeric_req_id = "SLGA-" + UUID.randomUUID().toString().toUpperCase();

				} else if (requestInfoSO.getRequestType().equalsIgnoreCase("RESTCONF")
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

				} else if (requestInfoSO.getRequestType().equalsIgnoreCase("Config MACD")) {
					alphaneumeric_req_id = "SLGM-" + UUID.randomUUID().toString().toUpperCase();

				} else {
					alphaneumeric_req_id = "SLGC-" + UUID.randomUUID().toString().toUpperCase();
				}
			}
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

			if (requestInfoSO.getOsVersion() != null || requestInfoSO.getOsVersion() != "") {
				version = requestInfoSO.getOsVersion();
			}
			if (requestInfoSO.getHostname() != null || requestInfoSO.getHostname() != "") {
				hostname = requestInfoSO.getHostname();
			}

			if (requestInfoSO.getCustomer() != null || requestInfoSO.getCustomer() != "") {
				customer = requestInfoSO.getCustomer();
			}

			if (requestInfoSO.getFamily() != null || requestInfoSO.getFamily() != "") {
				family = requestInfoSO.getFamily();
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
				 * zipcode = requestInfoSO.getZipcode(); managed =
				 * requestInfoSO.getManaged(); downtimerequired =
				 * requestInfoSO.getDownTimeRequired(); lastupgradedon =
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

			if (family != "") {
				requestEntity.setFamily(family);
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
		String query = "SELECT test_name,version FROM t_tststrategy_m_tstdetails where test_name LIKE ? order by test_name,version asc";
		List<TestDetail> requestInfoList = null;
		TestDetail request = null;
		ResultSet rs = null;

		try (Connection connection = ConnectionFactory.getConnection();
				PreparedStatement pst = connection.prepareStatement(query);) {
			requestInfoList = new ArrayList<TestDetail>();

			pst.setString(1, testNameUsed + "%");

			rs = pst.executeQuery();
			while (rs.next()) {
				request = new TestDetail();
				request.setTestName(rs.getString("test_name"));
				request.setVersion(rs.getString("version"));
				requestInfoList.add(request);
			}
		} catch (SQLException exe) {
			logger.error("SQL Exception in findByTestName method " + exe.getMessage());
		} finally {
			DBUtil.close(rs);
		}
		return requestInfoList;

	}

	public List<TestBundling> findByTestNameForSearch(String key, String value) {
		String query = null;
		List<TestBundling> requestInfoList = null;
		TestBundling request = null;
		ResultSet rs = null;

		if (key.equals("Device Family")) {
			query = "SELECT * FROM t_tststrategy_m_testbundling where device_family LIKE ?";
		} else if (key.equals("Vendor")) {
			query = "SELECT * FROM t_tststrategy_m_testbundling where vendor LIKE ?";
		} else if (key.equals("OS")) {
			query = "SELECT * FROM t_tststrategy_m_testbundling where os LIKE ?";
		} else if (key.equals("OS Version")) {
			query = "SELECT * FROM t_tststrategy_m_testbundling where os_version LIKE ?";
		} else if (key.equals("Bundle Name")) {
			query = "SELECT * FROM t_tststrategy_m_testbundling where test_bundle LIKE ?";
		} else if (key.equals("Region")) {
			query = "SELECT * FROM t_tststrategy_m_testbundling where region LIKE ?";
		}

		try (Connection connection = ConnectionFactory.getConnection();
				PreparedStatement pst = connection.prepareStatement(query);) {
			requestInfoList = new ArrayList<TestBundling>();
			pst.setString(1, value + "%");

			rs = pst.executeQuery();
			while (rs.next()) {

				request = new TestBundling();
				request.setId(rs.getInt("id"));
				request.setDeviceFamily(rs.getString("device_family"));
				request.setNetworkFunction(rs.getString("network_function"));
				request.setOs(rs.getString("os"));
				request.setOsVersion(rs.getString("os_version"));
				request.setRegion(rs.getString("region"));
				request.setVendor(rs.getString("vendor"));
				request.setTestBundle(rs.getString("test_bundle"));
				request.setUpdatedBy(rs.getString("updated_by"));
				request.setUpdatedDate(rs.getTimestamp("updated_date"));

				requestInfoList.add(request);
			}
		} catch (SQLException exe) {
			logger.error("SQL Exception in findByTestNameForSearch method " + exe.getMessage());
		} finally {
			DBUtil.close(rs);
		}
		return requestInfoList;

	}

	public List<FirmwareUpgradeDetail> findByVendorName(String vendor) {
		String query = "SELECT * FROM firmware_upgrade_single_device where vendor LIKE ?";
		List<FirmwareUpgradeDetail> requestInfoList = null;
		FirmwareUpgradeDetail request = null;
		ResultSet rs = null;

		try (Connection connection = ConnectionFactory.getConnection();
				PreparedStatement pst = connection.prepareStatement(query);) {
			requestInfoList = new ArrayList<FirmwareUpgradeDetail>();

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
		} catch (SQLException exe) {
			logger.error("SQL Exception in findByVendorName method " + exe.getMessage());
		} finally {
			DBUtil.close(rs);
		}
		return requestInfoList;

	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public List checkForDeviceLock(String requestId, String managementIp, String TestType) {
		String query = "Select * from devicelocked_managementip where management_ip=?";
		ResultSet rs = null;
		List deviceLockList = new ArrayList<>();
		if (TestType.equalsIgnoreCase("DeviceTest")) {
			try (Connection connection = ConnectionFactory.getConnection();
					PreparedStatement pst = connection.prepareStatement(query);) {

				pst.setString(1, managementIp);
				rs = pst.executeQuery();
				while (rs.next()) {
					requestId = rs.getString("locked_by");
					deviceLockList.add(requestId);
				}

			} catch (SQLException exe) {
				logger.error("SQL Exception in checkForDeviceLock method " + exe.getMessage());
			} finally {
				DBUtil.close(rs);
			}
		}
		return deviceLockList;
	}

	public String deleteForDeviceLock(String locked_by) {
		String query = "delete from devicelocked_managementip where locked_by = ?";
		String result = null;
		ResultSet rs = null;
		try (Connection connection = ConnectionFactory.getConnection();
				PreparedStatement pst = connection.prepareStatement(query);) {
			pst.setString(1, locked_by);

			pst.executeUpdate();
			result = "Success";
		} catch (SQLException exe) {
			logger.error("SQL Exception in deleteForDeviceLock method " + exe.getMessage());
		} finally {
			DBUtil.close(rs);
		}
		return result;

	}

	@SuppressWarnings("unchecked")
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
		String requestId = null, deliveryStatus = null;
		certificationTestService = new CertificationTestResultService();
		resultEnt = certificationTestService.getRecordByRequestId(request.getAlphanumericReqId(),
				Double.toString(request.getRequestVersion()));

		certificationTestPojo1 = getCertificationTestFlagData(request.getAlphanumericReqId(),
				Double.toString(request.getRequestVersion()), "preValidate");

		requestId = request.getAlphanumericReqId();
		String query = "SELECT * FROM webserviceinfo where alphanumeric_req_id =?";

		ResultSet rs = null;

		try (Connection connection = ConnectionFactory.getConnection();
				PreparedStatement pst = connection.prepareStatement(query);) {
			pst.setString(1, requestId);
			rs = pst.executeQuery();
			if (rs != null) {
				while (rs.next()) {
					deliveryStatus = rs.getString("deliever_config");
				}
			}
		} catch (SQLException exe) {
			logger.error("SQL Exception in deleteForDeviceLock method " + exe.getMessage());
		} finally {
			DBUtil.close(rs);
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
		if (certificationTestPojo1.getDeviceReachabilityTest().equalsIgnoreCase("0")) {
			reachabilityObj.put("testname", "Device Reachability test");
			reachabilityObj.put("status", "Not Conducted");
			reachabilityObj.put("outcome", "N/A");
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
		if (certificationTestPojo1.getIosVersionTest().equalsIgnoreCase("0")) {
			iosVersion.put("testname", "OS");
			iosVersion.put("status", "Not Conducted");
			iosVersion.put("outcome", "");
			iosVersion.put("notes", "N/A");
			iosVersion.put("CollectedValue", "N/A");
			iosVersion.put("EvaluationCriteria", "N/A");
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
		if (certificationTestPojo1.getDeviceModelTest().equalsIgnoreCase("0")) {
			deviceModel.put("testname", "Device Model");
			deviceModel.put("status", "Not Conducted");
			deviceModel.put("outcome", "");
			deviceModel.put("notes", "N/A");
			deviceModel.put("CollectedValue", "N/A");
			deviceModel.put("EvaluationCriteria", "N/A");
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
		if (certificationTestPojo1.getVendorTest().equalsIgnoreCase("0")) {
			vendorTest.put("testname", "Vendor Test");
			vendorTest.put("status", "Not Conducted");
			vendorTest.put("outcome", "");
			vendorTest.put("notes", "N/A");
			vendorTest.put("CollectedValue", "N/A");
			vendorTest.put("EvaluationCriteria", "N/A");
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

	@SuppressWarnings("unchecked")
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
		if (certificationTestPojo1.getDeviceReachabilityTest().equalsIgnoreCase("0")) {
			reachabilityObj.put("testname", "Device Reachability test");
			reachabilityObj.put("status", "Not Conducted");
			reachabilityObj.put("outcome", "N/A");
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
		if (certificationTestPojo1.getIosVersionTest().equalsIgnoreCase("0")) {
			iosVersion.put("testname", "OS");
			iosVersion.put("status", "Not Conducted");
			iosVersion.put("outcome", "N/A");
			iosVersion.put("notes", "N/A");
			iosVersion.put("CollectedValue", "N/A");
			iosVersion.put("EvaluationCriteria", "N/A");
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
		if (certificationTestPojo1.getDeviceModelTest().equalsIgnoreCase("0")) {
			deviceModel.put("testname", "Device Model");
			deviceModel.put("status", "Not Conducted");
			deviceModel.put("outcome", "N/A");
			deviceModel.put("notes", "N/A");
			deviceModel.put("CollectedValue", "N/A");
			deviceModel.put("EvaluationCriteria", "N/A");
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
		if (certificationTestPojo1.getVendorTest().equalsIgnoreCase("0")) {
			vendorTest.put("testname", "Vendor Test");
			vendorTest.put("status", "Not Conducted");
			vendorTest.put("outcome", "N/A");
			vendorTest.put("notes", "N/A");
			vendorTest.put("CollectedValue", "N/A");
			vendorTest.put("EvaluationCriteria", "N/A");
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
		} else {
			networkIfObj = null;
		}
		if (certificationTestPojo2.getShowInterfaceCmd().equalsIgnoreCase("1")) {
			waninterface.put("testname", "Wan Interface");
			waninterface.put("status", "Passed");
			waninterface.put("outcome", "");
			waninterface.put("notes", "N/A");
		} else {
			waninterface = null;
		}
		if (certificationTestPojo2.getShowVersionCmd().equalsIgnoreCase("1")) {
			networkPlatformIOS.put("testname", "Network Platform IOS");
			networkPlatformIOS.put("status", "Passed");
			networkPlatformIOS.put("outcome", "");
			networkPlatformIOS.put("notes", "N/A");
		} else {
			networkPlatformIOS = null;
		}
		if (certificationTestPojo2.getShowIpBgpSummaryCmd().equalsIgnoreCase("1")) {
			bgpneighbour.put("testname", "BGP Neighbour");
			bgpneighbour.put("status", "Passed");
			bgpneighbour.put("outcome", "");
			bgpneighbour.put("notes", "N/A");
		} else {
			bgpneighbour = null;
		}
		if (bgpneighbour != null) {
			networkArray.add(bgpneighbour);
		}
		if (waninterface != null) {
			networkArray.add(waninterface);
		}
		if (networkPlatformIOS != null) {
			networkArray.add(networkPlatformIOS);
		}
		if (networkIfObj != null) {
			networkArray.add(networkIfObj);
		}
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
		String query = "SELECT * FROM certificationtestvalidation WHERE alphanumeric_req_id=? AND version=?";
		ResultSet rs = null;
		try (Connection connection = ConnectionFactory.getConnection();
				PreparedStatement ps = connection.prepareStatement(query);) {
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
		} catch (SQLException exe) {
			logger.error("SQL Exception in findCertificationTestResultEntityByRequestID method " + exe.getMessage());
		} finally {
			DBUtil.close(rs);
		}
		return ent;
	}

	public String findByRequestId(String requestId) {
		String query = "SELECT * FROM t_tststrategy_m_config_results WHERE RequestId=?";
		ResultSet rs = null;
		String result = null;
		try (Connection connection = ConnectionFactory.getConnection();
				PreparedStatement ps = connection.prepareStatement(query);) {
			ps.setString(1, requestId);
			rs = ps.executeQuery();
			while (rs.next()) {
				result = rs.getString("testName");
			}
		} catch (SQLException exe) {
			logger.error("SQL Exception in findByRequestId method " + exe.getMessage());
		} finally {
			DBUtil.close(rs);
		}
		return result;
	}

	/* Dhanshri Mane */
	public int getTestDetails(String requestId, String testName, double requsetVersion) {
		String query = "select * from  t_tststrategy_m_config_results where RequestId = ? and testName= ? and request_version=?";
		ResultSet rs = null;
		int status = 0;
		try (Connection connection = ConnectionFactory.getConnection();
				PreparedStatement preparedStmt = connection.prepareStatement(query);) {
			preparedStmt.setString(1, requestId);
			preparedStmt.setString(2, testName);
			preparedStmt.setDouble(3, requsetVersion);
			rs = preparedStmt.executeQuery();

			int failuarCount = 0;
			if (rs != null) {
				while (rs.next()) {
					if (rs.getString("TestResult").equalsIgnoreCase("Passed")) {
						status = 1;
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
		} catch (SQLException exe) {
			logger.error("SQL Exception in getTestDetails method " + exe.getMessage());
		} finally {
			DBUtil.close(rs);
		}
		return status;
	}

	public List<FirmwareUpgradeDetail> findByFamily(String isFamily, String isVendor) {
		String query = "SELECT * FROM firmware_upgrade_single_device where family LIKE ? AND vendor LIKE ?";
		List<FirmwareUpgradeDetail> requestInfoList = null;
		FirmwareUpgradeDetail request = null;
		ResultSet rs = null;
		try (Connection connection = ConnectionFactory.getConnection();
				PreparedStatement pst = connection.prepareStatement(query);) {
			requestInfoList = new ArrayList<FirmwareUpgradeDetail>();
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
		} catch (SQLException exe) {
			logger.error("SQL Exception in findByFamily method " + exe.getMessage());
		} finally {
			DBUtil.close(rs);
		}
		return requestInfoList;

	}

	public final boolean updateBatchStatus(String batchId) {
		boolean result = false;
		String query = "update c3p_t_request_batch_info set r_batch_status='Completed' where r_batch_id=?";
		try (Connection connection = ConnectionFactory.getConnection();
				PreparedStatement ps = connection.prepareStatement(query);) {

			ps.setString(1, batchId);

			int i = ps.executeUpdate();
			if (i == 1) {
				result = true;
			} else {
				result = false;
			}
		} catch (SQLException exe) {
			logger.error("SQL Exception in updateBatchStatus method " + exe.getMessage());
		}
		return result;
	}

	public Map<String, String> insertBatchConfigRequestInDB(RequestInfoPojo requestInfoSO) {
		Map<String, String> hmap = new HashMap<String, String>();
		String Os = null, model = null, region = null, version = null, hostname = null, alphaneumeric_req_id = null,
				customer = null, siteName = null, siteId = null, vendor = null, deviceType = null, deviceFamily = null;
		String request_creator_name = null, batchId = null, requestStatus = null, certificationSelectionBit = null;
		String managementIP = null, scheduledTime = null, templateId = null;
		String networktype = null;
		double request_version = 0, request_parent_version = 0;
		boolean startup = false;

		RequestInfoEntity requestEntity = new RequestInfoEntity();
		BatchIdEntity batchIdEntity = new BatchIdEntity();
		try {

			if (requestInfoSO.getRequestType().equalsIgnoreCase("Config MACD")
					&& requestInfoSO.getNetworkType().equalsIgnoreCase("PNF")) {
				alphaneumeric_req_id = "SLGM-" + UUID.randomUUID().toString().toUpperCase();

			} else if (requestInfoSO.getRequestType().equalsIgnoreCase("Test")
					&& requestInfoSO.getNetworkType().equalsIgnoreCase("PNF")) {
				alphaneumeric_req_id = "SLGT-" + UUID.randomUUID().toString().toUpperCase();
			} else if (requestInfoSO.getRequestType().equalsIgnoreCase("Audit")
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
			if (requestInfoSO.getFamily() != null || requestInfoSO.getFamily() != "") {
				deviceFamily = requestInfoSO.getFamily();
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
			}
			if (deviceFamily != "") {
				requestEntity.setFamily(deviceFamily);
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
			} else {
				requestEntity.setCertificationSelectionBit(certificationSelectionBit);
			}

			requestEntity.setRequestElapsedTime("00:00:00");

			if (scheduledTime != null && scheduledTime != "") {

				SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");

				try {
					Date parsedDate = sdf.parse(scheduledTime);

					Timestamp timestampTimeForScheduled = new Timestamp(parsedDate.getTime());

					requestEntity.setSceheduledTime(timestampTimeForScheduled);
					requestEntity.setRequestTypeFlag("S");

				} catch (Exception e) {
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

	public boolean updateBatchRequestStatus(String requestId) {
		boolean result = false;
		String query = "update c3p_t_request_info set r_status= 'Failure' where r_alphanumeric_req_id=?";
		try (Connection connection = ConnectionFactory.getConnection();
				PreparedStatement ps = connection.prepareStatement(query);) {
			ps.setString(1, requestId);
			int i = ps.executeUpdate();
			if (i == 1) {
				result = true;
			} else {
				result = false;
			}
		} catch (SQLException exe) {
			logger.error("SQL Exception in updateBatchRequestStatus method " + exe.getMessage());
		}
		return result;
	}

	public final boolean updateRequestExecutionStatus(String requestId) {
		boolean result = false;
		String query = "update c3p_t_request_info set r_execution_status= true where r_alphanumeric_req_id=?";
		try (Connection connection = ConnectionFactory.getConnection();
				PreparedStatement ps = connection.prepareStatement(query);) {

			ps.setString(1, requestId);
			int i = ps.executeUpdate();
			if (i == 1) {
				result = true;
			} else {
				result = false;
			}
		} catch (SQLException exe) {
			logger.error("SQL Exception in updateRequestExecutionStatus method " + exe.getMessage());
		}
		return result;
	}

	public String getTestList(String requestID) {
		String query = "select testsselected from  t_tststrategy_m_config_transaction where RequestId = ?";
		ResultSet rs = null;
		String res = null;
		try (Connection connection = ConnectionFactory.getConnection();
				PreparedStatement preparedStmt = connection.prepareStatement(query);) {
			preparedStmt.setString(1, requestID);
			rs = preparedStmt.executeQuery();
			if (rs != null) {
				while (rs.next()) {
					res = rs.getString("TestsSelected");
				}
			}
		} catch (SQLException exe) {
			logger.error("SQL Exception in getTestList method " + exe.getMessage());
		} finally {
			DBUtil.close(rs);
		}
		return res;
	}

	/*
	 * Getting Test name with version
	 */
	@SuppressWarnings("unused")
	private String getTestNameAndVesrion(String name) {
		String testNameAndVersion = null;
		String testName = null;
		String testVersion = null;
		if (name != null) {
			testVersion = name.substring(name.lastIndexOf("_") + 1);
			testName = name.substring(name.indexOf("_") + 1);
			testName = testName.substring(testName.indexOf("_") + 1, testName.lastIndexOf("_"));
			testNameAndVersion = testName.concat("-v" + testVersion);
		}
		return testNameAndVersion;
	}

	private String covnertTStoString(Timestamp indate) {
		String dateString = null;
		Date date = new Date();
		date.setTime(indate.getTime());
		dateString = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss").format(date);
		return dateString;
	}

	private String separate(String string) {
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

	private void insertInternetCvrfso(RequestInfoSO request) {
		try (Connection connection = ConnectionFactory.getConnection();
				PreparedStatement prepStmt = connection.prepareStatement(INSERT_INTERNET_LCVRF_SO);) {
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

				if (networkIp == "") {
					prepStmt.setString(1, networkIp);
				} else {
					prepStmt.setNull(1, Types.VARCHAR);
				}
				if (AS != "") {
					prepStmt.setString(2, AS);
				} else {
					prepStmt.setNull(2, Types.VARCHAR);
				}
				if (neighbor1 == "") {
					prepStmt.setString(3, neighbor1);
				} else {
					prepStmt.setNull(3, Types.VARCHAR);
				}
				if (neighbor2 != "") {
					prepStmt.setString(4, neighbor2);
				} else {
					prepStmt.setNull(4, Types.VARCHAR);
				}
				if (neighbor1_remoteAS != "") {
					prepStmt.setString(5, neighbor1_remoteAS);
				} else {
					prepStmt.setNull(5, Types.VARCHAR);
				}

				if (neighbor2_remoteAS != "") {
					prepStmt.setString(6, neighbor2_remoteAS);
				} else {
					prepStmt.setNull(6, Types.VARCHAR);
				}
				if (networkIp_subnetMask != "") {
					prepStmt.setString(7, networkIp_subnetMask);
				} else {
					prepStmt.setNull(7, Types.VARCHAR);
				}
				if (routingProtocol != "") {
					prepStmt.setString(8, routingProtocol);
				} else {
					prepStmt.setNull(8, Types.VARCHAR);
				}
				prepStmt.execute("SET FOREIGN_KEY_CHECKS=0");
				prepStmt.executeUpdate();
			} else {
				prepStmt.setNull(1, Types.VARCHAR);
				prepStmt.setNull(2, Types.VARCHAR);
				prepStmt.setNull(3, Types.VARCHAR);
				prepStmt.setNull(4, Types.VARCHAR);
				prepStmt.setNull(5, Types.VARCHAR);
				prepStmt.setNull(6, Types.VARCHAR);
				prepStmt.setNull(7, Types.VARCHAR);
				prepStmt.setNull(8, Types.VARCHAR);
				prepStmt.execute("SET FOREIGN_KEY_CHECKS=0");
				prepStmt.executeUpdate();
			}
		} catch (SQLException exe) {
			logger.error("SQL Exception in insertInternetCvrfso method " + exe.getMessage());
		}
	}

	private void insertMisArPeSo(RequestInfoSO request) {
		try (Connection connection = ConnectionFactory.getConnection();
				PreparedStatement prepStmt = connection.prepareStatement(INSERT_MIS_AR_PE_SO);) {
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
				if (routerVrfVpnDIp != "") {
					prepStmt.setString(1, routerVrfVpnDIp);
				} else {
					prepStmt.setNull(1, Types.VARCHAR);
				}
				if (routerVrfVpnDGateway != "") {
					prepStmt.setString(2, routerVrfVpnDGateway);
				} else {
					prepStmt.setNull(2, Types.VARCHAR);
				}
				if (fastEthernetIp != "") {
					prepStmt.setString(3, fastEthernetIp);
				} else {
					prepStmt.setNull(3, Types.VARCHAR);
				}
				prepStmt.execute("SET FOREIGN_KEY_CHECKS=0");
				prepStmt.executeUpdate();

			} else {
				prepStmt.setNull(1, Types.VARCHAR);
				prepStmt.setNull(2, Types.VARCHAR);
				prepStmt.setNull(3, Types.VARCHAR);
				prepStmt.execute("SET FOREIGN_KEY_CHECKS=0");
				prepStmt.executeUpdate();
			}
		} catch (SQLException exe) {
			logger.error("SQL Exception in insertMisArPeSO method " + exe.getMessage());
		}
	}

	private void insertDeviceInterfaceSo(RequestInfoSO request) {
		try (Connection connection = ConnectionFactory.getConnection();
				PreparedStatement prepStmt = connection.prepareStatement(INSERT_DEVICE_INTERFACE_SO);) {
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

				if (name != "") {
					prepStmt.setString(1, name);
				} else {
					prepStmt.setNull(1, Types.VARCHAR);
				}
				if (description != "") {
					prepStmt.setString(2, description);
				} else {
					prepStmt.setNull(2, Types.VARCHAR);
				}
				if (ip != "") {
					prepStmt.setString(3, ip);
				} else {
					prepStmt.setNull(3, Types.VARCHAR);
				}
				if (mask != "") {
					prepStmt.setString(4, mask);
				} else {
					prepStmt.setNull(4, Types.VARCHAR);
				}
				if (speed == "") {
					prepStmt.setString(5, speed);
				} else {
					prepStmt.setNull(5, Types.VARCHAR);
				}
				if (encapsulation != "") {
					prepStmt.setString(6, encapsulation);
				} else {
					prepStmt.setNull(6, Types.VARCHAR);
				}
				if (bandwidth != "") {
					prepStmt.setString(7, bandwidth);
				} else {
					prepStmt.setNull(7, Types.VARCHAR);
				}

				prepStmt.execute("SET FOREIGN_KEY_CHECKS=0");
				prepStmt.executeUpdate();
			} else {
				prepStmt.setNull(1, Types.VARCHAR);
				prepStmt.setNull(2, Types.VARCHAR);
				prepStmt.setNull(3, Types.VARCHAR);
				prepStmt.setNull(4, Types.VARCHAR);
				prepStmt.setNull(5, Types.VARCHAR);
				prepStmt.setNull(6, Types.VARCHAR);
				prepStmt.setNull(7, Types.VARCHAR);
				prepStmt.execute("SET FOREIGN_KEY_CHECKS=0");
				prepStmt.executeUpdate();
			}

		} catch (SQLException exe) {
			logger.error("SQL Exception in insertMisArPeSO method " + exe.getMessage());
		}
	}

	private void insertBannerDataTable(String banner) {
		try (Connection connection = ConnectionFactory.getConnection();
				PreparedStatement prepStmt = connection.prepareStatement(INSERT_BANNER_DATA_TABLE);) {
			if (banner != "") {
				prepStmt.setString(1, banner);
			} else {
				prepStmt.setNull(1, Types.VARCHAR);
			}

			prepStmt.execute("SET FOREIGN_KEY_CHECKS=0");
			prepStmt.executeUpdate();
		} catch (SQLException exe) {
			logger.error("SQL Exception in insertBannerDataTable method " + exe.getMessage());
		}
	}

	private MisArPeSO getMisArPeSO(int requestInfoId) {
		MisArPeSO misArPeSo = new MisArPeSO();
		ResultSet resultSet = null;
		try (Connection connection = ConnectionFactory.getConnection();
				PreparedStatement prepStmt = connection.prepareStatement(GET_MIS_AR_PE_SO);) {
			prepStmt.setInt(1, requestInfoId);
			resultSet = prepStmt.executeQuery();
			if (resultSet != null) {
				while (resultSet.next()) {
					misArPeSo.setFastEthernetIp(resultSet.getString("fastEthernetIp"));
					misArPeSo.setRouterVrfVpnDGateway(resultSet.getString("routerVrfVpnDGateway"));
					misArPeSo.setRouterVrfVpnDIp(resultSet.getString("routerVrfVpnDIp"));
				}
			}
		} catch (SQLException exe) {
			logger.error("SQL Exception in getMisArPeSO method " + exe.getMessage());
		} finally {
			DBUtil.close(resultSet);
		}
		return misArPeSo;
	}

	private InternetLcVrfSO getInternetLcVrf(int requestInfoId) {
		InternetLcVrfSO internetLcVrfSO = new InternetLcVrfSO();
		ResultSet resultSet = null;
		try (Connection connection = ConnectionFactory.getConnection();
				PreparedStatement prepStmt = connection.prepareStatement(GET_INTERNET_LCVRF_SO);) {
			prepStmt.setInt(1, requestInfoId);
			resultSet = prepStmt.executeQuery();
			if (resultSet != null) {
				while (resultSet.next()) {
					internetLcVrfSO.setNetworkIp(resultSet.getString("networkIp"));
					internetLcVrfSO.setBgpASNumber(resultSet.getString("asNumber"));
					internetLcVrfSO.setNeighbor1(resultSet.getString("neighbor1"));
					internetLcVrfSO.setNeighbor2(resultSet.getString("neighbor2"));
					internetLcVrfSO.setNeighbor1_remoteAS(resultSet.getString("neighbor1_remoteAS"));
					internetLcVrfSO.setNeighbor2_remoteAS(resultSet.getString("neighbor2_remoteAS"));
					internetLcVrfSO.setRoutingProtocol(resultSet.getString("routingProtocol"));
					internetLcVrfSO.setNetworkIp_subnetMask(resultSet.getString("networkIp_subnetMask"));
				}
			}
		} catch (SQLException exe) {
			logger.error("SQL Exception in getInternetLcVrf method " + exe.getMessage());
		} finally {
			DBUtil.close(resultSet);
		}
		return internetLcVrfSO;
	}

	private DeviceInterfaceSO getDeviceInterfaceSO(int requestInfoId) {
		DeviceInterfaceSO deviceInterfaceSO = new DeviceInterfaceSO();
		ResultSet resultSet = null;
		try (Connection connection = ConnectionFactory.getConnection();
				PreparedStatement prepStmt = connection.prepareStatement(GET_DEVICE_INTERFACE_SO);) {
			prepStmt.setInt(1, requestInfoId);
			resultSet = prepStmt.executeQuery();
			if (resultSet != null) {
				while (resultSet.next()) {
					deviceInterfaceSO.setDescription(resultSet.getString("description"));
					deviceInterfaceSO.setIp(resultSet.getString("ip"));
					deviceInterfaceSO.setEncapsulation(resultSet.getString("encapsulation"));
					deviceInterfaceSO.setMask(resultSet.getString("mask"));
					deviceInterfaceSO.setName(resultSet.getString("name"));
					deviceInterfaceSO.setSpeed(resultSet.getString("speed"));
					deviceInterfaceSO.setBandwidth(resultSet.getString("Bandwidth"));
				}
			}
		} catch (SQLException exe) {
			logger.error("SQL Exception in getDeviceInterfaceSO method " + exe.getMessage());
		} finally {
			DBUtil.close(resultSet);
		}
		return deviceInterfaceSO;
	}

	private void updateRequestInfoSoByAlpReqVersion(String alpReqId, String version, String status, Timestamp endDate,
			String elapsedTime) {
		try (Connection connection = ConnectionFactory.getConnection();
				PreparedStatement prepStmt = connection.prepareStatement(UPDATE_REQUEST_INFO_SO_BY_ALPREQID_VERSION);) {
			prepStmt.setString(1, status);
			prepStmt.setTimestamp(2, endDate);
			prepStmt.setString(3, elapsedTime);
			prepStmt.setString(4, alpReqId);
			prepStmt.setString(5, version);
			prepStmt.executeUpdate();
		} catch (SQLException exe) {
			logger.error("SQL Exception in updateRequestInfoSoByAlpReqVersion method " + exe.getMessage());
		}
	}

	public List<TestStrategyPojo> getAllTestsForTestStrategy() {
		List<TestStrategyPojo> list = new ArrayList<TestStrategyPojo>();
		String query = "select * from t_tststrategy_m_tstdetails";
		ResultSet rs = null;
		try (Connection connection = ConnectionFactory.getConnection();
				PreparedStatement pst = connection.prepareStatement(query);) {

			rs = pst.executeQuery();
			TestStrategyPojo request;
			while (rs.next()) {
				request = new TestStrategyPojo();
				request.setTestName(rs.getString("test_name"));
				request.setVersion(rs.getString("version"));
				request.setTestId(rs.getString("id"));
				request.setVendor(rs.getString("vendor"));
				request.setDeviceFamily(rs.getString("device_family"));
				request.setDeviceModel(rs.getString("device_model"));

				request.setTest_category(rs.getString("test_category"));
				request.setOs(rs.getString("os"));
				request.setOsVersion(rs.getString("os_version"));
				request.setRegion(rs.getString("region"));
				request.setCreatedDate(rs.getString("created_on"));
				request.setCreatedBy(rs.getString("created_by"));

				request.setEnabled(rs.getBoolean("is_enabled"));

				list.add(request);
			}
		} catch (SQLException exe) {
			logger.error("SQL Exception in getAllTestsForTestStrategy method " + exe.getMessage());
		} finally {
			DBUtil.close(rs);
		}
		return list;
	}

	public List<TestStrategyPojo> findById(String key, List temp) {
		String query = null;
		List<TestStrategyPojo> requestInfoList = null;
		TestStrategyPojo request = null;
		ResultSet rs = null;
		int value = 0;
		requestInfoList = new ArrayList<TestStrategyPojo>();
		for (int i = 0; i < temp.size(); i++) {
			value = (int) temp.get(i);

			if (key.equals("id")) {
				query = "SELECT * FROM t_tststrategy_m_tstdetails where id LIKE ?";
			}

			try (Connection connection = ConnectionFactory.getConnection();
					PreparedStatement pst = connection.prepareStatement(query);) {

				pst.setString(1, value + "%");

				rs = pst.executeQuery();
				while (rs.next()) {

					request = new TestStrategyPojo();
					request.setTestName(rs.getString("test_name"));
					request.setVersion(rs.getString("version"));
					request.setTestId(rs.getString("id"));
					request.setVendor(rs.getString("vendor"));
					request.setDeviceFamily(rs.getString("device_family"));
					request.setDeviceModel(rs.getString("device_model"));

					request.setTest_category(rs.getString("test_category"));
					request.setOs(rs.getString("os"));
					request.setOsVersion(rs.getString("os_version"));
					request.setRegion(rs.getString("region"));
					request.setCreatedDate(rs.getString("created_on"));
					request.setCreatedBy(rs.getString("created_by"));

					request.setEnabled(rs.getBoolean("is_enabled"));
					requestInfoList.add(request);
				}

			} catch (SQLException exe) {
				logger.error("SQL Exception in findById method " + exe.getMessage());
			} finally {
				DBUtil.close(rs);
			}
		}
		return requestInfoList;
	}

	public List<TestBundlePojo> findTestIdList(int bundleId) {
		String query = null;
		List<TestBundlePojo> requestInfoList = new ArrayList<TestBundlePojo>();

		TestBundlePojo obj = null;
		ResultSet rs = null;

		query = "SELECT test_id FROM t_tststrategy_j_test_bundle where bundle_id LIKE ?";

		try (Connection connection = ConnectionFactory.getConnection();
				PreparedStatement pst = connection.prepareStatement(query);) {

			pst.setString(1, bundleId + "%");

			rs = pst.executeQuery();
			while (rs.next()) {
				obj = new TestBundlePojo();
				obj.setTest_id(rs.getInt("test_id"));

				requestInfoList.add(obj);
			}

		} catch (SQLException exe) {
			logger.error("SQL Exception in findTestIdList method " + exe.getMessage());
		} finally {
			DBUtil.close(rs);
		}

		return requestInfoList;
	}

	public List<TestStrategyPojo> getTestsForTestStrategyOnId(int testId) {
		List<TestStrategyPojo> list = new ArrayList<TestStrategyPojo>();
		String query = "select * from t_tststrategy_m_tstdetails where id LIKE ?";
		ResultSet rs = null;
		try (Connection connection = ConnectionFactory.getConnection();
				PreparedStatement pst = connection.prepareStatement(query);) {
			pst.setString(1, testId + "%");
			rs = pst.executeQuery();
			TestStrategyPojo request;
			while (rs.next()) {
				request = new TestStrategyPojo();
				request.setTestName(rs.getString("test_name"));
				request.setVersion(rs.getString("version"));
				request.setTestId(rs.getString("id"));
				request.setVendor(rs.getString("vendor"));
				request.setDeviceFamily(rs.getString("device_family"));
				request.setDeviceModel(rs.getString("device_model"));

				request.setTest_category(rs.getString("test_category"));
				request.setOs(rs.getString("os"));
				request.setOsVersion(rs.getString("os_version"));
				request.setRegion(rs.getString("region"));
				request.setCreatedDate(rs.getString("created_on"));
				request.setCreatedBy(rs.getString("created_by"));

				request.setEnabled(rs.getBoolean("is_enabled"));

				list.add(request);
			}
		} catch (SQLException exe) {
			logger.error("SQL Exception in getTestsForTestStrategyOnId method " + exe.getMessage());
		} finally {
			DBUtil.close(rs);
		}
		return list;
	}

	public List<TestStrategyPojo> findByForSearch(String key, String value) {
		String query = null;
		List<TestStrategyPojo> requestInfoList = null;
		TestStrategyPojo request = null;
		ResultSet rs = null;

		if (key.equals("Device Family")) {
			query = "SELECT * FROM t_tststrategy_m_tstdetails where device_family LIKE ?";
		} else if (key.equals("Vendor")) {
			query = "SELECT * FROM t_tststrategy_m_tstdetails where vendor LIKE ?";
		} else if (key.equals("OS")) {
			query = "SELECT * FROM t_tststrategy_m_tstdetails where os LIKE ?";
		} else if (key.equals("OS Version")) {
			query = "SELECT * FROM t_tststrategy_m_tstdetails where os_version LIKE ?";
		} else if (key.equals("Test Name")) {
			query = "SELECT * FROM t_tststrategy_m_tstdetails where test_name LIKE ?";
		} else if (key.equals("Region")) {
			query = "SELECT * FROM t_tststrategy_m_tstdetails where region LIKE ?";
		}

		try (Connection connection = ConnectionFactory.getConnection();
				PreparedStatement pst = connection.prepareStatement(query);) {
			requestInfoList = new ArrayList<TestStrategyPojo>();
			pst.setString(1, value + "%");

			rs = pst.executeQuery();
			while (rs.next()) {

				request = new TestStrategyPojo();
				request.setTestName(rs.getString("test_name"));
				request.setVersion(rs.getString("version"));
				request.setTestId(rs.getString("id"));
				request.setVendor(rs.getString("vendor"));
				request.setDeviceFamily(rs.getString("device_family"));
				request.setDeviceModel(rs.getString("device_model"));

				request.setTest_category(rs.getString("test_category"));
				request.setOs(rs.getString("os"));
				request.setOsVersion(rs.getString("os_version"));
				request.setRegion(rs.getString("region"));
				request.setCreatedDate(rs.getString("created_on"));
				request.setCreatedBy(rs.getString("created_by"));

				request.setEnabled(rs.getBoolean("is_enabled"));

				requestInfoList.add(request);
			}
		} catch (SQLException exe) {
			logger.error("SQL Exception in findByForSearch method " + exe.getMessage());
		} finally {
			DBUtil.close(rs);
		}
		return requestInfoList;
	}

	public List<Integer> findBundleId(int testId) {
		String query = null;
		List<Integer> bundleId = new ArrayList<Integer>();

		ResultSet rs = null;

		query = "SELECT bundle_id FROM t_tststrategy_j_test_bundle where test_id LIKE ?";

		try (Connection connection = ConnectionFactory.getConnection();
				PreparedStatement pst = connection.prepareStatement(query);) {

			pst.setString(1, testId + "%");

			rs = pst.executeQuery();
			while (rs.next()) {

				bundleId.add(rs.getInt("bundle_id"));

			}

		} catch (SQLException exe) {
			logger.error("SQL Exception in findBundleId method " + exe.getMessage());
		} finally {
			DBUtil.close(rs);
		}

		return bundleId;
	}

	public List<TestDetail> getAllTestsForSearch(int i, String tempTestCategoryName) {
		List<TestDetail> list = new ArrayList<TestDetail>();
		String query = "select * from t_tststrategy_m_tstdetails where id = ? and test_category = ?";
		ResultSet rs = null;
		try (Connection connection = ConnectionFactory.getConnection();
				PreparedStatement pst = connection.prepareStatement(query);) {
			pst.setInt(1, i);
			pst.setString(2, tempTestCategoryName);
			rs = pst.executeQuery();
			TestDetail request;
			while (rs.next()) {
				request = new TestDetail();
				request.setTestName(rs.getString("test_name"));
				request.setVersion(rs.getString("version"));
				request.setId(rs.getInt("id"));
				request.setVendor(rs.getString("vendor"));
				request.setDeviceFamily(rs.getString("device_family"));
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
		} catch (SQLException exe) {
			logger.error("SQL Exception in getAllTestsForSearch method " + exe.getMessage());
		} finally {
			DBUtil.close(rs);
		}
		return list;
	}

	public List<TestDetail> getBundleView(int testId) {
		List<TestDetail> list = new ArrayList<TestDetail>();
		String query = "select * from t_tststrategy_m_tstdetails where id LIKE ?";
		String queryTstRules = "select * from t_tststrategy_m_tstrules where test_name=?";
		ResultSet rs = null, rs1 = null;
		List<TestRules> rulelist = new ArrayList<TestRules>();
		try (Connection connection = ConnectionFactory.getConnection();
				PreparedStatement pst = connection.prepareStatement(query);) {
			pst.setString(1, testId + "%");
			rs = pst.executeQuery();
			TestDetail request;
			while (rs.next()) {
				request = new TestDetail();
				request.setTestName(rs.getString("test_name"));
				request.setVersion(rs.getString("version"));
				request.setTestId(rs.getString("id"));
				request.setVendor(rs.getString("vendor"));
				request.setTestConnectionProtocol(rs.getString("test_connection_protocol"));
				request.setTestCommand(rs.getString("test_command"));
				request.setTestCategory(rs.getString("test_category"));

				try {

					PreparedStatement tstRulesPs = connection.prepareStatement(queryTstRules);

					tstRulesPs.setString(1, request.getTestId());
					rs1 = tstRulesPs.executeQuery();
					while (rs1.next()) {
						TestRules rule = new TestRules();
						rule.setId(rs1.getInt("id"));
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
						if (rs1.getString("data_type").equals("Text")) {
							request.setText_attributes(rulelist);
						}
						if (rs1.getString("data_type").equals("Snippet")) {
							request.setSnippet_attributes(rulelist);
						}
						if (rs1.getString("data_type").equals("Section")) {
							request.setSection_attributes(rulelist);
						}
						if (rs1.getString("data_type").equals("Table")) {
							request.setTable_attributes(rulelist);
						}
						if (rs1.getString("data_type").equals("Keyword")) {
							request.setKeyword_attributes(rulelist);
						}

					}
				} catch (SQLException exe) {
					logger.error("SQL Exception in getBundleViewDB 2 method " + exe.getMessage());
				}

				list.add(request);
			}
		} catch (SQLException exe) {
			logger.error("SQL Exception in getBundleView DB1 method " + exe.getMessage());
		} finally {
			DBUtil.close(rs);
		}
		return list;
	}

	public List<TestDetail> findTestId(int bundleId) {
		String query = null;
		List<TestDetail> requestInfoList = new ArrayList<TestDetail>();

		TestDetail obj = null;
		ResultSet rs = null;

		query = "SELECT test_id FROM t_tststrategy_j_test_bundle where bundle_id LIKE ?";

		try (Connection connection = ConnectionFactory.getConnection();
				PreparedStatement pst = connection.prepareStatement(query);) {

			pst.setString(1, bundleId + "%");

			rs = pst.executeQuery();
			while (rs.next()) {
				obj = new TestDetail();
				obj.setId(rs.getInt("test_id"));

				requestInfoList.add(obj);
			}

		} catch (SQLException exe) {
			logger.error("SQL Exception in findTestId method " + exe.getMessage());
		} finally {
			DBUtil.close(rs);
		}

		return requestInfoList;
	}

}
