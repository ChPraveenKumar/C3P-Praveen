package com.techm.orion.utility;

import java.io.BufferedWriter;
import java.io.File;
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

public class ShowInventoryTest {
	private static final Logger logger = LogManager.getLogger(ShowInventoryTest.class);
	private static final String JSCH_CONFIG_INPUT_BUFFER= "max_input_buffer_size";

	public String inventoryInfo(String ip, String username, String password, String routername, String region)
			throws IOException {
		String result = null;
		try {
			JSch jsch = new JSch();
			Channel channel = null;
			Session session = jsch.getSession(username, ip, Integer.parseInt(C3PCoreAppLabels.PORT_SSH.getValue()));

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
			logger.info("Channel Connected to machine " + ip + " server for inventory test");
			channel.connect();
			InputStream input = channel.getInputStream();
			ps.println("show inventory");
			UtilityMethods.sleepThread(5000);
			printInventoryInfo(input, channel, routername, region);
			session.disconnect();
			channel.disconnect();
			result = "Pass";
		} catch (NumberFormatException e) {
			e.printStackTrace();
		} catch (JSchException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;

	}

	private String printInventoryInfo(InputStream input, Channel channel, String routername, String region)
			throws Exception {

		String osversionOnDevice = null;

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
				filepath = C3PCoreAppLabels.RESP_DOWNLOAD_HEALTH_CHECK_REPORTS_PATH.getValue()
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

		}
		if (channel.isClosed()) {
			logger.info("exit-status: " + channel.getExitStatus());
		}
		
		UtilityMethods.sleepThread(1000);
		
		return osversionOnDevice;

	}

}