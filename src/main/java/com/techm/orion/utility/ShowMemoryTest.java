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
import java.util.Arrays;
import java.util.Properties;
import java.util.Scanner;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;

public class ShowMemoryTest {
	private static final Logger logger = LogManager.getLogger(ShowMemoryTest.class);
	private static final String JSCH_CONFIG_INPUT_BUFFER= "max_input_buffer_size";

	public String memoryInfo(String ip, String username, String password, String routername, String region, String type)
			throws IOException {
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
			logger.info("Channel Connected to machine " + ip + " server for memory test");
			channel.connect();
			InputStream input = channel.getInputStream();
			ps.println("show memory");
			UtilityMethods.sleepThread(5000);
			result = printMemoryInfo(input, channel, routername, region, type);
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
					logger.error("Exception in memoryInfo" +e.getMessage());
				}
				channel.disconnect();
				session.disconnect();
			}
		}
		return result;

	}

	public Double getMemoryUsed(String hostname, String region, String type) {
		Double per = 0.0;
		try {
			String filepath = null;

			if (type.equalsIgnoreCase("Pre")) {
				filepath = C3PCoreAppLabels.RESP_DOWNLOAD_HEALTH_CHECK_REPORTS_PATH.getValue()
						+ "Pre_" + hostname + "_" + region + "_MemoryInfo.txt";
			} else {
				filepath = C3PCoreAppLabels.RESP_DOWNLOAD_HEALTH_CHECK_REPORTS_PATH.getValue() + type
						+ "_" + hostname + "_" + region + "_MemoryInfo.txt";
			}
			if (filepath != null) {
				String text = readFile(filepath);
				Scanner scanner = new Scanner(text);
				String nextLine = scanner.nextLine();
				if (nextLine.isEmpty() || nextLine == " ") {
					nextLine = scanner.nextLine();
				}
				nextLine = scanner.nextLine();

				// regex to break on any ammount of spaces
				String regex = "(\\s)+";

				String total = null, used = null;

				String[] header = nextLine.split(regex);

				// this is printing all columns, you can
				// access each column from row using the array
				// indexes, example header[0], header[1], header[2]...
				logger.info(Arrays.toString(header));

				// reading the rows
				while (scanner.hasNext()) {
					String[] row = scanner.nextLine().split(regex);

					// this is printing all columns, you can
					// access each column from row using the array
					// indexes, example row[0], row[1], row[2]...
					if(!Arrays.toString(row).contains("Head, Total(b)"))
					{
					logger.info(Arrays.toString(row));
					logger.info(row[0]);// first column (ID)
					total = row[2];
					used = row[3];
					break;
					}
				}

				logger.info("Total" + total + " " + "Used " + used);

				Double t = Double.parseDouble(total);
				Double u = Double.parseDouble(used);

				Double percentUsed = (u / t);
				percentUsed = percentUsed * 100;
				per = percentUsed;
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			return 0.0;
			// e.printStackTrace();
		}

		return per;
	}

	private String printMemoryInfo(InputStream input, Channel channel, String routername, String region, String type)
			throws Exception {

		String result = null;

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
				filepath = C3PCoreAppLabels.RESP_DOWNLOAD_HEALTH_CHECK_REPORTS_PATH.getValue() + type
						+ "_" + routername + "_" + region + "_MemoryInfo.txt";
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

			// reading the first line, always have header
			// I suppose
			String nextLine = scanner.nextLine();
			if (nextLine.isEmpty() || nextLine == " ") {
				nextLine = scanner.nextLine();
			}
			nextLine = scanner.nextLine();

			// regex to break on any ammount of spaces
			String regex = "(\\s)+";

			String total = null, used = null;

			String[] header = nextLine.split(regex);

			// this is printing all columns, you can
			// access each column from row using the array
			// indexes, example header[0], header[1], header[2]...
			logger.info(Arrays.toString(header));

			// reading the rows
			while (scanner.hasNext()) {
				String[] row = scanner.nextLine().split(regex);

				// this is printing all columns, you can
				// access each column from row using the array
				// indexes, example row[0], row[1], row[2]...
				logger.info(Arrays.toString(row));

				if(!Arrays.toString(row).contains("Head, Total(b)"))
				{
				logger.info(row[0]);// first column (ID)
				total = row[2];
				used = row[3];
				break;
				}
			}

			logger.info("Total" + total + " " + "Used " + used);

			Double t = Double.parseDouble(total);
			Double u = Double.parseDouble(used);

			Double percentUsed = (u / t);
			percentUsed = percentUsed * 100;

			if (percentUsed > 80) {
				result = "Processor used memory is " + percentUsed + " of total processor memory.";
			} else {
				result = "Pass";
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