package com.techm.orion.service;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.Properties;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.techm.orion.pojo.CreateConfigRequest;
import com.techm.orion.pojo.RequestInfoPojo;

public class CSVWriteAndConnectPython {
	private static final Logger logger = LogManager.getLogger(CSVWriteAndConnectPython.class);

	public static String TSA_PROPERTIES_FILE = "TSA.properties";
	public static final Properties TSA_PROPERTIES = new Properties();

	public String ReadWriteAndConnectAnalyser(CreateConfigRequest configRequest) throws IOException {
		CSVWriteAndConnectPython.loadProperties();
		String analyserPath = CSVWriteAndConnectPython.TSA_PROPERTIES.getProperty("analyserPath");
		PrintWriter pw = new PrintWriter(new File(analyserPath + "/SOThrData.csv"));

		StringBuilder sb = new StringBuilder();

		sb.append(configRequest.getC3p_interface().getBandwidth());
		sb.append(',');
		sb.append(configRequest.getThroughput());
		sb.append(',');
		sb.append("Pass");
		sb.append(',');
		sb.append("T");

		pw.write(sb.toString());
		pw.flush();
		pw.close();
		ProcessBuilder builder = new ProcessBuilder("cmd.exe");
		Process p = null;

		String line = "";
		String cvsSplitBy = ",";
		String result = "";

		try {
			p = builder.start();
			BufferedWriter p_stdin = new BufferedWriter(new OutputStreamWriter(p.getOutputStream()));
			p_stdin.write("cd " + analyserPath);
			p_stdin.newLine();
			p_stdin.flush();
			p_stdin.write("python testing.py");
			p_stdin.newLine();
			p_stdin.flush();

			try {
				Thread.sleep(17000);
			} catch (Exception ee) {
			}

			BufferedReader br = new BufferedReader(
					new FileReader(analyserPath + "/SOTrainingData_C3P_Throughput_data.csv"));
			while ((line = br.readLine()) != null) {

				// use comma as separator
				String[] throuhput = line.split(cvsSplitBy);
				if (throuhput[5] != null) {
					result = throuhput[5];
					logger.info(result);
				} else {
					result = "Test not conducted";
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return result;

	}

	public static boolean loadProperties() throws IOException {
		InputStream tsaPropFile = Thread.currentThread().getContextClassLoader()
				.getResourceAsStream(TSA_PROPERTIES_FILE);

		try {
			TSA_PROPERTIES.load(tsaPropFile);
		} catch (IOException exc) {
			exc.printStackTrace();
			return false;
		}
		return false;
	}

	/* method overloding for UIRevamp */
	public String ReadWriteAndConnectAnalyser(RequestInfoPojo requestinfo) throws IOException {
		CSVWriteAndConnectPython.loadProperties();
		String analyserPath = CSVWriteAndConnectPython.TSA_PROPERTIES.getProperty("analyserPath");
		PrintWriter pw = new PrintWriter(new File(analyserPath + "/SOThrData.csv"));

		StringBuilder sb = new StringBuilder();

		// sb.append(configRequest.getC3p_interface().getBandwidth());
		// sb.append(',');
		sb.append(requestinfo.getThroughput());
		sb.append(',');
		sb.append("Pass");
		sb.append(',');
		sb.append("T");

		pw.write(sb.toString());
		pw.flush();
		pw.close();
		ProcessBuilder builder = new ProcessBuilder("cmd.exe");
		Process p = null;

		String line = "";
		String cvsSplitBy = ",";
		String result = "";

		try {
			p = builder.start();
			BufferedWriter p_stdin = new BufferedWriter(new OutputStreamWriter(p.getOutputStream()));
			p_stdin.write("cd " + analyserPath);
			p_stdin.newLine();
			p_stdin.flush();
			p_stdin.write("python testing.py");
			p_stdin.newLine();
			p_stdin.flush();

			try {
				Thread.sleep(17000);
			} catch (Exception ee) {
			}

			BufferedReader br = new BufferedReader(
					new FileReader(analyserPath + "/SOTrainingData_C3P_Throughput_data.csv"));
			while ((line = br.readLine()) != null) {

				// use comma as separator
				String[] throuhput = line.split(cvsSplitBy);
				if (throuhput[5] != null) {
					result = throuhput[5];
					logger.info(result);
				} else {
					result = "Test not conducted";
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return result;

	}
}
