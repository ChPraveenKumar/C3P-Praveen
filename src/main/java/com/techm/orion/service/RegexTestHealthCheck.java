package com.techm.orion.service;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class RegexTestHealthCheck {
	public static String TSA_PROPERTIES_FILE="TSA.properties";
	public static final Properties TSA_PROPERTIES = new Properties();
	/*public static void main(String[] args) throws Exception*/
	
	@SuppressWarnings("null")
	public String PreValidationForHealthCheckThroughput(String requestId,String version) throws Exception{
		// TODO Auto-generated method stub
		RegexTestHealthCheck.loadProperties();
		String[] ar=null;
		String[] data=null;
		String[] data1=null;
		String latencyValue="";
		String[] dataArray=null;
		Map<String,String> hmap = new HashMap<String,String>();
		String exp=RegexTestHealthCheck.TSA_PROPERTIES.getProperty("RegexFilterForThroughput");
		data=exp.split("\\|");
        String text = readFile(requestId,version);
        Matcher m = Pattern.compile("(?m)^(.*?\\b"+data[1]+"\\b).*?").matcher(text);
        while (m.find())
        {
        	ar=  m.group().split(data[2]);
           break;
        }        
        while (m.find())
        {
        	ar=  m.group().split(data[2]);
           break;
        }
        
       data1=ar[1].trim().split(" ");
      int throughput= ((int)(Double.parseDouble(data1[0])*1000));
      System.out.println(throughput);
     String throughputValue= String.valueOf(throughput);
       return throughputValue;
	}
	public Map<String, String> PreValidationForHealthCheckPing(String requestId,String version) throws Exception{
		// TODO Auto-generated method stub
		RegexTestHealthCheck.loadProperties();
		String[] ar=null;
		String[] data=null;
		String[] data1=null;
		String latencyValue="";
		String[] dataArray=null;
		Map<String,String> hmap = new HashMap<String,String>();
		String exp="";
		
		String value="";
		String frameLoss="";
		exp=RegexTestHealthCheck.TSA_PROPERTIES.getProperty("RegexFilterForFrameLoss");
	
		data=exp.split("\\|");
		
		        String text = readFile(requestId,version);
		        Matcher m = Pattern.compile("(?m)^(.*?\\b"+data[1]+"\\b).*?").matcher(text);
		  								

		        while (m.find())
		        {
		        	ar=  m.group().split(data[2]);
		        	data1=ar[2].trim().split("=");
				       frameLoss=String.valueOf(data1[1].charAt(1));
		        }
		        
		       
		       hmap.put("frameloss", frameLoss);
		       
		       
		       Matcher matcher = Pattern.compile("(?m)^(.*?"+"Average ="+").*ms").matcher(text);
				
				while (matcher.find()) {
					
					dataArray=  matcher.group().replace("ms", "").trim().split(" ");
					
					latencyValue=dataArray[dataArray.length-1];
					
					}
				hmap.put("latency", latencyValue);
				
       return hmap;
	}
	private static String readFile(String requestIdForConfig,String version) throws IOException {
		
		String responseDownloadPath = RegexTestHealthCheck.TSA_PROPERTIES
				.getProperty("responseDownloadPath");
		
		 
        BufferedReader br = new BufferedReader(new FileReader(
				responseDownloadPath+"//"+requestIdForConfig+"V"+version
				+ "_HealthCheck.txt"));
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
}