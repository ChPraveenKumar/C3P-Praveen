package com.techm.orion.service;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.techm.orion.utility.TSALabels;
import com.techm.orion.utility.UtilityMethods;

public class CSVWriteAndConnectPythonTemplateSuggestion {
	private static final Logger logger = LogManager.getLogger(CSVWriteAndConnectPythonTemplateSuggestion.class);
	
	public String ReadWriteAndAnalyseSuggestion(String suggestion, String type) throws IOException {
		PrintWriter pw = new PrintWriter(new File(TSALabels.ANALYSER_PATH.getValue() + "InputTestData.csv"));

		StringBuilder sb = new StringBuilder();

		sb.append("Failure");
		sb.append(',');
		sb.append(suggestion);
		sb.append(',');
		sb.append(type);

		pw.write(sb.toString());
		pw.flush();
		pw.close();
		Process p = null;

		String line = "";
		String cvsSplitBy = ",";
		String result = "";
		BufferedReader br = null;
		try {
			ProcessBuilder builder = new ProcessBuilder("cmd.exe");
			p = builder.start();
			BufferedWriter p_stdin = new BufferedWriter(new OutputStreamWriter(p.getOutputStream()));
			//Check will be applicable for Windows environment only
			if("Windows".equals(TSALabels.APP_OS.getValue())) {
				p_stdin.write("d:");
				p_stdin.newLine();
				p_stdin.flush();
			}			
			p_stdin.write("cd " + TSALabels.ANALYSER_PATH.getValue());
			p_stdin.newLine();
			p_stdin.flush();
			p_stdin.write("python templateSuggestionTest.py");
			p_stdin.newLine();
			p_stdin.flush();

			UtilityMethods.sleepThread(17000);
			//Scanner s = new Scanner(p.getInputStream());

			br = new BufferedReader(new FileReader(TSALabels.ANALYSER_PATH.getValue() + "finalResultdata.csv"));
			while ((line = br.readLine()) != null) {

				// use comma as separator
				String[] prediction = line.split(cvsSplitBy);
				if (prediction[3] != null) {
					result = prediction[3];
					logger.info("result =>"+result);
				} else {
					result = "Test not conducted";
				}
			}
		} catch (Exception e) {
			logger.error("Exception in ReadWriteAndAnalyseSuggestion method "+e.getMessage());
		}finally {
			if(br !=null) {
				br.close();
			}
		}

		return result;
	}
}
