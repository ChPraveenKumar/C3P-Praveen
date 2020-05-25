package com.techm.orion.service;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import com.techm.orion.dao.RequestInfoDao;
import com.techm.orion.pojo.CreateConfigRequest;
import com.techm.orion.pojo.CreateConfigRequestDCM;
import com.techm.orion.pojo.ReoprtFlags;
import com.techm.orion.utility.InvokeFtl;
import com.techm.orion.utility.TextReport;

public class FinalReportTestSSH {

	public static String TSA_PROPERTIES_FILE = "TSA.properties";
	public static final Properties TSA_PROPERTIES = new Properties();

	@SuppressWarnings("unused")
	public void FlagCheckTest(CreateConfigRequestDCM configRequest) throws IOException {
		try {

			RequestInfoDao requestInfoDao=new RequestInfoDao();
			Map<String,String> hmapResult = new HashMap<String,String>();
			CreateConfigRequest createConfigRequest=new CreateConfigRequest();
			InvokeFtl invokeFtl=new InvokeFtl();
			
			
			List<ReoprtFlags> listFlag=requestInfoDao.getReportsInfoForAllRequestsDB();
			
			ReoprtFlags reoprtFlags=new ReoprtFlags();
			
			createConfigRequest.setHostname(configRequest.getHostname());
			createConfigRequest.setSiteid(configRequest.getSiteid());
			createConfigRequest.setManagementIp(configRequest.getManagementIp());
			createConfigRequest.setCustomer(configRequest.getCustomer());
			createConfigRequest.setModel(configRequest.getModel());
			if(null!=configRequest.getThroughput() || configRequest.getThroughput()!="")
			{
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
			
			 for(int i=0; i<listFlag.size();i++)
			    {
				if(listFlag.get(i).getAlphanumeric_req_id().equalsIgnoreCase(configRequest.getRequestId()))
				{
					reoprtFlags=listFlag.get(i);
				}
			    }
				
			 if(reoprtFlags.getGenerate_config()!=2 && reoprtFlags.getDeliever_config()!=2 && reoprtFlags.getHealth_checkup()!=2 && reoprtFlags.getApplication_test()!=2 && reoprtFlags.getNetwork_test()!=2)
			 {
				 requestInfoDao.editRequestforReportWebserviceInfo(configRequest.getRequestId(),Double.toString(configRequest.getRequest_version()),"customer_report","1","Success");
				 //customer report for success
				 String response= invokeFtl.generateCustomerReportSuccess(createConfigRequest);
				   try {
						String responseDownloadPath = TelnetCommunicationSSH.TSA_PROPERTIES
							.getProperty("responseDownloadPath");
						TextReport.writeFile(responseDownloadPath, configRequest.getRequestId()+"V"+Double.toString(configRequest.getRequest_version())
							+ "_customerReport.txt", response);
						
					    } catch (IOException exe) {
					    	exe.printStackTrace();

					    }
			 }
				
			 else
			 {
				 //customer report for failure
				 
				 requestInfoDao.editRequestforReportWebserviceInfo(configRequest.getRequestId(),Double.toString(configRequest.getRequest_version()),"customer_report","2","Failure");
				 
				 if(reoprtFlags.getGenerate_config()==2)
				 {
					 createConfigRequest.setGenerate_config("Failed");
				 }
				 if(reoprtFlags.getDeliever_config()==2)
				 {
					 createConfigRequest.setDeliever_config("Failed");
				 }
				 if(reoprtFlags.getApplication_test()==2)
				 {
					 createConfigRequest.setApplication_test("Failed");
				 }
				 if(reoprtFlags.getHealth_checkup()==2)
				 {
					 createConfigRequest.setHealth_checkup("Failed");
				 }
				 if(reoprtFlags.getNetwork_test()==2)
				 {
					 createConfigRequest.setNetwork_test("Failed");
				 }
				 createConfigRequest.setNetworkStatusValue(configRequest.getNetworkStatusValue());
				 createConfigRequest.setNetworkProtocolValue(configRequest.getNetworkProtocolValue());
				 String response= invokeFtl.generateCustomerReportFailure(createConfigRequest);
				   try {
						String responseDownloadPath = TelnetCommunicationSSH.TSA_PROPERTIES
							.getProperty("responseDownloadPath");
						TextReport.writeFile(responseDownloadPath, configRequest.getRequestId()+"V"+Double.toString(configRequest.getRequest_version())
							+ "_customerReport.txt", response);
						
					    } catch (IOException exe) {
					    	exe.printStackTrace();

					    }
			 }
			
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	
	public static boolean loadProperties() throws IOException {
		InputStream tsaPropFile = Thread.currentThread()
				.getContextClassLoader()
				.getResourceAsStream(TSA_PROPERTIES_FILE);

		try {
			TSA_PROPERTIES.load(tsaPropFile);
		} catch (IOException exc) {
			exc.printStackTrace();
			return false;
		}
		return false;
	}

	
	
}