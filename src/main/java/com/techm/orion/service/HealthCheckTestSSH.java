package com.techm.orion.service;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Scanner;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;
import com.techm.orion.dao.RequestInfoDao;
import com.techm.orion.pojo.CreateConfigRequestDCM;
import com.techm.orion.pojo.UserPojo;

public class HealthCheckTestSSH {

	public static String TSA_PROPERTIES_FILE = "TSA.properties";
	public static final Properties TSA_PROPERTIES = new Properties();
	//CreateConfigRequestDCM configRequest=new CreateConfigRequestDCM();
	/*public HealthCheckTestSSH(CreateConfigRequestDCM list) {
//		this();
		this.configRequest = list;
	}*/
	
	//@SuppressWarnings("unused")
	public void HealthCheckTest(CreateConfigRequestDCM configRequest) throws IOException {
	/*@Override
	public void run() {*/
		try {

			RequestInfoDao requestInfoDao=new RequestInfoDao();
			Map<String,String> hmapResult = new HashMap<String,String>();
			
			
			RegexTestHealthCheck regexTestHealthCheck=new RegexTestHealthCheck();
			CSVWriteAndConnectPython csvWriteAndConnectPython=new CSVWriteAndConnectPython();
			FinalReportTestSSH finalReportTestSSH=new FinalReportTestSSH();
			HealthCheckTestSSH.loadProperties();
			String host =configRequest.getManagementIp();
			UserPojo userPojo=new UserPojo();
			userPojo=requestInfoDao.getRouterCredentials();
			
			String user = userPojo.getUsername();
			String password = userPojo.getPassword();
			String port = HealthCheckTestSSH.TSA_PROPERTIES
					.getProperty("portSSH");
			String throughput="";
			String frameloss="";
			String latency="";
			//ArrayList<String> commandToPush = new ArrayList<String>();
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
				//for latency and frame loss test
				if(configRequest.getFrameLossTest().equalsIgnoreCase("1")|| configRequest.getLatencyTest().equalsIgnoreCase("1")){
					cmdPingCall(configRequest.getRequestId(),Double.toString(configRequest.getRequest_version()),configRequest.getManagementIp());
					printResult(input,configRequest.getRequestId(),Double.toString(configRequest.getRequest_version())); 
					printResult(input,configRequest.getRequestId(),Double.toString(configRequest.getRequest_version()));
					hmapResult=regexTestHealthCheck.PreValidationForHealthCheckPing(configRequest.getRequestId(),Double.toString(configRequest.getRequest_version()));
					for (Map.Entry<String, String> entry : hmapResult.entrySet()) {
					    
					    if(entry.getKey()=="frameloss")
					    {
					    	frameloss=entry.getValue();
					    	
					    }
					    if(entry.getKey()=="latency")
					    {
					    	latency=entry.getValue();
					    	
					    }
					    
					   
					}			
					configRequest.setFrameloss(frameloss);
					configRequest.setLatency(latency);
					requestInfoDao.updateHealthCheckTestStatus(configRequest.getRequestId(),Double.toString(configRequest.getRequest_version()), 0, 1, 1);
				}
				
				//for throughput test
					
				String resultAnalyser="";
					
					if(configRequest.getThroughputTest().equalsIgnoreCase("1")){
						String readFile = readFile();

						ps.println(readFile);
						try {
							Thread.sleep(1000);
						} catch (Exception ee) {
						}
					
						cmdCall(configRequest.getRequestId(),Double.toString(configRequest.getRequest_version()),configRequest.getManagementIp());
					try {
						Thread.sleep(1000);
					} catch (Exception ee) {
					}
					printResult(input,configRequest.getRequestId(),Double.toString(configRequest.getRequest_version()));   
					
					channel.disconnect();
				session.disconnect();
				
				throughput=regexTestHealthCheck.PreValidationForHealthCheckThroughput(configRequest.getRequestId(),Double.toString(configRequest.getRequest_version()));
				
				
				
				configRequest.setThroughput(throughput);
				//resultAnalyser=csvWriteAndConnectPython.ReadWriteAndConnectAnalyser(configRequest);
					}
				
				
				
				
				//error code validation
				if(resultAnalyser.equalsIgnoreCase("Pass"))
				{
					requestInfoDao.updateHealthCheckTestStatus(configRequest.getRequestId(),Double.toString(configRequest.getRequest_version()), Integer.parseInt(configRequest.getThroughputTest()), Integer.parseInt(configRequest.getFrameLossTest()), Integer.parseInt(configRequest.getLatencyTest()));requestInfoDao.updateHealthCheckTestStatus(configRequest.getRequestId(),Double.toString(configRequest.getRequest_version()), 1, 1, 1);
					requestInfoDao.editRequestforReportWebserviceInfo(configRequest.getRequestId(),Double.toString(configRequest.getRequest_version()),"health_check","1","In Progress");
					//to create final report if success
					finalReportTestSSH.FlagCheckTest(configRequest);
				}
				/*else if ((configRequest.getFrameLossTest().equalsIgnoreCase("1")|| configRequest.getLatencyTest().equalsIgnoreCase("1"))&& configRequest.getThroughputTest().equalsIgnoreCase("1")) {
					
				}*/
				else if(resultAnalyser.equalsIgnoreCase("Fail"))
				{
					//db call for flag set false
					requestInfoDao.updateHealthCheckTestStatus(configRequest.getRequestId(),Double.toString(configRequest.getRequest_version()), 2, Integer.parseInt(configRequest.getFrameLossTest()), Integer.parseInt(configRequest.getLatencyTest()));
					requestInfoDao.editRequestforReportWebserviceInfo(configRequest.getRequestId(),Double.toString(configRequest.getRequest_version()),"health_check","2","Failure");
					//to create final report if failure
					finalReportTestSSH.FlagCheckTest(configRequest);
				}
				else{
					
					requestInfoDao.updateHealthCheckTestStatus(configRequest.getRequestId(),Double.toString(configRequest.getRequest_version()), Integer.parseInt(configRequest.getThroughputTest()), Integer.parseInt(configRequest.getFrameLossTest()), Integer.parseInt(configRequest.getLatencyTest()));
					requestInfoDao.editRequestforReportWebserviceInfo(configRequest.getRequestId(),Double.toString(configRequest.getRequest_version()),"health_check","1","In Progress");
					//to create final report if success
					finalReportTestSSH.FlagCheckTest(configRequest);
					
				}
				System.out.println("DONE");
			} catch (IOException ex) {
				ex.printStackTrace();
			}

			session.disconnect();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	private static String readFile() throws IOException {
		String responseDownloadPath = TelnetCommunicationSSH.TSA_PROPERTIES
				.getProperty("responseDownloadPath");
		
		BufferedReader br = new BufferedReader(new FileReader(
				responseDownloadPath+"//"+"HealthcheckTestCommand.txt"));
		
        //BufferedReader br = new BufferedReader(new FileReader("D:/C3P/New folder/HealthcheckTestCommand.txt"));
        try {
            StringBuilder sb = new StringBuilder();
            String line = br.readLine();

            while (line != null) {
                sb.append(line);
                sb.append("\n");
                line = br.readLine();
            }
            return sb.toString();
        } finally {
            br.close();
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

	private static void printResult(InputStream input,String requestID,String version)
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
    				.getProperty("responseDownloadPath")+"//"+requestID+"V"+version+"_HealthCheck.txt";
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
		/*if (channel.isClosed()) {
			System.out.println("exit-status: " + channel.getExitStatus());

		}*/
		

	}
	private static void cmdCall(String requestId,String version,String managementIp) throws Exception
	{
		ProcessBuilder builder = new ProcessBuilder( "cmd.exe" );
        Process p=null;
        try {
            p = builder.start();
            BufferedWriter p_stdin = 
                    new BufferedWriter(new OutputStreamWriter(p.getOutputStream()));
            String filepath = NetworkTestSSH.TSA_PROPERTIES
    				.getProperty("analyserPath");
            /*for (int i=0; i<2; i++) {
            	 p_stdin.write("cd..");
            	 
                 p_stdin.newLine();
                 p_stdin.flush();
                 }*/
            p_stdin.write("cd "+filepath);
            p_stdin.newLine();
            p_stdin.flush();
            p_stdin.write("ttcp -t nbufs 1 verbose host "+managementIp);
            p_stdin.newLine();
             p_stdin.flush();
            
            
             try {
 				Thread.sleep(150000);
 			} catch (Exception ee) {
 			}
             InputStream input = p.getInputStream();
             printResult(input,requestId,version);
             p_stdin.write("exit");
             p_stdin.newLine();
             p_stdin.flush();
        }
       
        catch (IOException e) {
            e.printStackTrace();
        }

   
        
 }
	
	
	private static void cmdPingCall(String requestId,String version,String managementIp) throws Exception
	{
		ProcessBuilder builder = new ProcessBuilder( "cmd.exe" );
        Process p=null;
        try {
            p = builder.start();
            BufferedWriter p_stdin = 
                    new BufferedWriter(new OutputStreamWriter(p.getOutputStream()));
            
           
            
            String commandToPing = "ping "+managementIp+" -n 20";
			p_stdin.write(commandToPing);
			System.out.println("command To Ping : "+commandToPing);
			System.out.println("Management IP : "+managementIp);
            
            p_stdin.newLine();
             p_stdin.flush();
             try {
  				Thread.sleep(5000);
  			} catch (Exception ee) {
  			}
             p_stdin.write("exit");
             p_stdin.newLine();
             p_stdin.flush();
            }
       
        catch (IOException e) {
            e.printStackTrace();
        }

   Scanner s = new Scanner( p.getInputStream() );
   
   InputStream input = p.getInputStream();
   printResult(input,requestId,version);
   
    while (s.hasNext())
    {
        System.out.println( s.nextLine() );
    }
    s.close();
 }
	
}