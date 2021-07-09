package com.techm.orion.utility;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
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
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.techm.orion.pojo.PreValidateTest;

public class ShowInventoryTest {
	private static final Logger logger = LogManager.getLogger(ShowInventoryTest.class);

	public static String PROPERTIES_FILE = "TSA.properties";
	public static final Properties PROPERTIES = new Properties();

	public static boolean loadProperties() throws IOException {
		InputStream PropFile = Thread.currentThread().getContextClassLoader().getResourceAsStream(PROPERTIES_FILE);

		try {
			PROPERTIES.load(PropFile);
		} catch (IOException exc) {
			logger.error("Exception in loadProperties method "+exc.getMessage());
			exc.printStackTrace();
			return false;
		}
		return false;
	}

	public String inventoryInfo(String ip, String username, String password, String routername, String region)
			throws IOException {
		String result = null;
		ShowInventoryTest.loadProperties();
		String port = ShowInventoryTest.PROPERTIES.getProperty("portSSH");
		try {
			JSch jsch = new JSch();
			Channel channel = null;
			Session session = jsch.getSession(username, ip, Integer.parseInt(port));

			Properties config = new Properties();
			config.put("StrictHostKeyChecking", "no");
			session.setConfig(config);
			session.setPassword(password);
			session.connect();
			try {
				Thread.sleep(10000);
			} catch (Exception ee) {
			}
			channel = session.openChannel("shell");
			OutputStream ops = channel.getOutputStream();

			PrintStream ps = new PrintStream(ops, true);
			logger.info("Channel Connected to machine " + ip + " server for inventory test");
			channel.connect();
			InputStream input = channel.getInputStream();
			ps.println("show inventory");
			try {
				Thread.sleep(5000);
			} catch (Exception ee) {
			}
			printInventoryInfo(input, channel, routername, region);
			session.disconnect();
			channel.disconnect();
			result = "Pass";
		} catch (NumberFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JSchException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return result;

	}

	public String printInventoryInfo(InputStream input, Channel channel, String routername, String region)
			throws Exception {

		String osversionOnDevice = null;
		String[] ar = null;
		String[] data = null;
		String[] data1 = null;
		String[] comp = new String[10];
		String str1 = "";
		String exp = ShowVersionTest.PROPERTIES.getProperty("RegexFilterForPreValidation");
		boolean value = false;
		PreValidateTest preValidateTest = new PreValidateTest();
		data = exp.split("\\|");

		BufferedWriter bw = null;
		FileWriter fw = null;
		int SIZE = 1024;
		byte[] tmp = new byte[SIZE];

		while (input.available() > 0) {
			int i = input.read(tmp, 0, SIZE);
			if (i < 0)
				break;
			/* logger.info(new String(tmp, 0, i)); */
			String filepath = null;
			String s = new String(tmp, 0, i);
			if (!(s.equals(""))) {
				// logger.info(str);
				filepath = ShowVersionTest.PROPERTIES.getProperty("responseDownloadPathHealthCheckFolder")
						+ routername + "_" + region + "_InventoryInfo.txt";
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
			/*
			 * String text = readFile(filepath); Matcher m =
			 * Pattern.compile("(?m)^(.*?\\b"+data[1]+"\\b).*?").matcher(text);
			 * 
			 * while (m.find()) { ar= m.group().split(data[2]); break; } String
			 * indexPos=data[3];//to get the position data1=indexPos.split(","); int k=0;
			 * for (String s1 : data1) { { String[] str=ar[k].split(" ");//
			 * str1=str[Integer.parseInt(s1)].trim(); comp[k]=str1; k++; }
			 * 
			 * 
			 * 
			 * } int vendorflag=2; int versionflag=2; int modelflag=2;
			 * if(!comp[0].equalsIgnoreCase("")) {
			 * preValidateTest.setVendorActualValue(comp[0]);
			 * 
			 * } if(!comp[1].equalsIgnoreCase("")) {
			 * 
			 * preValidateTest.setModelActualValue(comp[1]);
			 * 
			 * } if(!comp[2].equalsIgnoreCase("")) {
			 * 
			 * preValidateTest.setOsVersionActualValue(comp[2].substring(0, 4));
			 * osversionOnDevice=preValidateTest.getOsVersionActualValue();
			 * 
			 * }
			 */

		}
		if (channel.isClosed()) {
			logger.info("exit-status: " + channel.getExitStatus());

		}
		
		try {
			Thread.sleep(1000);
		} catch (Exception ee) {
			logger.error("Exception in printInventoryInfo method "+ee.getMessage());
		}
		
		return osversionOnDevice;

	}

	private static String readFile(String path) throws IOException {

		BufferedReader br = new BufferedReader(new FileReader(path));
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

}