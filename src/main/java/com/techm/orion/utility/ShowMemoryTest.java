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
import com.techm.orion.pojo.PreValidateTest;

public class ShowMemoryTest {
	private static final Logger logger = LogManager.getLogger(ShowMemoryTest.class);

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

	public String memoryInfo(String ip, String username, String password, String routername, String region, String type)
			throws IOException {
		String result = null;
		ShowMemoryTest.loadProperties();
		String port = ShowMemoryTest.PROPERTIES.getProperty("portSSH");
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
			}
			channel = session.openChannel("shell");
			OutputStream ops = channel.getOutputStream();

			PrintStream ps = new PrintStream(ops, true);
			logger.info("Channel Connected to machine " + ip + " server for memory test");
			channel.connect();
			InputStream input = channel.getInputStream();
			ps.println("show memory");
			try {
				Thread.sleep(5000);
			} catch (Exception ee) {
			}
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

						Thread.sleep(5000);

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
			ShowMemoryTest.loadProperties();
			String filepath = null;

			if (type.equalsIgnoreCase("Pre")) {
				filepath = ShowMemoryTest.PROPERTIES.getProperty("responseDownloadPathHealthCheckFolder")
						+ "Pre_" + hostname + "_" + region + "_MemoryInfo.txt";
			} else {
				filepath = ShowMemoryTest.PROPERTIES.getProperty("responseDownloadPathHealthCheckFolder") + type
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

	public String printMemoryInfo(InputStream input, Channel channel, String routername, String region, String type)
			throws Exception {

		String result = null;
		String[] ar = null;
		String[] data = null;
		String[] data1 = null;
		String[] comp = new String[10];
		String str1 = "";
		String exp = ShowMemoryTest.PROPERTIES.getProperty("RegexFilterForPreValidation");
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
				filepath = ShowMemoryTest.PROPERTIES.getProperty("responseDownloadPathHealthCheckFolder") + type
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
		try {
			Thread.sleep(1000);
		} catch (Exception ee) {
			logger.error("Exception in printMemoryInfo method "+ee.getMessage());
		}
		
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