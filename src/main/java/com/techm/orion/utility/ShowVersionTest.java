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
	private static final String JSCH_CONFIG_INPUT_BUFFER= "max_input_buffer_size";

	public String getVersion(String hostname, String region, String type) {
		String version = null;
		String[] data = null;
		String[] data1 = null;
		String[] ar = null;
		String str1 = "";
		String[] comp = new String[10];
		PreValidateTest preValidateTest = new PreValidateTest();

		try {
			String filepath = null;
			String exp = C3PCoreAppLabels.REGEX_FILTER_PRE_VALIDATION.getValue();

			data = exp.split("\\|");
			if (type.equalsIgnoreCase("Pre")) {
				filepath = C3PCoreAppLabels.RESP_DOWNLOAD_VERSION_PATH.getValue() + "Pre_"
						+ hostname + "_" + region + "_VersionInfo.txt";
			} else {
				filepath = C3PCoreAppLabels.RESP_DOWNLOAD_VERSION_PATH.getValue() + type + "_"
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
		String osversionOnDevice = null;
		JSch jsch = new JSch();
		Channel channel = null;
		Session session = null;
		try {
			session = jsch.getSession(username, ip, Integer.parseInt(C3PCoreAppLabels.PORT_SSH.getValue()));

			Properties config = new Properties();
			config.put("StrictHostKeyChecking", "no");
			config.put(JSCH_CONFIG_INPUT_BUFFER, C3PCoreAppLabels.JSCH_CHANNEL_INPUT_BUFFER_SIZE.getValue());
			session.setConfig(config);
			session.setPassword(password);
			session.connect();
			UtilityMethods.sleepThread(10000);
			channel = session.openChannel("shell");
			OutputStream ops = channel.getOutputStream();

			PrintStream ps = new PrintStream(ops, true);
			logger.info("Channel Connected to machine " + ip + " server for version test");
			channel.connect();
			InputStream input = channel.getInputStream();
			ps.println("show version");
			UtilityMethods.sleepThread(5000);
			osversionOnDevice = printVersionversionInfo(input, channel, routername, region, type);
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

						UtilityMethods.sleepThread(5000);

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
		String exp = C3PCoreAppLabels.REGEX_FILTER_PRE_VALIDATION.getValue();
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
				filepath = C3PCoreAppLabels.RESP_DOWNLOAD_VERSION_PATH.getValue() + type + "_"
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
		UtilityMethods.sleepThread(1000);

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
