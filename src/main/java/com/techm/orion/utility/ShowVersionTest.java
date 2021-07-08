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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.techm.orion.pojo.PreValidateTest;

public class ShowVersionTest {
	private static final Logger logger = LogManager.getLogger(ShowVersionTest.class);

	public static String PROPERTIES_FILE = "TSA.properties";
	public static final Properties PROPERTIES = new Properties();

	public static boolean loadProperties() throws IOException {
		InputStream PropFile = Thread.currentThread().getContextClassLoader().getResourceAsStream(PROPERTIES_FILE);

		try {
			PROPERTIES.load(PropFile);
		} catch (IOException exc) {
			exc.printStackTrace();
			logger.error("Exception in loadProperties method "+exc.getMessage());
			return false;
		}
		return false;
	}

	public String getVersion(String hostname, String region, String type) {
		String version = null;
		String[] data = null;
		String[] data1 = null;
		String[] ar = null;
		String str1 = "";
		String[] comp = new String[10];
		PreValidateTest preValidateTest = new PreValidateTest();

		try {
			ShowVersionTest.loadProperties();
			String filepath = null;
			String exp = ShowVersionTest.PROPERTIES.getProperty("RegexFilterForPreValidation");

			data = exp.split("\\|");
			if (type.equalsIgnoreCase("Pre")) {
				filepath = ShowVersionTest.PROPERTIES.getProperty("responseDownloadPathVersion") + "//" + "Pre_"
						+ hostname + "_" + region + "_VersionInfo.txt";
			} else {
				filepath = ShowVersionTest.PROPERTIES.getProperty("responseDownloadPathVersion") + "//" + type + "_"
						+ hostname + "_" + region + "_VersionInfo.txt";
			}
			if (filepath != null) {
				String text = readFile(filepath);
				Matcher m = Pattern.compile("(?m)^(.*?\\b" + data[1] + "\\b).*?").matcher(text);
				while (m.find()) {
					ar = m.group().split(data[2]);
					break;
				}
				String indexPos = data[3];// to get the position
				data1 = indexPos.split(",");
				int k = 0;
				for (String s1 : data1) {
					{
						String[] str = ar[k].split(" ");//
						str1 = str[Integer.parseInt(s1)].trim();
						comp[k] = str1;
						k++;
					}

				}
				int vendorflag = 2;
				int versionflag = 2;
				int modelflag = 2;
				if (!comp[0].equalsIgnoreCase("")) {
					preValidateTest.setVendorActualValue(comp[0]);

				}
				if (!comp[1].equalsIgnoreCase("")) {

					preValidateTest.setModelActualValue(comp[1]);

				}
				if (!comp[2].equalsIgnoreCase("")) {

					preValidateTest.setOsVersionActualValue(comp[2].substring(0, 4));
					version = preValidateTest.getOsVersionActualValue();

				}

			}

		} catch (IOException e) {
			logger.error("Exception in getVersion method "+e.getMessage());
			e.printStackTrace();
		}
		return version;
	}

	public String versionInfo(String ip, String username, String password, String routername, String region,
			String type) throws IOException {
		boolean result = false;
		ShowVersionTest.loadProperties();
		String port = ShowVersionTest.PROPERTIES.getProperty("portSSH");
		String osversionOnDevice = null;
		JSch jsch = new JSch();
		Channel channel = null;
		Session session = null;
		try {
			session = jsch.getSession(username, ip, Integer.parseInt(port));

			Properties config = new Properties();
			config.put("StrictHostKeyChecking", "no");
			session.setConfig(config);
			session.setPassword(password);
			session.connect();
			try {
				Thread.sleep(10000);
			} catch (Exception ee) {
				logger.error("Exception in versionInfo method "+ee.getMessage());
			}
			channel = session.openChannel("shell");
			OutputStream ops = channel.getOutputStream();

			PrintStream ps = new PrintStream(ops, true);
			logger.info("Channel Connected to machine " + ip + " server for version test");
			channel.connect();
			InputStream input = channel.getInputStream();
			ps.println("show version");
			try {
				Thread.sleep(5000);
			} catch (Exception ee) {
				logger.error("Exception in versionInfo method "+ee.getMessage());
			}
			osversionOnDevice = printVersionversionInfo(input, channel, routername, region, type);
			result = true;
			channel.disconnect();
			session.disconnect();
		} catch (NumberFormatException e) {
			e.printStackTrace();
		} catch (JSchException e1) {
			e1.printStackTrace();
			osversionOnDevice = "JSchException";
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		finally {

			if (channel != null) {
				try {
					session = channel.getSession();

					if (channel.getExitStatus() == -1) {

						Thread.sleep(5000);

					}
				} catch (Exception e) {
					logger.error("Exception in versionInfo" +e.getMessage());
				}
				channel.disconnect();
				session.disconnect();
			}
		}
		return osversionOnDevice;
	}

	public String printVersionversionInfo(InputStream input, Channel channel, String routername, String region,
			String type) throws Exception {

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
				filepath = ShowVersionTest.PROPERTIES.getProperty("responseDownloadPathVersion") + "//" + type + "_"
						+ routername + "_" + region + "_VersionInfo.txt";
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
			String text = readFile(filepath);
			Matcher m = Pattern.compile("(?m)^(.*?\\b" + data[1] + "\\b).*?").matcher(text);

			while (m.find()) {
				ar = m.group().split(data[2]);
				break;
			}
			String indexPos = data[3];// to get the position
			data1 = indexPos.split(",");
			int k = 0;
			for (String s1 : data1) {
				{
					String[] str = ar[k].split(" ");//
					str1 = str[Integer.parseInt(s1)].trim();
					comp[k] = str1;
					k++;
				}

			}
			int vendorflag = 2;
			int versionflag = 2;
			int modelflag = 2;
			if (!comp[0].equalsIgnoreCase("")) {
				preValidateTest.setVendorActualValue(comp[0]);

			}
			if (!comp[1].equalsIgnoreCase("")) {

				preValidateTest.setModelActualValue(comp[1]);

			}
			if (!comp[2].equalsIgnoreCase("")) {

				preValidateTest.setOsVersionActualValue(comp[2].substring(0, 4));
				osversionOnDevice = preValidateTest.getOsVersionActualValue();

			}

		}
		if (channel.isClosed()) {
			logger.info("exit-status: " + channel.getExitStatus());

		}
		try {
			Thread.sleep(1000);
		} catch (Exception ee) {
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
