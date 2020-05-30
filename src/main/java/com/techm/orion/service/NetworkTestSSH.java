package com.techm.orion.service;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Properties;
import java.util.Scanner;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;
import com.techm.orion.dao.RequestInfoDao;
import com.techm.orion.pojo.CreateConfigRequestDCM;
import com.techm.orion.pojo.UserPojo;

public class NetworkTestSSH {

	public static String TSA_PROPERTIES_FILE = "TSA.properties";
	public static final Properties TSA_PROPERTIES = new Properties();
	
	@SuppressWarnings("unused")
	public void NetworkTest(CreateConfigRequestDCM configRequest) throws IOException {
		try {
			
			RequestInfoDao requestInfoDao=new RequestInfoDao();
			NetworkTestSSH.loadProperties();
			UserPojo userPojo=new UserPojo();
			userPojo=requestInfoDao.getRouterCredentials();
			String host = configRequest.getManagementIp();
			String user = userPojo.getUsername();
			String password = userPojo.getPassword();
			String port = NetworkTestSSH.TSA_PROPERTIES
					.getProperty("portSSH");
			ArrayList<String> commandToPush = new ArrayList<String>();
			JSch jsch = new JSch();
			Session session = jsch.getSession(user, host,
					Integer.parseInt(port));
			Properties config = new Properties();
			config.put("StrictHostKeyChecking", "no");
			session.setConfig(config);
			session.setPassword(password);
			session.connect();
			try {
				Channel channel = session.openChannel("shell");
				OutputStream ops = channel.getOutputStream();
				PrintStream ps = new PrintStream(ops, true);
				System.out.println("Channel Connected to machine " + host
						+ " server");
				channel.connect();
				InputStream input = channel.getInputStream();
				/*int commandCount = Integer
						.parseInt(NetworkTestSSH.TSA_PROPERTIES
								.getProperty("networkTestCommand"));
				for (int count = 1; count <=commandCount; count++) {
					ps.println(NetworkTestSSH.TSA_PROPERTIES
							.getProperty("NT" + count));

				}*/
				ps.println("terminal length 0");
				if(configRequest.getInterfaceStatus().equalsIgnoreCase("1"))
				{
					ps.println("show ip interface brief");
					printResult(input, channel,configRequest.getRequestId(),Double.toString(configRequest.getRequest_version()));
					configRequest.setNetwork_test_interfaceStatus("Passed");
				}
				if(configRequest.getWanInterface().equalsIgnoreCase("1"))
				{
					ps.println("show interface "+configRequest.getName());
					printResult(input, channel,configRequest.getRequestId(),Double.toString(configRequest.getRequest_version()));
					configRequest.setNetwork_test_wanInterface("Passed");
				}
				if(configRequest.getPlatformIOS().equalsIgnoreCase("1"))
				{
					ps.println("show version");
					printResult(input, channel,configRequest.getRequestId(),Double.toString(configRequest.getRequest_version()));
					configRequest.setNetwork_test_platformIOS("Passed");
				}
				if(configRequest.getBGPNeighbor().equalsIgnoreCase("1"))
				{
					ps.println("sh ip bgp summary");
					printResult(input, channel,configRequest.getRequestId(),Double.toString(configRequest.getRequest_version()));
					configRequest.setNetwork_test_BGPNeighbor("Passed");
				}
				
				try {
					Thread.sleep(2000);
				} catch (Exception ee) {
				}
				
				printResult(input, channel,configRequest.getRequestId(),Double.toString(configRequest.getRequest_version()));
				
				
				
				String content=validateNetworkTest(configRequest);
				if(content!=""){
				if(content.contains(configRequest.getName()) && content.contains("up")&&content.contains("down"))
				{
					configRequest.setNetworkStatusValue("up");
					configRequest.setNetworkProtocolValue("down");
				}
				else if(content.contains(configRequest.getName()) && content.contains("up") && !content.contains("down"))
				{
					configRequest.setNetworkStatusValue("up");
					configRequest.setNetworkProtocolValue("up");
				}
				else
				{
					configRequest.setNetworkStatusValue("down");
					configRequest.setNetworkProtocolValue("down");
				}
				}
				//working on simulator so condition has been set to true
				if(configRequest.getInterfaceStatus().equalsIgnoreCase("1")||configRequest.getWanInterface().equalsIgnoreCase("1")||configRequest.getPlatformIOS().equalsIgnoreCase("1")||configRequest.getBGPNeighbor().equalsIgnoreCase("1"))
				{
					
					requestInfoDao.updateNetworkTestStatus(configRequest.getRequestId(),Double.toString(configRequest.getRequest_version()),Integer.parseInt(configRequest.getInterfaceStatus()),Integer.parseInt(configRequest.getWanInterface()),Integer.parseInt(configRequest.getPlatformIOS()),Integer.parseInt(configRequest.getBGPNeighbor()));
					requestInfoDao.editRequestforReportWebserviceInfo(configRequest.getRequestId(),Double.toString(configRequest.getRequest_version()),"network_test","1","In Progress");
					
					HealthCheckTestSSH healthCheckTestSSH=new HealthCheckTestSSH();
					healthCheckTestSSH.HealthCheckTest(configRequest);
					
				}
				else if(configRequest.getInterfaceStatus().equalsIgnoreCase("0")&& configRequest.getWanInterface().equalsIgnoreCase("0")&&configRequest.getPlatformIOS().equalsIgnoreCase("0")&&configRequest.getBGPNeighbor().equalsIgnoreCase("0"))
				{
					HealthCheckTestSSH healthCheckTestSSH=new HealthCheckTestSSH();
					healthCheckTestSSH.HealthCheckTest(configRequest);
					
				}
				else
				{
					//db call to set flag false
					requestInfoDao.updateNetworkTestStatus(configRequest.getRequestId(),Double.toString(configRequest.getRequest_version()),Integer.parseInt(configRequest.getInterfaceStatus()),Integer.parseInt(configRequest.getWanInterface()),Integer.parseInt(configRequest.getPlatformIOS()),Integer.parseInt(configRequest.getBGPNeighbor()));
					requestInfoDao.editRequestforReportWebserviceInfo(configRequest.getRequestId(),Double.toString(configRequest.getRequest_version()),"network_test","2","Failure");
				}
				channel.disconnect();
				session.disconnect();
				System.out.println("DONE");
			} catch (IOException ex) {
				ex.printStackTrace();
			}

			session.disconnect();
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

	private static void printResult(InputStream input, Channel channel,String requestID,String version)
			throws Exception {
		BufferedWriter bw = null;
		FileWriter fw = null;
		int SIZE = 1024;
		byte[] tmp = new byte[SIZE];
		
		while (input.available() > 0) {
			int i = input.read(tmp, 0, SIZE);
			if (i < 0)
				break;
			/*System.out.print(new String(tmp, 0, i));*/
			
			String s=new String(tmp, 0, i);
			if(!(s.equals(""))) { 
           	 System.out.print(s);
           	 
           	String filepath = NetworkTestSSH.TSA_PROPERTIES
					.getProperty("responseDownloadPath")+"//"+requestID+"V"+version+"_networkTest.txt";
                File file = new File(filepath);
               
                
    			// if file doesnt exists, then create it
    			if (!file.exists()) {
    				file.createNewFile();
    				
    				fw = new FileWriter(file, true);
        			bw = new BufferedWriter(fw);
    				bw.append(s);
    				bw.close();
    			}
    			else{
    				fw = new FileWriter(file.getAbsoluteFile(), true);
        			bw = new BufferedWriter(fw);
    				bw.append(s);
    				bw.close();
    			}
			}
			
		}
		if (channel.isClosed()) {
			System.out.println("exit-status: " + channel.getExitStatus());

		}
		try {
			Thread.sleep(1000);
		} catch (Exception ee) {
		}

	}
	
	
		
		
	@SuppressWarnings("resource")
	public String validateNetworkTest(CreateConfigRequestDCM configRequest)
			throws Exception {
		
		String content = "";
		String path=NetworkTestSSH.TSA_PROPERTIES
				.getProperty("responseDownloadPath")+"//"+configRequest.getRequestId()+"V"+configRequest.getRequest_version()+"_networkTest.txt";
		
		File file =new File(path);
        Scanner in = null;
        try {
            in = new Scanner(file);
            while(in.hasNext())
            {
                String line=in.nextLine();
                if(line.contains(configRequest.getName())){
                	System.out.println(line);
                	content=line;
                    break;
                }
                        
            }
            
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
		return content;
	}

}