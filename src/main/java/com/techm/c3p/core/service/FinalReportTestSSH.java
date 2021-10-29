package com.techm.c3p.core.service;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.techm.c3p.core.dao.RequestInfoDao;
import com.techm.c3p.core.pojo.CreateConfigRequest;
import com.techm.c3p.core.pojo.CreateConfigRequestDCM;
import com.techm.c3p.core.pojo.ReoprtFlags;
import com.techm.c3p.core.utility.C3PCoreAppLabels;
import com.techm.c3p.core.utility.InvokeFtl;
import com.techm.c3p.core.utility.TextReport;

@Service
public class FinalReportTestSSH {
	private static final Logger logger = LogManager.getLogger(FinalReportTestSSH.class);

	@Autowired
	private RequestInfoDao requestInfoDao;
	
	@SuppressWarnings("unused")
	public void FlagCheckTest(CreateConfigRequestDCM configRequest) throws IOException {
		try {
			
			Map<String, String> hmapResult = new HashMap<String, String>();
			CreateConfigRequest createConfigRequest = new CreateConfigRequest();
			InvokeFtl invokeFtl = new InvokeFtl();

			List<ReoprtFlags> listFlag = requestInfoDao.getReportsInfoForAllRequestsDB();

			ReoprtFlags reoprtFlags = new ReoprtFlags();

			createConfigRequest.setHostname(configRequest.getHostname());
			createConfigRequest.setSiteid(configRequest.getSiteid());
			createConfigRequest.setManagementIp(configRequest.getManagementIp());
			createConfigRequest.setCustomer(configRequest.getCustomer());
			createConfigRequest.setModel(configRequest.getModel());
			if (null != configRequest.getThroughput() || configRequest.getThroughput() != "") {
				createConfigRequest.setThroughput(configRequest.getThroughput());
			}
			createConfigRequest.setFrameLoss(configRequest.getFrameloss());
			createConfigRequest.setLatency(configRequest.getLatency());
			createConfigRequest.setNetwork_test_interfaceStatus(configRequest.getNetwork_test_interfaceStatus());
			createConfigRequest.setNetwork_test_wanInterface(configRequest.getNetwork_test_wanInterface());
			createConfigRequest.setNetwork_test_platformIOS(configRequest.getNetwork_test_platformIOS());
			createConfigRequest.setNetwork_test_BGPNeighbor(configRequest.getNetwork_test_BGPNeighbor());
			createConfigRequest.setNetworkStatusValue(configRequest.getNetworkStatusValue());
			createConfigRequest.setNetworkProtocolValue(configRequest.getNetworkProtocolValue());

			for (int i = 0; i < listFlag.size(); i++) {
				if (listFlag.get(i).getAlphanumeric_req_id().equalsIgnoreCase(configRequest.getRequestId())) {
					reoprtFlags = listFlag.get(i);
				}
			}

			if (reoprtFlags.getGenerate_config() != 2 && reoprtFlags.getDeliever_config() != 2
					&& reoprtFlags.getHealth_checkup() != 2 && reoprtFlags.getApplication_test() != 2
					&& reoprtFlags.getNetwork_test() != 2) {
				requestInfoDao.editRequestforReportWebserviceInfo(configRequest.getRequestId(),
						Double.toString(configRequest.getRequest_version()), "customer_report", "1", "Success");
				// customer report for success
				String response = invokeFtl.generateCustomerReportSuccess(createConfigRequest);
				
				TextReport.writeFile(
						C3PCoreAppLabels.RESPONSE_DOWNLOAD_PATH.getValue(), configRequest.getRequestId() + "V"
								+ Double.toString(configRequest.getRequest_version()) + "_customerReport.txt",
						response);
			}

			else {
				// customer report for failure

				requestInfoDao.editRequestforReportWebserviceInfo(configRequest.getRequestId(),
						Double.toString(configRequest.getRequest_version()), "customer_report", "2", "Failure");

				if (reoprtFlags.getGenerate_config() == 2) {
					createConfigRequest.setGenerate_config("Failed");
				}
				if (reoprtFlags.getDeliever_config() == 2) {
					createConfigRequest.setDeliever_config("Failed");
				}
				if (reoprtFlags.getApplication_test() == 2) {
					createConfigRequest.setApplication_test("Failed");
				}
				if (reoprtFlags.getHealth_checkup() == 2) {
					createConfigRequest.setHealth_checkup("Failed");
				}
				if (reoprtFlags.getNetwork_test() == 2) {
					createConfigRequest.setNetwork_test("Failed");
				}
				createConfigRequest.setNetworkStatusValue(configRequest.getNetworkStatusValue());
				createConfigRequest.setNetworkProtocolValue(configRequest.getNetworkProtocolValue());
				String response = invokeFtl.generateCustomerReportFailure(createConfigRequest);		
				
				TextReport.writeFile(
						C3PCoreAppLabels.RESPONSE_DOWNLOAD_PATH.getValue(), configRequest.getRequestId() + "V"
								+ Double.toString(configRequest.getRequest_version()) + "_customerReport.txt",
						response);
			}

		} catch (Exception exe) {
			logger.error("Exception - FlagCheckTest- "+exe);
		}
	}

}