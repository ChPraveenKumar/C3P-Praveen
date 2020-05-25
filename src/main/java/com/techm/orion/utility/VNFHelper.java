package com.techm.orion.utility;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.io.IOUtils;
import org.json.XML;

import com.google.common.collect.Maps;
import com.hubspot.jinjava.Jinjava;
import com.techm.orion.pojo.CreateConfigRequestDCM;
import com.techm.orion.pojo.RequestInfoPojo;
import com.techm.orion.rest.VnfConfigService;

public class VNFHelper {
	public static String PROPERTIES_FILE = "TSA.properties";
	public static final Properties PROPERTIES = new Properties();

	public String saveXML(String data, String requestId,
			CreateConfigRequestDCM createConfigRequestDcm) {
		boolean result = true;
		String filepath = null, filepath2 = null;
		BufferedWriter bw = null;
		FileWriter fw = null;
		int SIZE = 1024;
		byte[] tmp = new byte[SIZE];
		try {
			VNFHelper.loadProperties();
			filepath = VNFHelper.PROPERTIES
					.getProperty("VnfConfigCreationPath")
					+ "//"
					+ requestId
					+ "_Configuration.xml";

			filepath2 = VNFHelper.PROPERTIES
					.getProperty("VnfConfigCreationPath")
					+ "//"
					+ requestId
					+ "_ConfigurationToPush.xml";
			File file = new File(filepath);
			File file1 = new File(filepath2);

			// first we generate header
			InvokeFtl invokeftl = new InvokeFtl();
			String header = invokeftl.generateheaderVNF(createConfigRequestDcm);
			String finalData = header.concat(data);
			// if file doesnt exists, then create it
			if (!file.exists()) {
				file.createNewFile();

				fw = new FileWriter(file, true);
				bw = new BufferedWriter(fw);
				bw.append(finalData);
				bw.close();
			}

			else {
				fw = new FileWriter(file.getAbsoluteFile(), true);
				bw = new BufferedWriter(fw);
				bw.append(finalData);
				bw.close();
			}

			if (!file1.exists()) {
				file1.createNewFile();

				fw = new FileWriter(file1, true);
				bw = new BufferedWriter(fw);
				bw.append(data);
				bw.close();
			}

			else {
				fw = new FileWriter(file1.getAbsoluteFile(), true);
				bw = new BufferedWriter(fw);
				bw.append(data);
				bw.close();
			}

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			result = false;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return filepath2;

	}

	public boolean pushOnVnfDevice(String file) {
		boolean result = true;
		String[] cmd = { "python",
				"D:\\configuration_files\\pythonScript\\editscript.py", file };
		Process p;
		try {
			p = Runtime.getRuntime().exec(cmd);

			BufferedReader in = new BufferedReader(new InputStreamReader(
					p.getInputStream()));
			String ret = in.readLine();

			BufferedReader bre = new BufferedReader(new InputStreamReader(
					p.getErrorStream()));
			String line;
			if (bre.readLine() == null) {

				// success
				System.out.println("" + ret);
			} else {
				System.out.println("" + bre.readLine());
				result=false;
			}
			bre.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return result;
	}

	public boolean cmdPingCall(String managementIp, String routername,
			String region) throws Exception {
		ProcessBuilder builder = new ProcessBuilder("cmd.exe");
		Process p = null;
		boolean flag = true;
		try {
			p = builder.start();
			BufferedWriter p_stdin = new BufferedWriter(new OutputStreamWriter(
					p.getOutputStream()));
			String commandToPing = "ping " + managementIp + " -n 20";
			System.out.print("Management ip " + managementIp);
			p_stdin.write(commandToPing);
			p_stdin.newLine();
			p_stdin.flush();
			try {
				Thread.sleep(21000);
			} catch (Exception ee) {
			}
			p_stdin.write("exit");
			p_stdin.newLine();
			p_stdin.flush();
		}

		catch (IOException e) {
			e.printStackTrace();
		}

		// Scanner s = new Scanner( p.getInputStream() );

		InputStream input = p.getInputStream();
		flag = printResult(input, routername, region);

		return flag;
	}

	private boolean printResult(InputStream input, String routername,
			String region) throws Exception {
		BufferedWriter bw = null;
		FileWriter fw = null;
		int SIZE = 1024;
		byte[] tmp = new byte[SIZE];
		boolean flag = true;

		while (input.available() > 0) {
			int i = input.read(tmp, 0, SIZE);
			if (i < 0)
				break;
			 System.out.print(new String(tmp, 0, i)); 
			String s = new String(tmp, 0, i);
			if (!(s.equals("")) && s.contains("Destination host unreachable")) {

				flag = false;

			} else if (!(s.equals("")) && s.contains("Request timed out.")) {
				flag = false;

			} else if (!(s.equals(""))
					&& s.contains("Destination net unreachable")) {
				flag = false;

			}
			VNFHelper.loadProperties();
			String filepath = VNFHelper.PROPERTIES
					.getProperty("responseDownloadPath")
					+ "//"
					+ routername
					+ "_" + region + "_Reachability.txt";
			File file = new File(filepath);

			// if file doesnt exists, then create it
			if (!file.exists()) {
				file.createNewFile();

				fw = new FileWriter(file, true);
				bw = new BufferedWriter(fw);
				bw.append(s);
				bw.close();
			}

			else {
				fw = new FileWriter(file.getAbsoluteFile(), true);
				bw = new BufferedWriter(fw);
				bw.append(s);
				bw.close();
			}

		}

		return flag;
	}

	public String loadXMLPayload(String filename) {
		String output = null;
		ClassLoader classLoader = getClass().getClassLoader();
		try (InputStream inputStream = classLoader
				.getResourceAsStream(filename)) {

			output = IOUtils.toString(inputStream, StandardCharsets.UTF_8);
			System.out.println("Payload read from resources: " + output);

		} catch (IOException e) {
			e.printStackTrace();
		}
		return output;
	}

	public String readConfigurationXML(String filepath) {
		String output = null;
		try {
			VNFHelper.loadProperties();
			File file = new File(filepath);
			output = new String(Files.readAllBytes(Paths.get(filepath)));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return output;
	}

	public static boolean loadProperties() throws IOException {
		InputStream PropFile = Thread.currentThread().getContextClassLoader()
				.getResourceAsStream(PROPERTIES_FILE);

		try {
			PROPERTIES.load(PropFile);
		} catch (IOException exc) {
			exc.printStackTrace();
			return false;
		}
		return false;
	}

	public String getPayload(String type, String xml) {
		String output = null;
		Jinjava jinjava = new Jinjava();
		Map<String, Object> context = Maps.newHashMap();

		org.json.JSONObject xmlJSONObj = XML.toJSONObject(xml);

		ClassLoader classLoader = new VnfConfigService().getClass()
				.getClassLoader();

		org.json.JSONObject configobj = xmlJSONObj.getJSONObject("config");
		org.json.JSONObject nativeobj = configobj.getJSONObject("native");

		if (type.equalsIgnoreCase("Loopback")) {
			InputStream is = VnfConfigService.class.getResourceAsStream("/LoopbackODLTemplate.xml");
			
			File file = new File(classLoader.getResource(
					"LoopbackODLTemplate.xml").getFile());

			org.json.JSONObject interfaceObj = nativeobj
					.getJSONObject("interface");
			org.json.JSONObject loopbackObj = interfaceObj
					.getJSONObject("Loopback");
			if(!loopbackObj.get("name").toString().isEmpty())
			{
			context.put("LOOPBACK_INDEX" , loopbackObj.getInt("name"));
			}
			if(!loopbackObj.get("description").toString().isEmpty())
			{
			context.put("LB_DESCRIPTION" , loopbackObj.getString("description"));
			}
			org.json.JSONObject ipObj = loopbackObj
					.getJSONObject("ip");
			org.json.JSONObject addressesObj = ipObj
					.getJSONObject("address");
			org.json.JSONObject primaryObj = addressesObj
					.getJSONObject("primary");
			
			if(!primaryObj.get("address").toString().isEmpty())
			{
			context.put("LB_IP_ADDRESS" , primaryObj.getString("address"));
			}
			if(!primaryObj.get("mask").toString().isEmpty())
			{
			context.put("LB_SUBNET_MASK" , primaryObj.getString("mask"));
			}
			String contents;
			
			try {
				output=IOUtils.toString(is, "UTF-8");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
				contents = is.toString();
				String renderedTemplate = jinjava.render(output, context);
				
				output=renderedTemplate;

			System.out.println("log");
		} else if (type.equalsIgnoreCase("Multilink")) {
			InputStream is = VnfConfigService.class.getResourceAsStream("/MultilinkODLTemplate.xml");
			
			File file = new File(classLoader.getResource(
					"MultilinkODLTemplate.xml").getFile());
			org.json.JSONObject interfaceObj = nativeobj
					.getJSONObject("interface");
			org.json.JSONObject loopbackObj = interfaceObj
					.getJSONObject("Multilink");
			if(!loopbackObj.get("name").toString().isEmpty())
			{
			context.put("MULTILINK_INDEX" , loopbackObj.getInt("name"));
			}
			if(!loopbackObj.get("description").toString().isEmpty())
			{
			context.put("ML_DESCRIPTION" , loopbackObj.getString("description"));
			}
			org.json.JSONObject ipObj = loopbackObj
					.getJSONObject("ip");
			org.json.JSONObject addressesObj = ipObj
					.getJSONObject("address");
			org.json.JSONObject primaryObj = addressesObj
					.getJSONObject("primary");
			if(!primaryObj.get("address").toString().isEmpty())
			{
			context.put("ML_IP_ADDRESS" , primaryObj.getString("address"));
			}
			if(!primaryObj.get("mask").toString().isEmpty())
			{
			context.put("ML_SUBNET_MASK" , primaryObj.getString("mask"));
			}
			String contents;
			try {
				output=IOUtils.toString(is, "UTF-8");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
				contents = is.toString();
				String renderedTemplate = jinjava.render(output, context);
				
				output=renderedTemplate;
		} else if (type.equalsIgnoreCase("Virtual-Template")) {
			InputStream is = VnfConfigService.class.getResourceAsStream("/Virtual-TemplateODL.xml");
			
			File file = new File(classLoader.getResource(
					"Virtual-TemplateODL.xml").getFile());
			org.json.JSONObject interfaceObj = nativeobj
					.getJSONObject("interface");
			org.json.JSONObject loopbackObj = interfaceObj
					.getJSONObject("Virtual-Template");
			
			if(!loopbackObj.get("name").toString().isEmpty())
			{
			context.put("VT_INDEX" , loopbackObj.getInt("name"));
			}
			if(!loopbackObj.get("description").toString().isEmpty())
			{
			context.put("VT_DESCRIPTION" , loopbackObj.getString("description"));
			}
			org.json.JSONObject ipObj = loopbackObj
					.getJSONObject("ip");
			org.json.JSONObject addressesObj = ipObj
					.getJSONObject("address");
			org.json.JSONObject primaryObj = addressesObj
					.getJSONObject("primary");
			
			if(!primaryObj.get("address").toString().isEmpty())
			{
			context.put("VT_IP_ADDRESS" , primaryObj.getString("address"));
			}
			if(!primaryObj.get("mask").toString().isEmpty())
			{
			context.put("VT_SUBNET_MASK" , primaryObj.getString("mask"));
			}
			String contents;
			try {
				output=IOUtils.toString(is, "UTF-8");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
				contents = is.toString();
				String renderedTemplate = jinjava.render(output, context);
				
				output=renderedTemplate;
	
		} else if (type.equalsIgnoreCase("BGP")) {

		}
		return output;
	}
//method overloding for UIRevamp
	public String saveXML(String data, String requestId, RequestInfoPojo requestInfoSO) {
		boolean result = true;
		String filepath = null, filepath2 = null;
		BufferedWriter bw = null;
		FileWriter fw = null;
		int SIZE = 1024;
		byte[] tmp = new byte[SIZE];
		try {
			VNFHelper.loadProperties();
			filepath = VNFHelper.PROPERTIES
					.getProperty("VnfConfigCreationPath")
					+ "//"
					+ requestId
					+ "_Configuration.xml";

			filepath2 = VNFHelper.PROPERTIES
					.getProperty("VnfConfigCreationPath")
					+ "//"
					+ requestId
					+ "_ConfigurationToPush.xml";
			File file = new File(filepath);
			File file1 = new File(filepath2);

			// first we generate header
			InvokeFtl invokeftl = new InvokeFtl();
			String header = invokeftl.generateheaderVNF(requestInfoSO);
			String finalData = header.concat(data);
			// if file doesnt exists, then create it
			if (!file.exists()) {
				file.createNewFile();

				fw = new FileWriter(file, true);
				bw = new BufferedWriter(fw);
				bw.append(finalData);
				bw.close();
			}

			else {
				fw = new FileWriter(file.getAbsoluteFile(), true);
				bw = new BufferedWriter(fw);
				bw.append(finalData);
				bw.close();
			}

			if (!file1.exists()) {
				file1.createNewFile();

				fw = new FileWriter(file1, true);
				bw = new BufferedWriter(fw);
				bw.append(data);
				bw.close();
			}

			else {
				fw = new FileWriter(file1.getAbsoluteFile(), true);
				bw = new BufferedWriter(fw);
				bw.append(data);
				bw.close();
			}

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			result = false;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return filepath2;

	
	}
}
