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
import java.util.Scanner;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class CSVWriteAndConnectPythonTemplateSuggestion {
	private static final Logger logger = LogManager.getLogger(CSVWriteAndConnectPythonTemplateSuggestion.class);
	
	public static String TSA_PROPERTIES_FILE = "TSA.properties";
	public static final Properties TSA_PROPERTIES = new Properties();

	public String ReadWriteAndAnalyseSuggestion(String suggestion, String type) throws IOException {
		CSVWriteAndConnectPythonTemplateSuggestion.loadProperties();
		String analyserPath = CSVWriteAndConnectPythonTemplateSuggestion.TSA_PROPERTIES.getProperty("analyserPath");
		PrintWriter pw = new PrintWriter(new File(analyserPath + "/InputTestData.csv"));

		StringBuilder sb = new StringBuilder();

		sb.append("Failure");
		sb.append(',');
		sb.append(suggestion);
		sb.append(',');
		sb.append(type);

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
			p_stdin.write("d:");
			p_stdin.newLine();
			p_stdin.flush();
			p_stdin.write("cd " + analyserPath);
			p_stdin.newLine();
			p_stdin.flush();
			p_stdin.write("python templateSuggestionTest.py");
			p_stdin.newLine();
			p_stdin.flush();

			try {
				Thread.sleep(17000);
			} catch (Exception ee) {

			}
			Scanner s = new Scanner(p.getInputStream());

			BufferedReader br = new BufferedReader(new FileReader(analyserPath + "/finalResultdata.csv"));
			while ((line = br.readLine()) != null) {

				// use comma as separator
				String[] prediction = line.split(cvsSplitBy);
				if (prediction[3] != null) {
					result = prediction[3];
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
}
