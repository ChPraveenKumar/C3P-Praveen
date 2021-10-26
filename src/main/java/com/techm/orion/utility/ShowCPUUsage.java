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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.Scanner;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;

public class ShowCPUUsage {
	private static final Logger logger = LogManager.getLogger(ShowCPUUsage.class);
	private static final String JSCH_CONFIG_INPUT_BUFFER= "max_input_buffer_size";

	public String cpuUsageInfo(String ip, String username, String password, String routername, String region,
			String type) throws IOException {
		String result = null;
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
			logger.info("Channel Connected to machine " + ip + " server for cpu usage test");
			channel.connect();
			InputStream input = channel.getInputStream();
			ps.println("sh processes");
			UtilityMethods.sleepThread(5000);
			result = printCPUUsageInfo(input, channel, routername, region, type);

			channel.disconnect();
			session.disconnect();
		} catch (NumberFormatException e) {
			e.printStackTrace();
		} catch (JSchException e) {
			e.printStackTrace();
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
					logger.error("Exception in cpuUsageInfo" +e.getMessage());
				}
				channel.disconnect();
				session.disconnect();
			}
		}
		return result;

	}

	public int getCPUUsagePercentage(String hostname, String region, String type) {
		int res = 0;
		try {
			String filepath = null;

			if (type.equalsIgnoreCase("Pre")) {
				filepath = C3PCoreAppLabels.RESP_DOWNLOAD_HEALTH_CHECK_REPORTS_PATH.getValue() + "Pre_"
						+ hostname + "_" + region + "_CPUusage.txt";
			} else {
				filepath = C3PCoreAppLabels.RESP_DOWNLOAD_HEALTH_CHECK_REPORTS_PATH.getValue() + type
						+ "_" + hostname + "_" + region + "_CPUusage.txt";
			}
			if (filepath != null) {
				String text = readFile(filepath);
				Scanner scanner = new Scanner(text);
				List<String> linesToValidate = new ArrayList<String>();
				int count = 0;
				while (scanner.hasNext()) {
					if (count < 2) {
						linesToValidate.add(scanner.nextLine());
						count++;
					} else {
						break;
					}
				}
				for (int k = 0; k < linesToValidate.size(); k++) {
					if (linesToValidate.get(k).indexOf("//") != 0) {
						String[] parts = linesToValidate.get(k).split("//", 2);

						List<String> arrayList = new ArrayList<String>(Arrays.asList(parts[0].split(" ")));

						for (int i1 = 0; i1 < arrayList.size(); i1++) {
							if (arrayList.get(i1).contains("%")) {
								String percent = arrayList.get(i1).replace("%", "");
								int per = Integer.parseInt(percent);
								res = per;
							}
						}
					} else {
					}
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			return 0;
			// e.printStackTrace();
		}
		return res;
	}

	public String printCPUUsageInfo(InputStream input, Channel channel, String routername, String region, String type)
			throws Exception {

		String result = null;

		boolean value = false;

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
				filepath = C3PCoreAppLabels.RESP_DOWNLOAD_HEALTH_CHECK_REPORTS_PATH.getValue() + type
						+ "_" + routername + "_" + region + "_CPUusage.txt";
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

			Scanner scanner = new Scanner(text);
			List<String> linesToValidate = new ArrayList<String>();
			int count = 0;
			while (scanner.hasNext()) {
				if (count <= 2) {
					linesToValidate.add(scanner.nextLine());
					count++;
				} else {
					break;
				}
			}

			for (int k = 0; k < linesToValidate.size(); k++) {
				if (linesToValidate.get(k).contains("/")) {
					String[] parts = linesToValidate.get(k).split("/", 2);

					List<String> arrayList = new ArrayList<String>(Arrays.asList(parts[0].split(" ")));

					for (int i1 = 0; i1 < arrayList.size(); i1++) {
						if (arrayList.get(i1).contains("%")) {
							String percent = arrayList.get(i1).replace("%", "");
							int per = Integer.parseInt(percent);
							if (per <= 50) {
								result = "Pass";
							} else {
								result = "CPU Utilization is " + per;
							}
						}
					}
				} else {
				}
			}

		}
		if (channel.isClosed()) {
			logger.info("exit-status: " + channel.getExitStatus());

		}
		UtilityMethods.sleepThread(1000);

		return result;

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
